package com.qjp.bang.controller;


import com.qjp.bang.service.TaskHistoryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * (TaskHistory)表控制层
 *
 * @author makejava
 * @since 2023-04-19 18:06:11
 */
@RestController
@RequestMapping("taskHistory")
public class TaskHistoryController {
    /**
     * 服务对象
     */
    @Resource
    private TaskHistoryService taskHistoryService;


}

