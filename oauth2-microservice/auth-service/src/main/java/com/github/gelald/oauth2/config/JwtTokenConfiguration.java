package com.github.gelald.oauth2.config;

import com.github.gelald.oauth2.properties.OAuthTokenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT中携带全面的用户信息，保存在jwt中携带过去校验就可以
 * 但是使用JWT的方式需要使用JwtAccessTokenConverter进行编码以及解码
 * Token令牌需要使用签名来验证是否合法，可以使用一个对称的签名，也可以使用非对称的密钥对
 *
 * @author WuYingBin
 * date: 2023/5/10
 */
@Configuration
public class JwtTokenConfiguration {
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    @Autowired
    private TokenEnhancer jwtTokenEnhancer;

    /**
     * 使用对称的密钥来签署Token
     */
    @Configuration
    @ConditionalOnProperty(prefix = "gelald.oauth.token", name = "key-type", havingValue = "signKey", matchIfMissing = true)
    public static class JwtSymmetricEncryptionConfiguration {
        @Autowired
        private OAuthTokenProperties oAuthTokenProperties;

        /**
         * 使用对称的密钥加密JWT中的OAuth2令牌
         */
        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter() {
            JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
            //生成签名的key
            jwtAccessTokenConverter.setSigningKey(oAuthTokenProperties.getSignKey());
            return jwtAccessTokenConverter;
        }
    }

    /**
     * 使用非对称密钥来签署Token
     */
    @Configuration
    @ConditionalOnProperty(prefix = "gelald.oauth.token", name = "key-type", havingValue = "keyPair")
    public static class JwtAsymmetricEncryptionConfiguration {
        @Autowired
        private OAuthTokenProperties oAuthTokenProperties;

        /**
         * 使用私钥加密JWT中的OAuth2令牌
         */
        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter() {
            JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
            //获取密钥对
            jwtAccessTokenConverter.setKeyPair(keyPair());
            return jwtAccessTokenConverter;
        }

        @Bean
        public KeyPair keyPair() {
            //从classpath下的证书中获取秘钥对
            //使用密码解密密钥对文件
            KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
                    new ClassPathResource(oAuthTokenProperties.getResource()),
                    oAuthTokenProperties.getPassword().toCharArray());
            //根据别名进入文件获取密钥对
            return keyStoreKeyFactory.getKeyPair(oAuthTokenProperties.getAlias());
        }
    }

    /**
     * Token增强器（在Token中设置更多的信息）
     */
    @Bean
    public TokenEnhancerChain tokenEnhancerChain() {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        if (jwtTokenEnhancer != null && jwtAccessTokenConverter != null) {
            List<TokenEnhancer> enhancerList = new ArrayList<>();
            enhancerList.add(jwtTokenEnhancer);
            enhancerList.add(jwtAccessTokenConverter);
            tokenEnhancerChain.setTokenEnhancers(enhancerList);
        }
        return tokenEnhancerChain;
    }
}
