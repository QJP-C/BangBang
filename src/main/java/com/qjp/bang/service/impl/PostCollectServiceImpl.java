package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.mapper.PostCollectMapper;
import com.qjp.bang.entity.PostCollect;
import com.qjp.bang.service.PostCollectService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * (PostCollect)表服务实现类
 *
 * @author makejava
 * @since 2023-04-22 16:06:27
 */
@Service("postCollectService")
public class PostCollectServiceImpl extends ServiceImpl<PostCollectMapper, PostCollect> implements PostCollectService {
    /**
     * 判断该用户是否已收藏
     * @param openid
     * @param postId
     * @return
     */
    @Override
    public boolean isCollect(String openid, String postId) {
        LambdaQueryWrapper<PostCollect> qw = new LambdaQueryWrapper<>();
        qw.eq(PostCollect::getPostId,postId).eq(PostCollect::getUserId,openid);
        return this.count()>0;
    }

    /**
     * 取消收藏
     * @param openid
     * @param postId
     * @return
     */
    @Override
    public boolean removeCollect(String openid, String postId) {
        LambdaQueryWrapper<PostCollect> qw = new LambdaQueryWrapper<>();
        qw.eq(PostCollect::getPostId,postId).eq(PostCollect::getUserId,openid);
        return this.remove(qw);
    }

    /**
     * 收藏
     * @param openid
     * @param postId
     * @return
     */
    @Override
    public boolean collectIt(String openid, String postId) {
        PostCollect postCollect = new PostCollect();
        postCollect.setPostId(postId);
        postCollect.setUserId(openid);
        postCollect.setCollectTime(LocalDateTime.now());
        return this.save(postCollect);
    }
}

