package com.threeatom.guidecore.entity;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PairingProperties implements TaskProperties {
    private boolean randomOrder;
    private int minRequiredPairs = -1; // -1 means all pairs must be matched
    private List<List<Integer>> grouping;
}