package com.threeatom.guidecore.mapper;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcMaster;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 主站点实例 Mapper 接口
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-11
 */
public interface GcMasterMapper extends BaseMapper<GcMaster> {
	
	
	GcMaster selectMasterByUid(Integer uid);
	
	GcMaster selectMasterByContext(String context);
	
	GcMaster selectMasterById(Integer id);
	
	boolean updateIntroVideoIdNull(Integer id);
	
	boolean updateSourceNull(Integer id);
	
	List<GcMaster> selectMasterPublicSubject();

	GcMaster getMasterByContext(String context);

	List<GcMaster> gcMasterList(@Param("masterIds") List<Integer> masterIds);

	boolean updateEmailById(@Param("id") Integer id,@Param("email") JSONArray email);

	List<GcMaster> selectMasterPublicSubjectByTag(@Param("masterId")Integer masterId,@Param("tag") String tag);

	List<GcMaster> selectMasterAndManager(@Param("searchFilter")String searchFilter);

	List<Map<String,Object>> subjectAdmins(@Param("masterId")Integer masterId,@Param("searchFilter")String searchFilter);
}
