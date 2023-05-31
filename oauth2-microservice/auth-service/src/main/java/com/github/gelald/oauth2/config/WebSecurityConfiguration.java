package com.github.gelald.oauth2.config;

import com.github.gelald.oauth2.security.SmsVerificationCodeAuthenticationProvider;
import com.github.gelald.oauth2.security.UsernamePasswordAuthenticationProvider;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */

@Configuration
@EnableWebSecurity
@Order(2)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;
    private final SmsVerificationCodeAuthenticationProvider smsVerificationCodeAuthenticationProvider;

    public WebSecurityConfiguration(UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider,
                                    SmsVerificationCodeAuthenticationProvider smsVerificationCodeAuthenticationProvider) {
        this.usernamePasswordAuthenticationProvider = usernamePasswordAuthenticationProvider;
        this.smsVerificationCodeAuthenticationProvider = smsVerificationCodeAuthenticationProvider;
    }

    //不定义没有password grant_type
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(this.usernamePasswordAuthenticationProvider)
                .authenticationProvider(this.smsVerificationCodeAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 放行所有请求，否则只有GET请求可以正常访问
        http.csrf().disable();
        //必须配置上登录才行formLogin或basicLogin
        // https://www.cnblogs.com/rock77/p/12651862.html
        http.formLogin()
                .and().authorizeRequests()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll();
//        .antMatchers("/rsa/publicKey", "/oauth/token", "/swagger-ui.html").permitAll()
//        .anyRequest().authenticated();
    }
}
