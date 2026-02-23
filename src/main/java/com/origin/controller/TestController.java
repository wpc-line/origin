package com.origin.controller;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("/hello")
    public String test(){
        return "hello";
    }

    @GetMapping("/redis-test")
    public String redistTest() {
        // 先查缓存，没有再查数据库（这里用字符串模拟）
        String cache = (String)redisTemplate.opsForValue().get("hello_key");
        if (cache != null) {
            return "缓存返回：" + cache;
        }
        // 模拟数据库查询
        String result = "Hello Architecture!";
        // 存入缓存，过期时间10分钟
        redisTemplate.opsForValue().set("hello_key", result, 10, TimeUnit.MINUTES);
        return "数据库返回：" + result;
    }
}
