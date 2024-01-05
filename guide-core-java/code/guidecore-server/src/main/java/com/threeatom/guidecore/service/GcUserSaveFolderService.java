package com.threeatom.guidecore.service;


import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;


public interface GcUserSaveFolderService  extends IService<GcUserSaveFolder> {

	List<GcUserSaveFolder> getPtHomePlayList(Integer userId, Integer masterId, List<Integer> folderIdList, HttpServletRequest request);

	List<GcUserSaveFolder> getPtNewHomePlayList(Integer userId, Integer masterId, List<Integer> folderIdList, HttpServletRequest request);
	List<GcUserSaveFolder> selectFolderForUserMaster(Integer userId, Integer masterId, List<Integer> folderIdList, HttpServletRequest request,List<Integer> myFolderIdList);

	List<GcUserSaveFolder> selectFolderAllVideo(Integer userId, Integer masterId, List<Integer> folderIdList, HttpServletRequest request,List<Integer> myFolderIdList);

	Integer countFolder(GcUserSaveFolder gcUserSaveFolder);

	List<GcUserSaveFolder> selectFolderInMaster(Integer masterId);

	Integer selectFolderByIdsAndUser(@Param("folderIds") List<Integer> folderIds,@Param("userId") Integer userId);

	GcUserSaveFolder getPlayListMetaConfig(Integer folderId,Integer fileId);
}