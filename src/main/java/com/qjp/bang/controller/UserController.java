package com.qjp.bang.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.utils.StringUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qjp.bang.common.R;
import com.qjp.bang.pojo.User;
import com.qjp.bang.pojo.UserLevel;
import com.qjp.bang.service.SendMailService;
import com.qjp.bang.service.SendSms;
import com.qjp.bang.service.UserLevelService;
import com.qjp.bang.service.UserService;
import com.qjp.bang.utils.JWTUtils;
import com.qjp.bang.utils.MD5Utils;
import com.qjp.bang.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
    @Autowired
    private SendSms sendSms;
    @Autowired
    private UserLevelService userLevelService;

    private  static final String SALT = "915950092";
    /**
     * 发送手机验证码
     * @param phone
     * @return
     */
    @GetMapping("/sms/{phone}")
    public R<String> sendSms(@PathVariable("phone") String phone){
        //查询手机号是否注册过
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone,phone);
        User one = userService.getOne(wrapper);
        if (one!=null){
            return R.error("该用户已存在！");
        }
        //得到电话，先看查一下redis中有无存放验证码
        String code = (String) redisTemplate.opsForValue().get(phone);
        //有则返回已存在
        if (!StringUtils.isEmpty(code)){
            return R.success("验证码未发送 ："+code+"还未过期！",code);
        }else {
            //没有则生成验证码，uuid随机生成四位数验证码
//            code = UUID.randomUUID().toString().substring(0,4);   //随机生成四个数形成验证码
            String code1 = ValidateCodeUtils.generateValidateCode(4).toString();
            HashMap<String, Object> map = new HashMap<>();
            map.put("code",code1);
            //调用方法发送信息 传入电话，模板，验证码   SMS_255290290   SMS_251070336
            boolean send = sendSms.addSendSms(phone, "SMS_255290290", map);
            //返回ture则发送成功
            if (send){
                //存入redis中并设置过期时间，这里设置5分钟过期
                redisTemplate.opsForValue().set(phone,code1,8, TimeUnit.MINUTES);
                return R.success("手机号:"+phone+"验证码发送成功!");
            }else {
                //返回false则发送失败
                return R.error("发送失败");
            }
        }
    }

    /**
     * 向邮箱发送验证码
     * @param
     * @return
     */
    @PostMapping("/email")
    public R<String> account(@RequestBody User user){
        //生成验证码
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        //查询该邮箱是否注册过
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail,user.getEmail());
        User one = userService.getOne(wrapper);
        //没有注册过进行注册
        if (one==null){
            //将生成的验证码缓存到Redis中，并且设置有效时间为5分钟
            redisTemplate.opsForValue().set(user.getEmail(),code,5, TimeUnit.MINUTES);
            sendMailService.sendMail(user.getEmail(), code);
            return R.success("验证码已发至邮箱");
        }
        return R.error("该用户已存在");
    }

    /**
     * 校验验证码
     * @param map
     * @return
     */
    @PostMapping("/yz")
    public R<String>  yanZ(@RequestBody Map map){
        String code = map.get("code").toString();
        //判断是短信验证还是邮箱验证
        int ss=0;
        for (Object key : map.keySet()) {
            if ("phone".equals(key)) {
                ss = 1;
                break;
            }
        }
        if(ss==0){
            //邮箱
            String email = map.get("email").toString();
            String codeInRedis = (String) redisTemplate.opsForValue().get(email);
            if (Objects.equals(codeInRedis, code)){
                redisTemplate.delete(email);
                return R.success("验证码正确");
            }
        }else {
            //手机号
            String phone = map.get("phone").toString();
            String codeInRedis = (String) redisTemplate.opsForValue().get(phone);
            if (Objects.equals(codeInRedis, code)){
                redisTemplate.delete(phone);
                return R.success("验证码正确");
            }
        }
        return R.error("验证码错误");
    }
    /**
     * 注册
     * @param map
     * @return
     */

    @PostMapping("/register")
    public R<User> register(@RequestBody Map map) throws IOException {
        //判断是短信注册还是邮箱注册
        int ss=0;
        for (Object key : map.keySet()) {
            if ("phone".equals(key)) {
                ss = 1;
                break;
            }
        }
        if(ss==0){
            //邮箱注册
            //获取邮箱
            String email = map.get("email").toString();
            //获取密码
            String password = map.get("password").toString();
            String s = MD5Utils.inputPassToDBPass(password, SALT);
            String[] result = email.split("@");
            String qq= result[0];
            //通过api获取用户的qq头像和昵称
            User ut = userService.register(qq);
            //判断当前邮箱对应的用户是否为新用户，如果是新用户完成注册
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, email);
            User user = userService.getOne(wrapper);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setPassword(s);
                if(ut.getUsername()!=null){
                    user.setUsername(ut.getUsername());
                }
                user.setHead(ut.getHead());
                user.setQq(qq);
                user.setCredibility(1);
                user.setExperience(0);
                user.setSex(1);
                user.setState(0);
                user.setPermissions(0);
                boolean save = userService.save(user);
                if (save) {
                    log.info("用户[{}]登录中",email);
                    return R.success(user,"注册成功");
                }
            }
            return R.error("用户已存在");
        }else {
            //短信注册
            //获取手机号
            String phone = map.get("phone").toString();
            //获取密码
            String password = map.get("password").toString();
            String s = MD5Utils.inputPassToDBPass(password, SALT);
            //判断当前手机号对应的用户是否为新用户，如果是新用户完成注册
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, phone);
            User user = userService.getOne(wrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setPassword(s);
                user.setCredibility(1);
                user.setExperience(0);
                user.setSex(1);
                user.setState(0);
                user.setPermissions(0);
                boolean save = userService.save(user);
                if (save) {
                    log.info("用户[{}]登录中",phone);
                    return R.success(user,"注册成功");
                }
            }
            return R.error("用户已存在");
        }
    }

    /**
     * 登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody User user) {
        if (user.getEmail()!=null){
            //邮箱
            log.info("邮箱：[{}]", user.getEmail());
            Map<String, Object> map = new HashMap<>();
            //查看用户是否存在
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail,user.getEmail());
            User userDB = userService.getOne(wrapper);
            if (userDB != null){
                //存在可登录
                String password = user.getPassword();
                String s = MD5Utils.inputPassToDBPass(password, SALT);
                wrapper.eq(User::getPassword,s);
                User user1 = userService.getOne(wrapper);
                if (user1!=null) {
                    Map<String, String> payload = new HashMap<>();
                    payload.put("id", userDB.getId());
                    payload.put("email", userDB.getEmail());
                    payload.put("permissions", String.valueOf(userDB.getPermissions()));
                    //生成JWT的令牌
                    String token = JWTUtils.getToken(payload);
                    map.put("state", true);
                    map.put("msg", "登录成功");
                    map.put("token", token);//响应token
                    //缓存token
                    redisTemplate.opsForValue().set(userDB.getId(), token, 7, TimeUnit.DAYS);
                    //改变用户登录状态
                    userDB.setState(1);
                    LambdaQueryWrapper<User> wrapper2 = new LambdaQueryWrapper<>();
                    wrapper2.eq(User::getId, userDB.getId());
                    userService.update(userDB, wrapper);
                }else {
                    return R.error("登录失败，密码错误！");
                }
            }else {
                return R.error("登录失败，该用户不存在！");
            }
            return R.success(map);
        }else {
            log.info("手机号：[{}]", user.getPhone());
            Map<String, Object> map = new HashMap<>();
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, user.getPhone());
            User userDB = userService.getOne(wrapper);
            if (userDB != null) {
                String password = user.getPassword();
                String s = MD5Utils.inputPassToDBPass(password, SALT);
                wrapper.eq(User::getPassword,s );
                User user1 = userService.getOne(wrapper);
                if (user1 != null) {
                    Map<String, String> payload = new HashMap<>();
                    payload.put("id", userDB.getId());
                    payload.put("phone", user.getPhone());
                    payload.put("permissions", String.valueOf(userDB.getPermissions()));
                    //生成JWT的令牌
                    String token = JWTUtils.getToken(payload);
                    map.put("state", true);
                    map.put("msg", "登录成功");
                    map.put("token", token);//响应token
                    redisTemplate.opsForValue().set(userDB.getId(), token, 7, TimeUnit.DAYS);
                    userDB.setState(1);
                    LambdaQueryWrapper<User> wrapper2 = new LambdaQueryWrapper<>();
                    wrapper2.eq(User::getId, userDB.getId());
                    userService.update(userDB, wrapper);
                } else {
                    return R.error("登录失败，密码错误！");
                }
            } else {
                return R.error("登录失败，该用户不存在！");
            }
            return R.success(map);
        }
    }

    /**
     * 退出登录
     * @return
     */
    @GetMapping("/exit")
    public R<String> exit(@RequestHeader(value="token") String token){
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
        DecodedJWT verify =JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
//        User byId = userService.getById(id);
        user.setId(id);
        userService.updateById(user);
//        userService.updateById(byId);
        return R.success("修改成功");
    }

    /**
     * 查看个人信息
     * @param
     * @return
     */
    @PostMapping
    public R<User> getAll(@RequestHeader(value="token") String token,@RequestBody(required = false) User user){
        String id;
        //参数是否有user
        if (user == null){
            //没有就查自己
            DecodedJWT verify =JWTUtils.verify(token);
             id = verify.getClaim("id").asString();
        }else {
            //有就查指定
             id = user.getId();
        }
        log.info("id:[{}]",id);
        LambdaQueryWrapper<User>  wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(id != null,User::getId,id)
                .select(User.class,i -> !"password".equals(i.getColumn()));
        User one = userService.getOne(wrapper);
        return R.success(one);
    }

    /**
     * 定位
     * @return
     */
    @GetMapping("/location")
    public R<String> location(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        String uri = "https://apis.map.qq.com/ws/location/v1/ip?key=VSXBZ-S76LQ-N6K5O-G3BNK-WCTYF-WSF2T";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        String body = response.getBody();
        JSONObject jsonObject = JSON.parseObject(body);
        String result = jsonObject.getString("result");
        JSONObject jsonObject1 = JSON.parseObject(result);
        String ad_info = jsonObject1.getString("ad_info");
        JSONObject jsonObject2 = JSON.parseObject(ad_info);
        String nation = jsonObject2.getString("nation");
        JSONObject jsonObject3 = JSON.parseObject(ad_info);
        String province = jsonObject3.getString("province");
        JSONObject jsonObject4 = JSON.parseObject(ad_info);
        String city = jsonObject4.getString("city");
        JSONObject jsonObject5 = JSON.parseObject(ad_info);
        String district = jsonObject5.getString("district");
        String location=nation+province+city+district;
        log.info("location:{}",location);
        return R.success(location);
    }

    /**
     * 加经验
     */
    @PostMapping("/up")
    public R<String> shen(@RequestHeader(value="token") String token,@RequestBody  User user){
        //增加的经验值
        int exp = user.getExperience();
        DecodedJWT verify =JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        //查用户信息
        User byId = userService.getById(id);
        //根据用户等级查升级所需的经验
        LambdaQueryWrapper<UserLevel> wrap = new LambdaQueryWrapper<>();
        wrap.eq(UserLevel::getLevel,byId.getCredibility());
        UserLevel userLevel = userLevelService.getOne(wrap);
        int s = userLevel.getExperience();
        //判断是否需要升级
        int vv ;
        if (exp<s){
            vv = 0;
            byId.setExperience(byId.getExperience()+exp);
        }else {
            vv=1;
            byId.setCredibility(byId.getCredibility()+1);
            byId.setExperience(exp-s);
        }
        userService.updateById(byId);
        if (vv == 0){
            return R.success("已增加"+exp+"经验");
        }
        return R.success("用户已升级至"+byId.getCredibility()+"级");
    }

    /**
     * 扣经验
     * @param token
     * @param user
     * @return
     */
    @PostMapping("/down")
    public R<String>  down(@RequestHeader(value="token") String token,@RequestBody  User user){
        int exp = user.getExperience();
        DecodedJWT verify =JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        User byId = userService.getById(id);
        LambdaQueryWrapper<UserLevel> wrap = new LambdaQueryWrapper<>();
        wrap.eq(UserLevel::getLevel,byId.getCredibility());
        UserLevel userLevel = userLevelService.getOne(wrap);
        int t = byId.getExperience() - exp;
        int vv ;
        if (t>=0){
            vv = 0;
            byId.setExperience(t);
        }else {
            vv=1;
            if (byId.getCredibility() == 1){
                byId.setExperience(0);
            }else {
                LambdaQueryWrapper<UserLevel> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(UserLevel::getLevel, userLevel.getLevel() - 1);
                UserLevel one = userLevelService.getOne(wrapper);
                byId.setCredibility(byId.getCredibility() - 1);
                byId.setExperience(one.getExperience() + t);
            }
        }
        userService.updateById(byId);
        if (vv == 0){
            return R.success("已减少"+exp+"经验");
        }
        return R.success("用户已降级至"+byId.getCredibility()+"级");
    }
}





























