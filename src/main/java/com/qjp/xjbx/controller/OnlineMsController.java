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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                    String time = (String) tos.toArray()[j];
                    String message = (String) redisTemplate.opsForHash().get(m,time);
                    log.info("message:{}",message);
                    JSONObject jsonObject1 = JSON.parseObject(message);
                    String to = jsonObject1.getString("to");

                    JSONObject jsonObject = JSON.parseObject(message);
                    String lastContext = jsonObject.getString("message");
                    OnlineMs onlineMs = new OnlineMs();
                    onlineMs.setFromId(from);
                    onlineMs.setToId(to);
                    onlineMs.setSendTime(LocalDateTime.parse(time));
                    onlineMs.setLastContext(lastContext);
                    onlineMs.setIsRead(0);
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

}
