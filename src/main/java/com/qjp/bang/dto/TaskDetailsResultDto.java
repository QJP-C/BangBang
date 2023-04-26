package com.qjp.bang.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("任务详情参数")
public class TaskDetailsResultDto {

    //发布人id
    @ApiModelProperty("发布人id")
    private String fromId;
    //发布人头像
    @ApiModelProperty("发布人头像")
    private String fromHead;
    //发布人昵称
    @ApiModelProperty("发布人昵称")
    private String fromName;
    //接单人id
    @ApiModelProperty("接单人id")
    private String toId;
    //接单人名称
    @ApiModelProperty("接单人名称")
    private String toName;
    //接单人头像
    @ApiModelProperty("接单人头像")
    private String toHead;
    //任务标题
    @ApiModelProperty("任务标题")
    private String title;
    //任务详情
    @ApiModelProperty("任务详情")
    private String details;
    //是否加急  1:加急  0:不急
    @ApiModelProperty("是否加急  1:加急  0:不急")
    private Integer urgent;
    //是否收藏   1:已收藏  2:未收藏
    @ApiModelProperty("是否收藏   1:已收藏  2:未收藏")
    private Integer isCollect;
    //任务状态   1:已发布  2:已接取  3:已完成   0:已逾期
    @ApiModelProperty("任务状态   1:已发布  2:已接取  3:已完成   0:已逾期")
    private Integer state;
    //类型名称
    @ApiModelProperty("类型名称")
    private String typeId;
    //赏金
    @ApiModelProperty("赏金")
    private Double money;
    //地址
    @ApiModelProperty("地址")
    private String location;
    //发布时间
    @ApiModelProperty("发布时间")
    private LocalDateTime releaseTime;
    //截止时间
    @ApiModelProperty("截止时间")
    private LocalDateTime limitTime;
    //任务附件
    @ApiModelProperty("任务附件")
    private String[] fromUrls;
    //提交附件
    @ApiModelProperty("提交附件")
    private String[] toUrls;
}
