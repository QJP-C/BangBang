package com.qjp.bang.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qjp.bang.common.R;
import com.qjp.bang.dto.MsgDto;
import com.qjp.bang.pojo.OnlineMs;
import com.qjp.bang.pojo.User;
import com.qjp.bang.service.OnlineMsService;
import com.qjp.bang.service.UserService;
import com.qjp.bang.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qjp
 */
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/lt")
@Transactional
public class OnlineMsController {
    @Autowired
    private UserService userService;
    @Autowired
    private OnlineMsService onlineMsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MessageController messageController;

    @Scheduled(fixedRate=14400000)//  每隔4个小时将Redis缓存的数据转存到Mysql中去
    public void configureTasks() {
        System.out.println("=========开始将Redis中的聊天记录转存到Mysql==========");
        log.info("=========开始将Redis中的聊天记录转存到Mysql==========");
        Set<String> keys = redisTemplate.keys("M"+"*");  //获取M开头的所有key
        if(!keys.isEmpty()){
            for (int i = 0; i < keys.size(); i++) {
                String m = (String) keys.toArray()[i];
                String from = m.replaceFirst("M", "");
                log.info("from:{}",from);
                //获取M开头的所有key的 hashKey
                Set<String> tos = redisTemplate.boundHashOps(m).keys();
                for (int j = 0; j < tos.size() ; j++) {
                    String toId = (String) tos.toArray()[j];
                    List<String> message = (List<String>) redisTemplate.opsForHash().get(m,toId);
                    log.info("message:{}",message);
                    for (String s : message) {
                        JSONObject jsonObject1 = JSON.parseObject(s);
                        String time = jsonObject1.getString("time");
                        JSONObject jsonObject = JSON.parseObject(s);
                        String lastContext = jsonObject.getString("message");
                        User fromById = userService.getById(from);
                        User toById = userService.getById(toId);
                        OnlineMs onlineMs = new OnlineMs();
                        onlineMs.setFromId(from);
                        onlineMs.setToId(toId);
                        onlineMs.setSendTime(LocalDateTime.parse(time));
                        onlineMs.setLastContext(lastContext);
                        onlineMs.setIsRead(0);
                        onlineMs.setFromHead(fromById.getHead());
                        onlineMs.setFromName(fromById.getUsername());
                        onlineMs.setToHead(toById.getHead());
                        onlineMs.setToName(toById.getUsername());
                        onlineMsService.save(onlineMs);
                    }
//                    JSONObject jsonObject1 = JSON.parseObject(message);
//                    String time = jsonObject1.getString("time");
//                    JSONObject jsonObject = JSON.parseObject(message);
//                    String lastContext = jsonObject.getString("message");
//                    OnlineMs onlineMs = new OnlineMs();
//                    onlineMs.setFromId(from);
//                    onlineMs.setToId(toId);
//                    onlineMs.setSendTime(LocalDateTime.parse(time));
//                    onlineMs.setLastContext(lastContext);
//                    onlineMs.setIsRead(0);
//                    onlineMsService.save(onlineMs);
                }
                redisTemplate.delete(m);
            }
        }else {
            System.out.println("==================暂无新消息记录===================");
            log.info("==================暂无新消息记录===================");
        }
        System.out.println("==================转存结束===================");
        log.info("==================转存结束===================");
    }

    /**
     * 查询与某人聊天记录
     * @return
     */
    @PostMapping("/jl")
    public R<List<OnlineMs>> get(@RequestBody OnlineMs onlineMs){
        this.configureTasks();
        LambdaQueryWrapper<OnlineMs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OnlineMs::getFromId,onlineMs.getFromId())
                .eq(OnlineMs::getToId,onlineMs.getToId())
                .or()
                .eq(OnlineMs::getFromId,onlineMs.getToId())
                .eq(OnlineMs::getToId,onlineMs.getFromId())
                .orderByAsc(OnlineMs::getSendTime);
        List<OnlineMs> list = onlineMsService.list(wrapper);
        List<OnlineMs> msList = list.stream().map((item)->{
            OnlineMs onlineMs1 = new OnlineMs();
            BeanUtils.copyProperties(item,onlineMs1);
            onlineMs1.setIsRead(1);
            return onlineMs1;
        }).collect(Collectors.toList());
        onlineMsService.updateBatchById(msList);
        return R.success(list);
    }

    /**
     * 删除聊天记录
     * @param onlineMs
     * @return
     */
    @DeleteMapping("/delete")
    public R<String> delete(@RequestBody OnlineMs onlineMs){
        this.configureTasks();
        boolean b = onlineMsService.removeById(onlineMs.getId());
        if (b){
            return R.success("删除成功");
        }else {
            return R.error("删除失败");
        }

    }

    /**
     * 发送消息给客户端
     */
    @PostMapping("/fa")
    public R<String> fa(@RequestParam(required = false) String id,
                        @RequestParam(required = false) String[] ids,
                        @RequestParam String msg,
                        @RequestParam String type){
        log.info("ids:{}", (Object) ids);
        //创建业务消息信息
        JSONObject obj = new JSONObject();
        obj.put("type", type);//业务类型
        obj.put("msgId", id);//消息id
        obj.put("msgTxt", msg);//消息内容
        if (id!=null){
            //单个用户发送 (userId为用户id)
            messageController.sendOneMessage(id, obj.toJSONString());
            return R.success("消息已发送至"+id);
        }
        if (ids != null){
            //多个用户发送 (userIds为多个用户id，逗号‘,’分隔)
            messageController.sendMoreMessage(ids, obj.toJSONString());
            return R.success("消息已发送至"+ Arrays.toString(ids));
        }
        //全体发送
        messageController.sendAllMessage(obj.toJSONString());
        return R.success("消息已群发");
    }

    /**
     * 消息列表
     * @param token
     * @return
     */
    @GetMapping("/lb")
    public R<List>  list(@RequestHeader(value = "token")String token) {
        //同步缓存与数据库中的聊天记录
        this.configureTasks();
        DecodedJWT verify = JWTUtils.verify(token);
        String userId = verify.getClaim("id").asString();
        //分组查询
        LambdaQueryWrapper<OnlineMs> wrap = new LambdaQueryWrapper<>();
        wrap    .groupBy(OnlineMs::getFromId)
                .select(OnlineMs::getFromId,OnlineMs::getSendTime,OnlineMs::getIsRead,
                        OnlineMs::getLastContext,OnlineMs::getToId,OnlineMs::getId,
                        OnlineMs::getFromHead,OnlineMs::getFromName,OnlineMs::getToHead,OnlineMs::getToName)
                .eq(OnlineMs::getFromId, userId)
                .or()
                .eq(OnlineMs::getToId, userId)
                .orderByDesc(OnlineMs::getFromId)
                .orderByDesc(OnlineMs::getSendTime);
        List<OnlineMs> list = onlineMsService.list(wrap);
        String fromId = null;
        String toId = null;
        List<MsgDto> msgList = new ArrayList<>();
        for (OnlineMs onlineMs : list) {
            if (!(Objects.equals(onlineMs.getFromId(), toId) && Objects.equals(onlineMs.getToId(), fromId))) {
                if (Objects.equals(userId,onlineMs.getFromId())){
                    MsgDto msg = new MsgDto();
                    msg.setHead(onlineMs.getToHead());
                    msg.setName(onlineMs.getToName());
                    msg.setId(onlineMs.getToId());
                    msgList.add(msg);
                }else {
                    MsgDto msg = new MsgDto();
                    msg.setHead(onlineMs.getFromHead());
                    msg.setName(onlineMs.getFromName());
                    msg.setId(onlineMs.getFromId());
                    msgList.add(msg);
                }
            }
            fromId=onlineMs.getFromId();
            toId=onlineMs.getToId();
        }
        log.info("list:{}",msgList);
        for (MsgDto msgDto : msgList) {
            LambdaQueryWrapper<OnlineMs> wrap1 = new LambdaQueryWrapper<>();
            wrap1   .eq(OnlineMs::getFromId,userId)
                    .eq(OnlineMs ::getToId,msgDto.getId())
                    .or()
                    .eq(OnlineMs ::getToId,userId)
                    .eq(OnlineMs ::getFromId,msgDto.getId())
                    .orderByDesc(OnlineMs::getSendTime).last("limit 1");
            OnlineMs one = onlineMsService.getOne(wrap1);
            msgDto.setSendTime(one.getSendTime());
            msgDto.setLastMsg(one.getLastContext());

            User byId = userService.getById(msgDto.getId());
            msgDto.setLogin(byId.getState()==1);
            LambdaQueryWrapper<OnlineMs> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OnlineMs ::getToId,userId)
                    .eq(OnlineMs ::getFromId,msgDto.getId())
                    .eq(OnlineMs::getIsRead,0);
            int count = onlineMsService.count(wrapper);
            msgDto.setIsRead(count);
        }
        log.info("msgList:{}",msgList);
        return R.success(msgList);
    }
//    }  /**
//     * 消息列表
//     * @param token
//     * @return
//     */
//    @GetMapping("/lb")
//    public R<List>  list(@RequestHeader(value = "token")String token){
//        //同步缓存与数据库中的聊天记录
//        this.configureTasks();
//        DecodedJWT verify = JWTUtils.verify(token);
//        String userId = verify.getClaim("id").asString();
//        //分组查询
//        LambdaQueryWrapper<OnlineMs> wrap = new LambdaQueryWrapper<>();
//        wrap
//                .groupBy(OnlineMs::getFromId)
//                .select(OnlineMs::getFromId,OnlineMs::getSendTime,OnlineMs::getIsRead,OnlineMs::getLastContext,OnlineMs::getToId,OnlineMs::getId)
//                .eq(OnlineMs::getFromId,userId)
//                .or()
//                .eq(OnlineMs ::getToId,userId)
//                .orderByDesc(OnlineMs::getSendTime)
////                .orderByDesc(OnlineMs::getFromId)
//        ;
//        List<OnlineMs> list = onlineMsService.list(wrap);
//        String fromId = null;
//        String toId = null;
//        ArrayList<String> ss = new ArrayList<>();
//        //去除重复数据，取出需要的id并加入ss集合
//        for (OnlineMs onlineMs : list) {
//            if (!(Objects.equals(onlineMs.getFromId(), toId) && Objects.equals(onlineMs.getToId(), fromId))){
//
//                if (Objects.equals(userId,onlineMs.getFromId())){
//                    ss.add(onlineMs.getToId());
//                }else {
//                    ss.add(onlineMs.getFromId());
//                }
//            }
//             fromId = onlineMs.getFromId();
//             toId = onlineMs.getToId();
//        }
//
//        return R.success(ss);
//    }

//    /**
//     * 未读消息
//     * @param token
//     * @return
//     */
//    @GetMapping("/read")
//    public R<List<OnlineMs>> isRead(@RequestHeader(value = "token")String token){
//        this.configureTasks();
//        DecodedJWT verify = JWTUtils.verify(token);
//        String userId = verify.getClaim("id").asString();
//        LambdaQueryWrapper<OnlineMs> wrap = new LambdaQueryWrapper<>();
//        wrap.eq(OnlineMs ::getToId,userId)
//                .eq(OnlineMs::getIsRead,0);
//        List<OnlineMs> list = onlineMsService.list(wrap);
//        return R.success(list);
//    }
}
