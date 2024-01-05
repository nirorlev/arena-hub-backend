package com.threeatom.guidecore.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcEvent;
import com.threeatom.guidecore.entity.StudentInfoVO;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 视频下的event 服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-18
 */
public interface GcEventService extends IService<GcEvent> {

    boolean saveEvent(GcEvent event,Integer masterId);

    boolean deleteEventByVid(Integer vid);

    boolean deleteEventByVids(List<Integer> vid);

    List<GcEvent> getEventListByVid(Integer vid,Integer masterId);

    List<GcEvent> selectGetEventListByVid(Integer vid,Integer userId,Integer masterId);

    List<Integer> getEventSubIdsByEventId(List<Integer> eids);

    public Integer getEventSubIdByEventId(Integer eid);

    JSONArray getEventListAndSelfAnswerByVid(Integer vid, Integer userId);

    List<Integer> getEventIdsByVideoId(Integer vid,Integer uid);
    
    List<Integer> getEventIdsByMasterId(Integer masterId);

    List<GcEvent> selectEventsByUploader(Integer uid);

	GcEvent getEventById(Integer eventId, Integer userId);

	List<GcEvent> getEventListByVideoIds(List<Integer> videoIds, Integer uid);
    
	 /**
     *  根据视频id集合和用户id
     *      查询问题的回答情况
     * @param videoIds
     * @param userId
     * @return
     */
	List<GcEvent> findEventAnswerByVideoIdsUser(List<Integer> videoIds, Integer userId,Integer masterId,Integer envFlag);

	List<GcEvent> findEventUserByVideoIdsUser(List<Integer> videoIds, Integer userId,Integer masterId,Integer envFlag);

	List<GcEvent> selectGetEventListAndSelfAnswerByVidFull(Integer vid, Integer userId,Integer masterId);

	Map<Integer, Object> videoEventsAnswerNumMap(Integer vid, Integer userId,Integer masterId);

    Integer countEventByVideoIds(List<Integer> videoList);
    List<GcEvent> findEventAnswerByVideoIdsUserList(List<Integer> videoIds, List<Integer> userId,Integer masterId);

    List<GcEvent> getEventNumByVideos(List<Integer> videoList,Integer masterId);

    List<GcEvent> selectEventByPermissionList(List<Integer> permissionList,List<Integer> userIdList,Integer masterId);

    List<GcEvent> selectEventBySubjectIdUserIds(List<Integer> userIds,Integer subjectId,Integer masterId);

    List<GcEvent> selectEventByUserIdAndSubjectId(Integer userId,Integer subjectId,Integer masterId);
}
