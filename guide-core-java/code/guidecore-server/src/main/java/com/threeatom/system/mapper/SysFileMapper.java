//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.system.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.system.entity.SysFile;

public interface SysFileMapper extends BaseMapper<SysFile> {
//    List<SysFile> selectFilesByFolderAndTagSearch(@Param("masterId")Integer masterId, @Param("sysId") Integer sysId, @Param("folder") String folder, @Param("searchKey") String searchKey);
    
    List<SysFile> selectFilesByTypeIndexAndTagSearch(@Param("folder")String folder,@Param("masterId")Integer masterId, @Param("searchKey") String searchKey, @Param("typeIndexIds") List<Integer> typeIndexIds,@Param("searchString")String searchString,@Param("fileId")Integer fileId,@Param("uploadUid")Integer uploadUid);

    Map<String, Object> selectFileTagJSONStrByFolder(@Param("masterId")Integer masterId, @Param("sysId") Integer sysId, @Param("folders") List<String> folders);
    
    Integer selectCountNotBelongMaster(@Param("masterId")Integer masterId, @Param("ids") Set<Integer> ids);

    SysFile selectByLogoId(@Param("masterId")Integer masterId);
//
//    List<SysFile> selectBySubId(@Param("subId")Integer subId);

    Integer selectFileTypeIndexByVideoId(@Param("videoId")Integer videoId);

    List<SysFile> getHistoryUpload(@Param("masterId") Integer masterId,@Param("userId") Integer userId,@Param("folder") String folder);

    List<SysFile> getSysFileByIdsOrVideos(@Param("fileList") List<Integer> fileList,@Param("videoList") List<Integer> videoList);

    SysFile selectById(@Param("id")Integer id);

    List<SysFile> selectBatchByFileIds(@Param("fileIdList")List<Integer> fileIdList);

    @MapKey("id")
    Map<Integer,SysFile> getFilesUploadByFileIds(@Param("fileIdList")List<Integer> fileIdList);
}
