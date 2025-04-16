package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcVideoComment;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GcVideoCommentMapper extends BaseMapper<GcVideoComment> {

    List<GcVideoComment> selectGetAllCommentByVideoId(Integer vid, Integer limit);

    List<GcVideoComment> selectGetAllCommentByVideoIdAndUserId(
        @Param("vid") Integer vid,
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId);

    Integer countCommentForVideo(Integer videoId, Integer userId, Integer masterId);

    List<GcVideoComment> getVideoComments(
        @Param("videoIds") List<Integer> videoIds, @Param("masterId") Integer masterId);

    List<GcVideoComment> selectCommentByMainCommentId(@Param("mainId") Integer mainId);

    Boolean insertComment(GcVideoComment videoComment);
}
