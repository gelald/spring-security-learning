package com.github.gelald.oauth2.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Data
@Component
@ConfigurationProperties(prefix = "gelald.oauth.client")
public class OAuthClientProperties implements Serializable {
    /**
     * 使用哪种方式存储客户端
     * properties、jdbc
     */
    private String type;
    /**
     * 客户端信息配置文件
     */
    private List<PropertiesClient> propertiesClients = new ArrayList<>();

    @Data
    public static class PropertiesClient {
        private String clientId;
        private String clientSecret;
        private List<String> authorizedGrantTypes;
        private String[] redirectUris;
        private List<String> scopes;
        private String[] authorities = new String[]{"ADMIN", "USER"};
        private Boolean autoApprove = false;
        private Integer accessTokenValiditySeconds = 3600;
        private Integer refreshTokenValiditySeconds = 3600;
    }

    public void setType(String type) {
        this.type = type;
    }
}
