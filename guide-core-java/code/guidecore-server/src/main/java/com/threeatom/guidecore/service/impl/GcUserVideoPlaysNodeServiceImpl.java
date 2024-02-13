package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcUserVideoPlaysNodeMapper;
import com.threeatom.guidecore.service.*;
import com.threeatom.system.service.SysFileService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户对视频的播放记录 服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
@Service
public class GcUserVideoPlaysNodeServiceImpl
        extends ServiceImpl<GcUserVideoPlaysNodeMapper, GcUserVideoPlaysNode>
        implements GcUserVideoPlaysNodeService {

    @Autowired GcSubjectService subjectService;

    @Autowired GcVideoService videoService;

    @Autowired GcUserService userService;

    @Autowired GcUserAccessService userAccessService;

    @Autowired GcEventService eventService;

    @Autowired GcUserAnswerService userAnswerService;

    @Autowired GcMasterMessageService masterMessageService;
    @Autowired GcMasterService masterService;
    @Autowired private SysFileService sysFileService;
    @Autowired private GcUserVideoPlaysNodeMapper userVideoPlaysNodeMapper;

    @Override
    public GcUserVideoPlaysNode getVideoPlayNodeByNodeId(Integer nodeId) {
        return userVideoPlaysNodeMapper.getVideoPlayNodeByNodeId(nodeId);
    }

    @Override
    public List<GcUserVideoPlaysNode> getVideoPlaysNodes(Integer videoplayId) {

        return this.baseMapper.getVideoPlayNodes(videoplayId);
    }

    public List<GcUserVideoPlaysNode> getVideoPlayNodesByVideoId(
            Integer vid, Integer userId, Integer masterId) {

        return this.baseMapper.getVideoPlayNodesByVideoId(vid, userId, masterId);
    }

    @Override
    public List<GcUserVideoPlaysNode> getLastVideoPlayNodes(Integer videoPlayId) {
        QueryWrapper<GcUserVideoPlaysNode> queryWrapper = new QueryWrapper<GcUserVideoPlaysNode>();
        queryWrapper.eq("videoplay_id", videoPlayId);
        queryWrapper.ne("end_time", 0);
        queryWrapper.orderByDesc("update_time");
        return list(queryWrapper);
    }
}
