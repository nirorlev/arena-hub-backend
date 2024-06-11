package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.ContentGroupChannelSubscriptionDto;
import com.threeatom.guidecore.entity.ContentGroupChannelSubscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ContentGroupChannelSubscriptionMapping {

    @Mapping(target = "channelTitle", source = "channel.channelName")
    @Mapping(target = "channelId", source = "channel.id")
    @Mapping(target = "channelImageUrl", source = "channel.imgFullFileUrl")
    @Mapping(target = "source.contentGroup.id", source = "contentGroup.id")
    @Mapping(target = "source.contentGroup.name", source = "contentGroup.groupName")
    @Mapping(target = "source.user.id", source = "createdBy.id")
    @Mapping(target = "source.user.firstName", source = "createdBy.info.firstName")
    @Mapping(target = "source.user.lastName", source = "createdBy.info.lastName")
    @Mapping(target = "source.user.profilePhotoUrl", source = "createdBy.info.avatarFile.fileUrl")
    ContentGroupChannelSubscriptionDto map(ContentGroupChannelSubscription contentGroupChannelSubscription);
}
