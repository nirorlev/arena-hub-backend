package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.CourseProgressDetailsDto;
import com.threeatom.guidecore.dto.response.ProgressDto;
import com.threeatom.guidecore.dto.response.VideoSourceDto;
import com.threeatom.guidecore.entity.GcSubject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateMapping.class, OwnerMapping.class})
public interface CourseMapping {

    @Mapping(target = "owner", source = "user")
    @Mapping(target = "avatarUrl", source = "subImgFile.fullFileUrl")
    VideoSourceDto map(GcSubject course);

    CourseProgressDetailsDto mapToCourseProgress(GcSubject course, Double progress);

    ProgressDto mapToProgress(Double progress);
}
