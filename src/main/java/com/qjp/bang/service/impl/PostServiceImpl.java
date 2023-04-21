package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.PostDetDto;
import com.qjp.bang.dto.PostNewParamDto;
import com.qjp.bang.entity.Post;
import com.qjp.bang.exception.BangException;
import com.qjp.bang.mapper.PostMapper;
import com.qjp.bang.service.FileService;
import com.qjp.bang.service.PostLikeService;
import com.qjp.bang.service.PostService;
import com.qjp.bang.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static com.qjp.bang.common.Constants.REDIS_BROWSE_KEY;

/**
 * (Post)表服务实现类
 *
 * @author makejava
 * @since 2023-04-21 20:44:28
 */
@Slf4j
@Service("postService")
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Resource
    private TopicService topicService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private PostLikeService postLikeService;
    @Resource
    private FileService fileService;
    /**
     *  发布帖子
     * @param openid
     * @param postNewParamDto
     * @return
     */
    @Override
    public R<String> savePost(String openid, PostNewParamDto postNewParamDto) {
        Post post = new Post();
        BeanUtils.copyProperties(postNewParamDto,post);
        post.setUserId(openid);
        post.setReleaseTime(LocalDateTime.now());
        boolean save = this.save(post);
        String postId = post.getId();
        String[] urls = postNewParamDto.getUrls();
        for (String url : urls) {
            boolean flag = fileService.addPostFile(url,postId);
            if (!flag) BangException.cast("附件入库失败！");
        }
        return save ? R.success("发布成功！") : R.error("发布失败");
    }

    /**
     * 帖子详情
     * @param openid
     * @param postId
     * @return
     */
    @Override
    public R<PostDetDto> onePost(String openid, String postId) {
        if (!postHave(postId)){
            return R.success("该帖子已删除或不存在！");
        }
        Post post = this.getById(postId);
        PostDetDto postDetDto = new PostDetDto();
        BeanUtils.copyProperties(post,postDetDto);
        //获取话题名
        String topicName = topicService.getTopicName(post.getTopicId());
        postDetDto.setTopicName(topicName);
        //获取浏览量并自增
        Long increment = redisTemplate.opsForValue().increment(REDIS_BROWSE_KEY + postId);
        postDetDto.setBrowse(increment);
        //获取帖子点赞量
        Long likeNum =  postLikeService.getLikeNum(postId);
        postDetDto.setLikeNum(likeNum);
        //获取附件
        String[] urls = fileService.getPostFiles(postId);
        postDetDto.setUrls(urls);
        return R.success(postDetDto);
    }

    /**
     * 点赞/取消帖子
     * @param openid
     * @param postId
     * @return
     */
    @Override
    public R<String> likePost(String openid, String postId) {
        if (!postHave(postId)){
            return R.success("该帖子已删除或不存在！");
        }
        //判断该用户是否已点赞
        boolean isLike = postLikeService.isLike(openid,postId);
        if (isLike){
            //已点赞 取消点赞
            boolean remove = postLikeService.removeLike(openid,postId);
            if (!remove) BangException.cast("取消点赞失败!");
        }
        //未点赞 点赞
        boolean like = postLikeService.likeIt(openid,postId);
        if (!like) BangException.cast("点赞失败！");
        return null;
    }


    /**
     * 该帖子是否存在
     * @param postId
     * @return
     */
    private boolean postHave(String postId){
        LambdaQueryWrapper<Post> qw = new LambdaQueryWrapper<>();
        qw.eq(Post::getId,postId);
        int count = this.count(qw);
        return count > 0;
    }
}

