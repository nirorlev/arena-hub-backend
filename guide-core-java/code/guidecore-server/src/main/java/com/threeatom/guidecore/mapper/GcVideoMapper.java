package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.controller.user.vo.videoLongVo;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.DbAnalyticsResultVideoIdDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.VideoListFilterDto;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.StudentInfoVO;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface GcVideoMapper extends BaseMapper<GcVideo> {

    GcVideo selectVideoByid(Integer id);

    List<GcVideo> selectVideoListBySubIds(List<Integer> subIds);

    List<GcVideo> selectVideosOneLevelSubIds(List<Integer> subIds);

    List<GcVideo> selectVideosSubIds(List<Integer> videoIds);

    List<GcVideo> getVideoIdListBySubId0(List<Integer> subId);

    List<GcVideo> getVideoIdListByPermissionId(
            @Param("accessPermissionId") List<Integer> accessPermissionId);

    List<GcVideo> getVideoListByUserIdsAndSubjectId(
            @Param("userIdList") List<Integer> userIdList,
            @Param("subjectId") Integer subjectId,
            @Param("masterId") Integer masterId);

    List<GcVideo> selectVideosBySubIdsAndVideoName(
            @Param("subIds") List<Integer> subIds, @Param("videoName") String videoName);

    List<GcVideo> selectVideoListBySubId(Integer subId);

    List<GcVideo> selectVideoListByIds(List<Integer> vids);

    List<Integer> selectVideoSubIdsbyVids(List<Integer> vids);

    Integer selectSubIdByVid(Integer vid);

    List<GcVideo> selectVideoListByTopSubIds(@Param("subIds") List<Integer> subIds);

    List<GcVideo> selectVideoAndEventByTopSubIds(@Param("subIds") List<Integer> subIds);

    Map<String, Object> selectVideoTopicSubjectInfo(Integer vid);

    List<GcVideo> getMasterVideoListByFuzzyName(Integer masterId, String videoName, Integer userId);

    List<GcVideo> getFuzzyNameVideoInMaster(Integer masterId, String videoName);

    List<GcVideo> selectLikeVideoByUserId(Integer userId, Integer masterId);

    List<GcVideo> selectSeenVideoListByTopSubIds(@Param("subIds") List<Integer> subIds, Integer uid);

    Integer countVideoNameInSub0(GcVideo v);

    Long sumVideoLongByIdUser(
            @Param("videoIds") List<Integer> videoIds, @Param("userId") Integer userId);

    Long sumVideoLong(@Param("videoIds") List<Integer> videoIds);

    List<videoLongVo> sumVideoLongBySubId(@Param("subIds") List<Integer> subIds);

    Long sumPlayVideoLongByIdUser(
            @Param("videoIds") List<Integer> videoIds, @Param("userId") Integer userId);

    List<GcVideo> pageVideo(Map<String, Object> params);

    List<GcVideo> selectVideoByVideoAndSub0NameIndex(
            String videoNameIndex, String subNameIndex, Integer masterId);

    Integer countVideosBySubId(@Param("ids") List<Integer> ids);

    List<GcVideo> selectVideoInfoBySubId(@Param("list") List<Integer> list);

    Integer selectLastVideoIdBySubjectId(Integer subId, Integer userId, Integer masterId);

    List<StudentInfoVO> getStudentSubTimeNum(List<Integer> videoList, List<Integer> userIdList);

    List<Integer> getVideoIdListBySubId(@Param("subIds") List<Integer> subId);

    List<GcVideo> getVideoListBySubId(@Param("subIds") List<Integer> subId);

    List<GcVideo> getVideoLongListByVideoId(@Param("videoIds") List<Integer> videoIds);

    Integer countVideoNumInPortal(
            @Param("masterId") Integer masterId,
            @Param("type") Integer type,
            @Param("state") Integer state,
            @Param("subIds") List<Integer> subIds,
            @Param("managerId") Integer managerId);

    List<GcVideo> getSysFileByIdsOrVideos(
            @Param("fileList") List<Integer> fileList, @Param("videoList") List<Integer> videoList);

    List<Integer> getIdsBySubIds(@Param("subIds") List<Integer> subIds);

    List<GcVideo> selectVideoPlayListBySubId(
            @Param("subId") Integer subId, @Param("userId") Integer userId);

    GcVideo selectVideoPlayByVideo(@Param("video") Integer video, @Param("userId") Integer userId);

    List<videoLongVo> getVideoLongMapBySubjectId(@Param("subjectIds") List<Integer> subjectIds);

    List<DbAnalyticsResultDto> getVideoCountAnalytics(
        @Param("filter") AnalyticsFilterDto filter,
        @Param("masterId") Integer masterId);

    List<DbAnalyticsResultDto> getTrendVideoCountAnalytics(
        @Param("filter") AnalyticsFilterDto filter,
        @Param("masterId") Integer masterId);

    List<GcVideo> getVideoListByQuery(
        @Param("filter") VideoListFilterDto filter, @Param("masterId") Integer masterId);

    List<DbAnalyticsResultVideoIdDto> getLikesByVideoAnalytics(
        @Param("filter") AnalyticsFilterDto filter, @Param("masterId") Integer masterId);

    GcVideo getVideoContentByFileId(@Param("fileId") Integer fileId);

    List<GcVideo> findByVideoIds(@Param("videoIds") List<Integer> videoIds);
}
