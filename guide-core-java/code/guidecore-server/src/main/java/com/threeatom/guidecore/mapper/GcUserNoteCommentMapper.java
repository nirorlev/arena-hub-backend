package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.controller.user.vo.UserNoteCommentVo;
import com.threeatom.guidecore.entity.GcUserNoteComment;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author huangpei
 * @title: GcUserNodeCommentMapper
 * @projectName guidecore
 * @description: TODO
 * @date 2021/10/27/02714:55
 */
public interface GcUserNoteCommentMapper extends BaseMapper<GcUserNoteComment> {

    List<UserNoteCommentVo> selectUserNoteComment(
            @Param("eventId") Integer eventId,
            @Param("targetUserId") Integer targetUserId,
            @Param("masterId") Integer masterId);

    Integer selectCommentNum(
            @Param("eventId") Integer eventId,
            @Param("targetUserId") Integer targetUserId,
            @Param("masterId") Integer masterId);

    List<GcUserNoteComment> selectCommentNumList(
            @Param("eventId") Integer eventId,
            @Param("targetUserIdList") List<Integer> targetUserIdList,
            @Param("masterId") Integer masterId);
}
