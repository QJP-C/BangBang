package com.qjp.xjbx.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qjp.xjbx.common.R;
import com.qjp.xjbx.pojo.OnlineMs;
import com.qjp.xjbx.pojo.User;
import com.qjp.xjbx.service.OnlineMsService;
import com.qjp.xjbx.service.UserService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/lt")
public class OnlineMsController {
    @Autowired
    private UserService userService;
    @Autowired
    private OnlineMsService onlineMsService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(fixedRate=14400000)//  每隔4个小时将Redis缓存的数据转存到Mysql中去
    private void configureTasks() {
        System.out.println("=========开始将Redis中的聊天记录转存到Mysql==========");
        log.info("=========开始将Redis中的聊天记录转存到Mysql==========");
        Set<String> keys = redisTemplate.keys("M"+"*");  //获取M开头的所有key
        if(!keys.isEmpty()){
            for (int i = 0; i < keys.size(); i++) {
                String m = (String) keys.toArray()[i];
                String from = m.replaceFirst("M", "");
                log.info("from:{}",from);
                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(User::getId,from);
                User one = userService.getOne(wrapper);
                String fromUsername = one.getUsername();
                String fromHead = one.getHead();
                Set<String> tos = redisTemplate.boundHashOps(m).keys();  //获取M开头的所有key的 hashKey
                for (int j = 0; j < tos.size() ; j++) {
                    String time = (String) tos.toArray()[j];
                    log.info("time:{}",time);
                    String message = (String) redisTemplate.opsForHash().get(m,time);
                    JSONObject jsonObject1 = JSON.parseObject(message);
                    String to = jsonObject1.getString("to");
                    LambdaQueryWrapper<User> wrapper1 = new LambdaQueryWrapper<>();
                    wrapper1.eq(User::getId,to);
                    User one1 = userService.getOne(wrapper1);
                    String toUsername = one1.getUsername();
                    String toHead = one1.getHead();
                    JSONObject jsonObject = JSON.parseObject(message);
//                    LocalDateTime time = LocalDateTime.parse(jsonObject.getString("time"));
                    String lastContext = jsonObject.getString("message");
                    OnlineMs onlineMs = new OnlineMs();
                    onlineMs.setFromId(from);
                    onlineMs.setFromName(fromUsername);
                    onlineMs.setFromHead(fromHead);
                    onlineMs.setToId(to);
                    onlineMs.setToHead(toHead);
                    onlineMs.setToName(toUsername);
                    onlineMs.setSendTime(LocalDateTime.parse(time));
                    onlineMs.setLastContext(lastContext);
                    onlineMsService.save(onlineMs);
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
        wrapper.eq(OnlineMs::getFromId,onlineMs.getFromId()).eq(OnlineMs::getToId,onlineMs.getToId())
                .orderByAsc(OnlineMs::getSendTime);
        List<OnlineMs> list = onlineMsService.list(wrapper);
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
    @Resource
    private MessageController messageController;
    /**
     * 发送消息给客户端
     */

    public  void fa(){
//        //创建业务消息信息
//        JSONObject obj = new JSONObject();
//        obj.put("cmd", "topic");//业务类型
//        obj.put("msgId", sysAnnouncement.getId());//消息id
//        obj.put("msgTxt", sysAnnouncement.getTitile());//消息内容
//        //全体发送
//        messageController.sendAllMessage(obj.toJSONString());
//        //单个用户发送 (userId为用户id)
//        messageController.sendOneMessage(userId, obj.toJSONString());
//        //多个用户发送 (userIds为多个用户id，逗号‘,’分隔)
//        messageController.sendMoreMessage(userIds, obj.toJSONString());
//    }

}

}
