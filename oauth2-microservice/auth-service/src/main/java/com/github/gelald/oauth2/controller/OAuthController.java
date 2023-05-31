package com.github.gelald.oauth2.controller;

import com.github.gelald.oauth2.dto.UserDTO;
import com.github.gelald.oauth2.properties.DecryptProperties;
import com.github.gelald.oauth2.response.Result;
import com.github.gelald.oauth2.response.ResultEnum;
import com.github.gelald.oauth2.services.UserDetailsServiceCustom;
import com.github.gelald.oauth2.services.impl.AuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.security.Principal;
import java.util.Map;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */

@Slf4j
@RestController
@RequestMapping("/oauth")
public class OAuthController {
    @Autowired
    private TokenEndpoint tokenEndpoint;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private DecryptProperties decryptProperties;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsServiceCustom userDetailsServiceCustom;


    @PostMapping("/login")
    public Result<?> authenticate(Principal principal,
                                  @RequestParam Map<String, String> parameters,
                                  HttpServletRequest httpServletRequest) {
        try {
            /*if (parameters.containsKey("code")) {
                // 如果有验证码则检查验证码是否正确
                String codeFromFront;
                if (decryptProperties.getCode()) {
                    codeFromFront = EncryptAndDecrypt.decryptAES2(parameters.get("code"));
                } else {
                    codeFromFront = parameters.get("code");
                }
                this.authService.checkVerifyCode(codeFromFront, httpServletRequest);
            }*/
            // 获取token
            final OAuth2AccessToken oAuth2AccessToken = this.tokenEndpoint.postAccessToken(principal, parameters).getBody();
            return Result.success(oAuth2AccessToken);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.info("认证失败: {}", e.getMessage());
            }
            return Result.failed(ResultEnum.AUTHENTICATION_FAILED, e.getMessage());
        }
    }

    @PostMapping("/registry")
    public UserDTO registry(@NotBlank @RequestParam("username") String username,
                            @NotBlank @RequestParam("password") String password) {
        return this.userDetailsServiceCustom.registry(username, password);
    }
}