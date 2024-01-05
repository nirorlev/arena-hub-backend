package com.threeatom.guidecore.controller.api.user;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.EnvType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcEventMapper;
import com.threeatom.guidecore.mapper.GcVideoMapper;
import com.threeatom.guidecore.service.*;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.system.service.SysFileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/v1/guidecore/workbook")
@Api(tags = "作业本数据")
public class WorkbookGuideCoreController extends GuideCoreController {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkbookGuideCoreController.class);
	
    @Autowired
    private GcSubjectService gcSubjectService;
    @Autowired
    private GcVideoService gcVideoService;
    
    @Autowired
    private GcUserEventResourceService gcUserEventResourceService;
    @Autowired
    private SysFileService sysFileService;

	@Autowired
	private GcUserVideoPlayService userVideoPlayService;//用户视频播放进度服务类--查询播放进度

	@Autowired
	private GcEventService gcEventService;//查询视频问题相关

	@Autowired
	private GcVideoMapper videoMapper;

	@Autowired
	private GcEventMapper gcEventMapper;
    
	
	@ApiOperation(value = "作业本-课程/视频/问题list", httpMethod = "GET")
    @PostMapping("/subVideoEventList")
    public Message subVideoEventList(@RequestBody JSONObject jsonRequest, HttpServletRequest request) {
		return envSubVideoEventList(jsonRequest,request,EnvType.GC.getCode());
	}

	@ApiOperation(value = "作业本-课程/视频/问题list", httpMethod = "GET")
	@PostMapping("/subVideoEventListPt")
	public Message subVideoEventListPt(@RequestBody JSONObject jsonRequest, HttpServletRequest request) {
		return envSubVideoEventList(jsonRequest,request,EnvType.PT.getCode());
	}



	public Message envSubVideoEventList(JSONObject jsonRequest, HttpServletRequest request,Integer envFlag) {
		Integer studentId = jsonRequest.getInteger("studentId");//老师需传，学生不用
		Integer subId  = jsonRequest.getInteger("subId");
		Integer masterId = request.getIntHeader("masterId");
		ApiAssert.notNull(subId, "subId不可空");
		if(studentId==null)studentId=this.getGcUser().getId();//studentId为空则当前是学生，studentId不为空则当前是老师
		if(null!=jsonRequest.get("masterId"))masterId=Integer.parseInt(jsonRequest.get("masterId").toString());

		List<GcSubject> list = gcSubjectService.getSubVideoEventList(subId,studentId,masterId,request);
		List<Integer> videoIdList = new ArrayList<>();
		List<GcVideo> videoList = new ArrayList<>();
		for(GcSubject subject: list) {
			Integer videosTotalNum=subject.getVideoChildList().size();//总视频数量
			Integer videosTotalLong=0;//视频总时长
			Integer eventTotalNum=0;//其下视频其下的总问题数event;
			Integer answeredEventNum=0;//其下视频其下的总问题的已回答数answer;
			Iterator<GcVideo> iterator = subject.getVideoChildList().listIterator();
			while (iterator.hasNext()){
				GcVideo gcVideo = iterator.next();
				if(null==gcVideo.getId()){
					iterator.remove();
				}
			}

			List<Integer> videoIds = subject.getVideoChildList().stream().map(GcVideo::getId).collect(Collectors.toList());
			List<GcVideo> newList = JSON.parseArray(JSON.toJSONString(subject.getVideoChildList()), GcVideo.class);
			videoIdList.addAll(videoIds);
			videoList.addAll(newList);

			for(GcVideo video: subject.getVideoChildList()) {
				if (null != video.getVideoFile()) {
					video.getVideoFile().setSnapshotUrl(sysFileService.getVideoSnapshotUrl(video.getVideoFile()));
				}
				if (video.getVideoLong() != null) videosTotalLong += video.getVideoLong();//累加视频数量
				Integer eventNumInVideo = TableConstant.COMMON_ZERO;//单个video下问题数量
				for (GcEvent event : video.getEventList()) {
					if (null != event.getId()) {
						eventTotalNum += 1;//累加event数量
						eventNumInVideo += 1;//单个video下问题数量
					}
					if (event.getMyAnswer() != null && event.getMyAnswer().getId() != null)
						answeredEventNum += 1;//累加已回答event数量
				}
				if (eventNumInVideo == TableConstant.COMMON_ZERO) {
					List<GcEvent> emptyEventList = new ArrayList<>();
					video.setEventList(emptyEventList);
				}
				List<GcEvent> eventList = video.getEventList();
				if(null!=eventList && eventList.size()>TableConstant.COMMON_ZERO) {
					Long answerdNum = eventList.stream().filter(e -> null != e.getMyAnswer()).count();
					video.setEventNum(eventList.size());
					video.setAnsweredNums(answerdNum.intValue());
				}

				subject.setVideosTotalNum(videosTotalNum);//总视频数量
				subject.setVideosTotalLong(videosTotalLong);//视频总时长
				subject.setEventTotalNum(eventTotalNum);//其下视频其下的总问题数event;
				subject.setAnsweredEventNum(answeredEventNum);//其下视频其下的总问题的已回答数answer;
			}
		}
		Map<Integer, GcUserVideoPlay> videoPalyStateByVideos = userVideoPlayService.findVideoPalyStateByVideos(videoIdList, getGcUser().getId(),masterId);
		List<GcEvent> eventAnswers = gcEventService.findEventAnswerByVideoIdsUser(videoIdList, getGcUser().getId(),masterId,envFlag);
		Map<Integer, List<GcEvent>> eventAnswerMap = new HashMap<>(0);
		if(CollectionUtils.isNotEmpty(eventAnswers)){
			eventAnswerMap = eventAnswers.stream().collect(Collectors.groupingBy(GcEvent::getVideoId));
		}
		List<GcEvent> events = gcEventService.getEventListByVideoIds(videoIdList,getGcUser().getId());
		List<GcVideo> videos = gcVideoService.buildVideoInfo(getGcUser().getId(),getSystem(), videoList,masterId,request,envFlag);

		for(GcVideo video: videos) {
			GcUserVideoPlay gcUserVideoPlay = videoPalyStateByVideos.get(video.getId());
			video.setCompleteStatus(TableConstant.VIDEO_COMPLETE_STATUS0);//默认值 防止外面空指针
			List<GcEvent> answers = eventAnswerMap.get(video.getId());
			Map<Integer,List<GcEvent>> eventmap = events.stream().collect(Collectors.groupingBy(GcEvent::getVideoId));
			List<GcEvent> eventList = eventmap.get(video.getId());
			if(gcUserVideoPlay != null){
				Integer playState = gcUserVideoPlay.getPlayState();
				video.setPlayState(playState  == null ? null : String.valueOf(playState));//视频播放状态
				video.setCompleteStatus(buildCompleteStatus(playState,  answers,eventList,request,envFlag));
			}
		}

		Map<Integer,List<GcVideo>> map = videos.stream().collect(Collectors.groupingBy(GcVideo::getSubId));
		for(GcSubject gcSubject : list){
			List<GcVideo> gcVideoList = map.get(gcSubject.getId());
			if(null!=gcVideoList && gcVideoList.size()>TableConstant.COMMON_ZERO){
				Long videoWatcheNum = gcVideoList.stream().filter(e->null!=e.getCompleteStatus() && TableConstant.VIDEO_COMPLETE_STATUS2==e.getCompleteStatus()).count();
				gcSubject.setVideoFinishedNum(videoWatcheNum);
			}
		}
		PageInfo<GcSubject> pageInfo = new PageInfo<>(list);
		return new Message().ok().addData("subVideoEventList", list);
	}

	@PostMapping("/selectVideosInTopic")
	public Message selectVideosInTopic(@RequestBody GcSubject gcSubject, HttpServletRequest request) {
		Message message = new Message();
		GcUser user = new GcUser();
		if(Objects.nonNull(gcSubject.getCurrentStudentUserId())){
			user.setId(gcSubject.getCurrentStudentUserId());
		}else {
			user = this.getGcUser();
		}
		if(Objects.isNull(gcSubject.getId())){
			throw new SystemException(I18NUtil.get("powtoon.topic.error"));
		}
		List<Integer> subIds = new ArrayList<>();
		subIds.add(gcSubject.getId());
		PageParam pageParam = new PageParam(request);
		Integer pageNum = pageParam.getPageNum();
		Integer pageSize=pageParam.getPageSize();
		if (pageNum > 0 && pageSize > 0) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<GcVideo> allVideos = videoMapper.selectVideoListByTopSubIds(subIds);
		if(CollectionUtils.isNotEmpty(allVideos)) {
			for (GcVideo gcVideo : allVideos) {
				SysFile sysFile = sysFileService.getById(gcVideo.getFileId());
				gcVideo.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(sysFile));
			}
			List<Integer> videoIds = allVideos.stream().map(GcVideo::getId).collect(Collectors.toList());
			List<GcEvent> eventList = gcEventMapper.getEventListByVideoIds(videoIds, user.getId());
			for (GcVideo gcVideo : allVideos) {
				for (GcEvent gcEvent : eventList) {
					if (gcVideo.getEventList().size() < 10 && gcEvent.getVideoId().equals(gcVideo.getId())) {
						gcVideo.getEventList().add(gcEvent);
					}
				}
			}
			PageInfo<GcVideo> pageInfo = new PageInfo<>(allVideos);
			return message.ok().addData("allVideos", pageInfo);
		}else {
			PageInfo<GcVideo> gcVideoPageInfo = new PageInfo<>(allVideos);
			return message.ok().addData("allVideos",gcVideoPageInfo);
		}
	}

	@PostMapping("/selectEventsInVideo")
	public Message selectEventsInVideo(@RequestBody GcVideo gcVideo, HttpServletRequest request) {
		Message message = new Message();
		GcUser user = new GcUser();
		if(Objects.nonNull(gcVideo.getCurrentStudentUserId())){
			user.setId(gcVideo.getCurrentStudentUserId());
		}else {
			user = this.getGcUser();
		}
		if(Objects.isNull(gcVideo.getId())){
			throw new SystemException(I18NUtil.get("guidecore.video.detail.error"));
		}
		List<Integer> videoIds = new ArrayList<>();
		videoIds.add(gcVideo.getId());
		PageParam pageParam = new PageParam(request);
		Integer pageNum = pageParam.getPageNum();
		Integer pageSize=pageParam.getPageSize();
		if (pageNum > 0 && pageSize > 0) {
			PageHelper.startPage(pageNum, pageSize);
		}else {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<GcEvent> eventList = gcEventMapper.getEventListByVideoIds(videoIds, user.getId());
		PageInfo<GcEvent> pageInfo = new PageInfo<>(eventList);
		return message.ok().addData("eventList",pageInfo);
	}



	private short buildCompleteStatus(Integer playState, List<GcEvent> answers,List<GcEvent> eventList,HttpServletRequest request,Integer envFlag) {
		if(playState == null){//没有播放记录
			return TableConstant.VIDEO_COMPLETE_STATUS0;
		}
		if(1 == playState && CollectionUtils.isEmpty(eventList)){
			//看完视频，没有问题
			return TableConstant.VIDEO_COMPLETE_STATUS2;
		}
		if(CollectionUtils.isNotEmpty(answers)){
			//有问题，全部回答完毕
			List<GcEvent> answereds = answers.stream().filter(a -> StringUtils.isNotEmpty(a.getAnswerJson())).collect(Collectors.toList());
			if(1 == playState && (CollectionUtils.isNotEmpty(answereds) && answers.size() == eventList.size())){
				return TableConstant.VIDEO_COMPLETE_STATUS2;
			}
		}
		if (TableConstant.COMMON_THREE==envFlag) {
			return TableConstant.VIDEO_COMPLETE_STATUS1;
		}
		return TableConstant.VIDEO_COMPLETE_STATUS2;
	}
	
	
	@ApiOperation(value = "作业本-单个话题的课程/视频/问题", httpMethod = "POST")
    @PostMapping("/topicContent")
    public Message topicContent(@RequestBody JSONObject jsonRequest, HttpServletRequest request) {
		Integer studentId = jsonRequest.getInteger("studentId");//老师需传，学生不用
		Integer subId  = jsonRequest.getInteger("subId");
		GcUser user = this.getGcUser();
		List<GcSubject> list = new ArrayList();
		Map<String,Object> numMap = new HashMap();
		Map<String,Object> eventAnswerStateMap=new HashMap();
		if (studentId != null) {
		//老师端	
			list = gcSubjectService.getLevel1VideoEventList(subId,studentId,user.getId());
			numMap = gcSubjectService.selectEventResNumMapForWorkbookTeacher(subId,studentId,user.getId());//资源回复数量
			eventAnswerStateMap = gcSubjectService.getAnswerMessageMapForTeacherWorkbook(subId,studentId,user.getId());//问题回答状态
        } else {
      	//学生端
        	list = gcSubjectService.getLevel1VideoEventList(subId,user.getId(),null);
        	numMap = gcSubjectService.selectEventResNumMapForWorkbook(subId,user.getId());//资源回复数量
        }
		
		
		for(GcSubject subject: list) {
			for(GcVideo video: subject.getVideoChildList()) {
				for(GcEvent event: video.getEventList()) {
					//设置问题图片
					if(event.getEventImageFile()!=null) sysFileService.getResFullUrl(event.getEventImageFile(), request);
					//设置回复的资源数量
					if(numMap.get(event.getId()+"_my")!=null)  event.setEventResMyNum(numMap.get(event.getId()+"_my")); 
					if(numMap.get(event.getId()+"_others_0")!=null)  event.setEventResOthersNumUnRead(numMap.get(event.getId()+"_others_0"));
					if(numMap.get(event.getId()+"_others_1")!=null)  event.setEventResOthersNumRead(numMap.get(event.getId()+"_others_1"));
					
					if(eventAnswerStateMap.get(event.getId())!=null) event.setEventAnswerState(eventAnswerStateMap.get(event.getId()));
				}
			}
		}
		
		return new Message().ok().addData("subVideoEventList", list);
//				.addData("numMap", numMap);
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
		if(null!=studentId){
			list = gcUserEventResourceService.getEventResListForWorkBook(eventId, studentId, studentId,masterId,request,null);
		}else {
			list = gcUserEventResourceService.getEventResListForWorkBook(eventId, this.getGcUser().getId(), studentId,masterId,request,null);
		}
		PageInfo<GcUserEventResource> pageInfo = new PageInfo<>(list);
		return new Message().ok().addData("eventResList", pageInfo);
	}
	
	
	
	
}
