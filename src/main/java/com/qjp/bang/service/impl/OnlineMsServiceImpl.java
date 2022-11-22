package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.mapper.OnlineMsMapper;
import com.qjp.bang.pojo.OnlineMs;
import com.qjp.bang.pojo.User;
import com.qjp.bang.service.OnlineMsService;
import com.qjp.bang.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qjp
 */
@Slf4j
@Service
public class OnlineMsServiceImpl extends ServiceImpl<OnlineMsMapper, OnlineMs> implements OnlineMsService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    @Override
    public String message(String from, String to, String message) {
        Object o = redisTemplate.opsForHash().get("M" + from, to);
        log.info("o:{}",o);
        if (o != null){
            List<String>  list = (List<String>) o;
            log.info("list:{}",list);
            String now = String.valueOf(LocalDateTime.now());
//            String m ="{time:"+ now +",message:"+message+"}";
            String m ="{\"time\":\""+ now +"\",\"message\":\""+message+"\"}";
            list.add(m);
            redisTemplate.opsForHash().put("M"+from,to,list);
        }else {
            log.info("from:{}",from);
            log.info("to:{}",to);
            log.info("message:{}",message);
            String now = String.valueOf(LocalDateTime.now());
//            String m ="{time:"+ now +",message:"+message+"}";
            String m ="{\"time\":\""+ now +"\",\"message\":\""+message+"\"}";
            ArrayList<String> list = new ArrayList<>();
            list.add(m);
            System.out.println(m);
            redisTemplate.opsForHash().put("M"+from,to,list);
            return "已存入Redis缓存";
        }
        return null;
    }

    @Override
    public String offline(String from, String to, String message) {

        User one = userService.getById(to);
        User one1 = userService.getById(from);
        if (null!=one1) {
            OnlineMs onlineMs = new OnlineMs();
            onlineMs.setFromHead(one1.getHead());
            onlineMs.setFromName(one1.getUsername());
            onlineMs.setToHead(one.getHead());
            onlineMs.setToName(one.getUsername());
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
