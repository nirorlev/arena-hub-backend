package com.threeatom.guidecore.controller.api.user;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.*;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.controller.user.vo.MessageFIlterVo;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.controller.user.vo.TeacherMesNumVo;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcEventMapper;
import com.threeatom.guidecore.service.*;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileCaptionService;
import com.threeatom.system.service.SysFileService;
import com.threeatom.system.service.SysSystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// import static com.threeatom.guidecore.controller.api.manager.PowtoonController.permit;

@RestController
@RequestMapping("/api/v1/guidecore/user") // 与UserGuideCoreController的一致，注意命名
@Api(tags = "用户端视频管理")
@Validated
public class VideoGuideCoreController extends GuideCoreController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoGuideCoreController.class);

    @Autowired private GcUserVideoActionService videoActionService;
    @Autowired private NewUiGcSubjectService service;
    @Autowired private GcVideoCommentService videoCommentService;
    @Autowired private GcSubjectService subjectService;
    @Autowired private GcVideoService videoService;
    @Autowired private GcUserVideoPlayService userVideoPlayService;

    @Autowired private GcUserVideoPlaysNodeService videoPlaysNodeService;

    @Autowired private GcEventService eventService;
    @Autowired private GcResourceService resourceService;

    @Autowired private SysFileService sysFileService;
    @Autowired private GcUserEventResourceService userEventResourceService;
    @Autowired private GcUserNoteService userNoteService;
    @Autowired private GcMasterMessageService masterMessageService;

    @Autowired private SysSystemService systemService;

    @Autowired private GcEventMapper gcEventMapper;

    @Autowired private Environment env;

    @Autowired private GcUserAccessService userAccessService;

    @Autowired private GcUserVideoPlaysNodeService userVideoPlaysNodeService;

    @Autowired private GcGroupService gcGroupService;

    @Autowired private GcTeacherDataService teacherDataService;

    @Autowired private GcUserAccessExtService userAccessExtService;
    @Autowired private SysFileCaptionService sysFileCaptionService;
    @Autowired private GcUserVideoPlayService gcUserVideoPlayService;
    @Autowired private GcUserVideoPlaysNodeService gcUserVideoPlaysNodeService;

    @Autowired private GcVideoService gcVideoService;

    @Autowired private GvgMasterService gvgMasterService;

    @Autowired private NewUiGcSubjectService newUiGcSubjectService;

    @ApiOperation(value = "用户视频点赞的视频列表", httpMethod = "GET", notes = "type操作类型1点赞2收藏")
    @GetMapping("/getLikeVideoByUserId")
    public Message getLikeVideoByUserId(HttpServletRequest request) {

        GcUser user = this.getGcUser();
        Integer masterId = getHeaderMasterId(request);

        List<GcVideo> list = videoService.selectLikeVideoByUserId(user.getId(), masterId);

        for (GcVideo video : list) {
            video.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(video));
        }
        return new Message().ok("操作成功！").addData("likeVideoList", list);
    }

    @ApiOperation(value = "用户视频（点赞，评价）", httpMethod = "POST", notes = "type操作类型1点赞2评价")
    @PostMapping("/videoActionOld")
    public Message videoActionOld(@RequestBody JSONObject jsonRequest, HttpServletRequest request) {
        Integer vid = jsonRequest.getInteger("vid");
        Integer type = jsonRequest.getInteger("type");

        this.assertResourceLimit(request, vid, SysResourceType.VIDEO);

        ApiAssert.notNull(vid, "参数vid缺失");
        ApiAssert.notNull(type, "参数type缺失");
        GcUser user = this.getGcUser();
        if (videoActionService.saveVideoAction(vid, user.getId(), type))
            return new Message().ok("操作成功！");
        else return new Message().error("操作失败！");
    }

    @ApiOperation(value = "用户视频（点赞，评价）", httpMethod = "POST", notes = "type操作类型1点赞2评价")
    @PostMapping("/videoAction")
    public Message videoAction(
            @RequestBody @Valid GcUserVideoAction gcUserVideoAction, HttpServletRequest request) {
        if (gcUserVideoAction.getVid() != null)
            gcUserVideoAction.setVideoId(gcUserVideoAction.getVid());

        //        this.assertResourceLimit(request, gcUserVideoAction.getVideoId(),
        // SysResourceType.VIDEO);

        int userId = this.getGcUser().getId();
        gcUserVideoAction.setUserId(userId);
        GcUserVideoAction oldVideoAction = null;

        if (null != gcUserVideoAction.getVideoId()) {
            oldVideoAction =
                    videoActionService.getOldVideoAction(
                            gcUserVideoAction.getVideoId(), userId, gcUserVideoAction.getType());
        } else if (null != gcUserVideoAction.getFileId()) {
            oldVideoAction =
                    videoActionService.getFileActionListByFileIdAndUserId(
                            gcUserVideoAction.getFileId(), userId);
        }

        // 点赞
        if (gcUserVideoAction.getType().intValue() == TableConstant.gcUserVideoAction_type_like1) {
            if (oldVideoAction != null) {
                boolean a = false;
                if (null != gcUserVideoAction.getVideoId()) {
                    a =
                            videoActionService.deleteOldVideoAction(
                                    gcUserVideoAction.getVideoId(), userId, gcUserVideoAction.getType());
                } else {
                    a =
                            videoActionService.deleteChannelOldVideoAction(
                                    gcUserVideoAction.getFileId(), userId, gcUserVideoAction.getType());
                }
                if (a) return new Message().ok("操作成功！");
            }

            if (null != gcUserVideoAction.getVideoId()) {
                GcVideo video = videoService.getVideoById(gcUserVideoAction.getVideoId());
                if (null != video) {
                    gcUserVideoAction.setFileId(video.getFileId());
                }
            }
            boolean a = videoActionService.saveOrUpdate(gcUserVideoAction);
            if (a) return new Message().ok("操作成功！");
        }

        if (gcUserVideoAction.getType().intValue() == TableConstant.gcUserVideoAction_type_rate2) {
            if (oldVideoAction != null) gcUserVideoAction.setId(oldVideoAction.getId());
            boolean a = videoActionService.saveOrUpdate(gcUserVideoAction);
            if (a) return new Message().ok("操作成功！");
        }
        return new Message().error("操作失败！");
    }

    @ApiOperation(value = "User video comments", httpMethod = "POST")
    @PostMapping("/videoComment")
    public Message videoComment(@RequestBody JSONObject jsonRequest, HttpServletRequest request) {
        Integer vid = jsonRequest.getInteger("vid");
        Integer masterId = getHeaderMasterId(request);
        String comment = jsonRequest.getString("comment");
        Integer fileId = jsonRequest.getInteger("fileId");
        ApiAssert.notNull(vid, "Parameter vid is missing");

        GcUser user = this.getGcUser();
        GcVideoComment videoComment = new GcVideoComment();
        videoComment.setMasterId(masterId);
        videoComment.setUserId(user.getId());
        videoComment.setComment(comment);
        videoComment.setVideoId(vid);
        if (null != fileId) {
            videoComment.setFileId(fileId);
            SysFile file = sysFileService.getById(fileId);
            file.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(file));
            videoComment.setCommentFile(file);
        }

        if (videoCommentService.saveVideoComment(videoComment)) {
            return new Message().ok("Comment successful")
                .addData("comment", videoComment);
        }

        return new Message().error("Comment failed!");
    }

    @PostMapping("/DelVideoResourceFile")
    public Message DelVideoResourceFile(
            @RequestBody JSONObject paramsObject, HttpServletRequest request) {
        Message message = new Message();
        Integer masterId = request.getIntHeader("masterId");
        GcUser user = this.getGcUser();
        Integer commentResourceFileId = paramsObject.getInteger("commentResourceFileId");
        if (commentResourceFileId == null) throw new SystemException(I18NUtil.get("resource.file.id"));
        GcUserEventResource resource = userEventResourceService.getById(commentResourceFileId);
        List<GcUserEventResource> userEventResourceList =
                userEventResourceService.selectGetEventResourceByEventIdAndTargetUserId(
                        resource.getEventId(), user.getId(), commentResourceFileId);
        if (userEventResourceList.size() != 0) {
            throw new SystemException("A teacher has replied to this feedback and cannot be deleted");
        }
        Integer deleteResult =
                userEventResourceService.deleteResourceFIle(commentResourceFileId, user.getId(), masterId);
        if (deleteResult != TableConstant.COMMON_ZERO) {
            return message.ok("Delete Successfully");
        } else {
            return message.ok("Insufficient permissions");
        }
    }

    @PostMapping("/DelVideoComment")
    public Message DelVideoComment(@RequestBody JSONObject paramsObject, HttpServletRequest request) {
        Message message = new Message();
        Integer masterId = request.getIntHeader("masterId");
        GcUser user = this.getGcUser();
        Integer commentId = paramsObject.getInteger("commentId");
        if (commentId == null) throw new SystemException(I18NUtil.get("comment.id.empty"));
        Integer deleteResult =
                videoCommentService.deleteVideoComment(commentId, user.getId(), masterId);
        if (deleteResult != TableConstant.COMMON_ZERO) {
            return message.ok("Delete Successfully");
        } else {
            return message.ok("Insufficient permissions");
        }
    }

    @ApiOperation(value = "根据科目id获取评论流", httpMethod = "GET")
    @GetMapping("/videoCommentList/{subId}")
    public Message videoCommentList(
            @PathVariable("subId") Integer subId, HttpServletRequest request) {
        //        ApiAssert.notNull(subId, "参数subId缺失");
        GcSubject sub = subjectService.getSubNameBysubId(subId);
        if (sub == null) {
            throw new SystemException(I18NUtil.get(I18NUtil.get("guidecore.master.canFindSubject")));
        }
        SysSystem sys = this.getSystem();
        GcUser user = this.getGcUser();
        return videoCommentService.getCommentStream(subId, user, sub, sys, request);
    }

    @ApiOperation(value = "获取评论详情列表", httpMethod = "GET")
    @GetMapping("/videoAllComment/{vid}")
    public Message videoComment(@PathVariable("vid") Integer vid, HttpServletRequest request) {
        GcUser user = new GcUser();
        Integer masterId = getHeaderMasterId(request);
        String token = request.getHeader("Authorization");
        // List<GcVideoComment> videoAllComment = videoCommentService.getAllCommentByVideoId(vid,null);
        if (null != token && !"".equals(token) && !"undefined".equals(token)) {
            user = this.getGcUser();
        }
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcVideoComment> videoAllComment =
                videoCommentService.getAllCommentByVideoIdAndUserId(vid, user.getId(), masterId);
        SysSystem sys = this.getSystem();
        for (GcVideoComment comment : videoAllComment) {
            SysFile avatarFile = comment.getUserAvatarFile();
            if (avatarFile != null)
                comment.setUserAvatarUrl(sysFileService.getResFullUrl(avatarFile, request));

            SysFile commentFile = comment.getCommentFile();
            if (commentFile != null) {
                sysFileService.getResFullUrl(commentFile, request);
                commentFile.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(commentFile));
            }
        }
        PageInfo<GcVideoComment> videoCommentPageInfo = new PageInfo<>(videoAllComment);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new Message()
                .ok()
                .addData("commentList", videoCommentPageInfo)
                .addData("systemTime", df.format(new Date()));
    }

    @ApiOperation(value = "回复评论", httpMethod = "POST")
    @PostMapping("/saveComment")
    public Message saveComment(
            @RequestBody GcVideoComment gcVideoComment, HttpServletRequest request) {
        GcUser user = this.getGcUser();
        gcVideoComment.setUserId(user.getId());
        GcVideoComment comment = videoCommentService.getById(gcVideoComment.getReplyCommentId());
        Integer masterId = getHeaderMasterId(request);
        if (comment.getReplyCommentId() == null && comment.getMainCommentId() == null) {
            gcVideoComment.setReplyCommentId(comment.getId());
            gcVideoComment.setMainCommentId(comment.getId());
        } else {
            gcVideoComment.setReplyCommentId(comment.getId());
            gcVideoComment.setMainCommentId(comment.getMainCommentId());
        }
        if (videoCommentService.insertComment(gcVideoComment)) {
            // 发送事件通知
            GcMasterMessage masterMessage = new GcMasterMessage();
            masterMessage.setEventType(EventResType.TEXT_0);
            if (gcVideoComment.getFileId() != null) {
                SysFile sysFile = sysFileService.getById(gcVideoComment.getFileId());
                masterMessage.setEventType(sysFile.getFileTypeIndex());
                sysFile.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(sysFile));
                gcVideoComment.setCommentFile(sysFile);
            }
            masterMessage.setMasterId(masterId);
            masterMessage.setUserId(user.getId());
            masterMessage.setTargetUserId(gcVideoComment.getTargetUserId());
            masterMessage.setVideoCommentId(gcVideoComment.getId());
            masterMessageService.saveMasterMessage(masterMessage);
            return new Message().ok().addData("list", gcVideoComment);
        }
        return new Message().error();
    }

    @ApiOperation(value = "查询回答问题消息", httpMethod = "POST")
    @PostMapping("/userNoteCommentMessageList")
    public Message userNoteCommentMessageList(
            @RequestBody(required = false) MessageFIlterVo messageFIlterVo, HttpServletRequest request) {
        messageFIlterVo = messageCommon(messageFIlterVo, request);
        List<Map<String, Object>> list =
                masterMessageService.getNoteCommentMessageList(messageFIlterVo, request);
        PageInfo<Map<String, Object>> page = new PageInfo<Map<String, Object>>(list);

        Message m = new Message().ok("获取成功");
        if (request.getHeader(PageParam.pageSizeStr) != null
                && request.getHeader(PageParam.pageNumStr) != null) {
            m.addData("eventList", page);
        } else {
            m.addData("eventList", list);
        }
        if (messageFIlterVo.getGroupId() != null) {
            messageFIlterVo.setReadState(TableConstant.gcMasterMessage_readState_0Unread);
            Integer evidenceNum =
                    userEventResourceService.countALLResourceListByGcMasterMessageTargetUserId(
                            messageFIlterVo);
            Integer answerNum =
                    userEventResourceService.countAnswerMessageListByGcMasterMessageTargetUserId(
                            messageFIlterVo);

            TeacherMesNumVo tmnVo = new TeacherMesNumVo();
            tmnVo.setEvidenceNum(evidenceNum);
            tmnVo.setAnswerNum(answerNum);
            m.addData("messageNum", tmnVo);
        }
        return m;
    }

    @ApiOperation(value = "查询评论", httpMethod = "GET")
    @GetMapping("/selectComment")
    public Message selectComment(Integer commentId, HttpServletRequest request) {
        List<GcVideoComment> list = videoCommentService.selectCommentByMainCommentId(commentId);
        for (GcVideoComment comment : list) {
            SysFile avatarFile = comment.getUserAvatarFile();
            if (avatarFile != null)
                comment.setUserAvatarUrl(sysFileService.getResFullUrl(avatarFile, request));

            SysFile commentFile = comment.getCommentFile();
            if (commentFile != null) {
                sysFileService.getResFullUrl(commentFile, request);
                sysFileService.getVideoSnapshotUrl(commentFile);
            }
        }
        return new Message().ok().addData("commentList", list);
    }

    @ApiOperation(value = "添加视频记录以及其下的节点", httpMethod = "Post")
    @PostMapping("/createVideoPlayRecordAndNode")
    public Message createVideoPlayRecordAndNode(
            @RequestBody GcUserVideoPlay userVideoPlay, HttpServletRequest request) {
        Integer masterId = request.getIntHeader("masterId");
        GcUser user = this.getGcUser();
        return gvgMasterService.createVideoPlayRecordAndNode(
                userVideoPlay, request, EnvType.PT.getCode(), user, masterId, this.getSystem());
    }

    @ApiOperation(value = "记录视频播放的时间", httpMethod = "POST")
    @PostMapping("/recordVideoPlayTime")
    public Message recordVideoPlayTime(
            @RequestBody GcUserVideoPlaysNode videoPlaysNode, HttpServletRequest request) {

        Integer endTime = videoPlaysNode.getEndTime();
        GcUserVideoPlaysNode oldVideoPlayNode =
                videoPlaysNodeService.getVideoPlayNodeByNodeId(videoPlaysNode.getId());
        oldVideoPlayNode.setEndTime(endTime);
        if (videoPlaysNodeService.saveOrUpdate(oldVideoPlayNode)) return new Message().ok("记录成功");
        else return new Message().ok("记录失败");
    }

    @ApiOperation(value = "回答问题通知-老师", httpMethod = "POST")
    @PostMapping("/getAnswerMessageList")
    public Message getAnswerMessageList(
            @RequestBody(required = false) MessageFIlterVo messageFIlterVo, HttpServletRequest request) {
        // @RequestBody(required=false) MessageFIlterVo messageFIlterVo,
        //    	MessageFIlterVo messageFIlterVo = null;
        Message m = new Message().ok("获取成功");
        messageFIlterVo = messageCommon(messageFIlterVo, request);

        if (null != messageFIlterVo.getEventId()) {
            GcEvent gcEvent = eventService.getById(messageFIlterVo.getEventId());
            GcVideo gcVideo = videoService.getById(gcEvent.getVideoId());
            GcSubject gcSubject = subjectService.getById(gcVideo.getSubId());
            GcSubject gcSubject0 = subjectService.getById(gcSubject.getFid());
            gcEvent.setSub0Name(gcSubject0.getName());
            m.addData("event", gcEvent);
        }

        List<Map<String, Object>> list =
                userEventResourceService.getAnswerMessageListByGcMasterMessageTargetUserId(
                        messageFIlterVo, this.getSystem(), request);
        //         List<Map<String, Object>> orderList = list.stream().sorted(Comparator.comparing(ma))
        PageInfo<Map<String, Object>> page = new PageInfo<Map<String, Object>>(list);
        for (Map<String, Object> map : list) {
            SysFile sysFile = new SysFile();
            if (null != map.get("subjectAvatarFileUrl") && null != map.get("subjectAvatarSaveType")) {
                sysFile.setFileUrl(map.get("subjectAvatarFileUrl").toString());
                sysFile.setSaveType(Integer.parseInt(map.get("subjectAvatarSaveType").toString()));
                String fileFullUrl = sysFileService.getResFullUrl(sysFile, request);
                map.put("avatarFullFileUrl", fileFullUrl);
            }
        }

        if (request.getHeader(PageParam.pageSizeStr) != null
                && request.getHeader(PageParam.pageNumStr) != null) {
            m.addData("answerMessageList", page);
        } else {
            m.addData("answerMessageList", list);
        }

        if (messageFIlterVo.getGroupId() != null) {
            messageFIlterVo.setReadState(TableConstant.gcMasterMessage_readState_0Unread);
            Integer evidenceNum =
                    userEventResourceService.countALLResourceListByGcMasterMessageTargetUserId(
                            messageFIlterVo);
            Integer answerNum =
                    userEventResourceService.countAnswerMessageListByGcMasterMessageTargetUserId(
                            messageFIlterVo);

            TeacherMesNumVo tmnVo = new TeacherMesNumVo();
            tmnVo.setEvidenceNum(evidenceNum);
            tmnVo.setAnswerNum(answerNum);
            m.addData("messageNum", tmnVo);
        }

        return m;
    }

    private MessageFIlterVo messageCommon(
            MessageFIlterVo messageFIlterVo, HttpServletRequest request) {
        Integer masterId = getHeaderMasterId(request);
        GcUser user = this.getGcUser();
        Integer userId = user.getId();
        if (messageFIlterVo == null) messageFIlterVo = new MessageFIlterVo();
        messageFIlterVo.setThisUserId(userId);
        messageFIlterVo.setMasterId(masterId);
        return messageFIlterVo;
    }

    @ApiOperation(value = "添加、更新视频note", httpMethod = "POST")
    @PostMapping("/videoNote")
    public Message videoNote(@RequestBody JSONObject jsonRequest, HttpServletRequest request) {
        String note = jsonRequest.getString("note");
        Integer masterId = request.getIntHeader("masterId");
        Integer noteId = jsonRequest.getInteger("noteId");
        Integer fileId = jsonRequest.getInteger("fileId");

        Integer vid = jsonRequest.getInteger("vid");
        ApiAssert.notNull(note, "参数note缺失");
        ApiAssert.notNull(vid, "参数vid缺失");

        GcUser user = this.getGcUser();
        if (null != user && null != masterId) {
            List<GcUserAccess> accessList =
                    userAccessService.getAccessListByUserAndMasterId(user.getId(), masterId);
            if (null != accessList && accessList.size() != 0) {
                if (accessList.size() != TableConstant.COMMON_ONE) {
                } else {
                    this.assertUserStudent(getHeaderMasterId(request));
                }
            }
        }
        GcUserNote videoNote = new GcUserNote();
        if (noteId != null) {
            videoNote.setId(noteId);
        }
        videoNote.setVideoId(vid);
        videoNote.setNoteContent(note);
        videoNote.setUserId(user.getId());
        videoNote.setMasterId(masterId);
        if (null != fileId) {
            videoNote.setFileId(fileId);
            SysFile file = sysFileService.getById(fileId);
            file.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(file));
            videoNote.setFile(file);
        }

        if (userNoteService.saveOrUpdate(videoNote))
            return new Message().ok("添加成功").addData("videoNote", videoNote);
        else return new Message().error("添加失败");
    }
}
