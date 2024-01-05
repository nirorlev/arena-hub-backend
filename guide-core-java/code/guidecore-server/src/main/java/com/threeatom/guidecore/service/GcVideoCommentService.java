package com.threeatom.guidecore.service;

import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcVideoComment;
import com.threeatom.system.entity.SysSystem;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 视频评论 服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
public interface GcVideoCommentService extends IService<GcVideoComment> {
    boolean saveVideoComment(GcVideoComment videoComment);

    List<GcVideoComment> getMyVideoCommentByVideoIds(List<Integer> videoIds, Integer userId);

    List<Integer> getCommentNumsByVideoIds(List<Integer> videoIds);

    List<GcVideoComment> getAllCommentByVideoId(Integer vid,Integer limit);

    List<GcVideoComment> getAllCommentByVideoIdAndUserId(Integer vid,Integer userId,Integer masterId);

    Message getCommentStream(Integer subId, GcUser user, GcSubject sub,SysSystem sys,HttpServletRequest request);
    

	Integer countCommentForVideo(Integer videoId, Integer userId,Integer masterId);

    /**
     * 	根据视频id加载评论列表，以及评论人信息
     * @param videoIds
     * @param limit
     * @return
     */
    List<GcVideoComment> getVideoComments(List<Integer> videoIds,Integer masterId);

    /**
     * 根据主评论id查询下级评论
     * @return
     */
    List<GcVideoComment> selectCommentByMainCommentId(Integer mainId);

    boolean insertComment(GcVideoComment gcVideoComment);

    Integer deleteVideoComment(Integer commentId,Integer userId,Integer masterId);
}
