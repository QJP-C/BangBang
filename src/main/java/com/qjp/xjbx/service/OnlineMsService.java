package com.qjp.xjbx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.xjbx.pojo.OnlineMs;

public interface OnlineMsService extends IService<OnlineMs> {
    String message(String from,String to,String message);

}
