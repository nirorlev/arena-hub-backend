package com.threeatom.guidecore.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberRange<T extends Number> {
    private T from;
    private T to;
}
