package com.yuanchenxi95.twig.framework.codecs

import com.google.common.io.ByteStreams
import com.google.protobuf.Message
import org.reactivestreams.Publisher
import org.springframework.core.ResolvableType
import org.springframework.core.codec.Decoder
import org.springframework.core.codec.DecodingException
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.lang.Nullable
import org.springframework.util.ConcurrentReferenceHashMap
import org.springframework.util.MimeType
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.IOException
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentMap

/**
 * A `Decoder` that reads [com.google.protobuf.Message]s using
 * [Google Protocol Buffers](https://developers.google.com/protocol-buffers/).
 */
class ProtobufJsonDecoder :
    ProtobufJsonCodecSupport(), Decoder<Message> {

    override fun canDecode(elementType: ResolvableType, mimeType: MimeType?): Boolean {
        return Message::class.java.isAssignableFrom(elementType.toClass()) && supportsMimeType(
            mimeType
        )
    }

    override fun decodeToMono(
        inputStream: Publisher<DataBuffer>,
        elementType: ResolvableType,
        @Nullable mimeType: MimeType?,
        @Nullable hints: Map<String, Any>?
    ): Mono<Message> {
        return DataBufferUtils.join(inputStream)
            .map { dataBuffer: DataBuffer ->
                decodeValue(
                    dataBuffer,
                    elementType
                )
            }
    }

    @Throws(DecodingException::class)
    override fun decode(
        dataBuffer: Publisher<DataBuffer>,
        targetType: ResolvableType,
        @Nullable mimeType: MimeType?,
        @Nullable hints: Map<String, Any>?
    ): Flux<Message> {
        return Flux.from(dataBuffer).map { decodeValue(it, targetType) }
    }

    @Throws(DecodingException::class)
    fun decodeValue(
        dataBuffer: DataBuffer,
        targetType: ResolvableType
    ): Message {
        try {
            val builder: Message.Builder = getMessageBuilder(targetType.toClass())
            val inputStream =
                ByteStreams.limit(dataBuffer.asInputStream(), DEFAULT_MESSAGE_MAX_SIZE)
            val bytes = ByteStreams.toByteArray(inputStream)
            if (bytes.size >= DEFAULT_MESSAGE_MAX_SIZE) {
                throw ServerWebInputException("Max message size '$DEFAULT_MESSAGE_MAX_SIZE' exceeds.")
            }
            decodeProtobuf(String(bytes), builder)
            return builder.build()
        } catch (ex: IOException) {
            // TODO, handle InvalidProtocolBufferException
            throw ServerWebInputException("I/O error while parsing input stream", null, ex)
        } catch (ex: ServerWebInputException) {
            throw ex
        } catch (ex: Exception) {
            throw ServerWebInputException(
                "Could not read Protobuf message: " + ex.message,
                null,
                ex
            )
        } finally {
            DataBufferUtils.release(dataBuffer)
        }
    }

    companion object {
        /** The default max size for aggregating messages.  */
        const val DEFAULT_MESSAGE_MAX_SIZE: Long = 1024 * 1024
        private val methodCache: ConcurrentMap<Class<*>, Method> = ConcurrentReferenceHashMap()

        /**
         * Create a new `Message.Builder` instance for the given class.
         *
         * This method uses a ConcurrentHashMap for caching method lookups.
         */
        @Throws(Exception::class)
        private fun getMessageBuilder(clazz: Class<*>): Message.Builder {
            var method: Method? = methodCache[clazz]
            if (method == null) {
                method = clazz.getMethod("newBuilder")
                methodCache[clazz] = method
            }
            return method!!.invoke(clazz) as Message.Builder
        }
    }

    override fun getDecodableMimeTypes(): List<MimeType> {
        return MIME_TYPES
    }
}
