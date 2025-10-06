package com.simpleAnalytics.Gateway.config;

import io.lettuce.core.codec.RedisCodec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class LongCodec implements RedisCodec<String, Long> {

    @Override
    public String decodeKey(ByteBuffer bytes) {
        return StandardCharsets.UTF_8.decode(bytes).toString();
    }

    @Override
    public Long decodeValue(ByteBuffer bytes) {
        return Long.parseLong(StandardCharsets.UTF_8.decode(bytes).toString());
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return StandardCharsets.UTF_8.encode(key);
    }

    @Override
    public ByteBuffer encodeValue(Long value) {
        return StandardCharsets.UTF_8.encode(value.toString());
    }
}
