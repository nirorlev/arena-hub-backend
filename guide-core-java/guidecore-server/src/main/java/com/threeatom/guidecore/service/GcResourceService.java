package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcResource;
import java.util.List;

public interface GcResourceService extends IService<GcResource> {

    boolean saveResource(GcResource resource);

    boolean deleteResourcesByVids(List<Integer> vid);

    int getResourceNum(Integer masterId, List<Integer> subIds, Integer managerId);

    List<GcResource> getResByVid(Integer vid);
}
