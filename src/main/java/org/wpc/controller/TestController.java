package org.wpc.controller;

import org.wpc.utils.MonitorUtils;
import org.wpc.utils.RedisCacheGuardUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MonitorUtils monitorUtils;
    @Resource
    private RedisCacheGuardUtils redisCacheGuardUtils;

    @GetMapping("/hello")
    public String hello(){
        String apiName = "/hello";
        String key = "hello_key";
        String result = null;

        try {
            // 监控埋点：记录访问
            monitorUtils.recordApiVisit(apiName);

            // 1. 先查缓存
            result = stringRedisTemplate.opsForValue().get(key);
            if (result != null) {
                return result; // 有缓存直接返回
            }

            // 2. 无缓存：防击穿→加互斥锁
            if (!redisCacheGuardUtils.tryLock(key)) {
                // 没抢到锁：等待50ms后重试（避免直接查数据库）
                Thread.sleep(50);
                return hello(); // 递归重试
            }

            // 3. 抢到锁：查数据库（模拟真实业务）
            String dbResult = "Hello Origin! (高并发防护已生效)";
            // 4. 写入缓存：防雪崩→随机过期时间（基础300秒）
            redisCacheGuardUtils.setCacheWithGuard(key, dbResult, 300);
            result = dbResult;

            // 5. 释放锁
            redisCacheGuardUtils.releaseLock(key);
        } catch (Exception e) {
            // 监控埋点：记录异常
            monitorUtils.recordApiError(apiName, e.getMessage());
            // 异常时释放锁，避免死锁
            redisCacheGuardUtils.releaseLock(key);
            result = "接口异常：" + e.getMessage();
        }
        return result;
    }
}
