package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.common.R;
import com.qjp.bang.entity.User;
import com.qjp.bang.entity.UserFollow;
import com.qjp.bang.mapper.UserFollowMapper;
import com.qjp.bang.service.UserFollowService;
import com.qjp.bang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * (UserFollow)表服务实现类
 *
 * @author makejava
 * @since 2023-04-15 20:58:50
 */
@Service("userFollowService")
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements UserFollowService {
    @Autowired
    private UserService userService;

    @Override
    public R<String> follow(String toId, String openid) {

        boolean have = haveOne(toId);
        if (!have) return R.error("没有这个用户！");

        boolean is = getIs(toId, openid);
        if (is){
            //已关注  取关
            LambdaQueryWrapper<UserFollow> qw = new LambdaQueryWrapper<>();
            qw.eq(UserFollow::getFollowId,toId);
            qw.eq(UserFollow::getUserId,openid);
            boolean remove = this.remove(qw);
            return remove ? R.success("取消关注成功！") : R.error("取消关注失败！");
        }
        //未关注 关注
        UserFollow userFollow = new UserFollow();
        userFollow.setFollowId(toId);
        userFollow.setUserId(openid);
        userFollow.setCreateTime(LocalDateTime.now());
        boolean save = this.save(userFollow);
        return save ? R.success("关注成功！") : R.error("关注失败");
    }



    /**
     * 查询是否有该用户
     *
     * @param id
     * @return
     */
    private boolean haveOne(String id) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getId, id);
        //是否有这个人
        int count = userService.count(qw);
        return count > 0;
    }

    /**
     * 是否已关注
     * @param toId
     * @param openid
     * @return
     */
    private boolean getIs(String toId, String openid) {
        LambdaQueryWrapper<UserFollow> qw = new LambdaQueryWrapper<>();
        qw.eq(UserFollow::getUserId, openid);
        qw.eq(UserFollow::getFollowId, toId);
        int count = this.count(qw);
        return count > 0;
    }
}

