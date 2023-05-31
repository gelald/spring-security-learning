package com.github.gelald.oauth2.services.impl;

import com.github.gelald.oauth2.constant.RedisConstant;
import com.github.gelald.oauth2.exception.UsernameOrPasswordErrorException;
import com.github.gelald.oauth2.response.Result;
import com.github.gelald.oauth2.response.ResultEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */

@Service
public class AuthServiceImpl {
    /**
     * 记录密码错误次数的单位时间
     * 如从第一次输入错误后，从此刻起10分钟内记录后续错误的次数
     */
    private static final Integer PERIOD_OF_RECORD_ERROR = 10;
    /**
     * 锁定用户的时间
     */
    private static final Integer LOCK_TIME = 10;

    private final RedisTemplate<String, Object> redisTemplate;

    public AuthServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 检查用户是否被锁定
     *
     * @param username 用户名
     */
    public void checkUserIsLock(String username) {
        String userLockKey = RedisConstant.USER_LOCK_KEY_PREFIX + username;
        Boolean isUserLock = (Boolean) this.redisTemplate.opsForValue().get(userLockKey);
        if (Boolean.TRUE.equals(isUserLock)) {
            // -1: 永久， -2: 不存在该值， 其他: 过期时间
            Long expire = this.redisTemplate.opsForValue().getOperations().getExpire(userLockKey);
            if (expire != null) {
                if (expire > 0 && expire < 120) {
                    throw new RuntimeException("用户已被锁定，请在" + expire + "秒后重新登录");
                } else if (expire >= 120) {
                    long expireMinute = expire / 60;
                    throw new RuntimeException("用户已被锁定，请在" + expireMinute + "分钟后重新登录");
                } else if (expire == -1) {
                    throw new RuntimeException("用户已被锁定，请联系管理员解锁");
                }
            }
        }
    }

    /**
     * 检查验证码是否正确
     *
     * @param codeFromFront      用户输入的验证码
     * @param httpServletRequest 用于获取系统生成的验证码
     */
    public void checkVerifyCode(String codeFromFront, HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(true);
        String codeInSession = (String) session.getAttribute("code");
        session.removeAttribute("code");
        if (codeInSession == null) {
            throw new RuntimeException("验证码过期,请刷新");
        }
        if (!(codeInSession.equalsIgnoreCase(codeFromFront))) {
            throw new RuntimeException("验证码错误");
        }
    }

    /**
     * 处理密码输入错误的异常
     *
     * @param username                         用户名
     * @param usernameOrPasswordErrorException 密码输入错误异常
     * @return 10分钟内，同一用户名连续输错5次密码将把用户锁定
     */
    public Result<?> handlePasswordError(String username, UsernameOrPasswordErrorException usernameOrPasswordErrorException) {
        // 10分钟内输错5次就锁定10分钟
        String userLockKey = RedisConstant.USER_LOCK_KEY_PREFIX + username;
        String userPasswordErrorKey = RedisConstant.USER_PASSWORD_ERROR_KEY_PREFIX + username;
        Integer passwordErrorTimes = (Integer) this.redisTemplate.opsForValue().get(userPasswordErrorKey);
        if (passwordErrorTimes != null) {
            // 如果有记录，那就往上累加
            if (passwordErrorTimes < 4) {
                // 如果记录次数小于4次，就继续累加
                int increasedTimes = passwordErrorTimes + 1;
                int leftTimes = 5 - increasedTimes;
                this.redisTemplate.opsForValue().set(userPasswordErrorKey, increasedTimes, 0);
                return Result.failed(ResultEnum.AUTHENTICATION_FAILED, usernameOrPasswordErrorException.getMessage() + leftTimes + "次错误后用户将锁定" + LOCK_TIME + "分钟");
            } else {
                // 如果记录等于4次(不可能大于4次)，就锁定
                this.redisTemplate.opsForValue().set(userLockKey, true, LOCK_TIME, TimeUnit.MINUTES);
                this.redisTemplate.opsForValue().getOperations().delete(userPasswordErrorKey);
                return Result.failed(ResultEnum.AUTHENTICATION_FAILED, "用户已被锁定，请于" + LOCK_TIME + "分钟后重试");
            }
        } else {
            // 如果没有记录，那么就开始记录
            this.redisTemplate.opsForValue().set(userPasswordErrorKey, 1, PERIOD_OF_RECORD_ERROR, TimeUnit.MINUTES);
            return Result.failed(ResultEnum.AUTHENTICATION_FAILED, usernameOrPasswordErrorException.getMessage() + "4次错误后用户将锁定" + LOCK_TIME + "分钟");
        }
    }
}
