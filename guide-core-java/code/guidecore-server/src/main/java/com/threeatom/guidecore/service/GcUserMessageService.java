package com.threeatom.guidecore.service;

import com.threeatom.guidecore.entity.GcUserMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-12-09
 */
public interface GcUserMessageService extends IService<GcUserMessage> {

    boolean saveUserMessage(GcUserMessage userMessage);

    List<GcUserMessage> getUnReadMessage(Integer masterId, Integer userId);

    List<GcUserMessage> getMessageListByUserIds(List<Integer> userIds, Integer masterId);

    List<GcUserMessage> getMessageListByTargetUserIdAndUserId(Integer targetUserId, Integer userId, Integer offset, Integer page,Integer masterId,HttpServletRequest request);

    Boolean setIsReadByTargetUserId(Integer targetUserId, Integer userId);

    GcUserMessage getNewMessage(Integer targetUserId, Integer userId,Integer masterId);
    
    List<Map<String,Object>> getMessageNumByTeacherIdAndUserIds(Integer teacherId,List<Integer> userIds,String order);

    int update(GcUserMessage userMessage);
}
