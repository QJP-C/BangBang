package com.qjp.bang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qjp.bang.entity.Topic;
import org.apache.ibatis.annotations.Mapper;

/**
 * (Topic)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-20 20:18:21
 */
@Mapper
public interface TopicMapper extends BaseMapper<Topic> {

}

