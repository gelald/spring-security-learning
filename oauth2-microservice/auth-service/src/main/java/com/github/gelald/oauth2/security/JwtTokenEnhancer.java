package com.github.gelald.oauth2.security;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Component
public class JwtTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalInformation = new HashMap<>();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsCustom) {
            UserDetailsCustom userDetailsCustom = (UserDetailsCustom) principal;
            additionalInformation.put("id", userDetailsCustom.getId());
            additionalInformation.put("username", userDetailsCustom.getUsername());
//            additionalInformation.put("name", userDetailsCustom.getName());
//            additionalInformation.put("mobile", userDetailsCustom.getMobileNumber());
//            additionalInformation.put("type", userDetailsCustom.getType());
//            additionalInformation.put("roles", userDetailsCustom.getRoles());
//            additionalInformation.put("loginCount", userDetailsCustom.getLoginCount());
        }
        additionalInformation.put("sign_time", Instant.now().toEpochMilli());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
        return accessToken;
    }
}