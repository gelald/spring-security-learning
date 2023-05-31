package com.github.gelald.oauth2.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Component
@FeignClient("sms-service")
public interface SmsFeignClient {
    @PostMapping("/sms/send/{type}/{phone}")
    ResponseEntity<Object> send(@PathVariable(name = "phone") String phone,
                                @PathVariable("type") String type,
                                @RequestBody String body);
}
