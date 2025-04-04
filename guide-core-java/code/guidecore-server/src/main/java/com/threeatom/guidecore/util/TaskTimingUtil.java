package com.threeatom.guidecore.util;

import com.threeatom.guidecore.enums.TaskType;
import java.util.EnumMap;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TaskTimingUtil {
    private static final EnumMap<TaskType, Integer> TASK_TIMING_MAP = new EnumMap<>(TaskType.class);

    static {
        TASK_TIMING_MAP.put(TaskType.OPEN_QUESTION, 60);
        TASK_TIMING_MAP.put(TaskType.MULTIPLE_CHOICE, 30);
        TASK_TIMING_MAP.put(TaskType.FILL_IN_THE_BLANK, 25);
        TASK_TIMING_MAP.put(TaskType.PAIRING, 20);
        TASK_TIMING_MAP.put(TaskType.SINGLE_CHOICE, 15);
    }

    public int getTaskTiming(TaskType taskType) {
        return TASK_TIMING_MAP.getOrDefault(taskType, 0);
    }
}
