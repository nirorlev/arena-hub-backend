package com.threeatom.guidecore.controller.api.manager;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.threeatom.guidecore.constant.EnvType;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.service.*;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.github.pagehelper.PageInfo;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.system.entity.SysSystem;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @auther: rjunchao
 * @date: 2021/9/4 8:55
 * @desc: 视频controller
 */

@RequestMapping("/api/v1/guidecore/video")
@RestController
@Api(tags = "新UI视频Controller", value = "NewUiGcVideoController", description = "NewUiGcVideoController")
public class NewUiGcVideoController extends GuideCoreController {

    private static final Logger log = LoggerFactory.getLogger(NewUiGcVideoController.class);


    @Autowired
    private GcVideoService service;

    @Autowired
    private NewUiGcSubjectService subjectService;

    @Autowired
    private NewUiGcSubjectService newUiGcSubjectService;

    @Autowired
    private GcUserVideoPlaysNodeService gcUserVideoPlaysNodeService;


    @Autowired
    private GcUserAccessPermissionService gcUserAccessPermissionService;

    @Autowired
    private GcUserAccessService gcUserAccessService;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private GcUserService gcUserService;//用户服务类--统计参与人数

    @Autowired
    private GcUserVideoActionService videoActionService;//用户视频操作--查询评论、点赞、星级评价

    @Autowired
    private GvgMasterService gvgMasterService;
//
//    @Autowired
//    private SysFileService sysFileService;//获取文件全路径

    @ApiOperation(value="新UI视频-根据课程id加载视频信息", notes = "新UI视频-根据课程id加载视频信息", httpMethod = "POST")
    @PostMapping("subjectId/{subjectId}")
    public Message getVideosBySubject(@PathVariable("subjectId") Integer subjectId, @RequestBody Map<String,Object> param, HttpServletRequest request){
        SysSystem system = this.getSystem();
        return gvgMasterService.getVideosBySubject(subjectId,param,request,system);
    }

}
