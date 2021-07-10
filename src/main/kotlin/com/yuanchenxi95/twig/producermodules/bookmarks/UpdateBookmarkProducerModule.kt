package com.yuanchenxi95.twig.producermodules.bookmarks

import com.google.protobuf.util.FieldMaskUtil
import com.yuanchenxi95.twig.constants.ErrorMessageUtils
import com.yuanchenxi95.twig.constants.ResourceType
import com.yuanchenxi95.twig.exceptions.ResourceNotFoundException
import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.models.StoredTag
import com.yuanchenxi95.twig.modelservices.StoredBookmarkService
import com.yuanchenxi95.twig.modelservices.StoredTagService
import com.yuanchenxi95.twig.modelservices.StoredTagsBookmarksService
import com.yuanchenxi95.twig.modelservices.StoredUrlService
import com.yuanchenxi95.twig.producermodules.ProducerModule
import com.yuanchenxi95.twig.protobuf.api.Bookmark
import com.yuanchenxi95.twig.protobuf.api.UpdateBookmarkRequest
import com.yuanchenxi95.twig.protobuf.api.UpdateBookmarkResponse
import com.yuanchenxi95.twig.utils.databaseutils.computeDiff
import com.yuanchenxi95.twig.utils.reactorutils.parallelExecuteWithLimit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono

@Component
class UpdateBookmarkProducerModule : ProducerModule<UpdateBookmarkResponse> {
    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @Autowired
    lateinit var storedTagService: StoredTagService

    @Autowired
    lateinit var storedTagsBookmarksService: StoredTagsBookmarksService

    @Autowired
    lateinit var storedBookmarkService: StoredBookmarkService

    @Autowired
    lateinit var storedUrlService: StoredUrlService

    inner class Executor(
        private val request: UpdateBookmarkRequest,
        private val bookmarkId: String,
        private val authentication: TwigAuthenticationToken
    ) : ProducerModule.ProducerModuleExecutor<UpdateBookmarkResponse> {

        private fun queryOrCreateTags(): Mono<List<StoredTag>> {
            val userId = authentication.getUserId()
            val tagsInTheRequest = request.bookmark.tagsList
            val existingTagsMono =
                storedTagService.queryTagsForUserByTagNames(userId, tagsInTheRequest)

            return existingTagsMono.flatMap { storedTags ->
                val tagsInTheDatabase = storedTags.map { it.tagName }
                val (_, tagsToCreate) = computeDiff(
                    HashSet(tagsInTheDatabase),
                    HashSet(tagsInTheRequest)
                )
                storedTagService.batchCreateTags(userId, tagsToCreate)
                    .map { createdTags -> storedTags.plus(createdTags) }
            }
        }

        private fun updateTags(): Mono<Void> {
            val userid = authentication.getUserId()
            val bookmarkId = bookmarkId
            val existingTagsForTheBookmarkMono =
                storedTagService.queryTagsForBookmark(userid, bookmarkId)
            val updatedTagsForTheBookmarkMono = queryOrCreateTags()
            return Mono.zip(existingTagsForTheBookmarkMono, updatedTagsForTheBookmarkMono)
                .flatMap { tuple ->
                    val existingTagIds = tuple.t1.map { it.id }
                    val updatedTagIds = tuple.t2.map { it.id }
                    val (tagsToDelete, tagsToCreate) = computeDiff(
                        HashSet(existingTagIds),
                        HashSet(updatedTagIds)
                    )
                    Mono.zip(
                        storedTagsBookmarksService.batchDeleteReferences(bookmarkId, tagsToDelete),
                        storedTagsBookmarksService.batchCreateReferences(
                            bookmarkId,
                            tagsToCreate
                        )
                    )
                }.then()
        }

        private fun getBookmark(): Mono<Bookmark> {
            return storedBookmarkService.selectOneBookmark(authentication.getUserId(), bookmarkId)
                .flatMap {
                    Mono.zip(
                        Mono.just(it),
                        storedUrlService.selectOneUrlById(it.urlId),
                        storedTagService.queryTagsForBookmark(
                            authentication.getUserId(),
                            bookmarkId
                        )
                    )
                }.map { tuple ->
                    Bookmark.newBuilder().setId(tuple.t1.id)
                        .setUrl(tuple.t2.url)
                        .addAllTags(tuple.t3.map { it.tagName })
                        .build()
                }
        }

        private fun updateBookmark(): Mono<UpdateBookmarkResponse> {
            val updateOperations = ArrayList<Mono<Void>>()

            val bookmarkToBeUpdated = Bookmark.newBuilder()
            FieldMaskUtil.merge(request.updateMask, request.bookmark, bookmarkToBeUpdated)

            if (bookmarkToBeUpdated.tagsList != null) {
                updateOperations.add(updateTags())
            }

            return parallelExecuteWithLimit(updateOperations).then(
                getBookmark().map {
                    UpdateBookmarkResponse.newBuilder().setBookmark(it).build()
                }
            )
        }

        private fun startUpdateBookmark(): Mono<UpdateBookmarkResponse> {
            return storedBookmarkService.selectOneBookmark(authentication.getUserId(), bookmarkId)
                .switchIfEmpty(
                    Mono.defer {
                        throw ResourceNotFoundException(
                            ErrorMessageUtils.resourceNotFoundError(
                                bookmarkId,
                                ResourceType.BOOKMARK
                            )
                        )
                    }
                ).flatMap {
                    updateBookmark()
                }
        }

        private fun transactionRunner(): Mono<UpdateBookmarkResponse> {
            val operator = TransactionalOperator.create(reactiveTransactionManager)
            return startUpdateBookmark().`as`(operator::transactional)
        }

        override fun execute(): Mono<UpdateBookmarkResponse> {
            return transactionRunner()
        }
    }
}
