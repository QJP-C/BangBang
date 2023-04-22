package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.entity.PostCollect;
import com.qjp.bang.mapper.PostLikeMapper;
import com.qjp.bang.entity.PostLike;
import com.qjp.bang.service.PostLikeService;
import org.springframework.stereotype.Service;

/**
 * (PostLike)表服务实现类
 *
 * @author makejava
 * @since 2023-04-22 00:34:24
 */
@Service("postLikeService")
public class PostLikeServiceImpl extends ServiceImpl<PostLikeMapper, PostLike> implements PostLikeService {
    /**
     * 该用户是否点赞该帖子
     *
     * @param openid
     * @param postId
     * @return
     */
    @Override
    public boolean isLike(String openid, String postId) {
        LambdaQueryWrapper<PostLike> qw = new LambdaQueryWrapper<>();
        qw.eq(PostLike::getPostId, postId).eq(PostLike::getUserId, openid);
        int count = this.count(qw);
        return count > 0;
    }

    /**
     * 取消点赞
     * @param openid
     * @param postId
     * @return
     */
    @Override
    public boolean removeLike(String openid, String postId) {
        LambdaQueryWrapper<PostLike> qw = new LambdaQueryWrapper<>();
        qw.eq(PostLike::getPostId, postId).eq(PostLike::getUserId, openid);
        return this.remove(qw);
    }

    /**
     * 点赞
     * @param openid
     * @param postId
     * @return
     */
    @Override
    public boolean likeIt(String openid, String postId) {
        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(openid);
        return this.save(postLike);
    }

    /**
     * 获取帖子点赞数
     * @param postId
     * @return
     */
    @Override
    public Long getLikeNum(String postId) {
        LambdaQueryWrapper<PostLike> qw = new LambdaQueryWrapper<>();
        qw.eq(PostLike::getPostId, postId);
        return (long) this.count(qw);
    }

    /**获取用户获赞数
     * @param id
     * @return
     */
    @Override
    public Long getUserLikeNum(String id) {
        LambdaQueryWrapper<PostLike> qw = new LambdaQueryWrapper<>();
        qw.eq(PostLike::getUserId, id);
        return (long) this.count(qw);
    }


}

