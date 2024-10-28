package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.dto.request.IdsDto;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannelContent;
import com.threeatom.guidecore.mapper.PtchannelContentMapper;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.PtChannelContentService;
import com.threeatom.system.entity.SysFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class PtChannelContentServiceImpl
    extends ServiceImpl<PtchannelContentMapper, PtChannelContent>
    implements PtChannelContentService {

    private final GcVideoService videoService;
    private final AuthorizationService authorizationService;

    public Boolean deleteContent(Integer fileId, Integer channelId) {
        QueryWrapper<PtChannelContent> queryWrapper = new QueryWrapper<PtChannelContent>();
        queryWrapper.eq("file_id", fileId);
        queryWrapper.eq("channel_id", channelId);
        return this.remove(queryWrapper);
    }

    public List<SysFile> selectVideosInChannel(
        Integer channelId, String order, Integer fileId, HttpServletRequest request, Integer userId) {
        PageParam pageParam = new PageParam(request);

        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.baseMapper.selectVideosInChannel(channelId, order, fileId, userId);
    }

    public List<PtChannelContent> selectContentExist(Integer channelId) {
        QueryWrapper<PtChannelContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("channel_id", channelId);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional
    public void saveOrUpdateChannelContent(List<PtChannelContent> ptChannelContent, List<SysFile> sysFileList,
                                           Integer channelId, PortalUser portalUser) {
        if (CollectionUtils.isEmpty(ptChannelContent)) {
            log.error("Channel content list cannot be empty");
            throw new IllegalArgumentException("Channel content list cannot be empty");
        }
        populateVideoFile(ptChannelContent, sysFileList);

        List<PtChannelContent> existingChannelContents = new ArrayList<>();
        List<PtChannelContent> newChannelContent = new ArrayList<>();

        for (PtChannelContent content : ptChannelContent) {
            Optional<GcVideo> videoContent = videoService.getVideoContent(content.getFileId());

            videoContent.ifPresentOrElse(video -> getChannelContent(video.getId())
                .ifPresentOrElse(existingChannelContent -> {
                    existingChannelContent.setChannelId(content.getChannelId());
                    existingChannelContents.add(existingChannelContent);
                }, () -> newChannelContent.add(content)), () -> newChannelContent.add(content));
        }

        updateBatchById(existingChannelContents);
        videoService.saveChannelContent(newChannelContent, channelId);

        for (PtChannelContent content : newChannelContent) {
            videoService.getVideoContent(content.getFileId()).ifPresent(videoContent -> {
                content.getVideoFile().setVideoId(videoContent.getId());
                content.getVideoFile().setPermissions(authorizationService.listPermissions(videoContent, portalUser));
                content.setContentId(videoContent.getId());
            });
        }

        this.saveOrUpdateBatch(newChannelContent);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PtChannelContent> getChannelContent(Integer contentId) {
        QueryWrapper<PtChannelContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("content_id", contentId);

        return Optional.ofNullable(this.getOne(queryWrapper));
    }

    @Override
    @Transactional
    public void updateContentOrder(IdsDto ids, Integer channelId, Integer masterId) {
        List<Integer> sortedIds = ids.getIds();
        List<PtChannelContent> content = selectContentExist(channelId);
        Map<Integer, Integer> idToOrderMap = sortedIds.stream()
            .collect(Collectors.toMap(Function.identity(), sortedIds::indexOf));

        sortContent(content, idToOrderMap);

        for (int i = 0; i < content.size(); i++) {
            content.get(i).setContentOrder(i);
            content.get(i).setUpdateTime(new Date());
        }

        updateBatchById(content);
    }

    private void populateVideoFile(List<PtChannelContent> channelContents, List<SysFile> videoFiles) {
        Map<Integer, SysFile> videoFileIdToFile = videoFiles.stream()
            .collect(Collectors.toMap(SysFile::getId, Function.identity()));

        channelContents.forEach(channelContent -> channelContent.setVideoFile(
            videoFileIdToFile.get(channelContent.getFileId())));
    }

    private void sortContent(List<PtChannelContent> content, Map<Integer, Integer> idToOrderMap) {
        content.sort((content1, content2) -> {
            Integer indexContent1 = idToOrderMap.get(content1.getId());
            Integer indexContent2 = idToOrderMap.get(content2.getId());

            // If both ids are in the sortedIds list, compare their positions
            if (indexContent1 != null && indexContent2 != null) {
                return indexContent1.compareTo(indexContent2);
            }

            // If only one id is in the sortedIds list, that content should come first
            if (indexContent1 != null) {
                return -1;
            }
            if (indexContent2 != null) {
                return 1;
            }

            // If neither id is in the sortedIds list, compare their contentOrder
            return Objects.compare(content1.getContentOrder(), content2.getContentOrder(),
                Comparator.nullsLast(Integer::compareTo));
        });
    }
}
