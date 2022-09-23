package com.qjp.xjbx;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
class XjbxApplicationTests {
@Resource
private StringRedisTemplate stringRedisTemplate;
    @Test
    void contextLoads() {
        String token1 = stringRedisTemplate.opsForValue().get("token");
        System.out.println(token1);
    }

}
