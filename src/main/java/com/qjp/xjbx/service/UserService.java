package com.qjp.xjbx.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.qjp.xjbx.pojo.User;

import java.io.IOException;

public interface UserService extends IService<User> {
    User register(String qq) throws IOException;
    User login (User user);
    boolean check(String token);
}
