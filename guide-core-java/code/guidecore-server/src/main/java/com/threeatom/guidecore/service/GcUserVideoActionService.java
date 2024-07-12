package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户对视频的操作，点赞 或者 收藏 等等  服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
public interface GcUserVideoActionService extends IService<GcUserVideoAction> {

    boolean saveVideoAction(Integer vid, Integer userId, Integer type);

    List<Integer> getVideoLikeNumsByVideoIds(List<Integer> videoIds);

    GcUserVideoAction getOldVideoAction(Integer vid, Integer userId, Integer type);

    GcUserVideoAction getOldChannelVideoAction(Integer contentId, Integer userId, Integer type);

    boolean deleteOldVideoAction(Integer vid, Integer userId, Integer type);

    boolean deleteChannelOldVideoAction(Integer contentId, Integer userId, Integer type);

    List<GcUserVideoAction> getVideoActionListByVidAndUserId(Integer vid, Integer userId);

    GcUserVideoAction getFileActionListByFileIdAndUserId(Integer contentId, Integer userId);

    List<GcUserVideoAction> getVideoActionListByFildId(List<Integer> contentIds, Integer userId);

    List<GcUserVideoAction> getVideoActionListByUserId(Integer userId);

    List<Map<String, Object>> countTypeRateForVideo(Integer videoId, Integer type);

    /**
     * 根据课程id查询评论、点赞、星级评价
     * @param subjectIds
     * @return
     */
    Map<Integer, List<GcUserVideoAction>> getVideoActionBySubject(Map<String, Object> params);

    /**
     * 根据课程id，类型查询星级评价平均值和评论人数
     * @param videoParams
     * @return
     */
    Map<Integer, GcUserVideoAction> getSubjectUserStar(Map<String, Object> videoParams);

    Map<Integer, GcUserVideoAction> gvggetSubjectUserStar(Map<String, Object> videoParams);

    Integer countLikeForVideo(Integer videoId);

    Integer countLikeForFile(Integer fileId);

    List<GcUserVideoAction> countLikeForFiles(List<Integer> fileId);

    GcUserVideoAction getActionByAction(GcUserVideoAction userVideoAction);
}
