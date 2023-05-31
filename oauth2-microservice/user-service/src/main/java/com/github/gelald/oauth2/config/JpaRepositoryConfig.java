package com.github.gelald.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.github.gelald.oauth2.repository")
public class JpaRepositoryConfig {
}
