package com.github.gelald.oauth2.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * MySQL数据库存储客户端信息
 *
 * @author WuYingBin
 * date: 2023/5/31
 */
@Service
@Primary
@ConditionalOnProperty(prefix = "gelald.oauth.client", name = "type", havingValue = "jdbc")
public class MySQLClientDetailsService implements ClientDetailsService {
    private final JdbcClientDetailsService jdbcClientDetailsService;

    public MySQLClientDetailsService(DataSource dataSource) {
        jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return jdbcClientDetailsService.loadClientByClientId(clientId);
    }
}
