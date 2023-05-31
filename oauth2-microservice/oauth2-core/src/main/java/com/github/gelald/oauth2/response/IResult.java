package com.github.gelald.oauth2.response;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
public interface IResult {
    /**
     * 返回结果响应码
     *
     * @return 结果响应码
     */
    Integer getCode();

    /**
     * 返回结果消息
     *
     * @return 结果消息
     */
    String getMessage();
}
