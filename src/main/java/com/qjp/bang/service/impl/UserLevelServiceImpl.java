package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.mapper.UserLevelMapper;
import com.qjp.bang.pojo.UserLevel;
import com.qjp.bang.service.UserLevelService;
import org.springframework.stereotype.Service;

/**
 * @author qjp
 */
@Service
public class UserLevelServiceImpl extends ServiceImpl<UserLevelMapper, UserLevel> implements UserLevelService {
}
