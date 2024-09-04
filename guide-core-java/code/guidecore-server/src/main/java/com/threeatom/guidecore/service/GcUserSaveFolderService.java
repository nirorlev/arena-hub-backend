package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
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

    GcUserSaveFolder getPlayListMetaConfig(Integer folderId, Integer fileId);

    List<DbAnalyticsResultDto> getPlaylistCountAnalytics(AnalyticsFilterDto filter, Integer masterId);

    List<DbAnalyticsResultDto> getTrendPlaylistCountAnalytics(AnalyticsFilterDto filter, Integer masterId);

    Integer countUserPrivatePlaylists(Integer userId, Integer masterId);

    Integer countUserPublicPlaylists(Integer userId, Integer masterId);
}
