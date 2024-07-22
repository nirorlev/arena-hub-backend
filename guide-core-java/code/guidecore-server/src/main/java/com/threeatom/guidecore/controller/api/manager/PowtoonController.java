package com.threeatom.guidecore.controller.api.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.PermitException;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.jwt.JwtUtil;
import com.threeatom.common.pdf.PdfModel;
import com.threeatom.common.pdf.PdfServicePt;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.config.PermitConfiguration;
import com.threeatom.guidecore.enums.CourseType;
import com.threeatom.guidecore.constant.AccessRoleType;
import com.threeatom.guidecore.constant.ActionsType;
import com.threeatom.guidecore.constant.EnvType;
import com.threeatom.guidecore.constant.EventUnifyType;
import com.threeatom.guidecore.constant.GroupsType;
import com.threeatom.guidecore.constant.ResourceType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.controller.user.vo.Groups;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.controller.user.vo.PermissionsVo;
import com.threeatom.guidecore.controller.user.vo.PtGroupsVo;
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
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.entity.PtChannelContent;
import com.threeatom.guidecore.entity.PtChannelSubscribe;
import com.threeatom.guidecore.entity.PtConfig;
import com.threeatom.guidecore.entity.PtLoginConfig;
import com.threeatom.guidecore.entity.PtTags;
import com.threeatom.guidecore.entity.PtViewSubject;
import com.threeatom.guidecore.entity.SysMenu;
import com.threeatom.guidecore.service.ContentGroupChannelSubscriptionService;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcEventService;
import com.threeatom.guidecore.service.GcMasterHomeInfoService;
import com.threeatom.guidecore.service.GcMasterService;
import com.threeatom.guidecore.service.GcProblemService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserAccessExtService;
import com.threeatom.guidecore.service.GcUserAccessPermissionService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.GcUserAnswerService;
import com.threeatom.guidecore.service.GcUserEventResourceService;
import com.threeatom.guidecore.service.GcUserFabulousService;
import com.threeatom.guidecore.service.GcUserInfoService;
import com.threeatom.guidecore.service.GcUserNoteCommentService;
import com.threeatom.guidecore.service.GcUserSaveContentFollowService;
import com.threeatom.guidecore.service.GcUserSaveContentService;
import com.threeatom.guidecore.service.GcUserSaveFolderService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.GcUserVideoActionService;
import com.threeatom.guidecore.service.GcUserVideoPlaysNodeService;
import com.threeatom.guidecore.service.GcVideoCommentService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.GvgMasterService;
import com.threeatom.guidecore.service.NewUiGcSubjectService;
import com.threeatom.guidecore.service.PtChannelContentService;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.service.PtChannelSubscribeService;
import com.threeatom.guidecore.service.PtConfigService;
import com.threeatom.guidecore.service.PtLoginConfigService;
import com.threeatom.guidecore.service.PtTagsService;
import com.threeatom.guidecore.service.PtViewSubjectService;
import com.threeatom.guidecore.service.SysMenuService;
import com.threeatom.guidecore.service.VideoThumbnailProvider;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.guidecore.util.RequestUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import com.threeatom.utils.HttpUtil;
import io.permit.sdk.Permit;
import io.permit.sdk.PermitConfig;
import io.permit.sdk.api.PermitApiError;
import io.permit.sdk.api.PermitContextError;
import io.permit.sdk.api.models.CreateOrUpdateResult;
import io.permit.sdk.enforcement.User;
import io.permit.sdk.openapi.models.RoleAssignmentRead;
import io.permit.sdk.openapi.models.TenantCreate;
import io.permit.sdk.openapi.models.TenantRead;
import io.permit.sdk.openapi.models.UserRead;
import io.permit.sdk.openapi.models.UserRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
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
	private NewUiGcSubjectService newUiGcSubjectService;

	@Autowired
	private GcUserVideoPlaysNodeService gcUserVideoPlaysNodeService;

	@Autowired
	private GcUserNoteCommentService gcUserNoteCommentService;

	@Autowired
	private GcSubjectService gcSubjectService;

	@Autowired
	private GcUserAccessPermissionService gcUserAccessPermissionService;

	@Autowired
	private GcUserAccessService gcUserAccessService;

	@Autowired
	private GcUserFabulousService gcUserFabulousService;

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
	private DataSource dataSource;

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
	private GcUserAnswerService userAnswerService;

	@Autowired
	private Environment env;

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
	private PermitConfiguration permitConfiguration;
	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private GcVideoCommentService videoCommentService;

	@Autowired
	private GcUserVideoActionService UserVideoActionService;

	@Autowired
	private PtViewSubjectService viewSubjectService;

	@Autowired
	private GcUserAccessExtService gcUserAccessExtService;

	@Autowired
	private  GcContentGroupCourseAssignmentService contentGroupCourseAssignmentService;

	@Autowired
	private ContentGroupChannelSubscriptionService contentGroupChannelSubscriptionService;

	@Autowired
	private VideoThumbnailProvider thumbnailProvider;


	@ApiOperation(value="搜索视频", notes = "搜索视频，复用gc环境的搜索", httpMethod = "POST")
	@PostMapping("search")
	public Message searchVideo(@RequestBody Map<String, Object> params, HttpServletRequest request) {
		//复用
		String portalId = request.getHeader("masterId");
		if(Objects.isNull(portalId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		String token = request.getHeader("Authorization");
		SysSystem system = this.getSystem();
		if (null != token && !"".equals(token) && !"undefined".equals(token)){
			GcUser gcUser = this.getGcUser();
			return gvgMasterService.searchResultPt(params,request,gcUser,system,EnvType.PT.getCode()).addData("date:::",new Date());
		}
		return gvgMasterService.searchResultPt(params,request,null,system,EnvType.PT.getCode());
	}

	@ApiOperation(value="新UI课程首页-包括课程名称查询接口", notes = "新UI课程首页", httpMethod = "POST")
	@PostMapping("/portalInfosUnlogin")
	public Message portalInfosUnlogin(@RequestBody JSONObject requestParams, HttpServletRequest request) {
		//复用
		SysSystem system = this.getSystem();
		String portalId = requestParams.getString("portalId");
		if(Objects.isNull(portalId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		String token = request.getHeader("Authorization");
		if (null != token && !"".equals(token) && !"undefined".equals(token)){
			GcUser user = this.getGcUser();
			return gvgMasterService.portalInfosUnlogin(requestParams,request,system,user,EnvType.PT.getCode()).addData("times",new Date());
		}
		return gvgMasterService.portalInfosUnlogin(requestParams,request,system,null,EnvType.PT.getCode()).addData("times",new Date());
	}

	@ApiOperation(value="新UI课程首页-包括课程名称查询接口", notes = "新UI课程首页", httpMethod = "POST")
	@PostMapping("/newSubjectIndex")
	public Message newSubjectIndex(@RequestBody JSONObject requestParams, HttpServletRequest request){
		Message message = new Message();
		Integer type = Integer.parseInt(requestParams.get("type").toString());
		JSONArray jsonArray = JSONObject.parseArray(JSON.toJSONString(requestParams.get("groupCodeList")));
		//课程状态
		Integer subjectState = null;
		if (null!=requestParams.get("subjectState")){
			subjectState = Integer.parseInt(requestParams.get("subjectState").toString());
		}
		//课程名搜索
		String name = null;
		if (null!=requestParams.get("name")){
			name = requestParams.get("name").toString();
		}
		List<String> groupCodeList = new ArrayList<>();
		if (null!=jsonArray){
			groupCodeList = jsonArray.toJavaList(String.class);
		}

		String selectType = null;
		if (null!=requestParams.get("selectType")){
			selectType = requestParams.get("selectType").toString();
		}
		GcUser user = this.getGcUser();
		Integer masterId = Integer.parseInt(request.getHeader("masterId"));
		//组装课程数据
		List<GcSubject> subjectList = new ArrayList<>();
		switch (type){
			//my learnings
			case TableConstant.COMMON_ZERO:
				//active
				if (null==selectType||selectType.equals("activeSubject")){
					List<GcSubject> activeSubject = gcSubjectService.selectActiveSubject(user.getId(),masterId,subjectState,name,request);
					PageInfo<GcSubject> activeSubjectPageInfo = new PageInfo<>(activeSubject);
					subjectList.addAll(activeSubject);
					message.addData("activeSubject",activeSubjectPageInfo);
				}
				//completed
				if (null==selectType||selectType.equals("completedSubject")){
					List<GcSubject> completedSubject = gcSubjectService.selectCompletedSubject(user.getId(),masterId,subjectState,name,request);
					PageInfo<GcSubject> completedSubjectPageInfo = new PageInfo<>(completedSubject);
					subjectList.addAll(completedSubject);
					message.addData("completedSubject",completedSubjectPageInfo);
				}
				//discover
				if (null==selectType||selectType.equals("discoverSubject")){
					List<GcSubject> discoverSubject = gcSubjectService.selectDiscoverSubject(user.getId(),masterId,subjectState,name,request);
					PageInfo<GcSubject> discoverSubjectPageInfo = new PageInfo<>(discoverSubject);
					subjectList.addAll(discoverSubject);
					message.addData("discoverSubject",discoverSubjectPageInfo);
				}
				//组装课程数据
				gcSubjectService.getSubjectInfoByList(subjectList,masterId,user.getId(),request);
				break;
			//my courses
			case TableConstant.COMMON_ONE:
				//drafts
				if (null==selectType||selectType.equals("draftsSubjectPageInfo")){
					List<GcSubject> draftsSubject = gcSubjectService.selectDraftsSubject(user.getId(),masterId,name,request);
					PageInfo<GcSubject> draftsSubjectPageInfo = new PageInfo<>(draftsSubject);
					subjectList.addAll(draftsSubject);
					message.addData("draftsSubject",draftsSubjectPageInfo);
				}
				//published
				if (null==selectType||selectType.equals("publishedSubject")){
					List<GcSubject> publishedSubject = gcSubjectService.selectPublishedSubject(user.getId(),masterId,name,request);
					PageInfo<GcSubject> publishedSubjectPageInfo = new PageInfo<>(publishedSubject);
					subjectList.addAll(publishedSubject);
					message.addData("publishedSubject",publishedSubjectPageInfo);
				}
				//createdByTeams
				if (null==selectType||selectType.equals("createdByTeamsSubject")){
					//判断是orgAdmin还是teamAdmin
					Integer adminFlag =  gcUserAccessService.selectUserAccessesByMasterId(user.getId(),masterId,GroupsType.orgAdmin);
					List<GcSubject> createdByTeamsSubject = new ArrayList<>();
					Integer orderType = null;
					if (null!=requestParams.get("orderType")){
						orderType = Integer.parseInt(requestParams.get("orderType").toString());
					}
					if (null!=adminFlag&&!adminFlag.equals(TableConstant.COMMON_ZERO)){
						createdByTeamsSubject = gcSubjectService.selectCreateByTeamsOrgAdmin(user.getId(),masterId,name,request,groupCodeList,orderType);
					}else {
						createdByTeamsSubject = gcSubjectService.selectCreatedByTeams(user.getId(),masterId,name,request,groupCodeList,orderType);
					}
					subjectList.addAll(createdByTeamsSubject);
					PageInfo<GcSubject> createdByTeamsSubjectPageInfo = new PageInfo<>(createdByTeamsSubject);
					message.addData("createdByTeamsSubject",createdByTeamsSubjectPageInfo);
				}
				//组装课程数据
				gcSubjectService.getSubjectInfoByList(subjectList,masterId,user.getId(),request);
				break;
			//discover
			case TableConstant.COMMON_TWO:
				Integer orderType = null;
				if (null!=requestParams.get("orderType")){
					orderType = Integer.parseInt(requestParams.get("orderType").toString());
				}

				//fromMyTeams
				if (null==selectType||selectType.equals("fromMyTeamSubject")) {
					List<GcSubject> fromMyTeamSubject = gcSubjectService.selectFromMyTeamSubject(user.getId(), masterId, name,request);
					PageInfo<GcSubject> fromMyTeamSubjectPageInfo = new PageInfo<>(fromMyTeamSubject);
					subjectList.addAll(fromMyTeamSubject);
					message.addData("fromMyTeamSubject",fromMyTeamSubjectPageInfo);
				}
				//companyResources
				if (null==selectType||selectType.equals("companyResourcesSubject")) {
					List<GcSubject> companyResourcesSubject = gcSubjectService.selectCompanyResourcesSubject(user.getId(), masterId,name, request);
					PageInfo<GcSubject> companyResourcesSubjectPageInfo = new PageInfo<>(companyResourcesSubject);
					subjectList.addAll(companyResourcesSubject);
					message.addData("companyResourcesSubject",companyResourcesSubjectPageInfo);
				}
				//allCourses
				if (null==selectType||selectType.equals("allCourseSubject")) {
					List<GcSubject> allCourseSubject = gcSubjectService.selectAllCourseSubject(user.getId(),masterId,name,request,orderType);
					PageInfo<GcSubject> allCourseSubjectPageInfo = new PageInfo<>(allCourseSubject);
					subjectList.addAll(allCourseSubject);
					message.addData("allCourseSubject",allCourseSubjectPageInfo);
				}
				//组装课程数据
				gcSubjectService.getSubjectInfoByList(subjectList,masterId,user.getId(),request);
				break;
		}
		message.setData((Map<String, Object>) JSON.toJSON(message.getData()));
		return message.ok();
	}

	@ApiOperation(value="pt新首页", notes = "新UI课程首页", httpMethod = "POST")
	@PostMapping("/newPtIndexHome")
	public Message newPtIndexHome(@RequestBody JSONObject requestParams, HttpServletRequest request){
		//复用
		SysSystem system = this.getSystem();
		String portalId = requestParams.getString("portalId");
		if(Objects.isNull(portalId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		String token = request.getHeader("Authorization");
		if (null != token && !"".equals(token) && !"undefined".equals(token)){
			GcUser user = this.getGcUser();
			return gvgMasterService.newPtIndexHome(requestParams,request,system,user).addData("times",new Date());
		}
		return gvgMasterService.newPtIndexHome(requestParams,request,system,null).addData("times",new Date());
	}

	@ApiOperation(value="设置课程已看", notes = "设置课程已看", httpMethod = "GET")
	@GetMapping("/setViewSubject")
	public Message setViewSubject(Integer subjectId,HttpServletRequest request){
		GcUser user = this.getGcUser();
		Integer masterId = Integer.parseInt(request.getHeader("masterId"));
		QueryWrapper<PtViewSubject> queryWrapper = new QueryWrapper<PtViewSubject>();
		queryWrapper.eq("master_id", masterId);
		queryWrapper.eq("user_id", user.getId());
		queryWrapper.eq("subject_id",subjectId);
		List<PtViewSubject> subjects = viewSubjectService.list(queryWrapper);
		if (subjects.size()!=TableConstant.COMMON_ZERO){
			PtViewSubject ptViewSubject = new PtViewSubject();
			ptViewSubject.setSubjectId(subjectId);
			ptViewSubject.setUserId(user.getId());
			ptViewSubject.setMasterId(masterId);
			viewSubjectService.saveOrUpdate(ptViewSubject);
		}
		return new Message().ok();
	}

	@ApiOperation(value="获取所有视频tag", notes = "获取所有视频tag", httpMethod = "POST")
	@GetMapping("/getPtVideoTags")
	public Message getPtVideoTags(@Param("name") String name,HttpServletRequest request) {
		String portalId = request.getHeader("masterId");
		if(Objects.isNull(portalId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		PtTags ptTags = new PtTags();
		ptTags.setMasterId(getHeaderMasterId(request));
		ptTags.setType(TableConstant.COMMON_TWO);
		ptTags.setTagText(name);
		List<String> allVideoTag = ptTagsService.selectPtTagList(ptTags,request);
		//allVideoTag = allVideoTag.stream().distinct().collect(Collectors.toList());
		PageInfo<String> pageInfo = new PageInfo<>(allVideoTag);
		return new Message().ok().addData("allVideoTag",pageInfo);
	}

	@ApiOperation(value="获取所有课程tag", notes = "获取所有视频tag", httpMethod = "POST")
	@GetMapping("/getPtSubjectTags")
	public Message getPtSubjectTags(@Param("name") String name,HttpServletRequest request) {
		String portalId = request.getHeader("masterId");
		if(Objects.isNull(portalId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		PtTags ptTags = new PtTags();
		ptTags.setMasterId(getHeaderMasterId(request));
		ptTags.setType(TableConstant.COMMON_ONE);
		ptTags.setTagText(name);

		List<String> allVideoTag = ptTagsService.selectPtTagList(ptTags,request);
		//allVideoTag = allVideoTag.stream().distinct().collect(Collectors.toList());
		PageInfo<String> pageInfo = new PageInfo<>(allVideoTag);
		return new Message().ok().addData("allSubjectTag",pageInfo);
	}

	@ApiOperation(value="获取所有资源tag", notes = "获取所有视频tag", httpMethod = "POST")
	@GetMapping("/getPtResourceTags")
	public Message getPtResourceTags(HttpServletRequest request) {
		String portalId = request.getHeader("masterId");
		if(Objects.isNull(portalId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}

		PtTags ptTags = new PtTags();
		ptTags.setMasterId(getHeaderMasterId(request));
		ptTags.setType(TableConstant.COMMON_THREE);

		List<String> allResourceTag = ptTagsService.selectPtTagList(ptTags,request);
		return new Message().ok().addData("allResourceTag",allResourceTag);
	}

	@ApiOperation(value = "保存课程/视频到一个文件夹", httpMethod = "GET")
	@PostMapping("/saveContentToFolder")
	public Message saveContentToFolder(@RequestBody GcUserSaveFolder gcUserSaveFolder, HttpServletRequest request) {
		String portalId = request.getHeader("masterId");
		if(Objects.isNull(portalId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		List<GcUserSaveFolder> list = gcUserSaveFolderService.selectFolderForUserMaster(this.getGcUser().getId(), getHeaderMasterId(request),null,request,null);
		if(CollectionUtils.isNotEmpty(list)) {
			List<Integer> allFolderIds = list.stream().map(GcUserSaveFolder::getId).collect(Collectors.toList());
			List<Integer> deleteFolderIds = allFolderIds.stream().filter(e -> null!=gcUserSaveFolder.getFolderId()&&!gcUserSaveFolder.getFolderId().contains(e)).collect(Collectors.toList());

			if(CollectionUtils.isNotEmpty(deleteFolderIds)&&Objects.nonNull(gcUserSaveFolder.getVideoId())) {
				GcVideo gcVideo = gcVideoService.getById(gcUserSaveFolder.getVideoId());
				List<Integer> deleteContentIds = gcUserSaveContentService.deleteList(gcVideo.getFileId(), deleteFolderIds);
				if(CollectionUtils.isNotEmpty(deleteContentIds)) {
					gcUserSaveContentService.removeByIds(deleteContentIds);
				}
			}
			if(CollectionUtils.isNotEmpty(deleteFolderIds)&&Objects.nonNull(gcUserSaveFolder.getFileId())) {
				List<Integer> deleteContentIds = gcUserSaveContentService.deleteListByFolderId(gcUserSaveFolder.getFileId(), deleteFolderIds);
				if(CollectionUtils.isNotEmpty(deleteContentIds)) {
					gcUserSaveContentService.removeByIds(deleteContentIds);
				}
			}
		}

		List<Integer> containsFolderId;
		if (null!=gcUserSaveFolder.getVideoId()){
			containsFolderId = gcUserSaveContentService.selectFolderIdByVideoId(gcUserSaveFolder.getVideoId(),request.getIntHeader("masterId"));
		}else {
			containsFolderId = gcUserSaveContentService.selectFolderIdByFileId(gcUserSaveFolder.getFileId(),request.getIntHeader("masterId"));
		}

		List<GcUserSaveContent> userSaveContents = new ArrayList<>();
		for(Integer folderId : gcUserSaveFolder.getFolderId()) {
			GcUserSaveContent gcUserSaveContent = new GcUserSaveContent();
			gcUserSaveContent.setUserId(this.getGcUser().getId());
			gcUserSaveContent.setMasterId(getHeaderMasterId(request));
			gcUserSaveContent.setVideoId(gcUserSaveFolder.getVideoId());
			gcUserSaveContent.setFolderId(folderId);
			if (null!=gcUserSaveFolder.getVideoId()){
				GcVideo videoContent = gcVideoService.getById(gcUserSaveFolder.getVideoId());
				gcUserSaveContent.setFileId(videoContent.getFileId());
				gcUserSaveContent.setContentId(videoContent.getId());
			} else if(null!=gcUserSaveFolder.getFileId()){
				gcUserSaveContent.setFileId(gcUserSaveFolder.getFileId());
				gcVideoService.getVideoContent(gcUserSaveFolder.getFileId())
					.ifPresent(videoContent -> gcUserSaveContent.setContentId(videoContent.getId()));
			}
			userSaveContents.add(gcUserSaveContent);
		}
		Iterator<GcUserSaveContent> folderIterator = userSaveContents.iterator();
		while (folderIterator.hasNext()) {
			JSONObject jsonObject = (JSONObject) JSONObject.toJSON(folderIterator.next());
			if(containsFolderId.contains(jsonObject.get("folderId"))){
				folderIterator.remove();
			}
		}

		if(gcUserSaveContentService.saveOrUpdateBatch(userSaveContents)) {
			return new Message().ok("保存成功").addData("content", userSaveContents);
		}

		return new Message().ok();
	}

	@ApiOperation(value="新UI课程首页-包括课程名称查询接口", notes = "新UI课程首页", httpMethod = "POST")
	@PostMapping("index")
	public Message index(@RequestBody Map<String, Object> params, HttpServletRequest request) throws IOException {
		//复用
		String portalId = request.getHeader("masterId");
		if(Objects.isNull(portalId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		SysSystem system = this.getSystem();
		GcUser user = this.getGcUser();

		return  gvgMasterService.index(params,request,system,user,EnvType.PT.getCode());
	}

	@PostMapping("navigation")
	@ApiOperation(value="新UI课程首页-课程导航页", notes = "课程导航页", httpMethod = "POST")
	//@RequiresRoles("admin")
	public Message navigation(@RequestBody (required=false) Map<String, Object> params, HttpServletRequest request) throws IOException, PermitContextError, PermitApiError {
		//复用
		Object fid = params.get("fid");
		if(Objects.isNull(fid)){
			throw new SystemException(I18NUtil.get("guidecore.course.navigation.error"));
		}
		String masterId = request.getHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		SysSystem system = this.getSystem();String token = request.getHeader("Authorization");
		if (null != token && !"".equals(token) && !"undefined".equals(token)) {
			GcUser user = this.getGcUser();

			boolean isFlag = this.permitCheck(user,ActionsType.view,Integer.parseInt(masterId),ResourceType.course,Integer.parseInt(fid.toString()),null,null);
			if (!isFlag){
				throw new PermitException("No permission for this!");
			}

			initPermit();

			boolean isOrgAdmin = false;
			UserRead userRoles = permit.api.users.get(user.getUsername());
			if (null!=userRoles.attributes){
				if (null!=userRoles.attributes.get("isOrgAdmin")){
					isOrgAdmin = (boolean) userRoles.attributes.get("isOrgAdmin");
				}
			}
			user.setIsOrgAdmin(isOrgAdmin);

			return gvgMasterService.navigation(params, request, system, user, EnvType.PT.getCode());
		}
		return gvgMasterService.navigation(params, request, system, new GcUser(), EnvType.PT.getCode());
	}

	@GetMapping("/downloadPDFCertPt")
	@ApiOperation(value="下载pdf", notes = "下载pdf", httpMethod = "POST")
	public void downloadPDFCertPt(HttpServletRequest request, HttpServletResponse response, Integer subId)throws IOException{
		GcUser user = this.getGcUser();
		boolean subjectCompleteStatus = true;
		GcSubject subject = gcSubjectService.getById(subId);
		Integer masterId = getHeaderMasterId(request);
		if(subject==null) {
			throw new SystemException(I18NUtil.get("powtoon.download.error"));
		}
		if(subject.getMasterId().intValue()!=masterId.intValue()) {
			throw new SystemException(I18NUtil.get("powtoon.download.subject.error"));
		}

		if(null==subject.getCertificatesFlag() || TableConstant.COMMON_ONE!=subject.getCertificatesFlag()){
			throw new SystemException(I18NUtil.get("powtoon.download.unable.error"));
		}

		List<GcVideo> videoList = service.selectVideoPlayListBySubId(subId,user.getId());
		if (null!=videoList&&TableConstant.COMMON_ZERO!=videoList.size()){
			for (GcVideo video : videoList) {
				if (null==video.getPlayState()||video.getPlayState().equals(TableConstant.COMMON_ZERO)){
					subjectCompleteStatus = false;
				}
			}
		}
		List<GcEvent> eventList = eventService.selectEventByUserIdAndSubjectId(user.getId(),subId,masterId);
		if (null!=eventList&&TableConstant.COMMON_ZERO!=eventList.size()) {
			for (GcEvent event : eventList) {
				if (null == event.getAnswerJson()) {
					subjectCompleteStatus = false;
				}
			}
		}

		if (!subjectCompleteStatus){
			throw new SystemException("Your course is not completed!");
		}
		subject.setSubjects(gcSubjectService.getChildSubjectBySubId(subject.getId()));
		//List<Integer> subIdList = subject.getSubjects().stream().map(GcSubject::getId).collect(Collectors.toList());
		/*Double courseTotalTime = 0.00;
		if(subIdList.size()!=TableConstant.COMMON_ZERO) {
			//计算package下所有课程的总时长,赋值到packagelist中
			Map<Integer, GcSubject> subjectsDurationMap = newUiGcSubjectService.sumSubject1Duration(subIdList);
			for (Integer key : subjectsDurationMap.keySet()) {
				courseTotalTime += Integer.parseInt(subjectsDurationMap.get(key).getSubjectVideoDuration().toString());
			}
		}*/
		/*courseTotalTime = courseTotalTime/60;
		if (null!=subject.getCpdHours()){
			Double cpdHours = Double.valueOf(subject.getCpdHours());
			courseTotalTime = cpdHours*60;
		}*/
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		Date date = new Date();
		String today = format.format(date);
		PdfModel model=new PdfModel();
		model.setUserName(user.getLastName()+" "+user.getFirstName());
		model.setCompletionDate(today);
		model.setContext(subject.getName()/*+" in less than "+String.format("%.2f",courseTotalTime)+" minutes"*/);
		ByteArrayOutputStream os = pdfServicePt.getPdfBytes(model);

		response.setContentType("application/pdf");
		response.setContentLength(os.size());
		response.setCharacterEncoding("utf-8");
		String name = URLEncoder.encode("Diploma-"+subject.getName(), "utf-8");
		response.setHeader("Content-Disposition", "attachment;filename=" + name + ".pdf");
		ServletOutputStream out = response.getOutputStream();
		os.writeTo(out);
		out.flush();
		out.close();
	}

	@ApiOperation(value="新UI视频-根据课程id加载视频信息", notes = "新UI视频-根据课程id加载视频信息", httpMethod = "POST")
	@PostMapping("subjectId/{subjectId}")
	public Message getVideosBySubject(@PathVariable("subjectId") Integer subjectId, @RequestBody Map<String,Object> param, HttpServletRequest request){
		Integer masterId = request.getIntHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		if(Objects.isNull(subjectId)){
			throw new SystemException(I18NUtil.get("powtoon.download.error"));
		}
		SysSystem system = this.getSystem();
		return gvgMasterService.getVideosBySubject(subjectId,param,request,system);
	}

	@GetMapping("/videoDetailPt")
	public Message videoDetailPt(HttpServletRequest request,Integer videoId) throws IOException {
		SysSystem system = this.getSystem();
		String token = request.getHeader("Authorization");
		if (null != token && !"".equals(token) && !"undefined".equals(token)) {
			GcUser user = this.getGcUser();
			GcMaster master = masterService.getById(RequestUtil.getMasterId(request).get());
			boolean isFlag = this.permitCheck(user,ActionsType.view,master.getId(),ResourceType.videoItem,videoId,null,null);
			if (!isFlag){
				throw new PermitException("No permission for this!");
			}
			return gvgMasterService.videoDetail(request, videoId, user, system, EnvType.PT.getCode());
		}else {
			return gvgMasterService.videoDetail(request, videoId, null, system, EnvType.PT.getCode());
		}
	}

	@PostMapping("/selectVideosAndEvents")
	public Message selectVideosAndEvents(@RequestBody GcSubject subject, HttpServletRequest request) {
		Message message = new Message();
		GcUser user = this.getGcUser();
		if(Objects.isNull(subject.getId())){
			throw new SystemException(I18NUtil.get("powtoon.topic.error"));
		}
		PageParam pageParam = new PageParam(request);
		Integer pageNum = pageParam.getPageNum();
		Integer pageSize=pageParam.getPageSize();
		if (pageNum > 0 && pageSize > 0) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<GcVideo> gcVideos = gcVideoService.getVideoListBySubId(subject.getId());
		if(CollectionUtils.isNotEmpty(gcVideos)) {
			List<Integer> vids = gcVideos.stream().map(GcVideo::getId).collect(Collectors.toList());
			List<GcEvent> eventList = gcEventService.getEventListByVideoIds(vids,user.getId());
			for(GcVideo gcVideo : gcVideos){
				List<GcEvent> gcEventList = new ArrayList<>();
				for(GcEvent gcEvent : eventList){
					if(gcEvent.getVideoId().equals(gcVideo.getId())){
						if(gcEventList.size()<10){
							gcEventList.add(gcEvent);
						}
					}
				}
				List<GcEvent> events = eventList.stream().filter(e->e.getVideoId().equals(gcVideo.getId())).collect(Collectors.toList());
				List<GcEvent> gcEvents = events.stream().filter(e->e.getMyAnswer()!=null).collect(Collectors.toList());
				gcVideo.setAnsweredSumNums(events.size());
				gcVideo.setAnsweredNums(gcEvents.size());
				PageInfo<GcEvent> pageInfo = new PageInfo<>(gcEventList);
				if(CollectionUtils.isNotEmpty(events)) {
					pageInfo.setTotal(events.size());
					BigDecimal page = BigDecimal.valueOf(events.size()).divide(BigDecimal.valueOf(10),BigDecimal.ROUND_UP);
					pageInfo.setPages(page.intValue());
				}
				gcVideo.setEventListPageInfo(pageInfo);
			}
		}
		PageInfo<GcVideo> pageInfo = new PageInfo<>(gcVideos);
		return message.ok().addData("taskVideoList",pageInfo);
	}

	@PostMapping("/selectEventsByVideoId")
	public Message selectEventsByVideoId(@RequestBody GcVideo gcVideo, HttpServletRequest request) {
		Message message = new Message();
		Integer masterId = request.getIntHeader("masterId");
		if(Objects.isNull(gcVideo.getId())){
			throw new SystemException(I18NUtil.get("powtoon.savefolder.error"));
		}
		GcUser user = this.getGcUser();

		PageParam pageParam = new PageParam(request);
		Integer pageNum = pageParam.getPageNum();
		Integer pageSize=pageParam.getPageSize();
		if (pageNum > 0 && pageSize > 0) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<GcEvent> eventList = gcEventService.selectGetEventListAndSelfAnswerByVidFull(gcVideo.getId(), user.getId(), masterId);
		int totalEventNum = 0;
		int answeredEventNum = 0;
		if (eventList != null) {
			totalEventNum = eventList.size();
		}
		if (Objects.nonNull(user.getId())) {
			Map<Integer, Object> answerNumMap = gcEventService.videoEventsAnswerNumMap(gcVideo.getId(), user.getId(), masterId);

			GcUserAccess gcUserAccess = new GcUserAccess();
			GcAccess access = new GcAccess();
			access.setRoleType(AccessRoleType.STUDENT);
			gcUserAccess.setAccess(access);
			//gcUserAccess = gcUserAccessService.getUserAccessByMasterIdAndUserId(masterId, user.getId());
			if (gcUserAccess.getAccess().getRoleType() == AccessRoleType.STUDENT) {//判断是否是老师用户，如果是老师用户则不会去查询已回答问题数量
				for (GcEvent event : eventList) {
					Map numMap = (Map) answerNumMap.get(event.getId());
					int num = 0;
					if (numMap != null && numMap.get("answerNum") != null) {
						num = ((Long) numMap.get("answerNum")).intValue();
					}
					event.setAnswerNum(num);
					if (event.getMyAnswer() != null) answeredEventNum += 1;

				}
			}
		}
		PageInfo<GcEvent> pageInfo = new PageInfo<>(eventList);
		message.addData("totalEventNum", totalEventNum);
		message.addData("answeredEventNum", answeredEventNum);
		return message.ok().addData("eventList",pageInfo);

	}

	@GetMapping("/getFileDetailPt")
	public Message getFileDetail(Integer fileId,HttpServletRequest request){
		GcUser user = this.getGcUser();
		SysFile file = sysFileService.getById(fileId);
		file.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(file));
		file.setFullFileUrl(sysFileService.getResFullUrl(file,request));
		GcVideo videoContent = gcVideoService.getVideoContent(fileId).orElseThrow();
		Integer countLike= gcUserVideoActionService.countLikeForFile(videoContent.getId());
		GcUserVideoAction videoActionList = gcUserVideoActionService.getFileActionListByFileIdAndUserId(videoContent.getId(), user.getId());
		int isLiked = 0;
		if (null!=videoActionList){
			isLiked= 1;
		}
		file.setLikedFlag(isLiked);
		file.setIsLiked(isLiked);
		file.setLikeNum(countLike);
		return new Message().ok().addData("file",file);
	}

	@GetMapping("/playListVideoDetailPt")
	public Message playListVideoDetailPt(HttpServletRequest request,Integer videoId,Integer playListId) {
        if(Objects.isNull(videoId)){
			throw new SystemException(I18NUtil.get("powtoon.savefolder.error"));
		}
		if(Objects.isNull(playListId)){
			throw new SystemException(I18NUtil.get("powtoon.playlist.error"));
		}
		Message message = new Message();
		SysFile file = sysFileService.getById(videoId);
		GcUser myUser =this.getGcUser();

		List<Integer> playListIds = new ArrayList<>();
		playListIds.add(playListId);
		List<GcUserSaveFolder> list = gcUserSaveFolderService.selectFolderAllVideo(null, getHeaderMasterId(request),playListIds,request,null);
		GcUser gcUser = gcUserService.getById(list.get(0).getUserId());
		GcUserInfo gcUserInfo = gcUserInfoService.getById(gcUser.getInfoId());
		gcUser.setInfo(gcUserInfo);
		if (null!=gcUserInfo.getAvatarFileId()){
			SysFile sysFile =sysFileService.getById(gcUserInfo.getAvatarFileId());
			sysFile.setFullFileUrl(sysFileService.getResFullUrl(sysFile,request));
			gcUser.getInfo().setAvatarFile(sysFile);
		}

		list.get(0).setUser(gcUser);
		List<Integer> listIds = list.stream().map(GcUserSaveFolder::getId).collect(Collectors.toList());
		List<GcUserSaveContentFollow> gcUserSaveContentFollowList = gcUserSaveContentFollowService.selectFollowListByPlayListId(listIds);
		Map<Integer,List<GcUserSaveContentFollow>> map = gcUserSaveContentFollowList.stream().collect(Collectors.groupingBy(GcUserSaveContentFollow::getFolderId));

		List<Integer> gcUserSaveContentFollowIdList = gcUserSaveContentFollowService.selectFollowPlayList(this.getGcUser().getId(),getHeaderMasterId(request));
		if(gcUserSaveContentFollowIdList.contains(playListId)){
			message.ok().addData("followFlag",TableConstant.COMMON_ONE);
		}

		for(GcUserSaveFolder folder: list) {
			if(Objects.nonNull(folder.getFileId())){
				SysFile sysFile = sysFileService.getById(folder.getFileId());
				String fullfileurl = sysFileService.getResFullUrl(sysFile,request);
				folder.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(sysFile));
				folder.setFullFileUrl(fullfileurl);
			}
			List<GcUserSaveContentFollow> list1 = map.get(folder.getId());
			if(CollectionUtils.isNotEmpty(list1)) {
				folder.setFollowNum(list1.size());
			}
			for(GcUserSaveContent content: folder.getSaveContentList()) {
				SysFile videoFile = content.getVideoFile();
				if(videoFile != null) {
					SysFile videoFileById = sysFileService.getById(videoFile.getId());
					content.setVideoFile(videoFileById);
					populateVideoContent(request, videoFileById, myUser.getId());
				}
			}
		}

		populateVideoContent(request, file, myUser.getId());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return message.ok().addData("thisVideo",file)
				.addData("playListDetail",list.get(0))
				.addData("systemTime",df.format(new Date()));
	}

	private void populateVideoContent(HttpServletRequest request, SysFile videoFile, Integer userId) {
		videoFile.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(videoFile));
		videoFile.setFullFileUrl(sysFileService.getResFullUrl(videoFile, request));
		gcVideoService.getVideoContent(videoFile.getId()).ifPresent(videoContent -> {
			videoFile.setVideoId(videoContent.getId());
			videoFile.setLikeNum(videoActionService.countLikeForVideo(videoContent.getId()));
			videoFile.setIsLiked(
				videoActionService.isLikedByUser(videoContent.getId(), userId) ? 1 : 0);
		});
	}

	@ApiOperation(value = "logout", httpMethod = "GET")
	@GetMapping("/logout")
	public Message logout(HttpServletRequest request){
		GcUser user = this.getGcUser();
		String accessToken = (String) redisOperator.get("PT:"+user.getUsername());
		Map<String, String> body = new HashMap<>();

		Integer masterId = getHeaderMasterId(request);
		QueryWrapper<PtLoginConfig> loginConfigQueryWrapper = new QueryWrapper<>();
		loginConfigQueryWrapper.eq("master_id",masterId);
		PtLoginConfig ptLoginConfig = ptLoginConfigService.getOne(loginConfigQueryWrapper);

		if (null!=accessToken){

			/*if (null==ptLoginConfig){
				ptLoginConfig = new PtLoginConfig();
				ptLoginConfig.setLogOut(env.getProperty("logOut"));
			}*/
			ptLoginConfig = getPtConfig(ptLoginConfig);
			body.put("token",accessToken);
			body.put("client_id",ptLoginConfig.getClientId());
			try {
				HttpUtil.sendPostFormUrlencoded(ptLoginConfig.getPtRootUrl()+ptLoginConfig.getLogOut(),body);
			}catch (Exception e){
				String msg = e.getMessage();
				throw new SystemException(I18NUtil.get("powtoon.S3upload.error")+msg);
			}
			redisOperator.del("PT:"+user.getUsername());
		}
		return new Message().ok();
	}

	@ApiOperation(value = "getPtMessage", httpMethod = "GET")
	@GetMapping("/getPtMessage")
	public Message getPtMessage(HttpServletRequest request){
		Integer masterId = getHeaderMasterId(request);
		QueryWrapper<PtLoginConfig> loginConfigQueryWrapper = new QueryWrapper<>();
		loginConfigQueryWrapper.eq("master_id",masterId);
		PtLoginConfig ptLoginConfig = ptLoginConfigService.getOne(loginConfigQueryWrapper);
		ptLoginConfig = getPtConfig(ptLoginConfig);
		return new Message().ok().addData("clientId",ptLoginConfig.getClientId())
				.addData("ptRootURL",ptLoginConfig.getPtRootUrl())
				.addData("test1027","updated2022-10-27")
				.addData("ptLoginConfig",ptLoginConfig)
				.addData("测试",new Date());
	}

	public PtLoginConfig getPtConfig(PtLoginConfig ptLoginConfig){
		if (null==ptLoginConfig){
			ptLoginConfig = new PtLoginConfig();
			ptLoginConfig.setOauthToken(env.getProperty("oauthToken"));
			ptLoginConfig.setUserUrl(env.getProperty("userUrl"));
			ptLoginConfig.setLogOut(env.getProperty("logOut"));
			ptLoginConfig.setLogOutUrl(env.getProperty("logoutUrl"));
			ptLoginConfig.setGroups(env.getProperty("groups"));
		}else {
			if (null==ptLoginConfig.getOauthToken()||ptLoginConfig.getOauthToken().equals("")){
				ptLoginConfig.setOauthToken(env.getProperty("oauthToken"));
			}
			if (null==ptLoginConfig.getUserUrl()||ptLoginConfig.getUserUrl().equals("")){
				ptLoginConfig.setUserUrl(env.getProperty("userUrl"));
			}
			if (null==ptLoginConfig.getLogOut()||ptLoginConfig.getLogOut().equals("")){
				ptLoginConfig.setLogOut(env.getProperty("logOut"));
			}
			if (null==ptLoginConfig.getLogOutUrl()||ptLoginConfig.getLogOutUrl().equals("")){
				ptLoginConfig.setLogOutUrl(env.getProperty("logoutUrl"));
			}
			if (null==ptLoginConfig.getGroups()||ptLoginConfig.getGroups().equals("")){
				ptLoginConfig.setGroups(env.getProperty("groups"));
			}
		}
		log.info("JSON.toJSONString(ptLoginConfig)::"+JSON.toJSONString(ptLoginConfig));
		return ptLoginConfig;
	}

	@ApiOperation(value = "getNewAccessList", httpMethod = "GET")
	@GetMapping("/getNewAccessList")
	public Message getNewAccessList(HttpServletRequest request){
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		GcUser user = this.getGcUser();
		List<GcAccess> gcAccessList = gcAccessService.selectAccessLevel0(masterId,user.getId());
		return new Message().ok().addData("accessList",gcAccessList);
	}

	@ApiOperation(value = "getTeamAccessList", httpMethod = "GET")
	@GetMapping("/getTeamAccessList")
	public Message getTeamAccessList(String name,HttpServletRequest request) {
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		GcUser user = this.getGcUser();
		PageInfo<GcAccess> accessList = null;
		PageParam pageParam = new PageParam(request);
		if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
			PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
		}
		accessList =new PageInfo<>(accessService.getTeamAccessList(name,masterId,user.getId(),request));
		return new Message().ok().addData("accessList",accessList);
	}

	@ApiOperation(value = "getTeamAccessSubjectNumList", httpMethod = "GET")
	@GetMapping("/getTeamAccessSubjectNumList")
	public Message getTeamAccessSubjectNumList(String name,HttpServletRequest request){
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		GcUser user = this.getGcUser();
		Integer adminFlag =  gcUserAccessService.selectUserAccessesByMasterId(user.getId(),masterId,GroupsType.orgAdmin);
		PageInfo<GcAccess> accessList = null;
		if (null!=adminFlag&&!adminFlag.equals(TableConstant.COMMON_ZERO)){
			List<Integer> availableTypeFour = subService.getUserPublicSubject(masterId,user.getId());
			List<Integer> availableTypeOneAndThree = subService.getUserCreateSubjectAdmin(masterId,user.getId());
			List<Integer> subIds = subService.getUserCreateSubject(masterId,user.getId());
			PageParam pageParam = new PageParam(request);
			if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
				PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
			}
			accessList = new PageInfo<>(accessService.getTeamAccessSubjectNumAdminList(name,masterId,user.getId(),availableTypeFour,availableTypeOneAndThree,subIds));
		}else {
			List<Integer> subIds = subService.getUserCreateSubject(user.getId(),masterId);
			PageParam pageParam = new PageParam(request);
			if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
				PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
			}
			accessList = new PageInfo<>(accessService.getTeamAccessSubjectNumList(name,masterId,user.getId(),subIds,request));
		}
		return new Message().ok().addData("accessList",accessList);
	}


	@ApiOperation(value = "getAccessList", httpMethod = "GET")
	@GetMapping("/getAccessList")
	public Message getAccessList(String name, HttpServletRequest request) {
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		GcUser user = this.getGcUser();
		PageInfo<GcAccess> accessList;
		initPermit();

		PageParam pageParam = new PageParam(request);
		if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
			PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
		}
		accessList = new PageInfo<>(accessService.listAccess(name, masterId, user.getId()));

		return new Message().ok().addData("accessList", accessList);
	}

	@ApiOperation(value = "getTeamUser", httpMethod = "GET")
	@PostMapping("/getTeamUser")
	public Message getTeamUser(@RequestBody Map<String, Object> params,HttpServletRequest request){
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		params.put("masterId",masterId);
		PageParam pageParam = new PageParam(request);
		if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
			PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
		}
		List<GcUser> userList = userService.getTeamUser(params,request);
		PageInfo<GcUser> pageInfo = new PageInfo<>(userList);
		return new Message().ok().addData("userList",pageInfo);
	}

	@ApiOperation(value = "getAllGroup", httpMethod = "GET")
	@PostMapping("/getAllGroup")
	public Message getAllGroup(@RequestBody Map<String, Object> params,HttpServletRequest request) {
		GcUser user = this.getGcUser();
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		initPermit();

		params.put("masterId",masterId);
		List<GcAccess> gcAccessList = accessService.listAllAccess(params, request);
		params.put("userId",user.getId());
		List<GcAccess> gcAccessList2 = accessService.listAllAccess(params,request);

		PageInfo<GcAccess> accessList = new PageInfo<>(gcAccessList2);
		Map<Integer,GcAccess> gcAccessMap = gcAccessList.stream().collect(Collectors.toMap(GcAccess::getId,GcAccess -> GcAccess, (key1, key2) -> key2, LinkedHashMap::new));

		accessList.getList().forEach(i->{
			if (null!=gcAccessMap.get(i.getId())){
				i.setUsers(gcAccessMap.get(i.getId()).getUsers());
			}
			if (null!=i.getUsers()){
				i.getUsers().forEach(j->{
					sysFileService.getResFullUrl(j.getInfo().getAvatarFile(), request);
				});
			}
		});

		return new Message().ok().addData("accessList",accessList);
	}

	@ApiOperation(value = "getCodeSubject", httpMethod = "GET")
	@GetMapping("/getCodeSubject")
	public Message getCodeSubject(String name,Integer type, Integer id, HttpServletRequest request) throws IOException {
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		GcAccess access = accessService.getById(id);
		List<Integer> idList = new ArrayList<>();
		PageInfo<GcSubject> pageInfo = new PageInfo<>();
		GcUser user = this.getGcUser();
		boolean isFlag = this.permitCheck(user,ActionsType.manageContent,masterId,ResourceType.contentGroup,access.getId(),null,null);
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}

		List<Integer> courseAssignmentIds = contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(access.getId());
		List<Integer> mustAssignmentIds = contentGroupCourseAssignmentService.getMustCoursesContentGroupAssignmentIds(access.getId());
		List<Integer> optionalAssignmentIds = contentGroupCourseAssignmentService.getOptionalCoursesContentGroupAssignmentIds(access.getId());

		if (type==TableConstant.COMMON_ZERO){
			if (!courseAssignmentIds.isEmpty()){
				idList.addAll(courseAssignmentIds);
			}
		}else if (type==TableConstant.COMMON_ONE){
			if (!mustAssignmentIds.isEmpty()){
				idList.addAll(mustAssignmentIds);
			}
		}else {
			if (!optionalAssignmentIds.isEmpty()){
				idList.addAll(optionalAssignmentIds);
			}
		}
		PageParam pageParam = new PageParam(request);
		if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
			PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
		}
		List<GcSubject> subjects = new ArrayList<>();
		Map<Integer,List<PtTags>> tagsMap = new HashMap<>();
		if (!idList.isEmpty()){
			subjects = gcSubjectService.listSubByIdsAndName(idList,name);
		}else {
			subjects = gcSubjectService.listSubByIdsAndName(null,null);
		}
		List<Integer> subjectIdList = subjects.stream().map(GcSubject::getId).collect(Collectors.toList());
		if (!subjectIdList.isEmpty()){
			QueryWrapper<PtTags> queryWrapper2 = new QueryWrapper<>();
			queryWrapper2.eq("master_id",masterId);
			queryWrapper2.eq("type",TableConstant.COMMON_ONE);
			queryWrapper2.in("subject_id",subjectIdList);
			List<PtTags> tagsList = ptTagsService.list(queryWrapper2);
			tagsMap = tagsList.stream().collect(Collectors.groupingBy(PtTags::getSubjectId));
		}
		Map<Integer, List<PtTags>> finalTagsMap = tagsMap;
		subjects.forEach(i->{
			if (mustAssignmentIds.contains(i.getId())){
				i.setIsMustSubject(TableConstant.COMMON_ZERO);
			}else {
				i.setIsMustSubject(TableConstant.COMMON_ONE);
			}
			if (null!= finalTagsMap.get(i.getId())){
				List<PtTags> ptTagsList = finalTagsMap.get(i.getId());
				List<String> stringList = ptTagsList.stream().map(PtTags::getTagText).collect(Collectors.toList());
				i.setAllTags(stringList);
			}
			sysFileService.updateImageUrls(i,request);
		});
		pageInfo = new PageInfo<>(subjects);
		return new Message().ok().addData("pageInfo",pageInfo).addData("access",access);
	}

	@ApiOperation(value = "getCodeChannel", httpMethod = "GET")
	@GetMapping("/getCodeChannel")
	public Message getCodeChannel(String name, Integer accessId, HttpServletRequest request) throws IOException {
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		GcAccess access = accessService.getById(accessId);
        GcUser user = this.getGcUser();

		boolean isFlag = this.permitCheck(user,ActionsType.manageContent,masterId,ResourceType.contentGroup,access.getId(),null,null);
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}

        List<Integer> subscribedChannelIds =
            new ArrayList<>(contentGroupChannelSubscriptionService.getSubscribedChannelIds(access.getId()));

		PageParam pageParam = new PageParam(request);
		if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
			PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
		}
		List<PtChannel> channels;
		if (!subscribedChannelIds.isEmpty()){
			channels = ptChannelService.selectChannelsByIdsAndName(subscribedChannelIds,name);
		}else {
			channels = ptChannelService.selectChannelsByIdsAndName(null,null);
		}
		Map<Integer, List<PtTags>> tagsMap = new HashMap<>();
		List<Integer> subjectIdList = channels.stream().map(PtChannel::getId).collect(Collectors.toList());
		if (!subjectIdList.isEmpty()){
			QueryWrapper<PtTags> queryWrapper2 = new QueryWrapper<>();
			queryWrapper2.eq("master_id",masterId);
			queryWrapper2.eq("type",TableConstant.COMMON_ONE);
			queryWrapper2.in("channel_id",subjectIdList);
			List<PtTags> tagsList = ptTagsService.list(queryWrapper2);
			tagsMap = tagsList.stream().collect(Collectors.groupingBy(PtTags::getChannelId));
		}
		Map<Integer, List<PtTags>> finalTagsMap = tagsMap;

		channels.forEach(channel->{
			if (null!= finalTagsMap.get(channel.getId())){
				List<PtTags> ptTagsList = finalTagsMap.get(channel.getId());
				List<String> stringList = ptTagsList.stream().map(PtTags::getTagText).collect(Collectors.toList());
				channel.setAllTags(stringList);
			}
			sysFileService.updateImageUrls(channel, request);
		});
		PageInfo<PtChannel> pageInfo = new PageInfo<>(channels);
		return new Message().ok().addData("pageInfo",pageInfo).addData("access",access);
	}

	@ApiOperation(value = "getAvailableCourses",httpMethod = "GET")
	@GetMapping("/getAvailableCourses")
	public Message getAvailableCourses(String name,Integer accessId,String orderType,HttpServletRequest request){
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		List<Integer> idList = contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(accessId);
		GcUser user = this.getGcUser();
		List<GcSubject> subjects = new ArrayList<>();
		Integer adminFlag =  gcUserAccessService.selectUserAccessesByMasterId(user.getId(),masterId,GroupsType.orgAdmin);
		PageParam pageParam = new PageParam(request);
		if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
			PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
		}
		if (null!=adminFlag&&TableConstant.COMMON_ZERO!=adminFlag){
			subjects = gcSubjectService.getAvailableCourses(name,masterId,idList,null,orderType);
		}else {
			//subjects = gcSubjectService.getAvailableCourses(name,masterId,idList,user.getId(),orderType);
			//2023-12-07更新
			//当前用户全部may课程,不在当前组的,不管是可管理还是可查看权限的may课程都查出来
			subjects = gcSubjectService.newGetAvailableCourses(name,masterId,idList,user.getId(),orderType);
		}

		Map<String, Object> videoParams = new HashMap<>(2);
		Integer ids = TableConstant.COMMON_ZERO;
		videoParams.put("ids",ids);
		videoParams.put("subjectIds", subjects.stream().map(GcSubject::getId).collect(Collectors.toList()));
		Map<Integer, GcUserVideoAction> subjectUserStar = videoActionService.getSubjectUserStar(videoParams);

		List<Integer> videoIdlist = gcVideoService.getVideoIdListBySubId(subjects.stream().map(GcSubject::getId).collect(Collectors.toList()));
		List<GcVideo> videoList = gcVideoService.getVideoLongListByVideoId(videoIdlist);
		if (null!=user){
			videoList = gcVideoService.buildVideoInfo(user.getId(),null,videoList,masterId,request,EnvType.PT.getCode());
		}
		Map<Integer,List<GcVideo>> groupBySubId = videoList.stream().filter(e -> null!=e.getSubjectSubId()).collect(Collectors.groupingBy(GcVideo::getSubjectSubId));
		Map<Integer, GcUser> subjectUsers = gcUserService.getWatchedUserNum(subjects.stream().map(GcSubject::getId).collect(Collectors.toList()), masterId);

		Map<Integer,List<PtTags>> tagListMap;
		if (subjects.size()!=TableConstant.COMMON_ZERO){
			//tag
			QueryWrapper<PtTags> queryWrapper = new QueryWrapper<>();
			queryWrapper.in("subject_id", subjects.stream().map(GcSubject::getId).collect(Collectors.toList()));
			queryWrapper.in("master_id", masterId);
			tagListMap = ptTagsService.list(queryWrapper).stream().collect(Collectors.groupingBy(PtTags::getSubjectId));
		} else {
			tagListMap = new HashMap<>();
		}


		subjects.forEach(i->{

			if (null!=tagListMap&&null!=tagListMap.get(i.getId())){
				List<PtTags> tagsList = tagListMap.get(i.getId());
				List<String> textList = tagsList.stream().map(PtTags::getTagText).collect(Collectors.toList());
				i.setCourseTags(JSONArray.parseArray(JSON.toJSONString(textList)));
			}

			if (null!=subjectUserStar.get(i.getId())){
				GcUserVideoAction action = subjectUserStar.get(i.getId());
				i.setStarValue(action.getSubjectStarAvg());//星级平均值
				// 打星总人数
				i.setStarUsers(action.getSubjectStarUsers());
			}else {
				i.setStarValue(TableConstant.starValue0);//星级平均值
				i.setStarUsers(TableConstant.starUsers);
			}

			if (null!=groupBySubId.get(i.getId())){
				List<GcVideo> gcVideos = groupBySubId.get(i.getId());
				Integer totalSeconds = gcVideos.stream().filter(a -> a.getVideoTime()!=null).mapToInt(GcVideo::getVideoTime).sum();
				i.setVideosTotalLong(totalSeconds);
				i.setVideosTotalNum(gcVideos.size());
			}else {
				i.setVideosTotalLong(TableConstant.COMMON_ZERO);
				i.setVideosTotalNum(TableConstant.COMMON_ZERO);
			}
			if (null!=subjectUsers.get(i.getId())){
				i.setSubjectUsers(subjectUsers.get(i.getId()).getSubjectUsers());
			}else {
				i.setSubjectUsers(TableConstant.COMMON_ZERO);
			}
			sysFileService.getResFullUrl(i.getSubImgFile(),request);
			sysFileService.getVideoSnapshotUrl(i.getSubImgFile());
		});
		PageInfo<GcSubject> pageInfo = new PageInfo<>(subjects);
		return new Message().ok().addData("subjects",pageInfo);
	}
	@ApiOperation(value = "assignedSubject",httpMethod = "POST")
	@PostMapping("/assignedSubject")
	public Message assignedSubject(@RequestBody Map<String, Object> params,HttpServletRequest request) throws IOException {
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		Integer type = Integer.parseInt(params.get("type").toString());
		Integer accessId = Integer.parseInt(params.get("accessId").toString());
		List<Integer> idList = (List<Integer>) params.get("idList");
		GcUser user = this.getGcUser();

		boolean isFlag = this.permitCheck(user,ActionsType.addContent,masterId,ResourceType.contentGroup,accessId,null,null);
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}
		GcAccess access = accessService.getById(accessId);
		List<GcUserAccess> userAccessList = gcUserAccessService.selectAllUserAccessByAccessId(access.getId(),masterId);
		List<Integer> userAccessIds = userAccessList.stream().map(GcUserAccess::getId).collect(Collectors.toList());
		List<GcUserAccessPermission> userAccessPermissions = gcUserAccessPermissionService.selectUserAccessPermissions(userAccessIds);

		if(null!=access.getSubjectJson()){
			List<Integer> subjectList = access.getSubjectJson().toJavaList(Integer.class);
			subjectList.addAll(idList);
			access.setSubjectJson(parseToJsonArray(subjectList));
		}
		if (type==TableConstant.COMMON_ZERO){
			if(null!=access.getMustSubjectJson()){
				List<Integer> mustSubjectList = access.getMustSubjectJson().toJavaList(Integer.class);
				mustSubjectList.addAll(idList);
				access.setMustSubjectJson(parseToJsonArray(mustSubjectList));
			}else {
				access.setMustSubjectJson(parseToJsonArray(idList));
			}
		}else {
			if(null!=access.getMaySubjectJson()){
				List<Integer> maySubjectList = access.getMaySubjectJson().toJavaList(Integer.class);
				maySubjectList.addAll(idList);
				access.setMaySubjectJson(parseToJsonArray(maySubjectList));
			}else {
				access.setMaySubjectJson(parseToJsonArray(idList));
			}
		}
		gcAccessService.saveOrUpdate(access);

		for (GcUserAccessPermission userAccessPermission : userAccessPermissions) {
			if(null!=userAccessPermission.getSubPermission()){
				List<Integer> subPermissionList = userAccessPermission.getSubPermission().toJavaList(Integer.class);
				subPermissionList.addAll(idList);
				userAccessPermission.setSubPermission(parseToJsonArray(subPermissionList));
			}else {
				userAccessPermission.setSubPermission(parseToJsonArray(idList));
			}

			if(type==TableConstant.COMMON_ZERO){
				if(null!=userAccessPermission.getMustSubjectJson()){
					List<Integer> subPermissionList = userAccessPermission.getMustSubjectJson().toJavaList(Integer.class);
					subPermissionList.addAll(idList);
					userAccessPermission.setMustSubjectJson(parseToJsonArray(subPermissionList));
				}else {
					userAccessPermission.setMustSubjectJson(parseToJsonArray(idList));
				}
			}else {
				if(null!=userAccessPermission.getMaySubjectJson()){
					List<Integer> subPermissionList = userAccessPermission.getMaySubjectJson().toJavaList(Integer.class);
					subPermissionList.addAll(idList);
					userAccessPermission.setMaySubjectJson(parseToJsonArray(subPermissionList));
				}else {
					userAccessPermission.setMaySubjectJson(parseToJsonArray(idList));
				}
			}
		}

		contentGroupCourseAssignmentService.save(user, idList, accessId, CourseType.ofType(type));
		gcUserAccessPermissionService.updateGcUserAccessPermissions(userAccessPermissions);
		return new Message().ok();
	}

	@ApiOperation(value = "removeSubject",httpMethod = "POST")
	@PostMapping("/removeSubject")
	public Message removeSubject(@RequestBody Map<String, Object> params,HttpServletRequest request){
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		Integer accessId = Integer.parseInt(params.get("accessId").toString());
		String idListString = params.get("idList").toString();
		List<Integer> idList = JSONArray.parseArray(idListString).toJavaList(Integer.class);
		GcAccess access = accessService.getById(accessId);
		List<GcUserAccess> userAccessList = gcUserAccessService.selectAllUserAccessByAccessId(access.getId(),masterId);
		List<Integer> userIds = userAccessList.stream().map(GcUserAccess::getUserId).collect(Collectors.toList());
		List<Integer> userAccessIds = userAccessList.stream().map(GcUserAccess::getId).collect(Collectors.toList());
		List<GcUserAccessPermission> userAccessPermissions = gcUserAccessPermissionService.selectUserAccessPermissions(userAccessIds);
		for (Integer integer : idList) {
			if (null!=access.getSubjectJson()){
				while (access.getSubjectJson().contains(integer)){
					access.getSubjectJson().remove(integer);
				}
			}
			if (null!=access.getMustSubjectJson()){
				while (access.getMustSubjectJson().contains(integer)){
					access.getMustSubjectJson().remove(integer);
				}
			}
			if (null!=access.getMaySubjectJson()){
				while (access.getMaySubjectJson().contains(integer)){
					access.getMaySubjectJson().remove(integer);
				}
			}
		}
		accessService.saveOrUpdate(access);
		//List<Integer> userIds = userAccessPermissions.stream().map(GcUserAccessPermission::getUserId).collect(Collectors.toList());
		Map<String,Object> map = new HashMap<>();
		map.put("userIds",userIds);
		map.put("subIds",idList);
		map.put("masterId",masterId);

		List<GcSubject> subjects = gcSubjectService.getCompletedTwoCourse(map);
		Map<Integer,List<GcSubject>> idMap = subjects.stream().collect(Collectors.groupingBy(GcSubject::getUserId));
		for (GcUserAccessPermission userAccessPermission : userAccessPermissions) {
			List<GcSubject> subjectList = idMap.get(userAccessPermission.getUserId());
			if (null!=subjectList){
				List<Integer> integerList = subjectList.stream().map(GcSubject::getId).collect(Collectors.toList());
				for (Integer integer : idList) {
					if (!integerList.contains(integer)){
						if (null!=userAccessPermission.getSubPermission()){
							while (userAccessPermission.getSubPermission().contains(integer)){
								userAccessPermission.getSubPermission().remove(integer);
							}
						}
						if (null!=userAccessPermission.getMustSubjectJson()){
							while (userAccessPermission.getMustSubjectJson().contains(integer)){
								userAccessPermission.getMustSubjectJson().remove(integer);
							}
						}
						if (null!=userAccessPermission.getMaySubjectJson()){
							while (userAccessPermission.getMaySubjectJson().contains(integer)){
								userAccessPermission.getMaySubjectJson().remove(integer);
							}
						}

					}
				}
			}else {
				for (Integer integer : idList) {
						if (null!=userAccessPermission.getSubPermission()){
							while (userAccessPermission.getSubPermission().contains(integer)){
								userAccessPermission.getSubPermission().remove(integer);
							}
						}
						if (null!=userAccessPermission.getMustSubjectJson()){
							while (userAccessPermission.getMustSubjectJson().contains(integer)){
								userAccessPermission.getMustSubjectJson().remove(integer);
							}
						}
						if (null!=userAccessPermission.getMaySubjectJson()){
							while (userAccessPermission.getMaySubjectJson().contains(integer)){
								userAccessPermission.getMaySubjectJson().remove(integer);
							}
						}
				}
			}
		}
		gcUserAccessPermissionService.updateGcUserAccessPermissions(userAccessPermissions);
		contentGroupCourseAssignmentService.removeCourseAssignmentsByCourseId(access, idList);
		return new Message().ok();
	}

	@ApiOperation(value = "getPublicCodeChannel", httpMethod = "GET")
	@GetMapping("/getPublicCodeChannel")
	public Message getCodeChannel(Integer accessId,String name,HttpServletRequest request){
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		GcAccess access = accessService.getById(accessId);
		List<Integer> subscribedChannelIds =
			new ArrayList<>(contentGroupChannelSubscriptionService.getSubscribedChannelIds(access.getId()));

		GcUser user = this.getGcUser();
		Integer adminFlag =  gcUserAccessService.selectUserAccessesByMasterId(user.getId(),masterId,GroupsType.orgAdmin);
		List<PtChannel> channels;
		PageParam pageParam = new PageParam(request);
		if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
			PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
		}
		if (null!= adminFlag&&adminFlag==TableConstant.COMMON_ONE){
				channels = ptChannelService.selectChannelsByIdAndName(subscribedChannelIds, name, null,masterId);
		}else {
			// All channels of the current user are checked, regardless of whether they are manageable or viewable.
			channels = ptChannelService.selectChannelsByIdAndName(subscribedChannelIds, name, user.getId(),masterId);
		}

		channels.forEach(channel->{
			if(Objects.nonNull(channel.getChannelImgFileId())) {
				SysFile sysFile = sysFileService.getById(channel.getChannelImgFileId());
				String imgFullFileUrl = sysFileService.getResFullUrl(sysFile, request);
				channel.setImgFullFileUrl(imgFullFileUrl);
			}
			if(Objects.nonNull(channel.getChannelAvatarFileId())) {
				SysFile avatarFile = sysFileService.getById(channel.getChannelAvatarFileId());
				String avatarFullFileUrl = sysFileService.getResFullUrl(avatarFile, request);
				avatarFile.setFullFileUrl(avatarFullFileUrl);
				channel.setAvatarFile(avatarFile);
			}
		});

		return new Message().ok()
			.addData("pageInfo", new PageInfo<>(channels));
	}

	@ApiOperation(value = "assignedPtChannel", httpMethod = "POST")
	@PostMapping("/assignedPtChannel")
	public Message setPtChannel(@RequestBody Map<String, Object> params,HttpServletRequest request) throws IOException {
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		Integer accessId = Integer.parseInt(params.get("accessId").toString());
		GcAccess access = accessService.getById(accessId);
		List<Integer> channelIds = (List<Integer>) params.get("channelIds");
		GcUser user = this.getGcUser();
		boolean isFlag = this.permitCheck(user,ActionsType.addContent,masterId,ResourceType.contentGroup,accessId,null,null);
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}
		List<GcUserAccess> userAccessList = gcUserAccessService.selectAllUserAccessByAccessId(access.getId(),masterId);
		List<Integer> userAccessIds = userAccessList.stream().map(GcUserAccess::getId).collect(Collectors.toList());
		List<GcUserAccessPermission> userAccessPermissions = gcUserAccessPermissionService.selectUserAccessPermissions(userAccessIds);
		contentGroupChannelSubscriptionService.subscribeChannels(access, channelIds, user);

		for (GcUserAccessPermission userAccessPermission : userAccessPermissions) {
			if (userAccessPermission.getSubscribePermission()!=null){
				List<Integer> subscribeList = userAccessPermission.getSubscribePermission().toJavaList(Integer.class);
				for (Integer channelId : channelIds) {
					if (!subscribeList.contains(channelId)){
						subscribeList.add(channelId);
					}
				}
				userAccessPermission.setSubscribePermission(parseToJsonArray(subscribeList));
			}else {
				userAccessPermission.setSubscribePermission(parseToJsonArray(channelIds));
			}
			if (null!=userAccessPermission.getChannelPermission()){
				List<Integer> channelPermission = userAccessPermission.getChannelPermission().toJavaList(Integer.class);
				for (Integer channelId : channelIds) {
					if (channelPermission.contains(channelId)){
						channelPermission.remove(channelId);
					}
				}
				userAccessPermission.setChannelPermission(parseToJsonArray(channelPermission));
			}
		}
		gcUserAccessPermissionService.updateBatchById(userAccessPermissions);
		return new Message().ok();
	}


	@ApiOperation(value = "removePtChannel", httpMethod = "POST")
	@PostMapping("/removePtChannel")
	public Message removePtChannel(@RequestBody Map<String, Object> params,HttpServletRequest request) throws IOException {
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		Integer accessId = Integer.parseInt(params.get("accessId").toString());
		GcAccess access = accessService.getById(accessId);
		GcUser user = this.getGcUser();
		boolean isFlag = this.permitCheck(user,ActionsType.manageContent,masterId,ResourceType.contentGroup,accessId,null,null);
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}
		List<Integer> channelIds = (List<Integer>) params.get("channelIds");
		List<GcUserAccess> userAccessList = gcUserAccessService.selectAllUserAccessByAccessId(access.getId(),masterId);
		List<Integer> userAccessIds = userAccessList.stream().map(GcUserAccess::getId).collect(Collectors.toList());
		List<GcUserAccessPermission> userAccessPermissions = gcUserAccessPermissionService.selectUserAccessPermissions(userAccessIds);

		for (GcUserAccessPermission userAccessPermission : userAccessPermissions) {
			if (userAccessPermission.getChannelPermission()!=null){
				List<Integer> channelPermissions = userAccessPermission.getChannelPermission().toJavaList(Integer.class);
				for (Integer channelId : channelIds) {
					if (!channelPermissions.contains(channelId)){
						channelPermissions.add(channelId);
					}
				}
				userAccessPermission.setChannelPermission(parseToJsonArray(channelPermissions));
			}else {
				userAccessPermission.setChannelPermission(parseToJsonArray(channelIds));
			}
			if (null!=userAccessPermission.getSubscribePermission()){
				List<Integer> subScribeList = userAccessPermission.getSubscribePermission().toJavaList(Integer.class);
				for (Integer channelId : channelIds) {
					while (subScribeList.contains(channelId)){
						subScribeList.remove(channelId);
					}
				}
				userAccessPermission.setSubscribePermission(parseToJsonArray(subScribeList));
			}
		}
		gcUserAccessPermissionService.updateBatchById(userAccessPermissions);
		return new Message().ok();
	}


	@GetMapping("/masterList")
	public Message masterList(){
		List<GcMaster> masterList = masterService.list();
		return new Message().ok().addData("masterList",masterList);
	}

	@GetMapping("/getPtGlobalConfig")
	public Message getPtGlobalConfig(Integer ptMasterId,HttpServletRequest request){
		GcMaster master = gcMasterService.getById(ptMasterId);
		if (null==master){
			return new Message().ok();
		}
		QueryWrapper<PtConfig> queryWrapper = new QueryWrapper<PtConfig>();
		queryWrapper.eq("master_id", master.getId());
		List<PtConfig> config = ptConfigService.list(queryWrapper);
		PtConfig ptConfig = new PtConfig();
		if (null==config||config.size()==TableConstant.COMMON_ZERO){
			return new Message().ok().addData("master",master).addData("config",null);
		}
		if (null!=config&&config.size()==TableConstant.COMMON_ONE){
			ptConfig = config.get(TableConstant.COMMON_ZERO);
		}else {
			ptConfig = config.get(config.size()-TableConstant.COMMON_ONE);
		}
		return new Message().ok().addData("master",master).addData("config",ptConfig).addData("test","1");
	}

	@GetMapping("/getPtConfig")
	public Message getPtConfig(HttpServletRequest request){
		QueryWrapper<PtConfig> queryWrapper = new QueryWrapper<PtConfig>();
		queryWrapper.isNotNull("master_id");
		queryWrapper.eq("id",TableConstant.COMMON_ONE);
		PtConfig config = ptConfigService.getOne(queryWrapper);
		if (null==config ){
			return new Message().ok().addData("config",null);
		}
		GcMaster master = masterService.getById(config.getMasterId());
		return new Message().ok().addData("config",config).addData("master",master);
	}

	@PostMapping("/savePtGlobalConfig")
	public Message savePtGlobalConfig(@RequestBody PtConfig ptConfig){
		ptConfigService.saveOrUpdate(ptConfig);
		GcMaster master = gcMasterService.getById(ptConfig.getMasterId());
		return new Message().ok().addData("ptConfig",ptConfig).addData("master",master);
	}

	@GetMapping("/getUserListByCode")
	public Message getUserListByCode(Integer accessId,HttpServletRequest request){
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		GcAccess access = accessService.getById(accessId);
		QueryWrapper<GcUserAccess> queryWrapper = new QueryWrapper<GcUserAccess>();
		queryWrapper.eq("master_id", masterId);
		queryWrapper.eq("access_id",access.getId());
		List<GcUserAccess> userAccessList = gcUserAccessService.list(queryWrapper);

		PageParam pageParam = new PageParam(request);
		if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
			PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
		}
		List<GcUser> gcUserList = gcUserService.getUserByUserAccessIds(userAccessList.stream().map(GcUserAccess::getId).collect(Collectors.toList()));
		gcUserList.forEach(i->{
			sysFileService.getResFullUrl(i.getInfo().getAvatarFile(), request);
		});
		PageInfo<GcUser> pageInfo = new PageInfo<>(gcUserList);
		return new Message().ok().addData("userList", pageInfo);
	}

	@ApiOperation(value = "systemSettings", httpMethod = "GET")
	@GetMapping("/systemSettings")
	public Message systemSettings(HttpServletRequest request){
		GcUser currentUser = this.getGcUser();
		QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("level",TableConstant.COMMON_TWO);

		Integer masterId = RequestUtil.getMasterId(request).orElse(null);
		List<SysMenu> sysMenuList = sysMenuService.getSysMenuListByMasterId(masterId, currentUser);
		List<SysMenu> homePageSections = sysMenuService.getLevel3ListByMasterId(masterId, currentUser);

		if (sysMenuList.isEmpty() || homePageSections.isEmpty()){
			sysMenuList=sysMenuService.getSysMenuList(masterId, currentUser);
			homePageSections=sysMenuService.getLevel3List(masterId, currentUser);
		}

		// Preheat the interface and optimize the first startup
		gcSubjectService.initJit();
		return new Message().ok()
			.addData("sysMenuList",sysMenuList)
			.addData("homePageSections",homePageSections);
	}

	@ApiOperation(value = "updateSettings", httpMethod = "POST")
	@PostMapping("/updateSettings")
	public Message updateSettings(@RequestBody List<SysMenu> sysMenu,HttpServletRequest request){
		String masterid = request.getHeader("Masterid");
		List<SysMenu> masterList=sysMenuService.getByMaster(Integer.parseInt(masterid));
		sysMenu.forEach(i->{
			if(masterList.isEmpty()){
				i.setId(null);
			}
			i.setMasterId(Integer.parseInt(masterid));
		});
		sysMenuService.saveOrUpdateBatch(sysMenu);
		if (masterList.isEmpty()){
			List<Integer> parentIdList=sysMenuService.getParentIdList(Integer.parseInt(masterid));
			List<SysMenu> ChildLevelList=sysMenuService.getChildLevelList(Integer.parseInt(masterid));
			ChildLevelList.forEach(i->{
				if(i.getParentId()==38){
					i.setParentId(parentIdList.get(TableConstant.COMMON_ZERO));
				}else if(i.getParentId()==42){
					i.setParentId(parentIdList.get(TableConstant.COMMON_ONE));
				}else if(i.getParentId()==44){
					i.setParentId(parentIdList.get(TableConstant.COMMON_TWO));
				}
			});
			sysMenuService.saveOrUpdateBatch(ChildLevelList);
		}
		return new Message().ok();
	}

	@ApiOperation(value = "updateMaySubject", httpMethod = "GET")
	@GetMapping("/updateMaySubject")
	public Message updateMaySubject(Integer subId,Integer accessId,Integer state,HttpServletRequest request){
		Integer masterId = Integer.parseInt(request.getHeader("masterid"));
		GcMaster master = gcMasterService.getById(masterId);
		GcAccess access = accessService.getById(accessId);
		List<GcUserAccess> userAccessList = gcUserAccessService.selectAllUserAccessByAccessId(accessId,masterId);
		List<GcUserAccessPermission> userAccessPermissions = gcUserAccessPermissionService.getPermissionByUserAccessIdList(userAccessList.stream().map(GcUserAccess::getId).collect(Collectors.toList()));
		//关闭
		if (TableConstant.COMMON_ZERO==state){
			if (null!=access.getMustSubjectJson()){
				access.getMustSubjectJson().remove(subId);
			}
			if (null!=access.getMaySubjectJson()){
				access.getMaySubjectJson().add(subId);
			}else {
				JSONArray jsonArray = new JSONArray();
				jsonArray.add(subId);
				access.setMaySubjectJson(jsonArray);
			}

			for (GcUserAccessPermission userAccessPermission : userAccessPermissions) {
				if (null!=userAccessPermission.getMustSubjectJson()){
					userAccessPermission.getMustSubjectJson().remove(subId);
				}
				if (null!=userAccessPermission.getMaySubjectJson()){
					userAccessPermission.getMaySubjectJson().add(subId);
				}else {
					JSONArray jsonArray = new JSONArray();
					jsonArray.add(subId);
					userAccessPermission.setMaySubjectJson(jsonArray);
				}
			}
		}else {
			if (null!=access.getMaySubjectJson()){
				access.getMaySubjectJson().remove(subId);
			}
			if (null!=access.getMustSubjectJson()){
				access.getMustSubjectJson().add(subId);
			}else {
				JSONArray jsonArray = new JSONArray();
				jsonArray.add(subId);
				access.setMustSubjectJson(jsonArray);
			}
			for (GcUserAccessPermission userAccessPermission : userAccessPermissions) {
				if (null!=userAccessPermission.getMaySubjectJson()){
					userAccessPermission.getMaySubjectJson().remove(subId);
				}
				if (null!=userAccessPermission.getMustSubjectJson()){
					userAccessPermission.getMustSubjectJson().add(subId);
				}else {
					JSONArray jsonArray = new JSONArray();
					jsonArray.add(subId);
					userAccessPermission.setMustSubjectJson(jsonArray);
				}
			}
		}
		contentGroupCourseAssignmentService.updateCourseAssignmentMandatoryOpposite(subId, accessId);
		accessService.saveOrUpdate(access);
		gcUserAccessPermissionService.saveOrUpdateBatch(userAccessPermissions);
		return new Message().ok();
	}

	@ApiOperation(value = "getToken", httpMethod = "GET")
	@GetMapping("/getToken")
	public Message getToken(String code,HttpServletRequest response,HttpServletRequest request) throws IOException, ClientException, PermitApiError, PermitContextError {
		Message message = new Message();
		Optional<Integer> masterIdOptional = RequestUtil.getMasterId(request);
		initPermit();

		GcMaster master = null;
		if (masterIdOptional.isEmpty()) {
			master = gcMasterService.getMaster("Powtoon");
		} else {
			master = gcMasterService.getMasterById(masterIdOptional.get());
		}
		Integer masterId = master.getId();
		QueryWrapper<PtLoginConfig> loginConfigQueryWrapper = new QueryWrapper<>();
		loginConfigQueryWrapper.eq("master_id",masterId);
		PtLoginConfig ptLoginConfig = ptLoginConfigService.getOne(loginConfigQueryWrapper);
		ptLoginConfig = getPtConfig(ptLoginConfig);
		String tokens = RequestUtil.getRequestAuthHeader(response);
		GcUser user = null;
		String token = null;
		String accessToken = null;
		String refreshToken=null;
		if (null==code){
			if (null != tokens && !tokens.isEmpty() && !tokens.equals("undefined")){
				Integer uid = Integer.parseInt(JwtUtil.getValueByToken(tokens, "uid"));
				user = userService.getUserByIdCache(uid);
				try {
					JwtUtil.verifyToken(tokens, user.getPassword());
				}catch (AuthenticationException e){
					return new Message().error(401, "Login has expired!");
				}
			}
		}

		List<Integer> subjectIdList = gcSubjectService.getLevel0SubLis(master.getId()).stream().map(GcSubject::getId).collect(Collectors.toList());
		GcAccess studentAccess = getGcAccess(master, subjectIdList);

		// Temporarily return pt field
		String thumbUrl = "null";
		String ptEmail = "null";
		String ptId = "null";

		String redirectUri = response.getHeader("redirectUri");
		if (null!=code){
			Map<String, String> body = new HashMap<>();
			body.put("client_id",ptLoginConfig.getClientId());
			body.put("grant_type","authorization_code");
			body.put("redirect_uri",redirectUri);
			body.put("code",code);
			log.info("getTokenUrl:"+ptLoginConfig.getPtRootUrl()+ptLoginConfig.getOauthToken());
			log.info("code::"+code);

			String JsonRequest = HttpUtil.sendPostFormUrlencoded(ptLoginConfig.getPtRootUrl()+ptLoginConfig.getOauthToken(),body);
			if (null==JsonRequest){
				throw new SystemException("Powtoon Token is null");
			}

			JSONObject requestJson = JSONObject.parseObject(JsonRequest);
			if (null==requestJson){
				throw new SystemException("Powtoon tokenReturn is null");
			}

			log.info("token return:"+requestJson.toJSONString());
			if (null==requestJson.getString("access_token")){
				throw new SystemException("Powtoon accessToken is null");
			}

			accessToken = requestJson.getString("access_token");
			refreshToken = requestJson.getString("refresh_token");
			// Get user information
			JSONObject object = HttpUtil.doGetAuthorization(ptLoginConfig.getPtRootUrl()+ptLoginConfig.getUserUrl(),"Bearer "+accessToken);
			log.info("PT登录接口返回::"+object.toJSONString());
			PermissionsVo permissions = object.toJavaObject(PermissionsVo.class);
			if (null==permissions||null==permissions.getPermissions()){
				throw new SystemException("Powtoon returns no permission data!");
			}

			// Get the group the user belongs to
			JSONObject groupObject = HttpUtil.doGetAuthorization(ptLoginConfig.getPtRootUrl()+ptLoginConfig.getGroups(),"Bearer "+accessToken);
			PtGroupsVo ptGroupsVo = groupObject.toJavaObject(PtGroupsVo.class);
			log.info("PtGroups interface returns:"+groupObject);
			Map<String,Groups> groupsMap = ptGroupsVo.getResults().stream().collect(Collectors.toMap(Groups::getId, (p) -> p));

			user = userService.getUserByUserName(permissions.getProfile().getEmail());
			// Add permission table data
			List<GcAccess> gcAccessesList = new ArrayList<>();
			List<GcAccess> gcAccessLists = new ArrayList<>();
			List<GcUserAccess> userAccessList = new ArrayList<>();
			List<GcUserAccessPermission> userAccessPermissions = new ArrayList<>();
			List<String> roleLists = getRoleLists(permissions);

			if (null==user){
                user = createGcUser(permissions, master, studentAccess, masterId);
			}else {
				// Update name
				user.setPtUser(TableConstant.COMMON_ONE);
				userService.updateById(user);
				GcUserInfo gcUserInfo = infoService.getById(user.getInfoId());
				gcUserInfo.setFirstName(permissions.getProfile().getFirstName());
				gcUserInfo.setLastName(permissions.getProfile().getLastName());

				if (null!=gcUserInfo.getAvatarFileId()){
					SysFile file = sysFileService.getById(gcUserInfo.getAvatarFileId());
					file.setFileUrl(permissions.getProfile().getThumbUrl());
					sysFileService.saveOrUpdate(file);
				}else {
					SysFile file = getSysFile(user, permissions, masterId);
					sysFileService.saveOrUpdate(file);
					gcUserInfo.setAvatarFileId(file.getId());
				}

				infoService.updateById(gcUserInfo);
				user = userService.getUserByIdCache(user.getId());
			}

			List<String> idList = permissions.getPermissions().getGroups().stream().map(Groups::getId).collect(Collectors.toList());
			// Add managed_groups
			List<String> managedList = permissions.getPermissions().getManaged_groups().stream().map(Groups::getId).collect(Collectors.toList());
			idList.addAll(managedList);
			// Query all groups
			List<GcAccess> accessLists = accessService.selectAccessByCodeAndMasterId(idList,masterId);
			ptChannelSubscribeService.autoSubscribeToContentGroupChannels(user, accessLists);

			Map<String,GcAccess> gcAccessMap = accessLists.stream().collect(Collectors.toMap(GcAccess::getCode, Function.identity(), (key1, key2) -> key2));
			for (Groups group : permissions.getPermissions().getGroups()) {
				GcAccess access = new GcAccess();
				if (null!=gcAccessMap.get(group.getId())){
					access = gcAccessMap.get(group.getId());
					access.setGroupName(group.getTitle());
                } else {
					access.setMasterId(masterId);
					access.setCode(group.getId());
					access.setGroupName(group.getTitle());
					access.setRoleType(TableConstant.COMMON_ONE);
					access.setCodeType(TableConstant.COMMON_ZERO);
					access.setSubjectJson(new JSONArray());
					access.setSubscribeJson(new JSONArray());
                }
                access.setRoleJson(JSONArray.parseArray("[" + JSON.toJSONString(GroupsType.groupMember) + "]"));
                if (null!=group.getRole_id()&&group.getRole_id().equals(GroupsType.orgAdmin)){
                    access.getRoleJson().add(GroupsType.orgAdmin);
                }
                gcAccessesList.add(access);
			}
			List<String> gcAccessesListCodes = gcAccessesList.stream().map(GcAccess::getCode).collect(Collectors.toList());

			for (Groups group : permissions.getPermissions().getManaged_groups()) {
				GcAccess access = gcAccessMap.get(group.getId());
				if(null!=access){
					access.setGroupName(group.getTitle());
					if (null!=access.getRoleJson()){
						access.getRoleJson().addAll(JSONArray.parseArray("[" + JSON.toJSONString(GroupsType.groupAdmin) + "]"));
					}else {
						access.setRoleJson(JSONArray.parseArray("[" + JSON.toJSONString(GroupsType.groupAdmin) + "]"));
					}
				}else {
					access = new GcAccess();
					access.setMasterId(masterId);
					access.setCode(group.getId());
					access.setGroupName(group.getTitle());
					access.setRoleType(TableConstant.COMMON_ONE);
					access.setCodeType(TableConstant.COMMON_ZERO);
					access.setSubjectJson(new JSONArray());
					access.setChannelJson(new JSONArray());
					access.setRoleJson(JSONArray.parseArray("[" + JSON.toJSONString(GroupsType.groupAdmin) + "]"));
				}
				gcAccessLists.add(access);
				if (!gcAccessesListCodes.contains(access.getCode())){
					gcAccessesList.add(access);
				}
			}
			if(!gcAccessLists.isEmpty()){
				accessService.insertOrUpdateList(gcAccessLists);
			}
			List<GcAccess> accessList = accessService.selectAccessByCodeAndMasterId(gcAccessesList.stream().map(GcAccess::getCode).collect(Collectors.toList()),masterId);
			Map<String,GcAccess> accessHashMap = gcAccessesList.stream().collect(Collectors.toMap(GcAccess::getCode, (p) -> p));
			// superAdmin user
			List<Integer> gcUserAccessList = gcUserAccessService.getAccessListBySuperAdmin(user.getId(),masterId);
			for (GcAccess access : accessList) {
				GcUserAccess userAccess = new GcUserAccess();
				userAccess.setUserId(user.getId());
				userAccess.setMasterId(masterId);
				userAccess.setAccessId(access.getId());
				if (null != accessHashMap.get(access.getCode())){
					userAccess.setRoleJson(accessHashMap.get(access.getCode()).getRoleJson());
				}
				if (gcUserAccessList.contains(access.getId())){
					userAccess.getRoleJson().add(GroupsType.superAdmin);
					roleLists.add(GroupsType.superAdmin);
				}
				//组权限
				if(null!=groupsMap.get(access.getCode())){
					userAccess.setParentCode(groupsMap.get(access.getCode()).getParent_group_id());
				}
				userAccess.setAccess(access);
				userAccessList.add(userAccess);
			}
			if(!userAccessList.isEmpty()) {
				gcUserAccessService.insertUserAccessList(userAccessList);
			}
			List<GcUserAccess> userAccesses = gcUserAccessService.getUserAccessListByMasterIdAndUserId(userAccessList.stream()
				.map(GcUserAccess::getUserId)
				.collect(Collectors.toList()),masterId);

			updateUserPermissions(userAccesses, userAccessPermissions);
			if (null!=permissions.getProfile().getThumbUrl()){
				thumbUrl = permissions.getProfile().getThumbUrl();
			}
			if (null!=permissions.getProfile().getEmail()){
				ptEmail = permissions.getProfile().getEmail();
			}
			if (null!=permissions.getProfile().getId()){
				ptId = permissions.getProfile().getId().toString();
			}
			List<Integer> codeIdList = new ArrayList<>();
			accessLists = gcAccessService.list();
			for (GcAccess access : accessLists) {
				if (!idList.contains(access.getCode())){
					codeIdList.add(access.getId());
				}
			}
			if(!codeIdList.isEmpty()){
				gcUserAccessService.deleteUserAccess(user.getId(),masterId,codeIdList);
			}
			// Sync to permit
			assignUser(user,roleLists,permissions,masterId);
			redisOperator.set("PT:"+user.getUsername(),accessToken);
			redisOperator.set("PT_refresh_token:"+user.getUsername(),refreshToken);
			//access_token存入redis
			redisOperator.set("access_token_userid"+user.getId(),"Bearer "+accessToken,requestJson.getLong("expires_in"));
		}
		if(code==null){
			if(null==redisOperator.get("PT:"+user.getUsername())
				|| null==redisOperator.get("access_token_userid"+user.getId())){
				return message.error(401, "Login has expired!");
			}

			Map<String, String> body = new HashMap<>();
			body.put("client_id",ptLoginConfig.getClientId());
			body.put("grant_type","refresh_token");
			body.put("refresh_token", (String) redisOperator.get("PT_refresh_token:"+user.getUsername()));
			String JsonRequest = HttpUtil.sendPostFormUrlencoded(ptLoginConfig.getPtRootUrl()+ptLoginConfig.getOauthToken(),body);
			JSONObject requestJson = JSONObject.parseObject(JsonRequest);
			accessToken = requestJson.getString("access_token");
			redisOperator.set("access_token_userid"+user.getId(),"Bearer "+accessToken,requestJson.getLong("expires_in"));
			redisOperator.set("PT_refresh_token:"+user.getUsername(),requestJson.getString("refresh_token"));
		}
		token = userService.getUserNativeToken(user, master);
		user.setFirstName(user.getInfo().getFirstName());
		user.setLastName(user.getInfo().getLastName());
		sysFileService.getResFullUrl(user.getInfo().getAvatarFile(), response);

		user.setThumbUrl(thumbUrl);
		user.setPtEmail(ptEmail);
		user.setPtId(ptId);

		//getToken returns logo url
		if(Objects.nonNull(master.getLogoId())){
			SysFile sysFile = sysFileService.getById(master.getLogoId());
			String logoUrl = sysFileService.getResFullUrl(sysFile,response);
			message.ok().addData("logoUrl",logoUrl);
		}

		UserRead userRoles = permit.api.users.get(user.getUsername());
		List<UserRole> roleList = userRoles.roles;

		boolean isOrgAdmin = false;
		boolean isTeamAdmin = false;
		if (null!=userRoles.attributes){
			if (null!=userRoles.attributes.get("isOrgAdmin")){
				isOrgAdmin = (boolean) userRoles.attributes.get("isOrgAdmin");
			}
			if (null!=userRoles.attributes.get("managedGroups")){
				JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(userRoles.attributes.get("managedGroups")));
				List<String> integers = jsonArray.toJavaList(String.class);
				if (!integers.isEmpty()){
					isTeamAdmin = true;
				}
			}
		}

		List<String> getRoleList = new ArrayList<>();

		for (UserRole userRole : roleList) {
			getRoleList.add(userRole.role);
		}
		List<Integer> gcUserAccessList = gcUserAccessService.getAccessListBySuperAdmin(user.getId(),masterId);
		if (null!=gcUserAccessList&& !gcUserAccessList.isEmpty()){
			getRoleList.add(GroupsType.superAdmin);
		}
		if (isOrgAdmin){
			getRoleList.add(GroupsType.admin);
		}
		if (isTeamAdmin){
			getRoleList.add(GroupsType.teamAdmin);
		}
		List<SysMenu> roleMenus = new ArrayList<>();
		if (!getRoleList.isEmpty()){
			roleMenus = sysMenuService.getMenuByRoles(getRoleList, user, masterId);
		}

		Integer isGroupAdmin = gcUserAccessService.getGroupAdmin(user.getId(),masterId);
		if (TableConstant.COMMON_ZERO!=isGroupAdmin||isOrgAdmin){
			SysMenu sysMenu = new SysMenu();
			sysMenu.setName("courses-groupAdmin");
			sysMenu.setKey("courses-groupAdmin");
			sysMenu.setState(TableConstant.COMMON_ZERO);
			sysMenu.setLevel(1);
			sysMenu.setRemarks("groupAdmin");
			roleMenus.add(sysMenu);
		}

		updateUserAccessLoginTime(user, masterId);

		return message.ok()
			.addData("user",user)
			.addData("token",token)
			.addData("accessToken",accessToken)
			.addData("logoutUrl",ptLoginConfig.getPtRootUrl()+ptLoginConfig.getLogOutUrl())
			.addData("roleMenus",roleMenus)
			.addData("isGroupAdmin",isGroupAdmin)
			.addData("isOrgAdmin",isOrgAdmin)
			.addData("ptRootUrl",ptLoginConfig.getPtRootUrl());
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

	private void updateUserPermissions(List<GcUserAccess> userAccesses, List<GcUserAccessPermission> userAccessPermissions) {
		for (GcUserAccess gcUserAccess : userAccesses) {
			GcUserAccessPermission permission = new GcUserAccessPermission();
			GcAccess access = gcUserAccess.getAccess();
			List<Integer> assignedCourses = contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(access.getId());
			List<Integer> mustAssignedCourses = contentGroupCourseAssignmentService.getMustCoursesContentGroupAssignmentIds(access.getId());
			List<Integer> optionalAssignedCourses = contentGroupCourseAssignmentService.getOptionalCoursesContentGroupAssignmentIds(access.getId());
			List<Integer> unsubscribedChannelIds =
				contentGroupChannelSubscriptionService.getPublicChannelIds(access.getId());
			List<Integer> subscribedChannelIds =
				contentGroupChannelSubscriptionService.getSubscribedChannelIds(access.getId());

			permission.setUserAccessId(gcUserAccess.getId());
			permission.setSubPermission(parseToJsonArray(assignedCourses));
			permission.setChannelPermission(parseToJsonArray(unsubscribedChannelIds));
			permission.setSubscribePermission(parseToJsonArray(subscribedChannelIds));
			permission.setMaySubjectJson(parseToJsonArray(optionalAssignedCourses));
			permission.setMustSubjectJson(parseToJsonArray(mustAssignedCourses));
			userAccessPermissions.add(permission);
		}
		if(!userAccessPermissions.isEmpty()) {
			gcUserAccessPermissionService.insertUserPermission(userAccessPermissions);
		}
	}

	private JSONArray parseToJsonArray(List<Integer> integerList) {
		return JSONArray.parseArray(JSON.toJSONString(integerList));
	}

	private SysFile getSysFile(GcUser user, PermissionsVo permissions, Integer masterId) {
		SysFile file = new SysFile();
		file.setSysId(TableConstant.COMMON_TWO);
		file.setUploadUid(user.getId());
		file.setName(permissions.getProfile().getThumbUrl());
		file.setFolder(TableConstant.sysFile_folder_guidecoreImages);
		file.setFileType(TableConstant.sysFile_fileType_resLink);
		file.setFileTypeIndex(TableConstant.COMMON_ONE);
		file.setMasterId(masterId);
		file.setSaveType(TableConstant.COMMON_THREE);
		file.setFileRemark(new JSONArray());
		file.setFileUrl(permissions.getProfile().getThumbUrl());
		return file;
	}

	private GcUser createGcUser(PermissionsVo permissions, GcMaster master, GcAccess studentAccess, Integer masterId)
		throws ClientException, IOException {
		GcUser user1 = userService.createGcUser(2, permissions.getProfile().getEmail(), get8UUID(),
			permissions.getProfile().getFirstName(), permissions.getProfile().getLastName());
		user1.setInfo(infoService.getById(user1.getInfoId()));
		accessService.checkUserAccess(master.getId(), user1.getId(), studentAccess.getCode(), null, null, null);
		user1.setPtUser(TableConstant.COMMON_ONE);
		user1.setFirstName(permissions.getProfile().getFirstName());
		user1.setLastName(permissions.getProfile().getLastName());

		SysFile file = new SysFile();
		file.setSysId(TableConstant.COMMON_TWO);
		file.setUploadUid(user1.getId());
		file.setName(permissions.getProfile().getThumbUrl());
		file.setFolder(TableConstant.sysFile_folder_guidecoreImages);
		file.setFileType(TableConstant.sysFile_fileType_resLink);
		file.setFileTypeIndex(TableConstant.COMMON_ONE);
		file.setMasterId(masterId);
		file.setSaveType(TableConstant.COMMON_THREE);
		file.setFileRemark(new JSONArray());

		sysFileService.saveOrUpdate(file);
		user1.getInfo().setAvatarFileId(file.getId());

		gcUserInfoService.saveOrUpdate(user1.getInfo());
		userService.updateById(user1);

		try {
			// Determine whether there is a global role (based on role (portal global): SuperAdmin, Admin, Member)
			if (!GroupsType.groupList.contains(permissions.getPermissions().getOrg().getRole_id())) {
				throw new Exception("xxx”!Please create the role xxx in Permit first.");
			}

		} catch (PermitContextError | IOException permitContextError) {
			log.error("Permit error when creating user", permitContextError);
		} catch (Exception e) {
			log.error("Error when creating user", e);
		}
		return user1;
	}

	private List<String> getRoleLists(PermissionsVo permissions) {
		List<String> roleLists = new ArrayList<>();

		// Determine whether the role is member type or admin type
		if (GroupsType.memberList.contains(permissions.getPermissions().getOrg().getRole_id())) {
			roleLists.add(GroupsType.member);
		} else if (GroupsType.adminList.contains(permissions.getPermissions().getOrg().getRole_id())) {
			roleLists.add(GroupsType.admin);
		} else if (GroupsType.superAdminList.contains(permissions.getPermissions().getOrg().getRole_id())) {
			roleLists.add(GroupsType.superAdmin);
		}
		if (GroupsType.orgAdmin.equals(permissions.getPermissions().getOrg().getRole_id())) {
			roleLists.add(GroupsType.member);
		}
		return roleLists;
	}

	private GcAccess getGcAccess(GcMaster master, List<Integer> subjectIdList) {
		QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("code","teacherPT");
		queryWrapper.eq("role_type",TableConstant.COMMON_ZERO);
		queryWrapper.eq("master_id", master.getId());
		GcAccess gcAccess = accessService.getOne(queryWrapper);

		queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("code","studentPT");
		queryWrapper.eq("role_type",1);
		queryWrapper.eq("master_id", master.getId());
		GcAccess studentAccess = accessService.getOne(queryWrapper);

		if (null == gcAccess || null == studentAccess) {
			if (null == gcAccess){
				gcAccess = new GcAccess();
				gcAccess.setMasterId(master.getId());
				gcAccess.setRoleType(TableConstant.COMMON_ZERO);
				gcAccess.setSubjectJson(parseToJsonArray(subjectIdList));
				gcAccess.setCodeType(TableConstant.COMMON_ZERO);
				gcAccess.setFreeFlag(TableConstant.COMMON_ZERO);
				gcAccess.setPackageShowFlag(TableConstant.COMMON_ONE);
				gcAccess.setCode("teacherPT");
				accessService.addAccess(gcAccess);
			}
			if (null==studentAccess){
				studentAccess = new GcAccess();
				studentAccess.setMasterId(master.getId());
				studentAccess.setSubjectJson(parseToJsonArray(subjectIdList));
				studentAccess.setCode("studentPT");
				studentAccess.setCodeType(TableConstant.COMMON_ZERO);
				studentAccess.setFreeFlag(TableConstant.COMMON_ZERO);
				studentAccess.setPackageShowFlag(TableConstant.COMMON_ONE);
				studentAccess.setRoleType(TableConstant.COMMON_ONE);
				studentAccess.setAdminId(gcAccess.getId());
				studentAccess.setId(null);
				accessService.addAccess(studentAccess);
			}else if(null == studentAccess.getAdminId()){
				studentAccess.setAdminId(gcAccess.getId());
				accessService.updateById(studentAccess);
			}
		}else {
			gcAccess.setSubjectJson(parseToJsonArray(subjectIdList));
			studentAccess.setSubjectJson(parseToJsonArray(subjectIdList));
			accessService.updateById(gcAccess);
			accessService.updateById(studentAccess);
		}
		return studentAccess;
	}

	private void initPermit() {
		permit =  new Permit(
				new PermitConfig.Builder(permitConfiguration.getApiKey())
						.withPdpAddress(permitConfiguration.getPdpAddress())
						.withDebugMode(true)
						.build()
		);
	}

	public String get8UUID(){
		UUID id=UUID.randomUUID();
		String[] idd=id.toString().split("-");
		return idd[0];
	}

	//同步用户
	public void assignUser(GcUser user,List<String> roleList,PermissionsVo permissionsVo,Integer masterId){
		GcMaster master = masterService.getMasterById(masterId);
		TenantRead tenant = null;
		try {
			tenant = permit.api.tenants.get(master.getContext());
		}catch (Exception | PermitApiError e){

		}
		try{
			//判断租户是否存在,不存在则新建
			if (null==tenant){
				tenant = permit.api.tenants.create(
						new TenantCreate(master.getContext(), master.getContext())
				);
			}
		}catch (Exception |PermitApiError e){

		}
		//attributes数据
		HashMap<String, Object> userAttributes = new HashMap<>();
		JSONArray adminGroups = new JSONArray();
		JSONArray memberGroups = new JSONArray();
		//JSONArray groups = new JSONArray();
		//判断memberGroups和adminGroups
		for (Groups memberGroup : permissionsVo.getPermissions().getGroups()) {
			memberGroups.add(memberGroup.getId().toString());
		}
		for (Groups adminGroup : permissionsVo.getPermissions().getManaged_groups()) {
			adminGroups.add(adminGroup.getId().toString());
		}

		if (roleList.contains(GroupsType.admin)){
			userAttributes.put("isOrgAdmin",Boolean.TRUE);
			roleList.remove(GroupsType.admin);
		}
		System.out.println("adminGroups::"+adminGroups.toString());
		//用户属性
		userAttributes.put("managedGroups",adminGroups);
		userAttributes.put("groups",memberGroups);
		//userAttributes.put("contentGroups",groups);
		//同步用户信息
		try {
			CreateOrUpdateResult<UserRead> response = permit.api.users.sync(
					(new User.Builder(user.getUsername()))
							.withEmail(user.getUsername()) // optional
							.withFirstName(user.getFirstName()) // optional
							.withLastName(user.getLastName()) // optional
							.withAttributes(userAttributes) // optional, used for ABAC permission checks
							.build()
			);
			//同步用户角色
			//RoleAssignmentRead[] assignedRoles = permit.api.users.getAssignedRoles(userRead.key);
			RoleAssignmentRead[] assignedRoles = permit.api.users.getAssignedRoles(response.getResult().key,tenant.key,1,50);
			List<String> oldRoleList = new ArrayList<>();
			for (RoleAssignmentRead assignedRole : assignedRoles) {
				oldRoleList.add(assignedRole.role);
			}
			for (String s : oldRoleList) {
				if (!roleList.contains(s)){
					permit.api.users.unassignRole(response.getResult().key, s, tenant.key);
				}
			}
			for (String s : roleList) {
				if (!oldRoleList.contains(s)){
					permit.api.users.assignRole(response.getResult().key, s, tenant.key);
				}else if (0==oldRoleList.size()){
					permit.api.users.assignRole(response.getResult().key, s, tenant.key);
				}
			}
		}catch (Exception | PermitApiError e){
			e.printStackTrace();
		}
	}

	@GetMapping("/updateData")
	public Message updateData(@Param("masterId")Integer masterId,@Param("userId")Integer userId){
		gcUserAccessPermissionService.updatePermissionData(masterId,userId);
		return new Message().ok();
	}

	@GetMapping("/getSubjectNameIndex")
	public Message getSubjectNameIndex(@Param("name")String name,HttpServletRequest request){
		GcMaster master = masterService.getById(RequestUtil.getMasterId(request).get());
		//name = URLDecoder.decode(name);
		Integer count = subService.getSubjectNameIndex(master.getId(),name);
		if (TableConstant.COMMON_ZERO!=count){
			//throw new SystemException(I18NUtil.get("subject.index.error").replace("{name}",name));
			return new Message().error(TableConstant.SUBJECT_ERROR_CODE,I18NUtil.get("subject.index.error").replace("{name}",name));
		}
		return new Message().ok();
	}

	@ApiOperation(value = "获取课程页面数据",httpMethod = "GET")
	@GetMapping("/getCoursesInfo")
	public Message getCoursesInfo(HttpServletRequest request){
		GcUser user = this.getGcUser();
		GcMaster master = masterService.getById(RequestUtil.getMasterId(request).get());
		//2023-04-24需修改
		List<GcUserAccessPermission> userAccessPermissionList = gcUserAccessPermissionService.getGroupMemberPermissionByUidList(user.getId(),master.getId(),GroupsType.groupMember);
		JSONArray jsonArray = new JSONArray();
		for (GcUserAccessPermission permission : userAccessPermissionList) {
			if (null!=permission.getMustSubjectJson()){
				jsonArray.addAll(permission.getMustSubjectJson());
			}
		}
		List<Integer> mustSubjectList = jsonArray.toJavaList(Integer.class);
		Integer mustSubjectSize = TableConstant.COMMON_ZERO;
		if (TableConstant.COMMON_ZERO!=mustSubjectList.size()){
			mustSubjectSize = subjectService.getSubjectNum(mustSubjectList);
		}
		List<Integer> channelIdList = new ArrayList<>();
		SysSystem system = this.getSystem();
		List<String> nameList = new ArrayList<>();
		nameList.add("channelIds");
		List<GcMasterHomeInfo> gcMasterHomeInfos = iGcMasterHomeInfoService.getGcMasterHomeInfoList(master.getId(),nameList,system,request);
		if(CollectionUtils.isNotEmpty(gcMasterHomeInfos)){
			channelIdList = gcMasterHomeInfos.get(TableConstant.COMMON_ZERO).getChannelIds().toJavaList(Integer.class);
		}
		List<Integer> publicSubjectIds = gcSubjectService.getPublicSubjectIds(master.getId());

		//Integer progressNum = gcSubjectService.selectSubjectPt(null,TableConstant.COMMON_ZERO,TableConstant.COMMON_ONE,null,master.getId(),user.getId(),channelIdList,publicSubjectIds);
		Integer progressNum = gcSubjectService.inProgressNum(user.getId(),master.getId());

		Integer published = gcSubjectService.getCreateUserPublished(user.getId(),master.getId(),TableConstant.COMMON_ONE);

		Integer myDrafts = gcSubjectService.getCreateUserPublished(user.getId(),master.getId(),TableConstant.COMMON_ZERO);


		List<Integer> idLists = new ArrayList<>();
		List<GcUserAccessPermission> permissionList =  gcUserAccessPermissionService.getGroupMemberByUidList(user.getId(),master.getId());
		for (GcUserAccessPermission permission : permissionList) {
			if(null!=permission.getMustSubjectJson()){
				idLists.addAll(permission.getMustSubjectJson().toJavaList(Integer.class));
			}
		}
		Integer DiscoverNum = gcSubjectService.selectSubjectPt(null,TableConstant.COMMON_FOUR,TableConstant.gcSubject_state_visible_1,null,master.getId(),user.getId(),channelIdList,idLists);
		//Integer DiscoverNum = gcSubjectService.getDiscoverNum(user.getId(),master.getId());
		Integer completedNum = gcSubjectService.selectSubjectPt(null,TableConstant.COMMON_TWO,TableConstant.COMMON_ONE,null,master.getId(),user.getId(),channelIdList,publicSubjectIds);

		//MyAssignmentNew
		Integer MyAssignmentNew = gcSubjectService.getNewMyAssignmentNew(master.getId(),user.getId());

		QueryWrapper<PtViewSubject> queryWrapper = new QueryWrapper<PtViewSubject>();
		queryWrapper.eq("master_id", master.getId());
		queryWrapper.eq("user_id", user.getId());
		List<PtViewSubject> subjectList = viewSubjectService.list(queryWrapper);
		subjectList.forEach(i->{
			idLists.add(i.getSubjectId());
		});

		Integer DiscoverNew = gcSubjectService.selectSubjectPt(null,TableConstant.COMMON_FOUR,TableConstant.gcSubject_state_visible_1,null,master.getId(),user.getId(),channelIdList,idLists);

		return new Message().ok()
				.addData("MyAssignments",mustSubjectSize)
				.addData("InProgress",progressNum)
				.addData("Published",published)
				.addData("Drafts",myDrafts)
				.addData("DiscoverNum",DiscoverNum)
				.addData("CompletedNum",completedNum)
				.addData("MyAssignmentNew",MyAssignmentNew)
				.addData("DiscoverNew",DiscoverNew);
	}

	@ApiOperation(value = "添加课程或者话题", httpMethod = "POST")
	@PostMapping("/saveSub")
	public Message saveSub(@RequestBody @ApiParam(name = "创建主题", value = "主题结构") GcSubject sub, HttpServletRequest request) throws IOException, PermitApiError, PermitContextError {
		//ApiAssert.ifStringNotInList(sub.getName(), CommonConstant.defaultNoCourseOrVideName, "课程名称错误，不可用该值");
		GcMaster master = this.getMaster();
		Integer masterId = null;
		GcUser user = this.getGcUser();
		Boolean isOrgAdmin = false;
		initPermit();
		UserRead userRoles = permit.api.users.get(user.getUsername());
		if (null!=userRoles.attributes){
			if (null!=userRoles.attributes.get("isOrgAdmin")){
				isOrgAdmin = (boolean) userRoles.attributes.get("isOrgAdmin");
			}
		}
		user.setIsOrgAdmin(isOrgAdmin);

		if (null==master&&null!=request.getHeader("masterId")){
			masterId = Integer.parseInt(request.getHeader("masterId"));
		}else {
			masterId = master.getId().intValue();
		}
		master = masterService.getById(masterId);
		boolean isFlag = false;
		List<String> ids = new ArrayList<>();
		if (null!=sub.getId()&&null==sub.getMoveDrafts()){
			GcSubject oldSubject = gcSubjectService.getById(sub.getId());
			List<String> mustAccessList = new ArrayList<>();
			List<String> accessListMay = new ArrayList<>();
			if(null!=sub.getAllPublished()&&sub.getAllPublished()==TableConstant.COMMON_ZERO){
				if (user.getIsOrgAdmin()){
					accessListMay = gcAccessService.findAccessListByMasterId(masterId).stream().map(GcAccess::getCode).collect(Collectors.toList());
				}else {
					accessListMay = gcAccessService.listAccess(null, masterId, user.getId()).stream().map(GcAccess::getCode).collect(Collectors.toList());
				}
				if (accessListMay.size()!=TableConstant.COMMON_ZERO){
					ids.addAll(accessListMay);
				}
			}
			if (null!=sub.getAllPublishedMay()&&sub.getAllPublishedMay().equals(TableConstant.COMMON_ZERO)){
				if (user.getIsOrgAdmin()){
					mustAccessList = gcAccessService.findAccessListByMasterId(masterId).stream().map(GcAccess::getCode).collect(Collectors.toList());
				}else {
					mustAccessList = gcAccessService.listAccess(null,masterId,user.getId()).stream().map(GcAccess::getCode).collect(Collectors.toList());
				}
				if (mustAccessList.size()!=TableConstant.COMMON_ZERO){
					ids.addAll(mustAccessList);
				}
			}

			if (null!=sub.getAccessIds()&&sub.getAccessIds().size()!=TableConstant.COMMON_ZERO&&null==sub.getAllPublishedMay()){
				ids.addAll(gcAccessService.listByIds(sub.getAccessIds()).stream().map(GcAccess::getCode).collect(Collectors.toList()));
			}
			if (null!=sub.getMustAccessIds()&&sub.getMustAccessIds().size()!=TableConstant.COMMON_ZERO&&null==sub.getAllPublished()){
				ids.addAll(gcAccessService.listByIds(sub.getMustAccessIds()).stream().map(GcAccess::getCode).collect(Collectors.toList()));
			}
			if (!oldSubject.getState().equals(sub.getState())&&null==sub.getFid()){
				if (!isOrgAdmin&&sub.getAvailableType().equals(TableConstant.COMMON_ONE)){
					throw new PermitException("No permission for this!");
				}
				//发布
				isFlag = this.permitCheck(user,ActionsType.addContent,masterId,ResourceType.contentGroup,sub.getId(),ids,null);
				if (null==oldSubject.getPublishedTime()&&sub.getState().equals(TableConstant.COMMON_ONE)){
					sub.setPublishedTime(new Date());
					sub.setPublishedUserId(user.getId());
				}
			}else {
				//修改
				isFlag = this.permitCheck(user,ActionsType.edit,masterId,ResourceType.course,sub.getId(),null,null);
			}
		}else if(null!=sub.getMoveDrafts()){
			//移动回发布前
			GcSubject subject = gcSubjectService.getById(sub.getId());
			if ((user.getIsOrgAdmin()&&null!=sub.getMoveDrafts())||(user.getId().equals(subject.getCreateUser()))){
				isFlag = true;
			}
		} else {
			//创建
			isFlag = this.permitCheck(user,ActionsType.createCourse,masterId,ResourceType.portal,null,null,null);
		}
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}
		if(null!=sub.getMoveDrafts()&&sub.getMoveDrafts().equals(TableConstant.COMMON_ZERO)){
			sub = gcSubjectService.getById(sub.getId());
			sub.setState(TableConstant.COMMON_ZERO);
			gcAccessService.deleteSubIdAccess(masterId,sub.getId());
			gcUserAccessPermissionService.deleteSubIdAccessPermissionList(masterId,sub.getId());
			contentGroupCourseAssignmentService.removeByMasterAndCourseId(masterId, sub.getId());
		}
		if (sub.getState() != null && sub.getState() == TableConstant.COMMON_ONE) {
			contentGroupCourseAssignmentService.save(user, sub, CourseType.MANDATORY);
			contentGroupCourseAssignmentService.save(user, sub, CourseType.OPTIONAL);
		}
		gcSubjectService.saveSubInfo(sub,null,master,user,request);

		List<GcUserAccessPermission> permission = gcUserAccessPermissionService.getPermissionByUidList(user.getId(),masterId);
		if (null==sub.getIsMyView()||sub.getIsMyView().equals(TableConstant.COMMON_ONE)){
			for (GcUserAccessPermission userAccessPermission : permission) {
				if (null!=userAccessPermission.getMustSubjectJson()){
					while (userAccessPermission.getMustSubjectJson().contains(sub.getId())){
						userAccessPermission.getMustSubjectJson().remove(sub.getId());
					}
				}
			}
			gcUserAccessPermissionService.updateBatchById(permission);
		}else {
			for (GcUserAccessPermission userAccessPermission : permission) {
				if (null==userAccessPermission.getMustSubjectJson()){
					JSONArray jsonArray = new JSONArray();
					jsonArray.add(sub.getId());
					userAccessPermission.setMustSubjectJson(jsonArray);
				}else {
					userAccessPermission.getMustSubjectJson().add(sub.getId());
				}
			}
			gcUserAccessPermissionService.updateBatchById(permission);
		}

		return new Message().ok("添加成功！").addData("sync", sub);
	}

	@ApiOperation(value = "添加视频记录以及其下的节点", httpMethod = "Post")
	@DeleteMapping("/delSub/{id}")
	public Message deleteSub(@PathVariable("id") Integer subId,HttpServletRequest request) throws IOException {
		GcMaster master = this.getMaster();
		Integer masterId = request.getIntHeader("masterId");
		GcUser user = this.getGcUser();
		boolean isFlag = this.permitCheck(user,ActionsType.delete,masterId,ResourceType.course,subId,null,null);
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}
		if (Objects.isNull(master)&&Objects.nonNull(masterId)){
			master = new GcMaster();
			master.setId(masterId);
		}
		return gvgMasterService.deleteSub(subId,EnvType.GC.getCode(),master,null);
	}

	@PostMapping("/videoComment")
	public Message videoComment(@RequestBody JSONObject jsonRequest,HttpServletRequest request) throws IOException {
		Integer vid = jsonRequest.getInteger("vid");
		Integer masterId = getHeaderMasterId(request);
		String comment = jsonRequest.getString("comment");
		Integer fileId = jsonRequest.getInteger("fileId");
		ApiAssert.notNull(vid, "参数vid缺失");
//        ApiAssert.notNull(comment, "参数comment缺失");
		GcUser user = this.getGcUser();
		boolean isFlag = this.permitCheck(user,ActionsType.comment,masterId,ResourceType.videoItem,vid,null,null);
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}
		GcVideoComment videoComment = new GcVideoComment();
		videoComment.setMasterId(masterId);
		videoComment.setUserId(user.getId());
		videoComment.setComment(comment);
		videoComment.setVideoId(vid);
		videoComment.setCreateTime(new Date());
		videoComment.setUpdateTime(new Date());
		if (null!=fileId){
			videoComment.setFileId(fileId);
			SysFile file = sysFileService.getById(fileId);
			file.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(file));
			videoComment.setCommentFile(file);
		}
		if (videoCommentService.saveVideoComment(videoComment)) {
			return new Message().ok("评论成功！").addData("comment",videoComment);
		} else {
			return new Message().error("评论失败！");
		}
	}

	@ApiOperation(value = "新建一个保存课程/视频的文件夹，带id可更新", httpMethod = "GET")
	@PostMapping("/newContentFolder")
	public Message newContentFolder(@RequestBody GcUserSaveFolder gcUserSaveFolder, HttpServletRequest request) throws IOException {
		ApiAssert.notNull(gcUserSaveFolder.getName(), "文件夹名称不可空");
		gcUserSaveFolder.setUserId(this.getGcUser().getId());
		gcUserSaveFolder.setMasterId(getHeaderMasterId(request));
		GcUser user = this.getGcUser();
		GcMaster master = masterService.getById(RequestUtil.getMasterId(request).get());
		boolean isFlag = this.permitCheck(user, ActionsType.createPlayList,master.getId(), ResourceType.portal,null,null,null);
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}
		if(gcUserSaveFolderService.saveOrUpdate(gcUserSaveFolder)) {
			return new Message().ok("保存成功").addData("folder", gcUserSaveFolder);
		}else {
			return new Message().ok("保存是吧");
		}

	}

	@ApiOperation(value = "添加视频记录以及其下的节点", httpMethod = "Post")
	@PostMapping("/createVideoPlayRecordAndNode")
	public Message createVideoPlayRecordAndNode(@RequestBody GcUserVideoPlay userVideoPlay, HttpServletRequest request) {
		Integer masterId = request.getIntHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		GcUser user = this.getGcUser();
		return gvgMasterService.createVideoPlayRecordAndNode(userVideoPlay,request,EnvType.PT.getCode(), user,masterId,this.getSystem());
	}

	@ApiOperation(value = "回答问题", httpMethod = "POST")
	@PostMapping("/answerQuestion")
	public Message answerQuestion(@RequestBody JSONObject jsonRequest, HttpServletRequest request) {
		Integer masterId = request.getIntHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		Integer eventId = jsonRequest.getInteger("eventId");
		if(Objects.isNull(eventId)){
			throw new SystemException(I18NUtil.get("powtoon.answer.error"));
		}
		return gvgMasterService.answerQuestion(jsonRequest,request,request.getIntHeader("masterId"),this.getGcUser(),EnvType.PT.getCode(),this.getSystem());
	}

	@ApiOperation(value = "Get a list of the contents of a single playlist", httpMethod = "POST")
	@PostMapping("/getContentFromOneFolder")
	public Message getContentFromOneFolder(@RequestBody GcUserSaveFolder gcUserSaveFolder, HttpServletRequest request) {
		Integer masterId = request.getIntHeader("masterId");
        if(Objects.isNull(gcUserSaveFolder.getId())){
			throw new SystemException(I18NUtil.get("powtoon.folder.error"));
		}
		String token = request.getHeader("Authorization");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (!"undefined".equals(token)) {
			GcUser gcUser = getGcUser();
			return gcMasterService.getContentFromOneFolder(gcUserSaveFolder,gcUser,request,EnvType.PT.getCode()).addData("systemTime",df.format(new Date()));
		}

		return gcMasterService.getContentFromOneFolder(gcUserSaveFolder,null,request,EnvType.PT.getCode()).addData("systemTime",df.format(new Date()));
	}

	@ApiOperation(value = "查询自己创建的所有二级课程")
	@PostMapping("/selectAllTopicList")
	public Message selectAllTopicList(@RequestBody GcSubject gcSubject, HttpServletRequest request) {
		Integer masterId = request.getIntHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		if(Objects.isNull(gcSubject.getId())){
			throw new SystemException(I18NUtil.get("powtoon.topic.error"));
		}
		GcMaster master = this.getMaster();
		if (Objects.isNull(master)&&Objects.nonNull(masterId)){
			master = new GcMaster();
			master.setId(masterId);
		}
		GcUser user = this.getGcUser();

		return new Message().ok().addData("topicList",subService.selectAllTopicList(masterId,TableConstant.COMMON_ONE,user.getId(),gcSubject));
	}

	@ApiOperation(value = "查询自己创建的所有二级课程")
	@GetMapping("/selectAllSub0ListByUserId")
	public Message selectAllSub0ListByUserId( HttpServletRequest request) {
		GcMaster master = this.getMaster();
		Integer masterId = request.getIntHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		if (Objects.isNull(master)&&Objects.nonNull(masterId)){
			master = new GcMaster();
			master.setId(masterId);
		}
		GcUser user = this.getGcUser();
		return new Message().ok().addData("sub0List",subService.selectAllSub0ListByUserId(masterId,TableConstant.COMMON_ZERO,user.getId()));
	}

	@ApiOperation(value = "批量更新问题")
	@GetMapping("/updateEventsTime")
	public Message updateEventsTime(@RequestBody List<GcEvent> eventList, HttpServletRequest request) {
		Integer masterId = request.getIntHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		GcMaster master = this.getMaster();
		if (Objects.isNull(master)&&Objects.nonNull(masterId)){
			master = new GcMaster();
			master.setId(masterId);
		}
		Integer videoId = eventList.get(0).getVideoId();
		return new Message().ok().addData("eventList",this.eventService.saveOrUpdateBatch(eventList));
	}

	@ApiOperation(value = "批量更新问题")
	@PostMapping("/getSysFile")
	public Message getSysFile(@RequestBody JSONObject jsonParams,HttpServletRequest request) {
		Integer masterId = request.getIntHeader("masterId");
		GcUser user = this.getGcUser();
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		GcMaster master = this.getMaster();
		if (Objects.isNull(master)&&Objects.nonNull(masterId)){
			master = new GcMaster();
			master.setId(masterId);
		}
		return new Message().ok().addData("eventList",gvgMasterService.getSysFile(jsonParams, EventUnifyType.powtoonResTypes,request,master,user.getId()));
	}

	@PostMapping("/delSub")
	public Message deleteSub(@RequestBody GcSubject subject,HttpServletRequest request) throws IOException {
		Integer masterId = request.getIntHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		GcMaster master = this.getMaster();
		if (Objects.isNull(master)&&Objects.nonNull(masterId)){
			master = new GcMaster();
			master.setId(masterId);
		}
		GcUser user = this.getGcUser();
		boolean isFlag = this.permitCheck(user,ActionsType.delete,masterId,ResourceType.course,subject.getId(),null,null);
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}
		if(Objects.nonNull(subject.getFid())){
			GcSubject gcSubject = subService.getById(subject.getId());
			GcSubject gcSubject0 = subService.getById(subject.getFid());
			gcSubject.setMasterId(gcSubject0.getMasterId());
			gcSubject.setSubId(subject.getFid());
			gcSubject.setFid(subject.getFid());
			if(subService.saveOrUpdate(gcSubject))return new Message().ok();
		}else {
			return gvgMasterService.deleteSub(subject.getId(),EnvType.PT.getCode(),master,this.getGcUser().getId());

		}
		return new Message().error();

//        System.out.println(master.getId());
//        if (subService.deleteSub(subId,master.getId())) return new Message().ok();
//        return new Message().error("删除失败");
	}

	@ApiOperation(value = "删除视频", httpMethod = "DELETE")
	@DeleteMapping("/delVideo/{id}")
	public Message deleteVideo(@PathVariable("id") Integer vid,HttpServletRequest request) throws IOException {
		GcMaster master = this.getMaster();
		Integer masterId = request.getIntHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		if (Objects.isNull(master)&&Objects.nonNull(masterId)){
			master = new GcMaster();
			master.setId(masterId);
		}
		GcUser user = this.getGcUser();
		boolean isFlag = this.permitCheck(user,ActionsType.delete,masterId,ResourceType.videoItem,vid,null,null);
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}
//        if (videoService.deleteVideo(vid)) return new Message().ok();
//
//        return new Message().error();
		return gvgMasterService.deleteVideoPt(vid,EnvType.PT.getCode(),this.getGcUser().getId(),master.getId(),request);
	}


	@ApiOperation(value = "channel新增修改")
	@GetMapping("/getBySlug")
	public Message getBySlug(String channelSlug,HttpServletRequest request){
		Integer masterId = request.getIntHeader("masterId");
		QueryWrapper<PtChannel> queryWrapper = new QueryWrapper<PtChannel>();
		queryWrapper.eq("master_id", masterId);
		queryWrapper.eq("channel_slug", channelSlug);
		Integer id = ptChannelService.getOne(queryWrapper).getId();
		return new Message().ok().addData("id",id);
	}


	@ApiOperation(value = "channel新增修改")
	@PostMapping("/saveOrUpdateChannel")
	public Message saveOrUpdateChannel(@RequestBody PtChannel ptChannel,HttpServletRequest request) throws IOException, PermitApiError, PermitContextError {
		Message message = new Message();
		Integer masterId = request.getIntHeader("masterId");
		GcMaster master = masterService.getById(masterId);
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.master.noMasterId"));
		}

		initPermit();
		boolean isOrgAdmin = false;
		GcUser user = this.getGcUser();
		if (null!=ptChannel.getVisibleFlag()&&ptChannel.getVisibleFlag().equals(TableConstant.COMMON_ONE)){
			UserRead userRoles = permit.api.users.get(user.getUsername());
			if (null!=userRoles.attributes){
				if (null!=userRoles.attributes.get("isOrgAdmin")){
					isOrgAdmin = (boolean) userRoles.attributes.get("isOrgAdmin");
				}
			}
		}

		boolean isAllowed = false;
		if (null!=ptChannel.getVisibleFlag()) {
			isAllowed = this.permitCheck(user, ActionsType.publish, masterId, ResourceType.channel, ptChannel.getId(), null, null);
			if (!isAllowed){
				throw new PermitException("No permission to change channel visibility!");
			}
		}

		if (null!=ptChannel.getId()){
			isAllowed = this.permitCheck(user,ActionsType.edit,masterId,ResourceType.channel,ptChannel.getId(),null,null);
		}else if (null!=ptChannel.getFid()){
			isAllowed = this.permitCheck(user,ActionsType.addContent,masterId,ResourceType.channel,ptChannel.getFid(),null,null);
		}else {
			isAllowed = this.permitCheck(user,ActionsType.createChannel,masterId,ResourceType.portal,null,null,null);
		}

		if (!isAllowed){
			throw new PermitException("No permission for this!");
		}

		ptChannel.setMasterId(masterId);
		if (null==ptChannel.getId()){
			ptChannel.setCreateUserId(user.getId());
		}
		if (Objects.isNull(master)){
			master = new GcMaster();
			master.setId(masterId);
		}
		if(Objects.nonNull(ptChannel.getChannelImgFileId())) {
			SysFile sysFile = sysFileService.getById(ptChannel.getChannelImgFileId());
			String imgFullFileUrl = sysFileService.getResFullUrl(sysFile, request);
			ptChannel.setImgFullFileUrl(imgFullFileUrl);
		}
		if(Objects.nonNull(ptChannel.getChannelAvatarFileId())) {
			SysFile avatarFile = sysFileService.getById(ptChannel.getChannelAvatarFileId());
			String avatarFullFileUrl = sysFileService.getResFullUrl(avatarFile, request);
			avatarFile.setFullFileUrl(avatarFullFileUrl);
			ptChannel.setAvatarFile(avatarFile);
		}
		try {

		if(Objects.nonNull(ptChannel.getVisibleFlag())) {
			if (ptChannel.getVisibleFlag() == 2) {
				if (ptChannelService.saveOrUpdate(ptChannel)) {
					if (null!=ptChannel.getTags()){
						//tag
						PtTags ptTags = new PtTags();
						ptTags.setChannelId(ptChannel.getId());
						ptTags.setMasterId(masterId);
						List<String> tagList = ptChannel.getTags().toJavaList(String.class);
						List<PtTags> newTagList = new ArrayList<>();
						int finalMasterId = masterId;
						tagList.forEach(i -> {
							//if (!newTagText.contains(i)){
							PtTags newTags = new PtTags();
							newTags.setMasterId(finalMasterId);
							newTags.setTagText(i);
							newTags.setChannelId(ptChannel.getId());
							newTags.setType(TableConstant.COMMON_ONE);
							newTags.setOrder(TableConstant.COMMON_ZERO);
							newTagList.add(newTags);
							//}
						});
						QueryWrapper<PtTags> queryWrapper2 = new QueryWrapper<>();
						queryWrapper2.in("master_id", masterId);
						queryWrapper2.in("channel_id", ptChannel.getId());
						queryWrapper2.in("type", TableConstant.COMMON_ONE);
						ptTagsService.remove(queryWrapper2);
						ptTagsService.saveOrUpdateBatch(newTagList);
						ptChannel.setAllTags(tagList);
					}

					List <Integer> subscribeAccessList = new ArrayList<>();
					if ((null!=ptChannel.getIsAllSubscribe()&&ptChannel.getIsAllSubscribe().equals(TableConstant.COMMON_ZERO))||(null!=ptChannel.getIsAllChoose()&&ptChannel.getIsAllChoose().equals(TableConstant.COMMON_ZERO))){
						if (isOrgAdmin){
							subscribeAccessList = accessService.findAccessListByMasterId(masterId).stream().map(GcAccess::getId).collect(Collectors.toList());
						}else {
							subscribeAccessList = accessService.listAccess(null, masterId, user.getId()).stream().map(GcAccess::getId).collect(Collectors.toList());
						}
						ptChannel.setAccessIdList(new ArrayList<>());
						ptChannel.setSubscribeAccessIdList(new ArrayList<>());
						if (null!=ptChannel.getIsAllChoose()&&ptChannel.getIsAllChoose().equals(TableConstant.COMMON_ZERO)){
							ptChannel.getAccessIdList().addAll(subscribeAccessList);
						}else {
							ptChannel.getSubscribeAccessIdList().addAll(subscribeAccessList);
						}
					}

					List<GcAccess> accessList = gcAccessService.getAccessByChannelId(masterId, ptChannel.getId());
					contentGroupChannelSubscriptionService.removeChannelFromContentGroups(accessList, ptChannel);

					List<GcUserAccessPermission> permissionList = gcUserAccessPermissionService.getContainsAccessPermissionList(ptChannel.getId().toString(),masterId);
					if (null!=permissionList && !permissionList.isEmpty()){
						for (GcUserAccessPermission permission : permissionList) {
							if (null!=permission.getChannelPermission()){
								permission.getChannelPermission().remove(ptChannel.getId());
							}
							if (null!=permission.getSubscribePermission()){
								permission.getSubscribePermission().remove(ptChannel.getId());
							}
						}
						gcUserAccessPermissionService.updateGcUserAccessPermissionsChannel(permissionList);
					}
					// Publish channel to team
					if (CollectionUtils.isNotEmpty(ptChannel.getAccessIdList()) && !ptChannel.getAccessIdList().isEmpty()) {
						contentGroupChannelSubscriptionService.savePublicChannels(ptChannel.getAccessIdList(), ptChannel.getId(), user);

						List<Integer> permissionUserIds = gcUserAccessService.selectGetUserAccessIdListUserIds(masterId, ptChannel.getAccessIdList());
						if (CollectionUtils.isNotEmpty(permissionUserIds)) {
							List<GcUserAccessPermission> gcUserAccessPermissionList = gcUserAccessPermissionService.selectUserAccessPermissions(permissionUserIds);
							for (GcUserAccessPermission gcUserAccessPermission : gcUserAccessPermissionList) {
								JSONArray jsonArray = gcUserAccessPermission.getChannelPermission();
								if (Objects.isNull(jsonArray)) {
									JSONArray array = new JSONArray();
									array.add(ptChannel.getId());
									gcUserAccessPermission.setChannelPermission(array);
								} else {
									if (!jsonArray.contains(ptChannel.getId())) {
										jsonArray.add(ptChannel.getId());
									}
									gcUserAccessPermission.setChannelPermission(jsonArray);
								}

							}
							gcUserAccessPermissionService.saveOrUpdateBatch(gcUserAccessPermissionList);
						}
					}

					if (CollectionUtils.isNotEmpty(ptChannel.getSubscribeAccessIdList()) && !ptChannel.getSubscribeAccessIdList().isEmpty()) {
						contentGroupChannelSubscriptionService.saveChannelSubscription(ptChannel.getSubscribeAccessIdList(), ptChannel.getId(), user);

						List<Integer> subscribePermissionUserIds = new ArrayList<>();
						if (null!=ptChannel.getSubscribeAccessIdList() && !ptChannel.getSubscribeAccessIdList().isEmpty()){
							subscribePermissionUserIds = gcUserAccessService.selectGetUserAccessIdListUserIds(masterId, ptChannel.getSubscribeAccessIdList());
						}
						if (CollectionUtils.isNotEmpty(subscribePermissionUserIds)) {
							List<GcUserAccessPermission> gcUserAccessPermissionList = gcUserAccessPermissionService.selectUserAccessPermissions(subscribePermissionUserIds);
							for (GcUserAccessPermission gcUserAccessPermission : gcUserAccessPermissionList) {
								JSONArray jsonArray = gcUserAccessPermission.getSubscribePermission();
								if (Objects.isNull(jsonArray)) {
									JSONArray array = new JSONArray();
									array.add(ptChannel.getId());
									gcUserAccessPermission.setSubscribePermission(array);
								} else {
									if (!jsonArray.contains(ptChannel.getId())) {
										jsonArray.add(ptChannel.getId());
									}
									gcUserAccessPermission.setSubscribePermission(jsonArray);
								}

							}
							gcUserAccessPermissionService.saveOrUpdateBatch(gcUserAccessPermissionList);
						}
					}
					return message.ok("success").addData("channel", ptChannel);
				}
			} else if (ptChannel.getVisibleFlag() == 1) {

				List<Integer> subscribePermissionUserIds = new ArrayList<>();
				List <Integer> subscribeAccessList = new ArrayList<>();
				if (null!=ptChannel.getIsAllSubscribe()&&ptChannel.getIsAllSubscribe().equals(TableConstant.COMMON_ZERO)){
					if (isOrgAdmin){
						subscribeAccessList = accessService.findAccessListByMasterId(masterId).stream().map(GcAccess::getId).collect(Collectors.toList());
					}else {
						subscribeAccessList = accessService.listAccess(null, masterId, user.getId()).stream().map(GcAccess::getId).collect(Collectors.toList());
					}
					ptChannel.getSubscribeAccessIdList().addAll(subscribeAccessList);
				}
				ptChannelService.saveOrUpdate(ptChannel);

				List<GcUserAccessPermission> userAccessPermissionList = gcUserAccessPermissionService.selectAllUsersInPortal(masterId);
				for (GcUserAccessPermission gcUserAccessPermission : userAccessPermissionList) {
					JSONArray jsonArray = gcUserAccessPermission.getChannelPermission();
					if (Objects.isNull(jsonArray)) {
						JSONArray array = new JSONArray();
						array.add(ptChannel.getId());
						gcUserAccessPermission.setChannelPermission(array);
					} else {
						if (!jsonArray.contains(ptChannel.getId())) {
							jsonArray.add(ptChannel.getId());
						}
						gcUserAccessPermission.setChannelPermission(jsonArray);
					}
				}
				gcUserAccessPermissionService.saveOrUpdateBatch(userAccessPermissionList);

				if (null!=ptChannel.getSubscribeAccessIdList()){
					List<GcUserAccessPermission> gcUserAccessPermissionList = new ArrayList<>();
					if (null!=ptChannel.getSubscribeAccessIdList()&&ptChannel.getSubscribeAccessIdList().size()!=TableConstant.COMMON_ZERO){
						subscribePermissionUserIds = gcUserAccessService.selectGetUserAccessIdListUserIds(masterId, ptChannel.getSubscribeAccessIdList());
					}
					if (CollectionUtils.isNotEmpty(subscribePermissionUserIds)) {
//						List<Integer> userAccessIds = gcUserAccessList.stream().map(GcUserAccess::getId).collect(Collectors.toList());
						gcUserAccessPermissionList = gcUserAccessPermissionService.selectUserAccessPermissions(subscribePermissionUserIds);
						for (GcUserAccessPermission permission : gcUserAccessPermissionList) {
							JSONArray array = new JSONArray();
							if (null==permission.getSubscribePermission()){
									array.add(ptChannel.getId());
									permission.setSubscribePermission(array);
							}else {
								array = permission.getSubPermission();
								array.add(ptChannel.getId());
								if (!permission.getSubscribePermission().contains(ptChannel.getId())){
									permission.setSubscribePermission(array);
								}
							}
						}
					}
					gcUserAccessPermissionService.saveOrUpdateBatch(gcUserAccessPermissionList);
				}
				return message.ok("success").addData("channel", ptChannel);
			} else if (ptChannel.getVisibleFlag() == 0) {
				if (ptChannelService.saveOrUpdate(ptChannel)) {
					return message.ok("success").addData("channel", ptChannel);
				} else {
					return message.error();
				}
			//公共
			}else if (ptChannel.getVisibleFlag() == TableConstant.COMMON_THREE){
				if (ptChannelService.saveOrUpdate(ptChannel)) {
					return message.ok("success").addData("channel", ptChannel);
				} else {
					return message.error();
				}
			}
		}else {
			if (ptChannelService.saveOrUpdate(ptChannel)) {
				return message.ok("success").addData("channel", ptChannel);
			} else {
				return message.error();
			}
		}
		}catch (DuplicateKeyException e){
			throw new SystemException(I18NUtil.get("userpt.channel.slug"));
		}
		return message.error();
	}

	@ApiOperation(value = "删除")
	@PostMapping("/deleteChannelSection")
	public Message deleteChannelSection(@RequestBody PtChannel ptChannel,HttpServletRequest request) throws IOException {
		Message message = new Message();
		if(Objects.isNull(ptChannel.getId())){
			throw new SystemException(I18NUtil.get("powtoon.channel.noChannelId"));
		}
		GcUser user = this.getGcUser();
		GcMaster master = masterService.getById(RequestUtil.getMasterId(request).get());
		boolean isFlag = this.permitCheck(user, ActionsType.delete, master.getId(), ResourceType.channel, ptChannel.getId(),null,null);
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}
		if(ptChannelService.removeById(ptChannel.getId())) {
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
		List<PtChannel> channels = ptChannelService.indexPtChannels(user.getId(),TableConstant.COMMON_ZERO,request,masterId);
		List<PtChannel> myChannels = ptChannelService.indexPtChannels(user.getId(),TableConstant.COMMON_ONE,request,masterId);
		PageInfo channelPageInfo = new PageInfo<>(channels);
		PageInfo myChannelPageInfo = new PageInfo<>(myChannels);
		message.ok().addData("myChannels",myChannelPageInfo);

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return message.ok()
			.addData("allChannelList",channelPageInfo)
			.addData("systemTime",df.format(new Date()));
	}

	@ApiOperation(value = "指定teamchannel查询")
	@PostMapping("/selectChannelsByTeam")
	public Message selectAllChannels(@RequestBody GcAccess access, HttpServletRequest request) {
		Message message = new Message();
		GcUser gcuser = this.getGcUser();
		Integer masterId = request.getIntHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.master.noMasterId"));
		}
		List<PtChannel> channels = ptChannelService.selectChannelsByTeam(access.getId(),masterId,gcuser.getId(),request);
		PageInfo<PtChannel> pageInfo = new PageInfo<>(channels);
		return message.ok().addData("channelPageInfo",pageInfo);
	}


	@ApiOperation(value = "查询categorylist", notes = "查询categorylist", httpMethod = "GET")
	@GetMapping("/selectCategoryList")
	public Message selectCategoryList(){
		Message message = new Message();
		List<GcCategory> gcCategories = gcProblemService.selectCategoryList();
		return message.ok().addData("categotyList",gcCategories);
	}

	@GetMapping("/selectPtchannelTags")
	public Message selectPtchannelTags(String name,HttpServletRequest request){
		Message message = new Message();
		Integer masterId = request.getIntHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.master.noMasterId"));
		}
		List<PtTags> ptTags = ptTagsService.selectPtChannelTags(masterId,request,name);
		PageInfo<PtTags> pageInfo = new PageInfo<>(ptTags);
		return message.ok().addData("tagPageInfo",pageInfo);
	}

	@ApiOperation(value = "channel详情页")
	@PostMapping("/selectChannelDetail")
	public Message selectChannelDetail(@RequestBody PtChannel ptChannel,HttpServletRequest request) throws IOException {
		Message message = new Message();
		GcMaster master = this.getMaster();
		Integer masterId = request.getIntHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.master.noMasterId"));
		}
		/*if(Objects.isNull(ptChannel.getId())){
			throw new SystemException(I18NUtil.get("powtoon.channel.noChannelId"));
		}*/
		Integer ptChannelId = null;
		if (null!=ptChannel.getId()){
			ptChannelId = ptChannel.getId();
		}else if (null!=ptChannel.getChannelSlug()){
			QueryWrapper<PtChannel> queryWrapper=new QueryWrapper<PtChannel>();
			queryWrapper.eq("channel_slug", ptChannel.getChannelSlug());
			queryWrapper.eq("master_id",masterId);
			ptChannelId = ptChannelService.getOne(queryWrapper).getId();
		}
		GcUser user = this.getGcUser();
		boolean isFlag = this.permitCheck(user, ActionsType.view, masterId, ResourceType.channel, ptChannelId,null,null);
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}

		String order = request.getHeader("order");
		PageInfo<GcSubject> pageInfo = new PageInfo<>();
		List<PtChannel> sectionList = new ArrayList<>();
		if (null!=ptChannel.getChannelSlug()){
			sectionList = ptChannelService.selectSectionList(null,ptChannel.getChannelSlug(),request,masterId);
		}
		if (null!=ptChannel.getId()){
			sectionList = ptChannelService.selectSectionList(ptChannel.getId(),null,request,null);
		}
		//List<PtChannel> sectionList = ptChannelService.selectSectionList(ptChannel.getId(),request);
		PageInfo sectionPageInfo = new PageInfo<>(sectionList);

		PtChannel channel = new PtChannel();
		if (null!=ptChannel.getChannelSlug()){
			channel = ptChannelService.selectChannelDetail(null,ptChannel.getChannelSlug(),request,order,masterId);
		}
		if (null!=ptChannel.getId()){
			channel = ptChannelService.selectChannelDetail(ptChannel.getId(),null,request,order,masterId);
		}

		//PtChannel channel = ptChannelService.selectChannelDetail(ptChannel.getId(),request,order);
		PtChannelSubscribe ptChannelSubscribe = new PtChannelSubscribe();
		if (null!=ptChannel.getId()){
			ptChannelSubscribe = ptChannelSubscribeService.selectIfSubscribe(user.getId(),ptChannel.getId());
		}else {
			QueryWrapper<PtChannel> queryWrapper = new QueryWrapper<PtChannel>();
			queryWrapper.eq("channel_slug",ptChannel.getChannelSlug());
			queryWrapper.eq("master_id",masterId);
			Integer channelId = ptChannelService.getOne(queryWrapper).getId();
			ptChannelSubscribe = ptChannelSubscribeService.selectIfSubscribe(user.getId(),channelId);
		}

		if(Objects.nonNull(ptChannelSubscribe)){
			channel.setFollowFlag(TableConstant.COMMON_ONE);
		}else {
			channel.setFollowFlag(TableConstant.COMMON_ZERO);
		}

		channel.setSectionPageInfo(sectionPageInfo);
		if(user.getId().equals(channel.getCreateUserId())){
			channel.setOwnFlag(TableConstant.COMMON_ONE);
		}else {
			channel.setOwnFlag(TableConstant.COMMON_ZERO);
		}
		SysFile sysFile = sysFileService.getById(channel.getChannelImgFileId());
		String channelImgFullFileUrl = sysFileService.getResFullUrl(sysFile,request);
		channel.setImgFullFileUrl(channelImgFullFileUrl);
		message.ok().addData("channel",channel);

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return message.ok().addData("systemTime",df.format(new Date()));
	}

	@ApiOperation(value = "文件id添加视频课程", httpMethod = "POST")
	@PostMapping("/saveVideo")
	public Message saveVideo(@RequestBody @ApiParam(name = "创建保存视频", value = "视频实体") GcVideo video, HttpServletRequest request) throws IOException {
		SysSystem sys = this.getSystem();
		GcMaster master = this.getMaster();
		Integer masterId = null;
		if (null==master&&null!=request.getHeader("masterId")){
			masterId = Integer.parseInt(request.getHeader("masterId"));
		}else {
			masterId = master.getId().intValue();
		}
		GcUser user = this.getGcUser();
		boolean isFlag = false;
		if (null!=video.getId()){
			isFlag = this.permitCheck(user, ActionsType.edit, masterId, ResourceType.videoItem, video.getId(),null,null);
		}else {
			isFlag = this.permitCheck(user, ActionsType.createVideoItem, masterId, ResourceType.portal, null,null,null);
		}
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}
		gcVideoService.saveVideoInfo(sys,video,masterId,request);

		if (null!=video.getSubId()) {
			video.setSubId0(gcSubjectService.getById(video.getSubId()).getFid());
		}
		return new Message().ok("添加成功！").addData("sync", video);
	}

	@ApiOperation(value = "查询section中的视频list")
	@PostMapping("/selectVideosInSection")
	public Message selectVideosInChannel(@RequestBody PtChannel ptChannel,HttpServletRequest request) throws IOException {
		Integer masterId = request.getIntHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}

		Message message = new Message();
		PtChannel channel = ptChannelService.getById(ptChannel.getId());
		String order = request.getHeader("order");
		List<SysFile> videoList;
		if(Objects.isNull(ptChannel.getSearchName())) {
			videoList = ptChannelService.selectVideosInSection(ptChannel.getId(), order, request, ptChannel.getSearchName(), null);
		}else {
			videoList = ptChannelService.selectVideosInSection(ptChannel.getId(), order, request, ptChannel.getSearchName(), channel.getLevel());
		}
		if (null!=videoList&& !videoList.isEmpty()){
			QueryWrapper<PtTags> queryWrapper2 = new QueryWrapper<>();
			queryWrapper2.eq("master_id",masterId);
			queryWrapper2.in("file_id",videoList.stream().map(SysFile::getId).collect(Collectors.toList()));
			queryWrapper2.eq("type",TableConstant.COMMON_TWO);
			List<PtTags> list = ptTagsService.list(queryWrapper2);

			Map<Integer,List<PtTags>> tagMap = list.stream().collect(Collectors.groupingBy(PtTags::getFileId));
			videoList.forEach(i->{
				if (null!=tagMap.get(i.getId())){
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
	public Message channelSubscribe(@RequestBody PtChannelSubscribe ptChannelSubscribe,HttpServletRequest request) throws IOException {
		Message message = new Message();
		GcUser user = this.getGcUser();
		Integer masterId = request.getIntHeader("masterId");
		boolean isFlag = this.permitCheck(user, ActionsType.subscribe, masterId, ResourceType.channel, ptChannelSubscribe.getChannelId(),null,null);
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}
		List<GcUserAccess> gcUserAccessList = gcUserAccessService.getAccessListByUserAndMasterId(user.getId(),masterId);
		List<Integer> gcUserAccessIds = gcUserAccessList.stream().map(GcUserAccess::getId).collect(Collectors.toList());
		List<GcUserAccessPermission> gcUserAccessPermissionList = gcUserAccessPermissionService.selectUserAccessPermissions(gcUserAccessIds);
		if(CollectionUtils.isNotEmpty(gcUserAccessPermissionList)){
			for(GcUserAccessPermission userAccessPermission : gcUserAccessPermissionList){
				JSONArray jsonArray  = new JSONArray();
				if(CollectionUtils.isEmpty(userAccessPermission.getSubscribePermission()) || userAccessPermission.getSubscribePermission()==null){
					jsonArray.add(ptChannelSubscribe.getChannelId());
					userAccessPermission.setSubscribePermission(jsonArray);
				}else {
					userAccessPermission.getSubscribePermission().add(ptChannelSubscribe.getChannelId());
				}
				if (null!=userAccessPermission.getChannelPermission()){
					userAccessPermission.getChannelPermission().remove(ptChannelSubscribe.getChannelId());
				}
			}
		}
		if(gcUserAccessPermissionService.saveOrUpdateBatch(gcUserAccessPermissionList)){
			ptChannelSubscribeService.subscribe(user, ptChannelSubscribe.getChannelId());
			return message.ok("success");
		}

		return message.error();
	}

	@ApiOperation(value = "channel取消订阅")
	@PostMapping("/channelUnSubscribe")
	public Message channelUnSubscribe(@RequestBody PtChannelSubscribe ptChannelSubscribe,HttpServletRequest request) throws IOException {
		Message message = new Message();
		GcUser user = this.getGcUser();
		Integer masterId = request.getIntHeader("masterId");
		boolean isFlag = this.permitCheck(user, ActionsType.unsubscribe, Integer.parseInt(request.getHeader("masterId")), ResourceType.channel, ptChannelSubscribe.getChannelId(),null,null);
		if (!isFlag){
			throw new PermitException("No permission for this!");
		}
		List<GcUserAccess> gcUserAccessList = gcUserAccessService.getAccessListByUserAndMasterId(user.getId(),masterId);
		List<Integer> gcUserAccessIds = gcUserAccessList.stream().map(GcUserAccess::getId).collect(Collectors.toList());
		List<GcUserAccessPermission> gcUserAccessPermissionList = gcUserAccessPermissionService.selectUserAccessPermissions(gcUserAccessIds);
		for(GcUserAccessPermission gcUserAccessPermission : gcUserAccessPermissionList){
			JSONArray jsonArray  = new JSONArray();
			if(CollectionUtils.isNotEmpty(gcUserAccessPermission.getSubscribePermission()) && gcUserAccessPermission.getSubscribePermission()!=null){
				if(gcUserAccessPermission.getSubscribePermission().contains(ptChannelSubscribe.getChannelId())){
					gcUserAccessPermission.getSubscribePermission().remove(ptChannelSubscribe.getChannelId());
				}
			}
			if (CollectionUtils.isEmpty(gcUserAccessPermission.getChannelPermission()) || gcUserAccessPermission.getChannelPermission() == null) {
				jsonArray.add(ptChannelSubscribe.getChannelId());
				gcUserAccessPermission.setChannelPermission(jsonArray);
			} else {
				gcUserAccessPermission.getChannelPermission().add(ptChannelSubscribe.getChannelId());
			}
		}
		ptChannelSubscribe.setUserId(user.getId());
		if(gcUserAccessPermissionService.saveOrUpdateBatch(gcUserAccessPermissionList)){
			ptChannelSubscribeService.unsubscribe(user, ptChannelSubscribe.getChannelId());
			return message.ok("success");
		}

		return message.error();
	}

	@ApiOperation(value = "channelContent保存内容")
	@PostMapping("/saveOrUpdateChannelContent")
	public Message saveChannelContent(@RequestBody List<PtChannelContent> ptChannelContent,HttpServletRequest request) throws IOException, PermitContextError, PermitApiError {
		Message message = new Message();
		if(CollectionUtils.isEmpty(ptChannelContent)){
			throw new SystemException(I18NUtil.get("powtoon.channel.noChannelContent"));
		}
		GcUser user = this.getGcUser();
		Integer masterId = request.getIntHeader("masterId");
		Integer channelFid = null;
		if (null!=ptChannelContent.get(TableConstant.COMMON_ZERO).getChannelId()){
			Integer channelId = ptChannelContent.get(TableConstant.COMMON_ZERO).getChannelId();
			if (null!=ptChannelService.getById(channelId).getFid()){
				channelFid = this.ptChannelService.getById(channelId).getFid();
			}else {
				channelFid = channelId;
			}
		}
		initPermit();
		boolean isOrgAdmin = false;
		UserRead userRoles = permit.api.users.get(user.getUsername());
		if (null!=userRoles.attributes){
			if (null!=userRoles.attributes.get("isOrgAdmin")){
				isOrgAdmin = (boolean) userRoles.attributes.get("isOrgAdmin");
			}
		}
		boolean isFlag = this.permitCheck(user, ActionsType.edit, masterId, ResourceType.channel, channelFid,null,null);
		if (!isFlag&&!isOrgAdmin){
			throw new PermitException("No permission for this!");
		}

		List<Integer> fileIds = ptChannelContent.stream().map(PtChannelContent::getFileId).collect(Collectors.toList());
		List<PtChannelContent> channelContentList = ptChannelContentService.selectContentExist(ptChannelContent.get(TableConstant.COMMON_ZERO).getChannelId());
		List<Integer> ids = channelContentList.stream().map(PtChannelContent::getFileId).collect(Collectors.toList());
		for(PtChannelContent channelContent : ptChannelContent){
			if(ids.contains(channelContent.getFileId())){
				throw new SystemException(I18NUtil.get("powtoon.channel.duplicate.video.error"));
			}
		}
		List<PtTags> tagsList = new ArrayList<>();

		List<SysFile> sysFileList = sysFileService.selectBatch(fileIds);
		ptChannelContentService.saveOrUpdateChannelContent(ptChannelContent, sysFileList, channelFid);

		for(PtChannelContent channelContent : ptChannelContent){
			channelContent.getCourseTags().forEach(i -> {
				PtTags newTags = new PtTags();
				newTags.setMasterId(masterId);
				newTags.setTagText(i.toString());
				newTags.setChannelId(channelContent.getChannelId());
				newTags.setType(TableConstant.COMMON_TWO);
				newTags.setOrder(TableConstant.COMMON_ZERO);
				newTags.setFileId(channelContent.getFileId());
				tagsList.add(newTags);
			});

			for(SysFile sysFile : sysFileList){
				if(sysFile.getId().equals(channelContent.getFileId())){
					String fullFileUrl = sysFileService.getResFullUrl(sysFile,request);
					String snapShotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
					sysFile.setFullFileUrl(fullFileUrl);
					sysFile.setSnapshotUrl(snapShotUrl);
					sysFile.setThumbNailUrl(thumbnailProvider.getThumbnailUrl(sysFile));
					channelContent.setVideoFile(sysFile);
				}
				if (null!=sysFile.getGcUser().getAvatarFileId()){
					SysFile file = sysFileService.getById(sysFile.getGcUser().getAvatarFileId());
					sysFile.getGcUser().setAvatarFullFileUrl(sysFileService.getResFullUrl(file,request));
				}
			}
		}
		ptTagsService.saveOrUpdateBatch(tagsList);
		return message.ok("success").addData("contentList",ptChannelContent);
	}

	@ApiOperation(value = "channelContent删除内容")
	@PostMapping("/deleteChannelContent")
	public Message deleteChannelContent(@RequestBody PtChannelContent ptChannelContent,HttpServletRequest request) throws IOException, PermitContextError, PermitApiError {
		Message message = new Message();
		if(Objects.isNull(ptChannelContent)){
			throw new SystemException(I18NUtil.get("powtoon.channel.noChannelContent"));
		}
		GcUser user = this.getGcUser();
		Integer masterId = request.getIntHeader("masterId");
		initPermit();
		boolean isOrgAdmin = false;
		UserRead userRoles = permit.api.users.get(user.getUsername());
		if (null!=userRoles.attributes){
			if (null!=userRoles.attributes.get("isOrgAdmin")){
				isOrgAdmin = (boolean) userRoles.attributes.get("isOrgAdmin");
			}
		}
		if (!isOrgAdmin){
			boolean isFlag = this.permitCheck(user, ActionsType.delete, masterId, ResourceType.channel, ptChannelContent.getChannelId(),null,null);
			if (!isFlag){
				throw new PermitException("No permission for this!");
			}
		}
		if(ptChannelContentService.deleteContent(ptChannelContent.getFileId(),ptChannelContent.getChannelId())){
			return message.ok("success");
		}else {
			return message.error();
		}
	}

	@ApiOperation(value = "channelContent修改视频顺序")
	@PostMapping("/changeContentOrder")
	public Message changeContentOrder(@RequestBody List<Integer> contentIds,HttpServletRequest request) {
		Message message = new Message();
		if(CollectionUtils.isEmpty(contentIds)){
			throw new SystemException(I18NUtil.get("powtoon.channel.noChannelContent"));
		}
		if(ptChannelContentService.changeContentOrder(contentIds)){
			return message.ok("success");
		}else {
			return message.error();
		}
	}

	@ApiOperation(value = "Video details page")
	@PostMapping("/contentVideoDetail")
	public Message contentVideoDetail(@RequestBody PtChannelContent ptChannelContent,HttpServletRequest request) {
		Message message = new Message();
		GcUser currentUser = this.getGcUser();
		if(Objects.isNull(ptChannelContent.getId())){
			throw new SystemException(I18NUtil.get("powtoon.channel.noChannelContent"));
		}
		ptChannelContent = ptChannelContentService.getById(ptChannelContent.getId());
		GcUserVideoAction gcUserVideoAction = gcUserVideoActionService.getOldChannelVideoAction(ptChannelContent.getContentId(),currentUser.getId(),TableConstant.COMMON_ONE);
		PtChannel ptchannel = ptChannelService.getById(ptChannelContent.getChannelId());
		GcUser user = userService.getById(ptchannel.getCreateUserId());
		GcUserInfo gcUserInfo = gcUserInfoService.getById(user.getInfoId());
		if (null!=gcUserInfo.getAvatarFileId()) {
			gcUserInfo.setAvatarFile(sysFileService.getById(gcUserInfo.getAvatarFileId()));
			sysFileService.getResFullUrl(gcUserInfo.getAvatarFile(),request);
		}
		user.setInfo(gcUserInfo);
		ptchannel.setCreateUser(user);
		GcVideo channelVideoContent = gcVideoService.getById(ptChannelContent.getContentId());
		SysFile videoFile = getFile(request, channelVideoContent, currentUser.getId());
		if(Objects.nonNull(gcUserVideoAction)){
			videoFile.setLikedFlag(TableConstant.COMMON_ONE);
		}else {
			videoFile.setLikedFlag(TableConstant.COMMON_ZERO);
		}
		message.ok().addData("thisVideo",videoFile);

		PtChannel ptChannel = new PtChannel();
		if(Objects.nonNull(ptChannelContent.getChannelId())) {
			ptChannel = ptChannelService.getById(ptChannelContent.getChannelId());
			String channelSnapShotUrl = sysFileService.getResFullUrl(sysFileService.getById(ptChannel.getChannelImgFileId()),request);
			ptChannel.setImgFullFileUrl(channelSnapShotUrl);
		}
		ptChannel.setCreateUser(user);
		if (null!=ptchannel.getFid()){
			PtChannel channel = ptChannelService.getById(ptchannel.getFid());
			ptChannel.setChannelSlug(channel.getChannelSlug());
		}
		message.ok().addData("channel",ptChannel);
		List<SysFile> videofiles = ptChannelContentService.selectVideosInChannel(ptChannel.getId(),null,ptChannelContent.getFileId(),request, currentUser.getId());
		for(SysFile sysFile : videofiles){
			populateVideoContent(request, sysFile, currentUser.getId());
		}
		PageInfo videoFiles = new PageInfo<>(videofiles);
		message.ok().addData("videoList",videoFiles);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return message.ok().addData("systemTime",df.format(new Date()));
	}

	@ApiOperation(value = "视频点赞")
	@PostMapping("/likeVideoInChannel")
	public Message likeVideoInChannel(@RequestBody GcUserVideoAction gcUserVideoAction,HttpServletRequest request) {
		Message message = new Message();
		GcUser user = this.getGcUser();
		if(Objects.isNull(gcUserVideoAction.getFileId())){
			throw new SystemException(I18NUtil.get("powtoon.channel.noContentFileId"));
		}
		GcVideo videoContent = gcVideoService.getVideoContent(gcUserVideoAction.getFileId()).orElseThrow();
		gcUserVideoAction.setContentId(videoContent.getId());

		GcUserVideoAction oldAction = videoActionService.getOldChannelVideoAction(gcUserVideoAction.getContentId(), user.getId(), gcUserVideoAction.getType());
		if(Objects.nonNull(oldAction)){
			videoActionService.deleteChannelOldVideoAction(gcUserVideoAction.getContentId(), user.getId(), gcUserVideoAction.getType());
		}else {
			gcUserVideoAction.setUserId(user.getId());
			videoActionService.saveOrUpdate(gcUserVideoAction);
		}
		return message.ok();
	}

	@ApiOperation(value = "修改视频名字")
	@PostMapping("/updateVideoName")
	public Message likeVideoInChannel(@RequestBody SysFile videoFile,HttpServletRequest request) {
		Message message = new Message();
		if(Objects.isNull(videoFile.getId())){
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
		list = gcUserEventResourceService.getEventResListForWorkBook(eventId, this.getGcUser().getId(), studentId,masterId,request,TableConstant.COMMON_ONE);
		//查询别人的评论
		List<GcUserEventResource> gcUserEventResourceList = gcUserEventResourceService.getEventResListForWorkBook(eventId, this.getGcUser().getId(), studentId,masterId,request,TableConstant.COMMON_TWO);
		PageInfo<GcUserEventResource> pageInfo = new PageInfo<>(list);
		PageInfo<GcUserEventResource> otherPageInfo = new PageInfo<>(gcUserEventResourceList);
		return new Message().ok().addData("eventResList", pageInfo).addData("otherPageInfo",otherPageInfo);
	}

	@ApiOperation(value = "删除单个文件夹", httpMethod = "GET")
	@GetMapping("/deleteContentFromOneFolder")
	public Message deleteFolder(Integer folderId) {
		ApiAssert.notNull(folderId, "folderId不可空");

		GcUserSaveFolder gcUserSaveFolder = new GcUserSaveFolder();
		gcUserSaveFolder.setId(folderId);
		gcUserSaveFolder.setUserId(this.getGcUser().getId());

		if(gcUserSaveFolderService.countFolder(gcUserSaveFolder)==0)
			return new Message().error("该用户folderId不存在记录");

		if (gcUserSaveFolderService.removeById(folderId))
			return new Message().ok("删除成功");
		else
			return new Message().error("删除失败");
	}

	@ApiOperation(value = "删除单个playList视频", httpMethod = "GET")
	@GetMapping("/deleteVideoFromOneFolder")
	public Message deleteVideoFromOneFolder(Integer folderId,Integer videoId) {
		ApiAssert.notNull(folderId, "folderId不可空");
		QueryWrapper<GcUserSaveContent> queryWrapper = new QueryWrapper<GcUserSaveContent>();
		queryWrapper.eq("video_id",videoId);
		queryWrapper.eq("folder_id",folderId);

		if (gcUserSaveContentService.remove(queryWrapper))
			return new Message().ok("删除成功");
		else
			return new Message().error("删除失败");
	}

	private SysFile getFile(HttpServletRequest request, GcVideo channelVideoContent, Integer userId) {
		Integer contentId = channelVideoContent.getId();
		SysFile videoFile = sysFileService.getById(channelVideoContent.getFileId());
		String snapShotUrl = sysFileService.getVideoSnapshotUrl(channelVideoContent);
		String fullFileUrl = sysFileService.getVideoPlayerUrl(videoFile, request);
		videoFile.setFullFileUrl(fullFileUrl);
		videoFile.setSnapshotUrl(snapShotUrl);
		videoFile.setVideoId(contentId);
		videoFile.setIsLiked(gcUserVideoActionService.isLikedByUser(contentId, userId) ? 1 : 0);
		videoFile.setLikeNum(gcUserVideoActionService.countLikeForVideo(contentId));
		return videoFile;
	}
}


