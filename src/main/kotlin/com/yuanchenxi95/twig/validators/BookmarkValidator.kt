package com.yuanchenxi95.twig.validators

import com.yuanchenxi95.twig.framework.validation.validationAssert
import com.yuanchenxi95.twig.protobuf.api.CreateBookmarkRequest
import org.apache.commons.validator.routines.UrlValidator

fun validateCreateBookmarkRequest(request: CreateBookmarkRequest) {
    val url = request.url
    validationAssert(url != null, "Bookmark url must not be null.")
    validationAssert(UrlValidator.getInstance().isValid(url), "URL '$url' is not valid.")
}
