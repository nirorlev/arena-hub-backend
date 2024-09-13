package com.threeatom.guidecore.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.dto.request.SearchDto;
import com.threeatom.guidecore.entity.*;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;

public interface GvgMasterService extends IService<GcMaster> {

    // 复用 gc的接口

    Message portalInfosUnlogin(
        JSONObject requestParams,
        GcUser user, HttpServletRequest request);

    Message newPtIndexHome(
            JSONObject requestParams, HttpServletRequest request, SysSystem system, PortalUser portalUser);

    Message search(
        SearchDto searchDto, HttpServletRequest request, GcUser user, SysSystem system);

    Message index(
            Map<String, Object> params,
            HttpServletRequest request,
            SysSystem system,
            GcUser gcUser,
            Integer envFLag);

    Message navigation(
            Map<String, Object> params,
            HttpServletRequest request,
            SysSystem system,
            GcUser user,
            Integer envFlag);

    Message getVideosBySubject(
            Integer subjectId,
            @RequestBody Map<String, Object> param,
            HttpServletRequest request,
            SysSystem system);

    SubjectTotals calcTotals(
            List<GcSubject> subjects,
            Integer userId,
            boolean ifStudent,
            Integer masterId,
            Integer envFlag);

    Message videoDetail(
            HttpServletRequest request, Integer videoId, GcUser user, SysSystem system, Integer envFlag);

    Message eventAnswerList(JSONObject jsonRequest, HttpServletRequest request, GcUser user);

    Message createVideoPlayRecordAndNode(
            GcUserVideoPlay userVideoPlay,
            HttpServletRequest request,
            Integer envFlag,
            GcUser gcUser,
            Integer masterId,
            SysSystem system);

    Message answerQuestion(
            JSONObject jsonObject,
            HttpServletRequest request,
            Integer masterId,
            GcUser gcUser,
            Integer envFlag,
            SysSystem system);

    PageInfo<SysFile> getSysFile(
            JSONObject jsonParams,
            List<Integer> typeIndexIds,
            HttpServletRequest request,
            GcMaster master,
            Integer uploadUid);

    Message deleteVideo(Integer vid, Integer envFlag, Integer userId, Integer masterId);

    Message deleteVideoPt(
            Integer vid, Integer envFlag, Integer userId, Integer masterId, HttpServletRequest request);

    Message deleteSub(Integer subId, Integer envFlag, GcMaster master, Integer userId);

    void saveInProgress(Integer subject, Integer masterId, HttpServletRequest request);
}
