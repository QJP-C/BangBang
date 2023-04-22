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
    /**
     * 新增话题
     * @param topic
     * @return
     */
    R<String> newTopic(Topic topic);

    /**
     * 话题列表
     * @return
     */
    R<List<Topic>> getList();

    /**
     * 关注/取关话题
     * @param openid
     * @param topicId
     * @return
     */
    R<String> followTopic(String openid, String topicId);

    /**
     * 获取话题名称
     * @param topicId
     * @return
     */
    String getTopicName(String topicId);

    /**
     * 话题是否存在
     * @param topicId
     * @return
     */
    boolean topicHave(String topicId);


}

