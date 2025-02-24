package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.response.ContentGroupCourseAssignmentDto;
import com.threeatom.guidecore.dto.response.GroupCourseAssignmentDto;
import com.threeatom.guidecore.entity.GcContentGroupCourseAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(uses = {CourseMapping.class, UserMapping.class, ContentGroupMapping.class})
public interface GcContentGroupCourseAssignmentMapping {

    @Mapping(target = "courseTitle", source = "course.name")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseImageUrl", source = "course.subImgFile.fullFileUrl")
    @Mapping(target = "source.contentGroup.id", source = "contentGroup.id")
    @Mapping(target = "source.contentGroup.name", source = "contentGroup.groupName")
    @Mapping(target = "source.user.id", source = "createdBy.id")
    @Mapping(target = "source.user.firstName", source = "createdBy.info.firstName")
    @Mapping(target = "source.user.lastName", source = "createdBy.info.lastName")
    @Mapping(target = "source.user.profilePhotoUrl", source = "createdBy.info.avatarFile.fileUrl")
    @Mapping(target = "mandatory", qualifiedByName = "convertToMandatoryBoolean")
    ContentGroupCourseAssignmentDto mapDeprecated(GcContentGroupCourseAssignment contentGroupCourseAssignment);

    @Mapping(target = "group", source = "contentGroup")
    @Mapping(target = "user", source = "createdBy")
    @Mapping(target = "mandatory", qualifiedByName = "convertToMandatoryBoolean")
    @Mapping(target = "updatedTime", source = "modifiedDate")
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
