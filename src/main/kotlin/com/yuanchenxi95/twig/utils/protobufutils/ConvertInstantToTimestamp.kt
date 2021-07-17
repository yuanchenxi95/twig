package com.yuanchenxi95.twig.utils.protobufutils

import com.google.protobuf.Timestamp
import java.time.Instant

fun convertInstantToTimestamp(instant: Instant): Timestamp {
    return Timestamp.newBuilder().setSeconds(instant.epochSecond).setNanos(instant.nano).build()
}
