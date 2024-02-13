package com.threeatom.guidecore.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.controller.user.vo.videoLongVo;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.StudentInfoVO;
import com.threeatom.guidecore.service.bll.GcVideoServiceBll;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Async;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-11
 */
public interface GcVideoService extends GcVideoServiceBll {

    List<GcVideo> getVideoListBySubIds(List<Integer> subIds);

    List<GcVideo> getVideoListByTopSubIds(List<Integer> subIds);

    List<GcVideo> getVideoListBySubId(Integer subId);

    GcVideo getVideoById(Integer vid);

    boolean saveVideo(GcVideo video);

    boolean deleteVideo(Integer vid);

    boolean deleteVideoBySubIds(List<Integer> subIds);

    int getVideoNum(Integer masterId, List<Integer> subIds, Integer managerId);

    GcVideo callbackSaveVideo(JSONObject object);

    Integer getSubIdByVid(Integer vid);

    boolean changeVideoOrder(List<Integer> videoIds);

    SysFile unifiedFileSave(JSONObject jsonObject);

    GcMaster callbackSaveMasterVideo(JSONObject jsonObject);

    List<GcVideo> selectLikeVideoByUserId(Integer userId, Integer masterId);

    List<GcVideo> getFuzzyNameVideoInMaster(Integer masterId, String videoName);

    /**
     * 根据视频id查询总时长
     */
    Long sumVideoLongByIdUser(List<Integer> videoIds, Integer userId);

    /**
     * 	根据课程id查询对应的视频信息，同时加载出评论数、点赞数、问题数、时长等信息
     * @param subjectIds
     * @return
     */
    Message getVideosBySubIds(
            Integer subjectIds, Map<String, Object> params, SysSystem sys, HttpServletRequest request);

    /**
     * 根据视频id和用户id查询播放的时长
     * @param videoIds
     * @param userId
     * @return
     */
    Long sumPlayVideoLongByIdUser(List<Integer> videoIds, int userId);

    /**
     * 根据课程id查询视频是否完成，根据 播放进度和问题回答数
     * @param subjectIds
     * @return
     */
    //	Map<Integer, List<GcVideo>> getVideoCompleteStatusBySubject(List<Integer> subjectIds, int
    // userId);

    /**
     * 根据一级课id集合查询视频并返回二级课程id
     * @param subjectIds
     * @param userId
     * @return
     */
    List<GcVideo> getVideosBySubjectIds0(
            List<Integer> subjectIds,
            Integer userId,
            Integer masterId,
            HttpServletRequest request,
            Integer envFlag);

    List<GcVideo> getVideoIdListBySubId0(
            List<Integer> subIds,
            Integer userId,
            Integer masterId,
            HttpServletRequest request,
            Integer envFlag);

    List<GcVideo> getVideoIdListByAccessId0(
            List<Integer> accessPermissionId,
            List<Integer> userId,
            Integer masterId,
            HttpServletRequest request);

    List<GcVideo> getVideoListByUserIdAndSubject(
            List<Integer> userId, Integer subjectId, Integer masterId, HttpServletRequest request);

    /**
     * 分页查询视频
     * @param params
     * @param request
     * @return
     */
    PageInfo<GcVideo> page(Map<String, Object> params, SysSystem sys, HttpServletRequest request);

    List<GcVideo> selectVideoByVideoAndSub0NameIndex(
            String videoName, String subName, Integer masterId);

    List<GcVideo> selectVideoInfoBySubId(List<Integer> subId);

    List<GcVideo> buildVideoInfo(
            Integer userId,
            SysSystem sys,
            List<GcVideo> gcVideos,
            Integer masterId,
            HttpServletRequest request,
            Integer envFlag);

    @Async
    void asyncMethodSaveVideo(GcVideo video, HttpServletRequest request);

    @Async
    void asyncMethodUpdateVideo(GcVideo video, HttpServletRequest request, SysSystem system);

    List<StudentInfoVO> getStudentSubTimeNum(List<Map<String, Object>> mapList, Integer masterId);

    List<Integer> getVideoIdListBySubId(List<Integer> subIds);

    List<GcVideo> getVideoListBySubId(List<Integer> subIds);

    List<GcVideo> getVideoLongListByVideoId(List<Integer> subIds);

    List<GcVideo> buildVideoInfoByList(
            List<Integer> userIdList,
            List<GcVideo> gcVideos,
            Integer masterId,
            Boolean isAccessId,
            List<Integer> permissionList,
            HttpServletRequest request);

    List<GcVideo> getSysFileByIdsOrVideos(List<Integer> fileList, List<Integer> videoList);

    List<GcVideo> selectVideoPlayListBySubId(Integer subId, Integer userId);

    GcVideo selectVideoPlayByVideo(Integer video, Integer userId);

    boolean saveVideoInfo(SysSystem sys, GcVideo video, Integer masterId, HttpServletRequest request);

    List<Integer> getIdsBySubIds(List<Integer> subIds);

    Map<Integer, videoLongVo> getVideoLongMapBySubjectId(List<Integer> subjectIds);
}
