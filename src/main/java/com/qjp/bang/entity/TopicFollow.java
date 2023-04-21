package com.qjp.bang.entity;

import lombok.Data;

/**
 * (TopicFollow)表实体类
 *
 * @author makejava
 * @since 2023-04-20 21:27:43
 */
@Data
public class TopicFollow  {
    
    private String id;
    //用户id
    private String userId;
    //话题id
    private String topicId;

    }

