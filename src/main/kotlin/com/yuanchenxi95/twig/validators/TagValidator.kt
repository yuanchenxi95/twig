package com.yuanchenxi95.twig.validators

import com.yuanchenxi95.twig.framework.validation.validationAssert
import com.yuanchenxi95.twig.protobuf.api.CreateTagRequest

fun validateTagName(tagName: String?) {
    validationAssert(!tagName.isNullOrBlank(), "Tag name must not be null or blank.")
}

fun validateCreateTagRequest(request: CreateTagRequest) {
    validateTagName(request.name)
}

fun validateDeleteTagRequest(tagId: String?) {
    validationAssert(!tagId.isNullOrBlank(), "Tag id must not be null.")
}
