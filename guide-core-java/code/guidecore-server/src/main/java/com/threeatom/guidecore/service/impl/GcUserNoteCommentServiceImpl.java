package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.controller.user.vo.UserNoteCommentVo;
import com.threeatom.guidecore.entity.GcUserNoteComment;
import com.threeatom.guidecore.mapper.GcUserNoteCommentMapper;
import com.threeatom.guidecore.service.GcUserNoteCommentService;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author huangpei
 * @title: GcUserNoteCommentServiceImpl
 * @projectName guidecore
 * @description: TODO
 * @date 2021/10/27/02714:54
 */
@Service
public class GcUserNoteCommentServiceImpl extends ServiceImpl<GcUserNoteCommentMapper, GcUserNoteComment> implements GcUserNoteCommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserNoteServiceImpl.class);

    @Autowired
    private SysFileService sysFileService;

    @Override
    public List<UserNoteCommentVo> selectNoteComment(Integer eventId, Integer targetUserId,HttpServletRequest request) {
        Integer masterId = request.getIntHeader("masterId");
        List<UserNoteCommentVo> list = this.baseMapper.selectUserNoteComment(eventId,targetUserId,masterId);
        for (UserNoteCommentVo commentVo : list) {
            SysFile avatarFile = commentVo.getUserAvatarFile();
            if (avatarFile!=null){
                commentVo.setUserAvatarUrl(sysFileService.getResFullUrl(avatarFile,request));
            }

            if (commentVo.getResFile()!=null){
                sysFileService.getResFullUrlSaveType2(commentVo.getResFile());
                commentVo.getResFile().setSnapshotUrl(sysFileService.getVideoSnapshotUrl(commentVo.getResFile()));
            }
        }
        return list;
    }

    @Override
    public Integer selectCommentNum(Integer eventId, Integer targetUserId,Integer masterId) {
        return this.baseMapper.selectCommentNum(eventId,targetUserId,masterId);
    }
    @Override
    public List<GcUserNoteComment> selectCommentNumList(Integer eventId, List<Integer> targetUserId, Integer masterId) {
        return this.baseMapper.selectCommentNumList(eventId,targetUserId,masterId);
    }

}
