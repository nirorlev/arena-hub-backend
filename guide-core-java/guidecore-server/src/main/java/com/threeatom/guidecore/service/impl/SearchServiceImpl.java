package com.threeatom.guidecore.service.impl;

import com.github.pagehelper.PageInfo;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.dto.request.SearchDto;
import com.threeatom.guidecore.entity.Course;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.enums.SearchType;
import com.threeatom.guidecore.service.CourseService;
import com.threeatom.guidecore.service.GcUserSaveFolderService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.service.SearchService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final GcVideoService videoService;
    private final PtChannelService channelService;
    private final CourseService courseService;
    private final GcUserSaveFolderService playlistService;

    @Override
    public Message search(SearchDto searchDto, PortalUser portalUser) {
        SearchType searchType = searchDto.getSearchType();
        String searchName = searchDto.getSearchName();

        return switch (searchType) {
            case VIDEO -> searchVideosMessage(searchName, portalUser);
            case COURSE -> searchCoursesMessage(searchName, portalUser);
            case CHANNEL -> searchChannelsMessage(searchName, portalUser);
            case PLAYLIST -> searchPlaylistMessage(searchName, portalUser);
            default -> search(searchName, portalUser);
        };
    }

    private Message search(String searchName, PortalUser portalUser) {
        return createMessageOk()
            .addData(searchVideosMessage(searchName, portalUser).getData())
            .addData(searchCoursesMessage(searchName, portalUser).getData())
            .addData(searchChannelsMessage(searchName, portalUser).getData())
            .addData(searchPlaylistMessage(searchName, portalUser).getData());
    }

    private Message searchPlaylistMessage(String searchName, PortalUser portalUser) {
        List<GcUserSaveFolder> playlists = searchPlaylists(searchName, portalUser);
        Message message = createMessageOk();

        if (playlists.isEmpty()) {
            return message.addData("recommenFolderListNullPageInfo", new PageInfo<>(searchSuggestedPlaylists(portalUser)));
        }

        return message.addData("recommenFolderListPageInfo", new PageInfo<>(playlists));
    }

    private List<GcUserSaveFolder> searchSuggestedPlaylists(PortalUser portalUser) {
        return playlistService.searchSuggestedPlaylist(portalUser);
    }

    private List<GcUserSaveFolder> searchPlaylists(String searchName, PortalUser portalUser) {
        return playlistService.searchPlaylists(searchName, portalUser);
    }

    private Message searchChannelsMessage(String searchName, PortalUser portalUser) {
        List<PtChannel> channels = searchChannels(searchName, portalUser);
        Message message = createMessageOk();

        if (channels.isEmpty()) {
            return message.addData("channelNullPage", new PageInfo<>(searchSuggestedChannels(portalUser)));
        }

        return message.addData("channelPage", new PageInfo<>(channels));
    }

    private List<PtChannel> searchSuggestedChannels(PortalUser portalUser) {
        return channelService.searchSuggestedChannels(portalUser);
    }

    private List<PtChannel> searchChannels(String searchName, PortalUser portalUser) {
        return channelService.searchChannels(searchName, portalUser);
    }

    private Message searchCoursesMessage(String searchName, PortalUser portalUser) {
        Message message = createMessageOk();

        List<Course> courses = searchCourses(searchName, portalUser);
        if (CollectionUtils.isEmpty(courses)) {
            return message.addData("subjectNullPage", new PageInfo<>(searchSuggestedCourses(portalUser)));
        }

        return message
            .addData("subjectPage", new PageInfo<>(courses));
    }

    private Message searchVideosMessage(String searchName, PortalUser portalUser) {
        List<PtChannel> channelVideos = searchChannelVideos(searchName, portalUser);
        List<GcVideo> courseVideos = searchCourseVideos(searchName, portalUser);

        Message message = createMessageOk()
            .addData("channelVideoPage", new PageInfo<>(channelVideos))
            .addData("videoPage", new PageInfo<>(courseVideos));

        if (channelVideos.isEmpty() && courseVideos.isEmpty()) {
            message.addData("videoNullPage", new PageInfo<>(searchSuggestedChannelVideos(portalUser)));
        }

        return message;
    }

    private List<PtChannel> searchSuggestedChannelVideos(PortalUser portalUser) {
        return channelService.searchSuggestedChannelsVideos(portalUser);
    }

    private List<GcVideo> searchCourseVideos(String searchName, PortalUser portalUser) {
        return videoService.searchCourseVideos(searchName, portalUser);
    }

    private List<PtChannel> searchChannelVideos(String searchName, PortalUser portalUser) {
        return channelService.searchChannelVideos(searchName, portalUser);
    }

    private List<Course> searchCourses(String searchName, PortalUser portalUser) {
        return courseService.searchCourses(searchName, portalUser);
    }

    private List<Course> searchSuggestedCourses(PortalUser portalUser) {
        return courseService.searchSuggestedCourses(portalUser);
    }

    private Message createMessageOk() {
        return new Message().ok()
            .addData("systemTime", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
    }
}
