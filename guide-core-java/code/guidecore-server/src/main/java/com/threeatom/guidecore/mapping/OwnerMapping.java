package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.OwnerDto;
import com.threeatom.guidecore.entity.GcUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OwnerMapping {

    @Mapping(target = "avatarUrl", source = "avatarFullFileUrl")
    OwnerDto map(GcUser user);
}
