package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.entity.File;
import com.qjp.bang.mapper.FileMapper;
import com.qjp.bang.service.FileService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * (File)表服务实现类
 *
 * @author makejava
 * @since 2023-04-17 20:04:28
 */
@Service("fileService")
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
    /**
     * 添加帖子附件
     * @param url
     * @param postId
     * @return
     */
    @Override
    public boolean addPostFile(String url, String postId) {
        File file = new File();
        file.setUrl(url);
        file.setBelong(3);
        file.setAboutId(postId);
        file.setCreateTime(LocalDateTime.now());
        return this.save(file);
    }

    /**
     * 获取帖子附件
     * @param postId
     * @return
     */
    @Override
    public String[] getPostFiles(String postId) {
        LambdaQueryWrapper<File> qw = new LambdaQueryWrapper<>();
        qw.eq(File::getAboutId,postId).eq(File::getBelong,"3");
        List<File> list = this.list(qw);
        if (list.size() ==0) return new String[0];
        String[] urls = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            urls[i] = list.get(i).getUrl();
        }
        return urls;
    }

    /**
     * 添加完成任务附件
     * @param url
     * @param taskId
     * @return
     */
    @Override
    public boolean addTaskFile(String url, String taskId) {
        File file = new File();
        file.setUrl(url);
        file.setBelong(2);
        file.setAboutId(taskId);
        file.setCreateTime(LocalDateTime.now());
        return this.save(file);
    }
}

