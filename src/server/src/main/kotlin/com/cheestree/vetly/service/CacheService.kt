package com.cheestree.vetly.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class CacheService(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    fun cacheValue(key: String, value: Any) {
        redisTemplate.opsForValue().set(key, value)
    }

    fun getValue(key: String): Any? =
        redisTemplate.opsForValue().get(key)
}