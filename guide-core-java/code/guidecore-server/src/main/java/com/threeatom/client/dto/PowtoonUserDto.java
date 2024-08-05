package com.threeatom.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PowtoonUserDto {
    private ProfileDto profile;
    private PermissionDto permissions;
}
