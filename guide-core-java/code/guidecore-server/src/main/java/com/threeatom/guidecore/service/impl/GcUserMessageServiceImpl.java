package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.GcUserMessage;
import com.threeatom.guidecore.mapper.GcUserMessageMapper;
import com.threeatom.guidecore.service.GcUserMessageService;
import com.threeatom.system.service.SysFileService;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-12-09
 */
@Service
public class GcUserMessageServiceImpl extends ServiceImpl<GcUserMessageMapper, GcUserMessage>
        implements GcUserMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserMessageServiceImpl.class);

    @Autowired private SysFileService sysFileService;

    @Override
    @Transactional
    public boolean saveUserMessage(GcUserMessage userMessage) {
        return this.save(userMessage);
    }

    @Override
    public List<GcUserMessage> getUnReadMessage(Integer masterId, Integer userId) {
        QueryWrapper<GcUserMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("target_user_id", userId);
        queryWrapper.eq("read_state", 0);
        return this.list(queryWrapper);
    }

    @Override
    public List<GcUserMessage> getMessageListByUserIds(List<Integer> userIds, Integer masterId) {
        QueryWrapper<GcUserMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("read_state", 0);
        queryWrapper.in("user_id", userIds);
        queryWrapper.orderByDesc("create_time");
        return this.list(queryWrapper);
    }

    @Override
    public List<GcUserMessage> getMessageListByTargetUserIdAndUserId(
            Integer targetUserId,
            Integer userId,
            Integer offset,
            Integer page,
            Integer masterId,
            HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.baseMapper.selectGetMessageListByTargetUserIdAndUserId(
                targetUserId, userId, (page - 1) * offset, offset, masterId);
    }

    @Override
    public Boolean setIsReadByTargetUserId(Integer targetUserId, Integer userId) {
        return this.baseMapper.setIsReadByTargetUserId(targetUserId, userId);
    }

    @Override
    public GcUserMessage getNewMessage(Integer targetUserId, Integer userId, Integer masterId) {
        return this.baseMapper.selectGetNewMessage(targetUserId, userId, masterId);
    }

    @Override
    public List<Map<String, Object>> getMessageNumByTeacherIdAndUserIds(
            Integer teacherId, List<Integer> userIds, String order) {
        // TODO Auto-generated method stub
        return this.baseMapper.selectUsersMessageByTeacherIdAndUserIds(teacherId, userIds, order);
    }

    @Override
    public int update(GcUserMessage userMessage) {
        return this.baseMapper.updateById(userMessage);
    }
}
