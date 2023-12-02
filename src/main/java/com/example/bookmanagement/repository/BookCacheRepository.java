package com.example.bookmanagement.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Repository
public class BookCacheRepository {

    private final RedisTemplate<String, String> bookRedisTemplate;
    private final int BLOCK_DAYS = 2; // 오늘 포함 3일
    private final Duration TTL = Duration.ofDays(BLOCK_DAYS); // 오늘 포함 3일

    public boolean isDelayedMember(UUID id) {
        String key = getKey(id);
        String value = bookRedisTemplate.opsForValue().get(key);
        return value != null;
    }

    public void setDelayedMember(UUID id) {
        String key = getKey(id);
        String value = LocalDate.now().plusDays(BLOCK_DAYS).toString();
        log.debug("Set Delayed Member = {}:{}", key, value);
        bookRedisTemplate.opsForValue().set(key, value, TTL);
    }

    private static String getKey(UUID id) {
        String key = "MEMBER_ID:" + id;
        return key;
    }
}
