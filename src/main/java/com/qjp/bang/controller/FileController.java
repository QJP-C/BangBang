package com.qjp.bang.controller;


import com.qjp.bang.service.FileService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * (File)表控制层
 *
 * @author makejava
 * @since 2023-04-17 20:04:27
 */
@Api(tags = "文件", value = "文件")
@RestController
@CrossOrigin
@RequestMapping("file")
public class FileController{
    /**
     * 服务对象
     */
    @Resource
    private FileService fileService;

}

