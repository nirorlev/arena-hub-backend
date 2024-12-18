package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcSubject;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

public interface NewUiGcSubjectMapper extends BaseMapper<GcSubject> {

    List<GcSubject> findSubjects(Map<String, Object> params);

    @MapKey("id")
    Map<Integer, GcSubject> sumSubjectDuration(@Param("ids") List<Integer> ids);

    @MapKey("id")
    Map<Integer, GcSubject> sumSubject1Duration(@Param("ids") List<Integer> ids);

    List<GcSubject> listByFid(Map<String, Object> params);

    List<GcSubject> listByIds(@Param("ids") List<Integer> ids, @Param("masterId") Integer masterId);

    List<Integer> countSessions(@Param("ids") List<Integer> ids);

    List<Integer> getTwoLevelSubByIds(@Param("ids") List<Integer> ids);

    List<GcSubject> getVideoNumByIds(@Param("ids") List<Integer> ids);

    List<GcSubject> select(@Param("subIds") List<Integer> subIds);

    Integer getSubjectNum(@Param("ids") List<Integer> ids);

    List<GcSubject> selectSubjectsByVids(@Param("vids") List<Integer> vids);

    List<GcSubject> selectSubjectsByIds(@Param("ids") List<Integer> ids);

}
