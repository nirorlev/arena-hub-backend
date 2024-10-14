package com.threeatom.guidecore.service;

import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.enums.BiEventAction;
import java.util.List;

public interface EventPublisherService {
    void publishBiEvent(BiEventAction action, GcUser user);

    void publishUserUpdated(Integer userId);

    void publishUserUpdated(List<Integer> userIds);

    void publishContentGroupUpdated(Integer contentGroupId);

    void publishVideoUpdated(Integer videoId);

    void publishCourseUpdated(Integer courseId);

    void publishCourseUpdated(List<Integer> courseIds);

    void publishChannelUpdated(Integer channelId);

    void publishChannelUpdated(List<Integer> channelIds);
}
