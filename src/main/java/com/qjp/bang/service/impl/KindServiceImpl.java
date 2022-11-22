package com.qjp.bang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjp.bang.mapper.KindMapper;
import com.qjp.bang.pojo.Kind;
import com.qjp.bang.service.KindService;
import org.springframework.stereotype.Service;

/**
 * @author qjp
 */
@Service
public class KindServiceImpl extends ServiceImpl<KindMapper, Kind> implements KindService {
}
