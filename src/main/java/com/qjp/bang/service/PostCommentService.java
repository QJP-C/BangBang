package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.entity.PostComment;

/**
 * (PostComment)表服务接口
 *
 * @author makejava
 * @since 2023-04-25 21:49:23
 */
public interface PostCommentService extends IService<PostComment> {
    /**
     * 评论帖子
     * @param openid
     * @param postId
     * @param text
     * @return
     */
    boolean commentPost(String openid, String postId, String text);


}

