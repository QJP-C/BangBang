package com.qjp.xjbx.controller;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
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
        //将生成的验证码缓存到Redis中，并且设置有效时间为5分钟
        redisTemplate.opsForValue().set(user.getAccount(),code,5, TimeUnit.MINUTES);
        sendMailService.sendMail(user.getAccount(), code);
        return R.success("验证码已发至邮箱");
    }

    /**
     * 注册
     * @param map
     * @return
     */
    @PostMapping("/register")
    public R<User> register(@RequestBody Map map) {
        //获取邮箱
        String account = map.get("account").toString();
        //获取验证码
        String code = map.get("code").toString();
        //获取密码
        String password = map.get("password").toString();
        //从Redis缓存中获取缓存中的验证码
        String codeInRedis = (String) redisTemplate.opsForValue().get(account);
        //进行验证码的比对（页面提交的验证码与Session中保存的验证码对比）
        if (null != codeInRedis && codeInRedis.equals(code)) {
            //比对成功，登录成功
            //判断当前手机号对应的用户是否为新用户，如果是新用户自动完成注册
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getAccount, account);
            User user = userService.getOne(wrapper);
            if (user == null) {
                user = new User();
                user.setAccount(account);
                user.setPassword(password);
                user.setUsername(account);
                boolean save = userService.save(user);
                if (save) {
                    redisTemplate.delete(account);
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
        log.info("密码：[{}]", user.getPassword());
        Map<String, Object> map = new HashMap<>();
        try {
            User userDB = userService.login(user);
            Map<String, String> payload = new HashMap<>();
            payload.put("id", userDB.getId());
            payload.put("account", userDB.getAccount());
            //生成JWT的令牌
            String token = JWTUtils.getToken(payload);
            map.put("state", true);
            map.put("msg", "登录成功");
            map.put("token", token);//响应token
            redisTemplate.opsForValue().set("token",token,60,TimeUnit.MINUTES);
            userDB.setState(1);
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            String id = userDB.getId();
            wrapper.eq(User::getId,userDB.getId());
            userService.update(userDB,wrapper);
        } catch (Exception e) {
            map.put("state", false);
            map.put("msg", e.getMessage());
        }
        return R.success(map);
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @GetMapping("/exit")
    public R<String> exit(HttpServletRequest request){
        String token = request.getHeader("token");
        DecodedJWT verify =JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId,id);
        User one = userService.getOne(wrapper);
        one.setState(0);
        userService.update(one,wrapper);
        redisTemplate.delete("token");
        return R.success("退出成功");
    }
    /**
     * 修改个人信息
     * @param user
     * @return
     */
    @PutMapping("/update")
    public R<String> update(@RequestBody User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccount, user.getAccount());
        userService.update(user, wrapper);
        return R.success("修改成功");

    }

    /**
     * 查看个人信息
     * @param request
     * @return
     */
    @GetMapping
    public R<User> getAll(HttpServletRequest request){
        String token = request.getHeader("token");
        DecodedJWT verify =JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        LambdaQueryWrapper<User>  wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(id != null,User::getId,id)
                .select(User.class,i -> !i.getColumn().equals("password"));
        User one = userService.getOne(wrapper);
        return R.success(one);
    }

}





























