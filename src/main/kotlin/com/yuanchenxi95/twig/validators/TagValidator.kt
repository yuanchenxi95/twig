package com.yuanchenxi95.twig.validators

import com.yuanchenxi95.twig.framework.validation.validationAssert
import com.yuanchenxi95.twig.protobuf.api.CreateTagRequest
import com.yuanchenxi95.twig.protobuf.api.ListTagRequest

fun validateCreateTagRequest(request: CreateTagRequest) {
    val tagName = request.name
    validationAssert(!tagName.isNullOrBlank(), "Tag name must not be null.")
}

fun validateDeleteTagRequest(tagName: String?) {
    validationAssert(!tagName.isNullOrBlank(), "Tag name must not be null.")
}

fun validateListTagRequest(request: ListTagRequest) {
    val maxResults = request.maxResults
    validationAssert(!maxResults.isNullOrBlank(), "Max Results must not be null.")
}
