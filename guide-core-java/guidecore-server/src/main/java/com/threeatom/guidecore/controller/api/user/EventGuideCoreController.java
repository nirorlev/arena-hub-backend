package com.threeatom.guidecore.controller.api.user;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.EnvType;
import com.threeatom.guidecore.constant.EventResType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.service.*;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import io.swagger.annotations.ApiOperation;
import java.util.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/guidecore/user")
public class EventGuideCoreController extends GuideCoreController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventGuideCoreController.class);

    @Autowired private GcUserAnswerService userAnswerService;
    @Resource private GcUserEventResourceService gcUserEventResourceService;
    @Autowired private GcEventService eventService;
    @Autowired private SysFileService sysFileService;
    @Autowired private GcUserEventResourceService userEventResourceService;
    @Autowired private GcMasterMessageService masterMessageService;
    @Autowired private GcUserService userService;
    @Autowired private GvgMasterService gvgMasterService;

    @ApiOperation(value = "回答问题获取答案", httpMethod = "GET")
    @GetMapping("/eventAnswerList/{eventId}")
    public Message eventAnswerList(
            @PathVariable("eventId") Integer eventId, HttpServletRequest request) {
        // test
        ApiAssert.notNull(eventId, "参数eventId缺失");
        GcUser user = this.getGcUser();
        GcEvent event = eventService.getEventById(eventId, user.getId());
        Integer masterId = request.getIntHeader("masterId");

        List<GcUserAnswer> eventAnswerList =
                userAnswerService.getAnswerListByEventId(eventId, null, masterId);

        for (GcUserAnswer eventAnswer : eventAnswerList) {
            SysFile userFile = new SysFile();
            userFile.setFileUrl(eventAnswer.getAvatarUrl());
            userFile.setSaveType(eventAnswer.getSaveType());
            if (eventAnswer.getAvatarUrl() != "")
                eventAnswer.setAvatarUrl(sysFileService.getResFullUrl(userFile, request));
        }

        List<GcUserEventResource> userEventResourceList =
                userEventResourceService.getEventResourceByEventIdAndUserId(eventId, user.getId());
        // 20200220修改
        if (userEventResourceList != null && userEventResourceList.size() > 0) {
            for (GcUserEventResource eventResource : userEventResourceList) {
                SysFile file = eventResource.getResFile();
                if (file != null) {
                    file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
                }
                // 2=video
                if (eventResource.getType() == EventResType.VIDEO_2) {
                    eventResource.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(file));
                }
            }
        }

        return new Message()
                .ok("访问成功")
                .addData("eventAnswerList", eventAnswerList)
                .addData("userEventResourceList", userEventResourceList)
                .addData("userId", user.getId())
                .addData("event", event);
    }

    @ApiOperation(value = "保存问题资源统一接口-新", httpMethod = "POST")
    @PostMapping("/saveEventResourceCommon")
    public Message saveEventResourceCommon(
            @RequestBody GcUserEventResource userEventResource, HttpServletRequest request) {
        ApiAssert.notNull(userEventResource.getEventId(), "eventId不可空");
        GcUser user = this.getGcUser();
        Integer masterId = getHeaderMasterId(request);
        userEventResource.setMasterId(masterId);
        userEventResource.setTimeNode(
                userEventResource.getTimeNode() == null ? -1 : userEventResource.getTimeNode());
        userEventResource.setUserId(user.getId());

        List<GcUserEventResource> userEventResourceList = new ArrayList<>();

        if (userEventResource.getTargetId() != null && userEventResource.getTargetId() > 0) {
            GcUserEventResource eventResource =
                    userEventResourceService.getById(userEventResource.getTargetId());
            userEventResource.setTargetUserId(eventResource.getUserId());
        } else if (null != userEventResource.getTargetUserIdList()
                && !userEventResource.getTargetUserIdList().isEmpty()) {
            for (Integer userId : userEventResource.getTargetUserIdList()) {
                GcUserEventResource gcUserEventResource = new GcUserEventResource();
                gcUserEventResource.setEventId(userEventResource.getEventId());
                gcUserEventResource.setUserId(user.getId());
                gcUserEventResource.setType(userEventResource.getType());
                gcUserEventResource.setContent(userEventResource.getContent());
                gcUserEventResource.setMasterId(masterId);
                gcUserEventResource.setTargetUserId(userId);
                userEventResourceList.add(gcUserEventResource);
            }
        }

        if (Objects.isNull(userEventResource.getTargetUserId())
                && CollectionUtils.isNotEmpty(userEventResource.getTargetUserIdList())) {
            userEventResourceService.saveBatch(userEventResourceList);
        } else {
            if (!userEventResourceService.saveEventAction(userEventResource)) {
                throw new SystemException(I18NUtil.get("upload.fail"));
            }
        }

        if (userEventResource.getTargetUserId() == null) {
            List<GcMasterMessage> masterMessageList = new ArrayList<>();
            List<Integer> sendIds = userService.getTalkerIds(user.getId(), masterId);
            if (!userEventResourceList.isEmpty()) {
                List<GcMasterMessage> gcMasterMessageList = new ArrayList<>();
                for (Integer userId : userEventResource.getTargetUserIdList()) {
                    GcMasterMessage gcmasterMessage = new GcMasterMessage();
                    gcmasterMessage.setEventType(userEventResource.getType());
                    gcmasterMessage.setMasterId(masterId);
                    gcmasterMessage.setUserId(user.getId());
                    gcmasterMessage.setTargetUserId(userId);
                    Optional<GcUserEventResource> gcUserEventResource =
                            userEventResourceList.stream()
                                    .filter(e -> e.getTargetUserId().equals(userId))
                                    .findFirst();
                    GcUserEventResource gcUserEventResource1 = gcUserEventResource.get();
                    gcmasterMessage.setResId(gcUserEventResource1.getId());
                    gcMasterMessageList.add(gcmasterMessage);
                }
                masterMessageService.saveBatch(gcMasterMessageList);
            } else if (CollectionUtils.isEmpty(userEventResource.getTargetUserIdList())
                    && !sendIds.isEmpty()) {
                for (Integer sendId : sendIds) {
                    GcMasterMessage masterMessage = new GcMasterMessage();
                    masterMessage.setEventType(userEventResource.getType());
                    masterMessage.setMasterId(masterId);
                    masterMessage.setUserId(user.getId());
                    masterMessage.setTargetUserId(sendId);
                    masterMessage.setResId(userEventResource.getId());
                    masterMessageList.add(masterMessage);
                }
                masterMessageService.saveBatchMasterMessage(masterMessageList);
            }
        } else {
            GcMasterMessage masterMessage = new GcMasterMessage();
            masterMessage.setEventType(userEventResource.getType());
            masterMessage.setMasterId(masterId);
            masterMessage.setUserId(user.getId());
            masterMessage.setTargetUserId(userEventResource.getTargetUserId());
            masterMessage.setResId(userEventResource.getId());
            masterMessageService.saveMasterMessage(masterMessage);
        }

        if (null != userEventResource.getFileId()) {
            SysFile file = sysFileService.getById(userEventResource.getFileId());
            file.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(file));
            userEventResource.setResFile(file);
        }

        return new Message().ok().addData("userEventResource", userEventResource);
    }
}
