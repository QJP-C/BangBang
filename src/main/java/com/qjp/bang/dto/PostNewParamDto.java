package com.qjp.bang.dto;

import lombok.Data;

@Data
public class PostNewParamDto {
    //文本
    private String text;
    //话题id
    private String topicId;
    //位置
    private String location;
    //附件
    private String[] urls;
}
