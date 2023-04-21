package com.qjp.bang.dto;

import com.qjp.bang.entity.Post;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务详情返回值")
public class PostDetDto extends Post {
    @ApiModelProperty("话题名")
    private String TopicName;
    @ApiModelProperty("浏览量")
    private Long browse;
    @ApiModelProperty("点赞量")
    private Long likeNum;
    @ApiModelProperty("是否收藏")
    private boolean isCollect;
    @ApiModelProperty("是否点赞")
    private boolean isLike;
    @ApiModelProperty("附件数组")
    private String[] urls;
}
