package com.qjp.xjbx.controller;
import com.qjp.xjbx.common.R;
import com.qjp.xjbx.utils.FileUtil;
import com.qjp.xjbx.utils.MinioUtils;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
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
//
//        byte[] body = download.getBody();
//        FileUtil.byte2File(body,)
//        FileOutputStream outputStream = null;
////        try {
////            outputStream = new FileOutputStream(new File());
////            outputStream.write(body);
////            outputStream.close();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
        return R.success(download);
    }
//    /**
//     * 文件上传
//     * @param file
//     * @return
//     */
//    @PostMapping("/upload")
//    public String upload1(MultipartFile file){
//
//        try {
//            PutObjectArgs objectArgs = PutObjectArgs.builder().object(file.getOriginalFilename())
//                    .bucket(bucketName)
//                    .contentType(file.getContentType())
//                    .stream(file.getInputStream(),file.getSize(),-1).build();
//
//            minioClient.putObject(objectArgs);
//            return "ok";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return e.getMessage();
//        }
//    }
//    /**
//     * 下载文件
//     * @param filename
//     */
//    @GetMapping("/download/{filename}")
//    public void download(@PathVariable String filename, HttpServletResponse res){
//
//        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName)
//                .object(filename).build();
//
//        try (GetObjectResponse response = minioClient.getObject(objectArgs)){
//            byte[] buf = new byte[1024];
//
//            int len;
//
//            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()){
//
//                while ((len=response.read(buf))!=-1){
//
//                    os.write(buf,0,len);
//
//                }
//                os.flush();
//
//                byte[] bytes = os.toByteArray();
//
//                res.setCharacterEncoding("utf-8");
//                res.setContentType("application/force-download");// 设置强制下载不打开
//                res.addHeader("Content-Disposition", "attachment;fileName=" + filename);
//                try ( ServletOutputStream stream = res.getOutputStream()){
//                    stream.write(bytes);
//                    stream.flush();
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}

