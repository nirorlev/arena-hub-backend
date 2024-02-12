package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.PtTags;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Administrator
 * @title: PtTags
 * @projectName jcasbin
 * @description: TODO
 * @date 2023/1/9/00910:24
 */
public interface PtTagsService extends IService<PtTags> {

    List<PtTags> selectPtChannelTags(Integer masterId, HttpServletRequest request, String name);

    List<PtTags> selectPtChannelTagByIds(List<Integer> ids, Integer masterId);

    List<String> selectPtTagList(PtTags ptTags, HttpServletRequest request);
}
