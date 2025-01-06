package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.UserManagedGroup;
import com.threeatom.guidecore.mapper.UserManagedGroupMapper;
import com.threeatom.guidecore.service.UserManagedGroupService;
import org.springframework.stereotype.Service;

@Service
public class UserManagedGroupServiceImpl extends ServiceImpl<UserManagedGroupMapper, UserManagedGroup>
    implements UserManagedGroupService {

}
