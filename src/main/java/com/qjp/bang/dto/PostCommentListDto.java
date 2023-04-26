package com.qjp.bang.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("帖子评论返回值")
public class PostCommentListDto {
    @ApiModelProperty("评论id")
    private String id;
    //用户id
    @ApiModelProperty("用户id")
    private String userId;
    //用户名
    @ApiModelProperty("用户名")
    private String username;
    //用户头像
    @ApiModelProperty("用户头像")
    private String head;
    //文本
    @ApiModelProperty("文本")
    private String text;
    //帖子id
    @ApiModelProperty("帖子id")
    private String postId;
    //评论时间
    @ApiModelProperty("评论时间")
    private LocalDateTime commentTime;
    //是否点赞
    @ApiModelProperty("是否点赞")
    private boolean like;
    //点赞数
    @ApiModelProperty("点赞数")
    private long likeNum;
}
