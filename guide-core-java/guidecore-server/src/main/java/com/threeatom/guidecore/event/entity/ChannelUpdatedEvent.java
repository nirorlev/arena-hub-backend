package com.threeatom.guidecore.event.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelUpdatedEvent extends EntityUpdatedEvent {

    public ChannelUpdatedEvent(Object source, Integer id) {
        super(source, id);
    }
}
