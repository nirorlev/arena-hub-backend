package com.threeatom.guidecore.service;

import com.threeatom.guidecore.entity.GcGroup;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 教师编辑的组权限 服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-12-25
 */
public interface GcGroupService extends IService<GcGroup> {
    
    /***
     * 获取相关userAccessId的所有相关组
     * @param userAccessIds
     * @return
     */
    List<GcGroup> getGroupListByUserAccessIds(List<Integer> userAccessIds);
    
    @Deprecated
    boolean checkGroupCode(String code);

}
