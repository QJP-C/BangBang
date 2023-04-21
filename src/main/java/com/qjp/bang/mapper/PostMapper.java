package com.qjp.bang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qjp.bang.entity.Post;
import org.apache.ibatis.annotations.Mapper;

/**
 * (Post)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-21 20:44:28
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {

}

