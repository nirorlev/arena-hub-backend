package com.threeatom.guidecore.listener;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.client.BiServiceClient;
import com.threeatom.guidecore.dto.request.BiEventDto;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BiEventListener {

    private static final Map<String, Function<BiEventDto, Object>> PAYLOAD = Map.of(
        "u", BiEventDto::getPowtoonUserId
        , "i", BiEventDto::getVisitorId
        , "c", event -> event.getAction().getCategory().getValue()
        , "a", event -> event.getAction().getAction()
        , "l", event -> event.getAction().getLabel()
        , "v", event -> event.getAction().getValue()
        , "d", event -> JSONObject.toJSONString(event.getAdditionalData())
    );

    private final BiServiceClient biServiceClient;

    @Async
    @EventListener
    public void handleBiReportEvent(BiEventDto event) {
        try {
            biServiceClient.send(payload(event));
            log.info("Event sent to BI service: {}", event);
        } catch (Exception e) {
            log.error("Failed to send event to BI service", e);
            throw e;
        }
    }

    private Map<String, Object> payload(BiEventDto event) {
        return PAYLOAD.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().apply(event)));
    }
}
