package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.mapper.TopicFollowMapper;
import com.qjp.bang.entity.TopicFollow;
import com.qjp.bang.service.TopicFollowService;
import org.springframework.stereotype.Service;

/**
 * (TopicFollow)表服务实现类
 *
 * @author makejava
 * @since 2023-04-20 21:27:43
 */
@Service("topicFollowService")
public class TopicFollowServiceImpl extends ServiceImpl<TopicFollowMapper, TopicFollow> implements TopicFollowService {

}

