package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.constant.EventUnifyType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.entity.GcMasterMessage;
import com.threeatom.guidecore.mapper.GcMasterMessageMapper;
import com.threeatom.guidecore.service.GcMasterMessageService;
import com.threeatom.system.service.SysFileService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GcMasterMessageServiceImpl extends ServiceImpl<GcMasterMessageMapper, GcMasterMessage>
        implements GcMasterMessageService {

    @Autowired private SysFileService sysFileService;

    @Override
    public void saveMasterMessage(GcMasterMessage masterMessage) {
        saveOrUpdate(masterMessage);
    }

    @Override
    public Integer getUnReadMessage(Integer masterId, Integer userId, Integer type) {
        QueryWrapper<GcMasterMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("target_user_id", userId);
        queryWrapper.eq("read_state", TableConstant.gcMasterMessage_readState_0Unread);
        if (type == 1) queryWrapper.eq("event_type", EventUnifyType.VIDEO_2);
        return this.count(queryWrapper);
    }

    @Override
    public boolean deleteAnswerMessage(GcMasterMessage masterMessage) {
        QueryWrapper<GcMasterMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", masterMessage.getUserId());
        queryWrapper.eq("master_id", masterMessage.getMasterId());
        queryWrapper.eq("target_user_id", masterMessage.getTargetUserId());
        queryWrapper.eq("user_answer_id", masterMessage.getUserAnswerId());
        return this.remove(queryWrapper);
    }

    @Override
    public void saveBatchMasterMessage(List<GcMasterMessage> masterMessageList) {
        saveBatch(masterMessageList);
    }

    // type为1 video未读，其他为所有未读？
    @Override
    public List<Map<String, Object>> getUnReadMessageByUserIds(
            List<Integer> userIds, Integer userId, Integer masterId, Integer type) {
        return this.baseMapper.selectGetUnReadMessageByUserIds(userIds, userId, masterId, type);
    }

    @Override
    public List<Integer> getMasterMessageResIdsById(
            Integer studentId, Integer userId, Integer masterId) {
        return this.baseMapper.selectGetMasterMessageResIdsById(studentId, userId, masterId);
    }

    // 设置已读通知type 1.video已读 2.全部已读
    @Override
    public boolean setIsRead(Integer studentId, Integer userId, Integer masterId, Integer type) {
        return this.baseMapper.selectSetIsRead(studentId, userId, masterId, type);
    }

    @Override
    public List<Map<String, Object>> getUnReadMessageCountByResourceIds(
            List<Integer> ResourceIds, Integer userId, Integer masterId, Integer teacherId) {
        return this.baseMapper.getUnReadMessageCountByResourceIds(
                ResourceIds, userId, masterId, teacherId);
    }

    @Override
    public List<Map<String, Object>> getAllReadMessageCountByResourceIds(
            List<Integer> ResourceIds, Integer userId, Integer masterId, Integer teacherId) {
        return this.baseMapper.getAllReadMessageCountByResourceIds(
                ResourceIds, userId, masterId, teacherId);
    }

    @Override
    public List<Map<String, Object>> getAllUnReadMessage(Integer masterId, Integer userId) {
        return this.baseMapper.getAllUnReadMessage(masterId, userId);
    }

    @Override
    public List<GcMasterMessage> getLastMessageByStudentIds(
            List<Integer> studentIds, Integer teacherId, Integer masterId) {
        return this.baseMapper.selectGetLastMessageByStudentIds(studentIds, teacherId, masterId);
    }

    @Override
    public List<Map<String, Object>> getMasterMessageListDetailByMessageIds(
            List<Integer> messageIds) {
        return this.baseMapper.selectGetMasterMessageListDetailByMessageIds(messageIds);
    }

    @Override
    public List<Map<String, Object>> getAllEventUnMessageDetail(
            Integer masterId, Integer userId, Integer targetUserId) {
        return this.baseMapper.selectGetAllEventUnMessageDetail(masterId, userId, targetUserId);
    }

    @Override
    public List<Map<String, Object>> getAllSubUnMessageDetail(
            Integer masterId, Integer userId, Integer targetUserId) {
        return this.baseMapper.selectGetAllSubUnMessageDetail(masterId, userId, targetUserId);
    }

    @Override
    public boolean setAnswerIsRead(
            List<Integer> eventIds, Integer masterId, Integer userId, Integer targetUserId) {
        return this.baseMapper.selectSetAnswerIsRead(eventIds, masterId, userId, targetUserId);
    }

    @Override
    public boolean setUserResourceIsReadByMessageIds(List<Integer> messageIds, Integer readMarker) {
        return this.baseMapper.selectSetResourceIsRead(messageIds, readMarker);
    }

    @Override
    public List<Map<String, Object>> getEventAnswerUnReadList(
            List<Integer> eventIds, Integer studentId, Integer masterId, Integer teacherId) {
        return this.baseMapper.selectGetEventAnswerUnReadList(eventIds, studentId, masterId, teacherId);
    }
}
