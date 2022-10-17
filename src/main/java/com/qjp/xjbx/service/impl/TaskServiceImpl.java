package com.qjp.xjbx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.xjbx.mapper.TaskMapper;
import com.qjp.xjbx.pojo.Task;
import com.qjp.xjbx.service.TaskService;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
}
