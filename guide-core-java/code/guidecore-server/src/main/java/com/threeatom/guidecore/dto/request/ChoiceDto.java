package com.threeatom.guidecore.dto.request;


import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChoiceDto {
    @NotNull(message = "Choice id is required")
    private Integer id;
    @NotNull(message = "Choice text is required")
    private String text;
}

