package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserSaveContent;
import java.util.List;

public interface GcUserSaveContentService extends IService<GcUserSaveContent> {

    Integer countSaveContent(GcUserSaveContent gcUserSaveContent);

    List<Integer> getTwoSubIdList(GcUserSaveContent userSaveContent);

    List<Integer> getOneSubIdList(GcUserSaveContent userSaveContent);

    List<Integer> getVideoIdList(GcUserSaveContent userSaveContent);

    List<Integer> deleteList(Integer videoId, List<Integer> folderIds);

    List<Integer> deleteListByFolderId(Integer fileId, List<Integer> folderId);

    List<Integer> selectFolderIdByVideoId(Integer videoId, Integer masterId);

    List<Integer> selectFolderIdByFileId(Integer fileId, Integer masterId);

    List<GcUserSaveContent> selectContetnByFolderId(Integer folderId);
}
