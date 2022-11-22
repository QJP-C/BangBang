package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.qjp.bang.pojo.User;

import java.io.IOException;

/**
 * @author qjp
 */
public interface UserService extends IService<User> {
    User register(String qq) throws IOException;

}
