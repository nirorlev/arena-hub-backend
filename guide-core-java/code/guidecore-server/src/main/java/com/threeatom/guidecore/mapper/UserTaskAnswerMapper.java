
package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.UserTaskAnswer;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserTaskAnswerMapper extends BaseMapper<UserTaskAnswer> {
    List<UserTaskAnswer> findTaskAnswers(Integer taskId, String taskType, Integer userId);

    UserTaskAnswer findById(Integer id, String taskType);
}
