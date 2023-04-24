package com.qjp.bang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjp.bang.entity.File;

/**
 * (File)表服务接口
 *
 * @author makejava
 * @since 2023-04-17 20:04:27
 */
public interface FileService extends IService<File> {
    /**
     * 添加帖子附件
     * @param url
     * @param postId
     * @return
     */
    boolean addPostFile(String url, String postId);

    /**
     * 获取帖子附件
     * @param postId
     * @return
     */
    String[] getPostFiles(String postId);
}

