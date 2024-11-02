package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUser;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface GcUserMapper extends BaseMapper<GcUser> {
    GcUser getGcUserByUserId(Integer id);

    List<GcUser> getUserByUserAccessIds(@Param("userAccessIds") List<Integer> userAccessIds);

    @MapKey("subjectId")
    Map<Integer, GcUser> getWatchedUserNum(
        @Param("subjectIds") List<Integer> subjectIds,
        @Param("masterId") Integer masterId);

    List<GcUser> getTeamUser(@Param("params") Map<String, Object> params);
}
