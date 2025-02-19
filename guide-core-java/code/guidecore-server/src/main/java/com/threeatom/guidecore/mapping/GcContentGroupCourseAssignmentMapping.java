package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.response.GroupCourseAssignmentDto;
import com.threeatom.guidecore.dto.response.UserDetailsDto;
import com.threeatom.guidecore.entity.GcContentGroupCourseAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(uses = {CourseMapping.class, UserDetailsDto.class, ContentGroupMapping.class})
public interface GcContentGroupCourseAssignmentMapping {

    @Mapping(target = "group", source = "contentGroup")
    @Mapping(target = "user", source = "createdBy")
    @Mapping(target = "mandatory", qualifiedByName = "convertToMandatoryBoolean")
    GroupCourseAssignmentDto map(GcContentGroupCourseAssignment contentGroupCourseAssignment);

    @Mapping(target = "mandatory", qualifiedByName = "convertToMandatoryInt")
    GcContentGroupCourseAssignment map(AssignCourseDto assignCourseDto, Integer createdByUserId);

    @Mapping(target = "modifiedDate", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "mandatory", qualifiedByName = "convertToMandatoryInt")
    void update(@MappingTarget GcContentGroupCourseAssignment contentGroupCourseAssignment,
                AssignCourseDto assignCourseDto, Integer createdByUserId);

    @Named("convertToMandatoryBoolean")
    default Boolean convertToMandatoryBoolean(Integer mandatory) {
        return mandatory == 1;
    }

    @Named("convertToMandatoryInt")
    default Integer convertToMandatoryInt(Boolean mandatory) {
        if (mandatory) {
            return 1;
        }

        return 0;
    }
}
