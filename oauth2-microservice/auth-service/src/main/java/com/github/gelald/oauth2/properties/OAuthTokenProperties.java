package com.github.gelald.oauth2.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Data
@Component
@ConfigurationProperties(prefix = "gelald.oauth.token")
public class OAuthTokenProperties implements Serializable {
    /**
     * jwt的密钥类型，signKey、keyPair
     */
    private String keyType;
    /**
     * 对称密钥
     */
    private String signKey;
    /**
     * 密钥对的资源文件
     */
    private String resource;
    /**
     * 密钥对别名
     */
    private String alias;
    /**
     * 资源文件密码
     */
    private String password;
    /**
     * 使用哪种存储策略
     */
    private String storeType;
}
