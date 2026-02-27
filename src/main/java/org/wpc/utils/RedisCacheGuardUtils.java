package org.wpc.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class RedisCacheGuardUtils {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private final Random random = new Random();

    public Boolean tryLock(String key){
        String lockKey = "lock:" + key;
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1",10, TimeUnit.SECONDS));
    }

    // 释放互斥锁
    public void releaseLock(String key) {
        String lockKey = "lock:" + key;
        stringRedisTemplate.delete(lockKey);
    }

    // 2. 防缓存雪崩：给不同key加随机过期时间（避免大量key同时过期）
    public long getRandomExpireTime(long baseTime) {
        // 基础过期时间±5分钟（比如300秒→270~330秒）
        return baseTime + random.nextInt(600) - 300;
    }
    // 3. 统一的缓存写入方法（集成防护逻辑）
    public void setCacheWithGuard(String key, String value, long baseExpireTime) {
        // 防雪崩：加随机过期时间
        long expireTime = getRandomExpireTime(baseExpireTime);
        stringRedisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
    }

}
