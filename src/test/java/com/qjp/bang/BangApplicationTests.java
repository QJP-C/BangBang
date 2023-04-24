package com.qjp.bang;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qjp.bang.dto.UserUpdate;
import com.qjp.bang.entity.Task;
import com.qjp.bang.entity.User;
import com.qjp.bang.service.TaskClassService;
import com.qjp.bang.service.TaskService;
import com.qjp.bang.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BangApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private TaskService taskService;
    @Test
    void csss() {
        UserUpdate userUpdate = new UserUpdate();
        userUpdate.setPhone("191911151");
        userUpdate.setSex(0);
        User user = userService.getById("1");
        BeanUtils.copyProperties(userUpdate, user);
        boolean b = userService.updateById(user);
        System.out.println(b);
    }

    @Test
    void ss() {
        String phone = "s18119451226";
        String code = "s1611";
        redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
        System.out.println(redisTemplate.opsForValue().get(phone));
    }

    @Test
    void  cscw(){
        User user = new User();
        user.setId("1");
        userService.getOne(new LambdaQueryWrapper<>(user));

    }
    @Test
    void  csscw(){
        String openid = "dnsondn";
        String token = "odinsoncon";
        redisTemplate.opsForValue().set(openid,token,7, TimeUnit.DAYS);
        System.out.println(redisTemplate.opsForValue().get(openid));
    }
    @Resource
    private TaskClassService taskClassService;
//    @Test
//    void taskClass(){
//        List<TaskClass> list = taskClassService.list();
//        Map<Integer, TaskClassDto> map = new HashMap<>();
//        for (TaskClass aClass : list) {
//            //是父节点
//            if (aClass.getFather()==0){
//                TaskClassDto taskClassDto = new TaskClassDto();
//                BeanUtils.copyProperties(aClass,taskClassDto);
//                List<TaskClass> list1 = new ArrayList<>();
//                taskClassDto.setSon(list1);
//                map.put(aClass.getId(),taskClassDto);
//            }
//        }
//        for (TaskClass aClass : list) {
//            if (aClass.getFather()!=0){
//                TaskClassDto taskClassDto = map.get(aClass.getFather());
//                List<TaskClass> son = taskClassDto.getSon();
//                son.add(aClass);
//                taskClassDto.setSon(son);
//                map.put(aClass.getFather(),taskClassDto);
//            }
//        }
//
//
//        System.out.println(map);
//
//
//    }

    @Test
    void nnn(){
        User user = new User();
        user.setId("oI1vd5DC3H0lVyJizpK58ZPS9Mz8");
        user.setPhone("121161611");
        userService.updateById(user);
    }

    @Test
    void redissss(){
        String key = "topicTest";
        String cishu = "topicTest";
        boolean member = redisTemplate.opsForSet().isMember(key, cishu);
        System.out.println(member);
    }

    @Test
    void testPage(){
        //分页构造器对象
        LambdaQueryWrapper<Task> qw = new LambdaQueryWrapper<>();
        Page<Task> pageInfo = new Page<>(1, 2);
        qw.orderByDesc(Task::getReleaseTime);
        taskService.page(pageInfo,qw);
        System.out.println(pageInfo.getRecords());
        List<Task> list = pageInfo.getRecords();
//        BeanUtils.copyProperties(pageInfo,list);
        list.forEach(System.out::println);
    }
    @Test
    void rediskeyadd(){
        System.out.println(redisTemplate.opsForValue().get("ss"));
    }
    @Test
    void rediskeyadd1(){
        System.out.println(redisTemplate.opsForValue().increment("ss"));
    }

    @Test
    void snovn(){
//        System.out.println(DateUtil.getZodiac(Month.JANUARY.getValue(), 28));
        String dateStr1 = "2017-03-01 22:33:23";
        Date date1 = DateUtil.parse(dateStr1);
        LocalDateTime of = LocalDateTimeUtil.of(date1);
        System.out.println(LocalDateTimeUtil.ofUTC(date1.getTime()));
    }
    @Test
    void testListSort(){
        LambdaQueryWrapper<Task> qw = new LambdaQueryWrapper<>();
        List<Task> list = taskService.list();
        list.forEach(System.out::println);
        System.out.println("================================================");
        ListUtil.sortByProperty(list, "limitTime");
        ListUtil.reverse(list);
        list.forEach(System.out::println);
    }
    @Test
    void RedisClass(){
        redisTemplate.opsForValue().set("aaa","aaaa");
        stringRedisTemplate.opsForValue().set("bbb","bbbb");
    }
    @Test
    void RedisClass1(){
        String date ="2023-04-24 19:41:08";
        Date date1 = DateUtil.parse(date);
        System.out.println(date1.getTime());
    }
    @Test
    void RedisClass21(){
        //当前 1682339809761
        System.out.println(System.currentTimeMillis());
    }

}


