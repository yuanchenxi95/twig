package com.yuanchenxi95.twig.validators

import com.yuanchenxi95.twig.framework.validation.validationAssert
import com.yuanchenxi95.twig.protobuf.api.CreateTagRequest

fun validateCreateTagRequest(request: CreateTagRequest) {
    val tagName = request.name
    validationAssert(!tagName.isNullOrBlank(), "Tag name must not be null.")
}

fun validateDeleteTagRequest(tagName: String?) {
    validationAssert(!tagName.isNullOrBlank(), "Tag name must not be null.")
}
