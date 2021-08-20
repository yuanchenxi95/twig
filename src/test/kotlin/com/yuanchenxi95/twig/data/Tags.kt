package com.yuanchenxi95.twig.data

import com.yuanchenxi95.twig.models.StoredTag
import com.yuanchenxi95.twig.protobuf.api.Tag
import com.yuanchenxi95.twig.protobuf.api.tag

const val TAG_ID_1 = "00000000-0000-0000-0000-000000000000"
const val TAG_ID_2 = "00000000-0000-0000-0000-000000000001"

val STORED_TAG_1 = StoredTag(
    id = TAG_ID_1,
    userId = STORED_USER_1.id,
    tagName = "FirstTag"
)

val API_TAG_1: Tag = tag {
    id = TAG_ID_1
    name = STORED_TAG_1.tagName
}

val STORED_TAG_2 = StoredTag(
    id = TAG_ID_2,
    userId = STORED_USER_1.id,
    tagName = "SecondTag"
)

val API_TAG_2: Tag = tag {
    id = TAG_ID_2
    name = STORED_TAG_2.tagName
}
