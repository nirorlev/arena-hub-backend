package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUserSaveContent;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GcUserSaveContentMapper extends BaseMapper<GcUserSaveContent> {

    List<Integer> getTwoSubIdList(@Param("entity") GcUserSaveContent userSaveContent);

    List<Integer> getOneSubIdList(@Param("entity") GcUserSaveContent userSaveContent);

    List<Integer> getVideoIdList(@Param("entity") GcUserSaveContent userSaveContent);

    List<Integer> deleteList(
        @Param("videoId") Integer videoId, @Param("foderIds") List<Integer> folderIds);

    List<Integer> deleteFileList(
        @Param("fileId") Integer fileId, @Param("foderIds") List<Integer> folderIds);

    List<Integer> selectFolderIdByVideoId(
        @Param("videoId") Integer videoId, @Param("masterId") Integer masterId);

    List<Integer> selectFolderIdByFileId(
        @Param("fileId") Integer fileId, @Param("masterId") Integer masterId);

    GcUserSaveContent getByPlaylistIdAndVideoId(
        @Param("playlistId") Integer playlistId, @Param("contentId") Integer videoId);

    List<GcUserSaveContent> findVideoContentByPlaylistId(@Param("playlistId") Integer playlistId);
}
