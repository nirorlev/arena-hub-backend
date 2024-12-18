package com.threeatom.guidecore.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcEventMapper;
import com.threeatom.guidecore.mapper.GcUserEventMapper;
import com.threeatom.guidecore.service.*;
import com.threeatom.guidecore.util.I18NUtil;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GcEventServiceImpl extends ServiceImpl<GcEventMapper, GcEvent>
        implements GcEventService {

    @Autowired private GcVideoService videoService;

    @Autowired private GcUserEventService gcUserEventService;

    @Autowired private GcUserEventMapper gcUserEventMapper;

    @Autowired @Lazy private GcUserEventResourceService userEventResourceService;

    @Autowired @Lazy private GcGroupService gcGroupService;

    @Autowired private GcUserAccessService gcUserAccessService;

    @Override
    @Transactional
    public boolean saveEvent(GcEvent event, Integer masterId) {

        try {
            ArrayList<GcUserEvent> userEvents = new ArrayList<>();
            // 校验数据
            Integer vid = event.getVideoId();
            Integer eid = event.getId();
            GcVideo video = videoService.getById(vid);
            if (video == null) throw new SystemException(I18NUtil.get("video.empty"));

            if (event.getEventType().equals(2)) {
                event.setExt(null);
            }

            if (event.getEventType().equals(3)) {
                if (event.getOtherQuesImgId() == null || event.getOtherQuesImgId().equals(0))
                    throw new SystemException(I18NUtil.get("file.upload"));

                Integer quesImgId = event.getOtherQuesImgId();
                JSONObject object = new JSONObject();
                object.put("quesImgId", quesImgId);
                event.setExt(object.toJSONString());
            }
            this.saveOrUpdate(event);

            // 上传人是老师时
            if (event.getUploadType() != null && event.getUploadType() == 2) {
                List<Integer> userIds = new ArrayList<>();
                if (Objects.nonNull(event.getClassId())) {
                    GcGroup group = gcGroupService.getById(event.getClassId());
                    List<Integer> userAccessIds = group.getGroupAccessIds().toJavaList(Integer.class);
                    List<GcUserAccess> userAccessList = gcUserAccessService.listByIds(userAccessIds);
                    userIds =
                            userAccessList.stream().map(GcUserAccess::getUserId).collect(Collectors.toList());
                } else {
                    userIds = event.getUserIds();
                }

                for (Integer userId : userIds) {
                    userEvents.add(new GcUserEvent(userId, event.getId(), masterId));
                }
                if (eid != null) {
                    gcUserEventService.updateUserEvents(eid, userIds, masterId);
                } else {
                    gcUserEventMapper.insertBatch(userEvents);
                }
            }

        } catch (SystemException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteEventByVid(Integer vid) {
        QueryWrapper<GcEvent> queryWrapper = new QueryWrapper<GcEvent>();
        queryWrapper.eq("video_id", vid);
        return this.remove(queryWrapper);
    }

    @Override
    public GcEvent getEventById(Integer eventId, Integer userId) {
        return this.baseMapper.getEventById(eventId, userId);
    }

    @Override
    public List<GcEvent> getEventListByVid(Integer vid, Integer masterId) {
        Integer uplpadType = TableConstant.COMMON_ONE;
        return this.baseMapper.selectGetEventListByVid(vid, masterId, uplpadType);
    }

    @Override
    public List<GcEvent> selectGetEventListByVid(Integer vid, Integer userId, Integer masterId) {
        Integer type = TableConstant.COMMON_ONE;
        return this.baseMapper.selectEventListByVid(vid, userId, type, masterId);
    }

    @Override
    public JSONArray getEventListAndSelfAnswerByVid(Integer vid, Integer userId) {
        List<GcEvent> eventList = this.baseMapper.selectGetEventsByVideoId(vid, userId);
        List<GcEvent> answerList = this.baseMapper.selectGetEventListAndSelfAnswerByVid(vid, userId);
        JSONArray reList = new JSONArray();
        for (GcEvent event : eventList) {
            List<GcEvent> newEvent =
                    answerList.stream()
                            .filter(a -> a.getId().equals(event.getId()))
                            .collect(Collectors.toList());
            if (newEvent.size() > 0) reList.add(newEvent.get(0));
            else reList.add(event);
            if (event.getUser() == null) {
                Integer ifUploadByYou = TableConstant.COMMON_ZERO;
                event.setIfUploadByYourself(ifUploadByYou);
            } else if (userId.equals(event.getUser().getId())) {
                Integer ifUploadByYourSelf = TableConstant.COMMON_ONE;
                event.setIfUploadByYourself(ifUploadByYourSelf);
            }

            List<GcUserEventResource> userEventResourceList =
                    userEventResourceService.getEventResourceByEventIdAndUserId(event.getId(), userId);
            event.setAnswerNum(userEventResourceList.size());
        }

        return reList;
    }

    @Override
    public List<GcEvent> selectGetEventListAndSelfAnswerByVidFull(
            Integer vid, Integer userId, Integer masterId) {
        List<GcEvent> list =
                this.baseMapper.selectGetEventListAndSelfAnswerByVidFull(vid, userId, masterId);
        List<Integer> eventIds = list.stream().map(GcEvent::getId).collect(Collectors.toList());
        Map<Integer, Object> events = new HashMap<>();
        if (eventIds.size() != 0) {
            events = gcUserEventService.selectUserEvents(eventIds);
        }
        if (list.size() != 0) {
            Map<Integer, Object> finalEvents = events;
            list.forEach(
                    i -> {
                        if (Objects.isNull(userId)) {
                            i.setMyAnswer(null);
                        }
                        Map map = (Map) finalEvents.get(i.getId());
                        if (null != map) {
                            List<Integer> userLists =
                                    Arrays.stream(
                                                    Arrays.asList(map.get("userList").toString().split(",")).stream()
                                                            .mapToInt(Integer::parseInt)
                                                            .toArray())
                                            .boxed()
                                            .collect(Collectors.toList());
                            i.setUserIds(userLists);
                        }
                    });
        }
        return list;
    }

    @Override
    public boolean deleteEventByVids(List<Integer> vids) {
        QueryWrapper<GcEvent> queryWrapper = new QueryWrapper<GcEvent>();
        queryWrapper.in("video_id", vids);
        return this.remove(queryWrapper);
    }

    @Override
    public List<Integer> getEventSubIdsByEventId(List<Integer> eids) {
        return this.baseMapper.selectEventSubIdsByEventId(eids);
    }

    @Override
    public Integer getEventSubIdByEventId(Integer eid) {
        return this.baseMapper.selectSubIdByEventId(eid);
    }

    @Override
    public List<Integer> getEventIdsByVideoId(Integer vid, Integer uid) {
        return this.baseMapper.selectGetEventIdsByVideoId(vid, uid);
    }

    @Override
    public List<GcEvent> getEventListByVideoIds(List<Integer> videoIds, Integer uid) {
        return this.baseMapper.getEventListByVideoIds(videoIds, uid);
    }

    @Override
    public List<GcEvent> findEventAnswerByVideoIdsUser(
            List<Integer> videoIds, Integer userId, Integer masterId, Integer envFlag) {
        return this.baseMapper.findEventAnswerByVideoIdsUser(videoIds, userId, masterId, envFlag);
    }

    @Override
    public List<GcEvent> findEventUserByVideoIdsUser(
            List<Integer> videoIds, Integer userId, Integer masterId, Integer envFlag) {
        return this.baseMapper.findEventUserByVideoIdsUser(videoIds, userId, masterId, envFlag);
    }

    @Override
    public List<GcEvent> selectEventsByUploader(Integer uid) {
        return this.list(new QueryWrapper<GcEvent>().eq("upload_user", uid));
    }

    @Override
    public List<Integer> getEventIdsByMasterId(Integer masterId) {
        // TODO Auto-generated method stub
        return this.baseMapper.selectEventIdsByMasterId(masterId);
    }

    @Override
    public Map<Integer, Object> videoEventsAnswerNumMap(
            Integer vid, Integer userId, Integer masterId) {
        return this.baseMapper.videoEventsAnswerNumMap(vid, userId, masterId);
    }

    @Override
    public Integer countEventByVideoIds(List<Integer> videoList) {
        return this.baseMapper.countEventByVideoIds(videoList);
    }

    @Override
    public List<GcEvent> findEventAnswerByVideoIdsUserList(
            List<Integer> videoIds, List<Integer> userId, Integer masterId) {
        return this.baseMapper.findEventAnswerByVideoIdsUserList(videoIds, userId, masterId);
    }

    @Override
    public List<GcEvent> getEventNumByVideos(List<Integer> videoList, Integer masterId) {
        return this.baseMapper.getEventNumByVideos(videoList, masterId);
    }

    @Override
    public List<GcEvent> selectEventByUserIdAndSubjectId(
            Integer userId, Integer subjectId, Integer masterId) {
        return this.baseMapper.selectEventByUserIdAndSubjectId(userId, subjectId, masterId);
    }

    @Override
    public List<GcEvent> selectEventBySubjectIdUserIds(
            List<Integer> userIds, Integer subjectId, Integer masterId) {
        return this.baseMapper.selectEventBySubjectIdUserIds(userIds, subjectId, masterId);
    }
}
