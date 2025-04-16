package com.threeatom.guidecore.event.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseUpdatedEvent extends EntityUpdatedEvent {

    public CourseUpdatedEvent(Object source, Integer id) {
        super(source, id);
    }
}
