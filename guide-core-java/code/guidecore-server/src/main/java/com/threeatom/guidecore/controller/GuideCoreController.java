package com.threeatom.guidecore.controller;

import com.threeatom.guidecore.enums.CourseAvailabilityType;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.threeatom.common.exception.PermitException;
import com.threeatom.config.PermitConfiguration;
import com.threeatom.guidecore.constant.*;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.service.*;
import io.permit.sdk.Permit;
import io.permit.sdk.PermitConfig;
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
	@Autowired
	private PtChannelContentService channelContentService;

	private static final Logger log = LoggerFactory.getLogger(GuideCoreController.class);
	public static Permit permit = null;

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

	public GcUserAccess getUserAccess(Integer masterId) {
		if(checkRole("user")) {
			Integer uid=Integer.parseInt(this.getTokenValue("uid"));
			return userAccessService.getUserAccessByMasterIdAndUserId(masterId, uid);
		}
		return null;
	}

	public void assertUserTeacher(Integer masterId) {
		GcUserAccess userAccess=this.getUserAccess(masterId);
		if(userAccess==null) throw new SystemException(I18NUtil.get("permission.error"));

		if(!userAccess.getAccess().getRoleType().equals(AccessRoleType.TEACHER))
			throw new SystemException(I18NUtil.get("permission.error"));

	}

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

		Integer subId;

		if (type == SysResourceType.EVENT) {
			subId = eventService.getEventSubIdByEventId(id);
		} else {
			subId = videoService.getSubIdByVid(id);
		}

		if (!assignmentCourseIds.contains(subId)) {
			throw new SystemException(I18NUtil.get("resource.permission.error"));
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

	public boolean permitCheck(GcUser userInfo, String action, Integer masterId, String resourceType, Integer resourceTypeUserId,List<String> codeList,List<Integer> subIds) {
		if (permitConfiguration.getPermitSwitch().equals(TableConstant.COMMON_ONE)){
			return true;
		}

		this.permit = new Permit(
				new PermitConfig.Builder(permitConfiguration.getApiKey())
						.withPdpAddress(permitConfiguration.getPdpAddress())
						.withDebugMode(true)
						.build()
		);

		List<String> accessCodes;
		List<GcUserAccess> userAccessList = userAccessService.getAccessListByUser(userInfo.getId());
		List<Integer> accessIds = userAccessList.stream().map(GcUserAccess::getAccessId).collect(Collectors.toList());
		accessCodes = accessService.listByIds(accessIds).stream().map(GcAccess::getCode).collect(Collectors.toList());

		User user = null;
		TenantRead tenant = null;
		Resource resource = null;
		Context context = new Context();
		boolean permitted;

		try {
			HashMap<String, Object> hashMap = new HashMap<>();
			List<String> accessList = new ArrayList<>();
			if (null!=resourceTypeUserId){
				GcUser gcUser = null;

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
						if (CourseAvailabilityType.PUBLIC.getValue().equals(subject.getAvailableType())){
							accessList = accessCodes;
						}
						break;
					case ResourceType.videoItem:
						GcVideo video = videoService.getById(resourceTypeUserId);
						GcSubject topic = subjectService.getById(video.getSubId());
						Integer createUser = null;

						if (topic != null) {
							GcSubject subject1 = subjectService.getById(topic.getFid());
							if (null!=subject1&&null!=subject1.getCreateUser()){
								createUser = subject1.getCreateUser();
								accessList = accessService.getAccessBySubjectId(masterId,subject1.getId()).stream().map(GcAccess::getCode).collect(Collectors.toList());
								if (CourseAvailabilityType.PUBLIC.getValue().equals(subject1.getAvailableType())){
									accessList = accessCodes;
								}
							}
						} else {
							createUser = getChannelOwnerUserId(video);
						}

						if (null!=createUser){
							gcUser = userService.getById(createUser);
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
						if (!accessList.isEmpty()){
							hashMap.put("groupIDs",JSONArray.parseArray(JSON.toJSONString(accessList)).toJSONString());
						}
						break;
				}
				if (null!=gcUser&&gcUser.getId().equals(userInfo.getId())){
					hashMap.put("owner",gcUser.getUsername());
					context.put("owner",gcUser.getUsername());
				}
			}
			log.info("accessList::"+accessList);

			if (resourceType.equals(ResourceType.contentGroup)&&action.equals(ActionsType.addContent)&&null!=codeList){
				log.info("addContent::groupIDs:"+JSONArray.parseArray(JSON.toJSONString(codeList)));
				hashMap.put("groupIDs",JSONArray.parseArray(JSON.toJSONString(codeList)));
			}

			if (!accessList.isEmpty()){
				log.info("json::"+JSONArray.parseArray(JSON.toJSONString(accessList)));
				hashMap.put("contentGroups",JSONArray.parseArray(JSON.toJSONString(accessList)));
			} else {
				log.info("no contentGroups"+new Date());
				hashMap.put("contentGroups",null);
			}
			GcMaster master = masterService.getById(masterId);
			tenant = permit.api.tenants.get(master.getContext());
			UserRead userRead = permit.api.users.get(userInfo.getUsername());

			user = User.fromString(userRead.key);
			resource = new Resource.Builder(resourceType)
					.withTenant(tenant.key)
					.withAttributes(hashMap)
					.build();

		 permitted = permit.check(User.fromString(user.getKey()), action, resource,context);
		}catch (Exception e){
			e.printStackTrace();
			throw new PermitException(e.getMessage());
		}
		if (!permitted){
			throw new PermitException("No permission for this!");
		}

		return true;
	}

	private Integer getChannelOwnerUserId(GcVideo video) {
		Optional<PtChannelContent> ptChannel = channelContentService.getChannelContent(video.getId());
		if (ptChannel.isPresent()) {
			PtChannelContent ptChannelContent = ptChannel.get();
			PtChannel channel = ptChannelService.getById(ptChannelContent.getChannelId());
			if (null!=channel&&null!=channel.getCreateUserId()){
				return channel.getCreateUserId();
			}
		}

		return null;
	}

}
