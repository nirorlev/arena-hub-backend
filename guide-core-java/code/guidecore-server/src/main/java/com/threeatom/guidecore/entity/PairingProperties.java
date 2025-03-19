package com.threeatom.guidecore.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PairingProperties implements TaskProperties {
    private boolean randomOrder;
    private int minRequiredPairs = -1; // -1 means all pairs must be matched
    private List<List<Integer>> grouping;

    public PairingProperties(PairingProperties pairingProperties) {
        this.randomOrder = pairingProperties.isRandomOrder();
        this.minRequiredPairs = pairingProperties.getMinRequiredPairs();
        this.grouping = pairingProperties.getGrouping() == null
            ? null
            : pairingProperties.getGrouping().stream().map(ArrayList::new).collect(Collectors.toList());
    }
}