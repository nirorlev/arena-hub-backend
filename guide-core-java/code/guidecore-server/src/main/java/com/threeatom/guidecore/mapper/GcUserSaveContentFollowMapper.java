package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUserSaveContentFollow;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qiaoxide
 * @since 2019-12-11
 */
public interface GcUserSaveContentFollowMapper extends BaseMapper<GcUserSaveContentFollow> {
	
	List<Integer> selectFollowList(@Param("userId") Integer userId, @Param("masterId") Integer masterId);

	List<GcUserSaveContentFollow> selectFollowListByPlayListId(@Param("ids")List<Integer> ids);
}
