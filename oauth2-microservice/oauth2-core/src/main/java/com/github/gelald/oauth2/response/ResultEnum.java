package com.github.gelald.oauth2.response;

import lombok.Getter;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Getter
public enum ResultEnum implements IResult {
    SUCCESS(200, "请求成功"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有访问权限"),
    VALIDATE_FAILED(411, "参数检验失败"),
    FAILED(500, "服务端异常"),
    AUTHENTICATION_FAILED(511, "认证失败");

    private final Integer code;
    private final String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
