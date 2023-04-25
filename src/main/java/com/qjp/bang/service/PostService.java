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
     * 按话题查(热门)
     * @param openid
     * @param topicId
     * @return
     */
    R<Page<PostListResDto>> pageByHotTopic(String openid, String topicId, int page, int pageSize);

    /**
     * 关注的用户动态
     * @param openid
     * @param max
     * @param offset
     * @param pageSize
     * @return
     */
    R queryPostOfFollow(String openid, Long max, Integer offset, Integer pageSize);

    /**
     * 按话题查(新帖)
     * @param openid
     * @param topicId
     * @param page
     * @param pageSize
     * @return
     */
    R<Page<PostListResDto>> pageByNewTopic(String openid, String topicId, int page, int pageSize);

    /**
     * 推荐
     *
     * @param openid
     * @param page
     * @param pageSize
     * @return
     */
    R queryPostOfRecommend(String openid, int page, int pageSize);

    /**
     * 图文
     * @param openid
     * @param page
     * @param pageSize
     * @return
     */
    R queryPostOfImageText(String openid, int page, int pageSize);

    /**
     * 个人动态
     * @param openid
     * @param page
     * @param pageSize
     * @return
     */
    R queryPostOfPersonal(String openid, int page, int pageSize);

    /**
     * 我的收藏(帖子)
     * @param openid
     * @param page
     * @param pageSize
     * @return
     */
    R queryPostOfMyCollect(String openid, int page, int pageSize);

    /**
     * 评论帖子
     * @param openid
     * @param postId
     * @return
     */
    R commentPost(String openid, String postId);
}

