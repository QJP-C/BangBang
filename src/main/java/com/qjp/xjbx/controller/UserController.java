package com.qjp.xjbx.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qjp.xjbx.common.BaseContext;
import com.qjp.xjbx.common.CustomException;
import com.qjp.xjbx.common.R;
import com.qjp.xjbx.pojo.User;
import com.qjp.xjbx.service.SendMailService;
import com.qjp.xjbx.service.UserService;
import com.qjp.xjbx.utils.HttpRestUtils;
import com.qjp.xjbx.utils.JWTUtils;
import com.qjp.xjbx.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
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

    /**
     * 向邮箱发送验证码
     * @param
     * @return
     */
    @PostMapping("/email")
    public R<String> account(@RequestBody User user){
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail,user.getEmail());
        User one = userService.getOne(wrapper);
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
        String email = map.get("email").toString();
        String code = map.get("code").toString();
        log.info("code:{}",code);
        String codeInRedis = (String) redisTemplate.opsForValue().get(email);
        if (Objects.equals(codeInRedis, code)){
            redisTemplate.delete(email);
            return R.success("验证码正确");
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
        //获取邮箱
        String email = map.get("email").toString();
        //获取密码
        String password = map.get("password").toString();
        String[] result = email.split("@") ;
        String qq= result[0];
        User ut = userService.register(qq);
        //判断当前手机号对应的用户是否为新用户，如果是新用户自动完成注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email)
                .select(User.class,i -> !i.getColumn().equals("password"));
        User user = userService.getOne(wrapper);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setUsername(ut.getUsername());
            user.setHead(ut.getHead());
            user.setQq(qq);
            boolean save = userService.save(user);
            if (save) {
                log.info("用户[{}]登录中",email);
                return R.success(user,"注册成功");
            }
        }
        return R.error("用户已存在");
    }

    /**
     * 登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody User user) {
        log.info("用卢名：[{}]", user.getEmail());
        Map<String, Object> map = new HashMap<>();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail,user.getEmail());
        User userDB = userService.getOne(wrapper);
        if (userDB != null){
//        User userDB = userService.login(user);
//        if (userDB!= null){
            wrapper.eq(User::getPassword,user.getPassword());
            User user1 = userService.getOne(wrapper);
            if (user1!=null) {
                Map<String, String> payload = new HashMap<>();
                payload.put("id", userDB.getId());
                payload.put("email", userDB.getEmail());
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


}





























