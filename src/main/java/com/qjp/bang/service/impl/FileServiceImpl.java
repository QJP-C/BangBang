package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.mapper.FileMapper;
import com.qjp.bang.entity.File;
import com.qjp.bang.service.FileService;
import org.springframework.stereotype.Service;

/**
 * (File)表服务实现类
 *
 * @author makejava
 * @since 2023-04-17 20:04:28
 */
@Service("fileService")
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

}

