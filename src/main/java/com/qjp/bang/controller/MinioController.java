package com.qjp.bang.controller;

import com.qjp.bang.common.R;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public R<Map<String, String>> upload(MultipartFile file) {
        List<String> upload = minioUtils.upload(new MultipartFile[]{file},"photo/");
        String path = address +"/"+bucketName+"/"+upload.get(0);
        log.info("head:[{}]",path);
        HashMap<String, String> map = new HashMap<>();
        map.put("url",path);
        return R.success(map);
    }


    @ApiOperation("批量文件上传")
    @PostMapping("/uploads")
    public R<List<String>> uploads(MultipartFile[] files){
//            try {
                List<String> list = new ArrayList<>();
                for (MultipartFile file:files){
                    List<String> upload = minioUtils.upload(new MultipartFile[]{file},"photo/");
                    String path = address +"/"+bucketName+"/"+upload.get(0);
                    list.add(path);
                }
                return R.success(list);
            }
//            catch (Exception e){
//                log.error("上传异常：{}",e.getMessage());
//                return R.error("上传异常！{}");
//            }
//        }
}

