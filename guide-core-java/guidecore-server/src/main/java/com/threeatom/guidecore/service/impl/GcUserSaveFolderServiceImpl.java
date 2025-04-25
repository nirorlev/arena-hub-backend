package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.CursorDto;
import com.threeatom.guidecore.dto.response.PageableDto;
import com.threeatom.guidecore.dto.response.PlaylistWithDetailsDto;
import com.threeatom.guidecore.dto.response.VideoSourceDto;
import com.threeatom.guidecore.dto.response.VideoWithSourceDetailsDto;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUserSaveContent;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.mapper.GcUserSaveFolderMapper;
import com.threeatom.guidecore.mapping.PlaylistMapping;
import com.threeatom.guidecore.mapping.VideoMapping;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserSaveContentService;
import com.threeatom.guidecore.service.GcUserSaveFolderService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.UnavailableVideoService;
import com.threeatom.guidecore.service.VideoThumbnailProvider;
import com.threeatom.guidecore.util.PaginationUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GcUserSaveFolderServiceImpl extends ServiceImpl<GcUserSaveFolderMapper, GcUserSaveFolder>
    implements GcUserSaveFolderService {

    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private VideoThumbnailProvider videoThumbnailProvider;

    @Autowired
    @Lazy
    private GcUserSaveContentService gcUserSaveContentService;

    @Autowired
    @Lazy
    private GcVideoService gcVideoService;

    @Autowired
    @Lazy
    private GcSubjectService gcSubjectService;

    @Autowired
    private PlaylistMapping playlistMapping;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private VideoMapping videoMapping;
    @Autowired
    private UnavailableVideoService unavailableVideoService;

    public List<GcUserSaveFolder> getPtHomePlayList(Integer userId, Integer masterId, List<Integer> folderIdList,
                                                    HttpServletRequest request) {
        String playListName = (String) request.getAttribute("playListName");
        PageParam pageParam = new PageParam(request);
        Integer pageSize = pageParam.getPageSize();
        Integer pageNum = pageParam.getPageNum();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcUserSaveFolder> gcUserSaveFolders =
            this.baseMapper.selectFolderForUserMaster(userId, masterId, folderIdList, null, playListName);
        gcUserSaveFolders.forEach(playlist -> {
            if (null != playlist.getUser().getInfo().getAvatarFileId()) {
                SysFile imgFile = sysFileService.getById(playlist.getUser().getInfo().getAvatarFileId());
                String url = sysFileService.getResFullUrl(imgFile, request);
                imgFile.setFullFileUrl(url);
                playlist.getUser().getInfo().setAvatarFile(imgFile);
            }
        });
        return gcUserSaveFolders;
    }


    @Override
    public List<GcUserSaveFolder> getPtNewHomePlayList(Integer userId, Integer masterId, List<Integer> folderIdList,
                                                       HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageSize = pageParam.getPageSize();
        Integer pageNum = pageParam.getPageNum();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcUserSaveFolder> gcUserSaveFolders =
            this.baseMapper.getPtNewHomePlayList(userId, masterId, folderIdList, null);
        List<Integer> fileIds = new ArrayList<>();
        for (GcUserSaveFolder gcUserSaveFolder : gcUserSaveFolders) {
            if (null != gcUserSaveFolder.getUser().getInfo().getAvatarFileId()) {
                fileIds.add(gcUserSaveFolder.getUser().getInfo().getAvatarFileId());
            }
        }

        if (CollectionUtils.isEmpty(fileIds)) {
            return gcUserSaveFolders;
        }

        List<SysFile> fileList = sysFileService.listByIds(fileIds);
        for (SysFile file : fileList) {
            file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
        }
        Map<Integer, SysFile> sysFileMap = fileList.stream().collect(
            Collectors.toMap(SysFile::getId, SysFile -> SysFile, (key1, key2) -> key2, LinkedHashMap::new));
        gcUserSaveFolders.forEach(i -> {
            if (null != i.getUser().getInfo().getAvatarFileId() &&
                null != sysFileMap.get(i.getUser().getInfo().getAvatarFileId())) {
                i.getUser().getInfo().setAvatarFile(sysFileMap.get(i.getUser().getInfo().getAvatarFileId()));
            }
        });
        return gcUserSaveFolders;
    }

    @Override
    public List<GcUserSaveFolder> selectFolderForUserMaster(Integer userId, Integer masterId,
                                                            List<Integer> folderIdList, HttpServletRequest request,
                                                            List<Integer> myFolderIdList) {
        PageParam pageParam = new PageParam(request);
        Integer pageSize = pageParam.getPageSize();
        Integer pageNum = pageParam.getPageNum();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcUserSaveFolder> gcUserSaveFolders =
            this.baseMapper.selectFolderForUserMaster(userId, masterId, folderIdList, myFolderIdList, null);
        for (GcUserSaveFolder gcUserSaveFolder : gcUserSaveFolders) {
            List<GcUserSaveContent> gcUserSaveContentList =
                gcUserSaveContentService.selectContentByPlaylistId(gcUserSaveFolder.getId());
            if (CollectionUtils.isNotEmpty(gcUserSaveContentList)) {
                for (GcUserSaveContent gcUserSaveContent : gcUserSaveContentList) {
                    if (Objects.nonNull(gcUserSaveContent.getFileId())) {
                        SysFile sysFile = sysFileService.getById(gcUserSaveContent.getFileId());
                        gcUserSaveContent.setVideoFile(sysFile);
                    }

                    if (Objects.nonNull(gcUserSaveContent.getSubId())) {
                        GcSubject subject = gcSubjectService.getById(gcUserSaveContent.getSubId());
                        SysFile sysFile = sysFileService.getById(subject.getSubImgId());
                        sysFile.setFullFileUrl(sysFileService.getResFullUrl(sysFile, request));
                        sysFile.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(sysFile));
                        subject.setSubImgFile(sysFile);
                        gcUserSaveContent.setSubject(subject);
                    }
                }
            }
            gcUserSaveFolder.setSaveContentList(gcUserSaveContentList);
        }
        return gcUserSaveFolders;
    }

    @Override
    public Integer countFolder(GcUserSaveFolder gcUserSaveFolder) {
        QueryWrapper<GcUserSaveFolder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", gcUserSaveFolder.getId());
        queryWrapper.eq("user_id", gcUserSaveFolder.getUserId());
        return this.count(queryWrapper);
    }

    @Override
    public List<GcUserSaveFolder> selectFolderInMaster(Integer masterId) {
        QueryWrapper<GcUserSaveFolder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("master_id", masterId);
        return this.list(queryWrapper);
    }

    @Override
    public Integer selectFolderByIdsAndUser(List<Integer> folderIds, Integer userId) {
        return this.baseMapper.selectFolderByIdsAndUser(folderIds, userId);
    }

    @Override
    public GcUserSaveFolder getPlayListMetaConfig(Integer folderId) {
        return this.baseMapper.getPlayListMetaConfig(folderId);
    }

    @Override
    public List<DbAnalyticsResultDto> getPlaylistCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        return baseMapper.getPlaylistCountAnalytics(filter, masterId);
    }

    @Override
    public Integer countUserPrivatePlaylists(Integer userId, Integer masterId) {
        return countPlaylists(userId, masterId, true);
    }

    @Override
    public Integer countUserPublicPlaylists(Integer userId, Integer masterId) {
        return countPlaylists(userId, masterId, false);
    }

    @Override
    public List<PlaylistWithDetailsDto> ownedPlaylists(PortalUser portalUser) {
        List<GcUserSaveFolder> playlists = this.baseMapper.ownedPlaylists(portalUser);
        return convertPlaylist(portalUser, playlists);
    }

    @Override
    public List<PlaylistWithDetailsDto> subscribed(PortalUser portalUser) {
        List<GcUserSaveFolder> playlists = this.baseMapper.subscribedPlaylists(portalUser);
        return convertPlaylist(portalUser, playlists);
    }

    @Override
    public PageableDto<PlaylistWithDetailsDto> discoverable(PortalUser portalUser, CursorDto cursor, Integer pageSize) {
        List<GcUserSaveFolder> playlists = this.baseMapper.paginatedDiscoverablePlaylists(portalUser, cursor);
        Integer totalCount = this.baseMapper.countDiscoverablePlaylists(portalUser);

        return PaginationUtil.createPageableDto(playlists, totalCount, pageSize,
            playlistList -> convertPlaylist(portalUser, playlistList), this::playlistNextCursor);
    }

    private String playlistNextCursor(List<GcUserSaveFolder> playlists) {
        if (CollectionUtils.isEmpty(playlists)) {
            return null;
        }

        GcUserSaveFolder lastPlaylist = playlists.get(playlists.size() - 1);
        CursorDto nextCursor = new CursorDto(lastPlaylist.getId(), lastPlaylist.getUpdateTime().toInstant().atZone(
            ZoneId.systemDefault()).toOffsetDateTime());
        return nextCursor.encode();
    }

    @Override
    public PageableDto<VideoWithSourceDetailsDto<VideoSourceDto>> findSubscribedPlaylistsLatestVideos(
        PortalUser portalUser, CursorDto cursor, Integer pageSize) {

        List<GcVideo> latestVideos = gcVideoService.findSubscribedPlaylistsLatestVideos(portalUser, cursor);
        unavailableVideoService.nullifyVideoData(portalUser, latestVideos);

        Integer totalCount = gcVideoService.countPlaylistLatestVideos(portalUser);

        return PaginationUtil.createPageableDto(latestVideos, totalCount, pageSize,
            this::convertVideoDetails,
            this::latestPlaylistVideosCursor);
    }

    @Override
    public List<VideoWithSourceDetailsDto<VideoSourceDto>> findPlaylistLatestVideos(PortalUser portalUser,
                                                                                    Integer playlistId) {
        GcUserSaveFolder playlist = getById(playlistId);
        if (!authorizationService.checkAccess(playlist, PermitAction.VIEW, portalUser)) {
            log.error("User {} does not have permission to view playlist {}", portalUser.getUserId(), playlistId);
            throw new ForbiddenException("You do not have permission to view this playlist");
        }

        List<GcVideo> playlistLatestVideos = gcVideoService.findPlaylistLatestVideos(playlistId, portalUser);
        unavailableVideoService.nullifyVideoData(portalUser, playlistLatestVideos);
        return convertVideoDetails(playlistLatestVideos);
    }

    @Override
    public List<GcUserSaveFolder> searchPlaylists(String searchName, PortalUser portalUser) {
        return baseMapper.searchPlaylists(searchName, portalUser.getUserId(), portalUser.getMasterId());
    }

    @Override
    public List<GcUserSaveFolder> searchSuggestedPlaylist(PortalUser portalUser) {
        return baseMapper.discoverablePlaylists(portalUser.getUserId(), portalUser.getMasterId());
    }

    private String latestPlaylistVideosCursor(List<GcVideo> videos) {
        return null;
    }

    private List<VideoWithSourceDetailsDto<VideoSourceDto>> convertVideoDetails(List<GcVideo> latestVideos) {
        return latestVideos.stream()
            .map(videoMapping::mapWithVideoSource)
            .collect(Collectors.toList());
    }

    private List<PlaylistWithDetailsDto> convertPlaylist(PortalUser portalUser, List<GcUserSaveFolder> playlists) {
        return playlists.stream()
            .map(playlist -> {
                playlist.setPermissions(authorizationService.listPermissions(playlist, portalUser));
                setFirstVideoSnapshotUrl(playlist);
                return playlistMapping.mapWithDetails(playlist);
            })
            .collect(Collectors.toList());
    }

    private void setFirstVideoSnapshotUrl(GcUserSaveFolder playlist) {
        List<GcUserSaveContent> saveContentList = playlist.getSaveContentList();
        if (CollectionUtils.isEmpty(saveContentList)) {
            return;
        }
        saveContentList.sort(
            Comparator.comparing(GcUserSaveContent::getUpdateTime, Comparator.nullsLast(Comparator.naturalOrder())));
        GcUserSaveContent saveContent = saveContentList.get(0);
        playlist.setSnapshotUrl(
            sysFileService.getFullFileUrl(videoThumbnailProvider.getThumbnailUrl(saveContent.getVideoFile())));
    }

    private int countPlaylists(Integer userId, Integer masterId, boolean isPrivate) {
        QueryWrapper<GcUserSaveFolder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("is_private", isPrivate);

        return this.count(queryWrapper);
    }
}
