package com.github.gelald.oauth2.configuration;

import cn.hutool.core.util.ArrayUtil;
import com.github.gelald.oauth2.authorization.AuthorizationManager;
import com.github.gelald.oauth2.component.RestAuthenticationEntryPoint;
import com.github.gelald.oauth2.component.RestfulAccessDeniedHandler;
import com.github.gelald.oauth2.constant.AuthConstant;
import com.github.gelald.oauth2.properties.GatewaySecureProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfiguration {
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private GatewaySecureProperties gatewaySecureProperties;
    @Autowired
    private RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(jwtConverter());
        httpSecurity.authorizeExchange()
                //白名单配置，无需token也能请求的url
                .pathMatchers(ArrayUtil.toArray(gatewaySecureProperties.getIgnoreUrls(), String.class)).permitAll()
                .anyExchange().access(authorizationManager)
                .and().exceptionHandling()
                //处理未授权(没有携带token或token过期)
                .accessDeniedHandler(restfulAccessDeniedHandler)
                //处理未认证(没有权限访问这个url)
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and().csrf().disable();
        return httpSecurity.build();
    }

    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(AuthConstant.AUTHORITY_PREFIX);
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(AuthConstant.AUTHORITY_CLAIM_NAME);
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
