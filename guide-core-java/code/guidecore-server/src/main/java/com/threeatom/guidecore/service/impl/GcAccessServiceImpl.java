package com.threeatom.guidecore.service.impl;

import static com.threeatom.utils.ToolUtil.parseToJsonArray;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.config.MondayConfiguration;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.user.vo.MondayApiVo;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcAccessMapper;
import com.threeatom.guidecore.mapper.NewUiGcSubjectMapper;
import com.threeatom.guidecore.service.*;
import com.threeatom.guidecore.util.I18NUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GcAccessServiceImpl extends ServiceImpl<GcAccessMapper, GcAccess>
        implements GcAccessService {

    @Autowired GcUserAccessService userAccessService;

    @Resource private NewUiGcSubjectMapper newUiGcSubjectMapper;
    @Autowired private GcUserVideoActionService videoActionService; // 用户视频操作--查询评论、点赞、星级评价
    @Autowired private GcMasterService gcMasterService;
    @Autowired private GcUserInfoService gcUserInfoService;
    @Autowired private MondayConfiguration mondayConfiguration;
    @Autowired @Lazy private GcUserService gcUserService;
    @Autowired
    private GcContentGroupCourseAssignmentService contentGroupCourseAssignmentService;

    @Override
    public List<GcAccess> getAccessByMasterIdAndCode(GcAccess access) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("master_id", access.getMasterId());
        queryWrapper.eq("code", access.getCode());
        return this.list(queryWrapper);
    }

    @Override
    public GcAccess selectFreeCodeByMaster(Integer masterId) {
        QueryWrapper queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("code_type", TableConstant.COMMON_TWO);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<GcAccess> selectMasterIdAndIds(Integer masterId, List<Integer> ids) {
        QueryWrapper queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.in("id", ids);
        return this.list(queryWrapper);
    }

    @Override
    public List<GcAccess> selectAccessByCodeAndMasterId(List<String> codeList, Integer masterId) {
        return this.baseMapper.selectAccessByCodeAndMasterId(codeList, masterId);
    }

    @Override
    public void insertOrUpdateList(List<GcAccess> accessList) {
        this.baseMapper.insertOrUpdateList(accessList);
    }

    @Override
    public List<GcAccess> selectAccessByIds(List<Integer> ids) {
        return this.baseMapper.selectAccessByIds(ids);
    }

    @Override
    public List<GcAccess> selectAccessBySubId(Integer subId, Integer masterId) {
        return this.baseMapper.selectAccessBySubId(subId, masterId);
    }

    @Override
    public void deleteSubIdAccess(Integer masterId, Integer subId) {
        List<GcAccess> accessList = this.selectAccessBySubId(subId, masterId);
        for (GcAccess access : accessList) {
            if (null != access.getSubjectJson() && access.getSubjectJson().contains(subId)) {
                while (access.getSubjectJson().contains(subId)) {
                    access.getSubjectJson().remove(subId);
                }
            }
            if (null != access.getMustSubjectJson() && access.getMustSubjectJson().contains(subId)) {
                while (access.getMustSubjectJson().contains(subId)) {
                    access.getMustSubjectJson().remove(subId);
                }
            }
            if (null != access.getMaySubjectJson() && access.getMaySubjectJson().contains(subId)) {
                while (access.getMaySubjectJson().contains(subId)) {
                    access.getMaySubjectJson().remove(subId);
                }
            }
        }
        if (null != accessList && accessList.size() != TableConstant.COMMON_ZERO) {
            contentGroupCourseAssignmentService.removeByMasterAndCourseId(masterId, subId);
            this.insertOrUpdateList(accessList);
        }
    }

    @Override
    public List<GcAccess> getAllAccessByMasterId(Integer masterId) {
        return this.baseMapper.getAllAccessByMasterId(masterId);
    }

    @Override
    @Transactional
    public boolean addAccess(GcAccess access) {
        userAccessService.clearCacheAll();
        this.saveOrUpdate(access);

        if (access.getId() != null) {

            QueryWrapper<GcUserAccess> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("access_id", access.getId());
            List<GcUserAccess> list = userAccessService.list(queryWrapper);
            List<Integer> userAccessIds =
                    list.stream().map(GcUserAccess::getId).collect(Collectors.toList());
            if (!userAccessIds.isEmpty()) {
                userAccessService.updateUserAccessPermission(userAccessIds, access);
            }
        }

        return true;
    }

    @Override
    public List<GcAccess> findAccessListByMasterId(Integer masterId) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.orderByDesc("subject_json::jsonb");
        queryWrapper.isNotNull("group_name");
        return this.list(queryWrapper);
    }

    @Override
    public List<GcAccess> getAdminAccessListByMasterId(Integer masterId) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("master_id", masterId);
        //        queryWrapper.isNull("package_name");
        return this.list(queryWrapper);
    }

    @Override
    public GcAccess getAccessByName(String accessName, Integer masterId) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("code", accessName);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<GcAccess> listAccess(String name, Integer masterId, Integer userId) {
        return this.baseMapper.listAccess(name, masterId, userId);
    }

    @Override
    public List<GcAccess> getTeamAccessList(
            String name, Integer masterId, Integer userId, HttpServletRequest request) {
        return this.baseMapper.getTeamAccessList(name, masterId, userId);
    }

    public List<GcAccess> getTeamAccessSubjectNumAdminList(
            String name,
            Integer masterId,
            Integer userId,
            List<Integer> availableTypeFour,
            List<Integer> availableTypeOneAndThree,
            List<Integer> subIds) {
        List<GcAccess> list = this.baseMapper.getTeamAccessSubjectNumAdminList(name, masterId, userId);

        for (GcAccess access : list) {
            List<Integer> mustAssignedCourses = contentGroupCourseAssignmentService.getMustCoursesContentGroupAssignmentIds(access.getId());
            List<Integer> optionalAssignedCourses = contentGroupCourseAssignmentService.getOptionalCoursesContentGroupAssignmentIds(access.getId());

            int coursesNum = access.getSubjectNum() + availableTypeOneAndThree.size();
            long coursesCountToExclude = Stream.concat(availableTypeFour.stream(), availableTypeOneAndThree.stream())
                .filter(courseId -> mustAssignedCourses.contains(courseId) || optionalAssignedCourses.contains(courseId))
                .count();

            access.setSubjectNum((int) (coursesNum - coursesCountToExclude));
        }
        return list;
    }

    @Override
    public List<GcAccess> getTeamAccessSubjectNumList(
            String name,
            Integer masterId,
            Integer userId,
            List<Integer> subIds,
            HttpServletRequest request) {
        List<GcAccess> list = this.baseMapper.getTeamAccessSubjectNumList(name, masterId, userId);
        for (GcAccess access : list) {
            List<Integer> mustAssignedCourses = contentGroupCourseAssignmentService.getMustCoursesContentGroupAssignmentIds(access.getId());
            List<Integer> optionalAssignedCourses = contentGroupCourseAssignmentService.getOptionalCoursesContentGroupAssignmentIds(access.getId());
            long coursesCountToExclude = subIds.stream()
                .filter(courseId -> mustAssignedCourses.contains(courseId) || optionalAssignedCourses.contains(courseId))
                .count();

            access.setSubjectNum((int) (access.getSubjectNum() - coursesCountToExclude));
        }
        return list;
    }

    @Override
    public List<GcAccess> selectAccessLevel0(Integer masterId, Integer userId) {
        List<GcAccess> gcAccessList = this.baseMapper.selectAccessLevel0(masterId, userId);
        gcAccessList = listToTree(gcAccessList);
        return gcAccessList;
    }

    private GcAccess createTeacherContentGroup(String code, int roleType, Integer masterId) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code","teacherPT");
        queryWrapper.eq("role_type",TableConstant.COMMON_ZERO);
        queryWrapper.eq("master_id", masterId);
        return getOne(queryWrapper);
    }

    private List<GcAccess> listToTree(List<GcAccess> gcAccessList) {
        List<GcAccess> newAccessList = new ArrayList<>();
        Map<String, GcAccess> gcAccessMap =
                gcAccessList.stream().collect(Collectors.toMap(GcAccess::getCode, (p) -> p));

        for (GcAccess access : gcAccessList) {
            if (null == access.getParentCode() || null == gcAccessMap.get(access.getParentCode())) {
                newAccessList.add(accessTree(access, gcAccessList));
            }
        }
        return newAccessList;
    }

    @Transactional
    @Override
    public GcAccess getTeacherStudentContentGroup(List<Integer> courseIds, Integer masterId) {
        GcAccess teacherContentGroup =
            createTeacherContentGroup("teacherPT", TableConstant.COMMON_ZERO, masterId);
        GcAccess studentAccess = createTeacherContentGroup("studentPT", TableConstant.COMMON_ONE, masterId);

        if (teacherContentGroup == null || studentAccess == null) {
            if (teacherContentGroup == null) {
                teacherContentGroup = createContentGroup(courseIds, masterId);
                addAccess(teacherContentGroup);
            }
            if (studentAccess == null) {
                studentAccess = createStudentContentGroup(courseIds, teacherContentGroup, masterId);
                addAccess(studentAccess);
            } else if (studentAccess.getAdminId() == null) {
                studentAccess.setAdminId(teacherContentGroup.getId());
                updateById(studentAccess);
            }

            return studentAccess;
        }

        teacherContentGroup.setSubjectJson(parseToJsonArray(courseIds));
        studentAccess.setSubjectJson(parseToJsonArray(courseIds));
        updateById(teacherContentGroup);
        updateById(studentAccess);
        return studentAccess;
    }

    private GcAccess createStudentContentGroup(List<Integer> subjectIdList, GcAccess gcAccess, Integer masterId) {
        GcAccess studentAccess = new GcAccess();
        studentAccess.setMasterId(masterId);
        studentAccess.setSubjectJson(parseToJsonArray(subjectIdList));
        studentAccess.setCode("studentPT");
        studentAccess.setCodeType(TableConstant.COMMON_ZERO);
        studentAccess.setFreeFlag(TableConstant.COMMON_ZERO);
        studentAccess.setPackageShowFlag(TableConstant.COMMON_ONE);
        studentAccess.setRoleType(TableConstant.COMMON_ONE);
        studentAccess.setAdminId(gcAccess.getId());
        studentAccess.setId(null);
        return studentAccess;
    }

    private GcAccess createContentGroup(List<Integer> subjectIdList, Integer masterId) {
        GcAccess gcAccess = new GcAccess();
        gcAccess.setMasterId(masterId);
        gcAccess.setRoleType(TableConstant.COMMON_ZERO);
        gcAccess.setSubjectJson(parseToJsonArray(subjectIdList));
        gcAccess.setCodeType(TableConstant.COMMON_ZERO);
        gcAccess.setFreeFlag(TableConstant.COMMON_ZERO);
        gcAccess.setPackageShowFlag(TableConstant.COMMON_ONE);
        gcAccess.setCode("teacherPT");
        return gcAccess;
    }

    private GcAccess accessTree(GcAccess gcAccess, List<GcAccess> gcAccessList) {
        for (GcAccess access : gcAccessList) {
            if (null != access.getParentCode() && access.getParentCode().equals(gcAccess.getCode())) {
                if (null == gcAccess.getAccessList()) {
                    gcAccess.setAccessList(new ArrayList<>());
                }
                gcAccess.getAccessList().add(accessTree(access, gcAccessList));
            }
        }
        return gcAccess;
    }

    @Override
    public List<GcAccess> listAllAccess(Map<String, Object> params, HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }

        return this.baseMapper.listAllAccess(params);
    }

    @Override
    public Integer deleteAccess(Integer id) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("id", id);
        return this.baseMapper.delete(queryWrapper);
    }

    @Transactional
    @Override
    public Message checkUserAccess(
            Integer masterId,
            Integer userId,
            String accessCode,
            Integer inviteUserId,
            GcUser user,
            HttpServletRequest request) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("master_id", masterId).eq("code", accessCode);
        GcAccess access = this.getOne(queryWrapper);
        if (access == null) throw new SystemException(I18NUtil.get("guidecore.master.codeError"));

        QueryWrapper<GcUserAccess> queryWrapper2 = new QueryWrapper<GcUserAccess>();
        queryWrapper2.eq("master_id", masterId).eq("user_id", userId).eq("access_id", access.getId());
        GcUserAccess userAccess = userAccessService.getOne(queryWrapper2);
        Message message = new Message().ok();
        if (userAccess == null) {
            userAccess = new GcUserAccess();
            userAccess.setUserId(userId);
            userAccess.setMasterId(masterId);
            userAccess.setAccessId(access.getId());
            userAccess.setAccess(access);
            // 初始化3个表
            userAccessService.createUserAccess(userAccess);
            // 发送邮件
            if (null != user) {
                GcMaster gcMaster = gcMasterService.getById(masterId);
                // String textBody = I18NUtil.get("guidecore.master.portalEmailText") +
                // gcMaster.getContext();
                String email = user.getUsername();
                List<String> emailList =
                        JSONObject.parseArray(gcMaster.getEmailCc().toJSONString(), String.class);
                emailList.add(email);
                String htmlBody =
                        I18NUtil.get("guidecore.master.email")
                                .replace("{user.name}", user.getFirstName() + user.getLastName())
                                .replace("{email}", user.getUsername())
                                .replace("{url}", request.getHeader("origin"));
            }
            if (mondayConfiguration.getCallMondayApiFlag().equals(TableConstant.COMMON_ONE)) {
                GcUser gcUser = gcUserService.getById(userId);
                GcUserInfo gcUserInfo = gcUserInfoService.getById(gcUser.getInfoId());
                GcMaster gcMaster = gcMasterService.getById(masterId);
                //					第一次加入新的门户在monday加一条记录
                MondayApiVo mondayApiVo = new MondayApiVo();
                mondayApiVo.setEmail(gcUser.getUsername());
                mondayApiVo.setCodeName(access.getCode());
                mondayApiVo.setUsername(gcUserInfo.getFirstName() + " " + gcUserInfo.getLastName());
                mondayApiVo.setPortalName(gcMaster.getContext());
                mondayApiVo.setPaidFlag(0);
                mondayApiVo.setBoardId(gcMaster.getBoardId());
            }
            message.addData("ifNewMaster", true);
        }
        if (inviteUserId != null && inviteUserId != 0) {
            GcUserAccess inviteUserAccess =
                    userAccessService.getUserAccessByMasterIdAndUserId(masterId, inviteUserId);
        }

        return message.addData("access", access).addData("userAccess", userAccess);
    }

    @Override
    public GcAccess getAccessById(Integer id) {
        return getById(id);
    }

    @Override
    public List<GcAccess> getAccessBySubjectId(Integer masterId, Integer id) {
        return this.baseMapper.getAccessBySubjectId(masterId, id);
    }

    public List<GcAccess> getAccessByChannelId(Integer masterId, Integer channelId) {
        return this.baseMapper.getAccessByChannelId(masterId, channelId);
    }

    @Override
    public List<GcAccess> getAccessByAdminId(Integer adminId) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("admin_id", adminId);
        return this.list(queryWrapper);
    }

    @Override
    public List<GcAccess> getAccessListByAdminId(Integer adminId) {
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<GcAccess>();
        queryWrapper.eq("admin_id", adminId);
        return this.list(queryWrapper);
    }

    @Override
    public Map<String, Long> getALlAccessCodeNumsByMasterId(Integer masterId) {
        return this.baseMapper.getALlAccessCodeNumsByMasterId(masterId);
    }

    @Override
    public List<GcAccess> getAllPackage(
            Integer masterId, PageParam pageParam, List<Integer> packageIdList, List<Integer> idList) {
        Map<String, Object> map = new HashMap<>();
        Message message = new Message();
        Integer showFlag = TableConstant.COMMON_ZERO;
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcAccess> allPackage =
                this.baseMapper.getAllPackage(masterId, showFlag, packageIdList, idList);
        return allPackage;
    }

    public List<GcAccess> selectAllPackage(Integer masterId) {
        List<GcAccess> allPackage = this.baseMapper.selectAllPackage(masterId);
        return allPackage;
    }
}
