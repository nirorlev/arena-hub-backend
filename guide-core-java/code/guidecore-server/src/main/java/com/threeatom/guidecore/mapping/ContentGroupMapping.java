package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.BasicGroupDto;
import com.threeatom.guidecore.dto.response.ContentGroupDto;
import com.threeatom.guidecore.entity.GcAccess;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ContentGroupMapping {

    @Mapping(target = "name", source = "groupName")
    ContentGroupDto map(GcAccess contentGroup);

    @Mapping(target = "name", source = "groupName")
    List<ContentGroupDto> map(List<GcAccess> contentGroups);

    @Mapping(target = "name", source = "groupName")
    @Mapping(target = "code", source = "code")
    BasicGroupDto mapBasicGroup(GcAccess contentGroup);
}
