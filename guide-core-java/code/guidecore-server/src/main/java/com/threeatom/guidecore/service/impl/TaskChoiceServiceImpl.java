
package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.TaskChoice;
import com.threeatom.guidecore.mapper.TaskChoiceMapper;
import com.threeatom.guidecore.service.TaskChoiceService;
import org.springframework.stereotype.Service;

@Service
public class TaskChoiceServiceImpl extends ServiceImpl<TaskChoiceMapper, TaskChoice>
        implements TaskChoiceService {
}
