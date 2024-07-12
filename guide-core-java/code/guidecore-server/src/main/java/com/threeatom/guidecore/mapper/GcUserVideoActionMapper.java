package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

public interface GcUserVideoActionMapper extends BaseMapper<GcUserVideoAction> {

    List<Map<String, Object>> countTypeRateForVideo(
        @Param("contentId") Integer contentId, @Param("type") Integer type);

    List<GcUserVideoAction> getVideoActionBySubject(Map<String, Object> params);

    @MapKey("subjectId")
    Map<Integer, GcUserVideoAction> getSubjectUserStar(Map<String, Object> params);

    @MapKey("subjectId")
    Map<Integer, GcUserVideoAction> gvggetSubjectUserStar(Map<String, Object> params);

    @MapKey("subjectId")
    Map<Integer, GcUserVideoAction> gvgGetSubjectUserStar(Map<String, Object> params);

    Integer countLikeForVideo(Integer videoId);

    Integer countLikeForFile(Integer contentId);

    List<GcUserVideoAction> countLikeForVideos(@Param("videoIds") List<Integer> videoIds);

    List<GcUserVideoAction> countLikeForFiles(@Param("contentIds") List<Integer> contentIds);
}
