package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.VideoListFilterDto;
import com.threeatom.guidecore.dto.response.VideoDto;
import com.threeatom.guidecore.dto.response.VideoWithDetailsDto;
import com.threeatom.guidecore.dto.response.analytic.VideoSearchResultDto;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.enums.VideoFileProvider;
import java.util.List;
import jdk.jfr.Name;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = PlaylistMapping.class)
public interface VideoMapping {

    @Mapping(target = "title", source = "videoName")
    @Mapping(target = "thumbUrl", source = "thumbnailUrl")
    @Mapping(target = "private", source = "originChannel.visibleFlag", qualifiedByName = "mapBoolean")
    @Mapping(target = "source", source = "videoFile.fileTypeIndex", qualifiedByName = "mapVideoSource")
    @Mapping(target = "origin.id", source = "originChannel.id")
    @Mapping(target = "origin.type", constant = "CHANNEL")
    @Mapping(target = "origin.channelSlug", source = "originChannel.channelSlug")
    @Mapping(target = "origin.contentId", source = "originChannel.channelContentId")
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
    @Mapping(target = "private", source = "originCourse.state", qualifiedByName = "mapBoolean")
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

    @Mapping(target = "step", constant = "0L")
    @Mapping(target = "aggregateBy", constant = "video-id")
    AnalyticsFilterDto mapFilter(VideoListFilterDto filter, List<Integer> videoIds);

    @Mapping(target = "name", source = "videoName")
    @Mapping(target = "description", source = "videoDesc")
    @Mapping(target = "fileTypeIndex", source = "videoFile.fileTypeIndex")
    @Mapping(target = "fileUrl", source = "videoFile.fullFileUrl")
    @Mapping(target = "snapshotUrl", source = "videoFile.snapshotUrl")
    @Mapping(target = "duration", source = "videoTime")
    VideoDto map(GcVideo videoFile);


    @Mapping(target = "name", source = "videoName")
    @Mapping(target = "description", source = "videoDesc")
    @Mapping(target = "fileTypeIndex", source = "videoFile.fileTypeIndex")
    @Mapping(target = "fileUrl", source = "videoFile.fileUrl")
    @Mapping(target = "snapshotUrl", source = ".", qualifiedByName = "mapSnapshotUrl")
    @Mapping(target = "duration", source = "videoTime")
    @Mapping(target = "isLiked", source = "isLiked", qualifiedByName = "mapBoolean")
    @Mapping(target = "commentsCount", source = "commentNum")
    @Mapping(target = "likesCount", source = "likeNum")
    @Mapping(target = "playlist", source = "playlist", qualifiedByName = "mapPlaylist")
    VideoWithDetailsDto mapWithDetails(GcVideo video);

    @Named("mapBoolean")
    default boolean mapPrivate(Integer code) {
        return code != null && code == 1;
    }

    @Named("mapVideoSource")
    default String mapVideoSource(Integer fileTypeIndex) {
        return VideoFileProvider.fromIndex(fileTypeIndex).name();
    }

    @Named("mapSnapshotUrl")
    default String mapSnapshotUrl(GcVideo video) {
        return StringUtils.isNotBlank(video.getSnapshotUrl())
            ? video.getSnapshotUrl()
            : video.getVideoFile().getFileUrl();
    }
}
