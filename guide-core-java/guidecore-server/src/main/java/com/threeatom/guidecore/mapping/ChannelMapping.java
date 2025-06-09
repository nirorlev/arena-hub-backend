package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.BasicChannelDto;
import com.threeatom.guidecore.dto.response.ChannelWithDetailsDto;
import com.threeatom.guidecore.dto.response.VideoSourceDto;
import com.threeatom.guidecore.entity.PtChannel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = UserMapping.class)
public interface ChannelMapping {

    @Named("mapBasic")
    @Mapping(target = "title", source = "channelName")
    @Mapping(target = "slug", source = "channelSlug")
    @Mapping(target = "thumbUrl", source = "imgFullFileUrl")
    BasicChannelDto mapBasic(PtChannel channel);

    @Mapping(target = "title", source = "channelName")
    @Mapping(target = "slug", source = "channelSlug")
    @Mapping(target = "owner", source = "createUser")
    @Mapping(target = "avatarUrl", source = "avatarFullFileUrl")
    @Mapping(target = "backgroundUrl", source = "imgFullFileUrl")
    @Mapping(target = "totalSubscribers", source = "subscribeNum")
    @Mapping(target = "lastContentUpdateTime", source = "lastContentUpdatedTime")
    ChannelWithDetailsDto map(PtChannel channel);

    @Mapping(target = "owner", source = "createUser")
    @Mapping(target = "name", source = "channelName")
    @Mapping(target = "avatarUrl", source = "avatarFile.fullFileUrl")
    VideoSourceDto mapVideoSource(PtChannel channel);
}
