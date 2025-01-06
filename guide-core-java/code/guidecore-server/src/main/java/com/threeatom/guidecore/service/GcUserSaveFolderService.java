package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.CursorDto;
import com.threeatom.guidecore.dto.response.PageableDto;
import com.threeatom.guidecore.dto.response.PlaylistWithDetailsDto;
import com.threeatom.guidecore.dto.response.VideoSourceDto;
import com.threeatom.guidecore.dto.response.VideoWithSourceDetailsDto;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.PortalUser;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Param;

public interface GcUserSaveFolderService extends IService<GcUserSaveFolder> {

    List<GcUserSaveFolder> getPtHomePlayList(
        Integer userId, Integer masterId, List<Integer> folderIdList, HttpServletRequest request);

    List<GcUserSaveFolder> getPtNewHomePlayList(
        Integer userId, Integer masterId, List<Integer> folderIdList, HttpServletRequest request);

    List<GcUserSaveFolder> selectFolderForUserMaster(
        Integer userId,
        Integer masterId,
        List<Integer> folderIdList,
        HttpServletRequest request,
        List<Integer> myFolderIdList);

    List<GcUserSaveFolder> selectFolderAllVideo(
        Integer userId,
        Integer masterId,
        List<Integer> folderIdList,
        HttpServletRequest request,
        List<Integer> myFolderIdList);

    Integer countFolder(GcUserSaveFolder gcUserSaveFolder);

    List<GcUserSaveFolder> selectFolderInMaster(Integer masterId);

    Integer selectFolderByIdsAndUser(
        @Param("folderIds") List<Integer> folderIds, @Param("userId") Integer userId);

    GcUserSaveFolder getPlayListMetaConfig(Integer folderId);

    List<DbAnalyticsResultDto> getPlaylistCountAnalytics(AnalyticsFilterDto filter, Integer masterId);

    Integer countUserPrivatePlaylists(Integer userId, Integer masterId);

    Integer countUserPublicPlaylists(Integer userId, Integer masterId);

    List<PlaylistWithDetailsDto> ownedPlaylists(PortalUser portalUser);

    List<PlaylistWithDetailsDto> subscribed(PortalUser portalUser);

    PageableDto<PlaylistWithDetailsDto> discoverable(PortalUser portalUser, CursorDto cursor, Integer pageSize);

    PageableDto<VideoWithSourceDetailsDto<VideoSourceDto>> playlistLatestVideos(PortalUser portalUser, CursorDto cursor,
                                                                                Integer pageSize);

    VideoWithSourceDetailsDto<VideoSourceDto> playerPageVideo(Integer playlistId, Integer videoId,
                                                              PortalUser portalUser);
}
