package com.github.gelald.oauth2.security;

import com.github.gelald.oauth2.services.UserDetailsServiceCustom;
import com.github.gelald.oauth2.token.SmsVerificationCodeAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */

@Slf4j
@Component
public class SmsVerificationCodeAuthenticationProvider implements AuthenticationProvider, MessageSourceAware {
    private static final String GRANT_TYPE = "sms_code";
    private final static String CODE_PREFIX = "CODE_KEY_";
    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private final UserDetailsServiceCustom userDetailsServiceCustom;
    private final RedisTemplate<String, Object> redisTemplate;

    public SmsVerificationCodeAuthenticationProvider(UserDetailsServiceCustom userDetailsServiceCustom,
                                                     RedisTemplate<String, Object> redisTemplate) {
        this.userDetailsServiceCustom = userDetailsServiceCustom;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("手机验证码认证方式......");
        String mobile = (String) authentication.getPrincipal();
        if (StringUtils.isEmpty(mobile)) {
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "请输入手机号码"));
        }
        String code = (String) authentication.getCredentials();
        if (StringUtils.isEmpty(code)) {
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "请输入验证码"));
        }
        //从redis查询验证码
        Object cacheCode = this.redisTemplate.opsForValue().get(CODE_PREFIX + mobile);
        if (StringUtils.isEmpty(cacheCode) || !cacheCode.equals(code)) {
            log.error("短信验证码错误");
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "验证码错误"));
        }
        UserDetails userDetails;
        try {
            userDetails = this.userDetailsServiceCustom.loadUserByMobile(mobile);
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", e.getMessage()));
        }
        SmsVerificationCodeAuthenticationToken authenticationToken = new SmsVerificationCodeAuthenticationToken(userDetails, code, userDetails.getAuthorities());
        authenticationToken.setDetails(authenticationToken.getDetails());
        //删除redis中的验证码
        this.redisTemplate.delete(CODE_PREFIX + mobile);
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 判断authentication是否是SmsVerificationCodeAuthenticationToken类型
        return SmsVerificationCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }
}
