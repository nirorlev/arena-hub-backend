package com.threeatom.guidecore.controller;

import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.BaseController;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.AccessRoleType;
import com.threeatom.guidecore.constant.SysResourceType;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcManager;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserAccess;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcEventService;
import com.threeatom.guidecore.service.GcManagerService;
import com.threeatom.guidecore.service.GcMasterService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.util.I18NUtil;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class GuideCoreController extends BaseController {

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
	private GcContentGroupCourseAssignmentService contentGroupCourseAssignmentService;

	public GcManager getManager() {
		if(checkRole("manager")) {
			Integer uid=Integer.parseInt(this.getTokenValue("uid"));
			return managerService.getManagerByIdCache(uid);
		}
		return null;
	}

	public boolean checkRole(String role) {
		String tokenRole=this.getTokenValue("role");
        return tokenRole.equals(role);
	}

	public GcMaster getMaster() {
		if(checkRole("manager")) {
			Integer uid=Integer.parseInt(this.getTokenValue("uid"));
			return masterService.getMasterByUidCache(uid);
		}
		return null;
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
        return Integer.parseInt(request.getHeader("masterId"));
	}
}
