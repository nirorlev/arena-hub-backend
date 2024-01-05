package com.threeatom.guidecore.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcEventMapper;
import com.threeatom.guidecore.mapper.GcVideoCommentMapper;
import com.threeatom.guidecore.service.GcEventService;
import com.threeatom.guidecore.service.GcResourceService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserVideoActionService;
import com.threeatom.guidecore.service.GcVideoCommentService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 视频评论 服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
@Service
public class GcVideoCommentServiceImpl extends ServiceImpl<GcVideoCommentMapper, GcVideoComment> implements GcVideoCommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcVideoCommentServiceImpl.class);

    @Autowired
    private GcUserVideoActionService videoActionService;
    @Autowired
    private GcVideoService videoService;
    @Autowired
    private GcSubjectService subjectService;
    @Autowired
    private SysFileService fileService;
    @Autowired
    private GcEventService eventService;
    @Autowired
    private GcResourceService resourceService;

    @Autowired
    private GcEventMapper gcEventMapper;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private GcSubjectService gcSubjectService;


    @Override
    public boolean saveVideoComment(GcVideoComment videoComment) {
        // TODO Auto-generated method stub
        return this.saveOrUpdate(videoComment);
    }

    @Override
    public List<GcVideoComment> getMyVideoCommentByVideoIds(List<Integer> videoIds, Integer userId) {
        if (videoIds.size() < 1){
            return null;
        }
        QueryWrapper<GcVideoComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("video_id", videoIds);
        if(userId != null && userId > 0){
            queryWrapper.eq("user_id", userId);
        }
        queryWrapper.orderByAsc("create_time");
        return this.list(queryWrapper);
    }

    @Override
    public List<Integer> getCommentNumsByVideoIds(List<Integer> videoIds) {
        if (videoIds.isEmpty()) {
            return null;
        } else {
            List<Integer> commentNums = new ArrayList<Integer>();
            for (Integer videoId : videoIds) {
                QueryWrapper<GcVideoComment> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("video_id", videoId);
                commentNums.add(this.count(queryWrapper));
            }
            return commentNums;
        }
    }

    @Override
    public List<GcVideoComment> getAllCommentByVideoId(Integer vid,Integer limit) {
        return this.baseMapper.selectGetAllCommentByVideoId(vid,limit);//limit 5
    }

    @Override
    public List<GcVideoComment> getAllCommentByVideoIdAndUserId(Integer vid, Integer userId,Integer masterId) {
        return this.baseMapper.selectGetAllCommentByVideoIdAndUserId(vid,userId,masterId);
    }

    @Override
    public Integer deleteVideoComment(Integer commentId,Integer userId, Integer masterId) {
        QueryWrapper<GcVideoComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", commentId);
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("master_id",masterId);
        return this.baseMapper.delete(queryWrapper);
    }

    @Override
    public Message getCommentStream(Integer subId, GcUser user, GcSubject sub, SysSystem sys, HttpServletRequest request) {
        List<Integer> subIds = new ArrayList<>();
        List<GcVideo> videoList = new ArrayList<>();
        if(sub.getSubId() != null){
            subIds.add(subId);
            videoList = videoService.getVideoListBySubId(subId);
        }else{
            subIds = subjectService.getSubjectChildIds(subId);
            videoList = videoService.getVideoListBySubIds(subIds);
        }
        List<Integer> videoIds = videoList.stream().map(v -> v.getId()).collect(Collectors.toList());

        List<Integer> videoLikeNums = videoActionService.getVideoLikeNumsByVideoIds(videoIds);
        List<Integer> videoCommentNums = this.getCommentNumsByVideoIds(videoIds);
//        List<GcUserVideoAction> userVideoActions = videoActionService.getMyLikeVideoList(user.getId());
        List<GcUserVideoAction> userVideoActions = videoActionService.getVideoActionListByUserId(user.getId());
        
        List<GcSubject> childSub = subjectService.getSubListByIds(subIds,request);
        //查询所有的问题列表
        //List<GcEvent> allEvent = eventService.getEventListByVideoIds(videoIds);
        List<GcEvent> allEvent = null;
        if(videoIds!=null && videoIds.size()>0)allEvent = gcEventMapper.getEventListByVideoIds(videoIds, user.getId());
        if (videoLikeNums != null && videoLikeNums.size() > 0){
            for (int i = 0; i < videoLikeNums.size(); i++) {
                videoList.get(i).setLikeNum(videoLikeNums.get(i));
                videoList.get(i).setCommentNum(videoCommentNums.get(i));
            }
        }

        JSONArray jsonSubArray = new JSONArray();
        for (GcSubject childSubject : childSub) {
            JSONArray jsonVideoArray = new JSONArray();
            for (GcVideo video : videoList) {
                if(childSubject.getId().equals(video.getSubId())){
                    SysFile file = video.getVideoFile();
                    if (file != null) video.setSnapshotUrl(fileService.getVideoSnapshotUrl(file));
                    JSONObject jsonVideoObject = new JSONObject();
                    jsonVideoObject.put("id", video.getId());
                    jsonVideoObject.put("videoName", video.getVideoName());
                    jsonVideoObject.put("videoDesc", video.getVideoDesc());
                    jsonVideoObject.put("subId", video.getSubId());
                    jsonVideoObject.put("snapshotUrl", video.getSnapshotUrl());
                    jsonVideoObject.put("commentNum", video.getCommentNum());
                    jsonVideoObject.put("likeNum", video.getLikeNum());
                    jsonVideoObject.put("videoSource", video.getVideoSource());
                    jsonVideoObject.put("sourceUrl", video.getSourceUrl());
                    jsonVideoObject.put("videoFullUrl", video.getVideoFile());
                    List<GcUserVideoAction> videoActioList = userVideoActions.stream().filter(va -> va.getVideoId().equals(video.getId())).collect(Collectors.toList());
                    
                    Integer isLike =0;
                    GcUserVideoAction rateVideoAction=null;
                    for(GcUserVideoAction va:videoActioList) {
                    	if(va.getType().intValue()==TableConstant.gcUserVideoAction_type_like1)isLike=1;
                    	if(va.getType().intValue()==TableConstant.gcUserVideoAction_type_rate2)rateVideoAction=va;
                    }
                    jsonVideoObject.put("isLike", isLike);
                    jsonVideoObject.put("rateVideoAction", rateVideoAction);
                    
                    //我的评论，待删除	
                  List<GcVideoComment> myComment = this.getMyVideoCommentByVideoIds(videoIds, user.getId());
                    List<GcVideoComment> commentList = myComment.stream().filter(mc -> mc.getVideoId().equals(video.getId())).collect(Collectors.toList());
                    jsonVideoObject.put("commentList", commentList);
                    
                    
                    //添加问题列表
                    List<GcEvent> thisEventList = allEvent.stream().filter(ae->ae.getVideoId().equals(video.getId())).collect(Collectors.toList());
                    jsonVideoObject.put("eventList", thisEventList);
                    //添加资源列表
                    jsonVideoObject.put("resourceList", resourceService.getResByVid(video.getId()));
                    //添加所有人的评论
                    List<GcVideoComment> allCommentList = this.getAllCommentByVideoId(video.getId(),5);
                    for (GcVideoComment videoComment:
                         allCommentList) {
                        SysFile userFile = videoComment.getUserAvatarFile();
                        if (userFile != null) videoComment.setUserAvatarUrl(fileService.getResFullUrl(userFile, request));
                        SysFile commentFile = videoComment.getCommentFile();
                        if (commentFile != null) {
                        	fileService.getResFullUrl(commentFile, request);
                        	fileService.getVideoSnapshotUrl(commentFile);
                        }
                    }
                    jsonVideoObject.put("allCommentList",allCommentList);
                    jsonVideoArray.add(jsonVideoObject);
                }
            }
            JSONObject videoListObject = new JSONObject();
            videoListObject.put("id", childSubject.getId());
            videoListObject.put("subId0", childSubject.getFid());
            videoListObject.put("name", childSubject.getName());
            videoListObject.put("videoList", jsonVideoArray);
            jsonSubArray.add(videoListObject);
        }
//        List<GcSubject> list = gcSubjectService.selectBySubId(sub.getId());
//        List<SysFile> otherFileList = sysFileService.selectBySubId(sub.getId());
        return new Message().ok().addData("subList", jsonSubArray);
    }

	@Override
	public Integer countCommentForVideo(Integer videoId, Integer userId,Integer masterId) {
		return this.baseMapper.countCommentForVideo(videoId,userId,masterId);
	}

    /**
     * 根据视频id加载评论列表，以及评论人信息
     *
     * @param videoIds
     * @return
     */
    @Override
    public List<GcVideoComment> getVideoComments(List<Integer> videoIds,Integer masterId) {
        return this.baseMapper.getVideoComments(videoIds,masterId);
    }

    @Override
    public List<GcVideoComment> selectCommentByMainCommentId(Integer mainId) {
        return this.baseMapper.selectCommentByMainCommentId(mainId);
    }

    @Override
    public boolean insertComment(GcVideoComment gcVideoComment) {
        return this.baseMapper.insertComment(gcVideoComment);
    }
}
