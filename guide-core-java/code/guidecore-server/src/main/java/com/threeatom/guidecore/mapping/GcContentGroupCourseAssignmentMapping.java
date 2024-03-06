package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.response.ContentGroupCourseAssignmentDto;
import com.threeatom.guidecore.entity.GcContentGroupCourseAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface GcContentGroupCourseAssignmentMapping {

    @Mapping(target = "courseTitle", source = "course.name")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseImageUrl", source = "course.subImgFile.fileUrl")
    @Mapping(target = "source.contentGroup.id", source = "contentGroup.id")
    @Mapping(target = "source.contentGroup.name", source = "contentGroup.groupName")
    @Mapping(target = "source.user.firstName", source = "createdBy.info.firstName")
    @Mapping(target = "source.user.lastName", source = "createdBy.info.lastName")
    @Mapping(target = "source.user.profilePhotoUrl", source = "createdBy.info.avatarFile.fileUrl")
    ContentGroupCourseAssignmentDto map(GcContentGroupCourseAssignment contentGroupCourseAssignment);

    GcContentGroupCourseAssignment map(AssignCourseDto assignCourseDto, Integer createdByUserId);

    @Mapping(target = "modifiedDate", expression = "java(java.time.LocalDateTime.now())")
    void update(@MappingTarget GcContentGroupCourseAssignment contentGroupCourseAssignment, AssignCourseDto assignCourseDto);
}
