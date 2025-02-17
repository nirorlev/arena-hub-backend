package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.AssignChannelDto;
import com.threeatom.guidecore.dto.response.ContentGroupChannelSubscriptionDto;
import com.threeatom.guidecore.dto.response.ContentGroupDto;
import com.threeatom.guidecore.entity.ContentGroupChannelSubscription;
import com.threeatom.guidecore.entity.GcAccess;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface ContentGroupMapping {

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
    ContentGroupChannelSubscriptionDto map(ContentGroupChannelSubscription contentGroupChannelSubscription);

    @Mapping(target = "name", source = "groupName")
    ContentGroupDto map(GcAccess contentGroup);

    @Mapping(target = "name", source = "groupName")
    List<ContentGroupDto> map(List<GcAccess> contentGroups);

    @Mapping(target = "modifiedDate", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "autoSubscribe", source = "autoSubscribe")
    void updateChannelAssignment(@MappingTarget ContentGroupChannelSubscription contentGroupChannelSubscription,
                                 AssignChannelDto assignChannelDto);
}
