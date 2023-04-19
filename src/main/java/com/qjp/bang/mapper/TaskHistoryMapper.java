package com.qjp.bang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qjp.bang.entity.TaskHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * (TaskHistory)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-19 18:06:11
 */
@Mapper
public interface TaskHistoryMapper extends BaseMapper<TaskHistory> {
    void addHistory(String openid, String taskId);
}

