package com.threeatom.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.EventUnifyType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PtTags;
import com.threeatom.guidecore.service.AwsS3StorageService;
import com.threeatom.guidecore.service.PowtoonExternalVideoService;
import com.threeatom.guidecore.service.PtTagsService;
import com.threeatom.guidecore.service.VideoThumbnailProvider;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guidecore/sysFile")
public class SysFIleController extends GuideCoreController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysFIleController.class);
    @Autowired private SysFileService sysFileService;

    @Autowired private PtTagsService tagsService;

    @Autowired private PowtoonExternalVideoService powtoonExternalVideoService;

    @Autowired private AwsS3StorageService awsS3StorageService;

    @Autowired private VideoThumbnailProvider thumbnailProvider;

    @ApiOperation(value = "保存链接到sys_file文件库", httpMethod = "POST")
    @PostMapping("/saveLink")
    public Message saveLink(@RequestBody SysFile sysFile, HttpServletRequest request) {
        if (null == sysFile.getId()) {
            ApiAssert.ifStringInList(
                    sysFile.getFileType(), TableConstant.sysFile_fileType_list, "fileType字段错误，请于后端人员确认");
        }
        GcMaster master = this.getMaster();
        GcUser user = this.getGcUser();
        if (null == master && null != request.getHeader("masterId")) {
            master = new GcMaster();
            master.setId(Integer.parseInt(request.getHeader("masterId")));
        }
        if (TableConstant.sysFile_userRole_portal1 == sysFile.getUserRole().intValue()) {
            if (null != this.getManager()) {
                sysFile.setUploadUid(this.getManager().getId());
            }
            sysFile.setMasterId(master.getId());
        } else if (TableConstant.sysFile_userRole_user2 == sysFile.getUserRole().intValue()) {
            sysFile.setUploadUid(this.getGcUser().getId());
            sysFile.setMasterId(master.getId());
        } else {
            throw new SystemException("userRole不存在，请查看通用枚举配置");
        }
        SysSystem sys = this.getSystem();
        sysFile.setSaveType(TableConstant.sysFile_saveType_link_3);
        sysFile.setSysId(sys.getId());
        Integer fileTypeIndex = sysFile.getFileTypeIndex();
        if (fileTypeIndex != null && EventUnifyType.powtoonVideoFileTypes.contains(fileTypeIndex)) {
            String fileKey = awsS3StorageService.uploadFileToS3(sysFile.getThumbNailUrl(), master.getId(), user.getId());
            sysFile.setThumbNailUrl(fileKey);
        }

        if (sysFileService.saveOrUpdate(sysFile)) {
            if (fileTypeIndex != null && EventUnifyType.powtoonVideoFileTypes.contains(fileTypeIndex)) {
                powtoonExternalVideoService.createExternalVideoForSysFile(sysFile);
            }
            sysFileService.getVideoSnapshotUrl(sysFile);
            sysFile.setThumbNailUrl(thumbnailProvider.getThumbnailUrl(sysFile));
            sysFile.setFullFileUrl(sysFileService.getResFullUrl(sysFile, request));
            sysFile.setFileUrl(sysFileService.getResFullUrl(sysFile, request));

            if (null != sysFile.getCourseTags()) {
                QueryWrapper<PtTags> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("master_id", master.getId());
                queryWrapper.in("file_id", sysFile.getId());
                queryWrapper.eq("type", TableConstant.COMMON_TWO);
                tagsService.remove(queryWrapper);
                List<String> tagList = sysFile.getCourseTags();
                List<PtTags> ptTagsList = new ArrayList<>();
                Integer finalMasterId = master.getId();
                tagList.forEach(
                        i -> {
                            PtTags newTags = new PtTags();
                            newTags.setMasterId(finalMasterId);
                            newTags.setTagText(i);
                            newTags.setFileId(sysFile.getId());
                            newTags.setType(TableConstant.COMMON_TWO);
                            newTags.setOrder(TableConstant.COMMON_ZERO);
                            ptTagsList.add(newTags);
                        });
                tagsService.saveOrUpdateBatch(ptTagsList);
            }
            return new Message().ok().addData("file", sysFile);
        }
        return new Message().error("保存失败");
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
