package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PtChannelContent;
import com.threeatom.guidecore.mapper.PtchannelContentMapper;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.PtChannelContentService;
import com.threeatom.system.entity.SysFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public Boolean changeContentOrder(List<Integer> contentIds) {
        List<PtChannelContent> ptChannelContents = new ArrayList<>();
        Integer order = 0;
        for (Integer id : contentIds) {
            PtChannelContent ptChannelContent = new PtChannelContent();
            ptChannelContent.setId(id);
            ptChannelContent.setContentOrder(order);
            order++;
            ptChannelContents.add(ptChannelContent);
        }
        return this.updateBatchById(ptChannelContents);
    }

    public Boolean deleteContent(Integer fileId, Integer channelId) {
        QueryWrapper<PtChannelContent> queryWrapper = new QueryWrapper<PtChannelContent>();
        queryWrapper.eq("file_id", fileId);
        queryWrapper.eq("channel_id", channelId);
        return this.remove(queryWrapper);
    }

    public List<SysFile> selectVideosInChannel(
        Integer channelId, String order, Integer fileId, HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.baseMapper.selectVideosInChannel(channelId, order, fileId);
    }

    public List<PtChannelContent> selectContentExist(Integer channelId) {
        QueryWrapper<PtChannelContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("channel_id", channelId);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional
    public void saveOrUpdateChannelContent(List<PtChannelContent> ptChannelContent, List<SysFile> sysFileList,
                                           Integer channelId) {
        if (CollectionUtils.isEmpty(ptChannelContent)) {
            log.error("Channel content list cannot be empty");
            throw new IllegalArgumentException("Channel content list cannot be empty");
        }

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
        videoService.saveChannelContent(newChannelContent, sysFileList, channelId);

        for (PtChannelContent content : newChannelContent) {
            videoService.getVideoContent(content.getFileId())
                .ifPresent(videoContent -> content.setContentId(videoContent.getId()));
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
}
