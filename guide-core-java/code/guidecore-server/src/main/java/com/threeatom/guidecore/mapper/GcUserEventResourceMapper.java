package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.controller.user.vo.MessageFIlterVo;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.GcUserEventResource;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 事件资源文件 Mapper 接口
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
public interface GcUserEventResourceMapper extends BaseMapper<GcUserEventResource> {

    List<GcUserEventResource> selectGetFilesByUserId(Integer userId, List<Integer> eventIds);

    List<GcUserEventResource> selectGetEventResourceByEventIdAndUserId(
            Integer eventId, Integer userId);

    List<GcUserEventResource> selectGetEventResourceByEventIdAndTargetUserId(
            Integer eventId, Integer userId, Integer commentResourceFileId);

    /***
     * 获取上传资源文件数量
     * @param subIds
     * @param userIds
     * @return
     */
    List<Map<String, Object>> selectUsersUploadResNumByUserIdsAndSubIds(
            @Param("subIds") List<Integer> subIds,
            @Param("userIds") List<Integer> userIds,
            @Param("order") String order);

    List<Map<String, Object>> selectGetUserListForRespond(
            @Param("userIds") List<Integer> userIds,
            @Param("userId") Integer userId,
            @Param("masterId") Integer masterId);

    List<Map<String, Object>> selectGetListByUserId(@Param("userId") Integer userId);

    List<Map<String, Object>> selectGetVideoTalksByEventIds(
            @Param("eventIds") List<Integer> eventIds, @Param("studentId") Integer studentId);

    List<GcUserEventResource> selectTimeNodeEventResource(
            @Param("targetId") Integer targetId, @Param("userId") Integer userId);

    List<Map<String, Object>> getALLResourceListByEventIds(
            @Param("eventIds") List<Integer> eventIds,
            @Param("studentId") Integer studentId,
            @Param("teacherId") Integer teacherId);

    List<Map<String, Object>> getALLResourceListByGcMasterMessageTargetUserId(
            @Param("message") MessageFIlterVo messageFIlterVo,
            @Param("pageParam") PageParam pageParam,
            @Param("videoIds") List<Integer> videoIds);

    List<Map<String, Object>> getAnswerMessageListByGcMasterMessageTargetUserId(
            @Param("message") MessageFIlterVo messageFIlterVo, @Param("pageParam") PageParam pageParam);

    List<GcUserEventResource> getEventResListForWorkBook(
            @Param("eventId") Integer eventId,
            @Param("userId") Integer userId,
            @Param("studentId") Integer studentId,
            @Param("masterId") Integer masterId,
            @Param("envType") Integer envType);

    Integer countALLResourceListByGcMasterMessageTargetUserId(MessageFIlterVo messageFIlterVo);

    // key是学生的userid
    List<Map<String, Object>> countALLResourceListByGcMasterMessageTargetUserId_map(
            MessageFIlterVo messageFIlterVo);

    Integer countAnswerMessageListByGcMasterMessageTargetUserId(MessageFIlterVo messageFIlterVo);

    List<Map<String, Object>> countAnswerMessageListByGcMasterMessageTargetUserId_map(
            MessageFIlterVo messageFIlterVo);

    Integer countVideoResourceListByGcMasterMessageTargetUserId(MessageFIlterVo messageFIlterVo);

    List<GcUserEventResource> selectUnCheckedTeacherMessage(
            @Param("userId") Integer userId,
            @Param("eventIds") List<Integer> eventIds,
            @Param("masterId") Integer masterId);

    List<GcUserEventResource> selectUnCheckedStudentMessage(
            @Param("studentId") Integer studentId,
            @Param("eventIds") List<Integer> eventIds,
            @Param("userId") Integer userId,
            @Param("masterId") Integer masterId);
}
