package com.example.inandout.api.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class ValueRedisRepository {
    private final StringRedisTemplate redisTemplate;

    public void saveValueWithExpiry(String key, String value, long expireTime) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(expireTime);
        valueOperations.set(key, value, expireDuration);
    }

    public void deleteRefreshToken(String refreshToken){
        redisTemplate.opsForValue().getAndDelete(refreshToken);
    }

    public Long getValue(String refreshToken){
        return Long.parseLong(redisTemplate.opsForValue().get(refreshToken));
    }
}
