package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.entity.GcResource;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.mapper.GcResourceMapper;
import com.threeatom.guidecore.service.GcResourceService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.util.I18NUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GcResourceServiceImpl extends ServiceImpl<GcResourceMapper, GcResource>
        implements GcResourceService {

    @Autowired private GcVideoService videoService;
    @Autowired private GcSubjectService subjectService;

    @Override
    public boolean deleteResourcesByVids(List<Integer> vids) {
        if (vids.size() < 1) return true;
        QueryWrapper<GcResource> queryWrapper = new QueryWrapper<GcResource>();
        queryWrapper.in("video_id", vids);
        return this.remove(queryWrapper);
    }

    @Override
    public boolean saveResource(GcResource resource) {
        // 校验数据
        Integer vid = resource.getVideoId();
        GcVideo video = videoService.getById(vid);
        if (video == null) throw new SystemException(I18NUtil.get("video.not.exist"));
        // 验证类型
        if (resource.getResourceType().equals(2)) {
            resource.setFileId(0);
        }

        return this.saveOrUpdate(resource);
    }

    @Override
    public int getResourceNum(Integer masterId, List<Integer> subIds, Integer managerId) {
        Integer type = TableConstant.COMMON_ONE;
        Integer state = TableConstant.COMMON_ZERO;
        return this.baseMapper.countResourceNum(masterId, type, state, subIds, managerId);
    }

    @Override
    public List<GcResource> getResByVid(Integer vid) {
        return this.baseMapper.selectResListByVid(vid);
    }
}
