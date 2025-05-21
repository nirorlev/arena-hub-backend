package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.Course;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

public interface NewUiGcSubjectMapper extends BaseMapper<Course> {

    List<Course> findSubjects(Map<String, Object> params);

    @MapKey("id")
    Map<Integer, Course> sumSubjectDuration(@Param("ids") List<Integer> ids);

    @MapKey("id")
    Map<Integer, Course> sumSubject1Duration(@Param("ids") List<Integer> ids);

    List<Course> listByFid(Map<String, Object> params);

    List<Course> listByIds(@Param("ids") List<Integer> ids, @Param("masterId") Integer masterId);

    List<Integer> countSessions(@Param("ids") List<Integer> ids);

    List<Integer> getTwoLevelSubByIds(@Param("ids") List<Integer> ids);

    List<Course> getVideoNumByIds(@Param("ids") List<Integer> ids);

    List<Course> select(@Param("subIds") List<Integer> subIds);

    Integer getSubjectNum(@Param("ids") List<Integer> ids);

    List<Course> selectSubjectsByVids(@Param("vids") List<Integer> vids);

    List<Course> selectSubjectsByIds(@Param("ids") List<Integer> ids);

}
