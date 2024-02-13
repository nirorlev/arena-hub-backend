package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.system.entity.SysFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

public interface PtchannelMapper extends BaseMapper<PtChannel> {

    List<PtChannel> selectChannelList(
            @Param("userId") Integer userId,
            @Param("type") Integer type,
            @Param("masterId") Integer masterId);

    List<PtChannel> indexPtChannels(
            @Param("userId") Integer userId,
            @Param("type") Integer type,
            @Param("masterId") Integer masterId,
            @Param("channelName") String channelName);

    List<PtChannel> selectNewIndexHomeChannels(
            @Param("userId") Integer userId, @Param("masterId") Integer masterId);

    List<PtChannel> selectPtchannelVideo(
            @Param("userId") Integer userId,
            @Param("name") String name,
            @Param("masterId") Integer masterId);

    PtChannel selectChannelDetail(
            @Param("channelId") Integer channelId,
            @Param("slug") String slug,
            @Param("order") String order,
            @Param("masterId") Integer masterId);

    List<SysFile> selectVideosInSection(
            @Param("sectionId") Integer sectionId,
            @Param("order") String order,
            @Param("searchName") String searchName,
            @Param("masterId") Integer masterId,
            @Param("level") Integer level);

    List<PtChannel> selectSectionList(
            @Param("channelId") Integer channelId,
            @Param("slug") String slug,
            @Param("order") String order,
            @Param("masterId") Integer masterId);

    PtChannel selectFollowedChannel(
            @Param("userId") Integer userId, @Param("masterId") Integer masterId);

    List<PtChannel> selectChannelsByTeam(
            @Param("accessId") Integer accessId, @Param("masterId") Integer masterId);

    List<PtChannel> selectChannelsByIdAndName(
            @Param("idList") List<Integer> idList,
            @Param("name") String name,
            @Param("userId") Integer userId,
            @Param("masterId") Integer masterId);

    List<PtChannel> newSelectChannelsByIdAndName(
            @Param("idList") List<Integer> idList,
            @Param("name") String name,
            @Param("userId") Integer userId,
            @Param("masterId") Integer masterId);

    List<PtChannel> selectChannelsByIdsAndName(
            @Param("idList") List<Integer> idList, @Param("name") String name);

    // 查询已经关注的的用户
    @MapKey("channel_slug")
    Map<String, HashMap> selectFollowedUsers(
            @Param("slug") List<String> slug, @Param("masterId") Integer masterId);

    List<PtChannel> searchChannels(
            @Param("searchName") String searchName,
            @Param("userId") Integer userId,
            @Param("type") Integer type,
            @Param("masterId") Integer masterId);

    List<PtChannel> searchChannelsBySysFile(
            @Param("searchName") String searchName,
            @Param("userId") Integer userId,
            @Param("masterId") Integer masterId);

    List<PtChannel> indexSubscribeChannel(
            @Param("userId") Integer userId, @Param("masterId") Integer masterId);

    List<PtChannel> indexVideoNowChannel(
            @Param("userId") Integer userId, @Param("masterId") Integer masterId);

    List<PtChannel> getAccessChannelList(
            @Param("channelIds") List<Integer> channelIds,
            @Param("masterId") Integer masterId,
            @Param("userId") Integer userId);
}
