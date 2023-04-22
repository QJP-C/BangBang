package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.UserInfo;
import com.qjp.bang.dto.UserMyInfo;
import com.qjp.bang.dto.UserUpdate;
import com.qjp.bang.entity.User;

/**
 * (User)表服务接口
 *
 * @author makejava
 * @since 2023-04-13 16:44:27
 */

public interface UserService extends IService<User> {
    R wxLogin(String code);

    R updateInfo(UserUpdate userUpdate, String openid);

    R send(String phone);

    R<String> check(String openid, String phone, String code);

    R<UserMyInfo> myInfo(String id);
    R<UserInfo> otherInfo(String id, String toOpenid);


    String getOneHead(String userId);
}

