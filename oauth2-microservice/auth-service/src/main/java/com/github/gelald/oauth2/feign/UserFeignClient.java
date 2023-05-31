package com.github.gelald.oauth2.feign;

import com.github.gelald.oauth2.dto.UserDTO;
import com.github.gelald.oauth2.feign.fallback.UserFallbackFactory;
import com.github.gelald.oauth2.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Component
@FeignClient(value = "user-service", fallbackFactory = UserFallbackFactory.class)
public interface UserFeignClient {
    @GetMapping("/user/loadAccountByUsername")
    Result<UserDTO> loadAccountByUsername(@RequestParam("username") String username);

    @GetMapping("/user/loadAccountByMobile")
    Result<UserDTO> loadAccountByMobile(@RequestParam("mobile") String mobile);

    @GetMapping("/user/registry")
    Result<UserDTO> registry(@RequestParam("username") String username,
                             @RequestParam("password") String password);
}
