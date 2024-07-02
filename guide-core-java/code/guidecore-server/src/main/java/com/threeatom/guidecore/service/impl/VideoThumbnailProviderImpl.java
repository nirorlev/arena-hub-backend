package com.threeatom.guidecore.service.impl;

import com.threeatom.guidecore.enums.VideoFileProvider;
import com.threeatom.guidecore.service.VideoThumbnailProvider;
import com.threeatom.system.entity.SysFile;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class VideoThumbnailProviderImpl implements VideoThumbnailProvider {

    private final static Map<VideoFileProvider, String> VIDEO_TYPE_TO_URL_MAPPING = new HashMap<>();
    private final static Map<VideoFileProvider, Pattern> VIDEO_TYPE_TO_URL_ID_REGEXP_MAPPING = new HashMap<>();

    static {
        VIDEO_TYPE_TO_URL_MAPPING.put(VideoFileProvider.YOUTUBE, "https://i.ytimg.com/vi/%s/hqdefault.jpg");
        VIDEO_TYPE_TO_URL_MAPPING.put(VideoFileProvider.VIMEO, "https://vumbnail.com/%s_medium.jpg");
        VIDEO_TYPE_TO_URL_MAPPING.put(VideoFileProvider.WISTIA, "%s?image_crop_resized=480x360");

        VIDEO_TYPE_TO_URL_ID_REGEXP_MAPPING.put(VideoFileProvider.YOUTUBE,
            Pattern.compile("https?://www\\.youtube\\.com/embed/(.*)"));
        VIDEO_TYPE_TO_URL_ID_REGEXP_MAPPING.put(VideoFileProvider.VIMEO, Pattern.compile("https?://vimeo\\.com/(.*)"));
    }

    @Override
    public String getThumbnailUrl(SysFile sysFile) {
        VideoFileProvider videoType = VideoFileProvider.fromIndex(sysFile.getFileTypeIndex());
        String snapshotUrl = sysFile.getSnapshotUrl();

        if (!StringUtils.isEmpty(snapshotUrl)) {
            return getThumbNailFromSnapshotUrl(snapshotUrl, videoType);
        }

        String videoUrl = StringUtils.isEmpty(sysFile.getFileUrl()) ? sysFile.getFullFileUrl() : sysFile.getFileUrl();
        return getFullVideoUrl(videoUrl, videoType);
    }

    private String getFullVideoUrl(String videoUrl, VideoFileProvider videoType) {
        if (videoUrl == null || !VIDEO_TYPE_TO_URL_MAPPING.containsKey(videoType)) {
            return videoUrl;
        }

        videoUrl = cleanUpUrl(videoUrl, videoType);
        String fullVideoUrl = VIDEO_TYPE_TO_URL_MAPPING.getOrDefault(videoType, "%s");

        if (VIDEO_TYPE_TO_URL_ID_REGEXP_MAPPING.containsKey(videoType)) {
            return String.format(fullVideoUrl, extractVideoId(videoUrl, videoType));
        }

        return String.format(fullVideoUrl, videoUrl);
    }

    private String cleanUpUrl(String videoUrl, VideoFileProvider videoType) {
        if (VideoFileProvider.WISTIA == videoType) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(videoUrl);
            builder.replaceQuery(null);
            return builder.toUriString();
        }

        return videoUrl;
    }

    private String getThumbNailFromSnapshotUrl(String snapshotUrl, VideoFileProvider videoType) {
        if (videoType == VideoFileProvider.WISTIA) {
            return String.format(VIDEO_TYPE_TO_URL_MAPPING.get(videoType), snapshotUrl);
        }

        return snapshotUrl;
    }

    private String extractVideoId(String videoUrl, VideoFileProvider videoType) {
        Matcher matcher = VIDEO_TYPE_TO_URL_ID_REGEXP_MAPPING.get(videoType)
            .matcher(videoUrl);
        return matcher.find() ? matcher.group(1) : "";
    }
}
