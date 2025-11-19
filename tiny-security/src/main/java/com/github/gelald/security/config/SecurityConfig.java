package com.github.gelald.security.config;

import jakarta.servlet.DispatcherType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

@Slf4j
@Configuration
// 这个注解可以不写，因为SpringBoot做了自动配置，只要classpath下引入 spring-boot-starter-security，SpringBoot就会自动尝试配置安全相关组件
// 显式信号，表明这个配置类是和安全相关的
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .csrf(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        // 对于Controller中转发Thymeleaf渲染的模板进行放行
                        // 对于Controller中抛出的异常进行放行
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .requestMatchers("/public/**", "/resource/**").permitAll()
                        .anyRequest().authenticated())

                // ********** Http Basic Login **********
                // 交互体验不好，一般只用于快速测试，不建议使用
                //.httpBasic(Customizer.withDefaults())
                // ********** Http Basic Login **********

                // ********** Form Login **********
                // login 流程重点关注：UsernamePasswordAuthenticationFilter
                // 1. 调用AuthenticationManager的authenticate方法，实际调用的实现类是ProviderManager
                // 2. ProviderManager维护了一个AuthenticationProvider的集合，会遍历它们去尝试每一个AuthenticationProvider
                // 3. 只有AuthenticationProvider的supports方法返回true，才能进行进一步的认证工作
                // 4. 最终DaoAuthenticationProvider满足要求，它会调用UserDetailsService来调用loadUserByUsername
                // 5. 找到User后会执行其他校验，比如账号状态等，还会执行密码的校验，所有校验都通过后会构建UsernamePasswordAuthenticationToken返回
                // 6. 最终UsernamePasswordAuthenticationFilter拿到认证结果后会构建SecurityContext并存入SecurityContextHolder，还会设置HTTP Session
                .formLogin(form -> form
                        // 访问自定义的login page，先访问到/login的Controller方法，再由Controller解析login.html页面
                        .loginPage("/login")
                        // 如果不配置defaultSuccessUrl，那么在登录后会默认跳转 /?continue 路径，这样就要配置根路径 / 对应的资源(Controller)，否则404
                        // 推荐显式配置defaultSuccessUrl，alwaysUse：如果用户之前尝试访问了某个受保护的页面，则跳转到那个页面；否则跳转到指定URL
                        // alwaysUse = true，用户直接访问/admin -> 被拦截跳转到/login -> 登录成功后会跳转/home
                        // alwaysUse = false或缺省，用户直接访问/admin -> 被拦截跳转到/login -> 登录成功后依然跳转/admin
                        .defaultSuccessUrl("/home", false).permitAll()
                )
                //.formLogin(Customizer.withDefaults())
                // ********** Form Login **********

                // ********** Remember me **********
                // 实现记住我功能重点关注：RememberMeAuthenticationFilter
                // 1. 如果发现SecurityContextHolder中的Authentication是null，那么才尝试进行RememberMe的autoLogin
                // 2. 获取remember-me cookie，从cookie中获取username，调用UserDetailsService来调用loadUserByUsername
                // 3. 根据预定义的RememberMe的生成、校验签名算法来生成签名，只有签名和cookie上的签名一致，才算通过autoLogin
                // 4. 最终构建出来一个RememberMeAuthenticationToken
                // 5. 后续流程和login流程类似
                .rememberMe(rememberMe -> rememberMe
                        .rememberMeServices(customRememberMeService(customUserDetailsService())))
                //.rememberMe(Customizer.withDefaults())
                // ********** Remember me **********

                // ********** Logout **********
                // logout 流程重点关注：LogoutFilter
                // 1. 从SecurityContextHolder中获取Authentication
                // 2. CompositeLogoutHandler维护了一系列LogoutHandler的实现类，会拿着这个Authentication执行logout的逻辑
                // 3. 其中重点关注两个LogoutHandler：CookieClearingLogoutHandler，如果logout逻辑中自定义了删除cookie的逻辑，会走这个handler
                // 另外一个LogoutHandler：SecurityContextLogoutHandler，核心工作：session.invalidate()、securityContextHolderStrategy.clearContext()
                // 让session失效，并清除SecurityContext
                .logout(logout -> logout
                        // 指定logout成功后的跳转路径
                        // 这里permitAll是为了直接放行，不需要在authorizeHttpRequests中另外配置
                        .logoutSuccessUrl("/logout/success").permitAll()
                        // 如果不做重定向，也可以配置只返回一个状态码，不过一般都会用重定向的方式
                        //.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                        // 可以设置logout时删除哪些cookies
                        //.deleteCookies("remember-me")
                        // 可以设置logout时清除网站数据，比如cookies、storage等
                        //.addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.COOKIES)))
                )
                //.logout(Customizer.withDefaults())
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
