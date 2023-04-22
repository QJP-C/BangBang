package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.PostDetDto;
import com.qjp.bang.dto.PostListResDto;
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

    R<String> collectPost(String openid, String postId);

    /**
     * 按话题查
     * @param openid
     * @param topicId
     * @return
     */
    R<Page<PostListResDto>> pageForTopic(String openid, String topicId, int page, int pageSize);

    /**
     * 关注的用户动态
     * @param openid
     * @param page
     * @param pageSize
     * @return
     */
    R<Page<PostListResDto>> pageByFollow(String openid, int page, int pageSize);
}

