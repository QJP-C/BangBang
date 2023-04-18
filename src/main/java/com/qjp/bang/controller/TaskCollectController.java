package com.qjp.bang.controller;


import com.qjp.bang.common.R;
import com.qjp.bang.service.TaskCollectService;
import com.qjp.bang.utils.JwtUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (TaskCollect)表控制层
 *
 * @author makejava
 * @since 2023-04-18 11:19:58
 */
@RestController
@RequestMapping("taskCollect")
public class TaskCollectController {
    /**
     * 服务对象
     */
    @Resource
    private TaskCollectService taskCollectService;

    @Resource
    JwtUtil jwtUtil;

    @ApiOperation("收藏/取消收藏")
    @GetMapping("collect/{taskId}")
    public R<String> likeTask(@RequestHeader("Authorization") String header, @PathVariable("taskId")String taskId){
        String openid = jwtUtil.getOpenidFromToken(header);
//    public R<String> likeTask(@PathVariable("taskId") String taskId) {
//        String openid = "oI1vd5DC3H0lVyJizpK58ZPS9Mz8";
        return taskCollectService.likeTask(openid, taskId);
    }
}

