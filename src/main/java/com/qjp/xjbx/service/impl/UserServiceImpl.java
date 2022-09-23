package com.qjp.xjbx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.qjp.xjbx.mapper.UserMapper;
import com.qjp.xjbx.pojo.User;
import com.qjp.xjbx.service.UserService;
import com.qjp.xjbx.utils.HttpRestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public User register(String qq)  {
        {
            try {
                //api url地址
                String url = "https://api.lixingyong.com/api/qq?id="+qq;
                //post请求
//                HttpMethod method = HttpMethod.GET;
                // 封装参数，千万不要替换为Map与HashMap，否则参数无法传递
                MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
                params.add("id", qq);
//                params.add("sex", "男");
//                params.add("age", "27");
//                params.add("address", "Jinan China");
//                params.add("time", new Date().toString());
                System.out.print("发送数据：" + params);
                //发送http请求并返回结果
                String result = HttpRestUtils.get(url, params);
                String[] attribute =result.split("\\,");
                String un= attribute[1];
                String replace = un.replace("\"", "");
                String username= replace.substring(9);
                log.info("Username:[{}]",username);
                String hd =attribute[2];
                String replace1 = hd.replace("\"", "");
                String head = replace1.substring(7);
                log.info("head:[{}]",head);
                System.out.print("接收反馈：" + result);
                User user = new User();
                user.setUsername(username);
                user.setHead(head);
                return user;
            } catch (Exception e) {
                log.info("获取qq头像出现错误");
                log.info(e.getMessage());
                return null;
            }

        }
    }
    @Override
    @Transactional
    public User login(User user) {
        //根据用户名密码
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccount,user.getAccount())
                .eq(User::getPassword,user.getPassword());
        User user1 = userMapper.selectOne(wrapper);
        if (user1 != null) {
                return user1;
        }
        throw new RuntimeException("登录失败~~");
    }

    @Override
    public boolean check(String token) {
        String token1 = (String) redisTemplate.opsForValue().get("token");
        log.info("1:"+token1);
        log.info("0:"+token);
        return Objects.equals(token1, token);
    }
}
