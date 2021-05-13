package com.yuanchenxi95.twig.framework.codecs

import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.core.io.buffer.DataBufferUtils
import java.io.IOException

/**
 * Encodes the [com.google.protobuf.Message]s into JSON string.
 */
fun encodeProtobufValue(message: Message, bufferFactory: DataBufferFactory): DataBuffer {
    val buffer = bufferFactory.allocateBuffer()
    var release = true
    return try {
        val str: String = JsonFormat.printer().omittingInsignificantWhitespace().print(message)
        buffer.asOutputStream().bufferedWriter().append(str).flush()
        release = false
        buffer
    } catch (ex: IOException) {
        throw IllegalStateException("Unexpected I/O error while writing to data buffer", ex)
    } finally {
        if (release) {
            DataBufferUtils.release(buffer)
        }
    }
}
