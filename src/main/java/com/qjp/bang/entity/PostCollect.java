package com.qjp.bang.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * (PostCollect)表实体类
 *
 * @author makejava
 * @since 2023-04-22 16:06:27
 */
@Data
public class PostCollect {

    private String id;
    //帖子
    private String postId;
    //用户id
    private String userId;
    //收藏时间
    private LocalDateTime collectTime;


}

