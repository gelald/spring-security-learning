package com.github.gelald.oauth2.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */

@Data
@Component
@ConfigurationProperties(prefix = "decrypt")
public class DecryptProperties {
    /**
     * 账号是否需要解密
     */
    private Boolean account = true;
    /**
     * 密码是否需要解密
     */
    private Boolean password = true;
    /**
     * 验证码是否需要解密
     */
    private Boolean code = true;
}