package com.threeatom.guidecore.event.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentGroupUpdatedEvent extends EntityUpdatedEvent {

    public ContentGroupUpdatedEvent(Object source, Integer id) {
        super(source, id);
    }
}
