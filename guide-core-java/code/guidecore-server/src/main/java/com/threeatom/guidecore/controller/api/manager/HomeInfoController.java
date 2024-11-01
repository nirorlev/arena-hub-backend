package com.threeatom.guidecore.controller.api.manager;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.PageInfo;
import com.stripe.exception.StripeException;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.config.MyPortalConfiguration;
import com.threeatom.config.SiteMapConfiguration;
import com.threeatom.config.metarielConfig;
import com.threeatom.guidecore.constant.EnvType;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.service.*;
import org.apache.ibatis.annotations.Param;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.constant.AccessRoleType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import com.threeatom.system.service.SysSystemService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1/guidecore/homeInfo")
@Api(tags = "门户首页数据")
public class HomeInfoController extends GuideCoreController {

	private static final Logger LOGGER = LoggerFactory.getLogger(HomeInfoController.class);

	//  1 Add comments
	//	2 Don't hard code values, get them from the enum class
	//	3. Default image URL
	//	4. How to add meta configuration class
	//	5.Return error information
	@Value("${frontendPath}")
	private  String  hubUrl;

	@Autowired
	private metarielConfig metarielConfig;
	@Value("${subscription.id:0}")
	private List<Integer> subscriptionIdList;
	@Autowired
	private GcMasterHomeInfoService iGcMasterHomeInfoService;
	@Autowired
	GcMasterService masterService;
	@Autowired
	private SysFileService sysFileService;
    @Autowired
    private SysSystemService systemService;
    @Autowired
	private GcSubjectAssociationService gcSubjectAssociationService;
	@Autowired
    private Environment env;
	
	@Autowired
    private GcUserVideoPlayService userVideoPlayService; 
	
    @Autowired
    private GcSubjectService subjectService;
    
    @Autowired
    private GcVideoService gcVideoService;

    @Autowired
	private GcMasterService gcMasterService;

    @Autowired
	private GcUserVideoActionService gcUserVideoActionService;

    @Autowired
	private NewUiGcSubjectService newUiGcSubjectService;

    @Autowired
	private GcAccessService gcAccessService;

    @Autowired
	private GcUserAccessPermissionService gcUserAccessPermissionService;

	@Autowired
	private GcUserVideoActionService videoActionService;//用户视频操作--查询评论、点赞、星级评价

	@Autowired
	private GvgMasterService gvgMasterService;

	@Autowired
	private GcUserAccessService gcUserAccessService;

	@Autowired
	private MyPortalConfiguration myPortalConfiguration;

	@Autowired
	private SiteMapConfiguration siteMapConfiguration;

	@Autowired
	RedisOperator redisOperator;

	@Autowired
	private GcUserSaveFolderService gcUserSaveFolderService;
	@Autowired
	private PtChannelContentService ptChannelContentService;
	@Autowired
	private PtChannelService ptChannelService;
	@Autowired
	private GcContentGroupCourseAssignmentService contentGroupCourseAssignmentService;


	@ApiOperation(value = "保存首页信息，及保存老师、学生端的‘欢迎’‘指引’视频", httpMethod = "POST")
    @PostMapping("/saveOrUpdate")
    public Message saveOrUpdate(@RequestBody List<GcMasterHomeInfo> list,HttpServletRequest request) {
		LOGGER.info(JSONObject.toJSONString(list));
		GcMaster master=this.getMaster();
		if(Objects.isNull(master)){
			master = masterService.getMasterById(request.getIntHeader("masterId"));
		}
		
		if(iGcMasterHomeInfoService.saveGcMasterHomeInfo(master.getId(), list)){
			return new Message().ok();
		}
		return new Message().error();
    }
	@ApiOperation(value = "获取首页信息", httpMethod = "GET")
    @GetMapping("/get")
	 public Message get(HttpServletRequest request) {
		GcMaster master=this.getMaster();
		SysSystem sys = this.getSystem();
		return new Message().ok().addData("homeInfoList", iGcMasterHomeInfoService.getGcMasterHomeInfoList(master.getId(), TableConstant.gcMasterHomeInfo_name_homepage_list, sys, request));
    }
	
	@ApiOperation(value = "获取欢迎视频信息", httpMethod = "GET")
    @GetMapping("/getWelcomeVideos")
	 public Message getWelcomeVideos(HttpServletRequest request) {
		GcMaster master=this.getMaster();
		SysSystem sys = this.getSystem();
		return new Message().ok().addData("welcomeVideosList", iGcMasterHomeInfoService.getGcMasterHomeInfoList(master.getId(), TableConstant.gcMasterHomeInfo_name_welcomeVideo_list, sys, request));
    }

	@ApiOperation(value = "用户端获取欢迎视频信息，学生及老师", httpMethod = "GET")
	@GetMapping("/getWelcomeVideosUserSide/{userRole}")
	public Message getWelcomeVideosUserSide(@PathVariable("userRole") Integer userRole,HttpServletRequest request) {

		List<String> list = null;
		Integer masterId = getHeaderMasterId(request);
		if(AccessRoleType.STUDENT==userRole) {
			this.assertUserStudent(masterId);
			list=TableConstant.gcMasterHomeInfo_name_welcomeVideo_stuList;
		}
		if(AccessRoleType.TEACHER==userRole) {
			this.assertUserTeacher(masterId);
			list=TableConstant.gcMasterHomeInfo_name_welcomeVideo_mentList;
		}
		SysSystem sys = this.getSystem();
		return new Message().ok().addData("welcomeVideosList", iGcMasterHomeInfoService.getGcMasterHomeInfoList(masterId, list, sys, request));
    }

    public GcMaster getMaster(String portalId) {
    	GcMaster gcMaster = masterService.getMasterByContext(portalId);
    	if(gcMaster==null) {
    		return null;
    	}
		if(gcMaster.getIntroVideoFile()!=null) {
        	sysFileService.getResFullUrlSaveType2(gcMaster.getIntroVideoFile());
        	gcMaster.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(gcMaster.getIntroVideoFile()));
        }
    	gcMaster.setLogoFullUrl(sysFileService.getResFullUrlSaveType2(gcMaster.getLogoFile()));
    	return gcMaster;
    }

    @PostMapping("/getSubByLevel0Sub")
    public Message getSubByLevel0Sub(@RequestBody JSONObject requestParams,HttpServletRequest request) {
    	Message m = new Message();
    	
    	String portalId = requestParams.getString("portalId");
    	GcMaster gcMaster = this.getMaster(portalId);
    	if(gcMaster==null) {
    		return m.error(I18NUtil.get("guidecore.getForHome.portalIdNotExist"));
    	}
    	Integer level0subId = requestParams.getInteger("level0subId");
    	String level0subNameIndex = requestParams.getString("level0subName");
    	
    	
    	List<GcSubject> level0sublist = subjectService.getLevel0SubLis(gcMaster.getId());

    	String sysIds = env.getProperty("systemId");
    	int sysId = Integer.parseInt(sysIds);
    	SysSystem sys = systemService.getSystemById(sysId);
    	
    	GcSubject subject = new GcSubject();
    	subject.setId(level0subId);
    	subject.setNameIndex(level0subNameIndex);
    	subject.setMasterId(gcMaster.getId());
    	JSONArray a = userVideoPlayService.getSubAndVideoPlayListForHome(subject, sys,request);
    	
    	m.addData("gcMaster", gcMaster);
    	m.ok().addData("subjectList", level0sublist);
    	m.ok().addData("topicList", a);

    	return m;
    }
	
    @PostMapping("/getVideoByLevel0SubNameAndVideoName")
    public Message getVideoByLevel0SubNameAndVideoName(@RequestBody JSONObject requestParams,HttpServletRequest request) {
    	String portalId = requestParams.getString("portalId");
    	String videoNameIndex = requestParams.getString("videoName");
    	String level0subNameIndex = requestParams.getString("level0subName");
    	Message m = new Message();
    	
    	GcMaster gcMaster = this.getMaster(portalId);
    	if(gcMaster==null) {
    		return m.error(I18NUtil.get("guidecore.getForHome.portalIdNotExist"));
    	}
    	GcVideo video = null;
    	List<GcVideo> videoList = gcVideoService.selectVideoByVideoAndSub0NameIndex(videoNameIndex,level0subNameIndex,gcMaster.getId());
    	if(videoList!=null && !videoList.isEmpty()) {
			video=videoList.get(0);
		}
    	
    	if(video!=null) {
			video.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(video));
		}
    	
    	m.ok().addData("video", video);
    	m.addData("gcMaster", gcMaster);
    	return m;
    }
    
    
    @PostMapping("/ipn5")
    public void ipn(@RequestBody JSONObject requestParams) {

    }
    
    
    @PostMapping("/ipn1")
    public void ipn1(@RequestBody JSONObject requestParams2,HttpServletRequest request) {
    	//获取参数名称
    	Enumeration<String> requestParams = request.getParameterNames();
    	//遍历获取参数
    	while (requestParams.hasMoreElements()) {
    	    String param = requestParams.nextElement();
    	    String value = request.getParameter(param);
            System.out.println("ipn回调-----"+param+":"+value);
    	}
    	
    	int a=1;
    }


	@ApiOperation(value="home页面,package查询", httpMethod = "GET")
	@GetMapping("/packageList")
	public Message packageList(@RequestParam(required = false) Integer yearlyFlag, HttpServletRequest request) throws StripeException {
		Message message = new Message();
        Integer masterId = request.getIntHeader("masterId");
		if(Objects.isNull(masterId)){
			throw new SystemException(I18NUtil.get("guidecore.unlogin.error"));
		}
		Integer status = gcSubjectAssociationService.selectPackageStatus(masterId);
		if (Objects.nonNull(status)&&status.equals(TableConstant.COMMON_ONE)){
			return new Message().ok();
		}
		List<GcAccess> packageList = new ArrayList<>();
		List<GcAccess> subcriptionPackageList = new ArrayList<>();
		List<Integer> assignedCourseIds = new ArrayList<>();

		if(myPortalConfiguration.getFreeBookSummaryPortalId() == masterId){
			packageList = gcAccessService.getAllPackage(masterId,new PageParam(request),null,subscriptionIdList);
			List<Integer> newIdList = packageList.stream().map(GcAccess::getId).collect(Collectors.toList());
			List<Integer> idList = new ArrayList<>();
			for(Integer id : subscriptionIdList){
				if(!newIdList.contains(id)){
					idList.add(id);
				}
			}
			subcriptionPackageList = gcAccessService.getAllPackage(masterId,new PageParam(request),idList,null);
			packageList.addAll(subcriptionPackageList);
		}else {
			packageList = gcAccessService.getAllPackage(masterId,new PageParam(request),null,null);
		}
		//my门户单独配置
		if(Objects.nonNull(yearlyFlag)){
			packageList = gcAccessService.getAllPackage(masterId,new PageParam(request),subscriptionIdList,null);
		}
        //初始化套餐下的平均星级，评星人数，课程总时长,课程id
		for(GcAccess packages :packageList){
        	packages.setOwnedFlag(TableConstant.COMMON_ZERO);
        	packages.setPackageCourseStarUsers(TableConstant.LONG_ZERO);
        	packages.setPackageCourseAvgStars(TableConstant.DOUBLE_ZERO);
        	packages.setPackageCourseTotalTime(TableConstant.LONG_ZERO);
        	//待优化，套餐封面package
			if(null!=packages.getPackageImgId()) {
				SysFile imgFile = sysFileService.getById(packages.getPackageImgId());
				sysFileService.getResFullUrl(imgFile, request);
				packages.setPackageImgFile(imgFile);
			}
			if(null!=packages.getPackageVideoFileId()){
                SysFile videoFile = sysFileService.getById(Integer.parseInt(packages.getPackageVideoFileId()));
                String fullUrl = sysFileService.getVideoSnapshotUrl(videoFile);
                packages.setPackageSnapShotUrl(fullUrl);
			}
			List<Integer> courseIdsByContentGroupId =
				contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(packages.getId());
			assignedCourseIds.addAll(courseIdsByContentGroupId);
		}

		if(!assignedCourseIds.isEmpty()) {
			//计算package下所有课程的总时长,赋值到packagelist中
			Map<Integer, GcSubject> subjectsDurationMap = newUiGcSubjectService.sumSubjectDuration(assignedCourseIds);
			for (Integer key : subjectsDurationMap.keySet()) {
				for (GcAccess packages : packageList) {
					Integer packageCourseTotalTime = Integer.parseInt(packages.getPackageCourseTotalTime().toString());
					if (contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(packages.getId()).contains(key)) {
						Integer courseTimes = Integer.parseInt(subjectsDurationMap.get(key).getSubjectVideoDuration().toString());
						packageCourseTotalTime += courseTimes;
						packages.setPackageCourseTotalTime(packageCourseTotalTime.longValue());
					}
				}
			}

			//计算package下所有课程的平均星级
			Map<String, Object> videoParams = new HashMap<>(2);
			Integer ids = TableConstant.COMMON_ZERO;
			videoParams.put("ids", ids);
			videoParams.put("subjectIds", assignedCourseIds);
			videoParams.put("type", TableConstant.gcUserVideoAction_type_star3);
			List<Integer> starKeys = new ArrayList<>();
			Map<Integer, GcUserVideoAction> subjectUserStar = videoActionService.getSubjectUserStar(videoParams);
			Iterator starAvgTimes = subjectUserStar.entrySet().iterator();
			while (starAvgTimes.hasNext()) {
				Map.Entry entry = (Map.Entry) starAvgTimes.next();
				starKeys.add(Integer.parseInt(entry.getKey().toString()));
			}
			//赋值给package
			for (Integer key : subjectUserStar.keySet()) {
				for (GcAccess packages : packageList) {
					Long PackageTotalStarUsers = packages.getPackageCourseStarUsers();
					Double PackageAvgStars = packages.getPackageCourseAvgStars();
					List<Integer> courseIdsByContentGroupId =
						contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(packages.getId());

					if (courseIdsByContentGroupId.contains(key)) {
						Long starUsers = subjectUserStar.get(key).getSubjectStarUsers();
						Double avgStars = subjectUserStar.get(key).getSubjectStarAvg();
						Integer times = TableConstant.COMMON_ZERO;
						PackageTotalStarUsers += starUsers;
						PackageAvgStars += avgStars;
						packages.setPackageCourseStarUsers(PackageTotalStarUsers);
						packages.setPackageCourseAvgStars(PackageAvgStars);
						for (Integer avgTimes : starKeys) {
							if (courseIdsByContentGroupId.contains(avgTimes) & subjectUserStar.get(avgTimes).getSubjectStarAvg() != TableConstant.DOUBLE_ZERO) {
								times++;
							}
						}
						packages.setTimes(times);
					}
				}
			}

			PageInfo<GcAccess> pageInfo = new PageInfo<>(packageList);
			//计算平均星级
			List<GcAccess> newPackageList = packageList.stream().map(singlePackage -> {
				BigDecimal times = new BigDecimal(singlePackage.getTimes()==null ? 0 : singlePackage.getTimes());
				BigDecimal totalStars = new BigDecimal(singlePackage.getPackageCourseAvgStars());
				BigDecimal avgStars = times.compareTo(BigDecimal.ZERO)==0? new BigDecimal("0"): totalStars.divide(times,BigDecimal.ROUND_DOWN);
				singlePackage.setPackageCourseAvgStars(avgStars.doubleValue());
				return singlePackage;
			}).collect(Collectors.toList());



			if(myPortalConfiguration.getFreeBookSummaryPortalId()==masterId){

				List<GcAccess> subscriptionList = new ArrayList<>();
				Iterator<GcAccess> iterator = newPackageList.iterator();
				while (iterator.hasNext()){
					GcAccess gcAccess = iterator.next();
					if(subscriptionIdList.contains(gcAccess.getId())){
						subscriptionList.add(gcAccess);
						iterator.remove();
					}
				}
				PageInfo<GcAccess> subscriptionPageInfo = new PageInfo<>(subscriptionList);
				message.ok().addData("subscriptionList",JSON.parse(JSON.toJSONString(subscriptionPageInfo)));
 			}
			pageInfo.setList(newPackageList);
			message.ok().addData("packageList", pageInfo);


			String token = request.getHeader("Authorization");
			if (null!=token && !"".equals(token) && !"undefined".equals(token)) {
				GcUser user = this.getGcUser();
			}
			message.ok().addData("packageList", pageInfo);
		}else {
			message.ok().addData("packageList",new PageInfo<GcAccess>());
		}
		return message.ok();

	}

	@ApiOperation(value="home页面,package查询", httpMethod = "GET")
	@GetMapping("/packageDetail")
	public Message packageDetail(@Param("accessId")Integer accessId, HttpServletRequest request) throws StripeException {
		if(Objects.isNull(accessId)){
			throw new SystemException(I18NUtil.get("guidecore.package.detail.error"));
		}
		Message message = new Message();
		Integer masterId = request.getIntHeader("masterId");
		GcMaster gcMaster = masterService.getMasterById(masterId);
        GcAccess gcAccess = gcAccessService.getAccessById(accessId);
		List<Integer> contentGroupIds = contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(gcAccess.getId());

        gcAccess.setOwnedFlag(TableConstant.COMMON_ZERO);
        if(null != gcAccess.getPackageImgId()) {
			SysFile imgFile = sysFileService.getById(gcAccess.getPackageImgId());
			String fullUrl = sysFileService.getResFullUrl(imgFile, request);
			gcAccess.setPackageImgFullUrl(fullUrl);
			gcAccess.setFileType(imgFile.getFileType());
		}
        if(null != gcAccess.getPackageVideoFileId()){
        	SysFile videoFile = sysFileService.getById(Integer.parseInt(gcAccess.getPackageVideoFileId()));
        	String snapShotUrl = sysFileService.getVideoSnapshotUrl(videoFile);
        	sysFileService.getResFullUrl(videoFile, request);
        	videoFile.setSnapshotUrl(snapShotUrl);
			gcAccess.setPackageVideoFile(videoFile);
		}
		//时长
		Map<Integer, GcSubject> subjectsDurationMap=newUiGcSubjectService.sumSubjectDuration(contentGroupIds);
		Long learningHours = TableConstant.LONG_ZERO;
		for(Integer key : subjectsDurationMap.keySet()){
            learningHours += subjectsDurationMap.get(key).getSubjectVideoDuration();
		}

		//视频数量
		List<GcVideo> videoList = gcVideoService.getVideosBySubjectIds0(contentGroupIds,TableConstant.COMMON_ZERO,masterId,request, EnvType.GC.getCode());
		Map<Integer,List<GcVideo>> videoMap = videoList.stream().collect(Collectors.groupingBy(GcVideo::getSubId));
		//星级
		Map<String, Object> videoParams = new HashMap<>(2);
		Integer ids = TableConstant.COMMON_ZERO;
		videoParams.put("ids",ids);
		videoParams.put("subjectIds", contentGroupIds);
		videoParams.put("type", TableConstant.gcUserVideoAction_type_star3);
		Map<Integer, GcUserVideoAction> subjectUserStar = videoActionService.getSubjectUserStar(videoParams);
		Double totalStars = TableConstant.DOUBLE_ZERO;
        Long totalStarsUsers = TableConstant.LONG_ZERO;
        Integer times = TableConstant.COMMON_ZERO;
		for(Integer key : subjectUserStar.keySet()){
			if(subjectUserStar.get(key).getSubjectStarAvg()!=null) {
				totalStars += subjectUserStar.get(key).getSubjectStarAvg();
                times++;
			}
			if(subjectUserStar.get(key).getSubjectStarUsers()!=null){
				totalStarsUsers += subjectUserStar.get(key).getSubjectStarUsers();
			}
		}
		if(totalStars!=TableConstant.DOUBLE_ZERO && times!=TableConstant.COMMON_ZERO){
		    BigDecimal stars = new BigDecimal(totalStars);
		    BigDecimal time = new BigDecimal((times));
		    gcAccess.setPackageCourseAvgStars(stars.divide(time,2).doubleValue());
        }
		//话题list
		Map<String, Object> subjectParams = new HashMap<>();
        subjectParams.put("userId",TableConstant.COMMON_ZERO);
		subjectParams.put("masterId",masterId);
		subjectParams.put("subjectIds",contentGroupIds);

		List<GcSubject> subjects = subjectService.getSubListByIds(contentGroupIds,request);
		List<GcSubject> level1Subjects = subjectService.selectAllLevel1SubList(contentGroupIds,null,masterId);
		for(GcSubject level1Subject : level1Subjects){
			if(videoMap.get(level1Subject.getId())!=null){
				level1Subject.setVideosTotalNum(videoMap.get(level1Subject.getId()).size());
				if(videoMap.get(level1Subject.getId())!=null ) {
					List<GcVideo> videos = videoMap.get(level1Subject.getId());
                    Integer videoTotalLong = videos.stream().filter(e->e.getVideoTime()!=null).mapToInt(GcVideo::getVideoTime).sum();
					level1Subject.setVideosTotalLong(videoTotalLong);

					level1Subject.setVideoChildList(videoMap.get(level1Subject.getId()));
					level1Subject.setVideosTotalNum(videoMap.get(level1Subject.getId()).size());
				}

			}else {
				level1Subject.setVideosTotalNum(TableConstant.COMMON_ZERO);
			}
			Integer videosLongInTopic = TableConstant.COMMON_ZERO;
			for(GcVideo gcVideo : videoList){
				if(gcVideo.getSubId().equals(level1Subject.getId())){
					if(gcVideo.getVideoTime()!=null) {
						videosLongInTopic += gcVideo.getVideoTime();
					}
				}
				level1Subject.setVideosTotalLong(videosLongInTopic);
			}
		}

		Map<Integer ,List<GcSubject>> map = level1Subjects.stream().collect(Collectors.groupingBy(GcSubject::getSubId));
		for(GcSubject gcSubject :subjects){
			if (map.get(gcSubject.getId())!=null){
				gcSubject.setSubjects(map.get(gcSubject.getId()));
			}
		}

		String token = request.getHeader("Authorization");
		if(token != null && !token.isEmpty() && !("undefined").equals(token)){
			GcUser user = this.getGcUser();
			if (user != null) {
				}else {
					gcAccess.setOwnedFlag(TableConstant.COMMON_ZERO);
				}
				GcUserAccess gcUserAccess = this.gcUserAccessService.getAccessByUserIdMaster(user.getId(),masterId);
				if(Objects.nonNull(gcUserAccess)) {
					GcAccess gcaccess = gcAccessService.getAccessById(gcUserAccess.getAccessId());
					message.addData("userAccess", gcaccess);
				}
		}

		return message.ok().addData("package",gcAccess)
				.addData("Modules",level1Subjects.size())
				.addData("learningHours",learningHours)
				.addData("videoNum",videoList.size())
				.addData("totalStarUsers",totalStarsUsers)
				.addData("totalAvgStars",totalStars)
				.addData("master",gcMaster)
				.addData("subjectsLevel0",subjects);
	}

	@GetMapping("/sitemap")
	public void allSubIdVidInMaster(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException, ParseException {
		Document document = DocumentHelper.createDocument();
		Element headElement = document.addElement("urlset","http://www.sitemaps.org/schemas/sitemap/0.9");
		QName qName = DocumentHelper.createQName("xmlns");
		Attribute attribute1 = DocumentHelper.createAttribute(headElement,"xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
		Attribute attribute2 = DocumentHelper.createAttribute(headElement,"xsi:schemaLocation","http://www.sitemaps.org/schemas/sitemap/0.9\n" +
				"http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd");

		String host = request.getHeader("host");
		GcMaster gcMaster = new GcMaster();
		if(host.equals(siteMapConfiguration.getSiteUrl())){
			gcMaster = gcMasterService.getMasterById(siteMapConfiguration.getDefaultId());
		}else {
			Integer contextIndex = host.indexOf(".");
			String context = host.substring(0,contextIndex);
			gcMaster = gcMasterService.getMasterByContext(context);
		}

		if(Objects.isNull(gcMaster)){
			Element element =headElement.addElement("Error");
			Element element1 = element.addElement("requestUrl");
			Element element2 = element.addElement("siteUrl");
			Element element3 = element.addElement("Message");
			Element element4 = element.addElement("host");
			element1.setText(host);
			element2.setText(siteMapConfiguration.getSiteUrl());
			element3.setText("Portal doesn't exist");
			element4.setText(request.getHeader("host"));
		}else if(gcMaster.getState().equals(TableConstant.COMMON_ONE)) {
			List<GcSubject> gcSubjectList = subjectService.getSubList(gcMaster.getId(),0);
			List<GcSubject> gcSubjectAssoList = subjectService.selectSubjectAssociation(gcMaster.getId(),null,true);
			List<Integer> subIdList = gcSubjectList.stream().map(GcSubject::getId).collect(Collectors.toList());
			List<Integer> gcSubjectAssoIdList = gcSubjectAssoList.stream().map(GcSubject::getId).collect(Collectors.toList());
			subIdList.addAll(gcSubjectAssoIdList);
			List<GcVideo> videoList = gcVideoService.getVideoListBySubId(subIdList);
			gcSubjectList.addAll(gcSubjectAssoList);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

			//筛选出首页数据的最新更新时间
			Date subjectMaxDate = gcSubjectList.stream().max(Comparator.comparing(GcSubject::getUpdateTime)).get().getUpdateTime();
			Date masterMaxDate = gcMaster.getUpdateTime();
			int compare = subjectMaxDate.compareTo(masterMaxDate);
			if(!host.contains(siteMapConfiguration.getSiteUrl())){
				Element element =headElement.addElement("Error");
				Element element1 = element.addElement("requestUrl");
				Element element2 = element.addElement("siteUrl");
				Element element3 = element.addElement("Message");
				Element element4 = element.addElement("host");
				element1.setText(host);
				element2.setText(siteMapConfiguration.getSiteUrl());
				element3.setText("Invalid host,please format the url like the siteUrl");
				element4.setText(request.getHeader("host"));
			}else {
				//homepage
				Element element = headElement.addElement("url");
				Element iocElement = element.addElement("loc");
				iocElement.setText("https://"+host);
				Element lastModEl = element.addElement("lastmod");
				if (compare < 0) {
					lastModEl.setText(df.format(masterMaxDate.getTime()));
				} else {
					lastModEl.setText(df.format(subjectMaxDate.getTime()));
				}

				headElement.add(attribute1);
				headElement.add(attribute2);
				for (GcSubject subject : gcSubjectList) {
					Element bodyElement = headElement.addElement("url");
					Element ioc = bodyElement.addElement("loc");
					ioc.setText("https://"+host + "/course-statics/" + subject.getId());
					Element lastMod = bodyElement.addElement("lastmod");
					String time = df.format(subject.getUpdateTime());
					lastMod.setText(time);
				}
				for (GcVideo gcVideo : videoList) {
					Element bodyElement = headElement.addElement("url");
					Element ioc = bodyElement.addElement("loc");
					ioc.setText("https://"+host + "/course-video/" + gcVideo.getId());
					Element lastMod = bodyElement.addElement("lastmod");
					String time = df.format(gcVideo.getUpdateTime());
					lastMod.setText(time);
				}
			}

		}

		OutputFormat outputFormat = OutputFormat.createPrettyPrint();
		outputFormat.setEncoding("UTF-8");
		outputFormat.setNewLineAfterDeclaration(false);
		outputFormat.setNewlines(true);
		outputFormat.setTrimText(true);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try{
			XMLWriter xmlWriter = new XMLWriter(byteArrayOutputStream,outputFormat);
			xmlWriter.write(document);
			xmlWriter.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try{
			httpServletResponse.reset();
			httpServletResponse.setContentType("application/xml");
			httpServletResponse.addHeader("Access-Control-Allow-Origin","*");
			httpServletResponse.setCharacterEncoding("utf-8");
			OutputStream outputStream = httpServletResponse.getOutputStream();
			outputStream.write(byteArrayOutputStream.toByteArray());
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	@GetMapping({"/html${frontendPath}/index.html", "/html${frontendPath}/indexFromCloudfront.html", "/html${frontendPath}", "/html${frontendPath}/"})
	public void html(HttpServletRequest request,HttpServletResponse response) {
		String xRequestUri = request.getHeader("x-request-uri");
		LOGGER.info("[SSR] Method: "+ request.getMethod()+", URI: "+request.getRequestURI()+", x-request-uri: "+request.getHeader("x-request-uri"));
		if(xRequestUri!=null){
			if (xRequestUri.endsWith("/")) {
				xRequestUri = xRequestUri.substring(0, xRequestUri.length() - 1);
			}
		}
		String fullFileUrl = "";
		String host = request.getHeader("host");
		Integer subOrVid = null;
		String channelUrlId=null;
		Boolean containNumber = false;
		Pattern pattern = Pattern.compile("[0-9]+");
		if(Objects.nonNull(xRequestUri)) {
			Matcher matcher = pattern.matcher(xRequestUri);
			containNumber = matcher.find();
		}
		if(Objects.nonNull(xRequestUri) && !"".equals(xRequestUri) && !"undefined".equals(xRequestUri) && !"/".equals(xRequestUri)) {
			if (xRequestUri.contains("?")) {
				xRequestUri = xRequestUri.substring(0, xRequestUri.indexOf("?"));
			}
			if(containNumber) {
				if (xRequestUri.contains(metarielConfig.getCourse()) || xRequestUri.contains(metarielConfig.getPlaylist())) {
					subOrVid = Integer.parseInt(xRequestUri.substring(xRequestUri.lastIndexOf("/") + 1, xRequestUri.length()));
				}
			}
			if (xRequestUri.contains(metarielConfig.getChannel()) ) {
				channelUrlId = xRequestUri.substring(xRequestUri.lastIndexOf("/") + 1, xRequestUri.length());
				channelUrlId.trim();
			}
		}

	String addMetaContent = "";
	String desc = "";
	String title = "";
	String homeUrl="";
	GcMaster gcMaster = new GcMaster();
	if (hubUrl!=""){
		homeUrl=hubUrl+"/";
	}
		Integer stats=0;
		Integer folderId = null;
		Integer courseId= null;
		String channelName =null;
		String[] split = xRequestUri.split("/");
		if (containNumber) {
			if ((hubUrl != "" && split.length == 4 && split[2].trim().equals("course")) ||
					(hubUrl == "" && split.length == 3 && split[1].trim().equals("course"))) {
				stats = 1;
				System.out.println("course/123");
			} else if ((hubUrl != "" && split.length == 5 && split[2].trim().equals("course")) ||
					(hubUrl == "" && split.length == 4 && split[1].trim().equals("course"))) {
				stats = 2;
				courseId = Integer.valueOf(split[split.length - 2]);
				System.out.println("course/123/123");
			} else if ((hubUrl != "" && split.length == 4 && split[2].trim().equals("playlist")) ||
					(hubUrl == "" && split.length == 3 && split[1].trim().equals("playlist"))) {
				stats = 3;
				System.out.println("playlist/123");
			} else if ((hubUrl != "" && split.length == 5 && split[2].trim().equals("playlist")) ||
					(hubUrl == "" && split.length == 4 && split[1].trim().equals("playlist"))) {
				stats = 4;
				folderId = Integer.valueOf(split[split.length - 2]);
				System.out.println("/playlist/121/8015");
			}else if ((hubUrl != "" && split.length == 5 && split[2].trim().equals("channel")) ||
					(hubUrl == "" && split.length == 4 && split[1].trim().equals("channel"))) {
				stats = 6;
				channelName=split[split.length - 2];
				System.out.println("channel/test1/123");
			}
		}
		if ((hubUrl != "" && split.length == 4 && split[2].trim().equals("channel")) ||
				(hubUrl == "" && split.length == 3 && split[1].trim().equals("channel"))) {
			stats = 5;
			System.out.println("channel/test1");
		}
		if(Objects.isNull(xRequestUri) || "".equals(xRequestUri) || "undefined".equals(xRequestUri) || "/".equals(xRequestUri)
				|| hubUrl.equals(xRequestUri) || homeUrl.equals(xRequestUri)){
			if(host.equals(siteMapConfiguration.getSiteUrl())){
				gcMaster = gcMasterService.getMasterById(siteMapConfiguration.getDefaultId());
			}else {
				Integer contextIndex = host.indexOf(".");
				String context = host.substring(0,contextIndex);
				gcMaster = gcMasterService.getMasterByContext(context);
			}
			if(Objects.nonNull(gcMaster.getLogoId())){
				SysFile sysFile = sysFileService.getById(gcMaster.getLogoId());
				fullFileUrl = sysFileService.getResFullUrl(sysFile,request);
			}
			if(Objects.isNull(host)){
				host = "";
			}
			if(Objects.isNull(gcMaster.getBrandDescription())){
				gcMaster.setBrandDescription("");
			}
			if(Objects.isNull(gcMaster.getPortalName())){
				gcMaster.setPortalName("");
			}

			addMetaContent=metaHtmlConfig(gcMaster,"homepageShareTitle","homepageShareDesc","homepageShareImg",host,request);


		}
		else if(stats==3 && containNumber){
			GcUserSaveFolder gcUserSaveFolder = gcUserSaveFolderService.getPlayListMetaConfig(subOrVid,null);
			if(Objects.isNull(gcUserSaveFolder)||gcUserSaveFolder.getSaveContentList().size()== TableConstant.COMMON_ZERO){
				return;
			}
			SysFile sysFile = sysFileService.getById(gcUserSaveFolder.getSaveContentList().get(0).getFileId());
			desc="Playlist last updated "+gcUserSaveFolder.getUpdateTime();
			title=gcUserSaveFolder.getName();
			addMetaContent = playListMetaConfig(sysFile,gcUserSaveFolder,host,request,title,desc);
		}
		else if(stats==4 && containNumber){
			GcUserSaveFolder gcUserSaveFolder = gcUserSaveFolderService.getPlayListMetaConfig(folderId,subOrVid);
			SysFile sysFile = sysFileService.getById(subOrVid);
			title = "\"" + sysFile.getName() + "\"" + " in " + "\"" + gcUserSaveFolder.getName() + "\"" + " playlist";
			addMetaContent = playListMetaConfig(sysFile,gcUserSaveFolder,host,request,title,sysFile.getDescribe());
		}
		else if(stats==5){
			PtChannel ptChannel= ptChannelService.getbyChannelSlug(channelUrlId);
			if(Objects.isNull(ptChannel)){
				return;
			}
			SysFile sysFile = sysFileService.getById(ptChannel.getChannelImgFileId());
			fullFileUrl = sysFileService.getResFullUrl(sysFile,request);
			if(Objects.isNull(fullFileUrl)){
				fullFileUrl = "";
			}
			if(Objects.isNull(ptChannel.getChannelName())){
				ptChannel.setChannelName("");
			}
			if(Objects.isNull(ptChannel.getDesc())){
				ptChannel.setDesc("");
			}
			addMetaContent = metaHtml(channelUrlId, ptChannel.getDesc(), fullFileUrl, host, request);
		}
		else if(stats==6 && containNumber){
			PtChannelContent ptChannelContent = ptChannelContentService.getById(Integer.parseInt(channelUrlId));
			if(Objects.isNull(ptChannelContent)){
				return;
			}
			SysFile sysFile = sysFileService.getById(ptChannelContent.getFileId());
			GcVideo videoContent = gcVideoService.getById(ptChannelContent.getContentId());
			if(sysFile.getFileTypeIndex().equals(13)){
				String fileUrl = sysFile.getFileUrl();
				String youtubeId = fileUrl.substring(fileUrl.lastIndexOf("/")+1,fileUrl.length());
				fullFileUrl = "https://i.ytimg.com/vi/"+youtubeId+"/hqdefault.jpg";
			}else if(sysFile.getFileTypeIndex().equals(14)){
				String fileUrl = sysFile.getFileUrl();
				String vimeoId = fileUrl.substring(fileUrl.lastIndexOf("/")+1,fileUrl.length());
				fullFileUrl = "https://vumbnail.com/"+vimeoId+"/_large.jpg";
			}else {
				fullFileUrl = sysFileService.getVideoSnapshotUrl(videoContent);
			}
			if(Objects.isNull(fullFileUrl)){
				fullFileUrl = "";
			}

			if(Objects.isNull(videoContent.getVideoName())){
				sysFile.setName("");
				videoContent.setVideoName("");
			}
			title = "\"" + videoContent.getVideoName() + "\"" + " in " + "\"" + channelName + "\"" + " courses";
			addMetaContent = metaHtml(title, videoContent.getVideoDesc(), fullFileUrl, host, request);
		}
		else if(xRequestUri.contains(metarielConfig.getChannel())){
			if(host.equals(siteMapConfiguration.getSiteUrl())){
				gcMaster = gcMasterService.getMasterById(siteMapConfiguration.getDefaultId());
			}else {
				Integer contextIndex = host.indexOf(".");
				String context = host.substring(0,contextIndex);
				gcMaster = gcMasterService.getMasterByContext(context);
			}


			addMetaContent =metaHtmlConfig(gcMaster,"channelPageShareTitle","channelPageShareDesc","channelPageShareImg",host,request);

		}else if(xRequestUri.contains(metarielConfig.getPlaylist()) && !containNumber){
			if(host.equals(siteMapConfiguration.getSiteUrl())){
				gcMaster = gcMasterService.getMasterById(siteMapConfiguration.getDefaultId());
			}else {
				Integer contextIndex = host.indexOf(".");
				String context = host.substring(0,contextIndex);
				gcMaster = gcMasterService.getMasterByContext(context);
			}


			addMetaContent=metaHtmlConfig(gcMaster,"playListPageShareTitle","playListPageShareDesc","playListPageShareImg",host,request);

	}else if(
			stats==1 && containNumber){
			GcSubject gcSubject = subjectService.getById(subOrVid);
			if(Objects.isNull(gcSubject)){
				return;
			}
			SysFile sysFile = sysFileService.getById(gcSubject.getSubImgId());
			fullFileUrl = sysFileService.getResFullUrl(sysFile,request);
			if(Objects.isNull(fullFileUrl)){
				fullFileUrl = "";
			}
			if(Objects.isNull(gcSubject.getName())){
				gcSubject.setName("");
			}
			if(Objects.isNull(gcSubject.getDescription())){
				gcSubject.setDescription("");
			}
			addMetaContent = metaHtml(gcSubject.getName(), gcSubject.getDescription(), fullFileUrl, host, request);


	}else if(
			stats==2  && containNumber){
			GcVideo gcVideo = gcVideoService.getById(subOrVid);
			GcSubject gcSubject = subjectService.getById(courseId);
			if(Objects.isNull(gcVideo)|| Objects.isNull(gcSubject)){
				return;
			}
			SysFile sysFile = sysFileService.getById(gcVideo.getFileId());
			if(sysFile.getFileTypeIndex().equals(13)){
				String fileUrl = sysFile.getFileUrl();
				String youtubeId = fileUrl.substring(fileUrl.lastIndexOf("/")+1,fileUrl.length());
				fullFileUrl = "https://i.ytimg.com/vi/"+youtubeId+"/hqdefault.jpg";
			}else if(sysFile.getFileTypeIndex().equals(14)){
				String fileUrl = sysFile.getFileUrl();
				String vimeoId = fileUrl.substring(fileUrl.lastIndexOf("/")+1,fileUrl.length());
				fullFileUrl = "https://vumbnail.com/"+vimeoId+"/_large.jpg";
			}else {
				fullFileUrl = sysFileService.getVideoSnapshotUrl(gcVideo);
			}
			if(Objects.isNull(fullFileUrl)){
				fullFileUrl = "";
			}
			if(Objects.isNull(gcVideo.getVideoName())){
				gcVideo.setVideoName("");
			}
			if(Objects.isNull(gcVideo.getVideoDesc())){
				gcVideo.setVideoDesc("");
			}
			 title = "\"" + gcVideo.getVideoName() + "\"" + " in " + "\"" + gcSubject.getName() + "\"" + " courses";
			addMetaContent = metaHtml(title, gcSubject.getDescription(), fullFileUrl, host, request);

		}
		else if(xRequestUri.contains(metarielConfig.getCourse())){
			if(host.equals(siteMapConfiguration.getSiteUrl())){
				gcMaster = gcMasterService.getMasterById(siteMapConfiguration.getDefaultId());
			}else {
				Integer contextIndex = host.indexOf(".");
				String context = host.substring(0,contextIndex);
				gcMaster = gcMasterService.getMasterByContext(context);
			}
			addMetaContent=metaHtmlConfig(gcMaster,"coursePageShareTitle","coursePageShareDesc","coursePageShareImg",host,request);
		}
		else if(xRequestUri.contains(metarielConfig.getPlaylist()) && containNumber &&
				!((hubUrl!="" && split.length==5 && split[2].trim().equals("playlist"))
				||!(hubUrl=="" && split.length==4 && split[1].trim().equals("playlist")))
		){
			GcUserSaveFolder gcUserSaveFolder = new GcUserSaveFolder();
			gcUserSaveFolder.setId(subOrVid);
			String thumbNail = "";
			Message message = gcMasterService.getContentFromOneFolder(gcUserSaveFolder,null,request,EnvType.PT.getCode());
			if(Objects.isNull(message.getData().get("id"))){
				return;
			}
			GcUserInfo gcUserInfo = (GcUserInfo) message.getData().get("userInfo");
			PageInfo pageInfo = new PageInfo();
			pageInfo = (PageInfo) message.getData().get("videoList");
			List<GcVideo> gcVideos = pageInfo.getList();
			if(CollectionUtils.isNotEmpty(gcVideos) && Objects.nonNull(gcVideos.get(0).getVideoFile()) ) {
				if(Objects.nonNull(gcVideos.get(0).getVideoFile().getSnapshotUrl())){
					thumbNail = gcVideos.get(0).getVideoFile().getSnapshotUrl();
				}else if(gcVideos.get(0).getVideoFile().getFileTypeIndex().equals(13)){
					String fileUrl = gcVideos.get(0).getVideoFile().getFileUrl();
					String youtubeId = fileUrl.substring(fileUrl.lastIndexOf("/")+1,fileUrl.length());
					thumbNail = "https://i.ytimg.com/vi/"+youtubeId+"/hqdefault.jpg";
				}else if(gcVideos.get(0).getVideoFile().getFileTypeIndex().equals(14)){
					String fileUrl = gcVideos.get(0).getVideoFile().getFileUrl();
					String vimeoId = fileUrl.substring(fileUrl.lastIndexOf("/")+1,fileUrl.length());
					thumbNail = "https://vumbnail.com/"+vimeoId+"/_large.jpg";
				}
			} else {
				thumbNail = "https://stage.store.demoguide.xyz/207/user/691/20221202/691_1669948409603_%E4%B8%8B%E8%BD%BD.png?Policy=eyJTdGF0ZW1lbnQiOiBbeyJSZXNvdXJjZSI6Imh0dHBzOi8vc3RhZ2Uuc3RvcmUuZGVtb2d1aWRlLnh5ei8yMDcvdXNlci82OTEvMjAyMjEyMDIvNjkxXzE2Njk5NDg0MDk2MDNfJUU0JUI4JThCJUU4JUJEJUJELnBuZyIsIkNvbmRpdGlvbiI6eyJEYXRlTGVzc1RoYW4iOnsiQVdTOkVwb2NoVGltZSI6NDg0NTEzMzIwMH0sIklwQWRkcmVzcyI6eyJBV1M6U291cmNlSXAiOiIwLjAuMC4wLzAifX19XX0_&Signature=JxrnVjhil8WMzttjQQBOhsvj2ttj9GtmGWaUlBf475imBpPC1ymy2VVt9-191nvuzHFLuPbXFMq6Q126uw~qrhwnCi1SSmKG~ymGgVXAWMiPEiWWY2Q2xRDKm6yCzXp0B5yZyyXeqy3kpiDNSa1UVrYJ3M~Ks0R3nSnztJ6FEOToWTTKhbZU-eQ7yPjhE9NQooIbIj~KY8FYlOfPFR6TaPPwqWXsMwmvInr6gJondtgspXhwvu57Qmq2oYoacmBLniUyhLOoc2qVZBDdvqKYgNxaRmd92iDPnmDtqLHv90cK8bMoXHMQ6UqgyyulNJ51zVEOnyb4gl5zuj1rWsx3ng__&Key-Pair-Id=K1JQYEVI2UZJ98";
			}

			String titleHtml =message.getData().get("name")+"Playlist";
			String descHtml= "Compiled by "+gcUserInfo.getFirstName()+" "+gcUserInfo.getLastName()+", last updated "+message.getData().get("lastUpdate");
			addMetaContent = metaHtml(titleHtml, descHtml, thumbNail, host, request);

		}
		PrintWriter printWriter = null;
		response.setHeader("Content-Type","text/html;charset=UTF-8");
		try {
			printWriter = response.getWriter();
			printWriter.write("<!doctype html>\n" +
					"<html lang=\"en\">\n" +
					"\n" +
					"<head>\n" +
					addMetaContent +
					"  <meta charset=\"utf-8\" />\n" +
					"  <meta content=\"width=device-width,initial-scale=1,maximum-scale=1,user-scalable=0,\" name=\"viewport\" />\n" +
					"  <meta name=\"theme-color\" content=\"#000000\" />\n" +
					"  <link rel=\"apple-touch-icon\" href=\""+hubUrl+"/apple-touch-icon.png\" />\n" +
					"  <link rel=\"manifest\" href=\""+hubUrl+"/manifest.json\" />\n" +
					"  <link rel=\"icon\" href=\""+hubUrl+"/favicon.ico\">\n" +
					"  <script type=\"module\" src=\""+hubUrl+"/arena.js\"></script>\n" +
					"  <script src=\""+hubUrl+"/globalConfig.js\"></script>\n" +
					"</head>\n" +
					"\n" +
					"<body><noscript>You need to enable JavaScript to run this app.</noscript>\n" +
					"  <div id=\"root\"></div>\n" +
					"  <script>\n" +
					"    window.onload = function () {\n" +
					"      var e = 0;\n" +
					"      document.addEventListener(\"touchstart\", (function (e) {\n" +
					"        e.touches.length > 1 && e.preventDefault()\n" +
					"      })), document.addEventListener(\"touchend\", (function (t) {\n" +
					"        var n = (new Date).getTime();\n" +
					"        n - e <= 300 && t.preventDefault(), e = n\n" +
					"      }), !1), document.addEventListener(\"gesturestart\", (function (e) {\n" +
					"        e.preventDefault()\n" +
					"      }))\n" +
					"    }\n" +
					"  </script>\n" +
					"</body>\n" +
					"\n" +
					"</html>\n" +
					"<!-- x-request-uri: "+xRequestUri+" -->\n"+
					"<!-- test1001: "+xRequestUri+" -->\n");
			printWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/html/robots.txt")
	public void robots(HttpServletRequest request,HttpServletResponse response) throws IOException {
		String host = request.getHeader("host");
		GcMaster gcMaster = new GcMaster();
		String content = "";
		PrintWriter printWriter = null;
		printWriter=response.getWriter();
		try {
			response.reset();
			if(host.equals(siteMapConfiguration.getSiteUrl())){
				gcMaster = gcMasterService.getMasterById(siteMapConfiguration.getDefaultId());
			}else {
				Integer contextIndex = host.indexOf(".");
				String context = host.substring(0,contextIndex);
				gcMaster = gcMasterService.getMasterByContext(context);
			}

			List<GcMasterHomeInfo> gcMasterHomeInfos = iGcMasterHomeInfoService.getGcMasterHomeInfoList(gcMaster.getId(),null,this.getSystem(),request);
			Optional<GcMasterHomeInfo> gcMasterHomeInfo = gcMasterHomeInfos.stream().filter(e->e.getName().equals("masterCrawlerSwitch")).findFirst();
			if(gcMasterHomeInfo.isPresent()){
				GcMasterHomeInfo homeInfo = gcMasterHomeInfo.get();
				if(Objects.nonNull(homeInfo.getContent())){
					if(homeInfo.getContent().equals("true")){
						content = "User-agent: *\n" +
								"Allow: /\n" +
								"\n" +
								"Sitemap: https://"+request.getHeader("host")+"/sitemap";
					}else if(homeInfo.getContent().equals("false")){
						content = "User-agent: *\n" +
								"Allow: /";
					}
				}
			}else {
				content = "User-agent: *\n" +
						"Allow: /\n";
			}

			response.setHeader("Content-Type","text;charset=UTF-8");
			response.setCharacterEncoding("utf-8");
			printWriter.write(content);
			printWriter.flush();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private  String metaHtmlConfig(GcMaster gcMaster,String MasterHomeInfoTitle,
								   String homeInfo2,String imgInfo2,String host,HttpServletRequest request){
		String title=null;
		String desc = null;
		String fullFileUrl = null;
		title = gcMaster.getPortalName();
		desc = gcMaster.getBrandDescription();

		List<GcMasterHomeInfo> gcMasterHomeInfos = iGcMasterHomeInfoService.getGcMasterHomeInfoList(gcMaster.getId(),null,this.getSystem(),request);
		Optional<GcMasterHomeInfo> gcMasterHomeInfo = gcMasterHomeInfos.stream().filter(e->e.getName().equals(MasterHomeInfoTitle)).findFirst();
		if(gcMasterHomeInfo.isPresent()){
			GcMasterHomeInfo homeInfo = gcMasterHomeInfo.get();
			if(Objects.nonNull(homeInfo.getContent())){
				title = homeInfo.getContent();
			}
		}

		Optional<GcMasterHomeInfo> homeInfo = gcMasterHomeInfos.stream().filter(e->e.getName().equals(homeInfo2)).findFirst();
		if(homeInfo.isPresent()) {
			GcMasterHomeInfo info = homeInfo.get();
			if (Objects.nonNull(info.getContent())) {
				desc = info.getContent();
			}
		}

		Optional<GcMasterHomeInfo> imgInfo = gcMasterHomeInfos.stream().filter(e->e.getName().equals(imgInfo2)).findFirst();
		if(imgInfo.isPresent()) {
			GcMasterHomeInfo info = imgInfo.get();
			if(Objects.nonNull(info.getFileId())) {
				SysFile sysFile = sysFileService.getById(info.getFileId());
				fullFileUrl = sysFileService.getResFullUrl(sysFile, request);
			}
		}
		String addMetaContent = metaHtml(title, desc, fullFileUrl, host, request);
		return addMetaContent;

	}

	private String metaHtml(String title, String desc, String fullFileUrl, String host, HttpServletRequest request) {
		desc = desc == null ? "" : desc;

		return "  <title>" + title + "</title>\n" +
			"  <meta name=\"title\" content=\"" + title + "\" />\n" +
			"  <meta name=\"description\" content=\"" + desc + "\" />\n" +
			"  <meta property=\"og:image\" content=\"" + fullFileUrl + "\"/>\n" +
			"  <meta property=\"og:title\" content=\"" + title + "\"/>\n" +
			"  <meta property=\"og:x-request-uri\" content=\"" + request.getHeader("x-request-uri") + "\"/>\n" +
			"  <meta property=\"og:description\" content=\"" + desc + "\"/>\n" +
			"  <meta property=\"og:url\" content=\"" + host + hubUrl + "\">\n" +
			"  <meta name=\"twitter:card\" content=\"summary_large_image\">\n" +
			"  <meta name=\"twitter:title\" content=\"" + title + "\">\n" +
			"  <meta name=\"twitter:description\" content=\"" + desc + "\">\n" +
			"  <meta name=\"twitter:url\" content=\"" + host + hubUrl + "\">\n" +
			"  <meta name=\"twitter:image\" content=\"" + fullFileUrl + "\">\n";
	}

	//test
	public static void main(String[] args) {
		String input = "/hub/course/1231231";
		String hubRegex = "^\\/\\w+\\/course+\\/\\d+$";
		String regex = "^\\/course+\\/\\d+$";
		Pattern pattern = Pattern.compile(hubRegex);
		Matcher matcher = pattern.matcher(input);

		while (matcher.find()) {
			String match = matcher.group();
			System.out.println("匹配到的字符串：" + match);
		}


			String str = "/playlist/12551/8015";
			String[] parts = str.split("/");
			String secondLastPart = parts[parts.length - 2];
			System.out.println("倒数第二个斜杠后面的数字是：" + secondLastPart);

	}


	private String playListMetaConfig(SysFile sysFile,GcUserSaveFolder gcUserSaveFolder,String host,HttpServletRequest request,String title,String description) {
		String fullFileUrl;
		if(sysFile.getFileTypeIndex().equals(13)){
			String fileUrl = sysFile.getFileUrl();
			String youtubeId = fileUrl.substring(fileUrl.lastIndexOf("/")+1,fileUrl.length());
			fullFileUrl = "https://i.ytimg.com/vi/"+youtubeId+"/hqdefault.jpg";
		}else if(sysFile.getFileTypeIndex().equals(14)){
			String fileUrl = sysFile.getFileUrl();
			String vimeoId = fileUrl.substring(fileUrl.lastIndexOf("/")+1,fileUrl.length());
			fullFileUrl = "https://vumbnail.com/"+vimeoId+"/_large.jpg";
		}else {
			fullFileUrl = sysFileService.getVideoSnapshotUrl(sysFile);
		}
		if(Objects.isNull(fullFileUrl)){
			fullFileUrl = "";
		}
		if(Objects.isNull(gcUserSaveFolder.getName())){
			gcUserSaveFolder.setName("");
		}
		if(Objects.isNull(sysFile.getName())){
			sysFile.setName("");
		}
        return metaHtml(title, description, fullFileUrl, host, request);
	}

}
