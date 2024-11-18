package com.threeatom.guidecore.service.impl;

import static com.threeatom.utils.ToolUtil.parseToJsonArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.guidecore.constant.AccessRoleType;
import com.threeatom.guidecore.constant.GroupsType;
import com.threeatom.guidecore.controller.user.vo.Groups;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.controller.user.vo.PtGroupsVo;
import com.threeatom.guidecore.controller.user.vo.UserCommonInfo;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcUserAccessExtMapper;
import com.threeatom.guidecore.mapper.GcUserAccessMapper;
import com.threeatom.guidecore.mapper.GcUserAccessPermissionMapper;
import com.threeatom.guidecore.service.ContentGroupChannelSubscriptionService;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcGroupService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserAccessPermissionService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GcUserAccessServiceImpl extends ServiceImpl<GcUserAccessMapper, GcUserAccess>
        implements GcUserAccessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserAccessServiceImpl.class);

    private static final String CACHE_TAG = "GcUserAccess";

    private static final String KEY_TAG_ENTITY = "'entity:uid-'+";

    @Autowired RedisOperator redisOperator;

    @Autowired @Lazy private GcAccessService gcAccessService;

    @Autowired @Lazy private GcGroupService groupService;

    @Autowired private GcUserAccessPermissionMapper userAccessPermissionMapper;
    @Autowired private GcUserAccessExtMapper userAccessExtMapper;

    @Lazy @Autowired private GcSubjectService gcSubjectService;

    @Autowired private SysFileService sysFileService;
    @Autowired private GcContentGroupCourseAssignmentService contentGroupCourseAssignmentService;
    @Autowired private ContentGroupChannelSubscriptionService contentGroupChannelSubscriptionService;
    @Autowired private GcUserAccessPermissionService gcUserAccessPermissionService;

    @Override
    public GcUserAccess getUserAccessByMasterIdAndUserId(Integer masterId, Integer userId) {
        return this.baseMapper.selectUserAccessByUserAndMaster(userId, masterId);
    }

    @Override
    public GcUserAccess selectUserAccessByManagerAndMaster(Integer managerId, Integer masterId) {
        return this.baseMapper.selectUserAccessByManagerAndMaster(managerId, masterId);
    }

    @Override
    public List<GcUserAccess> selectPtUserAccessByMasterIdAndUserId(
            Integer userId, Integer masterId) {

        return this.baseMapper.selectPtUserAccessByMasterIdAndUserId(userId, masterId);
    }

    @Override
    public void clearCache(Integer userId, Integer masterId) {

        redisOperator.del(
                redisOperator.getFullKeyByValueAndKey(
                        CACHE_TAG, "entity:uid-" + userId + "-masterId-" + masterId));
    }

    @Override
    public int deleteById(Integer id) {
        return this.baseMapper.deleteById(id);
    }

    @Override
    public List<GcUserAccess> getUserAccessListByUserId(Integer userId, HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.baseMapper.selectUserAccessByUser(userId);
    }

    @Override
    public void clearCacheAll() {
        @SuppressWarnings("unchecked")
        Set<String> sets = redisOperator.keys(CACHE_TAG + "*");
        LOGGER.info(sets.size() + "xxxxx");
        redisOperator.del(sets);
    }

    @Override
    public List<Integer> getUserAccessListUserIds(Integer masterId, List<Integer> accessIds) {
        return this.baseMapper.selectGetUserAccessListUserIds(masterId, accessIds);
    }

    @Override
    public List<Integer> selectGetUserAccessIdListUserIds(Integer masterId, List<Integer> accessIds) {
        return this.baseMapper.selectGetUserAccessIdListUserIds(masterId, accessIds);
    }

    @Override
    public Integer countUserAccessesByMasterIdAndRole(Integer userId, Integer masterId, String role) {
        return baseMapper.countUserAccessesByMasterIdAndRole(userId, masterId, role);
    }

    private List<GcUserAccess> selectUserAccessesByMasterIdAndRole(Integer userId, Integer masterId, String role) {
        return this.baseMapper.selectUserAccessesByMasterIdAndRole(userId, masterId, role);
    }

    @Override
    public List<GcUserAccess> getStudentsAccessByTeacherId(
            Integer userId, Integer masterId, Integer page, Integer pageNum) {
        GcUserAccess ua = this.getUserAccessByMasterIdAndUserId(masterId, userId);

        if (!ua.getAccess().getRoleType().equals(AccessRoleType.TEACHER))
            throw new SystemException(I18NUtil.get("user.teacher.error"));

        // 获取教师access下面所有的access
        List<GcAccess> list = gcAccessService.getAccessListByAdminId(ua.getAccessId());

        // 获取ids
        List<Integer> accessIds = list.stream().map(GcAccess::getId).collect(Collectors.toList());

        // 查询所有学生
        if (page > 0 && pageNum > 0) PageHelper.startPage(page, pageNum);
        List<GcUserAccess> studentAccessList = this.getUsersByAccessIds(accessIds, null);
        return studentAccessList;
    }

    @Override
    public List<GcUserAccess> getUsersByAccessIds(
            List<Integer> accessIds, UserCommonInfo commonInfo) {
        if (accessIds.size() < 1) throw new SystemException(I18NUtil.get("access.id"));

        return this.baseMapper.selectUserAccessListByAccessIds(accessIds, commonInfo);
    }

    @Override
    public Integer createOrUpdateById(GcUserAccessExt userAccessExt) {
        if (userAccessExt.getId() == null) return this.baseMapper.insertGcUserAccessExt(userAccessExt);
        else return this.baseMapper.updateGcUserAccessExtById(userAccessExt);
    }

    @Override
    public List<Map<String, Object>> getUsersLastLogInDataByMasterIdAndUserIds(
            Integer masterId, List<Integer> userIds, String order) {

        return this.baseMapper.selectUserAccessExtListByMasterIdAndUserIds(masterId, userIds, order);
    }

    @Override
    public List<GcUserAccessPermission> getUsersAccessPermissions(List<Integer> userAccessIds) {
        QueryWrapper<GcUserAccessPermission> queryWrapper = new QueryWrapper<GcUserAccessPermission>();
        queryWrapper.in("user_access_id", userAccessIds);

        return userAccessPermissionMapper.selectList(queryWrapper);
    }

    @Override
    public boolean createUserAccess(GcUserAccess userAccess) {

        if (this.save(userAccess)) {
            GcUserAccessExt userAccessExt = new GcUserAccessExt();
            userAccessExt.setUserAccessId(userAccess.getId());
            userAccessExtMapper.insert(userAccessExt);

            GcUserAccessPermission userAccessPermission = new GcUserAccessPermission();
            userAccessPermission.setSubPermission(getContentGroupCourseAssignments(userAccess));
            userAccessPermission.setUserAccessId(userAccess.getId());
            userAccessPermissionMapper.insert(userAccessPermission);
        }

        return false;
    }

    private JSONArray getContentGroupCourseAssignments(GcUserAccess userAccess) {
        List<Integer> contentGroupCourseAssignments =
            contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(userAccess.getAccess().getId());
        return JSONArray.parseArray(JSON.toJSONString(contentGroupCourseAssignments));
    }

    @Override
    public int updateUserAccessPermissions(List<GcUserAccessPermission> perList) {
        if (perList == null || perList.size() < 1) return 0;
        return this.userAccessPermissionMapper.updateGcUserAccessPermissions(perList);
    }

    @Override
    public Map<String, Integer> getLastUsersNum(List<Integer> lastDays, Integer teacherAccessId) {
        return this.baseMapper.selectLastUsersNum(lastDays, teacherAccessId);
    }

    @Override
    public Map<String, Integer> getActiveUsersNum(List<Integer> lastDays, Integer teacherAccessId) {
        return this.baseMapper.selectActiveUsersNum(lastDays, teacherAccessId);
    }

    @Override
    public Map<String, Long> getMasterIdUsersNum(Integer masterId) {
        return this.baseMapper.selectUsersNum(masterId);
    }

    @Override
    public List<Map<String, Object>> getAllUserInThisMaster(
            List<Integer> masterId,
            String searchFilter,
            HttpServletRequest request,
            PageParam pageParam,
            Integer accessId) {
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<Map<String, Object>> list =
                this.baseMapper.getAllUserInThisMaster(masterId, searchFilter, accessId);
        for (Map<String, Object> map : list) {
            if (Objects.nonNull(map.get("f_file_url"))) {
                Integer avatarFileId = Integer.parseInt(map.get("f_file_url").toString());
                SysFile sysFile = sysFileService.getById(avatarFileId);
                String fullFileUrl = sysFileService.getResFullUrl(sysFile, request);
                map.put("full_file_url", fullFileUrl);
            }
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getAllManagerInThisMaster(Integer masterId) {

        return this.baseMapper.getAllManagerInThisMaster(masterId);
    }

    @Override
    @Transactional
    public int updateUserAccessPermission(List<Integer> userAccessIds, GcAccess gcAccess) {
        // 查询相关用户的实体
        List<GcUserAccess> list = this.baseMapper.selectUserAccessListByIds(userAccessIds);
        // 将用户相关的组全部取出
        List<GcGroup> groupList = groupService.getGroupListByUserAccessIds(userAccessIds);

        List<GcUserAccessPermission> userAccessPermissionList =
                this.getUsersAccessPermissions(userAccessIds);

        List<GcUserAccessPermission> updatePermission = new ArrayList<GcUserAccessPermission>();
        for (GcUserAccessPermission accessPermission : userAccessPermissionList) {
            switch (gcAccess.getSaveType()) {
                case 0:
                    {
                        // 原逻辑，直接覆盖
                        GcUserAccessPermission permission = new GcUserAccessPermission();

                        Optional<GcUserAccess> optional =
                                list.stream()
                                        .filter(a -> a.getId().equals(accessPermission.getUserAccessId()))
                                        .findFirst();
                        if (!optional.isPresent()) throw new SystemException(I18NUtil.get("user.not.found"));
                        GcAccess access = optional.get().getAccess();
                        if (access == null)
                            throw new SystemException(I18NUtil.get("access.not.found"));
                        // 合并权限
                        Set<Integer> subs = new HashSet<>(
                            contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(access.getId()));
                        List<GcGroup> subGroupList =
                                groupList.stream()
                                        .filter(g -> g.getGroupAccessIds().contains(accessPermission.getUserAccessId()))
                                        .collect(Collectors.toList());

                        for (GcGroup group : subGroupList) {
                            subs.addAll(group.getSubIds().toJavaList(Integer.class));
                        }
                        permission.setId(accessPermission.getId());

                        permission.setSubPermission(JSONArray.parseArray(JSONArray.toJSONString(subs)));

                        updatePermission.add(permission);
                        break;
                    }
                case 1:
                    {
                        // 新增
                        // 按照用户当前权限更新
                        // 筛选出新增的部分
                        List<Integer> contentGroupCourseAssignments = contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(gcAccess.getId());
                        List<Object> newAddSubjects =
                            contentGroupCourseAssignments.stream()
                                        .filter(e -> !accessPermission.getSubPermission().contains(e))
                                        .collect(Collectors.toList());
                        GcUserAccessPermission gcUserAccessPermission = new GcUserAccessPermission();
                        gcUserAccessPermission.setId(accessPermission.getId());
                        accessPermission.getSubPermission().addAll(newAddSubjects);
                        gcUserAccessPermission.setSubPermission(accessPermission.getSubPermission());
                        updatePermission.add(gcUserAccessPermission);
                        break;
                    }
                case 2:
                    {
                        // 删除
                        // 查询门户下所有的课程
                        Integer masterId = gcAccess.getMasterId();
                        List<GcSubject> gcSubjectList = gcSubjectService.getSubList(masterId, 0);
                        List<GcSubject> gcSubjectAssoList =
                                gcSubjectService.selectSubjectAssociation(masterId, null, true);
                        List<Integer> subIdList =
                                gcSubjectList.stream().map(GcSubject::getId).collect(Collectors.toList());
                        List<Integer> gcSubjectAssoIdList =
                                gcSubjectAssoList.stream().map(GcSubject::getId).collect(Collectors.toList());
                        subIdList.addAll(gcSubjectAssoIdList);
                        // 筛选出此次更新未选中的课程
                        List<Integer> contentGroupCourseAssignments = contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(gcAccess.getId());
                        List<Object> unSelectedSubjects =
                                subIdList.stream()
                                        .filter(e -> !contentGroupCourseAssignments.contains(e))
                                        .collect(Collectors.toList());
                        // 筛选出用户权限去除要删除课程后的课程
                        List<Object> saveSubjects =
                                accessPermission.getSubPermission().stream()
                                        .filter(e -> !unSelectedSubjects.contains(e))
                                        .collect(Collectors.toList());
                        GcUserAccessPermission gcUserAccessPermission = new GcUserAccessPermission();
                        gcUserAccessPermission.setId(accessPermission.getId());
                        gcUserAccessPermission.setSubPermission(
                                JSONArray.parseArray(JSON.toJSONString(saveSubjects)));
                        updatePermission.add(gcUserAccessPermission);
                        break;
                    }
                case 3:
                    {
                        return 0;
                    }
            }
        }

        int row = this.updateUserAccessPermissions(updatePermission);
        LOGGER.info("更新的行数：" + row + " 行");
        return row;
    }

    @Override
    public Integer saveUserAccessPermission(GcUserAccessPermission userAccessPermission) {
        return this.userAccessPermissionMapper.insert(userAccessPermission);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BigDecimal addPoints(Integer userAccessId, BigDecimal point) {
        QueryWrapper<GcUserAccessExt> queryWrapper = new QueryWrapper<GcUserAccessExt>();
        queryWrapper.eq("user_access_id", userAccessId);
        GcUserAccessExt userAccessExt = userAccessExtMapper.getOneByUserAccessId(userAccessId);
        if (userAccessExt == null) throw new SystemException("没有找到用户数据表");

        GcUserAccessExt updateUserAccessExt = new GcUserAccessExt();
        updateUserAccessExt.setId(userAccessExt.getId());

        BigDecimal resultPoint = userAccessExt.getPoints().add(point);
        updateUserAccessExt.setPoints(resultPoint);

        userAccessExtMapper.updateById(updateUserAccessExt);

        return resultPoint;
    }

    @Override
    public BigDecimal reducePoints(Integer userAccessId, BigDecimal point) {
        QueryWrapper<GcUserAccessExt> queryWrapper = new QueryWrapper<GcUserAccessExt>();
        queryWrapper.eq("user_access_id", userAccessId);
        GcUserAccessExt userAccessExt = userAccessExtMapper.selectOne(queryWrapper);
        if (userAccessExt == null) throw new SystemException("没有找到用户数据表");

        if (userAccessExt.getPoints().compareTo(point) != -1) {
            GcUserAccessExt updateUserAccessExt = new GcUserAccessExt();
            updateUserAccessExt.setId(userAccessExt.getId());

            BigDecimal resultPoint = userAccessExt.getPoints().subtract(point);
            updateUserAccessExt.setPoints(resultPoint);
            userAccessExtMapper.updateById(updateUserAccessExt);
            return resultPoint;
        } else throw new SystemException("扣除失败！，没有更多的点数可以扣除了");
    }

    @Override
    public BigDecimal getCurrentUserPoints(Integer userAccessId) {
        QueryWrapper<GcUserAccessExt> queryWrapper = new QueryWrapper<GcUserAccessExt>();
        queryWrapper.eq("user_access_id", userAccessId);
        GcUserAccessExt userAccessExt = userAccessExtMapper.selectOne(queryWrapper);
        if (userAccessExt == null) throw new SystemException("没有找到用户数据表");
        return userAccessExt.getPoints();
    }

    @Override
    public GcUserAccess getAccessByUserIdMaster(Integer userId, Integer masterId) {
        return this.baseMapper.getAccessByUserIdMaster(userId, masterId);
    }

    @Override
    public List<GcUserAccess> getAccessByAccessId(Integer accessId) {
        QueryWrapper<GcUserAccess> queryWrapper = new QueryWrapper<GcUserAccess>();
        queryWrapper.eq("access_id", accessId);
        return this.list(queryWrapper);
    }

    @Override
    public GcUserAccess getByUserId(Integer userId) {
        QueryWrapper<GcUserAccess> queryWrapper = new QueryWrapper<GcUserAccess>();
        queryWrapper.eq("user_id", userId);
        return getOne(queryWrapper);
    }

    @Override
    public List<GcUserAccess> getUserAccessListByMasterIdAndUserId(
            List<Integer> userIdList, Integer masterId) {
        return this.baseMapper.getUserAccessListByMasterIdAndUserId(userIdList, masterId);
    }

    @Override
    public List<GcUserAccess> getAccessListByUser(Integer userId) {
        QueryWrapper<GcUserAccess> queryWrapper = new QueryWrapper<GcUserAccess>();
        queryWrapper.eq("user_id", userId);
        return this.list(queryWrapper);
    }

    @Override
    public List<GcUserAccess> getAccessListByUserAndMasterId(Integer userId, Integer masterId) {
        QueryWrapper<GcUserAccess> queryWrapper = new QueryWrapper<GcUserAccess>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("master_id", masterId);
        return this.list(queryWrapper);
    }

    @Override
    public List<Integer> getAccessListBySuperAdmin(Integer userId, Integer masterId) {
        return this.baseMapper.getAccessListBySuperAdmin(userId, masterId);
    }

    @Override
    public void insertUserAccessList(List<GcUserAccess> list) {
        this.baseMapper.insertUserAccessList(list);
    }

    @Override
    public void deleteUserAccess(Integer userId, Integer masterId, List<Integer> accessId) {
        QueryWrapper<GcUserAccess> queryWrapper = new QueryWrapper<GcUserAccess>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("master_id", masterId);
        queryWrapper.in("access_id", accessId);
        this.baseMapper.delete(queryWrapper);
    }

    @Override
    public List<GcUserAccess> selectAllUserAccessByAccessId(Integer accessId, Integer masterId) {
        QueryWrapper<GcUserAccess> queryWrapper = new QueryWrapper<GcUserAccess>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.in("access_id", accessId);
        return this.list(queryWrapper);
    }

    @Override
    public Set<Integer> getContentGroupIds(Integer userId, Integer masterId, String role) {
        return selectUserAccessesByMasterIdAndRole(userId, masterId, role)
                .stream()
                .map(GcUserAccess::getAccess)
                .map(GcAccess::getId)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void syncUserAccessWithPowtoonGroups(
        Integer masterId, List<GcAccess> allContentGroups, PtGroupsVo powtoonGroups, Integer userId) {

        List<GcUserAccess> userAccesses = new ArrayList<>();
        Map<String, Groups> codeToPowtoonGroups =
            powtoonGroups.getResults().stream().collect(Collectors.toMap(Groups::getId, Function.identity()));
        Map<String, GcAccess> allContentGroupCodeToContentGroup =
            allContentGroups.stream().collect(Collectors.toMap(GcAccess::getCode, Function.identity()));

        List<Integer> superAdminContentGroups = getAccessListBySuperAdmin(userId, masterId);
        for (GcAccess contentGroup : allContentGroups) {
            GcUserAccess userAccess = new GcUserAccess();
            userAccess.setUserId(userId);
            userAccess.setMasterId(masterId);
            userAccess.setAccessId(contentGroup.getId());

            if (null != allContentGroupCodeToContentGroup.get(contentGroup.getCode())) {
                userAccess.setRoleJson(allContentGroupCodeToContentGroup.get(contentGroup.getCode()).getRoleJson());
            }
            if (superAdminContentGroups.contains(contentGroup.getId())) {
                userAccess.getRoleJson().add(GroupsType.superAdmin);
            }

            if (null != codeToPowtoonGroups.get(contentGroup.getCode())) {
                userAccess.setParentCode(codeToPowtoonGroups.get(contentGroup.getCode()).getParent_group_id());
            }
            userAccess.setAccess(contentGroup);
            userAccesses.add(userAccess);
        }

        if (!userAccesses.isEmpty()) {
            insertUserAccessList(userAccesses);
        }

        updateUserPermissions(getUserAccessListByMasterIdAndUserId(getUserIds(userAccesses), masterId));
    }

    @Override
    @Transactional
    public void removeOutdatedContentGroupAccess(
        List<GcAccess> allContentGroups, List<String> newGroupCodes, Integer userId, Integer masterId) {

        List<Integer> contentGroupIdsToRemove = allContentGroups.stream()
            .filter(contentGroup -> !newGroupCodes.contains(contentGroup.getCode()))
            .map(GcAccess::getId)
            .collect(Collectors.toList());

        if (!contentGroupIdsToRemove.isEmpty()) {
            deleteUserAccess(userId, masterId, contentGroupIdsToRemove);
        }
    }

    private void updateUserPermissions(List<GcUserAccess> userAccesses) {
        List<GcUserAccessPermission> userAccessPermissions = new ArrayList<>();

        for (GcUserAccess gcUserAccess : userAccesses) {
            GcUserAccessPermission permission = new GcUserAccessPermission();
            GcAccess access = gcUserAccess.getAccess();
            List<Integer> assignedCourses =
                contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(access.getId());
            List<Integer> mustAssignedCourses =
                contentGroupCourseAssignmentService.getMustCoursesContentGroupAssignmentIds(access.getId());
            List<Integer> optionalAssignedCourses =
                contentGroupCourseAssignmentService.getOptionalCoursesContentGroupAssignmentIds(access.getId());
            List<Integer> unsubscribedChannelIds =
                contentGroupChannelSubscriptionService.getPublicChannelIds(access.getId());
            List<Integer> subscribedChannelIds =
                contentGroupChannelSubscriptionService.getSubscribedChannelIds(access.getId());

            permission.setUserAccessId(gcUserAccess.getId());
            permission.setSubPermission(parseToJsonArray(assignedCourses));
            permission.setChannelPermission(parseToJsonArray(unsubscribedChannelIds));
            permission.setSubscribePermission(parseToJsonArray(subscribedChannelIds));
            permission.setMaySubjectJson(parseToJsonArray(optionalAssignedCourses));
            permission.setMustSubjectJson(parseToJsonArray(mustAssignedCourses));
            userAccessPermissions.add(permission);
        }
        if (!userAccessPermissions.isEmpty()) {
            gcUserAccessPermissionService.insertUserPermission(userAccessPermissions);
        }
    }

    private List<Integer> getUserIds(List<GcUserAccess> userAccessList) {
        return userAccessList.stream()
            .map(GcUserAccess::getUserId)
            .collect(Collectors.toList());
    }
}
