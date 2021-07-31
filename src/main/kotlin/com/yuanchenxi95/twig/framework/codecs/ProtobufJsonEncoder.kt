package com.yuanchenxi95.twig.framework.codecs

import com.google.protobuf.Message
import org.reactivestreams.Publisher
import org.springframework.core.ResolvableType
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.http.MediaType
import org.springframework.http.codec.HttpMessageEncoder
import org.springframework.lang.Nullable
import org.springframework.util.MimeType
import reactor.core.publisher.Flux

/**
 * An `Encoder` that writes [com.google.protobuf.Message]s
 * using [Google Protocol Buffers](https://developers.google.com/protocol-buffers/).
 */
class ProtobufJsonEncoder : ProtobufJsonCodecSupport(), HttpMessageEncoder<Message> {
    override fun canEncode(elementType: ResolvableType, @Nullable mimeType: MimeType?): Boolean {
        return Message::class.java.isAssignableFrom(elementType.toClass()) && supportsMimeType(
            mimeType
        )
    }

    override fun encode(
        inputStream: Publisher<out Message>,
        bufferFactory: DataBufferFactory,
        elementType: ResolvableType,
        mimeType: MimeType?,
        hints: Map<String, Any>?
    ): Flux<DataBuffer> {
        return Flux.from(inputStream).map { message: Message ->
            encodeProtobufValue(
                message,
                bufferFactory
            )
        }
    }

    override fun encodeValue(
        message: Message,
        bufferFactory: DataBufferFactory,
        valueType: ResolvableType,
        mimeType: MimeType?,
        hints: Map<String, Any>?
    ): DataBuffer {
        return encodeProtobufValue(message, bufferFactory)
    }

    override fun getStreamingMediaTypes(): List<MediaType> {
        return listOf()
    }

    override fun getEncodableMimeTypes(): List<MimeType> {
        return MIME_TYPES
    }
}
