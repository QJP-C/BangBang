package com.qjp.bang.controller;

import com.qjp.bang.common.R;
import com.qjp.bang.utils.GPTUtil;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "GPT接口", value = "GPT接口")
@RestController
@CrossOrigin
@RequestMapping("/gpt")
public class GPTController {
    @PostMapping()
    public R get(@RequestBody Map<String,String> map){
        String text = GPTUtil.sendPost(map.get("msg"));
        System.out.println(text);
        return R.success(text);
//        JsonData jsonData = JsonData.bulidSuccess(text);
//        return jsonData;
    }
}
