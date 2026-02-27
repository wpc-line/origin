package org.wpc.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

// 监控工具类（埋点核心）
@Component
public class MonitorUtils {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 1. 接口访问次数埋点（统计QPS/访问量）
    public void recordApiVisit(String apiName) {
        // 用Redis计数：key=api:visit:接口名，value=访问次数
        String key = "api:visit:" + apiName;
        // 自增计数，24小时过期（避免Redis数据堆积）
        stringRedisTemplate.opsForValue().increment(key, 1);
        stringRedisTemplate.expire(key, 24, TimeUnit.HOURS);
    }

    // 2. 异常记录埋点（统计接口报错）
    public void recordApiError(String apiName, String errorMsg) {
        // 用Redis存储异常：key=api:error:接口名，value=异常信息（最新一条）
        String key = "api:error:" + apiName;
        stringRedisTemplate.opsForValue().set(key, errorMsg, 1, TimeUnit.HOURS);
    }
}