package com.example.bookmanagement.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

@SpringBootTest
class BookCacheRepositoryTest {

    @Autowired
    BookCacheRepository bookCacheRepository;

    @Autowired
    RedisTemplate<String, String> stringRedisTemplate;

    @DisplayName("레디스 데이터 저장 테스트 ")
    @Test
    void setTest() {
        UUID uuid = UUID.randomUUID();
        bookCacheRepository.setDelayedMember(uuid);

        String result = stringRedisTemplate.opsForValue().get("MEMBER_ID:" + uuid);
        Assertions.assertNotNull(result);
    }

}