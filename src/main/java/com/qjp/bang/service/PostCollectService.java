package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.entity.PostCollect;

import java.util.List;

/**
 * (PostCollect)表服务接口
 *
 * @author makejava
 * @since 2023-04-22 16:06:27
 */
public interface PostCollectService extends IService<PostCollect> {
    /**
     * 判断该用户是否已收藏
     * @param openid
     * @param postId
     * @return
     */
    boolean isCollect(String openid, String postId);

    /**
     * 取消收藏
     * @param openid
     * @param postId
     * @return
     */
    boolean removeCollect(String openid, String postId);

    /**
     * 收藏
     * @param openid
     * @param postId
     * @return
     */
    boolean collectIt(String openid, String postId);

    /**
     * 获取用户收藏的帖子的id集合
     * @param openid
     * @return
     */
    List<String> myCollects(String openid);
}

