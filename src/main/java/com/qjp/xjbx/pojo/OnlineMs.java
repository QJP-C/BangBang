package com.qjp.xjbx.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天
 * @author qjp
 */
@Data
public class OnlineMs {
    private String   id ;
    private String   fromId ;
    private String   lastContext ;
    private String   toId ;
    private LocalDateTime sendTime ;
    private Integer  isRead;
}
