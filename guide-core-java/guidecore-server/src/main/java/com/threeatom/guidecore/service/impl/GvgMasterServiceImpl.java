package com.threeatom.guidecore.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.AccessRoleType;
import com.threeatom.guidecore.constant.EnvType;
import com.threeatom.guidecore.constant.MessageEventType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.dto.request.SearchDto;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcEvent;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcMasterHomeInfo;
import com.threeatom.guidecore.entity.GcMasterMessage;
import com.threeatom.guidecore.entity.GcResource;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcSubjectComplete;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserAccess;
import com.threeatom.guidecore.entity.GcUserAnswer;
import com.threeatom.guidecore.entity.GcUserEventResource;
import com.threeatom.guidecore.entity.GcUserFabulous;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import com.threeatom.guidecore.entity.GcUserVideoPlay;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.entity.PtChannelContent;
import com.threeatom.guidecore.entity.PtTags;
import com.threeatom.guidecore.entity.SubjectTotals;
import com.threeatom.guidecore.enums.SearchType;
import com.threeatom.guidecore.mapper.GcMasterMapper;
import com.threeatom.guidecore.service.FeatureToggleService;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcEventService;
import com.threeatom.guidecore.service.GcMasterHomeInfoService;
import com.threeatom.guidecore.service.GcMasterMessageService;
import com.threeatom.guidecore.service.GcMasterService;
import com.threeatom.guidecore.service.GcResourceService;
import com.threeatom.guidecore.service.GcSubjectCompleteService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.GcUserAnswerService;
import com.threeatom.guidecore.service.GcUserEventResourceService;
import com.threeatom.guidecore.service.GcUserFabulousService;
import com.threeatom.guidecore.service.GcUserNoteCommentService;
import com.threeatom.guidecore.service.GcUserNoteService;
import com.threeatom.guidecore.service.GcUserSaveFolderService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.GcUserVideoActionService;
import com.threeatom.guidecore.service.GcUserVideoPlayService;
import com.threeatom.guidecore.service.GcVideoCommentService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.GvgMasterService;
import com.threeatom.guidecore.service.NewUiGcSubjectService;
import com.threeatom.guidecore.service.PtChannelContentService;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.service.PtTagsService;
import com.threeatom.guidecore.service.SysMenuService;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.guidecore.util.RequestUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysFileCaption;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileCaptionService;
import com.threeatom.system.service.SysFileService;
import io.swagger.annotations.ApiOperation;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class GvgMasterServiceImpl extends ServiceImpl<GcMasterMapper, GcMaster> implements GvgMasterService{

	@Autowired
	private SysFileService sysFileService;


	@Autowired
	private GcMasterHomeInfoService iGcMasterHomeInfoService;

	@Autowired
	private GcSubjectService subjectService;

	@Autowired
	private GcVideoService gcVideoService;

	@Autowired
	private GcMasterService gcMasterService;


	@Autowired
	private NewUiGcSubjectService newUiGcSubjectService;

	@Autowired
	private GcUserVideoActionService videoActionService;//用户视频操作--查询评论、点赞、星级评价

	@Autowired
	private GcVideoService service;

	@Autowired
	private PtChannelService ptChannelService;


	@Autowired
	private GcUserService gcUserService;//用户服务类--统计参与人数

	@Autowired
	private GcUserVideoActionService gcUserVideoActionService;

	@Autowired
	private GcEventService gcEventService;

	@Autowired
	private GcUserAccessService userAccessService;

	@Autowired
	private GcResourceService resourceService;

	@Autowired
	private GcVideoCommentService gcVideoCommentService;

	@Autowired
	private GcUserNoteService gcUserNoteService;

	@Autowired
	private GcEventService eventService;

	@Autowired
	private GcUserAnswerService userAnswerService;

	@Autowired
	private GcUserEventResourceService gcUserEventResourceService;

	@Autowired
	private GcUserNoteCommentService gcUserNoteCommentService;

	@Autowired
	private GcUserFabulousService gcUserFabulousService;

	@Autowired
	private GcUserAccessService gcUserAccessService;

	@Autowired
	private SysFileCaptionService sysFileCaptionService;

	@Autowired
	private GcUserVideoPlayService userVideoPlayService;

	@Autowired
	private GcUserService userService;

	@Autowired
	private GcMasterMessageService masterMessageService;

	@Autowired
	private GcAccessService gcAccessService;

	@Autowired
	private GcUserSaveFolderService gcUserSaveFolderService;

	@Autowired
	private GcSubjectCompleteService subjectCompleteService;

	@Autowired
	private GcSubjectService gcSubjectService;

	@Autowired
	private GcSubjectService subService;

	@Autowired
	private GcVideoService videoService;

	@Autowired
	private PtTagsService ptTagsService;

	@Autowired
	private GcUserVideoPlayService gcUserVideoPlayService;

	@Autowired
	private GcSubjectCompleteService completeService;

	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private GcContentGroupCourseAssignmentService courseAssignmentService;

	@Autowired
	private PtChannelContentService ptChannelContentService;

	@Autowired
	private SysMenuService sysMenuService;

	@Autowired
	private FeatureToggleService featureToggleService;

	private final String COURSE_SEARCH_FEATURE_TOGGLE = "coursesEnabled";

	public Message newPtIndexHome(JSONObject requestParams, HttpServletRequest request, SysSystem system, PortalUser portalUser) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Message message  = new Message();
		String portalId = requestParams.getString("portalId");
		GcMaster gcMaster = gcMasterService.getMaster(portalId);
		if (null!=portalUser){
		ExecutorService executor = Executors.newFixedThreadPool(3);//做3个线程
		executor.submit(() -> {
			List<GcSubject> subjectList = subjectService.selectSubjectByNewIndexHome(gcMaster.getId(),portalUser.getUserId(),new PageParam(request));
			PageParam pageParam = new PageParam(request);
			if (subjectList.size()>=pageParam.getPageSize()){
				subjectList =subjectList.subList(TableConstant.COMMON_ZERO,pageParam.getPageSize());
			}
			List<GcSubject> myMaySubject = subjectService.selectSubjectMay(gcMaster.getId(),portalUser.getUserId(),new PageParam(request));

			//两个课程都要进度等详细信息,放一起查询,避免两次查
			List<GcSubject> subjectInfo = new ArrayList<>();
			subjectInfo.addAll(subjectList);
			subjectInfo.addAll(myMaySubject);

			//课程进度评分等查询
			List<Integer> allLevel0subIds = subjectInfo.stream().map(GcSubject::getId).collect(Collectors.toList());
			List<Integer> imageSubIds = subjectInfo.stream().map(GcSubject::getSubImgId).collect(Collectors.toList());
			List<Integer> videoIdlist = gcVideoService.getVideoIdListBySubId(allLevel0subIds);
			List<GcVideo> videoList = gcVideoService.getVideoLongListByVideoId(videoIdlist);

			videoList = gcVideoService.buildVideoInfo(portalUser.getUserId(),null,videoList,gcMaster.getId(),request,EnvType.PT.getCode());
			Map<Integer,List<GcVideo>> groupBySubId = videoList.stream().filter(e -> null!=e.getSubjectSubId()).collect(Collectors.groupingBy(GcVideo::getSubjectSubId));

			List<SysFile> sysFileList = new ArrayList<>();
			if (TableConstant.COMMON_ZERO!=allLevel0subIds.size()){
				sysFileList = sysFileService.listByIds(imageSubIds);
			}
			Map<Integer,SysFile> sysFileMap = sysFileList.stream().collect(Collectors.toMap(SysFile::getId,SysFile -> SysFile, (key1, key2) -> key2, LinkedHashMap::new));
			Map<Integer, GcUser> subjectUsers = gcUserService.getWatchedUserNum(allLevel0subIds,gcMaster.getId());

			Map<String, Object> videoParams = new HashMap<>(2);
			Integer ids = TableConstant.COMMON_ZERO;
			videoParams.put("ids",ids);
			videoParams.put("subjectIds", allLevel0subIds);
			Map<Integer, GcUserVideoAction> subjectUserStar = new HashMap<>();
			subjectUserStar = videoActionService.getSubjectUserStar(videoParams);

			if(myMaySubject != null && myMaySubject.size() > 0){
				for(GcSubject li:myMaySubject) {
					if (null !=li.getSubImgId()&&null!=sysFileMap.get(li.getSubImgId())){
						SysFile file = sysFileMap.get(li.getSubImgId());
						file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
						li.setSubImgFile(file);
					}

					if(CollectionUtils.isNotEmpty(subjectUsers)) {
						GcUser gcUser = subjectUsers.get(li.getId());
						if (null != gcUser) {
							li.setSubjectUsers(gcUser.getSubjectUsers());
						}else {
							li.setSubjectUsers(TableConstant.COMMON_ZERO);
						}
					}

					//
					if(groupBySubId.get(li.getId())!=null){
						List<GcVideo> gcVideos = groupBySubId.get(li.getId());
						Integer totalSeconds = gcVideos.stream().filter(a -> a.getVideoTime()!=null).mapToInt(GcVideo::getVideoTime).sum();
						li.setVideosTotalLong(totalSeconds);
					}

					GcUserVideoAction videoActions = subjectUserStar.get(li.getId());
					if(videoActions != null) {
						//按type 进行分组
						// 1.2k type=3的平均值 1.2k是打星的总人数
						li.setStarValue(videoActions.getSubjectStarAvg());//星级平均值
						// 打星总人数
						li.setStarUsers(videoActions.getSubjectStarUsers());
					}else {
						li.setStarValue(TableConstant.starValue0);//星级平均值
						li.setStarUsers(TableConstant.starUsers);
					}
					if (null!=portalUser) {
						if (null!=groupBySubId.get(li.getId())){
							Map<Integer, List<GcVideo>> sub1Map = groupBySubId.get(li.getId()).stream().collect(Collectors.groupingBy(GcVideo::getSubId));
							List<GcSubject> twoSubject = newUiGcSubjectService.buildSubject1(sub1Map);
							SubjectTotals subjectTotals = calcTotals(twoSubject, portalUser.getUserId(), true, gcMaster.getId(), EnvType.PT.getCode());
							li.setPercents(new BigDecimal(subjectTotals.getTotalProgressPercent()));
						}
					}

					List<Integer> identifyingList = new ArrayList<>();
					//完成
					if (null!=li.getPercents()&&li.getPercents().equals(100)){
						li.setIdentifying(TableConstant.COMMON_TWO);
						identifyingList.add(TableConstant.COMMON_TWO);
					}
					//未开始
					if (null==li.getPercents()||li.getPercents().equals(0)){
						li.setIdentifying(TableConstant.COMMON_ZERO);
						identifyingList.add(TableConstant.COMMON_ZERO);
					}
					//证书
					if (null!=li.getCertificatesFlag()&&li.getCertificatesFlag().equals(TableConstant.COMMON_ONE)){
						identifyingList.add(TableConstant.COMMON_FOUR);
					}
					li.setIdentifyings(identifyingList);
				}
			}
			String json = JSON.toJSONString(myMaySubject,SerializerFeature.DisableCircularReferenceDetect);
			myMaySubject =JSONArray.parseArray(json,GcSubject.class);

			//subjectList的循环
			if(subjectList != null && subjectList.size() > 0){
				System.out.println("????????");
				for(GcSubject li:subjectList) {
					if (null !=li.getSubImgId()&&null!=sysFileMap.get(li.getSubImgId())){
						SysFile file = sysFileMap.get(li.getSubImgId());
						file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
						li.setSubImgFile(file);
					}

					if(CollectionUtils.isNotEmpty(subjectUsers)) {
						GcUser gcUser = subjectUsers.get(li.getId());
						if (null != gcUser) {
							li.setSubjectUsers(gcUser.getSubjectUsers());
						}else {
							li.setSubjectUsers(TableConstant.COMMON_ZERO);
						}
					}

					if(groupBySubId.get(li.getId())!=null){
						List<GcVideo> gcVideos = groupBySubId.get(li.getId());
						Integer totalSeconds = gcVideos.stream().filter(a -> a.getVideoTime()!=null).mapToInt(GcVideo::getVideoTime).sum();
						li.setVideosTotalLong(totalSeconds);
					}

					GcUserVideoAction videoActions = subjectUserStar.get(li.getId());
					if(videoActions != null) {
						//按type 进行分组
						// 1.2k type=3的平均值 1.2k是打星的总人数
						li.setStarValue(videoActions.getSubjectStarAvg());//星级平均值
						// 打星总人数
						li.setStarUsers(videoActions.getSubjectStarUsers());
					}else {
						li.setStarValue(TableConstant.starValue0);//星级平均值
						li.setStarUsers(TableConstant.starUsers);
					}
					if (null!=portalUser) {
						if (null!=groupBySubId.get(li.getId())){
							Map<Integer, List<GcVideo>> sub1Map = groupBySubId.get(li.getId()).stream().collect(Collectors.groupingBy(GcVideo::getSubId));
							List<GcSubject> twoSubject = newUiGcSubjectService.buildSubject1(sub1Map);
							SubjectTotals subjectTotals = calcTotals(twoSubject, portalUser.getUserId(), true, gcMaster.getId(), EnvType.PT.getCode());
							li.setPercents(new BigDecimal(subjectTotals.getTotalProgressPercent()));
						}
					}
					List<Integer> identifyingList = new ArrayList<>();
					//完成
					if (null!=li.getPercents()&&li.getPercents().equals(100)){
						li.setIdentifying(TableConstant.COMMON_TWO);
						identifyingList.add(TableConstant.COMMON_TWO);
					}
					//未开始
					if (null==li.getPercents()||li.getPercents().equals(0)&&li.getIsToDo()==TableConstant.COMMON_ZERO){
						li.setIdentifying(TableConstant.COMMON_ZERO);
						identifyingList.add(TableConstant.COMMON_ZERO);
					}
					//证书
					if (null!=li.getCertificatesFlag()&&li.getCertificatesFlag().equals(TableConstant.COMMON_ONE)){
						identifyingList.add(TableConstant.COMMON_FOUR);
					}
					li.setIdentifyings(identifyingList);
				}
			}
			PageInfo<GcSubject> subjectPageInfo = new PageInfo<>(subjectList);
			message.ok().addData("subjectList", subjectPageInfo);
			PageInfo<GcSubject> myMaySubjectPage = new PageInfo<>(myMaySubject);
			message.addData("discoverCourses",myMaySubjectPage);

			//已订阅的channel视频,自己上传的不显示
			List<PtChannel> channelPage = ptChannelService.searchChannelsBySysFileNew(portalUser, request);
			message.addData("channelVideoPage", new PageInfo<>(channelPage));

		});

			executor.submit(() -> {
				//My subscriptions-channel 我已订阅的(不含我创建的)；订阅时间排序
				List<PtChannel> channels = ptChannelService.newIndexHomeChannels(portalUser, request);
				message.addData("subscriptionsChannel", new PageInfo<>(channels));
				//playlist
				List<GcUserSaveFolder> recommentPlayList = gcUserSaveFolderService.selectFolderInMaster(gcMaster.getId());
				List<Integer> recommenFolderIds = recommentPlayList.stream().map(GcUserSaveFolder::getId).collect(Collectors.toList());
				List<GcUserSaveFolder> recomendedPlaylists = gcUserSaveFolderService.getPtNewHomePlayList(portalUser.getUserId(), gcMaster.getId(),recommenFolderIds,request);

				List<Integer> firstVideos = recomendedPlaylists.stream().filter(e->null!=e.getFirstVideoFileId()).map(GcUserSaveFolder::getFirstVideoFileId).collect(Collectors.toList());

				if (CollectionUtils.isNotEmpty(firstVideos)) {
					List<SysFile> fileList = sysFileService.listByIds(firstVideos);
					fileList.forEach(i -> i.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(i)));
					Map<Integer, SysFile> firstVideoMap =
						fileList.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));

					for (GcUserSaveFolder playlist : recomendedPlaylists) {
						//缩略图
						if (Objects.nonNull(playlist.getFirstVideoFileId()) &&
							null != firstVideoMap.get(playlist.getFirstVideoFileId())) {
							SysFile sysFile = firstVideoMap.get(playlist.getFirstVideoFileId());
							if (null != playlist.getSaveContentList().get(TableConstant.COMMON_ZERO)) {
								playlist.getSaveContentList().get(TableConstant.COMMON_ZERO)
									.setVideoFile(sysFile);
								playlist.getSaveContentList().get(TableConstant.COMMON_ZERO).getVideoFile()
									.setSnapshotUrl(sysFile.getSnapshotUrl());
							}
							playlist.setSnapshotUrl(sysFile.getSnapshotUrl());
						}
						playlist.setPermissions(authorizationService.listPermissions(playlist, portalUser));
					}
				}

				String playlistJson = JSON.toJSONString(recomendedPlaylists,SerializerFeature.DisableCircularReferenceDetect);
				recomendedPlaylists =JSONArray.parseArray(playlistJson,GcUserSaveFolder.class);
				message.addData("playList", new PageInfo<>(recomendedPlaylists));
			});

		executor.submit(()->{
			//Trending Now-channel视频： 最多赞+最多观看的channel视频 （含自己的）
			List<PtChannel> nowChannel = ptChannelService.getPtChannelVideoNow(portalUser, request);
			message.addData("nowChannel", new PageInfo<>(nowChannel));
		});

			executor.shutdown();
			try {
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);//设置等待时间最大（即为不设置）
			}catch (Exception exception){
				exception.printStackTrace();
			}
		}
		message.addData("systemTime",df.format(new Date()));
		return message;
	}

	@Override
	public Message portalInfosUnlogin(JSONObject requestParams, GcUser user, HttpServletRequest request) {
		if (requestParams.get("state") != null) {
			request.setAttribute("state", requestParams.get("state").toString());
		}
		if (requestParams.get("user") != null && user != null) {
			request.setAttribute("createUser", user.getId());
		}
		if (requestParams.get("type") != null && user != null) {
			request.setAttribute("type", requestParams.get("type"));
		}
		if (requestParams.get("subjectName") != null) {
			request.setAttribute("subjectName", requestParams.get("subjectName"));
		}
		if (user != null) {
			request.setAttribute("userId", user.getId());
		}

		request.setAttribute("isPt", TableConstant.COMMON_ZERO);
		String portalId = requestParams.getString("portalId");
		GcMaster gcMaster = gcMasterService.getMaster(portalId);

		if (Objects.nonNull(gcMaster.getFaviconLogoFileId())) {
			SysFile sysFile = sysFileService.getById(gcMaster.getFaviconLogoFileId());
			String faviconUrl = sysFileService.getResFullUrl(sysFile, request);
			gcMaster.setFaviconFullFileUrl(faviconUrl);
		}
		//查询此门户下是否有免费code
		GcAccess gcAccess = gcAccessService.selectFreeCodeByMaster(gcMaster.getId());
		if (Objects.nonNull(gcAccess)) {
			gcMaster.setFreeAccessCode(gcAccess);
		}
		if (Objects.nonNull(gcMaster.getLogoId())) {
			SysFile sysFile = sysFileService.getById(gcMaster.getLogoId());
			String fullFileUrl = sysFileService.getResFullUrl(sysFile, request);
			gcMaster.setLogoFullUrl(fullFileUrl);
		}
		SysFile logoFile = sysFileService.selectByLogoId(gcMaster.getLogoId());
		String logoFullUrl = sysFileService.getResFullUrl(logoFile, request);
		if (gcMaster.getProfilePhotoId() != null) {
			SysFile profileFile = sysFileService.getById(gcMaster.getProfilePhotoId());
			String profileUrl = sysFileService.getResFullUrl(profileFile, request);
			gcMaster.setProfilePhotoFullFileUrl(profileUrl);
		}
		gcMaster.setLogoFullUrl(logoFullUrl);

		List<GcMasterHomeInfo> infoList =
			iGcMasterHomeInfoService.getGcMasterHomeInfoList(gcMaster.getId(), TableConstant.gcMasterHomeInfo_name_page,
				null, request);

		return new Message().ok()
			.addData("homeInfo", infoList)
			.addData("homeInfoIndex", sysMenuService.getLevel3List())
			.addData("master", gcMaster);
	}

	@Override
	public Message search(SearchDto searchDto, HttpServletRequest request, GcUser user, SysSystem system) {
		try {
			SearchType searchType = searchDto.getSearchType();
			Map<String, Object> searchParameters = new HashMap<>();
			searchParameters.put("searchName", searchDto.getSearchName());
			searchParameters.put("returnType", searchDto.getReturnType());

			Integer userId = null;
			if (user != null) {
				searchParameters.put("userId", user.getId());
				userId = user.getId();
			}

			Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
			Message message = new Message().ok();
			message.addData("systemTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            return switch (searchType) {
				case ALL -> searchAll(request, user, system, searchParameters, userId, masterId, message);
                case VIDEO -> searchVideos(request, system, searchParameters, userId, masterId, message);
                case COURSE -> searchCourses(request, user, system, searchParameters, masterId, message);
                case CHANNEL -> searchChannels(request, user, searchParameters, userId, masterId, message);
                case PLAYLIST -> searchPlaylists(request, searchParameters, masterId, userId, message);
            };
        } catch (Exception e) {
			log.error("", e);
			return new Message().error(e.getMessage());
		}
	}

	private boolean isCourseSearchEnabled() {
        return Boolean.parseBoolean(featureToggleService.getFeatureToggle(COURSE_SEARCH_FEATURE_TOGGLE).getValue());
    }

	private Message searchAll(HttpServletRequest request, GcUser user, SysSystem system,
							  Map<String, Object> searchParameters, Integer userId, Integer masterId,
							  Message message) {
		searchVideos(request, system, searchParameters, userId, masterId, message);
		searchChannels(request, user, searchParameters, userId, masterId, message);
		searchPlaylists(request, searchParameters, masterId, userId, message);
		if (isCourseSearchEnabled()) {
			searchCourses(request, user, system, searchParameters, masterId, message);
		}
		return message;
	}

	private Message searchPlaylists(HttpServletRequest request, Map<String, Object> searchParameters, Integer masterId,
									Integer userId, Message message) {
		request.setAttribute("playListName", searchParameters.get("searchName").toString());
		List<Integer> recommendedPlaylistIds =
			gcUserSaveFolderService.selectFolderInMaster(masterId).stream()
				.map(GcUserSaveFolder::getId)
				.collect(Collectors.toList());
		List<GcUserSaveFolder> recommendedPlaylists =
			gcUserSaveFolderService.getPtHomePlayList(userId, masterId, recommendedPlaylistIds, request);
		for (GcUserSaveFolder playlist : recommendedPlaylists) {
			if (Objects.nonNull(playlist.getFirstVideoFileId())) {
				SysFile sysFile = sysFileService.getById(playlist.getFirstVideoFileId());
				String snapshotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
				playlist.setSnapshotUrl(snapshotUrl);
			}
		}

		if (recommendedPlaylists.isEmpty()) {
			request.removeAttribute("playListName");
			recommendedPlaylists =
				gcUserSaveFolderService.getPtHomePlayList(null, masterId, recommendedPlaylistIds, request);
			for (GcUserSaveFolder gcUserSaveFolder : recommendedPlaylists) {
				if (Objects.nonNull(gcUserSaveFolder.getFirstVideoFileId())) {
					SysFile sysFile = sysFileService.getById(gcUserSaveFolder.getFirstVideoFileId());
					String snapshotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
					gcUserSaveFolder.setSnapshotUrl(snapshotUrl);
				}
			}

			return message.addData("recommenFolderListNullPageInfo", new PageInfo<>(recommendedPlaylists));
		}

		return message.addData("recommenFolderListPageInfo", new PageInfo<>(recommendedPlaylists));
	}

	private Message searchChannels(HttpServletRequest request, GcUser user, Map<String, Object> searchParameters,
								   Integer userId, Integer masterId, Message message) {
		request.setAttribute("searchName", searchParameters.get("searchName").toString());
		List<PtChannel> channels = ptChannelService.indexSearchChannels(userId, null, request, masterId);
		if (channels.isEmpty()) {
			if (Objects.isNull(user)) {
				channels = ptChannelService.indexSearchChannels(null, null, request, masterId);
			} else {
				List<PtChannel> publicChannels =
					ptChannelService.indexSearchChannels(user.getId(), TableConstant.COMMON_ZERO, request,
						masterId);
				List<PtChannel> userChannels =
					ptChannelService.indexSearchChannels(user.getId(), TableConstant.COMMON_ONE, request,
						masterId);
				if (!publicChannels.isEmpty() && !userChannels.isEmpty()) {
					publicChannels.addAll(userChannels);//合并我的频道和公共频道
					channels = publicChannels;
				} else if (!publicChannels.isEmpty()) {
					channels = publicChannels;
				} else {
					channels = userChannels;
				}
			}

			return message.addData("channelNullPage", new PageInfo<>(channels));
		}

		return message.addData("channelPage", new PageInfo<>(channels));
	}

	private Message searchCourses(HttpServletRequest request, GcUser user, SysSystem system,
								  Map<String, Object> searchParameters, Integer masterId, Message message) {
		if (!isCourseSearchEnabled()) {
			throw new SystemException("Course search is disabled by feature toggle.");
		}
		searchParameters.remove("videoName");
		searchParameters.put("subjectName", searchParameters.get("searchName"));
		PageInfo<GcSubject> coursePageInfo =
			newUiGcSubjectService.list(searchParameters, system, request, EnvType.PT.getCode());
		if (CollectionUtils.isEmpty(coursePageInfo.getList())) {
			List<GcSubject> courses =
				subjectService.getLevel0SubListWithImg(masterId, request, null);
			List<Integer> courseIds =
				courses.stream().map(GcSubject::getId).collect(Collectors.toList());
			List<SysFile> courseFiles = new ArrayList<>();
			if (!courseIds.isEmpty()) {
				courseFiles = sysFileService.listByIds(courseIds);
			}

			Map<Integer, SysFile> idToCourseFile = courseFiles.stream().collect(
				Collectors.toMap(SysFile::getId, Function.identity(), (key1, key2) -> key2, LinkedHashMap::new));

			if (CollectionUtils.isNotEmpty(courses)) {
				for (GcSubject course : courses) {
					if (course.getSubImgId() == null) {
						continue;
					}

					if (idToCourseFile.get(course.getSubImgId()) != null) {
						SysFile file = idToCourseFile.get(course.getSubImgId());
						if (file != null) {
							file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
							course.setSubImgFile(file);
						}
					}
				}
			}

			Map<Integer, GcUser> courseIdToCourseUsers = gcUserService.getWatchedUserNum(courseIds, masterId);
			List<Integer> courseVideoIds = gcVideoService.getVideoIdListBySubId(courseIds);
			List<GcVideo> courseVideos = gcVideoService.getVideoLongListByVideoId(courseVideoIds);
			if (null != user) {
				courseVideos = gcVideoService.buildVideoInfo(user.getId(), null, courseVideos, masterId, request,
					EnvType.PT.getCode());
			}
			Map<Integer, List<GcVideo>> courseIdToVideos =
				courseVideos.stream().filter(e -> null != e.getSubjectSubId())
					.collect(Collectors.groupingBy(GcVideo::getSubjectSubId));

			for (GcSubject course : courses) {
				if (CollectionUtils.isNotEmpty(courseIdToCourseUsers)) {
					GcUser courseUser = courseIdToCourseUsers.get(course.getId());
					if (null != courseUser) {
						course.setSubjectUsers(courseUser.getSubjectUsers());
					} else {
						course.setSubjectUsers(TableConstant.COMMON_ZERO);
					}
				}
			}

			Map<String, Object> videoParams = new HashMap<>(2);
			Integer ids = TableConstant.COMMON_ZERO;
			videoParams.put("ids", ids);
			videoParams.put("subjectIds", courseIds);
			Map<Integer, GcUserVideoAction> courseIdToVideoAction = videoActionService.getSubjectUserStar(videoParams);
			for (GcSubject course : courses) {
				if (courseIdToVideos.get(course.getId()) != null) {
					Integer courseTotalSeconds =
						courseIdToVideos.get(course.getId()).stream()
							.filter(a -> a.getVideoTime() != null)
							.mapToInt(GcVideo::getVideoTime)
							.sum();
					course.setVideosTotalLong(courseTotalSeconds);
				}

				GcUserVideoAction videoActions = courseIdToVideoAction.get(course.getId());
				if (videoActions != null) {
					course.setStarValue(videoActions.getSubjectStarAvg());
					course.setStarUsers(videoActions.getSubjectStarUsers());
				} else {
					course.setStarValue(TableConstant.starValue0);
					course.setStarUsers(TableConstant.starUsers);
				}

				if (null != user) {
					if (null != courseIdToVideos.get(course.getId())) {
						Map<Integer, List<GcVideo>> sub1Map = courseIdToVideos.get(course.getId()).stream()
							.collect(Collectors.groupingBy(GcVideo::getSubId));
						List<GcSubject> twoSubject = newUiGcSubjectService.buildSubject1(sub1Map);
						SubjectTotals subjectTotals =
							calcTotals(twoSubject, user.getId(), true, masterId, EnvType.PT.getCode());
						course.setPercents(new BigDecimal(subjectTotals.getTotalProgressPercent()));
					}
				}
			}

			coursePageInfo = new PageInfo<>(courses);
			return message.addData("subjectNullPage", coursePageInfo);
		}

		List<Integer> courseVideoIds = gcVideoService.getVideoIdListBySubId(
			coursePageInfo.getList().stream().map(GcSubject::getId).collect(Collectors.toList()));
		List<GcVideo> videos = gcVideoService.getVideoLongListByVideoId(courseVideoIds);
		if (null != user) {
			videos = gcVideoService.buildVideoInfo(user.getId(), null, videos, masterId, request,
				EnvType.PT.getCode());
		}
		Map<Integer, List<GcVideo>> courseIdToVideos =
			videos.stream().filter(e -> null != e.getSubjectSubId())
				.collect(Collectors.groupingBy(GcVideo::getSubjectSubId));

		if (user != null) {
			for (GcSubject course : coursePageInfo.getList()) {
				if (courseIdToVideos.get(course.getId()) == null) {
					continue;
				}
				Map<Integer, List<GcVideo>> sub1Map = courseIdToVideos.get(course.getId()).stream()
					.collect(Collectors.groupingBy(GcVideo::getSubId));
				List<GcSubject> twoSubject = newUiGcSubjectService.buildSubject1(sub1Map);
				SubjectTotals courseTotals =
					calcTotals(twoSubject, user.getId(), true, masterId, EnvType.PT.getCode());
				course.setPercents(new BigDecimal(courseTotals.getTotalProgressPercent()));
			}
		}

		return message.addData("subjectPage", coursePageInfo);
	}

	private Message searchVideos(HttpServletRequest request, SysSystem system, Map<String, Object> searchParameters,
								 Integer userId, Integer masterId, Message message) {
		searchParameters.put("videoName", searchParameters.get("searchName"));
		searchParameters.put("pageNum", request.getHeader("pageNum"));
		searchParameters.put("pageSize", request.getHeader("pageSize"));
		request.setAttribute("searchName", searchParameters.get("searchName"));

		Boolean loadVideoSuggestions = true;

		List<PtChannel> channels = ptChannelService.searchChannelsBySysFile(userId, request, masterId);
		PageInfo<PtChannel> channelVideosPage = new PageInfo<>(channels);
		if (!channelVideosPage.getList().isEmpty()) {
			message.addData("channelVideoPage", channelVideosPage);
			loadVideoSuggestions = false;
		}

		if (isCourseSearchEnabled()) {
			PageInfo<GcVideo> courseVideosPage = service.page(searchParameters, system, request);
			if (!courseVideosPage.getList().isEmpty()) {
				message.addData("videoPage", courseVideosPage);
				loadVideoSuggestions = false;
			}
		}

		if (loadVideoSuggestions) {
			request.removeAttribute("searchName");
			channels = ptChannelService.searchChannelsBySysFile(userId, request, masterId);
			message.addData("videoNullPage", new PageInfo<>(channels));
		}
		return message;
	}

	@Override
	public Message navigation(Map<String, Object> params, HttpServletRequest request,SysSystem system,PortalUser portalUser,Integer envFlag){
		Integer masterId = request.getIntHeader("masterId");
		GcMaster gcMaster = gcMasterService.getById(masterId);
		Message msg = new Message().ok();
		if(params==null) params=new HashMap<>();
		if(params.get("fid")==null) {
			throw new SystemException(I18NUtil.get("一级课程id fid不可空"));
		}
		GcSubject subject=subjectService.getById(Integer.parseInt(params.get("fid").toString()));
		SysFile imgFile = sysFileService.getById(subject.getSubImgId());
		sysFileService.getResFullUrl(imgFile,request);
		subject.setSubImgFile(imgFile);
		if(null != subject.getSubImgId()){
			SysFile sysFile = sysFileService.getById(subject.getSubImgId());
			sysFileService.getResFullUrl(sysFile,request);
			subject.setSubImgFile(sysFile);
		}
		GcUser gcUser = null;
		if(params.get("studentId")==null) {
			gcUser = gcUserService.getUserByIdCache(portalUser.getUserId());
		}else {
			//后续需增加判断，该用户是否是这个学生的老师
			gcUser=gcUserService.getUserByIdCache((Integer)params.get("studentId"));
			sysFileService.getResFullUrl(gcUser.getInfo().getAvatarFile(), request);
			msg.addData("student", gcUser);
		}

		List<Integer> subIds = new ArrayList<>();
		subIds.add(Integer.parseInt(params.get("fid").toString()));
		Map<Integer, GcUser> subjectUsers = gcUserService.getWatchedUserNum(subIds,masterId);
		List<GcVideo> videosBySubjectIds0 = gcVideoService.getVideosBySubjectIds0(subIds, gcUser.getId(),masterId,request,envFlag);

		Integer resourceNums = resourceService.getResourceNum(masterId,subIds,null);

		Map<String, Object> videoParams = new HashMap<>(2);
		videoParams.put("subjectIds", subIds);
		videoParams.put("type", TableConstant.gcUserVideoAction_type_star3);
		Map<Integer, GcUserVideoAction> subjectUserStar = gcUserVideoActionService.getSubjectUserStar(videoParams);
		if(!subjectUsers.isEmpty()) {
			subject.setSubjectUsers(subjectUsers.get(Integer.parseInt(params.get("fid").toString())).getSubjectUsers());
		}
		if(null!=videosBySubjectIds0 && TableConstant.COMMON_ZERO!=videosBySubjectIds0.size()) {
			subject.setVideosTotalNum(videosBySubjectIds0.size());
		}
		if(null!=videosBySubjectIds0 && TableConstant.COMMON_ZERO!=videosBySubjectIds0.size()) {
			Integer eventTotalNum = videosBySubjectIds0.stream().filter(e -> null != e.getEventNum()).mapToInt(GcVideo::getEventNum).sum();
			Integer answeredEventNum = videosBySubjectIds0.stream().filter(e -> null != e.getAnsweredNums()).mapToInt(GcVideo::getAnsweredNums).sum();
			subject.setEventTotalNum(eventTotalNum);
			subject.setAnsweredEventNum(answeredEventNum);
		}else {
			subject.setEventTotalNum(TableConstant.COMMON_ZERO);
			subject.setAnsweredEventNum(TableConstant.COMMON_ZERO);
		}
		if(null!=subjectUserStar.get(Integer.parseInt(params.get("fid").toString()))) {
			subject.setStarValue(subjectUserStar.get(Integer.parseInt(params.get("fid").toString())).getSubjectStarAvg());
		}else {
			subject.setStarValue(TableConstant.starValue0);
		}

		try {
			Integer userId = gcUser.getId();
			params.put("userId", userId);
			List<Integer> subIdList = new ArrayList<>();
			PageInfo<GcSubject> page = newUiGcSubjectService.listSubjectByFid(params, system, request,true,subIdList, envFlag);
			if(page.getSize()==0) {//可能是没权限，可能二级课程是时空，还需优化

				QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<>();
				queryWrapper.eq("fid",params.get("fid"));
				queryWrapper.ne("state",0);
				int c = subjectService.count(queryWrapper);
				if(c!=0) {
					return new Message().error(800, "You do not have access to this course. Please get in touch with your mentor to gain access.");
				}
			}

			List<GcSubject> orderSubject = page.getList();
            List<Integer> videoIds = new ArrayList<>();
			List<Integer> orderTwoSubIds = orderSubject.stream().map(GcSubject::getId).collect(Collectors.toList());
			if (null!=orderTwoSubIds&&TableConstant.COMMON_ZERO!=orderTwoSubIds.size()){
				videoIds = videoService.getIdsBySubIds(orderTwoSubIds);
			}
			Map<Integer,List<PtTags>> tagListMap = new HashMap<>();
			if (TableConstant.COMMON_ZERO!=videoIds.size()){
				QueryWrapper<PtTags> tagsQueryWrapper = new QueryWrapper<>();
				tagsQueryWrapper.in("video_id",videoIds);
				if (null!=ptTagsService.list(tagsQueryWrapper)){
					tagListMap = ptTagsService.list(tagsQueryWrapper).stream().collect(Collectors.groupingBy(PtTags::getVideoId));
				}
			}

			Map<Integer, List<PtTags>> finalTagListMap = tagListMap;
			orderSubject.forEach(sub->{
				List<GcVideo> videos = sub.getGcVideos();
				if(null!=videos && TableConstant.COMMON_ZERO!=videos.size()) {
					Long videoFinishedNum = videos.stream().filter(e -> null != e.getCompleteStatus()).filter(e -> e.getCompleteStatus() == 2).count();
					sub.setVideoFinishedNum(videoFinishedNum);
					Long eventAnsweredNum = videos.stream().filter(e -> null != e.getAnsweredNums()).mapToLong(GcVideo::getAnsweredNums).sum();
					Integer eventTotalNum = videos.stream().filter(e -> null != e.getEventNum()).mapToInt(GcVideo::getEventNum).sum();
					sub.setEventTotalNum(eventTotalNum);
					sub.setAnsweredEventNum(eventAnsweredNum.intValue());
				}
				if (null!=videos&&videos.size()>0){
					for (GcVideo gcVideo : sub.getGcVideos()) {
						if (null!= finalTagListMap.get(gcVideo.getId())){
							List<PtTags> videoTagList = finalTagListMap.get(gcVideo.getId());
							List<String> videoTagText =  videoTagList.stream().map(PtTags::getTagText).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
							gcVideo.setCourseTags(JSONArray.parseArray(JSON.toJSONString(videoTagText)));
						}
					}
				}

			});
			page.setList(orderSubject);
			msg.addData("page", page);


			//计算右上角的进度、播放进度、视频完成数、问题数
			List<GcSubject> subjects = page.getList();
			for(int i=0;i<subjects.size();i++){
				Integer totalSeconds = TableConstant.COMMON_ZERO;
				if (Objects.nonNull(subjects.get(i))&&Objects.nonNull(subjects.get(i).getGcVideos())){
					subjects.get(i).setVideosTotalNum(subjects.get(i).getGcVideos().size());
					for(int j=0;j<subjects.get(i).getGcVideos().size();j++){
						if (null == subjects.get(i).getGcVideos().get(j).getEventList()){
							continue;
						}
						if (subjects.get(i).getGcVideos().get(j).getVideoTime()==null){
							totalSeconds += TableConstant.COMMON_ZERO;
						}else {
							totalSeconds += subjects.get(i).getGcVideos().get(j).getVideoTime();
						}
					}
					subjects.get(i).setVideosTotalLong(totalSeconds);
				}
			}
			List<GcSubject> subjects1 = subjects.stream().filter(e->null!=e.getGcVideos()).collect(Collectors.toList());
			List<GcSubject> subjectInSubTotal = new ArrayList<>();
			SubjectTotals subjectTotals = calcTotals(subjects1, userId,true,masterId,envFlag);

			if(envFlag.equals(EnvType.GC.getCode())) {
				List<GcVideo> videos = videoService.selectVideoPlayListBySubId(subject.getId(), portalUser.getUserId());
				List<Integer> vids = videos.stream().map(GcVideo::getId).collect(Collectors.toList());
				Map<Integer, GcUserVideoPlay> playMap = gcUserVideoPlayService.findVideoPalyStateByVideos(vids, portalUser.getUserId(), masterId);
				List<GcEvent> eventList = eventService.selectEventByUserIdAndSubjectId(portalUser.getUserId(), subject.getId(), masterId);
				List<GcEvent> eventAnswers = gcEventService.findEventAnswerByVideoIdsUser(vids, portalUser.getUserId(), masterId, EnvType.GC.getCode());
				Collection<GcUserVideoPlay> collection = playMap.values();
				List<GcUserVideoPlay> list = new ArrayList<GcUserVideoPlay>(collection);
				List<GcUserVideoPlay> sortedVideoList = list.stream().filter(e->e.getUpdateTime()!=null).sorted(Comparator.comparing(GcUserVideoPlay::getUpdateTime).reversed()).collect(Collectors.toList());
				List<GcEvent> sortedEventList = eventAnswers.stream().filter(e->e.getAnswerCreateTime()!=null).sorted(Comparator.comparing(GcEvent::getAnswerCreateTime).reversed()).collect(Collectors.toList());
				Integer compare = null;
				if (CollectionUtils.isNotEmpty(sortedEventList) && CollectionUtils.isNotEmpty(sortedVideoList)) {
					compare = sortedVideoList.get(0).getUpdateTime().compareTo(sortedEventList.get(0).getAnswerCreateTime());
				}
				DateFormat format = new SimpleDateFormat("MMMMM dd, yyyy", Locale.ENGLISH);
				if(Objects.nonNull(subjectTotals.getTotalProgressPercent())) {
					if (subjectTotals.getTotalProgressPercent().equals(100)) {
						if (Objects.nonNull(compare)) {
							if (compare < 0) {
								subjectTotals.setCompletionDate(format.format(sortedEventList.get(0).getAnswerCreateTime()));
							} else {
								subjectTotals.setCompletionDate(format.format(sortedVideoList.get(0).getUpdateTime()));
							}
						} else if (CollectionUtils.isNotEmpty(sortedEventList) && CollectionUtils.isEmpty(sortedVideoList)) {
							subjectTotals.setCompletionDate(format.format(sortedEventList.get(0).getAnswerCreateTime()));
						} else if (CollectionUtils.isEmpty(sortedEventList) && CollectionUtils.isNotEmpty(sortedVideoList)) {
							subjectTotals.setCompletionDate(format.format(sortedVideoList.get(0).getUpdateTime()));
						}
					}
				}
				//查询开始时间start
				List<GcUserVideoPlay> sortedStartVideoList = list.stream().filter(e->e.getUpdateTime()!=null).sorted(Comparator.comparing(GcUserVideoPlay::getUpdateTime)).collect(Collectors.toList());
				List<GcEvent> sortedStartEventList = eventAnswers.stream().filter(e->e.getAnswerCreateTime()!=null).sorted(Comparator.comparing(GcEvent::getAnswerCreateTime)).collect(Collectors.toList());
				Integer startCompare = null;
				if (CollectionUtils.isNotEmpty(sortedEventList) && CollectionUtils.isNotEmpty(sortedVideoList)) {
					startCompare = sortedVideoList.get(0).getUpdateTime().compareTo(sortedEventList.get(0).getAnswerCreateTime());
				}
				if (Objects.nonNull(startCompare)) {
					if (startCompare < 0) {
						subjectTotals.setStartTime(format.format(sortedStartEventList.get(0).getAnswerCreateTime()));
					} else {
						subjectTotals.setStartTime(format.format(sortedStartVideoList.get(0).getUpdateTime()));
					}
				} else if (CollectionUtils.isNotEmpty(sortedStartEventList) && CollectionUtils.isEmpty(sortedStartVideoList)) {
					subjectTotals.setStartTime(format.format(sortedStartEventList.get(0).getAnswerCreateTime()));
				} else if (CollectionUtils.isEmpty(sortedStartEventList) && CollectionUtils.isNotEmpty(sortedStartVideoList)) {
					subjectTotals.setStartTime(format.format(sortedStartVideoList.get(0).getUpdateTime()));
				}
			}

			//给二级课程排序
			List<GcSubject> orderSubList = subjectTotals.getSubjects();
			List<GcSubject> order = new ArrayList<>();
			if (Objects.nonNull(orderSubList)&&orderSubList.size()!=0){
				order = orderSubList.stream().sorted(Comparator.comparing(GcSubject::getOrder)).collect(Collectors.toList());
			}
			subjectTotals.setSubjects(order);
			msg.addData("subjectTotals",subjectTotals);

			Integer lastVideoId = newUiGcSubjectService.selectLastVideoId(Integer.parseInt(params.get("fid").toString()),userId,masterId);
			if(null==lastVideoId && null!=order && TableConstant.COMMON_ZERO!=order.size()){
				Map<String, Object> subParam =  new HashMap<>(1);
				Integer firstSubId = order.get(0).getId();
				params.put("subjectId",firstSubId);
				params.put("userId", portalUser.getUserId());
				params.put("masterId",masterId);
				Message message =  service.getVideosBySubIds(firstSubId, subParam, system, request);
				List<GcVideo> videoList = new ArrayList<>();
				Map<String,Object> map = message.getData();
				PageInfo pageInfo = (PageInfo) map.get("videos");
				videoList = pageInfo.getList();
				if(CollectionUtils.isNotEmpty(videoList)) {
					Integer startVideoId = videoList.get(0).getId();
					msg.addData("startVideoId", startVideoId);
				}
			}
			msg.addData("lastVideoId",lastVideoId);

			if (envFlag.equals(EnvType.PT.getCode())){
				QueryWrapper<PtTags> queryWrapper = new QueryWrapper<>();
				queryWrapper.in("subject_id", subject.getId());
				queryWrapper.in("master_id", masterId);
				List<String> tagsLists = ptTagsService.list(queryWrapper).stream().map(PtTags::getTagText).collect(Collectors.toList());
				subject.setCourseTags(JSONArray.parseArray(JSON.toJSONString(tagsLists)));
			}
			//课程详情页封面
			if(envFlag.equals(EnvType.PT.getCode()) && CollectionUtils.isNotEmpty(subject.getSubdetail_img_id())){
				Integer imgId = Integer.parseInt(subject.getSubdetail_img_id().get("subDetailImgId").toString());
				SysFile sysFile = sysFileService.getById(imgId);
				String fullUrl = sysFileService.getResFullUrl(sysFile,request);
				subject.setSubDetailImgUrl(fullUrl);
			}
			subject.setResourceNum(resourceNums);
			//添加是否本人能查看反显
			List<Integer> courseIds = courseAssignmentService.getMustCoursesContentGroupAssignmentIds(userId,masterId);
			boolean isMyView = courseIds.stream()
				.anyMatch(courseId -> Objects.equals(courseId, subject.getId()));
			subject.setIsMyView(isMyView ? TableConstant.COMMON_ZERO : TableConstant.COMMON_ONE);

			if ((subject.getCreateUser().equals(userId) || portalUser.isOrgAdmin()) && subject.isPrivate()){
				subject.setMode(TableConstant.COMMON_ONE);
			}else {
				subject.setMode(TableConstant.COMMON_ZERO);
			}
			subject.setPermissions(authorizationService.listPermissions(subject, portalUser));

			msg.addData("subject", subject);
			msg.addData("master",gcMaster);
			return msg;
		}catch (Exception e){
			log.error("", e);
			return new Message().error(e.getMessage());
		}
	}


	@Override
	public SubjectTotals calcTotals(List<GcSubject> subjects, Integer userId, boolean ifStudent, Integer masterId,Integer envFlag) {
		SubjectTotals totals = new SubjectTotals();
		totals.setUserId(userId);
		if(CollectionUtils.isNotEmpty(subjects)){
			Map<Integer, List<GcVideo>> videoCompleteStatusBySubject =subjectVideos(subjects);
			if(MapUtils.isNotEmpty(videoCompleteStatusBySubject)){
				List<GcSubject>  vos = new ArrayList<>(subjects.size());
				List<GcVideo> gcVideos = new ArrayList<>();
				for(Map.Entry<Integer, List<GcVideo>> map : videoCompleteStatusBySubject.entrySet()){
					//计算单个课程是否完成，课程下的所有视频都完成，则该课程前端显示绿色，黄色、灰色
					//根据视频来设置二级课程的完成情况
					GcSubject vo = new GcSubject();
					vo.setId(map.getKey());
					vo.setSubjectCompleteStatus(TableConstant.SUBJECT_COMPLETE_STATUS0);
					List<GcVideo> tmpVideos = map.getValue();
					if(CollectionUtils.isNotEmpty(tmpVideos)){
						int min = tmpVideos.stream().mapToInt(GcVideo::getCompleteStatus).min().getAsInt();
						int max = tmpVideos.stream().mapToInt(GcVideo::getCompleteStatus).max().getAsInt();
						vo.setSubjectCompleteStatus(TableConstant.SUBJECT_COMPLETE_STATUS1);//
						if(min == max){//相同就设置为一个值
							vo.setSubjectCompleteStatus((short)max);
						}
						gcVideos.addAll(tmpVideos);
					}
					vos.add(vo);
				}
				//返回值里添加排序字段
				for (GcSubject gcSubject : subjects) {
					for (GcSubject gcSub : vos) {
						if (gcSub.getId() == gcSubject.getId()) {
							gcSub.setOrder(gcSubject.getOrder());
						}
					}
				}
				//排序
				if(!vos.stream().filter(e->e.getOrder()==null).findAny().isPresent()) {
					vos = vos.stream().sorted(Comparator.comparing(GcSubject::getOrder)).collect(Collectors.toList());
				}
				totals.setSubjects(vos);

				List<GcVideo> playState1 = gcVideos.stream().filter(e -> TableConstant.VIDEO_COMPLETE_STATUS2 == e.getCompleteStatus()).collect(Collectors.toList());
				Integer sumTasks = gcVideos.stream().filter(e -> null != e.getAnsweredSumNums()).mapToInt(GcVideo::getAnsweredSumNums).sum();
				Integer sumVideos = gcVideos.size();
				Integer answeredTaksNum = gcVideos.stream().filter(e -> null != e.getAnsweredNums()).mapToInt(GcVideo::getAnsweredNums).sum();
				List<GcVideo> videos = gcVideos.stream().filter(e -> null != e.getPlayState() & ("1").equals(e.getPlayState())).collect(Collectors.toList());
				if(CollectionUtils.isNotEmpty(videos) || TableConstant.COMMON_ZERO!=sumTasks) {
					BigDecimal percent = new BigDecimal(answeredTaksNum + videos.size()).divide(new BigDecimal(sumTasks + sumVideos), 2, BigDecimal.ROUND_DOWN);
					totals.setTotalProgressPercent(percent.multiply(BigDecimal.valueOf(100)).intValue());//视频总数
				}else {
					totals.setTotalProgressPercent(0);
				}

				totals.setLessonsTotalProgress(gcVideos.size());
				totals.setLessonsCompleteProgress(playState1.size());


				//这里是课程的播放进度，重新计算
//				totals.setTotalProgressPercent(calcSubjectProgressPercent(vos));//所有视频的播放进度百分比，可能有用
				if (!ifStudent) return totals;

				if(CollectionUtils.isNotEmpty(gcVideos)){
					List<Integer> videoIds = gcVideos.stream().map(GcVideo::getId).collect(Collectors.toList());
					//2、视频播放分钟数和总的时长
					//查询视频总时长
					totals.setVideoTotalProgress(gcVideoService.sumVideoLongByIdUser(videoIds, userId));
					//查询已看分钟数 一个视频多次看取endtime最大的一个，一个视频可能看多次
					if(Objects.nonNull(userId)) {
						totals.setVideoCompleteProgress(gcVideoService.sumPlayVideoLongByIdUser(videoIds, userId));
					}


					//4、已回答问题数和问题总数
					List<GcEvent> eventList = gcEventService.getEventListByVideoIds(videoIds,userId);
					totals.setTaskTotalProgress(eventList.size());//问题总数
					if(Objects.nonNull(userId)) {
						List<GcEvent> eventAnswers = gcEventService.findEventAnswerByVideoIdsUser(videoIds, userId, masterId, envFlag);//查询视频
						if (CollectionUtils.isNotEmpty(eventAnswers)) {
							Set<GcEvent> collect = eventAnswers.stream().filter(e -> StringUtils.isNotBlank(e.getAnswerJson())).collect(Collectors.toSet());
							totals.setTaskCompleteProgress(collect.size());//已完成问题数
						}
					}
				}
			}
		}
		return totals;
	}

	private Map<Integer, List<GcVideo>> subjectVideos(List<GcSubject> subjects) {
		Map<Integer, List<GcVideo>> map = new HashMap<>(subjects.size());
		for(GcSubject subject : subjects){
			map.put(subject.getId(), subject.getGcVideos());
		}
		return map;
	}

	@Override
	public Message getVideosBySubject(Integer subjectId, @RequestBody Map<String,Object> param, HttpServletRequest request,SysSystem system){
		Integer masterId = request.getIntHeader("masterId");
		Map<String, Object> params =  new HashMap<>(1);
		params.put("userId", param.get("userId"));
		params.put("masterId",masterId);
		return service.getVideosBySubIds(subjectId, params, system, request);
	}

	@Override
	public Message videoDetail(HttpServletRequest request,Integer videoId,GcUser user,SysSystem system,Integer envFlag) {
		Integer masterId = request.getIntHeader("masterId");
		GcMaster gcMaster = gcMasterService.getMasterById(masterId);
		//当前视频
		GcVideo video = gcVideoService.getById(videoId);
		GcVideo thisVideo = gcVideoService.findByVideoId(videoId);

		GcVideo videoPlay = gcVideoService.selectVideoPlayByVideo(thisVideo.getId(),user.getId());
		if (null!=videoPlay.getPlayState()){
			thisVideo.setPlayState(videoPlay.getPlayState());
		}

		thisVideo.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(thisVideo));
		GcSubject subject = subjectService.getById(video.getSubId());

		GcUserAccess gcUserAccess = gcUserAccessService.getAccessByUserIdMaster(user.getId(), masterId);
		List<Integer> courseIds = courseAssignmentService.getCourseIdsByContentGroupId(gcUserAccess.getAccessId());

		PageParam pageParam = new PageParam(request);
		Integer pageNum = pageParam.getPageNum();
		Integer pageSize=pageParam.getPageSize();
		if (pageNum > 0 && pageSize > 0) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<GcResource> resourceServiceList = resourceService.getResByVid(videoId);
		//如果没登录，不返回fullfileurl
		thisVideo.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(thisVideo));
		if (Objects.nonNull(user.getId()) && courseIds.contains(subject.getFid())) {
			sysFileService.getResFullUrl(thisVideo.getVideoFile(), request);
			//资源list
			if(CollectionUtils.isNotEmpty(resourceServiceList)) {
				for (GcResource resource : resourceServiceList) {
					sysFileService.getResFullUrl(resource.getResFile(), request);
				}
			}
		}

		//获取一级课程信息
		GcSubject subject0 = subjectService.getById(subject.getFid());
		thisVideo.setFSubjectName(subject0.getName());
		if(null!=subject0.getCertificatesFlag() && TableConstant.COMMON_ONE==subject0.getCertificatesFlag()){
			subject0.setEnableCertificatesFlag(TableConstant.COMMON_ONE);
		}
		int resourceNum=0;
		if(resourceServiceList!=null)resourceNum=resourceServiceList.size();

//    	第几个单元
		int unitNum=1;
		List<GcSubject> l= subjectService.selecUnitNumForVideo(videoId);
		for(int i = 0; i < l.size(); i++) {
			GcSubject s= l.get(i);
			if(s.getId().intValue()==s.getId2().intValue()) {
				unitNum=i+1;
				break;
			}
		}
//    	点赞数
		Integer countLike= gcUserVideoActionService.countLikeForVideo(videoId);
//    	评论数
//
		Integer countComment= gcVideoCommentService.countCommentForVideo(videoId,null,masterId);
//    	note数
		Integer countNote=gcUserNoteService.countNoteForVideo(videoId,user.getId(),masterId);

		//点赞，评价, userVideoAction
		List<GcUserVideoAction> videoActionList = gcUserVideoActionService.getVideoActionsByContentAndUserId(videoId, user.getId());
		int isLiked =0;
		GcUserVideoAction rateVideoAction=null;
		GcUserVideoAction startAction=null;
		Double starValue=null;
		for(GcUserVideoAction va:videoActionList) {
			if(va.getType() ==TableConstant.gcUserVideoAction_type_like1)isLiked=1;
			if(va.getType() ==TableConstant.gcUserVideoAction_type_rate2)rateVideoAction=va;
			if(va.getType() ==TableConstant.gcUserVideoAction_type_star3)starValue=va.getStarValue();
		}

		PageInfo<GcResource> resourcePageInfo = new PageInfo<>(resourceServiceList);

		Message m = new Message().ok();
		m.addData("thisVideo", thisVideo);
		m.addData("resourceServiceList", resourcePageInfo);
		m.addData("resourceNum", resourceNum);
		m.addData("unitNum", unitNum);
		m.addData("countLike", countLike);
		m.addData("countComment", countComment);
		m.addData("countNote", countNote);
		m.addData("isLiked", isLiked);//点赞
		m.addData("starValue", starValue);//打星评价
		m.addData("subject0",subject0);//一级课程信息
		m.addData("返回说明", "thisVideo视频对象，eventList问题列表，totalEventNum总问题数，"
				+ "answeredEventNum已回答问题数，resourceServiceList资源列表，resourceNum总资源数，"
				+ "unitNum第几单元，countLike总点赞数，countComment总评论数，countNote总笔记数，isLike本人是否已点赞1=是，starValue-打星评价");


		List<Integer> subIdList = new ArrayList<>();
		subIdList.add(thisVideo.getSubId0());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("masterId", masterId);
		params.put("fid", thisVideo.getSubId0());
		params.put("userId", user.getId());
		PageInfo<GcSubject> page = new PageInfo<>();
		if(null==user.getId()) {
			page = newUiGcSubjectService.listSubjectByFid(params, system, request, false, subIdList, envFlag);
		}else {
			page = newUiGcSubjectService.listSubjectByFid(params, system, request, true, subIdList, envFlag);
		}


		//通过order给视频排序
		List<GcSubject> orderSubject = page.getList();
		for(GcSubject gcSubject : orderSubject){
			List<GcVideo> VideoList = gcSubject.getGcVideos();
			List<GcVideo> orderVideoList = new ArrayList<>();
			if (Objects.nonNull(gcSubject.getGcVideos())){
				orderVideoList = VideoList.stream().sorted(Comparator.comparing(GcVideo::getOrder)).collect(Collectors.toList());
			}
			gcSubject.setGcVideos(orderVideoList);
		}


		//一级课程总进度
		if(Objects.nonNull(user.getId())) {
			List<Integer> subId = new ArrayList<>();
			GcSubject gcSubject = subjectService.getById(thisVideo.getSubId0());
			thisVideo.setSubjectName(gcSubject.getName());
			subId.add(thisVideo.getSubId0());
			List<GcSubject> subjects = subjectService.getChildSubjectBySubId(thisVideo.getSubId0());
			List<GcVideo> gcVideos = gcVideoService.getVideoIdListBySubId0(subId,user.getId(),masterId,request,envFlag);
			Map<Integer,List<GcVideo>> map = gcVideos.stream().filter(e->null!=e.getSubId()).collect(Collectors.groupingBy(GcVideo::getSubId));
			for(GcSubject gcSubject1 : subjects){
				List<GcVideo> list = map.get(gcSubject1.getId());
				if(null != list && TableConstant.COMMON_ZERO!=list.size()){
					gcSubject1.setGcVideos(list);
				}
			}
			SubjectTotals subjectTotals = calcTotals(subjects,user.getId(),false,masterId,envFlag);
			m.addData("subjectTotal",subjectTotals);

			//二级课程进度
			GcSubject gcSubject1 = subjectService.getById(thisVideo.getSubId());
			Integer suboId = thisVideo.getSubId0();
			List<Integer> suboList = new ArrayList<>();
			subIdList.add(suboId);
			List<GcVideo> gcVideoList = gcVideoService.getVideoListByTopSubIds(suboList);
			List<GcVideo> buildVideoList = gcVideoService.buildVideoInfo(user.getId(), system, gcVideoList, masterId, request, EnvType.GC.getCode());
			Map<Integer, List<GcVideo>> videoMap = buildVideoList.stream().collect(Collectors.groupingBy(GcVideo::getSubId));
			for (Integer key : videoMap.keySet()) {
				List<GcVideo> videoList = videoMap.get(key);
				for (GcSubject gcSubject2 : orderSubject) {
					if (key.equals(gcSubject2.getId())) {
						Integer sumTasks = videoList.stream().filter(e -> null != e.getAnsweredSumNums()).mapToInt(GcVideo::getAnsweredSumNums).sum();
						Integer sumVideos = videoList.size();
						Integer answeredTaksNum = videoList.stream().filter(e -> null != e.getAnsweredNums()).mapToInt(GcVideo::getAnsweredNums).sum();
						List<GcVideo> videos = videoList.stream().filter(e -> null != e.getPlayState() & ("1").equals(e.getPlayState())).collect(Collectors.toList());
						BigDecimal percent = new BigDecimal(answeredTaksNum + videos.size()).divide(new BigDecimal(sumTasks + sumVideos), 2, BigDecimal.ROUND_DOWN);
						gcSubject2.setTotalPercent(percent);
					}
				}
			}
			page.setList(orderSubject);
		}

		//任务list,pt环境中task页面的数据，并返回问题回答的进度百分比
		if(Objects.nonNull(user.getId())) {
			GcSubject gcSubject2 = gcSubjectService.getSubByVid(videoId);
			List<GcVideo> videos = gcVideoService.getVideoListBySubId(gcSubject2.getId());
			List<Integer> videoIdList = videos.stream().map(GcVideo::getId).collect(Collectors.toList());
			List<GcEvent> eventlist = gcEventService.getEventListByVideoIds(videoIdList, user.getId());
			Map<Integer, List<GcEvent>> map1 = eventlist.stream().collect(Collectors.groupingBy(GcEvent::getVideoId));
			for (GcSubject gcSubject3 : orderSubject) {
				if (thisVideo.getSubId().equals(gcSubject3.getId())) {
					List<GcVideo> gcVideoList1 = gcSubject3.getGcVideos();
					if (null != gcVideoList1 && TableConstant.COMMON_ZERO != gcVideoList1.size()) {
						for (GcVideo gcVideo : gcVideoList1) {
							List<GcEvent> gcEvents = map1.get(gcVideo.getId());
							List<GcEvent> gcEventList = new ArrayList<>();
							if(CollectionUtils.isNotEmpty(gcEvents)){
								for(GcEvent gcEvent : gcEvents){
									if(gcEventList.size()<10) {
										gcEventList.add(gcEvent);
									}
								}
							}
							gcVideo.setEventList(gcEventList);
							if (null != gcVideo.getAnsweredNums() && null != gcVideo.getAnsweredSumNums()) {
								BigDecimal videoPercent = BigDecimal.valueOf(gcVideo.getAnsweredNums()).divide(BigDecimal.valueOf(gcVideo.getAnsweredSumNums()), 2, BigDecimal.ROUND_DOWN);
								gcVideo.setTaskProgress(videoPercent);
							} else {
								gcVideo.setTaskProgress(BigDecimal.ZERO);
							}
						}
					}
					m.addData("taskVideoList", JSON.toJSON(gcVideoList1));
				}
			}
		}
		List<GcEvent> eventList = gcEventService.selectGetEventListAndSelfAnswerByVidFull(videoId, user.getId(), masterId);
		int totalEventNum = 0;
		int answeredEventNum = 0;
		if (eventList != null) {
			totalEventNum = eventList.size();
		}
		if (Objects.nonNull(user.getId())) {
			Map<Integer, Object> answerNumMap = gcEventService.videoEventsAnswerNumMap(videoId, user.getId(), masterId);
			GcUserAccess userAccess = new GcUserAccess();
			if (envFlag.equals(EnvType.PT.getCode())){
				GcAccess access = new GcAccess();
				access.setRoleType(AccessRoleType.STUDENT);
				userAccess.setAccess(access);
			}else {
				userAccess = userAccessService.getUserAccessByMasterIdAndUserId(masterId, user.getId());
			}
			if (userAccess.getAccess().getRoleType() == AccessRoleType.STUDENT) {//判断是否是老师用户，如果是老师用户则不会去查询已回答问题数量
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
		m.addData("eventList", eventList);
		m.addData("totalEventNum", totalEventNum);
		m.addData("answeredEventNum", answeredEventNum);
		m.addData("naviList", page);

		SysFileCaption sysFileCaption = sysFileCaptionService.selectMainSysFile(videoId);
		List<SysFileCaption> captionIdList = new ArrayList<>();
		if (null!=sysFileCaption){
			m.addData("captionIdList",captionIdList);
			SysFile  sysFile = sysFileCaptionService.selectSysFileCaption(sysFileCaption.getId());
			if (null == sysFile && null == sysFileCaption.getYmCode()){
				if (sysFileCaption.getYmTaskId()!=null) {
					return m.ok("Subtitle acquisition in progress, please wait").addData("state",0);
				}
				return m.ok("Subtitle generation does not meet the requirements, please check the video information,errorCode:"+sysFileCaption.getYmCode()).addData("state",3);
			}
			if (null!=sysFileCaption.getYmCode()){
				if (!sysFileCaption.getYmCode().equals(TableConstant.COMMON_ZERO)){
					return m.ok("Subtitle generation failed,errorCode:"+sysFileCaption.getYmCode()).addData("state",1);
				}
				if (sysFileCaption.getYmCode() == 0 && sysFileCaption.getYmTaskId() !=null){
					m.ok("Subtitle generation succeeded").addData("state",2);
				}
			}
			captionIdList = sysFileCaptionService.selectSysFileCaptionId(videoId);
			List<Integer> idList = captionIdList.stream().map(SysFileCaption::getCaptionFileId).collect(Collectors.toList());
			List<SysFile> fileList = sysFileService.listByIds(idList);
			Map<Integer,SysFile> sysFileMap = fileList.stream().collect(Collectors.toMap(SysFile::getId, (p) -> p));

			for(int i=0;i<captionIdList.size();i++){
				SysFileCaption fileCaption = captionIdList.get(i);
				if (null!=fileCaption&&null!=fileCaption.getCaptionFileId()){
					String captionUrl = sysFileService.getResFullUrl(sysFileMap.get(fileCaption.getCaptionFileId()),request);
					sysFileMap.get(fileCaption.getCaptionFileId()).setFullFileUrl(captionUrl);
					captionIdList.get(i).setSysFileList(sysFileMap.get(fileCaption.getCaptionFileId()));
				}
			}
		}
		m.addData("captionIdList",captionIdList);
		m.addData("master",gcMaster);
		return m;
	}

	@Override
	public Message eventAnswerList(JSONObject jsonRequest, HttpServletRequest request,GcUser user) {
		Integer masterId = request.getIntHeader("masterId");
		Integer otherUserId = jsonRequest.getInteger("otherUserId");//其他用户的id，可空，空则查询本人回答
		Integer eventId = jsonRequest.getInteger("eventId");
		GcEvent event = eventService.getById(eventId);
		ApiAssert.notNull(eventId, "事件id不可空");
		GcUserAnswer thisUserEventAnswer = new GcUserAnswer();
		//我的或他人的回答，otherUserId空时，查询本人id
		Integer thisUserId=user.getId();
		if(otherUserId!=null) {
			thisUserId=otherUserId;
		}
		thisUserEventAnswer = userAnswerService.getMyEventAnswerByEventId(eventId, thisUserId, masterId);
		if (null!=thisUserEventAnswer&&null!=thisUserEventAnswer.getUserId()){
			GcUser gcUser = userService.getUserInfo(thisUserEventAnswer.getUserId());
			thisUserEventAnswer.setUser(gcUser);
		}


		Message m = new Message().ok();
		m.addData("参数说明", "otherUserId-点他人头像时，传该用户的id，可空，空时返回本人的数据");
		m.addData("thisUserEventAnswer", thisUserEventAnswer);
		//我的或他人的资源list，问题题时返回，选择题不返回
		List<GcUserEventResource> thisUserEventResourceList = gcUserEventResourceService.getEventResListForWorkBook(eventId, thisUserId, null,masterId,request,null);

		//快速修复查看别人的回答，显示了老师回复的内容，后续得改sql
		if(otherUserId!=null && otherUserId!=user.getId()) {
			for(int i=0; i<thisUserEventResourceList.size(); i++) {
				if(1==thisUserEventResourceList.get(i).getIsTeacher()) {
					thisUserEventResourceList.remove(i);
					i--;
				}
			}
		}
		//快速修复查看别人的回答，显示了老师回复的内容，后续得改sql

		m.addData("thisUserEventResourceList", thisUserEventResourceList);

		//评论数量
		Integer commentNum = gcUserNoteCommentService.selectCommentNum(eventId,thisUserId,masterId);
		m.addData("commentNum",commentNum);
		//点赞数量
		Integer fabulousNum = gcUserFabulousService.getEventFabulousNum(eventId,thisUserId,otherUserId);
		m.addData("fabulousNum",fabulousNum);
		//点赞状态
		GcUserFabulous fabulousState = gcUserFabulousService.getUserFabulous(
			gcUserFabulous(user, otherUserId, eventId));
		if (fabulousState == null){
			m.addData("isFabulousState",0);
		}else {
			m.addData("isFabulousState",1);
		}
		m.addData("返回说明", "thisUserEventAnswer-当前用户的回答，thisUserEventResourceList-回答资源list，仅问答题有，isFabulousState-点赞状态,1=已点赞，fabulousNum-点赞数量" +
				"，commentNum-评论数量");
		return m;
	}

	private GcUserFabulous gcUserFabulous(GcUser user, Integer otherUserId, Integer eventId) {
		GcUserFabulous gcUserFabulous = new GcUserFabulous();
		gcUserFabulous.setUserId(user.getId());
		gcUserFabulous.setTargetUserId(otherUserId);
		gcUserFabulous.setEventId(eventId);
		return gcUserFabulous;
	}

	@Override
	public Message createVideoPlayRecordAndNode(@RequestBody GcUserVideoPlay userVideoPlay, HttpServletRequest request,Integer envFlag,GcUser user,Integer masterId,SysSystem system) {
		if (null!=userVideoPlay.getFileId()){
			return saveChannelContentVideoPlay(userVideoPlay, user, masterId, userVideoPlay.getFileId());
		}

		GcVideo thisVideo = gcVideoService.getVideoById(userVideoPlay.getVideoId());

		Optional<PtChannelContent> channelContent = ptChannelContentService.getChannelContent(thisVideo.getId());
		if (channelContent.isPresent()) {
			return saveChannelContentVideoPlay(userVideoPlay, user, masterId, thisVideo.getFileId());
		}

		ApiAssert.notNull(masterId, "masterId is missing");
		Message message = new Message();

		userVideoPlay.setUserId(user.getId());
		userVideoPlay.setMasterId(masterId);
		GcUserVideoPlay videoPlay = userVideoPlayService.saveVideoPlayAndVideoPlaysNode(userVideoPlay);


		List<Integer> subIdList = new ArrayList<>();
		Map<String, Object> params = new HashMap<>();
		params.put("masterId", masterId);
		params.put("pageSize", 1000);
		params.put("fid", thisVideo.getSubId0());
		params.put("userId", user.getId());

		SubjectTotals subjectTotals = null;
		//Progress is calculated after each viewing
		if(TableConstant.COMMON_THREE==envFlag) {
			PageInfo<GcSubject> page = newUiGcSubjectService.listSubjectByFid(params, system, request, true, subIdList, envFlag);
			List<GcSubject> orderSubject = page.getList();
			//Level 1 Course Overall Progress
			if (Objects.nonNull(user.getId())) {
				List<Integer> subId = new ArrayList<>();
				GcSubject gcSubject = subjectService.getById(thisVideo.getSubId0());
				thisVideo.setSubjectName(gcSubject.getName());
				subId.add(thisVideo.getSubId0());
				List<GcSubject> subjects = subjectService.getChildSubjectBySubId(thisVideo.getSubId0());
				List<GcVideo> gcVideos = gcVideoService.getVideoIdListBySubId0(subId, user.getId(), masterId, request, envFlag);
				Map<Integer, List<GcVideo>> map = gcVideos.stream().filter(e -> null != e.getSubId()).collect(Collectors.groupingBy(GcVideo::getSubId));
				for (GcSubject gcSubject1 : subjects) {
					List<GcVideo> list = map.get(gcSubject1.getId());
					if (null != list && !list.isEmpty()) {
						gcSubject1.setGcVideos(list);
					}
				}
				subjectTotals = calcTotals(subjects, user.getId(), false, masterId,envFlag);
				message.addData("subjectTotal", subjectTotals);

				//Level 2 Course Progress
				Integer suboId = thisVideo.getSubId0();
				List<Integer> suboList = new ArrayList<>();
				subIdList.add(suboId);
				List<GcVideo> gcVideoList = gcVideoService.getVideoListByTopSubIds(suboList);
				List<GcVideo> buildVideoList = gcVideoService.buildVideoInfo(user.getId(), system, gcVideoList, masterId, request, envFlag);
				Map<Integer, List<GcVideo>> videoMap = buildVideoList.stream().collect(Collectors.groupingBy(GcVideo::getSubId));
				for (Integer key : videoMap.keySet()) {
					List<GcVideo> videoList = videoMap.get(key);
					for (GcSubject gcSubject2 : orderSubject) {
						if (key.equals(gcSubject2.getId())) {
							Integer sumTasks = videoList.stream().filter(e -> null != e.getAnsweredSumNums()).mapToInt(GcVideo::getAnsweredSumNums).sum();
							Integer sumVideos = videoList.size();
							Integer answeredTaksNum = videoList.stream().filter(e -> null != e.getAnsweredNums()).mapToInt(GcVideo::getAnsweredNums).sum();
							List<GcVideo> videos = videoList.stream().filter(e -> null != e.getPlayState() & ("1").equals(e.getPlayState())).collect(Collectors.toList());
							BigDecimal percent = new BigDecimal(answeredTaksNum + videos.size()).divide(new BigDecimal(sumTasks + sumVideos), 2, BigDecimal.ROUND_DOWN);
							gcSubject2.setTotalPercent(percent);
						}
					}
				}
				Optional<GcSubject> gcSubject2 = orderSubject.stream().filter(e->e.getId().equals(thisVideo.getSubId())).findFirst();
				message.ok().addData("subject",gcSubject2);
				page.setList(orderSubject);
			}
		}

		//Add course completion progress data
		GcSubjectComplete complete = subjectCompleteService.getSubjectCompleteInfo(masterId,user.getId(),thisVideo.getSubId0());
		if (null==complete){
			complete = new GcSubjectComplete();
			complete.setCreateTime(new Date());
		}
		if (null!=subjectTotals&&subjectTotals.getTotalProgressPercent().equals(TableConstant.SUBJECT_COMPLETE_PERCENT0)){
			complete.setInProgress(TableConstant.SUBJECT_COMPLETE_PERCENT0);
			complete.setSubjectState(TableConstant.COMMON_ONE);
		}else if (null!=subjectTotals&&!subjectTotals.getTotalProgressPercent().equals(TableConstant.SUBJECT_COMPLETE_PERCENT0)&&!subjectTotals.getTotalProgressPercent().equals(TableConstant.COMMON_ZERO)){
			complete.setInProgress(subjectTotals.getTotalProgressPercent());
			complete.setSubjectState(TableConstant.COMMON_TWO);
		}else if (null!=subjectTotals&&subjectTotals.getTotalProgressPercent().equals(TableConstant.COMMON_ZERO)){
			complete.setInProgress(subjectTotals.getTotalProgressPercent());
			complete.setSubjectState(TableConstant.COMMON_ZERO);
		}
		complete.setMasterId(masterId);
		complete.setSubjectId(thisVideo.getSubId0());
		complete.setUpdateTime(new Date());
		complete.setUserId(user.getId());
		subjectCompleteService.saveOrUpdate(complete);

		return  message.ok("Get Success")
				.addData("videoPlayNode", videoPlay.getVideoPlaysNode())
				.addData("videoPlay", videoPlay);
	}

	private Message saveChannelContentVideoPlay(GcUserVideoPlay userVideoPlay, GcUser user, Integer masterId,
												Integer thisVideo) {
		userVideoPlay.setUserId(user.getId());
		userVideoPlay.setMasterId(masterId);
		SysFile file = sysFileService.getById(thisVideo);
		GcUserVideoPlay videoPlay = userVideoPlayService.saveVideoPlayAndVideoPlaysNode(userVideoPlay);

		return new Message().ok("Get Success")
			.addData("videoPlayNode", videoPlay.getVideoPlaysNode())
			.addData("videoPlay", videoPlay)
			.addData("file", file);
	}

	@Override
	public Message answerQuestion(@RequestBody JSONObject jsonRequest, HttpServletRequest request,Integer masterId,GcUser user,Integer envFlag,SysSystem system) {
		Message message = new Message();
		ApiAssert.assertId(masterId, "缺少空间id");
		Integer eventId = jsonRequest.getInteger("eventId");
		GcEvent event = eventService.getById(eventId);
		String answerJsonString = jsonRequest.get("answerJson").toString();
		GcUserAnswer answer = new GcUserAnswer();
		GcUserAnswer history = new GcUserAnswer();
		history = userAnswerService.getMyEventAnswerByEventId(eventId, user.getId(), masterId);
		if (history != null)
			answer.setId(history.getId());
		answer.setMasterId(masterId);
		answer.setEventId(eventId);
		answer.setUserId(user.getId());
		answer.setAnswerJson(JSONObject.parseObject(answerJsonString));


		if (!userAnswerService.saveUserAnswer(answer)) {
			throw new SystemException("没有成功！");
		}
		SubjectTotals subjectTotals = null;
		Integer sub0Id = null;
		if(TableConstant.COMMON_THREE==envFlag && Objects.isNull(history)) {
			GcVideo gcVideo = gcVideoService.getById(event.getVideoId());
			GcSubject gcSubject = gcSubjectService.getById(gcVideo.getSubId());
			sub0Id = gcSubject.getFid();
			List<Integer> subIdList = new ArrayList<>();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("masterId", masterId);
			params.put("pageSize", 1000);
			params.put("fid", gcSubject.getFid());
			params.put("userId", user.getId());
			PageInfo<GcSubject> page = newUiGcSubjectService.listSubjectByFid(params, system, request, true, subIdList, envFlag);
			List<GcSubject> orderSubject = page.getList();
			//一级课程总进度
			if (Objects.nonNull(user.getId())) {
				List<Integer> subId = new ArrayList<>();
				subId.add(gcSubject.getFid());
				List<GcSubject> subjects = subjectService.getChildSubjectBySubId(gcSubject.getFid());
				List<GcVideo> gcVideos = gcVideoService.getVideoIdListBySubId0(subId, user.getId(), masterId, request, envFlag);
				Map<Integer, List<GcVideo>> map = gcVideos.stream().filter(e -> null != e.getSubId()).collect(Collectors.groupingBy(GcVideo::getSubId));
				for (GcSubject gcSubject1 : subjects) {
					List<GcVideo> list = map.get(gcSubject1.getId());
					if (null != list && TableConstant.COMMON_ZERO != list.size()) {
						gcSubject1.setGcVideos(list);
					}
				}
				subjectTotals = calcTotals(subjects, user.getId(), false, masterId,envFlag);
				message.addData("subjectTotal", subjectTotals);

				//二级课程进度
				GcSubject gcSubject1 = subjectService.getById(gcVideo.getSubId());
				Integer suboId = gcSubject.getFid();
				List<Integer> suboList = new ArrayList<>();
				subIdList.add(suboId);
				List<GcVideo> gcVideoList = gcVideoService.getVideoListByTopSubIds(suboList);
				List<GcVideo> buildVideoList = gcVideoService.buildVideoInfo(user.getId(), system, gcVideoList, masterId, request, EnvType.GC.getCode());
				Map<Integer, List<GcVideo>> videoMap = buildVideoList.stream().collect(Collectors.groupingBy(GcVideo::getSubId));
				for (Integer key : videoMap.keySet()) {
					List<GcVideo> videoList = videoMap.get(key);
					for (GcSubject gcSubject2 : orderSubject) {
						if (key.equals(gcSubject2.getId())) {
							Integer sumTasks = videoList.stream().filter(e -> null != e.getAnsweredSumNums()).mapToInt(GcVideo::getAnsweredSumNums).sum();
							Integer sumVideos = videoList.size();
							Integer answeredTaksNum = videoList.stream().filter(e -> null != e.getAnsweredNums()).mapToInt(GcVideo::getAnsweredNums).sum();
							List<GcVideo> videos = videoList.stream().filter(e -> null != e.getPlayState() & ("1").equals(e.getPlayState())).collect(Collectors.toList());
							BigDecimal percent = new BigDecimal(answeredTaksNum + videos.size()).divide(new BigDecimal(sumTasks + sumVideos), 2, BigDecimal.ROUND_DOWN);
							gcSubject2.setTotalPercent(percent);
						}
					}
				}
				Optional<GcSubject> gcSubject2 = orderSubject.stream().filter(e->e.getId().equals(gcVideo.getSubId())).findFirst();
				message.ok().addData("subject",gcSubject2);
				page.setList(orderSubject);
			}
		}

		//加入课程完成进度数据
		GcSubjectComplete complete = subjectCompleteService.getSubjectCompleteInfo(masterId,user.getId(),sub0Id);
		if (null==complete){
			complete = new GcSubjectComplete();
			complete.setCreateTime(new Date());
		}
		if (null!=subjectTotals&&subjectTotals.getTotalProgressPercent().equals(TableConstant.SUBJECT_COMPLETE_PERCENT0)){
			complete.setInProgress(TableConstant.SUBJECT_COMPLETE_PERCENT0);
			complete.setSubjectState(TableConstant.COMMON_ONE);
		}else if (null!=subjectTotals&&!subjectTotals.getTotalProgressPercent().equals(TableConstant.SUBJECT_COMPLETE_PERCENT0)&&!subjectTotals.getTotalProgressPercent().equals(TableConstant.COMMON_ZERO)){
			complete.setInProgress(subjectTotals.getTotalProgressPercent());
			complete.setSubjectState(TableConstant.COMMON_TWO);
		}else if (null!=subjectTotals&&subjectTotals.getTotalProgressPercent().equals(TableConstant.COMMON_ZERO)){
			complete.setInProgress(subjectTotals.getTotalProgressPercent());
			complete.setSubjectState(TableConstant.COMMON_ZERO);
		}
		complete.setMasterId(masterId);
		complete.setSubjectId(sub0Id);
		complete.setUpdateTime(new Date());
		complete.setUserId(user.getId());
		subjectCompleteService.saveOrUpdate(complete);



		if(envFlag!=TableConstant.COMMON_THREE){
			//群发老师通知
			List<GcMasterMessage> masterMessageList = new ArrayList<>();
			List<Integer> sendIds = userService.getTalkerIds(user.getId(), masterId);
			if (sendIds.size() > 0) {
				for (Integer sendId : sendIds) {
					GcMasterMessage masterMessage = new GcMasterMessage();
					masterMessage.setEventType(MessageEventType.QUESTION_5);
					masterMessage.setMasterId(masterId);
					masterMessage.setUserId(user.getId());
					masterMessage.setTargetUserId(sendId);
					masterMessage.setUserAnswerId(answer.getId());
					masterMessageList.add(masterMessage);
					masterMessageService.deleteAnswerMessage(masterMessage);
				}
				masterMessageService.saveBatchMasterMessage(masterMessageList);
			}
		}
		//判断学生选择题是否回答正确
		List<String> rightAnswer = new ArrayList<>();
		if(TableConstant.COMMON_ONE==event.getEventType()){
			String ext = event.getExt();
			JSONArray jsonArray = JSONArray.parseArray(ext);
			for(Object obj : jsonArray){
				JSONObject jsonObject = (JSONObject) JSONObject.toJSON(obj);
				if(Boolean.parseBoolean(jsonObject.get("checked").toString())){
					rightAnswer.add(jsonObject.get("value").toString());
				}
			}
			JSONArray jsonArray1 = JSONArray.parseArray(jsonRequest.getString("answerJson"));
			if(Objects.nonNull(event.getAnswerMessageFlag()) && event.getAnswerMessageFlag().equals(TableConstant.COMMON_ONE)) {
				if (JSONObject.parseArray(jsonArray1.toJSONString(), String.class).containsAll(rightAnswer) && rightAnswer.containsAll(JSONObject.parseArray(jsonArray1.toJSONString(), String.class))) {
					message.ok().addData("ifRightAnswer", TableConstant.COMMON_ONE);
				}else {
					message.ok().addData("ifRightAnswer", TableConstant.COMMON_ZERO);
				}
			}
		}
		return message.ok("回答成功！");

	}

	@ApiOperation(value = "删除分类", httpMethod = "DELETE")
	@DeleteMapping("/delSub/{id}/{sub0Id}")
	public Message deleteSub(@PathVariable("id") Integer subId, @PathVariable(value = "sub0Id",required = false)Integer sub0Id, HttpServletRequest request,GcMaster master,Integer envFlag) {
		Integer masterId = request.getIntHeader("masterId");
		if (Objects.isNull(master)&&Objects.nonNull(masterId)){
			master = new GcMaster();
			master.setId(masterId);
		}
		System.out.println(master.getId());
		if(EnvType.GC.getCode()==envFlag) {
			if (subService.deleteSub(subId, master.getId())) return new Message().ok();
		}else if(EnvType.PT.getCode()==envFlag){
			if(Objects.nonNull(sub0Id)){
				GcSubject gcSubject0 = subService.getById(sub0Id);
				if(!gcSubject0.getType().equals(TableConstant.COMMON_ZERO))return new Message().error("Can't move topic to another topic");
				GcSubject gcSubject = subService.getById(subId);
				gcSubject.setFid(sub0Id);
				gcSubject.setSubId(sub0Id);
				subService.saveOrUpdate(gcSubject);
				return new Message().ok();
			}else {
				subService.deleteSub(subId, master.getId());
				return new Message().ok();
			}
		}
		return new Message().error("删除失败");
	}

	public PageInfo<SysFile> getSysFile(JSONObject jsonParams, List<Integer> typeIndexIds,HttpServletRequest request,GcMaster master,Integer uploadUid) {
		Integer pageSize=jsonParams.getInteger("pageSize");
		Integer pageNum=jsonParams.getInteger("pageNum");
		String searchTag=jsonParams.getString("searchTag");
		String searchString=jsonParams.getString("searchString");
		Integer fileId=jsonParams.getInteger("fileId");
		List<SysFile> list;
		if (null!=master){
			list=sysFileService.getFiles(null,master.getId(), typeIndexIds, searchTag, pageNum, pageSize,searchString,fileId,uploadUid);
		}else {
			list=sysFileService.getFiles(TableConstant.screenrock_folder,null, typeIndexIds, searchTag, pageNum, pageSize,searchString,fileId,uploadUid);
		}
		for(SysFile sysFile:list){
			String snapShotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
			sysFile.setSnapshotUrl(snapShotUrl);
			String fullFileUrl = sysFileService.getResFullUrl(sysFile,request);
			sysFile.setFullFileUrl(fullFileUrl);
		}
		PageInfo<SysFile> page=new PageInfo<SysFile>(list);
		return page;
	}

	@Async
	@Override
	public void saveInProgress(Integer subject,Integer masterId,HttpServletRequest request){

		List<GcSubjectComplete> completes = completeService.selectBySubjectId(masterId,subject);

		List<Integer> userIds = completes.stream().map(GcSubjectComplete::getUserId).collect(Collectors.toList());

		List<SubjectTotals> subjectTotalsList = new ArrayList<>();

		//一级课程总进度
		if (CollectionUtils.isNotEmpty(userIds)) {
			List<Integer> subId = new ArrayList<>();
			subId.add(subject);
			List<GcSubject> subjects = subjectService.getChildSubjectBySubId(subject);
			List<GcVideo> gcVideos = gcVideoService.getVideoListByUserIdAndSubject(userIds,subject,masterId,request);

			Map<Integer,List<GcVideo>> map = gcVideos.stream().filter(e->null!=e.getSubId()).collect(Collectors.groupingBy(GcVideo::getUserId));

			for (Integer userId : userIds) {
				List<GcVideo> list = map.get(userId);
				for(GcSubject gcSubject1 : subjects){
					if(null != list && TableConstant.COMMON_ZERO!=list.size()){
						gcSubject1.setGcVideos(list);
					}
				}
				SubjectTotals  subjectTotals = new SubjectTotals();
				//计算进度
				subjectTotals = calcTotals(subjects,userId,false,masterId,EnvType.PT.getCode());
				subjectTotalsList.add(subjectTotals);
			}
		}
		Map<Integer,GcSubjectComplete> completeMap = completes.stream().collect(Collectors.toMap(GcSubjectComplete::getUserId, (p) -> p));
		for (SubjectTotals subjectTotals : subjectTotalsList) {
			GcSubjectComplete complete = completeMap.get(subjectTotals.getUserId());
			if (null!=subjectTotals&&subjectTotals.getTotalProgressPercent().equals(TableConstant.SUBJECT_COMPLETE_PERCENT0)){
				complete.setInProgress(TableConstant.SUBJECT_COMPLETE_PERCENT0);
				complete.setSubjectState(TableConstant.COMMON_ONE);
			}else if (null!=subjectTotals&&!subjectTotals.getTotalProgressPercent().equals(TableConstant.SUBJECT_COMPLETE_PERCENT0)&&!subjectTotals.getTotalProgressPercent().equals(TableConstant.COMMON_ZERO)){
				complete.setInProgress(subjectTotals.getTotalProgressPercent());
				complete.setSubjectState(TableConstant.COMMON_TWO);
			}else if (null!=subjectTotals&&subjectTotals.getTotalProgressPercent().equals(TableConstant.COMMON_ZERO)){
				complete.setInProgress(subjectTotals.getTotalProgressPercent());
				complete.setSubjectState(TableConstant.COMMON_ZERO);
			}
			complete.setUpdateTime(new Date());
			completeService.saveOrUpdate(complete);
		}
	}



	public Message deleteVideoPt(Integer vid,Integer envFlag,Integer userId,Integer masterId,HttpServletRequest request) {
		if(envFlag==EnvType.GC.getCode()) {
			if (videoService.deleteVideo(vid)){
				return new Message().ok();
			}
		}else if(envFlag==EnvType.PT.getCode()){
			GcVideo video = videoService.getById(vid);
			GcSubject subject = new GcSubject();
			if (null!=video){
				subject = subjectService.getById(video.getSubId());
			}
			videoService.deleteVideo(vid);
			if (null!=subject&&null!=subject.getFid()){
				saveInProgress(subject.getFid(),masterId,request);
			}
			return new Message().ok();
		}
		return new Message().error();
	}

	public Message deleteVideo(Integer vid,Integer envFlag,Integer userId,Integer masterId) {
		if(envFlag==EnvType.GC.getCode()) {
			GcVideo video = videoService.getById(vid);
			GcSubject subject = new GcSubject();
			if (null!=video){
				subject = subjectService.getById(vid);
			}
			videoService.deleteVideo(vid);
            if (videoService.deleteVideo(vid)){
				return new Message().ok();
			}
		}else if(envFlag==EnvType.PT.getCode()){
			videoService.deleteVideo(vid);
			return new Message().ok();
		}
		return new Message().error();
	}

	public Message deleteSub(Integer subId, Integer envFlag, GcMaster master, Integer userId) {
		if (envFlag == EnvType.GC.getCode()) {
			courseAssignmentService.removeByMasterAndCourseId(master.getId(), subId);

			if (subService.deleteSub(subId, master.getId())) {
				return new Message().ok();
			}
			return new Message().error(I18NUtil.get("guidecore.resource.deleteSucc"));
		}

		if (envFlag == EnvType.PT.getCode()) {
			subService.deleteSub(subId, master.getId());
			courseAssignmentService.removeByMasterAndCourseId(master.getId(), subId);

			return new Message().ok();
		}
		return new Message().error("删除失败");
	}

}
