package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserMessage;
import java.util.List;

/**
 * @author Administrator
 * @title: SysFileCaptionMapper
 * @projectName guidecore
 * @description: TODO
 * @date 2021/11/8/00810:05
 */
public interface NewUIUserMapper extends BaseMapper<GcUser> {

    //    Integer countNewMessagesDetail( Integer uid,Integer portalId);

    List<GcUserMessage> countNewMessagesDetail(
            Integer uid, Integer portalId, Integer teacherAccessId);
}
