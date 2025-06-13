package com.cheestree.vetly.config

import com.cheestree.vetly.http.deserializer.CustomOffsetDateTimeDeserializer
import com.cheestree.vetly.http.serializer.CustomOffsetDateTimeSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Configuration
class JacksonConfig(
    private val appConfig: AppConfig,
) {
    @Primary
    @Bean
    fun objectMapper(): ObjectMapper {
        val objectMapper = Jackson2ObjectMapperBuilder.json().build<ObjectMapper>()
        val module = SimpleModule()
        val dateTimeFormatter = DateTimeFormatter.ofPattern(appConfig.format.dateFormat)
        module.addSerializer(OffsetDateTime::class.java, CustomOffsetDateTimeSerializer(dateTimeFormatter))
        module.addDeserializer(OffsetDateTime::class.java, CustomOffsetDateTimeDeserializer(dateTimeFormatter))
        objectMapper.registerModule(module)
        return objectMapper
    }
}
