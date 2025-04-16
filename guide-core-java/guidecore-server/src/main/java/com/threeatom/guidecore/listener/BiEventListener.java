package com.threeatom.guidecore.listener;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.client.BiServiceClient;
import com.threeatom.guidecore.event.BiEvent;
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

    private static final Map<String, Function<BiEvent, Object>> PAYLOAD = Map.of(
        "u", BiEvent::getPowtoonUserId
        , "i", BiEvent::getVisitorId
        , "c", event -> event.getEvent().getCategory().getValue()
        , "a", event -> event.getEvent().getAction()
        , "l", event -> event.getEvent().getLabel()
        , "v", event -> event.getEvent().getValue()
        , "d", event -> JSONObject.toJSONString(event.getAdditionalData())
    );

    private final BiServiceClient biServiceClient;

    @Async
    @EventListener
    public void handleBiReportEvent(BiEvent event) {
        try {
            biServiceClient.send(payload(event));
            log.info("Event sent to BI service: {}", event);
        } catch (Exception e) {
            log.error("Failed to send event to BI service", e);
            throw e;
        }
    }

    private Map<String, Object> payload(BiEvent event) {
        return PAYLOAD.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().apply(event)));
    }
}
