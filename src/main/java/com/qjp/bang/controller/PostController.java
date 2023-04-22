package com.qjp.bang.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.PostDetDto;
import com.qjp.bang.dto.PostListResDto;
import com.qjp.bang.dto.PostNewParamDto;
import com.qjp.bang.service.PostService;
import com.qjp.bang.utils.JwtUtil;
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

    @ApiOperation("按话题查")
    @GetMapping("list/{topicId}")
    public R<Page<PostListResDto>> pageByTopic(@RequestHeader("Authorization") String header,
                                         @PathVariable(value = "topicId", required = false) String topicId,
                                         @RequestParam("page") int page,
                                         @RequestParam("pageSize") int pageSize) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.pageForTopic(openid, topicId, page, pageSize);
    }

    @ApiOperation("关注的用户动态")
    @GetMapping("followList")
    public R<Page<PostListResDto>> pageByFollow(@RequestHeader("Authorization") String header,
                                                @RequestParam("page") int page,
                                                @RequestParam("pageSize") int pageSize){
        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.pageByFollow(openid, page, pageSize);
    }

}

