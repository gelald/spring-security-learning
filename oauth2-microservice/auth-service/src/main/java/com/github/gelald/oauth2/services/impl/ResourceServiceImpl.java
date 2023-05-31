package com.github.gelald.oauth2.services.impl;

import cn.hutool.core.collection.CollUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */

@Service
public class ResourceServiceImpl {

    private Map<String, List<String>> resourceRolesMap;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void initData() {
        resourceRolesMap = new TreeMap<>();
        resourceRolesMap.put("/**", CollUtil.toList("ADMIN", "USER"));
        resourceRolesMap.put("/hello/sayHi", CollUtil.toList("ADMIN", "TEST", "USER"));
//        redisTemplate.opsForHash().putAll(RedisConstant.RESOURCE_ROLES_MAP, resourceRolesMap);
    }
}
