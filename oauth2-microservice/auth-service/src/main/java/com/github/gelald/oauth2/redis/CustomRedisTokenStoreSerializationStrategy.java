package com.github.gelald.oauth2.redis;

import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.redis.StandardStringSerializationStrategy;

/**
 * @author WuYingBin
 * date: 2023/5/17
 */
public class CustomRedisTokenStoreSerializationStrategy extends StandardStringSerializationStrategy {
    private static final Jackson2JsonRedisSerializer<OAuth2Authentication> SERIALIZER = new Jackson2JsonRedisSerializer<>(OAuth2Authentication.class);

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T deserializeInternal(byte[] bytes, Class<T> clazz) {
        return (T) SERIALIZER.deserialize(bytes);
    }

    @Override
    protected byte[] serializeInternal(Object object) {
        return SERIALIZER.serialize(object);
    }
}
