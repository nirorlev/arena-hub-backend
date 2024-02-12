package com.threeatom.guidecore.controller.api.user.newUI;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.constant.SysConstant;
import com.threeatom.guidecore.constant.*;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.controller.user.vo.UserNoteCommentVo;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.service.*;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/guidecore/newui/user") // 与VideoGuideCoreController的一致，注意命名
@Api(tags = "用户端数据管理")
public class NewUIUserController extends GuideCoreController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewUIUserController.class);

    @Autowired private GcMasterService masterService;
    @Autowired private GcEventService eventService;
    @Autowired private GcUserAccessService userAccessService;
    @Autowired private SysFileService sysFileService;
    @Autowired private GcMasterMessageService masterMessageService;
    @Autowired private GcUserService userService;
    @Autowired private GcSubjectService subjectService;
    @Autowired private GcUserAnswerService userAnswerService;
    @Autowired private GcUserNoteService userNoteService;
    @Autowired private GcUserService gcUserService;
    @Autowired private GcUserInfoService gcUserInfoService;
    @Autowired private GcUserEventResourceService gcUserEventResourceService;
    @Autowired private GcUserNoteCommentService gcUserNoteCommentService;
    @Autowired private GcUserFabulousService gcUserFabulousService;
    @Autowired private GcVideoService gcVideoService;
    @Autowired private GcUserAccessService gcUserAccessService;
    @Autowired private GvgMasterService gvgMasterService;

    @ApiOperation(value = "获取课程页数据", httpMethod = "GET")
    @GetMapping("/getHomeData")
    public Message getHomeData(HttpServletRequest request) {
        Integer masterId = getHeaderMasterId(request);
        ApiAssert.notNull(masterId, "masterId " + I18NUtil.get("guidecore.master.valueRuleError"));

        GcUser user = this.getGcUser();
        SysSystem sys = this.getSystem();

        GcUserAccess userAccess =
                userAccessService.getUserAccessByMasterIdAndUserId(masterId, user.getId());
        ApiAssert.notNull(userAccess, 403, "没有访问空间的权限");
        GcUserAccessPermission permission =
                userAccessService.getUserAccessPermission(userAccess.getId());
        ApiAssert.notNull(permission, 403, "没有找到用户权限表");
        List<Integer> subIds = permission.getSubPermission().toJavaList(Integer.class);

        GcMaster master = masterService.getMasterById(masterId);
        master.setLogoFullUrl(sysFileService.getResFullUrl(master.getLogoFile(), request));

        Message message = new Message();
        return message;
    }

    @ApiOperation(value = "视频详情页", httpMethod = "GET")
    @GetMapping("/videoDetail")
    public Message videoDetail(HttpServletRequest request, Integer videoId) {
        SysSystem system = this.getSystem();
        if (Objects.isNull(videoId)) {
            throw new SystemException(I18NUtil.get("guidecore.video.detail.error"));
        }
        String token = request.getHeader("Authorization");
        if (null != token && !"".equals(token) && !"undefined".equals(token)) {
            GcUser user = this.getGcUser();
            return gvgMasterService.videoDetail(request, videoId, user, system, EnvType.GC.getCode());
        } else {
            return gvgMasterService.videoDetail(request, videoId, null, system, EnvType.GC.getCode());
        }
    }

    @ApiOperation(value = "视频详情页-问题详情框", httpMethod = "GET")
    @PostMapping("/eventDetail")
    public Message videoEventDetail(@RequestBody JSONObject jsonRequest, HttpServletRequest request) {
        Message m = new Message().ok();
        Integer eventId = jsonRequest.getInteger("eventId");
        Integer masterId = request.getIntHeader("masterId");
        GcMaster master = masterService.getById(masterId);
        ApiAssert.notNull(eventId, "事件id不可空");
        // 问题信息
        GcEvent event = eventService.getEventById(eventId, this.getGcUser().getId());
        // 其他回答过问题的用户的头像
        List<GcUserAnswer> eventAnswerList = new ArrayList<>();
        if (null == master.getAnswerShowFlag()
                || TableConstant.COMMON_ONE == master.getAnswerShowFlag()) {
            eventAnswerList =
                    userAnswerService.getAnswerListByEventId(eventId, this.getGcUser().getId(), masterId);
        } else {
            eventAnswerList = new ArrayList<>();
        }

        List<Integer> userIdList = new ArrayList<>();
        for (GcUserAnswer eventAnswer : eventAnswerList) {
            userIdList.add(eventAnswer.getUserId());
            SysFile userFile = new SysFile();
            userFile.setFileUrl(eventAnswer.getAvatarUrl());
            userFile.setSaveType(eventAnswer.getSaveType());
            if (eventAnswer.getAvatarUrl() != "")
                eventAnswer.setAvatarUrl(sysFileService.getResFullUrl(userFile, request));
        }

        if (userIdList != null && TableConstant.COMMON_ZERO != userIdList.size()) {
            List<GcUserFabulous> fabulousNum =
                    gcUserFabulousService.getEventFabulousNumList(eventId, userIdList);
            List<GcUserNoteComment> commentNum =
                    gcUserNoteCommentService.selectCommentNumList(eventId, userIdList, masterId);
            Map<Integer, List<GcUserFabulous>> fabulousmap =
                    fabulousNum.stream().collect(Collectors.groupingBy(GcUserFabulous::getTargetUserId));
            Map<Integer, List<GcUserNoteComment>> commentNummap =
                    commentNum.stream().collect(Collectors.groupingBy(GcUserNoteComment::getTargetUserId));
            for (Integer key : fabulousmap.keySet()) {
                List<GcUserFabulous> gcUserFabulous = fabulousmap.get(key);
                for (GcUserAnswer gcUserAnswer : eventAnswerList) {
                    if (gcUserAnswer.getUserId().equals(key)) {
                        gcUserAnswer.setLikeNum(gcUserFabulous.size());
                    }
                }
            }

            for (Integer key : commentNummap.keySet()) {
                List<GcUserNoteComment> gcUserNoteComments = commentNummap.get(key);
                for (GcUserAnswer gcUserAnswer : eventAnswerList) {
                    if (gcUserAnswer.getUserId().equals(key)) {
                        gcUserAnswer.setCommentNum(gcUserNoteComments.size());
                    }
                }
            }
        }

        //        //暂时循环调用
        //        for(GcUserAnswer gcUserAnswer : eventAnswerList){
        //            Integer userId = gcUserAnswer.getUserId();
        //            JSONObject jsonObject = new JSONObject();
        //            jsonObject.put("eventId",eventId);
        //            jsonObject.put("otherUserId",userId);
        //            Message answerList =
        // gvgMasterService.eventAnswerList(jsonObject,request,this.getGcUser());
        //            gcUserAnswer.setAnswerMap(answerList.getData());
        //        }

        m.addData("eventAnswerList", eventAnswerList);
        m.addData("event", event);
        m.addData("说明", "event-事件详情，eventAnswerList-事件回答头像list");
        return m;
    }

    @ApiOperation(value = "问题回答详情", httpMethod = "POST")
    @PostMapping("/InteractionsEventAnswerList")
    public Message InteractionsEventAnswerList(
            @RequestBody JSONObject jsonRequest, HttpServletRequest request) {
        Message message = new Message();
        Integer eventId = jsonRequest.getInteger("eventId");
        Integer masterId = request.getIntHeader("masterId");
        GcMaster gcMaster = masterService.getById(masterId);
        ApiAssert.notNull(eventId, "事件id不可空");
        GcUser user = this.getGcUser();
        GcEvent event = eventService.getById(eventId);
        Integer videoId = event.getVideoId();
        // 门户上传的问题list
        //        List<GcEvent> portalEventsList = gcEventService.selectEventUploadByPortal(videoId);
        List<GcEvent> gcEventList = new ArrayList<>();
        if (jsonRequest.getInteger("studentId") == null) {
            // 如果studentid为空，代表是学生用户,查找视频下自己有权限的问题
            gcEventList = eventService.selectGetEventListByVid(videoId, user.getId(), masterId);
        } else {
            // 如果studentid不为空，代表是教师用户，查找视频下所有问题
            gcEventList = eventService.getEventListByVid(videoId, masterId);
        }
        // 课程IDlist
        List<Integer> gcEventIds =
                gcEventList.stream().map(GcEvent::getId).collect(Collectors.toList());
        GcVideo gcVideo = gcVideoService.getVideoById(videoId);
        SysFile sysFile = sysFileService.getById(gcVideo.getFileId());
        String videoFileUrl = sysFileService.getResFullUrl(sysFile, request);
        sysFile.setFullFileUrl(videoFileUrl);
        gcVideo.setVideoFile(sysFile);
        List<GcUserAnswer> eventAnswerList = new ArrayList<>();
        List<GcUserEventResource> unReadMessages = new ArrayList<>();
        Map<Integer, List<GcUserAnswer>> eventMap = new HashMap<>();
        Map<Integer, List<GcUserEventResource>> resMessageList = new HashMap<>();
        if (gcEventIds.size() != TableConstant.COMMON_ZERO) {
            // 查询所有问题下的回答
            if (null == gcMaster.getAnswerShowFlag()
                    || TableConstant.COMMON_ONE == gcMaster.getAnswerShowFlag()) {
                eventAnswerList =
                        userAnswerService.getAnswerListByEvent(gcEventIds, masterId, user.getId());
            } else {
                eventAnswerList =
                        userAnswerService.getMyAnswerListByEvent(gcEventIds, masterId, user.getId());
            }
            // 按照问题分类
            eventMap = eventAnswerList.stream().collect(Collectors.groupingBy(GcUserAnswer::getEventId));
            // 查询老师回复未读的数量
            if (jsonRequest.getInteger("studentId") == null) {
                // 如果studentid为空，代表是学生用户，查找老师发送的消息
                unReadMessages =
                        gcUserEventResourceService.selectUnCheckedTeacherMessage(
                                user.getId(), gcEventIds, masterId);
            } else {
                // 如果studentid不为空，代表是教师用户，查找学生发送的消息
                unReadMessages =
                        gcUserEventResourceService.selectUnCheckedStudentMessage(
                                jsonRequest.getInteger("studentId"), gcEventIds, user.getId(), masterId);
            }
            // 根据问题分类
            resMessageList =
                    unReadMessages.stream().collect(Collectors.groupingBy(GcUserEventResource::getEventId));
            // 计算问题下回答总数以及未读消息数量
            for (GcEvent event1 : gcEventList) {
                List<GcUserAnswer> gcUserAnswers = eventMap.get(event1.getId());
                event1.setThisUserAnsweredOrNot(TableConstant.COMMON_ZERO);
                if (null != gcUserAnswers) {
                    event1.setAnswerList(gcUserAnswers);
                    event1.setAnswerNum(gcUserAnswers.size());
                    Optional<GcUserAnswer> myOptionAnswer =
                            gcUserAnswers.stream().filter(e -> user.getId().equals(e.getUserId())).findFirst();
                    // 判断当前学生用户是否回答过这个问题
                    if (myOptionAnswer.isPresent()) {
                        event1.setThisUserAnsweredOrNot(TableConstant.COMMON_ONE);
                        if (event1.getEventType().equals(TableConstant.COMMON_ONE)) {
                            GcUserAnswer gcUserAnswer = myOptionAnswer.get();
                            JSONArray jsonArray = JSONArray.parseArray(gcUserAnswer.getAnswerJson());
                            List<String> rightAnswer = new ArrayList<>();
                            String ext = event1.getExt();
                            JSONArray jsonArray1 = JSONArray.parseArray(ext);
                            for (Object obj : jsonArray1) {
                                JSONObject jsonObject = (JSONObject) JSONObject.toJSON(obj);
                                if (Boolean.parseBoolean(jsonObject.get("checked").toString())) {
                                    rightAnswer.add(jsonObject.get("value").toString());
                                }
                            }
                            if (JSONObject.parseArray(jsonArray.toJSONString(), String.class)
                                            .containsAll(rightAnswer)
                                    && rightAnswer.containsAll(
                                            JSONObject.parseArray(jsonArray.toJSONString(), String.class))) {
                                gcUserAnswer.setIfAnswerRight(TableConstant.COMMON_ONE);
                            } else {
                                gcUserAnswer.setIfAnswerRight(TableConstant.COMMON_ZERO);
                            }
                        }
                    }
                }
                List<GcUserEventResource> gcUserEventResources = resMessageList.get(event1.getId());
                // 计算未读消息的数量
                if (null != gcUserEventResources) {
                    List<GcUserEventResource> unreadMessagesFromTeacherList =
                            gcUserEventResources.stream()
                                    .filter(e -> null != e.getReadState())
                                    .filter(e -> e.getReadState() == TableConstant.COMMON_ZERO)
                                    .collect(Collectors.toList());
                    event1.setUnReadMessages(unreadMessagesFromTeacherList.size());
                }
            }
            // 按照eventtime顺序排序问题
            List<GcEvent> orderByEventTimeSubjectList =
                    gcEventList.stream()
                            .sorted(Comparator.comparing(GcEvent::getEventTime))
                            .collect(Collectors.toList());
            gcVideo.setEventList(orderByEventTimeSubjectList);
        }
        // 学生自己的回答
        // 用户信息
        GcUser studentUser = gcUserService.getUserByIdCache(jsonRequest.getInteger("studentId"));
        if (studentUser != null) {
            Integer imgFileId = studentUser.getInfo().getAvatarFileId();
            if (imgFileId != null) {
                SysFile imgFile = sysFileService.getById(imgFileId);
                String url = sysFileService.getResFullUrl(imgFile, request);
                imgFile.setFullFileUrl(url);
                studentUser.getInfo().setAvatarFile(imgFile);
            }
        }

        int answeredEventNum = 0;
        // 计算任务数和已回答数
        //        Map<Integer,Object> answerNumMap = gcEventService.videoEventsAnswerNumMap(videoId,
        // user.getId(),masterId);
        //            for (GcEvent answerEvent : gcEventList) {
        //                Map numMap = (Map) answerNumMap.get(answerEvent.getId());
        //                int num = 0;
        //                if (numMap != null && numMap.get("answerNum") != null) {
        //                    num = ((Long) numMap.get("answerNum")).intValue();
        //                    //循环计算已回答的问题数量，每有一条数量加1
        //                    if(num > TableConstant.COMMON_ZERO){
        //                        answeredEventNum +=TableConstant.COMMON_ONE;
        //                    }
        //                }
        //            }
        List<GcEvent> answeredEvents =
                gcEventList.stream()
                        .filter(e -> e.getThisUserAnsweredOrNot() == TableConstant.COMMON_ONE)
                        .collect(Collectors.toList());
        gcVideo.setAnsweredNums(answeredEvents.size());
        gcVideo.setAnsweredSumNums(gcEventList.size());
        GcSubject gcSubject0 = subjectService.getById(gcVideo.getSubId0());
        gcVideo.setSubjectName(gcSubject0.getName());
        message.ok().addData("gcVideo", gcVideo);
        message.ok().addData("userInfo", studentUser);
        return message.ok();
    }

    @ApiOperation(value = "问题回答详情", httpMethod = "GET")
    @PostMapping("/eventAnswerDetail")
    public Message eventAnswerList(@RequestBody JSONObject jsonRequest, HttpServletRequest request) {
        GcUser user = this.getGcUser();
        return gvgMasterService.eventAnswerList(jsonRequest, request, user);
    }

    @ApiOperation(value = "问题回答详情", httpMethod = "POST")
    @PostMapping("/changeUsername")
    public Message changeUsername(@RequestBody GcUser gcUser, HttpServletRequest request)
            throws Exception {
        GcManager gcManager = this.getManager();
        GcMaster master = this.getMaster();
        GcUser user = userService.getUserByUserName(gcUser.getUsername());
        if (null != user) throw new SystemException(I18NUtil.get("guidecore.email"));
        //        List<GcUserAccess> gcUserAccessList =
        // gcUserAccessService.getAccessListByUser(gcUser.getId());
        //        List<Integer> masterIdList =
        // gcUserAccessList.stream().map(GcUserAccess::getMasterId).collect(Collectors.toList());
        //        if(null!=masterIdList && masterIdList.size()>TableConstant.COMMON_ZERO &&
        // masterIdList.contains(gcUser.getSubjectMasterId())){
        //            return new Message().error("the user does not belong to this master");
        //        }
        if (TableConstant.COMMON_ONE == gcManager.getSuperAdminFlag()) {
            return new Message()
                    .ok()
                    .addData("", this.gcUserService.updateById(gcUser))
                    .addData("newEmail", gcUser);
        } else {
            List<GcUserAccess> gcUserAccessList = gcUserAccessService.getAccessListByUser(gcUser.getId());
            List<Integer> masterIdList =
                    gcUserAccessList.stream().map(GcUserAccess::getMasterId).collect(Collectors.toList());
            if (null != masterIdList
                    && masterIdList.size() > TableConstant.COMMON_ZERO
                    && masterIdList.contains(master.getId())) {
                return new Message()
                        .ok()
                        .addData("", this.gcUserService.updateById(gcUser))
                        .addData("newEmail", gcUser);
            } else {
                return new Message().error("the user does not belong to the master");
            }
        }
    }

    @ApiOperation(value = "视频笔记list", httpMethod = "GET")
    @PostMapping("/videoNoteList")
    public Message videoNoteList(@RequestBody JSONObject jsonRequest, HttpServletRequest request) {
        Integer masterId = request.getIntHeader("masterId");
        Integer videoId = jsonRequest.getInteger("videoId");
        ApiAssert.notNull(videoId, "视频id不可空");

        Message m = new Message().ok();

        List<GcUserNote> userNoteList =
                userNoteService.selectNoteByUserId(this.getGcUser().getId(), videoId, masterId, request);
        for (GcUserNote note : userNoteList) {
            if (note.getFile() != null) sysFileService.getResFullUrl(note.getFile(), request);
            sysFileService.getVideoSnapshotUrl(note.getFile());
        }
        PageInfo<GcUserNote> userNotePageInfo = new PageInfo<>(userNoteList);
        m.addData("userNoteList", userNotePageInfo);
        return m;
    }

    @ApiOperation(value = "删除视频笔记", httpMethod = "GET")
    @GetMapping("/deleteVideoNote")
    public Message deleteVideoNote(Integer noteId, HttpServletRequest request) {
        ApiAssert.notNull(noteId, "note id不可空");
        GcUserNote note = new GcUserNote();
        note.setId(noteId);
        note.setUserId(this.getGcUser().getId());
        if (userNoteService.countNoteByNote(note) == 0)
            return new Message().error("该用户没有该笔记id权限，或该id不存在");

        if (userNoteService.removeById(noteId)) return new Message().ok("删除成功");
        else return new Message().error("删除失败");
    }

    @ApiOperation(value = "获取笔记list", httpMethod = "GET")
    @GetMapping("/selectNoteListByUserMaster")
    public Message selectNoteListByUserMaster(HttpServletRequest request) {
        List<GcUserNote> list =
                userNoteService.selectNoteListByUserMaster(
                        this.getGcUser().getId(), getHeaderMasterId(request), request);
        PageInfo<GcUserNote> pageInfo = new PageInfo<>(list);
        return new Message()
                .ok("删除成功")
                .addData("list", pageInfo)
                .addData("返回说明", "id：视频id，fileNum：文件数量，videoName：视频名称");
    }

    @ApiOperation(value = "修改个人信息", httpMethod = "POST")
    @PostMapping("/changeUserInfo")
    public Message putStarValue(@RequestBody GcUserInfo info, HttpServletRequest request) {
        String username = info.getUsername();
        if (username != null) {
            GcUser existEmailUser = gcUserService.getUserByUserName(username);
            if (existEmailUser != null)
                throw new SystemException(I18NUtil.get("guidecore.user.emailExist"));
            GcUser user = this.getGcUser();
            user.setUsername(username);
            gcUserService.updateById(user);
        }

        info.setId(this.getGcUser().getInfoId());

        if (gcUserInfoService.updateById(info)) return new Message().ok("保存成功");
        else return new Message().error("保存失败");
    }

    @ApiOperation(value = "在个人中心修改密码", httpMethod = "POST")
    @PostMapping("/changePassword")
    public Message changePassword(@RequestBody JSONObject requestParams, HttpServletRequest request)
            throws ClientException {
        GcManager gcManager = this.getManager();
        GcUser user = this.getGcUser();
        String oldPassword = requestParams.getString("oldPassword");
        String newPassword = requestParams.getString("newPassword");
        user = userService.getUserByUserName(user.getUsername());

        gcUserService.checkGcUser(user.getUsername(), oldPassword);

        String pwdHash =
                new SimpleHash("MD5", newPassword, user.getSalt() + SysConstant.PASS_SALT).toHex();
        user.setPassword(pwdHash);
        if (userService.saveOrUpdate(user)) {
            return new Message().ok("更改成功");
        } else {
            return new Message().error("更改失败");
        }
    }

    @ApiOperation(value = "查询回答问题下评论", httpMethod = "GET")
    @GetMapping("/selectNodeComment")
    public Message selectComment(HttpServletRequest request, Integer eventId, Integer otherUserId) {
        Integer masterId = request.getIntHeader("masterId");
        List<UserNoteCommentVo> voList =
                gcUserNoteCommentService.selectNoteComment(eventId, otherUserId, request);
        return new Message().ok().addData("list", voList);
    }

    @ApiOperation(value = "保存回答问题下评论", httpMethod = "POST")
    @PostMapping("/saveNoteComment")
    public Message saveNoteComment(
            HttpServletRequest request, @RequestBody GcUserNoteComment userNoteComment) {
        GcUser user = this.getGcUser();
        Integer masterId = request.getIntHeader("masterId");
        userNoteComment.setMasterId(masterId);
        userNoteComment.setUserId(user.getId());
        if (gcUserNoteCommentService.save(userNoteComment)) {

            // 发送事件通知
            GcMasterMessage masterMessage = new GcMasterMessage();
            masterMessage.setEventType(EventResType.TEXT_0);
            if (userNoteComment.getFileId() != null) {
                SysFile sysFile = sysFileService.getById(userNoteComment.getFileId());
                masterMessage.setEventType(sysFile.getFileTypeIndex());
                userNoteComment.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(sysFile));
            }
            masterMessage.setMasterId(getHeaderMasterId(request));
            masterMessage.setUserId(user.getId());
            masterMessage.setTargetUserId(userNoteComment.getTargetUserId());
            masterMessage.setUserNoteCommentId(userNoteComment.getId());
            masterMessageService.saveMasterMessage(masterMessage);
            return new Message().ok("评论成功").addData("userNoteComment", userNoteComment);
        }
        return new Message().error("评论失败");
    }
}
