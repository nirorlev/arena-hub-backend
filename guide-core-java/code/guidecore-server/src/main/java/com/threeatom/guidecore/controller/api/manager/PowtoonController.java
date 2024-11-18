package com.threeatom.guidecore.controller.api.manager;

import static com.threeatom.utils.ToolUtil.parseToJsonArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.threeatom.client.dto.PowtoonAuthDto;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.PermitException;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.pdf.PdfModel;
import com.threeatom.common.pdf.PdfServicePt;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.guidecore.constant.AccessRoleType;
import com.threeatom.guidecore.constant.EnvType;
import com.threeatom.guidecore.constant.EventUnifyType;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.dto.request.AuthTokenDto;
import com.threeatom.guidecore.dto.request.SearchDto;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcCategory;
import com.threeatom.guidecore.entity.GcEvent;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcMasterHomeInfo;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserAccess;
import com.threeatom.guidecore.entity.GcUserAccessExt;
import com.threeatom.guidecore.entity.GcUserAccessPermission;
import com.threeatom.guidecore.entity.GcUserEventResource;
import com.threeatom.guidecore.entity.GcUserInfo;
import com.threeatom.guidecore.entity.GcUserSaveContent;
import com.threeatom.guidecore.entity.GcUserSaveContentFollow;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import com.threeatom.guidecore.entity.GcUserVideoPlay;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.GcVideoComment;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.entity.PtChannelContent;
import com.threeatom.guidecore.entity.PtChannelSubscribe;
import com.threeatom.guidecore.entity.PtConfig;
import com.threeatom.guidecore.entity.PtLoginConfig;
import com.threeatom.guidecore.entity.PtTags;
import com.threeatom.guidecore.entity.PtViewSubject;
import com.threeatom.guidecore.entity.SysMenu;
import com.threeatom.guidecore.enums.BiEventAction;
import com.threeatom.guidecore.enums.CourseType;
import com.threeatom.guidecore.enums.UserGroupRole;
import com.threeatom.guidecore.exception.LicenseLimitExceededException;
import com.threeatom.guidecore.service.ContentGroupChannelSubscriptionService;
import com.threeatom.guidecore.service.EventPublisherService;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcEventService;
import com.threeatom.guidecore.service.GcMasterHomeInfoService;
import com.threeatom.guidecore.service.GcMasterService;
import com.threeatom.guidecore.service.GcProblemService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserAccessExtService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.GcUserEventResourceService;
import com.threeatom.guidecore.service.GcUserInfoService;
import com.threeatom.guidecore.service.GcUserSaveContentFollowService;
import com.threeatom.guidecore.service.GcUserSaveContentService;
import com.threeatom.guidecore.service.GcUserSaveFolderService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.GcUserVideoActionService;
import com.threeatom.guidecore.service.GcVideoCommentService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.GvgMasterService;
import com.threeatom.guidecore.service.NewUiGcSubjectService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.service.PtChannelContentService;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.service.PtChannelSubscribeService;
import com.threeatom.guidecore.service.PtConfigService;
import com.threeatom.guidecore.service.PtLoginConfigService;
import com.threeatom.guidecore.service.PtTagsService;
import com.threeatom.guidecore.service.PtViewSubjectService;
import com.threeatom.guidecore.service.SysMenuService;
import com.threeatom.guidecore.service.UserLicenseService;
import com.threeatom.guidecore.service.VideoThumbnailProvider;
import com.threeatom.guidecore.util.AuthorizationUtil;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.guidecore.util.RequestUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import com.threeatom.utils.HttpUtil;
import io.permit.sdk.api.PermitApiError;
import io.permit.sdk.api.PermitContextError;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/powtoon/home")
@Api(tags = "home")
public class PowtoonController extends GuideCoreController {

    private static final Logger log = LoggerFactory.getLogger(NewUiGcVideoController.class);

    @Autowired
    private GcVideoService service;

    @Autowired
    private NewUiGcSubjectService subjectService;

    @Autowired
    private GcSubjectService gcSubjectService;

    @Autowired
    private GcUserAccessService gcUserAccessService;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private GcProblemService gcProblemService;

    @Autowired
    private GcUserService gcUserService;//用户服务类--统计参与人数

    @Autowired
    private GcUserVideoActionService videoActionService;//用户视频操作--查询评论、点赞、星级评价

    @Autowired
    private GcMasterHomeInfoService iGcMasterHomeInfoService;

    @Autowired
    private GcVideoService gcVideoService;

    @Autowired
    private GcMasterService gcMasterService;

    @Autowired
    private PtConfigService ptConfigService;

    @Autowired
    private GcEventService gcEventService;

    @Autowired
    private GcUserVideoActionService gcUserVideoActionService;

    @Autowired
    private GvgMasterService gvgMasterService;

    @Autowired
    private GcUserService userService;

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private GcAccessService accessService;

    @Autowired
    private GcUserInfoService infoService;

    @Autowired
    private GcMasterService masterService;

    @Autowired
    private GcEventService eventService;

    @Autowired
    private PdfServicePt pdfServicePt;

    @Autowired
    private GcUserSaveContentService gcUserSaveContentService;

    @Autowired
    private GcUserSaveFolderService gcUserSaveFolderService;

    @Autowired
    private GcUserEventResourceService gcUserEventResourceService;

    @Autowired
    private GcUserSaveContentFollowService gcUserSaveContentFollowService;

    @Autowired
    private GcUserInfoService gcUserInfoService;

    @Autowired
    private PtLoginConfigService ptLoginConfigService;

    @Autowired
    private GcSubjectService subService;

    @Autowired
    private PtTagsService ptTagsService;

    @Autowired
    private PtChannelService ptChannelService;

    @Autowired
    private PtChannelContentService ptChannelContentService;

    @Autowired
    private PtChannelSubscribeService ptChannelSubscribeService;

    @Autowired
    private GcAccessService gcAccessService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private GcVideoCommentService videoCommentService;

    @Autowired
    private PtViewSubjectService viewSubjectService;

    @Autowired
    private GcUserAccessExtService gcUserAccessExtService;

    @Autowired
    private GcContentGroupCourseAssignmentService contentGroupCourseAssignmentService;

    @Autowired
    private ContentGroupChannelSubscriptionService contentGroupChannelSubscriptionService;

    @Autowired
    private VideoThumbnailProvider thumbnailProvider;

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private UserLicenseService userLicenseService;
    @Autowired
    private PortalUserService portalUserService;
    @Autowired
    private EventPublisherService eventPublisherService;

    @ApiOperation(value = "Search videos", httpMethod = "POST")
    @PostMapping("search")
    public Message searchVideo(@RequestBody @Valid SearchDto searchDto, HttpServletRequest request) {
        RequestUtil.getMasterId(request)
            .orElseThrow(() -> new SystemException(I18NUtil.get("guidecore.unlogin.error")));

        String token = RequestUtil.getRequestAuthHeader(request);
        SysSystem system = this.getSystem();

        if (!StringUtils.isEmpty(token) && !"undefined".equals(token)) {
            GcUser gcUser = this.getGcUser();
            return gvgMasterService.search(searchDto, request, gcUser, system)
                .addData("date:::", new Date());
        }

        return gvgMasterService.search(searchDto, request, null, system);
    }

    @ApiOperation(value = "New UI course homepage - including course name query interface", notes = "New UI Course Home", httpMethod = "POST")
    @PostMapping("/portalInfosUnlogin")
    public Message portalInfosUnlogin(@RequestBody JSONObject requestParams, HttpServletRequest request) {
        String portalId = requestParams.getString("portalId");

        if (Objects.isNull(portalId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }

        String token = request.getHeader("Authorization");
        if (!StringUtils.isEmpty(token) && !"undefined".equals(token)) {
            GcUser user = this.getGcUser();
            return gvgMasterService.portalInfosUnlogin(requestParams, user, request)
                .addData("times", new Date());
        }

        return gvgMasterService.portalInfosUnlogin(requestParams, null, request)
            .addData("times", new Date());
    }

    @ApiOperation(value = "新UI课程首页-包括课程名称查询接口", notes = "新UI课程首页", httpMethod = "POST")
    @PostMapping("/newSubjectIndex")
    public Message newSubjectIndex(@RequestBody JSONObject requestParams, HttpServletRequest request) {
        Message message = new Message();
        Integer type = Integer.parseInt(requestParams.get("type").toString());
        JSONArray jsonArray = JSONObject.parseArray(JSON.toJSONString(requestParams.get("groupCodeList")));
        //课程状态
        Integer subjectState = null;
        if (null != requestParams.get("subjectState")) {
            subjectState = Integer.parseInt(requestParams.get("subjectState").toString());
        }
        //课程名搜索
        String name = null;
        if (null != requestParams.get("name")) {
            name = requestParams.get("name").toString();
        }
        List<String> groupCodeList = new ArrayList<>();
        if (null != jsonArray) {
            groupCodeList = jsonArray.toJavaList(String.class);
        }

        String selectType = null;
        if (null != requestParams.get("selectType")) {
            selectType = requestParams.get("selectType").toString();
        }
        GcUser user = this.getGcUser();
        Integer masterId = Integer.parseInt(request.getHeader("masterId"));
        //组装课程数据
        List<GcSubject> subjectList = new ArrayList<>();
        switch (type) {
            //my learnings
            case TableConstant.COMMON_ZERO:
                //active
                if (null == selectType || selectType.equals("activeSubject")) {
                    List<GcSubject> activeSubject =
                        gcSubjectService.selectActiveSubject(user.getId(), masterId, subjectState, name, request);
                    PageInfo<GcSubject> activeSubjectPageInfo = new PageInfo<>(activeSubject);
                    subjectList.addAll(activeSubject);
                    message.addData("activeSubject", activeSubjectPageInfo);
                }
                //completed
                if (null == selectType || selectType.equals("completedSubject")) {
                    List<GcSubject> completedSubject =
                        gcSubjectService.selectCompletedSubject(user.getId(), masterId, subjectState, name, request);
                    PageInfo<GcSubject> completedSubjectPageInfo = new PageInfo<>(completedSubject);
                    subjectList.addAll(completedSubject);
                    message.addData("completedSubject", completedSubjectPageInfo);
                }
                //discover
                if (null == selectType || selectType.equals("discoverSubject")) {
                    List<GcSubject> discoverSubject =
                        gcSubjectService.selectDiscoverSubject(user.getId(), masterId, subjectState, name, request);
                    PageInfo<GcSubject> discoverSubjectPageInfo = new PageInfo<>(discoverSubject);
                    subjectList.addAll(discoverSubject);
                    message.addData("discoverSubject", discoverSubjectPageInfo);
                }
                //组装课程数据
                gcSubjectService.getSubjectInfoByList(subjectList, masterId, user.getId(), request);
                break;
            //my courses
            case TableConstant.COMMON_ONE:
                //drafts
                if (null == selectType || selectType.equals("draftsSubjectPageInfo")) {
                    List<GcSubject> draftsSubject =
                        gcSubjectService.selectDraftsSubject(user.getId(), masterId, name, request);
                    PageInfo<GcSubject> draftsSubjectPageInfo = new PageInfo<>(draftsSubject);
                    subjectList.addAll(draftsSubject);
                    message.addData("draftsSubject", draftsSubjectPageInfo);
                }
                //published
                if (null == selectType || selectType.equals("publishedSubject")) {
                    List<GcSubject> publishedSubject =
                        gcSubjectService.selectPublishedSubject(user.getId(), masterId, name, request);
                    PageInfo<GcSubject> publishedSubjectPageInfo = new PageInfo<>(publishedSubject);
                    subjectList.addAll(publishedSubject);
                    message.addData("publishedSubject", publishedSubjectPageInfo);
                }
                //createdByTeams
                if (null == selectType || selectType.equals("createdByTeamsSubject")) {
                    //判断是orgAdmin还是teamAdmin
                    Integer adminFlag = gcUserAccessService.countUserAccessesByMasterIdAndRole(user.getId(), masterId,
                        UserGroupRole.ORG_ADMIN.getRole());
                    List<GcSubject> createdByTeamsSubject = new ArrayList<>();
                    Integer orderType = null;
                    if (null != requestParams.get("orderType")) {
                        orderType = Integer.parseInt(requestParams.get("orderType").toString());
                    }
                    if (null != adminFlag && !adminFlag.equals(TableConstant.COMMON_ZERO)) {
                        createdByTeamsSubject =
                            gcSubjectService.selectCreateByTeamsOrgAdmin(user.getId(), masterId, name, request,
                                groupCodeList, orderType);
                    } else {
                        createdByTeamsSubject =
                            gcSubjectService.selectCreatedByTeams(user.getId(), masterId, name, request, groupCodeList,
                                orderType);
                    }
                    subjectList.addAll(createdByTeamsSubject);
                    PageInfo<GcSubject> createdByTeamsSubjectPageInfo = new PageInfo<>(createdByTeamsSubject);
                    message.addData("createdByTeamsSubject", createdByTeamsSubjectPageInfo);
                }
                //组装课程数据
                gcSubjectService.getSubjectInfoByList(subjectList, masterId, user.getId(), request);
                break;
            //discover
            case TableConstant.COMMON_TWO:
                Integer orderType = null;
                if (null != requestParams.get("orderType")) {
                    orderType = Integer.parseInt(requestParams.get("orderType").toString());
                }

                //fromMyTeams
                if (null == selectType || selectType.equals("fromMyTeamSubject")) {
                    List<GcSubject> fromMyTeamSubject =
                        gcSubjectService.selectFromMyTeamSubject(user.getId(), masterId, name, request);
                    PageInfo<GcSubject> fromMyTeamSubjectPageInfo = new PageInfo<>(fromMyTeamSubject);
                    subjectList.addAll(fromMyTeamSubject);
                    message.addData("fromMyTeamSubject", fromMyTeamSubjectPageInfo);
                }
                //companyResources
                if (null == selectType || selectType.equals("companyResourcesSubject")) {
                    List<GcSubject> companyResourcesSubject =
                        gcSubjectService.selectCompanyResourcesSubject(user.getId(), masterId, name, request);
                    PageInfo<GcSubject> companyResourcesSubjectPageInfo = new PageInfo<>(companyResourcesSubject);
                    subjectList.addAll(companyResourcesSubject);
                    message.addData("companyResourcesSubject", companyResourcesSubjectPageInfo);
                }
                //allCourses
                if (null == selectType || selectType.equals("allCourseSubject")) {
                    List<GcSubject> allCourseSubject =
                        gcSubjectService.selectAllCourseSubject(user.getId(), masterId, name, request, orderType);
                    PageInfo<GcSubject> allCourseSubjectPageInfo = new PageInfo<>(allCourseSubject);
                    subjectList.addAll(allCourseSubject);
                    message.addData("allCourseSubject", allCourseSubjectPageInfo);
                }
                //组装课程数据
                gcSubjectService.getSubjectInfoByList(subjectList, masterId, user.getId(), request);
                break;
        }
        message.setData((Map<String, Object>) JSON.toJSON(message.getData()));
        return message.ok();
    }

    @ApiOperation(value = "pt新首页", notes = "新UI课程首页", httpMethod = "POST")
    @PostMapping("/newPtIndexHome")
    public Message newPtIndexHome(@RequestBody JSONObject requestParams, HttpServletRequest request) {
        SysSystem system = this.getSystem();
        String portalId = requestParams.getString("portalId");

        if (Objects.isNull(portalId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }
        String token = request.getHeader("Authorization");

        if (!StringUtils.isEmpty(token) && !"undefined".equals(token)) {
            PortalUser portalUser =
                portalUserService.getByUserAndMasterId(this.getGcUser().getId(), getHeaderMasterId(request));
            return gvgMasterService.newPtIndexHome(requestParams, request, system, portalUser)
                .addData("times", new Date());
        }
        return gvgMasterService.newPtIndexHome(requestParams, request, system, null).addData("times", new Date());
    }

    @ApiOperation(value = "设置课程已看", notes = "设置课程已看", httpMethod = "GET")
    @GetMapping("/setViewSubject")
    public Message setViewSubject(Integer subjectId, HttpServletRequest request) {
        GcUser user = this.getGcUser();
        Integer masterId = Integer.parseInt(request.getHeader("masterId"));
        QueryWrapper<PtViewSubject> queryWrapper = new QueryWrapper<PtViewSubject>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("user_id", user.getId());
        queryWrapper.eq("subject_id", subjectId);
        List<PtViewSubject> subjects = viewSubjectService.list(queryWrapper);
        if (subjects.size() != TableConstant.COMMON_ZERO) {
            PtViewSubject ptViewSubject = new PtViewSubject();
            ptViewSubject.setSubjectId(subjectId);
            ptViewSubject.setUserId(user.getId());
            ptViewSubject.setMasterId(masterId);
            viewSubjectService.saveOrUpdate(ptViewSubject);
        }
        return new Message().ok();
    }

    @ApiOperation(value = "获取所有视频tag", notes = "获取所有视频tag", httpMethod = "POST")
    @GetMapping("/getPtVideoTags")
    public Message getPtVideoTags(@Param("name") String name, HttpServletRequest request) {
        String portalId = request.getHeader("masterId");
        if (Objects.isNull(portalId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }
        PtTags ptTags = new PtTags();
        ptTags.setMasterId(getHeaderMasterId(request));
        ptTags.setType(TableConstant.COMMON_TWO);
        ptTags.setTagText(name);
        List<String> allVideoTag = ptTagsService.selectPtTagList(ptTags, request);
        PageInfo<String> pageInfo = new PageInfo<>(allVideoTag);
        return new Message().ok().addData("allVideoTag", pageInfo);
    }

    @ApiOperation(value = "获取所有课程tag", notes = "获取所有视频tag", httpMethod = "POST")
    @GetMapping("/getPtSubjectTags")
    public Message getPtSubjectTags(@Param("name") String name, HttpServletRequest request) {
        String portalId = request.getHeader("masterId");
        if (Objects.isNull(portalId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }
        PtTags ptTags = new PtTags();
        ptTags.setMasterId(getHeaderMasterId(request));
        ptTags.setType(TableConstant.COMMON_ONE);
        ptTags.setTagText(name);

        List<String> allVideoTag = ptTagsService.selectPtTagList(ptTags, request);
        PageInfo<String> pageInfo = new PageInfo<>(allVideoTag);
        return new Message().ok().addData("allSubjectTag", pageInfo);
    }

    @ApiOperation(value = "获取所有资源tag", notes = "获取所有视频tag", httpMethod = "POST")
    @GetMapping("/getPtResourceTags")
    public Message getPtResourceTags(HttpServletRequest request) {
        String portalId = request.getHeader("masterId");
        if (Objects.isNull(portalId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }

        PtTags ptTags = new PtTags();
        ptTags.setMasterId(getHeaderMasterId(request));
        ptTags.setType(TableConstant.COMMON_THREE);

        List<String> allResourceTag = ptTagsService.selectPtTagList(ptTags, request);
        return new Message().ok().addData("allResourceTag", allResourceTag);
    }

    @ApiOperation(value = "保存课程/视频到一个文件夹", httpMethod = "GET")
    @PostMapping("/saveContentToFolder")
    public Message saveContentToFolder(@RequestBody GcUserSaveFolder gcUserSaveFolder, HttpServletRequest request) {
        String portalId = request.getHeader("masterId");
        if (Objects.isNull(portalId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }
        List<GcUserSaveFolder> list =
            gcUserSaveFolderService.selectFolderForUserMaster(this.getGcUser().getId(), getHeaderMasterId(request),
                null, request, null);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Integer> allFolderIds = list.stream().map(GcUserSaveFolder::getId).collect(Collectors.toList());
            List<Integer> deleteFolderIds = allFolderIds.stream()
                .filter(e -> null != gcUserSaveFolder.getFolderId() && !gcUserSaveFolder.getFolderId().contains(e))
                .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(deleteFolderIds) && Objects.nonNull(gcUserSaveFolder.getVideoId())) {
                GcVideo gcVideo = gcVideoService.getById(gcUserSaveFolder.getVideoId());
                List<Integer> deleteContentIds =
                    gcUserSaveContentService.deleteList(gcVideo.getFileId(), deleteFolderIds);
                if (CollectionUtils.isNotEmpty(deleteContentIds)) {
                    gcUserSaveContentService.removeByIds(deleteContentIds);
                }
            }
            if (CollectionUtils.isNotEmpty(deleteFolderIds) && Objects.nonNull(gcUserSaveFolder.getFileId())) {
                List<Integer> deleteContentIds =
                    gcUserSaveContentService.deleteListByFolderId(gcUserSaveFolder.getFileId(), deleteFolderIds);
                if (CollectionUtils.isNotEmpty(deleteContentIds)) {
                    gcUserSaveContentService.removeByIds(deleteContentIds);
                }
            }
        }

        List<Integer> containsFolderId;
        if (null != gcUserSaveFolder.getVideoId()) {
            containsFolderId = gcUserSaveContentService.selectFolderIdByVideoId(gcUserSaveFolder.getVideoId(),
                request.getIntHeader("masterId"));
        } else {
            containsFolderId = gcUserSaveContentService.selectFolderIdByFileId(gcUserSaveFolder.getFileId(),
                request.getIntHeader("masterId"));
        }

        List<GcUserSaveContent> userSaveContents = new ArrayList<>();
        for (Integer folderId : gcUserSaveFolder.getFolderId()) {
            GcUserSaveContent gcUserSaveContent = new GcUserSaveContent();
            gcUserSaveContent.setUserId(this.getGcUser().getId());
            gcUserSaveContent.setMasterId(getHeaderMasterId(request));
            gcUserSaveContent.setVideoId(gcUserSaveFolder.getVideoId());
            gcUserSaveContent.setFolderId(folderId);
            if (null != gcUserSaveFolder.getVideoId()) {
                GcVideo videoContent = gcVideoService.getById(gcUserSaveFolder.getVideoId());
                gcUserSaveContent.setFileId(videoContent.getFileId());
                gcUserSaveContent.setContentId(videoContent.getId());
            } else if (null != gcUserSaveFolder.getFileId()) {
                gcUserSaveContent.setFileId(gcUserSaveFolder.getFileId());
                gcVideoService.getVideoContent(gcUserSaveFolder.getFileId())
                    .ifPresent(videoContent -> gcUserSaveContent.setContentId(videoContent.getId()));
            }
            userSaveContents.add(gcUserSaveContent);
        }
        Iterator<GcUserSaveContent> folderIterator = userSaveContents.iterator();
        while (folderIterator.hasNext()) {
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(folderIterator.next());
            if (containsFolderId.contains(jsonObject.get("folderId"))) {
                folderIterator.remove();
            }
        }

        if (gcUserSaveContentService.saveOrUpdateBatch(userSaveContents)) {
            return new Message().ok("保存成功").addData("content", userSaveContents);
        }

        return new Message().ok();
    }

    @PostMapping("navigation")
    public Message navigation(@RequestBody(required = false) Map<String, Object> params, HttpServletRequest request) {
        Object fid = params.get("fid");
        if (Objects.isNull(fid)) {
            throw new SystemException(I18NUtil.get("guidecore.course.navigation.error"));
        }
        String masterId = request.getHeader("masterId");
        if (Objects.isNull(masterId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }
        SysSystem system = this.getSystem();
        String token = request.getHeader("Authorization");
        if (!StringUtils.isEmpty(token) && !"undefined".equals(token)) {
            GcUser user = this.getGcUser();

            GcSubject course = gcSubjectService.getById(Integer.parseInt(fid.toString()));
            PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), Integer.valueOf(masterId));

            if (!authorizationService.checkAccess(course, PermitAction.VIEW, portalUser)) {
                throw new PermitException("No permission for this!");
            }

            user.setIsOrgAdmin(portalUser.isOrgAdmin());

            return gvgMasterService.navigation(params, request, system, user, EnvType.PT.getCode());
        }

        return gvgMasterService.navigation(params, request, system, new GcUser(), EnvType.PT.getCode());
    }

    @GetMapping("/downloadPDFCertPt")
    @ApiOperation(value = "下载pdf", notes = "下载pdf", httpMethod = "POST")
    public void downloadPDFCertPt(HttpServletRequest request, HttpServletResponse response, Integer subId)
        throws IOException {
        GcUser user = this.getGcUser();
        boolean subjectCompleteStatus = true;
        GcSubject subject = gcSubjectService.getById(subId);
        Integer masterId = getHeaderMasterId(request);
        if (subject == null) {
            throw new SystemException(I18NUtil.get("powtoon.download.error"));
        }
        if (subject.getMasterId().intValue() != masterId.intValue()) {
            throw new SystemException(I18NUtil.get("powtoon.download.subject.error"));
        }

        if (null == subject.getCertificatesFlag() || TableConstant.COMMON_ONE != subject.getCertificatesFlag()) {
            throw new SystemException(I18NUtil.get("powtoon.download.unable.error"));
        }

        List<GcVideo> videoList = service.selectVideoPlayListBySubId(subId, user.getId());
        if (null != videoList && TableConstant.COMMON_ZERO != videoList.size()) {
            for (GcVideo video : videoList) {
                if (null == video.getPlayState() || video.getPlayState().equals(TableConstant.COMMON_ZERO)) {
                    subjectCompleteStatus = false;
                }
            }
        }
        List<GcEvent> eventList = eventService.selectEventByUserIdAndSubjectId(user.getId(), subId, masterId);
        if (null != eventList && TableConstant.COMMON_ZERO != eventList.size()) {
            for (GcEvent event : eventList) {
                if (null == event.getAnswerJson()) {
                    subjectCompleteStatus = false;
                }
            }
        }

        if (!subjectCompleteStatus) {
            throw new SystemException("Your course is not completed!");
        }
        subject.setSubjects(gcSubjectService.getChildSubjectBySubId(subject.getId()));
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date = new Date();
        String today = format.format(date);
        PdfModel model = new PdfModel();
        model.setUserName(user.getLastName() + " " + user.getFirstName());
        model.setCompletionDate(today);
        model.setContext(subject.getName()/*+" in less than "+String.format("%.2f",courseTotalTime)+" minutes"*/);
        ByteArrayOutputStream os = pdfServicePt.getPdfBytes(model);

        response.setContentType("application/pdf");
        response.setContentLength(os.size());
        response.setCharacterEncoding("utf-8");
        String name = URLEncoder.encode("Diploma-" + subject.getName(), "utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + name + ".pdf");
        ServletOutputStream out = response.getOutputStream();
        os.writeTo(out);
        out.flush();
        out.close();
    }

    @ApiOperation(value = "新UI视频-根据课程id加载视频信息", notes = "新UI视频-根据课程id加载视频信息", httpMethod = "POST")
    @PostMapping("subjectId/{subjectId}")
    public Message getVideosBySubject(@PathVariable("subjectId") Integer subjectId,
                                      @RequestBody Map<String, Object> param, HttpServletRequest request) {
        Integer masterId = request.getIntHeader("masterId");
        if (Objects.isNull(masterId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }
        if (Objects.isNull(subjectId)) {
            throw new SystemException(I18NUtil.get("powtoon.download.error"));
        }
        SysSystem system = this.getSystem();
        return gvgMasterService.getVideosBySubject(subjectId, param, request, system);
    }

    @GetMapping("/videoDetailPt")
    public Message videoDetailPt(HttpServletRequest request, Integer videoId) {
        SysSystem system = this.getSystem();
        String token = RequestUtil.getRequestAuthHeader(request);
        if (!StringUtils.isEmpty(token) && !"undefined".equals(token)) {
            GcUser user = this.getGcUser();
            GcMaster master = masterService.getById(RequestUtil.getMasterId(request).orElseThrow());
            GcVideo video = gcVideoService.findByVideoId(videoId);
            PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), master.getId());

            if (!authorizationService.checkAccess(video, PermitAction.VIEW, portalUser)) {
                throw new PermitException("No permission for this!");
            }
            return gvgMasterService.videoDetail(request, videoId, user, system, EnvType.PT.getCode());
        }

        return gvgMasterService.videoDetail(request, videoId, null, system, EnvType.PT.getCode());
    }

    @PostMapping("/selectVideosAndEvents")
    public Message selectVideosAndEvents(@RequestBody GcSubject subject, HttpServletRequest request) {
        Message message = new Message();
        GcUser user = this.getGcUser();
        if (Objects.isNull(subject.getId())) {
            throw new SystemException(I18NUtil.get("powtoon.topic.error"));
        }
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcVideo> gcVideos = gcVideoService.getVideoListBySubId(subject.getId());
        if (CollectionUtils.isNotEmpty(gcVideos)) {
            List<Integer> vids = gcVideos.stream().map(GcVideo::getId).collect(Collectors.toList());
            List<GcEvent> eventList = gcEventService.getEventListByVideoIds(vids, user.getId());
            for (GcVideo gcVideo : gcVideos) {
                List<GcEvent> gcEventList = new ArrayList<>();
                for (GcEvent gcEvent : eventList) {
                    if (gcEvent.getVideoId().equals(gcVideo.getId())) {
                        if (gcEventList.size() < 10) {
                            gcEventList.add(gcEvent);
                        }
                    }
                }
                List<GcEvent> events =
                    eventList.stream().filter(e -> e.getVideoId().equals(gcVideo.getId())).collect(Collectors.toList());
                List<GcEvent> gcEvents =
                    events.stream().filter(e -> e.getMyAnswer() != null).collect(Collectors.toList());
                gcVideo.setAnsweredSumNums(events.size());
                gcVideo.setAnsweredNums(gcEvents.size());
                PageInfo<GcEvent> pageInfo = new PageInfo<>(gcEventList);
                if (CollectionUtils.isNotEmpty(events)) {
                    pageInfo.setTotal(events.size());
                    BigDecimal page =
                        BigDecimal.valueOf(events.size()).divide(BigDecimal.valueOf(10), BigDecimal.ROUND_UP);
                    pageInfo.setPages(page.intValue());
                }
                gcVideo.setEventListPageInfo(pageInfo);
            }
        }
        PageInfo<GcVideo> pageInfo = new PageInfo<>(gcVideos);
        return message.ok().addData("taskVideoList", pageInfo);
    }

    @PostMapping("/selectEventsByVideoId")
    public Message selectEventsByVideoId(@RequestBody GcVideo gcVideo, HttpServletRequest request) {
        Message message = new Message();
        Integer masterId = request.getIntHeader("masterId");
        if (Objects.isNull(gcVideo.getId())) {
            throw new SystemException(I18NUtil.get("powtoon.savefolder.error"));
        }
        GcUser user = this.getGcUser();

        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcEvent> eventList =
            gcEventService.selectGetEventListAndSelfAnswerByVidFull(gcVideo.getId(), user.getId(), masterId);
        int totalEventNum = 0;
        int answeredEventNum = 0;
        if (eventList != null) {
            totalEventNum = eventList.size();
        }
        if (Objects.nonNull(user.getId())) {
            Map<Integer, Object> answerNumMap =
                gcEventService.videoEventsAnswerNumMap(gcVideo.getId(), user.getId(), masterId);

            GcUserAccess gcUserAccess = new GcUserAccess();
            GcAccess access = new GcAccess();
            access.setRoleType(AccessRoleType.STUDENT);
            gcUserAccess.setAccess(access);
            if (gcUserAccess.getAccess().getRoleType() == AccessRoleType.STUDENT) {//判断是否是老师用户，如果是老师用户则不会去查询已回答问题数量
                for (GcEvent event : eventList) {
                    Map numMap = (Map) answerNumMap.get(event.getId());
                    int num = 0;
                    if (numMap != null && numMap.get("answerNum") != null) {
                        num = ((Long) numMap.get("answerNum")).intValue();
                    }
                    event.setAnswerNum(num);
                    if (event.getMyAnswer() != null) {
                        answeredEventNum += 1;
                    }

                }
            }
        }
        PageInfo<GcEvent> pageInfo = new PageInfo<>(eventList);
        message.addData("totalEventNum", totalEventNum);
        message.addData("answeredEventNum", answeredEventNum);
        return message.ok().addData("eventList", pageInfo);

    }

    @GetMapping("/getFileDetailPt")
    public Message getFileDetail(Integer fileId, HttpServletRequest request) {
        GcUser user = this.getGcUser();
        SysFile file = sysFileService.getById(fileId);
        file.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(file));
        file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
        GcVideo videoContent = gcVideoService.getVideoContent(fileId).orElseThrow();
        Integer countLike = gcUserVideoActionService.countLikeForFile(videoContent.getId());
        GcUserVideoAction videoActionList =
            gcUserVideoActionService.getFileActionListByFileIdAndUserId(videoContent.getId(), user.getId());
        int isLiked = 0;
        if (null != videoActionList) {
            isLiked = 1;
        }
        file.setLikedFlag(isLiked);
        file.setIsLiked(isLiked);
        file.setLikeNum(countLike);
        return new Message().ok().addData("file", file);
    }

    private void populateVideoContent(HttpServletRequest request, SysFile videoFile, PortalUser portalUser) {
        videoFile.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(videoFile));
        videoFile.setFullFileUrl(sysFileService.getResFullUrl(videoFile, request));
        gcVideoService.getVideoContent(videoFile.getId()).ifPresent(videoContent -> {
            videoContent.setVideoFile(videoFile);
            videoFile.setVideoId(videoContent.getId());
            videoFile.setLikeNum(videoActionService.countLikeForVideo(videoContent.getId()));
            videoFile.setIsLiked(
                videoActionService.isLikedByUser(videoContent.getId(), portalUser.getUserId()) ? 1 : 0);
            Map<String, Boolean> permissions = authorizationService.listPermissions(videoContent, portalUser);
            videoContent.setPermissions(permissions);
            videoContent.getVideoFile().setPermissions(permissions);
            gcVideoService.updateVideoFilePrivacy(videoFile, videoContent);
        });

    }

    @ApiOperation(value = "logout", httpMethod = "GET")
    @GetMapping("/logout")
    public Message logout(HttpServletRequest request) {
        GcUser user = this.getGcUser();
        String accessToken = (String) redisOperator.get("PT:" + user.getUsername());
        Map<String, String> body = new HashMap<>();

        if (null != accessToken) {
            PtLoginConfig ptLoginConfig = ptLoginConfigService.getPopulatedPtLoginConfig(getHeaderMasterId(request));
            body.put("token", accessToken);
            body.put("client_id", ptLoginConfig.getClientId());
            try {
                HttpUtil.sendPostFormUrlencoded(ptLoginConfig.getPtRootUrl() + ptLoginConfig.getLogOut(), body);
            } catch (Exception e) {
                String msg = e.getMessage();
                throw new SystemException(I18NUtil.get("powtoon.S3upload.error") + msg);
            }
            redisOperator.del("PT:" + user.getUsername());
        }

        return new Message().ok();
    }

    @ApiOperation(value = "getPtMessage", httpMethod = "GET")
    @GetMapping("/getPtMessage")
    public Message getPtMessage(HttpServletRequest request) {
        PtLoginConfig ptLoginConfig = ptLoginConfigService.getPopulatedPtLoginConfig(getHeaderMasterId(request));
        return new Message().ok().addData("clientId", ptLoginConfig.getClientId())
            .addData("ptRootURL", ptLoginConfig.getPtRootUrl())
            .addData("test1027", "updated2022-10-27")
            .addData("ptLoginConfig", ptLoginConfig)
            .addData("测试", new Date());
    }

    @ApiOperation(value = "getNewAccessList", httpMethod = "GET")
    @GetMapping("/getNewAccessList")
    public Message getNewAccessList(HttpServletRequest request) {
        Integer masterId = Integer.parseInt(request.getHeader("masterid"));
        GcUser user = this.getGcUser();
        List<GcAccess> gcAccessList = gcAccessService.selectAccessLevel0(masterId, user.getId());
        return new Message().ok().addData("accessList", gcAccessList);
    }

    @ApiOperation(value = "getTeamAccessList", httpMethod = "GET")
    @GetMapping("/getTeamAccessList")
    public Message getTeamAccessList(String name, HttpServletRequest request) {
        Integer masterId = Integer.parseInt(request.getHeader("masterid"));
        GcUser user = this.getGcUser();
        PageInfo<GcAccess> accessList = null;
        PageParam pageParam = new PageParam(request);
        if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        accessList = new PageInfo<>(accessService.getTeamAccessList(name, masterId, user.getId(), request));
        return new Message().ok().addData("accessList", accessList);
    }

    @ApiOperation(value = "getTeamAccessSubjectNumList", httpMethod = "GET")
    @GetMapping("/getTeamAccessSubjectNumList")
    public Message getTeamAccessSubjectNumList(String name, HttpServletRequest request) {
        Integer masterId = Integer.parseInt(request.getHeader("masterid"));
        GcUser user = this.getGcUser();
        Integer adminFlag = gcUserAccessService.countUserAccessesByMasterIdAndRole(user.getId(), masterId,
            UserGroupRole.ORG_ADMIN.getRole());
        PageInfo<GcAccess> accessList = null;
        if (null != adminFlag && !adminFlag.equals(TableConstant.COMMON_ZERO)) {
            List<Integer> privateCourseIds = subService.getUserPublicSubject(masterId, user.getId());
            List<Integer> publicCourseIds = subService.getUserCreateSubjectAdmin(masterId, user.getId());
            List<Integer> subIds = subService.getUserCreateSubject(masterId, user.getId());
            PageParam pageParam = new PageParam(request);
            if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
                PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            }
            accessList = new PageInfo<>(
                accessService.getTeamAccessSubjectNumAdminList(name, masterId, user.getId(), privateCourseIds,
                    publicCourseIds, subIds));
        } else {
            List<Integer> subIds = subService.getUserCreateSubject(user.getId(), masterId);
            PageParam pageParam = new PageParam(request);
            if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
                PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            }
            accessList = new PageInfo<>(
                accessService.getTeamAccessSubjectNumList(name, masterId, user.getId(), subIds, request));
        }
        return new Message().ok().addData("accessList", accessList);
    }


    @ApiOperation(value = "getAccessList", httpMethod = "GET")
    @GetMapping("/getAccessList")
    public Message getAccessList(String name, HttpServletRequest request) {
        Integer masterId = Integer.parseInt(request.getHeader("masterid"));
        GcUser user = this.getGcUser();
        PageParam pageParam = new PageParam(request);

        if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }

        PageInfo<GcAccess> accessList = new PageInfo<>(accessService.listAccess(name, masterId, user.getId()));

        return new Message().ok().addData("accessList", accessList);
    }

    @ApiOperation(value = "getTeamUser", httpMethod = "GET")
    @PostMapping("/getTeamUser")
    public Message getTeamUser(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer masterId = Integer.parseInt(request.getHeader("masterid"));
        params.put("masterId", masterId);
        PageParam pageParam = new PageParam(request);
        if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        List<GcUser> userList = userService.getTeamUser(params, request);
        PageInfo<GcUser> pageInfo = new PageInfo<>(userList);
        return new Message().ok().addData("userList", pageInfo);
    }

    @ApiOperation(value = "getAllGroup", httpMethod = "GET")
    @PostMapping("/getAllGroup")
    public Message getAllGroup(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        GcUser currentUser = this.getGcUser();
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();

        params.put("masterId", masterId);
        List<GcAccess> contentGroupByMasterId = accessService.listAllAccess(params, request);
        params.put("userId", currentUser.getId());
        List<GcAccess> contentGroupsByMasterAndUserId = accessService.listAllAccess(params, request);

        updateCourseWithUsers(contentGroupByMasterId, contentGroupsByMasterAndUserId, request);

        return new Message().ok().addData("accessList", new PageInfo<>(contentGroupsByMasterAndUserId));
    }

    private void updateCourseWithUsers(List<GcAccess> contentGroupByMasterId,
                                       List<GcAccess> contentGroupsByMasterAndUserId, HttpServletRequest request) {
        Map<Integer, GcAccess> idToContentGroupByMasterId = contentGroupByMasterId.stream()
            .collect(Collectors.toMap(GcAccess::getId, Function.identity(), (key1, key2) -> key2, LinkedHashMap::new));

        contentGroupsByMasterAndUserId.forEach(contentGroup -> {
            if (idToContentGroupByMasterId.get(contentGroup.getId()) != null) {
                contentGroup.setUsers(idToContentGroupByMasterId.get(contentGroup.getId()).getUsers());
            }
            if (contentGroup.getUsers() != null) {
                contentGroup.getUsers().forEach(user -> sysFileService.getResFullUrl(user.getInfo().getAvatarFile(),
                    request));
            }
        });
    }

    @ApiOperation(value = "getCodeSubject", httpMethod = "GET")
    @GetMapping("/getCodeSubject")
    public Message getCodeSubject(String name, Integer type, Integer id, HttpServletRequest request)
        throws IOException {
        Integer masterId = Integer.parseInt(request.getHeader("masterid"));
        GcAccess access = accessService.getById(id);
        List<Integer> idList = new ArrayList<>();
        PageInfo<GcSubject> pageInfo;
        GcUser user = this.getGcUser();

        GcAccess contentGroup = accessService.getAccessById(access.getId());
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);
        if (!authorizationService.checkAccess(contentGroup, PermitAction.MANAGE_CONTENT, portalUser)) {
            throw new PermitException("No permission for this!");
        }

        List<Integer> courseAssignmentIds =
            contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(access.getId());
        List<Integer> mustAssignmentIds =
            contentGroupCourseAssignmentService.getMustCoursesContentGroupAssignmentIds(access.getId());
        List<Integer> optionalAssignmentIds =
            contentGroupCourseAssignmentService.getOptionalCoursesContentGroupAssignmentIds(access.getId());

        if (type == TableConstant.COMMON_ZERO) {
            if (!courseAssignmentIds.isEmpty()) {
                idList.addAll(courseAssignmentIds);
            }
        } else if (type == TableConstant.COMMON_ONE) {
            if (!mustAssignmentIds.isEmpty()) {
                idList.addAll(mustAssignmentIds);
            }
        } else {
            if (!optionalAssignmentIds.isEmpty()) {
                idList.addAll(optionalAssignmentIds);
            }
        }
        PageParam pageParam = new PageParam(request);
        if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        List<GcSubject> subjects = new ArrayList<>();
        Map<Integer, List<PtTags>> tagsMap = new HashMap<>();
        if (!idList.isEmpty()) {
            subjects = gcSubjectService.listSubByIdsAndName(idList, name);
        } else {
            subjects = gcSubjectService.listSubByIdsAndName(null, null);
        }
        List<Integer> subjectIdList = subjects.stream().map(GcSubject::getId).collect(Collectors.toList());
        if (!subjectIdList.isEmpty()) {
            QueryWrapper<PtTags> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("master_id", masterId);
            queryWrapper2.eq("type", TableConstant.COMMON_ONE);
            queryWrapper2.in("subject_id", subjectIdList);
            List<PtTags> tagsList = ptTagsService.list(queryWrapper2);
            tagsMap = tagsList.stream().collect(Collectors.groupingBy(PtTags::getSubjectId));
        }
        Map<Integer, List<PtTags>> finalTagsMap = tagsMap;
        subjects.forEach(i -> {
            if (mustAssignmentIds.contains(i.getId())) {
                i.setIsMustSubject(TableConstant.COMMON_ZERO);
            } else {
                i.setIsMustSubject(TableConstant.COMMON_ONE);
            }
            if (null != finalTagsMap.get(i.getId())) {
                List<PtTags> ptTagsList = finalTagsMap.get(i.getId());
                List<String> stringList = ptTagsList.stream().map(PtTags::getTagText).collect(Collectors.toList());
                i.setAllTags(stringList);
            }
            sysFileService.updateImageUrls(i, request);
        });
        pageInfo = new PageInfo<>(subjects);
        return new Message().ok().addData("pageInfo", pageInfo).addData("access", access);
    }

    @ApiOperation(value = "getCodeChannel", httpMethod = "GET")
    @GetMapping("/getCodeChannel")
    public Message getCodeChannel(String name, Integer accessId, HttpServletRequest request) throws IOException {
        Integer masterId = Integer.parseInt(request.getHeader("masterid"));
        GcAccess access = accessService.getById(accessId);
        GcUser user = this.getGcUser();

        GcAccess contentGroup = accessService.getAccessById(access.getId());
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);

        if (!authorizationService.checkAccess(contentGroup, PermitAction.MANAGE_CONTENT, portalUser)) {
            throw new PermitException("No permission for this!");
        }

        List<Integer> subscribedChannelIds =
            new ArrayList<>(contentGroupChannelSubscriptionService.getSubscribedChannelIds(access.getId()));

        PageParam pageParam = new PageParam(request);
        if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        List<PtChannel> channels;
        if (!subscribedChannelIds.isEmpty()) {
            channels = ptChannelService.selectChannelsByIdsAndName(subscribedChannelIds, name);
        } else {
            channels = ptChannelService.selectChannelsByIdsAndName(null, null);
        }
        Map<Integer, List<PtTags>> tagsMap = new HashMap<>();
        List<Integer> subjectIdList = channels.stream().map(PtChannel::getId).collect(Collectors.toList());
        if (!subjectIdList.isEmpty()) {
            QueryWrapper<PtTags> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("master_id", masterId);
            queryWrapper2.eq("type", TableConstant.COMMON_ONE);
            queryWrapper2.in("channel_id", subjectIdList);
            List<PtTags> tagsList = ptTagsService.list(queryWrapper2);
            tagsMap = tagsList.stream().collect(Collectors.groupingBy(PtTags::getChannelId));
        }
        Map<Integer, List<PtTags>> finalTagsMap = tagsMap;

        channels.forEach(channel -> {
            if (null != finalTagsMap.get(channel.getId())) {
                List<PtTags> ptTagsList = finalTagsMap.get(channel.getId());
                List<String> stringList = ptTagsList.stream().map(PtTags::getTagText).collect(Collectors.toList());
                channel.setAllTags(stringList);
            }
            sysFileService.updateImageUrls(channel, request);
        });
        PageInfo<PtChannel> pageInfo = new PageInfo<>(channels);
        return new Message().ok().addData("pageInfo", pageInfo).addData("access", access);
    }

    @ApiOperation(value = "getAvailableCourses", httpMethod = "GET")
    @GetMapping("/getAvailableCourses")
    public Message getAvailableCourses(String name, Integer accessId, String orderType, HttpServletRequest request) {
        Integer masterId = Integer.parseInt(request.getHeader("masterid"));
        List<Integer> idList = contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(accessId);
        GcUser user = this.getGcUser();
        List<GcSubject> subjects = new ArrayList<>();
        Integer adminFlag = gcUserAccessService.countUserAccessesByMasterIdAndRole(user.getId(), masterId,
            UserGroupRole.ORG_ADMIN.getRole());
        PageParam pageParam = new PageParam(request);
        if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        if (null != adminFlag && TableConstant.COMMON_ZERO != adminFlag) {
            subjects = gcSubjectService.getAvailableCourses(name, masterId, idList, null, orderType);
        } else {
            // All 'may' courses of the current user, not in the current group, regardless of whether they have management or viewing permissions, are queried out
            subjects = gcSubjectService.newGetAvailableCourses(name, masterId, idList, user.getId(), orderType);
        }

        Map<String, Object> videoParams = new HashMap<>(2);
        Integer ids = TableConstant.COMMON_ZERO;
        videoParams.put("ids", ids);
        videoParams.put("subjectIds", subjects.stream().map(GcSubject::getId).collect(Collectors.toList()));
        Map<Integer, GcUserVideoAction> subjectUserStar = videoActionService.getSubjectUserStar(videoParams);

        List<Integer> videoIdlist =
            gcVideoService.getVideoIdListBySubId(subjects.stream().map(GcSubject::getId).collect(Collectors.toList()));
        List<GcVideo> videoList = gcVideoService.getVideoLongListByVideoId(videoIdlist);
        if (null != user) {
            videoList =
                gcVideoService.buildVideoInfo(user.getId(), null, videoList, masterId, request, EnvType.PT.getCode());
        }
        Map<Integer, List<GcVideo>> groupBySubId = videoList.stream().filter(e -> null != e.getSubjectSubId())
            .collect(Collectors.groupingBy(GcVideo::getSubjectSubId));
        Map<Integer, GcUser> subjectUsers =
            gcUserService.getWatchedUserNum(subjects.stream().map(GcSubject::getId).collect(Collectors.toList()),
                masterId);

        Map<Integer, List<PtTags>> tagListMap;
        if (subjects.size() != TableConstant.COMMON_ZERO) {
            //tag
            QueryWrapper<PtTags> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("subject_id", subjects.stream().map(GcSubject::getId).collect(Collectors.toList()));
            queryWrapper.in("master_id", masterId);
            tagListMap = ptTagsService.list(queryWrapper).stream().collect(Collectors.groupingBy(PtTags::getSubjectId));
        } else {
            tagListMap = new HashMap<>();
        }


        subjects.forEach(i -> {

            if (null != tagListMap && null != tagListMap.get(i.getId())) {
                List<PtTags> tagsList = tagListMap.get(i.getId());
                List<String> textList = tagsList.stream().map(PtTags::getTagText).collect(Collectors.toList());
                i.setCourseTags(JSONArray.parseArray(JSON.toJSONString(textList)));
            }

            if (null != subjectUserStar.get(i.getId())) {
                GcUserVideoAction action = subjectUserStar.get(i.getId());
                i.setStarValue(action.getSubjectStarAvg());//星级平均值
                // 打星总人数
                i.setStarUsers(action.getSubjectStarUsers());
            } else {
                i.setStarValue(TableConstant.starValue0);//星级平均值
                i.setStarUsers(TableConstant.starUsers);
            }

            if (null != groupBySubId.get(i.getId())) {
                List<GcVideo> gcVideos = groupBySubId.get(i.getId());
                Integer totalSeconds =
                    gcVideos.stream().filter(a -> a.getVideoTime() != null).mapToInt(GcVideo::getVideoTime).sum();
                i.setVideosTotalLong(totalSeconds);
                i.setVideosTotalNum(gcVideos.size());
            } else {
                i.setVideosTotalLong(TableConstant.COMMON_ZERO);
                i.setVideosTotalNum(TableConstant.COMMON_ZERO);
            }
            if (null != subjectUsers.get(i.getId())) {
                i.setSubjectUsers(subjectUsers.get(i.getId()).getSubjectUsers());
            } else {
                i.setSubjectUsers(TableConstant.COMMON_ZERO);
            }
            sysFileService.getResFullUrl(i.getSubImgFile(), request);
            sysFileService.getVideoSnapshotUrl(i.getSubImgFile());
        });
        PageInfo<GcSubject> pageInfo = new PageInfo<>(subjects);
        return new Message().ok().addData("subjects", pageInfo);
    }

    @ApiOperation(value = "assignedSubject", httpMethod = "POST")
    @PostMapping("/assignedSubject")
    public Message assignedSubject(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer masterId = Integer.parseInt(request.getHeader("masterid"));
        Integer type = Integer.parseInt(params.get("type").toString());
        Integer accessId = Integer.parseInt(params.get("accessId").toString());
        List<Integer> idList = (List<Integer>) params.get("idList");
        GcUser user = this.getGcUser();

        GcAccess contentGroup = accessService.getById(accessId);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);

        if (!authorizationService.checkAccess(contentGroup, PermitAction.ADD_CONTENT, portalUser)) {
            throw new PermitException("No permission for this!");
        }

        if (null != contentGroup.getSubjectJson()) {
            List<Integer> subjectList = contentGroup.getSubjectJson().toJavaList(Integer.class);
            subjectList.addAll(idList);
            contentGroup.setSubjectJson(parseToJsonArray(subjectList));
        }
        if (type == TableConstant.COMMON_ZERO) {
            if (null != contentGroup.getMustSubjectJson()) {
                List<Integer> mustSubjectList = contentGroup.getMustSubjectJson().toJavaList(Integer.class);
                mustSubjectList.addAll(idList);
                contentGroup.setMustSubjectJson(parseToJsonArray(mustSubjectList));
            } else {
                contentGroup.setMustSubjectJson(parseToJsonArray(idList));
            }
        } else {
            if (null != contentGroup.getMaySubjectJson()) {
                List<Integer> maySubjectList = contentGroup.getMaySubjectJson().toJavaList(Integer.class);
                maySubjectList.addAll(idList);
                contentGroup.setMaySubjectJson(parseToJsonArray(maySubjectList));
            } else {
                contentGroup.setMaySubjectJson(parseToJsonArray(idList));
            }
        }

        gcAccessService.saveOrUpdate(contentGroup);
        contentGroupCourseAssignmentService.save(user, idList, accessId, CourseType.ofType(type));
        eventPublisherService.publishContentGroupUpdated(contentGroup.getId());
        eventPublisherService.publishCourseUpdated(idList);

        return new Message().ok();
    }

    @ApiOperation(value = "removeSubject", httpMethod = "POST")
    @PostMapping("/removeSubject")
    public Message removeSubject(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String idListString = params.get("idList").toString();
        List<Integer> idList = JSONArray.parseArray(idListString).toJavaList(Integer.class);
        GcAccess access = accessService.getById(Integer.parseInt(params.get("accessId").toString()));

        for (Integer integer : idList) {
            if (null != access.getSubjectJson()) {
                while (access.getSubjectJson().contains(integer)) {
                    access.getSubjectJson().remove(integer);
                }
            }
            if (null != access.getMustSubjectJson()) {
                while (access.getMustSubjectJson().contains(integer)) {
                    access.getMustSubjectJson().remove(integer);
                }
            }
            if (null != access.getMaySubjectJson()) {
                while (access.getMaySubjectJson().contains(integer)) {
                    access.getMaySubjectJson().remove(integer);
                }
            }
        }
        accessService.saveOrUpdate(access);
        contentGroupCourseAssignmentService.removeCourseAssignmentsByCourseId(access, idList);

        return new Message().ok();
    }

    @ApiOperation(value = "getPublicCodeChannel", httpMethod = "GET")
    @GetMapping("/getPublicCodeChannel")
    public Message getCodeChannel(Integer accessId, String name, HttpServletRequest request) {
        Integer masterId = Integer.parseInt(request.getHeader("masterid"));
        GcAccess access = accessService.getById(accessId);
        List<Integer> subscribedChannelIds =
            new ArrayList<>(contentGroupChannelSubscriptionService.getSubscribedChannelIds(access.getId()));

        GcUser user = this.getGcUser();
        Integer adminFlag = gcUserAccessService.countUserAccessesByMasterIdAndRole(user.getId(), masterId,
            UserGroupRole.ORG_ADMIN.getRole());
        List<PtChannel> channels;
        PageParam pageParam = new PageParam(request);
        if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        if (null != adminFlag && adminFlag > TableConstant.COMMON_ZERO) {
            channels = ptChannelService.selectChannelsByIdAndName(subscribedChannelIds, name, null, masterId);
        } else {
            // All channels of the current user are checked, regardless of whether they are manageable or viewable.
            channels = ptChannelService.selectChannelsByIdAndName(subscribedChannelIds, name, user.getId(), masterId);
        }

        channels.forEach(channel -> {
            if (Objects.nonNull(channel.getChannelImgFileId())) {
                updateChannelBackgroundImage(channel, request);
            }
            if (Objects.nonNull(channel.getChannelAvatarFileId())) {
                updateChannelAvatarImage(channel, request);
            }
        });

        return new Message().ok()
            .addData("pageInfo", new PageInfo<>(channels));
    }

    @ApiOperation(value = "assignedPtChannel", httpMethod = "POST")
    @PostMapping("/assignedPtChannel")
    public Message setPtChannel(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer masterId = Integer.parseInt(request.getHeader("masterid"));
        Integer accessId = Integer.parseInt(params.get("accessId").toString());
        GcAccess contentGroup = accessService.getById(accessId);
        List<Integer> channelIds = (List<Integer>) params.get("channelIds");
        GcUser user = this.getGcUser();
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);

        if (!authorizationService.checkAccess(contentGroup, PermitAction.ADD_CONTENT, portalUser)) {
            throw new PermitException("No permission for this!");
        }

        contentGroupChannelSubscriptionService.subscribeChannels(contentGroup, channelIds, user);
        eventPublisherService.publishContentGroupUpdated(contentGroup.getId());
        eventPublisherService.publishChannelUpdated(channelIds);
        return new Message().ok();
    }


    @ApiOperation(value = "removePtChannel", httpMethod = "POST")
    @PostMapping("/removePtChannel")
    public Message removePtChannel(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer masterId = Integer.parseInt(request.getHeader("masterid"));
        Integer accessId = Integer.parseInt(params.get("accessId").toString());
        GcAccess contentGroup = accessService.getById(accessId);
        GcUser user = this.getGcUser();
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);

        if (!authorizationService.checkAccess(contentGroup, PermitAction.MANAGE_CONTENT, portalUser)) {
            throw new PermitException("No permission for this!");
        }

        List<Integer> channelIds = (List<Integer>) params.get("channelIds");

        contentGroupChannelSubscriptionService.removeChannelsFromContentGroups(List.of(contentGroup), channelIds);
        eventPublisherService.publishContentGroupUpdated(contentGroup.getId());
        eventPublisherService.publishChannelUpdated(channelIds);

        return new Message().ok();
    }


    @GetMapping("/masterList")
    public Message masterList() {
        List<GcMaster> masterList = masterService.list();
        return new Message().ok().addData("masterList", masterList);
    }

    @GetMapping("/getPtGlobalConfig")
    public Message getPtGlobalConfig(Integer ptMasterId, HttpServletRequest request) {
        GcMaster master = gcMasterService.getById(ptMasterId);
        if (null == master) {
            return new Message().ok();
        }
        QueryWrapper<PtConfig> queryWrapper = new QueryWrapper<PtConfig>();
        queryWrapper.eq("master_id", master.getId());
        List<PtConfig> config = ptConfigService.list(queryWrapper);
        PtConfig ptConfig = new PtConfig();
        if (null == config || config.size() == TableConstant.COMMON_ZERO) {
            return new Message().ok().addData("master", master).addData("config", null);
        }
        if (null != config && config.size() == TableConstant.COMMON_ONE) {
            ptConfig = config.get(TableConstant.COMMON_ZERO);
        } else {
            ptConfig = config.get(config.size() - TableConstant.COMMON_ONE);
        }
        return new Message().ok().addData("master", master).addData("config", ptConfig).addData("test", "1");
    }

    @GetMapping("/getPtConfig")
    public Message getPtConfig(HttpServletRequest request) {
        QueryWrapper<PtConfig> queryWrapper = new QueryWrapper<PtConfig>();
        queryWrapper.isNotNull("master_id");
        queryWrapper.eq("id", TableConstant.COMMON_ONE);
        PtConfig config = ptConfigService.getOne(queryWrapper);
        if (null == config) {
            return new Message().ok().addData("config", null);
        }
        GcMaster master = masterService.getById(config.getMasterId());
        return new Message().ok().addData("config", config).addData("master", master);
    }

    @PostMapping("/savePtGlobalConfig")
    public Message savePtGlobalConfig(@RequestBody PtConfig ptConfig) {
        ptConfigService.saveOrUpdate(ptConfig);
        GcMaster master = gcMasterService.getById(ptConfig.getMasterId());
        return new Message().ok().addData("ptConfig", ptConfig).addData("master", master);
    }

    @GetMapping("/getUserListByCode")
    public Message getUserListByCode(Integer accessId, HttpServletRequest request) {
        Integer masterId = Integer.parseInt(request.getHeader("masterid"));
        GcAccess access = accessService.getById(accessId);
        QueryWrapper<GcUserAccess> queryWrapper = new QueryWrapper<GcUserAccess>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("access_id", access.getId());
        List<GcUserAccess> userAccessList = gcUserAccessService.list(queryWrapper);

        PageParam pageParam = new PageParam(request);
        if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        List<GcUser> gcUserList = gcUserService.getUserByUserAccessIds(
            userAccessList.stream().map(GcUserAccess::getId).collect(Collectors.toList()));
        gcUserList.forEach(i -> {
            sysFileService.getResFullUrl(i.getInfo().getAvatarFile(), request);
        });
        PageInfo<GcUser> pageInfo = new PageInfo<>(gcUserList);
        return new Message().ok().addData("userList", pageInfo);
    }

    @ApiOperation(value = "systemSettings", httpMethod = "GET")
    @GetMapping("/systemSettings")
    public Message systemSettings(HttpServletRequest request) {
        GcUser currentUser = this.getGcUser();
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("level", TableConstant.COMMON_TWO);

        Integer masterId = RequestUtil.getMasterId(request).orElse(null);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);
        List<SysMenu> sysMenuList = sysMenuService.getSysMenuListByMasterId(portalUser);

        if (sysMenuList.isEmpty()) {
            sysMenuList = sysMenuService.getSysMenuList(portalUser);
        }

        gcSubjectService.initJit();
        return new Message().ok()
            .addData("sysMenuList", sysMenuList);
    }

    @ApiOperation(value = "updateSettings", httpMethod = "POST")
    @PostMapping("/updateSettings")
    public Message updateSettings(@RequestBody List<SysMenu> sysMenu, HttpServletRequest request) {
        String masterid = request.getHeader("Masterid");
        List<SysMenu> masterList = sysMenuService.getByMaster(Integer.parseInt(masterid));
        sysMenu.forEach(i -> {
            if (masterList.isEmpty()) {
                i.setId(null);
            }
            i.setMasterId(Integer.parseInt(masterid));
        });
        sysMenuService.saveOrUpdateBatch(sysMenu);
        if (masterList.isEmpty()) {
            List<Integer> parentIdList = sysMenuService.getParentIdList(Integer.parseInt(masterid));
            List<SysMenu> ChildLevelList = sysMenuService.getChildLevelList(Integer.parseInt(masterid));
            ChildLevelList.forEach(i -> {
                if (i.getParentId() == 38) {
                    i.setParentId(parentIdList.get(TableConstant.COMMON_ZERO));
                } else if (i.getParentId() == 42) {
                    i.setParentId(parentIdList.get(TableConstant.COMMON_ONE));
                } else if (i.getParentId() == 44) {
                    i.setParentId(parentIdList.get(TableConstant.COMMON_TWO));
                }
            });
            sysMenuService.saveOrUpdateBatch(ChildLevelList);
        }
        return new Message().ok();
    }

    @ApiOperation(value = "updateMaySubject", httpMethod = "GET")
    @GetMapping("/updateMaySubject")
    public Message updateMaySubject(Integer subId, Integer accessId, Integer state, HttpServletRequest request) {
        GcAccess access = accessService.getById(accessId);
        //关闭
        if (TableConstant.COMMON_ZERO == state) {
            if (null != access.getMustSubjectJson()) {
                access.getMustSubjectJson().remove(subId);
            }
            if (null != access.getMaySubjectJson()) {
                access.getMaySubjectJson().add(subId);
            } else {
                JSONArray jsonArray = new JSONArray();
                jsonArray.add(subId);
                access.setMaySubjectJson(jsonArray);
            }
        } else {
            if (null != access.getMaySubjectJson()) {
                access.getMaySubjectJson().remove(subId);
            }
            if (null != access.getMustSubjectJson()) {
                access.getMustSubjectJson().add(subId);
            } else {
                JSONArray jsonArray = new JSONArray();
                jsonArray.add(subId);
                access.setMustSubjectJson(jsonArray);
            }
        }
        contentGroupCourseAssignmentService.updateCourseAssignmentMandatoryOpposite(subId, accessId);
        accessService.saveOrUpdate(access);
        return new Message().ok();
    }

    @ApiOperation(value = "getToken", httpMethod = "GET")
    @PostMapping("/getToken")
    public Message getToken(@RequestBody(required = false) AuthTokenDto authTokenDto,
                            HttpServletRequest request)
        throws IOException, ClientException {
        Integer masterId = getMaster(request).getId();
        PtLoginConfig loginConfig = ptLoginConfigService.getPopulatedPtLoginConfig(masterId);

        try {
            final String code = authTokenDto.getCode();

            if (code != null) {
                PowtoonAuthDto authInfo = getAuth(code, request.getHeader("redirectUri"), loginConfig);
                GcUser user = gcUserService.syncPowtoonUser(authInfo.getAccessToken(), loginConfig, masterId);
                createAuthInRedis(user, authInfo);
                updateUserAccessLoginTime(user, masterId);

                eventPublisherService.publishBiEvent(BiEventAction.LOGIN, user);
                return new Message().ok()
                    .addData("token", userService.generateJwtToken(user, masterId));
            }
        } catch (AuthenticationException e) {
            return new Message().error(401, e.getMessage());
        }

        return new Message().error(400, "Invalid code");
    }

    private PowtoonAuthDto getAuth(String code, String redirectUri, PtLoginConfig ptLoginConfig) {
        Map<String, String> parameters = getTokenRequestBody(code, redirectUri, ptLoginConfig);
        PowtoonAuthDto authInfo = getToken(ptLoginConfig, parameters);

        log.info("getTokenUrl:" + ptLoginConfig.getPtRootUrl() + ptLoginConfig.getOauthToken());
        log.info("code::" + code);
        log.info("token return:" + authInfo);

        String accessToken = authInfo.getAccessToken();
        if (accessToken == null) {
            throw new SystemException("Powtoon accessToken is null");
        }

        return authInfo;
    }


    private PowtoonAuthDto getToken(PtLoginConfig ptLoginConfig, Map<String, String> parameters) {
        String powtoonAuthResponse =
            HttpUtil.sendPostFormUrlencoded(ptLoginConfig.getPtRootUrl() + ptLoginConfig.getOauthToken(),
                parameters);
        if (powtoonAuthResponse == null) {
            throw new SystemException("Powtoon Token is null");
        }

        return JSON.parseObject(powtoonAuthResponse, PowtoonAuthDto.class);
    }

    private Map<String, String> getTokenRequestBody(String code, String redirectUri, PtLoginConfig loginConfig) {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("client_id", loginConfig.getClientId());
        parameters.put("client_secret", loginConfig.getClientSecret());
        parameters.put("grant_type", "authorization_code");
        parameters.put("redirect_uri", redirectUri);
        parameters.put("code", code);

        return parameters;
    }

    private List<Integer> getUserIds(List<GcUserAccess> userAccessList) {
        return userAccessList.stream()
            .map(GcUserAccess::getUserId)
            .collect(Collectors.toList());
    }

    private List<String> getContentGroupCodes(List<GcAccess> gcAccessesList) {
        return gcAccessesList.stream().map(GcAccess::getCode).collect(Collectors.toList());
    }

    private void createAuthInRedis(GcUser user, PowtoonAuthDto authInfo) {
        redisOperator.set("PT:" + user.getUsername(), authInfo.getAccessToken());
        redisOperator.set("access_token_userid" + user.getId(), authInfo.getAccessToken(),
            authInfo.getExpiresIn());
        redisOperator.set("PT_refresh_token:" + user.getUsername(), authInfo.getRefreshToken());
    }

    private GcMaster getMaster(HttpServletRequest request) {
        Optional<Integer> masterIdOptional = RequestUtil.getMasterId(request);
        if (masterIdOptional.isEmpty()) {
            return gcMasterService.getMaster("Powtoon");
        }

        return gcMasterService.getMasterById(masterIdOptional.get());
    }

    private void updateUserAccessLoginTime(GcUser user, Integer masterId) {
        List<GcUserAccess> userAccessList = gcUserAccessService.getAccessListByUserAndMasterId(user.getId(), masterId);
        List<Integer> idList = userAccessList.stream().map(GcUserAccess::getId).collect(Collectors.toList());
        List<GcUserAccessExt> extList = new ArrayList<>();
        for (Integer integer : idList) {
            GcUserAccessExt ext = new GcUserAccessExt();
            ext.setUserAccessId(integer);
            ext.setLogInTime(new Date());
            extList.add(ext);
        }
        gcUserAccessExtService.saveBatch(extList);
    }

    @GetMapping("/getSubjectNameIndex")
    public Message getSubjectNameIndex(@Param("name") String name, HttpServletRequest request) {
        GcMaster master = masterService.getById(RequestUtil.getMasterId(request).get());
        Integer count = subService.getSubjectNameIndex(master.getId(), name);
        if (TableConstant.COMMON_ZERO != count) {
            return new Message().error(TableConstant.SUBJECT_ERROR_CODE,
                I18NUtil.get("subject.index.error").replace("{name}", name));
        }
        return new Message().ok();
    }

    @ApiOperation(value = "获取课程页面数据", httpMethod = "GET")
    @GetMapping("/getCoursesInfo")
    public Message getCoursesInfo(HttpServletRequest request) {
        GcUser user = this.getGcUser();
        GcMaster master = masterService.getById(RequestUtil.getMasterId(request).get());
        List<Integer> mustCourseIds = contentGroupCourseAssignmentService.getMustCourseIds(user.getId(), master.getId(), UserGroupRole.GROUP_MEMBER);
        Integer mustSubjectSize = TableConstant.COMMON_ZERO;
        if (!mustCourseIds.isEmpty()) {
            mustSubjectSize = subjectService.getSubjectNum(mustCourseIds);
        }
        List<Integer> channelIdList = new ArrayList<>();
        SysSystem system = this.getSystem();
        List<String> nameList = new ArrayList<>();
        nameList.add("channelIds");
        List<GcMasterHomeInfo> gcMasterHomeInfos =
            iGcMasterHomeInfoService.getGcMasterHomeInfoList(master.getId(), nameList, system, request);
        if (CollectionUtils.isNotEmpty(gcMasterHomeInfos)) {
            channelIdList = gcMasterHomeInfos.get(TableConstant.COMMON_ZERO).getChannelIds().toJavaList(Integer.class);
        }
        List<Integer> publicSubjectIds = gcSubjectService.getPublicSubjectIds(master.getId());

        Integer progressNum = gcSubjectService.inProgressNum(user.getId(), master.getId());

        Integer published =
            gcSubjectService.getCreateUserPublished(user.getId(), master.getId(), TableConstant.COMMON_ONE);

        Integer myDrafts =
            gcSubjectService.getCreateUserPublished(user.getId(), master.getId(), TableConstant.COMMON_ZERO);


        List<Integer> courseIds = contentGroupCourseAssignmentService.getMustCourseIds(user.getId(), master.getId(), UserGroupRole.GROUP_MEMBER);
        Integer DiscoverNum =
            gcSubjectService.selectSubjectPt(null, TableConstant.COMMON_FOUR, TableConstant.gcSubject_state_visible_1,
                null, master.getId(), user.getId(), channelIdList, courseIds);
        Integer completedNum =
            gcSubjectService.selectSubjectPt(null, TableConstant.COMMON_TWO, TableConstant.COMMON_ONE, null,
                master.getId(), user.getId(), channelIdList, publicSubjectIds);

        Integer MyAssignmentNew = gcSubjectService.getNewMyAssignmentNew(master.getId(), user.getId());

        QueryWrapper<PtViewSubject> queryWrapper = new QueryWrapper<PtViewSubject>();
        queryWrapper.eq("master_id", master.getId());
        queryWrapper.eq("user_id", user.getId());
        List<PtViewSubject> subjectList = viewSubjectService.list(queryWrapper);
        subjectList.forEach(i -> {
            courseIds.add(i.getSubjectId());
        });

        Integer DiscoverNew =
            gcSubjectService.selectSubjectPt(null, TableConstant.COMMON_FOUR, TableConstant.gcSubject_state_visible_1,
                null, master.getId(), user.getId(), channelIdList, courseIds);

        return new Message().ok()
            .addData("MyAssignments", mustSubjectSize)
            .addData("InProgress", progressNum)
            .addData("Published", published)
            .addData("Drafts", myDrafts)
            .addData("DiscoverNum", DiscoverNum)
            .addData("CompletedNum", completedNum)
            .addData("MyAssignmentNew", MyAssignmentNew)
            .addData("DiscoverNew", DiscoverNew);
    }

    @ApiOperation(value = "添加课程或者话题", httpMethod = "POST")
    @PostMapping("/saveSub")
    public Message saveSub(@RequestBody @ApiParam(name = "创建主题", value = "主题结构") GcSubject course,
                           HttpServletRequest request) throws IOException, PermitApiError, PermitContextError {
        GcMaster master = this.getMaster();
        Integer masterId = null;
        GcUser user = this.getGcUser();

        if (null == master && null != request.getHeader("masterId")) {
            masterId = Integer.parseInt(request.getHeader("masterId"));
        } else {
            masterId = master.getId().intValue();
        }
        master = masterService.getById(masterId);
        boolean isFlag = false;
        List<String> ids = new ArrayList<>();
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), master.getId());
        user.setIsOrgAdmin(portalUser.isOrgAdmin());
        gcSubjectService.populateUserId(course, user);

        if (null != course.getId() && null == course.getMoveDrafts()) {
            GcSubject oldSubject = gcSubjectService.getById(course.getId());
            List<String> mustAccessList = new ArrayList<>();
            List<String> accessListMay = new ArrayList<>();
            if (null != course.getAllPublished() && course.getAllPublished() == TableConstant.COMMON_ZERO) {
                if (user.getIsOrgAdmin()) {
                    accessListMay = getContentGroupCodes(gcAccessService.findAccessListByMasterId(masterId));
                } else {
                    accessListMay = getContentGroupCodes(gcAccessService.listAccess(null, masterId, user.getId()));
                }
                if (accessListMay.size() != TableConstant.COMMON_ZERO) {
                    ids.addAll(accessListMay);
                }
            }
            if (null != course.getAllPublishedMay() && course.getAllPublishedMay().equals(TableConstant.COMMON_ZERO)) {
                if (user.getIsOrgAdmin()) {
                    mustAccessList = getContentGroupCodes(gcAccessService.findAccessListByMasterId(masterId));
                } else {
                    mustAccessList = getContentGroupCodes(gcAccessService.listAccess(null, masterId, user.getId()));
                }
                if (mustAccessList.size() != TableConstant.COMMON_ZERO) {
                    ids.addAll(mustAccessList);
                }
            }

            if (null != course.getAccessIds() && course.getAccessIds().size() != TableConstant.COMMON_ZERO &&
                null == course.getAllPublishedMay()) {
                ids.addAll(getContentGroupCodes(gcAccessService.listByIds(course.getAccessIds())));
            }
            if (null != course.getMustAccessIds() && course.getMustAccessIds().size() != TableConstant.COMMON_ZERO &&
                null == course.getAllPublished()) {
                ids.addAll(getContentGroupCodes(gcAccessService.listByIds(course.getMustAccessIds())));
            }
            if (!oldSubject.getState().equals(course.getState()) && null == course.getFid()) {
                isFlag = authorizationService.checkAccess(course, PermitAction.ADD_CONTENT, portalUser);
                if (null == oldSubject.getPublishedTime() && course.getState().equals(TableConstant.COMMON_ONE)) {
                    course.setPublishedTime(new Date());
                    course.setPublishedUserId(user.getId());
                }
            } else {
                //修改
                eventPublisherService.publishCourseUpdated(course.getId());
                isFlag = authorizationService.checkAccess(course, PermitAction.EDIT, portalUser);
            }
        } else if (null != course.getMoveDrafts()) {
            //移动回发布前
            GcSubject subject = gcSubjectService.getById(course.getId());
            if ((user.getIsOrgAdmin() && null != course.getMoveDrafts()) ||
                (user.getId().equals(subject.getCreateUser()))) {
                isFlag = true;
            }
        } else {
            //创建
            isFlag = authorizationService.checkAccess(course, PermitAction.ADD_CONTENT, portalUser);
        }
        if (!isFlag) {
            throw new PermitException("No permission for this!");
        }
        if (null != course.getMoveDrafts() && course.getMoveDrafts().equals(TableConstant.COMMON_ZERO)) {
            course = gcSubjectService.getById(course.getId());
            course.setState(TableConstant.COMMON_ZERO);
            gcAccessService.deleteSubIdAccess(masterId, course.getId());
            contentGroupCourseAssignmentService.removeByMasterAndCourseId(masterId, course.getId());
        }
        if (course.getState() != null && course.getState() == TableConstant.COMMON_ONE) {
            contentGroupCourseAssignmentService.save(user, course, CourseType.MANDATORY);
            contentGroupCourseAssignmentService.save(user, course, CourseType.OPTIONAL);
        }
        gcSubjectService.saveSubInfo(course, null, master, user, request);

        return new Message().ok("添加成功！").addData("sync", course);
    }

    @ApiOperation(value = "添加视频记录以及其下的节点", httpMethod = "Post")
    @DeleteMapping("/delSub/{id}")
    public Message deleteSub(@PathVariable("id") Integer subId, HttpServletRequest request) throws IOException {
        GcMaster master = this.getMaster();
        Integer masterId = request.getIntHeader("masterId");
        GcUser user = this.getGcUser();
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);
        GcAccess contentGroup = gcAccessService.getById(subId);

        if (!authorizationService.checkAccess(contentGroup, PermitAction.DELETE, portalUser)) {
            throw new PermitException("No permission for this!");
        }

        if (Objects.isNull(master)) {
            master = new GcMaster();
            master.setId(masterId);
        }
        eventPublisherService.publishContentGroupUpdated(subId);

        return gvgMasterService.deleteSub(subId, EnvType.GC.getCode(), master, null);
    }

    @PostMapping("/videoComment")
    public Message videoComment(@RequestBody JSONObject jsonRequest, HttpServletRequest request) throws IOException {
        Integer vid = jsonRequest.getInteger("vid");
        Integer masterId = getHeaderMasterId(request);
        String comment = jsonRequest.getString("comment");
        Integer fileId = jsonRequest.getInteger("fileId");
        ApiAssert.notNull(vid, "参数vid缺失");
        GcUser user = this.getGcUser();
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);
        GcVideo video = gcVideoService.findByVideoId(vid);

        if (!authorizationService.checkAccess(video, PermitAction.COMMENT, portalUser)) {
            throw new PermitException("No permission for this!");
        }
        GcVideoComment videoComment = new GcVideoComment();
        videoComment.setMasterId(masterId);
        videoComment.setUserId(user.getId());
        videoComment.setComment(comment);
        videoComment.setVideoId(vid);
        videoComment.setCreateTime(OffsetDateTime.now());
        videoComment.setUpdateTime(OffsetDateTime.now());
        if (null != fileId) {
            videoComment.setFileId(fileId);
            SysFile file = sysFileService.getById(fileId);
            file.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(file));
            videoComment.setCommentFile(file);
        }
        if (videoCommentService.saveVideoComment(videoComment)) {
            return new Message().ok("评论成功！").addData("comment", videoComment);
        } else {
            return new Message().error("评论失败！");
        }
    }

    @PostMapping("/newContentFolder")
    public Message newContentFolder(@RequestBody GcUserSaveFolder playlist, HttpServletRequest request) {
        ApiAssert.notNull(playlist.getName(), "The folder name cannot be empty!");
        GcUser user = this.getGcUser();
        GcMaster master = masterService.getById(RequestUtil.getMasterId(request).get());
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), master.getId());

        playlist.setUserId(user.getId());
        playlist.setMasterId(getHeaderMasterId(request));

        if (!authorizationService.checkAccess(playlist, PermitAction.CREATE, portalUser)) {
            throw new PermitException("No permission for this!");
        }

        try {
            userLicenseService.checkPlaylistLimit(playlist, portalUser);
            if (gcUserSaveFolderService.saveOrUpdate(playlist)) {
                return new Message().ok("Saved successfully")
                    .addData("folder", playlist);
            }
        } catch (LicenseLimitExceededException e) {
            return new Message().error(HttpStatus.FORBIDDEN.value(), e.getMessage());
        }

        return new Message().ok();
    }

    @ApiOperation(value = "添加视频记录以及其下的节点", httpMethod = "Post")
    @PostMapping("/createVideoPlayRecordAndNode")
    public Message createVideoPlayRecordAndNode(@RequestBody GcUserVideoPlay userVideoPlay,
                                                HttpServletRequest request) {
        Integer masterId = request.getIntHeader("masterId");
        if (Objects.isNull(masterId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }
        GcUser user = this.getGcUser();
        return gvgMasterService.createVideoPlayRecordAndNode(userVideoPlay, request, EnvType.PT.getCode(), user,
            masterId, this.getSystem());
    }

    @ApiOperation(value = "回答问题", httpMethod = "POST")
    @PostMapping("/answerQuestion")
    public Message answerQuestion(@RequestBody JSONObject jsonRequest, HttpServletRequest request) {
        Integer masterId = request.getIntHeader("masterId");
        if (Objects.isNull(masterId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }
        Integer eventId = jsonRequest.getInteger("eventId");
        if (Objects.isNull(eventId)) {
            throw new SystemException(I18NUtil.get("powtoon.answer.error"));
        }
        return gvgMasterService.answerQuestion(jsonRequest, request, request.getIntHeader("masterId"), this.getGcUser(),
            EnvType.PT.getCode(), this.getSystem());
    }

    @ApiOperation(value = "Get a list of the contents of a single playlist", httpMethod = "POST")
    @PostMapping("/getContentFromOneFolder")
    public Message getContentFromOneFolder(@RequestBody GcUserSaveFolder gcUserSaveFolder, HttpServletRequest request) {
        if (Objects.isNull(gcUserSaveFolder.getId())) {
            throw new SystemException(I18NUtil.get("powtoon.folder.error"));
        }
        String token = request.getHeader("Authorization");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!StringUtils.isEmpty(token) && !"undefined".equals(token)) {
            GcUser gcUser = getGcUser();
            return gcMasterService.getContentFromOneFolder(gcUserSaveFolder, gcUser, request, EnvType.PT.getCode())
                .addData("systemTime", df.format(new Date()));
        }

        return gcMasterService.getContentFromOneFolder(gcUserSaveFolder, null, request, EnvType.PT.getCode())
            .addData("systemTime", df.format(new Date()));
    }

    @ApiOperation(value = "查询自己创建的所有二级课程")
    @PostMapping("/selectAllTopicList")
    public Message selectAllTopicList(@RequestBody GcSubject gcSubject, HttpServletRequest request) {
        Integer masterId = request.getIntHeader("masterId");
        if (Objects.isNull(masterId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }
        if (Objects.isNull(gcSubject.getId())) {
            throw new SystemException(I18NUtil.get("powtoon.topic.error"));
        }
        GcMaster master = this.getMaster();
        if (Objects.isNull(master) && Objects.nonNull(masterId)) {
            master = new GcMaster();
            master.setId(masterId);
        }
        GcUser user = this.getGcUser();

        return new Message().ok().addData("topicList",
            subService.selectAllTopicList(masterId, TableConstant.COMMON_ONE, user.getId(), gcSubject));
    }

    @ApiOperation(value = "查询自己创建的所有二级课程")
    @GetMapping("/selectAllSub0ListByUserId")
    public Message selectAllSub0ListByUserId(HttpServletRequest request) {
        GcMaster master = this.getMaster();
        Integer masterId = request.getIntHeader("masterId");
        if (Objects.isNull(masterId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }
        if (Objects.isNull(master) && Objects.nonNull(masterId)) {
            master = new GcMaster();
            master.setId(masterId);
        }
        GcUser user = this.getGcUser();
        return new Message().ok().addData("sub0List",
            subService.selectAllSub0ListByUserId(masterId, TableConstant.COMMON_ZERO, user.getId()));
    }

    @ApiOperation(value = "批量更新问题")
    @GetMapping("/updateEventsTime")
    public Message updateEventsTime(@RequestBody List<GcEvent> eventList, HttpServletRequest request) {
        Integer masterId = request.getIntHeader("masterId");
        if (Objects.isNull(masterId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }
        GcMaster master = this.getMaster();
        if (Objects.isNull(master) && Objects.nonNull(masterId)) {
            master = new GcMaster();
            master.setId(masterId);
        }
        Integer videoId = eventList.get(0).getVideoId();
        return new Message().ok().addData("eventList", this.eventService.saveOrUpdateBatch(eventList));
    }

    @ApiOperation(value = "批量更新问题")
    @PostMapping("/getSysFile")
    public Message getSysFile(@RequestBody JSONObject jsonParams, HttpServletRequest request) {
        Integer masterId = request.getIntHeader("masterId");
        GcUser user = this.getGcUser();
        if (Objects.isNull(masterId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }
        GcMaster master = this.getMaster();
        if (Objects.isNull(master) && Objects.nonNull(masterId)) {
            master = new GcMaster();
            master.setId(masterId);
        }
        return new Message().ok().addData("eventList",
            gvgMasterService.getSysFile(jsonParams, EventUnifyType.POWTOON_RES_TYPES, request, master, user.getId()));
    }

    @PostMapping("/delSub")
    public Message deleteSub(@RequestBody GcSubject course, HttpServletRequest request) throws IOException {
        Integer masterId = request.getIntHeader("masterId");
        if (Objects.isNull(masterId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }
        GcMaster master = this.getMaster();
        if (Objects.isNull(master) && Objects.nonNull(masterId)) {
            master = new GcMaster();
            master.setId(masterId);
        }
        GcUser user = this.getGcUser();
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), master.getId());
        gcSubjectService.populateUserId(course, user);

        if (!authorizationService.checkAccess(course, PermitAction.DELETE, portalUser)) {
            throw new PermitException("No permission for this!");
        }
        eventPublisherService.publishCourseUpdated(course.getId());

        if (Objects.nonNull(course.getFid())) {
            GcSubject gcSubject = subService.getById(course.getId());
            GcSubject gcSubject0 = subService.getById(course.getFid());
            gcSubject.setMasterId(gcSubject0.getMasterId());
            gcSubject.setSubId(course.getFid());
            gcSubject.setFid(course.getFid());
            if (subService.saveOrUpdate(gcSubject)) {
                return new Message().ok();
            }
        } else {
            return gvgMasterService.deleteSub(course.getId(), EnvType.PT.getCode(), master, this.getGcUser().getId());

        }
        return new Message().error();
    }

    @DeleteMapping("/delVideo/{id}")
    public Message deleteVideo(@PathVariable("id") Integer vid, HttpServletRequest request) throws IOException {
        GcMaster master = this.getMaster();
        Integer masterId = request.getIntHeader("masterId");
        if (Objects.isNull(master)) {
            master = new GcMaster();
            master.setId(masterId);
        }
        GcUser user = this.getGcUser();
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), master.getId());
        GcVideo video = gcVideoService.findByVideoId(vid);

        if (!authorizationService.checkAccess(video, PermitAction.DELETE, portalUser)) {
            throw new PermitException("No permission for this!");
        }

        Message message =
            gvgMasterService.deleteVideoPt(vid, EnvType.PT.getCode(), this.getGcUser().getId(), master.getId(),
                request);
        eventPublisherService.publishVideoUpdated(vid);

        return message;
    }

    @GetMapping("/getBySlug")
    public Message getBySlug(String channelSlug, HttpServletRequest request) {
        Integer masterId = request.getIntHeader("masterId");
        int channelId = ptChannelService.findBySlugAndMasterId(channelSlug, masterId).getId();
        return new Message().ok().addData("id", channelId);
    }

    @ApiOperation(value = "channel新增修改")
    @PostMapping("/saveOrUpdateChannel")
    public Message saveOrUpdateChannel(@RequestBody PtChannel channel, HttpServletRequest request) {
        Message message = new Message();
        Integer masterId = request.getIntHeader("masterId");
        Integer userId = AuthorizationUtil.getUserUid(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(userId, masterId);

        checkChannelPermission(channel, userId, portalUser);

        GcMaster master = masterService.getById(masterId);
        channel.setMasterId(masterId);
        if (Objects.isNull(master)) {
            master = new GcMaster();
            master.setId(masterId);
        }

        try {
            if (Objects.nonNull(channel.getVisibleFlag())) {
                if (channel.isCertainTeams()) {
                    saveOrUpdateCertainTeamsChannel(channel, portalUser, masterId, userId);
                } else if (channel.isPublic()) {
                    saveOrUpdatePublicChannel(channel, portalUser, masterId, userId);
                } else if (channel.isPrivate()) {
                    updateChannel(channel, request, masterId, portalUser);
                }

                channel = getChannelWithPermissions(channel.getId(), request, masterId, portalUser);
                message.addData("channel", channel);
                return message.ok();
            }

            updateChannel(channel, request, masterId, portalUser);
            message.addData("channel", channel);
            return message.ok();
        } catch (DuplicateKeyException e) {
            throw new SystemException(I18NUtil.get("userpt.channel.slug"));
        } catch (LicenseLimitExceededException e) {
            message.error(HttpStatus.FORBIDDEN.value(), "License limit exceeded");
            return message;
        }
    }

    private void saveOrUpdatePublicChannel(PtChannel channel, PortalUser portalUser, Integer masterId, Integer userId) {
        userLicenseService.checkChannelLimit(channel, portalUser);

        updateContentGroupIdsToAssign(channel, masterId, userId, portalUser.isOrgAdmin() && channel.isPublic());

        ptChannelService.saveOrUpdate(channel);

        if (CollectionUtils.isNotEmpty(channel.getSubscribeAccessIdList())) {
            contentGroupChannelSubscriptionService.saveChannelSubscription(
                channel.getSubscribeAccessIdList(), channel.getId(), userId);
        }
    }

    private void saveOrUpdateCertainTeamsChannel(PtChannel channel, PortalUser portalUser, Integer masterId,
                                                 Integer userId) {
        userLicenseService.checkChannelLimit(channel, portalUser);
        ptChannelService.saveOrUpdate(channel);

        updateContentGroupIdsToAssign(channel, masterId, userId, portalUser.isOrgAdmin() && channel.isPublic());

        List<GcAccess> existingAssignedContentGroups = gcAccessService.getAccessByChannelId(masterId, channel.getId());
        contentGroupChannelSubscriptionService.removeChannelsFromContentGroups(existingAssignedContentGroups,
            List.of(channel.getId()));

        // Publish channel to team
        if (CollectionUtils.isNotEmpty(channel.getAccessIdList())) {
            contentGroupChannelSubscriptionService.savePublicChannels(
                channel.getAccessIdList(), channel.getId(), userId);
        }

        if (CollectionUtils.isNotEmpty(channel.getSubscribeAccessIdList())) {
            contentGroupChannelSubscriptionService.saveChannelSubscription(
                channel.getSubscribeAccessIdList(), channel.getId(), userId);
        }
    }

    private void updateContentGroupIdsToAssign(PtChannel channel, Integer masterId, Integer userId, boolean orgAdmin) {
        if ((channel.getIsAllSubscribe() == null || !channel.getIsAllSubscribe().equals(TableConstant.COMMON_ZERO)) &&
            (channel.getIsAllChoose() == null || !channel.getIsAllChoose().equals(TableConstant.COMMON_ZERO))) {
            return;
        }

        List<Integer> contentGroupIds = getContentGroupsAssignChannelTo(masterId, userId, orgAdmin);
        if (channel.getIsAllChoose() != null && channel.getIsAllChoose().equals(TableConstant.COMMON_ZERO)) {
            channel.setAccessIdList(contentGroupIds);
            return;
        }

        channel.setSubscribeAccessIdList(contentGroupIds);
    }

    private List<Integer> getContentGroupsAssignChannelTo(Integer masterId, Integer userId, boolean publicOrgAdmin) {
        if (publicOrgAdmin) {
            return accessService.findAccessListByMasterId(masterId).stream()
                .map(GcAccess::getId)
                .collect(Collectors.toList());
        }

        return accessService.listAccess(null, masterId, userId).stream()
            .map(GcAccess::getId)
            .collect(Collectors.toList());
    }

    private PtChannel getChannelWithPermissions(Integer channelId, HttpServletRequest request, Integer masterId,
                                                PortalUser portalUser) {
        PtChannel channel = ptChannelService.selectChannelDetail(channelId, null, request, null, masterId);
        channel.setPermissions(authorizationService.listPermissions(channel, portalUser));
        return channel;
    }

    private void updateChannel(PtChannel channel, HttpServletRequest request, Integer masterId, PortalUser portalUser) {
        ptChannelService.saveOrUpdate(channel);
        getChannelWithPermissions(channel.getId(), request, masterId, portalUser);
    }

    private void updateChannelAvatarImage(PtChannel channel, HttpServletRequest request) {
        SysFile avatarFile = sysFileService.getById(channel.getChannelAvatarFileId());
        String avatarFullFileUrl = sysFileService.getResFullUrl(avatarFile, request);
        avatarFile.setFullFileUrl(avatarFullFileUrl);
        channel.setAvatarFile(avatarFile);
    }

    private void updateChannelBackgroundImage(PtChannel channel, HttpServletRequest request) {
        SysFile sysFile = sysFileService.getById(channel.getChannelImgFileId());
        String imgFullFileUrl = sysFileService.getResFullUrl(sysFile, request);
        channel.setImgFullFileUrl(imgFullFileUrl);
    }

    private void checkChannelPermission(PtChannel channel, Integer userId, PortalUser portalUser) {
        ptChannelService.populateCreatedUserId(channel, userId);
        boolean isAllowed;

        if (channel.getId() != null) {
            checkChannelPublishPermission(channel, portalUser);
            isAllowed = authorizationService.checkAccess(channel, PermitAction.EDIT, portalUser);
            eventPublisherService.publishChannelUpdated(channel.getId());
        } else if (channel.isSection()) {
            isAllowed = authorizationService.checkAccess(channel, PermitAction.ADD_CONTENT, portalUser);
        } else {
            isAllowed = authorizationService.checkAccess(channel, PermitAction.CREATE, portalUser);
        }

        if (!isAllowed) {
            throw new PermitException("No permission for this!");
        }
    }

    private void checkChannelPublishPermission(PtChannel channel, PortalUser portalUser) {
        PtChannel existingChannel = ptChannelService.getById(channel.getId());
        if (existingChannel == null) {
            throw new SystemException("Channel doesn't exist.");
        }
        if (channel.getVisibleFlag() != null &&
            !channel.getVisibleFlag().equals(existingChannel.getVisibleFlag()) &&
            !authorizationService.checkAccess(existingChannel, PermitAction.PUBLISH, portalUser)) {
            throw new PermitException("No permission to change channel visibility!");
        }
    }

    @PostMapping("/deleteChannelSection")
    public Message deleteChannelSection(@RequestBody PtChannel channel, HttpServletRequest request) {
        Message message = new Message();
        if (Objects.isNull(channel.getId())) {
            throw new SystemException(I18NUtil.get("powtoon.channel.noChannelId"));
        }
        GcUser user = this.getGcUser();
        GcMaster master = masterService.getById(RequestUtil.getMasterId(request).get());
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), master.getId());

        ptChannelService.populateCreatedUserId(channel, user.getId());
        if (!authorizationService.checkAccess(channel, PermitAction.DELETE, portalUser)) {
            throw new PermitException("No permission for this!");
        }

        eventPublisherService.publishChannelUpdated(channel.getId());
        if (ptChannelService.removeById(channel.getId())) {
            return message.ok("success");
        }

        return message.error();
    }

    @SneakyThrows
    @ApiOperation(value = "channel首页")
    @PostMapping("/selectAllChannels")
    public Message selectAllChannels(HttpServletRequest request) {
        Message message = new Message();
        Integer masterId = request.getIntHeader("masterId");
        GcUser user = this.getGcUser();
        List<PtChannel> channels =
            ptChannelService.indexPtChannels(user.getId(), TableConstant.COMMON_ZERO, request, masterId);
        List<PtChannel> myChannels =
            ptChannelService.indexPtChannels(user.getId(), TableConstant.COMMON_ONE, request, masterId);
        PageInfo channelPageInfo = new PageInfo<>(channels);
        PageInfo myChannelPageInfo = new PageInfo<>(myChannels);
        message.ok().addData("myChannels", myChannelPageInfo);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return message.ok()
            .addData("allChannelList", channelPageInfo)
            .addData("systemTime", df.format(new Date()));
    }

    @ApiOperation(value = "指定teamchannel查询")
    @PostMapping("/selectChannelsByTeam")
    public Message selectAllChannels(@RequestBody GcAccess access, HttpServletRequest request) {
        Message message = new Message();
        GcUser gcuser = this.getGcUser();
        Integer masterId = request.getIntHeader("masterId");
        if (Objects.isNull(masterId)) {
            throw new SystemException(I18NUtil.get("guidecore.master.noMasterId"));
        }
        List<PtChannel> channels =
            ptChannelService.selectChannelsByTeam(access.getId(), masterId, gcuser.getId(), request);
        PageInfo<PtChannel> pageInfo = new PageInfo<>(channels);
        return message.ok().addData("channelPageInfo", pageInfo);
    }


    @ApiOperation(value = "查询categorylist", notes = "查询categorylist", httpMethod = "GET")
    @GetMapping("/selectCategoryList")
    public Message selectCategoryList() {
        Message message = new Message();
        List<GcCategory> gcCategories = gcProblemService.selectCategoryList();
        return message.ok().addData("categotyList", gcCategories);
    }

    @GetMapping("/selectPtchannelTags")
    public Message selectPtchannelTags(String name, HttpServletRequest request) {
        Message message = new Message();
        Integer masterId = request.getIntHeader("masterId");
        if (Objects.isNull(masterId)) {
            throw new SystemException(I18NUtil.get("guidecore.master.noMasterId"));
        }
        List<PtTags> ptTags = ptTagsService.selectPtChannelTags(masterId, request, name);
        PageInfo<PtTags> pageInfo = new PageInfo<>(ptTags);
        return message.ok().addData("tagPageInfo", pageInfo);
    }

    @PostMapping("/selectChannelDetail")
    public Message selectChannelDetail(@RequestBody PtChannel ptChannel, HttpServletRequest request) {
        Message message = new Message();
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();

        if (ptChannel.getChannelSlug() != null) {
            PtChannel existingChannel = ptChannelService.findBySlugAndMasterId(ptChannel.getChannelSlug(), masterId);
            ptChannel.setId(existingChannel.getId());
            ptChannel.setVisibleFlag(existingChannel.getVisibleFlag());
            ptChannel.setCreateUserId(existingChannel.getCreateUserId());
        }

        GcUser user = this.getGcUser();
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);
        if (!authorizationService.checkAccess(ptChannel, PermitAction.VIEW, portalUser)) {
            throw new PermitException("No permission for this!");
        }

        String order = request.getHeader("order");
        List<PtChannel> channelSections = new ArrayList<>();
        if (ptChannel.getChannelSlug() != null) {
            channelSections = ptChannelService.selectSectionList(null, ptChannel.getChannelSlug(), request, masterId);
        }
        if (ptChannel.getId() != null) {
            channelSections = ptChannelService.selectSectionList(ptChannel.getId(), null, request, null);
        }

        PageInfo<PtChannel> sectionPageInfo = new PageInfo<>(channelSections);
        PtChannel channel = new PtChannel();

        if (null != ptChannel.getChannelSlug()) {
            channel = ptChannelService.selectChannelDetail(null, ptChannel.getChannelSlug(), request, order, masterId);
        }
        if (null != ptChannel.getId()) {
            channel = ptChannelService.selectChannelDetail(ptChannel.getId(), null, request, order, masterId);
        }
        channel.setPermissions(authorizationService.listPermissions(channel, portalUser));

        PtChannelSubscribe ptChannelSubscribe;
        if (null != ptChannel.getId()) {
            ptChannelSubscribe = ptChannelSubscribeService.selectIfSubscribe(user.getId(), ptChannel.getId());
        } else {
            int channelId = ptChannelService.findBySlugAndMasterId(ptChannel.getChannelSlug(), masterId).getId();
            ptChannelSubscribe = ptChannelSubscribeService.selectIfSubscribe(user.getId(), channelId);
        }

        if (Objects.nonNull(ptChannelSubscribe)) {
            channel.setFollowFlag(TableConstant.COMMON_ONE);
        } else {
            channel.setFollowFlag(TableConstant.COMMON_ZERO);
        }

        channel.setSectionPageInfo(sectionPageInfo);
        if (user.getId().equals(channel.getCreateUserId())) {
            channel.setOwnFlag(TableConstant.COMMON_ONE);
        } else {
            channel.setOwnFlag(TableConstant.COMMON_ZERO);
        }
        updateChannelBackgroundImage(channel, request);

        return message.ok()
            .addData("channel", channel)
            .addData("systemTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    @ApiOperation(value = "查询section中的视频list")
    @PostMapping("/selectVideosInSection")
    public Message selectVideosInChannel(@RequestBody PtChannel ptChannel, HttpServletRequest request)
        throws IOException {
        Integer masterId = request.getIntHeader("masterId");
        if (Objects.isNull(masterId)) {
            throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
        }
        GcUser user = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);
        Message message = new Message();
        PtChannel channel = ptChannelService.getById(ptChannel.getId());
        String order = request.getHeader("order");

        List<SysFile> videoList;
        if (Objects.isNull(ptChannel.getSearchName())) {
            videoList =
                ptChannelService.selectVideosInSection(ptChannel.getId(), order, request, ptChannel.getSearchName(),
                    null, portalUser);
        } else {
            videoList =
                ptChannelService.selectVideosInSection(ptChannel.getId(), order, request, ptChannel.getSearchName(),
                    channel.getLevel(), portalUser);
        }
        if (null != videoList && !videoList.isEmpty()) {
            QueryWrapper<PtTags> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("master_id", masterId);
            queryWrapper2.in("file_id", videoList.stream().map(SysFile::getId).collect(Collectors.toList()));
            queryWrapper2.eq("type", TableConstant.COMMON_TWO);
            List<PtTags> list = ptTagsService.list(queryWrapper2);

            Map<Integer, List<PtTags>> tagMap = list.stream().collect(Collectors.groupingBy(PtTags::getFileId));
            videoList.forEach(i -> {
                if (null != tagMap.get(i.getId())) {
                    List<PtTags> tagsList = tagMap.get(i.getId());
                    List<String> strings = tagsList.stream().map(PtTags::getTagText).collect(Collectors.toList());
                    i.setCourseTags(strings);
                }
            });

        }

        return message.ok().addData("videos", new PageInfo<>(videoList));
    }

    @ApiOperation(value = "channel订阅")
    @PostMapping("/channelSubscribe")
    public Message channelSubscribe(@RequestBody PtChannelSubscribe ptChannelSubscribe, HttpServletRequest request) {
        Message message = new Message();
        GcUser user = this.getGcUser();
        Integer masterId = request.getIntHeader("masterId");
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);
        PtChannel channel = ptChannelService.getById(ptChannelSubscribe.getChannelId());

        if (!authorizationService.checkAccess(channel, PermitAction.SUBSCRIBE, portalUser)) {
            throw new PermitException("No permission for this!");
        }

        ptChannelSubscribeService.subscribe(user, ptChannelSubscribe.getChannelId());

        return message.error();
    }

    @ApiOperation(value = "channel取消订阅")
    @PostMapping("/channelUnSubscribe")
    public Message channelUnSubscribe(@RequestBody PtChannelSubscribe ptChannelSubscribe, HttpServletRequest request) {
        Message message = new Message();
        GcUser user = this.getGcUser();
        Integer masterId = request.getIntHeader("masterId");
        PtChannel channel = ptChannelService.getById(ptChannelSubscribe.getChannelId());
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);

        if (!authorizationService.checkAccess(channel, PermitAction.SUBSCRIBE, portalUser)) {
            throw new PermitException("No permission for this!");
        }

        ptChannelSubscribe.setUserId(user.getId());
        ptChannelSubscribeService.unsubscribe(user, ptChannelSubscribe.getChannelId());

        return message.error();
    }

    @ApiOperation(value = "channelContent保存内容")
    @PostMapping("/saveOrUpdateChannelContent")
    public Message saveChannelContent(@RequestBody List<PtChannelContent> ptChannelContent,
                                      HttpServletRequest request) {
        Message message = new Message();
        if (CollectionUtils.isEmpty(ptChannelContent)) {
            throw new SystemException(I18NUtil.get("powtoon.channel.noChannelContent"));
        }
        GcUser user = this.getGcUser();
        Integer masterId = request.getIntHeader("masterId");
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);
        Integer channelId;
        PtChannel channel;

        if (null != ptChannelContent.get(TableConstant.COMMON_ZERO).getChannelId()) {
            channel = ptChannelService.getById(ptChannelContent.get(TableConstant.COMMON_ZERO).getChannelId());
            if (channel.isSection()) {
                channel = ptChannelService.getById(channel.getFid());
                channelId = channel.getId();
            } else {
                channelId = ptChannelContent.get(TableConstant.COMMON_ZERO).getChannelId();
            }
        } else {
            return new Message().error(400, "Channel ID is required");
        }

        eventPublisherService.publishChannelUpdated(channel.getId());
        if (!authorizationService.checkAccess(channel, PermitAction.EDIT, portalUser)) {
            throw new PermitException("No permission for this!");
        }

        List<Integer> fileIds = ptChannelContent.stream().map(PtChannelContent::getFileId).collect(Collectors.toList());
        List<PtChannelContent> channelContentList =
            ptChannelContentService.selectContentExist(ptChannelContent.get(TableConstant.COMMON_ZERO).getChannelId());
        List<Integer> ids = channelContentList.stream().map(PtChannelContent::getFileId).collect(Collectors.toList());
        for (PtChannelContent channelContent : ptChannelContent) {
            if (ids.contains(channelContent.getFileId())) {
                throw new SystemException(I18NUtil.get("powtoon.channel.duplicate.video.error"));
            }
        }
        List<PtTags> tagsList = new ArrayList<>();

        List<SysFile> sysFileList = sysFileService.selectBatch(fileIds);
        ptChannelContentService.saveOrUpdateChannelContent(ptChannelContent, sysFileList, channelId, portalUser);

        for (PtChannelContent channelContent : ptChannelContent) {
            channelContent.getCourseTags().forEach(tag -> tagsList.add(createTag(channelContent, tag, masterId)));
            updateVideoFileUrls(request, channelContent);
        }
        ptTagsService.saveOrUpdateBatch(tagsList);
        return message.ok("success").addData("contentList", ptChannelContent);
    }

    private PtTags createTag(PtChannelContent channelContent, Object i, Integer masterId) {
        PtTags newTags = new PtTags();
        newTags.setMasterId(masterId);
        newTags.setTagText(i.toString());
        newTags.setChannelId(channelContent.getChannelId());
        newTags.setType(TableConstant.COMMON_TWO);
        newTags.setOrder(TableConstant.COMMON_ZERO);
        newTags.setFileId(channelContent.getFileId());
        return newTags;
    }

    private void updateVideoFileUrls(HttpServletRequest request, PtChannelContent channelContent) {
        SysFile sysFile = channelContent.getVideoFile();
        String fullFileUrl = sysFileService.getResFullUrl(sysFile, request);
        String snapShotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
        sysFile.setFullFileUrl(fullFileUrl);
        sysFile.setSnapshotUrl(snapShotUrl);
        sysFile.setThumbNailUrl(thumbnailProvider.getThumbnailUrl(sysFile));
        channelContent.setVideoFile(sysFile);

        if (null != sysFile.getGcUser().getAvatarFileId()) {
            SysFile file = sysFileService.getById(sysFile.getGcUser().getAvatarFileId());
            sysFile.getGcUser().setAvatarFullFileUrl(sysFileService.getResFullUrl(file, request));
        }
    }

    @ApiOperation(value = "channelContent删除内容")
    @PostMapping("/deleteChannelContent")
    public Message deleteChannelContent(@RequestBody PtChannelContent ptChannelContent, HttpServletRequest request) {
        if (Objects.isNull(ptChannelContent)) {
            throw new SystemException(I18NUtil.get("powtoon.channel.noChannelContent"));
        }

        GcUser user = this.getGcUser();
        Integer masterId = request.getIntHeader("masterId");
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);
        PtChannel channel = ptChannelService.getById(ptChannelContent.getChannelId());

        if (!authorizationService.checkAccess(channel, PermitAction.DELETE, portalUser)) {
            throw new PermitException("No permission for this!");
        }
        if (ptChannelContentService.deleteContent(ptChannelContent.getFileId(), ptChannelContent.getChannelId())) {
            return new Message().ok("success");
        }

        eventPublisherService.publishChannelUpdated(channel.getId());
        return new Message().error();
    }

    @ApiOperation(value = "Video details page")
    @PostMapping("/contentVideoDetail")
    public Message contentVideoDetail(@RequestBody PtChannelContent ptChannelContent, HttpServletRequest request) {
        Message message = new Message();
        if(Objects.isNull(ptChannelContent.getId())){
			throw new SystemException(I18NUtil.get("powtoon.channel.noChannelContent"));
		}

		ptChannelContent = ptChannelContentService.getById(ptChannelContent.getId());
		Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
		GcUser currentUser = this.getGcUser();
		PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

		GcVideo channelVideoContent = gcVideoService.findByVideoId(ptChannelContent.getContentId());
		if (!authorizationService.checkAccess(channelVideoContent, PermitAction.VIEW, portalUser)) {
			throw new PermitException("No permission for this!");
		}

		PtChannel ptchannel = ptChannelService.getById(ptChannelContent.getChannelId());
		GcUser channelCreator = userService.getById(ptchannel.getCreateUserId());
		GcUserInfo gcUserInfo = gcUserInfoService.getById(channelCreator.getInfoId());
		if (null!=gcUserInfo.getAvatarFileId()) {
			gcUserInfo.setAvatarFile(sysFileService.getById(gcUserInfo.getAvatarFileId()));
			sysFileService.getResFullUrl(gcUserInfo.getAvatarFile(),request);
		}

		channelCreator.setInfo(gcUserInfo);
        ptchannel.setCreateUser(channelCreator);
		SysFile videoFile = gcVideoService.updateVideoFile(request, channelVideoContent, portalUser);
		GcUserVideoAction gcUserVideoAction = gcUserVideoActionService.getOldChannelVideoAction(ptChannelContent.getContentId(),currentUser.getId(),TableConstant.COMMON_ONE);
        if (Objects.nonNull(gcUserVideoAction)) {
            videoFile.setLikedFlag(TableConstant.COMMON_ONE);
        } else {
            videoFile.setLikedFlag(TableConstant.COMMON_ZERO);
        }
        message.ok().addData("thisVideo", videoFile);

        PtChannel ptChannel = new PtChannel();
        if (Objects.nonNull(ptChannelContent.getChannelId())) {
            ptChannel = ptChannelService.getById(ptChannelContent.getChannelId());
            String channelSnapShotUrl =
                sysFileService.getResFullUrl(sysFileService.getById(ptChannel.getChannelImgFileId()), request);
            ptChannel.setImgFullFileUrl(channelSnapShotUrl);
        }
        ptChannel.setCreateUser(channelCreator);
        if (ptChannel.isSection()) {
            PtChannel channel = ptChannelService.getById(ptchannel.getFid());
            ptChannel.setChannelSlug(channel.getChannelSlug());
            ptChannel.setPermissions(authorizationService.listPermissions(ptChannel, portalUser));
        }
        message.ok().addData("channel", ptChannel);
        List<SysFile> videofiles =
            ptChannelContentService.selectVideosInChannel(ptChannel.getId(), null, ptChannelContent.getFileId(),
                request, currentUser.getId());
        for (SysFile sysFile : videofiles) {
            populateVideoContent(request, sysFile, portalUser);
        }
        message.ok().addData("videoList", new PageInfo<>(videofiles));
        return message.ok().addData("systemTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    @ApiOperation(value = "视频点赞")
    @PostMapping("/likeVideoInChannel")
    public Message likeVideoInChannel(@RequestBody GcUserVideoAction gcUserVideoAction, HttpServletRequest request) {
        Message message = new Message();
        GcUser user = this.getGcUser();
        if (Objects.isNull(gcUserVideoAction.getFileId())) {
            throw new SystemException(I18NUtil.get("powtoon.channel.noContentFileId"));
        }
        GcVideo videoContent = gcVideoService.getVideoContent(gcUserVideoAction.getFileId()).orElseThrow();
        gcUserVideoAction.setContentId(videoContent.getId());

        GcUserVideoAction oldAction =
            videoActionService.getOldChannelVideoAction(gcUserVideoAction.getContentId(), user.getId(),
                gcUserVideoAction.getType());
        if (Objects.nonNull(oldAction)) {
            videoActionService.deleteChannelOldVideoAction(gcUserVideoAction.getContentId(), user.getId(),
                gcUserVideoAction.getType());
        } else {
            gcUserVideoAction.setUserId(user.getId());
            videoActionService.saveOrUpdate(gcUserVideoAction);
        }
        return message.ok();
    }

    @ApiOperation(value = "修改视频名字")
    @PostMapping("/updateVideoName")
    public Message likeVideoInChannel(@RequestBody SysFile videoFile, HttpServletRequest request) {
        Message message = new Message();
        if (Objects.isNull(videoFile.getId())) {
            throw new SystemException(I18NUtil.get("powtoon.channel.noContentFileId"));
        }
        SysFile file = sysFileService.getById(videoFile.getId());
        file.setName(videoFile.getName());
        sysFileService.saveOrUpdate(file);
        return message.ok();
    }

    @ApiOperation(value = "作业本事件回复内容-新", httpMethod = "POST")
    @PostMapping("/getEventResListForWorkBook")
    public Message getEventResListForWorkBook(@RequestBody JSONObject jsonRequest, HttpServletRequest request) {
        Integer studentId = jsonRequest.getInteger("studentId");//分享班级中分享方老师的userid
        Integer teacherUserId = jsonRequest.getInteger("teacherUserId");
        GcUser user = this.getGcUser();
        Integer masterId = request.getIntHeader("masterId");
        Integer eventId = jsonRequest.getInteger("eventId");
        ApiAssert.notNull(eventId, "事件id不可空");
        List<GcUserEventResource> list = new ArrayList<>();
        //查询自己的评论
        list = gcUserEventResourceService.getEventResListForWorkBook(eventId, this.getGcUser().getId(), studentId,
            masterId, request, TableConstant.COMMON_ONE);
        //查询别人的评论
        List<GcUserEventResource> gcUserEventResourceList =
            gcUserEventResourceService.getEventResListForWorkBook(eventId, this.getGcUser().getId(), studentId,
                masterId, request, TableConstant.COMMON_TWO);
        PageInfo<GcUserEventResource> pageInfo = new PageInfo<>(list);
        PageInfo<GcUserEventResource> otherPageInfo = new PageInfo<>(gcUserEventResourceList);
        return new Message().ok().addData("eventResList", pageInfo).addData("otherPageInfo", otherPageInfo);
    }

    @GetMapping("/deleteContentFromOneFolder")
    public Message deleteFolder(Integer folderId) {
        ApiAssert.notNull(folderId, "folderId cannot be empty");
        Integer userId = this.getGcUser().getId();

        GcUserSaveFolder gcUserSaveFolder = new GcUserSaveFolder();
        gcUserSaveFolder.setId(folderId);
        gcUserSaveFolder.setUserId(userId);

        if (gcUserSaveFolderService.countFolder(gcUserSaveFolder) == 0) {
            return new Message().error("The user folderId does not exist");
        }

        if (gcUserSaveFolderService.removeById(folderId)) {
            return new Message().ok("Successfully deleted");
        }

        return new Message().error("Failed to delete");
    }

    @ApiOperation(value = "删除单个playList视频", httpMethod = "GET")
    @GetMapping("/deleteVideoFromOneFolder")
    public Message deleteVideoFromOneFolder(Integer folderId, Integer videoId) {
        ApiAssert.notNull(folderId, "folderId不可空");
        QueryWrapper<GcUserSaveContent> queryWrapper = new QueryWrapper<GcUserSaveContent>();
        queryWrapper.eq("video_id", videoId);
        queryWrapper.eq("folder_id", folderId);

        if (gcUserSaveContentService.remove(queryWrapper)) {
            return new Message().ok("删除成功");
        } else {
            return new Message().error("删除失败");
        }
    }
}


