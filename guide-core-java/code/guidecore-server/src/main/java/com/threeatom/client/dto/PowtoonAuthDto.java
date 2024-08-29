package com.threeatom.client.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PowtoonAuthDto {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
}
