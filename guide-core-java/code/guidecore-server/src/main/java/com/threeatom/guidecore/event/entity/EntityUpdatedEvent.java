package com.threeatom.guidecore.event.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public abstract class EntityUpdatedEvent extends ApplicationEvent {

    private final Integer id;

    public EntityUpdatedEvent(Object source, Integer id) {
        super(source);
        this.id = id;
    }
}
