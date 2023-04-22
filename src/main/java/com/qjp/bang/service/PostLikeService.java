package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.entity.PostLike;

/**
 * (PostLike)表服务接口
 *
 * @author makejava
 * @since 2023-04-22 00:34:24
 */
public interface PostLikeService extends IService<PostLike> {
    /**
     * 该用户是否点赞该帖子
     * @param openid
     * @param postId
     * @return
     */
    boolean isLike(String openid, String postId);

    /**
     * 取消点赞
     * @param openid
     * @param postId
     * @return
     */
    boolean removeLike(String openid, String postId);

    /**
     * 点赞
     * @param openid
     * @param postId
     * @return
     */
    boolean likeIt(String openid, String postId);

    /**
     * 获取帖子点赞数
     * @param postId
     * @return
     */
    Long getLikeNum(String postId);

    /**
     * 获取用户获赞数
     * @param id
     * @return
     */
    Long getUserLikeNum(String id);

}

