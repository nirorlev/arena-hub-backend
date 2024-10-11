package com.threeatom.guidecore.event;

import com.threeatom.guidecore.enums.BiEventAction;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class BiEvent extends ApplicationEvent {
    private Integer powtoonUserId;
    private String visitorId;
    private Map<String, String> additionalData = Map.of();
    private final BiEventAction event;

    public BiEvent(Object source, BiEventAction event) {
        super(source);
        this.event = event;
    }
}

