package com.threeatom.system.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.system.entity.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Administrator
 * @title: SysFileCaptionService
 * @projectName guidecore
 * @description: TODO
 * @date 2021/11/8/00810:56
 */
public interface SysFileCaptionService extends IService<SysFileCaption> {
    SysFile selectSysFileCaption(Integer id);

    List<SysFileCaption> selectSysFileCaptionId(Integer id);

    SysFileCaption saveYmId(Integer id);

    SysFileCaption callYunMao(SysCaptionRequest request);

    SysFileCaption selectById(Integer id);

    SysFileCaption selectMainSysFile(Integer id);

    @Async
    void asyncTask(SysFileCaption fileCaption, MultipartFile file, SysSystem sys);

    SysFileCaption getCaptionInfo(SysFileCaption sysFileCaption);

    void updateCaptionState(JSONArray targetLang,Integer videoId);

    void deleteCaption(Integer videoId);

    SysFileCaption selectSrtData(Integer videoId);
}
