package com.threeatom.guidecore.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReactionDetailsDto {
    private Integer count = 0;
    private boolean isCurrentUserReacted;

}
