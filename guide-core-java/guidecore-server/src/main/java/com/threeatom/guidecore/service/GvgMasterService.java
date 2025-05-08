package com.threeatom.guidecore.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.dto.request.SearchDto;
import com.threeatom.guidecore.entity.Course;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;

public interface GvgMasterService extends IService<GcMaster> {

    Message portalInfosUnlogin(
        JSONObject requestParams,
        GcUser user, HttpServletRequest request);

    Message newPtIndexHome(
        JSONObject requestParams, HttpServletRequest request, SysSystem system, PortalUser portalUser);

    Message search(SearchDto searchDto, HttpServletRequest request, GcUser user, SysSystem system);

    Message navigation(
        Map<String, Object> params,
        HttpServletRequest request,
        SysSystem system,
        PortalUser portalUser,
        Integer envFlag);

    Message getVideosBySubject(
        Integer subjectId,
        @RequestBody Map<String, Object> param,
        HttpServletRequest request,
        SysSystem system);

    Message videoDetail(HttpServletRequest request, Integer videoId, PortalUser portalUser);

    Message eventAnswerList(JSONObject jsonRequest, HttpServletRequest request, GcUser user);

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

}
