package com.threeatom.guidecore.service.impl;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.alibaba.druid.sql.visitor.functions.IfNull;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcFeedBackMapper;
import com.threeatom.guidecore.mapper.PtchannelMapper;
import com.threeatom.guidecore.service.GcFeedBackService;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.service.PtTagsService;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PtChannelServiceImpl extends ServiceImpl<PtchannelMapper, PtChannel> implements PtChannelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserSaveFolderServiceImpl.class);

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private PtTagsService tagsService;

    public List<PtChannel> indexPtChannels(Integer userId, Integer type,HttpServletRequest request,Integer masterId){
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<PtChannel> channels = this.baseMapper.indexPtChannels(userId,type,masterId,null);
        Map<Integer,List<PtTags>> tagMap = new HashMap<>();
        if (channels.size()!=TableConstant.COMMON_ZERO){
            List<PtTags> tagsList = tagsService.selectPtChannelTagByIds(channels.stream().map(PtChannel::getId).collect(Collectors.toList()), masterId);
            tagMap = tagsList.stream().collect(Collectors.groupingBy(PtTags::getChannelId));
        }


        List<Integer> channelIdList = channels.stream() .map(PtChannel::getId) .collect(Collectors.toList());
        Map<Integer,PtChannel> ptChannelMap = new HashMap<>();
        if (channelIdList.size()!=TableConstant.COMMON_ZERO) {
            List<PtChannel> accessChannelList = this.baseMapper.getAccessChannelList(channelIdList, masterId, userId);
            ptChannelMap = accessChannelList.stream().collect(Collectors.toMap(PtChannel::getId,PtChannel -> PtChannel, (key1, key2) -> key2, LinkedHashMap::new));
        }

        for(PtChannel channel : channels){
            if (null!=tagMap.get(channel.getId())){
                List<PtTags> tagsList = tagMap.get(channel.getId());
                List<String> strings = tagsList.stream().map(PtTags::getTagText).collect(Collectors.toList());
                channel.setChannelTags(StringUtils.join(strings, ","));
            }
            if (null!=ptChannelMap&&null!=ptChannelMap.get(channel.getId())){
                PtChannel accessChannel = ptChannelMap.get(channel.getId());
                if (null!=accessChannel.getSubscribeAccessList()){
                    channel.setSubscribeAccessList(accessChannel.getSubscribeAccessList());
                }
                if (null!=accessChannel.getAccessList()){
                    channel.setAccessList(accessChannel.getAccessList());
                }
            }
            if(Objects.nonNull(channel.getChannelAvatarFileId())){
                SysFile avatarFile = sysFileService.getById(channel.getChannelAvatarFileId());
                channel.setAvatarFullFileUrl(sysFileService.getResFullUrl(avatarFile,request));
            }

            if(Objects.nonNull(channel.getChannelImgFileId())){
                SysFile imgFile = sysFileService.getById(channel.getChannelImgFileId());
                channel.setImgFullFileUrl(sysFileService.getResFullUrl(imgFile,request));
            }

        }
        return channels;
    }

    public List<PtChannel> selectPtChannels(Integer userId, Integer type,HttpServletRequest request,Integer masterId){
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<PtChannel> channels = this.baseMapper.selectChannelList(userId,type,masterId);

        //查询订阅人数
//        for(PtChannel ptChannel : channels){
//            ptChannel.setFollowFlag(TableConstant.COMMON_ZERO);
//        }

        //查询当前用户follow的channel
//        PtChannel followedChannel = this.baseMapper.selectFollowedChannel(userId,masterId);
//        if(Objects.nonNull(followedChannel)){
//            if(CollectionUtils.isNotEmpty(followedChannel.getSubscribeAccessIds())){
//                for(PtChannel ptChannel : channels){
//                    if(followedChannel.getSubscribeAccessIds().contains(ptChannel.getId())){
//                        ptChannel.setFollowFlag(TableConstant.COMMON_ONE);
//                    }
//                }
//            }
//        }



        List<String> slugList = channels.stream().map(PtChannel::getChannelSlug).collect(Collectors.toList());
        Map<String, HashMap> ptChannelsMap = new HashMap<>();
        if (slugList.size()!=TableConstant.COMMON_ZERO && !CollectionUtils.isEmpty(slugList)){
            ptChannelsMap = baseMapper.selectFollowedUsers(slugList, masterId);
        }
        for(PtChannel channel : channels){
//            主要添加文件
            if(CollectionUtils.isNotEmpty(channel.getVideoList())) {
                for (SysFile gcvideofile : channel.getVideoList()) {
                    SysFile imgFile = sysFileService.getById(gcvideofile.getId());
                    String imgFullFileUrl = sysFileService.getResFullUrl(imgFile, request);
                    gcvideofile.setFullFileUrl(imgFullFileUrl);
                }
            }
            //查询订阅人数sql已去重
            if (!"".equals(channel.getSubscribeUserIds()) && Objects.nonNull(channel.getSubscribeUserIds()) //判断slug不为空
                    && null!=ptChannelsMap && ptChannelsMap.size()!=TableConstant.COMMON_ZERO){  //判断ptChannelsMap不为空
                //获取订阅人数
                HashMap subscribers = ptChannelsMap.get(channel.getChannelSlug());
                if(subscribers != null ){
                    // List<String> subscribeUserIds = Arrays.asList(subscribers.getSubscribeUserIds().split(","));
                    // subscribeUserIds = subscribeUserIds.stream().distinct().collect(Collectors.toList());
                    Long subscribeUserIds = (Long) subscribers.get("subscribeUserIds");
                    channel.setSubscribeNum(subscribeUserIds.intValue());
                }
            }else {
                channel.setSubscribeNum(TableConstant.COMMON_ZERO);
            }
        }
        for(PtChannel ptChannel : channels){
            if(Objects.nonNull(ptChannel.getChannelAvatarFileId())){
                SysFile avatarFile = sysFileService.getById(ptChannel.getChannelAvatarFileId());
                ptChannel.setAvatarFullFileUrl(sysFileService.getResFullUrl(avatarFile,request));
            }
            if(Objects.nonNull(ptChannel.getChannelImgFileId())){
                SysFile imgFile = sysFileService.getById(ptChannel.getChannelImgFileId());
                ptChannel.setImgFullFileUrl(sysFileService.getResFullUrl(imgFile,request));
            }
        }
        return channels;
    }

    public PtChannel selectChannelDetail(Integer channelId,String slug,HttpServletRequest request,String order,Integer masterId){
//        QueryWrapper<PtChannel> queryWrapper = new QueryWrapper<PtChannel>();
//        queryWrapper.eq("id",channelId);
//        return this.getOne(queryWrapper);
        PtChannel ptChannel = new PtChannel();
        if (null!=channelId){
            ptChannel = this.baseMapper.selectChannelDetail(channelId,null,order,null);
        }else {
            ptChannel = this.baseMapper.selectChannelDetail(null,slug,order,masterId);
        }
//        for(PtChannel channel : ptChannel.getSectionList()){
//            for(SysFile sysFile : channel.getVideoList()){
//                String fullFileUrl = sysFileService.getResFullUrl(sysFile,request);
//                String snapShotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
//                sysFile.setSnapshotUrl(snapShotUrl);
//                sysFile.setFullFileUrl(fullFileUrl);
//            }
//        }
        if(!"".equals(ptChannel.getSubscribeUserIds()) && Objects.nonNull(ptChannel.getSubscribeUserIds())){
            List<String> subscribeUserIds = Arrays.asList(ptChannel.getSubscribeUserIds().split(","));
            subscribeUserIds = subscribeUserIds.stream().distinct().collect(Collectors.toList());
            ptChannel.setSubscribeNum(subscribeUserIds.size());
        }else {
            ptChannel.setSubscribeNum(TableConstant.COMMON_ZERO);
        }
        SysFile avatarFile = sysFileService.getById(ptChannel.getChannelAvatarFileId());
        SysFile imgFile = sysFileService.getById(ptChannel.getChannelImgFileId());
        String imgFileUrl = sysFileService.getResFullUrl(imgFile,request);
        String avatarUrl = sysFileService.getResFullUrl(avatarFile,request);
        ptChannel.setImgFullFileUrl(imgFileUrl);
        ptChannel.setAvatarFullFileUrl(avatarUrl);
        for(SysFile sysFile : ptChannel.getVideoList()){
            String fullFileUrl = sysFileService.getResFullUrl(sysFile,request);
            String snapShotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
            sysFile.setSnapshotUrl(snapShotUrl);
            sysFile.setFullFileUrl(fullFileUrl);
        }
        for(GcUser user : ptChannel.getUserList()){
            SysFile sysFile = sysFileService.getById(user.getAvatarFileId());
            String avatarFullFileUrl = sysFileService.getResFullUrl(sysFile,request);
            user.setAvatarFullFileUrl(avatarFullFileUrl);
        }



        return ptChannel;
    }



    public List<PtChannel> selectSectionList(Integer fid,String slug,HttpServletRequest request,Integer masterId){
//        QueryWrapper<PtChannel> queryWrapper = new QueryWrapper<PtChannel>();
//        queryWrapper.eq("fid",fid);

        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<PtChannel> sectionList = this.baseMapper.selectSectionList(fid,slug,request.getHeader("order"),masterId);
//        List<PtChannel> sectionList = this.list(queryWrapper);
        return sectionList;
    }

    public List<SysFile> selectVideosInSection(Integer sectionId,String order,HttpServletRequest request,String searchName,Integer level){
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<SysFile> videos = this.baseMapper.selectVideosInSection(sectionId,order,searchName,request.getIntHeader("masterId"),level);
        if(CollectionUtils.isNotEmpty(videos)) {
            Map<Integer,SysFile> createFileMap = new HashMap<>();
            List<GcUser> userList = videos.stream().map(SysFile::getGcUser).collect(Collectors.toList());
            if (null!=userList&&TableConstant.COMMON_ZERO!=userList.size()){
                List<SysFile> createFile = sysFileService.listByIds(userList.stream().map(GcUser::getAvatarFileId).collect(Collectors.toList()));
                createFileMap = createFile.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));
            }
            for (SysFile sysFile : videos) {
                String fullFileUrl = sysFileService.getResFullUrl(sysFile, request);
                String snapShotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
                sysFile.setSnapshotUrl(snapShotUrl);
                sysFile.setFullFileUrl(fullFileUrl);
                if (null!=sysFile.getGcUser().getAvatarFileId()){
                    if (null!=createFileMap&&null!=createFileMap.get(sysFile.getGcUser().getAvatarFileId())){
                        sysFile.getGcUser().setAvatarFullFileUrl(sysFileService.getResFullUrl(createFileMap.get(sysFile.getGcUser().getAvatarFileId()),request));
                    }
                }
            }
        }
        return videos;
    }

    public List<PtChannel> selectChannelsByTeam(Integer accessId,Integer masterId,Integer userId,HttpServletRequest request){
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<PtChannel> channels =  this.baseMapper.selectChannelsByTeam(accessId,masterId);
        //查询订阅人数
        for(PtChannel ptChannel : channels){
            ptChannel.setFollowFlag(TableConstant.COMMON_ZERO);
        }

        //查询当前用户follow的channel
        PtChannel followedChannel = this.baseMapper.selectFollowedChannel(userId,masterId);
        if(Objects.nonNull(followedChannel)){
            if(CollectionUtils.isNotEmpty(followedChannel.getSubscribeAccessIds())){
                for(PtChannel ptChannel : channels){
                    if(followedChannel.getSubscribeAccessIds().contains(ptChannel.getId())){
                        ptChannel.setFollowFlag(TableConstant.COMMON_ONE);
                    }
                }
            }
        }



        for(PtChannel channel : channels){
            if(CollectionUtils.isNotEmpty(channel.getVideoList())) {
                for (SysFile gcvideofile : channel.getVideoList()) {
                    SysFile imgFile = sysFileService.getById(gcvideofile.getId());
                    String imgFullFileUrl = sysFileService.getResFullUrl(imgFile, request);
                    gcvideofile.setFullFileUrl(imgFullFileUrl);
                }
            }
        }
        for(PtChannel ptChannel : channels){
            if(Objects.nonNull(ptChannel.getChannelAvatarFileId())){
                SysFile avatarFile = sysFileService.getById(ptChannel.getChannelAvatarFileId());
                ptChannel.setAvatarFullFileUrl(sysFileService.getResFullUrl(avatarFile,request));
            }
            if(Objects.nonNull(ptChannel.getChannelImgFileId())){
                SysFile imgFile = sysFileService.getById(ptChannel.getChannelImgFileId());
                ptChannel.setImgFullFileUrl(sysFileService.getResFullUrl(imgFile,request));
            }
        }

        return channels;

    }

    @Override
    public List<PtChannel> selectChannelsByIdAndName(List<Integer> idList, String name,Integer userId,Integer masterId) {
        return this.baseMapper.selectChannelsByIdAndName(idList,name,userId,masterId);
    }


    public List<PtChannel> selectChannelsByIdsAndName(List<Integer> idList,String name){
        return this.baseMapper.selectChannelsByIdsAndName(idList,name);
    }
    @Override
    public List<PtChannel> indexSearchChannels(Integer userId, Integer type,HttpServletRequest request,Integer masterId){
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        String searchName = "";
        if (null!=request.getAttribute("searchName")){
            searchName = request.getAttribute("searchName").toString();
        }
        List<PtChannel> channels = this.baseMapper.indexPtChannels(userId,type,masterId,searchName);

        Map<Integer,List<PtTags>> tagMap = new HashMap<>();
        if (channels.size()!=TableConstant.COMMON_ZERO){
            List<PtTags> tagsList = tagsService.selectPtChannelTagByIds(channels.stream().map(PtChannel::getId).collect(Collectors.toList()), masterId);
            tagMap = tagsList.stream().collect(Collectors.groupingBy(PtTags::getChannelId));
        }

        for(PtChannel channel : channels){
            if (null!=tagMap.get(channel.getId())){
                List<PtTags> tagsList = tagMap.get(channel.getId());
                List<String> strings = tagsList.stream().map(PtTags::getTagText).collect(Collectors.toList());
                channel.setChannelTags(StringUtils.join(strings, ","));
            }
            if(Objects.nonNull(channel.getChannelAvatarFileId())){
                SysFile avatarFile = sysFileService.getById(channel.getChannelAvatarFileId());
                channel.setAvatarFullFileUrl(sysFileService.getResFullUrl(avatarFile,request));
            }

            if(Objects.nonNull(channel.getChannelImgFileId())){
                SysFile imgFile = sysFileService.getById(channel.getChannelImgFileId());
                channel.setImgFullFileUrl(sysFileService.getResFullUrl(imgFile,request));
            }
        }
        return channels;
    }

    @Override
    public List<PtChannel> newIndexHomeChannels(Integer userId, HttpServletRequest request, Integer masterId) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }

        List<PtChannel> channels = this.baseMapper.selectNewIndexHomeChannels(userId,masterId);

        Map<Integer,List<PtTags>> tagMap = new HashMap<>();
        if (channels.size()!=TableConstant.COMMON_ZERO){
            List<PtTags> tagsList = tagsService.selectPtChannelTagByIds(channels.stream().map(PtChannel::getId).collect(Collectors.toList()), masterId);
            tagMap = tagsList.stream().collect(Collectors.groupingBy(PtTags::getChannelId));
        }

        for(PtChannel channel : channels){
            if (null!=tagMap.get(channel.getId())){
                List<PtTags> tagsList = tagMap.get(channel.getId());
                List<String> strings = tagsList.stream().map(PtTags::getTagText).collect(Collectors.toList());
                channel.setChannelTags(StringUtils.join(strings, ","));
            }
            if(Objects.nonNull(channel.getChannelAvatarFileId())){
                SysFile avatarFile = sysFileService.getById(channel.getChannelAvatarFileId());
                channel.setAvatarFullFileUrl(sysFileService.getResFullUrl(avatarFile,request));
            }

            if(Objects.nonNull(channel.getChannelImgFileId())){
                SysFile imgFile = sysFileService.getById(channel.getChannelImgFileId());
                channel.setImgFullFileUrl(sysFileService.getResFullUrl(imgFile,request));
            }
        }
        return channels;
    }

    @Override
    public List<PtChannel> searchChannelsBySysFile(Integer userId, HttpServletRequest request, Integer masterId) {
        List<PtChannel> channels = new ArrayList<>();
        if (null!=request.getAttribute("searchName")){
            channels = this.baseMapper.searchChannelsBySysFile(request.getAttribute("searchName").toString(),userId,masterId);
        }else {
            channels = this.baseMapper.searchChannelsBySysFile(null,userId,masterId);
        }
        List<Integer> idList = channels.stream().map(PtChannel::getFileId).collect(Collectors.toList());
        if (TableConstant.COMMON_ZERO==idList.size()){
            return new ArrayList<>();
        }
        List<SysFile> fileList = sysFileService.listByIds(idList);


        Map<Integer,SysFile> createFileMap = new HashMap<>();
        List<GcUser> createUserFile = channels.stream().map(PtChannel::getCreateUser).collect(Collectors.toList());
        if (null!=createUserFile&&createUserFile.size()!=TableConstant.COMMON_ZERO){
            if (createUserFile.stream().filter(users -> null!=users&&null!=users.getAvatarFileId()).map(GcUser::getAvatarFileId).collect(Collectors.toList()).size()!=TableConstant.COMMON_ZERO){
                List<SysFile> createFile = sysFileService.listByIds(createUserFile.stream().filter(users -> null!=users&&null!=users.getAvatarFileId()).map(GcUser::getAvatarFileId).collect(Collectors.toList()));
                createFileMap = createFile.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));
            }
        }

        Map<Integer, SysFile> fileMap = fileList.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));
        for (PtChannel channel : channels) {
            if (fileMap.get(channel.fileId) != null) {
                channel.setVideoFile(fileMap.get(channel.fileId));
                channel.getVideoFile().setSnapshotUrl(sysFileService.getVideoSnapshotUrl(fileMap.get(channel.fileId)));
                channel.getVideoFile().setFullFileUrl(sysFileService.getResFullUrl(fileMap.get(channel.fileId),request));
            }
            if (null!=channel.getCreateUser()&&null!=channel.getCreateUser().getAvatarFileId()){
                if (null!=createFileMap.get(channel.getCreateUser().getAvatarFileId())){
                    channel.getCreateUser().setAvatarFullFileUrl(createFileMap.get(channel.getCreateUser().getAvatarFileId()).getFileUrl());
                }
            }
        }
        return channels;
    }


    @Override
    public List<PtChannel> searchChannelsBySysFileNew(Integer userId, HttpServletRequest request, Integer masterId) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<PtChannel> channels = this.baseMapper.indexSubscribeChannel(userId,masterId);
        List<Integer> idList = channels.stream().map(PtChannel::getFileId).collect(Collectors.toList());
        idList.addAll(channels.stream().map(PtChannel::getChannelAvatarFileId).collect(Collectors.toList()));
        if (TableConstant.COMMON_ZERO==idList.size()){
            return new ArrayList<>();
        }
        List<SysFile> fileList = sysFileService.listByIds(idList);


        Map<Integer,SysFile> createFileMap = new HashMap<>();
        List<GcUser> createUserFile = channels.stream().map(PtChannel::getCreateUser).collect(Collectors.toList());

        if (null!=createUserFile&&createUserFile.size()!=TableConstant.COMMON_ZERO){
            if (createUserFile.stream().filter(users -> null!=users&&null!=users.getAvatarFileId()).map(GcUser::getAvatarFileId).collect(Collectors.toList()).size()!=TableConstant.COMMON_ZERO){
                List<SysFile> createFile = sysFileService.listByIds(createUserFile.stream().filter(users -> null!=users&&null!=users.getAvatarFileId()).map(GcUser::getAvatarFileId).collect(Collectors.toList()));
                createFileMap = createFile.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));
            }
        }

        Map<Integer, SysFile> fileMap = fileList.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));
        for (PtChannel channel : channels) {
            if (fileMap.get(channel.fileId) != null) {
                channel.setVideoFile(fileMap.get(channel.fileId));
                channel.getVideoFile().setSnapshotUrl(sysFileService.getVideoSnapshotUrl(fileMap.get(channel.fileId)));
                channel.getVideoFile().setFullFileUrl(sysFileService.getResFullUrl(fileMap.get(channel.fileId),request));
            }
            if (null!=fileMap.get(channel.getChannelAvatarFileId())){
                channel.setAvatarFullFileUrl(sysFileService.getResFullUrl(fileMap.get(channel.getChannelAvatarFileId()),request));
            }
            if (null!=channel.getCreateUser()&&null!=channel.getCreateUser().getAvatarFileId()){
                if (null!=createFileMap.get(channel.getCreateUser().getAvatarFileId())){
                    channel.getCreateUser().setAvatarFullFileUrl(createFileMap.get(channel.getCreateUser().getAvatarFileId()).getFileUrl());
                }
            }
        }
        return channels;
    }

    @Override
    public List<PtChannel> getPtChannelVideoNow(Integer userId, HttpServletRequest request, Integer masterId) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<PtChannel> channels = this.baseMapper.indexVideoNowChannel(userId,masterId);
        List<Integer> idList = channels.stream().map(PtChannel::getFileId).collect(Collectors.toList());
        idList.addAll(channels.stream().map(PtChannel::getChannelAvatarFileId).collect(Collectors.toList()));
        if (TableConstant.COMMON_ZERO==idList.size()){
            return new ArrayList<>();
        }

        List<SysFile> fileList = sysFileService.listByIds(idList);

        Map<Integer,SysFile> createFileMap = new HashMap<>();
        List<GcUser> createUserFile = channels.stream().map(PtChannel::getCreateUser).collect(Collectors.toList());
        if (null!=createUserFile&&createUserFile.size()!=TableConstant.COMMON_ZERO){
            if (createUserFile.stream().filter(users -> null!=users&&null!=users.getAvatarFileId()).map(GcUser::getAvatarFileId).collect(Collectors.toList()).size()!=TableConstant.COMMON_ZERO){
                List<SysFile> createFile = sysFileService.listByIds(createUserFile.stream().filter(users -> null!=users&&null!=users.getAvatarFileId()).map(GcUser::getAvatarFileId).collect(Collectors.toList()));
                createFileMap = createFile.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));
            }
        }

        Map<Integer, SysFile> fileMap = fileList.stream().collect(Collectors.toMap(SysFile::getId, sysFile -> sysFile));
        for (PtChannel channel : channels) {
            if (fileMap.get(channel.fileId) != null) {
                channel.setVideoFile(fileMap.get(channel.fileId));
                channel.getVideoFile().setSnapshotUrl(sysFileService.getVideoSnapshotUrl(fileMap.get(channel.fileId)));
                channel.getVideoFile().setFullFileUrl(sysFileService.getResFullUrl(fileMap.get(channel.fileId),request));
            }
            if (null!=fileMap.get(channel.getChannelAvatarFileId())){
                channel.setAvatarFullFileUrl(sysFileService.getResFullUrl(fileMap.get(channel.getChannelAvatarFileId()),request));
            }

            if (null!=channel.getCreateUser()&&null!=channel.getCreateUser().getAvatarFileId()){
                if (null!=createFileMap.get(channel.getCreateUser().getAvatarFileId())){
                    channel.getCreateUser().setAvatarFullFileUrl(createFileMap.get(channel.getCreateUser().getAvatarFileId()).getFileUrl());
                }
            }
        }
        return channels;
    }

    @Override
    public PtChannel getbyChannelSlug(String ChannelSlug) {
        QueryWrapper<PtChannel> wrapper = new QueryWrapper<>();
        wrapper.eq("channel_slug",ChannelSlug);
        return this.baseMapper.selectOne(wrapper);
    }
}
