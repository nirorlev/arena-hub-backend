package com.threeatom.system.service.impl;

import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PowtoonExternalVideo;
import com.threeatom.guidecore.service.PowtoonExternalVideoService;
import com.threeatom.guidecore.service.impl.PowtoonVideoProviderService;

import com.threeatom.guidecore.entity.PtChannel;
import java.io.*;
import java.net.URL;
import java.security.Security;
import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.threeatom.common.redis.RedisOperator;
import com.threeatom.config.AwsUploadSignUrlConfiguration;
import com.threeatom.guidecore.constant.EventUnifyType;
import com.threeatom.guidecore.util.I18NUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jets3t.service.CloudFrontService;
import org.jets3t.service.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.model.PutObjectResult;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.oss.AliyunOssService;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.entity.SysUser;
import com.threeatom.system.mapper.SysFileMapper;
import com.threeatom.system.service.SysFileService;
import com.threeatom.system.service.SysSystemService;
import com.threeatom.utils.FileUtil;

@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements SysFileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SysFileServiceImpl.class);
    private static final int DISK_SAVE_TYPE = 2;
    private static final int FILE_SAVE_TYPE = 1;

    @Value("${web.profile-path:config/static}")
    private String uploadPath;

    @Autowired
    private Map<String, AliyunOssService> ossServiceMap;

    @Autowired
    private SysSystemService systemService;

    @Resource
    SysFileMapper sysFileMapper;

    @Lazy
    @Autowired
    SysFileService sysFileService;

    @Autowired
    private AwsUploadSignUrlConfiguration awsUploadSignUrlConfiguration;

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private PowtoonExternalVideoService powtoonExternalVideoService;

    @Autowired
    private PowtoonVideoProviderService powtoonVideoProviderService;
    
    public SysFileServiceImpl() {
    }

    public SysFile getInfoById(Integer id) {
    	return this.sysFileMapper.selectById(id);
    }

    private String getVideoPlayerUrlFromExternalVideo(SysFile sysFile) {
        if (sysFile.getFileTypeIndex() != EventUnifyType.powtoonFileTypeIndex) return null;
        PowtoonExternalVideo externalVideo = powtoonExternalVideoService.getBySysFileId(sysFile.getId());
        if (externalVideo == null) return null;

        try {
            JSONObject videoData = powtoonVideoProviderService.getVideoDataFromExternalVideo(externalVideo);
            return videoData.getString("playerUrl");
        } catch (SystemException e) {
            LOGGER.error("Failed to get video player URL from external video", e);
            return null;
        }
    }
    
    private AliyunOssService getCurrentOssService(SysSystem sys) {
       /* AliyunOssService ossService = (AliyunOssService)this.ossServiceMap.get(sys.getBusiness().getKey());
        if (ossService == null) {
            //throw new SystemException(I18NUtil.get("oss.error"));
            return ossService;
        } else {
            return ossService;
        }*/
        AliyunOssService ossService = null;
        return ossService;
    }

    public String saveSysFileToProfile(String folder, String fileName, InputStream fileIs) {
        String filePath = "/upload" + File.separator + folder + File.separator + fileName;
        LOGGER.info(filePath);
        File f = new File(this.uploadPath + filePath);
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        try {
            FileUtils.copyToFile(fileIs, f);
            return filePath;
        } catch (Exception var7) {
            var7.printStackTrace();
            return "";
        }
    }

    public SysFile saveSysImg(SysUser user, MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().trim();
        InputStream is = null;

        try {
            is = file.getInputStream();
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        String url = this.saveSysFileToProfile("images", fileName, is);
        SysFile fileEntity = new SysFile();
        fileEntity.setSysId(user.getSysId());
        fileEntity.setFolder("images");
        fileEntity.setFileUrl(url);
        fileEntity.setUploadUid(user.getId());
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setSaveType(FILE_SAVE_TYPE);
        fileEntity.setFileType(file.getContentType());
        this.save(fileEntity);
        return fileEntity;
    }

    public SysFile saveSysImg(Integer uploaderId, SysSystem sys, String folder, Integer saveType, MultipartFile file) {
        InputStream is = null;

        try {
            is = file.getInputStream();
        } catch (IOException var14) {
            var14.printStackTrace();
        }

        String fileName = UUID.randomUUID().toString() + FileUtil.getExtensionName(file.getOriginalFilename());
        AliyunOssService ossService = this.getCurrentOssService(sys);
        String url = "";
        String md5 = "";
        switch(saveType) {
        case FILE_SAVE_TYPE:
            String fileFullFolder = "images" + File.separator + folder;
            url = this.saveSysFileToProfile(fileFullFolder, fileName, is);
            break;
        case DISK_SAVE_TYPE:
            String objectName = folder + File.separator + fileName;
            PutObjectResult result = this.saveOss(ossService, objectName, is);
            md5 = result.getETag();
            url = objectName;
        }

        SysFile fileEntity = new SysFile();
        fileEntity.setSysId(sys.getId());
        fileEntity.setFolder(folder);
        fileEntity.setFileUrl(url);
        fileEntity.setUploadUid(uploaderId);
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setSaveType(saveType);
        fileEntity.setMd5(md5);
        fileEntity.setFileType(file.getContentType());
        this.save(fileEntity);
        return fileEntity;
    }

    public String getResFullUrl(SysFile sysFile,  HttpServletRequest request) {
        if (sysFile == null || sysFile.getFileUrl()==null ) {
            return null;
        } 
            String fullFileUrl = sysFile.getFileUrl();
            if(request==null) {
                fullFileUrl = getResFullUrlSaveType2( sysFile);
                sysFile.setFullFileUrl(fullFileUrl);
                return fullFileUrl;
            }
            switch(sysFile.getSaveType()) {
            case FILE_SAVE_TYPE://本地保存
                String contextPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                fullFileUrl = contextPath + sysFile.getFileUrl();
                break;
            case DISK_SAVE_TYPE://oss保存
            	//原方法
//            	IOssService ossService = this.getCurrentOssService(sys);
//                String objectName = sysFile.getFileUrl();
//                fullFileUrl = ossService.getObjectUrl(ossService.getCurrentBucketName(), objectName, 3600);
                fullFileUrl = getResFullUrlSaveType2( sysFile);
                break;
            case 3://链接形式，无需处理
                //2022/06/17 为返回s3类型做修改
            	fullFileUrl = sysFile.getFileUrl();
                //2022/10/19 s3拼接路径
                if (!sysFile.getFileUrl().contains("https")||!sysFile.getFileUrl().contains("http")){
                fullFileUrl = getS3Url(sysFile.getFileUrl());
                }
            	break;
            }

            sysFile.setFullFileUrl(fullFileUrl);
            return fullFileUrl;
        
    }

    public String getS3Url(String S3ObjectKey) {
        //1.加载Hash和签名算法类
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        byte[] derPrivateKey = null;
        String signedUrl = null;
        try {
            if (null == redisOperator.get("awsPrivateKey")) {
                String keyName = awsUploadSignUrlConfiguration.getPrivateKeyFilePath();
                ClassPathResource classPathResource = new ClassPathResource("privatekey/" + keyName);
                //解析秘钥
                ClassLoader classLoader = getClass().getClassLoader();
                URL url = classLoader.getResource(classPathResource.getPath());
                if (url == null) {
                    throw new IllegalArgumentException("cannot find url: " + "privatekey/" + keyName);
                }
                InputStream in = new FileInputStream(url.getFile());
                byte[] data = FileUtil.toByteArray(in);
                in.close();
                derPrivateKey = data;
                //保存秘钥
                String byteToString = Base64.getEncoder().encodeToString(derPrivateKey);
                redisOperator.set("awsPrivateKey", byteToString);
            } else {
                derPrivateKey = Base64.getDecoder().decode((String) redisOperator.get("awsPrivateKey"));
            }
            String param_UrlToBeSigned = "https://" + awsUploadSignUrlConfiguration.getDistributionDomain() + "/" + S3ObjectKey;

            Date param_DateLessThan = ServiceUtils.parseIso8601Date("2123-07-15T22:20:00.000Z");

            String policy = CloudFrontService.buildPolicyForSignedUrl(
                    param_UrlToBeSigned,
                    param_DateLessThan,
                    awsUploadSignUrlConfiguration.getLimitToIpAddressCIDR(),
                    null
            );
            signedUrl = CloudFrontService.signUrl(param_UrlToBeSigned, awsUploadSignUrlConfiguration.getKeyPairId(), derPrivateKey, policy);
        }catch (Exception e){
            String msg = e.getMessage();
            throw new SystemException(I18NUtil.get("powtoon.logout.error")+msg);
        }
        return signedUrl;
    }
    
    public String getResFullUrlSaveType2(SysFile sysFile) {
        if (sysFile == null || sysFile.getFileUrl().isEmpty() ) {
            return "";
        } 
        if(sysFile.getSaveType()==TableConstant.sysFile_saveType_link_3) {
        	return sysFile.getFileUrl();
        }
        if(sysFile.getSaveType()!= DISK_SAVE_TYPE) {
        	return "";
        }
        String fullFileUrl = sysFile.getFileUrl();
        AliyunOssService ossService = this.getCurrentOssService(systemService.getSystem());
        String objectName = sysFile.getFileUrl();
        fullFileUrl = ossService.getObjectUrl(ossService.getCurrentBucketName(), objectName, 3600);

        sysFile.setFullFileUrl(fullFileUrl);
        return fullFileUrl;
        
    }

    public SysFile saveVedio(Integer uploaderId, SysSystem sys, String folder, Integer saveType, MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + FileUtil.getExtensionName(file.getOriginalFilename());
        AliyunOssService ossService = this.getCurrentOssService(sys);
        InputStream is = null;

        try {
            is = file.getInputStream();
        } catch (IOException var13) {
            var13.printStackTrace();
        }

        String url = "";
        String md5 = "";
        switch(saveType) {
            case FILE_SAVE_TYPE:
                url = this.saveSysFileToProfile(folder, fileName, is);
                break;
            case DISK_SAVE_TYPE:
                String objectName = folder + File.separator + fileName;
                PutObjectResult result = this.saveOss(ossService, objectName, is);
                md5 = result.getETag();
                url = objectName;
        }

        SysFile fileEntity = new SysFile();
        fileEntity.setSysId(sys.getId());
        fileEntity.setFolder(folder);
        fileEntity.setFileUrl(url);
        fileEntity.setUploadUid(uploaderId);
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setSaveType(saveType);
        fileEntity.setFileType(file.getContentType());
        fileEntity.setMd5(md5);
        this.save(fileEntity);
        return fileEntity;
    }

    private PutObjectResult saveOss(AliyunOssService ossService, String objectName, InputStream is) {
        return ossService.uploadObject(ossService.getCurrentBucketName(), objectName, is);
    }

    public SysFile saveRes(Integer uploaderId, SysSystem sys, String folder, Integer saveType, MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + FileUtil.getExtensionName(file.getOriginalFilename());
        AliyunOssService ossService = this.getCurrentOssService(sys);
        InputStream is = null;

        try {
            is = file.getInputStream();
        } catch (IOException var13) {
            var13.printStackTrace();
        }

        String url = "";
        String md5 = "";
        switch(saveType) {
        case FILE_SAVE_TYPE:
            url = this.saveSysFileToProfile(folder, fileName, is);
            break;
        case DISK_SAVE_TYPE:
            String objectName = folder + File.separator + fileName;
            PutObjectResult result = this.saveOss(ossService, objectName, is);
            md5 = result.getETag();
            url = objectName;
        }

        LOGGER.info(file.getContentType());
        SysFile fileEntity = new SysFile();
        fileEntity.setSysId(sys.getId());
        fileEntity.setFolder(folder);
        fileEntity.setFileUrl(url);
        fileEntity.setUploadUid(uploaderId);
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setSaveType(saveType);
        fileEntity.setMd5(md5);
        fileEntity.setFileType(file.getContentType());
        this.save(fileEntity);
        return fileEntity;
    }

    //原20210304前使用，文件通过后端上传，后改成前端上传，后端只保存记录
    public SysFile saveRes(Integer uploaderId, SysSystem sys, String folder, Integer saveType, String oriFileName, MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + FileUtil.getExtensionName(file.getOriginalFilename());
        AliyunOssService ossService = this.getCurrentOssService(sys);
        InputStream is = null;

        try {
            is = file.getInputStream();
        } catch (IOException var14) {
            var14.printStackTrace();
        }
        String url = "";
        String md5 = "";
        switch(saveType) {
        case FILE_SAVE_TYPE:
            url = this.saveSysFileToProfile(folder, fileName, is);
            break;
        case DISK_SAVE_TYPE:
            String objectName = folder + File.separator + fileName;
            if (objectName.contains("\\")){
                objectName = objectName.replace("\\","/");
            }
            if (file.getOriginalFilename().contains(".vtt")){
                try {
                    byte a[]=new byte[(int)file.getSize()];
                    is.read(a);
                    StringBuilder sb = new StringBuilder(new String(a).replace(",","."));
                    sb.insert(0,"WEBVTT"+"\n\n");
                    is = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            PutObjectResult result = this.saveOss(ossService, objectName, is);
            md5 = result.getETag();
            url = objectName;
        }
        LOGGER.info(file.getContentType());
        SysFile fileEntity = new SysFile();
        fileEntity.setSysId(sys.getId());
        fileEntity.setFolder(folder);
        fileEntity.setFileUrl(url);
        fileEntity.setUploadUid(uploaderId);
        fileEntity.setName(oriFileName);
        fileEntity.setSaveType(saveType);
        fileEntity.setMd5(md5);
        fileEntity.setFileType(file.getContentType());
        if (url.contains(".srt")){
            fileEntity.setFileTypeIndex(EventUnifyType.SRT_12);
        }
        this.save(fileEntity);
        return fileEntity;
    }

    public JSONObject saveFilePolicy(Integer uploaderId, SysSystem sys, String folder, Integer saveType, String originFileName, Map<String, Object> extParams) {
        String fileName = UUID.randomUUID() + FileUtil.getExtensionName(originFileName);
        AliyunOssService ossService = this.getCurrentOssService(sys);
        Map<String, Object> map = new HashMap();
        map.put("uploaderId", uploaderId);
        map.put("sysId", sys.getId());
        map.put("folder", folder);
        map.put("originName", originFileName);
        map.put("saveType", saveType);
        Iterator var10 = extParams.entrySet().iterator();

        while(var10.hasNext()) {
            Entry<String, Object> entry = (Entry)var10.next();
            map.put(entry.getKey(), entry.getValue());
        }

        String objectName = folder + File.separator + fileName;
        return ossService.uploadObjectPolicy(ossService.getCurrentBucketName(), objectName, map);
    }

    // TODO: cleanup and replace usage by the method with GcVideo
    public String getVideoSnapshotUrl(SysFile sysFile) {
        if (null == sysFile ) {
            return null;
        }
        if (null!=sysFile.getThumbNailId()){
            SysFile thumbNail = sysFileService.getById(sysFile.getThumbNailId());
            return getResFullUrl(thumbNail,null);
        }
        if(("video/webm").equals(sysFile.getFileType()) && Objects.nonNull(sysFile.getThumbNailId())){
            SysFile thumbNailFile = sysFileService.getById(sysFile.getThumbNailId());
            return getResFullUrlSaveType2(thumbNailFile);
        }
        String videoSnapshotUrl = "";
        AliyunOssService ossService = this.getCurrentOssService(systemService.getSystem());
        if (null!=sysFile.getThumbNailUrl()){
            videoSnapshotUrl = sysFile.getThumbNailUrl();
            sysFile.setSnapshotUrl(sysFile.getThumbNailUrl());

            if (!sysFile.getThumbNailUrl().contains("https")||!sysFile.getThumbNailUrl().contains("http")){
                videoSnapshotUrl = getS3Url(sysFile.getThumbNailUrl());
                sysFile.setSnapshotUrl(videoSnapshotUrl);
            }
            return videoSnapshotUrl;
        }
        switch(sysFile.getSaveType()) {
        case DISK_SAVE_TYPE:
            if ((null!=sysFile.getFileTypeIndex()&&sysFile.getFileTypeIndex().equals(EventUnifyType.AUDIO_3))||(null!=sysFile.getFolder()&&sysFile.getFolder().equals(TableConstant.sysFile_folder_guidecoreAudio))){
                break;
            }
            String objectName = sysFile.getFileUrl();
            videoSnapshotUrl = ossService.getVideoSnapshot(ossService.getCurrentBucketName(), objectName, 10000);//第3秒为视频预览图
            sysFile.setSnapshotUrl(videoSnapshotUrl);
            break;
        case FILE_SAVE_TYPE:
             break;
        default:
            sysFile.setSnapshotUrl(null);
             break;

        }
        return videoSnapshotUrl;
    }

    @Override
    public String getVideoSnapshotUrl(GcVideo gcVideo) {
        if (gcVideo == null || gcVideo.getVideoFile() == null) {
            return null;
        }

        SysFile videoFile = gcVideo.getVideoFile();

        String videoSnapshotUrl = "";
        AliyunOssService ossService = this.getCurrentOssService(systemService.getSystem());
        if (null!=gcVideo.getThumbnailUrl()){
            videoSnapshotUrl = gcVideo.getThumbnailUrl();
            videoFile.setSnapshotUrl(videoSnapshotUrl);

            if (!gcVideo.getThumbnailUrl().contains("https")||!gcVideo.getThumbnailUrl().contains("http")){
                videoSnapshotUrl = getS3Url(gcVideo.getThumbnailUrl());
                videoFile.setSnapshotUrl(videoSnapshotUrl);
            }
            return videoSnapshotUrl;
        }
        switch(videoFile.getSaveType()) {
            case DISK_SAVE_TYPE:
                if ((null!=videoFile.getFileTypeIndex()&&videoFile.getFileTypeIndex().equals(EventUnifyType.AUDIO_3))||(null!=videoFile.getFolder()&&videoFile.getFolder().equals(TableConstant.sysFile_folder_guidecoreAudio))){
                    break;
                }
                String objectName = videoFile.getFileUrl();
                videoSnapshotUrl = ossService.getVideoSnapshot(ossService.getCurrentBucketName(), objectName, 10000);
                videoFile.setSnapshotUrl(videoSnapshotUrl);
                break;
            case FILE_SAVE_TYPE:
                 break;
            default:
                videoFile.setSnapshotUrl(null);
                 break;
        }
        return videoSnapshotUrl;
    }

    public SysFile saveWxImgUrl(Integer uploaderId, SysSystem sys, String folder, Integer saveType, String url) {
        String fileName = UUID.randomUUID() + ".png";
        String objectName = folder + File.separator + fileName;
        AliyunOssService ossService = this.getCurrentOssService(sys);
        switch(saveType) {
        case DISK_SAVE_TYPE:
            url = ossService.uploadNetObject(ossService.getCurrentBucketName(), objectName, url);
        default:
            SysFile fileEntity = new SysFile();
            fileEntity.setSysId(sys.getId());
            fileEntity.setFolder(folder);
            fileEntity.setFileUrl(url);
            fileEntity.setUploadUid(uploaderId);
            fileEntity.setName(fileName);
            fileEntity.setSaveType(saveType);
            fileEntity.setFileType("image/png");
            this.save(fileEntity);
            return fileEntity;
        }
    }

    public JSONObject saveFilePolicyMD5(Integer uploaderId, SysSystem sys, String folder, Integer saveType, String originFileName, String md5, Map<String, Object> extParams) {
        if (StringUtils.isEmpty(md5)) {
            throw new SystemException("md5不能为null");
        } else {
            String fileName = UUID.randomUUID().toString() + FileUtil.getExtensionName(originFileName);
            AliyunOssService ossService = this.getCurrentOssService(sys);
            Map<String, Object> map = new HashMap();
            map.put("uploaderId", uploaderId);
            map.put("sysId", sys.getId());
            map.put("folder", folder);
            map.put("originName", originFileName);
            map.put("saveType", saveType);
            map.put("md5", md5);
            Iterator var11 = extParams.entrySet().iterator();

            while(var11.hasNext()) {
                Entry<String, Object> entry = (Entry)var11.next();
                map.put(entry.getKey(), entry.getValue());
            }

            String objectName = folder + File.separator + fileName;
            return ossService.uploadObjectPolicy(ossService.getCurrentBucketName(), objectName, map);
        }
    }

    @Override
    public List<SysFile> getFiles(String folder,Integer masterId, List<Integer> typeIndexIds, String tag, Integer pageNum, Integer pageSize,String searchString,Integer fileId,Integer uploadUid) {
        if (pageNum == null) {
            pageNum = 0;
        }

        if (pageSize == null) {
            pageSize = 0;
        }

        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }

        return ((SysFileMapper)this.baseMapper).selectFilesByTypeIndexAndTagSearch(folder,masterId, tag, typeIndexIds,searchString,fileId,uploadUid);
    }

    public List<String> getAllTag(Integer masterId,SysSystem sys, List<String> folders) {
        Map<String, Object> map = ((SysFileMapper)this.baseMapper).selectFileTagJSONStrByFolder(masterId, sys.getId(), folders);
        List<String> result = new ArrayList();
        if (map != null && map.containsKey("tagJSONArray")) {
            JSONArray array = (JSONArray)map.get("tagJSONArray");

            for(int i = 0; i < array.size(); ++i) {
                JSONArray arr = array.getJSONArray(i);

                for(int j = 0; j < arr.size(); ++j) {
                    String tag = arr.getString(j);
                    if (!result.contains(tag)) {
                        result.add(tag);
                    }
                }
            }
        }

        return result;
    }

    public SysFile selectByLogoId(Integer masterId) {
        SysFile sysFile = sysFileMapper.selectByLogoId(masterId);


        return sysFile;
    }

//    public List<SysFile> selectBySubId(Integer subId) {
//        List<SysFile> sysFile = sysFileMapper.selectBySubId(subId);
//        return sysFile;
//    }

    public Integer selectFileTypeIndexByVideoId(Integer videoId) {
        Integer fileTypeIndex = sysFileMapper.selectFileTypeIndexByVideoId(videoId);
        return fileTypeIndex;
    }

    @Override
    public List<SysFile> getHistoryUpload(Integer masterId, Integer userId, String folder) {
        return sysFileMapper.getHistoryUpload(masterId,userId,folder);
    }

    @Override
    public List<SysFile> selectBatch(List<Integer> fileIds) {
        return sysFileMapper.selectBatchByFileIds(fileIds);
    }

    @Override
    public void deleteFile(SysSystem sys, SysFile file) {
        AliyunOssService ossService = this.getCurrentOssService(sys);
        ossService.deleteObject(ossService.getCurrentBucketName(),file.getFileUrl());
    }

    @Override
    public Map<Integer,SysFile> getFilesUploadByFileIds(List<Integer> fileIds) {
        return sysFileMapper.getFilesUploadByFileIds(fileIds);
    }

    @Override
    public String getVideoPlayerUrl(SysFile sysFile, HttpServletRequest request) {
        String playerUrl = getVideoPlayerUrlFromExternalVideo(sysFile);
        if (playerUrl != null) return playerUrl;
        return getResFullUrl(sysFile, request);
    }

    public void updateImageUrls(PtChannel channel, HttpServletRequest request) {
        if(Objects.nonNull(channel.getCreateUser())) {
            SysFile sysFile = getById(channel.getCreateUser().getAvatarFileId());
            String imgFullFileUrl = getResFullUrl(sysFile, request);
            channel.getCreateUser().setAvatarFullFileUrl(imgFullFileUrl);
        }

        if(Objects.nonNull(channel.getChannelImgFileId())) {
            SysFile sysFile = getById(channel.getChannelImgFileId());
            String imgFullFileUrl = getResFullUrl(sysFile, request);
            channel.setImgFullFileUrl(imgFullFileUrl);
        }

        if(Objects.nonNull(channel.getChannelAvatarFileId())) {
            SysFile avatarFile = getById(channel.getChannelAvatarFileId());
            String avatarFullFileUrl = getResFullUrl(avatarFile, request);
            avatarFile.setFullFileUrl(avatarFullFileUrl);
            channel.setAvatarFile(avatarFile);
        }
    }

    @Override
    public void updateImageUrls(GcSubject course, HttpServletRequest request) {
        SysFile courseImage = course.getSubImgFile();

        getResFullUrl(courseImage, request);
        getVideoSnapshotUrl(courseImage);
    }
}
