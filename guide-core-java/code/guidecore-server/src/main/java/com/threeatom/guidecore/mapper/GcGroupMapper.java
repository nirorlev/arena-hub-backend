package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcGroup;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 教师编辑的组权限 Mapper 接口
 * </p>
 *
 * @author qiaoxide
 * @since 2019-12-25
 */
public interface GcGroupMapper extends BaseMapper<GcGroup> {

    List<Map<String, Object>> selectGetGroupListByUserId(
            @Param("userId") Integer userId, @Param("masterId") Integer masterId);

    /***
     * 用户数组里面相关的组集合
     * @param userAccessIds
     * @return
     */
    List<GcGroup> selectGcGroupByUserAccessIds(@Param("whereSql") String whereSql);

    List<GcGroup> selectGroupListByUserIdAndMasterId(
            @Param("userId") Integer userId,
            @Param("masterId") Integer masterId,
            @Param("name") String name);

    List<GcGroup> getUserIdByGroupIds(@Param("groupIds") List<Integer> groupIds);

    List<GcGroup> selectGroupList(@Param("userAccessId") Integer userAccessId);
}
