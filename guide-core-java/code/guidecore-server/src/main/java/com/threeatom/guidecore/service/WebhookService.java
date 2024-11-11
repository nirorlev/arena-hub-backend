package com.threeatom.guidecore.service;

import com.threeatom.guidecore.dto.request.WebhookDto;

public interface WebhookService {
    void handleWebhook(WebhookDto webhook);
}
