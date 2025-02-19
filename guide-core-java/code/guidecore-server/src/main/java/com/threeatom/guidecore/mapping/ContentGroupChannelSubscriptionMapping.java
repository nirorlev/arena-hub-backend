package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.SubscribeChannelDto;
import com.threeatom.guidecore.dto.response.GroupChannelSubscriptionDto;
import com.threeatom.guidecore.entity.ContentGroupChannelSubscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {ChannelMapping.class, UserMapping.class, ContentGroupMapping.class})
public interface ContentGroupChannelSubscriptionMapping {

    @Mapping(target = "user", source = "createdBy")
    @Mapping(target = "group", source = "contentGroup")
    @Mapping(target = "channel", source = "channel", qualifiedByName = "mapBasic")
    GroupChannelSubscriptionDto map(ContentGroupChannelSubscription contentGroupChannelSubscription);

    @Mapping(target = "modifiedDate", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "autoSubscribe", source = "autoSubscribe")
    void updateChannelSubscription(@MappingTarget ContentGroupChannelSubscription contentGroupChannelSubscription,
                                   SubscribeChannelDto subscribeChannelDto);
}
