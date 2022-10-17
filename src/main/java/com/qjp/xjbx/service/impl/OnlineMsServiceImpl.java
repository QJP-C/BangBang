package com.qjp.xjbx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.xjbx.mapper.OnlineMsMapper;
import com.qjp.xjbx.pojo.OnlineMs;
import com.qjp.xjbx.service.OnlineMsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class OnlineMsServiceImpl extends ServiceImpl<OnlineMsMapper, OnlineMs> implements OnlineMsService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public String message(String from, String to, String message) {
        log.info("from:{}",from);
        log.info("to:{}",to);
        log.info("message:{}",message);
        String m ="{\"to\":\""+ to +"\",\"message\":\""+message+"\"}";
        String now = String.valueOf(LocalDateTime.now());
        System.out.println(m);
        redisTemplate.opsForHash().put("M"+from,now,m);


        return null;
    }

}
