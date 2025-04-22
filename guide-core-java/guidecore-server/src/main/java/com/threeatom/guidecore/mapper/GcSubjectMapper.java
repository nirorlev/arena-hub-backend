package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcSubject;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

public interface GcSubjectMapper extends BaseMapper<GcSubject> {

    List<GcSubject> listSubWithAssoByIds(Integer masterId, List<Integer> list);

    GcSubject selectSubByVid(Integer vid);

    List<GcSubject> listSubByIds(List<Integer> list);

    List<GcSubject> listSubByIdsAndName(List<Integer> list, String name);

    List<GcSubject> selectSubjectAssociation(
        @Param("masterId") Integer masterId,
        @Param("subIds") List<Integer> subIds,
        @Param("ifLevel0") boolean ifLevel0);

    List<GcSubject> getSubjectListCommon(
        @Param("masterId") Integer masterId,
        @Param("level") Integer level,
        @Param("state") Integer state);

    List<GcSubject> getSubjectList(
        @Param("masterId") Integer masterId,
        @Param("level") Integer level,
        @Param("state") Integer state,
        @Param("channelIds") List<Integer> channelIds,
        @Param("createUser") Integer createUser);

    List<GcSubject> selectSubjectPt(
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

    List<GcSubject> getAvailableCourses(
        @Param("name") String name,
        @Param("masterId") Integer masterId,
        @Param("subIds") List<Integer> subIds,
        @Param("userId") Integer userId,
        @Param("orderType") String orderType);

    List<GcSubject> newGetAvailableCourses(
        @Param("name") String name,
        @Param("masterId") Integer masterId,
        @Param("subIds") List<Integer> subIds,
        @Param("userId") Integer userId,
        @Param("orderType") String orderType);

    List<GcSubject> getCompletedTwoCourse(@Param("param") Map<String, Object> paramMap);

    List<GcSubject> getSubVideoEventList(
        @Param("subId") Integer subId,
        @Param("studentId") Integer studentId,
        @Param("masterId") Integer masterId);

    List<GcSubject> getLevel1VideoEventList(
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

    Integer countCourseForName(GcSubject subject);

    List<GcSubject> selectS1ByS0NameIndex(GcSubject subject);

    List<GcSubject> selecUnitNumForVideo(Integer videoId);

    List<GcSubject> selectTwoSubjectsByFids(@Param("fids") List<Integer> fids);

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

    List<GcSubject> selectAllLevel1SubList(
        @Param("subIds") List<Integer> subIds,
        @Param("order") String order,
        @Param("masterId") Integer masterId);

    GcSubject selectTagsById(@Param("id") Integer subjectId, @Param("masterId") Integer masterId);

    List<GcSubject> selectListByMasterId(@Param("masterId") Integer masterId);

    List<Integer> selectSubjectByCreateUser(
        @Param("masterId") Integer masterId, @Param("createUser") Integer createUser);

    List<Integer> getPublicSubjectIds(@Param("masterID") Integer masterId);

    List<GcSubject> selectSubjectByNewIndexHome(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("subIdList") List<Integer> subIdList);

    List<GcSubject> selectMaySubjectByNewIndexHome(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("subIdList") List<Integer> subIdList,
        @Param("progress") Integer progress);

    List<GcSubject> selectActiveSubject(
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

    List<GcSubject> selectCompletedSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name,
        @Param("subjectState") Integer subjectState);

    List<GcSubject> selectPublishedSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name);

    List<GcSubject> selectCreatedByTeamsSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name,
        @Param("groupCodeList") List<String> groupCodeList,
        @Param("orderType") Integer orderType);

    List<GcSubject> selectCreateByTeamsOrgAdmin(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name,
        @Param("groupCodeList") List<String> groupCodeList,
        @Param("orderType") Integer orderType);

    List<GcSubject> selectCompanyResourcesSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name,
        @Param("orgMaySubjectIds") List<Integer> orgMaySubjectIds);

    List<GcSubject> selectAllCourseSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name,
        @Param("orderType") Integer orderType);

    List<GcSubject> selectFromMyTeamSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name);

    List<GcSubject> selectDraftsSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name);

    List<GcSubject> selectDiscoverSubject(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("name") String name,
        @Param("subjectState") Integer subjectState,
        @Param("orgMaySubjectIds") List<Integer> orgMaySubjectIds);

    List<Integer> getNewAssignments(
        @Param("userId") Integer userId, @Param("masterId") Integer masterId);
}
