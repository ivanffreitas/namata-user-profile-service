package com.namata.userprofile.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class CacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager fallbackCacheManager() {
        logger.warn("Redis não está disponível. Usando cache em memória como fallback.");
        return new ConcurrentMapCacheManager();
    }

    /**
     * Verifica se o Redis está disponível
     */
    public boolean isRedisAvailable() {
        try {
            LettuceConnectionFactory factory = new LettuceConnectionFactory(redisHost, redisPort);
            factory.afterPropertiesSet();
            factory.getConnection().ping();
            factory.destroy();
            return true;
        } catch (Exception e) {
            logger.warn("Redis não está disponível em {}:{} - {}", redisHost, redisPort, e.getMessage());
            return false;
        }
    }
}