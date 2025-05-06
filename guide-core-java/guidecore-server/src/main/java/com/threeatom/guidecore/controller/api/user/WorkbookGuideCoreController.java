package com.threeatom.guidecore.controller.api.user;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.GcEvent;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserEventResource;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.mapper.GcEventMapper;
import com.threeatom.guidecore.mapper.GcVideoMapper;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserEventResourceService;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.service.SysFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guidecore/workbook")
@RequiredArgsConstructor
@Api(tags = "作业本数据")
public class WorkbookGuideCoreController extends GuideCoreController {

    private final GcSubjectService gcSubjectService;
    private final GcUserEventResourceService gcUserEventResourceService;
    private final SysFileService sysFileService;
    private final GcVideoMapper videoMapper;
    private final GcEventMapper gcEventMapper;

    @PostMapping("/selectVideosInTopic")
    public Message selectVideosInTopic(@RequestBody GcSubject gcSubject, HttpServletRequest request) {
        Message message = new Message();
        GcUser user = new GcUser();
        if (Objects.nonNull(gcSubject.getCurrentStudentUserId())) {
            user.setId(gcSubject.getCurrentStudentUserId());
        } else {
            user = this.getGcUser();
        }
        if (Objects.isNull(gcSubject.getId())) {
            throw new SystemException(I18NUtil.get("powtoon.topic.error"));
        }
        List<Integer> subIds = new ArrayList<>();
        subIds.add(gcSubject.getId());
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcVideo> allVideos = videoMapper.selectVideoListByTopSubIds(subIds);
        if (CollectionUtils.isNotEmpty(allVideos)) {
            for (GcVideo gcVideo : allVideos) {
                gcVideo.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(gcVideo));
            }
            List<Integer> videoIds = allVideos.stream().map(GcVideo::getId).collect(Collectors.toList());
            List<GcEvent> eventList = gcEventMapper.getEventListByVideoIds(videoIds, user.getId());
            for (GcVideo gcVideo : allVideos) {
                for (GcEvent gcEvent : eventList) {
                    if (gcVideo.getEventList().size() < 10 && gcEvent.getVideoId().equals(gcVideo.getId())) {
                        gcVideo.getEventList().add(gcEvent);
                    }
                }
            }
            PageInfo<GcVideo> pageInfo = new PageInfo<>(allVideos);
            return message.ok().addData("allVideos", pageInfo);
        } else {
            PageInfo<GcVideo> gcVideoPageInfo = new PageInfo<>(allVideos);
            return message.ok().addData("allVideos", gcVideoPageInfo);
        }
    }

    @PostMapping("/selectEventsInVideo")
    public Message selectEventsInVideo(@RequestBody GcVideo gcVideo, HttpServletRequest request) {
        Message message = new Message();
        GcUser user = new GcUser();
        if (Objects.nonNull(gcVideo.getCurrentStudentUserId())) {
            user.setId(gcVideo.getCurrentStudentUserId());
        } else {
            user = this.getGcUser();
        }
        if (Objects.isNull(gcVideo.getId())) {
            throw new SystemException(I18NUtil.get("guidecore.video.detail.error"));
        }
        List<Integer> videoIds = new ArrayList<>();
        videoIds.add(gcVideo.getId());
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        } else {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcEvent> eventList = gcEventMapper.getEventListByVideoIds(videoIds, user.getId());
        PageInfo<GcEvent> pageInfo = new PageInfo<>(eventList);
        return message.ok().addData("eventList", pageInfo);
    }

    @ApiOperation(value = "作业本-单个话题的课程/视频/问题", httpMethod = "POST")
    @PostMapping("/topicContent")
    public Message topicContent(@RequestBody JSONObject jsonRequest, HttpServletRequest request) {
        Integer studentId = jsonRequest.getInteger("studentId"); // 老师需传，学生不用
        Integer subId = jsonRequest.getInteger("subId");
        GcUser user = this.getGcUser();
        List<GcSubject> list = new ArrayList();
        Map<String, Object> numMap = new HashMap();
        Map<String, Object> eventAnswerStateMap = new HashMap();
        if (studentId != null) {
            // 老师端
            list = gcSubjectService.getLevel1VideoEventList(subId, studentId, user.getId());
            numMap =
                gcSubjectService.selectEventResNumMapForWorkbookTeacher(
                    subId, studentId, user.getId()); // 资源回复数量
            eventAnswerStateMap =
                gcSubjectService.getAnswerMessageMapForTeacherWorkbook(
                    subId, studentId, user.getId()); // 问题回答状态
        } else {
            // 学生端
            list = gcSubjectService.getLevel1VideoEventList(subId, user.getId(), null);
            numMap = gcSubjectService.selectEventResNumMapForWorkbook(subId, user.getId()); // 资源回复数量
        }

        for (GcSubject subject : list) {
            for (GcVideo video : subject.getVideoChildList()) {
                for (GcEvent event : video.getEventList()) {
                    // 设置问题图片
                    if (event.getEventImageFile() != null) {
                        sysFileService.getResFullUrl(event.getEventImageFile(), request);
                    }
                    // 设置回复的资源数量
                    if (numMap.get(event.getId() + "_my") != null) {
                        event.setEventResMyNum(numMap.get(event.getId() + "_my"));
                    }
                    if (numMap.get(event.getId() + "_others_0") != null) {
                        event.setEventResOthersNumUnRead(numMap.get(event.getId() + "_others_0"));
                    }
                    if (numMap.get(event.getId() + "_others_1") != null) {
                        event.setEventResOthersNumRead(numMap.get(event.getId() + "_others_1"));
                    }

                    if (eventAnswerStateMap.get(event.getId()) != null) {
                        event.setEventAnswerState(eventAnswerStateMap.get(event.getId()));
                    }
                }
            }
        }

        return new Message().ok().addData("subVideoEventList", list);
    }

    @ApiOperation(value = "作业本事件回复内容-新", httpMethod = "POST")
    @PostMapping("/getEventResListForWorkBook")
    public Message getEventResListForWorkBook(
        @RequestBody JSONObject jsonRequest, HttpServletRequest request) {
        Integer studentId = jsonRequest.getInteger("studentId"); // 分享班级中分享方老师的userid
        Integer teacherUserId = jsonRequest.getInteger("teacherUserId");
        GcUser user = this.getGcUser();
        Integer masterId = request.getIntHeader("masterId");
        Integer eventId = jsonRequest.getInteger("eventId");
        ApiAssert.notNull(eventId, "事件id不可空");
        List<GcUserEventResource> list = new ArrayList<>();
        if (null != studentId) {
            list =
                gcUserEventResourceService.getEventResListForWorkBook(
                    eventId, studentId, studentId, masterId, request, null);
        } else {
            list =
                gcUserEventResourceService.getEventResListForWorkBook(
                    eventId, this.getGcUser().getId(), studentId, masterId, request, null);
        }
        PageInfo<GcUserEventResource> pageInfo = new PageInfo<>(list);
        return new Message().ok().addData("eventResList", pageInfo);
    }
}
