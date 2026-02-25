package com.origin.controller;

import com.origin.utils.MonitorUtils;
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
    @Resource
    private MonitorUtils monitorUtils;

    @GetMapping("/hello")
    public String test(){
        String apiName = "/hello";
        try {
            // 埋点：记录接口访问
            monitorUtils.recordApiVisit(apiName);
            // 空项目基础返回（后续可替换为业务逻辑）
            return "Hello Origin! (空项目已加监控埋点)";
        } catch (Exception e) {
            // 埋点：记录接口异常
            monitorUtils.recordApiError(apiName, e.getMessage());
            return "接口异常：" + e.getMessage();
        }
    }
}
