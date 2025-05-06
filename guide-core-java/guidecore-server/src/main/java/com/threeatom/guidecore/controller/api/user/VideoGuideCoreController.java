package com.threeatom.guidecore.controller.api.user;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.EventResType;
import com.threeatom.guidecore.constant.SysResourceType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.controller.user.vo.MessageFIlterVo;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.controller.user.vo.TeacherMesNumVo;
import com.threeatom.guidecore.entity.GcEvent;
import com.threeatom.guidecore.entity.GcMasterMessage;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserAccess;
import com.threeatom.guidecore.entity.GcUserEventResource;
import com.threeatom.guidecore.entity.GcUserNote;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.GcVideoComment;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.GcEventService;
import com.threeatom.guidecore.service.GcMasterMessageService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.GcUserEventResourceService;
import com.threeatom.guidecore.service.GcUserNoteService;
import com.threeatom.guidecore.service.GcUserVideoActionService;
import com.threeatom.guidecore.service.GcVideoCommentService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.service.UnavailableVideoService;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guidecore/user") // 与UserGuideCoreController的一致，注意命名
@Api(tags = "用户端视频管理")
@Validated
public class VideoGuideCoreController extends GuideCoreController {

    @Autowired private GcUserVideoActionService videoActionService;
    @Autowired private GcVideoCommentService videoCommentService;
    @Autowired private GcSubjectService subjectService;
    @Autowired private GcVideoService videoService;
    @Autowired private GcEventService eventService;
    @Autowired private PortalUserService portalUserService;
    @Autowired private SysFileService sysFileService;
    @Autowired private GcUserEventResourceService userEventResourceService;
    @Autowired private GcUserNoteService userNoteService;
    @Autowired private GcMasterMessageService masterMessageService;
    @Autowired private GcUserAccessService userAccessService;
    @Autowired private UnavailableVideoService unavailableVideoService;

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
        if (gcUserVideoAction.getVid() != null) {
            gcUserVideoAction.setVideoId(gcUserVideoAction.getVid());
        }

        gcUserVideoAction.setContentId(gcUserVideoAction.getVideoId());
        int userId = this.getGcUser().getId();
        gcUserVideoAction.setUserId(userId);
        GcUserVideoAction oldVideoAction = null;

        if (null != gcUserVideoAction.getContentId()) {
            oldVideoAction =
                    videoActionService.getOldVideoAction(
                            gcUserVideoAction.getContentId(), userId, gcUserVideoAction.getType());
        }

        if (gcUserVideoAction.getType() == TableConstant.gcUserVideoAction_type_like1) {
            if (oldVideoAction != null) {
                boolean sucess = false;
                if (null != gcUserVideoAction.getContentId()) {
                    sucess =
                            videoActionService.deleteOldVideoAction(
                                    gcUserVideoAction.getContentId(), userId, gcUserVideoAction.getType());
                }
                if (sucess) return new Message().ok("操作成功！");
            }

            if (videoActionService.saveOrUpdate(gcUserVideoAction)) {
                return new Message().ok("操作成功！");
            }
        }

        if (gcUserVideoAction.getType() == TableConstant.gcUserVideoAction_type_rate2) {
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
        Integer masterId = getHeaderMasterId(request);
        GcUser user = this.getGcUser();

        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }

        List<GcVideoComment> videoAllComment =
                videoCommentService.getAllCommentByVideoIdAndUserId(vid, user.getId(), masterId);

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
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);
        GcVideo video = videoService.findByVideoId(vid);
        unavailableVideoService.nullifyVideoComments(portalUser, video, videoCommentPageInfo);

        return new Message()
                .ok()
                .addData("commentList", videoCommentPageInfo)
                .addData("systemTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
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

    @ApiOperation(value = "回答问题通知-老师", httpMethod = "POST")
    @PostMapping("/getAnswerMessageList")
    public Message getAnswerMessageList(
            @RequestBody(required = false) MessageFIlterVo messageFIlterVo, HttpServletRequest request) {
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
