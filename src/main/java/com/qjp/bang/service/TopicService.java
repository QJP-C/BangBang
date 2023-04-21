package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.common.R;
import com.qjp.bang.entity.Topic;

import java.util.List;

/**
 * (Topic)表服务接口
 *
 * @author makejava
 * @since 2023-04-20 20:18:21
 */
public interface TopicService extends IService<Topic> {

    R<String> newTopic(Topic topic);

    R<List<Topic>> getList();

    R<String> followTopic(String openid, String topicId);
}

