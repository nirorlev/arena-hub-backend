package com.threeatom.guidecore.mapper;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.threeatom.guidecore.entity.GcUser;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcSubject;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-11
 */
public interface GcSubjectMapper extends BaseMapper<GcSubject> {

	List<GcSubject> listSubWithAssoByIds(Integer masterId, List<Integer> list);
	
	List<GcSubject> listSubWithAssoWithChildSubNotHiddenByIds(Integer masterId, List<Integer> list);
	
    GcSubject selectSubByVid(Integer vid);
    
    GcSubject selectByid(Integer id);

    Integer selectGetSubIdByEventId(Integer eventId);

    Integer getTopSubByVideoId(Integer videoId);
    
    List<GcSubject>  listSubByIds (List<Integer> list);

    List<GcSubject> listSubByIdsAndName(List<Integer> list,String name);
    
//    List<GcSubject>  selectImportedSubject(Integer masterId);
    //ifLevel0 表示只查询顶级课程，子课程不查询
    List<GcSubject>  selectSubjectAssociation(@Param("masterId") Integer masterId, @Param("subIds") List<Integer> subIds, @Param("ifLevel0") boolean ifLevel0);
    
    List<GcSubject> getSubjectListCommon(@Param("masterId") Integer masterId, @Param("level") Integer level, @Param("state") Integer state);

    List<GcSubject> getSubjectList(@Param("masterId") Integer masterId, @Param("level") Integer level, @Param("state") Integer state,@Param("channelIds")List<Integer> channelIds,@Param("createUser")Integer createUser);

    List<GcSubject> selectSubjectPt(@Param("subjectName")String subjectName,@Param("type")Integer type,@Param("state")Integer state,@Param("createUser")Integer createUser,@Param("masterId")Integer masterId,@Param("userId")Integer userId,@Param("channelIds")List<Integer> channelIds,@Param("subIdList") List<Integer> subIdList);

    Integer getCreateUserPublished(@Param("userId") Integer userId,@Param("masterId") Integer masterId,@Param("state")Integer state);

    Integer getDiscoverNum(@Param("userId")Integer userId, @Param("masterId")Integer masterId);

    List<GcSubject> getAvailableCourses(@Param("name")String name,@Param("masterId")Integer masterId,@Param("subIds") List<Integer> subIds,@Param("userId")Integer userId,@Param("orderType")String orderType);

    List<GcSubject> newGetAvailableCourses(@Param("name")String name,@Param("masterId")Integer masterId,@Param("subIds") List<Integer> subIds,@Param("userId")Integer userId,@Param("orderType")String orderType);

    List<GcSubject> getCompletedTwoCourse(@Param("param") Map<String,Object> paramMap);

    List<GcSubject> getSubListByPermissionIds(@Param("permissionIds")List<Integer> permissionIds);

    List<GcSubject> getSubListByPermissionIdsFid(@Param("permissionIds")List<Integer> permissionIds);

    List<GcSubject> getSubVideoEventList(@Param("subId") Integer subId, @Param("studentId") Integer studentId,@Param("masterId")Integer masterId);
    
    List<GcSubject> getLevel1VideoEventList(@Param("subId") Integer subId,@Param("userId") Integer userId, @Param("teacherId") Integer teacherId);
    
    @MapKey("numKey")
    Map<String,Object> selectEventResNumMapForWorkbook(@Param("subId") Integer subId,@Param("userId") Integer userId);
    
    @MapKey("numKey")
    Map<String,Object> selectEventResNumMapForWorkbookTeacher(
    		@Param("subId") Integer subId,
    		@Param("studentId") Integer studentId,
    		@Param("teacherId") Integer teacherId);
    
    @MapKey("eventId")
    Map<String,Object> getAnswerMessageMapForTeacherWorkbook(@Param("subId") Integer subId,@Param("studentId") Integer studentId,@Param("teacherId") Integer teacherId);
    
    Integer countCourseForName(GcSubject subject);
    
    List<GcSubject>selectS1ByS0NameIndex(GcSubject subject);
    
    List<GcSubject> selecUnitNumForVideo(Integer videoId);

    List<GcSubject> selectSubListById(Integer id);

    List<GcSubject> selectAllSubByUserId(@Param("masterId") Integer masterId,@Param("userId") Integer userId);

    List<GcSubject> selectSubjectByVids(@Param("vids")List<Integer> vids);

    List<GcSubject> selectTwoSubjectsByFids(@Param("fids")List<Integer> fids);

    Integer countCourseInPortal(@Param("masterId")Integer masterId,@Param("type")Integer type,@Param("subIds")List<Integer> subIds,@Param("managerId")Integer managerId);

    Integer countImportedToicNum(@Param("masterId")Integer masterId, @Param("type")Integer type, @Param("state")Integer state, @Param("subIds")List<Integer> subIds, @Param("managerId")Integer managerId);

    List<GcSubject> selectAllSubList(@Param("masterId")Integer masterId,@Param("stateAndType")Integer stateAndType);

    List<GcSubject> selectAllLevel1SubList(@Param("subIds")List<Integer> subIds,@Param("order")String order,@Param("masterId") Integer masterId);

    List<GcSubject> gvgSelectAllLevel1SubList(@Param("subIds")List<Integer> subIds,@Param("order")String order,@Param("masterId") Integer masterId);

    List<GcSubject> selectSubjectVideoList(@Param("subIds")List<Integer> subIds,@Param("order")String order,@Param("masterId") Integer masterId,@Param("context")String context);

    List<GcSubject> selectCategorySubject(@Param("cateParams")Map<String,Object> cateParams);

    List<GcUser> selectAllSubsByPartipants(@Param("params")Map<String,Object> params);

    List<GcSubject> recommentCourse(@Param("userId") Integer userId);

    GcSubject selectTagsById(@Param("id")Integer subjectId,@Param("masterId") Integer masterId);

    List<GcSubject> selectListByMasterId(@Param("masterId") Integer masterId);

    List<GcSubject> selectSubjectByIdList(@Param("list")List<Integer> list,@Param("tag")String tag,@Param("masterId")Integer masterId);

    List<Integer> selectSubIdListByMaster(@Param("masterId")Integer masterId);

    List<Integer> selectSubjectByCreateUser(@Param("masterId")Integer masterId,@Param("createUser")Integer createUser);

    List<Integer> getPublicSubjectIds(@Param("masterID")Integer masterId);

    List<GcSubject> getSublistWithImg();

    List<GcSubject> selectSubjectById(@Param("subId")Integer subId);
    
    GcSubject selectFirstSubForMaster(@Param("masterId") Integer masterId);

    GcSubject getSubjectByNameIndex(@Param("portalId") String portalId,@Param("nameIndex")String nameIndex);

    List<GcSubject> getSubjectUserInfo(@Param("userIdList") List<Integer> userIdList,@Param("subList") List<Integer> subList,@Param("masterId") Integer masterId);

    List<GcSubject> getUserSubjectList(@Param("param") Map<String,Object> paramMap);

    List<GcSubject> getCompleteSubject(@Param("param") Map<String,Object> paramMap);

    Integer productNums(@Param("masterId")Integer masterId);

    List<Integer> selectNotNullVideoList(List<Integer> subIds,Integer masterId);

    List<GcSubject> selectSubjectByNewIndexHome(@Param("userId") Integer userId,@Param("masterId") Integer masterId,@Param("subIdList") List<Integer> subIdList);

    List<GcSubject> selectMaySubjectByNewIndexHome(@Param("userId")Integer userId,@Param("masterId")Integer masterId,@Param("subIdList") List<Integer> subIdList,@Param("progress") Integer progress);

    List<GcSubject> selectActiveSubject(@Param("userId")Integer userId,@Param("masterId")Integer masterId,@Param("name")String name,@Param("subjectState") Integer subjectState,@Param("orgMustSubjectIds") List<Integer> orgMustSubjectIds);

    Integer getSubjectNameIndex(@Param("masterId")Integer masterId,@Param("nameIndex")String nameIndex);

    Integer getNewMyAssignmentNew(@Param("masterId") Integer masterId,@Param("userId") Integer userId);

    List<Integer> getUserCreateSubject(@Param("masterId") Integer masterId,@Param("userId") Integer userId);

    List<Integer> getUserCreateSubjectAdmin(@Param("masterId")Integer masterId,@Param("userId")Integer userId);

    List<Integer> getUserPublicSubject(@Param("masterId")Integer masterId,@Param("userId") Integer userId);

    List<GcSubject> selectOrgMustJsonArrayList(@Param("userId")Integer userId,@Param("masterId")Integer masterId);

    List<GcSubject> selectOrgMayJsonArrayList(@Param("userId")Integer userId,@Param("masterId")Integer masterId);


    List<GcSubject> selectCompletedSubject(@Param("userId")Integer userId,@Param("masterId")Integer masterId,@Param("name") String name,@Param("subjectState") Integer subjectState);

    List<GcSubject> selectPublishedSubject(@Param("userId")Integer userId,@Param("masterId")Integer masterId,@Param("name")String name);

    List<GcSubject> selectCreatedByTeamsSubject(@Param("userId")Integer userId,@Param("masterId")Integer masterId,@Param("name")String name,@Param("groupCodeList") List<String> groupCodeList,@Param("orderType")Integer orderType);

    List<GcSubject> selectCreateByTeamsOrgAdmin(@Param("userId")Integer userId,@Param("masterId")Integer masterId,@Param("name")String name,@Param("groupCodeList") List<String> groupCodeList,@Param("orderType")Integer orderType);

    List<GcSubject> selectCompanyResourcesSubject(@Param("userId")Integer userId,@Param("masterId")Integer masterId,@Param("name")String name,@Param("orgMaySubjectIds")List<Integer> orgMaySubjectIds);

    List<GcSubject> selectAllCourseSubject(@Param("userId")Integer userId,@Param("masterId")Integer masterId,@Param("name")String name,@Param("orderType")Integer orderType);

    List<GcSubject> selectFromMyTeamSubject(@Param("userId")Integer userId,@Param("masterId")Integer masterId,@Param("name")String name);

    List<GcSubject> selectDraftsSubject(@Param("userId")Integer userId,@Param("masterId")Integer masterId,@Param("name") String name);

    List<GcSubject> selectDiscoverSubject(@Param("userId")Integer userId,@Param("masterId")Integer masterId,@Param("name") String name,@Param("subjectState") Integer subjectState,@Param("orgMaySubjectIds") List<Integer> orgMaySubjectIds);

    List<Integer> getNewAssignments(@Param("userId")Integer userId,@Param("masterId")Integer masterId);
}
