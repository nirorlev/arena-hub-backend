package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserVideoPlay;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.excel.vo.StudentBehaviorDataExcel;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface GcUserVideoPlayMapper extends BaseMapper<GcUserVideoPlay> {

    List<Map<String, Object>> selectVideosPlayTimeByUserIdsAndSubIds(@Param("subIds") List<Integer> subIds,
                                                                     @Param("userIds") List<Integer> userIds,
                                                                     @Param("order") String order);


    List<Map<String, Object>> selectVideoWatchedDataByUserIdsAndSubIds(@Param("subIds") List<Integer> subIds,
                                                                       @Param("userIds") List<Integer> userIds,
                                                                       @Param("order") String order);

    List<GcVideo> selectVideosPlayHistoryBySubIds(Integer subId, Integer userId, Integer masterId);

    List<GcUserVideoPlay> selectVideosPlayHistoryInterval(Integer vid, Integer userId, Integer masterId);

    List<Map<String, Object>> showAllSubjectVideoPlayHistoryNum(@Param("subIds") List<Integer> subIds,
                                                                @Param("userIds") List<Integer> userIds,
                                                                Integer masterId, String startTime, String endTime);


    List<StudentBehaviorDataExcel> showAllSubjectVideoWatchNum(@Param("subIds") List<Integer> subIds,
                                                               @Param("userIds") List<Integer> userIds,
                                                               Integer masterId);


    List<Map<String, Object>> showVideoPlayHistoryNumByStudentIdsAndSubject(@Param("subIds") List<Integer> subIds,
                                                                            @Param("userIds") List<Integer> userIds,
                                                                            Integer masterId, String startTime,
                                                                            String endTime);

    List<GcSubject> getWatchedSubject(@Param("subIds") List<Integer> subIds, Integer userId, Integer masterId);

    List<GcSubject> getWatchedSubjectById(@Param("subIds") List<Integer> IdList, Integer userId, Integer masterId);

    List<GcUserVideoPlay> getVideoPlayByVideoId(Integer vid);

    List<GcUserVideoPlay> getVideoPlayNowTimeByVidsAndGroupByVid(@Param("vids") List<Integer> vids, Integer userId,
                                                                 Integer masterId);

    List<GcUser> getWatchCompletedStudentBySubject(Integer masterId, Integer subId);

    List<GcUser> getWatchCompletedStudentBySubjectList(Integer masterId, List<Integer> subIds);

    List<GcUser> getWatchCompletedStudentByTwoSubjectList(Integer masterId, List<Integer> subIds);

    List<Integer> selectUserVideoPlay(Integer userId, Integer portalId);


    @MapKey("subject_id")
    Map<Integer, List<GcUserVideoPlay>> findVideoPlayBySubject(@Param("ids") List<Integer> ids,
                                                               @Param("userId") Integer userId);

    GcUserVideoPlay findVideoPalyStateByVideoUser(@Param("videoId") Integer videoId, @Param("userId") Integer userId);

    @MapKey("videoId")
    Map<Integer, GcUserVideoPlay> findVideoPalyStateByVideos(@Param("videoIds") List<Integer> videoIds,
                                                             @Param("userId") Integer userId,
                                                             @Param("masterId") Integer masterId);

    List<GcUserVideoPlay> findVideoPalyStateByVideosUsers(@Param("videoIds") List<Integer> videoIds,
                                                          @Param("userIdList") List<Integer> userIdList,
                                                          @Param("masterId") Integer masterId);

    List<Integer> getVideoIdsByUserIds(List<Integer> ids, Integer masterId);

    @MapKey("subId")
    Map<Integer, Object> getLastVideoPlayList(@Param("list") List<Integer> list, @Param("userId") Integer userId,
                                              @Param("masterId") Integer masterId);

    List<GcUserVideoPlay> getUserVideoPlayInfo(@Param("videoIds") List<Integer> videoIds);

    List<GcUserVideoPlay> getUserVideoPlayList(@Param("list") List<Integer> videoIds,
                                               @Param("userList") List<Integer> userIdList,
                                               @Param("masterId") Integer masterId);

    @MapKey("videoId")
    Map<Integer, Object> getVideoPlayCount(@Param("videoIds") List<Integer> videoIds);

    List<GcUserVideoPlay> getLearningRecords(@Param("sub0Id") Integer sub0Id, @Param("sub1Id") Integer sub1Id,
                                             @Param("videoId") Integer videoId, @Param("userId") Integer userId,
                                             @Param("masterId") Integer masterId);
}
