package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.common.R;
import com.qjp.bang.entity.Topic;
import com.qjp.bang.entity.TopicFollow;
import com.qjp.bang.entity.User;
import com.qjp.bang.exception.BangException;
import com.qjp.bang.mapper.TopicMapper;
import com.qjp.bang.service.TopicFollowService;
import com.qjp.bang.service.TopicService;
import com.qjp.bang.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Topic)表服务实现类
 *
 * @author makejava
 * @since 2023-04-20 20:18:21
 */
@Service("topicService")
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {
    @Resource
    private UserService userService;
    @Resource
    private TopicFollowService topicFollowService;

    /**
     * 新增话题
     * @param topic
     * @return
     */
    @Override
    public R<String> newTopic(Topic topic) {
        boolean save = this.save(topic);
        return save ? R.success("新增成功！") : R.success("新增失败！");
    }

    /**
     * 话题列表
     * @return
     */
    @Override
    public R<List<Topic>> getList() {
        List<Topic> list = this.list();
        if (list == null) {
            return R.error("暂无话题");
        }
        return R.success(list);
    }

    /**
     * 关注/取关话题
     * @param openid
     * @param topicId
     * @return
     */
    @Override
    public R<String> followTopic(String openid, String topicId) {
        //用户是否存在
        if (!userHave(openid)) {
            return R.error("您的账号有误");
        }
        //话题是否存在
        if (!topicHave(topicId)) {
            return R.error("话题不存在");
        }
        //该用户是否关注该话题
        if (topicIsFollow(openid, topicId)) {
            LambdaQueryWrapper<TopicFollow> qw = new LambdaQueryWrapper<>();
            qw.eq(TopicFollow::getTopicId, topicId).eq(TopicFollow::getUserId, openid);
            return topicFollowService.remove(qw) ? R.success("取关成功！") : R.success("取消关注失败！");
        } else {
            TopicFollow topicFollow = new TopicFollow();
            topicFollow.setUserId(openid);
            topicFollow.setTopicId(topicId);
            return topicFollowService.save(topicFollow) ? R.success("关注成功！") : R.error("关注失败！");
        }
    }

    /**
     * 获取话题名称
     * @param topicId
     * @return
     */
    @Override
    public String getTopicName(String topicId) {
        Topic topic = this.getById(topicId);
        if (topic==null){
            BangException.cast("话题不存在！");
        }
        return topic.getName();
    }

    /**
     * 用户是否存在
     * @param openid
     * @return
     */
    private boolean userHave(String openid) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getId, openid);
        return userService.count(qw) > 0;
    }

    /**
     * 话题是否存在
     *
     * @param topicId
     * @return
     */
    @Override
    public boolean topicHave(String topicId) {
        LambdaQueryWrapper<Topic> qw = new LambdaQueryWrapper<>();
        qw.eq(Topic::getId, topicId);
        return this.count(qw) > 0;
    }


    /**
     * 该用户是否关注该话题
     *
     * @param openId
     * @param topicId
     * @return
     */
    private boolean topicIsFollow(String openId, String topicId) {
        LambdaQueryWrapper<TopicFollow> qw = new LambdaQueryWrapper<>();
        qw.eq(TopicFollow::getUserId, openId).eq(TopicFollow::getTopicId, topicId);
        return topicFollowService.count(qw) > 0;
    }
}

