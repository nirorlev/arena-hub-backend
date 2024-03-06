package com.threeatom.guidecore.controller;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.PermitException;
import com.threeatom.config.PermitConfiguration;
import com.threeatom.guidecore.constant.*;
import com.threeatom.guidecore.controller.api.manager.NewUiGcVideoController;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.service.*;
import io.permit.sdk.Permit;
import io.permit.sdk.PermitConfig;
import io.permit.sdk.api.PermitApiError;
import io.permit.sdk.enforcement.Resource;
import io.permit.sdk.enforcement.User;
import io.permit.sdk.openapi.models.TenantRead;
import io.permit.sdk.openapi.models.UserRead;
import io.permit.sdk.util.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.BaseController;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.util.I18NUtil;

public class GuideCoreController extends BaseController{

	@Autowired
	GcManagerService managerService;
	@Autowired
	GcMasterService masterService;
	@Autowired
	GcUserService userService;
	@Autowired
	GcUserAccessService userAccessService;
	@Autowired
	GcEventService eventService;
	@Autowired
	GcVideoService videoService;
	@Autowired
	GcSubjectService subjectService;
	@Autowired
	private PermitConfiguration permitConfiguration;
	@Autowired
	private PtChannelService ptChannelService;
	@Autowired
	private GcAccessService accessService;
	@Autowired
	private GcUserSaveFolderService folderService;
	@Autowired
	private GcContentGroupCourseAssignmentService contentGroupCourseAssignmentService;


	private static final Logger log = LoggerFactory.getLogger(GuideCoreController.class);

	public GcManager getManager() {
		if(checkRole("manager")) {
			Integer uid=Integer.parseInt(this.getTokenValue("uid"));
			return managerService.getManagerByIdCache(uid);
		}
		return null;
	}

	public boolean checkRole(String role) {
		String tokenRole=this.getTokenValue("role");
		if(tokenRole.equals(role)) return true;
		else return false;
	}

	public GcMaster getMaster() {
		if(checkRole("manager")) {
			Integer uid=Integer.parseInt(this.getTokenValue("uid"));
			return masterService.getMasterByUidCache(uid);
		}
		return null;
	}

	public GcMaster getMasterByid(int uid) {
		return masterService.getMasterByUidCache(uid);
	}

	public GcUser getGcUser() {
		if(checkRole("user")) {
			Integer uid=Integer.parseInt(this.getTokenValue("uid"));
			return userService.getUserByIdCache(uid);
		}
		return null;
	}


	/***
	 * 获取权限信息
	 * @param masterId
	 * @return
	 */
	public GcUserAccess getUserAccess(Integer masterId) {
		if(checkRole("user")) {
			Integer uid=Integer.parseInt(this.getTokenValue("uid"));
			return userAccessService.getUserAccessByMasterIdAndUserId(masterId, uid);
		}
		return null;
	}

	/**
	 * 验证是否是教师权限
	 * @param masterId
	 */
	public void assertUserTeacher(Integer masterId) {
		GcUserAccess userAccess=this.getUserAccess(masterId);
		if(userAccess==null) throw new SystemException(I18NUtil.get("permission.error"));

		if(!userAccess.getAccess().getRoleType().equals(AccessRoleType.TEACHER))
			throw new SystemException(I18NUtil.get("permission.error"));

	}
	/**
	 * 验证是否是学生权限
	 * @param masterId
	 */
	public void assertUserStudent(Integer masterId) {
		GcUserAccess userAccess=this.getUserAccess(masterId);
		if(userAccess==null) throw new SystemException(I18NUtil.get("guidecore.master.userNoPortalAccess"));

		if(!userAccess.getAccess().getRoleType().equals(AccessRoleType.STUDENT))
			throw new SystemException(I18NUtil.get("guidecore.master.noPortalStudentAccess"));
	}
	public boolean whetherUserIsStudent(Integer masterId) {
		GcUserAccess userAccess=this.getUserAccess(masterId);
		if(userAccess==null)return false;
		if(Objects.nonNull(userAccess.getAccess().getRoleType()) && !userAccess.getAccess().getRoleType().equals(AccessRoleType.STUDENT))return false;
		return true;
	}


	public void assertResourceLimit(HttpServletRequest request,Integer id,int type) {
		Integer masterId=Integer.parseInt(request.getHeader("masterId"));

		ApiAssert.assertId(masterId, I18NUtil.get("masterId.error"));

		GcUserAccess userAccess=this.getUserAccess(masterId);
		if(userAccess==null) throw new SystemException(I18NUtil.get("resource.permission.error"));
		GcAccess access = userAccess.getAccess();
		List<Integer> assignmentCourseIds =
			contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(access.getId());

		if(assignmentCourseIds.isEmpty()) throw new SystemException(I18NUtil.get("resource.permission.error"));

		if(type==SysResourceType.EVENT) {
			if(!assignmentCourseIds.contains(eventService.getEventSubIdByEventId(id))) throw new SystemException(I18NUtil.get("resource.permission.error"));
		}else {
			if(!assignmentCourseIds.contains(videoService.getSubIdByVid(id))) throw new SystemException(I18NUtil.get("resource.permission.error"));
		}

	}


	public Integer getHeaderMasterId(HttpServletRequest request) {
		String masterIdStr=request.getHeader("masterId");
		if(Objects.isNull(masterIdStr)){
			throw new SystemException(I18NUtil.get("powtoon.portal.id.notfound"));
		}
		Integer masterId = Integer.parseInt(request.getHeader("masterId"));
		return masterId;

	}



	/**
	 * 随机字符串
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(62);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}
	public static Permit permit = null;

	public boolean permitCheck(GcUser userInfo, String action, Integer masterId, String resourceType, Integer resourceTypeUserId,List<String> codeList,List<Integer> subIds) throws IOException {

		/*if (1==1){
			return true;
		}*/
		if (permitConfiguration.getPermitSwitch().equals(TableConstant.COMMON_ONE)){
			return true;
		}
		this.permit = new Permit(
				new PermitConfig.Builder(permitConfiguration.getApiKey())
						.withPdpAddress(permitConfiguration.getPdpAddress())
						.withDebugMode(true)
						.build()
		);
		List<String> accessCodes = new ArrayList<>();
		List<GcUserAccess> userAccessList = userAccessService.getAccessListByUser(userInfo.getId());
		List<Integer> accessIds = userAccessList.stream().map(GcUserAccess::getAccessId).collect(Collectors.toList());
		accessCodes = accessService.listByIds(accessIds).stream().map(GcAccess::getCode).collect(Collectors.toList());

		User user = null;
		TenantRead tenant = null;
		Resource resource = null;
		Context context = new Context();
		boolean permitted = false;
		try {
			HashMap<String, Object> hashMap = new HashMap<>();
			//resourceType 指传入的资源类型
			//resourceTypeUserId 指资源的id,如果传入的是course,就代表是课程id videoItem则是视频id
			List<String> accessList = new ArrayList<>();
			if (null!=resourceTypeUserId){
				GcUser gcUser = null;
				//通过资源id获取资源的创建人
				switch (resourceType){
					case ResourceType.course:
						Integer createUserId = null;
						GcSubject subject = subjectService.getById(resourceTypeUserId);
						if (null!=subject&&null!=subject.getCreateUser()){
							createUserId = subject.getCreateUser();
						}
						if (null!=createUserId){
							gcUser = userService.getById(createUserId);
						}
						accessList = accessService.getAccessBySubjectId(masterId,resourceTypeUserId).stream().map(GcAccess::getCode).collect(Collectors.toList());
						if (null!=subject.getAvailableType()&&(subject.getAvailableType().equals(TableConstant.COMMON_ONE)||subject.getAvailableType().equals(TableConstant.COMMON_THREE))){
							accessList = accessCodes;
						}
						break;
					case ResourceType.videoItem:
						GcVideo video = videoService.getById(resourceTypeUserId);
						GcSubject subject1 = subjectService.getById(subjectService.getById(video.getSubId()).getFid());
						Integer createUser = null;
						if (null!=subject1&&null!=subject1.getCreateUser()){
							createUser = subject1.getCreateUser();
						}
						if (null!=createUser){
							gcUser = userService.getById(createUser);
						}
						accessList = accessService.getAccessBySubjectId(masterId,subject1.getId()).stream().map(GcAccess::getCode).collect(Collectors.toList());
						if (null!=subject1.getAvailableType()&&(subject1.getAvailableType().equals(TableConstant.COMMON_ONE)||subject1.getAvailableType().equals(TableConstant.COMMON_THREE))){
							accessList = accessCodes;
						}
						break;
					case ResourceType.channel:
						PtChannel channel = ptChannelService.getById(resourceTypeUserId);
						gcUser = userService.getById(channel.getCreateUserId());
						accessList = accessService.getAccessByChannelId(masterId,resourceTypeUserId).stream().map(GcAccess::getCode).collect(Collectors.toList());
						if (null!=channel.getVisibleFlag()&&channel.getVisibleFlag().equals(TableConstant.COMMON_ONE)){
							accessList = accessCodes;
						}
						hashMap.put("owner",userInfo.getUsername());
						break;
					case ResourceType.playList:
						if (null!=subIds){
							Integer folderSize = folderService.selectFolderByIdsAndUser(subIds,userInfo.getId());
							if (folderSize==subIds.size()){
								hashMap.put("owner",userInfo.getUsername());
							}
						}else {
							GcUserSaveFolder folder = folderService.getById(resourceTypeUserId);
							gcUser = userService.getById(folderService.getById(folder.getId()).getUserId());
						}
						break;
					case ResourceType.portal:
						break;
					case ResourceType.contentGroup:
						GcAccess access = accessService.getAccessById(resourceTypeUserId);
						if (null!=access){
							accessList.add(access.getCode());
						}
						if (accessList.size()!=TableConstant.COMMON_ZERO){
							hashMap.put("groupIDs",JSONArray.parseArray(JSON.toJSONString(accessList)).toJSONString());
						}
						break;
				}
				//传入owners字段,判断是否是创建者
				if (null!=gcUser&&gcUser.getId().equals(userInfo.getId())){
					hashMap.put("owner",gcUser.getUsername());
					context.put("owner",gcUser.getUsername());
				}
			}
			log.info("accessList::"+accessList);
			//发布时检查组
			if (resourceType.equals(ResourceType.contentGroup)&&action.equals(ActionsType.addContent)&&null!=codeList){
				log.info("addContent::groupIDs:"+JSONArray.parseArray(JSON.toJSONString(codeList)));
				hashMap.put("groupIDs",JSONArray.parseArray(JSON.toJSONString(codeList)));
			}
			//查询时检查权限
			if (null!=accessList&&accessList.size()!= TableConstant.COMMON_ZERO){
				log.info("json::"+JSONArray.parseArray(JSON.toJSONString(accessList)));
				hashMap.put("contentGroups",JSONArray.parseArray(JSON.toJSONString(accessList)));
			}else {
				log.info("no contentGroups"+new Date());
				hashMap.put("contentGroups",null);
			}
			GcMaster master = masterService.getById(masterId);
			//获取tenant
			tenant = permit.api.tenants.get(master.getContext());
			//获取用户key
			UserRead userRead = permit.api.users.get(userInfo.getUsername());
			user = User.fromString(userRead.key);
			resource = new Resource.Builder(resourceType)
					.withTenant(tenant.key).withAttributes(hashMap)
					.build();

		//进行权限效验
		 permitted = permit.check(
				User.fromString(user.getKey()),
				action,
				resource,context
		);
		}catch (Exception | PermitApiError e){
			e.printStackTrace();
			throw new PermitException(e.getMessage());
			//e.printStackTrace();
		}
		if (!permitted){
			throw new PermitException("No permission for this!");
		}
		return permitted;
	}

}
