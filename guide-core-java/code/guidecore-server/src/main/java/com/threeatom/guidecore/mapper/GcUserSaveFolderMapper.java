package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.PortalUser;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GcUserSaveFolderMapper extends BaseMapper<GcUserSaveFolder> {

    List<GcUserSaveFolder> selectFolderForUserMaster(
            Integer userId,
            Integer masterId,
            List<Integer> folderIdList,
            List<Integer> myFolderIdList,
            String playListName);

    List<GcUserSaveFolder> getPtNewHomePlayList(
            Integer userId, Integer masterId, List<Integer> folderIdList, List<Integer> myFolderIdList);

    List<GcUserSaveFolder> selectFolderAllVideo(
            Integer userId, Integer masterId, List<Integer> folderIdList, List<Integer> myFolderIdList);

    Integer selectFolderByIdsAndUser(
            @Param("folderIds") List<Integer> folderIds, @Param("userId") Integer userId);

    GcUserSaveFolder getPlayListMetaConfig(
            @Param("folderId") Integer folderId, @Param("fileId") Integer fileId);

    List<DbAnalyticsResultDto> getPlaylistCountAnalytics(
        @Param("filter") AnalyticsFilterDto filter,
        @Param("masterId") Integer masterId);

    List<GcUserSaveFolder> ownedPlaylists(@Param("portalUser") PortalUser portalUser, @Param("pageNum") Integer pageNum,
                                          @Param("pageSize") Integer pageSize);
}
