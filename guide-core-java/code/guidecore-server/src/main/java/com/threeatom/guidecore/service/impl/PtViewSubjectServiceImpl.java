package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.PtViewSubject;
import com.threeatom.guidecore.mapper.PtViewSubjectMapper;
import com.threeatom.guidecore.service.PtViewSubjectService;
import org.springframework.stereotype.Service;

@Service
public class PtViewSubjectServiceImpl extends ServiceImpl<PtViewSubjectMapper, PtViewSubject>
        implements PtViewSubjectService {}
