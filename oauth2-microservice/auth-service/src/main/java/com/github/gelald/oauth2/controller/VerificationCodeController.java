package com.github.gelald.oauth2.controller;

import cn.hutool.core.util.RandomUtil;
import com.github.gelald.oauth2.feign.SmsFeignClient;
import com.github.gelald.oauth2.utils.VerifyCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Slf4j
@RestController
@RequestMapping("/verification-code")
public class VerificationCodeController {
    private final static String CODE_PREFIX = "CODE_KEY_";
    private final static String SMS_TYPE = "login";
    private final SmsFeignClient smsFeignClient;
    private final RedisTemplate<String, Object> redisTemplate;

    public VerificationCodeController(SmsFeignClient smsFeignClient,
                                      RedisTemplate<String, Object> redisTemplate) {
        this.smsFeignClient = smsFeignClient;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/smsCode/{mobile}")
    public String generateVerificationCode(@PathVariable String mobile) {
        //判断上次发送的验证码是否失效
        Object originCode = this.redisTemplate.opsForValue().get(CODE_PREFIX + mobile);
        if (!StringUtils.isEmpty(originCode)) {
            log.error("手机号码: {} 验证码未失效: {}", mobile, originCode);
            throw new RuntimeException("验证码未失效，请失效后再次申请");
        }
        //生成随机数作为手机验证码
        String verificationCode =  RandomUtil.randomNumbers(6);
        log.info("手机号码: {} 验证码: {} 有效期: 60秒", mobile, verificationCode);
        //存进redis
        this.redisTemplate.opsForValue().set(CODE_PREFIX + mobile, verificationCode, 60L, TimeUnit.SECONDS);
        //异步发送验证码,不需等待结果返回
        String smsBody = "{ \"code\": " + verificationCode + "}";
        this.smsFeignClient.send(mobile, SMS_TYPE, smsBody);
        return verificationCode;
    }

    @GetMapping("/authImage")
    public void getAuthImage(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        VerifyCodeUtils.draw(httpServletRequest, httpServletResponse);
    }
}
