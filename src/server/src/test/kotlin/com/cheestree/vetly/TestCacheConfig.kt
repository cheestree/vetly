package com.cheestree.vetly

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.support.NoOpCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@TestConfiguration
@Profile("test")
class TestCacheConfig {
    @Bean
    fun cacheManager(): CacheManager = NoOpCacheManager()
}
