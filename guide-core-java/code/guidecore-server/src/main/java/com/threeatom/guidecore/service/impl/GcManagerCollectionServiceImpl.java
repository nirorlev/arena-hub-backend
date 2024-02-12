package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcManagerCollection;
import com.threeatom.guidecore.mapper.GcManagerCollectionMapper;
import com.threeatom.guidecore.service.GcManagerCollectionService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-11
 */
@Service
public class GcManagerCollectionServiceImpl
        extends ServiceImpl<GcManagerCollectionMapper, GcManagerCollection>
        implements GcManagerCollectionService {}
