//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.system.entity.SysBusiness;
import com.threeatom.system.mapper.SysBusinessMapper;
import com.threeatom.system.service.SysBusinessService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SysBusinessServiceImpl extends ServiceImpl<SysBusinessMapper, SysBusiness>
        implements SysBusinessService {
    private static final String CACHE_TAG = "SysBusiness";
    private static final String KEY_TAG_ENTITY = "'entity:key-'+";

    public SysBusinessServiceImpl() {}

    @Cacheable(
            value = {"SysBusiness"},
            key = "'entity:key-'+#p0")
    public SysBusiness getSysBusinessByKeyCache(String key) {
        QueryWrapper<SysBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("\"key\"", key);
        return this.getOne(queryWrapper);
    }
}
