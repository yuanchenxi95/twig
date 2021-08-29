package com.yuanchenxi95.twig.validators

import com.yuanchenxi95.twig.framework.validation.ValidationError
import com.yuanchenxi95.twig.framework.validation.validationAssert
import com.yuanchenxi95.twig.protobuf.api.Bookmark
import com.yuanchenxi95.twig.protobuf.api.CreateBookmarkRequest
import com.yuanchenxi95.twig.protobuf.api.UpdateBookmarkRequest
import com.yuanchenxi95.twig.utils.datautils.decodeBookmarkPageToken
import org.apache.commons.validator.routines.UrlValidator
import java.net.URI

fun validateBookmarkDisplayName(bookmark: Bookmark) {
    validationAssert(
        bookmark.displayName.length <= 500,
        "Bookmark display name cannot be longer than 500."
    )
}

fun validateBookmarkTags(bookmark: Bookmark) {
    bookmark.tagsList.forEach(::validateTagName)
}

fun validateUrl(url: String) {
    validationAssert(url.isNotBlank(), "Url must not be null.")
    validationAssert(UrlValidator.getInstance().isValid(url), "URL '$url' is not valid.")
}

fun validateUriScheme(uri: URI) {
    val scheme = uri.scheme.toLowerCase()
    validationAssert(scheme == "http" || scheme == "https", "Only http or https are allowed.")
}

fun validateCreateBookmarkRequest(request: CreateBookmarkRequest) {
    val bookmark = request.bookmark
    validationAssert(bookmark.id.isNullOrBlank(), "Bookmark id must be null")
    validateBookmarkDisplayName(bookmark)
    validateUrl(bookmark.url)
    validateBookmarkTags(bookmark)
}

fun validateUpdateBookmarkRequest(request: UpdateBookmarkRequest, bookmarkId: String) {
    validationAssert(request.hasUpdateMask(), "Update Mask cannot be null.")

    val bookmark = request.bookmark
    validationAssert(bookmark.id != null, "Bookmark id must not be null.")
    validationAssert(bookmark.id == bookmarkId, "Bookmark id does not match the id in the path.")
    validateBookmarkDisplayName(bookmark)
    validateBookmarkTags(bookmark)
}

fun validateDeleteBookmarkRequest(bookmarkId: String?) {
    validationAssert(!bookmarkId.isNullOrBlank(), "Bookmark id must not be null.")
}

fun validateListBookmarkRequest(pageSize: Int, pageToken: String) {
    validationAssert(pageSize > 0, "Page Size must be positive integer.")

    if (pageToken.isEmpty()) {
        return
    }

    try {
        decodeBookmarkPageToken(pageToken)
    } catch (e: Exception) {
        throw ValidationError("Page Token format error.")
    }
}
