package com.threeatom.guidecore.service;

import com.github.pagehelper.PageInfo;
import com.threeatom.guidecore.entity.Course;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.system.entity.SysSystem;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface NewUiGcSubjectService {

    PageInfo<Course> list(
            Map<String, Object> params, SysSystem system, HttpServletRequest request, Integer envFlag);

    Map<Integer, Course> sumSubjectDuration(List<Integer> subjectIds);

    PageInfo<Course> listSubjectByFid(
            Map<String, Object> params,
            HttpServletRequest request,
            boolean ifLogin,
            List<Integer> subIds);

    List<Course> selectBuildSubject(Map<String, Object> params, HttpServletRequest request);

    List<Course> selectTwoSubjectByIds(List<Integer> subjectIds, HttpServletRequest request);

    Map<Integer, GcUserVideoAction> getStarActions(List<Integer> subjectIds);

    List<Course> buildSubject1(Map<Integer, List<GcVideo>> sub1Map);

    List<Course> buildSubject2(
            List<Course> subjects,
            Integer userId,
            SysSystem system,
            HttpServletRequest request,
            Integer code);

    Integer getSubjectNum(List<Integer> subIds);
}
