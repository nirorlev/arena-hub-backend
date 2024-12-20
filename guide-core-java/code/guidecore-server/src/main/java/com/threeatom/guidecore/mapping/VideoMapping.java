package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.VideoListFilterDto;
import com.threeatom.guidecore.dto.response.ChannelDto;
import com.threeatom.guidecore.dto.response.VideoDto;
import com.threeatom.guidecore.dto.response.VideoSourceDto;
import com.threeatom.guidecore.dto.response.VideoWithDetailsDto;
import com.threeatom.guidecore.dto.response.VideoWithSourceDetailsDto;
import com.threeatom.guidecore.dto.response.analytic.VideoSearchResultDto;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.enums.VideoFileProvider;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(uses = {DateMapping.class, ChannelMapping.class, OwnerMapping.class, PlaylistMapping.class})
public abstract class VideoMapping {

    @Autowired
    private ChannelMapping channelMapping;
    @Autowired
    private CourseMapping courseMapping;
    @Autowired
    private PlaylistMapping playlistMapping;

    @Mapping(target = "title", source = "videoName")
    @Mapping(target = "thumbUrl", source = "thumbnailUrl")
    @Mapping(target = "private", source = "originChannel.visibleFlag", qualifiedByName = "mapBoolean")
    @Mapping(target = "source", source = "videoFile.fileTypeIndex", qualifiedByName = "mapVideoUploadSource")
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
    public abstract VideoSearchResultDto mapChannelOrigin(GcVideo video);

    @Mapping(target = "title", source = "videoName")
    @Mapping(target = "thumbUrl", source = "thumbnailUrl")
    @Mapping(target = "private", source = "originCourse.state", qualifiedByName = "mapBoolean")
    @Mapping(target = "source", source = "videoFile.fileTypeIndex", qualifiedByName = "mapVideoUploadSource")
    @Mapping(target = "origin.id", source = "originCourse.id")
    @Mapping(target = "origin.type", constant = "COURSE")
    @Mapping(target = "origin.title", source = "originCourse.name")
    @Mapping(target = "owner.id", source = "originCourse.userInfo.id")
    @Mapping(target = "owner.firstName", source = "originCourse.userInfo.firstName")
    @Mapping(target = "owner.lastName", source = "originCourse.userInfo.lastName")
    @Mapping(target = "owner.thumbUrl", source = "originCourse.userInfo.avatarFullFileUrl")
    @Mapping(target = "created", source = "createTime")
    @Mapping(target = "updated", source = "updateTime")
    public abstract VideoSearchResultDto mapCourseOrigin(GcVideo video);

    @Mapping(target = "step", constant = "0L")
    @Mapping(target = "aggregateBy", constant = "video-id")
    public abstract AnalyticsFilterDto mapFilter(VideoListFilterDto filter, List<Integer> videoIds);

    @Mapping(target = "name", source = "videoName")
    @Mapping(target = "description", source = "videoDesc")
    @Mapping(target = "fileTypeIndex", source = "videoFile.fileTypeIndex")
    @Mapping(target = "fileUrl", source = "videoFile.fullFileUrl")
    @Mapping(target = "snapshotUrl", source = "videoFile.snapshotUrl")
    @Mapping(target = "duration", source = "videoTime")
    public abstract VideoDto map(GcVideo videoFile);

    @Mapping(target = "id", source = "video.id")
    @Mapping(target = "snapshotUrl", source = "video.snapshotUrl")
    @Mapping(target = "createTime", source = "video.createTime")
    @Mapping(target = "updateTime", source = "video.updateTime")
    @Mapping(target = "permissions", source = "video.permissions")
    @Mapping(target = "name", source = "video.videoName")
    @Mapping(target = "description", source = "video.videoDesc")
    @Mapping(target = "fileTypeIndex", source = "video.videoFile.fileTypeIndex")
    @Mapping(target = "fileUrl", source = "video.videoFile.fullFileUrl")
    @Mapping(target = "duration", source = "video.videoTime")
    @Mapping(target = "isLiked", source = "video.isLiked", qualifiedByName = "mapBoolean")
    @Mapping(target = "commentsCount", source = "video.commentNum")
    @Mapping(target = "likesCount", source = "video.likeNum")
    @Mapping(target = "origin", source = "video", qualifiedByName = "mapVideoSource")
    public abstract VideoWithSourceDetailsDto<VideoSourceDto> mapWithVideoSource(GcVideo video);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "snapshotUrl", source = "snapshotUrl")
    @Mapping(target = "createTime", source = "createTime")
    @Mapping(target = "updateTime", source = "updateTime")
    @Mapping(target = "permissions", source = "permissions")
    @Mapping(target = "name", source = "videoName")
    @Mapping(target = "description", source = "videoDesc")
    @Mapping(target = "fileTypeIndex", source = "videoFile.fileTypeIndex")
    @Mapping(target = "fileUrl", source = "videoFile.fullFileUrl")
    @Mapping(target = "duration", source = "videoTime")
    @Mapping(target = "isLiked", source = "video.isLiked", qualifiedByName = "mapBoolean")
    @Mapping(target = "commentsCount", source = "commentNum")
    @Mapping(target = "likesCount", source = "likeNum")
    @Mapping(target = "deprecatedContentId", source = "video.originChannel.channelContentId")
    @Mapping(target = "origin", source = "originChannel")
    public abstract VideoWithSourceDetailsDto<ChannelDto> mapWithDetailsChannelSource(GcVideo video);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "snapshotUrl", source = "snapshotUrl")
    @Mapping(target = "createTime", source = "createTime")
    @Mapping(target = "updateTime", source = "updateTime")
    @Mapping(target = "permissions", source = "permissions")
    @Mapping(target = "name", source = "videoName")
    @Mapping(target = "description", source = "videoDesc")
    @Mapping(target = "fileTypeIndex", source = "videoFile.fileTypeIndex")
    @Mapping(target = "fileUrl", source = "videoFile.fullFileUrl")
    @Mapping(target = "duration", source = "videoTime")
    @Mapping(target = "isLiked", source = "video.isLiked", qualifiedByName = "mapBoolean")
    @Mapping(target = "commentsCount", source = "commentNum")
    @Mapping(target = "likesCount", source = "likeNum")
    public abstract VideoWithDetailsDto mapPlaylistVideoWithDetails(GcVideo video);

    @Named("mapVideoSource")
    protected VideoSourceDto mapVideoSource(GcVideo video) {
        if (video.getOriginChannel() != null) {
            return channelMapping.mapVideoSource(video.getOriginChannel());
        }
        if (video.getOriginCourse() != null) {
            return courseMapping.map(video.getOriginCourse());
        }

        return null;
    }

    @Named("mapBoolean")
    protected boolean mapPrivate(Integer code) {
        return code != null && code == 1;
    }

    @Named("mapVideoUploadSource")
    protected String mapVideoUploadSource(Integer fileTypeIndex) {
        return VideoFileProvider.fromIndex(fileTypeIndex).name();
    }
}
