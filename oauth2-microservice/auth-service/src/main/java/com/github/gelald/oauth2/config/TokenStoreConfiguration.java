package com.github.gelald.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;

/**
 * OAuth2 Token持久化策略配置，定义 Token 如何存储
 *
 * @author WuYingBin
 * date: 2023/5/31
 */
@Configuration
public class TokenStoreConfiguration {
    private DataSource dataSource;
    private RedisConnectionFactory redisConnectionFactory;
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    /**
     * 将令牌信息(access_token)保存在内存中
     */
    @Bean
    @ConditionalOnProperty(prefix = "gelald.oauth.token", name = "store-type", havingValue = "memory")
    public TokenStore inMemoryTokenStore() {
        return new InMemoryTokenStore();
    }

    /**
     * 将令牌信息(access_token)保存在数据库中
     */
    @Bean
    @ConditionalOnProperty(prefix = "gelald.oauth.token", name = "store-type", havingValue = "jdbc")
    public TokenStore jdbcTokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    /**
     * 将令牌信息(access_token)保存在Redis中
     */
    @Bean
    @ConditionalOnProperty(prefix = "gelald.oauth.token", name = "store-type", havingValue = "redis", matchIfMissing = true)
    public TokenStore redisTokenStore() {
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        //使用自定义的序列化工具，使得Token更具有可读性
        //redisTokenStore.setSerializationStrategy(new CustomRedisTokenStoreSerializationStrategy());
        return redisTokenStore;
    }

    /**
     * 使用jwtTokenStore存储token
     * 使用Jwt方式保存令牌，它不需要进行存储
     */
    @Bean
    @ConditionalOnProperty(prefix = "gelald.oauth.token", name = "store-type", havingValue = "jwt")
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    @Autowired(required = false)
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired(required = false)
    public void setRedisConnectionFactory(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Autowired(required = false)
    public void setJwtAccessTokenConverter(JwtAccessTokenConverter jwtAccessTokenConverter) {
        this.jwtAccessTokenConverter = jwtAccessTokenConverter;
    }
}
