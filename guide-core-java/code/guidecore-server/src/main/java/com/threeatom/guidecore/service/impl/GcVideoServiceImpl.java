package com.threeatom.guidecore.service.impl;

import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.DbAnalyticsResultVideoIdDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.CursorDto;
import com.threeatom.guidecore.dto.request.VideoListFilterDto;
import com.threeatom.guidecore.dto.response.VideoDto;
import com.threeatom.guidecore.dto.response.analytic.VideoSearchResponseDto;
import com.threeatom.guidecore.dto.response.analytic.VideoSearchResultDto;
import com.threeatom.guidecore.enums.AnalyticsType;
import com.threeatom.guidecore.enums.SortOrder;
import com.threeatom.guidecore.facade.AnalyticsFacade;
import com.threeatom.guidecore.mapping.VideoMapping;
import com.threeatom.guidecore.util.RequestUtil;
import com.threeatom.guidecore.util.stringWidthConvertUtil;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.constant.*;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.service.*;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysCaptionRequest;
import com.threeatom.system.entity.SysFileCaption;
import com.threeatom.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.mapper.GcUserVideoPlayMapper;
import com.threeatom.guidecore.mapper.GcVideoMapper;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileCaptionService;
import com.threeatom.system.service.SysFileService;
import com.threeatom.guidecore.controller.user.vo.videoLongVo;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class GcVideoServiceImpl extends ServiceImpl<GcVideoMapper, GcVideo> implements GcVideoService {


	private static final Logger LOGGER = LoggerFactory.getLogger(GcVideoServiceImpl.class);

	@Lazy
	@Autowired
	private GcEventService eventService;


	@Lazy
	@Autowired
	private GcSubjectService subjectService;

	@Lazy
	@Autowired
	private GcResourceService resourceService;

	@Autowired
	private SysFileService fileService;

	@Autowired
	GcMasterService gcMasterService;

	@Autowired
	GcUserAccessPermissionService gcUserAccessPermissionService;

	@Autowired
	SysFileCaptionService sysFileCaptionService;

	@Autowired
	GcUserVideoPlayMapper gcUserVideoPlayMapper;

	@Autowired
	GcVideoMapper gcVideoMapper;

	@Lazy
	@Autowired
	private GcUserVideoActionService videoActionService;//用户视频操作--查询评论、点赞、星级评价

	@Lazy
	@Autowired
	private GcVideoCommentService gcVideoCommentService;//视频评论

	@Lazy
	@Autowired
	private GcEventService gcEventService;//查询视频问题相关

	@Lazy
	@Autowired
	private SysFileService sysFileService;//获取文件全路径

	@Lazy
	@Autowired
	private GcUserVideoPlayService userVideoPlayService;//用户视频播放进度服务类--查询播放进度

	@Autowired
	private PtTagsService ptTagsService;

	@Autowired
	private GcSubjectCompleteService subjectCompleteService;

	@Autowired
	private VideoMapping videoMapping;

	@Autowired
	private VideoThumbnailProvider videoThumbnailProvider;

	@Autowired
	private AnalyticsFacade analyticsFacade;

	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private VideoPlaySessionService videoPlaySessionService;
	@Autowired
	private PtChannelSubscribeService channelSubscribeService;

	@Override
	public List<GcVideo> getVideoListBySubIds(List<Integer> subIds) {
		if(subIds!=null&&subIds.size()>0) {
			List<GcVideo> list = this.baseMapper.selectVideoListBySubIds(subIds);
			list.forEach(i->{
				SysFile file = i.getVideoFile();
				file.setFullFileUrl(sysFileService.getResFullUrl(file, null));
			});
			return list;
		}else {
			return new ArrayList<GcVideo>();
		}
	}

	@Override
	public List<GcVideo> selectLikeVideoByUserId(Integer userId, Integer masterId) {
		List<GcVideo> list = this.baseMapper.selectLikeVideoByUserId(userId,masterId);
        return list;
	}

	@Override
	public GcVideo getVideoById(Integer vid) {
		return this.baseMapper.selectVideoByid(vid);
	}

	@Override
	@Transactional
	public boolean saveVideo(GcVideo video) {
		return this.saveOrUpdate(video);
	}

	//删除视频下的全部事件

	@Override
	@Transactional
	public boolean deleteVideo(Integer vid) {
		//删除视频,先删除视频下面的所有的事件
		eventService.deleteEventByVid(vid);

		return this.removeById(vid);
	}

	@Override
	@Transactional
	public boolean deleteVideoBySubIds(List<Integer> subIds) {
		QueryWrapper<GcVideo> queryWrapper=new QueryWrapper<GcVideo>();
		queryWrapper.select("id").in("sub_id", subIds);

		List<GcVideo> list= this.list(queryWrapper);
		if(list.size()<1) {
			return true;
		}
		List<Integer> videoIds= getVideoIds(list);
		LOGGER.info(videoIds.size()+"   "+list.size());
		//批量删除视频下的事件
		eventService.deleteEventByVids(videoIds);

		//批量删除视频下的资源


		return this.remove(queryWrapper);
	}

	@Override
	public int getVideoNum(Integer masterId,List<Integer> subIds,Integer managerId) {
		Integer type =TableConstant.COMMON_ONE;
		Integer state = TableConstant.COMMON_ZERO;
		return this.baseMapper.countVideoNumInPortal(masterId,type,state,subIds,managerId);
	}

	@Override
	public List<GcVideo> getVideoListBySubId(Integer subId) {
		return this.baseMapper.selectVideoListBySubId(subId);
	}


	@Override
	public List<GcVideo> getFuzzyNameVideoInMaster(Integer masterId,String videoName){
		return this.baseMapper.getFuzzyNameVideoInMaster(masterId,videoName);
	}


	@Override
	public Long sumVideoLongByIdUser(List<Integer> videoIds, Integer userId) {
		Long l= this.baseMapper.sumVideoLongByIdUser(videoIds, userId);
		if(l==null) {
			l=new Long(0);
		}
		return l;
	}

	public Message getVideosBySubIds(Integer subjectId, Map<String, Object> params, SysSystem sys, HttpServletRequest request) {

		PageParam pageParam = new PageParam(request);
		Integer pageNum = pageParam.getPageNum();
		Integer pageSize=pageParam.getPageSize();
		if (pageNum > 0 && pageSize > 0) {
			Integer masterId = request.getIntHeader("masterId");
			PageHelper.startPage(pageNum, pageSize);
			List<GcVideo> gcVideos = this.baseMapper.selectVideoListBySubId(subjectId);
			Integer userId = (Integer) params.get("userId");
			PageInfo pageInfo = new PageInfo<>(gcVideos);
			List<GcVideo> newVideos = buildVideoInfo(userId, sys, gcVideos,masterId,request, EnvType.GC.getCode());
			pageInfo.setList(newVideos);
			return new Message().ok().addData("videos",pageInfo);
		}else {
			Integer masterId = request.getIntHeader("masterId");
			List<GcVideo> gcVideos = this.baseMapper.selectVideoListBySubId(subjectId);
			Integer userId = (Integer) params.get("userId");
			List<GcVideo> newGcVideos = buildVideoInfo(userId, sys, gcVideos,masterId,request,EnvType.GC.getCode());
			return new Message().ok().addData("videos",newGcVideos);
		}
	}

	public List<GcVideo> getVideoBySubIds(List<Integer> subjectList,Map<String, Object> params, SysSystem sys, HttpServletRequest request){
		List<GcVideo> gcVideos = this.baseMapper.selectVideoListBySubIds(subjectList);
		Integer masterId = request.getIntHeader("masterId");
		List<Integer> userIdList = (List<Integer>) params.get("userId");
		List<GcVideo> newGcVideos = buildVideoInfoByList(userIdList, gcVideos,masterId,false,null,request);
		return newGcVideos;
	}

	@Override
	public void asyncMethodSaveVideo(GcVideo video, HttpServletRequest request) {
		SysFileCaption sysFileCaption1 = new SysFileCaption();
		sysFileCaption1.setLang(video.getLang());
		sysFileCaption1.setVideoId(video.getId());
		sysFileCaption1 = sysFileCaptionService.getCaptionInfo(sysFileCaption1);
		if (null==sysFileCaption1||null==sysFileCaption1.getId()){
			sysFileCaption1 = new SysFileCaption();
			sysFileCaption1.setLang(video.getLang());
			sysFileCaption1.setVideoId(video.getId());
			sysFileCaptionService.saveOrUpdate(sysFileCaption1);
		}

		SysCaptionRequest sysCaptionRequest = new SysCaptionRequest();
		SysFile file = sysFileService.getById(video.getFileId());
		sysCaptionRequest.setFileUrl(sysFileService.getResFullUrl(file,request));
		sysCaptionRequest.setCaptionId(sysFileCaption1.getId());
		sysCaptionRequest.setLanguage(LanuageType.getByCode(video.getLang()).getDesc());
		SysFileCaption sysFileCaption = sysFileCaptionService.callYunMao(sysCaptionRequest);
		sysFileCaption1.setYmCode(sysFileCaption.getYmCode());
		if (null!=sysFileCaption.getYmTaskId()){
			sysFileCaption1.setYmTaskId(sysFileCaption.getYmTaskId());
		}
		sysFileCaptionService.saveOrUpdate(sysFileCaption1);
	}

	@Override
	public void asyncMethodUpdateVideo(GcVideo video, HttpServletRequest request,SysSystem system) {
		List<SysFileCaption> sysFileCaptionList = sysFileCaptionService.selectSysFileCaptionId(video.getId());
		List<String> oldLang = sysFileCaptionList.stream().map(SysFileCaption::getLang).collect(Collectors.toList());

		List<String> langList = video.getTargetLang().toJavaList(String.class);
		List<String> newLangList = new ArrayList<>();
		langList.forEach(i->{
			if (!oldLang.contains(i)){
				newLangList.add(i);
			}
		});
		if (newLangList.size()!=0){
			SysFileCaption caption = new SysFileCaption();
			caption.setNewTargetLangJson(newLangList);
			caption.setLang(video.getLang());
			caption.setVideoId(video.getId());
			MultipartFile file = null;

			SysFileCaption fileCaption = sysFileCaptionService.selectSrtData(video.getId());
			SysFile captionFile = fileService.getInfoById(fileCaption.getCaptionFileId());
			try {
				file = FileUtil.createFileItem(fileService.getResFullUrl(captionFile,request));
			}catch (Exception e){
				e.printStackTrace();
			}
			sysFileCaptionService.asyncTask(caption,file,system);
		}
	}


	@Override
	public List<StudentInfoVO> getStudentSubTimeNum(List<Map<String, Object>> mapList,Integer masterId) {
		List<Integer> userIdList = new ArrayList<>();
		mapList.forEach(i->{
			userIdList.add((Integer) i.get("userId"));
		});
		if (userIdList.size()==0){
			return new ArrayList<>();
		}
		List<Integer> videoIdList = gcUserVideoPlayMapper.getVideoIdsByUserIds(userIdList,masterId);
		if (videoIdList.size()==0){
			return new ArrayList<>();
		}
		return baseMapper.getStudentSubTimeNum(videoIdList,userIdList);
	}


	public List<GcVideo> buildVideoInfoByListUsAge(List<Integer> userIdList,List<GcVideo> gcVideos,Integer masterId,Boolean isAccessId,List<Integer> permissionList,HttpServletRequest request){
		List<GcVideo> newGcVideos = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(gcVideos)){
			List<Integer> videoIds = getVideoIds(gcVideos);
			//总问题数量
			List<GcEvent> eventNum = gcEventService.getEventNumByVideos(videoIds,masterId);

			//查询问题 已回答数量
			List<GcEvent> eventAnswers = null;
			if (isAccessId){
				eventAnswers = gcEventService.selectEventByPermissionList(permissionList, userIdList,masterId);
			}else {
				eventAnswers = gcEventService.findEventAnswerByVideoIdsUserList(videoIds, userIdList,masterId);
			}
			List<GcUserVideoPlay> videoPalyStateByVideos = userVideoPlayService.findVideoPalyStateByVideosUsers(videoIds, userIdList,masterId);

			for (GcVideo gcVideo : gcVideos) {
				SysFile videoFile = gcVideo.getVideoFile();
				if(videoFile != null){
					gcVideo.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(videoFile));//设置视频的路径
					gcVideo.setVideoFile(videoFile);
					Integer videoLong = videoFile.getVideoLong();
					gcVideo.setVideoTime(videoLong == null ? null : videoLong);//视频时长
				}
				gcVideo.setCompleteStatus(TableConstant.VIDEO_COMPLETE_STATUS0);//默认值 防止外面空指针
			}

			for (Integer userId : userIdList) {
				for (GcVideo gcVideo : gcVideos) {
					GcVideo videos = new GcVideo();
					List<GcEvent> answereds = new ArrayList<>();
					if (Objects.nonNull(eventNum)&&Objects.nonNull(eventAnswers)){
						List<GcEvent> eventLists = eventNum.stream().filter(a->a.getVideoId().equals(gcVideo.getId())).collect(Collectors.toList());
						gcVideo.setEventNum(eventLists.size());
						gcVideo.setEventList(eventLists);
						answereds = eventAnswers.stream().filter(a->StringUtils.isNotEmpty(a.getAnswerJson())&&a.getUserId().equals(userId)).collect(Collectors.toList());
						if(CollectionUtils.isNotEmpty(answereds)){
							gcVideo.setAnsweredNums(answereds.size());
						}else {
							gcVideo.setAnsweredNums(TableConstant.COMMON_ZERO);
						}
						gcVideo.setAnsweredSumNums(eventLists.size());//总数
					}
					List<GcEvent> eventList = eventNum.stream().filter(a->a.getVideoId().equals(gcVideo.getId())).collect(Collectors.toList());

					for (GcUserVideoPlay videoPlay : videoPalyStateByVideos) {
						if (videoPlay.getUserId().equals(userId)&&gcVideo.getId().equals(videoPlay.getVideoId())){
							GcUserVideoPlay gcUserVideoPlay = videoPlay;
							if(gcUserVideoPlay != null){
								Integer playState = gcUserVideoPlay.getPlayState();
								gcVideo.setPlayState(playState  == null ? null : String.valueOf(playState));//视频播放状态
								//问题答案添加
								for (GcEvent gcEvent : eventList) {
									for (GcEvent answered : answereds) {
										if (gcEvent.getId().equals(answered.getId())){
											gcEvent.setAnswerJson(answered.getAnswerJson());
										}
									}
								}
								gcVideo.setCompleteStatus(buildCompleteStatus(playState, answereds,eventList,request,EnvType.GC.getCode()));
							}
						}
					}
					gcVideo.setUserId(userId);
					BeanUtils.copyProperties(gcVideo,videos);
					newGcVideos.add(videos);
				}
			}
		}
		return newGcVideos;
	}

	@Override
	public List<GcVideo> buildVideoInfoByList(List<Integer> userIdList,List<GcVideo> gcVideos,Integer masterId,Boolean isAccessId,List<Integer> permissionList,HttpServletRequest request){
		List<GcVideo> newGcVideos = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(gcVideos)){
			List<Integer> videoIds = getVideoIds(gcVideos);

			Map<String, Object> videoParams = new HashMap<>();
			videoParams.put("contentIds",videoIds);
			videoParams.put("type",1);
			//查询点赞
			Map<Integer, List<GcUserVideoAction>> videoMap = videoActionService.getVideoActionBySubject(videoParams);
			// 查询评论 根据视频id查询评论 查询评论数，不用加userID
			List<GcVideoComment> commentVideoIds = gcVideoCommentService.getVideoComments(videoIds,masterId);
			Map<Integer, List<GcVideoComment>> commentVideoMap = new HashMap<>(commentVideoIds.size());
			if(CollectionUtils.isNotEmpty(commentVideoIds)){
				commentVideoMap = commentVideoIds.stream().collect(Collectors.groupingBy(GcVideoComment::getVideoId));
			}
			//总问题数量
			List<GcEvent> eventNum = gcEventService.getEventNumByVideos(videoIds,masterId);

			//查询问题 已回答数量
			List<GcEvent> eventAnswers = null;
			if (isAccessId){
				eventAnswers = gcEventService.selectEventByPermissionList(permissionList, userIdList,masterId);
			}else {
				eventAnswers = gcEventService.findEventAnswerByVideoIdsUserList(videoIds, userIdList,masterId);
			}

			List<GcUserVideoPlay> videoPalyStateByVideos = userVideoPlayService.findVideoPalyStateByVideosUsers(videoIds, userIdList,masterId);

			for (Integer userId : userIdList) {
				for (GcVideo gcVideo : gcVideos) {
					gcVideo.setPlayState(null);
					GcVideo videos = new GcVideo();
					Integer id = gcVideo.getId();
					//设置视频点赞数
					List<GcUserVideoAction> likes = videoMap.get(id);
					if(CollectionUtils.isNotEmpty(likes)){
						gcVideo.setLikeNum(likes.size());
					}
					//设置评论数和评论列表
					List<GcVideoComment> gcVideoComments = commentVideoMap.get(id);
					if(CollectionUtils.isNotEmpty(gcVideoComments)){
						gcVideo.setCommentNum(gcVideoComments.size());//评论数量
					}
					List<GcEvent> answereds = new ArrayList<>();
					if (Objects.nonNull(eventNum)&&Objects.nonNull(eventAnswers)){
						List<GcEvent> eventLists = eventNum.stream().filter(a->a.getVideoId().equals(gcVideo.getId())).collect(Collectors.toList());
						gcVideo.setEventNum(eventLists.size());
						gcVideo.setEventList(eventLists);
						answereds = eventAnswers.stream().filter(a->StringUtils.isNotEmpty(a.getAnswerJson())&&a.getUserId().equals(userId)&&a.getVideoId().equals(gcVideo.getId())).collect(Collectors.toList());
						if(CollectionUtils.isNotEmpty(answereds)){
							gcVideo.setAnsweredNums(answereds.size());
						}else {
							gcVideo.setAnsweredNums(TableConstant.COMMON_ZERO);
						}
						gcVideo.setAnsweredSumNums(eventLists.size());//总数
					}
					SysFile videoFile = gcVideo.getVideoFile();
					if(videoFile != null){
						gcVideo.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(gcVideo));//设置视频的路径
						Integer videoLong = videoFile.getVideoLong();
						gcVideo.setVideoTime(videoLong);//视频时长
					}
					gcVideo.setCompleteStatus(TableConstant.VIDEO_COMPLETE_STATUS0);//默认值 防止外面空指针
					List<GcEvent> eventList = eventNum.stream().filter(a->a.getVideoId().equals(gcVideo.getId())).collect(Collectors.toList());
					eventList.forEach(i->{
						i.setAnswerJson(null);
					});
					for (GcUserVideoPlay videoPlay : videoPalyStateByVideos) {
						if (videoPlay.getUserId().equals(userId)&&gcVideo.getId().equals(videoPlay.getVideoId())){
							GcUserVideoPlay gcUserVideoPlay = videoPlay;
							if(gcUserVideoPlay != null){
								Integer playState = gcUserVideoPlay.getPlayState();
								gcVideo.setPlayState(playState  == null ? null : String.valueOf(playState));//视频播放状态
								//问题答案添加
								for (GcEvent gcEvent : eventList) {
									for (GcEvent answered : answereds) {
										if (gcEvent.getId().equals(answered.getId())){
											gcEvent.setAnswerJson(answered.getAnswerJson());
										}
									}
								}
								gcVideo.setCompleteStatus(buildCompleteStatus(playState, answereds,eventList,request,EnvType.GC.getCode()));
							}
						}
					}
					gcVideo.setUserId(userId);
					BeanUtils.copyProperties(gcVideo,videos);
					newGcVideos.add(videos);
				}
			}
		}
		return newGcVideos;
	}

	@Override
	public List<GcVideo> selectVideoPlayListBySubId(Integer subId, Integer userId) {
		return this.baseMapper.selectVideoPlayListBySubId(subId,userId);
	}

	@Override
	public GcVideo selectVideoPlayByVideo(Integer video, Integer userId) {
		return this.baseMapper.selectVideoPlayByVideo(video,userId);
	}

	@Override
	public boolean saveVideoInfo(SysSystem sys, GcVideo video, Integer masterId, HttpServletRequest request) {
		if (null != video.getId()) {
			Message message = new Message();

			this.saveOrUpdate(video);
			SysFile file = new SysFile();
			if (Objects.nonNull(video.getFileId())) {
				file = sysFileService.getVideoFile(video, request);
				video.setVideoTime(file.getVideoLong());
				video.setThumbnailUrl(file.getThumbNailUrl());
			}

			populateVideoEvents(video, masterId, message);

			if (CollectionUtils.isNotEmpty(video.getTargetLang())) {
				file.setTargetLangJson(video.getTargetLang());
				sysFileService.saveOrUpdate(file);
				sysFileCaptionService.updateCaptionState(video.getTargetLang(), video.getId());
				asyncMethodUpdateVideo(video, request, sys);
			}

			return true;
		}
		ApiAssert.ifStringNotInList(video.getVideoName(), CommonConstant.defaultNoCourseOrVideName,
			"The video name is incorrect and the value cannot be used");
		if (video.getFileId() != null) {
			ApiAssert.jsonValueIntegerIn(video.getVideoSource(), VideoConstant.GCVIDEO_VIDEOSOURCE_jsonStr,
				"VideoSource值错误，必须为: " + VideoConstant.GCVIDEO_VIDEOSOURCE_jsonStr);
			SysFile file = sysFileService.getById(video.getFileId());
			if (file == null) {
				throw new SystemException("The video id does not exist");
			}
			String url = sysFileService.getResFullUrl(file, request);
			video.setVideoFullUrl(url);
			video.setVideoFile(file);
			video.setThumbnailUrl(file.getThumbNailUrl());
			video.setVideoTime(file.getVideoLong());
            video.setOriginCourseId(getOriginCourseId(Collections.singletonList(video)));
		} else {
			// Save lesson via video link
			ApiAssert.notNull(video.getVideoSource(), "The video source cannot be null");
		}
		if (video.getVideoName() == null || video.getSubId() == null) {
			throw new SystemException("The video name and course id cannot be empty");
		}
		List<GcVideo> videoList = this.getVideoListBySubId(video.getSubId());
		if (CollectionUtils.isNotEmpty(videoList) && null == video.getId()) {
			int max = videoList.stream().mapToInt(GcVideo::getOrder).max().getAsInt();
			video.setOrder(max + 1);
		}
		boolean savedSuccessfully = this.saveVideo(video);
		// New videos cancel the previously completed course progress
		subjectCompleteService.updateStateByVideoId(video.getId(), masterId);

		SysFile sysFile = new SysFile();
		if (null != video.getIfCaption() && TableConstant.COMMON_ONE == video.getIfCaption()) {
			this.asyncMethodSaveVideo(video, request);
			sysFile.setIfCaption(video.getIfCaption().toString());
		}
		sysFile.setName(video.getVideoName());
		sysFile.setId(video.getFileId());
		sysFile.setTargetLangJson(video.getTargetLang());
		sysFileService.updateById(sysFile);
		Integer fileTypeIndex = sysFileService.selectFileTypeIndexByVideoId(video.getId());
		video.setFileTypeIndex(fileTypeIndex);

		SysFile newVideoFile = sysFileService.getById(video.getFileId());
		String fullFileUrl = sysFileService.getResFullUrl(newVideoFile, request);
		newVideoFile.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(video));
		newVideoFile.setFullFileUrl(fullFileUrl);
		video.setVideoFile(newVideoFile);
		updateCourseTags(Collections.singletonList(video), masterId);

		return savedSuccessfully;
	}

	private void populateVideoEvents(GcVideo video, Integer masterId, Message message) {
		List<GcEvent> eventList = eventService.getEventListByVid(video.getId(), masterId);
		if(CollectionUtils.isNotEmpty(eventList)){
			message.addData("eventNum",eventList.size());
		}
		List<GcResource>  resources = resourceService.getResByVid(video.getId());
		if(CollectionUtils.isNotEmpty(resources)){
			message.addData("resourceNum",resources.size());
		}
	}

	private List<PtTags> createCourseTags(GcVideo video, Integer masterId) {
		List<String> tagList = video.getCourseTags().toJavaList(String.class);
		return tagList.stream()
			.map(tag -> createCourseTag(masterId, tag, video.getId()))
			.collect(Collectors.toList());
	}

	private PtTags createCourseTag(Integer masterId, String tagText, Integer videoId) {
		PtTags courseTag = new PtTags();
		courseTag.setMasterId(masterId);
		courseTag.setTagText(tagText);
		courseTag.setVideoId(videoId);
		courseTag.setType(TableConstant.COMMON_TWO);
		courseTag.setOrder(TableConstant.COMMON_ZERO);
		return courseTag;
	}

	@Override
	public List<Integer> getIdsBySubIds(List<Integer> subIds) {
		return this.baseMapper.getIdsBySubIds(subIds);
	}

	@Override
	public Map<Integer, videoLongVo> getVideoLongMapBySubjectId(List<Integer> subjectIds) {
		Map<Integer, videoLongVo> map = new HashMap<>();
		if (TableConstant.COMMON_ZERO!=subjectIds.size()){
			List<videoLongVo> videoLongVos = this.baseMapper.getVideoLongMapBySubjectId(subjectIds);
			map = videoLongVos.stream().collect(Collectors.toMap(videoLongVo::getSubId,videoLongVo -> videoLongVo, (key1, key2) -> key2, LinkedHashMap::new));
		}
		return map;
	}

	@Override
	@Transactional
	public void saveChannelContent(List<PtChannelContent> ptChannelContent, Integer originChannelId) {
		List<GcVideo> channelVideoContent = ptChannelContent.stream()
			.map(channelContent -> createChannelVideoContent(channelContent, originChannelId))
			.collect(Collectors.toList());

		saveBatch(channelVideoContent);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<GcVideo> getVideoContent(Integer fileId) {
		return Optional.ofNullable(baseMapper.getVideoContentByFileId(fileId));
	}

	@Override
	public GcVideo getVideoContentByFileId(Integer fileId) {
		return baseMapper.getVideoContentByFileId(fileId);
	}


	@Override
	public void updateVideoFilePrivacy(SysFile videoFile, GcVideo video) {
		if (video == null) {
			return;
		}

		videoFile.setIsPrivate(video.isPrivate());
	}

	@Override
	public VideoSearchResponseDto getVideoListByQuery(VideoListFilterDto filter, PortalUser portalUser,
													  HttpServletRequest request) {
		List<GcVideo> videos = this.baseMapper.getVideoListByQuery(filter, portalUser.getMasterId());

		List<VideoSearchResultDto> result = getChannelOriginVideoListResult(request, videos);
		result.addAll(getCourseOriginVideoListResult(request, videos));

		if (filter.getSortBy() == null) {
			return createVideoSearchResponse(result);
		}

		Map<Integer, String> videoIdAnalytics =
			analyticsFacade.getVideoIdAnalytics(videoMapping.mapFilter(filter, getVideoIds(videos)), filter.getSortBy(), portalUser);
		return createVideoSearchResponse(populateSortedByValue(result, videoIdAnalytics, filter));
	}

	@Override
	@Transactional
	public boolean createVideos(List<GcVideo> videoList, HttpServletRequest request) {
        Integer originCourseId = getOriginCourseId(videoList);

		for (GcVideo video : videoList) {
			ApiAssert.notNull(video.getVideoName(), "Video name cannot be empty");
			ApiAssert.notNull(video.getFileId(), "The file id cannot be empty");

			SysFile file = sysFileService.getById(video.getFileId());
			if (file == null) {
				log.error("Failed to save videos for course {}. File not found, file id: {}", originCourseId,
					video.getFileId());
				return false;
			}
			updateVideo(originCourseId, request, video, file);
		}
		return saveOrUpdateBatch(videoList);
	}

	private void updateVideo(Integer originCourseId, HttpServletRequest request, GcVideo video, SysFile file) {
		video.setVideoName(stringWidthConvertUtil.stringWidthConvert(video.getVideoName()));
		video.setVideoFullUrl(sysFileService.getResFullUrl(file, request));
		video.setSubId0(originCourseId);
		video.setOriginCourseId(originCourseId);
		video.setThumbnailUrl(file.getThumbNailUrl());
		video.setVideoTime(file.getVideoLong());
	}

	@Override
	public void updateCourseTags(List<GcVideo> videoList, Integer masterId) {
		if (videoList.isEmpty()) {
			return;
		}

		List<PtTags> tags = videoList.stream()
			.filter(video -> video.getCourseTags() != null)
			.flatMap(video -> createCourseTags(video, masterId).stream())
			.collect(Collectors.toList());

		removeCourseTags(masterId, getVideoIds(videoList));

		if (!tags.isEmpty()) {
			ptTagsService.saveOrUpdateBatch(tags);
		}
	}

	@Override
	public List<DbAnalyticsResultVideoIdDto> getLikesByVideoAnalytics(AnalyticsFilterDto filter, Integer masterId) {
		return this.baseMapper.getLikesByVideoAnalytics(filter, masterId);
	}

	@Override
	public List<GcVideo> findByVideoIds(List<Integer> videoIds) {
		if (CollectionUtils.isEmpty(videoIds)) {
			return List.of();
		}

		return this.baseMapper.findByVideoIds(videoIds);
	}

	@Override
	public List<Integer> getVideoIdsByChannelIds(List<Integer> channelIds) {
		if (CollectionUtils.isEmpty(channelIds)) {
			return List.of();
		}

		return this.baseMapper.getVideoIdsByChannelIds(channelIds);
	}

	private void syncVideoInformationWithFile(GcVideo video, SysFile file) {
		String videoThumbnailUrl = video.getThumbnailUrl();
		String fileThumbnailUrl = file.getThumbNailUrl();
		if (fileThumbnailUrl == null || fileThumbnailUrl.equals(videoThumbnailUrl)) {
			return;
		}
		video.setThumbnailUrl(fileThumbnailUrl);
		updateById(video);
	}

	@Override
	public SysFile updateVideoFile(HttpServletRequest request, GcVideo video, PortalUser portalUser) {
		Integer contentId = video.getId();
		SysFile videoFile = sysFileService.getById(video.getFileId());
		sysFileService.updateVideoInformation(videoFile, portalUser);
		video.setVideoFile(videoFile);
		syncVideoInformationWithFile(video, videoFile);
		String snapShotUrl = sysFileService.getVideoSnapshotUrl(video);
		String fullFileUrl = sysFileService.getVideoPlayerUrl(videoFile, request);
		videoFile.setFullFileUrl(fullFileUrl);
		videoFile.setSnapshotUrl(snapShotUrl);
		videoFile.setVideoId(contentId);
		videoFile.setIsLiked(videoActionService.isLikedByUser(contentId, portalUser.getUserId()) ? 1 : 0);
		videoFile.setLikeNum(videoActionService.countLikeForVideo(contentId));
		updateVideoFilePrivacy(videoFile, video);
		Map<String, Boolean> permissions = authorizationService.listPermissions(video, portalUser);
		video.setPermissions(permissions);
		video.getVideoFile().setPermissions(permissions);
		return videoFile;
	}

	@Override
	public VideoDto getVideo(Integer videoId, PortalUser portalUser, HttpServletRequest request) {
		GcVideo video = findByVideoId(videoId);
		if (!authorizationService.checkAccess(video, PermitAction.VIEW, portalUser)) {
			throw new ForbiddenException("No permission to view the video");
		}

		updateVideoFile(request, video, portalUser);
		return videoMapping.map(video);
	}

	@Override
	public List<GcVideo> findSubscribedPlaylistsLatestVideos(PortalUser portalUser, CursorDto cursor) {
		List<GcVideo> latestVideos =
			baseMapper.findLatestUserSubscribedPlaylistsVideos(portalUser, cursor);
		populateVideoData(latestVideos, portalUser);

		return latestVideos;
	}

	@Override
	public List<GcVideo> findPlaylistLatestVideos(Integer playlistId, PortalUser portalUser) {
		List<GcVideo> latestVideos = baseMapper.findPlaylistLatestVideos(playlistId);
		populateVideoData(latestVideos, portalUser);

		return latestVideos;
	}

	@Override
	public List<GcVideo> channelLatestVideos(Integer channelId, PortalUser portalUser) {
		List<GcVideo> latestVideos = baseMapper.findLatestChannelVideos(channelId);
		populateVideoData(latestVideos, portalUser);
		return latestVideos;
	}

	@Override
	public List<GcVideo> subscribedLatestChannelVideos(PortalUser portalUser) {
		List<GcVideo> latestVideos = baseMapper.findSubscribedLatestChannelVideos(portalUser.getUserId(), portalUser.getMasterId());
		populateVideoData(latestVideos, portalUser);
		return latestVideos;
	}

	@Override
	public Integer countPlaylistLatestVideos(PortalUser portalUser) {
		return baseMapper.countLatestUserSubscribedPlaylistVideos(portalUser);
	}

	@Override
	public void populateVideoData(List<GcVideo> videos, PortalUser portalUser) {
		videos.forEach(video -> {
			video.setIsLiked(videoActionService.isLikedByUser(video.getId(), portalUser.getUserId()) ? 1 : 0);
			video.setLikeNum(videoActionService.countLikeForVideo(video.getId()));
			video.setPermissions(authorizationService.listPermissions(video, portalUser));
			video.setViewsCount(videoPlaySessionService.getVideoViewsCount(video.getId(), portalUser.getMasterId()));
			updateVideoUrls(video);
		});
	}

	@Override
	public List<Integer> getVideoOriginSubscriberIds(GcVideo video, Integer masterId) {
		PtChannel originChannel = video.getOriginChannel();
		if (originChannel != null) {
			List<PtChannelSubscribe> channelSubscribes =
				channelSubscribeService.getChannelSubscribes(originChannel.getId());
			return channelSubscribes.stream()
				.map(PtChannelSubscribe::getUserId)
				.collect(Collectors.toList());
		}

		return List.of();
	}

	@Override
	public GcVideo findByVideoId(Integer videoId) {
		List<GcVideo> videos = this.baseMapper.findByVideoIds(List.of(videoId));
		if (CollectionUtils.isEmpty(videos)) {
			return null;
		}

		return videos.get(0);
	}

	private void removeCourseTags(Integer masterId, List<Integer> videoIds) {
		QueryWrapper<PtTags> queryWrapper = new QueryWrapper<>();
		queryWrapper.in("video_id", videoIds);
		queryWrapper.in("master_id", masterId);
		queryWrapper.in("type", TableConstant.COMMON_TWO);
		ptTagsService.remove(queryWrapper);
	}

    private Integer getOriginCourseId(List<GcVideo> videoList) {
        if (videoList.isEmpty()) {
            return null;
        }
        Integer courseId = videoList.get(TableConstant.COMMON_ZERO).getSubId();
        Integer originCourseId = subjectService.getById(courseId).getFid();

        return originCourseId == null ? courseId : originCourseId;
    }

	private void updateVideoUrls(HttpServletRequest request, GcVideo video) {
		SysFile videoFile = video.getVideoFile();
		String fullFileUrl = sysFileService.getResFullUrl(videoFile, request);
		String snapShotUrl = sysFileService.getVideoSnapshotUrl(videoFile);

		videoFile.setSnapshotUrl(snapShotUrl);
		videoFile.setThumbNailUrl(snapShotUrl);
		videoFile.setFullFileUrl(fullFileUrl);
		video.setThumbnailUrl(videoThumbnailProvider.getThumbnailUrl(videoFile));
	}

	private void updateVideoUrls(GcVideo video) {
		SysFile videoFile = video.getVideoFile();

		videoFile.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(videoFile));
		videoFile.setFullFileUrl(sysFileService.getFullFileUrl(videoFile.getFileUrl()));

		String thumbnailUrl = videoThumbnailProvider.getThumbnailUrl(videoFile);
		video.setThumbnailUrl(sysFileService.getFullFileUrl(thumbnailUrl));
		video.setSnapshotUrl(sysFileService.getFullFileUrl(thumbnailUrl));

		if (video.getOriginCourse() != null && video.getOriginCourse().getSubImgFile() != null) {
			video.getOriginCourse().getSubImgFile().setFullFileUrl(
				sysFileService.getFullFileUrl(video.getOriginCourse().getSubImgFile().getFileUrl()));
		}
		if (video.getOriginChannel() != null && video.getOriginChannel().getAvatarFile() != null) {
			video.getOriginChannel().getAvatarFile().setFullFileUrl(
				sysFileService.getFullFileUrl(video.getOriginChannel().getAvatarFile().getFileUrl()));
		}
	}

	private VideoSearchResponseDto createVideoSearchResponse(List<VideoSearchResultDto> searchResult) {
		VideoSearchResponseDto response = new VideoSearchResponseDto();
		response.setResult(searchResult);

		return response;
	}

	private GcVideo createChannelVideoContent(PtChannelContent channelContent, Integer originChannelId) {
		SysFile videoFile = channelContent.getVideoFile();

		if (videoFile == null) {
			return null;
		}

		GcVideo gcVideo = new GcVideo();
		gcVideo.setVideoName(videoFile.getName());
		gcVideo.setVideoDesc(videoFile.getDescription());
		gcVideo.setOrder(channelContent.getContentOrder());
		gcVideo.setFileId(videoFile.getId());
		gcVideo.setThumbnailUrl(videoFile.getThumbNailUrl());
		gcVideo.setVideoTime(videoFile.getVideoLong());
		gcVideo.setOriginChannelId(originChannelId);

		return gcVideo;
	}

	@Override
	public List<DbAnalyticsResultDto> getVideoCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
		return this.baseMapper.getVideoCountAnalytics(filter, masterId);
	}

	@Override
	public List<GcVideo> buildVideoInfo(Integer userId, SysSystem sys, List<GcVideo> gcVideos,Integer masterId,HttpServletRequest request,Integer envFlag) {
        if (!CollectionUtils.isNotEmpty(gcVideos)) {
            return new ArrayList<>();
        }

        List<Integer> videoIds = getVideoIds(gcVideos);
        Map<String, Object> videoParams = new HashMap<>(3);
        videoParams.put("contentIds", videoIds);
        videoParams.put("type", 1);//点赞
        //查询点赞
        Map<Integer, List<GcUserVideoAction>> videoMap = videoActionService.getVideoActionBySubject(videoParams);
        // 查询评论 根据视频id查询评论 评论列表单独接口
        List<GcVideoComment> commentVideoIds = gcVideoCommentService.getVideoComments(videoIds,masterId);//查询评论数，不用加userID
        Map<Integer, List<GcVideoComment>> commentVideoMap = new HashMap<>(commentVideoIds.size());
        if(CollectionUtils.isNotEmpty(commentVideoIds)){
            commentVideoMap = commentVideoIds.stream().collect(Collectors.groupingBy(GcVideoComment::getVideoId));
        }
        //查询问题 已回答/总问题数
        List<GcEvent> eventAnswers = gcEventService.findEventAnswerByVideoIdsUser(videoIds, userId,masterId,envFlag);
        Map<Integer, List<GcEvent>> eventAnswerMap = new HashMap<>(0);
        if(CollectionUtils.isNotEmpty(eventAnswers)){
            eventAnswerMap = eventAnswers.stream().collect(Collectors.groupingBy(GcEvent::getVideoId));
        }

        Map<Integer, GcUserVideoPlay> videoPalyStateByVideos = new HashMap<>();
        if(null!=userId) {
            videoPalyStateByVideos = userVideoPlayService.findVideoPalyStateByVideos(videoIds, userId, masterId);
        }
        Map<Integer,Object> videoPlayCount = userVideoPlayService.getVideoPlayCount(videoIds);

        //查询单个视频播放进度
        List<GcEvent> events = gcEventService.getEventListByVideoIds(videoIds,userId);
        Map<Integer,List<GcEvent>> eventmap = events.stream().collect(Collectors.groupingBy(GcEvent::getVideoId));
        List<GcVideo> newGcVideos = new ArrayList<>(gcVideos.size());
        for (GcVideo video : gcVideos){
            if (Objects.nonNull(videoPlayCount.get(video.getId()))){
                Map map = (Map)videoPlayCount.get(video.getId());
                video.setViewsCount(Integer.parseInt(map.get("countnum").toString()));
            }

            List<GcEvent> eventList = eventmap.get(video.getId());
            Integer id = video.getId();
            //设置视频点赞数
            List<GcUserVideoAction> likes = videoMap.get(id);
            if(CollectionUtils.isNotEmpty(likes)){
                video.setLikeNum(likes.size());
            }
            //设置评论数和评论列表
            List<GcVideoComment> gcVideoComments = commentVideoMap.get(id);
            if(CollectionUtils.isNotEmpty(gcVideoComments)){
                video.setCommentNum(gcVideoComments.size());//评论数量
            }
            //设置已回答的问题和问题总数
            List<GcEvent> answers = eventAnswerMap.get(id);
            if(CollectionUtils.isNotEmpty(answers)){
                video.setEventList(answers);
                List<GcEvent> answereds = answers.stream().filter(a -> StringUtils.isNotEmpty(a.getAnswerJson())).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(answereds)){
                    video.setAnsweredNums(answereds.size());
                }
            }
            if (CollectionUtils.isNotEmpty(eventList)) {
                video.setAnsweredSumNums(eventList.size());
                video.setEventNum(eventList.size());
            }
            SysFile videoFile = video.getVideoFile();
            if(videoFile != null){
                video.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(video));
                video.getVideoFile().setFullFileUrl(sysFileService.getResFullUrl(videoFile,request));
                video.setVideoTime(videoFile.getVideoLong());
                videoFile.setVideoId(video.getId());
            }
            // 改成在外面查出来，在这里set
            GcUserVideoPlay gcUserVideoPlay = videoPalyStateByVideos.get(id);
            video.setCompleteStatus(TableConstant.VIDEO_COMPLETE_STATUS0);//默认值 防止外面空指针
            if(gcUserVideoPlay != null){
                Integer playState = gcUserVideoPlay.getPlayState();
                video.setPlayState(playState  == null ? null : String.valueOf(playState));//视频播放状态
                video.setCompleteStatus(buildCompleteStatus(playState,  answers,eventList,request,envFlag));
            }

            newGcVideos.add(video);
        }
        return newGcVideos;
    }

	private short buildCompleteStatus(Integer playState, List<GcEvent> answers,List<GcEvent> eventList,HttpServletRequest request,Integer envFlag) {
		if(playState == null){//没有播放记录
			return TableConstant.VIDEO_COMPLETE_STATUS0;
		}
			//pt环境
			if(CollectionUtils.isNotEmpty(eventList)){
				if(playState==1 && CollectionUtils.isNotEmpty(answers)) {
					List<GcEvent> answereds = answers.stream().filter(a -> StringUtils.isNotEmpty(a.getAnswerJson())).collect(Collectors.toList());
					if (1 == playState && (CollectionUtils.isNotEmpty(answereds) && answers.size() == eventList.size())) {
						return TableConstant.VIDEO_COMPLETE_STATUS2;
					} else if (1 == playState && (CollectionUtils.isNotEmpty(answereds) && answers.size() < eventList.size())) {
						return TableConstant.VIDEO_COMPLETE_STATUS1;
					}
				}else if(playState == 0 && CollectionUtils.isEmpty(answers)){
					return TableConstant.VIDEO_COMPLETE_STATUS0;
				}else {
					return TableConstant.VIDEO_COMPLETE_STATUS1;
				}
			}else{
				//视频没有问题的情况
				if(playState==1){
					return TableConstant.VIDEO_COMPLETE_STATUS2;
				}else {
					return TableConstant.VIDEO_COMPLETE_STATUS0;
				}
			}

		return TableConstant.VIDEO_COMPLETE_STATUS2;
	}

	@Override
	public Long sumPlayVideoLongByIdUser(List<Integer> videoIds, int userId) {
		Long l= this.baseMapper.sumPlayVideoLongByIdUser(videoIds, userId);
		if(l==null) {
			l=new Long(0);
		}
		return l;
	}

	@Override
	public  List<GcVideo> getVideosBySubjectIds0(List<Integer> subjectIds, Integer userId,Integer masterId,HttpServletRequest request,Integer envFlag) {
		if(CollectionUtils.isNotEmpty(subjectIds)){
			List<Integer> gcVideoIdSubList = subjectService.countSessions(subjectIds);
			List<GcVideo> gcVideoss = null;
			if (null!=gcVideoIdSubList&&gcVideoIdSubList.size()!=0){
				gcVideoss = this.baseMapper.selectVideosSubIds(gcVideoIdSubList);
			}
			List<GcVideo> gcVideos = this.buildVideoInfo(userId, null, gcVideoss,masterId,request,envFlag);
			return gcVideos;
		}
		return new ArrayList<GcVideo>(0);
	}

	@Override
	public  List<GcVideo> getVideoIdListBySubId0(List<Integer> subIds,Integer userId,Integer masterId,HttpServletRequest request,Integer envFlag) {
		if(CollectionUtils.isNotEmpty(subIds)){
			List<GcVideo> gcVideoss = this.baseMapper.getVideoIdListBySubId0(subIds);//公共方法，查询出来在进行groupby
			List<GcVideo> gcVideos = this.buildVideoInfo(userId, null, gcVideoss,masterId,request,envFlag);
			return gcVideos;
		}
		return new ArrayList<GcVideo>(0);

	}
	@Override
	public  List<GcVideo> getVideoIdListByAccessId0(List<Integer> accessPermissionId,List<Integer> userId,Integer masterId,HttpServletRequest request) {
		if (CollectionUtils.isNotEmpty(accessPermissionId)){
			List<GcVideo> gcVideos = this.baseMapper.getVideoIdListByPermissionId(accessPermissionId);
			List<GcVideo> gcVideoList = this.buildVideoInfoByList(userId,gcVideos,masterId,true,accessPermissionId,request);
			return gcVideoList;
		}
		return null;
	}

	@Override
	public List<GcVideo> getVideoListByUserIdAndSubject(List<Integer> userId,Integer subjectId,Integer masterId,HttpServletRequest request){
		List<GcVideo> videos = this.baseMapper.getVideoListByUserIdsAndSubjectId(userId,subjectId,masterId);
		List<GcVideo> gcVideoList = this.buildVideoInfoByListPt(subjectId,userId,videos,masterId,request);
		return gcVideoList;
	}


	public List<GcVideo> buildVideoInfoByListPt(Integer subjectId,List<Integer> userIdList,List<GcVideo> gcVideos,Integer masterId,HttpServletRequest request){
		List<GcVideo> newGcVideos = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(gcVideos)){
			List<Integer> videoIds = getVideoIds(gcVideos);

			Map<String, Object> videoParams = new HashMap<>();
			videoParams.put("contentIds",videoIds);
			videoParams.put("type",1);
			//查询点赞
			Map<Integer, List<GcUserVideoAction>> videoMap = videoActionService.getVideoActionBySubject(videoParams);
			// 查询评论 根据视频id查询评论 查询评论数，不用加userID
			List<GcVideoComment> commentVideoIds = gcVideoCommentService.getVideoComments(videoIds,masterId);
			Map<Integer, List<GcVideoComment>> commentVideoMap = new HashMap<>(commentVideoIds.size());
			if(CollectionUtils.isNotEmpty(commentVideoIds)){
				commentVideoMap = commentVideoIds.stream().collect(Collectors.groupingBy(GcVideoComment::getVideoId));
			}
			//总问题数量
			List<GcEvent> eventNum = gcEventService.getEventNumByVideos(videoIds,masterId);

			//查询问题 已回答数量
			List<GcEvent> eventAnswers = gcEventService.selectEventBySubjectIdUserIds(userIdList,subjectId,masterId);

			List<GcUserVideoPlay> videoPalyStateByVideos = userVideoPlayService.findVideoPalyStateByVideosUsers(videoIds, userIdList,masterId);

			for (Integer userId : userIdList) {
				for (GcVideo gcVideo : gcVideos) {
					gcVideo.setPlayState(null);
					GcVideo videos = new GcVideo();
					Integer id = gcVideo.getId();
					//设置视频点赞数
					List<GcUserVideoAction> likes = videoMap.get(id);
					if(CollectionUtils.isNotEmpty(likes)){
						gcVideo.setLikeNum(likes.size());
					}
					//设置评论数和评论列表
					List<GcVideoComment> gcVideoComments = commentVideoMap.get(id);
					if(CollectionUtils.isNotEmpty(gcVideoComments)){
						gcVideo.setCommentNum(gcVideoComments.size());//评论数量
					}
					List<GcEvent> answereds = new ArrayList<>();
					if (Objects.nonNull(eventNum)&&Objects.nonNull(eventAnswers)){
						List<GcEvent> eventLists = eventNum.stream().filter(a->a.getVideoId().equals(gcVideo.getId())).collect(Collectors.toList());
						gcVideo.setEventNum(eventLists.size());
						gcVideo.setEventList(eventLists);
						answereds = eventAnswers.stream().filter(a->StringUtils.isNotEmpty(a.getAnswerJson())&&a.getUserId().equals(userId)&&a.getVideoId().equals(gcVideo.getId())).collect(Collectors.toList());
						if(CollectionUtils.isNotEmpty(answereds)){
							gcVideo.setAnsweredNums(answereds.size());
						}else {
							gcVideo.setAnsweredNums(TableConstant.COMMON_ZERO);
						}
						gcVideo.setAnsweredSumNums(eventLists.size());//总数
					}
					SysFile videoFile = gcVideo.getVideoFile();
					if(videoFile != null){
						gcVideo.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(gcVideo));
						gcVideo.setVideoTime(videoFile.getVideoLong());
					}
					gcVideo.setCompleteStatus(TableConstant.VIDEO_COMPLETE_STATUS0);//默认值 防止外面空指针
					List<GcEvent> eventList = eventNum.stream().filter(a->a.getVideoId().equals(gcVideo.getId())).collect(Collectors.toList());
					eventList.forEach(i->{
						i.setAnswerJson(null);
					});
					for (GcUserVideoPlay videoPlay : videoPalyStateByVideos) {
						if (videoPlay.getUserId().equals(userId)&&gcVideo.getId().equals(videoPlay.getVideoId())){
							GcUserVideoPlay gcUserVideoPlay = videoPlay;
							if(gcUserVideoPlay != null){
								Integer playState = gcUserVideoPlay.getPlayState();
								gcVideo.setPlayState(playState  == null ? null : String.valueOf(playState));//视频播放状态
								//问题答案添加
								for (GcEvent gcEvent : eventList) {
									for (GcEvent answered : answereds) {
										if (gcEvent.getId().equals(answered.getId())){
											gcEvent.setAnswerJson(answered.getAnswerJson());
										}
									}
								}
								gcVideo.setCompleteStatus(buildCompleteStatus(playState, answereds,eventList,request,EnvType.GC.getCode()));
							}
						}
					}
					//gcVideo.setCompleteStatus(buildCompleteStatus(Integer.parseInt(gcVideo.getPlayState()),  eventList));
					gcVideo.setUserId(userId);
					BeanUtils.copyProperties(gcVideo,videos);
					newGcVideos.add(videos);
				}
			}
		}
		return newGcVideos;
	}

	@Override
	public PageInfo<GcVideo> page(Map<String, Object> searchParameters, SysSystem system, HttpServletRequest request) {
		int pageNum = 1;
		int pageSize = 10;

		try {
			pageNum = Integer.parseInt(searchParameters.get("pageNum") == null ? "1": searchParameters.get("pageNum").toString());
			pageSize = Integer.parseInt(searchParameters.get("pageSize") == null ? "10": searchParameters.get("pageSize").toString());
		} catch (Exception ignored) {}

		Integer masterId = RequestUtil.getMasterId(request).orElseThrow(() -> new SystemException(I18NUtil.get("powtoon.portal.id.notfound")));
		searchParameters.put("masterId", masterId);

		Page<GcVideo> page = PageHelper.startPage(pageNum, pageSize, true);
		List<GcVideo> videos = this.baseMapper.searchVideo(searchParameters);

		PageInfo<GcVideo> pageInfo = new PageInfo<>(page);
		pageInfo.setList(videos);
		List<Integer> videoFileIds = videos.stream().map(GcVideo::getFileId).collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(videoFileIds)) {
			Map<Integer, SysFile> idToVideoFile = fileService.getFilesUploadByFileIds(videoFileIds);

			for (GcVideo gcVideo : videos) {
                if (idToVideoFile == null) {
                    continue;
                }

                SysFile videoFile = idToVideoFile.get(gcVideo.getVideoFile().getId());
                if (videoFile != null) {
                    gcVideo.getVideoFile().setGcUser(videoFile.getGcUser());
                }
            }
		}

		Integer userId = (Integer) searchParameters.get("userId");
		pageInfo.setList(this.buildVideoInfo(userId, system, videos, masterId, request, EnvType.GC.getCode()));
		return pageInfo;
	}

	@Override
	public SysFile unifiedFileSave(JSONObject jsonObject) {
		//添加到数据库中
				SysFile fileEntity=new SysFile();
				fileEntity.setSysId(jsonObject.getInteger("sysId"));
				fileEntity.setFolder(jsonObject.getString("folder"));
				fileEntity.setFileUrl(jsonObject.getString("object"));
				fileEntity.setUploadUid(jsonObject.getInteger("uploaderId"));
				fileEntity.setName(jsonObject.getString("originName"));
				fileEntity.setSaveType(jsonObject.getInteger("saveType"));
				fileEntity.setFileType(jsonObject.getString("mimeType"));
				fileEntity.setSize(jsonObject.getString("size"));
				fileEntity.setFileTypeIndex(jsonObject.getInteger("fileTypeIndex"));
				fileEntity.setThumbNailId(jsonObject.getInteger("thumbNailId"));
				if(jsonObject.getInteger("masterId")!=null) {
					fileEntity.setMasterId(jsonObject.getInteger("masterId"));
				}
				if(jsonObject.getInteger("userRole")!=null) {
					fileEntity.setUserRole(jsonObject.getInteger("userRole"));
				}

				if(jsonObject.getInteger("videoLong")!=null){
					fileEntity.setVideoLong(jsonObject.getInteger("videoLong"));
				}
				if (jsonObject.getString("description")!=null){
					fileEntity.setDescription(jsonObject.getString("description"));
				}
				if (null!=jsonObject.getString("uuid")){
					fileEntity.setUuid(jsonObject.getString("uuid"));
				}
				String md5="";
				if(jsonObject.getString("etag")!=null)
					md5=jsonObject.getString("etag");
				fileEntity.setMd5(md5);
				//批量上传添加标签
				String tag=jsonObject.getString("tag");
				if(StringUtils.isNotBlank(tag)) {
					fileEntity.getFileRemark().add(tag);
				}
				fileService.save(fileEntity);
				return fileEntity;
	}


	@Transactional
	@Override
	public GcVideo callbackSaveVideo(JSONObject jsonObject) {
		//添加到数据库中
		SysFile fileEntity=this.unifiedFileSave(jsonObject);

		GcVideo video=new GcVideo();

		video.setVideoName(jsonObject.getString("videoName"));
		video.setVideoDesc(jsonObject.getString("videoDesc"));
		video.setFileId(fileEntity.getId());
		video.setVideoFile(fileEntity);
		video.setVideoSource(jsonObject.getInteger("videoSource"));
		video.setSubId(jsonObject.getInteger("subId"));
		if(jsonObject.getInteger("id") != null)
			video.setId(jsonObject.getInteger("id"));
		this.saveOrUpdate(video);
		return video;
	}


	@Transactional
	@Override
	public GcMaster callbackSaveMasterVideo(JSONObject jsonObject) {
		//添加到数据库中
		SysFile fileEntity=this.unifiedFileSave(jsonObject);

		GcMaster gcMaster = gcMasterService.getById(jsonObject.getInteger("masterId"));
		if(gcMaster!=null) {
			gcMaster.setIntroVideoId(fileEntity.getId());
			gcMaster.setIntroVideoFile(fileEntity);
			gcMasterService.saveOrUpdate(gcMaster);
		}
		return gcMaster;
	}

	@Override
	public Integer getSubIdByVid(Integer vid) {
		return this.baseMapper.selectSubIdByVid(vid);
	}

	@Override
	public List<GcVideo> getVideoListByTopSubIds(List<Integer> subIds) {
		return this.baseMapper.selectVideoListByTopSubIds(subIds);
	}


	@Override
	public boolean changeVideoOrder(List<Integer> videoIds) {
		List<GcVideo> videoList = new ArrayList<>();
		Integer order = 1;
		for (Integer videoId:videoIds) {
			GcVideo newSubject = new GcVideo();
			newSubject.setId(videoId);
			newSubject.setOrder(order);
			videoList.add(newSubject);
			order++;
		}
		return this.updateBatchById(videoList);
	}

	@Override
	public Map<String, Object> getVideoSubjectInfo(Integer vid) {
		return this.baseMapper.selectVideoTopicSubjectInfo(vid);
	}

	@Override
	public List<GcVideo> selectVideoByVideoAndSub0NameIndex(String videoNameIndex,String subNameIndex,Integer masterId) {
		return this.baseMapper.selectVideoByVideoAndSub0NameIndex(videoNameIndex,subNameIndex,masterId);
	}
	@Override
	public List<GcVideo> selectVideoInfoBySubId(List<Integer> subId) {
		return this.baseMapper.selectVideoInfoBySubId(subId);
	}

	@Override
	public List<Integer> getVideoIdListBySubId(List<Integer> subId) {
		if (subId.isEmpty()){
			return new ArrayList<>();
		}
		return this.baseMapper.getVideoIdListBySubId(subId);
	}

	@Override
	public List<GcVideo> getVideoListBySubId(List<Integer> subId) {
		return this.baseMapper.getVideoListBySubId(subId);
	}

	@Override
	public List<GcVideo> getVideoLongListByVideoId(List<Integer> videoIds) {
		return this.baseMapper.getVideoLongListByVideoId(videoIds);
	}

	private List<VideoSearchResultDto> getCourseOriginVideoListResult(HttpServletRequest request, List<GcVideo> videos) {
		return videos.stream()
			.filter(video -> video.getOriginCourseId() != null)
			.map(video -> {
				updateVideoUrls(request, video);
				return videoMapping.mapCourseOrigin(video);
			})
			.collect(Collectors.toList());
	}

	private List<VideoSearchResultDto> getChannelOriginVideoListResult(HttpServletRequest request, List<GcVideo> videos) {
		return videos.stream()
			.filter(video -> video.getOriginChannelId() != null)
			.map(video -> {
				updateVideoUrls(request, video);
				return videoMapping.mapChannelOrigin(video);
			})
			.collect(Collectors.toList());
	}

	private List<Integer> getVideoIds(List<GcVideo> videos) {
		return videos.stream()
            .map(GcVideo::getId).collect(
			Collectors.toList());
	}

	private List<VideoSearchResultDto> populateSortedByValue(List<VideoSearchResultDto> result, Map<Integer, String> analytics,
															 VideoListFilterDto filter) {
		if (analytics.isEmpty()) {
			return result;
		}

		result.forEach(videoSearchResult -> setSortBy(videoSearchResult, filter.getSortBy())
			.accept(analytics.get(videoSearchResult.getId())));

		return sortVideoSearchResultByAnalytics(result, filter.getSortOrder(), filter.getSortBy());
	}

	private Consumer<String> setSortBy(VideoSearchResultDto videoSearchResult, AnalyticsType sortBy) {
		if (AnalyticsType.VIDEO_VIEW_COUNT.equals(sortBy)) {
			return videoSearchResult::setVideoViewCount;
		}
		if (AnalyticsType.VIEWERS_COUNT.equals(sortBy)) {
			return videoSearchResult::setViewersCount;
		}
		if (AnalyticsType.ENGAGEMENT_RATE.equals(sortBy)) {
			return videoSearchResult::setEngagementRate;
		}
		if (AnalyticsType.DROP_OFF_RATE.equals(sortBy)) {
			return videoSearchResult::setDropOffRate;
		}
		if (AnalyticsType.LIKES.equals(sortBy)) {
			return videoSearchResult::setVideoLikesCount;
		}

		return videoSearchResult::setVideoWatchingTime;
	}

	private List<VideoSearchResultDto> sortVideoSearchResultByAnalytics(List<VideoSearchResultDto> result, SortOrder sortDirection, AnalyticsType sortBy) {
		Comparator<VideoSearchResultDto> comparator = Comparator.comparing(getComparingField(sortBy), Comparator.nullsLast(Comparator.naturalOrder()));

		if (SortOrder.DESC.equals(sortDirection)) {
			comparator = Comparator.comparing(getComparingField(sortBy), Comparator.nullsLast(Comparator.naturalOrder())).reversed();
		}

		return result.stream()
			.sorted(comparator)
			.collect(Collectors.toList());
	}

	private Function<VideoSearchResultDto, String> getComparingField(AnalyticsType sortBy) {
		if (AnalyticsType.VIDEO_VIEW_COUNT.equals(sortBy)) {
			return VideoSearchResultDto::getVideoViewCount;
		}
		if (AnalyticsType.VIEWERS_COUNT.equals(sortBy)) {
			return VideoSearchResultDto::getViewersCount;
		}
		if (AnalyticsType.ENGAGEMENT_RATE.equals(sortBy)) {
			return VideoSearchResultDto::getEngagementRate;
		}
		if (AnalyticsType.DROP_OFF_RATE.equals(sortBy)) {
			return VideoSearchResultDto::getDropOffRate;
		}
		if (AnalyticsType.LIKES.equals(sortBy)) {
			return VideoSearchResultDto::getVideoLikesCount;
		}

		return VideoSearchResultDto::getVideoWatchingTime;
	}
}
