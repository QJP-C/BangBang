package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.UserInfo;
import com.qjp.bang.dto.UserMyInfo;
import com.qjp.bang.dto.UserUpdate;
import com.qjp.bang.entity.User;

import java.util.Map;

/**
 * (User)表服务接口
 *
 * @author makejava
 * @since 2023-04-13 16:44:27
 */

public interface UserService extends IService<User> {
    /**
     * 微信登陆
     * @param code
     * @return
     */
    R wxLogin(String code);

    /**
     * 修改用户信息
     * @param userUpdate
     * @param openid
     * @return
     */
    R updateInfo(UserUpdate userUpdate, String openid);

    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    R send(String phone);

    /**
     * 校验验证码
     * @param openid
     * @param phone
     * @param code
     * @return
     */
    R<String> check(String openid, String phone, String code);

    /**
     * 个人信息
     * @param id
     * @return
     */
    R<UserMyInfo> myInfo(String id);

    /**
     * 他人信息
     * @param id
     * @param toOpenid
     * @return
     */
    R<UserInfo> otherInfo(String id, String toOpenid);

    /**
     * 获取用户资料
     * @param userId
     * @return
     */
    Map<String, String> getOneInfo(String userId);

    /**
     * 查询是否有该用户
     * @param id
     * @return
     */
    boolean haveOne(String id);
}

