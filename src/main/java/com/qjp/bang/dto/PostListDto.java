package com.qjp.bang.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostListDto {
    private String id;
    //发贴用户id
    @ApiModelProperty("发贴用户id")
    private String userId;
    //发帖用户头像
    @ApiModelProperty("发帖用户头像")
    private String head;
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
