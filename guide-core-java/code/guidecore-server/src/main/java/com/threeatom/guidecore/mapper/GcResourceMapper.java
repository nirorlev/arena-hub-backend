package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcResource;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-19
 */
public interface GcResourceMapper extends BaseMapper<GcResource> {

    List<GcResource> selectResListByVid(Integer vid);

    Integer countResourceNum(
            @Param("masterId") Integer masterId,
            @Param("type") Integer type,
            @Param("state") Integer state,
            @Param("subIds") List<Integer> subIds,
            @Param("managerId") Integer managerId);
}
