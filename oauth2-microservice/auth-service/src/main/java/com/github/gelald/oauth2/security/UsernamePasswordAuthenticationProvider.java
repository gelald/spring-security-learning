package com.github.gelald.oauth2.security;

import com.github.gelald.oauth2.exception.UsernameOrPasswordErrorException;
import com.github.gelald.oauth2.properties.DecryptProperties;
import com.github.gelald.oauth2.services.impl.AuthServiceImpl;
import com.github.gelald.oauth2.utils.EncryptAndDecrypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */

@Slf4j
@Component
public class UsernamePasswordAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DecryptProperties decryptProperties;
    @Autowired
    private AuthServiceImpl authService;

    public UsernamePasswordAuthenticationProvider() {
        // 有这句，UserNotFountException才不会被SpringSecurity内部消化
        super.hideUserNotFoundExceptions = false;
    }

    protected UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    @Override
    protected void doAfterPropertiesSet() throws Exception {
        Assert.notNull(this.getUserDetailsService(), "A UserDetailsService must be set");
    }

    /**
     * 检索用户信息
     */
    @Override
    protected final UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        String rawUsername;
        if (decryptProperties.getAccount()) {
            rawUsername = EncryptAndDecrypt.decryptAES2(username);
            if (rawUsername == null) {
                throw new RuntimeException("解码错误");
            }
        } else {
            rawUsername = username;
        }
        // 检查用户是否被锁定
        this.authService.checkUserIsLock(rawUsername);
        UserDetails loadedUser = this.getUserDetailsService().loadUserByUsername(rawUsername);
        log.info("[{}]用户登录", loadedUser.getUsername());
        return loadedUser;
    }

    /**
     * 附加检查
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        this.passwordCheck(authentication.getCredentials(), userDetails.getPassword(), userDetails.getUsername());
    }

    /**
     * 密码校验
     * credentials:请求中的密码（可能被加密过）
     * userPassword:数据库中的密码
     * username:数据库中的用户名（明文）
     */
    protected void passwordCheck(Object credentials, String userPassword, String username) {
        if (credentials == null) {
            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "No credentials provided"));
        }
        String decryptedPassword;
        if (decryptProperties.getPassword()) {
            decryptedPassword = EncryptAndDecrypt.decryptAES2(credentials.toString());
            if (decryptedPassword == null) {
                throw new RuntimeException("解码错误");
            }
        } else {
            decryptedPassword = credentials.toString();
        }
        boolean passwordMatch = passwordEncoder.matches(decryptedPassword, userPassword);
        if (!passwordMatch) {
            this.authService.handlePasswordError(username, new UsernameOrPasswordErrorException("密码错误"));
        }
    }
}
