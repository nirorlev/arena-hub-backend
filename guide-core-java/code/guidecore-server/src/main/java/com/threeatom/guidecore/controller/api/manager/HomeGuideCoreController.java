package com.threeatom.guidecore.controller.api.manager;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.constant.SysConstant;
import com.threeatom.guidecore.constant.*;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.controller.manager.vo.HomePage;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.dto.response.VersionDto;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.service.*;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.guidecore.util.RequestUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guidecore")
@Api(tags = "仪表盘数据")
@Slf4j
public class HomeGuideCoreController extends GuideCoreController {

    @Autowired private GcManagerService managerService;
    @Autowired private GcMasterService masterService;
    @Autowired private SysFileService sysFileService;
    @Autowired private GcSubjectService subjectService;
    @Autowired private GcVideoService videoService;
    @Autowired private GcResourceService resourceService;
    @Autowired private GcUserAccessService userAccessService;
    @Autowired private GcAccessService accessService;
    @Autowired RedisOperator redisOperator;
    @Autowired private Environment env;
    @Autowired private GcTeacherDataService teacherDataService;
    @Autowired private GcProblemService gcProblemService;
    @Autowired private GcAccessService gcAccessService;
    @Autowired private FrontendVersionService frontendVersionService;

    @PostMapping("/saveHomeVideo")
    public Message saveHomeVideo(@RequestBody JSONObject jsonRequest) {
        if (jsonRequest == null
                || jsonRequest.getInteger("fileId") == null
                || jsonRequest.getInteger("fileId") == null) {
            return new Message().error("fileId不可为空");
        }

        GcMaster master = this.getMaster();

        master.setIntroVideoId(jsonRequest.getInteger("fileId"));

        if (masterService.setMaster(master)) {
            return new Message().ok().addData("master", master);
        }
        return new Message().error();
    }

    @ApiOperation(value = "获取首页数据", httpMethod = "GET")
    @GetMapping("/getHomeData")
    @ApiResponses({@ApiResponse(code = 200, message = "请求成功", response = HomePage.class)})
    public Message homePageData(HttpServletRequest request) {
        SysSystem sys = this.getSystem();
        GcManager manager = this.getManager();
        GcMaster master = this.getMaster();
        if (Objects.nonNull(master.getFaviconLogoFileId())) {
            SysFile sysFile = sysFileService.getById(master.getFaviconLogoFileId());
            String fullFileUrl = sysFileService.getResFullUrl(sysFile, request);
            master.setFaviconFullFileUrl(fullFileUrl);
        }
        // 查询此门户下是否有免费code
        GcAccess gcAccess = gcAccessService.selectFreeCodeByMaster(master.getId());
        if (Objects.nonNull(gcAccess)) {
            master.setFreeAccessCode(gcAccess);
        }
        if (master.getIntroVideoFile() != null) {
            SysFile sf = master.getIntroVideoFile();
            sf.setFullFileUrl(sysFileService.getResFullUrl(master.getIntroVideoFile(), request));
        }
        HomePage gp = new HomePage();

        Map<String, Long> map = userAccessService.getMasterIdUsersNum(master.getId());
        Integer studentNum = map.get("studentNum").intValue();
        Integer teacherNum = map.get("teacherNum").intValue();

        gp.setActiveUserNum(studentNum + teacherNum);

        Map<String, Long> map1 = accessService.getALlAccessCodeNumsByMasterId(master.getId());
        Integer adminNum = map1.get("adminNum").intValue();
        Integer userNum = map1.get("userNum").intValue();
        Integer subjectAdminNum = map1.get("subjectAdminNum").intValue();
        gp.setAdminCodeUserNum(adminNum);
        gp.setUserCodeUserNum(userNum);
        gp.setSubjectAdminCodeUserNum(subjectAdminNum);

        if (TableConstant.COMMON_ZERO == manager.getLevel()) {
            gp.setSubNum(subjectService.getSubjectNum(master.getId(), null, null));
            gp.setTopicNum(subjectService.getSubTopicNum(master.getId(), null, null));
            gp.setVideoNum(videoService.getVideoNum(master.getId(), null, null));
            gp.setResNum(resourceService.getResourceNum(master.getId(), null, null));
        } else if (TableConstant.COMMON_ONE == manager.getLevel()) {
            GcUserAccess gcUserAccess =
                    userAccessService.selectUserAccessByManagerAndMaster(manager.getId(), master.getId());
            GcUserAccessPermission gcUserAccessPermission =
                    userAccessService.getUserAccessPermission(gcUserAccess.getId());
            List<Integer> subIds = gcUserAccessPermission.getSubPermission().toJavaList(Integer.class);
            gp.setSubNum(subIds.size());
            gp.setTopicNum(subjectService.getSubTopicNum(master.getId(), subIds, manager.getId()));
            gp.setVideoNum(videoService.getVideoNum(master.getId(), subIds, manager.getId()));
            gp.setResNum(resourceService.getResourceNum(master.getId(), subIds, manager.getId()));
        }
        master.setLogoFullUrl(sysFileService.getResFullUrl(master.getLogoFile(), request));
        if (Objects.nonNull(master.getAdminLogoFileId())) {
            SysFile sysFile = sysFileService.getById(master.getAdminLogoFileId());
            String fullFileUrl = sysFileService.getResFullUrl(sysFile, request);
            master.setAdminLogoFullFileUrl(fullFileUrl);
        }
        Integer fid = null;
        if (master.getExtVar() != null) {
            fid = master.getExtVar().getInteger("bgFid");
        }

        if (fid != null) {
            SysFile file = sysFileService.getById(fid);
            if (file != null) {
                String fullUrl = sysFileService.getResFullUrl(file, request);
                master.getExtVar().put("fullUrl", fullUrl);
            }
        }
        master.setEmailCc(masterService.getById(master.getId()).getEmailCc());
        master.setManagerId(manager.getId());
        gp.setMaster(master);
        Message msg = new Message().ok();

        // languagelist的返回
        LanuageType[] lanuageTypes = LanuageType.values();
        Map<String, String> languageList = new HashMap<>();
        for (LanuageType lanuageType : lanuageTypes) {
            languageList.put(lanuageType.getCode(), lanuageType.getDesc());
        }

        // category
        List<GcCategory> gcCategories = gcProblemService.selectCategoryList();

        msg.addData("category", gcCategories);
        msg.addData("language", languageList);
        msg.addData("HomeData", gp);
        msg.addData("platForm", VideoCallPlateformType.values());
        msg.addData("timeZone", TimeZoneType.values());
        return msg;
    }

    @ApiOperation(value = "获取门户下用户行为数据图表", httpMethod = "POST")
    @PostMapping("/homeUserBehaviorChartsData")
    public Message homeUserBehaviorChartsData(
            @RequestBody JSONObject jsonRequest, HttpServletRequest request) {
        Message message = new Message();
        Integer masterId = getHeaderMasterId(request);
        ApiAssert.notNull(jsonRequest, "参数缺失");
        ApiAssert.notNull(masterId, "masterId缺失");
        List<Integer> masterIds = new ArrayList<>();
        masterIds.add(masterId);
        Integer overDate = jsonRequest.getInteger("overDate");
        ApiAssert.notNull(overDate, "必填参数overDate缺失");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 根据天数计算时间
        Date end = new Date();
        Calendar cld = Calendar.getInstance();
        cld.setTime(new Date());
        cld.add(Calendar.DAY_OF_MONTH, (~(overDate - 2)));

        String startTime = sdf.format(cld.getTime());
        String endTime = sdf.format(end);
        List<Map<String, Object>> allUserInThisMaster =
                userAccessService.getAllUserInThisMaster(
                        masterIds, null, request, new PageParam(request), null);
        List<Integer> user_id =
                allUserInThisMaster.stream()
                        .map(p -> (Integer) p.get("user_id"))
                        .collect(Collectors.toList());
        List<Integer> subjectIds = subjectService.getSubjectIds(masterId);
        JSONObject jsonObject =
                teacherDataService.getStudentBehaviorChartsData(
                        user_id, subjectIds, masterId, startTime, endTime, message, this.getManager().getId());

        return message.ok("查询成功").addData("studentBehaviorChartsData", jsonObject);
    }

    @ApiOperation(value = "关闭网站", httpMethod = "POST")
    @PostMapping("/close")
    public Message close() {
        Integer managerId = this.getManager().getId();
        if (masterService.setMasterState(managerId, 0)) return new Message().ok();
        else return new Message().error();
    }

    @ApiOperation(value = "打开网站", httpMethod = "POST")
    @PostMapping("/open")
    public Message open() {
        Integer managerId = this.getManager().getId();
        if (masterService.setMasterState(managerId, 1)) return new Message().ok();
        else return new Message().error();
    }

    @ApiOperation(value = "关闭网站", httpMethod = "POST")
    @PostMapping("/superAdminClose")
    public Message close(@RequestBody GcMaster gcMaster) {
        if (masterService.superAdminSetMasterState(gcMaster.getId(), 0))
            return new Message().ok("success");
        else return new Message().ok("fail");
    }

    @ApiOperation(value = "打开网站", httpMethod = "POST")
    @PostMapping("/superAdminOpen")
    public Message open(@RequestBody GcMaster gcMaster) {
        if (masterService.superAdminSetMasterState(gcMaster.getId(), 1)) return new Message().ok();
        else return new Message().error();
    }

    @ApiOperation(value = "登录", httpMethod = "POST")
    @ApiImplicitParams(
            value = {
                @ApiImplicitParam(
                        name = "username",
                        value = "用户名，邮箱",
                        required = true,
                        dataType = "String"),
                @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String")
            })
    @PostMapping("/login")
    public Message Login(@RequestBody JSONObject jsonRequest) {
        String username = jsonRequest.getString("username");
        String password = jsonRequest.getString("password");

        ApiAssert.notEmpty(username, I18NUtil.get("guidecore.native.noUserName"));
        ApiAssert.notEmpty(password, I18NUtil.get("guidecore.native.noPassword"));

        String token = managerService.loginGetToken(username, password);
        return new Message().ok().addData("token", token);
    }

    @ApiOperation(value = "注册", httpMethod = "POST")
    @ApiImplicitParams(
            value = {
                @ApiImplicitParam(name = "email", value = "email", required = true, dataType = "String"),
                @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String"),
                @ApiImplicitParam(name = "firstName", value = "名字前缀", required = true, dataType = "String"),
                @ApiImplicitParam(name = "lastName", value = "名字后缀", required = true, dataType = "String"),
                @ApiImplicitParam(name = "code", value = "注册码", dataType = "String")
            })
    @PostMapping("/register")
    public Message Register(@RequestBody JSONObject jsonRequest) {
        String email = jsonRequest.getString("email");
        String password = jsonRequest.getString("password");
        String fName = jsonRequest.getString("firstName");
        String lName = jsonRequest.getString("lastName");
        String code = jsonRequest.getString("code");
        String type = jsonRequest.getString("type");

        ApiAssert.notEmpty(email, I18NUtil.get("guidecore.master.noEmail"));
        ApiAssert.notEmpty(password, I18NUtil.get("guidecore.native.noPassword"));

        List<SysSystem> sysList = this.getSystemList("guidecore");

        if (null != type) {
            String registerCode = env.getProperty("registercode");
            if (null == registerCode) {
                registerCode = "6WajnPPO";
            }
            if (type.equals(EnvType.PT.getDesc())) {
                if (!registerCode.equals(code)) {
                    return new Message().error(I18NUtil.get("guidecore.master.codeError"));
                } else {
                    code = null;
                }
            }
        }

        managerService.createManager(sysList.get(0).getId(), email, password, fName, lName, code);
        return new Message().ok(I18NUtil.get("guidecore.master.register.success"));
    }

    // newgt站点，admin忘记密码前缀，与用户端WechatUserGuideCoreController的发送验证码区分开
    private static final String email_prefix_newgt = "newgt_"; // 用于重置密码
    private static final String email_prefix_newgt_resend_es = "newgt_60_"; // 用户判断1分账只能发一次

    @ApiOperation(value = "修改密码", httpMethod = "POST")
    @PostMapping("/editMasterUserInfo")
    public Message editMasterUserInfo(@RequestBody GcManager gcManager) {
        String email = gcManager.getUsername();
        String password = gcManager.getPassword();
        String lastName = gcManager.getLastName();
        String firstName = gcManager.getFirstName();

        ApiAssert.notEmpty(email, I18NUtil.get("guidecore.master.noEmail"));
        ApiAssert.notEmpty(lastName, I18NUtil.get("guidecore.native.noLastName"));
        ApiAssert.notEmpty(firstName, I18NUtil.get("guidecore.native.noFirstName"));

        GcManager user = this.getManager();
        if (user == null) {
            return new Message().error(I18NUtil.get("guidecore.master.login.usernameError"));
        }

        user.setLastName(gcManager.getLastName());
        user.setFirstName(gcManager.getFirstName());
        user.setUsername(gcManager.getUsername());
        if (null != password) {
            String pwdHash =
                    new SimpleHash("MD5", password, user.getSalt() + SysConstant.PASS_SALT).toHex();
            user.setPassword(pwdHash);
        }
        if (managerService.saveOrUpdateManager(user)) {
            return new Message().ok().addData("user", user);
        } else {
            return new Message().error();
        }
    }

    @ApiOperation(value = "修改密码", httpMethod = "POST")
    @PostMapping("/changePassword")
    public Message changePassword(@RequestBody JSONObject requestParams) {
        String email = requestParams.getString("email");
        String captcha = requestParams.getString("captcha");
        String password = requestParams.getString("password");

        ApiAssert.notEmpty(email, I18NUtil.get("guidecore.master.noEmail"));
        ApiAssert.notEmpty(captcha, I18NUtil.get("guidecore.master.sendEmailCaptcha"));
        ApiAssert.notEmpty(password, I18NUtil.get("guidecore.native.noPassword"));

        GcManager user = managerService.getManagerByUsername(email);
        if (user == null) {
            return new Message().error(I18NUtil.get("guidecore.master.login.usernameError"));
        }

        String rb = (String) redisOperator.get(email_prefix_newgt + email);

        ApiAssert.notEmpty(rb, I18NUtil.get("guidecore.master.invalidEmailCaptcha"));
        if (!rb.equals(captcha)) {
            return new Message().error(I18NUtil.get("guidecore.master.errorEmailCaptcha"));
        }

        String pwdHash =
                new SimpleHash("MD5", password, user.getSalt() + SysConstant.PASS_SALT).toHex();
        user.setPassword(pwdHash);
        if (managerService.saveOrUpdateManager(user)) {
            return new Message().ok();
        } else {
            return new Message().error();
        }
    }

    @Order(1)
    @GetMapping("/version")
    public Message getVersion(HttpServletRequest request, HttpServletResponse response) {
        try {
            VersionDto versionDto = JSONObject.parseObject(new FileInputStream("./version.json"), VersionDto.class);
            versionDto.setFrontend(getRequestedFrontendVersion(request, response));
            return new Message().ok().addData("result", versionDto);
        } catch (IOException e) {
            String errorMessage = String.format("Failed to get version due to %s", e.getMessage());
            log.error(errorMessage);
            return new Message().error(errorMessage);
        }
    }

    private String getRequestedFrontendVersion(HttpServletRequest request, HttpServletResponse response) {
        String requestedVersion = RequestUtil.getRequestedFrontendVersion(request, response);
        Integer masterId = RequestUtil.getMasterId(request).orElse(null);
        return frontendVersionService.getVersion(requestedVersion, masterId);
    }
}
