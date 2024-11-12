package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.PlaylistContentDto;
import com.threeatom.guidecore.dto.response.PlaylistDto;
import com.threeatom.guidecore.entity.GcUserSaveContent;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcVideo;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = DateMapping.class)
public interface PlaylistMapping {

    @Mapping(target = "snapshotUrl", source = "playlist.saveContentList", qualifiedByName = "mapSnapshotUrl")
    @Mapping(target = "videoNum", source = "playlist.saveContentList", qualifiedByName = "mapVideoNum")
    PlaylistDto map(GcUserSaveFolder playlist);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "videoId", source = "contentId")
    PlaylistContentDto mapContent(GcUserSaveContent content);

    @Named("mapSnapshotUrl")
    default String mapSnapshotUrl(List<GcUserSaveContent> playlistContent) {
        return playlistContent.stream()
            .max(Comparator.comparing(GcUserSaveContent::getCreateTime))
            .map(content -> {
                GcVideo video = content.getVideo();
                return StringUtils.isNotBlank(video.getSnapshotUrl())
                    ? video.getSnapshotUrl()
                    : content.getVideoFile().getFileUrl();
            })
            .orElse("");
    }

    @Named("mapVideoNum")
    default Integer mapVideoNum(List<GcUserSaveContent> playlistContent) {
        return playlistContent.size();
    }

}
