package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.user.vo.MessageFIlterVo;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.GcUserEventResource;
import com.threeatom.guidecore.mapper.GcUserEventResourceMapper;
import com.threeatom.guidecore.service.GcEventService;
import com.threeatom.guidecore.service.GcMasterMessageService;
import com.threeatom.guidecore.service.GcUserAnswerService;
import com.threeatom.guidecore.service.GcUserEventResourceService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import com.threeatom.system.service.SysSystemService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class GcUserEventResourceServiceImpl
        extends ServiceImpl<GcUserEventResourceMapper, GcUserEventResource>
        implements GcUserEventResourceService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GcUserEventResourceServiceImpl.class);

    @Autowired private GcVideoService videoService;

    @Autowired private GcEventService eventService;

    @Autowired private GcUserService userService;
    @Autowired private SysFileService sysFileService;
    @Autowired private GcUserAnswerService userAnswerService;
    @Autowired private GcMasterMessageService masterMessageService;

    @Autowired private SysSystemService systemService;
    @Autowired private Environment env;

    @Override
    public List<GcUserEventResource> getEventResListForWorkBook(
            Integer eventId,
            Integer userId,
            Integer studentId,
            Integer masterId,
            HttpServletRequest request,
            Integer envType) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcUserEventResource> list =
                this.baseMapper.getEventResListForWorkBook(eventId, userId, studentId, masterId, envType);
        for (GcUserEventResource resource : list) {
            if ((null != studentId && studentId.equals(resource.getThisUser().getId()))
                    || (null == studentId && userId.equals(resource.getThisUser().getId()))) {
                resource.setIsTeacher(TableConstant.COMMON_ZERO);
            } else {
                resource.setIsTeacher(TableConstant.COMMON_ONE);
            }
            if (resource.getResFile() != null) {
                sysFileService.getResFullUrl(resource.getResFile(), request); // 完整路径
                resource
                        .getResFile()
                        .setSnapshotUrl(sysFileService.getVideoSnapshotUrl(resource.getResFile())); // 缩略图
            }

            if (resource.getThisUser() != null
                    && resource.getThisUser().getInfo() != null
                    && resource.getThisUser().getInfo().getAvatarFile() != null)
                sysFileService.getResFullUrlSaveType2(resource.getThisUser().getInfo().getAvatarFile());

            if (resource.getTargetUser() != null
                    && resource.getTargetUser().getInfo() != null
                    && resource.getTargetUser().getInfo().getAvatarFile() != null)
                sysFileService.getResFullUrlSaveType2(resource.getTargetUser().getInfo().getAvatarFile());
        }
        return list;
    }

    @Override
    public boolean saveEventAction(GcUserEventResource UserEventResource) {
        if (UserEventResource.getTargetId() != null && UserEventResource.getTargetId() == 0)
            UserEventResource.setTargetId(null);
        return this.saveOrUpdate(UserEventResource);
    }

    @Override
    public List<GcUserEventResource> getEventResourceByEventIdAndUserId(
            Integer eventId, Integer userId) {
        return this.baseMapper.selectGetEventResourceByEventIdAndUserId(eventId, userId);
    }

    @Override
    public List<GcUserEventResource> selectGetEventResourceByEventIdAndTargetUserId(
            Integer eventId, Integer userId, Integer commentResourceFileId) {
        return this.baseMapper.selectGetEventResourceByEventIdAndTargetUserId(
                eventId, userId, commentResourceFileId);
    }

    @Override
    public List<Map<String, Object>> getUsersUploadResNumByUserIdsAndSubIds(
            List<Integer> subIds, List<Integer> userIds, String order) {
        return this.baseMapper.selectUsersUploadResNumByUserIdsAndSubIds(subIds, userIds, order);
    }

    @Override
    public Integer deleteResourceFIle(
            Integer commentResourceFileId, Integer userId, Integer masterId) {
        QueryWrapper<GcUserEventResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("id", commentResourceFileId);
        queryWrapper.eq("master_id", masterId);
        return this.baseMapper.delete(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> getUserListForRespond(
            List<Integer> userIds, Integer userId, Integer masterId) {
        if (userIds == null) userIds = new ArrayList<Integer>();
        if (userIds.size() == 0) {
            userIds.add(0);
        }

        return this.baseMapper.selectGetUserListForRespond(userIds, userId, masterId);
    }

    @Override
    public List<Map<String, Object>> getALLResourceListByEventIds(
            List<Integer> eventId, Integer studentId, Integer teacherId) {
        return this.baseMapper.getALLResourceListByEventIds(eventId, studentId, teacherId);
    }

    @Override
    public Integer countALLResourceListByGcMasterMessageTargetUserId(
            MessageFIlterVo messageFIlterVo) {
        return this.baseMapper.countALLResourceListByGcMasterMessageTargetUserId(messageFIlterVo);
    }

    @Override
    public Integer countAnswerMessageListByGcMasterMessageTargetUserId(
            MessageFIlterVo messageFIlterVo) {
        return this.baseMapper.countAnswerMessageListByGcMasterMessageTargetUserId(messageFIlterVo);
    }

    @Override
    public List<Map<String, Object>> getAnswerMessageListByGcMasterMessageTargetUserId(
            MessageFIlterVo messageFIlterVo, SysSystem sys, HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }

        List<Map<String, Object>> l =
                this.baseMapper.getAnswerMessageListByGcMasterMessageTargetUserId(
                        messageFIlterVo, pageParam);
        // 组装头像
        if (l != null && l.size() > 0) {
            for (Map<String, Object> videoResource : l) {
                // 组装头像
                SysFile userFile = new SysFile();
                userFile.setFileUrl(
                        videoResource.containsKey("file_url") ? (String) videoResource.get("file_url") : "");
                userFile.setSaveType(
                        videoResource.containsKey("save_type") ? (Integer) videoResource.get("save_type") : 2);
                videoResource.put("userFullFileUrl", sysFileService.getResFullUrl(userFile, request));
            }
        }
        return l;
    }

    @Override
    public List<GcUserEventResource> selectUnCheckedTeacherMessage(
            Integer userId, List<Integer> eventIds, Integer masterId) {
        return this.baseMapper.selectUnCheckedTeacherMessage(userId, eventIds, masterId);
    }

    @Override
    public List<GcUserEventResource> selectUnCheckedStudentMessage(
            Integer studentId, List<Integer> eventIds, Integer userId, Integer masterId) {
        return this.baseMapper.selectUnCheckedStudentMessage(studentId, eventIds, userId, masterId);
    }

}
