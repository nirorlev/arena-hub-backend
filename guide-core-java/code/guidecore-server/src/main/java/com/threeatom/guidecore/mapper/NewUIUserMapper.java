package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserMessage;
import com.threeatom.system.entity.SysFileCaption;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @title: SysFileCaptionMapper
 * @projectName guidecore
 * @description: TODO
 * @date 2021/11/8/00810:05
 */
public interface NewUIUserMapper extends BaseMapper<GcUser> {



//    Integer countNewMessagesDetail( Integer uid,Integer portalId);

    List<GcUserMessage> countNewMessagesDetail(Integer uid,Integer portalId,Integer teacherAccessId);

}
