package com.threeatom.guidecore.service.impl;

import static com.threeatom.guidecore.enums.ChannelVisibilityFlag.CERTAIN_TEAMS;
import static com.threeatom.guidecore.enums.ChannelVisibilityFlag.PRIVATE;
import static com.threeatom.guidecore.enums.ChannelVisibilityFlag.PUBLIC;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.IdsDto;
import com.threeatom.guidecore.dto.response.ChannelDto;
import com.threeatom.guidecore.dto.response.ChannelWithDetailsDto;
import com.threeatom.guidecore.dto.response.ChannelLatestVideosDto;
import com.threeatom.guidecore.dto.response.ChannelSectionVideosDto;
import com.threeatom.guidecore.dto.response.VideoSourceDto;
import com.threeatom.guidecore.dto.response.VideoWithDetailsDto;
import com.threeatom.guidecore.dto.response.VideoWithSourceDetailsDto;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.enums.ChannelVisibilityFlag;
import com.threeatom.guidecore.mapper.PtchannelMapper;
import com.threeatom.guidecore.mapping.ChannelMapping;
import com.threeatom.guidecore.mapping.VideoMapping;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.GcUserVideoActionService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.service.PtTagsService;
import com.threeatom.guidecore.service.VideoThumbnailProvider;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PtChannelServiceImpl extends ServiceImpl<PtchannelMapper, PtChannel> implements PtChannelService {

    private final SysFileService sysFileService;
    private final PtTagsService tagsService;
    private final ChannelMapping channelMapping;
    private final VideoThumbnailProvider thumbnailProvider;
    private final GcUserVideoActionService userVideoActionService;
    private final AuthorizationService authorizationService;
    private final VideoMapping videoMapping;

    @Lazy
    @Autowired
    private GcUserService userService;

    @Lazy
    @Autowired
    private GcVideoService videoService;

    public PtChannel selectChannelDetail(
        Integer channelId, String slug, HttpServletRequest request, String order, Integer masterId) {

        PtChannel ptChannel;
        if (null != channelId) {
            ptChannel = this.baseMapper.selectChannelDetail(channelId, null, order, null);
        } else {
            ptChannel = this.baseMapper.selectChannelDetail(null, slug, order, masterId);
        }

        SysFile avatarFile = sysFileService.getById(ptChannel.getChannelAvatarFileId());
        SysFile imgFile = sysFileService.getById(ptChannel.getChannelImgFileId());
        String imgFileUrl = sysFileService.getResFullUrl(imgFile, request);
        String avatarUrl = sysFileService.getResFullUrl(avatarFile, request);
        ptChannel.setImgFullFileUrl(imgFileUrl);
        ptChannel.setAvatarFullFileUrl(avatarUrl);

        updateImageUrls(request, ptChannel);
        updateUserAvatar(request, ptChannel);

        return ptChannel;
    }

    public List<PtChannel> selectSectionList(
        Integer fid, String slug, HttpServletRequest request, Integer masterId) {

        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }

        List<PtChannel> channels = this.baseMapper.selectSectionList(fid, slug, request.getHeader("order"), masterId);
        return channels.stream()
            .sorted(Comparator.comparing(PtChannel::getOrder, Comparator.nullsFirst(Comparator.naturalOrder())))
            .collect(Collectors.toList());
    }

    public List<SysFile> selectVideosInSection(
        Integer sectionId,
        String order,
        HttpServletRequest request,
        String searchName,
        Integer level, PortalUser portalUser) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        Integer userId = userService.getCurrentUser(request).getId();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<SysFile> videos = this.baseMapper.selectVideosInSection(
                        sectionId, order, searchName, request.getIntHeader("masterId"), level);
        if (CollectionUtils.isNotEmpty(videos)) {
            Map<Integer, SysFile> createFileMap = new HashMap<>();
            List<GcUser> userList = videos.stream().map(SysFile::getGcUser).collect(Collectors.toList());
            if (!userList.isEmpty()) {
                List<SysFile> createFile = sysFileService.listByIds(
                    userList.stream().map(GcUser::getAvatarFileId).collect(Collectors.toList()));
                createFileMap =
                        createFile.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));
            }
            for (SysFile sysFile : videos) {
                String fullFileUrl = sysFileService.getResFullUrl(sysFile, request);
                String snapShotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
                sysFile.setSnapshotUrl(snapShotUrl);
                sysFile.setThumbNailUrl(snapShotUrl);
                sysFile.setFullFileUrl(fullFileUrl);
                sysFile.setThumbNailUrl(thumbnailProvider.getThumbnailUrl(sysFile));
                sysFile.setLikeNum(userVideoActionService.countLikeForVideo(sysFile.getVideoId()));
                sysFile.setIsLiked(isLikedByUser(sysFile.getVideoId(), userId));
                GcVideo video = videoService.getVideoContentByFileId(sysFile.getId());
                sysFile.setCommentNumber(video.getCommentNum());
                video.setVideoFile(sysFile);
                videoService.updateVideoFilePrivacy(sysFile, video);
                Map<String, Boolean> permissions = authorizationService.listPermissions(video, portalUser);
                video.setPermissions(permissions);
                video.getVideoFile().setPermissions(permissions);

                if (null != sysFile.getGcUser().getAvatarFileId()) {
                    if (null != createFileMap.get(sysFile.getGcUser().getAvatarFileId())) {
                        sysFile
                                .getGcUser()
                                .setAvatarFullFileUrl(
                                        sysFileService.getResFullUrl(
                                                createFileMap.get(sysFile.getGcUser().getAvatarFileId()), request));
                    }
                }
            }
        }

        return videos;
    }

    private int isLikedByUser(Integer contentId, Integer userId) {
        return userVideoActionService.isLikedByUser(contentId, userId) ? 1 : 0;
    }

    @Override
    public List<PtChannel> selectChannelsByIdAndName(
            List<Integer> idList, String name, Integer userId, Integer masterId) {
        return this.baseMapper.selectChannelsByIdAndName(idList, name, userId, masterId);
    }

    public List<PtChannel> selectChannelsByIdsAndName(List<Integer> idList, String name) {
        return this.baseMapper.selectChannelsByIdsAndName(idList, name);
    }

    @Override
    public List<PtChannel> indexSearchChannels(
            Integer userId, Integer type, HttpServletRequest request, Integer masterId) {
        String searchName = "";
        if (null != request.getAttribute("searchName")) {
            searchName = request.getAttribute("searchName").toString();
        }
        List<PtChannel> channels = this.baseMapper.indexPtChannels(userId, type, masterId, searchName);

        Map<Integer, List<PtTags>> tagMap = new HashMap<>();
        if (channels.size() != TableConstant.COMMON_ZERO) {
            List<PtTags> tagsList =
                    tagsService.selectPtChannelTagByIds(
                            channels.stream().map(PtChannel::getId).collect(Collectors.toList()), masterId);
            tagMap = tagsList.stream().collect(Collectors.groupingBy(PtTags::getChannelId));
        }

        for (PtChannel channel : channels) {
            if (null != tagMap.get(channel.getId())) {
                List<PtTags> tagsList = tagMap.get(channel.getId());
                List<String> strings =
                        tagsList.stream().map(PtTags::getTagText).collect(Collectors.toList());
                channel.setChannelTags(StringUtils.join(strings, ","));
            }
            updateUrls(request, channel);
        }
        return channels;
    }

    private void updateUrls(HttpServletRequest request, PtChannel channel) {
        if (Objects.nonNull(channel.getChannelAvatarFileId())) {
            SysFile avatarFile = sysFileService.getById(channel.getChannelAvatarFileId());
            channel.setAvatarFullFileUrl(sysFileService.getResFullUrl(avatarFile, request));
        }

        if (Objects.nonNull(channel.getChannelImgFileId())) {
            SysFile imgFile = sysFileService.getById(channel.getChannelImgFileId());
            channel.setImgFullFileUrl(sysFileService.getResFullUrl(imgFile, request));
        }
    }

    @Override
    public List<PtChannel> newIndexHomeChannels(PortalUser portalUser, HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }

        List<PtChannel> channels = this.baseMapper.selectNewIndexHomeChannels(portalUser.getUserId(), portalUser.getMasterId());

        Map<Integer, List<PtTags>> tagMap = new HashMap<>();
        if (!channels.isEmpty()) {
            List<PtTags> tagsList =
                    tagsService.selectPtChannelTagByIds(
                            channels.stream().map(PtChannel::getId).collect(Collectors.toList()), portalUser.getMasterId());
            tagMap = tagsList.stream().collect(Collectors.groupingBy(PtTags::getChannelId));
        }

        List<Integer> channelsFiles = new ArrayList<>();
        channelsFiles =
                channels.stream()
                        .filter(e -> null != e.getChannelAvatarFileId())
                        .map(PtChannel::getChannelAvatarFileId)
                        .collect(Collectors.toList());
        List<Integer> imageFiles =
                channels.stream()
                        .filter(e -> null != e.getChannelImgFileId())
                        .map(PtChannel::getChannelImgFileId)
                        .collect(Collectors.toList());
        channelsFiles.addAll(imageFiles);
        Map<Integer, SysFile> sysFileMap = new HashMap<>();
        if (!channelsFiles.isEmpty()) {
            List<SysFile> fileList = sysFileService.listByIds(channelsFiles);
            fileList.forEach(file -> file.setFullFileUrl(sysFileService.getResFullUrl(file, request)));
            sysFileMap = fileList.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));
        }

        for (PtChannel channel : channels) {
            if (null != tagMap.get(channel.getId())) {
                List<PtTags> tagsList = tagMap.get(channel.getId());
                List<String> strings =
                        tagsList.stream().map(PtTags::getTagText).collect(Collectors.toList());
                channel.setChannelTags(StringUtils.join(strings, ","));
            }
            if (Objects.nonNull(channel.getChannelAvatarFileId())
                    && null != sysFileMap.get(channel.getChannelAvatarFileId())) {
                // SysFile avatarFile = sysFileService.getById(channel.getChannelAvatarFileId());
                channel.setAvatarFullFileUrl(
                        sysFileMap.get(channel.getChannelAvatarFileId()).getFullFileUrl());
            }

            if (Objects.nonNull(channel.getChannelImgFileId())
                    && null != sysFileMap.get(channel.getChannelImgFileId())) {
                // SysFile imgFile = sysFileService.getById(channel.getChannelImgFileId());
                channel.setImgFullFileUrl(sysFileMap.get(channel.getChannelImgFileId()).getFullFileUrl());
            }
            channel.setPermissions(authorizationService.listPermissions(channel, portalUser));
        }
        return channels;
    }

    @Override
    public List<PtChannel> searchChannelsBySysFile(Integer userId, HttpServletRequest request, Integer masterId) {
        List<PtChannel> channels;
        if (request.getAttribute("searchName") != null) {
            channels = this.baseMapper.searchChannelsBySysFile(
                request.getAttribute("searchName").toString(), userId, masterId);
        } else {
            channels = this.baseMapper.searchChannelsBySysFile(null, userId, masterId);
        }

        List<Integer> channelFileId = channels.stream().map(PtChannel::getFileId).collect(Collectors.toList());
        if (channelFileId.isEmpty()) {
            return new ArrayList<>();
        }

        List<SysFile> channelFiles = sysFileService.listByIds(channelFileId);
        Map<Integer, SysFile> idToChannelOwnerAvatarFile = new HashMap<>();
        List<GcUser> channelOwners =
                channels.stream().map(PtChannel::getCreateUser).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(channelOwners) && userAvatarFileExists(channelOwners)) {
            List<Integer> channelOwnerAvatarIds = channelOwners.stream()
                .filter(user -> null != user && null != user.getAvatarFileId())
                .map(GcUser::getAvatarFileId)
                .collect(Collectors.toList());
            List<SysFile> channelOwnerAvatarFiles = sysFileService.listByIds(channelOwnerAvatarIds);
            idToChannelOwnerAvatarFile =
                channelOwnerAvatarFiles.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));
        }

        Map<Integer, SysFile> idToChannelFiles =
                channelFiles.stream().collect(Collectors.toMap(SysFile::getId, Function.identity()));
        for (PtChannel channel : channels) {
            SysFile channelVideoFile = idToChannelFiles.get(channel.getFileId());
            if (channelVideoFile != null) {
                channel.setVideoFile(channelVideoFile);
                channelVideoFile.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(channelVideoFile));
                channelVideoFile.setFullFileUrl(sysFileService.getResFullUrl(channelVideoFile, request));
                videoService.getVideoContent(channelVideoFile.getId())
                    .ifPresent(videoContent -> channelVideoFile.setVideoId(videoContent.getId()));
            }

            if (channel.getCreateUser() != null && channel.getCreateUser().getAvatarFileId() != null) {
                if (idToChannelOwnerAvatarFile.get(channel.getCreateUser().getAvatarFileId()) != null) {
                    channel.getCreateUser().setAvatarFullFileUrl(
                        idToChannelOwnerAvatarFile.get(channel.getCreateUser().getAvatarFileId()).getFileUrl());
                }
            }
        }

        return channels;
    }

    private boolean userAvatarFileExists(List<GcUser> createUserFile) {
        return createUserFile.stream()
            .filter(users -> null != users && null != users.getAvatarFileId())
            .map(GcUser::getAvatarFileId)
            .findAny()
            .isPresent();
    }

    @Override
    public List<PtChannel> searchChannelsBySysFileNew(PortalUser portalUser, HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<PtChannel> channels = this.baseMapper.indexSubscribeChannel(portalUser.getUserId(), portalUser.getMasterId());
        List<Integer> idList = channels.stream().map(PtChannel::getFileId).collect(Collectors.toList());
        idList.addAll(
                channels.stream().map(PtChannel::getChannelAvatarFileId).collect(Collectors.toList()));
        if (idList.isEmpty()) {
            return new ArrayList<>();
        }

        List<GcUser> createUserFile =
                channels.stream().map(PtChannel::getCreateUser).collect(Collectors.toList());
        for (GcUser user : createUserFile) {
            if (null != user && null != user.getAvatarFileId()) {
                idList.add(user.getAvatarFileId());
            }
        }

        List<SysFile> fileList = sysFileService.listByIds(idList);
        List<GcVideo> videos = videoService.findByVideoIds(idList);

        Map<Integer, SysFile> fileMap =
                fileList.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));
        for (PtChannel channel : channels) {
            SysFile videoFile = fileMap.get(channel.fileId);
            if (videoFile != null) {
                channel.setVideoFile(videoFile);
                videoFile.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(videoFile));
                videoFile.setFullFileUrl(sysFileService.getResFullUrl(videoFile, request));
                videoService.getVideoContent(videoFile.getId()).ifPresent(videoContent -> {
                    videoContent.setVideoFile(videoFile);
                    videoFile.setVideoId(videoContent.getId());
                    channel.setIsLiked(userVideoActionService.isLikedByUser(videoContent.getId(), portalUser.getUserId()) ? 1 : 0);
                    channel.setLikeNum(userVideoActionService.countLikeForVideo(videoContent.getId()));
                    Map<String, Boolean> permissions = authorizationService.listPermissions(videoContent, portalUser);
                    videoContent.setPermissions(permissions);
                    videoContent.getVideoFile().setPermissions(permissions);
                });
            }
            if (null != fileMap.get(channel.getChannelAvatarFileId())) {
                channel.setAvatarFullFileUrl(
                        sysFileService.getResFullUrl(fileMap.get(channel.getChannelAvatarFileId()), request));
            }
            if (null != channel.getCreateUser() && null != channel.getCreateUser().getAvatarFileId()) {
                if (null != fileMap.get(channel.getCreateUser().getAvatarFileId())) {
                    channel
                            .getCreateUser()
                            .setAvatarFullFileUrl(
                                    fileMap.get(channel.getCreateUser().getAvatarFileId()).getFileUrl());
                }
            }
        }
        return channels;
    }

    @Override
    public List<PtChannel> getPtChannelVideoNow(PortalUser portalUser, HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<PtChannel> channels = this.baseMapper.indexVideoNowChannel(portalUser.getUserId(), portalUser.getMasterId());
        List<Integer> idList = channels.stream().map(PtChannel::getFileId).collect(Collectors.toList());
        idList.addAll(
                channels.stream().map(PtChannel::getChannelAvatarFileId).collect(Collectors.toList()));
        if (idList.isEmpty()) {
            return new ArrayList<>();
        }

        List<SysFile> fileList = sysFileService.listByIds(idList);

        for (SysFile file : fileList) {
            file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
            file.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(file));
            videoService.getVideoContent(file.getId()).ifPresent(videoContent -> {
                videoContent.setVideoFile(file);
                file.setVideoId(videoContent.getId());
                Map<String, Boolean> permissions = authorizationService.listPermissions(videoContent, portalUser);
                videoContent.setPermissions(permissions);
                videoContent.getVideoFile().setPermissions(permissions);
            });
        }

        Map<Integer, SysFile> createFileMap = new HashMap<>();
        List<GcUser> createUserFile =
                channels.stream().map(PtChannel::getCreateUser).collect(Collectors.toList());
        if (null != createUserFile && !createUserFile.isEmpty()) {
            if (userAvatarFileExists(createUserFile)) {
                List<SysFile> createFile =
                        sysFileService.listByIds(
                                createUserFile.stream()
                                        .filter(users -> null != users && null != users.getAvatarFileId())
                                        .map(GcUser::getAvatarFileId)
                                        .collect(Collectors.toList()));
                createFileMap =
                        createFile.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));
            }
        }

        Map<Integer, SysFile> fileMap =
                fileList.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));

        for (PtChannel channel : channels) {
            if (fileMap.get(channel.fileId) != null) {
                SysFile videoFile = fileMap.get(channel.fileId);
                channel.setVideoFile(videoFile);
                channel.getVideoFile().setSnapshotUrl(videoFile.getSnapshotUrl());
                channel.getVideoFile().setFullFileUrl(videoFile.getFullFileUrl());
                channel.setIsLiked(userVideoActionService.isLikedByUser(videoFile.getVideoId(), portalUser.getUserId()) ? 1 : 0);
                channel.setLikeNum(userVideoActionService.countLikeForVideo(videoFile.getVideoId()));
            }
            if (null != fileMap.get(channel.getChannelAvatarFileId())) {
                channel.setAvatarFullFileUrl(
                        fileMap.get(channel.getChannelAvatarFileId()).getFullFileUrl());
            }

            if (null != channel.getCreateUser() && null != channel.getCreateUser().getAvatarFileId()) {
                if (null != createFileMap.get(channel.getCreateUser().getAvatarFileId())) {
                    channel
                            .getCreateUser()
                            .setAvatarFullFileUrl(
                                    createFileMap.get(channel.getCreateUser().getAvatarFileId()).getFileUrl());
                }
            }
        }
        return channels;
    }

    @Override
    public PtChannel getbyChannelSlug(String ChannelSlug) {
        QueryWrapper<PtChannel> wrapper = new QueryWrapper<>();
        wrapper.eq("channel_slug", ChannelSlug);
        return this.baseMapper.selectOne(wrapper);
    }

    @Override
    public List<DbAnalyticsResultDto> getChannelsCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        return baseMapper.getChannelsCountAnalytics(filter, masterId);
    }

    @Override
    public List<ChannelWithDetailsDto> getOwnedChannels(PortalUser portalUser, HttpServletRequest request) {
        List<PtChannel> channels = baseMapper.selectOwnChannels(portalUser.getUserId(), portalUser.getMasterId());
        channels.forEach(channel -> {
            updateUrls(request, channel);
            channel.setPermissions(authorizationService.listPermissions(channel, portalUser));
        });
        return convert(channels);
    }

    @Override
    public List<ChannelWithDetailsDto> getSubscribedChannels(PortalUser portalUser, HttpServletRequest request) {
        List<PtChannel> channels = baseMapper.selectSubscribedChannels(portalUser.getUserId(), portalUser.getMasterId());
        channels.forEach(channel -> {
            updateUrls(request, channel);
            channel.setPermissions(authorizationService.listPermissions(channel, portalUser));
        });
        return convert(channels);
    }

    @Override
    public List<ChannelWithDetailsDto> getDiscoverableChannels(PortalUser portalUser, HttpServletRequest request) {
        List<PtChannel> channels = baseMapper.selectDiscoverableChannels(portalUser.getUserId(), portalUser.getMasterId());
        channels.forEach(channel -> {
            updateUrls(request, channel);
            channel.setPermissions(authorizationService.listPermissions(channel, portalUser));
        });
        return convert(channels);
    }

    private void updateUserAvatar(HttpServletRequest request, PtChannel ptChannel) {
        for (GcUser user : ptChannel.getUserList()) {
            SysFile sysFile = sysFileService.getById(user.getAvatarFileId());
            String avatarFullFileUrl = sysFileService.getResFullUrl(sysFile, request);
            user.setAvatarFullFileUrl(avatarFullFileUrl);
        }
    }

    private void updateImageUrls(HttpServletRequest request, PtChannel ptChannel) {
        for (SysFile sysFile : ptChannel.getVideoList()) {
            String fullFileUrl = sysFileService.getResFullUrl(sysFile, request);
            String snapShotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
            sysFile.setSnapshotUrl(snapShotUrl);
            sysFile.setFullFileUrl(fullFileUrl);
        }
    }

    @Override
    @Transactional
    public void updateSectionOrder(IdsDto sectionIds, Integer masterId) {
        List<Integer> channelIdsToSort = sectionIds.getIds();
        List<PtChannel> channels = findByIdAndMasterId(channelIdsToSort, masterId);

        if (channels.isEmpty()) {
            return;
        }

        Map<Integer, PtChannel> idToChannel = channels.stream()
            .collect(Collectors.toMap(PtChannel::getId, channel -> channel));
        for (int i = 0; i < channelIdsToSort.size(); i++) {
            idToChannel.get(channelIdsToSort.get(i)).setOrder(i);
        }

        this.updateBatchById(idToChannel.values());
    }

    public List<PtChannel> findByIdAndMasterId(List<Integer> channelIds, Integer masterId) {
        QueryWrapper<PtChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", channelIds);
        queryWrapper.eq("master_id", masterId);
        return this.list(queryWrapper);
    }

    @Override
    public PtChannel findBySlugAndMasterId(String slug, Integer masterId) {
        QueryWrapper<PtChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("channel_slug", slug);
        queryWrapper.eq("master_id", masterId);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(readOnly = true)
    public PtChannel findById(Integer id) {
        PtChannel channel = this.getById(id);

        if (channel == null) {
            log.info("Failed to find channel with id {}", id);
            throw new IllegalArgumentException(String.format("Channel with id %s not found", id));
        }

        if (channel.isSection()) {
            channel = this.getById(channel.getFid());
        }

        return channel;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countUserPrivateChannels(Integer userId, Integer masterId) {
        return countChannels(userId, masterId, List.of(PRIVATE));
    }

    @Override
    public Integer countUserPublishedChannels(Integer userId, Integer masterId) {
        return countChannels(userId, masterId, List.of(PUBLIC, CERTAIN_TEAMS));
    }

    @Override
    public void populateCreatedUserId(PtChannel channel, Integer userId) {
        if (channel.getId() == null) {
            channel.setCreateUserId(userId);
            return;
        }

        PtChannel existingChannel = this.getById(channel.getId());
        if (existingChannel.isSection()) {
            existingChannel = this.getById(existingChannel.getFid());
            channel.setCreateUserId(existingChannel.getCreateUserId());
            return;
        }

        channel.setCreateUserId(existingChannel.getCreateUserId());
    }

    @Override
    public ChannelLatestVideosDto sectionLatestVideos(Integer channelId, PortalUser portalUser) {
        List<GcVideo> latestVideos = videoService.channelLatestVideos(channelId, portalUser);

        return ChannelLatestVideosDto.builder()
            .videos(channelLatestVideos(latestVideos))
            .sections(sectionLatestVideos(latestVideos))
            .build();
    }

    @Override
    public List<VideoWithSourceDetailsDto<ChannelDto>> subscribedLatestVideos(PortalUser portalUser) {
        List<GcVideo> latestVideos = videoService.subscribedLatestChannelVideos(portalUser);

        return latestVideos.stream()
            .map(videoMapping::mapWithDetailsChannelSource)
            .collect(Collectors.toList());
    }

    @Override
    public VideoWithSourceDetailsDto<VideoSourceDto> channelVideoPlayerPage(Integer videoId, Integer channelId,
                                                                            PortalUser portalUser) {
        GcVideo video = videoService.findByVideoId(videoId);
        if (!authorizationService.checkAccess(video, PermitAction.VIEW, portalUser)) {
            log.error("User {} does not have permission to view video {}", portalUser.getUserId(), videoId);
            throw new ForbiddenException("No permission to view this video");
        }
        PtChannel channel = video.getOriginChannel();
        if (channel == null
            || !channel.getId().equals(channelId)
            || !authorizationService.checkAccess(channel, PermitAction.VIEW, portalUser)) {
            log.error("User {} does not have permission to view channel {} with video id {}", portalUser.getUserId(),
                channelId, videoId);
            throw new ForbiddenException("No permission to view this channel");
        }

        videoService.populateVideoData(List.of(video), portalUser);
        List<Integer> videoOriginSubscriberIds =
            videoService.getVideoOriginSubscriberIds(video, portalUser.getUserId());

        VideoWithSourceDetailsDto<VideoSourceDto> videoWithDetails = videoMapping.mapWithVideoSource(video);
        videoWithDetails.getOrigin().setSubscribersCount(videoOriginSubscriberIds.size());
        videoWithDetails.getOrigin().setSubscribed(videoOriginSubscriberIds.contains(portalUser.getUserId()));
        return videoWithDetails;
    }

    private List<VideoWithDetailsDto> channelLatestVideos(List<GcVideo> videos) {
        return videos.stream()
            .filter(video -> !video.getOriginChannel().isSection())
            .map(videoMapping::mapWithDetailsChannelSource)
            .collect(Collectors.toList());
    }

    private List<ChannelSectionVideosDto> sectionLatestVideos(List<GcVideo> videos) {
        Map<Integer, List<GcVideo>> sectionIdToVideos = videos.stream()
            .filter(video -> video.getOriginChannel().isSection())
            .collect(Collectors.groupingBy(video -> video.getOriginChannel().getId()));

        return sectionIdToVideos.values().stream()
            .filter(gcVideos -> !gcVideos.isEmpty())
            .map(this::channelSectionVideos)
            .collect(Collectors.toList());
    }

    private ChannelSectionVideosDto channelSectionVideos(List<GcVideo> sectionVideos) {
        return ChannelSectionVideosDto.builder()
            .name(sectionVideos.get(0).getOriginChannel().getChannelName())
            .videos(sectionVideos.stream().map(videoMapping::mapWithDetailsChannelSource).collect(Collectors.toList()))
            .build();
    }

    private int countChannels(Integer userId, Integer masterId, List<ChannelVisibilityFlag> channelVisibilityFlags) {
        QueryWrapper<PtChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_user_id", userId);
        queryWrapper.eq("master_id", masterId);
        queryWrapper.in("visible_flag", getVisibilityValues(channelVisibilityFlags));

        return this.count(queryWrapper);
    }

    private List<Integer> getVisibilityValues(List<ChannelVisibilityFlag> channelVisibilityFlags) {
        return channelVisibilityFlags.stream().map(ChannelVisibilityFlag::getValue).collect(Collectors.toList());
    }

    private List<ChannelWithDetailsDto> convert(List<PtChannel> channels) {
        return channels.stream()
            .map(channelMapping::map)
            .collect(Collectors.toList());
    }
}