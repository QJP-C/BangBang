package com.qjp.bang.dto;

import com.qjp.bang.entity.Post;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务详情返回值")
public class PostDetDto extends Post {
    @ApiModelProperty("发帖用户头像")
    private String head;
    @ApiModelProperty("话题名")
    private String TopicName;
    @ApiModelProperty("浏览量")
    private Long browse;
    @ApiModelProperty("点赞量")
    private Long likeNum;
    @ApiModelProperty("是否关注发帖人")
    private boolean followUser;
    @ApiModelProperty("是否收藏")
    private boolean collect;
    @ApiModelProperty("是否点赞")
    private boolean like;
    @ApiModelProperty("附件数组")
    private String[] urls;

}
