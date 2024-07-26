package com.threeatom.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.system.entity.SysRole;
import com.threeatom.system.mapper.SysRoleMapper;
import com.threeatom.system.service.SysRoleService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SysRoleServiceImpl.class);
    private static final String CACHE_TAG = "SysRole";

    public SysRoleServiceImpl() {}

    @Cacheable(
            value = {"SysRole"},
            key = "'entity-list:uid-'+#p0")
    public List<SysRole> getUserRolesCache(Integer uid) {
        LOGGER.info("从数据库中获取roles");
        return ((SysRoleMapper) this.baseMapper).queryRolesByUid(uid);
    }

    public List<SysRole> getUserRoles(Integer uid) {
        return ((SysRoleMapper) this.baseMapper).queryRolesByUid(uid);
    }

    public List<SysRole> getRoles(Integer sysId) {
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper();
        queryWrapper.eq("sys_id", sysId);
        return this.list(queryWrapper);
    }
}
