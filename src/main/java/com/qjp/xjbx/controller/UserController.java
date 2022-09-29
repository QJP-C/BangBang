package com.qjp.xjbx.controller;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qjp.xjbx.common.BaseContext;
import com.qjp.xjbx.common.CustomException;
import com.qjp.xjbx.common.R;
import com.qjp.xjbx.pojo.User;
import com.qjp.xjbx.service.SendMailService;
import com.qjp.xjbx.service.UserService;
import com.qjp.xjbx.utils.JWTUtils;
import com.qjp.xjbx.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@CrossOrigin
@Transactional
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SendMailService sendMailService;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 向邮箱发送验证码
     * @param
     * @return
     */
    @PostMapping("/account")
    public R<String> account(@RequestBody User user){
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccount,user.getAccount());
        User one = userService.getOne(wrapper);
        if (one==null){
            //将生成的验证码缓存到Redis中，并且设置有效时间为5分钟
            redisTemplate.opsForValue().set(user.getAccount(),code,5, TimeUnit.MINUTES);
            sendMailService.sendMail(user.getAccount(), code);
            return R.success("验证码已发至邮箱");
        }
        return R.error("该用户已存在");
    }

    /**
     * 注册
     * @param map
     * @return
     */

    @PostMapping("/register")
    public R<User> register(@RequestBody Map map) throws IOException {
        //获取邮箱
        String account = map.get("account").toString();
        //获取验证码
        String code = map.get("code").toString();
        //获取密码
        String password = map.get("password").toString();
        String[] result = account.split("@") ;
        String qq= result[0];
        User ut = userService.register(qq);
        //从Redis缓存中获取缓存中的验证码
        String codeInRedis = (String) redisTemplate.opsForValue().get(account);
        //进行验证码的比对（页面提交的验证码与Session中保存的验证码对比）
        if (null != codeInRedis && codeInRedis.equals(code)) {
            //比对成功，登录成功
            //判断当前手机号对应的用户是否为新用户，如果是新用户自动完成注册
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getAccount, account)
                    .select(User.class,i -> !i.getColumn().equals("password"));
            User user = userService.getOne(wrapper);
            if (user == null) {
                user = new User();
                user.setAccount(account);
                user.setPassword(password);
                user.setUsername(ut.getUsername());
                user.setHead(ut.getHead());
                user.setQq(qq);
                boolean save = userService.save(user);
                if (save) {
                    redisTemplate.delete(account);
                    log.info("用户[{}]登录中",account);
                    return R.success(user,"注册成功");
                }
            }
            redisTemplate.delete(account);
            return R.error("用户已存在");
        }
        return R.error("验证码错误");
    }

    /**
     * 登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody User user) {
        log.info("用卢名：[{}]", user.getAccount());
        Map<String, Object> map = new HashMap<>();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccount,user.getAccount());
        User userDB = userService.getOne(wrapper);
        if (userDB != null){
//        User userDB = userService.login(user);
//        if (userDB!= null){
            wrapper.eq(User::getPassword,user.getPassword());
            User user1 = userService.getOne(wrapper);
            if (user1!=null) {
                Map<String, String> payload = new HashMap<>();
                payload.put("id", userDB.getId());
                payload.put("account", userDB.getAccount());
                //生成JWT的令牌
                String token = JWTUtils.getToken(payload);
                map.put("state", true);
                map.put("msg", "登录成功");
                map.put("token", token);//响应token
                redisTemplate.opsForValue().set(userDB.getId(), token, 7, TimeUnit.DAYS);
                userDB.setState(1);
                LambdaQueryWrapper<User> wrapper2 = new LambdaQueryWrapper<>();
                wrapper2.eq(User::getId, userDB.getId());
                BaseContext.setCurrentId(Long.valueOf(userDB.getId()));
                userService.update(userDB, wrapper);
            }else {
                return R.error("登录失败，密码错误！");
            }
        }else {
            return R.error("登录失败，该用户不存在！");
        }

        return R.success(map);
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @GetMapping("/exit")
    public R<String> exit(@RequestHeader(value="token", required = false) String token,HttpServletRequest request){
//        String token = request.getHeader("token");
        DecodedJWT verify =JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId,id);
        User one = userService.getOne(wrapper);
        one.setState(0);
        userService.update(one,wrapper);
        redisTemplate.delete(id);
        return R.success("退出成功");
    }
    /**
     * 修改个人信息
     * @param user
     * @return
     */
    @PutMapping("/update")
    public R<String> update(@RequestHeader(value="token") String token,@RequestBody User user) {
//        String token = request.getHeader("token");
        DecodedJWT verify =JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, id);
        userService.update(user, wrapper);
        return R.success("修改成功");

    }

    /**
     * 查看个人信息
     * @param
     * @return
     */
    @PostMapping
    public R<User> getAll(HttpServletRequest request){
        String token = request.getHeader("token");
        DecodedJWT verify =JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        log.info("id:[{}]",id);
        LambdaQueryWrapper<User>  wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(id != null,User::getId,id)
                .select(User.class,i -> !i.getColumn().equals("password"));
        User one = userService.getOne(wrapper);
        return R.success(one);
    }

}





























