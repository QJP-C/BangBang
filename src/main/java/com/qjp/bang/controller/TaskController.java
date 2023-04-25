package com.qjp.bang.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.TaskDetailsResultDto;
import com.qjp.bang.dto.TaskListResDto;
import com.qjp.bang.dto.TaskNewClassDto;
import com.qjp.bang.dto.TaskNewDto;
import com.qjp.bang.entity.TaskClass;
import com.qjp.bang.service.TaskClassService;
import com.qjp.bang.service.TaskCollectService;
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
    private TaskCollectService taskCollectService;
    @Resource
    JwtUtil jwtUtil;

    @ApiOperation("新增任务")
    @PostMapping("new")
    public R<String> newTask(@RequestHeader("Authorization") String header, @NotBlank @RequestBody TaskNewDto taskNewDto) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return taskService.newTask(openid, taskNewDto);
    }
    @ApiOperation("获取任务分类")
    @GetMapping
    public R<List<TaskClass>> getType() {
        return taskClassService.getType();
    }

    @ApiOperation("新增分类")
    @PostMapping("newClass")
    public R<String> newClass(@RequestBody TaskNewClassDto taskNewClassDto){
        return taskClassService.newClass(taskNewClassDto);
    }
    @ApiOperation("收藏/取消收藏")
    @GetMapping("collect/{taskId}")
    public R<String> likeTask(@RequestHeader("Authorization") String header, @PathVariable("taskId")String taskId){
        String openid = jwtUtil.getOpenidFromToken(header);
        return taskCollectService.collectTask(openid, taskId);
    }
    @ApiOperation("任务详情")
    @GetMapping("/one/{taskId}")
    public R<TaskDetailsResultDto> taskDetails(@RequestHeader("Authorization") String header, @PathVariable("taskId") String taskId) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return taskService.taskDetails(openid, taskId);
    }

    @ApiOperation("任务列表")
    @GetMapping("taskList")
    public R<Page<TaskListResDto>> getLists(@RequestHeader("Authorization") String header,
                                            @RequestParam("page") int page,
                                            @RequestParam("pageSize") int pageSize,
                                            @RequestParam(value = "typeId", required = false) String typeId,
                                            @RequestParam(value = "search", required = false) String search) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return taskService.taskList(openid, typeId, search, page, pageSize);
    }

    @ApiOperation("我的发布(任务)")
    @GetMapping("myList")
    public R<Page<TaskListResDto>> myList(@RequestHeader("Authorization") String header,
                                          @RequestParam(value = "status", required = false) Integer status,
                                          @RequestParam(value = "page") int page,
                                          @RequestParam(value = "pageSize") int pageSize) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return taskService.myList(openid, status,page,pageSize);
    }

    @ApiOperation("我的足迹")
    @GetMapping("history")
    public R<Page<TaskListResDto>> history(@RequestHeader("Authorization") String header,
                                           @RequestParam(value = "page") int page,
                                           @RequestParam(value = "pageSize") int pageSize) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return taskService.history(openid,page,pageSize);
    }

    @ApiOperation("我的收藏(任务)")
    @GetMapping("like")
    public R<Page<TaskListResDto>> myLike(@RequestHeader("Authorization") String header,
                                          @RequestParam(value = "page") int page,
                                          @RequestParam(value = "pageSize") int pageSize) {
        String openid = jwtUtil.getOpenidFromToken(header);
        return taskService.myCollect(openid,page,pageSize);
    }


}

