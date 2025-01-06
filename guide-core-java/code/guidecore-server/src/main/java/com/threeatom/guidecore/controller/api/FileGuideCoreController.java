package com.threeatom.guidecore.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.constant.ObjectStorageConstants;
import com.threeatom.guidecore.constant.EventUnifyType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.constant.UploadType;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.entity.GcManager;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.service.AwsS3StorageService;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import io.swagger.annotations.Api;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/guidecore/file")
@Api(tags = "文件上传")
public class FileGuideCoreController extends GuideCoreController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileGuideCoreController.class);

    @Autowired private SysFileService fileService;

    @Autowired private AwsS3StorageService awsS3StorageService;

    @PostMapping("/uploadImg")
    public Message uploadImg(MultipartFile img, HttpServletRequest request) {
        GcManager manager = this.getManager();
        SysSystem sys = this.getSystem();
        SysFile file =
                fileService.saveSysImg(
                        manager.getId(),
                        sys,
                        TableConstant.sysFile_folder_guidecoreImages,
                        ObjectStorageConstants.ALIYUN_OSS,
                        img);

        fileService.getResFullUrl(file, request);

        return new Message().ok().addData("fileImg", file);
    }

    @PostMapping("/uploadEventImg")
    public Message uploadEventImg(MultipartFile img, HttpServletRequest request) {
        GcManager manager = this.getManager();
        SysSystem sys = this.getSystem();
        SysFile file =
                fileService.saveSysImg(
                        manager.getId(), sys, "guidecore/event/images", ObjectStorageConstants.ALIYUN_OSS, img);

        fileService.getResFullUrl(file, request);

        return new Message().ok().addData("fileImg", file);
    }

    @PostMapping("/uploadVedio")
    public Message uploadVedio(MultipartFile vedio, HttpServletRequest request) {
        GcManager manager = this.getManager();
        SysSystem sys = this.getSystem();
        SysFile file =
                fileService.saveVedio(
                        manager.getId(),
                        sys,
                        TableConstant.sysFile_folder_guidecoreVedio,
                        ObjectStorageConstants.ALIYUN_OSS,
                        vedio);

        fileService.getResFullUrl(file, request);

        return new Message().ok().addData("fileVedio", file);
    }

    @PostMapping("/uploadRes")
    public Message uploadRes(MultipartFile res, HttpServletRequest request) {
        GcManager manager = this.getManager();
        SysSystem sys = this.getSystem();

        SysFile file =
                fileService.saveRes(
                        manager.getId(),
                        sys,
                        TableConstant.sysFile_folder_guidecoreRes,
                        ObjectStorageConstants.ALIYUN_OSS,
                        res);
        fileService.getResFullUrl(file, request);
        return new Message().ok().addData("fileRes", file);
    }

    @GetMapping("/getFile/{id}")
    public Message getFile(@PathVariable("id") Integer id, HttpServletRequest request) {
        if (id == null || id.equals(0)) throw new SystemException(I18NUtil.get("id.illegal"));
        SysSystem sys = this.getSystem();
        SysFile file = fileService.getById(id);
        fileService.getResFullUrl(file, request);
        return new Message().ok().addData("file", file);
    }

    // 课程上传
    @PostMapping("/uploadPolicyVideo")
    public Message uploadPolicyVideo(@RequestBody JSONObject jsonObject) {
        GcManager manager = this.getManager();
        SysSystem sys = this.getSystem();

        String originFileName = jsonObject.getString("originFileName");
        String videoName = jsonObject.getString("videoName");

        Integer subId = jsonObject.getInteger("subId");
        Integer videoSource = jsonObject.getInteger("videoSource");
        Integer id = jsonObject.getInteger("id");

        Map<String, Object> extParams = new HashMap<>();
        extParams.put("videoName", videoName);
        if (StringUtils.isBlank(jsonObject.getString("videoDesc")) != true) {
            extParams.put("videoDesc", jsonObject.getString("videoDesc"));
        }

        if (jsonObject.getInteger("videoLong") != null) {
            extParams.put("videoLong", jsonObject.getString("videoLong"));
        }

        extParams.put("subId", subId);
        extParams.put("videoSource", videoSource);
        extParams.put("id", id);
        JSONObject policyStr =
                fileService.saveFilePolicy(
                        manager.getId(),
                        sys,
                        TableConstant.sysFile_folder_guidecoreVedio,
                        ObjectStorageConstants.ALIYUN_OSS,
                        originFileName,
                        extParams);

        return new Message().ok().addData("policy", policyStr);
    }

    // 门户视频上传
    @PostMapping("/uploadPolicyMasterVideo")
    public Message uploadPolicyMasterVideo(@RequestBody JSONObject jsonObject) {
        GcManager manager = this.getManager();
        SysSystem sys = this.getSystem();

        String originFileName = jsonObject.getString("originFileName");
        GcMaster master = this.getMaster();

        Map<String, Object> extParams = new HashMap<>();
        extParams.put("masterId", master.getId());
        extParams.put("uploadType", UploadType.portalFrontPageVideo_2);

        JSONObject policyStr =
                fileService.saveFilePolicy(
                        manager.getId(),
                        sys,
                        TableConstant.sysFile_folder_guidecoreVedio,
                        ObjectStorageConstants.ALIYUN_OSS,
                        originFileName,
                        extParams);

        return new Message().ok().addData("policy", policyStr);
    }

    // 批量上传
    // 用户端上传
    @PostMapping("/uploadPolicyFile")
    public Message uploadPolicyFile(@RequestBody JSONObject jsonParams, HttpServletRequest request) {
        Integer uploaderId = null;
        //		GcManager manager=this.getManager();
        SysSystem sys = this.getSystem();
        Integer masterId = null;
        GcMaster master = this.getMaster();

        if (master == null) {
            masterId = getHeaderMasterId(request);
            if (masterId == null) throw new SystemException(I18NUtil.get("student.site.error"));
        } else {
            masterId = master.getId();
        }

        String originFileName = jsonParams.getString("originFileName");
        String tag = jsonParams.getString("tag");
        String type = jsonParams.getString("type");

        Map<String, Object> extParams = new HashMap<>();

        String folder = "";
        if (type == null) { // 学生端
            folder = jsonParams.getString("folder");
            extParams.put("fileTypeIndex", jsonParams.getString("fileTypeIndex"));
            uploaderId = this.getGcUser().getId();
        } else if (type.equals("video")) {
            folder = TableConstant.sysFile_folder_guidecoreVedio;
            extParams.put("fileTypeIndex", EventUnifyType.VIDEO_2);
            uploaderId = this.getManager().getId();
        } else if (type.equals("res")) {
            folder = TableConstant.sysFile_folder_guidecoreRes;
            extParams.put("fileTypeIndex", EventUnifyType.RES_FILE);
            uploaderId = this.getManager().getId();
        } else if (type.equals("audio")) {
            folder = TableConstant.sysFile_folder_guidecoreAudio;
            extParams.put("fileTypeIndex", EventUnifyType.AUDIO_3);
            uploaderId = this.getManager().getId();
        } else if (type.equals("document")) {
            folder = TableConstant.sysFile_folder_guidecoreDocument;
            extParams.put("fileTypeIndex", EventUnifyType.DOC_4);
            uploaderId = this.getManager().getId();
        } else if (type.equals("photo")) {
            folder = TableConstant.sysFile_folder_guidecoreImages;
            extParams.put("fileTypeIndex", EventUnifyType.IMAGE_1);
            // 门户端用户端新增给webm视频添加缩略图
            if (Objects.nonNull(this.getManager())) {
                uploaderId = this.getManager().getId();
            } else {
                uploaderId = this.getGcUser().getId();
            }
        }

        if (StringUtils.isEmpty(folder)) throw new SystemException(I18NUtil.get("resource.type.error"));

        if (jsonParams.getInteger("videoLong") != null) {
            extParams.put("videoLong", jsonParams.getInteger("videoLong"));
        }

        if (jsonParams.getString("ifFragment") != null) {
            extParams.put("ifFragment", jsonParams.getString("ifFragment"));
        }

        extParams.put("uploadType", UploadType.portalBulk_1); // 回调时判断用
        if (StringUtils.isNotBlank(tag)) {
            extParams.put("tag", tag);
        }

        extParams.put("masterId", masterId);
        extParams.put("thumbNailId", jsonParams.get("thumbNailId"));

        JSONObject policyStr =
                fileService.saveFilePolicy(
                        uploaderId, sys, folder, ObjectStorageConstants.ALIYUN_OSS, originFileName, extParams);

        return new Message().ok().addData("policy", policyStr);
    }

    // 需优化
    @PostMapping("/uploadPolicyFileFromUser")
    public Message uploadPolicyFileFromUser(
            @RequestBody JSONObject jsonParams, HttpServletRequest request) {
        GcUser user = this.getGcUser();
        SysSystem sys = this.getSystem();
        Integer masterId = Integer.parseInt(request.getHeader("masterId"));

        String originFileName = jsonParams.getString("originFileName");
        String type = jsonParams.getString("type");

        Map<String, Object> extParams = new HashMap<>();
        String folder = "";
        folder = TableConstant.sysFile_folder_guidecoreUserEvent;
        extParams.put("fileTypeIndex", type);

        if (StringUtils.isEmpty(folder)) throw new SystemException(I18NUtil.get("resource.type.error"));

        extParams.put("uploadType", UploadType.portalBulk_1); // 回调时判断用

        extParams.put("masterId", masterId);
        extParams.put("userRole", TableConstant.sysFile_userRole_user2);

        JSONObject policyStr =
                fileService.saveFilePolicy(
                        user.getId(),
                        sys,
                        folder,
                        ObjectStorageConstants.ALIYUN_OSS,
                        originFileName,
                        extParams);

        return new Message().ok().addData("policy", policyStr);
    }

    @SneakyThrows
    @PostMapping("/awsUploadSignUrl")
    public Message awsUploadSignUrl(@RequestBody JSONObject jsonParams) {
        String key = jsonParams.getString("S3ObjectKey");
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        String signedUrl = awsS3StorageService.generateSignedUrl(key);
        return new Message().ok().addData("signedUrl", signedUrl);
    }
}
