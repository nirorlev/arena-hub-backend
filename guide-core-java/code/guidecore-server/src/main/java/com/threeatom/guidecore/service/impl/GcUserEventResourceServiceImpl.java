package com.threeatom.guidecore.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.util.I18NUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.EventResType;
import com.threeatom.guidecore.constant.EventUnifyType;
import com.threeatom.guidecore.controller.user.vo.MessageFIlterVo;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.GcEvent;
import com.threeatom.guidecore.entity.GcMasterMessage;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserAnswer;
import com.threeatom.guidecore.entity.GcUserEventResource;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.mapper.GcUserEventResourceMapper;
import com.threeatom.guidecore.service.GcEventService;
import com.threeatom.guidecore.service.GcMasterMessageService;
import com.threeatom.guidecore.service.GcUserAnswerService;
import com.threeatom.guidecore.service.GcUserEventResourceService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import com.threeatom.system.service.SysSystemService;

/**
 * <p>
 * 事件资源文件 服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
@Service
public class GcUserEventResourceServiceImpl extends ServiceImpl<GcUserEventResourceMapper, GcUserEventResource> implements GcUserEventResourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserEventResourceServiceImpl.class);

    @Autowired
    private GcVideoService videoService;

    @Autowired
    private GcEventService eventService;

    @Autowired
    private GcUserService userService;
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private GcUserAnswerService userAnswerService;
    @Autowired
    private GcMasterMessageService masterMessageService;
    
    @Autowired
    private SysSystemService systemService;
	@Autowired
    private Environment env;

	@Override
	public List<GcUserEventResource> getEventResListForWorkBook(Integer eventId, Integer userId, Integer studentId,Integer masterId,HttpServletRequest request,Integer envType){
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
		List<GcUserEventResource> list =  this.baseMapper.getEventResListForWorkBook(eventId, userId, studentId,masterId,envType);
		for(GcUserEventResource resource: list) {
		    if ((null!=studentId&&studentId.equals(resource.getThisUser().getId()))||(null==studentId&&userId.equals(resource.getThisUser().getId()))){
		        resource.setIsTeacher(TableConstant.COMMON_ZERO);
            }else {
                resource.setIsTeacher(TableConstant.COMMON_ONE);
            }
			if(resource.getResFile()!=null) {
				sysFileService.getResFullUrl(resource.getResFile(),request);//完整路径
				resource.getResFile().setSnapshotUrl(sysFileService.getVideoSnapshotUrl(resource.getResFile()));//缩略图
			}
			
			
			if(resource.getThisUser()!=null && resource.getThisUser().getInfo()!=null && resource.getThisUser().getInfo().getAvatarFile()!=null)
				sysFileService.getResFullUrlSaveType2(resource.getThisUser().getInfo().getAvatarFile());
			
			if(resource.getTargetUser()!=null && resource.getTargetUser().getInfo()!=null && resource.getTargetUser().getInfo().getAvatarFile()!=null)
				sysFileService.getResFullUrlSaveType2(resource.getTargetUser().getInfo().getAvatarFile());
			
		}
		return list;
	}

    @Override
    public boolean saveEventAction(GcUserEventResource UserEventResource) {
        // TODO Auto-generated method stub
    	if(UserEventResource.getTargetId()!=null && UserEventResource.getTargetId() ==0)UserEventResource.setTargetId(null);
        return this.saveOrUpdate(UserEventResource);
    }


    @Override
    public List<GcUserEventResource> getEventResourceByEventIdAndUserId(Integer eventId, Integer userId) {
        return this.baseMapper.selectGetEventResourceByEventIdAndUserId(eventId, userId);
    }

    @Override
    public List<GcUserEventResource> selectGetEventResourceByEventIdAndTargetUserId(Integer eventId, Integer userId,Integer commentResourceFileId) {
        return this.baseMapper.selectGetEventResourceByEventIdAndTargetUserId(eventId,userId,commentResourceFileId);
    }

    @Override
    public List<Map<String, Object>> getUsersUploadResNumByUserIdsAndSubIds(List<Integer> subIds,
                                                                            List<Integer> userIds, String order) {
        // TODO Auto-generated method stub
        return this.baseMapper.selectUsersUploadResNumByUserIdsAndSubIds(subIds, userIds, order);
    }

    @Override
    public Integer deleteResourceFIle(Integer commentResourceFileId,Integer userId,Integer masterId) {
        QueryWrapper<GcUserEventResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("id",commentResourceFileId);
        queryWrapper.eq("master_id",masterId);
        return this.baseMapper.delete(queryWrapper);
    }


    @Override
    public List<Map<String, Object>> getUserListForRespond(List<Integer> userIds, Integer userId,Integer masterId) {
    	if(userIds==null) userIds=new ArrayList<Integer>();
    	if(userIds.size()==0) {
    		userIds.add(0);
    	}
    	
        return this.baseMapper.selectGetUserListForRespond(userIds, userId,masterId);
    }

    @Override
    public List<Map<String, Object>> getALLResourceListByEventIds(List<Integer> eventId,Integer studentId,Integer teacherId) {
        return this.baseMapper.getALLResourceListByEventIds(eventId,studentId,teacherId);
    }
    
    
    @Override
    public Integer countALLResourceListByGcMasterMessageTargetUserId(MessageFIlterVo messageFIlterVo) {
    	return this.baseMapper.countALLResourceListByGcMasterMessageTargetUserId(messageFIlterVo);
    }

    @Override
    public Integer countAnswerMessageListByGcMasterMessageTargetUserId(MessageFIlterVo messageFIlterVo) {
    	return this.baseMapper.countAnswerMessageListByGcMasterMessageTargetUserId(messageFIlterVo);
    }

	@Override
    public List<Map<String, Object>> getAnswerMessageListByGcMasterMessageTargetUserId(MessageFIlterVo messageFIlterVo, SysSystem sys, HttpServletRequest request) {
		PageParam pageParam = new PageParam(request);
    	if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
    	
    	List<Map<String, Object>> l = this.baseMapper.getAnswerMessageListByGcMasterMessageTargetUserId(messageFIlterVo,pageParam);
    	//组装头像
        if(l != null && l.size() > 0){
        	 for (Map<String, Object> videoResource : l) {
                 //组装头像
                 SysFile userFile = new SysFile();
                 userFile.setFileUrl(videoResource.containsKey("file_url")?(String) videoResource.get("file_url"):"");
                 userFile.setSaveType(videoResource.containsKey("save_type")?(Integer) videoResource.get("save_type"):2);
                 videoResource.put("userFullFileUrl", sysFileService.getResFullUrl(userFile, request));
             }
        }
        return l;
    }

    @Override
    public List<GcUserEventResource> selectUnCheckedTeacherMessage(Integer userId,List<Integer> eventIds,Integer masterId) {
        // TODO Auto-generated method stub
        return this.baseMapper.selectUnCheckedTeacherMessage(userId,eventIds,masterId);
    }

    @Override
    public List<GcUserEventResource> selectUnCheckedStudentMessage(Integer studentId,List<Integer> eventIds,Integer userId,Integer masterId) {
        // TODO Auto-generated method stub
        return this.baseMapper.selectUnCheckedStudentMessage(studentId,eventIds,userId,masterId);
    }
	
	//OSS 回调用
	@Override
	public GcUserEventResource uploadEventResourceFile(JSONObject jsonObject ) {
//		GcUserEventResource userEventResource = new GcUserEventResource();
		Integer eventId = jsonObject.getInteger("eventId");
		Integer resType = jsonObject.getInteger("resType");
		Integer targetId = jsonObject.getInteger("targetId");
		Integer timeNode  = jsonObject.getInteger("timeNode");
		Integer targetUserId  = jsonObject.getInteger("targetUserId");
		Integer masterId = jsonObject.getInteger("masterId");
		 String fileName = jsonObject.getString("originFileName");
//		 Integer masterId = getHeaderMasterId(request);
		 Integer userId=  jsonObject.getInteger("userId");
		 

	        ApiAssert.jsonValueIntegerIn(resType, EventResType.JSON_STR, "请填写" + EventResType.JSON_STR);
//	        String fileName = file.getOriginalFilename();
	        //获取事件
	        GcEvent event = eventService.getById(eventId);
	        if (event == null) throw new SystemException(I18NUtil.get("event.empty"));

//	        if (file == null) throw new SystemException("上传文件");

	        //上传视频
//	        GcUser user = this.getGcUser();
//	        SysSystem sys = this.getSystem();
	        GcUserEventResource userEventResource = new GcUserEventResource();


//	        if (resType.equals(EventResType.TEXT)) {//作业本上传内容类型
//	            throw new SystemException("这里需要上传文件");
//	        }
	        
//	        SysFile sysFile = sysFileService.saveRes(userId, sys, SysFilePath.event, ObjectStorageConstants.ALIYUN_OSS, fileName, file);
//	        sysFileService.getResFullUrl(sysFile, sys, request);
	        //保存视频
	        SysFile sysFile = videoService.unifiedFileSave(jsonObject);
	        
	        String sysIds = env.getProperty("systemId");
	    	int sysId = Integer.parseInt(sysIds);
	    	SysSystem sys = systemService.getSystemById(sysId);
	    	
	        sysFileService.getResFullUrlSaveType2(sysFile);
	        
	        //保存事件内容
	        userEventResource.setFileId(sysFile.getId());
	        userEventResource.setResFile(sysFile);
	        userEventResource.setUserId(userId);
	        userEventResource.setTargetUserId(targetUserId);
	        userEventResource.setTargetId(targetId);
	        userEventResource.setTimeNode(timeNode==null?-1:timeNode);
	        userEventResource.setType(resType);
	        userEventResource.setEventId(eventId);
	        
	        if(targetId!=null&&targetId>0) {
	        	GcUserEventResource eventResource=this.getById(targetId);
	        	userEventResource.setTargetUserId(eventResource.getUserId());
	        }
	        
	        if (resType.equals(EventResType.VIDEO_2))
	            userEventResource.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(userEventResource.getResFile()));
	        if (!this.saveEventAction(userEventResource))
	        	throw new SystemException("上传失败");
	            
	        
	        
	        //系统通知
	        
	        if (targetUserId == null) {
	            List<GcMasterMessage> masterMessageList = new ArrayList<>();
	            List<Integer> sendIds = userService.getTalkerIds(userId, masterId);
	            if (sendIds.size() > 0) {
	                for (Integer sendId : sendIds) {
	                    GcMasterMessage masterMessage = new GcMasterMessage();
	                    masterMessage.setEventType(resType);
	                    masterMessage.setMasterId(masterId);
	                    masterMessage.setUserId(userId);
	                    masterMessage.setTargetUserId(sendId);
	                    masterMessage.setResId(userEventResource.getId());
	                    masterMessageList.add(masterMessage);
	                }
	                masterMessageService.saveBatchMasterMessage(masterMessageList);
	            }
	        } else {
	            //发送事件通知
	            GcMasterMessage masterMessage = new GcMasterMessage();
	            masterMessage.setEventType(resType);
	            masterMessage.setMasterId(masterId);
	            masterMessage.setUserId(userId);
	            masterMessage.setTargetUserId(targetUserId);
	            masterMessage.setResId(userEventResource.getId());
	            masterMessageService.saveMasterMessage(masterMessage);
	        }
		
		return userEventResource;
	}
}
