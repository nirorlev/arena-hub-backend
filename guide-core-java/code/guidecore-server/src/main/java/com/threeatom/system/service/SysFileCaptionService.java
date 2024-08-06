package com.threeatom.system.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.system.entity.*;
import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

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

    void updateCaptionState(JSONArray targetLang, Integer videoId);

    void deleteCaption(Integer videoId);

    SysFileCaption selectSrtData(Integer videoId);
}
