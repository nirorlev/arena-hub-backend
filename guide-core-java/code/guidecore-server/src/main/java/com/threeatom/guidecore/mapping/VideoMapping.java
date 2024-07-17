package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.analytic.VideoSearchResultDto;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.enums.VideoFileProvider;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface VideoMapping {

    @Mapping(target = "title", source = "videoName")
    @Mapping(target = "thumbUrl", source = "thumbnailUrl")
    @Mapping(target = "private", source = "originChannel.visibleFlag", qualifiedByName = "mapPrivate")
    @Mapping(target = "source", source = "videoFile.fileTypeIndex", qualifiedByName = "mapVideoSource")
    @Mapping(target = "origin.id", source = "originChannel.id")
    @Mapping(target = "origin.type", constant = "CHANNEL")
    @Mapping(target = "origin.title", source = "originChannel.channelName")
    @Mapping(target = "owner.id", source = "originChannel.createUser.id")
    @Mapping(target = "owner.firstName", source = "originChannel.createUser.firstName")
    @Mapping(target = "owner.lastName", source = "originChannel.createUser.lastName")
    @Mapping(target = "owner.thumbUrl", source = "originChannel.createUser.avatarFullFileUrl")
    @Mapping(target = "created", source = "createTime")
    @Mapping(target = "updated", source = "updateTime")
    VideoSearchResultDto mapChannelOrigin(GcVideo video);

    @Mapping(target = "title", source = "videoName")
    @Mapping(target = "thumbUrl", source = "thumbnailUrl")
    @Mapping(target = "private", source = "originCourse.state", qualifiedByName = "mapPrivate")
    @Mapping(target = "source", source = "videoFile.fileTypeIndex", qualifiedByName = "mapVideoSource")
    @Mapping(target = "origin.id", source = "originCourse.id")
    @Mapping(target = "origin.type", constant = "COURSE")
    @Mapping(target = "origin.title", source = "originCourse.name")
    @Mapping(target = "owner.id", source = "originCourse.userInfo.id")
    @Mapping(target = "owner.firstName", source = "originCourse.userInfo.firstName")
    @Mapping(target = "owner.lastName", source = "originCourse.userInfo.lastName")
    @Mapping(target = "owner.thumbUrl", source = "originCourse.userInfo.avatarFullFileUrl")
    @Mapping(target = "created", source = "createTime")
    @Mapping(target = "updated", source = "updateTime")
    VideoSearchResultDto mapCourseOrigin(GcVideo video);

    @Named("mapPrivate")
    default boolean mapPrivate(Integer code) {
        return code != null && code == 1;
    }

    @Named("mapVideoSource")
    default String mapVideoSource(Integer fileTypeIndex) {
        return VideoFileProvider.fromIndex(fileTypeIndex).name();
    }
}
