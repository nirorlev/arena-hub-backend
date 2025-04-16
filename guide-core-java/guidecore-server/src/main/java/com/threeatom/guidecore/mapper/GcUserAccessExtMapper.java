package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUserAccessExt;
import java.util.List;
import java.util.Map;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface GcUserAccessExtMapper extends BaseMapper<GcUserAccessExt> {
    List<Map<String, Object>> getUserLoginNum(
            List<Integer> userIds, String startDate, String endDate);

    GcUserAccessExt getOneByUserAccessId(@Param("userAccessId") Integer userAccessId);

    List<Map<String, Object>> getByManagerId(Integer managerId, String startDate, String endDate);

    List<Map<String, Object>> getAllUserLastLogin(@Param("lastDaysScope") Integer lastDaysScope);
}
