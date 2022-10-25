package com.qjp.xjbx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.xjbx.mapper.KindMapper;
import com.qjp.xjbx.pojo.Kind;
import com.qjp.xjbx.service.KindService;
import org.springframework.stereotype.Service;

@Service
public class KindServiceImpl extends ServiceImpl<KindMapper, Kind> implements KindService {
}
