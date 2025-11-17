package com.github.gelald.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

@Slf4j
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .csrf(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**", "/resource/**").permitAll()
                        .anyRequest().authenticated())

                // ********** Http Basic Login **********
                // 交互体验不好，一般只用于快速测试，不建议使用
                //.httpBasic(Customizer.withDefaults())
                // ********** Http Basic Login **********

                // ********** Form Login **********
                // 使用默认的实现可能会出现无法访问bootstrap资源的问题
                // https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css
                //.formLogin(Customizer.withDefaults())
                .formLogin(form -> form
                        // 访问自定义的login page，先访问到/login的Controller方法，再由Controller解析login.html页面
                        .loginPage("/login")
                        // 如果不配置defaultSuccessUrl，那么在登录后会默认跳转 /?continue 路径，这样就要配置根路径 / 对应的资源(Controller)，否则404
                        // 推荐显式配置defaultSuccessUrl，alwaysUse：如果用户之前尝试访问了某个受保护的页面，则跳转到那个页面；否则跳转到指定URL
                        // alwaysUse = true，用户直接访问/admin -> 被拦截跳转到/login -> 登录成功后会跳转/home
                        // alwaysUse = false或缺省，用户直接访问/admin -> 被拦截跳转到/login -> 登录成功后依然跳转/admin
                        .defaultSuccessUrl("/home", false).permitAll()
                )
                // ********** Form Login **********

                // ********** Remember me **********
                // 实现记住我功能
                .rememberMe(rememberMe -> rememberMe
                        .rememberMeServices(customRememberMeService(customUserDetailsService())))
                // ********** Remember me **********

                // ********** Logout **********
                // 默认注销的实现
                //.logout(Customizer.withDefaults())
                .logout(logout -> logout
                        // 指定logout成功后的跳转路径
                        // 这里permitAll是为了直接放行，不需要在authorizeHttpRequests中另外配置
                        .logoutSuccessUrl("/logout/success").permitAll()
                )
                // ********** Logout **********
        ;

        // @formatter:on
        return http.build();
    }

    /**
     * 自定义内存用户
     * {@link org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration}会在如果我们没有自定定义UserDetailsService Bean时执行初始化内存用户
     * 默认用户名是: user，默认密码会输出在控制台中
     * <p>
     * 如果不使用内存存储的方式，也并非一定要用JdbcDaoImpl/JdbcUserDetailsManager来初始化
     * 因为这样做相当于把自己系统的User和SpringSecurity强耦合了
     * <p>
     * 可以采用这种方式：找一个Service来实现UserDetailsService去实现loadUserByUsername
     * 里面的逻辑可以根据我们自己的Dao层的结构来实现
     */
    @Bean
    public UserDetailsService customUserDetailsService() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .roles("USER", "ADMIN")
                .build();
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public RememberMeServices customRememberMeService(UserDetailsService userDetailsService) {
        // 使用基于哈希令牌的处理方法，使用 SHA-256 算法给令牌生成签名、验证签名
        TokenBasedRememberMeServices.RememberMeTokenAlgorithm encodingAlgorithm = TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256;
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices("custom-remember-me", userDetailsService, encodingAlgorithm);
        rememberMe.setMatchingAlgorithm(TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256);
        return rememberMe;
    }
}
