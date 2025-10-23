package com.cheestree.vetly.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class CacheService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    fun cacheValue(key: String, value: String) {
        redisTemplate.opsForValue().set(key, value)
    }

    fun getValue(key: String): String? =
        redisTemplate.opsForValue().get(key)
}