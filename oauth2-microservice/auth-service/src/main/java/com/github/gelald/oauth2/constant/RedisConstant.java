package com.github.gelald.oauth2.constant;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
public class RedisConstant {

    public static final String RESOURCE_ROLES_MAP = "AUTH:RESOURCE_ROLES_MAP";
    /**
     * 记录密码错误的redis键前缀
     */
    public static final String USER_PASSWORD_ERROR_KEY_PREFIX = "pwer_";
    /**
     * 记录锁定账号的redis键前缀
     */
    public static final String USER_LOCK_KEY_PREFIX = "lock_";

}
