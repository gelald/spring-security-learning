package com.github.gelald.cas.biz.config;

import com.github.gelald.cas.biz.filter.SSOFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author WuYingBin
 * Date 2022/12/22 0022
 */
@Configuration
public class FilterConfig {

//    @Bean
    public FilterRegistrationBean<SSOFilter> ssoFilterFilterRegistrationBean(RedisTemplate<Object, Object> redisTemplate) {
        FilterRegistrationBean<SSOFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SSOFilter(redisTemplate));
        registrationBean.addUrlPatterns("/");
        registrationBean.setName("ssoFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
