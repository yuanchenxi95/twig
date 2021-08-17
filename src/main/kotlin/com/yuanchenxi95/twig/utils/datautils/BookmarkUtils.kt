package com.yuanchenxi95.twig.utils.datautils

import java.time.Instant
import java.util.*

private const val DELIMITER = "-"

fun decodeBookmarkPageToken(pageToken: String): Pair<Instant, String> {
    val results = pageToken.split(DELIMITER)
    val createTimeStr = results[0]
    val bookmarkIdStr = results[1]

    val createTimeDecode = Base64.getDecoder().decode(createTimeStr)
    val bookmarkIdDecode = Base64.getDecoder().decode(bookmarkIdStr)

    return Pair(Instant.parse(String(createTimeDecode)), String(bookmarkIdDecode))
}

fun encodeBookmarkPageToken(lastCreateTime: Instant, lastId: String): String {
    return Base64.getEncoder().encodeToString(lastCreateTime.toString().toByteArray()) + DELIMITER +
        Base64.getEncoder().encodeToString(lastId.toByteArray())
}
