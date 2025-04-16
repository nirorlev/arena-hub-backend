package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcGroup;
import java.util.List;

public interface GcGroupService extends IService<GcGroup> {

    List<GcGroup> getGroupListByUserAccessIds(List<Integer> userAccessIds);

    @Deprecated
    boolean checkGroupCode(String code);
}
