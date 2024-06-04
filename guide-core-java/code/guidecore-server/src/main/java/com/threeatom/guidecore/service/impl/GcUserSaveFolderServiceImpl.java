package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUserSaveContent;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.mapper.GcUserSaveFolderMapper;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserSaveContentService;
import com.threeatom.guidecore.service.GcUserSaveFolderService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-12-11
 */
@Service
public class GcUserSaveFolderServiceImpl extends ServiceImpl<GcUserSaveFolderMapper, GcUserSaveFolder> implements GcUserSaveFolderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserSaveFolderServiceImpl.class);

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    @Lazy
    private GcUserSaveContentService gcUserSaveContentService;

    @Autowired
    @Lazy
    private GcVideoService gcVideoService;

    @Autowired
    @Lazy
    private GcSubjectService gcSubjectService;


    public List<GcUserSaveFolder> getPtHomePlayList(Integer userId, Integer masterId, List<Integer> folderIdList, HttpServletRequest request){
        String playListName = (String) request.getAttribute("playListName");
        PageParam pageParam = new PageParam(request);
        Integer pageSize = pageParam.getPageSize();
        Integer pageNum = pageParam.getPageNum();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcUserSaveFolder> gcUserSaveFolders = this.baseMapper.selectFolderForUserMaster(userId, masterId,folderIdList,null,playListName);
        gcUserSaveFolders.forEach(i->{
            if (null!=i.getUser().getInfo().getAvatarFileId()){
                SysFile imgFile = sysFileService.getById(i.getUser().getInfo().getAvatarFileId());
                String url = sysFileService.getResFullUrl(imgFile, request);
                imgFile.setFullFileUrl(url);
                i.getUser().getInfo().setAvatarFile(imgFile);
            }
        });
        return gcUserSaveFolders;
    }


    @Override
    public List<GcUserSaveFolder> getPtNewHomePlayList(Integer userId, Integer masterId, List<Integer> folderIdList, HttpServletRequest request){
       // String playListName = (String) request.getAttribute("playListName");
        PageParam pageParam = new PageParam(request);
        Integer pageSize = pageParam.getPageSize();
        Integer pageNum = pageParam.getPageNum();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcUserSaveFolder> gcUserSaveFolders = this.baseMapper.getPtNewHomePlayList(userId, masterId,folderIdList,null);
        List<Integer> fileIds = new ArrayList<>();
        for (GcUserSaveFolder gcUserSaveFolder : gcUserSaveFolders) {
            if (null!=gcUserSaveFolder.getUser().getInfo().getAvatarFileId()){
                fileIds.add(gcUserSaveFolder.getUser().getInfo().getAvatarFileId());
            }
        }

        List<SysFile> fileList = sysFileService.listByIds(fileIds);
        for (SysFile file : fileList) {
            file.setFullFileUrl(sysFileService.getResFullUrl(file,request));
        }
        Map<Integer,SysFile> sysFileMap = fileList.stream().collect(Collectors.toMap(SysFile::getId,SysFile -> SysFile, (key1, key2) -> key2, LinkedHashMap::new));

        gcUserSaveFolders.forEach(i->{
            if (null!=i.getUser().getInfo().getAvatarFileId()&&null!=sysFileMap.get(i.getUser().getInfo().getAvatarFileId())){
                i.getUser().getInfo().setAvatarFile(sysFileMap.get(i.getUser().getInfo().getAvatarFileId()));
            }
        });
        return gcUserSaveFolders;
    }

	@Override
    public List<GcUserSaveFolder> selectFolderForUserMaster(Integer userId, Integer masterId, List<Integer> folderIdList, HttpServletRequest request,List<Integer> myFolderIdList){
        PageParam pageParam = new PageParam(request);
        Integer pageSize = pageParam.getPageSize();
        Integer pageNum = pageParam.getPageNum();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcUserSaveFolder> gcUserSaveFolders = this.baseMapper.selectFolderForUserMaster(userId, masterId,folderIdList,myFolderIdList,null);
        for(GcUserSaveFolder gcUserSaveFolder : gcUserSaveFolders){
            List<GcUserSaveContent> gcUserSaveContentList = gcUserSaveContentService.selectContetnByFolderId(gcUserSaveFolder.getId());
            if(CollectionUtils.isNotEmpty(gcUserSaveContentList)){
                for(GcUserSaveContent gcUserSaveContent:gcUserSaveContentList){
                    if (Objects.nonNull(gcUserSaveContent.getFileId())){
                        SysFile sysFile = sysFileService.getById(gcUserSaveContent.getFileId());
                        gcUserSaveContent.setVideoFile(sysFile);
                    }

                    if(Objects.nonNull(gcUserSaveContent.getSubId())){
                        GcSubject subject = gcSubjectService.getById(gcUserSaveContent.getSubId());
                        SysFile sysFile = sysFileService.getById(subject.getSubImgId());
                        sysFile.setFullFileUrl(sysFileService.getResFullUrl(sysFile,request));
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

    public List<GcUserSaveFolder> selectFolderAllVideo(Integer userId, Integer masterId, List<Integer> folderIdList, HttpServletRequest request,List<Integer> myFolderIdList){
        PageParam pageParam = new PageParam(request);
        Integer pageSize = pageParam.getPageSize();
        Integer pageNum = pageParam.getPageNum();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcUserSaveFolder> gcUserSaveFolders = this.baseMapper.selectFolderAllVideo(userId, masterId,folderIdList,myFolderIdList);
        for(GcUserSaveFolder gcUserSaveFolder : gcUserSaveFolders){
            List<GcUserSaveContent> gcUserSaveContents = gcUserSaveFolder.getSaveContentList();
            for(GcUserSaveContent gcUserSaveContent : gcUserSaveContents){
                if(Objects.nonNull(gcUserSaveContent.getFileId())){
                    SysFile sysFile =sysFileService.getById(gcUserSaveContent.getFileId());
                    gcUserSaveContent.setVideoFile(sysFile);
                    String snapshotUrl = sysFileService.getVideoSnapshotUrl(sysFile);
                    gcUserSaveContent.getVideoFile().setSnapshotUrl(snapshotUrl);
                }
            }
            if (null != gcUserSaveFolder.getUser() && null != gcUserSaveFolder.getUser().getInfo() &&
                null != gcUserSaveFolder.getUser().getInfo().getAvatarFileId()){
                SysFile sysFile =sysFileService.getById(gcUserSaveFolder.getUser().getInfo().getAvatarFileId());
                sysFile.setFullFileUrl(sysFileService.getResFullUrl(sysFile,request));
                gcUserSaveFolder.getUser().getInfo().setAvatarFile(sysFile);
            }

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
//        .ne("if_private", 1)
//        .or()
//        .eq("master_id", masterId)
//        .isNull("if_private");
        return this.list(queryWrapper);
    }

    @Override
    public Integer selectFolderByIdsAndUser(List<Integer> folderIds, Integer userId) {
        return this.baseMapper.selectFolderByIdsAndUser(folderIds,userId);
    }

    @Override
    public GcUserSaveFolder getPlayListMetaConfig(Integer folderId,Integer fileId) {
        return this.baseMapper.getPlayListMetaConfig(folderId,fileId);
    }

    @Override
    public List<DbAnalyticsResultDto> getPlaylistCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        return baseMapper.getPlaylistCountAnalytics(filter, masterId);
    }
}
