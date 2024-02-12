package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.PtChannelContent;
import com.threeatom.guidecore.mapper.PtchannelContentMapper;
import com.threeatom.guidecore.service.PtChannelContentService;
import com.threeatom.system.entity.SysFile;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PtChannelContentServiceImpl
        extends ServiceImpl<PtchannelContentMapper, PtChannelContent>
        implements PtChannelContentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserSaveFolderServiceImpl.class);

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
}
