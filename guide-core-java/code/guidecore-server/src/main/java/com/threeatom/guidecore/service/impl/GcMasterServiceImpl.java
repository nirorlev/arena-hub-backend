package com.threeatom.guidecore.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.controller.Message;
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
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 主站点实例 服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-11
 */
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

    private static final String CACHE_TAG = "GcMaster";

    private static final String KEY_TAG_ENTITY = "'entity:uid-'+";

    @Override
    @Cacheable(value = CACHE_TAG, key = KEY_TAG_ENTITY + "#p0")
    public GcMaster getMasterByUidCache(Integer uid) {
        // TODO Auto-generated method stub

        return this.baseMapper.selectMasterByUid(uid);
    }

    @Override
    @CacheEvict(value = CACHE_TAG, key = KEY_TAG_ENTITY + "#p0")
    public boolean setMasterState(Integer uid, Integer value) {
        // TODO Auto-generated method stub
        UpdateWrapper<GcMaster> updateWrap = new UpdateWrapper<GcMaster>();
        updateWrap.set("state", value).eq("manager_id", uid);
        return this.update(updateWrap);
    }

    @Override
    @CacheEvict(value = CACHE_TAG, key = KEY_TAG_ENTITY + "#p0")
    public boolean superAdminSetMasterState(Integer masterId, Integer value) {
        // TODO Auto-generated method stub
        UpdateWrapper<GcMaster> updateWrap = new UpdateWrapper<GcMaster>();
        updateWrap.set("state", value).eq("id", masterId);
        return this.update(updateWrap);
    }

    @Override
    @CacheEvict(value = CACHE_TAG, key = KEY_TAG_ENTITY + "#master.managerId")
    public boolean setMaster(GcMaster master) {
        // TODO Auto-generated method stub
        return this.updateById(master);
    }

    @Override
    public boolean updateSourceNull(Integer id) {
        // TODO Auto-generated method stub
        return this.baseMapper.updateSourceNull(id);
    }

    @Override
    public GcMaster getMasterByContext(String context) {
        // TODO Auto-generated method stub
        return this.baseMapper.selectMasterByContext(context);
    }

    @Override
    public GcMaster getMasterById(Integer id) {
        // TODO Auto-generated method stub

        return this.baseMapper.selectMasterById(id);
    }

    @Override
    public Object getMasterConfig(Integer id, String key) {
        // TODO Auto-generated method stub
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
            @RequestBody GcUserSaveFolder gcUserSaveFolder,
            GcUser user,
            HttpServletRequest request,
            Integer envFlag) {
        Message m = new Message();
        Map<String, Object> params = new HashMap<>();
        List<GcSubject> twoList = new ArrayList<>();
        List<SysFile> fileList = new ArrayList<>();

        try {
            Integer userId = null;
            if (Objects.nonNull(user)) {
                userId = user.getId();
                params.put("userId", userId);
            }
            gcUserSaveFolder = gcUserSaveFolderService.getById(gcUserSaveFolder.getId());
            GcUserSaveContent content = new GcUserSaveContent();
            if (Objects.nonNull(user)) {
                content =
                        new GcUserSaveContent(
                                user.getId(), gcUserSaveFolder.getMasterId(), gcUserSaveFolder.getId());
            } else {
                content =
                        new GcUserSaveContent(null, gcUserSaveFolder.getMasterId(), gcUserSaveFolder.getId());
            }
            m.addData("gcUserSaveFolder", gcUserSaveFolder);
            m.addData("id", gcUserSaveFolder.getId());
            m.addData("name", gcUserSaveFolder.getName());
            if (envFlag.equals(EnvType.PT.getCode())) {
                GcUserSaveFolder saveFolder = gcUserSaveFolderService.getById(gcUserSaveFolder.getId());
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
            if (CollectionUtils.isNotEmpty(contentIds)) {
                List<Integer> fileIds = gcVideoService.listByIds(contentIds).stream().map(GcVideo::getFileId).toList();
                fileList = sysFileService.listByIds(fileIds);

                for (GcUserSaveContent userSaveContent : list) {
                    for (SysFile file : fileList) {
                        if (userSaveContent.getFileId().equals(file.getId())) {
                            file.setContentId(userSaveContent.getId());
                            file.setVideoId(userSaveContent.getContentId());
                            file.setIsLiked(gcUserVideoActionService.isLikedByUser(userSaveContent.getContentId(), userId) ? 1 : 0);
                        }
                    }
                }
                m.addData("firstVideoId", contentIds.stream().findFirst());
            }
            int followFlag = TableConstant.COMMON_ZERO;
            if (CollectionUtils.isNotEmpty(list) && envFlag.equals(EnvType.PT.getCode())) {
                m.addData("videoNum", list.size());
            }
            if (Objects.nonNull(user)) {
                List<Integer> follows =
                        gcUserSaveContentFollowService.selectFollowPlayList(
                                user.getId(), request.getIntHeader("masterId"));
                if (CollectionUtils.isNotEmpty(follows)) {
                    if (follows.contains(gcUserSaveFolder.getId())) {
                        followFlag = TableConstant.COMMON_ONE;
                    } else {
                        followFlag = TableConstant.COMMON_ZERO;
                    }
                }
                m.addData("followFlag", followFlag);
            }
            if (CollectionUtils.isNotEmpty(list)) {
                List<GcUserVideoAction> gcVideos = gcUserVideoActionService.countLikeForFiles(contentIds);
                for (SysFile file : fileList) {
                    for (GcUserVideoAction gcUserVideoAction : gcVideos) {
                        if (file.getId().equals(gcUserVideoAction.getContentId())) {
                            file.setLikeNum(gcUserVideoAction.getVideoLikeNum());
                        }
                    }
                    file.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(file));
                    file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
                }
            }
            PageInfo<SysFile> pageInfo = new PageInfo<>(fileList);
            m.addData("videoList", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message().error(e.getMessage());
        }
        return m.ok("查询成功");
    }
}
