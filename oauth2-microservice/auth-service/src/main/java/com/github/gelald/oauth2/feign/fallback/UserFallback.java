package com.github.gelald.oauth2.feign.fallback;

import com.github.gelald.oauth2.dto.UserDTO;
import com.github.gelald.oauth2.feign.UserFeignClient;
import com.github.gelald.oauth2.response.Result;
import org.springframework.stereotype.Component;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Component
public class UserFallback implements UserFeignClient {
    @Override
    public Result<UserDTO> loadAccountByUsername(String username) {
        return Result.failed();
    }

    @Override
    public Result<UserDTO> loadAccountByMobile(String mobile) {
        return Result.failed();
    }

    @Override
    public Result<UserDTO> registry(String username, String password) {
        return Result.failed();
    }
}