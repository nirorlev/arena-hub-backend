package com.threeatom.client;


import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "bi-service", url = "${bi.service.host}")
public interface BiServiceClient {

    @GetMapping("/event.gif")
    void send(@SpringQueryMap Map<String, Object> eventPayload);
}
