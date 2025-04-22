package com.threeatom.guidecore.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserVideoPlay;
import com.threeatom.guidecore.entity.GcUserVideoPlaysNode;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.mapper.GcSubjectMapper;
import com.threeatom.guidecore.mapper.GcUserVideoPlayMapper;
import com.threeatom.guidecore.mapper.GcUserVideoPlaysNodeMapper;
import com.threeatom.guidecore.service.GcEventService;
import com.threeatom.guidecore.service.GcMasterMessageService;
import com.threeatom.guidecore.service.GcMasterService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.GcUserAnswerService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.GcUserVideoPlayService;
import com.threeatom.guidecore.service.GcUserVideoPlaysNodeService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;

@Service
public class GcUserVideoPlayServiceImpl extends ServiceImpl<GcUserVideoPlayMapper, GcUserVideoPlay> implements GcUserVideoPlayService {

    @Autowired
    GcSubjectService subjectService;

    @Autowired
    GcVideoService videoService;

    @Autowired
    GcUserService userService;

    @Autowired
    GcUserAccessService userAccessService;

    @Autowired
    GcEventService eventService;

    @Autowired
    GcUserAnswerService userAnswerService;

    @Autowired
    GcMasterMessageService masterMessageService;
    @Autowired
    GcMasterService masterService;
    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private GcUserVideoPlaysNodeService videoPlaysNodeService;

    @Autowired
    private GcUserVideoPlaysNodeMapper userVideoPlaysNodeMapper;

    @Resource
    private GcSubjectMapper gcSubjectMapper;


    @Override
    public JSONArray getSubAndVideoPlayListForHome(GcSubject subject, SysSystem sys,HttpServletRequest request) {
        List<GcSubject> parentList = null;

        if(subject.getNameIndex()!=null) {
            parentList=gcSubjectMapper.selectS1ByS0NameIndex(subject);

        }else {
            parentList=subjectService.getChildSubjectBySubId(subject.getId());
        }


        if (parentList.size() < 1) {
            return null;
        }
        List<Integer> parentSubIds = parentList.stream().map(GcSubject::getId).collect(Collectors.toList());
        List<Integer> subIdList = parentList.stream().map(GcSubject::getId).collect(Collectors.toList());

        //获取到主题下的所有视频ID
        List<GcVideo> videoList = videoService.getVideoListBySubIds(subIdList);
        if(videoList.size() > 0){
            for (GcVideo video: videoList) {
                sysFileService.getVideoSnapshotUrl(video);
            }
        }

        //拼数据
        JSONArray jarr = new JSONArray();
        for (GcSubject sub : parentList) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("subId", sub.getId());
            jsonObj.put("topicName", sub.getName());
            //查找sub下面子集
            List<GcSubject> childSubList = parentList.stream().filter(s -> s.getId().equals(sub.getId())).collect(Collectors.toList());

            for (GcSubject chilrenSub : childSubList) {
                //添加topic
                JSONObject topicObject = new JSONObject();

                JSONArray videoInfoArr = new JSONArray();
                //搜索子集下面的视频
                List<GcVideo> subVideoList = videoList.stream().filter(v -> v.getSubId().equals(chilrenSub.getId())).collect(Collectors.toList());

                jsonObj.put("list", subVideoList);


            }
            jarr.add(jsonObj);
        }
        return jarr;
    }

    @Override
    @Transactional
    public GcUserVideoPlay saveVideoPlayAndVideoPlaysNode(GcUserVideoPlay videoPlay) {

        Integer startTime = videoPlay.getVideoPlaysNode().getStartTime();
        //新增视频观看记录
        if(videoPlay.getId()==null){
            this.baseMapper.insert(videoPlay);
        }
        if(videoPlay.getId()!=null &&videoPlay.getPlayState()!=null){
            GcUserVideoPlay userVideoPlay = this.baseMapper.selectById(videoPlay.getId());
            userVideoPlay.setPlayState(videoPlay.getPlayState());
            this.saveOrUpdate(userVideoPlay);
        }

        GcUserVideoPlaysNode videoPlaysNode = new GcUserVideoPlaysNode();
        videoPlaysNode.setVideoplayId(videoPlay.getId());
        videoPlaysNode.setStartTime(startTime);
        userVideoPlaysNodeMapper.insert(videoPlaysNode);
        videoPlay.setVideoPlaysNode(videoPlaysNode);
        return videoPlay;
    }

    @Override
    public Integer getVideoPlayTime(List<GcUserVideoPlaysNode> videoPlaysNodes ){
    	if(videoPlaysNodes==null || videoPlaysNodes.size()==0) {
    		return null;
    	}
        Integer videoPlayTime = 0;
        Integer nodeTime = 0;

        GcUserVideoPlaysNode currentVideoPlaysNode = videoPlaysNodes.get(0);
        GcUserVideoPlaysNode nextVideoPlaysNode = null;
        for (int i = 0; i < videoPlaysNodes.size()-1; i++) {
            nextVideoPlaysNode = videoPlaysNodes.get(i + 1);
            if(currentVideoPlaysNode.getEndTime()==0||nextVideoPlaysNode.getEndTime()==0) continue;
            //判断节点在时间线上是否有交集
            if(currentVideoPlaysNode.getEndTime() < nextVideoPlaysNode.getStartTime()){
                nodeTime = currentVideoPlaysNode.getEndTime() - currentVideoPlaysNode.getStartTime();
                videoPlayTime += nodeTime;
                currentVideoPlaysNode = videoPlaysNodes.get(i + 1);
            }else{
                //节点间有交集则合并为一个节点
                int maxNodeTime = Math.max(currentVideoPlaysNode.getEndTime(), nextVideoPlaysNode.getEndTime());
                int minNodeTime = Math.min(currentVideoPlaysNode.getStartTime(), nextVideoPlaysNode.getEndTime());
                currentVideoPlaysNode = new GcUserVideoPlaysNode(minNodeTime,maxNodeTime);
            }
        }
        nodeTime = (currentVideoPlaysNode.getEndTime()==0) ? 0 : currentVideoPlaysNode.getEndTime() - currentVideoPlaysNode.getStartTime();

        return videoPlayTime += nodeTime;
    }

    @Override
    public List<GcUserVideoPlay> getVideoPlayByVideoId(Integer vid) {
        return this.baseMapper.getVideoPlayByVideoId(vid);
    }

    @Override
    public List<Integer> getWatchCompletedStudentBySubject(Integer masterId, List<Integer> subIds) {

        List<GcUser> list = new ArrayList<>();

        List<GcUser> userList = this.baseMapper.getWatchCompletedStudentBySubjectList(masterId,subIds);
        list.addAll(userList);
        list =list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(GcUser::getId))), ArrayList::new));
        return list.size()<=0?null:list.stream().map(GcUser::getId).collect(Collectors.toList());
    }

    @Override
    public  Map<Integer, GcUserVideoPlay> findVideoPalyStateByVideos(List<Integer> videoIds, Integer userId,Integer masterId) {
        return this.baseMapper.findVideoPalyStateByVideos(videoIds, userId,masterId);
    }

    @Override
    public Map<Integer, Object> getLastVideoPlayList(List<Integer> subId, Integer userId, Integer masterId) {
        return this.baseMapper.getLastVideoPlayList(subId,userId,masterId);
    }

    @Override
    public List<GcUserVideoPlay> findVideoPalyStateByVideosUsers(List<Integer> videoIds, List<Integer> userIdList, Integer masterId) {
        return this.baseMapper.findVideoPalyStateByVideosUsers(videoIds,userIdList,masterId);
    }



    @Override
    public List<GcUserVideoPlay> getUserVideoPlayList(List<Integer> videoIds, List<Integer> userIdList, Integer masterId) {
        return this.baseMapper.getUserVideoPlayList(videoIds,userIdList,masterId);
    }

    @Override
    public Map<Integer, Object> getVideoPlayCount(List<Integer> videoIds) {
        return this.baseMapper.getVideoPlayCount(videoIds);
    }


}
