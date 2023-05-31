package com.github.gelald.oauth2.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * @author WuYingBin
 * date: 2023/5/18
 */
public class UsernameOrPasswordErrorException extends OAuth2Exception {
    public UsernameOrPasswordErrorException(String message) {
        super(message);
    }
}