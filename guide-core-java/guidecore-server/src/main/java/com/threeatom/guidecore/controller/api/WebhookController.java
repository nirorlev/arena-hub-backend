package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.request.WebhookDto;
import com.threeatom.guidecore.service.WebhookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Webhooks API", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle webhook requests")
    public ResponseEntity<Void> handleWebhook(@RequestBody WebhookDto webhook) {
        webhookService.handleWebhook(webhook);
        return ResponseEntity.ok().build();
    }
}