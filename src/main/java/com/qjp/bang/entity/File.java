package com.qjp.bang.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * (File)表实体类
 *
 * @author makejava
 * @since 2023-04-17 20:04:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("文件类")
public class File {

    private String id;
    //属于  1任务  2帖子
    private Integer belong;
    //关联的任务或帖子的id
    private String aboutId;
    //路径
    private String url;
    //创建时间
    private LocalDateTime createTime;

}

