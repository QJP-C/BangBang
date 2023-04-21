package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.PostDetDto;
import com.qjp.bang.dto.PostNewParamDto;
import com.qjp.bang.entity.Post;

/**
 * (Post)表服务接口
 *
 * @author makejava
 * @since 2023-04-21 20:44:28
 */
public interface PostService extends IService<Post> {
    /**
     * 发布帖子
     * @param openid
     * @param postNewParamDto
     * @return
     */
    R<String> savePost(String openid, PostNewParamDto postNewParamDto);

    /**
     * 帖子详情
     * @param openid
     * @param postId
     * @return
     */
    R<PostDetDto> onePost(String openid, String postId);

    /**
     * 点赞/取消帖子
     * @param openid
     * @param postId
     * @return
     */
    R<String> likePost(String openid, String postId);
}

