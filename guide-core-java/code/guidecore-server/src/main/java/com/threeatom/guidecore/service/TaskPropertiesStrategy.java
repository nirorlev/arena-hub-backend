package com.threeatom.guidecore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.threeatom.guidecore.entity.TaskProperties;
import com.threeatom.guidecore.enums.TaskType;

public interface TaskPropertiesStrategy {

    TaskProperties createProperties(TaskType type, String jsonProperties) throws JsonProcessingException;
}
