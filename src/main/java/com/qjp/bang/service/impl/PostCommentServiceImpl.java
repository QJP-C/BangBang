package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.mapper.PostCommentMapper;
import com.qjp.bang.entity.PostComment;
import com.qjp.bang.service.PostCommentService;
import org.springframework.stereotype.Service;

/**
 * (PostComment)表服务实现类
 *
 * @author makejava
 * @since 2023-04-25 21:49:23
 */
@Service("postCommentService")
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment> implements PostCommentService {

}

