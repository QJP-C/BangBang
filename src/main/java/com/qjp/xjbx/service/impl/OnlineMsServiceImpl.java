package com.qjp.xjbx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.xjbx.mapper.OnlineMsMapper;
import com.qjp.xjbx.pojo.OnlineMs;
import com.qjp.xjbx.pojo.User;
import com.qjp.xjbx.service.OnlineMsService;
import com.qjp.xjbx.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OnlineMsServiceImpl extends ServiceImpl<OnlineMsMapper, OnlineMs> implements OnlineMsService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    @Override
    public String message(String from, String to, String message) {
        log.info("from:{}",from);
        log.info("to:{}",to);
        log.info("message:{}",message);
        String m ="{\"to\":\""+ to +"\",\"message\":\""+message+"\"}";
        String now = String.valueOf(LocalDateTime.now());
        System.out.println(m);
        redisTemplate.opsForHash().put("M"+from,now,m);
        return "已存入Redis缓存";
    }

    @Override
    public String offline(String from, String to, String message) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId,to);
        User one = userService.getOne(wrapper);
        LambdaQueryWrapper<User> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(User::getId,from);
        User one1 = userService.getOne(wrapper1);
        if (null!=one1) {
            OnlineMs onlineMs = new OnlineMs();
            onlineMs.setToId(to);
            onlineMs.setLastContext(message);
            onlineMs.setFromId(from);
            onlineMs.setSendTime(LocalDateTime.now());
            onlineMs.setIsRead(0);
            this.save(onlineMs);
            log.info("离线消息已存储");
            return "离线消息已存储";
        }
        return "该用户不存在！";

    }
    @Override
    public  void fa(String id,String msg){

    }

}
