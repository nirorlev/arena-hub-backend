package com.threeatom.guidecore.mapper;

import java.util.List;
import java.util.Map;

import com.threeatom.guidecore.entity.GcVideo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import org.springframework.data.annotation.Id;

/**
 * <p>
 * 用户对视频的操作，点赞 或者 收藏 等等  Mapper 接口
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
public interface GcUserVideoActionMapper extends BaseMapper<GcUserVideoAction> {

	List<Map<String ,Object>> countTypeRateForVideo(@Param("videoId") Integer videoId,@Param("type") Integer type);
	/**
	 * 根据课程id查询评论、点赞、星级评价
	 * @param subjectIds
	 * @return
	 */
//	@MapKey("videoId")
	List<GcUserVideoAction> getVideoActionBySubject(Map<String, Object> params);

	/**
	 * 根据课程id查询评论、点赞、星级评价
	 * @param subjectIds
	 * @return
	 */
	@MapKey("subjectId")
	Map<Integer, GcUserVideoAction> getSubjectUserStar(Map<String, Object> params);

	@MapKey("subjectId")
	Map<Integer, GcUserVideoAction> gvggetSubjectUserStar(Map<String, Object> params);

	@MapKey("subjectId")
	Map<Integer, GcUserVideoAction> gvgGetSubjectUserStar(Map<String, Object> params);


	@MapKey("subjectId")
	Map<Integer, GcUserVideoAction> getSubjectUserStars(@Param("subId")Integer subId, @Param("videoList")List<Integer> videoList);


    Integer countLikeForVideo(Integer videoId);

	Integer countLikeForFile(Integer fileId);

    List<GcUserVideoAction> countLikeForVideos(@Param("videoIds")List<Integer> videoIds);

	List<GcUserVideoAction> countLikeForFiles(@Param("fileIds")List<Integer> fileIds);

    List<GcUserVideoAction> getStarList(@Param("videoIds")List<Integer> videoIds);

	List<GcUserVideoAction> getReviewListByCourseId(@Param("subId")Integer subId,@Param("starType")Integer starType,@Param("starFilter")Integer starFilter,@Param("starOrder")Integer starOrder);
}
