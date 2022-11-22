package com.qjp.bang.pojo;

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
    private String   fromHead;
    private String   fromName;
    private String   lastContext ;
    private String   toId ;
    private String   toHead ;
    private String   toName ;
    private LocalDateTime sendTime ;
    private Integer  isRead;
}
