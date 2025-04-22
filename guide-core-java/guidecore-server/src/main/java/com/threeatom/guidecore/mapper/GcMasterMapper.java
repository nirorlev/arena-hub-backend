package com.threeatom.guidecore.mapper;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcMaster;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface GcMasterMapper extends BaseMapper<GcMaster> {

    GcMaster selectMasterByUid(Integer uid);

    GcMaster selectMasterByContext(String context);

    GcMaster selectMasterById(Integer id);

    boolean updateIntroVideoIdNull(Integer id);

    boolean updateSourceNull(Integer id);

    GcMaster getMasterByContext(String context);

    List<GcMaster> gcMasterList(@Param("masterIds") List<Integer> masterIds);

    boolean updateEmailById(@Param("id") Integer id, @Param("email") JSONArray email);

    List<GcMaster> selectMasterAndManager(@Param("searchFilter") String searchFilter);

    List<Map<String, Object>> subjectAdmins(
            @Param("masterId") Integer masterId, @Param("searchFilter") String searchFilter);
}
