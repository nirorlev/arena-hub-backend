package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import java.util.List;
import java.util.Map;

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

    Map<Integer, List<GcUserVideoAction>> getVideoActionBySubject(Map<String, Object> params);

    Map<Integer, GcUserVideoAction> getSubjectUserStar(Map<String, Object> videoParams);

    Map<Integer, GcUserVideoAction> gvggetSubjectUserStar(Map<String, Object> videoParams);

    Integer countLikeForVideo(Integer videoId);

    Integer countLikeForFile(Integer fileId);

    List<GcUserVideoAction> countLikeForFiles(List<Integer> fileId);

    GcUserVideoAction getActionByAction(GcUserVideoAction userVideoAction);

    boolean isLikedByUser(Integer contentId, Integer userId);

    void updateReactions(Integer contentId, Integer userId, Map<String, Boolean> reactions);
}
