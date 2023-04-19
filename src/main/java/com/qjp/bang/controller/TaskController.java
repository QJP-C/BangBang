package com.qjp.bang.controller;


import com.qjp.bang.common.R;
import com.qjp.bang.dto.TaskDetailsResultDto;
import com.qjp.bang.dto.TaskListResDto;
import com.qjp.bang.dto.TaskNewDto;
import com.qjp.bang.service.TaskClassService;
import com.qjp.bang.service.TaskService;
import com.qjp.bang.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * (Task)表控制层
 *
 * @author makejava
 * @since 2023-04-17 11:34:27
 */
@Api(tags = "任务相关接口", value = "任务相关接口")
@RestController
@CrossOrigin
@Slf4j
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
    public R<String> newTask(@RequestHeader("Authorization") String header, @NotBlank @RequestBody TaskNewDto taskNewDto){
        String openid = jwtUtil.getOpenidFromToken(header);
        return taskService.newTask(openid, taskNewDto);
    }

    @ApiOperation("任务详情")
    @GetMapping("/one/{taskId}")
    public R<TaskDetailsResultDto> taskDetails(@RequestHeader("Authorization") String header,@PathVariable("taskId") String taskId){
        String openid = jwtUtil.getOpenidFromToken(header);
        return taskService.taskDetails(openid,taskId);
    }

    @ApiOperation("任务列表")
    @GetMapping("List")
    public R<List<TaskListResDto>> getList(@RequestHeader("Authorization") String header,
                                           @RequestParam(value = "typeId",required = false)String typeId,
                                            @RequestParam(value = "search",required = false) String search
    ){
        log.info("进来了");
        String openid = jwtUtil.getOpenidFromToken(header);
        return taskService.taskList(openid,typeId,search);
    }


    @ApiOperation("我的发布")
    @GetMapping("myList")
    public R<List<TaskListResDto>> myList(@RequestHeader("Authorization") String header,
                                @RequestParam(value = "status",required = false)Integer status){
        String openid = jwtUtil.getOpenidFromToken(header);
        return taskService.myList(openid,status);
    }

    @ApiOperation("我的足迹")
    @GetMapping("history")
    public R<List<TaskListResDto>> history(@RequestHeader("Authorization") String header){
        String openid = jwtUtil.getOpenidFromToken(header);
        return taskService.history(openid);
    }

    @ApiOperation("我的收藏")
    @GetMapping("like")
    public R<List<TaskListResDto>> myLike(@RequestHeader("Authorization") String header) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return taskService.myLike(openid);
    }
}

