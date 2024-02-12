package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.config.CourseStarConfiguration;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import com.threeatom.guidecore.mapper.GcUserVideoActionMapper;
import com.threeatom.guidecore.service.GcUserVideoActionService;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户对视频的操作，点赞 或者 收藏 等等  服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
@Service
public class GcUserVideoActionServiceImpl
        extends ServiceImpl<GcUserVideoActionMapper, GcUserVideoAction>
        implements GcUserVideoActionService {

    @Autowired private CourseStarConfiguration courseStarConfiguration;

    //    private static final Logger LOGGER =
    // LoggerFactory.getLogger(GcUserVideoActionServiceImpl.class);

    @Override
    @Transactional
    public boolean saveVideoAction(Integer vid, Integer userId, Integer type) {
        // TODO Auto-generated method stub
        GcUserVideoAction oldVideoAction = this.getOldVideoAction(vid, userId, type);
        boolean re = false;
        if (oldVideoAction != null) {
            re = this.deleteOldVideoAction(vid, userId, type);
        } else {
            GcUserVideoAction videoAction = new GcUserVideoAction();
            videoAction.setUserId(userId);
            videoAction.setType(type);
            videoAction.setVideoId(vid);
            re = this.saveOrUpdate(videoAction);
        }
        return re;
    }

    @Override
    public List<Integer> getVideoLikeNumsByVideoIds(List<Integer> videoIds) {
        if (videoIds.isEmpty()) {
            return null;
        } else {
            List<Integer> likeNums = new ArrayList<Integer>();
            for (Integer videoId : videoIds) {
                QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("video_id", videoId);
                queryWrapper.eq("type", TableConstant.gcUserVideoAction_type_like1);
                likeNums.add(this.count(queryWrapper));
            }
            return likeNums;
        }
    }

    @Override
    public List<GcUserVideoAction> getVideoActionListByUserId(Integer userId) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.list(queryWrapper);
    }

    @Override
    public List<GcUserVideoAction> getVideoActionListByVidAndUserId(Integer vid, Integer userId) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("video_id", vid);
        return this.list(queryWrapper);
    }

    @Override
    public GcUserVideoAction getFileActionListByFileIdAndUserId(Integer fileId, Integer userId) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("file_id", fileId);
        queryWrapper.eq("type", TableConstant.gcUserVideoAction_type_like1);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<GcUserVideoAction> getVideoActionListByFildId(List<Integer> fileId, Integer userId) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("file_id", fileId);
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("type", TableConstant.gcUserVideoAction_type_like1);
        return this.list(queryWrapper);
    }

    @Override
    public GcUserVideoAction getOldVideoAction(Integer vid, Integer userId, Integer type) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("type", type);
        queryWrapper.eq("video_id", vid);
        return getOne(queryWrapper);
    }

    @Override
    public GcUserVideoAction getOldChannelVideoAction(Integer fileId, Integer userId, Integer type) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("type", type);
        queryWrapper.eq("file_id", fileId);
        return getOne(queryWrapper);
    }

    @Override
    public boolean deleteOldVideoAction(Integer vid, Integer userId, Integer type) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("type", type);
        queryWrapper.eq("video_id", vid);
        return this.remove(queryWrapper);
    }

    @Override
    public boolean deleteChannelOldVideoAction(Integer fileId, Integer userId, Integer type) {
        QueryWrapper<GcUserVideoAction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("type", type);
        queryWrapper.eq("file_id", fileId);
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
            return vos.stream().collect(Collectors.groupingBy(GcUserVideoAction::getVideoId));
        }
        return new HashMap<>(0);
    }

    /**
     * 根据课程id，类型查询星级评价平均值和评论人数
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
                //            else {
                //                GcUserVideoAction videoAction = new GcUserVideoAction();
                //                videoAction.setSubjectId(gcUserVideoAction.getSubjectId());
                //                videoAction.setSubjectStarUsers(gcUserVideoAction.getSubjectStarUsers());
                //                videoAction.setSubjectStarAvg(gcUserVideoAction.getSubjectStarAvg());
                //            }
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
                    continue;
                }
                //            else {
                //                GcUserVideoAction videoAction = new GcUserVideoAction();
                //                videoAction.setSubjectId(gcUserVideoAction.getSubjectId());
                //                videoAction.setSubjectStarUsers(gcUserVideoAction.getSubjectStarUsers());
                //                videoAction.setSubjectStarAvg(gcUserVideoAction.getSubjectStarAvg());
                //            }
            }
        }
        return map;
    }

    @Override
    public Integer countLikeForVideo(Integer videoId) {
        return this.baseMapper.countLikeForVideo(videoId);
    }

    @Override
    public Integer countLikeForFile(Integer fileId) {
        return this.baseMapper.countLikeForFile(fileId);
    }

    @Override
    public List<GcUserVideoAction> countLikeForFiles(List<Integer> fileId) {
        if (null != fileId && fileId.size() > 0) {
            return this.baseMapper.countLikeForFiles(fileId);
        }
        return new ArrayList<>();
    }
}
