package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.UserDetailsDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OwnerMapping {

    @Mapping(target = "thumbUrl", source = "avatarFullFileUrl")
    UserDetailsDto map(GcUser user);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "firstName", source = "userInfo.firstName")
    @Mapping(target = "lastName", source = "userInfo.lastName")
    @Mapping(target = "thumbUrl", source = "userInfo.avatarFile.fileUrl")
    UserDetailsDto map(GcUser user, GcUserInfo userInfo);
}
