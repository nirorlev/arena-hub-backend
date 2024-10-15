package com.threeatom.guidecore.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.controller.Message;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.EnvType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcMasterMapper;
import com.threeatom.guidecore.mapper.NewUIUserMapper;
import com.threeatom.guidecore.service.*;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class GcMasterServiceImpl extends ServiceImpl<GcMasterMapper, GcMaster>
        implements GcMasterService {
    @Resource NewUIUserMapper newUIUserMapper;

    @Autowired private SysFileService sysFileService;

    @Resource GcMasterMapper gcMasterMapper;

    @Autowired @Lazy private GcUserSaveFolderService gcUserSaveFolderService;

    @Autowired @Lazy private GcUserService gcUserService;

    @Autowired @Lazy private GcUserInfoService gcUserInfoService;

    @Autowired @Lazy private GcUserSaveContentService gcUserSaveContentService;

    @Autowired @Lazy private NewUiGcSubjectService subjectService;

    @Autowired @Lazy private GcVideoService gcVideoService;

    @Autowired private GcUserVideoActionService gcUserVideoActionService;

    @Autowired @Lazy private GcUserSaveContentFollowService gcUserSaveContentFollowService;

    @Autowired @Lazy private NewUiGcSubjectService newUiGcSubjectService;

    @Autowired private UnavailableVideoService unavailableVideoService;
    @Autowired private PortalUserService portalUserService;
    @Autowired private AuthorizationService authorizationService;

    private static final String CACHE_TAG = "GcMaster";

    private static final String KEY_TAG_ENTITY = "'entity:uid-'+";

    @Override
    @Cacheable(value = CACHE_TAG, key = KEY_TAG_ENTITY + "#p0")
    public GcMaster getMasterByUidCache(Integer uid) {
        return this.baseMapper.selectMasterByUid(uid);
    }

    @Override
    @CacheEvict(value = CACHE_TAG, key = KEY_TAG_ENTITY + "#p0")
    public boolean setMasterState(Integer uid, Integer value) {
        UpdateWrapper<GcMaster> updateWrap = new UpdateWrapper<GcMaster>();
        updateWrap.set("state", value).eq("manager_id", uid);
        return this.update(updateWrap);
    }

    @Override
    @CacheEvict(value = CACHE_TAG, key = KEY_TAG_ENTITY + "#p0")
    public boolean superAdminSetMasterState(Integer masterId, Integer value) {
        UpdateWrapper<GcMaster> updateWrap = new UpdateWrapper<GcMaster>();
        updateWrap.set("state", value).eq("id", masterId);
        return this.update(updateWrap);
    }

    @Override
    @CacheEvict(value = CACHE_TAG, key = KEY_TAG_ENTITY + "#master.managerId")
    public boolean setMaster(GcMaster master) {
        return this.updateById(master);
    }

    @Override
    public boolean updateSourceNull(Integer id) {
        return this.baseMapper.updateSourceNull(id);
    }

    @Override
    public GcMaster getMasterByContext(String context) {
        return this.baseMapper.selectMasterByContext(context);
    }

    @Override
    public GcMaster getMasterById(Integer id) {
        return this.baseMapper.selectMasterById(id);
    }

    @Override
    public Object getMasterConfig(Integer id, String key) {
        GcMaster master = this.getById(id);
        if (master.getExtVar() == null) {
            master.setExtVar(new JSONObject());
        }
        JSONObject config = master.getExtVar();
        if (config.containsKey(key)) return config.get(key);
        return null;
    }

    @Override
    public GcMaster getMaster(String context) {
        return gcMasterMapper.getMasterByContext(context);
    }

    @Override
    public Message getContentFromOneFolder(
            @RequestBody GcUserSaveFolder playlist,
            GcUser user,
            HttpServletRequest request,
            Integer envFlag) {
        Message m = new Message();
        Map<String, Object> params = new HashMap<>();
        List<GcSubject> twoList = new ArrayList<>();
        int masterId = request.getIntHeader("masterId");

        try {
            Integer userId = null;
            if (Objects.nonNull(user)) {
                userId = user.getId();
                params.put("userId", userId);
            }

            PortalUser portalUser = portalUserService.getByUserAndMasterId(userId, masterId);
            playlist = gcUserSaveFolderService.getById(playlist.getId());
            playlist.setPermissions(authorizationService.listPermissions(playlist, portalUser));

            GcUserSaveContent content = new GcUserSaveContent();
            if (Objects.nonNull(user)) {
                content =
                        new GcUserSaveContent(
                                user.getId(), playlist.getMasterId(), playlist.getId());
            } else {
                content =
                        new GcUserSaveContent(null, playlist.getMasterId(), playlist.getId());
            }
            m.addData("gcUserSaveFolder", playlist);
            m.addData("id", playlist.getId());
            m.addData("name", playlist.getName());
            if (envFlag.equals(EnvType.PT.getCode())) {
                GcUserSaveFolder saveFolder = gcUserSaveFolderService.getById(playlist.getId());
                GcUser folderUser = gcUserService.getById(saveFolder.getUserId());
                GcUserInfo gcUserInfo = gcUserInfoService.getById(folderUser.getInfoId());
                gcUserInfo.setUserId(folderUser.getId());
                gcUserInfo.setUsername(folderUser.getUsername());
                if (null != gcUserInfo.getAvatarFileId()) {
                    gcUserInfo.setAvatarFile(sysFileService.getById(gcUserInfo.getAvatarFileId()));
                    gcUserInfo
                            .getAvatarFile()
                            .setFullFileUrl(sysFileService.getResFullUrl(gcUserInfo.getAvatarFile(), request));
                }
                m.addData("userInfo", gcUserInfo);
                m.addData("lastUpdate", saveFolder.getUpdateTime());
            }
            // 获取一级课程
            List<Integer> oneSubIdList = this.gcUserSaveContentService.getOneSubIdList(content);
            if (!oneSubIdList.isEmpty()) {
                params.put("subjectIds", oneSubIdList);
                List<GcSubject> oneList = subjectService.selectBuildSubject(params, request);
                newUiGcSubjectService.buildSubject2(
                        oneList, user.getId(), new SysSystem(), request, EnvType.GC.getCode());
                PageInfo<GcSubject> subjectPageInfo = new PageInfo<>(oneList);
                m.addData("oneLevelList", subjectPageInfo);
            }

            // 获取二级课程
            List<Integer> twoSubIdList = this.gcUserSaveContentService.getTwoSubIdList(content);
            if (!twoSubIdList.isEmpty()) {
                twoList = subjectService.selectTwoSubjectByIds(twoSubIdList, request);
            }
            PageInfo<GcSubject> gcSubjectPageInfo = new PageInfo<>(twoList);
            m.addData("twoLevelList", gcSubjectPageInfo);
            // 获取视频
            List<GcUserSaveContent> list =
                    gcUserSaveContentService.selectContetnByFolderId(content.getFolderId());
            List<Integer> contentIds = this.gcUserSaveContentService.getVideoIdList(content);
            List<GcVideo> videos = gcVideoService.findByVideoIds(contentIds);
            Map<Integer, GcVideo> fileIdToVideo = videos.stream().collect(Collectors.toMap(GcVideo::getFileId, Function.identity()));
            List<SysFile> videoFiles = videos.stream().map(GcVideo::getVideoFile).collect(Collectors.toList());

            for (GcUserSaveContent userSaveContent : list) {
                for (SysFile file : videoFiles) {
                    if (userSaveContent.getFileId().equals(file.getId())) {
                        GcVideo video = fileIdToVideo.get(file.getId());

                        file.setContentId(userSaveContent.getId());
                        file.setVideoId(userSaveContent.getContentId());
                        file.setIsLiked(gcUserVideoActionService.isLikedByUser(userSaveContent.getContentId(), userId) ? 1 : 0);
                        file.setLikeNum(gcUserVideoActionService.countLikeForVideo(userSaveContent.getContentId()));
                        file.setCommentNumber(video.getCommentNum());
                        gcVideoService.updateVideoFilePrivacy(file, video);
                    }
                }
            }
            m.addData("firstVideoId", contentIds.stream().findFirst());

            int followFlag = TableConstant.COMMON_ZERO;
            if (CollectionUtils.isNotEmpty(list) && envFlag.equals(EnvType.PT.getCode())) {
                m.addData("videoNum", list.size());
            }

            if (Objects.nonNull(user)) {
                List<Integer> follows =
                        gcUserSaveContentFollowService.selectFollowPlayList(
                                user.getId(), masterId);
                if (CollectionUtils.isNotEmpty(follows)) {
                    if (follows.contains(playlist.getId())) {
                        followFlag = TableConstant.COMMON_ONE;
                    } else {
                        followFlag = TableConstant.COMMON_ZERO;
                    }
                }
                m.addData("followFlag", followFlag);
            }
            if (CollectionUtils.isNotEmpty(list)) {
                List<GcUserVideoAction> gcVideos = gcUserVideoActionService.countLikeForFiles(contentIds);
                for (SysFile file : videoFiles) {
                    for (GcUserVideoAction gcUserVideoAction : gcVideos) {
                        if (file.getId().equals(gcUserVideoAction.getContentId())) {
                            file.setLikeNum(gcUserVideoAction.getVideoLikeNum());
                        }
                    }
                    file.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(file));
                    file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
                }
            }

            unavailableVideoService.nullifyVideoData(portalUser, videos);
            videos.forEach(video -> {
                Map<String, Boolean> permissions = authorizationService.listPermissions(video, portalUser);
                video.setPermissions(permissions);
                video.getVideoFile().setPermissions(permissions);
            });
            m.addData("videoList", new PageInfo<>(videoFiles));
        } catch (Exception e) {
            e.printStackTrace();
            return new Message().error(e.getMessage());
        }
        return m.ok("查询成功");
    }
}
