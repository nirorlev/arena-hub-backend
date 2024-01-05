package com.threeatom.guidecore.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.EnvType;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcVideoMapper;
import com.threeatom.guidecore.service.*;
import com.threeatom.guidecore.util.I18NUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.ApiAssert;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.mapper.NewUiGcSubjectMapper;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;

/**
* @author: rjunchao
* @date: 2021-8-15 10:16:46
* @desc: 新ui对应的课程service
*
*/
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

	@Autowired
	GcUserVideoPlayService gcUserVideoPlayService;

	@Resource
	NewUiGcSubjectMapper newUiGcSubjectMapper;

	@Autowired
	private GcSubjectService gcSubjectService;

	@Autowired
	@Lazy
	private GvgMasterService gvgMasterService;
//
//	@Autowired
//	private GcVideoService videoService;//视频业务类

	/**
	 * 分页查询课程信息
	 */
	@Override
	public PageInfo<GcSubject> list(Map<String, Object> params, SysSystem system, HttpServletRequest request,Integer envFlag) {

//		ApiAssert.notEmpty(id, "没有找到masterId");

		PageInfo<GcSubject> pageInfo = page(params, request);
		Integer userId = (Integer) params.get("userId");
		buildSubject(pageInfo, userId, system, request,envFlag);
		return pageInfo;
	}

	private PageInfo<GcSubject> page(Map<String, Object> params, HttpServletRequest request) {
		String referer = request.getHeader("referer");
//		Integer pageNum = 1;
//		Integer pageSize = 10;

		PageParam pageParam = new PageParam(request);
		Integer pageNum = pageParam.getPageNum();
		Integer pageSize=pageParam.getPageSize();


		String masterId = request.getHeader("masterId");
//		ApiAssert.notEmpty(masterId, "没有找到masterId");
		if(Objects.isNull(masterId)){
		throw new SystemException(I18NUtil.get("powtoon.portal.id.notfound"));
		}
		params.put("masterId", masterId);
//		if("https://govidigo.cn/".equals(referer)){
//			params.put("gvgFlag",TableConstant.COMMON_ONE);
//		}else {
//			params.put("gvgFlag",null);
//		}
		if (pageNum > 0 && pageSize > 0) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<GcSubject> gcSubjects = this.baseMapper.findSubjects(params);
		return new PageInfo<>(gcSubjects);
	}

	private PageInfo<GcSubject> listByFid(Map<String, Object> params, HttpServletRequest request) {
		/*Integer pageNum = 1;
		Integer pageSize = 10;
		try {
			pageNum = Integer.parseInt(params.get("pageNum") == null ? "1": params.get("pageNum").toString());
			pageSize = Integer.parseInt(params.get("pageSize") == null ? "8": params.get("pageSize").toString());
		} catch (Exception e) {}*/
		PageParam pageParam = new PageParam(request);
		Integer pageNum = pageParam.getPageNum();
		Integer pageSize=pageParam.getPageSize();

		String masterId = request.getHeader("masterId");
 //		ApiAssert.notEmpty(masterId, "没有找到masterId");
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
		List<GcSubject> list = this.baseMapper.findSubjects(params);
//		Integer userId = (Integer) params.get("userId");
//		return buildSubject2(list,userId,new SysSystem(),request,EnvType.GC.getCode());
		return list;
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
			//3、查询出话题list
			log.info("3、查询出话题list");//课程下视频播放进度
//			Map<Integer, List<GcVideo>> videoPlayMap = gcVideoService.getVideoCompleteStatusBySubject(subjectIds, userId);
			Map<Integer,Object> lastVideoPlayMap = gcUserVideoPlayService.getLastVideoPlayList(subjectIds,userId,masterId);

			List<GcVideo> videosBySubjectIds0 = gcVideoService.getVideosBySubjectIds0(subjectIds, userId,masterId,request, envFlag);
			Map<Integer, List<GcVideo>> sub0Map = new HashMap<>(0);
			if(CollectionUtils.isNotEmpty(videosBySubjectIds0)){
				//按一级课程id
				sub0Map = videosBySubjectIds0.stream().filter(map->map.getSubId0()!=null).collect(Collectors.groupingBy(GcVideo::getSubId0));
			}
//					4、整体百分比进度
//				5、统计参与人数
//			Map<Integer, GcUser> subjectUsers = gcUserService.getUsersBySubject(subjectIds,masterId);
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
//				log.info(sub.getId()+"");
//				log.info(sub.getName());
				//图片路径转换
				SysFile subImgFile = sub.getSubImgFile();
				sysFileService.getResFullUrl(subImgFile,request);
				sub.setSubImgFile(subImgFile);
				//设置课程的时长 单位秒
				GcSubject subjectVL = subjectsDurationMap.get(subjectId);
				if(subjectVL != null){
					sub.setSubjectVideoDuration(subjectVL.getSubjectVideoDuration());
				}
				//设置话题list 前端根据paly_state显示绿色或者黄色
//				List<GcVideo> gcVideos = videoPlayMap.get(subjectId);
//				if(CollectionUtils.isNotEmpty(gcVideos)) {
//					//设置
////					sub.setGcVideoCompletes(gcVideos);
//					sub.setGcVideos(gcVideos);//视频播放list,播放
//					//整体百分比进度
//					sub.setVideoProgressPercent(calcVideoProgressPercent(gcVideos));
//				}

				//设置二级课程的数量
				List<GcVideo> gcVideos = sub0Map.get(subjectId);
				if(CollectionUtils.isNotEmpty(gcVideos)) {
					Map<Integer, List<GcVideo>> sub1Map = gcVideos.stream().collect(Collectors.groupingBy(GcVideo::getSubId));//根据视频中的二级课程id在进行分组
					List<GcSubject> subjects1 = buildSubject1(sub1Map);
					sub.setSubjects(subjects1);//二级课程 没有视频也需要设置二级课程的值
					sub.setGcVideos(gcVideos);
					//整体百分比进度
//					sub.setVideoProgressPercent(calcVideoProgressPercent(gcVideos));
					SubjectTotals subjectTotals = gvgMasterService.calcTotals(subjects1,userId,true,masterId,envFlag);
					sub.setVideoProgressPercent(subjectTotals.getTotalProgressPercent());
					gcVideos.forEach(i->{
						if (null != i.getPlayState()){
							sub.setSubPlayState(TableConstant.VIDEO_PLAY_STATUS0);
						}
					});
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
				
				Integer videoNum  = gcSubjects.stream().filter(GcSubject -> sub.getId().equals(GcSubject.getFid())).collect(Collectors.toList()).stream().collect(Collectors.summingInt(GcSubject::getVideosTotalNum));
				sub.setVideosTotalNum(videoNum);
				Map map = (Map) lastVideoPlayMap.get(sub.getId());
				if (null != map){
					sub.setLastVideoId((Integer) map.get("videoId"));
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
		Map<Integer, GcUserVideoAction> subjectUserStar = videoActionService.getSubjectUserStar(videoParams);
		return subjectUserStar;
	}

	public List<GcSubject> orderUpdate(List<GcSubject> list, JSONArray jsonArray){
		List<GcSubject> subList = new ArrayList<>();
		List<GcSubject> newSubList = new ArrayList<>();
		if (null!=jsonArray){
		for (Object o : jsonArray) {
			for (GcSubject subject : list) {
				if (subject.getId().equals(o)){
					subList.add(subject);
				}
			}
		}
		for (GcSubject subject : list) {
			if (!jsonArray.contains(subject.getId())){
				newSubList.add(subject);
			}
		}
			subList.addAll(newSubList);
		}else {
			subList = list;
		}
		return subList;
	}

	/**
	 * 构建二级课程
	 * @param sub1Map
	 * @return
	 */
	@Override
	public List<GcSubject> buildSubject1(Map<Integer, List<GcVideo>> sub1Map) {
		List<GcSubject> subjects = new ArrayList<>(sub1Map.keySet().size());
		for(Map.Entry<Integer, List<GcVideo>> sub1 : sub1Map.entrySet()){
			GcSubject subject = new GcSubject();
			subject.setId(sub1.getKey());//二级课程id
			List<GcVideo> value = sub1.getValue();
			int min = value.stream().mapToInt(GcVideo::getCompleteStatus).min().getAsInt();
			int max = value.stream().mapToInt(GcVideo::getCompleteStatus).max().getAsInt();
			subject.setSubjectCompleteStatus(TableConstant.SUBJECT_COMPLETE_STATUS1);//
			if(min == max){//相同就设置为一个值
				subject.setSubjectCompleteStatus((short)max);
			}
			subject.setGcVideos(value);//视频列表
			subjects.add(subject);
		}
		return subjects;
	}

	@Override
	public List<GcSubject> getTagNameAndIds(Integer masterId, Integer userId,String tagText) {
		List<GcSubject> list = this.baseMapper.getTagNameAndIds(masterId,userId,tagText);
		return list;
	}

	@Override
	public Integer getSubjectNum(List<Integer> subIds) {
		return this.baseMapper.getSubjectNum(subIds);
	}

	/**
	 * 计算课程下视频的播放进度
	 * 	根据完成状态计算百分比
	 * @param userVideoPlays
	 * @return
	 */
	private int calcVideoProgressPercent(List<GcVideo> userVideoPlays) {
		if(CollectionUtils.isEmpty(userVideoPlays)){
			return 0;
		}

		List<GcVideo> finishs = userVideoPlays.stream().filter(e -> TableConstant.VIDEO_COMPLETE_STATUS2 == e.getCompleteStatus()).collect(Collectors.toList());
//		if(CollectionUtils.isEmpty(finishs)) {
//			return 0;
//		}

		List<GcVideo> harfs = userVideoPlays.stream().filter(e -> TableConstant.SUBJECT_COMPLETE_STATUS1 == e.getCompleteStatus()).collect(Collectors.toList());
		int harfSize = 0;
		if(CollectionUtils.isNotEmpty(harfs)){
			harfSize = harfs.size();
		}
		return calcPercent(userVideoPlays.size(), finishs.size(), harfSize);
	}

	private int calcPercent(int size, int finishsSize, int harfSize) {
		int sub = size - finishsSize;
		//全部看完了
		if (sub == 0) {
			return 100;
		}
		//部分看完 直接用总的-看完的
		//进度=play_state=1的数量*100%+play_state=0的数量*50% 比如看完2条，看过2条，一共10条，则进度为 30%
		//已完成的数量/总的数量，保留两位在乘以100
		//看完的百分比 绿色的
		BigDecimal playState1 = new BigDecimal(finishsSize).divide(new BigDecimal(size), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
		//看过的百分比 黄色的,进度新计算方式：只计算已经看完的视频数量占的百分比
		BigDecimal playState0 = new BigDecimal(harfSize).divide(new BigDecimal(size), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("50"));
		return playState1.intValue();
	}

	/**
	 * 	统计课程的时长，单位秒
	 */
	@Override
	public Map<Integer, GcSubject> sumSubjectDuration(List<Integer> subjectIds) {
		return  this.baseMapper.sumSubjectDuration(subjectIds);
	}
	public Map<Integer,List<GcSubject>> listSubjectByFidList(Map<String, Object> params, SysSystem sys, HttpServletRequest request,boolean ifLogin,List<Integer> accessPermissionId){
		List<GcSubject> subjects = new ArrayList<>();
		List<Integer> userList = (List<Integer>) params.get("userList");
		if (!ifLogin){
			subjects = newUiGcSubjectMapper.selectSubjectByAccessIds(accessPermissionId);
		}
		Map<Integer,List<GcSubject>> subjectMap = subjects.stream().collect(Collectors.groupingBy(GcSubject::getSubjectAssociationId));

		List<GcVideo> gcVideos = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(subjects)){
			Integer masterId = request.getIntHeader("masterId");
			if(!ifLogin) {
				gcVideos = gcVideoService.getVideoIdListByAccessId0(accessPermissionId,userList,masterId,request);
			}
		}
		for (Integer key: subjectMap.keySet()){
			List<GcSubject> subjectList = subjectMap.get(key);
			for (GcSubject subject : subjectList) {
				List<GcVideo> videoList = new ArrayList<>();
				for (GcVideo gcVideo : gcVideos) {
					if (Objects.isNull(subject.getUserId())||Objects.isNull(gcVideo.getUserId())||Objects.isNull(subject.getId())||Objects.isNull(gcVideo.getSubId())){
						continue;
					}
					if (subject.getUserId().equals(gcVideo.getUserId())&&subject.getId().equals(gcVideo.getSubId())){
						videoList.add(gcVideo);
					}
				}
				if (videoList.size()!=0){
					int min = videoList.stream().mapToInt(GcVideo::getCompleteStatus).min().getAsInt();
					int max = videoList.stream().mapToInt(GcVideo::getCompleteStatus).max().getAsInt();
					subject.setSubjectCompleteStatus(TableConstant.SUBJECT_COMPLETE_STATUS1);
					if(min == max){//相同就设置为一个值
						subject.setSubjectCompleteStatus((short)max);
					}
					subject.setGcVideos(videoList);
				}
			}
			subjectMap.put(key,subjectList);
		}

		return subjectMap;
	}
	/**
	 * 根据一级课程id查询二级课程及视频信息
	 *
	 * @param params
	 * @param request
	 * @return
	 */
	@Override
	public PageInfo<GcSubject> listSubjectByFid(Map<String, Object> params, SysSystem sys, HttpServletRequest request,boolean ifLogin,List<Integer> subIds,Integer envFlag) {
		Object idObj = new Object();
		List<GcSubject> subjects = new ArrayList<>();
		PageInfo<GcSubject> page = new PageInfo<>();
		if(ifLogin) {
			idObj = params.get("fid");//一级课程id
			if (idObj == null) {
				return new PageInfo<>();//没有返回null
			}
			//查询课程信息
			page = listByFid(params, request);
			//加载视频信息 视频列表单独加载
			// 加载视频下的进度信息
			subjects = page.getList();
		}else {
			PageParam pageParam = new PageParam(request);
			Integer pageNum = pageParam.getPageNum();
			Integer pageSize=pageParam.getPageSize();
//		 	if (pageNum == null) {
//	            pageNum = 0;
//	        }
//
//	        if (pageSize == null) {
//	            pageSize = 0;
//	        }
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
				gcVideos = gcVideoService.getVideosBySubjectIds0(Arrays.asList(Integer.parseInt(idObj.toString())), userId,masterId,request,envFlag);
				videoCompleteStatus = gcVideos.stream().filter(GcVideo -> Objects.nonNull(GcVideo.getId())).collect(Collectors.groupingBy(GcVideo::getSubId));//根据二级课程分组
			}else {
				gcVideos = gcVideoService.getVideoIdListBySubId0(subIds, userId,masterId,request,envFlag);
				videoCompleteStatus = gcVideos.stream().filter(GcVideo -> Objects.nonNull(GcVideo.getId())).collect(Collectors.groupingBy(GcVideo::getSubId));//根据二级课程分组
			}
			List<GcSubject> vos = new ArrayList<>(subjects.size());
			for(GcSubject subject  : subjects){
				Integer id = subject.getId();
				List<GcVideo> tmpGcvideos = videoCompleteStatus.get(id);
				if(CollectionUtils.isNotEmpty(tmpGcvideos)){
					subject.setGcVideos(tmpGcvideos);
					int min = tmpGcvideos.stream().mapToInt(GcVideo::getCompleteStatus).min().getAsInt();
					int max = tmpGcvideos.stream().mapToInt(GcVideo::getCompleteStatus).max().getAsInt();
					subject.setSubjectCompleteStatus(TableConstant.SUBJECT_COMPLETE_STATUS1);//
					if(min == max){//相同就设置为一个值
						subject.setSubjectCompleteStatus((short)max);
					}
				}
				vos.add(subject);
			}
			page.setList(vos);
		}
		return page;
	}



	/**
	 * 根据课程号查询上一个播放的视频ID
	 *
	 * @return
	 */
	@Override
	public Integer selectLastVideoId(Integer subId,Integer userId,Integer masterId) {
		return gcVideoMapper.selectLastVideoIdBySubjectId(subId,userId,masterId);
	}

	@Override
	public List<GcSubject> selectSubjects(List<Integer> subIds) {
		return newUiGcSubjectMapper.select(subIds);
	}
	@Override
	public List<String> selectAllTag(Integer masterId,Integer userId){
		return newUiGcSubjectMapper.selectAllTag(masterId,userId);
	}

	@Override
	public List<String> selectSubjectTag(Integer masterId, Integer userId) {
		return newUiGcSubjectMapper.selectSubjectTag(masterId,userId);
	}


}
