package com.qjp.xjbx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.qjp.xjbx.mapper.UserMapper;
import com.qjp.xjbx.pojo.User;
import com.qjp.xjbx.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Resource
    private RedisTemplate redisTemplate;
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
