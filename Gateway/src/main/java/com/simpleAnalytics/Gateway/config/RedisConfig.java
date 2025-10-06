package com.simpleAnalytics.Gateway.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Default localhost:6379
        return new LettuceConnectionFactory();
    }

    @Bean(destroyMethod = "shutdown")
    public RedisClient redisClient() {
        return RedisClient.create("redis://localhost:6379");
    }

    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, Long> connection(RedisClient redisClient) {
        return redisClient.connect(new LongCodec());
    }

    @Bean
    public RedisAsyncCommands<String, Long> asyncCommands(StatefulRedisConnection<String, Long> connection) {
        return connection.async();
    }

    @Bean
    public RedisTemplate<String, Long> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key serializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value serializer (convert Long to string safely)
        template.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        template.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));

        template.afterPropertiesSet();
        return template;
    }
}
