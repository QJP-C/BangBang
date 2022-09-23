package com.qjp.xjbx.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.qjp.xjbx.pojo.User;

public interface UserService extends IService<User> {
    User login (User user);
    boolean check(String token);
}
