package com.threeatom.guidecore.service.impl;

import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.enums.OriginType;
import java.util.*;
import java.util.List;
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
	private GcUserAccessService userAccessService;

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


	@Override
	public List<GcVideo> getVideoListBySubIds(List<Integer> subIds) {
		// TODO Auto-generated method stub
//		QueryWrapper<GcVideo> queryWrapper=new QueryWrapper<GcVideo>();
//        queryWrapper.in("sub_id", subIds);
//        return this.list(queryWrapper);
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
		// TODO Auto-generated method stub
		List<GcVideo> list = this.baseMapper.selectLikeVideoByUserId(userId,masterId);
        return list;
	}

	@Override
	public GcVideo getVideoById(Integer vid) {
		// TODO Auto-generated method stub
		return this.baseMapper.selectVideoByid(vid);
	}

	@Override
	public boolean saveVideo(GcVideo video) {

		//新增生成order字段
		if (video.getId() != null){
			//查找同级video数
			/*List<GcVideo> orderList = new ArrayList<GcVideo>();
			QueryWrapper<GcVideo> queryWrapper = new QueryWrapper<GcVideo>();
			queryWrapper.eq("sub_id", video.getSubId());
			orderList = this.list(queryWrapper);
			video.setOrder((orderList.size()+1));*/
		}

		// TODO Auto-generated method stub
//		if(this.videoSourceAssert(video))

		return this.saveOrUpdate(video);
//		return false;
	}

	//删除视频下的全部事件

	@Override
	@Transactional
	public boolean deleteVideo(Integer vid) {
		// TODO Auto-generated method stub
		//删除视频,先删除视频下面的所有的事件
		eventService.deleteEventByVid(vid);

		return this.removeById(vid);
	}

	@Override
	@Transactional
	public boolean deleteVideoBySubIds(List<Integer> subIds) {
		// TODO Auto-generated method stub
		QueryWrapper<GcVideo> queryWrapper=new QueryWrapper<GcVideo>();
		queryWrapper.select("id").in("sub_id", subIds);

		List<GcVideo> list= this.list(queryWrapper);
		if(list.size()<1) {
			return true;
		}
		List<Integer> videoIds=list.stream().map(GcVideo::getId).collect(Collectors.toList());
		LOGGER.info(videoIds.size()+"   "+list.size());
		//批量删除视频下的事件
		eventService.deleteEventByVids(videoIds);

		//批量删除视频下的资源


		return this.remove(queryWrapper);
	}

	@Override
	public int getVideoNum(Integer masterId,List<Integer> subIds,Integer managerId) {
//		// TODO Auto-generated method stub
//		List<Integer> ids=subjectService.getSubjectIds(masterId);
//
//		if(ids.size()<1) {
//			return 0;
//		}
//
//		QueryWrapper<GcVideo> queryWrapper=new QueryWrapper<GcVideo>();
//
//		queryWrapper.in("sub_id", ids);
//
//		return this.count(queryWrapper);
		Integer type =TableConstant.COMMON_ONE;
		Integer state = TableConstant.COMMON_ZERO;
		return this.baseMapper.countVideoNumInPortal(masterId,type,state,subIds,managerId);
	}

	@Override
	public List<GcVideo> getVideoListBySubId(Integer subId) {
		// TODO Auto-generated method stub
		return this.baseMapper.selectVideoListBySubId(subId);
	}


	//门户端
	@Override
	public List<GcVideo> getFuzzyNameVideoInMaster(Integer masterId,String videoName){
		return this.baseMapper.getFuzzyNameVideoInMaster(masterId,videoName);
	}


	/**
	 * 根据视频id查询总时长
	 *
	 * @param videoIds
	 * @param userId
	 */
	@Override
	public Long sumVideoLongByIdUser(List<Integer> videoIds, Integer userId) {
		Long l= this.baseMapper.sumVideoLongByIdUser(videoIds, userId);
		if(l==null) {
			l=new Long(0);
		}
		return l;
	}

	/**
	 * 根据课程id查询对应的视频信息，同时加载出评论数、点赞数、问题数、时长等信息
	 *
	 * @return
	 */
	public Message getVideosBySubIds(Integer subjectId, Map<String, Object> params, SysSystem sys, HttpServletRequest request) {

		//查询出视频信息
//		Object videoNameObj = params.get("videoName");
//		String videoName = null;
//		if(videoNameObj != null){
//			videoName = videoNameObj.toString();
//		}
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
		/*if (!video.getTargetLang().contains(video.getLang())){
			sysFileCaption1.setState(TableConstant.COMMON_ZERO);
		}*/
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
			List<Integer> videoIds = gcVideos.stream().map(GcVideo::getId).collect(Collectors.toList());
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
			List<Integer> videoIds = gcVideos.stream().map(GcVideo::getId).collect(Collectors.toList());

			//Map<Integer,List<GcVideo>>videoMaps = gcVideos.stream().collect(Collectors.groupingBy(GcVideo::getSubId));

			Map<String, Object> videoParams = new HashMap<>();
			videoParams.put("videoIds",videoIds);
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
	public List<GcVideo> selectVideoPlayListBySubId(Integer subId, Integer userId) {
		return this.baseMapper.selectVideoPlayListBySubId(subId,userId);
	}

	@Override
	public GcVideo selectVideoPlayByVideo(Integer video, Integer userId) {
		return this.baseMapper.selectVideoPlayByVideo(video,userId);
	}

	@Override
	public boolean saveVideoInfo(SysSystem sys,GcVideo video,Integer masterId, HttpServletRequest request) {
		if (null!=video.getId()){
			Message message = new Message();
			//GcVideo oldVideo = videoService.getVideoById(video.getId());
			this.saveOrUpdate(video);
			SysFile file = new SysFile();
			if(Objects.nonNull(video.getFileId())) {
				file = sysFileService.getInfoById(video.getFileId());
				String url = sysFileService.getResFullUrl(file, request);
				video.setVideoFullUrl(url);
				String fullFileUrl = sysFileService.getResFullUrl(file,request);
				file.setFullFileUrl(fullFileUrl);
				String snapshoturl = sysFileService.getVideoSnapshotUrl(video);
				file.setSnapshotUrl(snapshoturl);
				video.setVideoFile(file);
			}

			List<GcEvent> eventList = eventService.getEventListByVid(video.getId(),masterId);
			if(CollectionUtils.isNotEmpty(eventList)){
				message.addData("eventNum",eventList.size());
			}
			List<GcResource>  resources = resourceService.getResByVid(video.getId());
			if(CollectionUtils.isNotEmpty(resources)){
				message.addData("resourceNum",resources.size());
			}

            /*if (oldVideo.getFileId()!=video.getFileId()){
                sysFileCaptionService.deleteCaption(oldVideo.getId());
                videoService.asyncMethodSaveVideo(video,request);
            }else {
                sysFileCaptionService.updateCaptionState(video.getTargetLang(),video.getId());
                videoService.asyncMethodUpdateVideo(video,request,sys);
            }*/
			if (null!=video.getTargetLang()&&video.getTargetLang().size()!=0) {
				//video.getTargetLang().add(video.getLang());
				file.setTargetLangJson(video.getTargetLang());
				sysFileService.saveOrUpdate(file);
				sysFileCaptionService.updateCaptionState(video.getTargetLang(), video.getId());
				this.asyncMethodUpdateVideo(video, request, sys);
			}

			return true;//message.ok("添加成功！").addData("sync", video);
		}
		ApiAssert.ifStringNotInList(video.getVideoName(), CommonConstant.defaultNoCourseOrVideName, "视频名称错误，不可用该值");
		//通过视频id保存课程
		if(video.getFileId()!=null) {
//    		if(VideoConstant.LOCAL!=video.getVideoSource()) return new Message().error("通过视频id保存课程的videoSource必须为1");
			ApiAssert.jsonValueIntegerIn(video.getVideoSource(), VideoConstant.GCVIDEO_VIDEOSOURCE_jsonStr, "VideoSource值错误，必须为: "+VideoConstant.GCVIDEO_VIDEOSOURCE_jsonStr);
			SysFile file = sysFileService.getById(video.getFileId());
			if(file==null) throw new SystemException("该视频id不存在");
//    		video.setVideoFile(file);
			String url = sysFileService.getResFullUrl(file, request);
			video.setVideoFullUrl(url);
			video.setVideoFile(file);
		}else {
			//通过视频链接保存课程
			ApiAssert.notNull(video.getVideoSource(), "视频源不能为null");
		}
		if(video.getVideoName()==null || video.getSubId() ==null)throw new SystemException("视频名称及课程id不可空");
//    	if(video.getVideoName().contains("-"))throw new SystemException("名称不可含横杠字符-");
//        int count = videoService.countVideoNameInSub0(video);
//        if(count>0) {
//            return new Message().error(10000, "'" + video.getVideoName() + "'- " + I18NUtil.get("guidecore.master.sameVideoNameNotice"));
//        }
		List<GcVideo> videoList = this.getVideoListBySubId(video.getSubId());
		if (null!=videoList&&TableConstant.COMMON_ZERO!=videoList.size()&&null==video.getId()){
			Integer max = videoList.stream().mapToInt(GcVideo::getOrder).max().getAsInt();
			video.setOrder(max+1);
		}
		boolean flag = this.saveVideo(video);
		//新视频取消原来完成的课程进度
		subjectCompleteService.updateStateByVideoId(video.getId(),masterId);

		SysFile sysFile = new SysFile();
		if(null!=video.getIfCaption()&&TableConstant.COMMON_ONE==video.getIfCaption()){
			this.asyncMethodSaveVideo(video,request);
			sysFile.setIfCaption(video.getIfCaption().toString());
		}
		sysFile.setName(video.getVideoName());
		sysFile.setId(video.getFileId());
		sysFile.setTargetLangJson(video.getTargetLang());
		sysFileService.updateById(sysFile);
		Integer fileTypeIndex = sysFileService.selectFileTypeIndexByVideoId(video.getId());
		video.setFileTypeIndex(fileTypeIndex);

		SysFile newVideoFile = sysFileService.getById(video.getFileId());
		String snapshoturl = sysFileService.getVideoSnapshotUrl(video);
		String fullFileUrl = sysFileService.getResFullUrl(newVideoFile,request);
		newVideoFile.setSnapshotUrl(snapshoturl);
		newVideoFile.setFullFileUrl(fullFileUrl);
		video.setVideoFile(newVideoFile);

		if (null!=video.getCourseTags()){
			QueryWrapper<PtTags> queryWrapper = new QueryWrapper<>();
			queryWrapper.in("video_id", video.getId());
			queryWrapper.in("master_id",masterId);
			queryWrapper.in("type",TableConstant.COMMON_TWO);
			ptTagsService.remove(queryWrapper);
			List<String> tagList = video.getCourseTags().toJavaList(String.class);
			List<PtTags> ptTagsList = new ArrayList<>();
			Integer finalMasterId = masterId;
			tagList.forEach(i->{
				PtTags newTags = new PtTags();
				newTags.setMasterId(finalMasterId);
				newTags.setTagText(i);
				newTags.setVideoId(video.getId());
				newTags.setType(TableConstant.COMMON_TWO);
				newTags.setOrder(TableConstant.COMMON_ZERO);
				ptTagsList.add(newTags);
			});
			ptTagsService.saveOrUpdateBatch(ptTagsList);
		}
		return flag;
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
	public void saveChannelContent(List<PtChannelContent> ptChannelContent, List<SysFile> sysFiles) {
		List<GcVideo> channelVideoContent = ptChannelContent.stream()
			.map(channelContent -> createChannelVideoContent(sysFiles, channelContent))
			.collect(Collectors.toList());

		saveBatch(channelVideoContent);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<GcVideo> getChannelVideoContent(PtChannelContent channelContent) {
		QueryWrapper<GcVideo> queryWrapper = new QueryWrapper<>();

		queryWrapper.eq("file_id", channelContent.getFileId());

		return Optional.ofNullable(getOne(queryWrapper));
	}

	private GcVideo createChannelVideoContent(List<SysFile> sysFiles, PtChannelContent channelContent) {
		SysFile videoFile = getVideoFile(channelContent.getFileId(), sysFiles);

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

		return gcVideo;
	}

	private SysFile getVideoFile(Integer fileId, List<SysFile> sysFiles) {
		return sysFiles.stream()
			.filter(sysFile -> sysFile.getId().equals(fileId))
			.findFirst()
			.orElse(null);
	}

	@Override
	public List<DbAnalyticsResultDto> getVideoCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
		return this.baseMapper.getVideoCountAnalytics(filter, masterId);
	}

	@Override
	public List<GcVideo> getSysFileByIdsOrVideos(List<Integer> fileList, List<Integer> videoList) {
		return this.baseMapper.getSysFileByIdsOrVideos(fileList,videoList);
	}

	@Override
	public List<GcVideo> buildVideoInfo(Integer userId, SysSystem sys, List<GcVideo> gcVideos,Integer masterId,HttpServletRequest request,Integer envFlag) {
		if(CollectionUtils.isNotEmpty(gcVideos)){
			List<Integer> videoIds = gcVideos.stream().map(GcVideo::getId).collect(Collectors.toList());
			Map<String, Object> videoParams = new HashMap<>(3);
			videoParams.put("videoIds", videoIds);
//			videoParams.put("subjectIds", subjectIds);
			videoParams.put("type", 1);//点赞
//			videoParams.putAll(params);//
			//查询点赞
			Map<Integer, List<GcUserVideoAction>> videoMap = videoActionService.getVideoActionBySubject(videoParams);
			// 查询评论 根据视频id查询评论 评论列表单独接口
			List<GcVideoComment> commentVideoIds = gcVideoCommentService.getVideoComments(videoIds,masterId);//查询评论数，不用加userID
//
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
			Map<Integer,Object> videoPlayCount = new HashMap<>();
			videoPlayCount = userVideoPlayService.getVideoPlayCount(videoIds);

			//查询单个视频播放进度
			List<GcEvent> events = gcEventService.getEventListByVideoIds(videoIds,userId);
			Map<Integer,List<GcEvent>> eventmap = events.stream().collect(Collectors.groupingBy(GcEvent::getVideoId));
			List<GcVideo> newGcVideos = new ArrayList<>(gcVideos.size());
			for (GcVideo video : gcVideos){
				if (Objects.nonNull(videoPlayCount.get(video.getId()))){
					Map map = (Map)videoPlayCount.get(video.getId());
					video.setPlayNum(Integer.parseInt(map.get("countNum").toString()));
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
//					video.setCommentList(gcVideoComments);//评论列表
				}
				//设置已回答的问题和问题总数
				List<GcEvent> answers = eventAnswerMap.get(id);
				if(CollectionUtils.isNotEmpty(answers)){
//					video.setEventNum(answers.size());
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
				}
//				sysFileService.getResFullUrl(videoFile, sys, request);
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
		return new ArrayList<>(0);
	}

	private short buildCompleteStatus(Integer playState, List<GcEvent> answers,List<GcEvent> eventList,HttpServletRequest request,Integer envFlag) {
		if(playState == null){//没有播放记录
			return TableConstant.VIDEO_COMPLETE_STATUS0;
		}
//		if(TableConstant.COMMON_THREE==envFlag){
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

//		}
//		if(1 == playState /*&& CollectionUtils.isEmpty(eventList)*/){
//			//看完视频，没有问题
//			return TableConstant.VIDEO_COMPLETE_STATUS2;
//		}
//		if(0 == playState /*&& CollectionUtils.isNotEmpty(eventList) && CollectionUtils.isEmpty(answers)*/){
//			//看了视频但是没看完，并且问题没回答,gc环境
//			return TableConstant.VIDEO_COMPLETE_STATUS1;
//		}
//		if (TableConstant.COMMON_THREE==envFlag) {
//			 return TableConstant.VIDEO_COMPLETE_STATUS1;
//		}
		return TableConstant.VIDEO_COMPLETE_STATUS2;
	}

	/**
	 * 根据视频id和用户id查询播放的时长
	 *
	 * @param videoIds
	 * @param userId
	 * @return
	 */
	@Override
	public Long sumPlayVideoLongByIdUser(List<Integer> videoIds, int userId) {
		Long l= this.baseMapper.sumPlayVideoLongByIdUser(videoIds, userId);
		if(l==null) {
			l=new Long(0);
		}
		return l;
	}

	/**
	 * 根据一级课程id查询返回视频信息，
	 * 	后面需要根据一级课程id或者二级课程id进行分组
	 * @param subjectIds
	 * @param userId
	 * @return
	 */
	@Override
	public  List<GcVideo> getVideosBySubjectIds0(List<Integer> subjectIds, Integer userId,Integer masterId,HttpServletRequest request,Integer envFlag) {
		if(CollectionUtils.isNotEmpty(subjectIds)){
			List<Integer> gcVideoIdSubList = subjectService.countSessions(subjectIds);
			List<GcVideo> gcVideoss = null;
			if (null!=gcVideoIdSubList&&gcVideoIdSubList.size()!=0){
				gcVideoss = this.baseMapper.selectVideosSubIds(gcVideoIdSubList);
			}
			//List<GcVideo> gcVideoss = this.baseMapper.selectVideosOneLevelSubIds(subjectIds);//公共方法，查询出来在进行groupby
			List<GcVideo> gcVideos = this.buildVideoInfo(userId, null, gcVideoss,masterId,request,envFlag);
			return gcVideos;
//			if(CollectionUtils.isNotEmpty(gcVideoss)){
//				List<GcVideo> videos = new ArrayList<GcVideo>(gcVideoss.size());
//				for(GcVideo video : gcVideoss){
//					GcVideo tmpVideo = getVideoCompleteStatusByVideoId(video, userId);
//					if(tmpVideo != null){
//						videos.add(tmpVideo);
//					}
//				}
//				return videos;
//			}
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
			List<Integer> videoIds = gcVideos.stream().map(GcVideo::getId).collect(Collectors.toList());

			Map<String, Object> videoParams = new HashMap<>();
			videoParams.put("videoIds",videoIds);
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

	/**
	 * 根据课程id查询视频是否完成，根据播放进度和问题回答数
	 *
	 * @param
	 * @return
	 */
//	@Override
//	public List<GcVideo> getVideoCompleteStatusByVideo(List<Integer> videoIds, int userId) {
//		if(CollectionUtils.isNotEmpty(videoIds)){
//			//查询得到播放进度
//			Map<Integer, GcUserVideoPlay> videoPlayBySubject = userVideoPlayService.findVideoPalyStateByVideos(videoIds, userId);
//			//查询问题 key为videoId
//			List<GcEvent> eventAnswers = gcEventService.findEventAnswerByVideoIdsUser(videoIds, userId);
//			Map<Integer, List<GcEvent>> eventAnswerMap = new HashMap<>(0);
//			if(CollectionUtils.isNotEmpty(eventAnswers)){
//				eventAnswerMap = eventAnswers.stream().collect(Collectors.groupingBy(GcEvent::getVideoId));
//			}
//
//			int len = videoIds.size();
//			List<GcVideo> videos = new ArrayList<>(len);
//			for(Integer videoId : videoIds){
//				GcVideo video = new GcVideo();
//				video.setId(videoId);
//				GcUserVideoPlay play = videoPlayBySubject.get(videoId);
//				List<GcEvent> events = eventAnswerMap.get(videoId);
//				//判断是否完成了视频，视频playstate == 1 and 问题全部回答完或者没问题
//				if(play == null){
//					//没有播放直接直接设置完成状态为0
//					video.setCompleteStatus(TableConstant.VIDEO_COMPLETE_STATUS0);
//				}else{
//					//有播放记录
//					video.setCompleteStatus(TableConstant.VIDEO_COMPLETE_STATUS1);
//					Integer playState = play.getPlayState();
//					if(TableConstant.COMMON_ONE == playState){//播放完成
//						//在判断问题
//						if(CollectionUtils.isEmpty(events)){
//							//没有问题，播放完成设置视频完成状态为绿色
//							video.setCompleteStatus(TableConstant.VIDEO_COMPLETE_STATUS2);
//						}else{
//							//有问题，需要看是否都回答了
//							List<GcEvent> answerJson = events.stream().filter(e -> StringUtils.isNotEmpty(e.getAnswerJson())).collect(Collectors.toList());
//							if(CollectionUtils.isNotEmpty(answerJson) && answerJson.size() == events.size()){
//								//有问题且都回答完毕
//								video.setCompleteStatus(TableConstant.VIDEO_COMPLETE_STATUS2);
//							}
//							video.setEventList(events);//问题回答list
//						}
//					}
//				}
//				videos.add(video);
//			}
//			return videos;
//		}
//		return new ArrayList<>(0);
//	}


//	public GcVideo getVideoCompleteStatusByVideoId(GcVideo video, int userId) {
//		if(video != null){
//
//			Integer videoId = video.getId();
//			//查询得到播放进度
//			GcUserVideoPlay play = userVideoPlayService.findVideoPalyStateByVideoId(videoId, userId);
//			//查询问题 key为videoId
//			List<Integer> videoIds = Arrays.asList(videoId);
//			List<GcEvent> events = gcEventService.findEventAnswerByVideoIdsUser(videoIds, userId);
//			//判断是否完成了视频，视频playstate == 1 and 问题全部回答完或者没问题
//			if(play == null){
//				//没有播放直接直接设置完成状态为0
//				video.setCompleteStatus(TableConstant.VIDEO_COMPLETE_STATUS0);
//			}else{
//				//有播放记录
//				video.setCompleteStatus(TableConstant.VIDEO_COMPLETE_STATUS1);
//				Integer playState = play.getPlayState();
//				if(TableConstant.COMMON_ONE == playState){//播放完成
//					//在判断问题
//					if(CollectionUtils.isEmpty(events)){
//						//没有问题，播放完成设置视频完成状态为绿色
//						video.setCompleteStatus(TableConstant.VIDEO_COMPLETE_STATUS2);
//					}else{
//						//有问题，需要看是否都回答了
//						List<GcEvent> answerJson = events.stream().filter(e -> StringUtils.isNotEmpty(e.getAnswerJson())).collect(Collectors.toList());
//						if(CollectionUtils.isNotEmpty(answerJson) && answerJson.size() == events.size()){
//							//有问题且都回答完毕
//							video.setCompleteStatus(TableConstant.VIDEO_COMPLETE_STATUS2);
//						}
//						video.setEventList(events);//问题回答list
//					}
//				}
//			}
//			return video;
//		}
//		return null;
//	}

	/**
	 * 根据课程id加载视频信息
	 *
	 * @param subjectId
	 * @param userId
	 * @param isLoadVideoStatus 是否加载视频完成状态
	 * @return
	 */
//	@Override
//	public List<GcVideo> getVideosBySubId(Integer subjectId, Integer userId, boolean isLoadVideoStatus) {
//		List<GcVideo> gcVideoss = this.baseMapper.selectVideoListBySubId(subjectId);
//		if(CollectionUtils.isNotEmpty(gcVideoss) && isLoadVideoStatus){//有视频并且需要加载视频完成状态
//			List<Integer> ids = gcVideoss.stream().map(GcVideo::getId).collect(Collectors.toList());
//			List<GcVideo> videoStatus = getVideoCompleteStatusByVideo(ids, userId);
//			Map<Integer, GcVideo> videoStatusMap = videoStatus.stream().collect(Collectors.toMap(GcVideo::getId, Function.identity(), (key1, key2) -> key2));
//			//不能直接返回，文件信息丢失，只需要设置状态
//			for(GcVideo video : gcVideoss){
//				GcVideo tmp = videoStatusMap.get(video.getId());
//				if(tmp !=  null){
//					video.setCompleteStatus(tmp.getCompleteStatus());
////					video.set
//				}
//			}
//		}
//		return gcVideoss;
//	}

	/**
	 * 分页查询视频
	 *
	 * @param params
	 * @param request
	 * @return
	 */
	@Override
	public PageInfo<GcVideo> page(Map<String, Object> params,SysSystem sys, HttpServletRequest request) {
		Integer pageNum = 1;
		Integer pageSize = 10;
		try {
			pageNum = Integer.parseInt(params.get("pageNum") == null ? "1": params.get("pageNum").toString());
			pageSize = Integer.parseInt(params.get("pageSize") == null ? "10": params.get("pageSize").toString());
		} catch (Exception e) {}
		String masterId = request.getHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("powtoon.portal.id.notfound"));
		}
		params.put("masterId", masterId);
		Page<GcVideo> page = PageHelper.startPage(pageNum, pageSize, true);
		this.baseMapper.pageVideo(params);
		PageInfo<GcVideo> pageInfo = new PageInfo<GcVideo>(page);
		//设置播放状态等字段
		List<GcVideo> gcVideos = pageInfo.getList();
		List<Integer> ids = gcVideos.stream().map(GcVideo::getFileId).collect(Collectors.toList());
		if (null!=ids&&ids.size()!=TableConstant.COMMON_ZERO){
		Map<Integer,SysFile> fileMap = fileService.getFilesUploadByFileIds(ids);
		for (GcVideo gcVideo : gcVideos) {
			if (null!=fileMap) {
				SysFile file = fileMap.get(gcVideo.getVideoFile().getId());
				if (null != file) {
					gcVideo.getVideoFile().setGcUser(file.getGcUser());
				}
			}
		}
		}
		Integer userId = (Integer) params.get("userId");
		pageInfo.setList(this.buildVideoInfo(userId, sys, gcVideos,Integer.parseInt(masterId),request,EnvType.GC.getCode()));
		return pageInfo;
	}

	public List<GcVideo> buildInfo(List<GcVideo> gcVideoss){

		return new ArrayList<>();
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
//				String langJson=jsonObject.getString("langJson");
//				if(StringUtils.isNotBlank(langJson)){
//					fileEntity.getLangJson().add(langJson);
//				}
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
		// TODO Auto-generated method stub
		
		return this.baseMapper.selectSubIdByVid(vid);
	}

	@Override
	public List<GcVideo> getVideoListByTopSubIds(List<Integer> subIds) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		if (TableConstant.COMMON_ZERO==subId.size()){
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

}
