package com.qjp.xjbx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.xjbx.mapper.TaskAuditMapper;
import com.qjp.xjbx.pojo.TaskAudit;
import com.qjp.xjbx.service.TaskAuditService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author qjp
 */
@Service
public class TaskAuditServiceImpl extends ServiceImpl<TaskAuditMapper, TaskAudit> implements TaskAuditService {


}
