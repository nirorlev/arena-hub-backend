package com.threeatom.guidecore.mapping;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.threeatom.guidecore.dto.response.PlaylistWithDetailsDto;
import com.threeatom.guidecore.dto.response.VideoSourceDto;
import com.threeatom.guidecore.entity.GcUserSaveContent;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = {DateMapping.class, OwnerMapping.class, ContentMapping.class})
public interface PlaylistMapping {

    @Mapping(target = "size", source = "saveContentList", qualifiedByName = "mapPlaylistSize")
    @Mapping(target = "owner", source = "user")
    PlaylistWithDetailsDto mapWithDetails(GcUserSaveFolder playlist);

    @Mapping(target = "owner", source = "user")
    VideoSourceDto mapSource(GcUserSaveFolder playlist);

    @Named("mapPlaylistSize")
    default Integer mapPlaylistSize(List<GcUserSaveContent> playlistContent) {
        if (CollectionUtils.isEmpty(playlistContent)) {
            return 0;
        }

        return playlistContent.size();
    }
}
