package com.github.gelald.oauth2.client;

import com.github.gelald.oauth2.properties.OAuthClientProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置文件定义客户端信息
 *
 * @author WuYingBin
 * date: 2023/5/31
 */
@Service
@Primary
@ConditionalOnProperty(prefix = "gelald.oauth.client", name = "type", havingValue = "properties", matchIfMissing = true)
public class PropertiesClientDetailsService implements ClientDetailsService {
    private final List<ClientDetails> clients = new ArrayList<>();

    public PropertiesClientDetailsService(OAuthClientProperties oAuthClientProperties) {
        for (OAuthClientProperties.PropertiesClient propertiesClient : oAuthClientProperties.getPropertiesClients()) {
            BaseClientDetails client = new BaseClientDetails();
            client.setClientId(propertiesClient.getClientId());
            client.setClientSecret(propertiesClient.getClientSecret());
            client.setAuthorizedGrantTypes(propertiesClient.getAuthorizedGrantTypes());
            client.setScope(propertiesClient.getScopes());
            client.setAccessTokenValiditySeconds(propertiesClient.getAccessTokenValiditySeconds());
            client.setRefreshTokenValiditySeconds(propertiesClient.getRefreshTokenValiditySeconds());
            clients.add(client);
        }
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return clients.stream()
                .filter(client -> client.getClientId().equals(clientId))
                .findFirst()
                .orElseThrow(() -> new ClientRegistrationException("Client not found"));
    }
}
