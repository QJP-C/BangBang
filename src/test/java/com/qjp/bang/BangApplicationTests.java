package com.qjp.bang;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qjp.bang.dto.TaskClassDto;
import com.qjp.bang.dto.UserUpdate;
import com.qjp.bang.entity.TaskClass;
import com.qjp.bang.entity.User;
import com.qjp.bang.service.TaskClassService;
import com.qjp.bang.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BangApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private UserService userService;

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
    @Test
    void taskClass(){
        List<TaskClass> list = taskClassService.list();
        Map<Integer, TaskClassDto> map = new HashMap<>();
        for (TaskClass aClass : list) {
            //是父节点
            if (aClass.getFather()==0){
                TaskClassDto taskClassDto = new TaskClassDto();
                BeanUtils.copyProperties(aClass,taskClassDto);
                List<TaskClass> list1 = new ArrayList<>();
                taskClassDto.setSon(list1);
                map.put(aClass.getId(),taskClassDto);
            }
        }
        for (TaskClass aClass : list) {
            if (aClass.getFather()!=0){
                TaskClassDto taskClassDto = map.get(aClass.getFather());
                List<TaskClass> son = taskClassDto.getSon();
                son.add(aClass);
                taskClassDto.setSon(son);
                map.put(aClass.getFather(),taskClassDto);
            }
        }


        System.out.println(map);


    }
}


