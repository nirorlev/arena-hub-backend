package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUserMessage;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface GcUserMessageMapper extends BaseMapper<GcUserMessage> {

    List<GcUserMessage> selectGetMessageListByTargetUserIdAndUserId(
            @Param("targetUserId") Integer targetUserId,
            @Param("userId") Integer userId,
            @Param("offset") Integer offset,
            @Param("page") Integer page,
            @Param("masterId") Integer masterId);

    Boolean setIsReadByTargetUserId(
            @Param("targetUserId") Integer targetUserId, @Param("userId") Integer userId);

    GcUserMessage selectGetNewMessage(
            @Param("targetUserId") Integer targetUserId,
            @Param("userId") Integer userId,
            @Param("masterId") Integer masterId);

    List<GcUserMessage> selectGetLastMessageByUserIds(
            @Param("userIds") List<Integer> userIds, @Param("targetUserId") Integer targetUserId);

    List<Map<String, Object>> selectUsersMessageByTeacherIdAndUserIds(
            @Param("teacherId") Integer teacherId,
            @Param("userIds") List<Integer> userIds,
            @Param("order") String order);

    Integer countUnReadUserMessage(
            @Param("targetUserId") Integer targetUserId,
            @Param("masterId") Integer masterId,
            @Param("groupId") Integer groupId);

    List<GcUserMessage> selectRemoteMessages(
            @Param("targetUserId") Integer targetUserId,
            @Param("targetManagerId") Integer targetManagerId,
            @Param("messageType") Integer type);
}
