package com.threeatom.guidecore.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.*;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.utils.data.TreeNode;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface GcSubjectService extends IService<GcSubject> {

    boolean saveSub(GcSubject sub);

    TreeNode<GcSubject> getTreeNode(Integer masterId);

    List<GcSubject> getSubList(Integer masterId, Integer subType);

    List<GcSubject> selectAllTopicList(
            Integer masterId, Integer subType, Integer userId, GcSubject gcSubject);

    List<GcSubject> selectAllSub0ListByUserId(Integer masterId, Integer subType, Integer userId);

    List<GcSubject> getSubList0(Integer masterId);

    List<GcSubject> getSubListTop(Integer masterId);

    int getSubTopicNum(Integer masterId, List<Integer> subIds, Integer managerId);

    int getSubjectNum(Integer masterId, List<Integer> subIds, Integer managerId);

    List<Integer> getSubjectIds(Integer masterId);

    List<Integer> getPublicSubjectIds(Integer masterId);

    List<Integer> getSubjectChildIds(Integer subId);

    GcSubject getSubNameBysubId(Integer subId);

    List<GcSubject> getSubjectUserInfo(
            List<Integer> userIdList, List<Integer> subList, Integer masterId);

    List<GcSubject> getChildSubjectBySubId(Integer subId);

    GcSubject getSubByVid(Integer vid);

    List<GcSubject> getSubListByIds(List<Integer> subIds, HttpServletRequest request);

    boolean changeSubOrder(List<Integer> subIds, Integer masterId);

    // 给eventlist添加未读的标记
    JSONArray addUnReadTag(
            List<GcEvent> eventList, Integer teacherId, Integer studentId, Integer masterId);

    List<GcSubject> getSubListWithImg(Integer id, SysSystem sys, HttpServletRequest request);

    List<GcSubject> listSubByIds(List<Integer> subIds);

    List<GcSubject> listSubByIdsAndName(List<Integer> subIds, String name);

    List<GcSubject> getLevel0SubListWithImg(
            Integer masterId,
            SysSystem sys,
            HttpServletRequest request,
            PageParam pageParam,
            List<Integer> channelIds);

    List<GcSubject> getLevel0SubLis(Integer masterId);

    List<Integer> getCourseIds(Integer masterId);

    List<GcSubject> getSubjectChild(Integer subId);

    List<GcSubject> getSubListWithImgByIds(
            List<Integer> subIds, SysSystem sys, HttpServletRequest request, Integer masterId);

    List<GcSubject> getSubListWithHidden(Integer masterId);

    boolean deleteSub(Integer subId, Integer masterId);

    GcSubject getSubByToken(String token);

    List<GcSubject> listSubWithAssoByIds(Integer masterId, List<Integer> subIds);

    List<GcSubject> setSubListImg(List<GcSubject> list, SysSystem sys, HttpServletRequest request);

    List<GcSubject> selectSubjectAssociation(
            Integer masterId, List<Integer> subIds, boolean ifLevel0);

    Map<String, Object> selectEventResNumMapForWorkbook(Integer subId, Integer userId);

    Map<String, Object> selectEventResNumMapForWorkbookTeacher(
            Integer subId, Integer studentId, Integer teacherId);

    Map<String, Object> getAnswerMessageMapForTeacherWorkbook(
            Integer subId, Integer studentId, Integer teacherId);

    List<GcSubject> getSubVideoEventList(
            Integer subId, Integer studentId, Integer masterId, HttpServletRequest request);

    List<GcSubject> getLevel1VideoEventList(Integer subId, Integer userId, Integer teacherId);

    Integer countCourseForName(GcSubject subject);

    List<GcSubject> selecUnitNumForVideo(Integer videoId);

    List<Integer> countSessions(List<Integer> id);

    List<GcSubject> selectAllSubByUserId(
            Integer masterId, Integer userId, HttpServletRequest request);

    List<GcSubject> selectTwoSubjectsByFids(List<Integer> fids);

    List<GcSubject> selectAllLevel1SubList(List<Integer> subIds, String order, Integer masterId);

    GcSubject saveSubInfo(
            GcSubject sub, GcManager manager, GcMaster master, GcUser user, HttpServletRequest request);

    Integer selectSubjectPt(
            String subjectName,
            Integer type,
            Integer state,
            Integer createUser,
            Integer masterId,
            Integer userId,
            List<Integer> channelIds,
            List<Integer> publicSubjectIds);

    Integer getCreateUserPublished(Integer userId, Integer masterId, Integer state);

    List<GcSubject> getAvailableCourses(
            String name, Integer masterId, List<Integer> subIds, Integer userId, String order);

    List<GcSubject> newGetAvailableCourses(
            String name, Integer masterId, List<Integer> subIds, Integer userId, String order);

    List<GcSubject> getCompletedTwoCourse(Map<String, Object> paramMap);

    List<GcSubject> selectSubjectByNewIndexHome(
            Integer masterId, Integer userId, PageParam pageParam);

    List<GcSubject> selectSubjectMay(Integer masterId, Integer userId, PageParam pageParam);

    List<GcSubject> selectActiveSubject(
            Integer userId,
            Integer masterId,
            Integer subjectState,
            String name,
            HttpServletRequest request);

    List<GcSubject> selectCompletedSubject(
            Integer userId,
            Integer masterId,
            Integer subjectState,
            String name,
            HttpServletRequest request);

    List<GcSubject> selectDiscoverSubject(
            Integer userId,
            Integer masterId,
            Integer subjectState,
            String name,
            HttpServletRequest request);

    List<GcSubject> selectDraftsSubject(
            Integer userId, Integer masterId, String name, HttpServletRequest request);

    List<GcSubject> selectFromMyTeamSubject(
            Integer userId, Integer masterId, String name, HttpServletRequest request);

    List<GcSubject> selectCompanyResourcesSubject(
            Integer userId, Integer masterId, String name, HttpServletRequest request);

    List<GcSubject> selectAllCourseSubject(
            Integer userId, Integer masterId, String name, HttpServletRequest request, Integer orderType);

    List<GcSubject> selectPublishedSubject(
            Integer userId, Integer masterId, String name, HttpServletRequest request);

    List<GcSubject> selectCreatedByTeams(
            Integer userId,
            Integer masterId,
            String name,
            HttpServletRequest request,
            List<String> groupCodeList,
            Integer orderType);

    List<GcSubject> selectCreateByTeamsOrgAdmin(
            Integer userId,
            Integer masterId,
            String name,
            HttpServletRequest request,
            List<String> groupCodeList,
            Integer orderType);

    List<GcSubject> getSubjectInfoByList(
            List<GcSubject> level0sublist, Integer masterId, Integer userId, HttpServletRequest request);

    Integer inProgressNum(Integer userId, Integer masterId);

    Integer getSubjectNameIndex(Integer masterId, String nameIndex);

    Integer getNewMyAssignmentNew(Integer masterId, Integer userId);

    void initJit();

    List<Integer> getUserCreateSubject(Integer masterId, Integer userId);

    List<Integer> getUserCreateSubjectAdmin(Integer masterId, Integer userId);

    List<Integer> getUserPublicSubject(Integer masterId, Integer userId);
}
