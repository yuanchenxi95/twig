package com.yuanchenxi95.twig.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("bookmark")
class StoredBookmark(val hostname: String, var uri: String, val protocol: String) {
    @Id
    var id: Long? = null

    override fun toString(): String {
        return String.format(
            "Customer[id=%d, hostname='%s', uri='%s', protocol='%s']",
            id, hostname, uri, protocol
        )
    }
}
