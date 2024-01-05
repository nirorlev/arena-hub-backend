package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.*;
import org.apache.ibatis.annotations.MapKey;
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
 * @since 2019-11-25
 */
@Component
public interface GcUserEventMapper extends BaseMapper<GcUserEvent> {

    void insertBatch(@Param("userEvents") List<GcUserEvent> lstData);

    @MapKey("eventId")
    Map<Integer,Object> selectUserEvents(@Param("list") List<Integer> userIds);
}
