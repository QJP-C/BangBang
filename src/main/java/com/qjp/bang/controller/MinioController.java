package com.qjp.bang.controller;

import com.qjp.bang.utils.MinioUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author qjp
 */
@Api(tags = "文件上传")
@Slf4j
@RequestMapping("/mo")
@CrossOrigin
@RestController
public class MinioController {
    @Autowired
    private MinioUtils minioUtils;
    @Value("${minio.bucketName}")
    private String bucketName;
    //    @Value("${minio.endpoint}")
    //    private String address;
    private final String address = "http://114.116.95.152:9000";
    /**
     * 上传
     * @param file
     * @return
     */
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public String upload(MultipartFile file) {
        List<String> upload = minioUtils.upload(new MultipartFile[]{file},"photo/");
        String path = address +"/"+bucketName+"/"+upload.get(0);
        log.info("head:[{}]",path);
        return path;
    }

}

