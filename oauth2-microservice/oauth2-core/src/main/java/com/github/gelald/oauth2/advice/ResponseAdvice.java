package com.github.gelald.oauth2.advice;

import com.github.gelald.oauth2.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Slf4j
@RestControllerAdvice(basePackages = {
        "com.github.gelald.oauth2",
        "org.springframework.security.oauth2.provider.endpoint"
})
public class ResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        //如果在Controller中显式地使用CommonResult来包装结果，那么就不再包装
        if ((Result.class).equals(returnType.getParameterType())) {
            return false;
        }
        //公钥结果不包装，否则网关获取公钥异常，或者网关获取逻辑调整
        if ("net.minidev.json.JSONObject".equals(returnType.getParameterName())) {
            return false;
        }
        //OAuth2Exception不拦截，由认证服务自己拦截处理
        if (returnType.getGenericParameterType().toString().contains("OAuth2Exception")) {
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        return Result.success(body);
    }
}