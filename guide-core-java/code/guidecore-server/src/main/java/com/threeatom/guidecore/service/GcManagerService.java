package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcManager;
import javax.servlet.http.HttpServletRequest;

public interface GcManagerService extends IService<GcManager> {

    GcManager getManagerByIdCache(Integer id);

    boolean createManager(
        Integer sysId, String code, String email, String password, String fName, String lName);

    String loginGetToken(String email, String password);

    GcManager getManagerByUsername(String username);

    boolean saveOrUpdateManager(GcManager manager);

    GcManager getCurrentManager(HttpServletRequest request);
}
