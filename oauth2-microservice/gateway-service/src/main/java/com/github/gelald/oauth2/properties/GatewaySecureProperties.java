package com.github.gelald.oauth2.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway.secure")
public class GatewaySecureProperties {
    private List<String> ignoreUrls;
    private Boolean dev;
}
