package com.threeatom.system.controller;

import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.EventUnifyType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.AwsS3StorageService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.service.PowtoonExternalVideoService;
import com.threeatom.guidecore.service.PtTagsService;
import com.threeatom.guidecore.service.VideoThumbnailProvider;
import com.threeatom.guidecore.util.RequestUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guidecore/sysFile")
public class SysFIleController extends GuideCoreController {

    @Autowired private SysFileService sysFileService;

    @Autowired private PtTagsService tagsService;

    @Autowired private PowtoonExternalVideoService powtoonExternalVideoService;

    @Autowired private VideoThumbnailProvider thumbnailProvider;

    @Autowired private PortalUserService portalUserService;

    @PostMapping("/saveLink")
    public Message saveLink(@RequestBody SysFile sysFile, HttpServletRequest request) {
        if (sysFile.getId() == null) {
            ApiAssert.ifStringInList(
                    sysFile.getFileType(), TableConstant.sysFile_fileType_list, "The fileType field is incorrect. Please confirm with the backend staff");
        }
        GcMaster master = this.getMaster();
        GcUser user = this.getGcUser();
        Optional<Integer> masterId = RequestUtil.getMasterId(request);

        if (master == null && masterId.isPresent()) {
            master = new GcMaster();
            master.setId(masterId.get());
        }

        Integer userRole = sysFile.getUserRole();
        if (TableConstant.sysFile_userRole_portal1 == userRole) {
            if (null != this.getManager()) {
                sysFile.setUploadUid(this.getManager().getId());
            }
            sysFile.setMasterId(master.getId());
        } else if (TableConstant.sysFile_userRole_user2 == userRole) {
            sysFile.setUploadUid(this.getGcUser().getId());
            assert master != null;
            sysFile.setMasterId(master.getId());
        } else {
            throw new SystemException(String.format("UserRole '%s' does not exist", userRole));
        }

        SysSystem system = this.getSystem();
        sysFile.setSaveType(TableConstant.sysFile_saveType_link_3);
        sysFile.setSysId(system.getId());

        Integer fileTypeIndex = sysFile.getFileTypeIndex();
        if (fileTypeIndex != null && EventUnifyType.powtoonVideoFileTypes.contains(fileTypeIndex)) {
            sysFileService.uploadThumbnailToS3(sysFile, user.getId(), master.getId());
        }

        if (!sysFileService.saveOrUpdate(sysFile)) {
            return new Message().error("Save failed");
        }

        if (fileTypeIndex != null && EventUnifyType.powtoonVideoFileTypes.contains(fileTypeIndex)) {
            powtoonExternalVideoService.createExternalVideoForSysFile(sysFile);
        }
        sysFileService.getVideoSnapshotUrl(sysFile);
        updateFileUrls(sysFile, request);

        tagsService.updateTags(master.getId(), sysFile.getId(), sysFile.getCourseTags());

        return new Message().ok().addData("file", sysFile);
    }

    private void updateFileUrls(SysFile sysFile, HttpServletRequest request) {
        sysFile.setThumbNailUrl(thumbnailProvider.getThumbnailUrl(sysFile));
        sysFile.setFullFileUrl(sysFileService.getResFullUrl(sysFile, request));
        sysFile.setFileUrl(sysFileService.getResFullUrl(sysFile, request));
    }

    @PostMapping("/saveBatichLink")
    public Message saveBatichLink(@RequestBody List<SysFile> sysFile1, HttpServletRequest request) {
        for (SysFile sysFile : sysFile1) {
            ApiAssert.notNull(sysFile1);
            ApiAssert.ifStringInList(
                    sysFile.getFileType(), TableConstant.sysFile_fileType_list, "fileType字段错误，请于后端人员确认");
            SysSystem sys = this.getSystem();
            sysFile.setSysId(sys.getId());
            String value = sysFile.getFileUrl().substring(sysFile.getFileUrl().length() - 11);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("https://media.screenrock.com/");
            for (int i = 0; i < value.length(); i++) {
                stringBuffer.append(value.charAt(i));
                stringBuffer.append("/");
            }
            stringBuffer.append("file.mp4");
            sysFile.setSaveType(TableConstant.sysFile_saveType_link_3);
            sysFile.setFileUrl(stringBuffer.toString());
            sysFile.setFullFileUrl(sysFileService.getResFullUrl(sysFile, null));
            sysFile.setFileUrl(sysFileService.getResFullUrl(sysFile, null));
        }
        if (sysFileService.saveBatch(sysFile1)) {
            return new Message().ok().addData("file", sysFile1);
        }
        return new Message().error("保存失败");
    }

    @ApiOperation(value = "保存视频时长到文件库", httpMethod = "POST")
    @PostMapping("/saveVdeoLong")
    public Message saveVdeoLong(@RequestBody SysFile sysFile) {
        if (sysFile.getVideoLong() == null) throw new SystemException("视频长度不可空");
        SysFile file = sysFileService.getById(sysFile);
        if (file == null) throw new SystemException("视频id不存在");
        file.setVideoLong(sysFile.getVideoLong());

        if (sysFileService.updateById(file)) {
            return new Message().ok("保存成功").addData("file", file);
        }
        return new Message().error("保存失败");
    }
}
