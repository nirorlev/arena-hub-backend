package com.threeatom.guidecore.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.entity.GcUserVideoPlay;
import com.threeatom.guidecore.entity.GcUserVideoPlaysNode;
import com.threeatom.system.entity.SysSystem;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户对视频的播放记录 服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
public interface GcUserVideoPlaysNodeService extends IService<GcUserVideoPlaysNode> {

    GcUserVideoPlaysNode getVideoPlayNodeByNodeId(Integer nodeId);

    List<GcUserVideoPlaysNode> getVideoPlaysNodes(Integer videoplayId);

    List<GcUserVideoPlaysNode> getLastVideoPlayNodes(Integer videoPlayId);
}
