package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUserEvent;
import com.threeatom.guidecore.entity.GcUserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface GcUserInfoMapper extends BaseMapper<GcUserInfo> {

}
