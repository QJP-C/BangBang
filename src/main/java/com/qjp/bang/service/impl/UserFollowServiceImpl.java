package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.UserListDto;
import com.qjp.bang.entity.User;
import com.qjp.bang.entity.UserFollow;
import com.qjp.bang.exception.BangException;
import com.qjp.bang.mapper.UserFollowMapper;
import com.qjp.bang.service.UserFollowService;
import com.qjp.bang.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (UserFollow)表服务实现类
 *
 * @author makejava
 * @since 2023-04-15 20:58:50
 */
@Service("userFollowService")
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements UserFollowService {
    @Resource
    private UserService userService;

    /**
     * 关注/取关用户
     * @param toId
     * @param openid
     * @return
     */
    @Override
    public R<String> follow(String toId, String openid) {

        boolean have = userService.haveOne(toId);
        if (!have) return R.error("没有这个用户！");

        boolean is = isFollow(toId, openid);
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
     * 获取用户关注数
     * @param id
     * @return
     */
    @Override
    public Long userFollowNum(String id) {
        LambdaQueryWrapper<UserFollow> qw = new LambdaQueryWrapper<>();
        qw.eq(UserFollow::getUserId,id);
        return (long) this.count(qw);
    }

    /**
     * 获取用户粉丝数
     * @param id
     * @return
     */
    @Override
    public Long userFansNum(String id) {
        LambdaQueryWrapper<UserFollow> qw = new LambdaQueryWrapper<>();
        qw.eq(UserFollow::getFollowId,id);
        return (long) this.count(qw);
    }


    /**
     * 是否已关注
     * @param toId
     * @param openid
     * @return
     */
    @Override
    public boolean isFollow(String toId, String openid) {
        LambdaQueryWrapper<UserFollow> qw = new LambdaQueryWrapper<>();
        qw.eq(UserFollow::getUserId, openid);
        qw.eq(UserFollow::getFollowId, toId);
        int count = this.count(qw);
        return count > 0;
    }

    /**
     * 获取关注的用户ids
     * @param openid
     * @return
     */
    @Override
    public String[] getIdByFollow(String openid) {
        LambdaQueryWrapper<UserFollow> qw = new LambdaQueryWrapper<>();
        qw.eq(UserFollow::getUserId,openid);
        int count = this.count(qw);
        if (count<=0) return new String[0];
        List<UserFollow> list = this.list(qw);
        String[] ids = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ids[i] = list.get(i).getFollowId();
        }
        return ids;
    }

    /**
     * 我的关注
     * @param openid
     * @return
     */
    @Override
    public R myFollow(String openid) {
        if (!userService.haveOne(openid)) BangException.cast("您的账号有误！请联系管理员");
        LambdaQueryWrapper<UserFollow> qw = new LambdaQueryWrapper<>();
        qw.eq(UserFollow::getUserId,openid).orderByDesc(UserFollow::getCreateTime);
        List<UserFollow> list = this.list(qw);
        List<String> ids = new ArrayList<>(list.size());
        for (UserFollow follow : list) {
            ids.add(follow.getFollowId());
        }
        List<User> users = userService.listByIds(ids);
        List<UserListDto> resDto = users.stream().map(user -> {
            UserListDto userListDto = new UserListDto();
            BeanUtils.copyProperties(user, userListDto);
            //是否关注
            boolean follow = this.isFollow(user.getId(), openid);
            if (follow) {
                userListDto.setFollow(true);
                //是否互关
                boolean followToo = this.isFollow(openid, user.getId());
                if (followToo) userListDto.setFollowToo(true);
            } else {
                userListDto.setFollow(false);
                userListDto.setFollowToo(false);
            }
            return userListDto;
        }).collect(Collectors.toList());

        return R.success(resDto);
    }

    /**
     * 我的粉丝
     * @param openid
     * @return
     */
    @Override
    public R myFans(String openid) {
        if (!userService.haveOne(openid)) BangException.cast("您的账号有误！请联系管理员");
        LambdaQueryWrapper<UserFollow> qw = new LambdaQueryWrapper<>();
        qw.eq(UserFollow::getFollowId,openid).orderByDesc(UserFollow::getCreateTime);
        List<UserFollow> list = this.list(qw);
        List<String> ids = new ArrayList<>(list.size());
        for (UserFollow follow : list) {
            ids.add(follow.getFollowId());
        }
        List<User> users = userService.listByIds(ids);
        List<UserListDto> resDto = users.stream().map(user -> {
            UserListDto userListDto = new UserListDto();
            BeanUtils.copyProperties(user, userListDto);
            //是否关注
            boolean follow = this.isFollow(user.getId(), openid);
            if (follow) {
                userListDto.setFollow(true);
                //是否互关
                boolean followToo = this.isFollow(openid, user.getId());
                if (followToo) userListDto.setFollowToo(true);
            } else {
                userListDto.setFollow(false);
                userListDto.setFollowToo(false);
            }
            return userListDto;
        }).collect(Collectors.toList());

        return R.success(resDto);
    }
}

