package com.threeatom.guidecore.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserVideoPlay;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.excel.vo.StudentBehaviorDataExcel;

/**
 * <p>
 * 用户对视频的播放记录 Mapper 接口
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
@Component
public interface GcUserVideoPlayMapper extends BaseMapper<GcUserVideoPlay> {

/*
    List<Map<String ,Object>> selectGetNewVideoListByUserIds(@Param("userIds") List<Integer> userIds);
*/

    /*
     *//***
     * 获取这个教师权限组下的合法视频的播放记录
     * @param subIds
     * @param userIds
     * @return
     *//*
    /**
     * 
     * 获取用户对于这个课程访问的合法视频播放时长
     * @param subIds
     * @param userIds
     * @return
     */
    /*
     */
    List<Map<String,Object>> selectVideosPlayTimeByUserIdsAndSubIds(@Param("subIds") List<Integer> subIds,@Param("userIds") List<Integer> userIds,@Param("order") String order);


    List<Map<String,Object>> selectVideoWatchedDataByUserIdsAndSubIds(@Param("subIds") List<Integer> subIds,@Param("userIds") List<Integer> userIds,@Param("order") String order);

    List<GcVideo> selectVideosPlayHistoryBySubIds(Integer subId,Integer userId,Integer masterId);

    /**
     * 查询视频观看历史的时间段
     * @param vid
     * @return
     */
    List<GcUserVideoPlay> selectVideosPlayHistoryInterval(Integer vid,Integer userId,Integer masterId );

    /**
     * 统计所有科目或单科的播放次数
     * @param subIds
     * @param userIds
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String,Object>> showAllSubjectVideoPlayHistoryNum(@Param("subIds") List<Integer> subIds,@Param("userIds") List<Integer> userIds,Integer masterId,String startTime,String endTime);


    List<StudentBehaviorDataExcel> showAllSubjectVideoWatchNum(@Param("subIds") List<Integer> subIds, @Param("userIds") List<Integer> userIds, Integer masterId);


    /**
     * 查询每一科的播放次数
     * @param subIds
     * @param userIds
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String,Object>> showVideoPlayHistoryNumByStudentIdsAndSubject(@Param("subIds") List<Integer> subIds,@Param("userIds") List<Integer> userIds,Integer masterId,String startTime,String endTime);

    List<GcSubject> getWatchedSubject(@Param("subIds") List<Integer> subIds, Integer userId,Integer masterId);

    List<GcSubject> getWatchedSubjectById(@Param("subIds")List<Integer> IdList,Integer userId,Integer masterId);

    List<GcUserVideoPlay> getVideoPlayByVideoId(Integer vid);

    List<GcUserVideoPlay> getVideoPlayNowTimeByVidsAndGroupByVid(@Param("vids") List<Integer> vids,Integer userId, Integer masterId);

    List<GcUser> getWatchCompletedStudentBySubject(Integer masterId, Integer subId);

    List<GcUser> getWatchCompletedStudentBySubjectList(Integer masterId, List<Integer> subIds);

    List<GcUser> getWatchCompletedStudentByTwoSubjectList(Integer masterId,List<Integer> subIds);

    List<Integer> selectUserVideoPlay(Integer userId,Integer portalId);


    /**
     *  	根据课程id和userid查询对应的视频播放记录
     * @param ids
     * @param userId
     * @return
     */
    @MapKey("subject_id")
    Map<Integer, List<GcUserVideoPlay>> findVideoPlayBySubject(@Param("ids") List<Integer> ids, @Param("userId") Integer userId);

    /**
     * 根据视频id，用户id查询视频播放状态，空间id暂时不需要
     *
     * @param videoId
     * @param userId
     * @return
     */
    GcUserVideoPlay findVideoPalyStateByVideoUser(@Param("videoId") Integer videoId, @Param("userId") Integer userId);

    /**
     * 根据视频id，用户id查询视频播放状态，空间id暂时不需要
     *
     * @param videoId
     * @param userId
     * @return
     */
    @MapKey("videoId")
    Map<Integer, GcUserVideoPlay> findVideoPalyStateByVideos(@Param("videoIds") List<Integer> videoIds, @Param("userId") Integer userId,@Param("masterId")Integer masterId);

    List<GcUserVideoPlay> findVideoPalyStateByVideosUsers(@Param("videoIds")List<Integer> videoIds,@Param("userIdList")List<Integer> userIdList,@Param("masterId")Integer masterId);
    /**
     * 根据用户idList获取播放视频id
     * @param ids
     * @return
     */
    List<Integer> getVideoIdsByUserIds(List<Integer> ids,Integer masterId);

    @MapKey("subId")
    Map<Integer,Object> getLastVideoPlayList(@Param("list") List<Integer> list,@Param("userId") Integer userId,@Param("masterId") Integer masterId);

    List<GcUserVideoPlay> getUserVideoPlayInfo(@Param("videoIds") List<Integer> videoIds);

    List<GcUserVideoPlay> getUserVideoPlayList(@Param("list") List<Integer> videoIds,@Param("userList") List<Integer> userIdList,@Param("masterId") Integer masterId);

    @MapKey("videoId")
    Map<Integer,Object> getVideoPlayCount(@Param("videoIds") List<Integer> videoIds);

    List<GcUserVideoPlay> getLearningRecords(@Param("sub0Id")Integer sub0Id,@Param("sub1Id")Integer sub1Id,@Param("videoId")Integer videoId,@Param("userId")Integer userId,@Param("masterId")Integer masterId);
    }
