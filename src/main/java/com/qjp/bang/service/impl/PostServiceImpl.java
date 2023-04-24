package com.qjp.bang.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.FollowListResDto;
import com.qjp.bang.dto.PostDetDto;
import com.qjp.bang.dto.PostListResDto;
import com.qjp.bang.dto.PostNewParamDto;
import com.qjp.bang.entity.Post;
import com.qjp.bang.entity.UserFollow;
import com.qjp.bang.exception.BangException;
import com.qjp.bang.mapper.PostMapper;
import com.qjp.bang.service.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.qjp.bang.common.Constants.REDIS_BROWSE_KEY;
import static com.qjp.bang.common.Constants.REDIS_FEED_KEY;

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
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private PostLikeService postLikeService;
    @Resource
    private FileService fileService;
    @Resource
    private PostCollectService postCollectService;
    @Resource
    private UserFollowService userFollowService;
    @Resource
    private UserService userService;


    /**
     * 发布帖子
     *
     * @param openid
     * @param postNewParamDto
     * @return
     */
    @Override
    public R<String> savePost(String openid, PostNewParamDto postNewParamDto) {
        Post post = new Post();
        BeanUtils.copyProperties(postNewParamDto, post);
        post.setUserId(openid);
        post.setReleaseTime(LocalDateTime.now());
        boolean save = this.save(post);
        String postId = post.getId();
        String[] urls = postNewParamDto.getUrls();
        for (String url : urls) {
            boolean flag = fileService.addPostFile(url, postId);
            if (!flag) BangException.cast("附件入库失败！");
        }
        LambdaQueryWrapper<UserFollow> qw = new LambdaQueryWrapper<>();
        qw.eq(UserFollow::getFollowId, openid);
        List<UserFollow> list = userFollowService.list(qw);
        for (UserFollow userFollow : list) {
            //粉丝id
            String userId = userFollow.getUserId();
            //推送
            String key = REDIS_FEED_KEY + userId;
            stringRedisTemplate.opsForZSet().add(key, postId, System.currentTimeMillis());
        }
        return save ? R.success("发布成功！") : R.error("发布失败");
    }

    /**
     * 帖子详情
     *
     * @param openid
     * @param postId
     * @return
     */
    @Override
    public R<PostDetDto> onePost(String openid, String postId) {
        //该帖子是否存在
        if (!postHave(postId)) {
            BangException.cast("该帖子不存在或已删除！");
        }
        Post post = this.getById(postId);
        PostDetDto postDetDto = new PostDetDto();
        BeanUtils.copyProperties(post, postDetDto);
        //获取话题名
        String topicName = topicService.getTopicName(post.getTopicId());
        postDetDto.setTopicName(topicName);
        //获取发帖人信息
        Map<String,String> userInfo = userService.getOneInfo(post.getUserId());
        postDetDto.setHead(userInfo.get("head"));
        postDetDto.setUsername(userInfo.get("username"));
        //是否关注发帖人
        boolean isFollow = userFollowService.isFollow(post.getUserId(), openid);
        postDetDto.setFollowUser(isFollow);
        //获取浏览量并自增
        Long increment = stringRedisTemplate.opsForValue().increment(REDIS_BROWSE_KEY + postId);
        postDetDto.setBrowse(increment);
        //获取帖子点赞量
        Long likeNum = postLikeService.getLikeNum(postId);
        postDetDto.setLikeNum(likeNum);
        //获取附件
        String[] urls = fileService.getPostFiles(postId);
        postDetDto.setUrls(urls);
        //是否点赞
        boolean isLike = postLikeService.isLike(openid, postId);
        postDetDto.setLike(isLike);
        //是否收藏
        boolean collect = postCollectService.isCollect(openid, postId);
        postDetDto.setCollect(collect);
        return R.success(postDetDto);
    }

    /**
     * 点赞/取消帖子
     *
     * @param openid
     * @param postId
     * @return
     */
    @Override
    public R<String> likePost(String openid, String postId) {
        //该帖子是否存在
        if (!postHave(postId)) {
            BangException.cast("该帖子不存在或已删除！");
        }
        //判断该用户是否已点赞
        boolean isLike = postLikeService.isLike(openid, postId);
        if (isLike) {
            //已点赞 取消点赞
            boolean remove = postLikeService.removeLike(openid, postId);
            if (!remove) BangException.cast("取消点赞失败!");
            return R.success("取消点赞成功！");
        }
        //未点赞 点赞
        boolean like = postLikeService.likeIt(openid, postId);
        if (!like) BangException.cast("点赞失败！");
        return R.success("点赞成功!");
    }

    /**
     * 帖子收藏/取消
     *
     * @param openid
     * @param postId
     * @return
     */
    @Override
    public R<String> collectPost(String openid, String postId) {
        //该帖子是否存在
        if (!postHave(postId)) {
            BangException.cast("该帖子不存在或已删除！");
        }
        //判断该用户是否已收藏
        boolean isCollect = postCollectService.isCollect(openid, postId);
        if (isCollect) {
            //已收藏 取消收藏
            boolean remove = postCollectService.removeCollect(openid, postId);
            if (!remove) BangException.cast("取消收藏失败!");
            return R.success("取消收藏成功！");
        }
        //未收藏 收藏
        boolean collectIt = postCollectService.collectIt(openid, postId);
        if (!collectIt) BangException.cast("收藏失败！");
        return R.success("收藏成功!");
    }

    /**
     * 按话题题查
     *
     * @param openid
     * @param topicId
     * @return
     */
    @Override
    public R<Page<PostListResDto>> pageForTopic(String openid, String topicId, int page, int pageSize) {
        //该话题是否存在
        if (!topicService.topicHave(topicId)) {
            BangException.cast("该帖子不存在或已删除！");
        }
        //该话题的帖子 查询条件
        LambdaQueryWrapper<Post> qw = new LambdaQueryWrapper<>();
        qw.eq(Post::getTopicId, topicId);
        //构造返回值
        Page<PostListResDto> resDtoList = getPostListResDtos(openid, qw, page, pageSize);
        return R.success(resDtoList);
    }


    /**关注的用户动态
     * @param openid
     * @param max      上一次查询的最小时间
     * @param offset   要跳过的元素的个数
     * @param pageSize
     * @return
     */
    @Override
    public R queryPostOfFollow(String openid, Long max, Integer offset, Integer pageSize) {
        //1.获取当前用户
        //2.查询收件箱
        String key = REDIS_FEED_KEY + openid;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, pageSize);
        //3,非空判断
        if (CollectionUtil.isEmpty(typedTuples)){
            return R.success(new ArrayList<>(),"您还没有关注其他人哦！O_o!");
        }
        //4.解析数据：blogId、minTime(时间戳)、offset
        List<Long> postIds = new ArrayList<>(typedTuples.size());
        long minTime = 0;
        int os = 1;
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            //获取帖子id
            postIds.add(Long.valueOf(typedTuple.getValue()));
            //获取分数（时间戳）
            long time = typedTuple.getScore().longValue();
            //下一个是否等于当前这个
            if (time == minTime){//相等 offset加一
                os++;
            }else {
                minTime = time;
                os = 1; //os计数器重置
            }
        }
        //5.根据postId查
//        String ids = StrUtil.join(",", postIds);
//        List<Post> posts = query().in("id",ids).last("ORDER BY FILED(id,"+ids+")").list();
        List<Post> posts = listByIds(postIds);
        ListUtil.sortByProperty(posts, "releaseTime");
        ListUtil.reverse(posts);
        List<PostListResDto> resDtoList = posts.stream().map(post -> {
            String postId = post.getId();
            PostListResDto postListResDto = new PostListResDto();
            BeanUtils.copyProperties(post, postListResDto);
            //获取话题名
            String topicName = topicService.getTopicName(post.getTopicId());
            postListResDto.setTopicName(topicName);
            //获取发帖人信息
            Map<String,String> userInfo = userService.getOneInfo(post.getUserId());
            postListResDto.setHead(userInfo.get("head"));
            postListResDto.setUsername(userInfo.get("username"));
            //是否关注发帖人
            postListResDto.setFollow(true);
            //获取帖子点赞量
            Long likeNum = postLikeService.getLikeNum(postId);
            postListResDto.setLikeNum(likeNum);
            //获取附件
            String[] urls = fileService.getPostFiles(postId);
            postListResDto.setUrls(urls);
            //是否点赞
            boolean isLike = postLikeService.isLike(openid, postId);
            postListResDto.setLike(isLike);
            //是否收藏
            boolean collect = postCollectService.isCollect(openid, postId);
            postListResDto.setCollect(collect);
            return postListResDto;
        }).collect(Collectors.toList());

        //5.封装并返回
        FollowListResDto dto = new FollowListResDto();
        dto.setSize(resDtoList.size());
        dto.setTotal(Integer.valueOf(String.valueOf(stringRedisTemplate.opsForZSet().zCard(key))));
        dto.setList(resDtoList);
        dto.setOffset(os);
        dto.setMinTime(minTime);
        return R.success(dto);
    }

    /**
     * 获取帖子列表构造返回数据
     *
     * @param openid
     * @param qw
     * @param page
     * @param pageSize
     * @return
     */
    @NotNull
    private Page<PostListResDto> getPostListResDtos(String openid,
                                                    LambdaQueryWrapper<Post> qw,
                                                    int page,
                                                    int pageSize) {
        Page<Post> pageInfo = new Page<>(page, pageSize);
        Page<PostListResDto> dtoPage = new Page<>(page, pageSize);
        this.page(pageInfo, qw);
        List<Post> list = pageInfo.getRecords();
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<PostListResDto> resDtoList = list.stream().map(post -> {
            String postId = post.getId();
            PostListResDto postListResDto = new PostListResDto();
            BeanUtils.copyProperties(post, postListResDto);
            //获取话题名
            String topicName = topicService.getTopicName(post.getTopicId());
            postListResDto.setTopicName(topicName);
            //获取发帖人信息
            Map<String,String> userInfo = userService.getOneInfo(post.getUserId());
            postListResDto.setHead(userInfo.get("head"));
            postListResDto.setUsername(userInfo.get("username"));
            //是否关注发帖人
            boolean isFollow = userFollowService.isFollow(post.getUserId(), openid);
            postListResDto.setFollow(isFollow);
            //获取帖子点赞量
            Long likeNum = postLikeService.getLikeNum(postId);
            postListResDto.setLikeNum(likeNum);
            //获取附件
            String[] urls = fileService.getPostFiles(postId);
            postListResDto.setUrls(urls);
            //是否点赞
            boolean isLike = postLikeService.isLike(openid, postId);
            postListResDto.setLike(isLike);
            //是否收藏
            boolean collect = postCollectService.isCollect(openid, postId);
            postListResDto.setCollect(collect);
            return postListResDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(resDtoList);
        return dtoPage;
    }


    /**
     * 该帖子是否存在
     *
     * @param postId
     * @return
     */
    private boolean postHave(String postId) {
        LambdaQueryWrapper<Post> qw = new LambdaQueryWrapper<>();
        qw.eq(Post::getId, postId);
        return this.count(qw) > 0;
    }
}

