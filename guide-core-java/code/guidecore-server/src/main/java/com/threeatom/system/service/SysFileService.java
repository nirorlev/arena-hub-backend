package com.threeatom.system.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PowtoonExternalVideo;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.entity.SysUser;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface SysFileService extends IService<SysFile> {
    String saveSysFileToProfile(String folder, String fileName, InputStream fileIs);

    SysFile saveSysImg(SysUser user, MultipartFile file);

    SysFile saveSysImg(
            Integer uploaderId, SysSystem sys, String folder, Integer saveType, MultipartFile file);

    String getResFullUrl(SysFile sysFile, HttpServletRequest request);

    SysFile saveVedio(
            Integer uploaderId, SysSystem sys, String folder, Integer saveType, MultipartFile file);

    SysFile saveWxImgUrl(
            Integer upInteger, SysSystem sys, String folder, Integer saveType, String url);

    SysFile saveRes(
            Integer uploaderId, SysSystem sys, String folder, Integer saveType, MultipartFile file);

    SysFile saveRes(
            Integer uploaderId,
            SysSystem sys,
            String folder,
            Integer saveType,
            String oriFileName,
            MultipartFile file);

    JSONObject saveFilePolicy(
            Integer uploaderId,
            SysSystem sys,
            String folder,
            Integer saveType,
            String originFileName,
            Map<String, Object> extParams);

    JSONObject saveFilePolicyMD5(
            Integer uploaderId,
            SysSystem sys,
            String folder,
            Integer saveType,
            String originFileName,
            String md5,
            Map<String, Object> extParams);

    String getVideoSnapshotUrl(SysFile sysFile);

    String getVideoSnapshotUrl(GcVideo gcVideo);

    List<SysFile> getFiles(
            String folder,
            Integer masterId,
            List<Integer> typeIndexIds,
            String tag,
            Integer pageNum,
            Integer pageSize,
            String searchString,
            Integer fileId,
            Integer uploadUid);

    List<String> getAllTag(Integer masterId, SysSystem sys, List<String> folders);

    String getFullFileUrl(String fileUrl);

    String getResFullUrlSaveType2(SysFile introVideoFile);

    SysFile selectByLogoId(Integer id);

    Integer selectFileTypeIndexByVideoId(Integer videoId);

    SysFile getInfoById(Integer id);

    List<SysFile> getHistoryUpload(Integer masterId, Integer userId, String folder);

    List<SysFile> selectBatch(List<Integer> fileIds);

    void deleteFile(SysSystem sys, SysFile file);

    Map<Integer, SysFile> getFilesUploadByFileIds(List<Integer> fileIds);

    String getVideoPlayerUrl(SysFile sysFile, HttpServletRequest request);

    void updateImageUrls(PtChannel channel, HttpServletRequest request);

    void updateImageUrls(GcSubject channel, HttpServletRequest request);

    void updateVideoInformation(SysFile sysFile, PortalUser portalUser);

    void updateVideoInformation(SysFile sysFile, PowtoonExternalVideo powtoonExternalVideo);

    void uploadThumbnailToS3(SysFile sysFile, PortalUser portalUser);

    SysFile getVideoFile(GcVideo video, HttpServletRequest request);

    SysFile createUserAvatarFile(Integer uploadUserId, String thumbUrl, Integer masterId);
}
