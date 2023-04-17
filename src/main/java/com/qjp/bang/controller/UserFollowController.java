package com.qjp.bang.controller;


import com.qjp.bang.common.R;
import com.qjp.bang.service.UserFollowService;
import com.qjp.bang.service.UserService;
import com.qjp.bang.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * (UserFollow)表控制层
 *
 * @author makejava
 * @since 2023-04-15 20:58:49
 */

@Api(tags = "关注取关", value = "关注取关")
@RestController
@CrossOrigin
@RequestMapping("userFollow")
public class UserFollowController{
    /**
     * 服务对象
     */
    @Resource
    private UserFollowService userFollowService;
    @Resource
    private UserService userService;
    @Resource
    JwtUtil jwtUtil;

    @ApiOperation("关注取关")
    @PostMapping("/follow")
    public R<String> follow(@RequestHeader("Authorization") String header,
                            @NotBlank @RequestBody Map<String,String> toId){
        String openid = jwtUtil.getOpenidFromToken(header);
        return userFollowService.follow(toId.get("toId"),openid);
    }
}

