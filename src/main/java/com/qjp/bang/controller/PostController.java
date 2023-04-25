package com.qjp.bang.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.PostDetDto;
import com.qjp.bang.dto.PostListResDto;
import com.qjp.bang.dto.PostNewParamDto;
import com.qjp.bang.service.PostService;
import com.qjp.bang.utils.JwtUtil;
import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (Post)表控制层
 *
 * @author makejava
 * @since 2023-04-21 20:44:27
 */
@RestController
@Api(tags = "帖子相关接口", value = "帖子相关接口")
@Slf4j
@RequestMapping("post")
public class PostController {
    /**
     * 服务对象
     */
    @Resource
    private PostService postService;
    @Resource
    JwtUtil jwtUtil;

    @ApiOperation("新增帖子")
    @PostMapping("newPost")
    public R<String> savePost(@RequestHeader("Authorization") String header, @RequestBody PostNewParamDto postNewParamDto) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.savePost(openid, postNewParamDto);
    }

    @ApiOperation("帖子详情")
    @GetMapping("/one/{postId}")
    public R<PostDetDto> getOne(@RequestHeader("Authorization") String header, @PathVariable("postId") String postId) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.onePost(openid, postId);
    }

    @ApiOperation("帖子点赞/取消")
    @GetMapping("like/{postId}")
    public R<String> likePost(@RequestHeader("Authorization") String header, @PathVariable("postId") String postId) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.likePost(openid, postId);
    }

    @ApiOperation("帖子收藏/取消")
    @GetMapping("collect/{postId}")
    public R<String> collectPost(@RequestHeader("Authorization") String header, @PathVariable("postId") String postId) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.collectPost(openid, postId);
    }

    @ApiOperation("按话题查(热门)")
    @GetMapping("listByHot")
    public R<Page<PostListResDto>> pageByHotTopic(@RequestHeader("Authorization") String header,
                                                  @PathVariable(value = "topicId", required = false) String topicId,
                                                  @RequestParam("page") int page,
                                                  @RequestParam("pageSize") int pageSize) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.pageByHotTopic(openid, topicId, page, pageSize);
    }

    @ApiOperation("按话题查(新帖)")
    @GetMapping("listByNew")
    public R<Page<PostListResDto>> pageByNewTopic(@RequestHeader("Authorization") String header,
                                                  @PathVariable(value = "topicId", required = false) String topicId,
                                                  @RequestParam("page") int page,
                                                  @RequestParam("pageSize") int pageSize) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.pageByNewTopic(openid, topicId, page, pageSize);
    }


    /**
     * @param header
     * @param max    上一次查询的最小时间
     * @param offset 要跳过的元素的个数（和上次查询最小的值相同的个数）
     * @return
     */
    @ApiOperation("关注的用户动态")
    @GetMapping("listByFollow")
    public R pageByFollow(@RequestHeader("Authorization") String header,
                          @RequestParam("lastId") Long max,
                          @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                          @RequestParam("pageSize") Integer pageSize
    ) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.queryPostOfFollow(openid, max, offset, pageSize);
    }

    @ApiOperation("推荐")
    @GetMapping("listByRecommend")
    public R queryPostOfRecommend(@RequestHeader("Authorization") String header,
                                  @RequestParam("page") int page,
                                  @RequestParam("pageSize") int pageSize) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.queryPostOfRecommend(openid, page, pageSize);
    }

    @ApiOperation("图文")
    @GetMapping("listByImageText")
    public R queryPostOfImageText(@RequestHeader("Authorization") String header,
                                  @RequestParam("page") int page,
                                  @RequestParam("pageSize") int pageSize) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.queryPostOfImageText(openid, page, pageSize);
    }

    @ApiOperation("个人动态")
    @GetMapping("listByPersonal")
    public R queryPostOfPersonal(@RequestHeader("Authorization") String header,
                                 @RequestParam(value = "openid", required = false) String openid,
                                 @RequestParam("page") int page,
                                 @RequestParam("pageSize") int pageSize) {
        if (StringUtil.isNullOrEmpty(openid)) {
            openid = jwtUtil.getOpenidFromToken(header);
        }
        return postService.queryPostOfPersonal(openid, page, pageSize);
    }

    @ApiOperation("我的收藏(帖子)")
    @GetMapping("myCollect")
    public R queryPostOfMyCollect(@RequestHeader("Authorization") String header,
                                  @RequestParam("page") int page,
                                  @RequestParam("pageSize") int pageSize) {

        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.queryPostOfMyCollect(openid, page, pageSize);
    }

    /**
     * 评论帖子
     * @param header
     * @param postId
     * @return
     */
    @ApiOperation("评论帖子")
    @PostMapping("comment")
    public R commentPost(@RequestHeader("Authorization") String header,@PathVariable("postId")String postId) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.commentPost(openid,postId);
    }

}

