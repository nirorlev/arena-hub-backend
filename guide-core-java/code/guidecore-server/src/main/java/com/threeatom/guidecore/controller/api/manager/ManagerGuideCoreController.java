package com.threeatom.guidecore.controller.api.manager;

import com.threeatom.common.exception.PermitException;
import com.threeatom.common.permissions.service.AuthorizationService;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.PageInfo;
import com.threeatom.constant.SysConstant;
import com.threeatom.guidecore.constant.*;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.service.*;
import com.threeatom.guidecore.util.stringWidthConvertUtil;
import com.threeatom.system.entity.SysFileCaption;
import com.threeatom.system.service.SysFileCaptionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.constant.ObjectStorageConstants;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import com.threeatom.utils.ToolUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/v1/guidecore/manager")
@RequiresRoles({"manager"})
@Api(tags = "管理平台功能ManagerGuideCoreController")
public class ManagerGuideCoreController extends GuideCoreController {


    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerGuideCoreController.class);
    @Autowired
    private GcAccessService accessService;
    @Autowired
    private GcSubjectService subService;
    @Autowired
    private GcMasterService masterService;
    @Autowired
    private GcVideoService videoService;
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private SysFileCaptionService sysFileCaptionService;
    @Autowired
    private GcEventService eventService;
    @Autowired
    private GcResourceService resourceService;
    @Autowired
    private GcUserAccessService userAccessService;
    @Autowired
    private RedisOperator redisOperator;
    @Autowired
    private GcSubjectAssociationService gcSubjectAssociationService;
    @Autowired
    private GcUserVideoActionService gcUserVideoActionService;
    @Autowired
    private GcVideoService gcVideoService;
    @Autowired
    private GcUserAccessService gcUserAccessService;
    @Autowired
    private GcAccessService gcAccessService;
    @Autowired
    private GcUserAccessPermissionService gcUserAccessPermissionService;
    @Autowired
    private GcMasterHomeInfoService masterHomeInfoService;
    @Autowired
    private GcUserService gcUserService;
    @Autowired
    private GcUserService userService;
    @Autowired
    private PtLoginConfigService ptLoginConfigService;
    @Autowired
    private PtTagsService ptTagsService;
    @Autowired
    private GvgMasterService gvgMasterService;
    @Autowired
    private CourseContentService courseContentService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private PortalUserService portalUserService;

    @GetMapping("/getFuzzyNameVideoInMaster/{videoName}")
    public Message getFuzzyNameVideoInMaster(@PathVariable("videoName") String videoName, HttpServletRequest request) {
        ApiAssert.notNull(videoName, "videoName " + I18NUtil.get("guidecore.master.valueRuleError"));
        GcMaster master = this.getMaster();
        if(Objects.isNull(master)){
            Integer masterId = request.getIntHeader("masterId");
            master = masterService.getMasterById(masterId);
        }
        List<GcVideo> videoList = videoService.getFuzzyNameVideoInMaster(master.getId(), videoName);
        if(!videoList.isEmpty()){
            for (GcVideo video : videoList) {
                sysFileService.getResFullUrl(video.getVideoFile(), request);
                video.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(video));
                if (null!=video.getThumbnailUrl()){
                    video.setSnapshotUrl(video.getThumbnailUrl());
                }
            }
        }
        Message message = new Message().ok();
        return message.addData("videoList", videoList);
    }

    @ApiOperation(value = "获取homePage信息")
    @GetMapping("/getHomePageBuilder")
    public Message getHomePageBuilder(HttpServletRequest request){
        GcMaster master = this.getMaster();
        if(Objects.isNull(master)){
            Integer masterId = request.getIntHeader("masterId");
            master = masterService.getMasterById(masterId);
        }
        List<GcMasterHomeInfo> infoList = masterHomeInfoService.getGcMasterHomeInfoList(master.getId(),TableConstant.gcMasterHomeInfo_name_page,null,request);
        return new Message().ok().addData("infoList",infoList);
    }

    @GetMapping("/user")
    @ApiOperation(value = "获取用户信息", httpMethod = "GET")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功", response = GcManager.class)
    })
    public Message getUser() {
        GcManager manager = this.getManager();
        return new Message().ok().addData("manager", manager);
    }


    @ApiOperation(value = "保存基本配置", httpMethod = "POST")
    @PostMapping("/save")
    public Message save(@RequestBody @ApiParam(name = "主站信息", value = "站点信息") GcMaster master) {
        GcManager manager = this.getManager();
        if(Objects.isNull(manager.getSuperAdminFlag()) || !manager.getSuperAdminFlag().equals(1)) {
            master.setManagerId(manager.getId());
        }
        String context= master.getContext();
        if(StringUtils.isNotEmpty(context)) {
            GcMaster m=masterService.getMasterByContext(context);
            if(m!=null&&!m.getId().equals(master.getId())) throw new SystemException(I18NUtil.get("guidecore.native.hasContent"));

            ApiAssert.ifStringNotInList(context, CommonConstant.defaultNoPortalName, I18NUtil.get("guidecore.native.defaultNoPortalName"));
        }
        if (masterService.setMaster(master)) {
            return new Message().ok(200, "保存成功").addData("master",master);
        }else {
            return new Message().error();
        }

    }

    @ApiOperation(value = "科目和主题的树状结构图", httpMethod = "GET")
    @GetMapping("/subList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功", response = GcSubject.class)
    })
    public Message subjectList(HttpServletRequest request) {

        GcMaster master = this.getMaster();
        if(Objects.isNull(master)){
            Integer masterId = request.getIntHeader("masterId");
            master = masterService.getMasterById(masterId);
        }
        SysSystem sys=this.getSystem();
        List<GcSubject> list = null;
        GcManager manager = this.getManager();
        if(manager.getLevel()!=null&&manager.getLevel()== LevelType.MASTER_MANAGER){//课程管理员
            //判断用户层级得到科目和主题的权限信息json
            GcUserAccess userAccess = userAccessService.selectUserAccessByManagerAndMaster(manager.getId(),master.getId());
            GcUserAccessPermission permission = userAccessService.getUserAccessPermission(userAccess.getId());
            ApiAssert.notNull(permission, 403, "没有找到用户权限表");
            List<Integer> subIds = permission.getSubPermission().toJavaList(Integer.class);

            List<GcSubject> subjectAssociationList= subService.selectSubjectAssociation(master.getId(),subIds,false);
            subjectAssociationList=subService.setSubListImg(subjectAssociationList, sys, request);
            List<Integer> assoSubIds = subjectAssociationList.stream().map(GcSubject::getId).collect(Collectors.toList());
            subIds.removeAll(assoSubIds);
            //导入课程
            list = subService.getSubListWithImgByIds(subIds, sys, request,master.getId());
//          //门户课程
            list.addAll(subjectAssociationList);
        }else{
            list = subService.getSubListWithImg(master.getId(), sys, request);
            List<GcSubject> subjectAssociationList= subService.selectSubjectAssociation(master.getId(),null,false);
            list.addAll(subjectAssociationList);
        }
        return new Message().ok().addData("list", list);
    }

    @ApiOperation(value = "保存AccessCode", httpMethod = "POST")
    @PostMapping("/saveCode")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功", response = GcAccess.class)
    })
    public Message saveAccessCode(@RequestBody @ApiParam(name = "创建access", value = "accesscode") GcAccess access,HttpServletRequest request) {
        GcMaster master = this.getMaster();
        LOGGER.info(access.toString());
        access.setMasterId(master.getId());
        if(access.getPackageShowFlag()==null){
            access.setPackageShowFlag(TableConstant.COMMON_ONE);
        }else if(access.getPackageShowFlag()!=null){
            access.setPackageShowFlag(access.getPackageShowFlag());
        }

        GcAccess gcAccess = gcAccessService.selectFreeCodeByMaster(master.getId());
        if(Objects.nonNull(gcAccess)) {
            if ((Objects.isNull(access.getId()) || !access.getId().equals(gcAccess.getId())) && (Objects.nonNull(access.getDefaultCodeFlag()) && access.getDefaultCodeFlag().equals(1))) {
                throw new SystemException(I18NUtil.get("guidecore.duplicate.code.error"));
            }
        }
        //判断注册码类型
        if(access.getRoleType()!=null && !access.getRoleType().equals("")){
            ApiAssert.jsonValueIntegerIn(access.getRoleType(), "[0,1,2]", "roleType只能为0或1");
            if(Objects.nonNull(access.getDefaultCodeFlag()) && access.getDefaultCodeFlag().equals(1)){
                access.setCodeType(AccessCodeType.DEFAULT_ACCESS_CODE);
            }else {
                access.setCodeType(AccessCodeType.USER_ACCESS_CODE);
            }
        }else{
            if (Objects.isNull(access.getCode())){
                String code = ToolUtil.getRandomString(3)+System.currentTimeMillis()+ToolUtil.getRandomString(2);
                access.setCode(code);
            }
            access.setCodeType(AccessCodeType.MASTER_ACCESS_CODE);
            access.setRoleType(TableConstant.COMMON_TWO);
        }
        List<GcAccess> list = accessService.getAccessByMasterIdAndCode(access);

        if(access.getId()==null && list!=null && list.size()>0) {
            return new Message().error(I18NUtil.get("guidecore.master.accessCodeExist"));
        }
        if (accessService.addAccess(access)) return new Message().ok("添加成功").addData("sync", access);

        else return new Message().error("添加失败");
    }


    @ApiOperation(value = "删除AccessCode", httpMethod = "POST")
    @PostMapping("/deleteCode")
    public Message deleteCode(@RequestBody  GcAccess gcAccess) {
        Message message = new Message();
        List<GcUserAccess> gcUserAccessList = gcUserAccessService.getAccessByAccessId(gcAccess.getId());
        if(gcUserAccessList.size()!=TableConstant.COMMON_ZERO) {
            return message.error(I18NUtil.get("guidecore.delete.code.fail"));
        }else {
            gcAccessService.deleteAccess(gcAccess.getId());
            return message.ok(I18NUtil.get("guidecore.delete.code.success"));
        }
    }

    @ApiOperation(value = "删除用户权限", httpMethod = "POST")
    @PostMapping("/deleteUserAccess")
    public Message deleteUserAccess(@RequestBody GcUserAccess gcUser,HttpServletRequest request) {
        Message message = new Message();
        GcMaster master = this.getMaster();
        GcManager gcManager = this.getManager();
        List<GcUserAccess> gcUserAccessList = gcUserAccessService.getUserAccessListByUserId(gcUser.getUserId(),request);
        List<Integer> masterIdList = gcUserAccessList.stream().map(GcUserAccess::getMasterId).collect(Collectors.toList());
        if(Objects.nonNull(gcManager) && Objects.nonNull(gcManager.getSuperAdminFlag()) && TableConstant.COMMON_ONE==gcManager.getSuperAdminFlag()){
            GcUserAccess gcUserAccess = gcUserAccessService.getAccessByUserIdMaster(gcUser.getUserId(),gcUser.getMasterId());
            if(TableConstant.COMMON_ZERO!=gcUserAccessList.size() && TableConstant.COMMON_ONE==gcUserAccessList.size()){
                gcUserAccessService.deleteById(gcUserAccess.getId());
                this.gcUserService.deleteById(gcUserAccess.getUserId());
            }else {
                gcUserAccessService.deleteById(gcUserAccess.getId());
            }
        }else {
            GcUserAccess gcUserAccess = gcUserAccessService.getAccessByUserIdMaster(gcUser.getUserId(),master.getId());
            if (null != gcUserAccess && null!=masterIdList && masterIdList.size()>TableConstant.COMMON_ZERO && masterIdList.contains(gcUserAccess.getMasterId())) {
                if (TableConstant.COMMON_ZERO != gcUserAccessList.size() && TableConstant.COMMON_ONE == gcUserAccessList.size()) {
                    gcUserAccessService.deleteById(gcUserAccess.getId());
                    this.gcUserService.deleteById(gcUserAccess.getUserId());
                    return new Message().ok("success");
                } else {
                    gcUserAccessService.deleteById(gcUserAccess.getId());
                    return new Message().ok("success");
                }
            }
        }
        return new Message().ok();
    }

    @ApiOperation(value = "代码list", httpMethod = "POST")
    @PostMapping("/codeList")
    public Message listAccessCode(@RequestBody GcMaster filterMaster, HttpServletRequest request) {
        GcMaster master = this.getMaster();
        GcManager manager = this.getManager();
        List<Integer> masterIds = new ArrayList<>();
        masterIds.add(master.getId());
        //门户端注册码集合
        List<GcAccess> managerCodeList = new ArrayList<>();
        //用户端注册码集合
        List<GcAccess> userCodeList = new ArrayList<>();
        //注册码集合
        List<GcAccess> list = accessService.findAccessListByMasterId(master.getId());
        List<GcSubject> sublist = subService.getSubListTop(master.getId());
        List<GcAccess> adminCodeList = accessService.getAdminAccessListByMasterId(master.getId());
        List<Map<String, Object>> userList = userAccessService.getAllUserInThisMaster(masterIds,filterMaster.getSearchFilter(),request,new PageParam(request),null);
        //使用门户端注册码注册的用户集合
        List<Map<String, Object>> managerList = userAccessService.getAllManagerInThisMaster(master.getId());

        for (GcAccess access : list) {
            if(null!=access.getPackageVideoFileId()){
                SysFile videoFile = sysFileService.getById(Integer.parseInt(access.getPackageVideoFileId()));
                sysFileService.getResFullUrl(videoFile,request);
                access.setPackageVideoFile(videoFile);
            }
            //构造门户端注册码集合
            if(access.getCodeType()!=null&&access.getCodeType()==AccessCodeType.MASTER_ACCESS_CODE){
                managerCodeList.add(access);
                //统计门户端用户注册码的注册情况
                List<Map<String, Object>> masterSampleList = managerList.stream().filter(m -> m.get("code").equals(access.getCode())).collect(Collectors.toList());
                access.setUserNum(masterSampleList.size());

            }else{
                userCodeList.add(access);
                //统计用户端用户注册码的注册情况
                List<Map<String, Object>> sampleList = userList.stream().filter(u -> u.get("code").equals(access.getCode())).collect(Collectors.toList());
                access.setUserNum(sampleList.size());
            }
            if(access.getPackageImgId()!=null) {
                String fullFileUrl = sysFileService.getResFullUrl(sysFileService.getById(access.getPackageImgId()),request);
                access.setPackageImgFullUrl(fullFileUrl);
            }


        }
        SysSystem sys = this.getSystem();

        if (userList.size() != 0) {
            for (Map<String,Object> user:userList){
                SysFile file = new SysFile();
                if (null!=user.get("f_file_url")) {
                    file.setFileUrl(user.get("f_file_url").toString());
                }
                if(null!=user.get("f_save_type")) {
                    file.setSaveType(Integer.parseInt(user.get("f_save_type").toString()));
                }
                if(file.getFileUrl() != null && file.getSaveType() != null)
                    user.put("full_file_url",sysFileService.getResFullUrl(file,  request));
                if(TableConstant.COMMON_ZERO!=Integer.parseInt(user.get("joinType").toString())){
                    user.put("joinType",1);
                }
            }
        }
        return new Message().ok()
                .addData("list", userCodeList)
                .addData("managerCodeList", managerCodeList)
                .addData("subjectList", sublist)
                .addData("adminCodeList", adminCodeList)
                .addData("managerList", managerList)
                .addData("userList", new PageInfo<>(userList));
    }

    private static final String masterRandomToken = "masterRandomToken_";


    @ApiOperation(value = "超级管理员获取用户端token", httpMethod = "POST")
    @PostMapping("/superAdminGetUserToken")
    public Message superAdminGetUserToken(@RequestBody GcMaster filterMaster) {
        GcUser gcUser = userService.getById(filterMaster.getUserId());
        String token = userService.generateJwtToken(gcUser, filterMaster.getId());
        return new Message().ok().addData("token",token);
    }

    @PostMapping("/changePassword")
    public Message changePassword(@RequestBody JSONObject requestParams, HttpServletRequest request) throws Exception {
        GcMaster master = this.getMaster();
        GcManager gcManager = this.getManager();
        //如果是超级管理员则直接重置该用户的密码，并且发送邮件
        Integer userId = requestParams.getInteger("userId");
        GcUser user = gcUserService.getById(userId);
        String val = "";
        Random random = new Random();
        //用循环输出六个字符进行拼接
        for (int i = 0; i < 8; i++) {
            // 本次循环是数字还是字母
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "number";
            // 字母
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 本次字母为大写还是小写（ASCII）
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                //生成这个字符
                val += (char) (choice + random.nextInt(26));
            }
            // 数字
            else if ("number".equalsIgnoreCase(charOrNum)) {
                //数字可以直接生成
                val += String.valueOf(random.nextInt(10));
            }
        }
        String pwdHash = new SimpleHash("MD5", val, user.getSalt() + SysConstant.PASS_SALT).toHex();
        user.setPassword(pwdHash);
        if(null != gcManager && TableConstant.COMMON_ONE==gcManager.getSuperAdminFlag()){
            String newPwd = new SimpleHash("MD5", requestParams.get("newPassword"), user.getSalt() + SysConstant.PASS_SALT).toHex();
            user.setPassword(newPwd);
            if(userService.saveOrUpdate(user)){
                //发送邮件
                String email = user.getUsername();
                List<String> emailList = new ArrayList<>();
                emailList.add(email);
                return new Message().ok("success");
            }else{
                return new Message().error("fail");
            }
        }else {
            List<GcUserAccess> gcUserAccessList = gcUserAccessService.getAccessListByUser(userId);
            List<Integer> masterIdList = gcUserAccessList.stream().map(GcUserAccess::getMasterId).collect(Collectors.toList());
            if(null!=masterIdList && masterIdList.size()>TableConstant.COMMON_ZERO && masterIdList.contains(master.getId())){
                if(userService.saveOrUpdate(user)){
                    //发送邮件
                    try {
                        String textBody = I18NUtil.get("guidecore.superAdminChangePassword") + val;
                        String email = user.getUsername();
                        List<String> emailList = new ArrayList<>();
                        emailList.add(email);
                        return new Message().ok("success");
                    }catch (Exception e){
                        throw new Exception("invalid email");
                    }
                }else{
                    return new Message().error("fail");
                }
            }
        }
        return new Message().ok();
    }

    @ApiOperation(value = "生成可导入课程的token", httpMethod = "GET")
    @GetMapping("/getSubjectImportToken")
    public Message getSubjectImportToken () {
        GcMaster master = this.getMaster();
        String randomToken = ToolUtil.getRandomString(3)+System.currentTimeMillis()+ToolUtil.getRandomString(2);
        LOGGER.info("randomToken: "+randomToken);
        redisOperator.set(masterRandomToken+master.getId(),randomToken,300);//300秒
        return new Message().ok().addData("subjectImportToken", randomToken);
    }


    @ApiOperation(value = "导入课程", httpMethod = "Post")
    @PostMapping("/importSubject")
    public Message importSubject(@RequestBody GcSubject sub) {
        GcMaster master = this.getMaster();

        GcManager manager = this.getManager();
        GcSubjectAssociation sa = new GcSubjectAssociation();
        sa.setMasterId(master.getId());
        sa.setRelationType(TableConstant.gcSubjectAssociation_relationType_1import);
        GcSubject subject =null;
        if(sub.getToken()!=null) {
            //通过token导入课程
            subject = subService.getSubByToken(sub.getToken());
            if(subject==null) {
                return new Message().error(I18NUtil.get("guidecore.master.canFindSubject"));
            }

            sa.setSubjectId(subject.getId());
        }else {
            ApiAssert.notNull(sub.getId(), "课程id不可空");
            //导入公共课
            subject = subService.getById(sub.getId());
            if(subject==null) return new Message().error(I18NUtil.get("guidecore.master.canFindSubject"));
            if(subject.getIsPublic().intValue()!=TableConstant.gcSubject_isPublic_1)return new Message().error(I18NUtil.get("guidecore.master.notPublicSubject"));
            sa.setSubjectId(subject.getId());
        }
        if(subject.getMasterId().intValue()==master.getId()) {
            //不可添加自身课程
            return new Message().error(I18NUtil.get("guidecore.master.cantImportYourOwnSubject"));
        }


        //判断是否添加过该课程
        int count = gcSubjectAssociationService.selectCount(subject.getId(), master.getId());
        if(count>0) {
            return new Message().error(I18NUtil.get("guidecore.master.duplicateImportSubject"));
        }

        if(null==sub.getFid() && null!=manager) {
            GcUserAccess gcUserAccess = gcUserAccessService.selectUserAccessByManagerAndMaster(manager.getId(),master.getId());
            if(null!=gcUserAccess) {
                GcAccess gcAccess = gcAccessService.getAccessById(gcUserAccess.getAccessId());
                GcUserAccessPermission gcUserAccessPermission = gcUserAccessService.getUserAccessPermission(gcUserAccess.getId());
                JSONArray permissionJsonArray = gcUserAccessPermission.getSubPermission();
                JSONArray jsonArray = gcAccess.getSubjectJson();
                if (!jsonArray.contains(sub.getId())) {
                    jsonArray.add(sub.getId());
                    gcAccessService.updateById(gcAccess);
                }
                if (!permissionJsonArray.contains(sub.getId())) {
                    permissionJsonArray.add(sub.getId());
                    gcUserAccessPermissionService.saveOrUpdate(gcUserAccessPermission);
                }
            }
        }
        gcSubjectAssociationService.save(sa);
        return new Message().ok("绑定成功！");
    }


    @ApiOperation(value = "添加课程或者话题", httpMethod = "POST")
    @PostMapping("/saveSub")
    public Message saveSub(@RequestBody @ApiParam(name = "创建主题", value = "主题结构") GcSubject sub,HttpServletRequest request) {
        ApiAssert.ifStringNotInList(sub.getName(), CommonConstant.defaultNoCourseOrVideName, "课程名称错误，不可用该值");

        GcManager manager = this.getManager();




        GcMaster master = this.getMaster();
        Integer masterId = null;
        if (null==master&&null!=request.getHeader("masterId")){
            masterId = Integer.parseInt(request.getHeader("masterId"));
        }else {
            masterId = master.getId().intValue();
        }
        master = masterService.getById(masterId);
        GcUser user = this.getGcUser();
        subService.saveSubInfo(sub,manager,master,user,request);
        return new Message().ok("添加成功！").addData("sync", sub);
    }

    @ApiOperation(value = "修改课程排序", httpMethod = "POST")
    @PostMapping("/changeSubOrder")
    public Message changeSubOrder(@RequestBody JSONObject requestParams,HttpServletRequest request) {
        List<Integer> subIds =JSONObject.parseArray(requestParams.getString("subIds"),  Integer.class);
        if (subIds.size() < 0)  throw new SystemException("缺少排序参数");
        GcMaster master = this.getMaster();
        if (null==master&&null!=request.getHeader("masterId")){
            master = new GcMaster();
            master.setId(Integer.parseInt(request.getHeader("masterId")));
        }


        if (subService.changeSubOrder(subIds,master.getId()))
            return new Message().ok("修改成功！");
        else {
            return new Message().error("修改失败！");
        }
    }

    @ApiOperation(value = "修改视频排序", httpMethod = "POST")
    @PostMapping("/changeVideoOrder")
    public Message changeVideoOrder(@RequestBody JSONObject requestParams) {
        List<Integer> videoIds =JSONObject.parseArray(requestParams.getString("videoIds"),  Integer.class);
        if (videoIds.size() < 0)  throw new SystemException("缺少排序参数");
        if (videoService.changeVideoOrder(videoIds))
            return new Message().ok("修改成功！");
        else
            return new Message().error("修改失败！");
    }


    @ApiOperation(value = "视频的列表", httpMethod = "GET")
    @GetMapping("/videoList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功", response = GcVideo.class)
    })
    public Message videoListAll() {

        try {
            GcMaster master = this.getMaster();
            GcManager manager = this.getManager();
            List<Integer> subIds;
            if(manager.getLevel()!=null&&manager.getLevel()== LevelType.MASTER_MANAGER){
                //判断用户层级得到科目和主题的权限信息json
                GcUserAccess userAccess = userAccessService.selectUserAccessByManagerAndMaster(manager.getId(),master.getId());
                GcUserAccessPermission permission = userAccessService.getUserAccessPermission(userAccess.getId());
                ApiAssert.notNull(permission, 403, "没有找到用户权限表");
                subIds = permission.getSubPermission().toJavaList(Integer.class);

                List<GcSubject> subjectAssociationList= subService.selectSubjectAssociation(master.getId(),subIds,false);
                List<Integer> assoSubIds = subjectAssociationList.stream().map(GcSubject::getId).collect(Collectors.toList());
                subIds.addAll(assoSubIds);
                List<GcSubject> level1Subids = subService.selectAllLevel1SubList(subIds,null,master.getId());
                List<Integer> level1subids = level1Subids.stream().map(GcSubject::getId).collect(Collectors.toList());
                subIds.addAll(level1subids);
            }else{
                List<GcSubject> subList = subService.getSubListWithHidden(master.getId());
                List<GcSubject> associationSubList= subService.selectSubjectAssociation(master.getId(),null,false);
                subList.addAll(associationSubList);
                subIds = subList.stream().map(GcSubject::getId).collect(Collectors.toList());
            }
            List<GcVideo> list = null;
            if (subIds.size() > 0) list = videoService.getVideoListBySubIds(subIds);

            return new Message().ok().addData("list", list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message().error();
    }

    @ApiOperation(value = "视频的列表", httpMethod = "GET")
    @GetMapping("/videoList/{subId}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功", response = GcVideo.class)
    })
    public Message videoList(@PathVariable("subId") Integer subId) {

        List<GcVideo> list = videoService.getVideoListBySubId(subId);
        return new Message().ok().addData("list", list);

    }

    @ApiOperation(value = "Update course video", httpMethod = "POST")
    @PostMapping("/saveVideo")
    public Message saveVideo(@RequestBody @ApiParam(name = "Update video", value = "Video entity") GcVideo video, HttpServletRequest request) {
        SysSystem system = this.getSystem();
        GcMaster master = this.getMaster();
        GcUser user = gcUserService.getCurrentUser(request);
        Integer masterId;
        if (null == master && null != request.getHeader("masterId")) {
            masterId = Integer.parseInt(request.getHeader("masterId"));
        } else {
            masterId = master.getId();
        }
        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);
        GcVideo existingVideo = videoService.findByVideoId(video.getId());
        if (!authorizationService.checkAccess(existingVideo, PermitAction.EDIT, portalUser)) {
            throw new PermitException("No permission for this!");
        }

        boolean successful = gcVideoService.saveVideoInfo(system, video, masterId, request);

        if (successful) {
            video.setSubId0(subService.getById(video.getSubId()).getFid());
            return new Message().ok("添加成功！").addData("sync", video);
        }

        return new Message().error("添加失败！");
    }

    @ApiOperation(value = "Batch save video courses with multiple file IDs", httpMethod = "POST")
    @PostMapping("/saveVideoBatch")
    public Message saveVideoBatch(
        @RequestBody @ApiParam(name = "Save Video", value = "Video entities") List<GcVideo> videoList,
        HttpServletRequest request) {

        Integer masterId = request.getIntHeader("masterId");
        if (videoService.createVideos(videoList, request)) {
            courseContentService.saveCourseContents(videoList);

            videoService.updateCourseTags(videoList, masterId);
            return new Message().ok("Added successfully")
                .addData("sync", videoList);
        }

        return new Message().error("Failed to add videos");
    }

    //弃用
    @ApiOperation(value = "添加课程或者话题", httpMethod = "POST")
    @PostMapping("/saveVideoAndFile")
    public Message saveVideo(MultipartFile file, String ext) {
        GcVideo video = JSONObject.parseObject(ext, GcVideo.class);
        ApiAssert.notNull(video, "序列化异常");
        ApiAssert.notNull(video.getVideoSource(), "视频源不能为null");
        if (file == null) {
            if (videoService.saveVideo(video)) return new Message().ok("添加成功！").addData("sync", video);
        }
        GcManager manager = this.getManager();
        SysSystem sys = this.getSystem();
        //保存视频
        SysFile sysFile = sysFileService.saveVedio(manager.getId(), sys, TableConstant.sysFile_folder_guidecoreVedio, ObjectStorageConstants.ALIYUN_OSS, file);
        if (sysFile != null && !sysFile.getId().equals(0)) {

            video.setFileId(sysFile.getId());
            video.setVideoFile(sysFile);
            if (videoService.saveVideo(video)) return new Message().ok("添加成功！").addData("sync", video);

        }
        return new Message().error();
    }

    @ApiOperation(value = "保存YouTube", httpMethod = "POST")
    @PostMapping("/saveYoutubeVideos")
    public Message saveYoutubeVideos(@RequestBody List<SysFile> youtubeSysfileList) {
        GcManager manager = this.getManager();
        SysSystem system = this.getSystem();

        try {
            if (youtubeSysfileList.size() == TableConstant.COMMON_ONE) {
                if (null != youtubeSysfileList.get(TableConstant.COMMON_ZERO).getId()) {
                    SysFile youtubeFile = youtubeSysfileList.get(TableConstant.COMMON_ZERO);
                    String fileUrl = youtubeFile.getFileUrl();
                    String youtubeFileId = fileUrl.substring(fileUrl.length() - TableConstant.youtubeFileId);
                    String fullFileUrl = "https://www.youtube.com/embed/" + youtubeFileId;
                    youtubeFile.setFileUrl(fullFileUrl);
                    if(Objects.nonNull(manager)) {
                        youtubeFile.setUploadUid(manager.getId());
                    }else {
                        youtubeFile.setUploadUid(this.getGcUser().getId());
                    }
                    youtubeFile.setName(stringWidthConvertUtil.stringWidthConvert(youtubeFile.getName()));
                    youtubeFile.setSysId(system.getId());
                    sysFileService.saveOrUpdate(youtubeFile);
                    youtubeSysfileList.set(TableConstant.COMMON_ZERO, youtubeFile);
                    return new Message().ok().addData("youtubeSysfile", youtubeSysfileList);
                }
            }
            for (SysFile sysFile : youtubeSysfileList) {
                String fileUrl = sysFile.getFileUrl();
                String youtubeFileId = fileUrl.substring(fileUrl.length() - TableConstant.youtubeFileId, fileUrl.length());
                String fullFileUrl = "https://www.youtube.com/embed/" + youtubeFileId;
                sysFile.setFileUrl(fullFileUrl);
                sysFile.setSaveType(TableConstant.sysFile_saveType_youtubeLink_6);
                sysFile.setFileTypeIndex(TableConstant.youtueFileTypeIndex);
                if(Objects.nonNull(manager)) {
                    sysFile.setUploadUid(manager.getId());
                }else {
                    sysFile.setUploadUid(this.getGcUser().getId());
                }
                sysFile.setName(stringWidthConvertUtil.stringWidthConvert(sysFile.getName()));
                sysFile.setSysId(system.getId());
            }
            sysFileService.saveBatch(youtubeSysfileList);
        }catch (Exception e){
            return new Message().error().addData("error",e.getMessage());
        }
        return new Message().ok().addData("youtubeSysfile", youtubeSysfileList);
    }

    @ApiOperation(value = "视频", httpMethod = "GET")
    @GetMapping("/video/{id}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功", response = GcVideo.class)
    })
    public Message getVideo(@PathVariable("id") Integer vid, HttpServletRequest request) {
        GcMaster master = this.getMaster();
        if (Objects.isNull(master)&&null!=request.getHeader("masterId")){
            master = new GcMaster();
            master.setId(Integer.parseInt(request.getHeader("masterId")));
        }
        GcUser user = this.getGcUser();
        GcVideo video = videoService.getVideoById(vid);
        String url = sysFileService.getResFullUrl(video.getVideoFile(),request);
        video.setVideoFullUrl(url);
        List<GcEvent> list = null;
        if (video != null) list = eventService.getEventListByVid(vid,master.getId());
        List<Map<String, Object>> rateList = gcUserVideoActionService.countTypeRateForVideo(video.getId(),TableConstant.gcUserVideoAction_type_rate2);
        List<GcEvent> portalEventList =list.stream().filter(e->e.getUploadType().equals(TableConstant.COMMON_ONE)).collect(Collectors.toList());

        QueryWrapper<PtTags> tagsQueryWrapper = new QueryWrapper<>();
        tagsQueryWrapper.in("video_id",video.getId());
        List<PtTags> videoTagList = ptTagsService.list(tagsQueryWrapper);
        if (null!=videoTagList && TableConstant.COMMON_ZERO!=videoTagList.size()){
            List<String> videoTagText =  videoTagList.stream().map(PtTags::getTagText).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
            video.setCourseTags(JSONArray.parseArray(JSON.toJSONString(videoTagText)));
        }
        Message message = new Message().addData("vedio", video).addData("eventList", portalEventList).addData("rateList", rateList);
        SysFileCaption sysFileCaption = sysFileCaptionService.selectMainSysFile(vid);
        List<SysFileCaption> captionIdList = new ArrayList<>();
        message.addData("captionIdList",captionIdList);
        if (null!=sysFileCaption){
            SysFile  sysFile = sysFileCaptionService.selectSysFileCaption(sysFileCaption.getId());
            if (sysFile == null){
                if (null!=sysFileCaption.getYmTaskId()&&sysFileCaption.getYmCode().equals(TableConstant.COMMON_ZERO)) {
                    return message.ok("Subtitle acquisition in progress, please wait").addData("state",0);
                }
                return message.ok("Subtitle generation does not meet the requirements, please check the video information,errorCode:"+sysFileCaption.getYmCode()).addData("state",3);
            }
            if (sysFileCaption.getYmCode()!=null){
                if (!sysFileCaption.getYmCode().equals(TableConstant.COMMON_ZERO)){
                    return message.ok("Subtitle generation failed,errorCode:"+sysFileCaption.getYmCode()).addData("state",1);
                }
                if (sysFileCaption.getYmCode().equals(TableConstant.COMMON_ZERO)  && null!=sysFileCaption.getYmTaskId()){
                    message.ok("Subtitle generation succeeded").addData("state",2);
                }
            }
            captionIdList = sysFileCaptionService.selectSysFileCaptionId(vid);

            List<Integer> idList = captionIdList.stream().map(SysFileCaption::getCaptionFileId).collect(Collectors.toList());
            List<SysFile> fileList = sysFileService.listByIds(idList);
            Map<Integer,SysFile> sysFileMap = fileList.stream().collect(Collectors.toMap(SysFile::getId, (p) -> p));

            for(int i=0;i<captionIdList.size();i++){
                SysFileCaption fileCaption = captionIdList.get(i);
                if (null!=fileCaption&&null!=fileCaption.getCaptionFileId()){
                    String captionUrl = sysFileService.getResFullUrl(sysFileMap.get(fileCaption.getCaptionFileId()),request);
                    sysFileMap.get(fileCaption.getCaptionFileId()).setFullFileUrl(captionUrl);
                    captionIdList.get(i).setSysFileList(sysFileMap.get(fileCaption.getCaptionFileId()));
                }
            }
        }
        return message.ok().addData("captionIdList",captionIdList);
    }

    @ApiOperation(value = "删除视频", httpMethod = "DELETE")
    @DeleteMapping("/delVideo/{id}")
    public Message deleteVideo(@PathVariable("id") Integer vid,HttpServletRequest request) {
        return gvgMasterService.deleteVideo(vid,EnvType.GC.getCode(),null,null);
    }

    @ApiOperation(value = "保存事件", httpMethod = "POST")
    @PostMapping("/saveEvent")
    public Message saveEvent(@RequestBody @ApiParam(name = "视频事件", value = "事件信息") GcEvent gcEvent,HttpServletRequest request) {
        GcMaster master = this.getMaster();
        if ((null==master||null==master.getId())&&null!=request.getHeader("masterId")){
            master = new GcMaster();
            master.setId(Integer.parseInt(request.getHeader("masterId")));
        }
        if(Objects.nonNull(gcEvent.getFileId())){
            Message message = new Message();
            SysFile file = sysFileService.getById(gcEvent.getFileId());
            List<GcEvent> eventList = eventService.getEventListByVid(gcEvent.getVideoId(),master.getId());
            if(eventList!=null && eventList.size()>=TableConstant.COMMON_ZERO){
                Integer videoLong = file.getVideoLong();
                for(GcEvent event : eventList) {
                    if (event.getEventTime() >= videoLong) {
                        event.setEventTime(videoLong);
                    }
                }
                eventService.saveOrUpdateBatch(eventList);
                List<GcResource> resources = resourceService.getResByVid(gcEvent.getVideoId());
                if(CollectionUtils.isNotEmpty(resources)){
                    message.ok().addData("resourceNum",resources.size());
                }
                return message.ok("success").addData("sync", gcEvent).addData("eventNum", eventList.size());
            }
        }
        if (eventService.saveEvent(gcEvent,master.getId())) {
            if(null!=gcEvent.getLinkFileId()){
                SysFile file = sysFileService.getById(gcEvent.getLinkFileId());
                sysFileService.getResFullUrl(file,request);
                gcEvent.setLinkFile(file);
            }
            if (null!=gcEvent.getLinkVideoId()){
                GcVideo video = videoService.getById(gcEvent.getLinkVideoId());
                SysFile file = sysFileService.getById(video.getFileId());
                sysFileService.getResFullUrl(file,request);
                video.setVideoFile(file);
                gcEvent.setLinkVideo(video);
            }
            List<Integer> videoIds = new ArrayList<>();
            videoIds.add(gcEvent.getVideoId());
            Integer eventNum = eventService.countEventByVideoIds(videoIds);

            if (null!=gcEvent.getVideoId()){
                GcVideo video = videoService.getById(gcEvent.getVideoId());
                GcSubject subject = subService.getById(video.getSubId());
                gvgMasterService.saveInProgress(subject.getFid(),master.getId(),request);
            }
            return new Message().ok("添加成功！").addData("sync", gcEvent).addData("eventNum",eventNum);
        }
        return new Message().error();
    }

    @ApiOperation(value = "视频事件list", httpMethod = "GET")
    @GetMapping("/eventList/{vid}")
    public Message listEvent(@PathVariable("vid") Integer vid,HttpServletRequest request) {
        GcMaster master = this.getMaster();
        List<GcEvent> eventList = eventService.getEventListByVid(vid,master.getId());
        //筛选出门户上传的视频
        List<GcEvent> portalEventList =eventList.stream().filter(e->e.getUploadType().equals(TableConstant.COMMON_ONE)).collect(Collectors.toList());
        return new Message().ok().addData("list", portalEventList);
    }

    @ApiOperation(value = "删除事件", httpMethod = "DELETE")
    @DeleteMapping("/delEvent/{id}")

    public Message deleteEvent(@PathVariable("id") Integer eventId,HttpServletRequest request) {
        GcMaster master = this.getMaster();
        if ((null==master||null==master.getId())&&null!=request.getHeader("masterId")){
            master = new GcMaster();
            master.setId(Integer.parseInt(request.getHeader("masterId")));
        }
        if (null!=eventId){
            GcEvent gcEvent = eventService.getById(eventId);
            GcVideo video = videoService.getById(gcEvent.getVideoId());
            GcSubject subject = subService.getById(video.getSubId());
            gvgMasterService.saveInProgress(subject.getFid(),master.getId(),request);
        }
        if (eventService.removeById(eventId)) {
            return new Message().ok();
        }
        return new Message().error();
    }

    @ApiOperation(value = "删除分类", httpMethod = "DELETE")
    @DeleteMapping("/delSub/{id}")
    public Message deleteSub(@PathVariable("id") Integer subId,HttpServletRequest request) {
        GcMaster master = this.getMaster();
        Integer masterId = request.getIntHeader("masterId");
        if (Objects.isNull(master)&&Objects.nonNull(masterId)){
            master = new GcMaster();
            master.setId(masterId);
        }
        return gvgMasterService.deleteSub(subId,EnvType.GC.getCode(),master,null);
    }

    @ApiOperation(value = "事件类型", httpMethod = "GET")
    @GetMapping("/getEventType")
    public Message getEventType() {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Map<String, Object> resultMap2 = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        resultMap.put("eventTypeName", "选择题");
        resultMap.put("id", 1);

        resultMap2.put("eventTypeName", "描述题");
        resultMap2.put("id", 2);

        list.add(resultMap);
        list.add(resultMap2);

        return new Message().ok().addData("eventTypeList", list);
    }

    @ApiOperation(value = "保存事件", httpMethod = "POST")
    @PostMapping("/saveRes")
    public Message saveRes(@RequestBody @ApiParam(name = "视频资源", value = "视频信息") GcResource res,HttpServletRequest request) {
        ApiAssert.jsonValueIntegerIn(res.getResourceType(), "[1,2]", "类型值不合法！");
        if (resourceService.saveResource(res)){
            if (null!=res.getCourseTags()&&res.getCourseTags().size()!=0){
                QueryWrapper<PtTags> queryWrapper = new QueryWrapper<PtTags>();
                queryWrapper.eq("resource_id",res.getId());
                ptTagsService.remove(queryWrapper);

                Integer masterId = Integer.parseInt(request.getHeader("masterId"));
                List<String> tagList = res.getCourseTags().toJavaList(String.class);
                List<PtTags> ptTagsList = new ArrayList<>();
                Integer finalMasterId = masterId;
                tagList.forEach(i->{
                    PtTags newTags = new PtTags();
                    newTags.setMasterId(finalMasterId);
                    newTags.setTagText(i);
                    newTags.setResourceId(res.getId());
                    newTags.setType(TableConstant.COMMON_THREE);
                    newTags.setOrder(TableConstant.COMMON_ZERO);
                    ptTagsList.add(newTags);
                });
                ptTagsService.saveOrUpdateBatch(ptTagsList);
            }
            return new Message().ok("添加成功！").addData("sync", res);
        }

        return new Message().error();
    }

    @ApiOperation(value = "删除资源", httpMethod = "DELETE")
    @DeleteMapping("/delRes/{id}")
    public Message deleteRes(@PathVariable("id") Integer resId) {
        if (resourceService.removeById(resId)) return new Message().ok();
        return new Message().error();
    }

    @ApiOperation(value = "资源list", httpMethod = "GET")
    @GetMapping("/resList/{vid}")
    public Message resList(@PathVariable("vid") Integer vid,HttpServletRequest request) {
        Integer masterId = Integer.parseInt(request.getHeader("masterId"));
        List<GcResource> list = resourceService.getResByVid(vid);
        QueryWrapper<PtTags> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("master_id",masterId);
        queryWrapper.in("type",TableConstant.COMMON_THREE);
        Map<Integer,List<PtTags>> listMap =ptTagsService.list(queryWrapper).stream().collect(Collectors.groupingBy(PtTags::getResourceId));
        list.forEach(i->{
            if (null!=listMap.get(i.getId())){
                JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(listMap.get(i.getId()).stream().map(PtTags::getTagText).collect(Collectors.toList())));
                i.setCourseTags(jsonArray);
            }
        });

        return new Message().ok().addData("list", list);
    }

    @ApiOperation(value = "保存配置", httpMethod = "POST")
    @PostMapping("/saveConfig")
    public Message saveConfig(@RequestBody JSONObject objectParams) {
        Integer activeConfig = objectParams.getInteger("activeConfig");

        GcMaster master=this.getMaster();
        if(activeConfig!=null) {
            ApiAssert.jsonValueIntegerIn(activeConfig, "[0,1]", "值为0或者1");
            if(master.getExtVar()==null) {
                master.setExtVar(new JSONObject());
            }
            JSONObject config=master.getExtVar();
            config.put("activeConfig", activeConfig);
            GcMaster updateMaster=new GcMaster();
            updateMaster.setId(master.getId());
            updateMaster.setManagerId(master.getManagerId());
            updateMaster.setExtVar(config);
            master.setExtVar(config);
            masterService.setMaster(updateMaster);
        }

        return new Message().ok();

    }
    @ApiOperation(value = "保存ptOssConfig", httpMethod = "Post")
    @PostMapping("/savePtConfig")
    public Message savePtConfig(@RequestBody PtLoginConfig loginConfig){
        loginConfig.setMasterId(this.getMaster().getId());
        loginConfig.setCreateTime(new Date());
        loginConfig.setUpdateTime(new Date());
        loginConfig.setCreateBy(this.getManager().getUsername());
        loginConfig.setUpdateBy(this.getManager().getUsername());
        ptLoginConfigService.saveOrUpdate(loginConfig);
        return new Message().ok().addData("loginConfig",loginConfig);
    }

    @ApiOperation(value = "获取ptOssConfig", httpMethod = "GET")
    @GetMapping("/getPtConfig")
    public Message getPtConfig(){
        GcMaster master = this.getMaster();
        QueryWrapper<PtLoginConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("master_id",master.getId());
        PtLoginConfig loginConfig = ptLoginConfigService.getOne(queryWrapper);
        return new Message().ok().addData("ptConfig",loginConfig);
    }
}
