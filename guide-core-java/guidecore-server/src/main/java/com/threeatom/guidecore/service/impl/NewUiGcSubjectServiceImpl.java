package com.threeatom.guidecore.service.impl;

import com.threeatom.guidecore.util.RequestUtil;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcVideoMapper;
import com.threeatom.guidecore.service.*;
import com.threeatom.guidecore.util.I18NUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.mapper.NewUiGcSubjectMapper;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;

@Service
public class NewUiGcSubjectServiceImpl  extends ServiceImpl<NewUiGcSubjectMapper, GcSubject> implements NewUiGcSubjectService{

	private static final Logger log = LoggerFactory.getLogger(NewUiGcSubjectServiceImpl.class);

	@Autowired
	private GcUserService gcUserService;//用户服务类--统计参与人数

	@Autowired
	private GcUserVideoActionService videoActionService;//用户视频操作--查询评论、点赞、星级评价

	@Autowired
	private SysFileService sysFileService;//获取文件全路径

	@Autowired
	private GcVideoService gcVideoService;//查询课程下的视频信息

	@Autowired
	GcVideoMapper gcVideoMapper;

	@Resource
	NewUiGcSubjectMapper newUiGcSubjectMapper;

	@Autowired
	@Lazy
	private GvgMasterService gvgMasterService;

	@Override
	public PageInfo<GcSubject> list(Map<String, Object> params, SysSystem system, HttpServletRequest request,Integer envFlag) {
		PageInfo<GcSubject> pageInfo = page(params, request);
		Integer userId = (Integer) params.get("userId");
		buildSubject(pageInfo, userId, system, request,envFlag);
		return pageInfo;
	}

	private PageInfo<GcSubject> page(Map<String, Object> params, HttpServletRequest request) {
		PageParam pageParam = new PageParam(request);
		Integer pageNum = pageParam.getPageNum();
		Integer pageSize=pageParam.getPageSize();

		Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
		params.put("masterId", masterId);
		if (pageNum > 0 && pageSize > 0) {
			PageHelper.startPage(pageNum, pageSize);
		}
		return new PageInfo<>(this.baseMapper.findSubjects(params));
	}

	private PageInfo<GcSubject> listByFid(Map<String, Object> params, HttpServletRequest request) {
		PageParam pageParam = new PageParam(request);
		Integer pageNum = pageParam.getPageNum();
		Integer pageSize=pageParam.getPageSize();

		Integer masterId = RequestUtil.getMasterId(request).get();
		params.put("masterId", masterId);
		Page<GcSubject> page = PageHelper.startPage(pageNum, pageSize, true);
		this.baseMapper.listByFid(params);
		return new PageInfo<>(page);
	}

	public void buildSubject(PageInfo<GcSubject> page, Integer userId, SysSystem sys, HttpServletRequest request,Integer envFlag){
		List<GcSubject> list = buildSubject2(page.getList(),userId,sys,request,envFlag);
		page.setList(list);
	}

	public List<GcSubject> selectBuildSubject(Map<String, Object> params, HttpServletRequest request){
		String masterId = request.getHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("powtoon.portal.id.notfound"));
		}
		params.put("masterId", masterId);
		PageParam pageParam = new PageParam(request);
		Integer pageNum = pageParam.getPageNum();
		Integer pageSize=pageParam.getPageSize();
		if (pageNum > 0 && pageSize > 0) {
			PageHelper.startPage(pageNum, pageSize);
		}
        return this.baseMapper.findSubjects(params);
	}

	@Override
	public List<GcSubject> selectTwoSubjectByIds(List<Integer> subjectIds,HttpServletRequest request) {
		Integer masterId = Integer.parseInt(request.getHeader("masterId"));
		PageParam pageParam = new PageParam(request);
		Integer pageNum = pageParam.getPageNum();
		Integer pageSize=pageParam.getPageSize();
		if (pageNum > 0 && pageSize > 0) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<GcSubject> subjectList = this.baseMapper.listByIds(subjectIds,masterId);
		List<GcVideo> videoList = gcVideoService.selectVideoInfoBySubId(subjectList.stream().map(GcSubject::getId).collect(Collectors.toList()));
		for (GcSubject gcSubject : subjectList) {
			SysFile file=gcSubject.getSubImgFile();
			if(file!=null){
				gcSubject.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(file));
			}
			//获取二级课程视频
			gcSubject.setGcVideos(videoList.stream().filter(GcVideo -> gcSubject.getId().equals(GcVideo.getSubId())).collect(Collectors.toList()));
		}
		return subjectList;
	}

	@Override
	public List<GcSubject> buildSubject2(List<GcSubject> subjects, Integer userId, SysSystem sys, HttpServletRequest request,Integer envFlag){
		List<GcSubject> newSubjects = null;
		Integer masterId = Integer.parseInt(request.getHeader("masterId"));
		//课程列表
		if(CollectionUtils.isNotEmpty(subjects)) {
			//根据空间id、用户id查询出来的课程，后面直接根据课程id去查询，可以不用带空间id和userId也可以带,根据情况而定
			log.info("开始组装显示内容");
			List<Integer> subjectIds = subjects.stream().map(GcSubject::getId).collect(Collectors.toList());
			log.info("2、统计课程时长 单位秒");
			Map<Integer, GcSubject> subjectsDurationMap = sumSubjectDuration(subjectIds);

			List<GcVideo> videosBySubjectIds0 = gcVideoService.getVideosBySubjectIds0(subjectIds, userId,masterId);
			Map<Integer, List<GcVideo>> sub0Map = new HashMap<>(0);
			if(CollectionUtils.isNotEmpty(videosBySubjectIds0)){
				//按一级课程id
				sub0Map = videosBySubjectIds0.stream().filter(map->map.getSubId0()!=null).collect(Collectors.groupingBy(GcVideo::getSubId0));
			}
//					4、整体百分比进度
//				5、统计参与人数
			Map<Integer, GcUser> subjectUsers = gcUserService.getWatchedUserNum(subjectIds,masterId);

//			6、话题进度,查询评论总数和星级评价
			Map<Integer, GcUserVideoAction> subjectUserStar = this.getStarActions(subjectIds);
			
			//8、判断是传递了查询name参数，如果传了加载课程对应的视频数据
			newSubjects = new ArrayList<>(subjects.size());
			//获取二级视频下视频数量
			List<Integer> twoLevelSubId = this.baseMapper.getTwoLevelSubByIds(subjectIds);
			List<GcSubject> gcSubjects = this.baseMapper.getVideoNumByIds(twoLevelSubId);

			for(GcSubject sub : subjects) {
				Integer subjectId = sub.getId();
				SysFile subImgFile = sub.getSubImgFile();
				sysFileService.getResFullUrl(subImgFile,request);
				sub.setSubImgFile(subImgFile);
				//设置课程的时长 单位秒
				GcSubject subjectVL = subjectsDurationMap.get(subjectId);
				if(subjectVL != null){
					sub.setSubjectVideoDuration(subjectVL.getSubjectVideoDuration());
				}
				List<GcVideo> gcVideos = sub0Map.get(subjectId);
				if(CollectionUtils.isNotEmpty(gcVideos)) {
					Map<Integer, List<GcVideo>> sub1Map = gcVideos.stream().collect(Collectors.groupingBy(GcVideo::getSubId));//根据视频中的二级课程id在进行分组
					List<GcSubject> subjects1 = buildSubject1(sub1Map);
					sub.setSubjects(subjects1);//二级课程 没有视频也需要设置二级课程的值
					sub.setGcVideos(gcVideos);
				}

				if(CollectionUtils.isNotEmpty(sub.getSubjects())){
					for (GcSubject subject:sub.getSubjects()){
						subject.setGcVideos(null);
					}
				}
				sub.setGcVideos(null);

				//设置参与人数
				GcUser users = subjectUsers.get(subjectId);
				if(users != null){
					sub.setSubjectUsers(users.getSubjectUsers());
				}
				//设置评论数和星级评论数
				GcUserVideoAction videoActions = subjectUserStar.get(subjectId);
				if(videoActions != null) {
					//按type 进行分组
					// 1.2k type=3的平均值 1.2k是打星的总人数
					sub.setStarValue(videoActions.getSubjectStarAvg());//星级平均值
					// 打星总人数
					sub.setStarUsers(videoActions.getSubjectStarUsers());
				}
				
				//假数据
				if(masterId==107) {
					if(videoActions == null) {
						videoActions=new GcUserVideoAction();
						Double d = 0.0;
						Long l = (long) 0;
						videoActions.setSubjectStarAvg(d);
						videoActions.setSubjectStarUsers(l);
					}
					Double s = (videoActions.getSubjectStarAvg()*videoActions.getSubjectStarUsers()+4.5*20)/(videoActions.getSubjectStarUsers()+20);
					if(s<4)s=4.0;
					sub.setStarValue(s);
					long n = videoActions.getSubjectStarUsers()+20;
					if(n>100)n=99;
					sub.setStarUsers(n);
				}

				newSubjects.add(sub);
			}
		}
		return newSubjects;
	}
	
	//key=课程id，value=action
	@Override
	public Map<Integer, GcUserVideoAction>  getStarActions(List<Integer> subjectIds) {
//		6、话题进度,查询评论总数和星级评价
		Map<String, Object> videoParams = new HashMap<>(2);
		videoParams.put("subjectIds", subjectIds);
		videoParams.put("type", TableConstant.gcUserVideoAction_type_star3);
		return videoActionService.getSubjectUserStar(videoParams);
	}

	@Override
	public List<GcSubject> buildSubject1(Map<Integer, List<GcVideo>> sub1Map) {
		List<GcSubject> subjects = new ArrayList<>(sub1Map.keySet().size());
		for(Map.Entry<Integer, List<GcVideo>> sub1 : sub1Map.entrySet()){
			GcSubject subject = new GcSubject();
			subject.setId(sub1.getKey());//二级课程id
			List<GcVideo> value = sub1.getValue();
			subject.setGcVideos(value);//视频列表
			subjects.add(subject);
		}
		return subjects;
	}

	@Override
	public Integer getSubjectNum(List<Integer> subIds) {
		return this.baseMapper.getSubjectNum(subIds);
	}

	@Override
	public Map<Integer, GcSubject> sumSubjectDuration(List<Integer> subjectIds) {
		return  this.baseMapper.sumSubjectDuration(subjectIds);
	}

	@Override
	public PageInfo<GcSubject> listSubjectByFid(Map<String, Object> params, HttpServletRequest request,boolean ifLogin,List<Integer> subIds) {
		Object idObj = new Object();
		List<GcSubject> subjects = new ArrayList<>();
		PageInfo<GcSubject> page = new PageInfo<>();
		if(ifLogin) {
			idObj = params.get("fid");//一级课程id
			if (idObj == null) {
				return new PageInfo<>();//没有返回null
			}
			params.put("fid", Integer.parseInt(idObj.toString()));
			//查询课程信息
			page = listByFid(params, request);
			//加载视频信息 视频列表单独加载
			// 加载视频下的进度信息
			subjects = page.getList();
		}else {
			PageParam pageParam = new PageParam(request);
			Integer pageNum = pageParam.getPageNum();
			Integer pageSize=pageParam.getPageSize();
			if (pageNum > 0 && pageSize > 0) {
				PageHelper.startPage(pageNum, pageSize);
			}
			subjects = newUiGcSubjectMapper.select(subIds);
			page.setList(subjects);
		}
		if(CollectionUtils.isNotEmpty(subjects)){
			Integer userId = (Integer) params.get("userId");
			Integer masterId = request.getIntHeader("masterId");
			List<GcVideo> gcVideos = new ArrayList<>();
			Map<Integer, List<GcVideo>> videoCompleteStatus = new HashMap<>();
			//二级课程对应的视频列表
			if(ifLogin) {
				gcVideos = gcVideoService.getVideosBySubjectIds0(Arrays.asList(Integer.parseInt(idObj.toString())), userId,masterId);
				videoCompleteStatus = gcVideos.stream().filter(GcVideo -> Objects.nonNull(GcVideo.getId())).collect(Collectors.groupingBy(GcVideo::getSubId));//根据二级课程分组
			}else {
				gcVideos = gcVideoService.getVideoIdListBySubId0(subIds, userId,masterId);
				videoCompleteStatus = gcVideos.stream().filter(GcVideo -> Objects.nonNull(GcVideo.getId())).collect(Collectors.groupingBy(GcVideo::getSubId));//根据二级课程分组
			}
			List<GcSubject> vos = new ArrayList<>(subjects.size());
			for(GcSubject subject  : subjects){
				Integer id = subject.getId();
				List<GcVideo> tmpGcvideos = videoCompleteStatus.get(id);
				if(CollectionUtils.isNotEmpty(tmpGcvideos)){
					subject.setGcVideos(tmpGcvideos);
				}
				vos.add(subject);
			}
			page.setList(vos);
		}
		return page;
	}

}
