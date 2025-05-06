package com.threeatom.guidecore.service;

import com.github.pagehelper.PageInfo;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.system.entity.SysSystem;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface NewUiGcSubjectService {

    PageInfo<GcSubject> list(
            Map<String, Object> params, SysSystem system, HttpServletRequest request, Integer envFlag);

    Map<Integer, GcSubject> sumSubjectDuration(List<Integer> subjectIds);

    PageInfo<GcSubject> listSubjectByFid(
            Map<String, Object> params,
            HttpServletRequest request,
            boolean ifLogin,
            List<Integer> subIds);

    List<GcSubject> selectBuildSubject(Map<String, Object> params, HttpServletRequest request);

    List<GcSubject> selectTwoSubjectByIds(List<Integer> subjectIds, HttpServletRequest request);

    Integer selectLastVideoId(Integer subId, Integer userId, Integer masterId);

    Map<Integer, GcUserVideoAction> getStarActions(List<Integer> subjectIds);

    List<GcSubject> buildSubject1(Map<Integer, List<GcVideo>> sub1Map);

    List<GcSubject> buildSubject2(
            List<GcSubject> subjects,
            Integer userId,
            SysSystem system,
            HttpServletRequest request,
            Integer code);

    Integer getSubjectNum(List<Integer> subIds);
}
