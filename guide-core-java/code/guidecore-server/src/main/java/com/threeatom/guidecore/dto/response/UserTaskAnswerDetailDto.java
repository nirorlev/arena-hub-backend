package com.threeatom.guidecore.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTaskAnswerDetailDto extends UserDetailsDto {
    private List<UserTaskAnswerDto> answers;
}
