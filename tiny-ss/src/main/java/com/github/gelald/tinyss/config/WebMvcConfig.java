package com.github.gelald.tinyss.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 直接访问页面，不需要Controller
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/").setViewName("welcome");
        registry.addViewController("/index").setViewName("welcome");
        registry.addViewController("/error").setViewName("error");
        registry.addViewController("/access-denied").setViewName("access-denied");
    }
}