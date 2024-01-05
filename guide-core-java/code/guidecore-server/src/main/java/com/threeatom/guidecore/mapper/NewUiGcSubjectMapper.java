package com.threeatom.guidecore.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcSubject;

/**
* @author: rjunchao
* @date: 2021-8-15 10:22:57
* @desc: 课程首页
*/
public interface NewUiGcSubjectMapper  extends BaseMapper<GcSubject> {


	/**
	 * 	分页查询课程信息和课程图片信息
	 * 	根据一级课程id查询二级课程信息
	 * 	
	 * @param params
	 * @param page
	 * @return
	 */
	List<GcSubject> findSubjects(Map<String, Object> params);

	List<GcSubject> findSubjectsWithVideos(Map<String, Object> params);

	List<GcSubject> findSubjectsByTag(@Param("query") Map<String, Object> params);

	List<GcSubject> getTagNameAndIds(@Param("masterId")Integer masterId,@Param("userId")Integer userId,@Param("tagText")String tagText);
	/**
	 * 	按课程id集合统计课程时长
	 * @param ids
	 * @return
	 */
	@MapKey("id")
	Map<Integer, GcSubject> sumSubjectDuration(@Param("ids") List<Integer> ids);

	/**
	 * 	按一级课程id集合统计课程时长
	 * @param ids
	 * @return
	 */
	@MapKey("id")
	Map<Integer, GcSubject> sumSubject1Duration(@Param("ids") List<Integer> ids);

	/**
	 * 根据一级课程id查询二级课程
	 * @param params
	 * @return
	 */
	List<GcSubject> listByFid(Map<String, Object> params);

	/**
	 * 根据id集合查询课程信息
	 * @return
	 */
	List<GcSubject> listByIds(@Param("ids")List<Integer> ids,@Param("masterId") Integer masterId);
	/**
	 * 根据id查询总视频课程数
	 * @return
	 */
	List<Integer> countSubjects(@Param("id")Integer id);

	List<Integer> countSessions(@Param("ids") List<Integer> ids);

	/**
	 * 根据一级课程id查询二级课程id
	 * @param ids
	 * @return
	 */
	List<Integer> getTwoLevelSubByIds(@Param("ids")List<Integer> ids);

	/**
	 * 根据二级课程id获取所有课程下id
	 * @param ids
	 * @return
	 */
	List<GcSubject> getVideoNumByIds(@Param("ids")List<Integer> ids);

	List<GcSubject> select(@Param("subIds")List<Integer> subIds);

	Integer getSubjectNum(@Param("ids")List<Integer> ids);

	List<GcSubject> selectSubjectsByVids(@Param("vids")List<Integer> vids);

	List<GcSubject> selectSubjectsByIds(@Param("ids")List<Integer> ids);

	List<String> selectAllTag(@Param("masterId")Integer masterId,@Param("userId")Integer userId);

	List<String> selectSubjectTag(@Param("masterId")Integer masterId,@Param("userId")Integer userId);

	List<GcSubject> selectSubjectByAccessIds(@Param("accessPermissionId")List<Integer> accessPermissionId);

}
