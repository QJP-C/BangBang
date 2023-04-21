package com.qjp.bang.controller;


import com.qjp.bang.common.R;
import com.qjp.bang.dto.PostDetDto;
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
@Api(tags = "帖子相关接口",value = "帖子相关接口")
@Slf4j
@RequestMapping("post")
public class PostController  {
    /**
     * 服务对象
     */
    @Resource
    private PostService postService;
    @Resource
    JwtUtil jwtUtil;

    @ApiOperation("新增帖子")
    @PostMapping("new")
    public R<String> savePost(@RequestHeader("Authorization") String header, @RequestBody PostNewParamDto postNewParamDto){
        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.savePost(openid,postNewParamDto);
    }
    @ApiOperation("帖子详情")
    @GetMapping("/one/{postId}")
    public R<PostDetDto> getOne(@RequestHeader("Authorization") String header, @PathVariable("postId")String postId){
        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.onePost(openid,postId);
    }
    @ApiOperation("点赞/取消帖子")
    @GetMapping("like/{postId}")
    public R<String> likePost(@RequestHeader("Authorization") String header, @PathVariable("postId")String postId){
        String openid = jwtUtil.getOpenidFromToken(header);
        return postService.likePost(openid,postId);
    }
}

