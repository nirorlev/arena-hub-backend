package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.ChannelDto;
import com.threeatom.guidecore.entity.PtChannel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = OwnerMapping.class)
public interface ChannelMapping {

    @Mapping(target = "title", source = "channelName")
    @Mapping(target = "slug", source = "channelSlug")
    @Mapping(target = "owner", source = "createUser")
    @Mapping(target = "avatarUrl", source = "avatarFullFileUrl")
    @Mapping(target = "backgroundUrl", source = "imgFullFileUrl")
    @Mapping(target = "totalSubscribers", source = "subscribeNum")
    @Mapping(target = "isPublic", source = "visibleFlag", qualifiedByName = "mapIsPublic")
    ChannelDto map(PtChannel channel);

    @Named("mapIsPublic")
    default boolean mapIsPublic(Integer visibleFlag) {
        return visibleFlag != null && visibleFlag != 0;
    }
}
