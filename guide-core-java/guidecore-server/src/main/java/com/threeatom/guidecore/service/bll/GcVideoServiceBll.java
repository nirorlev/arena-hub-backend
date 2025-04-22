package com.threeatom.guidecore.service.bll;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcVideo;
import java.util.Map;

public interface GcVideoServiceBll extends IService<GcVideo> {

    public Map<String, Object> getVideoSubjectInfo(Integer vid);
}
