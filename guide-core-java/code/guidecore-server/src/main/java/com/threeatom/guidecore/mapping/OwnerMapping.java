package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.UserDetailsDto;
import com.threeatom.guidecore.entity.GcUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OwnerMapping {

    @Mapping(target = "thumbUrl", source = "avatarFullFileUrl")
    UserDetailsDto map(GcUser user);
}
