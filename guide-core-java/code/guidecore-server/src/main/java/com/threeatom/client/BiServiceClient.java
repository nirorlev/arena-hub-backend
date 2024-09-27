package com.threeatom.client;


import com.threeatom.guidecore.enums.BiEventType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "bi-service", url = "https://runtime-placeholder.com")
public interface BiServiceClient {

    @GetMapping
    default void sendEvent(BiEventType type) {
        System.out.println("Sending event to BI system: " + type);
    }
}
