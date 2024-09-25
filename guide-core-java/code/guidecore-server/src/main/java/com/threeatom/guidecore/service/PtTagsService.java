package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.PtTags;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface PtTagsService extends IService<PtTags> {

    List<PtTags> selectPtChannelTags(Integer masterId, HttpServletRequest request, String name);

    List<PtTags> selectPtChannelTagByIds(List<Integer> ids, Integer masterId);

    List<String> selectPtTagList(PtTags ptTags, HttpServletRequest request);

    void updateTags(Integer masterId, Integer fileId, List<String> newTags);
}
