
package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.Notification;
import com.threeatom.guidecore.mapper.NotificationMapper;
import com.threeatom.guidecore.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
        implements NotificationService {
}
