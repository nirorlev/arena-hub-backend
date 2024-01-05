package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUserSaveContent;
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
public interface GcUserSaveContentMapper extends BaseMapper<GcUserSaveContent> {

    /**
     * 获取二级课程id
     * @param userSaveContent
     * @return
     */
    List<Integer> getTwoSubIdList(@Param("entity")GcUserSaveContent userSaveContent);

    /**
     * 获取一级课程id
     * @param userSaveContent
     * @return
     */
    List<Integer> getOneSubIdList(@Param("entity")GcUserSaveContent userSaveContent);

    /**
     * 获取视频id
     * @param userSaveContent
     * @return
     */
    List<Integer> getVideoIdList(@Param("entity") GcUserSaveContent userSaveContent);

    List<Integer> deleteList(@Param("videoId") Integer videoId,@Param("foderIds")List<Integer> folderIds);

    List<Integer> deleteFileList(@Param("fileId") Integer fileId,@Param("foderIds")List<Integer> folderIds);

    List<Integer> selectFolderIdByVideoId(@Param("videoId") Integer videoId,@Param("masterId")Integer masterId);

    List<Integer> selectFolderIdByFileId(@Param("fileId") Integer fileId,@Param("masterId") Integer masterId);

}
