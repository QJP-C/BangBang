package com.qjp.bang.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("帖子新增参数")
public class PostNewParamDto {
    //文本
    @ApiModelProperty("文本")
    private String text;
    //话题id
    @ApiModelProperty("话题id")
    private String topicId;
    //位置
    @ApiModelProperty("位置")
    private String location;
    //附件
    @ApiModelProperty("附件")
    private String[] urls;
    //是否有视频  1有视频 0无视频
    @ApiModelProperty("是否有视频  1有视频 0无视频")
    private int isVideo;
}
