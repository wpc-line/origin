package com.origin.controller;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class TestController {

    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("/hello")
    public String test(){
        String key = "hello_key";
        // 1. 先查缓存
        String result = (String)redisTemplate.opsForValue().get(key);

        // 2. 缓存穿透防护：缓存空值
        if (result == null) {
            // 查数据库（模拟真实业务）
            String dbResult = "Hello Redis!";

            // 3. 缓存加过期时间（300秒=5分钟，避免数据永久有效）
            if (dbResult == null) {
                // 空值也缓存，设置短一点的过期时间（60秒）
                redisTemplate.opsForValue().set(key, "", 60, TimeUnit.SECONDS);
            } else {
                // 正常数据缓存，设置5分钟过期
                redisTemplate.opsForValue().set(key, dbResult, 300, TimeUnit.SECONDS);
            }
            result = dbResult;
        }
        return result;
    }
}
