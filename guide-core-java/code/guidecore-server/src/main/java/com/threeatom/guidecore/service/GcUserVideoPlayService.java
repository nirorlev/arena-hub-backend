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

/**
 * <p>
 * 用户对视频的播放记录 服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
public interface GcUserVideoPlayService extends IService<GcUserVideoPlay> {

    /***
     * 查找用户集合和课程集合下面的视频播放总时长
     * @param subIds 课程集合，限定一个课程集合
     * @param userIds
     * @return
     */
    /*    */

    /**
     * 新增视频播放记录以及其下的节点
     * @param videoPlay
     * @return
     */
    GcUserVideoPlay saveVideoPlayAndVideoPlaysNode(GcUserVideoPlay videoPlay);

    Integer getVideoPlayTime(List<GcUserVideoPlaysNode> videoPlaysNodes);

    List<GcUserVideoPlay> getVideoPlayByVideoId(Integer vid);

    List<Integer> getWatchCompletedStudentBySubject(Integer masterId, List<Integer> subIds);

    JSONArray getSubAndVideoPlayListForHome(
            GcSubject subject, SysSystem sys, HttpServletRequest request);

    /**
     *  根据视频id，用户id查询视频播放状态，空间id暂时不需要
     * @param subjectId
     * @param videoId
     * @param userId
     * @return key 视频id
     */
    Map<Integer, GcUserVideoPlay> findVideoPalyStateByVideos(
            List<Integer> videoIds, Integer userId, Integer masterId);

    Map<Integer, Object> getLastVideoPlayList(List<Integer> subId, Integer userId, Integer masterId);

    List<GcUserVideoPlay> findVideoPalyStateByVideosUsers(
            List<Integer> videoIds, List<Integer> userIdList, Integer masterId);

    List<GcUserVideoPlay> getUserVideoPlayList(
            List<Integer> videoIds, List<Integer> userIdList, Integer masterId);

    Map<Integer, Object> getVideoPlayCount(List<Integer> videoIds);
}
