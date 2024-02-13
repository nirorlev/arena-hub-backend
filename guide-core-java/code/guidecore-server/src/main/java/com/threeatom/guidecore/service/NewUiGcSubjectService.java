package com.threeatom.guidecore.service;

/**
 * @author: rjunchao
 * @date: 2021-8-15 10:16:06
 * @desc:
 */
import com.github.pagehelper.PageInfo;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.system.entity.SysSystem;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface NewUiGcSubjectService {

    /**
     * 	分页查询课程信息
     * @param params
     * @param request
     * @return
     */
    PageInfo<GcSubject> list(
            Map<String, Object> params, SysSystem system, HttpServletRequest request, Integer envFlag);

    /**
     * 根据客户id集合查询课程时长，
     * @param subjectIds
     * @return 单位秒
     */
    Map<Integer, GcSubject> sumSubjectDuration(List<Integer> subjectIds);

    /**
     * 	根据一级课程id查询二级课程及视频信息
     * @param params
     * @param request
     * @return
     */
    PageInfo<GcSubject> listSubjectByFid(
            Map<String, Object> params,
            SysSystem sys,
            HttpServletRequest request,
            boolean ifLogin,
            List<Integer> subIds,
            Integer envFlag);

    List<GcSubject> selectBuildSubject(Map<String, Object> params, HttpServletRequest request);

    List<GcSubject> selectTwoSubjectByIds(List<Integer> subjectIds, HttpServletRequest request);

    Integer selectLastVideoId(Integer subId, Integer userId, Integer masterId);

    List<GcSubject> selectSubjects(List<Integer> subIds);

    List<String> selectAllTag(Integer masterId, Integer userId);

    List<String> selectSubjectTag(Integer masterId, Integer userId);

    Map<Integer, GcUserVideoAction> getStarActions(List<Integer> subjectIds);

    List<GcSubject> buildSubject1(Map<Integer, List<GcVideo>> sub1Map);

    List<GcSubject> getTagNameAndIds(Integer masterId, Integer userId, String tagText);

    List<GcSubject> buildSubject2(
            List<GcSubject> subjects,
            Integer userId,
            SysSystem system,
            HttpServletRequest request,
            Integer code);

    Integer getSubjectNum(List<Integer> subIds);
}
