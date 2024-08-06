package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUserVideoPlaysNode;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface GcUserVideoPlaysNodeMapper extends BaseMapper<GcUserVideoPlaysNode> {

    GcUserVideoPlaysNode getVideoPlayNodeByNodeId(Integer nodeId);

    List<GcUserVideoPlaysNode> getVideoPlayNodes(Integer videoplayId);

    List<GcUserVideoPlaysNode> getVideoPlayNodesByVideoId(
            Integer vid, Integer userId, Integer masterId);

    List<GcUserVideoPlaysNode> getVideoPlayNodesByUserId(Integer userId, Integer masterId);

    List<Integer> getVideoNodeByPlayId(@Param("videoIds") List<Integer> videoIds);

    List<GcUserVideoPlaysNode> getPlayNodeByPlayId(@Param("playIds") List<Integer> playIds);

    GcUserVideoPlaysNode getLastWatchDetail(
            @Param("masterId") Integer masterId,
            @Param("userId") Integer userId,
            @Param("vid") Integer vid);

    List<GcUserVideoPlaysNode> getVideoPlayNodesByUserIds(
            @Param("userIds") List<Integer> userId,
            @Param("masterId") Integer masterId,
            @Param("videoIds") List<Integer> videoIds);
}
