package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.controller.user.vo.MessageFIlterVo;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.GcMasterMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GcMasterMessageMapper extends BaseMapper<GcMasterMessage> {

    List<Map<String, Object>> selectGetUnReadMessageByUserIds(@Param("userIds") List<Integer> userIds, @Param("userId") Integer userId, @Param("masterId") Integer masterId,@Param("type") Integer type);

    List<Integer> selectGetMasterMessageResIdsById(@Param("studentId") Integer studentId, @Param("userId") Integer userId, @Param("masterId") Integer masterId);

    boolean selectSetIsRead(@Param("studentId") Integer studentId, @Param("userId") Integer userId, @Param("masterId") Integer masterId,@Param("type") Integer type);

    boolean updateMessageRead(@Param("masterMessage")GcMasterMessage masterMessage);
    
    List<Map<String ,Object>> getUnReadMessageCountByResourceIds(@Param("ResourceIds") List<Integer> ResourceIds,@Param("userId") Integer userId,@Param("masterId") Integer masterId,@Param("teacherId")  Integer teacherId);

    List<Map<String ,Object>> getAllReadMessageCountByResourceIds(@Param("ResourceIds") List<Integer> ResourceIds,@Param("userId") Integer userId,@Param("masterId") Integer masterId,@Param("teacherId")  Integer teacherId);

    List<Map<String,Object>> getAllUnReadMessage(@Param("masterId") Integer masterId,@Param("userId") Integer userId);

    List<GcMasterMessage> selectGetLastMessageByStudentIds(@Param("studentIds") List<Integer> studentIds,@Param("teacherId") Integer teacherId,@Param("masterId") Integer masterId);

    List<Map<String, Object>> selectGetMasterMessageListDetailByMessageIds(@Param("messageIds") List<Integer> messageIds);

    List<Map<String,Object>> selectGetAllEventUnMessageDetail(@Param("masterId") Integer masterId,@Param("userId") Integer userId,@Param("targetUserId") Integer targetUserId);

    List<Map<String,Object>> selectGetAllSubUnMessageDetail(@Param("masterId") Integer masterId,@Param("userId") Integer userId,@Param("targetUserId") Integer targetUserId);

    boolean selectSetAnswerIsRead(@Param("eventIds") List<Integer> eventIds,@Param("masterId") Integer masterId,@Param("userId") Integer userId,@Param("targetUserId") Integer targetUserId);

    boolean deleteMessageForSchedule(@Param("userIdsList") List<Integer> userIdsList,@Param("masterMessage")GcMasterMessage masterMessage);
    
    int deleteMessageCommon(@Param("masterMessage")GcMasterMessage masterMessage);
    
    
    boolean selectSetResourceIsRead(@Param("messageIds") List<Integer> messageIds,@Param("readMarker") Integer readMarker);

    List<Map<String,Object>> selectGetEventAnswerUnReadList(@Param("eventIds") List<Integer> eventIds,@Param("studentId") Integer studentId,@Param("masterId") Integer masterId,@Param("teacherId") Integer teacherId);

    List<Map<String,Object>> getVideoCommentMessageListByTargetUserId(@Param("message") MessageFIlterVo messageFIlterVo, @Param("pageParam") PageParam pageParam);

    List<Map<String,Object>> getNoteCommentMessageListByTargetUserId(@Param("message") MessageFIlterVo messageFIlterVo,@Param("pageParam") PageParam pageParam);

    Integer countVideoMessage(MessageFIlterVo messageFIlterVo);

    Integer countNoteCommentMessage(MessageFIlterVo messageFIlterVo);

    List<Map<String,Object>> selectScheduleNotifications(@Param("userId")Integer userId,@Param("readState")Integer readState);
}
