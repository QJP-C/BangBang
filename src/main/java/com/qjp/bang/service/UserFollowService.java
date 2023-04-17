package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.common.R;
import com.qjp.bang.entity.UserFollow;

/**
 * (UserFollow)表服务接口
 *
 * @author makejava
 * @since 2023-04-15 20:58:50
 */
public interface UserFollowService extends IService<UserFollow> {
    R<String> follow(String toId, String openid);

}

