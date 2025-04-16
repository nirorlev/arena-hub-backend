package com.threeatom.guidecore.entity;

import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

@Data
public class VideoPlaySegmentId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long segmentId;
    private UUID sessionId;
}
