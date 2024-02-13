package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserNote;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-12-11
 */
public interface GcUserNoteService extends IService<GcUserNote> {
    Integer countNoteForVideo(Integer videoId, Integer userId, Integer masterId);

    List<GcUserNote> selectNoteByUserId(
            Integer userId, Integer videoId, Integer masterId, HttpServletRequest request);

    Integer countNoteByNote(GcUserNote note);

    List<GcUserNote> selectNoteListByUserMaster(
            Integer userId, Integer masterId, HttpServletRequest request);
}
