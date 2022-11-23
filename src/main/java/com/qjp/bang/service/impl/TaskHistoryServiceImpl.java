package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.mapper.TaskHistoryMapper;
import com.qjp.bang.pojo.TaskHistory;
import com.qjp.bang.service.TaskHistoryService;
import lombok.Data;
import org.springframework.stereotype.Service;

/**
 * @Author:QJP
 * @Date: 2022/11/23  17:50
 * @Version 1.0
 */
@Service
public class TaskHistoryServiceImpl extends ServiceImpl<TaskHistoryMapper, TaskHistory> implements TaskHistoryService {
}
