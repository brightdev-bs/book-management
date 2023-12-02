package com.example.bookmanagement.repository;

import com.example.bookmanagement.entity.Member;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Repository
public class BookCacheRepository {

    private final RedisTemplate<String, String> bookRedisTemplate;
    private final int BLOCK_DAYS = 3;
    private final Duration TTL = Duration.ofDays(BLOCK_DAYS);

    public void setDelayedMember(UUID id) {
        String key = String.valueOf(id);
        String value = LocalDate.now().plusDays(BLOCK_DAYS).toString();
        log.debug("Set Delayed Member = {}:{}", key, value);
        bookRedisTemplate.opsForValue().set(key, value, TTL);
    }
}
