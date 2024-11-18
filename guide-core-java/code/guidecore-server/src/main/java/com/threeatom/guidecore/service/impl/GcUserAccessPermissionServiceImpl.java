package com.threeatom.guidecore.service.impl;

import com.threeatom.guidecore.constant.TableConstant;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcUserAccessPermission;
import com.threeatom.guidecore.mapper.GcUserAccessPermissionMapper;
import com.threeatom.guidecore.service.GcUserAccessPermissionService;

import java.util.List;

@Service
public class GcUserAccessPermissionServiceImpl  extends ServiceImpl<GcUserAccessPermissionMapper, GcUserAccessPermission> implements GcUserAccessPermissionService {

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

    @Override
    public void updatePermissionData(Integer masterId,Integer userId) {
        this.baseMapper.updatePermissionData(masterId,userId);
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
