package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.pojo.OnlineMs;
/**
 * @author qjp
 */
public interface OnlineMsService extends IService<OnlineMs> {
    String message(String from,String to,String message);
    String offline(String from,String to,String message);
    void fa(String id,String msg);
}
