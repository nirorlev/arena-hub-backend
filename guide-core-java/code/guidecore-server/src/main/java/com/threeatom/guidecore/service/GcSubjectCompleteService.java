package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcSubjectComplete;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author PC
 * @title: GcSubjectComplete
 * @projectName uploadServer
 * @description: TODO
 * @date 2023/12/811:11
 */
public interface GcSubjectCompleteService extends IService<GcSubjectComplete> {

    GcSubjectComplete getSubjectCompleteInfo(Integer masterId,Integer userId,Integer subjectId);

    void updateStateByVideoId(Integer masterId,Integer videoId);

    List<GcSubjectComplete> selectBySubjectId(Integer masterId,Integer subjectId);
}
