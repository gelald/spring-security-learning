package com.github.gelald.oauth2.feign.fallback;

import com.github.gelald.oauth2.feign.UserFeignClient;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Component
public class UserFallbackFactory implements FallbackFactory<UserFeignClient> {
    @Override
    public UserFeignClient create(Throwable throwable) {
        throwable.printStackTrace();
        return new UserFallback();
    }
}