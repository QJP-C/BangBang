package com.qjp.bang.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * (UserFollow)表实体类
 *
 * @author makejava
 * @since 2023-04-15 20:58:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("用户关注实体类")
public class UserFollow {

    private String id;
    //用户id
    private String userId;
    //关注的用户id
    private String followId;

    private LocalDateTime createTime;


}

