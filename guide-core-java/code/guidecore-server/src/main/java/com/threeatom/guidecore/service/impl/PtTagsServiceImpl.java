package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.PtTags;
import com.threeatom.guidecore.mapper.PtTagsMapper;
import com.threeatom.guidecore.service.PtTagsService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class PtTagsServiceImpl extends ServiceImpl<PtTagsMapper, PtTags> implements PtTagsService {

    @Override
    public List<PtTags> selectPtChannelTags(
            Integer masterId, HttpServletRequest request, String name) {
        QueryWrapper<PtTags> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("channel_id");
        queryWrapper.eq("master_id", masterId);
        if (Objects.nonNull(name)) {
            queryWrapper.like("tag_text", name);
        }
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.list(queryWrapper);
    }

    @Override
    public List<PtTags> selectPtChannelTagByIds(List<Integer> ids, Integer masterId) {
        QueryWrapper<PtTags> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("channel_id", ids);
        queryWrapper.eq("master_id", masterId);
        return this.list(queryWrapper);
    }

    @Override
    public List<String> selectPtTagList(PtTags ptTags, HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.baseMapper.selectPtTagList(ptTags);
    }

    private void remove(Integer masterId, Integer fileId) {
        QueryWrapper<PtTags> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.in("file_id", fileId);
        queryWrapper.eq("type", TableConstant.COMMON_TWO);
        this.remove(queryWrapper);
    }

    @Transactional
    public void updateTags(Integer masterId, Integer fileId, List<String> newTags) {
        if (CollectionUtils.isEmpty(newTags)) {
            return;
        }

        remove(masterId, fileId);
        List<PtTags> ptTags = new ArrayList<>();
        newTags.forEach(newTag -> {
            PtTags tag = createTag(masterId, fileId, newTag);
            ptTags.add(tag);
        });

        saveOrUpdateBatch(ptTags);
    }

    private PtTags createTag(Integer masterId, Integer fileId, String tag) {
        PtTags newTags = new PtTags();
        newTags.setMasterId(masterId);
        newTags.setTagText(tag);
        newTags.setFileId(fileId);
        newTags.setType(TableConstant.COMMON_TWO);
        newTags.setOrder(TableConstant.COMMON_ZERO);
        return newTags;
    }
}
