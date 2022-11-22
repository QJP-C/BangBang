package com.qjp.bang.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.qjp.bang.mapper.UserMapper;
import com.qjp.bang.pojo.User;
import com.qjp.bang.service.UserService;
import com.qjp.bang.utils.HttpRestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;

/**
 * @author qjp
 */
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
                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                System.out.print("发送数据：" + params);
                //发送http请求并返回结果
                String result = HttpRestUtils.get(url, params);
                JSONObject jsonObject = JSON.parseObject(result);
                String username= jsonObject.getString("nickname");
                log.info("Username:[{}]",username);
                String head = "http://q1.qlogo.cn/g?b=qq&nk="+qq+"&s=640";
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


}
