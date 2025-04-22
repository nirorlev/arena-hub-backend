package com.threeatom.guidecore.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.controller.user.vo.MessageFIlterVo;
import com.threeatom.guidecore.entity.GcUserEventResource;
import com.threeatom.system.entity.SysSystem;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface GcUserEventResourceService extends IService<GcUserEventResource> {

    boolean saveEventAction(GcUserEventResource UserEventResource);

    List<GcUserEventResource> getEventResourceByEventIdAndUserId(Integer eventId, Integer userId);

    List<GcUserEventResource> selectGetEventResourceByEventIdAndTargetUserId(
            Integer eventId, Integer userId, Integer commentResourceFileId);

    List<Map<String, Object>> getUsersUploadResNumByUserIdsAndSubIds(
            List<Integer> subIds, List<Integer> userIds, String order);

    List<Map<String, Object>> getUserListForRespond(
            List<Integer> userIds, Integer userId, Integer masterId);

    List<Map<String, Object>> getALLResourceListByEventIds(
            List<Integer> eventId, Integer studentId, Integer teacherId);

    GcUserEventResource uploadEventResourceFile(JSONObject jsonObject);

    List<Map<String, Object>> getAnswerMessageListByGcMasterMessageTargetUserId(
            MessageFIlterVo messageFIlterVo, SysSystem sys, HttpServletRequest request);

    List<GcUserEventResource> getEventResListForWorkBook(
            Integer eventId,
            Integer userId,
            Integer studentId,
            Integer masterId,
            HttpServletRequest request,
            Integer envType);

    Integer countALLResourceListByGcMasterMessageTargetUserId(MessageFIlterVo messageFIlterVo);

    Integer countAnswerMessageListByGcMasterMessageTargetUserId(MessageFIlterVo messageFIlterVo);

    List<GcUserEventResource> selectUnCheckedTeacherMessage(
            Integer userId, List<Integer> eventIds, Integer masterId);

    List<GcUserEventResource> selectUnCheckedStudentMessage(
            Integer studentId, List<Integer> eventIds, Integer userId, Integer masterId);

    Integer deleteResourceFIle(Integer commentResourceFileId, Integer userId, Integer masterId);
}
