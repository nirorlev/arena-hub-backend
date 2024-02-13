package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 主站点实例 服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-11
 */
public interface GcMasterService extends IService<GcMaster> {

    GcMaster getMasterByUidCache(Integer uid);

    boolean setMasterState(Integer uid, Integer value);

    boolean superAdminSetMasterState(Integer masterId, Integer value);

    boolean setMaster(GcMaster master);

    GcMaster getMasterByContext(String context);

    GcMaster getMasterById(Integer id);

    Object getMasterConfig(Integer id, String key);

    boolean updateSourceNull(Integer id);

    GcMaster getMaster(String context);

    Message getContentFromOneFolder(
            GcUserSaveFolder gcUserSaveFolder, GcUser user, HttpServletRequest request, Integer envFlag);
}
