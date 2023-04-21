package com.qjp.bang.controller;


import com.qjp.bang.common.R;
import com.qjp.bang.dto.TaskNewClassDto;
import com.qjp.bang.entity.TaskClass;
import com.qjp.bang.service.TaskClassService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * (TaskClass)表控制层
 *
 * @author makejava
 * @since 2023-04-17 19:48:07
 */
@Api(tags = "任务分类接口", value = "任务分类接口")
@RestController
@CrossOrigin
@RequestMapping("taskClass")
public class TaskClassController {
    /**
     * 服务对象
     */
    @Resource
    private TaskClassService taskClassService;

    @ApiModelProperty("获取任务分类")
    @GetMapping
    public R<List<TaskClass>> getType() {
        return taskClassService.getType();
    }

    @ApiModelProperty("新增分类")
    @PostMapping("new")
    public R<String> newClass(@RequestBody TaskNewClassDto taskNewClassDto){
        return taskClassService.newClass(taskNewClassDto);
    }
}

