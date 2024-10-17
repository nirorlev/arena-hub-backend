package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.IdsDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannelContent;
import com.threeatom.system.entity.SysFile;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public interface PtChannelContentService extends IService<PtChannelContent> {

    Boolean changeContentOrder(List<Integer> contentIds);

    List<SysFile> selectVideosInChannel(
        Integer channelId, String order, Integer videoFileId, HttpServletRequest request, Integer userId);

    Boolean deleteContent(Integer videoId, Integer channelId);

    List<PtChannelContent> selectContentExist(Integer channelId);

    void saveOrUpdateChannelContent(List<PtChannelContent> ptChannelContent, List<SysFile> sysFileList, Integer channelId,
                                    PortalUser portalUser);

    Optional<PtChannelContent> getChannelContent(Integer contentId);

    void updateContentOrder(IdsDto ids, Integer channelId, Integer masterId);
}
