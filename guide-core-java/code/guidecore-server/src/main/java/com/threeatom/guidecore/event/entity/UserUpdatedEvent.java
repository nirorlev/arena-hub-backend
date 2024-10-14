package com.threeatom.guidecore.event.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdatedEvent extends EntityUpdatedEvent {

    public UserUpdatedEvent(Object source, Integer id) {
        super(source, id);
    }
}
