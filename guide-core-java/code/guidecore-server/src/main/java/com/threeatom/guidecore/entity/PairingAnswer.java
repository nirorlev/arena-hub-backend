package com.threeatom.guidecore.entity;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PairingAnswer extends Answer {
    private List<List<Integer>> choiceIds;
}