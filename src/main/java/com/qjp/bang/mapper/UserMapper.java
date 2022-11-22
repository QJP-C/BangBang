package com.qjp.bang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.qjp.bang.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author qjp
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

