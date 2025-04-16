package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.AccessGroupDetailsDto;
import com.threeatom.guidecore.entity.GcAccess;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SharableListMapping {

    @Mapping(target = "name", source = "groupName")
    AccessGroupDetailsDto map(GcAccess access);
}
