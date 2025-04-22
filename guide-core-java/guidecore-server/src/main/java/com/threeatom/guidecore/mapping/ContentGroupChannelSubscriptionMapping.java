package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.SubscribeChannelDto;
import com.threeatom.guidecore.dto.response.ContentGroupChannelSubscriptionDto;
import com.threeatom.guidecore.dto.response.GroupChannelSubscriptionDto;
import com.threeatom.guidecore.entity.ContentGroupChannelSubscription;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {ChannelMapping.class, UserMapping.class, ContentGroupMapping.class})
public interface ContentGroupChannelSubscriptionMapping {

    @Mapping(target = "user", source = "createdBy")
    @Mapping(target = "group", source = "contentGroup")
    GroupChannelSubscriptionDto map(ContentGroupChannelSubscription contentGroupChannelSubscription);

    List<GroupChannelSubscriptionDto> map(List<ContentGroupChannelSubscription> contentGroupChannelSubscriptions);

    @Mapping(target = "modifiedDate", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "autoSubscribe", source = "autoSubscribe")
    void updateChannelSubscription(@MappingTarget ContentGroupChannelSubscription contentGroupChannelSubscription,
                                   SubscribeChannelDto subscribeChannelDto);

    @Mapping(target = "channelTitle", source = "channel.channelName")
    @Mapping(target = "channelSlug", source = "channel.channelSlug")
    @Mapping(target = "channelId", source = "channel.id")
    @Mapping(target = "channelImageUrl", source = "channel.imgFullFileUrl")
    @Mapping(target = "source.contentGroup.id", source = "contentGroup.id")
    @Mapping(target = "source.contentGroup.name", source = "contentGroup.groupName")
    @Mapping(target = "source.user.id", source = "createdBy.id")
    @Mapping(target = "source.user.firstName", source = "createdBy.info.firstName")
    @Mapping(target = "source.user.lastName", source = "createdBy.info.lastName")
    @Mapping(target = "source.user.profilePhotoUrl", source = "createdBy.info.avatarFile.fileUrl")
    ContentGroupChannelSubscriptionDto mapDeprecated(ContentGroupChannelSubscription contentGroupChannelSubscription);
}
