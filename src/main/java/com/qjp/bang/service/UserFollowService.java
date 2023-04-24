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
    /**
     * 关注/取关用户
     * @param toId
     * @param openid
     * @return
     */
    R<String> follow(String toId, String openid);

    /**
     * 获取用户关注数
     * @param id
     * @return
     */
    Long userFollowNum(String id);

    /**
     * 获取用户粉丝数
     * @param id
     * @return
     */
    Long userFansNum(String id);

    /**
     * 是否已关注
     * @param userId
     * @param openid
     * @return
     */
    boolean isFollow(String userId, String openid);

    /**
     * 获取关注的用户ids
     * @param openid
     * @return
     */
    String[] getIdByFollow(String openid);

    /**
     * 我的关注
     * @param openid
     * @return
     */
    R myFollow(String openid);

    /**
     * 我的粉丝
     * @param openid
     * @return
     */
    R myFans(String openid);
}

