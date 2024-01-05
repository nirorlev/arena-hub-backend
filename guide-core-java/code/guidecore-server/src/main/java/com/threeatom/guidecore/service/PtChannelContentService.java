package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.entity.PtChannelContent;
import com.threeatom.system.entity.SysFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 系统帮助-反馈
 *
 * @author huangpei
 * @Date 2021-10-26
 */
public interface PtChannelContentService extends IService<PtChannelContent> {

    Boolean changeContentOrder(List<Integer> contentIds);

    List<SysFile> selectVideosInChannel(Integer channelId, String order, Integer videoFileId, HttpServletRequest request);

    Boolean deleteContent(Integer videoId,Integer channelId);

    List<PtChannelContent> selectContentExist(Integer channelId);



}
