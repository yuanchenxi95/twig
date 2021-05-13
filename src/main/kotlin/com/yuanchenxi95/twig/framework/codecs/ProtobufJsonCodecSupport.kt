package com.yuanchenxi95.twig.framework.codecs

import org.springframework.http.MediaType
import org.springframework.util.MimeType

/**
 * Base class providing support methods for Protobuf encoding and decoding.
 */
abstract class ProtobufJsonCodecSupport {
    protected fun supportsMimeType(mimeType: MimeType?): Boolean {
        return mimeType == null || MIME_TYPES.stream().anyMatch {
            it.isCompatibleWith(
                mimeType
            )
        }
    }

    companion object {
        val MIME_TYPES =
            listOf(
                MediaType.APPLICATION_JSON
            )
    }
}
