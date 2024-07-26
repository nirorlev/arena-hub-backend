package com.threeatom.guidecore.service.bll;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserAccess;
import java.math.BigDecimal;

public interface GcUserAccessServiceBll extends IService<GcUserAccess> {
    public BigDecimal addPoints(Integer userAccessId, BigDecimal point);

    public BigDecimal reducePoints(Integer userAccessId, BigDecimal point);

    public BigDecimal getCurrentUserPoints(Integer userAccessId);
}
