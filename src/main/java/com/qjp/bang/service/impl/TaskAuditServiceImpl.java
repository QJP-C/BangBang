package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.mapper.TaskAuditMapper;
import com.qjp.bang.entity.TaskAudit;
import com.qjp.bang.service.TaskAuditService;
import org.springframework.stereotype.Service;

/**
 * (TaskAudit)表服务实现类
 *
 * @author makejava
 * @since 2023-04-18 21:41:53
 */
@Service("taskAuditService")
public class TaskAuditServiceImpl extends ServiceImpl<TaskAuditMapper, TaskAudit> implements TaskAuditService {

}

