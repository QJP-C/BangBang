package com.qjp.bang.controller;


import com.qjp.bang.common.R;
import com.qjp.bang.dto.TaskGetOneResultDto;
import com.qjp.bang.dto.TaskNewDto;
import com.qjp.bang.service.TaskClassService;
import com.qjp.bang.service.TaskService;
import com.qjp.bang.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

/**
 * (Task)表控制层
 *
 * @author makejava
 * @since 2023-04-17 11:34:27
 */
@Api(tags = "任务相关接口", value = "任务相关接口")
@RestController
@CrossOrigin
@RequestMapping("task")
public class TaskController {
    /**
     * 服务对象
     */
    @Resource
    private TaskService taskService;
    @Resource
    private TaskClassService taskClassService;
    @Resource
    JwtUtil jwtUtil;

    @ApiOperation("新增任务")
    @PostMapping("new")
//    public R<String> newTask(@RequestHeader("Authorization") String header, @NotBlank @RequestBody TaskNewDto taskNewDto){
//        String openid = jwtUtil.getOpenidFromToken(header);
        public R<String> newTask(@NotBlank @RequestBody TaskNewDto taskNewDto){
        String openid = "oI1vd5DC3H0lVyJizpK58ZPS9Mz8";
        return taskService.newTask(openid,taskNewDto);
    }

    @ApiOperation("任务详情")
    @GetMapping("/one")
    public R<TaskGetOneResultDto> taskDetails(){
        return null;
    }

}

