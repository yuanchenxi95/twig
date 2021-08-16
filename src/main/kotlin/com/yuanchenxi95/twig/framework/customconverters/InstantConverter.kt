package com.yuanchenxi95.twig.framework.customconverters

import org.springframework.core.convert.converter.Converter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class InstantConverter : Converter<LocalDateTime, Instant> {
    override fun convert(source: LocalDateTime): Instant? {
        return source.atZone(ZoneOffset.UTC).toInstant()
    }
}
