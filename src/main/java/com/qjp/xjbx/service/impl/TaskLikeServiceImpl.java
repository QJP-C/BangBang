package com.qjp.xjbx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.xjbx.mapper.TaskLikeMapper;
import com.qjp.xjbx.pojo.TaskLike;
import com.qjp.xjbx.service.TaskLikeService;
import com.qjp.xjbx.service.TaskService;
import org.springframework.stereotype.Service;

@Service
public class TaskLikeServiceImpl extends ServiceImpl<TaskLikeMapper,TaskLike> implements TaskLikeService {
}
