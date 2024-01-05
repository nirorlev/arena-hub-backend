package com.threeatom.guidecore.service.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.exceptions.ClientException;
import com.threeatom.guidecore.constant.*;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.service.*;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.mapper.GcUserAccessExtMapper;
import com.threeatom.guidecore.mapper.GcUserAnswerMapper;
import com.threeatom.guidecore.mapper.GcUserMapper;
import com.threeatom.guidecore.mapper.GcUserVideoPlayMapper;

import static java.util.stream.Collectors.*;

@Service
public class GcTeacherDataServiceImpl extends ServiceImpl<GcUserMapper, GcUser> implements GcTeacherDataService{

	@Autowired
	private GcUserVideoPlayMapper userVideoPlayMapper;

	@Autowired
	private GcUserAnswerMapper userAnswerMapper;

	@Autowired
	private GcUserAccessExtMapper userAccessExtMapper;


	@Override
	public JSONObject getStudentBehaviorChartsData(List<Integer> userIds, List<Integer> subIds,Integer masterId,String startDate, String endDate, Message message,Integer managerId) {

		if(userIds==null || userIds.size()==0|| subIds==null || subIds.size()==0)
			return null;

		JSONObject jsonObject = new JSONObject();

		List<Map<String, Object>> videoPlayHistoryMap = userVideoPlayMapper.showVideoPlayHistoryNumByStudentIdsAndSubject(subIds, userIds,masterId, startDate, endDate);
		List<Map<String,Object>>loginNumMap = userAccessExtMapper.getUserLoginNum(userIds, startDate, endDate);
		List<Map<String,Object>> userAnswerNumMap = userAnswerMapper.getUserAnswerNum(userIds, startDate, endDate);
		List<Map<String,Object>> loginNumMapInCurrentPortal = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map: loginNumMap){
			if(masterId.equals(map.get("masterId"))){
				loginNumMapInCurrentPortal.add(map);
			}
		}
		jsonObject.put("LoginsNum",loginNumMap);
		jsonObject.put("WatchesNum",videoPlayHistoryMap);
		jsonObject.put("AnswersNum",userAnswerNumMap);
		jsonObject.put("loginNumMapInCurrentPortal", JSON.toJSON(loginNumMapInCurrentPortal));
		jsonObject.put("managerLoginNum",userAccessExtMapper.getByManagerId(managerId, startDate, endDate));

		return jsonObject;
		
	}







}
