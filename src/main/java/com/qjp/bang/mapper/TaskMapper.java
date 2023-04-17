package com.qjp.bang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qjp.bang.entity.Task;
import org.apache.ibatis.annotations.Mapper;

/**
 * (Task)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-17 11:34:28
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

}

