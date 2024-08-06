package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserMessage;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface GcUserMessageService extends IService<GcUserMessage> {

    boolean saveUserMessage(GcUserMessage userMessage);

    List<GcUserMessage> getUnReadMessage(Integer masterId, Integer userId);

    List<GcUserMessage> getMessageListByUserIds(List<Integer> userIds, Integer masterId);

    List<GcUserMessage> getMessageListByTargetUserIdAndUserId(
            Integer targetUserId,
            Integer userId,
            Integer offset,
            Integer page,
            Integer masterId,
            HttpServletRequest request);

    Boolean setIsReadByTargetUserId(Integer targetUserId, Integer userId);

    GcUserMessage getNewMessage(Integer targetUserId, Integer userId, Integer masterId);

    List<Map<String, Object>> getMessageNumByTeacherIdAndUserIds(
            Integer teacherId, List<Integer> userIds, String order);

    int update(GcUserMessage userMessage);
}
