package com.github.gelald.oauth2.config;

import com.github.gelald.oauth2.granter.SmsVerificationCodeTokenGranter;
import com.github.gelald.oauth2.services.impl.AlwaysCreateTokenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>@EnableAuthorizationServer</code> 表明这是授权服务器的核心配置，此配置类需要继承 <code>AuthorizationServerConfigurerAdapter</code> 该类重写 <code>configure</code> 方法定义授权服务器策略
 *
 * @author WuYingBin
 * date: 2023/5/31
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenStore tokenStore;
    @Autowired(required = false)
    private TokenEnhancerChain tokenEnhancerChain;
    @Value("${token.always-create:false}")
    private Boolean alwaysCreateToken;
    /**
     * Token使用Jwt的格式时使用
     */
    @Autowired
    private AccessTokenConverter jwtAccessTokenConverter;
    @Autowired
    private ClientDetailsService clientDetailsService;

    /**
     * 配置授权服务器端点，如令牌存储，令牌自定义，用户批准和授权类型，不包括端点安全配置
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                //认证管理器
                .authenticationManager(authenticationManager)
                //token的存储方式
                .tokenStore(tokenStore)
                //Token使用JWT的格式，即使不使用JwtTokenStore
                .accessTokenConverter(jwtAccessTokenConverter);
        if (tokenEnhancerChain != null) {
            //自定义令牌增强
            endpoints.tokenEnhancer(tokenEnhancerChain);
        }
//                .tokenGranter(this.tokenGranter(endpoints))
        //实现了令牌创建、获取、刷新、撤销等方法，存储令牌的工作委托给了TokenStore
//                .tokenServices(Boolean.TRUE.equals(alwaysCreateToken) ? defaultTokenServices() : null);
    }

    /**
     * 配置授权服务器端点的安全
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer
                //允许客户端发送表单来进行权限认证来获取令牌
                .allowFormAuthenticationForClients()
                //允许所有资源服务器访问公钥endpoint(/oauth/token_key)
                .tokenKeyAccess("permitAll()")
                //只允许认证过的用户访问令牌解析endpoint(/oauth/check_token)
                .checkTokenAccess("isAuthenticated()");
    }

    /**
     * 配置客户端信息
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //根据配置文件，决定是用哪一个ClientDetailsService的实现类
        //注意由于ClientDetailsServiceConfiguration#clientDetailsService已经被标注为@Bean
        //所以自己的实现类需要加@Primary
        clients.withClientDetails(clientDetailsService);
    }

    /**
     * 创建grant_type列表
     */
    private TokenGranter tokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> list = new ArrayList<>();
        if (authenticationManager != null) {
            //这里配置密码模式
            list.add(new ResourceOwnerPasswordTokenGranter(
                    authenticationManager,
                    endpoints.getTokenServices(),
                    endpoints.getClientDetailsService(),
                    endpoints.getOAuth2RequestFactory()));
            //自定义手机号验证码模式
            list.add(new SmsVerificationCodeTokenGranter(
                    authenticationManager,
                    endpoints.getTokenServices(),
                    endpoints.getClientDetailsService(),
                    endpoints.getOAuth2RequestFactory()));
        }
        //刷新token模式
        list.add(new RefreshTokenGranter(
                endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory()));
        //授权码模式
        list.add(new AuthorizationCodeTokenGranter(
                endpoints.getTokenServices(),
                endpoints.getAuthorizationCodeServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory()));
        //简化模式
        list.add(new ImplicitTokenGranter(
                endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory()));
        //客户端模式
        list.add(new ClientCredentialsTokenGranter(
                endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory()));
        return new CompositeTokenGranter(list);
    }

    @Bean
    public AuthorizationServerTokenServices defaultTokenServices() {
        DefaultTokenServices defaultTokenServices = new AlwaysCreateTokenServiceImpl();
        defaultTokenServices.setAccessTokenValiditySeconds(60 * 60 * 2);
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setReuseRefreshToken(false);
        defaultTokenServices.setTokenStore(tokenStore);
        defaultTokenServices.setTokenEnhancer(tokenEnhancerChain);
        return defaultTokenServices;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
