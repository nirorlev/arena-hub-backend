package com.threeatom.guidecore.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUserVideoPlay;
import com.threeatom.guidecore.entity.GcUserVideoPlaysNode;
import com.threeatom.system.entity.SysSystem;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface GcUserVideoPlayService extends IService<GcUserVideoPlay> {
    GcUserVideoPlay saveVideoPlayAndVideoPlaysNode(GcUserVideoPlay videoPlay);

    Integer getVideoPlayTime(List<GcUserVideoPlaysNode> videoPlaysNodes);

    List<GcUserVideoPlay> getVideoPlayByVideoId(Integer vid);

    List<Integer> getWatchCompletedStudentBySubject(Integer masterId, List<Integer> subIds);

    JSONArray getSubAndVideoPlayListForHome(
            GcSubject subject, SysSystem sys, HttpServletRequest request);

    Map<Integer, GcUserVideoPlay> findVideoPalyStateByVideos(
            List<Integer> videoIds, Integer userId, Integer masterId);

    Map<Integer, Object> getLastVideoPlayList(List<Integer> subId, Integer userId, Integer masterId);

    List<GcUserVideoPlay> findVideoPalyStateByVideosUsers(
            List<Integer> videoIds, List<Integer> userIdList, Integer masterId);

    List<GcUserVideoPlay> getUserVideoPlayList(
            List<Integer> videoIds, List<Integer> userIdList, Integer masterId);

    Map<Integer, Object> getVideoPlayCount(List<Integer> videoIds);
}
