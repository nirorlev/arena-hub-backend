package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcMasterHomeInfo;
import com.threeatom.system.entity.SysSystem;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface GcMasterHomeInfoService extends IService<GcMasterHomeInfo> {

    public List<GcMasterHomeInfo> getGcMasterHomeInfoList(
            Integer masterId, List<String> nameList, SysSystem sys, HttpServletRequest request);

    public boolean saveGcMasterHomeInfo(Integer masterId, List<GcMasterHomeInfo> infoList);
}
