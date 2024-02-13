//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.system.entity.SysBusiness;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.entity.SysSystemConfig;
import com.threeatom.system.mapper.SysSystemConfigMapper;
import com.threeatom.system.mapper.SysSystemMapper;
import com.threeatom.system.service.SysSystemService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class SysSystemServiceImpl extends ServiceImpl<SysSystemMapper, SysSystem>
        implements SysSystemService {
    private static final String CACHE_TAG = "SysSystem";
    private static final String KEY_TAG_ENTITY = "'entity:'+";
    private static final String KEY_TAG_LIST_BUSINESSKEY = "'list:key-'+";

    @Autowired private Environment env;

    @Autowired private SysSystemConfigMapper systemConfigMapper;

    public SysSystemServiceImpl() {}

    @Cacheable(
            value = {"SysSystem"},
            key = "'list:key-'+#p0")
    public List<SysSystem> getSystemListByBusinessKeyCache(String key) {
        return ((SysSystemMapper) this.baseMapper).selectSysSystemListByBusinessKey(key);
    }

    @Cacheable(
            value = {"SysSystem"},
            key = "'entity:'+#p0")
    public SysSystem getSystemById(Integer id) {
        return ((SysSystemMapper) this.baseMapper).selectSystemById(id);
    }

    public SysSystemConfig getSystemConfig(Integer sysId) {
        QueryWrapper<SysSystemConfig> queryWrapper = new QueryWrapper();
        queryWrapper.eq("sys_id", sysId);
        return (SysSystemConfig) this.systemConfigMapper.selectOne(queryWrapper);
    }

    @Override
    public SysSystem getSystem() {
        String sysIds = env.getProperty("systemId");
        int sysId = Integer.parseInt(sysIds);
        //    	SysSystem sys = systemService.getSystemById(sysId);

        SysSystem sys = new SysSystem();
        sys.setId(sysId);
        sys.setName("xxx");
        SysBusiness sb = new SysBusiness();
        sb.setKey("xxx");
        sys.setBusiness(sb);
        return sys;
    }
}
