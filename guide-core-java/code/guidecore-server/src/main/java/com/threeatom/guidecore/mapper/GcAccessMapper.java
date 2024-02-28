package com.threeatom.guidecore.mapper;

import com.threeatom.guidecore.entity.GcAccess;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-11
 */
@Component
public interface GcAccessMapper extends BaseMapper<GcAccess> {

    List<Map<String,Object>> getAccessAndUserNums(Integer masterId);

    Map<String,Long> getALlAccessCodeNumsByMasterId(Integer masterId);

    List<GcAccess> getContainsAccessList(@Param("ptId") String ptId,@Param("masterId") Integer masterId);
    
    List<GcAccess> listContainsSub(Integer masterId,Integer subjectId);

    List<GcAccess> getAllPackage(Integer masterId,Integer showFlag,List<Integer> packageIdList,List<Integer> idList);

    List<GcAccess> selectAllPackage(Integer masterId);

    List<GcAccess> listAccess(@Param("name")String name,@Param("masterId") Integer masterId,@Param("userId")Integer userId);

    List<GcAccess> getTeamAccessList(@Param("name")String name,@Param("masterId") Integer masterId,@Param("userId")Integer userId);

    List<GcAccess> getTeamAccessSubjectNumAdminList(@Param("name")String name,@Param("masterId") Integer masterId,@Param("userId")Integer userId);

    List<GcAccess> getTeamAccessSubjectNumList(@Param("name")String name,@Param("masterId") Integer masterId,@Param("userId")Integer userId);

    List<GcAccess> listAllAccess(@Param("query")Map<String, Object> params);

    List<GcAccess> getAccessBySubjectId(@Param("masterId") Integer masterId,@Param("id") Integer id);

    List<GcAccess> getAccessByChannelId(@Param("masterId") Integer masterId,@Param("id") Integer id);

    void insertOrUpdateList(List<GcAccess> accessList);

    void insertOrUpdateChannel(List<GcAccess> accessList);

    List<GcAccess> selectAccessByCodeAndMasterId(@Param("list") List<String> codeList,Integer masterId);

    List<GcAccess> selectAccessByIds(@Param("list") List<Integer> idList);

    List<GcAccess> selectAccessBySubId(@Param("subId") String subId,@Param("masterId")Integer masterId);

    List<GcAccess> getAllAccessByMasterId(@Param("masterId")Integer masterId);

    List<GcAccess> selectAccessLevel0(@Param("masterId")Integer masterId,@Param("userId") Integer userId);
}
