package com.qjp.bang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qjp.bang.entity.PostComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * (PostComment)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-25 21:49:22
 */
@Mapper
public interface PostCommentMapper extends BaseMapper<PostComment> {

}

