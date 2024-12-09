package com.example.inandout.api.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setValues(String refreshToken, Long memberId) {
        System.out.println("RedisService/setValues");
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(refreshToken, memberId);
    }
}
