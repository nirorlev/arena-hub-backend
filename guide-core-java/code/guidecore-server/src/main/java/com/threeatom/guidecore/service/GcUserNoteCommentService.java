package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.controller.user.vo.UserNoteCommentVo;
import com.threeatom.guidecore.entity.GcUserNoteComment;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface GcUserNoteCommentService extends IService<GcUserNoteComment> {

    List<UserNoteCommentVo> selectNoteComment(
            Integer eventId, Integer targetUserId, HttpServletRequest request);

    Integer selectCommentNum(Integer eventId, Integer targetUserId, Integer masterId);

    List<GcUserNoteComment> selectCommentNumList(
            Integer eventId, List<Integer> targetUserId, Integer masterId);
}
