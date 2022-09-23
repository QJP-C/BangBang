package com.qjp.xjbx.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component//项目运行时就注入Spring容器
public class RedisUtil {
    @Autowired
    private  RedisTemplate redisTemplate;

    //赋值一个静态的redis
    public static RedisTemplate redis;

    @PostConstruct //此注解表示构造时赋值
    public void redisTemplate() {
        redis = this.redisTemplate;
    }
}