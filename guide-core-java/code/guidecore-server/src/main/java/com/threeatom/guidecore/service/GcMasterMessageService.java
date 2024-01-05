package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.controller.user.vo.MessageFIlterVo;
import com.threeatom.guidecore.entity.GcMasterMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface GcMasterMessageService extends IService<GcMasterMessage> {

    void saveMasterMessage(GcMasterMessage masterMessage);

    Integer getUnReadMessage(Integer masterId, Integer userId, Integer type);

    void saveBatchMasterMessage(List<GcMasterMessage> masterMessageList);

    List<Map<String, Object>> getUnReadMessageByUserIds(List<Integer> userIds, Integer userId, Integer masterId, Integer type);

    List<Integer> getMasterMessageResIdsById(Integer studentId, Integer userId, Integer masterId);

    boolean setIsRead(Integer studentId, Integer userId, Integer masterId, Integer type);

    List<Map<String, Object>> getUnReadMessageCountByResourceIds(List<Integer> ResourceIds, Integer userId, Integer masterId, Integer teacherId);

    List<Map<String, Object>> getAllReadMessageCountByResourceIds(List<Integer> ResourceIds, Integer userId, Integer masterId, Integer teacherId);

    List<Map<String, Object>> getAllUnReadMessage(Integer masterId, Integer userId);

    List<GcMasterMessage> getLastMessageByStudentIds(List<Integer> studentIds, Integer teacherId, Integer masterId);

    List<Map<String, Object>> getMasterMessageListDetailByMessageIds(List<Integer> messageIds);

    List<Map<String, Object>> getAllEventUnMessageDetail(Integer masterId, Integer userId, Integer targetUserId);

    List<Map<String, Object>> getAllSubUnMessageDetail(Integer masterId, Integer userId, Integer targetUserId);

    //设置video问题回答已读
    boolean setAnswerIsRead(List<Integer> eventIds, Integer masterId, Integer userId, Integer targetUserId);

    //设置资源问题回答已读
    boolean setUserResourceIsReadByMessageIds(List<Integer> messageIds, Integer readMarker);

    //老师获取学生问题答案的未读消息
    List<Map<String, Object>> getEventAnswerUnReadList(List<Integer> eventIds, Integer studentId, Integer masterId, Integer teacherId);

	boolean deleteAnswerMessage(GcMasterMessage masterMessage);

    List<Map<String,Object>> getNoteCommentMessageList(MessageFIlterVo messageFIlterVo, HttpServletRequest request);

}
