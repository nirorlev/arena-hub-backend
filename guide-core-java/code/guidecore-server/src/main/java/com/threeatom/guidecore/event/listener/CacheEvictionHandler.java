package com.threeatom.guidecore.event.listener;

import com.threeatom.guidecore.constant.AuthorizationItemCacheName;
import com.threeatom.guidecore.event.entity.ChannelUpdatedEvent;
import com.threeatom.guidecore.event.entity.ContentGroupUpdatedEvent;
import com.threeatom.guidecore.event.entity.CourseUpdatedEvent;
import com.threeatom.guidecore.event.entity.UserUpdatedEvent;
import com.threeatom.guidecore.event.entity.VideoItemUpdatedEvent;
import com.threeatom.guidecore.service.GcVideoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheEvictionHandler {

    private final CacheManager cacheManager;
    private final GcVideoService videoService;

    @EventListener
    void handleUserUpdated(UserUpdatedEvent event) {
        evictCacheEntry(AuthorizationItemCacheName.PORTAL_USER, event.getId());
    }

    @EventListener
    void handleContentGroupUpdated(ContentGroupUpdatedEvent event) {
        evictCacheEntry(AuthorizationItemCacheName.CONTENT_GROUP, event.getId());
    }

    @EventListener
    void handleVideoUpdated(VideoItemUpdatedEvent event) {
        evictCacheEntry(AuthorizationItemCacheName.VIDEO, event.getId());
    }

    @EventListener
    void handleCourseUpdated(CourseUpdatedEvent event) {
        Integer courseId = event.getId();
        evictCacheEntry(AuthorizationItemCacheName.COURSE, courseId);
        List<Integer> courseVideoIds = videoService.getIdsBySubIds(List.of(courseId));
        courseVideoIds.forEach(videoId -> evictCacheEntry(AuthorizationItemCacheName.VIDEO, videoId));
    }

    @EventListener
    void handleChannelUpdated(ChannelUpdatedEvent event) {
        Integer channelId = event.getId();
        evictCacheEntry(AuthorizationItemCacheName.CHANNEL, channelId);
        List<Integer> channelVideoIds = videoService.getVideoIdsByChannelIds(List.of(channelId));
        channelVideoIds.forEach(videoId -> evictCacheEntry(AuthorizationItemCacheName.VIDEO, videoId));
    }

    private void evictCacheEntry(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            return;
        }

        log.warn("Cannot evict resource with id: {}. The cache with name '{}' is not found", key, cacheName);
    }

}
