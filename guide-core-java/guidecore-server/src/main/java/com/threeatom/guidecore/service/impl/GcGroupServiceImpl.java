package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcGroup;
import com.threeatom.guidecore.mapper.GcGroupMapper;
import com.threeatom.guidecore.service.GcGroupService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.GcVideoService;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GcGroupServiceImpl extends ServiceImpl<GcGroupMapper, GcGroup>
        implements GcGroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcGroupServiceImpl.class);
    @Autowired GcVideoService videoService;
    @Autowired GcUserAccessService userAccessService;

    @Override
    @Deprecated
    public boolean checkGroupCode(String code) {
        QueryWrapper<GcGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("code", code);
        if (this.getOne(queryWrapper) != null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public List<GcGroup> getGroupListByUserAccessIds(List<Integer> userAccessIds) {
        // 拼接foreach ， 并在中间都拼接 or
        String sql =
                userAccessIds.stream()
                        .map(
                                id ->
                                        new StringBuffer("JSON_CONTAINS(group_access_ids, \"")
                                                .append(id)
                                                .append("\")")
                                                .toString())
                        .collect(Collectors.joining(" or "));

        return this.baseMapper.selectGcGroupByUserAccessIds(sql);
    }
}
