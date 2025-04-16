package com.threeatom.guidecore.controller.api.manager;

import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.GcUserVideoActionService;
import com.threeatom.guidecore.service.GcUserVideoPlaysNodeService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.GvgMasterService;
import com.threeatom.guidecore.service.NewUiGcSubjectService;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/guidecore/video")
@RestController
@Api(
    tags = "新UI视频Controller",
    value = "NewUiGcVideoController",
    description = "NewUiGcVideoController")
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
    private GcUserAccessService gcUserAccessService;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private GcUserService gcUserService; // 用户服务类--统计参与人数

    @Autowired
    private GcUserVideoActionService videoActionService; // 用户视频操作--查询评论、点赞、星级评价

    @Autowired
    private GvgMasterService gvgMasterService;

    @ApiOperation(value = "新UI视频-根据课程id加载视频信息", notes = "新UI视频-根据课程id加载视频信息", httpMethod = "POST")
    @PostMapping("subjectId/{subjectId}")
    public Message getVideosBySubject(@PathVariable("subjectId") Integer subjectId,
                                      @RequestBody Map<String, Object> param, HttpServletRequest request) {
        SysSystem system = this.getSystem();
        return gvgMasterService.getVideosBySubject(subjectId, param, request, system);
    }
}
