package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.UserDetailsDto;
import com.threeatom.guidecore.entity.GcUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapping {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "info.firstName")
    @Mapping(target = "lastName", source = "info.lastName")
    @Mapping(target = "thumbUrl", source = "info.avatarFile.fileUrl")
    UserDetailsDto map(GcUser user);
}
