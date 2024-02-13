package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.GcUserNote;
import com.threeatom.guidecore.mapper.GcUserNoteMapper;
import com.threeatom.guidecore.service.GcUserNoteService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-12-11
 */
@Service
public class GcUserNoteServiceImpl extends ServiceImpl<GcUserNoteMapper, GcUserNote>
        implements GcUserNoteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserNoteServiceImpl.class);

    @Override
    public List<GcUserNote> selectNoteByUserId(
            Integer userId, Integer videoId, Integer masterId, HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.baseMapper.selectNoteByUserId(userId, videoId, masterId);
    }

    @Override
    public Integer countNoteByNote(GcUserNote note) {
        QueryWrapper<GcUserNote> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", note.getId());
        queryWrapper.eq("user_id", note.getUserId());
        return this.count(queryWrapper);
    }

    @Override
    public Integer countNoteForVideo(Integer videoId, Integer userId, Integer masterId) {
        return this.baseMapper.countNoteForVideoAndUser(videoId, userId, masterId);
    }

    @Override
    public List<GcUserNote> selectNoteListByUserMaster(
            Integer userId, Integer masterId, HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.baseMapper.selectNoteListByUserMaster(userId, masterId);
    }
}
