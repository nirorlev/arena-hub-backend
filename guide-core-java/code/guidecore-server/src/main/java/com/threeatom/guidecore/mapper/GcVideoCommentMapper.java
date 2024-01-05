package com.threeatom.guidecore.mapper;

import com.threeatom.guidecore.entity.GcVideoComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 视频评论 Mapper 接口
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
public interface GcVideoCommentMapper extends BaseMapper<GcVideoComment> {

    List<GcVideoComment> selectGetAllCommentByVideoId(Integer vid, Integer limit);

    List<GcVideoComment> selectGetAllCommentByVideoIdAndUserId(@Param("vid") Integer vid,@Param("userId") Integer userId,@Param("masterId")Integer masterId);
    
    Integer countCommentForVideo(Integer videoId,Integer userId,Integer masterId);
    /**
     * 	根据视频id加载评论列表，以及评论人信息
     * @param videoIds
     * @return
     */
    List<GcVideoComment> getVideoComments(@Param("videoIds")List<Integer> videoIds,@Param("masterId")Integer masterId);

    List<GcVideoComment> selectCommentByMainCommentId(@Param("mainId") Integer mainId);

    Boolean insertComment(GcVideoComment videoComment);

    Integer countCommentByVideoList(@Param("list")List<Integer> videoIds);
}
