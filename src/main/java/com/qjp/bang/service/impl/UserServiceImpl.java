package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.UserInfo;
import com.qjp.bang.dto.UserMyInfo;
import com.qjp.bang.dto.UserUpdate;
import com.qjp.bang.entity.User;
import com.qjp.bang.entity.UserFollow;
import com.qjp.bang.mapper.UserMapper;
import com.qjp.bang.service.PostLikeService;
import com.qjp.bang.service.SendSms;
import com.qjp.bang.service.UserFollowService;
import com.qjp.bang.service.UserService;
import com.qjp.bang.utils.EncryptUtil;
import com.qjp.bang.utils.GetUserInfoUtil;
import com.qjp.bang.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * (User)表服务实现类
 *
 * @author makejava
 * @since 2023-04-13 16:44:27
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    JwtUtil jwtUtil;

    @Resource
    SendSms sendSms;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    PostLikeService postLikeService;

    @Resource
    private UserFollowService userFollowService;               

    /**
     * 微信登陆
     * @param code
     * @return
     */
    @Override
    public R wxLogin(String code) {
        //通过code换取信息
        JSONObject resultJson = GetUserInfoUtil.getResultJson(code);

        if (resultJson.has("openid")) {
            //获取sessionKey和openId
            String sessionKey = resultJson.get("session_key").toString();
            String openid = resultJson.get("openid").toString();
            System.out.println(openid);
            //生成自定义登录态session
            String session = null;
            Map<String, String> sessionMap = new HashMap<>();

            sessionMap.put("sessionKey", sessionKey);
            sessionMap.put("openid", openid);
            session = JSONObject.fromObject(sessionMap).toString();

            //加密session
            try {
                EncryptUtil encryptUtil = new EncryptUtil();
                session = encryptUtil.encrypt(session);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //生成token
            String token = jwtUtil.getToken(session);
            stringRedisTemplate.opsForValue().set(openid,token,7, TimeUnit.DAYS);
            Map<String, String> result = new HashMap<>();
            result.put("token", token);

            //查询用户是否存在
            User owner = this.getById(openid);
            if (owner == null) { //用户不存在，新建用户信息
                User user = new User();
                user.setId(openid);
                user.setSex(0);
                String substring = openid.substring(0, 8);
                user.setUsername("帮帮用户" + substring);
                user.setSignature("这个用户懒且不够个性，暂时没有个性签名");
                boolean rs = this.save(user);
                if (!rs) {
                    log.info("用户登陆失败 id: {}",openid);
                    return R.error("登录失败");
                }
            }
            log.info("用户登陆 id: {}",openid);
            return R.success(result, "登录成功"); //用户存在，返回结果
        }
        log.info("用户授权失败");
        return R.error("授权失败" + resultJson.getString("errmsg"));
    }


    /**
     * 修改用户信息
     * @param userUpdate
     * @param openid
     * @return
     */
    @Override
    public R updateInfo(UserUpdate userUpdate, String openid) {
        User user = this.getById(openid);
        BeanUtils.copyProperties(userUpdate,user);
        boolean b = this.updateById(user);
        return b ? R.success("修改成功") : R.error("修改失败");
    }

    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    @Override
    public R send(String phone) {
        //查看该手机号是否绑定过
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getPhone,phone);
        int count = this.count(qw);
        if ( count>0 ){
            return R.error("该手机号已绑定其他账号，请使用其他手机号!");
        }

        boolean b = sendSms.addSendSms(phone);
        if (!b){
            return R.error("发送失败,请检查您的手机号是否填写正确!");
        }

        return R.success("发送成功!");
    }

    /**
     * 校验验证码
     * @param openid
     * @param phone
     * @param code
     * @return
     */
    @Override
    public R<String> check(String openid, String phone, String code) {
        String codeInRedis = (String) stringRedisTemplate.opsForValue().get(phone);
        User user = new User();
        user.setId(openid);
        user.setPhone(phone);
        this.updateById(user);
        return codeInRedis.equals(code) ?  R.success("验证码正确") : R.error("验证码错误");
    }

    /**
     * 个人信息
     * @param id
     * @return
     */
    @Override
    public R<UserMyInfo> myInfo(String id) {
        User user = this.getById(id);
        UserMyInfo userMyInfo = new UserMyInfo();
        //获取被赞数
        Long likeNum = postLikeService.getUserLikeNum(id);
        userMyInfo.setNice(likeNum);
        //获取关注数
        Long followNum = userFollowService.userFollowNum(id);
        userMyInfo.setFollow(followNum);
        //获取粉丝数
        Long fansNum = userFollowService.userFansNum(id);
        userMyInfo.setFans(fansNum);
        BeanUtils.copyProperties(user,userMyInfo);
        return R.success(userMyInfo);
    }

    /**
     * 他人信息
     * @param id
     * @param toOpenid
     * @return
     */
    @Override
    public R<UserInfo> otherInfo(String id, String toOpenid) {
        boolean b = haveOne(toOpenid);
        if (!b){
            return R.error("不存在该用户");
        }
        User user = this.getById(id);
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user,userInfo);
        LambdaQueryWrapper<UserFollow> qw = new LambdaQueryWrapper<>();
        qw.eq(UserFollow::getFollowId,toOpenid);
        qw.eq(UserFollow::getUserId,id);
        int count = userFollowService.count(qw);
        if (count>0){
            userInfo.setIsFollow(true);
        }
        //获取粉丝数
        Long fansNum = userFollowService.userFansNum(id);
        userInfo.setFans(fansNum);
        //获取被赞数
        Long likeNum = postLikeService.getUserLikeNum(id);
        userInfo.setNice(likeNum);
        //获取关注数
        Long followNum = userFollowService.userFollowNum(id);
        userInfo.setFollow(followNum);
        return R.success(userInfo);
    }

    /**
     * 获取用户资料
     * @param userId
     * @return
     */
    @Override
    public Map<String, String> getOneInfo(String userId) {
        User user = this.getById(userId);
        Map<String,String> res = new HashMap<String,String>();
        res.put("head",user.getHead());
        res.put("username", user.getUsername());
        return res;
    }

    /**
     * 查询是否有该用户
     *
     * @param id
     * @return
     */
    @Override
    public boolean haveOne(String id) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getId, id);
        //是否有这个人
        int count = this.count(qw);
        return count > 0;
    }

}

