package com.qjp.bang.controller;


import com.qjp.bang.common.R;
import com.qjp.bang.dto.TaskClassDto;
import com.qjp.bang.service.TaskClassService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

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
    public R<Map<Integer, TaskClassDto>> getType() {
        return taskClassService.getType();
    }
}

