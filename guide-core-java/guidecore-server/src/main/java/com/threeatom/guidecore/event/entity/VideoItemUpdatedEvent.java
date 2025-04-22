package com.threeatom.guidecore.event.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoItemUpdatedEvent extends EntityUpdatedEvent {

    public VideoItemUpdatedEvent(Object source, Integer id) {
        super(source, id);
    }
}
