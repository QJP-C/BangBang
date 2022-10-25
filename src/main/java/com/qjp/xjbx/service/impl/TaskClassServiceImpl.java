package com.qjp.xjbx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.xjbx.dto.TaskDto;
import com.qjp.xjbx.mapper.TaskClassMapper;
import com.qjp.xjbx.mapper.TaskMapper;
import com.qjp.xjbx.pojo.Task;
import com.qjp.xjbx.pojo.TaskClass;
import com.qjp.xjbx.service.TaskClassService;
import com.qjp.xjbx.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskClassServiceImpl extends ServiceImpl<TaskClassMapper, TaskClass> implements TaskClassService {

}
