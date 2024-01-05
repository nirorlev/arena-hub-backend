package com.threeatom.guidecore.mapper;

import java.util.List;
import java.util.Map;

import com.threeatom.guidecore.entity.StudentInfoVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.controller.user.vo.UserCommonInfo;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.excel.vo.StudentBehaviorDataExcel;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-25
 */
@Component
public interface GcUserMapper extends BaseMapper<GcUser> {
    GcUser getGcUserByUserId(Integer id);

    List<GcUser> getGcUserByUserIds(JSONArray userIds);

    List<GcUser> selectGetTalkerByUserIds(List<Integer> userIds);

    List<GcUser> getUserByUserAccessIds(@Param("userAccessIds") List<Integer> userAccessIds);

    List<Map<String,Object>> getUserCommonInfo(UserCommonInfo userCommonInfo,PageParam pageParam);

    List<Map<String,Object>> getUserAgeCommonInfo(UserCommonInfo userCommonInfo,PageParam pageParam);

    List<StudentBehaviorDataExcel> getStudentBehaviorExcelData(Integer accessId, Integer masterId, @Param(Constants.WRAPPER) Wrapper query);
    
    List<GcUser> userTalkerList(@Param("userId") Integer userId,@Param("masterId") Integer masterId,@Param("ifStudent") Boolean ifStudent,@Param("groupId") Integer groupId );
    /**
     * 	根据课程id统计参与人数
     * @param ids
     * @param userId
     * @return
     */
    @MapKey("subjectId")
    Map<Integer, GcUser> getUsersBySubject(@Param("ids") List<Integer> ids,@Param("masterId")Integer masterId);

    @MapKey("subjectId")
    Map<Integer, GcUser> getWatchedUserNum(@Param("subjectIds") List<Integer> subjectIds,@Param("masterId")Integer masterId);

    List<StudentInfoVO> getStudentVideoNum(@Param("list") List<Integer> ids);

    List<Map<String,Object>> selectTeacherListByMasterId(@Param("masterId")Integer masterId,@Param("userId")Integer userId);

    List<GcUser> getTeamUser(@Param("params") Map<String, Object> params);
}
