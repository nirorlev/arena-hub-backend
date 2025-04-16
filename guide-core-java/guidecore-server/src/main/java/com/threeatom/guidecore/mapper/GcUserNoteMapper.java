package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUserNote;
import java.util.List;

public interface GcUserNoteMapper extends BaseMapper<GcUserNote> {

    List<GcUserNote> selectNoteByUserId(Integer userId, Integer videoId, Integer masterId);

    Integer countNoteForVideoAndUser(Integer videoId, Integer userId, Integer masterId);

    List<GcUserNote> selectNoteListByUserMaster(Integer userId, Integer masterId);
}
