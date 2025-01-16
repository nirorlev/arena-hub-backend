package com.threeatom.guidecore.service.impl;

import static com.threeatom.utils.ToolUtil.parseToJsonArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.client.dto.GroupDto;
import com.threeatom.client.dto.ManagedGroupDto;
import com.threeatom.client.dto.PowtoonUserDto;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.controller.user.vo.PtGroupsVo;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserAccess;
import com.threeatom.guidecore.entity.GcUserInfo;
import com.threeatom.guidecore.enums.UserGroupRole;
import com.threeatom.guidecore.mapper.GcAccessMapper;
import com.threeatom.guidecore.mapping.ContentGroupMapping;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcMasterService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.GcUserInfoService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.PtChannelSubscribeService;
import com.threeatom.guidecore.util.I18NUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class GcAccessServiceImpl extends ServiceImpl<GcAccessMapper, GcAccess>
    implements GcAccessService {

    @Autowired
    GcUserAccessService userAccessService;

    @Autowired
    private GcMasterService gcMasterService;
    @Autowired
    private GcUserInfoService gcUserInfoService;
    @Autowired
    @Lazy
    private GcUserService gcUserService;
    @Autowired
    private GcContentGroupCourseAssignmentService contentGroupCourseAssignmentService;
    @Autowired
    private PtChannelSubscribeService ptChannelSubscribeService;
    @Autowired
    private ContentGroupMapping contentGroupMapping;

    @Override
    public List<GcAccess> getAccessByMasterIdAndCode(GcAccess access) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("master_id", access.getMasterId());
        queryWrapper.eq("code", access.getCode());
        return this.list(queryWrapper);
    }

    @Override
    public GcAccess selectFreeCodeByMaster(Integer masterId) {
        QueryWrapper queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("code_type", TableConstant.COMMON_TWO);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<GcAccess> selectMasterIdAndIds(Integer masterId, List<Integer> ids) {
        QueryWrapper queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.in("id", ids);
        return this.list(queryWrapper);
    }

    @Override
    public List<GcAccess> selectAccessByCodeAndMasterId(List<String> codeList, Integer masterId) {
        return this.baseMapper.selectAccessByCodeAndMasterId(codeList, masterId);
    }

    @Override
    public void insertOrUpdateList(List<GcAccess> accessList) {
        this.baseMapper.insertOrUpdateList(accessList);
    }

    @Override
    public List<GcAccess> selectAccessByIds(List<Integer> ids) {
        return this.baseMapper.selectAccessByIds(ids);
    }

    @Override
    public List<GcAccess> selectAccessBySubId(Integer subId, Integer masterId) {
        return this.baseMapper.selectAccessBySubId(subId, masterId);
    }

    @Override
    public void deleteSubIdAccess(Integer masterId, Integer subId) {
        List<GcAccess> accessList = this.selectAccessBySubId(subId, masterId);
        if (CollectionUtils.isEmpty(accessList)) {
            return;
        }

        contentGroupCourseAssignmentService.removeByMasterAndCourseId(masterId, subId);
        this.insertOrUpdateList(accessList);
    }

    @Override
    public List<GcAccess> getAllAccessByMasterId(Integer masterId) {
        return this.baseMapper.getAllAccessByMasterId(masterId);
    }

    @Override
    @Transactional
    public boolean addAccess(GcAccess access) {
        userAccessService.clearCacheAll();
        this.saveOrUpdate(access);
        return true;
    }

    @Override
    public List<GcAccess> findAccessListByMasterId(Integer masterId) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.isNotNull("group_name");
        return this.list(queryWrapper);
    }

    @Override
    public List<GcAccess> getAdminAccessListByMasterId(Integer masterId) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("master_id", masterId);
        //        queryWrapper.isNull("package_name");
        return this.list(queryWrapper);
    }

    @Override
    public GcAccess getAccessByName(String accessName, Integer masterId) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("code", accessName);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<GcAccess> listAccess(String name, Integer masterId, Integer userId) {
        return this.baseMapper.listAccess(name, masterId, userId);
    }

    @Override
    public List<GcAccess> getTeamAccessList(
        String name, Integer masterId, Integer userId, HttpServletRequest request) {
        return this.baseMapper.getTeamAccessList(name, masterId, userId);
    }

    public List<GcAccess> getTeamAccessSubjectNumAdminList(
        String name,
        Integer masterId,
        Integer userId,
        List<Integer> privateCourseIds,
        List<Integer> publicCourseIds,
        List<Integer> subIds) {
        List<GcAccess> list = this.baseMapper.getTeamAccessSubjectNumAdminList(name, masterId, userId);

        for (GcAccess access : list) {
            List<Integer> mustAssignedCourses =
                contentGroupCourseAssignmentService.getMustCoursesContentGroupAssignmentIds(access.getId());
            List<Integer> optionalAssignedCourses =
                contentGroupCourseAssignmentService.getOptionalCoursesContentGroupAssignmentIds(access.getId());

            int coursesNum = access.getSubjectNum() + publicCourseIds.size();
            long coursesCountToExclude = Stream.concat(privateCourseIds.stream(), publicCourseIds.stream())
                .filter(
                    courseId -> mustAssignedCourses.contains(courseId) || optionalAssignedCourses.contains(courseId))
                .count();

            access.setSubjectNum((int) (coursesNum - coursesCountToExclude));
        }
        return list;
    }

    @Override
    public List<GcAccess> getTeamAccessSubjectNumList(
        String name,
        Integer masterId,
        Integer userId,
        List<Integer> subIds,
        HttpServletRequest request) {
        List<GcAccess> list = this.baseMapper.getTeamAccessSubjectNumList(name, masterId, userId);
        for (GcAccess access : list) {
            List<Integer> mustAssignedCourses =
                contentGroupCourseAssignmentService.getMustCoursesContentGroupAssignmentIds(access.getId());
            List<Integer> optionalAssignedCourses =
                contentGroupCourseAssignmentService.getOptionalCoursesContentGroupAssignmentIds(access.getId());
            long coursesCountToExclude = subIds.stream()
                .filter(
                    courseId -> mustAssignedCourses.contains(courseId) || optionalAssignedCourses.contains(courseId))
                .count();

            access.setSubjectNum((int) (access.getSubjectNum() - coursesCountToExclude));
        }
        return list;
    }

    @Override
    public List<GcAccess> selectAccessLevel0(Integer masterId, Integer userId) {
        List<GcAccess> gcAccessList = this.baseMapper.selectAccessLevel0(masterId, userId);
        gcAccessList = listToTree(gcAccessList);
        return gcAccessList;
    }

    @Override
    @Transactional
    public void syncContentGroupsWithPowtoonGroups(
        PowtoonUserDto powtoonUser, PtGroupsVo groups, Integer masterId, Integer userId) {

        List<GroupDto> memberGroups = powtoonUser.getPermissions().getGroups();
        List<ManagedGroupDto> managedGroups = powtoonUser.getPermissions().getManagedGroups();

        List<String> memberGroupCodes = getMemberGroupCodes(memberGroups);
        List<String> managedGroupCodes = getManagedGroupCodes(managedGroups);
        List<String> allGroupCodes = new ArrayList<>(memberGroupCodes);

        allGroupCodes.addAll(managedGroupCodes);

        List<GcAccess> memberContentGroups =
            createOrUpdateMemberContentGroups(allGroupCodes, masterId, memberGroups);
        List<GcAccess> managedContentGroups =
            saveOrUpdateManagedContentGroups(allGroupCodes, masterId, managedGroups);
        List<GcAccess> allContentGroups = getAllContentGroups(memberContentGroups, managedContentGroups, masterId);
        List<GcAccess> dbContentGroups = this.list();

        ptChannelSubscribeService.autoSubscribeToContentGroupChannels(memberContentGroups, userId);
        userAccessService.syncUserAccessWithPowtoonGroups(masterId, allContentGroups, groups, userId);
        userAccessService.removeOutdatedContentGroupAccess(dbContentGroups, allGroupCodes, userId, masterId);
    }

    @Override
    public List<GcAccess> findContentGroupsByCodes(List<String> codes) {
        if (codes.isEmpty()) {
            return List.of();
        }

        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("code", codes);
        return this.list(queryWrapper);
    }

    private GcAccess getContentGroup(String code, int roleType, Integer masterId) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        queryWrapper.eq("role_type", roleType);
        queryWrapper.eq("master_id", masterId);
        return getOne(queryWrapper);
    }

    private List<GcAccess> listToTree(List<GcAccess> gcAccessList) {
        List<GcAccess> newAccessList = new ArrayList<>();
        Map<String, GcAccess> gcAccessMap =
            gcAccessList.stream().collect(Collectors.toMap(GcAccess::getCode, (p) -> p));

        for (GcAccess access : gcAccessList) {
            if (null == access.getParentCode() || null == gcAccessMap.get(access.getParentCode())) {
                newAccessList.add(accessTree(access, gcAccessList));
            }
        }
        return newAccessList;
    }

    @Transactional
    @Override
    public GcAccess getStudentContentGroup(List<Integer> courseIds, Integer masterId) {
        GcAccess teacherContentGroup =
            getContentGroup("teacherPT", TableConstant.COMMON_ZERO, masterId);
        GcAccess studentAccess = getContentGroup("studentPT", TableConstant.COMMON_ONE, masterId);

        if (teacherContentGroup == null || studentAccess == null) {
            if (teacherContentGroup == null) {
                teacherContentGroup = createTeacherContentGroup(courseIds, masterId);
                addAccess(teacherContentGroup);
            }
            if (studentAccess == null) {
                studentAccess = createStudentContentGroup(courseIds, teacherContentGroup, masterId);
                addAccess(studentAccess);
            } else if (studentAccess.getAdminId() == null) {
                studentAccess.setAdminId(teacherContentGroup.getId());
                updateById(studentAccess);
            }

            return studentAccess;
        }

        updateById(teacherContentGroup);
        updateById(studentAccess);
        return studentAccess;
    }

    private GcAccess createStudentContentGroup(List<Integer> subjectIdList, GcAccess gcAccess, Integer masterId) {
        GcAccess studentAccess = new GcAccess();
        studentAccess.setMasterId(masterId);
        studentAccess.setCode("studentPT");
        studentAccess.setCodeType(TableConstant.COMMON_ZERO);
        studentAccess.setFreeFlag(TableConstant.COMMON_ZERO);
        studentAccess.setPackageShowFlag(TableConstant.COMMON_ONE);
        studentAccess.setRoleType(TableConstant.COMMON_ONE);
        studentAccess.setAdminId(gcAccess.getId());
        studentAccess.setId(null);
        return studentAccess;
    }

    private GcAccess createTeacherContentGroup(List<Integer> subjectIdList, Integer masterId) {
        GcAccess gcAccess = new GcAccess();
        gcAccess.setMasterId(masterId);
        gcAccess.setRoleType(TableConstant.COMMON_ZERO);
        gcAccess.setCodeType(TableConstant.COMMON_ZERO);
        gcAccess.setFreeFlag(TableConstant.COMMON_ZERO);
        gcAccess.setPackageShowFlag(TableConstant.COMMON_ONE);
        gcAccess.setCode("teacherPT");
        return gcAccess;
    }

    private GcAccess accessTree(GcAccess gcAccess, List<GcAccess> gcAccessList) {
        for (GcAccess access : gcAccessList) {
            if (null != access.getParentCode() && access.getParentCode().equals(gcAccess.getCode())) {
                if (null == gcAccess.getAccessList()) {
                    gcAccess.setAccessList(new ArrayList<>());
                }
                gcAccess.getAccessList().add(accessTree(access, gcAccessList));
            }
        }
        return gcAccess;
    }

    @Override
    public List<GcAccess> listAllAccess(Map<String, Object> params, HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }

        return this.baseMapper.listAllAccess(params);
    }

    @Override
    public Integer deleteAccess(Integer id) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("id", id);
        return this.baseMapper.delete(queryWrapper);
    }

    @Transactional
    @Override
    public Message checkUserAccess(
        Integer masterId,
        Integer userId,
        String accessCode,
        Integer inviteUserId,
        GcUser user,
        HttpServletRequest request) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("master_id", masterId).eq("code", accessCode);
        GcAccess access = this.getOne(queryWrapper);
        if (access == null) {
            throw new SystemException(I18NUtil.get("guidecore.master.codeError"));
        }

        QueryWrapper<GcUserAccess> queryWrapper2 = new QueryWrapper<GcUserAccess>();
        queryWrapper2.eq("master_id", masterId).eq("user_id", userId).eq("access_id", access.getId());
        GcUserAccess userAccess = userAccessService.getOne(queryWrapper2);
        Message message = new Message().ok();
        if (userAccess == null) {
            userAccess = new GcUserAccess();
            userAccess.setUserId(userId);
            userAccess.setMasterId(masterId);
            userAccess.setAccessId(access.getId());
            userAccess.setAccess(access);
            // 初始化3个表
            userAccessService.createUserAccess(userAccess);
            // 发送邮件
            if (null != user) {
                GcMaster gcMaster = gcMasterService.getById(masterId);
                // String textBody = I18NUtil.get("guidecore.master.portalEmailText") +
                // gcMaster.getContext();
                String email = user.getUsername();
                List<String> emailList =
                    JSONObject.parseArray(gcMaster.getEmailCc().toJSONString(), String.class);
                emailList.add(email);
            }
            message.addData("ifNewMaster", true);
        }
        if (inviteUserId != null && inviteUserId != 0) {
            GcUserAccess inviteUserAccess =
                userAccessService.getUserAccessByMasterIdAndUserId(masterId, inviteUserId);
        }

        return message.addData("access", access).addData("userAccess", userAccess);
    }

    @Override
    public GcAccess getAccessById(Integer id) {
        return getById(id);
    }

    @Override
    public List<GcAccess> getAccessBySubjectId(Integer masterId, Integer id) {
        return this.baseMapper.getAccessBySubjectId(masterId, id);
    }

    public List<GcAccess> getAccessByChannelId(Integer masterId, Integer channelId) {
        return this.baseMapper.getAccessByChannelId(masterId, channelId);
    }

    @Override
    public List<GcAccess> getAccessByAdminId(Integer adminId) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("admin_id", adminId);
        return this.list(queryWrapper);
    }

    @Override
    public List<GcAccess> getAccessListByAdminId(Integer adminId) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("admin_id", adminId);
        return this.list(queryWrapper);
    }

    @Override
    public Map<String, Long> getALlAccessCodeNumsByMasterId(Integer masterId) {
        return this.baseMapper.getALlAccessCodeNumsByMasterId(masterId);
    }

    @Override
    public List<GcAccess> getAllPackage(
        Integer masterId, PageParam pageParam, List<Integer> packageIdList, List<Integer> idList) {
        Map<String, Object> map = new HashMap<>();
        Message message = new Message();
        Integer showFlag = TableConstant.COMMON_ZERO;
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }

        return this.baseMapper.getAllPackage(masterId, showFlag, packageIdList, idList);
    }

    private List<GcAccess> createOrUpdateMemberContentGroups(
        List<String> groupCodes, Integer masterId, List<GroupDto> powtoonGroups) {

        List<GcAccess> contentGroups = selectAccessByCodeAndMasterId(groupCodes, masterId);
        Map<String, GcAccess> codeToContentGroup = getCodeToContentGroup(contentGroups);
        List<GcAccess> memberContentGroups = new ArrayList<>();

        for (GroupDto group : powtoonGroups) {
            GcAccess contentGroup;
            if (null != codeToContentGroup.get(group.getId())) {
                contentGroup = codeToContentGroup.get(group.getId());
                contentGroup.setGroupName(group.getTitle());
            } else {
                contentGroup = createMemberContentGroup(masterId, group);
            }

            contentGroup.setRoleJson(
                JSONArray.parseArray("[" + JSON.toJSONString(UserGroupRole.GROUP_MEMBER.getRole()) + "]"));
            if (null != group.getRoleId() && group.getRoleId().equals(UserGroupRole.ORG_ADMIN.getRole())) {
                contentGroup.getRoleJson().add(UserGroupRole.ORG_ADMIN.getRole());
            }
            memberContentGroups.add(contentGroup);
        }
        if (!memberContentGroups.isEmpty()) {
            insertOrUpdateList(memberContentGroups);
        }

        return memberContentGroups;
    }

    private List<GcAccess> saveOrUpdateManagedContentGroups(
        List<String> groupCodes, Integer masterId, List<ManagedGroupDto> managedGroups) {

        List<GcAccess> managedContentGroups = new ArrayList<>();
        List<GcAccess> contentGroups = selectAccessByCodeAndMasterId(groupCodes, masterId);
        Map<String, GcAccess> codeToContentGroup = getCodeToContentGroup(contentGroups);

        for (ManagedGroupDto managedGroup : managedGroups) {
            GcAccess access = codeToContentGroup.get(managedGroup.getId());
            if (null != access) {
                access.setGroupName(managedGroup.getTitle());
                if (null != access.getRoleJson()) {
                    access.getRoleJson()
                        .addAll(
                            JSONArray.parseArray("[" + JSON.toJSONString(UserGroupRole.GROUP_ADMIN.getRole()) + "]"));
                } else {
                    access.setRoleJson(
                        JSONArray.parseArray("[" + JSON.toJSONString(UserGroupRole.GROUP_ADMIN.getRole()) + "]"));
                }
            } else {
                access = createManagerContentGroup(masterId, managedGroup);
            }

            managedContentGroups.add(access);
        }

        if (!managedContentGroups.isEmpty()) {
            insertOrUpdateList(managedContentGroups);
        }

        return managedContentGroups;
    }

    private List<GcAccess> getAllContentGroups(
        List<GcAccess> memberContentGroups, List<GcAccess> managedContentGroups, Integer masterId) {
        List<GcAccess> allContentGroups = new ArrayList<>(memberContentGroups);
        List<String> memberContentGroupCodes = getContentGroupCodes(memberContentGroups);

        managedContentGroups.forEach(managedContentGroup -> {
            if (!memberContentGroupCodes.contains(managedContentGroup.getCode())) {
                allContentGroups.add(managedContentGroup);
            }
        });

        return selectAccessByCodeAndMasterId(getContentGroupCodes(allContentGroups), masterId);
    }

    private GcAccess createManagerContentGroup(Integer masterId, ManagedGroupDto group) {
        GcAccess contentGroup = createContentGroup(masterId, group.getId(), group.getTitle());
        contentGroup.setChannelJson(new JSONArray());
        contentGroup.setRoleJson(
            JSONArray.parseArray("[" + JSON.toJSONString(UserGroupRole.GROUP_ADMIN.getRole()) + "]"));
        return contentGroup;
    }

    private GcAccess createMemberContentGroup(Integer masterId, GroupDto group) {
        GcAccess contentGroup = createContentGroup(masterId, group.getId(), group.getTitle());
        contentGroup.setSubscribeJson(new JSONArray());
        return contentGroup;
    }

    private GcAccess createContentGroup(Integer masterId, String code, String title) {
        GcAccess contentGroup = new GcAccess();
        contentGroup.setMasterId(masterId);
        contentGroup.setCode(code);
        contentGroup.setGroupName(title);
        contentGroup.setRoleType(TableConstant.COMMON_ONE);
        contentGroup.setCodeType(TableConstant.COMMON_ZERO);
        return contentGroup;
    }

    private List<String> getContentGroupCodes(List<GcAccess> gcAccessesList) {
        return gcAccessesList.stream().map(GcAccess::getCode).collect(Collectors.toList());
    }

    private Map<String, GcAccess> getCodeToContentGroup(List<GcAccess> contentGroupIds) {
        return contentGroupIds.stream()
            .collect(Collectors.toMap(GcAccess::getCode, Function.identity(), (key1, key2) -> key2));
    }

    private List<String> getManagedGroupCodes(List<ManagedGroupDto> managedGroups) {
        return managedGroups.stream().map(ManagedGroupDto::getId).collect(Collectors.toList());
    }

    private List<String> getMemberGroupCodes(List<GroupDto> memberGroups) {
        return memberGroups.stream().map(GroupDto::getId).collect(Collectors.toList());
    }
}
