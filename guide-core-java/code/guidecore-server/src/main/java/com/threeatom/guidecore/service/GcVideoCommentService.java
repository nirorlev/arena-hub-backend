package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcVideoComment;
import com.threeatom.system.entity.SysSystem;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface GcVideoCommentService extends IService<GcVideoComment> {
    boolean saveVideoComment(GcVideoComment videoComment);

    List<GcVideoComment> getMyVideoCommentByVideoIds(List<Integer> videoIds, Integer userId);

    List<Integer> getCommentNumsByVideoIds(List<Integer> videoIds);

    List<GcVideoComment> getAllCommentByVideoId(Integer vid, Integer limit);

    List<GcVideoComment> getAllCommentByVideoIdAndUserId(
            Integer vid, Integer userId, Integer masterId);

    Message getCommentStream(
            Integer subId, GcUser user, GcSubject sub, SysSystem sys, HttpServletRequest request);

    Integer countCommentForVideo(Integer videoId, Integer userId, Integer masterId);

    List<GcVideoComment> getVideoComments(List<Integer> videoIds, Integer masterId);

    List<GcVideoComment> selectCommentByMainCommentId(Integer mainId);

    boolean insertComment(GcVideoComment gcVideoComment);

    Integer deleteVideoComment(Integer commentId, Integer userId, Integer masterId);
}
