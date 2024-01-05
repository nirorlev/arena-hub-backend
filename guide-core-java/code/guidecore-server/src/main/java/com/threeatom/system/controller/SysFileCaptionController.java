package com.threeatom.system.controller;

import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.EventUnifyType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysFileCaption;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.mapper.SysFileCaptionMapper;
import com.threeatom.system.service.SysFileCaptionService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author huangpei
 * @title: SysFileCaptionController
 * @projectName guidecore
 * @description: TODO
 * @date 2021/11/5/00511:06
 */
@RestController
@RequestMapping("/api/v1/guidecore/sysFileCaption")
public class SysFileCaptionController extends GuideCoreController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SysFIleController.class);

    @Autowired
    SysFileCaptionService sysFileCaptionService;

    @ApiOperation(value = "查询字幕文件", httpMethod = "POST")
    @PostMapping("/selectSysFileCaption")
    public Message selectSysFileCaption(@RequestBody SysFileCaption sysFileCaption ) {
        SysFile captionList = this.sysFileCaptionService.selectSysFileCaption(sysFileCaption.getId());
//        return captionList;
        return (new Message()).ok().addData("fileCaption", captionList);
    }

    @ApiOperation(value = "保存云猫任务ID", httpMethod = "POST")
    @PostMapping("/saveSysFileCaption")
    public Message saveSysFileCaption(@RequestBody SysFileCaption sysFileCaption) {
        this.sysFileCaptionService.saveYmId(sysFileCaption.getId());
        return (new Message()).ok().addData("file", sysFileCaption);


    }



}
