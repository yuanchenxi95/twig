package com.yuanchenxi95.twig.validators

import com.yuanchenxi95.twig.framework.validation.ValidationError
import com.yuanchenxi95.twig.framework.validation.validationAssert
import com.yuanchenxi95.twig.protobuf.api.Bookmark
import com.yuanchenxi95.twig.protobuf.api.CreateBookmarkRequest
import com.yuanchenxi95.twig.protobuf.api.UpdateBookmarkRequest
import com.yuanchenxi95.twig.utils.datautils.decodeBookmarkPageToken
import org.apache.commons.validator.routines.UrlValidator
import java.util.*

fun validateBookmarkDisplayName(bookmark: Bookmark) {
    validationAssert(
        bookmark.displayName.length <= 500,
        "Bookmark display name cannot be longer than 500."
    )
}

fun validateBookmarkTags(bookmark: Bookmark) {
    bookmark.tagsList.forEach(::validateTagName)
}

fun validateCreateBookmarkRequest(request: CreateBookmarkRequest) {
    val bookmark = request.bookmark
    validationAssert(bookmark.id.isNullOrBlank(), "Bookmark id must be null")
    validateBookmarkDisplayName(bookmark)
    val url = bookmark.url
    validationAssert(url != null, "Bookmark url must not be null.")
    validationAssert(UrlValidator.getInstance().isValid(url), "URL '$url' is not valid.")
    validateBookmarkTags(bookmark)
}

fun validateUpdateBookmarkRequest(request: UpdateBookmarkRequest, bookmarkId: String) {
    validationAssert(request.hasUpdateMask(), "Update Mask cannot be null.")

    val bookmark = request.bookmark
    validationAssert(bookmark.id != null, "Bookmark id must not be null.")
    validationAssert(bookmark.id == bookmarkId, "Bookmark id does not match the id in the path.")
    validateBookmarkDisplayName(bookmark)
    validateBookmarkTags(bookmark)
    val allFields = request.updateMask.allFields
//    validationAssert(allFields.containsKey(Bookmark.getDescriptor()))
//    if(reqcontains(Bookmark.))
}

fun validateListBookmarkRequest(pageSize: Int, pageToken: String) {
    validationAssert(pageSize > 0, "Page Size must be positive integer.")

    if (pageToken.isNullOrEmpty()) {
        return
    }

    try {
        decodeBookmarkPageToken(pageToken)
    } catch (e: Exception) {
        throw ValidationError("Page Token format error.")
    }
}
