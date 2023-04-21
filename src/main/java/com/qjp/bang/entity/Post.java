package com.qjp.bang.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * (Post)表实体类
 *
 * @author makejava
 * @since 2023-04-21 20:44:28
 */
@Data
public class Post {
    
    private String id;
    //发贴用户id
    private String userId;
    //文本
    @ApiModelProperty("文本")
    private String text;
    //话题id
    @ApiModelProperty("话题id")
    private String topicId;
    //发布时间
    @ApiModelProperty("发布时间")
    private LocalDateTime releaseTime;
    //位置
    @ApiModelProperty("位置")
    private String location;


}

