package com.threeatom.guidecore.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUserAccess;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcUserAccessPermission;
import com.threeatom.guidecore.mapper.GcUserAccessPermissionMapper;
import com.threeatom.guidecore.service.GcUserAccessPermissionService;

import java.sql.Wrapper;
import java.util.List;
import java.util.Map;

@Service
public class GcUserAccessPermissionServiceImpl  extends ServiceImpl<GcUserAccessPermissionMapper, GcUserAccessPermission> implements GcUserAccessPermissionService {

    @Override
    public int updateGcUserAccessPermissions(List<GcUserAccessPermission> perList) {
        return this.baseMapper.updateGcUserAccessPermissions(perList);
    }

    @Override
    public void updateGcUserAccessPermissionsChannel(List<GcUserAccessPermission> perList) {
        this.baseMapper.updateGcUserAccessPermissionsChannel(perList);
    }

    @Override
    public List<GcUserAccessPermission> selectAllUsersInPortal(Integer masterId) {
        return this.baseMapper.selectAllUsersInPortal(masterId);
    }

    @Override
    public List<GcUserAccessPermission> getContainsAccessPermissionList(String ptId, Integer masterId) {
        return this.baseMapper.getContainsAccessPermissionList(ptId,masterId);
    }

    @Override
    public List<GcUserAccessPermission> getContainsSubjectAccessPermissionList(String ptId) {
        return this.baseMapper.getContainsSubjectAccessPermissionList(ptId);
    }

    @Override
    public void deleteSubIdAccessPermissionList(Integer masterId, Integer subId) {
        List<GcUserAccessPermission> userAccessPermissions = this.baseMapper.getContainsSubjectAccessPermissionList(subId.toString());
        for (GcUserAccessPermission userAccessPermission : userAccessPermissions) {
            if (null!=userAccessPermission.getSubPermission()&&userAccessPermission.getSubPermission().contains(subId)){
                while (userAccessPermission.getSubPermission().contains(subId)){
                    userAccessPermission.getSubPermission().remove(subId);
                }
            }
            if (null!=userAccessPermission.getMaySubjectJson()&&userAccessPermission.getMaySubjectJson().contains(subId)){
                while (userAccessPermission.getMaySubjectJson().contains(subId)){
                    userAccessPermission.getMaySubjectJson().remove(subId);
                }
            }
            if (null!=userAccessPermission.getMustSubjectJson()&&userAccessPermission.getMustSubjectJson().contains(subId)){
                while (userAccessPermission.getMustSubjectJson().contains(subId)){
                    userAccessPermission.getMustSubjectJson().remove(subId);
                }
            }
        }

        if (null!=userAccessPermissions&&userAccessPermissions.size()!=TableConstant.COMMON_ZERO){
            this.baseMapper.removeUserPermission(userAccessPermissions);
        }
    }

/*    @Override
    public List<GcUserAccessPermission> getGcUserAccessPermissions(List<Integer> ids) {
        QueryWrapper<GcUserAccessPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_access_id", ids);

        return this.baseMapper.selectList(queryWrapper);
    }*/

    @Override
    public GcUserAccessPermission getPermissionByUid(Integer userId,Integer portalId) {
        return this.baseMapper.getPermissionByUid(userId,portalId);
    }

    @Override
    public GcUserAccessPermission getPermissionByUserAccessId(Integer userAccessId) {
        return this.baseMapper.getPermissionByUserAccessId(userAccessId);
    }

    @Override
    public List<GcUserAccessPermission> selectUserAccessPermissions(List<Integer> userAccessIds) {
        // TODO Auto-generated method stub
        QueryWrapper queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.in("user_access_id",userAccessIds);
        return this.list(queryWrapper);
    }

    @Override
    public List<GcUserAccessPermission> getPermissionByUserAccessIdList(List<Integer> ids) {
        QueryWrapper queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.in("user_access_id",ids);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public void insertUserPermission(List<GcUserAccessPermission> permissionList) {
        this.baseMapper.insertUserPermission(permissionList);
    }

    @Override
    public List<GcUserAccessPermission> getPermissionByUidList(Integer userId, Integer masterId) {
        return this.baseMapper.getPermissionByUidList(userId,masterId);
    }
    @Override
    public List<GcUserAccessPermission> getGroupMemberByUidList(Integer userId , Integer masterId){
        return this.baseMapper.getGroupMemberByUidList(userId,masterId);
    }

    @Override
    public List<GcUserAccessPermission> getGroupMemberPermissionByUidList(Integer userId, Integer masterId,String roleName) {
        return this.baseMapper.getGroupMemberPermissionByUidList(userId,masterId,roleName);
    }
}
