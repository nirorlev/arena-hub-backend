package com.threeatom.guidecore.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.dto.response.AssignedCourseDto;
import com.threeatom.guidecore.dto.response.CourseDto;
import com.threeatom.guidecore.dto.response.CourseListDto;
import com.threeatom.guidecore.dto.response.CourseProgramDto;
import com.threeatom.guidecore.dto.response.CourseVideoBookmarkDto;
import com.threeatom.guidecore.entity.Course;
import com.threeatom.guidecore.entity.GcEvent;
import com.threeatom.guidecore.entity.GcManager;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.utils.data.TreeNode;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface CourseService extends IService<Course> {

    boolean saveSub(Course sub);

    TreeNode<Course> getTreeNode(Integer masterId);

    List<Course> getSubList(Integer masterId, Integer subType);

    List<Course> selectAllTopicList(
        Integer masterId, Integer subType, Integer userId, Course course);

    List<Course> selectAllSub0ListByUserId(Integer masterId, Integer subType, Integer userId);

    List<Course> getSubList0(Integer masterId);

    List<Course> getSubListTop(Integer masterId);

    int getSubTopicNum(Integer masterId, List<Integer> subIds, Integer managerId);

    int getSubjectNum(Integer masterId, List<Integer> subIds, Integer managerId);

    List<Integer> getSubjectIds(Integer masterId);

    List<Integer> getPublicSubjectIds(Integer masterId);

    List<Integer> getSubjectChildIds(Integer subId);

    Course getSubNameBysubId(Integer subId);

    List<Course> getChildSubjectBySubId(Integer subId);

    Course getSubByVid(Integer vid);

    List<Course> getSubListByIds(List<Integer> subIds, HttpServletRequest request);

    boolean changeSubOrder(List<Integer> subIds, Integer masterId);

    // 给eventlist添加未读的标记
    JSONArray addUnReadTag(
        List<GcEvent> eventList, Integer teacherId, Integer studentId, Integer masterId);

    List<Course> getSubListWithImg(Integer id, SysSystem sys, HttpServletRequest request);

    List<Course> listSubByIds(List<Integer> subIds);

    List<Course> listSubByIdsAndName(List<Integer> subIds, String name);

    List<Course> getLevel0SubListWithImg(
        Integer masterId, HttpServletRequest request, List<Integer> channelIds);

    List<Course> getLevel0SubLis(Integer masterId);

    List<Integer> getCourseIds(Integer masterId);

    List<Course> getSubjectChild(Integer subId);

    List<Course> getSubListWithImgByIds(
        List<Integer> subIds, SysSystem sys, HttpServletRequest request, Integer masterId);

    List<Course> getSubListWithHidden(Integer masterId);

    boolean deleteSub(Integer subId, Integer masterId);

    List<Course> listSubWithAssoByIds(Integer masterId, List<Integer> subIds);

    List<Course> setSubListImg(List<Course> list, SysSystem sys, HttpServletRequest request);

    List<Course> selectSubjectAssociation(
        Integer masterId, List<Integer> subIds, boolean ifLevel0);

    Map<String, Object> selectEventResNumMapForWorkbook(Integer subId, Integer userId);

    Map<String, Object> selectEventResNumMapForWorkbookTeacher(
        Integer subId, Integer studentId, Integer teacherId);

    Map<String, Object> getAnswerMessageMapForTeacherWorkbook(
        Integer subId, Integer studentId, Integer teacherId);

    List<Course> getSubVideoEventList(
        Integer subId, Integer studentId, Integer masterId, HttpServletRequest request);

    List<Course> getLevel1VideoEventList(Integer subId, Integer userId, Integer teacherId);

    Integer countCourseForName(Course subject);

    List<Course> selecUnitNumForVideo(Integer videoId);

    List<Integer> countSessions(List<Integer> id);

    List<Course> selectTwoSubjectsByFids(List<Integer> fids);

    List<Course> selectAllLevel1SubList(List<Integer> subIds, String order, Integer masterId);

    Course saveSubInfo(Course sub, GcManager manager, GcMaster master, GcUser user, HttpServletRequest request);

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

    List<Course> getAvailableCourses(
        String name, Integer masterId, List<Integer> subIds, Integer userId, String order);

    List<Course> newGetAvailableCourses(
        String name, Integer masterId, List<Integer> subIds, Integer userId, String order);

    List<Course> selectSubjectByNewIndexHome(
        Integer masterId, Integer userId, PageParam pageParam);

    List<Course> selectSubjectMay(Integer masterId, Integer userId, PageParam pageParam);

    List<Course> selectActiveSubject(
        Integer userId,
        Integer masterId,
        Integer subjectState,
        String name,
        HttpServletRequest request);

    List<Course> selectCompletedSubject(
        Integer userId,
        Integer masterId,
        Integer subjectState,
        String name,
        HttpServletRequest request);

    List<Course> selectDiscoverSubject(
        Integer userId,
        Integer masterId,
        Integer subjectState,
        String name,
        HttpServletRequest request);

    List<Course> selectDraftsSubject(
        Integer userId, Integer masterId, String name, HttpServletRequest request);

    List<Course> selectFromMyTeamSubject(
        Integer userId, Integer masterId, String name, HttpServletRequest request);

    List<Course> selectCompanyResourcesSubject(
        Integer userId, Integer masterId, String name, HttpServletRequest request);

    List<Course> selectAllCourseSubject(
        Integer userId, Integer masterId, String name, HttpServletRequest request, Integer orderType);

    List<Course> selectPublishedSubject(
        Integer userId, Integer masterId, String name, HttpServletRequest request);

    List<Course> selectCreatedByTeams(
        Integer userId,
        Integer masterId,
        String name,
        HttpServletRequest request,
        List<String> groupCodeList,
        Integer orderType);

    List<Course> selectCreateByTeamsOrgAdmin(
        Integer userId,
        Integer masterId,
        String name,
        HttpServletRequest request,
        List<String> groupCodeList,
        Integer orderType);

    List<Course> getSubjectInfoByList(
        List<Course> level0sublist, Integer masterId, Integer userId, HttpServletRequest request);

    Integer inProgressNum(Integer userId, Integer masterId);

    Integer getSubjectNameIndex(Integer masterId, String nameIndex);

    Integer getNewMyAssignmentNew(Integer masterId, Integer userId);

    void initJit();

    List<Integer> getUserCreateSubject(Integer masterId, Integer userId);

    List<Integer> getUserCreateSubjectAdmin(Integer masterId, Integer userId);

    List<Integer> getUserPublicSubject(Integer masterId, Integer userId);

    void populateUserId(Course course, GcUser user);

    CourseProgramDto courseProgram(Integer courseId, PortalUser portalUser);

    void updateUrls(Course course);

    CourseVideoBookmarkDto lastViewedBookmark(Integer courseId, PortalUser portalUser);

    List<GcVideo> courseVideos(Integer courseId);

    CourseListDto<AssignedCourseDto> getAssignedCourses(PortalUser portalUser);

    CourseListDto<CourseDto> getOwnedCourses(PortalUser portalUser);

    CourseListDto<CourseDto> getDiscoverableCourses(PortalUser portalUser);

    List<Course> searchCourses(String searchName, PortalUser portalUser);

    List<Course> searchSuggestedCourses(PortalUser portalUser);

    CourseDto getCourseDetails(Integer courseId, PortalUser portalUser);
}
