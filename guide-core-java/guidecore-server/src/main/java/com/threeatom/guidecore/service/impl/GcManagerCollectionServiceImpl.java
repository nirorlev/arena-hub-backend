package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcManagerCollection;
import com.threeatom.guidecore.mapper.GcManagerCollectionMapper;
import com.threeatom.guidecore.service.GcManagerCollectionService;
import org.springframework.stereotype.Service;

@Service
public class GcManagerCollectionServiceImpl
        extends ServiceImpl<GcManagerCollectionMapper, GcManagerCollection>
        implements GcManagerCollectionService {}
