package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.guidecore.constant.AccessRoleType;
import com.threeatom.guidecore.controller.user.vo.Groups;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.controller.user.vo.UserCommonInfo;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcUserAccessExtMapper;
import com.threeatom.guidecore.mapper.GcUserAccessMapper;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcGroupService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class GcUserAccessServiceImpl extends ServiceImpl<GcUserAccessMapper, GcUserAccess>
        implements GcUserAccessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserAccessServiceImpl.class);

    private static final String CACHE_TAG = "GcUserAccess";

    @Autowired RedisOperator redisOperator;

    @Autowired @Lazy private GcAccessService gcAccessService;

    @Autowired @Lazy private GcGroupService groupService;

    @Autowired private GcUserAccessExtMapper userAccessExtMapper;

    @Lazy @Autowired private GcSubjectService gcSubjectService;

    @Autowired private SysFileService sysFileService;
    @Autowired private GcContentGroupCourseAssignmentService contentGroupCourseAssignmentService;

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

    private List<GcUserAccess> selectUserAccessesByMasterIdAndRole(Integer userId, Integer masterId, List<String> roles) {
        return this.baseMapper.selectUserAccessesByMasterIdAndRole(userId, masterId, roles);
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
    public boolean createUserAccess(GcUserAccess userAccess) {

        if (this.save(userAccess)) {
            GcUserAccessExt userAccessExt = new GcUserAccessExt();
            userAccessExt.setUserAccessId(userAccess.getId());
            userAccessExtMapper.insert(userAccessExt);
        }

        return false;
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
    public GcUserAccess getAccessByUserIdMaster(Integer userId, Integer masterId) {
        List<GcUserAccess> accessByUserIdMaster = this.baseMapper.getAccessByUserIdMaster(userId, masterId);
        return CollectionUtils.isEmpty(accessByUserIdMaster) ? null : accessByUserIdMaster.get(0);
    }

    @Override
    public List<GcUserAccess> getAccessByAccessId(Integer accessId) {
        QueryWrapper<GcUserAccess> queryWrapper = new QueryWrapper<GcUserAccess>();
        queryWrapper.eq("access_id", accessId);
        return this.list(queryWrapper);
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
    public Set<Integer> getContentGroupIds(Integer userId, Integer masterId, List<String> roles) {
        return selectUserAccessesByMasterIdAndRole(userId, masterId, roles)
                .stream()
                .map(GcUserAccess::getAccess)
                .map(GcAccess::getId)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void syncUserAccessWithPowtoonGroups(
        Integer masterId, List<GcAccess> allPowtoonUserContentGroups, Integer userId, List<Groups> powtoonGroups) {

        List<GcUserAccess> userContentGroups = new ArrayList<>();
        Map<String, Groups> codeToPowtoonGroups =
            powtoonGroups.stream().collect(Collectors.toMap(Groups::getId, Function.identity()));
        Map<String, GcAccess> codeToAllPowtoonUserContentGroups =
            allPowtoonUserContentGroups.stream().collect(Collectors.toMap(GcAccess::getCode, Function.identity()));

        for (GcAccess contentGroup : allPowtoonUserContentGroups) {
            GcUserAccess userAccess = new GcUserAccess();
            userAccess.setUserId(userId);
            userAccess.setMasterId(masterId);
            userAccess.setAccessId(contentGroup.getId());

            if (codeToAllPowtoonUserContentGroups.get(contentGroup.getCode()) != null) {
                userAccess.setRoleJson(codeToAllPowtoonUserContentGroups.get(contentGroup.getCode()).getRoleJson());
            }

            if (codeToPowtoonGroups.get(contentGroup.getCode()) != null) {
                userAccess.setParentCode(codeToPowtoonGroups.get(contentGroup.getCode()).getParent_group_id());
            }
            userAccess.setAccess(contentGroup);
            userContentGroups.add(userAccess);
        }

        if (!userContentGroups.isEmpty()) {
            insertUserAccessList(userContentGroups);
        }
    }

    @Override
    @Transactional
    public void removeOutdatedContentGroupAccess(
        List<GcAccess> allContentGroups, List<String> powtoonUserGroupCodes, Integer userId, Integer masterId) {

        List<Integer> contentGroupIdsToRemove = allContentGroups.stream()
            .filter(contentGroup -> !powtoonUserGroupCodes.contains(contentGroup.getCode()))
            .map(GcAccess::getId)
            .collect(Collectors.toList());

        if (!contentGroupIdsToRemove.isEmpty()) {
            deleteUserAccess(userId, masterId, contentGroupIdsToRemove);
        }
    }

}
