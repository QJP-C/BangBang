package com.qjp.bang.controller;


import com.qjp.bang.service.TaskAuditService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * (TaskAudit)表控制层
 *
 * @author makejava
 * @since 2023-04-18 21:41:53
 */
@RestController
@RequestMapping("taskAudit")
public class TaskAuditController {
    /**
     * 服务对象
     */
    @Resource
    private TaskAuditService taskAuditService;


}

