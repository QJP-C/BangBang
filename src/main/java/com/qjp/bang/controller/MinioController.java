package com.qjp.bang.controller;
import com.qjp.bang.common.R;
import com.qjp.bang.utils.MinioUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
/**
 * @author qjp
 */
@Slf4j
@RequestMapping("/mo")
@CrossOrigin
@Transactional
@RestController
public class MinioController {
    @Autowired
    private MinioUtils minioUtils;
    @Value("${minio.endpoint}")
    private String address;
    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * 上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {

        List<String> upload = minioUtils.upload(new MultipartFile[]{file});
        String head = "http://114.116.95.152:9000"+"/"+bucketName+"/"+upload.get(0);
        log.info("head:[{}]",head);
        return R.success(head);
    }

    /**
     * 下载
     * @param name
     * @return
     */
    @GetMapping("/download")
    public R<ResponseEntity<byte[]>> download( String name ){
        ResponseEntity<byte[]> download = minioUtils.download(name);

        return R.success(download);
    }
}

