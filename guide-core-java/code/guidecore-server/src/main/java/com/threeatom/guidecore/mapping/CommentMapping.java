package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.CommentDto;
import com.threeatom.guidecore.entity.GcUserFabulous;
import com.threeatom.guidecore.entity.GcVideoComment;
import com.threeatom.guidecore.entity.PortalUser;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = {DateMapping.class, OwnerMapping.class})
public interface CommentMapping {

    @Mapping(target = "likesCount", source = "comment.userFabulousList", qualifiedByName = "mapLikesCount")
    @Mapping(target = "liked", expression = "java(mapIsLiked(comment, userId))")
    CommentDto map(GcVideoComment comment, Integer userId);

    @Mapping(target = "comment", source = "commentDto.text")
    @Mapping(target = "userId", source = "portalUser.userId")
    @Mapping(target = "masterId", source = "portalUser.masterId")
    @Mapping(target = "videoId", source = "videoId")
    GcVideoComment map(com.threeatom.guidecore.dto.request.CommentDto commentDto, PortalUser portalUser,
                       Integer videoId);

    @Named("mapLikesCount")
    default Integer mapLikesCount(List<GcUserFabulous> fabulous) {
        if (fabulous == null || fabulous.isEmpty()) {
            return 0;
        }

        return fabulous.size();
    }

    @Named("mapIsLiked")
    default boolean mapIsLiked(GcVideoComment comment, Integer userId) {
        List<GcUserFabulous> fabulous = comment.getUserFabulousList();
        if (fabulous == null || fabulous.isEmpty()) {
            return false;
        }

        return fabulous.stream().anyMatch(f -> f.getUserId().equals(userId));
    }
}
