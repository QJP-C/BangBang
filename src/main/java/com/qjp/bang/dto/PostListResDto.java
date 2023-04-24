package com.qjp.bang.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("帖子分页集合响应参数")
public class PostListResDto {
    @ApiModelProperty("帖子id")
    private String id;
    //发贴用户id
    @ApiModelProperty("发贴用户id")
    private String userId;
    //发帖用户头像
    @ApiModelProperty("发帖用户头像")
    private String head;
    //发帖用户头像
    @ApiModelProperty("发帖用户昵称")
    private String username;
    //是否关注发贴人
    @ApiModelProperty("是否关注发贴人")
    private boolean follow;
    //文本
    @ApiModelProperty("文本")
    private String text;
    //话题id
    @ApiModelProperty("话题id")
    private String topicId;
    //话题名称
    @ApiModelProperty("话题名称")
    private String topicName;
    //发布时间
    @ApiModelProperty("发布时间")
    private LocalDateTime releaseTime;
    //位置
    @ApiModelProperty("位置")
    private String location;
    //是否点赞
    @ApiModelProperty("是否点赞")
    private boolean like;
    //点赞数
    @ApiModelProperty("点赞数")
    private Long likeNum;
    //是否收藏
    @ApiModelProperty("收藏")
    private boolean collect;
    //附件数组
    @ApiModelProperty("附件数组")
    private String[] urls;
}
