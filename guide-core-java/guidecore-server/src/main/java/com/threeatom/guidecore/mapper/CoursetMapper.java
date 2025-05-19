package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.Course;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

public interface CoursetMapper extends BaseMapper<Course> {

    List<Course> listSubWithAssoByIds(Integer masterId, List<Integer> list);

    Course selectSubByVid(Integer vid);

    List<Course> listSubByIds(List<Integer> list);

    List<Course> listSubByIdsAndName(List<Integer> list, String name);

    List<Course> selectSubjectAssociation(
        @Param("masterId") Integer masterId,
        @Param("subIds") List<Integer> subIds,
        @Param("ifLevel0") boolean ifLevel0);

    List<Course> getSubjectListCommon(
        @Param("masterId") Integer masterId,
        @Param("level") Integer level,
        @Param("state") Integer state);

    List<Course> getSubjectList(
        @Param("masterId") Integer masterId,
        @Param("level") Integer level,
        @Param("state") Integer state,
        @Param("channelIds") List<Integer> channelIds,
        @Param("createUser") Integer createUser);

    List<Course> selectSubjectPt(
        @Param("subjectName") String subjectName,
        @Param("type") Integer type,
        @Param("state") Integer state,
        @Param("createUser") Integer createUser,
        @Param("masterId") Integer masterId,
        @Param("userId") Integer userId,
        @Param("channelIds") List<Integer> channelIds,
        @Param("subIdList") List<Integer> subIdList);

    Integer getCreateUserPublished(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("state") Integer state);

    List<Course> getAvailableCourses(
        @Param("name") String name,
        @Param("masterId") Integer masterId,
        @Param("subIds") List<Integer> subIds,
        @Param("userId") Integer userId,
        @Param("orderType") String orderType);

    List<Course> newGetAvailableCourses(
        @Param("name") String name,
        @Param("masterId") Integer masterId,
        @Param("subIds") List<Integer> subIds,
        @Param("userId") Integer userId,
        @Param("orderType") String orderType);

    List<Course> getCompletedTwoCourse(@Param("param") Map<String, Object> paramMap);

    List<Course> getSubVideoEventList(
        @Param("subId") Integer subId,
        @Param("studentId") Integer studentId,
        @Param("masterId") Integer masterId);

    List<Course> getLevel1VideoEventList(
        @Param("subId") Integer subId,
        @Param("userId") Integer userId,
        @Param("teacherId") Integer teacherId);

    @MapKey("numKey")
    Map<String, Object> selectEventResNumMapForWorkbook(
        @Param("subId") Integer subId, @Param("userId") Integer userId);

    @MapKey("numKey")
    Map<String, Object> selectEventResNumMapForWorkbookTeacher(
        @Param("subId") Integer subId,
        @Param("studentId") Integer studentId,
        @Param("teacherId") Integer teacherId);

    @MapKey("eventId")
    Map<String, Object> getAnswerMessageMapForTeacherWorkbook(
        @Param("subId") Integer subId,
        @Param("studentId") Integer studentId,
        @Param("teacherId") Integer teacherId);

    Integer countCourseForName(Course subject);

    List<Course> selectS1ByS0NameIndex(Course subject);

    List<Course> selecUnitNumForVideo(Integer videoId);

    List<Course> selectTwoSubjectsByFids(@Param("fids") List<Integer> fids);

    Integer countCourseInPortal(
        @Param("masterId") Integer masterId,
        @Param("type") Integer type,
        @Param("subIds") List<Integer> subIds,
        @Param("managerId") Integer managerId);

    Integer countImportedToicNum(
        @Param("masterId") Integer masterId,
        @Param("type") Integer type,
        @Param("state") Integer state,
        @Param("subIds") List<Integer> subIds,
        @Param("managerId") Integer managerId);

    List<Course> selectAllLevel1SubList(
        @Param("subIds") List<Integer> subIds,
        @Param("order") String order,
        @Param("masterId") Integer masterId);

    Course selectTagsById(@Param("id") Integer subjectId, @Param("masterId") Integer masterId);

    List<Course> selectListByMasterId(@Param("masterId") Integer masterId);

    List<Integer> selectSubjectByCreateUser(
        @Param("masterId") Integer masterId, @Param("createUser") Integer createUser);

    List<Integer> getPublicSubjectIds(@Param("masterID") Integer masterId);

    List<Course> selectSubjectByNewIndexHome(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("subIdList") List<Integer> subIdList);

    List<Course> selectMaySubjectByNewIndexHome(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("subIdList") List<Integer> subIdList,
        @Param("progress") Integer progress);

    List<Course> selectActiveSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name,
        @Param("subjectState") Integer subjectState,
        @Param("orgMustSubjectIds") List<Integer> orgMustSubjectIds);

    Integer getSubjectNameIndex(
        @Param("masterId") Integer masterId, @Param("nameIndex") String nameIndex);

    Integer getNewMyAssignmentNew(
        @Param("masterId") Integer masterId, @Param("userId") Integer userId);

    List<Integer> getUserCreateSubject(
        @Param("masterId") Integer masterId, @Param("userId") Integer userId);

    List<Integer> getUserCreateSubjectAdmin(
        @Param("masterId") Integer masterId, @Param("userId") Integer userId);

    List<Integer> getUserPublicSubject(
        @Param("masterId") Integer masterId, @Param("userId") Integer userId);

    List<Course> selectCompletedSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name,
        @Param("subjectState") Integer subjectState);

    List<Course> selectPublishedSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name);

    List<Course> selectCreatedByTeamsSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name,
        @Param("groupCodeList") List<String> groupCodeList,
        @Param("orderType") Integer orderType);

    List<Course> selectCreateByTeamsOrgAdmin(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name,
        @Param("groupCodeList") List<String> groupCodeList,
        @Param("orderType") Integer orderType);

    List<Course> selectCompanyResourcesSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name,
        @Param("orgMaySubjectIds") List<Integer> orgMaySubjectIds);

    List<Course> selectAllCourseSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name,
        @Param("orderType") Integer orderType);

    List<Course> selectFromMyTeamSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name);

    List<Course> selectDraftsSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name);

    List<Course> selectDiscoverSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name,
        @Param("subjectState") Integer subjectState,
        @Param("orgMaySubjectIds") List<Integer> orgMaySubjectIds);

    List<Course> coursesByState(Integer state, Integer masterId);

    List<Integer> getNewAssignments(
        @Param("userId") Integer userId, @Param("masterId") Integer masterId);

    List<Course> searchCourses(@Param("searchName") String searchName, @Param("userId") Integer userId,
                               @Param("masterId") Integer masterId);

    List<Course> ownedCourses(Integer userId, Integer masterId);

    Course getCourseById(Integer courseId);
}
