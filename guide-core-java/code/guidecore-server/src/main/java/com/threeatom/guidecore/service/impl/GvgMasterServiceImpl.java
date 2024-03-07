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
import com.threeatom.config.ChannelConfiguration;
import com.threeatom.guidecore.constant.AccessRoleType;
import com.threeatom.guidecore.constant.EnvType;
import com.threeatom.guidecore.constant.MessageEventType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.user.vo.PageParam;
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
import com.threeatom.guidecore.entity.GcUserAccessPermission;
import com.threeatom.guidecore.entity.GcUserAnswer;
import com.threeatom.guidecore.entity.GcUserEventResource;
import com.threeatom.guidecore.entity.GcUserFabulous;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import com.threeatom.guidecore.entity.GcUserVideoPlay;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.entity.PtTags;
import com.threeatom.guidecore.entity.SubjectTotals;
import com.threeatom.guidecore.entity.SysMenu;
import com.threeatom.guidecore.mapper.GcMasterMapper;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcEventService;
import com.threeatom.guidecore.service.GcMasterHomeInfoService;
import com.threeatom.guidecore.service.GcMasterMessageService;
import com.threeatom.guidecore.service.GcMasterService;
import com.threeatom.guidecore.service.GcResourceService;
import com.threeatom.guidecore.service.GcSubjectCompleteService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserAccessPermissionService;
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
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.service.PtTagsService;
import com.threeatom.guidecore.service.SysMenuService;
import com.threeatom.guidecore.util.I18NUtil;
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
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 主站点实例 服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-11
 */
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
	private GcUserAccessPermissionService gcUserAccessPermissionService;

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
	private ChannelConfiguration channelConfiguration;

	@Autowired
	private GcUserSaveFolderService gcUserSaveFolderService;

	@Autowired
	private GcSubjectCompleteService subjectCompleteService;


	private static final String CACHE_TAG = "GcMaster";
	private static final String KEY_TAG_ENTITY = "'entity:uid-'+";
	@Autowired
	private GcSubjectService gcSubjectService;

	@Autowired
	private GcSubjectService subService;

	@Autowired
	private GcVideoService videoService;

	@Autowired
	private SysFileService iSysFileService;

	@Autowired
	private PtTagsService ptTagsService;

	@Autowired
	private GcUserVideoPlayService gcUserVideoPlayService;

	@Autowired
	private GcUserAnswerService gcUserAnswerService;

	@Autowired
	private GcSubjectCompleteService completeService;

	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private GcContentGroupCourseAssignmentService contentGroupCourseAssignmentService;

//	@Value("${channel.id:0}")
//	private List<Integer> channelIdList;

	public Message newPtIndexHome(JSONObject requestParams, HttpServletRequest request, SysSystem system,GcUser user) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Message message  = new Message();
		String portalId = requestParams.getString("portalId");
		GcMaster gcMaster = gcMasterService.getMaster(portalId);
		if (null!=user){
		ExecutorService executor = Executors.newFixedThreadPool(3);//做3个线程
		executor.submit(() -> {
			List<GcSubject> subjectList = subjectService.selectSubjectByNewIndexHome(gcMaster.getId(),user.getId(),new PageParam(request));
			PageParam pageParam = new PageParam(request);
			if (subjectList.size()>=pageParam.getPageSize()){
				subjectList =subjectList.subList(TableConstant.COMMON_ZERO,pageParam.getPageSize());
			}
			List<GcSubject> myMaySubject = subjectService.selectSubjectMay(gcMaster.getId(),user.getId(),new PageParam(request));

			//两个课程都要进度等详细信息,放一起查询,避免两次查
			List<GcSubject> subjectInfo = new ArrayList<>();
			subjectInfo.addAll(subjectList);
			subjectInfo.addAll(myMaySubject);

			//课程进度评分等查询
			List<Integer> allLevel0subIds = subjectInfo.stream().map(GcSubject::getId).collect(Collectors.toList());
			List<Integer> imageSubIds = subjectInfo.stream().map(GcSubject::getSubImgId).collect(Collectors.toList());
			List<Integer> videoIdlist = gcVideoService.getVideoIdListBySubId(allLevel0subIds);
			List<GcVideo> videoList = gcVideoService.getVideoLongListByVideoId(videoIdlist);

			videoList = gcVideoService.buildVideoInfo(user.getId(),null,videoList,gcMaster.getId(),request,EnvType.PT.getCode());
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

			//myMaySubject的循环 ,因为分页插件,所以循环还是两个
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
						Integer totalSeconds = gcVideos.stream().filter(a -> a.getVideoLong()!=null).mapToInt(GcVideo::getVideoLong).sum();
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
					if (null!=user) {
						if (null!=groupBySubId.get(li.getId())){
							Map<Integer, List<GcVideo>> sub1Map = groupBySubId.get(li.getId()).stream().collect(Collectors.groupingBy(GcVideo::getSubId));
							List<GcSubject> twoSubject = newUiGcSubjectService.buildSubject1(sub1Map);
							SubjectTotals subjectTotals = calcTotals(twoSubject, user.getId(), true, gcMaster.getId(), EnvType.PT.getCode());
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

					//
					if(groupBySubId.get(li.getId())!=null){
						List<GcVideo> gcVideos = groupBySubId.get(li.getId());
						Integer totalSeconds = gcVideos.stream().filter(a -> a.getVideoLong()!=null).mapToInt(GcVideo::getVideoLong).sum();
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
					if (null!=user) {
						if (null!=groupBySubId.get(li.getId())){
							Map<Integer, List<GcVideo>> sub1Map = groupBySubId.get(li.getId()).stream().collect(Collectors.groupingBy(GcVideo::getSubId));
							List<GcSubject> twoSubject = newUiGcSubjectService.buildSubject1(sub1Map);
							SubjectTotals subjectTotals = calcTotals(twoSubject, user.getId(), true, gcMaster.getId(), EnvType.PT.getCode());
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
			//String json1 = JSON.toJSONString(subjectList,SerializerFeature.DisableCircularReferenceDetect);
			//subjectList =JSONArray.parseArray(json1,GcSubject.class);
			PageInfo<GcSubject> subjectPageInfo = new PageInfo<>(subjectList);
			message.ok().addData("subjectList", subjectPageInfo);
			//Discover courses-课程
			PageInfo<GcSubject> myMaySubjectPage = new PageInfo<>(myMaySubject);
			message.addData("discoverCourses",myMaySubjectPage);

			//已订阅的channel视频,自己上传的不显示
			PageInfo<PtChannel> channelPageInfo = new PageInfo<>();
			List<PtChannel> channelPage = new ArrayList<>();
			channelPage = ptChannelService.searchChannelsBySysFileNew(user.getId(),request,gcMaster.getId());
			channelPageInfo = new PageInfo<>(channelPage);
			message.addData("channelVideoPage",channelPageInfo);

		});

			executor.submit(() -> {
				//My subscriptions-channel 我已订阅的(不含我创建的)；订阅时间排序
				List<PtChannel> ptChannelList = ptChannelService.newIndexHomeChannels(user.getId(),request,gcMaster.getId());
				PageInfo<PtChannel> pageInfo = new PageInfo<>(ptChannelList);
				message.addData("subscriptionsChannel",pageInfo);
				//playlist
				List<GcUserSaveFolder> recommentPlayList = gcUserSaveFolderService.selectFolderInMaster(gcMaster.getId());
				List<Integer> recommenFolderIds = recommentPlayList.stream().map(GcUserSaveFolder::getId).collect(Collectors.toList());
				List<GcUserSaveFolder> recommenFolderList = gcUserSaveFolderService.getPtNewHomePlayList(user.getId(), gcMaster.getId(),recommenFolderIds,request);

				List<Integer> firstVideos = recommenFolderList.stream().filter(e->null!=e.getFirstVideoFileId()).map(GcUserSaveFolder::getFirstVideoFileId).collect(Collectors.toList());
				List<SysFile> fileList = sysFileService.listByIds(firstVideos);
				fileList.forEach(i->{
					i.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(i));
				});
				Map<Integer,SysFile> firstVideoMap = fileList.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));

				for(GcUserSaveFolder gcUserSaveFolder : recommenFolderList){
					//缩略图
					if(Objects.nonNull(gcUserSaveFolder.getFirstVideoFileId())&&null!=firstVideoMap.get(gcUserSaveFolder.getFirstVideoFileId())) {
						//SysFile sysFile = sysFileService.getById(gcUserSaveFolder.getFirstVideoFileId());
						//String fullfileurl = sysFileService.getVideoSnapshotUrl(sysFile);
						SysFile sysFile = firstVideoMap.get(gcUserSaveFolder.getFirstVideoFileId());
						if (null!=gcUserSaveFolder.getSaveContentList().get(TableConstant.COMMON_ZERO)){
							gcUserSaveFolder.getSaveContentList().get(TableConstant.COMMON_ZERO).setVideoFile(sysFile);
							gcUserSaveFolder.getSaveContentList().get(TableConstant.COMMON_ZERO).getVideoFile().setSnapshotUrl(sysFile.getSnapshotUrl());
						}
						gcUserSaveFolder.setSnapshotUrl(sysFile.getSnapshotUrl());
					}
				}

				String playlistJson = JSON.toJSONString(recommenFolderList,SerializerFeature.DisableCircularReferenceDetect);
				recommenFolderList =JSONArray.parseArray(playlistJson,GcUserSaveFolder.class);
				PageInfo<GcUserSaveFolder> recommenFolderListPageInfo = new PageInfo<>(recommenFolderList);
				message.addData("playList",recommenFolderListPageInfo);
			});

		executor.submit(()->{
			//Trending Now-channel视频： 最多赞+最多观看的channel视频 （含自己的）
			List<PtChannel> nowChannel = ptChannelService.getPtChannelVideoNow(user.getId(),request,gcMaster.getId());
			PageInfo<PtChannel> nowChannelPage = new PageInfo<>(nowChannel);
			message.addData("nowChannel",nowChannelPage);
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
	public Message portalInfosUnlogin(JSONObject requestParams,HttpServletRequest request,SysSystem system,GcUser user,Integer envFlag) {
		if (null!=requestParams.get("state")){
			request.setAttribute("state",requestParams.get("state").toString());
		}
		if (null!=requestParams.get("user")){
			if (null!=user){
				request.setAttribute("createUser",user.getId());
			}
		}
		if (null!=requestParams.get("type")&&null!=user){
			request.setAttribute("type",requestParams.get("type"));
		}
		if (null!=requestParams.get("subjectName")){
			request.setAttribute("subjectName",requestParams.get("subjectName"));
		}

		if(null!=user){
			request.setAttribute("userId",user.getId());
		}
		if (envFlag.equals(EnvType.PT.getCode())){
			request.setAttribute("isPt",TableConstant.COMMON_ZERO);
		}
		Message message = new Message();
//		List<Integer> channelIds = channelIdList;
		String portalId = requestParams.getString("portalId");
		GcMaster gcMaster = gcMasterService.getMaster(portalId);
		if(Objects.nonNull(gcMaster.getFaviconLogoFileId())){
			SysFile sysFile = sysFileService.getById(gcMaster.getFaviconLogoFileId());
			String faviconUrl = sysFileService.getResFullUrl(sysFile,request);
			gcMaster.setFaviconFullFileUrl(faviconUrl);
		}
		//查询此门户下是否有免费code
		GcAccess gcAccess = gcAccessService.selectFreeCodeByMaster(gcMaster.getId());
		if(Objects.nonNull(gcAccess)){
			gcMaster.setFreeAccessCode(gcAccess);
		}
		if(Objects.nonNull(gcMaster.getLogoId())){
			SysFile sysFile = sysFileService.getById(gcMaster.getLogoId());
			String fullFileUrl = sysFileService.getResFullUrl(sysFile,request);
			gcMaster.setLogoFullUrl(fullFileUrl);
		}
		if(gcMaster==null) {
			return message.error(I18NUtil.get("guidecore.getForHome.portalIdNotExist"));
		}
		SysFile logofile = sysFileService.selectByLogoId(gcMaster.getLogoId());
		String logoFullUrl = sysFileService.getResFullUrl(logofile,request);
		if(null != gcMaster.getProfilePhotoId()){
			SysFile profileFile = sysFileService.getById(gcMaster.getProfilePhotoId());
			String profileUrl = sysFileService.getResFullUrl(profileFile,request);
			gcMaster.setProfilePhotoFullFileUrl(profileUrl);
		}
		gcMaster.setLogoFullUrl(logoFullUrl);

			//查询用户是否选择过code，如果选择过直接进入首页，没选择过进入code选择列表
			//taglist
			List<String> subWithTagList = new ArrayList<>();
			String token = request.getHeader("Authorization");
			if (null != token && !"".equals(token) && !"undefined".equals(token)){
				if (Objects.nonNull(user)){
					subWithTagList = newUiGcSubjectService.selectAllTag(gcMaster.getId(),user.getId());
				}
			}else {
				subWithTagList = newUiGcSubjectService.selectAllTag(gcMaster.getId(),null);
			}

			List<GcMasterHomeInfo> infoList = iGcMasterHomeInfoService.getGcMasterHomeInfoList(gcMaster.getId(),TableConstant.gcMasterHomeInfo_name_page,null,request);
			if (null!=user&&envFlag.equals(EnvType.PT.getCode())){
				QueryWrapper<PtTags> queryWrapper = new QueryWrapper<>();
				queryWrapper.in("master_id",gcMaster.getId());
				queryWrapper.in("type",TableConstant.COMMON_ONE);
				subWithTagList = ptTagsService.list(queryWrapper).stream().map(PtTags::getTagText).collect(Collectors.toList());
				//去重
				subWithTagList = subWithTagList.stream().distinct().collect(Collectors.toList());
			}

			List<SysMenu> menuList = sysMenuService.getLevel3List(null);
			message.ok().addData("allTags",subWithTagList);
			message.ok().addData("homeInfo",infoList);
			message.ok().addData("homeInfoIndex",menuList);
			message.ok().addData("master",gcMaster);
		return message.ok();
	}

	@Override
	public Message search(Map<String, Object> params, HttpServletRequest request,GcUser user,SysSystem system,Integer envFlag){
		try {
			GcUser gcUser = null;
			Integer userId = null;
			String token = request.getHeader("Authorization");
			if (null != token && !"".equals(token) && !"undefined".equals(token)){
				params.put("userId", user.getId());
			}
			Integer masterId = request.getIntHeader("masterId");
			Object returnTypeObj = params.get("returnType");
			Message msg = new Message().ok();
			//taglist
			List<String> subWithTagList = newUiGcSubjectService.selectAllTag(masterId,userId);
			msg.addData("allTagList",subWithTagList);
			if(returnTypeObj == null || TableConstant.VIDEO_SEARCH_RETURN_TYPE1.equals(returnTypeObj.toString())) {
				//加载视频
				PageInfo<GcVideo> page = service.page(params, system, request);
				List<GcVideo> gcVideoList = page.getList();
				List<Integer> videoIdList = gcVideoList.stream().map(GcVideo::getId).collect(Collectors.toList());
				msg.addData("videoIdList",videoIdList);
				return msg.addData("videoPage", service.page(params, system, request));
			}
			if(TableConstant.VIDEO_SEARCH_RETURN_TYPE2.equals(returnTypeObj.toString()) && Objects.nonNull(params.get("subjectName")) ) {
				params.put("videoInCourseName",params.get("subjectName"));
				//加载课程
//                if(null==params.get("RelatedVideoIds")){
//				Map<String,Object> videoParams = new HashMap<>();
//				videoParams.put("pageNum",params.get("pageNum"));
//				videoParams.put("pageSize",params.get("pageSize"));
//				videoParams.put("videoName",params.get("subjectName"));
//				videoParams.put("returnType",TableConstant.COMMON_ONE);
//				PageInfo<GcVideo> page = service.page(videoParams, system, request);
//				if(page.getList().size() != TableConstant.COMMON_ZERO) {
//					List<GcVideo> gcVideoList = page.getList();
//					List<Integer> videoIdList = gcVideoList.stream().map(GcVideo::getId).collect(Collectors.toList());
//					//查询相关视频所在的二级课程
//					List<GcSubject> contentRelatedSubjects = newUiGcSubjectService.selectSubjectsByVids(videoIdList);
//					//一级课程id
//					List<Integer> contentRelated0SubjectsIds = contentRelatedSubjects.stream().distinct().map(GcSubject::getFid).collect(Collectors.toList());
//					//拼接视频所在在的课程的基本信息
//					//课程下包含了搜索条件中视频的一级课程
//					List<GcSubject> contentRelated0Subjects = newUiGcSubjectService.selectSubjectsByIds(contentRelated0SubjectsIds);
//					//统计参与人数
//					Map<Integer, GcUser> subjectUsers = gcUserService.getUsersBySubject(contentRelated0SubjectsIds,masterId);
//					//统计一级课程下的视频数量
//					List<GcVideo> videosBySubjectIds0 = service.getVideosBySubjectIds0(contentRelated0SubjectsIds, userId,masterId,request,EnvType.GC.getCode());
//					Map<Integer, List<GcVideo>> sub0Map = new HashMap<>(0);
//					if(CollectionUtils.isNotEmpty(videosBySubjectIds0)){
//						//按一级课程id
//						sub0Map = videosBySubjectIds0.stream().filter(map->map.getSubId0()!=null).collect(Collectors.groupingBy(GcVideo::getSubId0));
//					}
//					//话题进度,查询评论总数和星级评价
//					Map<String, Object> NewvideoParams = new HashMap<>(2);
//					NewvideoParams.put("subjectIds", contentRelated0SubjectsIds);
//					NewvideoParams.put("type", TableConstant.gcUserVideoAction_type_star3);
//					Map<Integer, GcUserVideoAction> subjectUserStar = videoActionService.getSubjectUserStar(NewvideoParams);
//					//装载图片fullurl，待优化
//					for (GcSubject subject : contentRelated0Subjects) {
//						if (null != subject.getSubImgFile()) {
//							SysFile subImgFile = subject.getSubImgFile();
//							subImgFile.setFullFileUrl(sysFileService.getResFullUrl(subImgFile, request));
//						}
//						GcUser users = subjectUsers.get(subject.getId());
//						if(users!=null)subject.setSubjectUsers(users.getSubjectUsers());
//						GcUserVideoAction videoActions = subjectUserStar.get(subject.getId());
//						if(videoActions!=null){
//							subject.setStarUsers(videoActions.getSubjectStarUsers());
//							subject.setStarValue(videoActions.getSubjectStarAvg());
//						}else {
//							subject.setStarUsers(TableConstant.starUsers);
//							subject.setStarValue(TableConstant.starValue0);
//						}
//						List<GcVideo> videoList = sub0Map.get(subject.getId());
//						if(videoList.size()!=TableConstant.COMMON_ZERO)subject.setVideosTotalNum(videoList.size());
//					}
//					PageInfo<GcSubject> pageInfo = newUiGcSubjectService.list(params,system, request,envFlag);
//					if(pageInfo.getList()==null){
//						pageInfo.setList(contentRelated0Subjects);
//						return msg.addData("subjectPage", pageInfo);
//					}
//					List<GcSubject> nameRelatedSubjects = pageInfo.getList();
//					List<Integer> nameRelatedSubjectsIds = nameRelatedSubjects.stream().map(GcSubject::getId).collect(Collectors.toList());
//					for (GcSubject gcSubject : contentRelated0Subjects) {
//						if (!nameRelatedSubjectsIds.contains(gcSubject.getId())) {
//							nameRelatedSubjects.add(gcSubject);
//						}
//					}
//					pageInfo.setList(nameRelatedSubjects);
////					pageInfo.setTotal(nameRelatedSubjects.size());
//					return msg.addData("subjectPage", pageInfo);
//				}
//			}else if(TableConstant.VIDEO_SEARCH_RETURN_TYPE2.equals(returnTypeObj.toString()) && Objects.isNull(params.get("subjectName")) && envFlag.equals(EnvType.PT.getCode())){
//				//查询所有课程
				return msg.addData("subjectPage",newUiGcSubjectService.list(params, system, request,envFlag));
			}
			//tag查询
			if(TableConstant.VIDEO_SEARCH_RETURN_TYPE3.equals(returnTypeObj.toString())){
				List<Integer> subIds = new ArrayList<>();
				if (Objects.isNull(userId)){
					List<GcSubject> level0sublist = subjectService.getLevel0SubListWithImg(masterId, system, request,new PageParam(request),null);
					subIds = level0sublist.stream().map(GcSubject::getId).collect(Collectors.toList());
				}else {
					//查询对应有权限的课程
					GcUserAccessPermission gcUserAccessPermissions = gcUserAccessPermissionService.getPermissionByUid(userId,masterId);
					JSONArray subPermission = gcUserAccessPermissions.getSubPermission();
					if(!subPermission.isEmpty()) {
						subIds = new ArrayList<>();
						for (Object obj : subPermission) {
							subIds.add(Integer.parseInt(obj.toString()));
						}
					}
				}
				params.put("subIds",subIds);
				PageInfo<GcSubject> pageInfo = newUiGcSubjectService.list(params, system, request,envFlag);
				return msg.addData("tagCourse",pageInfo);
			}
			return msg;
		} catch (Exception e) {
			log.error("", e);
			return new Message().error(e.getMessage());
		}
	}



	public Message searchResultPt(Map<String, Object> params, HttpServletRequest request,GcUser user,SysSystem system,Integer envFlag){
		try {
			GcUser gcUser = null;
			Integer userId = null;
			String token = request.getHeader("Authorization");
			if (null != token && !"".equals(token) && !"undefined".equals(token)){
				params.put("userId", user.getId());
				userId = user.getId();
			}
			Integer masterId = request.getIntHeader("masterId");
			Object returnTypeObj = params.get("returnType");
			Message msg = new Message().ok();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			msg.addData("systemTime",df.format(new Date()));
			//taglist
			List<String> subWithTagList = newUiGcSubjectService.selectAllTag(masterId,userId);
			msg.addData("allTagList",subWithTagList);
			if(returnTypeObj == null || TableConstant.VIDEO_SEARCH_RETURN_TYPE1.equals(returnTypeObj.toString())|| TableConstant.RESULTS_SEARCH_RETURN_TYPE7.equals(returnTypeObj.toString())) {
				//加载视频
				params.put("videoName",params.get("searchName"));
				params.put("pageNum",request.getHeader("pageNum"));
				params.put("pageSize",request.getHeader("pageSize"));
				params.put("videoNum",TableConstant.COMMON_ZERO);
				params.put("state",TableConstant.COMMON_ONE);
				PageInfo<GcVideo> page = service.page(params, system, request);
				PageParam pageParam = new PageParam(request);
				if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
					PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
				}
				PageInfo<PtChannel> channelPageInfo = new PageInfo<>();
				List<PtChannel> channelPage = new ArrayList<>();
				request.setAttribute("searchName",params.get("searchName"));
				channelPage = ptChannelService.searchChannelsBySysFile(userId,request,masterId);
				channelPageInfo = new PageInfo<>(channelPage);
				if (channelPage.size()!=TableConstant.COMMON_ZERO){
					msg.addData("channelVideoPage",channelPageInfo);
				}

				if (TableConstant.COMMON_ZERO==page.getList().size()&&!TableConstant.RESULTS_SEARCH_RETURN_TYPE7.equals(returnTypeObj.toString())){
					params.remove("videoNum");
					//page = service.page(params, system, request);
					PageInfo<PtChannel> videoNullPage = new PageInfo<>();
					PageParam pageParam2 = new PageParam(request);
					if (pageParam2.getPageNum() > 0 && pageParam2.getPageSize() > 0) {
						PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
					}
					request.removeAttribute("searchName");
					channelPage = ptChannelService.searchChannelsBySysFile(userId,request,masterId);
					videoNullPage = new PageInfo<>(channelPage);
					return msg.addData("videoNullPage", videoNullPage);

				}
				if (TableConstant.RESULTS_SEARCH_RETURN_TYPE7.equals(returnTypeObj.toString())){
					page.getList().forEach(video -> {
						video.setVideoName(video.getVideoName().replace("%20", " "));
					});
					msg.addData("videoPage",page);
					params.remove("videoNum");
					if (page.getList().size()==TableConstant.COMMON_ZERO){
						//page = service.page(params, system, request);
						PageInfo<PtChannel> videoNullPage = new PageInfo<>();
						PageParam pageParam2 = new PageParam(request);
						if (pageParam2.getPageNum() > 0 && pageParam2.getPageSize() > 0) {
							PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
						}
						request.removeAttribute("searchName");
						channelPage = ptChannelService.searchChannelsBySysFile(userId,request,masterId);
						videoNullPage = new PageInfo<>(channelPage);
						msg.addData("videoNullPage", videoNullPage);
					}
				}else {
					page.getList().forEach(video -> {
						video.setVideoName(video.getVideoName().replace("%20", " "));
					});
					return msg.addData("videoPage", page);
				}
			}

			if(TableConstant.VIDEO_SEARCH_RETURN_TYPE2.equals(returnTypeObj.toString()) && Objects.nonNull(params.get("searchName")) || TableConstant.RESULTS_SEARCH_RETURN_TYPE7.equals(returnTypeObj.toString())) {
				params.remove("videoName");
				params.put("subjectName",params.get("searchName"));
//				//查询所有课程
				PageInfo<GcSubject> pageInfo = new PageInfo<>();
				pageInfo = newUiGcSubjectService.list(params,system, request,envFlag);
				if ((null==pageInfo.getList()||TableConstant.COMMON_ZERO==pageInfo.getList().size())&&!TableConstant.RESULTS_SEARCH_RETURN_TYPE7.equals(returnTypeObj.toString())){
					PageParam pageParam = new PageParam(request);
					if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
						PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
					}
					List<GcSubject> level0sublist = new ArrayList<>();
					level0sublist = subjectService.getLevel0SubListWithImg(masterId, system, request,new PageParam(request),null);
					List<Integer> level0subIds = level0sublist.stream().map(GcSubject::getId).collect(Collectors.toList());
					List<SysFile> sysFileList = new ArrayList<>();
					if (TableConstant.COMMON_ZERO!=level0subIds.size()){
						sysFileList = sysFileService.listByIds(level0subIds);
					}
					Map<Integer,SysFile> sysFileMap = sysFileList.stream().collect(Collectors.toMap(SysFile::getId,SysFile -> SysFile, (key1, key2) -> key2, LinkedHashMap::new));
					//待优化
					if(level0sublist != null && level0sublist.size() > 0){
						for(GcSubject li:level0sublist) {
							if(li.getSubImgId()==null ) {
								continue;
							}
							if (null!=sysFileMap&&sysFileMap.get(li.getSubImgId())!=null){
								SysFile file = sysFileMap.get(li.getSubImgId());
								if(file!=null) {
									file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
									li.setSubImgFile(file);
								}
							}
						}
					}
					List<Integer> allLevel0subIds = level0sublist.stream().map(GcSubject::getId).collect(Collectors.toList());
					Map<Integer, GcUser> subjectUsers = gcUserService.getWatchedUserNum(allLevel0subIds,masterId);
					List<Integer> videoIdlist = gcVideoService.getVideoIdListBySubId(allLevel0subIds);
					List<GcVideo> videoList = gcVideoService.getVideoLongListByVideoId(videoIdlist);
					if (null!=user){
						videoList = gcVideoService.buildVideoInfo(user.getId(),null,videoList,masterId,request,EnvType.PT.getCode());
					}
					Map<Integer,List<GcVideo>> groupBySubId = videoList.stream().filter(e -> null!=e.getSubjectSubId()).collect(Collectors.groupingBy(GcVideo::getSubjectSubId));

					for(GcSubject gcSubject : level0sublist){
						if(CollectionUtils.isNotEmpty(subjectUsers)) {
							GcUser gcUser2 = subjectUsers.get(gcSubject.getId());
							if (null != gcUser2) {
								gcSubject.setSubjectUsers(gcUser2.getSubjectUsers());
							}else {
								gcSubject.setSubjectUsers(TableConstant.COMMON_ZERO);
							}
						}
					}

					Map<String, Object> videoParams = new HashMap<>(2);
					Integer ids = TableConstant.COMMON_ZERO;
					videoParams.put("ids",ids);
					videoParams.put("subjectIds", level0subIds);
					Map<Integer, GcUserVideoAction> subjectUserStar = new HashMap<>();
					if(envFlag==EnvType.GVG.getCode()){
						subjectUserStar = videoActionService.gvggetSubjectUserStar(videoParams);
					}else{
						subjectUserStar = videoActionService.getSubjectUserStar(videoParams);
					}
					GcUserAccessPermission gcUserAccessPermission = null;
					if (null!=user&&envFlag.equals(EnvType.GVG.getCode())){
						GcUserAccess gcUserAccess = gcUserAccessService.getAccessByUserIdMaster(user.getId(),masterId);
						gcUserAccessPermission = gcUserAccessService.getUserAccessPermission(gcUserAccess.getId());
					}
					for(GcSubject gcSubject : level0sublist){
						if(groupBySubId.get(gcSubject.getId())!=null){
							List<GcVideo> gcVideos = groupBySubId.get(gcSubject.getId());
							Integer totalSeconds = gcVideos.stream().filter(a -> a.getVideoLong()!=null).mapToInt(GcVideo::getVideoLong).sum();
							gcSubject.setVideosTotalLong(totalSeconds);
						}

						GcUserVideoAction videoActions = subjectUserStar.get(gcSubject.getId());
						if(videoActions != null) {
							//按type 进行分组
							// 1.2k type=3的平均值 1.2k是打星的总人数
							gcSubject.setStarValue(videoActions.getSubjectStarAvg());//星级平均值
							// 打星总人数
							gcSubject.setStarUsers(videoActions.getSubjectStarUsers());
						}else {
							gcSubject.setStarValue(TableConstant.starValue0);//星级平均值
							gcSubject.setStarUsers(TableConstant.starUsers);
						}
						if (null!=gcUserAccessPermission) {
							if (gcUserAccessPermission.getSubPermission().contains(gcSubject.getId())) {
								gcSubject.setOwnFlag(TableConstant.COMMON_ONE);
							} else {
								gcSubject.setOwnFlag(TableConstant.COMMON_ZERO);
							}
						}
						if (null!=user) {
							if (null!=groupBySubId.get(gcSubject.getId())){
								Map<Integer, List<GcVideo>> sub1Map = groupBySubId.get(gcSubject.getId()).stream().collect(Collectors.groupingBy(GcVideo::getSubId));
								List<GcSubject> twoSubject = newUiGcSubjectService.buildSubject1(sub1Map);
								SubjectTotals subjectTotals = calcTotals(twoSubject, user.getId(), true, masterId, envFlag);
								gcSubject.setPercents(new BigDecimal(subjectTotals.getTotalProgressPercent()));
							}
						}
					}
					pageInfo = new PageInfo<>(level0sublist);
					return msg.addData("subjectNullPage", pageInfo);
				}else {
					if (null!=pageInfo.getList()&&pageInfo.getList().size()!=TableConstant.COMMON_ZERO){
						List<Integer> videoIdlist = gcVideoService.getVideoIdListBySubId(pageInfo.getList().stream().map(GcSubject::getId).collect(Collectors.toList()));
						List<GcVideo> videoList = gcVideoService.getVideoLongListByVideoId(videoIdlist);
						if (null!=user){
							videoList = gcVideoService.buildVideoInfo(user.getId(),null,videoList,masterId,request,EnvType.PT.getCode());
						}
						Map<Integer,List<GcVideo>> groupBySubId = videoList.stream().filter(e -> null!=e.getSubjectSubId()).collect(Collectors.groupingBy(GcVideo::getSubjectSubId));

						for (GcSubject gcSubject : pageInfo.getList()) {
							if (null!=user) {
								if (null!=groupBySubId.get(gcSubject.getId())){
									Map<Integer, List<GcVideo>> sub1Map = groupBySubId.get(gcSubject.getId()).stream().collect(Collectors.groupingBy(GcVideo::getSubId));
									List<GcSubject> twoSubject = newUiGcSubjectService.buildSubject1(sub1Map);
									SubjectTotals subjectTotals = calcTotals(twoSubject, user.getId(), true, masterId, envFlag);
									gcSubject.setPercents(new BigDecimal(subjectTotals.getTotalProgressPercent()));
								}
							}
						}
					}
					if (TableConstant.RESULTS_SEARCH_RETURN_TYPE7.equals(returnTypeObj.toString())){
						msg.addData("subjectPage", pageInfo);
					}else {
						return msg.addData("subjectPage", pageInfo);
					}
				}
			}
			//tag查询
			if(TableConstant.VIDEO_SEARCH_RETURN_TYPE3.equals(returnTypeObj.toString())){
				List<Integer> subIds = new ArrayList<>();
				if (Objects.isNull(userId)){
					List<GcSubject> level0sublist = subjectService.getLevel0SubListWithImg(masterId, system, request,new PageParam(request),null);
					subIds = level0sublist.stream().map(GcSubject::getId).collect(Collectors.toList());
				}else {
					//查询对应有权限的课程
					GcUserAccessPermission gcUserAccessPermissions = gcUserAccessPermissionService.getPermissionByUid(userId,masterId);
					JSONArray subPermission = gcUserAccessPermissions.getSubPermission();
					if(!subPermission.isEmpty()) {
						subIds = new ArrayList<>();
						for (Object obj : subPermission) {
							subIds.add(Integer.parseInt(obj.toString()));
						}
					}
				}
				params.put("subIds",subIds);
				PageInfo<GcSubject> pageInfo = newUiGcSubjectService.list(params, system, request,envFlag);
				return msg.addData("tagCourse",pageInfo);
			}
			if (TableConstant.CHANNEL_SEARCH_RETURN_TYPE5.equals(returnTypeObj.toString())||TableConstant.RESULTS_SEARCH_RETURN_TYPE7.equals(returnTypeObj.toString())){
				PageParam pageParam = new PageParam(request);
				if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
					PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
				}
				//查询channel
				List<PtChannel> ptChannelList = new ArrayList<>();
				request.setAttribute("searchName",params.get("searchName").toString());
				ptChannelList = ptChannelService.indexSearchChannels(userId,null,request,masterId);
				if (TableConstant.COMMON_ZERO==ptChannelList.size()&&!TableConstant.RESULTS_SEARCH_RETURN_TYPE7.equals(returnTypeObj.toString())){
					if(Objects.isNull(user)){
						ptChannelList = ptChannelService.indexSearchChannels(null,null,request,masterId);
					}else {
						List<PtChannel> publicChannels = ptChannelService.indexSearchChannels(user.getId(),TableConstant.COMMON_ZERO,request,masterId);
						List<PtChannel> mychannels =     ptChannelService.indexSearchChannels(user.getId(),TableConstant.COMMON_ONE,request,masterId);
						/*channels = ptChannelService.selectPtChannels(user.getId(),TableConstant.COMMON_ONE,request,gcMaster.getId());*/
						if(publicChannels.size()>0&&mychannels.size()>0){
							publicChannels.addAll(mychannels);//合并我的频道和公共频道
							ptChannelList = publicChannels;
						}else if(publicChannels.size()>0){
							ptChannelList = publicChannels;
						}else {
							ptChannelList = mychannels;
						}
					}
					PageInfo<PtChannel> pageInfo = new PageInfo<>(ptChannelList);
					return msg.addData("channelNullPage",pageInfo);
				}else {
					PageInfo<PtChannel> pageInfo = new PageInfo<>(ptChannelList);
					if (TableConstant.RESULTS_SEARCH_RETURN_TYPE7.equals(returnTypeObj.toString())){
						msg.addData("channelPage",pageInfo);
					}else {
						return msg.addData("channelPage", pageInfo);
					}
				}
			}
			if (TableConstant.PLAYLIST_SEARCH_RETURN_TYPE6.equals(returnTypeObj.toString())||TableConstant.RESULTS_SEARCH_RETURN_TYPE7.equals(returnTypeObj.toString())){
				request.setAttribute("playListName",params.get("searchName").toString());
				//recommenplaylist
				List<GcUserSaveFolder> recommentPlayList = gcUserSaveFolderService.selectFolderInMaster(masterId);
				List<Integer> recommenFolderIds = recommentPlayList.stream().map(GcUserSaveFolder::getId).collect(Collectors.toList());
				List<GcUserSaveFolder> recommenFolderList = gcUserSaveFolderService.getPtHomePlayList(userId,masterId,recommenFolderIds,request);
				for(GcUserSaveFolder gcUserSaveFolder : recommenFolderList){
					//缩略图
					if(Objects.nonNull(gcUserSaveFolder.getFirstVideoFileId())) {
						SysFile sysFile = sysFileService.getById(gcUserSaveFolder.getFirstVideoFileId());
						String snapshotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
						gcUserSaveFolder.setSnapshotUrl(snapshotUrl);
					}
				}
				PageInfo<GcUserSaveFolder> recommenFolderListPageInfo = new PageInfo<>(recommenFolderList);
				if (TableConstant.COMMON_ZERO==recommenFolderList.size()&&!TableConstant.RESULTS_SEARCH_RETURN_TYPE7.equals(returnTypeObj.toString())){
					request.removeAttribute("playListName");
					recommenFolderList = gcUserSaveFolderService.getPtHomePlayList(null,masterId,recommenFolderIds,request);
					for(GcUserSaveFolder gcUserSaveFolder : recommenFolderList){
						//缩略图
						if(Objects.nonNull(gcUserSaveFolder.getFirstVideoFileId())) {
							SysFile sysFile = sysFileService.getById(gcUserSaveFolder.getFirstVideoFileId());
							String snapshotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
							gcUserSaveFolder.setSnapshotUrl(snapshotUrl);
						}
					}
					recommenFolderListPageInfo = new PageInfo<>(recommenFolderList);
					return msg.addData("recommenFolderListNullPageInfo",recommenFolderListPageInfo);
				}else {
					if(TableConstant.RESULTS_SEARCH_RETURN_TYPE7.equals(returnTypeObj.toString())){
						msg.addData("recommenFolderListPageInfo",recommenFolderListPageInfo);
					}else {
						return msg.addData("recommenFolderListPageInfo",recommenFolderListPageInfo);
					}
				}
			}
			//LocalDateTime localDateTime = LocalDateTime.now();
			return msg;
		} catch (Exception e) {
			log.error("", e);
			return new Message().error(e.getMessage());
		}
	}

	public Message activeSubject(){
		//subjectService.
		return new Message().ok();
	}

	@Override
	public Message index(Map<String, Object> params, HttpServletRequest request,SysSystem system,GcUser gcUser,Integer envFlag){
		Message message = new Message();
		PageInfo<GcSubject> page = new PageInfo<>();
		try {
			Integer masterId = request.getIntHeader("masterId");
			Integer userId = gcUser.getId();
			List<Integer> subIds = new ArrayList<>();
			if (envFlag.equals(EnvType.PT.getCode())){
				List<GcUserAccessPermission>gcUserAccessPermissionsList = gcUserAccessPermissionService.getPermissionByUidList(userId,masterId);
				List<GcUserAccessPermission> gcUserAccessPermissionList = new ArrayList<>();
				if (TableConstant.COMMON_ZERO!=gcUserAccessPermissionsList.size()){
					gcUserAccessPermissionsList.forEach(i->{
						if (null!=i.getShortTermPermission()){
							gcUserAccessPermissionList.addAll(i.getShortTermPermission().toJavaList(GcUserAccessPermission.class));
						}
						if (null!=i.getSubPermission()){
							for (Object o : i.getSubPermission()) {
								subIds.add(Integer.parseInt(o.toString()));
							}
						}
						if (null!=i.getMustSubjectJson()){
							for (Object o : i.getMustSubjectJson()) {
								subIds.add(Integer.parseInt(o.toString()));
							}
						}
					});
				}
				Date date = new Date();
				if (CollectionUtils.isNotEmpty(gcUserAccessPermissionList)) {
					for (GcUserAccessPermission gcUserAccessPermission : gcUserAccessPermissionList) {
						if (gcUserAccessPermission.getExpired().after(date)) {
							if (!subIds.contains(gcUserAccessPermission.getId())) {
								subIds.add(gcUserAccessPermission.getId());
							}
						} else if (gcUserAccessPermission.getExpired().before(date)) {
							if (subIds.contains(gcUserAccessPermission.getId())) {
								subIds.remove(gcUserAccessPermission.getId());
							}
						}
					}
					params.put("subjectIds", subIds);
				}
			}else {
				GcUserAccessPermission gcUserAccessPermissions = gcUserAccessPermissionService.getPermissionByUid(userId, masterId);
				JSONArray subPermission = gcUserAccessPermissions.getSubPermission();
				if (!subPermission.isEmpty()) {
					for (Object obj : subPermission) {
						subIds.add(Integer.parseInt(obj.toString()));
					}
				}
				params.put("userId", userId);
				if (null != gcUserAccessPermissions.getShortTermPermission()) {
					List<GcUserAccessPermission> gcUserAccessPermissionList = gcUserAccessPermissions.getShortTermPermission().toJavaList(GcUserAccessPermission.class);
					//List<Integer> shortTermIds = gcUserAccessPermissionList.stream().map(GcUserAccessPermission::getId).collect(Collectors.toList());
					Date date = new Date();
					if (CollectionUtils.isNotEmpty(gcUserAccessPermissionList)) {
						for (GcUserAccessPermission gcUserAccessPermission : gcUserAccessPermissionList) {
							if (gcUserAccessPermission.getExpired().after(date)) {
								if (!subIds.contains(gcUserAccessPermission.getId())) {
									subIds.add(gcUserAccessPermission.getId());
								}
							} else if (gcUserAccessPermission.getExpired().before(date)) {
								if (subIds.contains(gcUserAccessPermission.getId())) {
									subIds.remove(gcUserAccessPermission.getId());
								}
							}
						}
						params.put("subjectIds", subIds);
					}
				}
			}
			if(null!=params.get("tag")) {
				params.put("subIds", subIds);
			}
			List<Integer> channelIdList = new ArrayList<>();
			List<String> nameList = new ArrayList<>();
			nameList.add("channelIds");
			List<GcMasterHomeInfo> gcMasterHomeInfos = iGcMasterHomeInfoService.getGcMasterHomeInfoList(masterId,nameList,system,request);
			if(CollectionUtils.isNotEmpty(gcMasterHomeInfos)){
				channelIdList = gcMasterHomeInfos.get(TableConstant.COMMON_ZERO).getChannelIds().toJavaList(Integer.class);
			}
//			List<GcMaster> masterChannels = channelConfiguration.getChannelIdList();
//			if(CollectionUtils.isNotEmpty(masterChannels)) {
//				Optional<GcMaster> channelMaster = masterChannels.stream().filter(e -> e.getId().equals(masterId)).findFirst();
////				if (channelMaster.isPresent()) {
////					channelIdList = channelMaster.get().getChannelIds();
////				}
//			}
			if((null!=channelIdList && channelIdList.size()>TableConstant.COMMON_ZERO)){
				params.put("channelIdList",channelIdList);
			}
			//1、分页查询出课程信息
			//if(CollectionUtils.isNotEmpty(subIds)){
				page = newUiGcSubjectService.list(params,system, request,envFlag);
			//}
			//2.查询出学生的所有课程
			List<GcSubject> allSubList = subjectService.selectAllSubByUserId(masterId,userId,request);
			PageInfo<GcSubject> pageInfo = new PageInfo<>(allSubList);
			message.ok().addData("allSubList",pageInfo);
			//给二级课程按照order排序
			List<GcSubject> subjects = page.getList();
			if(null!=subjects) {
				List<Integer> sub0Ids = subjects.stream().map(GcSubject::getId).collect(Collectors.toList());
				List<GcSubject> subTwoList = subjectService.selectTwoSubjectsByFids(sub0Ids);
				for (GcSubject subject : subjects) {
					List<GcSubject> subjects1 = subject.getSubjects();
					List<GcSubject> orderedSubTwoList = new ArrayList<>();
					if (null != subjects1) {
						for (GcSubject subTwo : subjects1) {
							for (GcSubject gcSubject : subTwoList) {
								if (subTwo.getId().equals(gcSubject.getId())) {
									subTwo.setOrder(gcSubject.getOrder());
								}
							}
						}
						orderedSubTwoList = subjects1.stream().sorted(Comparator.comparing(GcSubject::getOrder)).collect(Collectors.toList());
					}
					subject.setSubjects(orderedSubTwoList);
					if(null!=subject.getCertificatesFlag() && TableConstant.COMMON_ONE==subject.getCertificatesFlag() && null!=subject.getVideoProgressPercent() && 100==subject.getVideoProgressPercent()){
						subject.setEnableCertificatesFlag(TableConstant.COMMON_ONE);
					}
				}
				page.setList(subjects);
			}
			List<String> allTags = newUiGcSubjectService.selectSubjectTag(masterId,userId);
			message.ok().addData("allTags",allTags);
			message.ok().addData("page",page);
			//message.ok().addData("allSubList",allSubList);

			List<GcMasterHomeInfo> allHomeInfos = iGcMasterHomeInfoService.getGcMasterHomeInfoList(masterId,TableConstant.gcMasterHomeInfo_name_homepage_list,system,request);
			message.ok().addData("homeInfos",allHomeInfos);
			return  message.ok();
		} catch (Exception e) {
			log.error("", e);
			return new Message().error(e.getMessage());
		}
	}

	@Override
	public Message navigation(Map<String, Object> params, HttpServletRequest request,SysSystem system,GcUser user,Integer envFlag){
		Integer masterId = request.getIntHeader("masterId");
		GcMaster gcMaster = gcMasterService.getById(masterId);
		Message msg = new Message().ok();
		if(params==null) params=new HashMap<>();
		if(params.get("fid")==null) {
			throw new SystemException(I18NUtil.get("一级课程id fid不可空"));
		}
		GcSubject subject=subjectService.getById(params.get("fid").toString());
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
			gcUser = user;
		}else {
			//后续需增加判断，该用户是否是这个学生的老师
			gcUser=gcUserService.getUserByIdCache((Integer)params.get("studentId"));
			sysFileService.getResFullUrl(gcUser.getInfo().getAvatarFile(), request);
			msg.addData("student", gcUser);
		}

		List<Integer> subIds = new ArrayList<>();
		subIds.add(Integer.parseInt(params.get("fid").toString()));
//		Map<Integer, GcUser> subjectUsers = gcUserService.getUsersBySubject(subIds,masterId);
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
			List<GcSubject> orderTwoSubList = orderSubject;//.stream().sorted(Comparator.comparing(GcSubject::getOrder).thenComparing(GcSubject::getCreateTime)).collect(Collectors.toList());
			List<Integer> videoIds = new ArrayList<>();
			List<Integer> orderTwoSubIds = orderTwoSubList.stream().map(GcSubject::getId).collect(Collectors.toList());
			if (null!=orderTwoSubIds&&TableConstant.COMMON_ZERO!=orderTwoSubIds.size()){
				videoIds = videoService.getIdsBySubIds(orderTwoSubIds);
			}
			Map<Integer,List<PtTags>> tagListMap = new HashMap<>();
			if (TableConstant.COMMON_ZERO!=videoIds.size()){
				QueryWrapper<PtTags> tagsQueryWrapper = new QueryWrapper<>();
				tagsQueryWrapper.in("video_id",videoIds);
				//tagsQueryWrapper.eq("master_id", masterId);
				if (null!=ptTagsService.list(tagsQueryWrapper)){
					tagListMap = ptTagsService.list(tagsQueryWrapper).stream().collect(Collectors.groupingBy(PtTags::getVideoId));
				}
			}

			Map<Integer, List<PtTags>> finalTagListMap = tagListMap;
			orderTwoSubList.forEach(sub->{
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
//			List<GcSubject> countTwoSubList = (List<SysFile>) orderTwoSubList.stream().map(sub ->{
//
//				return sysFileCaptionService.selectSysFileCaption(sysFileCaption1.getId());
//			}).collect(Collectors.toList());
			//orderTwoSubList = orderSubject.stream().sorted(Comparator.comparing(GcSubject::getCreateTime).reversed()).collect(Collectors.toList());
			page.setList(orderTwoSubList);
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
//						if(subjects.get(i).getGcVideos().get(j).getEventList().size()!=TableConstant.COMMON_ZERO) {
//							GcVideo gcVideo =subjects.get(i).getGcVideos().get(j);
//							Integer a = subjects.get(i).getGcVideos().get(j).getEventList().size();
//							subjects.get(i).getGcVideos().get(j).setAnsweredSumNums(subjects.get(i).getGcVideos().get(j).getEventList().size());
//						}else {
//							subjects.get(i).getGcVideos().get(j).setAnsweredSumNums(TableConstant.COMMON_ZERO);
//						}
						if (subjects.get(i).getGcVideos().get(j).getVideoTime()==null){
							totalSeconds += TableConstant.COMMON_ZERO;
						}else {
							totalSeconds += subjects.get(i).getGcVideos().get(j).getVideoTime();
						}
					}
					subjects.get(i).setVideosTotalLong(totalSeconds);
				}
//				else{
//					subjects.remove(subjects.get(i));
//					continue;
//				}
			}
			List<GcSubject> subjects1 = subjects.stream().filter(e->null!=e.getGcVideos()).collect(Collectors.toList());
			List<GcSubject> subjectInSubTotal = new ArrayList<>();
//			for(int i=0;i<subjects.size();i++){
//				GcSubject gcSubject = new GcSubject();
//				gcSubject.setSubjectCompleteStatus(subjects.get(i).getSubjectCompleteStatus());
//				gcSubject.setId(subjects.get(i).getId());
//				subjectInSubTotal.add(gcSubject);
//			}
			SubjectTotals subjectTotals = calcTotals(subjects1, userId,true,masterId,envFlag);

			if(envFlag.equals(EnvType.GC.getCode())) {
				List<GcVideo> videos = videoService.selectVideoPlayListBySubId(subject.getId(), user.getId());
				List<Integer> vids = videos.stream().map(GcVideo::getId).collect(Collectors.toList());
				Map<Integer, GcUserVideoPlay> playMap = gcUserVideoPlayService.findVideoPalyStateByVideos(vids, user.getId(), masterId);
				List<GcEvent> eventList = eventService.selectEventByUserIdAndSubjectId(user.getId(), subject.getId(), masterId);
				List<GcEvent> eventAnswers = gcEventService.findEventAnswerByVideoIdsUser(vids, user.getId(), masterId, EnvType.GC.getCode());
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
				params.put("userId", user.getId());
				params.put("masterId",masterId);
				Message message =  service.getVideosBySubIds(firstSubId, subParam, system, request);
				List<GcVideo> videoList = new ArrayList<>();
				Map<String,Object> map = message.getData();
				PageInfo pageInfo = (PageInfo) map.get("videos");
				videoList = pageInfo.getList();
				/*if(Objects.nonNull(object)) {
					if (object instanceof ArrayList<?>) {
						for (Object o : (List<?>) object) {
							videoList.add(GcVideo.class.cast(o));
						}
					}
				}*/
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
			//判断选择题的正确率是否达到了百分之八十，未达到不能下载证书
//			if(subject.getCertificatesFlag()==1 && gcSubjectService.selectAccuracyByUser(userId,masterId,subject.getId(),subjectTotals)){
//				subject.setEnableCertificatesFlag(1);
//			}else {
//				subject.setEnableCertificatesFlag(0);
//			}
			subject.setResourceNum(resourceNums);
			//添加是否本人能查看反显
			List<GcUserAccessPermission> userAccessPermission = gcUserAccessPermissionService.getPermissionByUidList(userId,masterId);
			Integer isMyView = TableConstant.COMMON_ONE;
			for (GcUserAccessPermission permission : userAccessPermission) {
				if(null!=permission.getMustSubjectJson()&&permission.getMustSubjectJson().contains(subject.getId())){
					isMyView = TableConstant.COMMON_ZERO;
					break;
				}
			}
			subject.setIsMyView(isMyView);
			if (subject.getState()==TableConstant.COMMON_ZERO){
				subject.setCourseState(TableConstant.COMMON_ONE);
			}else {
				subject.setCourseState(TableConstant.COMMON_ZERO);
			}

			if ((subject.getCreateUser().equals(userId)||gcUser.getIsOrgAdmin()==true)&&subject.getState().equals(TableConstant.COMMON_ZERO)){
				subject.setMode(TableConstant.COMMON_ONE);
			}else {
				subject.setMode(TableConstant.COMMON_ZERO);
			}

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
			//1、计算总的进度 查询这个课程下的所有
//			List<Integer> subjectIds = subjects.stream().map(GcSubject::getId).collect(Collectors.toList());
//			Map<Integer, List<GcUserVideoPlay>> videoPlayBySubject = userVideoPlayService.findVideoPlayBySubject(subjectIds, userId);
//			Map<Integer, List<GcVideo>> videoCompleteStatusBySubject = gcVideoService.getVideoCompleteStatusBySubject(subjectIds, userId);
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
//				if (envFlag.equals(EnvType.PT.getCode())) {
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

//				} else {
//					if (CollectionUtils.isNotEmpty(playState1)) {
//						totals.setLessonsCompleteProgress(playState1.size());//已完成视频数
//						BigDecimal playState21 = new BigDecimal(TableConstant.COMMON_ZERO);
//						playState21 = new BigDecimal(playState1.size()).divide(new BigDecimal(gcVideos.size()), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
//						totals.setTotalProgressPercent(playState21.intValue());
//					}
//				}
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
		List<Integer> subIds = new ArrayList<>();
		//当前视频
		GcVideo video = gcVideoService.getById(videoId);
		GcVideo thisVideo = gcVideoService.getVideoById(videoId);

		GcVideo videoPlay = gcVideoService.selectVideoPlayByVideo(thisVideo.getId(),user.getId());
		if (null!=videoPlay.getPlayState()){
			thisVideo.setPlayState(videoPlay.getPlayState());
		}

		thisVideo.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(thisVideo.getVideoFile()));
		GcSubject subject = subjectService.getById(video.getSubId());
		GcUserAccessPermission gcUserAccessPermission = new GcUserAccessPermission();
		List<Integer> permissionSubIds = new ArrayList<>();

		if (envFlag.equals(EnvType.PT.getCode())){
			if (null!= user){
				List<GcUserAccess> gcUserAccess = gcUserAccessService.selectPtUserAccessByMasterIdAndUserId(user.getId(),masterId);
				List<GcUserAccessPermission> userAccessPermissions = gcUserAccessService.getUsersAccessPermissions(gcUserAccess.stream().map(GcUserAccess::getId).collect(Collectors.toList()));
				for (GcUserAccessPermission userAccessPermission : userAccessPermissions) {
					if (null!=userAccessPermission.getSubPermission()){
						permissionSubIds.addAll(userAccessPermission.getSubPermission().toJavaList(Integer.class));
					}
				}
				permissionSubIds.add(subject.getFid());
			}else {
				user = new GcUser();
			}
		}else {
			if (null != user){
				GcUserAccess gcUserAccess = gcUserAccessService.getAccessByUserIdMaster(user.getId(),masterId);
				gcUserAccessPermission = gcUserAccessService.getUserAccessPermission(gcUserAccess.getId());
				if (null!=gcUserAccessPermission.getSubPermission()){
					permissionSubIds.addAll(gcUserAccessPermission.getSubPermission().toJavaList(Integer.class));
				}
			}else {
				user = new GcUser();
			}
		}


//		if (null != user){
//			GcUserAccess gcUserAccess = gcUserAccessService.getAccessByUserIdMaster(user.getId(),masterId);
//			gcUserAccessPermission = gcUserAccess.getGcUserAccessPermission();
//			JSONArray subPermission = gcUserAccessPermission.getSubPermission();
//			//判断该用户是否拥有当前视频权限，以及当前视频所在的课程是否过期
//			List<Integer> accessPermission = gcUserAccessPermission.getSubPermission().toJavaList(Integer.class);
//			if(!accessPermission.contains(subject.getFid())){
//				return new Message().error(I18NUtil.get("guidecore.video.access"));
//			}else if(accessPermission.contains(subject.getFid()) && null!=gcUserAccessPermission.getShortTermPermission()){
//				List<SysPackagePeriod> gcUserAccessPermissionList = gcUserAccessPermission.getShortTermPermission().toJavaList(SysPackagePeriod.class);
//				List<Integer> shortTermIds = gcUserAccessPermissionList.stream().map(SysPackagePeriod::getId).collect(Collectors.toList());
//				Date date = new Date();
//				if (shortTermIds.contains(subject.getFid())) {
//					Optional<SysPackagePeriod> userAccessPermission = gcUserAccessPermissionList.stream().filter(e->subject.getFid().equals(e.getId())).findFirst();
//					SysPackagePeriod sysPackagePeriod = userAccessPermission.get();
//					if(sysPackagePeriod.getExpired().before(date)){
//						return new Message().error(I18NUtil.get("guidecore.video.access"));
//					}
//				}
//			}
//		}else {
//			user = new GcUser();
//		}


		List<GcResource> resourceServiceList = new ArrayList<>();
		PageParam pageParam = new PageParam(request);
		Integer pageNum = pageParam.getPageNum();
		Integer pageSize=pageParam.getPageSize();
		if (pageNum > 0 && pageSize > 0) {
			PageHelper.startPage(pageNum, pageSize);
		}
		resourceServiceList = resourceService.getResByVid(videoId);
		//如果没登录，不返回fullfileurl
		thisVideo.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(thisVideo.getVideoFile()));
		if (Objects.nonNull(user.getId()) && permissionSubIds.contains(subject.getFid())) {
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
//    	问题数/已答问题数





//    	资源数
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
		List<GcUserVideoAction> videoActionList = gcUserVideoActionService.getVideoActionListByVidAndUserId(videoId, user.getId());
		Integer isLike =0;
		GcUserVideoAction rateVideoAction=null;
		GcUserVideoAction startAction=null;
		Double starValue=null;
		for(GcUserVideoAction va:videoActionList) {
			if(va.getType().intValue()==TableConstant.gcUserVideoAction_type_like1)isLike=1;
			if(va.getType().intValue()==TableConstant.gcUserVideoAction_type_rate2)rateVideoAction=va;
			if(va.getType().intValue()==TableConstant.gcUserVideoAction_type_star3)starValue=va.getStarValue();
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
		m.addData("isLike", isLike);//点赞
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
			GcUserAccess gcUserAccess = new GcUserAccess();
			if (envFlag.equals(EnvType.PT.getCode())){
				GcAccess access = new GcAccess();
				access.setRoleType(AccessRoleType.STUDENT);
				gcUserAccess.setAccess(access);
			}else {
				gcUserAccess = userAccessService.getUserAccessByMasterIdAndUserId(masterId, user.getId());
			}
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
		m.addData("eventList", eventList);
		m.addData("totalEventNum", totalEventNum);
		m.addData("answeredEventNum", answeredEventNum);
//		}
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
		//if(event.getEventType().intValue()==TableConstant.gcEvent_eventType_freeType2) {
		List<GcUserEventResource> thisUserEventResourceList = gcUserEventResourceService.getEventResListForWorkBook(eventId, thisUserId, null,masterId,request,null);

		//快速修复查看别人的回答，显示了老师回复的内容，后续得改sql
		if(otherUserId!=null && otherUserId!=user.getId()) {
//			int size=thisUserEventResourceList.size();
			for(int i=0; i<thisUserEventResourceList.size(); i++) {
				if(1==thisUserEventResourceList.get(i).getIsTeacher()) {
					thisUserEventResourceList.remove(i);
					i--;
				}
			}
		}
		//快速修复查看别人的回答，显示了老师回复的内容，后续得改sql

		m.addData("thisUserEventResourceList", thisUserEventResourceList);
		//}

		//评论数量
		Integer commentNum = gcUserNoteCommentService.selectCommentNum(eventId,thisUserId,masterId);
		m.addData("commentNum",commentNum);
		//点赞数量
		Integer fabulousNum = gcUserFabulousService.getEventFabulousNum(eventId,thisUserId,otherUserId);
		m.addData("fabulousNum",fabulousNum);
		//点赞状态
		GcUserFabulous fabulousState = gcUserFabulousService.getUserFabulous(new GcUserFabulous(user.getId(),otherUserId,null,eventId));
		if (fabulousState == null){
			m.addData("isFabulousState",0);
		}else {
			m.addData("isFabulousState",1);
		}
		m.addData("返回说明", "thisUserEventAnswer-当前用户的回答，thisUserEventResourceList-回答资源list，仅问答题有，isFabulousState-点赞状态,1=已点赞，fabulousNum-点赞数量" +
				"，commentNum-评论数量");
		return m;
	}

	@Override
	public Message createVideoPlayRecordAndNode(@RequestBody GcUserVideoPlay userVideoPlay, HttpServletRequest request,Integer envFlag,GcUser user,Integer masterId,SysSystem system) {
		Message message = new Message();

		if (null!=userVideoPlay.getFileId()){
			userVideoPlay.setUserId(user.getId());
			userVideoPlay.setMasterId(masterId);
			SysFile file = sysFileService.getById(userVideoPlay.getFileId());
			GcUserVideoPlay videoPlay = userVideoPlayService.saveVideoPlayAndVideoPlaysNode(userVideoPlay);

			return message.ok("获取成功")
					.addData("videoPlayNode", videoPlay.getVideoPlaysNode())
					.addData("videoPlay", videoPlay)
					.addData("file",file);
		}

		GcVideo thisVideo = gcVideoService.getVideoById(userVideoPlay.getVideoId());


		ApiAssert.notNull(masterId, "masterId缺失");

		userVideoPlay.setUserId(user.getId());
		userVideoPlay.setMasterId(masterId);
		GcUserVideoPlay videoPlay = userVideoPlayService.saveVideoPlayAndVideoPlaysNode(userVideoPlay);


		List<Integer> subIdList = new ArrayList<>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("masterId", masterId);
		params.put("pageSize", 1000);
		params.put("fid", thisVideo.getSubId0());
		params.put("userId", user.getId());

		SubjectTotals subjectTotals = null;
		//每次观看完都进行进度计算
		if(TableConstant.COMMON_THREE==envFlag /*&& TableConstant.COMMON_ONE==userVideoPlay.getPlayState()*/) {
			PageInfo<GcSubject> page = newUiGcSubjectService.listSubjectByFid(params, system, request, true, subIdList, envFlag);
			List<GcSubject> orderSubject = page.getList();
			//一级课程总进度
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
					if (null != list && TableConstant.COMMON_ZERO != list.size()) {
						gcSubject1.setGcVideos(list);
					}
				}
				subjectTotals = calcTotals(subjects, user.getId(), false, masterId,envFlag);
				message.addData("subjectTotal", subjectTotals);

				//二级课程进度
				GcSubject gcSubject1 = subjectService.getById(thisVideo.getSubId());
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

		//加入课程完成进度数据
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

		return  message.ok("获取成功")
				.addData("videoPlayNode", videoPlay.getVideoPlaysNode())
				.addData("videoPlay", videoPlay);
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
		answer.setAnswerJson(answerJsonString);


		answer.setAnswerJson(answerJsonString);
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
//                masterMessage.setResId(answer.getId());
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
		List<SysFile> list = new ArrayList<>();
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
			//List<GcUserVideoPlay> gcUserVideoPlays = gcUserVideoPlayService.getLearningRecords(null,null,vid,userId,masterId);
			//List<GcUserAnswer> gcUserAnswers = gcUserAnswerService.getAnswerLearningRecords(null,null,vid,userId,masterId);
			//if(CollectionUtils.isNotEmpty(gcUserVideoPlays)||CollectionUtils.isNotEmpty(gcUserAnswers)){
			//	throw new SystemException(I18NUtil.get("powtoon.delete.course.error"));
			//}else if (videoService.deleteVideo(vid)) return new Message().ok();
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
			if (null!=subject&&null!=subject.getFid()){
				//completeService.saveInProgress(subject.getFid(),masterId,userId,request);
			}
			if (videoService.deleteVideo(vid)){
				return new Message().ok();
			}
		}else if(envFlag==EnvType.PT.getCode()){
			videoService.deleteVideo(vid);
			return new Message().ok();
		}
		return new Message().error();
	}

	public Message deleteSub(Integer subId,Integer envFlag,GcMaster master,Integer userId) {
		if(envFlag==EnvType.GC.getCode()) {
			System.out.println(master.getId());
			List<GcAccess> accessList = gcAccessService.selectAccessBySubId(subId,master.getId());
			if(CollectionUtils.isNotEmpty(accessList)){
				for(GcAccess gcAccess:accessList){
					if (null!=gcAccess.getSubjectJson()){
						gcAccess.getSubjectJson().remove(subId);
					}
					if (null!=gcAccess.getMaySubjectJson()){
						gcAccess.getMaySubjectJson().remove(subId);
					}
					if (null!=gcAccess.getMustSubjectJson()){
						gcAccess.getMustSubjectJson().remove(subId);
					}
				}
			}
			gcAccessService.updateBatchById(accessList);
			List<GcUserAccessPermission> userAccessPermissions = gcUserAccessPermissionService.getContainsSubjectAccessPermissionList(subId.toString());
			if(CollectionUtils.isNotEmpty(userAccessPermissions)){
				for(GcUserAccessPermission gcUserAccessPermission:userAccessPermissions){
					if (null!=gcUserAccessPermission.getSubPermission()){
						gcUserAccessPermission.getSubPermission().remove(subId);
					}
					if (null!=gcUserAccessPermission.getMaySubjectJson()){
						gcUserAccessPermission.getMaySubjectJson().remove(subId);
					}
					if (null!=gcUserAccessPermission.getMustSubjectJson()){
						gcUserAccessPermission.getMustSubjectJson().remove(subId);
					}
				}
			}

			gcUserAccessPermissionService.updateBatchById(userAccessPermissions);
			contentGroupCourseAssignmentService.removeByMasterAndCourseId(master.getId(),subId);

			if (subService.deleteSub(subId, master.getId())) return new Message().ok();
			return new Message().error(I18NUtil.get("guidecore.resource.deleteSucc"));
		}else if(envFlag==EnvType.PT.getCode()){
				subService.deleteSub(subId, master.getId());
				List<GcAccess> accessList = gcAccessService.selectAccessBySubId(subId,master.getId());
				if(CollectionUtils.isNotEmpty(accessList)){
					for(GcAccess gcAccess:accessList){
						if (null!=gcAccess.getSubjectJson()){
							gcAccess.getSubjectJson().remove(subId);
						}
						if (null!=gcAccess.getMaySubjectJson()){
							gcAccess.getMaySubjectJson().remove(subId);
						}
						if (null!=gcAccess.getMustSubjectJson()){
							gcAccess.getMustSubjectJson().remove(subId);
						}
					}
				}
				gcAccessService.updateBatchById(accessList);
				List<GcUserAccessPermission> userAccessPermissions = gcUserAccessPermissionService.getContainsSubjectAccessPermissionList(subId.toString());
				if(CollectionUtils.isNotEmpty(userAccessPermissions)){
					for(GcUserAccessPermission gcUserAccessPermission:userAccessPermissions){
						if (null!=gcUserAccessPermission.getSubPermission()){
							gcUserAccessPermission.getSubPermission().remove(subId);
						}
						if (null!=gcUserAccessPermission.getMaySubjectJson()){
							gcUserAccessPermission.getMaySubjectJson().remove(subId);
						}
						if (null!=gcUserAccessPermission.getMustSubjectJson()){
							gcUserAccessPermission.getMustSubjectJson().remove(subId);
						}
					}
				}
				gcUserAccessPermissionService.updateBatchById(userAccessPermissions);
                contentGroupCourseAssignmentService.removeByMasterAndCourseId(master.getId(),subId);

				return new Message().ok();
		}
		return new Message().error("删除失败");
	}

}
