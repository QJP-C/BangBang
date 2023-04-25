package com.qjp.bang.entity;

import lombok.Data;

import java.util.Date;

/**
 * (PostComment)表实体类
 *
 * @author makejava
 * @since 2023-04-25 21:49:22
 */
@Data
public class PostComment {

    private String id;
    //用户id
    private String userId;
    //文本
    private String text;
    //帖子
    private String postId;
    //评论时间
    private Date commentTime;

}

