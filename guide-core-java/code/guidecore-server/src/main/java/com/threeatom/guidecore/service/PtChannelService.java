package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.IdsDto;
import com.threeatom.guidecore.dto.response.ChannelDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.system.entity.SysFile;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface PtChannelService extends IService<PtChannel> {

    List<PtChannel> indexPtChannels(
            Integer userId, Integer type, HttpServletRequest request, Integer masterId);

    PtChannel selectChannelDetail(
            Integer channelId, String slug, HttpServletRequest request, String order, Integer masterId);

    List<PtChannel> selectSectionList(
            Integer fid, String slug, HttpServletRequest request, Integer masterId);

    List<SysFile> selectVideosInSection(
        Integer sectionId,
        String order,
        HttpServletRequest request,
        String searchName,
        Integer level, PortalUser portalUser);

    List<PtChannel> selectChannelsByTeam(
            Integer accessId, Integer masterId, Integer userId, HttpServletRequest request);

    List<PtChannel> selectChannelsByIdAndName(
            List<Integer> idList, String name, Integer userId, Integer masterId);

    List<PtChannel> selectChannelsByIdsAndName(List<Integer> idList, String name);

    List<PtChannel> indexSearchChannels(
            Integer userId, Integer type, HttpServletRequest request, Integer masterId);

    List<PtChannel> newIndexHomeChannels(PortalUser portalUser, HttpServletRequest request);

    List<PtChannel> searchChannelsBySysFile(
            Integer userId, HttpServletRequest request, Integer masterId);

    List<PtChannel> searchChannelsBySysFileNew(PortalUser portalUser, HttpServletRequest request);

    List<PtChannel> getPtChannelVideoNow(PortalUser portalUser, HttpServletRequest request);

    PtChannel getbyChannelSlug(String channelName);

    List<DbAnalyticsResultDto> getChannelsCountAnalytics(AnalyticsFilterDto filter, Integer masterId);

    List<ChannelDto> getOwnedChannels(PortalUser portalUser, HttpServletRequest request);

    List<ChannelDto> getSubscribedChannels(PortalUser portalUser, HttpServletRequest request);

    List<ChannelDto> getDiscoverableChannels(PortalUser portalUser, HttpServletRequest request);

    void updateSectionOrder(IdsDto sectionIds, Integer masterId);

    PtChannel findBySlugAndMasterId(String slug, Integer masterId);

    PtChannel findById(Integer id);

    Integer countUserPrivateChannels(Integer userId, Integer masterId);

    Integer countUserPublishedChannels(Integer userId, Integer masterId);

    List<DbAnalyticsResultDto> getTrendChannelsCountAnalytics(AnalyticsFilterDto filter, Integer masterId);
}
