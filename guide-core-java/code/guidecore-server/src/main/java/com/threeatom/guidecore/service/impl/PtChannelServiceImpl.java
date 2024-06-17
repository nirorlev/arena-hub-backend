package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.response.ChannelDto;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.PtchannelMapper;
import com.threeatom.guidecore.mapping.ChannelMapping;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.service.PtTagsService;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PtChannelServiceImpl extends ServiceImpl<PtchannelMapper, PtChannel>
        implements PtChannelService {

    private final SysFileService sysFileService;
    private final PtTagsService tagsService;
    private final ChannelMapping channelMapping;

    public List<PtChannel> indexPtChannels(Integer userId, Integer type, HttpServletRequest request, Integer masterId) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<PtChannel> channels = this.baseMapper.indexPtChannels(userId, type, masterId, null);
        Map<Integer, List<PtTags>> tagMap = new HashMap<>();
        if (!channels.isEmpty()) {
            List<PtTags> tagsList =
                    tagsService.selectPtChannelTagByIds(
                            channels.stream().map(PtChannel::getId).collect(Collectors.toList()), masterId);
            tagMap = tagsList.stream().collect(Collectors.groupingBy(PtTags::getChannelId));
        }

        List<Integer> channelIdList =
                channels.stream().map(PtChannel::getId).collect(Collectors.toList());
        Map<Integer, PtChannel> ptChannelMap = new HashMap<>();
        if (!channelIdList.isEmpty()) {
            List<PtChannel> accessChannelList =
                    this.baseMapper.getAccessChannelList(channelIdList, masterId, userId);
            ptChannelMap =
                    accessChannelList.stream()
                            .collect(
                                    Collectors.toMap(
                                            PtChannel::getId,
                                            PtChannel -> PtChannel,
                                            (key1, key2) -> key2,
                                            LinkedHashMap::new));
        }

        for (PtChannel channel : channels) {
            if (null != tagMap.get(channel.getId())) {
                List<PtTags> tagsList = tagMap.get(channel.getId());
                List<String> strings =
                        tagsList.stream().map(PtTags::getTagText).collect(Collectors.toList());
                channel.setChannelTags(StringUtils.join(strings, ","));
            }
            if (null != ptChannelMap.get(channel.getId())) {
                PtChannel accessChannel = ptChannelMap.get(channel.getId());
                if (null != accessChannel.getSubscribeAccessList()) {
                    channel.setSubscribeAccessList(accessChannel.getSubscribeAccessList());
                }
                if (null != accessChannel.getAccessList()) {
                    channel.setAccessList(accessChannel.getAccessList());
                }
            }
            if (Objects.nonNull(channel.getChannelAvatarFileId())) {
                SysFile avatarFile = sysFileService.getById(channel.getChannelAvatarFileId());
                channel.setAvatarFullFileUrl(sysFileService.getResFullUrl(avatarFile, request));
            }

            if (Objects.nonNull(channel.getChannelImgFileId())) {
                SysFile imgFile = sysFileService.getById(channel.getChannelImgFileId());
                channel.setImgFullFileUrl(sysFileService.getResFullUrl(imgFile, request));
            }
        }
        return channels;
    }

    public PtChannel selectChannelDetail(
            Integer channelId, String slug, HttpServletRequest request, String order, Integer masterId) {
        //        QueryWrapper<PtChannel> queryWrapper = new QueryWrapper<PtChannel>();
        //        queryWrapper.eq("id",channelId);
        //        return this.getOne(queryWrapper);
        PtChannel ptChannel = new PtChannel();
        if (null != channelId) {
            ptChannel = this.baseMapper.selectChannelDetail(channelId, null, order, null);
        } else {
            ptChannel = this.baseMapper.selectChannelDetail(null, slug, order, masterId);
        }
        //        for(PtChannel channel : ptChannel.getSectionList()){
        //            for(SysFile sysFile : channel.getVideoList()){
        //                String fullFileUrl = sysFileService.getResFullUrl(sysFile,request);
        //                String snapShotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
        //                sysFile.setSnapshotUrl(snapShotUrl);
        //                sysFile.setFullFileUrl(fullFileUrl);
        //            }
        //        }
        if (!"".equals(ptChannel.getSubscribeUserIds())
                && Objects.nonNull(ptChannel.getSubscribeUserIds())) {
            List<String> subscribeUserIds = Arrays.asList(ptChannel.getSubscribeUserIds().split(","));
            subscribeUserIds = subscribeUserIds.stream().distinct().collect(Collectors.toList());
            ptChannel.setSubscribeNum(subscribeUserIds.size());
        } else {
            ptChannel.setSubscribeNum(TableConstant.COMMON_ZERO);
        }
        SysFile avatarFile = sysFileService.getById(ptChannel.getChannelAvatarFileId());
        SysFile imgFile = sysFileService.getById(ptChannel.getChannelImgFileId());
        String imgFileUrl = sysFileService.getResFullUrl(imgFile, request);
        String avatarUrl = sysFileService.getResFullUrl(avatarFile, request);
        ptChannel.setImgFullFileUrl(imgFileUrl);
        ptChannel.setAvatarFullFileUrl(avatarUrl);
        for (SysFile sysFile : ptChannel.getVideoList()) {
            String fullFileUrl = sysFileService.getResFullUrl(sysFile, request);
            String snapShotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
            sysFile.setSnapshotUrl(snapShotUrl);
            sysFile.setFullFileUrl(fullFileUrl);
        }
        for (GcUser user : ptChannel.getUserList()) {
            SysFile sysFile = sysFileService.getById(user.getAvatarFileId());
            String avatarFullFileUrl = sysFileService.getResFullUrl(sysFile, request);
            user.setAvatarFullFileUrl(avatarFullFileUrl);
        }

        return ptChannel;
    }

    public List<PtChannel> selectSectionList(
            Integer fid, String slug, HttpServletRequest request, Integer masterId) {
        //        QueryWrapper<PtChannel> queryWrapper = new QueryWrapper<PtChannel>();
        //        queryWrapper.eq("fid",fid);

        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<PtChannel> sectionList =
                this.baseMapper.selectSectionList(fid, slug, request.getHeader("order"), masterId);
        //        List<PtChannel> sectionList = this.list(queryWrapper);
        return sectionList;
    }

    public List<SysFile> selectVideosInSection(
            Integer sectionId,
            String order,
            HttpServletRequest request,
            String searchName,
            Integer level) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<SysFile> videos =
                this.baseMapper.selectVideosInSection(
                        sectionId, order, searchName, request.getIntHeader("masterId"), level);
        if (CollectionUtils.isNotEmpty(videos)) {
            Map<Integer, SysFile> createFileMap = new HashMap<>();
            List<GcUser> userList = videos.stream().map(SysFile::getGcUser).collect(Collectors.toList());
            if (!userList.isEmpty()) {
                List<SysFile> createFile =
                        sysFileService.listByIds(
                                userList.stream().map(GcUser::getAvatarFileId).collect(Collectors.toList()));
                createFileMap =
                        createFile.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));
            }
            for (SysFile sysFile : videos) {
                String fullFileUrl = sysFileService.getResFullUrl(sysFile, request);
                String snapShotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
                sysFile.setSnapshotUrl(snapShotUrl);
                sysFile.setFullFileUrl(fullFileUrl);
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

    public List<PtChannel> selectChannelsByTeam(
            Integer accessId, Integer masterId, Integer userId, HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<PtChannel> channels = this.baseMapper.selectChannelsByTeam(accessId, masterId);
        // 查询订阅人数
        for (PtChannel ptChannel : channels) {
            ptChannel.setFollowFlag(TableConstant.COMMON_ZERO);
        }

        // 查询当前用户follow的channel
        PtChannel followedChannel = this.baseMapper.selectFollowedChannel(userId, masterId);
        if (Objects.nonNull(followedChannel)) {
            if (CollectionUtils.isNotEmpty(followedChannel.getSubscribeAccessIds())) {
                for (PtChannel ptChannel : channels) {
                    if (followedChannel.getSubscribeAccessIds().contains(ptChannel.getId())) {
                        ptChannel.setFollowFlag(TableConstant.COMMON_ONE);
                    }
                }
            }
        }

        for (PtChannel channel : channels) {
            if (CollectionUtils.isNotEmpty(channel.getVideoList())) {
                for (SysFile gcvideofile : channel.getVideoList()) {
                    SysFile imgFile = sysFileService.getById(gcvideofile.getId());
                    String imgFullFileUrl = sysFileService.getResFullUrl(imgFile, request);
                    gcvideofile.setFullFileUrl(imgFullFileUrl);
                }
            }
        }
        for (PtChannel ptChannel : channels) {
            if (Objects.nonNull(ptChannel.getChannelAvatarFileId())) {
                SysFile avatarFile = sysFileService.getById(ptChannel.getChannelAvatarFileId());
                ptChannel.setAvatarFullFileUrl(sysFileService.getResFullUrl(avatarFile, request));
            }
            if (Objects.nonNull(ptChannel.getChannelImgFileId())) {
                SysFile imgFile = sysFileService.getById(ptChannel.getChannelImgFileId());
                ptChannel.setImgFullFileUrl(sysFileService.getResFullUrl(imgFile, request));
            }
        }

        return channels;
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
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
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
            if (Objects.nonNull(channel.getChannelAvatarFileId())) {
                SysFile avatarFile = sysFileService.getById(channel.getChannelAvatarFileId());
                channel.setAvatarFullFileUrl(sysFileService.getResFullUrl(avatarFile, request));
            }

            if (Objects.nonNull(channel.getChannelImgFileId())) {
                SysFile imgFile = sysFileService.getById(channel.getChannelImgFileId());
                channel.setImgFullFileUrl(sysFileService.getResFullUrl(imgFile, request));
            }
        }
        return channels;
    }

    @Override
    public List<PtChannel> newIndexHomeChannels(
            Integer userId, HttpServletRequest request, Integer masterId) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }

        List<PtChannel> channels = this.baseMapper.selectNewIndexHomeChannels(userId, masterId);

        Map<Integer, List<PtTags>> tagMap = new HashMap<>();
        if (channels.size() != TableConstant.COMMON_ZERO) {
            List<PtTags> tagsList =
                    tagsService.selectPtChannelTagByIds(
                            channels.stream().map(PtChannel::getId).collect(Collectors.toList()), masterId);
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
        if (TableConstant.COMMON_ZERO != channelsFiles.size()) {
            List<SysFile> fileList = sysFileService.listByIds(channelsFiles);
            fileList.forEach(
                    i -> {
                        i.setFullFileUrl(sysFileService.getResFullUrl(i, request));
                    });
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
        }
        return channels;
    }

    @Override
    public List<PtChannel> searchChannelsBySysFile(
            Integer userId, HttpServletRequest request, Integer masterId) {
        List<PtChannel> channels = new ArrayList<>();
        if (null != request.getAttribute("searchName")) {
            channels =
                    this.baseMapper.searchChannelsBySysFile(
                            request.getAttribute("searchName").toString(), userId, masterId);
        } else {
            channels = this.baseMapper.searchChannelsBySysFile(null, userId, masterId);
        }
        List<Integer> idList = channels.stream().map(PtChannel::getFileId).collect(Collectors.toList());
        if (TableConstant.COMMON_ZERO == idList.size()) {
            return new ArrayList<>();
        }
        List<SysFile> fileList = sysFileService.listByIds(idList);

        Map<Integer, SysFile> createFileMap = new HashMap<>();
        List<GcUser> createUserFile =
                channels.stream().map(PtChannel::getCreateUser).collect(Collectors.toList());
        if (null != createUserFile && createUserFile.size() != TableConstant.COMMON_ZERO) {
            if (createUserFile.stream()
                            .filter(users -> null != users && null != users.getAvatarFileId())
                            .map(GcUser::getAvatarFileId)
                            .collect(Collectors.toList())
                            .size()
                    != TableConstant.COMMON_ZERO) {
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
                channel.setVideoFile(fileMap.get(channel.fileId));
                channel
                        .getVideoFile()
                        .setSnapshotUrl(sysFileService.getVideoSnapshotUrl(fileMap.get(channel.fileId)));
                channel
                        .getVideoFile()
                        .setFullFileUrl(sysFileService.getResFullUrl(fileMap.get(channel.fileId), request));
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
    public List<PtChannel> searchChannelsBySysFileNew(
            Integer userId, HttpServletRequest request, Integer masterId) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<PtChannel> channels = this.baseMapper.indexSubscribeChannel(userId, masterId);
        List<Integer> idList = channels.stream().map(PtChannel::getFileId).collect(Collectors.toList());
        idList.addAll(
                channels.stream().map(PtChannel::getChannelAvatarFileId).collect(Collectors.toList()));
        if (TableConstant.COMMON_ZERO == idList.size()) {
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

        // Map<Integer,SysFile> createFileMap = new HashMap<>();
        // List<GcUser> createUserFile =
        // channels.stream().map(PtChannel::getCreateUser).collect(Collectors.toList());

        // if (null!=createUserFile&&createUserFile.size()!=TableConstant.COMMON_ZERO){
        //     if (createUserFile.stream().filter(users ->
        // null!=users&&null!=users.getAvatarFileId()).map(GcUser::getAvatarFileId).collect(Collectors.toList()).size()!=TableConstant.COMMON_ZERO){
        //         List<SysFile> createFile =
        // sysFileService.listByIds(createUserFile.stream().filter(users ->
        // null!=users&&null!=users.getAvatarFileId()).map(GcUser::getAvatarFileId).collect(Collectors.toList()));
        //         createFileMap = createFile.stream().collect(Collectors.toMap(SysFile::getId, sysFile
        // -> sysFile));
        //     }
        // }

        Map<Integer, SysFile> fileMap =
                fileList.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));
        for (PtChannel channel : channels) {
            if (fileMap.get(channel.fileId) != null) {
                channel.setVideoFile(fileMap.get(channel.fileId));
                channel
                        .getVideoFile()
                        .setSnapshotUrl(sysFileService.getVideoSnapshotUrl(fileMap.get(channel.fileId)));
                channel
                        .getVideoFile()
                        .setFullFileUrl(sysFileService.getResFullUrl(fileMap.get(channel.fileId), request));
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
    public List<PtChannel> getPtChannelVideoNow(
            Integer userId, HttpServletRequest request, Integer masterId) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<PtChannel> channels = this.baseMapper.indexVideoNowChannel(userId, masterId);
        List<Integer> idList = channels.stream().map(PtChannel::getFileId).collect(Collectors.toList());
        idList.addAll(
                channels.stream().map(PtChannel::getChannelAvatarFileId).collect(Collectors.toList()));
        if (TableConstant.COMMON_ZERO == idList.size()) {
            return new ArrayList<>();
        }

        List<SysFile> fileList = sysFileService.listByIds(idList);

        for (SysFile file : fileList) {
            file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
            file.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(file));
        }

        Map<Integer, SysFile> createFileMap = new HashMap<>();
        List<GcUser> createUserFile =
                channels.stream().map(PtChannel::getCreateUser).collect(Collectors.toList());
        if (null != createUserFile && createUserFile.size() != TableConstant.COMMON_ZERO) {
            if (createUserFile.stream()
                            .filter(users -> null != users && null != users.getAvatarFileId())
                            .map(GcUser::getAvatarFileId)
                            .collect(Collectors.toList())
                            .size()
                    != TableConstant.COMMON_ZERO) {
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
                channel.setVideoFile(fileMap.get(channel.fileId));
                channel.getVideoFile().setSnapshotUrl(fileMap.get(channel.fileId).getSnapshotUrl());
                channel.getVideoFile().setFullFileUrl(fileMap.get(channel.fileId).getFullFileUrl());
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
        return baseMapper.getChannelCountAnalytics(filter, masterId);
    }

    @Override
    public List<ChannelDto> getOwnerChannels(GcUser user, Integer masterId) {
        List<PtChannel> channels = baseMapper.selectOwnChannels(user.getId(), masterId);

        return channels.stream()
            .map(channelMapping::map)
            .collect(Collectors.toList());
    }
}