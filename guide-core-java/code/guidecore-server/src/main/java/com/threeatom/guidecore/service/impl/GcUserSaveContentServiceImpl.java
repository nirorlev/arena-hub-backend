package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcUserSaveContent;
import com.threeatom.guidecore.mapper.GcUserSaveContentMapper;
import com.threeatom.guidecore.service.GcUserSaveContentService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class GcUserSaveContentServiceImpl
        extends ServiceImpl<GcUserSaveContentMapper, GcUserSaveContent>
        implements GcUserSaveContentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserSaveContentServiceImpl.class);

    @Override
    public Integer countSaveContent(GcUserSaveContent gcUserSaveContent) {
        // TODO Auto-generated method stub
        QueryWrapper<GcUserSaveContent> queryWrapper = new QueryWrapper<GcUserSaveContent>();
        queryWrapper.eq("user_id", gcUserSaveContent.getUserId());

        if (gcUserSaveContent.getMasterId() != null) {
            queryWrapper.eq("master_id", gcUserSaveContent.getMasterId());
        }
        if (gcUserSaveContent.getId() != null) {
            queryWrapper.eq("id", gcUserSaveContent.getId());
        }

        if (gcUserSaveContent.getVideoId() != null) {
            queryWrapper.eq("video_id", gcUserSaveContent.getVideoId());
        }
        if (gcUserSaveContent.getSubId() != null) {
            queryWrapper.eq("sub_id", gcUserSaveContent.getSubId());
        }
        return this.count(queryWrapper);
    }

    @Override
    public List<Integer> getTwoSubIdList(GcUserSaveContent userSaveContent) {
        return this.baseMapper.getTwoSubIdList(userSaveContent);
    }

    @Override
    public List<Integer> getOneSubIdList(GcUserSaveContent userSaveContent) {
        return this.baseMapper.getOneSubIdList(userSaveContent);
    }

    @Override
    public List<Integer> getVideoIdList(GcUserSaveContent userSaveContent) {
        return this.baseMapper.getVideoIdList(userSaveContent);
    }

    @Override
    public List<Integer> deleteList(Integer videoId, List<Integer> folderIds) {
        return this.baseMapper.deleteList(videoId, folderIds);
    }

    @Override
    public List<Integer> deleteListByFolderId(Integer fileId, List<Integer> folderId) {
        return this.baseMapper.deleteFileList(fileId, folderId);
    }

    @Override
    public List<Integer> selectFolderIdByVideoId(Integer videoId, Integer masterId) {
        return this.baseMapper.selectFolderIdByVideoId(videoId, masterId);
    }

    @Override
    public List<Integer> selectFolderIdByFileId(Integer fileId, Integer masterId) {
        return this.baseMapper.selectFolderIdByFileId(fileId, masterId);
    }

    @Override
    public List<GcUserSaveContent> selectContetnByFolderId(Integer folderId) {
        QueryWrapper<GcUserSaveContent> queryWrapper = new QueryWrapper<GcUserSaveContent>();
        queryWrapper.eq("folder_id", folderId);
        return this.list(queryWrapper);
    }
}
