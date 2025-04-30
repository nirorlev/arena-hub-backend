package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcEvent;
import com.threeatom.guidecore.entity.StudentInfoVO;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.MapKey;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface GcEventMapper extends BaseMapper<GcEvent> {

    GcEvent getEventById(Integer id, Integer userId);

    List<GcEvent> selectGetEventListByVid(Integer vid, Integer masterId, Integer type);

    List<GcEvent> selectEventListByVid(Integer vid, Integer userId, Integer type, Integer masterId);

    List<GcEvent> selectGetEventListAndSelfAnswerByVid(Integer vid, Integer userId);

    List<GcEvent> selectGetEventListAndSelfAnswerByVids(
            @Param("vids") List<Integer> vids, Integer userId, Integer masterId);

    List<GcEvent> selectGetEventListAndSelfAnswerByVidFull(
            Integer vid, Integer userId, Integer masterId);

    List<GcEvent> getEventNumByAdmin(
            @Param("vids") List<Integer> vids, Integer userId, Integer masterId);

    List<GcEvent> getEventListByVideoIds(
            @Param("vids") List<Integer> vids, @Param("uid") Integer uid);

    List<Integer> selectEventSubIdsByEventId(List<Integer> videoIds);

    Integer selectSubIdByEventId(Integer eid);

    List<Integer> selectGetEventIdsByVideoId(Integer vid, Integer uid);

    List<GcEvent> selectGetEventsByVideoId(Integer vid, Integer uid);

    List<Integer> selectEventIdsByMasterId(Integer masterId);

    List<GcEvent> findEventAnswerByVideoIdsUser(
            @Param("videoIds") List<Integer> videoIds,
            @Param("userId") Integer userId,
            @Param("masterId") Integer masterId);

    List<GcEvent> findEventUserByVideoIdsUser(
            @Param("videoIds") List<Integer> videoIds,
            @Param("userId") Integer userId,
            @Param("masterId") Integer masterId,
            @Param("envFlag") Integer envFlag);

    List<GcEvent> findEventAnswerByVideoIds(
            @Param("videoIds") List<Integer> videoIds,
            @Param("userId") Integer userId,
            @Param("masterId") Integer masterId,
            @Param("envFlag") Integer envFlag);

    @MapKey("eventId")
    Map<Integer, Object> videoEventsAnswerNumMap(Integer vid, Integer userId, Integer masterId);

    Integer countEventByVideoIds(@Param("list") List<Integer> videoList);

    List<GcEvent> selectEventUploadByPortal(@Param("vid") Integer vid, @Param("type") Integer type);

    List<GcEvent> selectAllEventByTeacherId(
            @Param("masterId") Integer masterId,
            @Param("userId") Integer userId,
            @Param("subIds") List<Integer> subIds,
            @Param("order") String order,
            @Param("sub1Ids") List<Integer> sub1Ids,
            @Param("videoIds") List<Integer> videoIds);

    List<GcEvent> findEventAnswerByVideoIdsUserList(
            @Param("videoIds") List<Integer> videoIds,
            @Param("userIdList") List<Integer> userIdList,
            @Param("masterId") Integer masterId);

    List<GcEvent> getEventNumByVideos(
            @Param("videoIds") List<Integer> videoIds, @Param("masterId") Integer masterId);

    List<GcEvent> selectEventBySubjectIdUserIds(
            @Param("userList") List<Integer> userList,
            @Param("subjectId") Integer subjectId,
            @Param("masterId") Integer masterId);

    List<GcEvent> selectEventByUserIdAndSubjectId(
            @Param("userId") Integer userId,
            @Param("subjectId") Integer subjectId,
            @Param("masterId") Integer masterId);
}
