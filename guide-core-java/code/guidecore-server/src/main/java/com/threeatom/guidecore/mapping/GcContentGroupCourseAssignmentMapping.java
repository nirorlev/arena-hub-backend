package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.response.ContentGroupCourseAssignmentDto;
import com.threeatom.guidecore.entity.GcContentGroupCourseAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface GcContentGroupCourseAssignmentMapping {

    @Mapping(target = "courseTitle", source = "course.name")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseImageUrl", source = "course.subImgFile.fileUrl")
    @Mapping(target = "source.contentGroup.id", source = "access.id")
    @Mapping(target = "source.contentGroup.name", source = "access.groupName")
    @Mapping(target = "source.user.firstName", source = "user.info.firstName")
    @Mapping(target = "source.user.lastName", source = "user.info.lastName")
    @Mapping(target = "source.user.profilePhotoUrl", source = "user.info.avatarFile.fileUrl")
    ContentGroupCourseAssignmentDto map(GcContentGroupCourseAssignment contentGroupCourseAssignment);

    GcContentGroupCourseAssignment map(AssignCourseDto assignCourseDto);
}
