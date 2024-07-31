package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserVideoPlaysNode;
import java.util.List;

public interface GcUserVideoPlaysNodeService extends IService<GcUserVideoPlaysNode> {

    GcUserVideoPlaysNode getVideoPlayNodeByNodeId(Integer nodeId);

    List<GcUserVideoPlaysNode> getVideoPlaysNodes(Integer videoplayId);

    List<GcUserVideoPlaysNode> getLastVideoPlayNodes(Integer videoPlayId);
}
