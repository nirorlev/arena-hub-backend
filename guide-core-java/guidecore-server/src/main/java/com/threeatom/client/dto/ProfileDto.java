package com.threeatom.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String thumbUrl;
}
