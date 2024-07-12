package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.config.CourseStarConfiguration;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import com.threeatom.guidecore.enums.ReactionType;
import com.threeatom.guidecore.mapper.GcUserVideoActionMapper;
import com.threeatom.guidecore.service.GcUserVideoActionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GcUserVideoActionServiceImpl extends ServiceImpl<GcUserVideoActionMapper, GcUserVideoAction>
    implements GcUserVideoActionService {

    @Autowired
    private CourseStarConfiguration courseStarConfiguration;

    @Override
    @Transactional
    public boolean saveVideoAction(Integer contentId, Integer userId, Integer type) {
        GcUserVideoAction oldVideoAction = this.getOldVideoAction(contentId, userId, type);
        if (oldVideoAction != null) {
            return this.deleteOldVideoAction(contentId, userId, type);
        }

        GcUserVideoAction videoAction = new GcUserVideoAction();
        videoAction.setUserId(userId);
        videoAction.setType(type);
        videoAction.setVideoId(contentId);
            videoAction.setContentId(contentId);
        return this.saveOrUpdate(videoAction);
    }

    @Override
    public List<Integer> getVideoLikeNumsByVideoIds(List<Integer> videoIds) {
        if (videoIds.isEmpty()) {
            return null;
        }

        List<Integer> likeNums = new ArrayList<>();
        for (Integer videoId : videoIds) {
            QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("content_id", videoId);
            queryWrapper.eq("type", TableConstant.gcUserVideoAction_type_like1);
            likeNums.add(this.count(queryWrapper));
        }
        return likeNums;
    }

    @Override
    public List<GcUserVideoAction> getVideoActionListByUserId(Integer userId) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.list(queryWrapper);
    }

    @Override
    public List<GcUserVideoAction> getVideoActionListByVidAndUserId(Integer content_id, Integer userId) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("content_id", content_id);
        return this.list(queryWrapper);
    }

    @Override
    public GcUserVideoAction getFileActionListByFileIdAndUserId(Integer contentId, Integer userId) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("content_id", contentId);
        queryWrapper.eq("type", TableConstant.gcUserVideoAction_type_like1);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<GcUserVideoAction> getVideoActionListByFildId(List<Integer> contentIds, Integer userId) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("content_id", contentIds);
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("type", TableConstant.gcUserVideoAction_type_like1);
        return this.list(queryWrapper);
    }

    @Override
    public GcUserVideoAction getOldVideoAction(Integer vid, Integer userId, Integer type) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("type", type);
        queryWrapper.eq("content_id", vid);
        return getOne(queryWrapper);
    }

    @Override
    public GcUserVideoAction getOldChannelVideoAction(Integer contentId, Integer userId, Integer type) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("type", type);
        queryWrapper.eq("content_id", contentId);
        return getOne(queryWrapper);
    }

    @Override
    public boolean deleteOldVideoAction(Integer content_id, Integer userId, Integer type) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("type", type);
        queryWrapper.eq("content_id", content_id);
        return this.remove(queryWrapper);
    }

    @Override
    public boolean deleteChannelOldVideoAction(Integer contentId, Integer userId, Integer type) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("type", type);
        queryWrapper.eq("content_id", contentId);
        return this.remove(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> countTypeRateForVideo(Integer videoId, Integer type) {
        return this.baseMapper.countTypeRateForVideo(videoId, type);
    }

    /**
     * 根据课程id查询评论、点赞、星级评价
     */
    @Override
    public Map<Integer, List<GcUserVideoAction>> getVideoActionBySubject(Map<String, Object> params) {
        List<GcUserVideoAction> vos = this.baseMapper.getVideoActionBySubject(params);
        if (CollectionUtils.isNotEmpty(vos)) {
            return vos.stream().collect(Collectors.groupingBy(GcUserVideoAction::getContentId));
        }
        return new HashMap<>(0);
    }

    /**
     * 根据课程id，类型查询星级评价平均值和评论人数
     *
     * @param videoParams
     * @return
     */
    @Override
    public Map<Integer, GcUserVideoAction> getSubjectUserStar(Map<String, Object> videoParams) {
        Map<Integer, GcUserVideoAction> map = this.baseMapper.getSubjectUserStar(videoParams);
        List<GcUserVideoAction> starList = courseStarConfiguration.getStarcourselist();
        // 英国117门户的子域名造的假数据，其他的没有
        if (CollectionUtils.isNotEmpty(starList)) {
            for (GcUserVideoAction gcUserVideoAction : starList) {
                GcUserVideoAction userVideoAction = map.get(gcUserVideoAction.getSubjectId());
                if (Objects.nonNull(userVideoAction)) {
                    Long newStarusers =
                        gcUserVideoAction.getSubjectStarUsers() + userVideoAction.getSubjectStarUsers();
                    Double newAvgStar =
                        (gcUserVideoAction.getSubjectStarAvg() * gcUserVideoAction.getSubjectStarUsers()
                            + userVideoAction.getSubjectStarUsers() * userVideoAction.getSubjectStarAvg())
                            / newStarusers;
                    userVideoAction.setSubjectStarUsers(newStarusers);
                    userVideoAction.setSubjectStarAvg(newAvgStar);
                    userVideoAction.setSubjectId(gcUserVideoAction.getSubjectId());
                    map.put(gcUserVideoAction.getSubjectId(), userVideoAction);
                    continue;
                }
            }
        }
        return map;
    }

    @Override
    public Map<Integer, GcUserVideoAction> gvggetSubjectUserStar(Map<String, Object> videoParams) {
        Map<Integer, GcUserVideoAction> map = this.baseMapper.gvggetSubjectUserStar(videoParams);
        List<GcUserVideoAction> starList = courseStarConfiguration.getStarcourselist();
        // 英国117门户的子域名造的假数据，其他的没有
        if (CollectionUtils.isNotEmpty(starList)) {
            for (GcUserVideoAction gcUserVideoAction : starList) {
                GcUserVideoAction userVideoAction = map.get(gcUserVideoAction.getSubjectId());
                if (Objects.nonNull(userVideoAction)) {
                    Long newStarusers =
                        gcUserVideoAction.getSubjectStarUsers() + userVideoAction.getSubjectStarUsers();
                    Double newAvgStar =
                        (gcUserVideoAction.getSubjectStarAvg() * gcUserVideoAction.getSubjectStarUsers()
                            + userVideoAction.getSubjectStarUsers() * userVideoAction.getSubjectStarAvg())
                            / newStarusers;
                    userVideoAction.setSubjectStarUsers(newStarusers);
                    userVideoAction.setSubjectStarAvg(newAvgStar);
                    userVideoAction.setSubjectId(gcUserVideoAction.getSubjectId());
                    map.put(gcUserVideoAction.getSubjectId(), userVideoAction);
                }
            }
        }
        return map;
    }

    @Override
    public Integer countLikeForVideo(Integer videoId) {
        return this.baseMapper.countLikeForVideo(videoId);
    }

    @Override
    public Integer countLikeForFile(Integer contentId) {
        return this.baseMapper.countLikeForFile(contentId);
    }

    @Override
    public List<GcUserVideoAction> countLikeForFiles(List<Integer> contentIds) {
        if (CollectionUtils.isNotEmpty(contentIds)) {
            return this.baseMapper.countLikeForFiles(contentIds);
        }

        return new ArrayList<>();
    }

    @Override
    public GcUserVideoAction getActionByAction(GcUserVideoAction userVideoAction) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userVideoAction.getUserId());
        queryWrapper.eq("type", userVideoAction.getType());
        queryWrapper.eq("content_id", userVideoAction.getContentId());
        return getOne(queryWrapper);
    }

    @Override
    public boolean isLikedByUser(Integer contentId, Integer userId) {
        if (userId == null) {
            return false;
        }

        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("content_id", contentId);
        queryWrapper.eq("type", ReactionType.LIKE.getReactionCode());
        return this.count(queryWrapper) > 0;
    }

    @Override
    @Transactional
    public void updateReactions(Integer contentId, Integer userId, Map<String, Boolean> reactions) {
        deleteReactions(contentId, userId, reactions);
        saveReactions(contentId, userId, reactions);
    }

    private void saveReactions(Integer contentId, Integer userId, Map<String, Boolean> reactions) {
        reactions.entrySet().stream()
            .filter(Map.Entry::getValue)
            .forEach(
                entry -> saveVideoAction(contentId, userId, ReactionType.valueOf(entry.getKey()).getReactionCode()));
    }

    private void deleteReactions(Integer contentId, Integer userId, Map<String, Boolean> reactions) {
        reactions.entrySet().stream()
            .filter(entry -> !entry.getValue())
            .forEach(entry -> deleteOldVideoAction(contentId, userId,
                ReactionType.valueOf(entry.getKey()).getReactionCode()));
    }
}
