package com.threeatom.guidecore.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonArray;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.guidecore.constant.*;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.controller.user.vo.videoLongVo;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.*;
import com.threeatom.guidecore.service.*;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.mapper.SysFileMapper;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.SystemException;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import com.threeatom.system.service.SysSystemService;
import com.threeatom.utils.TreeUtil;
import com.threeatom.utils.data.TreeNode;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-11
 */
@Service
public class GcSubjectServiceImpl extends ServiceImpl<GcSubjectMapper, GcSubject> implements GcSubjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcSubjectServiceImpl.class);

    @Autowired
    private GcVideoService videoService;
    @Autowired
    private GcUserAccessService userAccessService;
    @Autowired
    private GcEventService eventService;
    @Autowired
    private GcMasterMessageService masterMessageService;
    @Autowired
    private GcUserAnswerService userAnswerService;
    @Autowired
    private GcUserEventResourceService userEventResourceService;
    @Autowired
    private SysFileService sysFileService;
    @Resource
    private GcSubjectAssociationMapper gcSubjectAssociationMapper;

    @Autowired
    private GcEventMapper eventMapper;
    @Autowired
    private GcUserAccessPermissionMapper gcUserAccessPermissionMapper;
    @Autowired
    private GcAccessMapper gcAccessMapper;
    @Autowired
    private GcAccessService gcAccessService;
    @Autowired
    private GcUserAccessPermissionService gcUserAccessPermissionService;
    @Autowired
    private SysSystemService systemService;

    @Resource
    NewUiGcSubjectMapper newUiGcSubjectMapper;
    @Resource
    SysFileMapper sysFileMapper;
    @Resource
    GcSubjectMapper gcSubjectMapper;
    @Autowired
    private PtTagsService ptTagsService;
    @Autowired
    private RedisOperator redisOperator;
    @Autowired
    private GcUserService gcUserService;//用户服务类--统计参与人数
    @Autowired
    private GcUserVideoActionService videoActionService;//用户视频操作--查询评论、点赞、星级评价
    
    @Override
    public boolean saveSub(GcSubject sub) {
        // TODO Auto-generated method stub
        //if(sub.getId())
        this.formatSub(sub);
        return this.saveOrUpdate(sub);
    }

    private GcSubject formatSub(GcSubject sub) {
        //生成顶级sub
        if (sub.getFid()==null) {
            sub.setLevel(0);
            sub.setType(0);
            sub.setSubId(null);
        } else {
            List<GcSubject> list = this.getSubList(sub.getMasterId(),null);
            sub.setType(TableConstant.gcSubject_type_topic1);
            //计算当前属于第几层
            List<GcSubject> resultList = new ArrayList<GcSubject>();
            this.lookupParent(list, sub, resultList);
            //sub.setSubId(subId);
            if (resultList.size() < 2) throw new SystemException("找不到父节点");
            LOGGER.info(resultList.size() + "");
            sub.setLevel(resultList.size() - 1);
            sub.setSubId(resultList.get(resultList.size() - 1).getId());
        }

        //计算order
        if (sub.getId() != null) {
            List<GcSubject> orderList = new ArrayList<GcSubject>();
            QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
            queryWrapper.eq("master_id", sub.getMasterId());
            queryWrapper.eq("fid", sub.getFid());
            queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
            orderList = this.list(queryWrapper);
            sub.setOrder((orderList.size() + 1));
        }

        return sub;
    }

    /**
     * 搜索subject的父级,递归函数,直到为找不到父级
     *
     * @param list
     * @param subject
     * @param resultList
     * @return
     */
    private GcSubject lookupParent(List<GcSubject> list, GcSubject subject, List<GcSubject> resultList) {
        if (subject != null) {
            resultList.add(subject);
            for (GcSubject sub : list) {
                if (subject.getFid()!=null && subject.getFid().equals(sub.getId())) {
                    return this.lookupParent(list, sub, resultList);
                }
            }
            return null;
        } else {
            return null;
        }

    }

    /***
     * 向下搜索
     * @param list
     * @param subject
     * @param resultList
     * @return
     */
    private void lookupChildren(List<GcSubject> list, GcSubject subject, List<GcSubject> resultList) {
        if (subject != null) resultList.add(subject);
        //搜索subject下所有的子级
        for (GcSubject sub : list) {
            if (sub.getFid()!=null && sub.getFid().equals(subject.getId())) {
                this.lookupChildren(list, sub, resultList);
            }
        }
    }


    @Override
    public TreeNode<GcSubject> getTreeNode(Integer masterId) {
        // TODO Auto-generated method stub
        List<GcSubject> list = this.getSubList(masterId,null);
        GcSubject root = new GcSubject();
        root.setId(0);
        return TreeUtil.createTree(list, root);
    }
    
    @Override
    public List<GcSubject> getSubListWithImg(Integer masterId, SysSystem sys, HttpServletRequest request){
//    	 List<GcSubject> list =this.getSubListWithHidden(masterId);
    	 List<GcSubject> list =this.baseMapper.getSubjectListCommon(masterId, null, null);
    	 if(list != null && list.size() > 0){
             for(GcSubject li:list) {
                 if (null!=li.getCourseTags()&&li.getCourseTags().size()!=0) {
                     List lists = JSONArray.parseArray(li.getCourseTags().toJSONString());
                     HashSet hs = new HashSet(lists);
                     li.setCourseTags(JSONArray.parseArray(JSONObject.toJSONString(hs)));
                 }
                 sysFileService.getResFullUrl(li.getSubImgFile(), request);
                 if(CollectionUtils.isNotEmpty(li.getSubdetail_img_id()) && Objects.nonNull(li.getSubdetail_img_id().get("subDetailImgId"))){
                     SysFile sysFile = sysFileService.getById(Integer.parseInt(li.getSubdetail_img_id().get("subDetailImgId").toString()));
                     String fullUrl = sysFileService.getResFullUrl(sysFile,request);
                     li.setSubDetailImgUrl(fullUrl);
                 }
             }
         }
    	 return list;
    }
    
    @Override
    public List<GcSubject> getLevel0SubListWithImg(Integer masterId, SysSystem sys, HttpServletRequest request, PageParam pageParam,List<Integer> channelIds){
//          List<GcSubject> list = this.getLevel0SubLis(masterId);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();

        Integer state = TableConstant.gcSubject_state_visible_1;
        if (null!=request.getAttribute("state")){
            state = Integer.parseInt(request.getAttribute("state").toString());
        }
        Integer createUser = null;
        if (null!=request.getAttribute("createUser")){
            createUser = Integer.parseInt(request.getAttribute("createUser").toString());
        }
        String subjectName = null;
        if (null!=request.getAttribute("subjectName")){
            subjectName = request.getAttribute("subjectName").toString();
        }
        List<GcSubject> list = new ArrayList<>();
        if (null!=request.getAttribute("isPt")){
            if (null==request.getAttribute("type")){
                if (null==request.getAttribute("userId")){
                    if (pageNum > 0 && pageSize > 0) {
                        PageHelper.startPage(pageNum, pageSize);
                    }
                    list =this.baseMapper.getSubjectList(masterId, TableConstant.gcSubject_type_subject0,state,channelIds,createUser);
                }else {
                    //自己创建的课程
                    Integer userId = Integer.parseInt(request.getAttribute("userId").toString());
                    List<Integer> idLists = this.baseMapper.selectSubjectByCreateUser(masterId,userId);

                    if (pageNum > 0 && pageSize > 0) {
                        PageHelper.startPage(pageNum, pageSize);
                    }
                    list = this.baseMapper.selectSubjectPt(subjectName,null,state,createUser,masterId,Integer.parseInt(request.getAttribute("userId").toString()),channelIds,idLists);
                }
            }else {
                List<Integer> idLists = new ArrayList<>();
                Integer type = Integer.parseInt(request.getAttribute("type").toString());
                if(type.equals(TableConstant.COMMON_FOUR)){
                    List<GcUserAccessPermission> permissionList =  gcUserAccessPermissionService.getGroupMemberByUidList(Integer.parseInt(request.getAttribute("userId").toString()),masterId);
                    for (GcUserAccessPermission permission : permissionList) {
                        if(null!=permission.getMustSubjectJson()){
                            idLists.addAll(permission.getMustSubjectJson().toJavaList(Integer.class));
                        }

                    }
                }else if (type.equals(TableConstant.COMMON_ZERO)||type.equals(TableConstant.COMMON_TWO)){
                    idLists = baseMapper.getPublicSubjectIds(masterId);
                }

                if (pageNum > 0 && pageSize > 0) {
                    PageHelper.startPage(pageNum, pageSize);
                }
                list = this.baseMapper.selectSubjectPt(subjectName,Integer.parseInt(request.getAttribute("type").toString()),state,createUser,masterId,Integer.parseInt(request.getAttribute("userId").toString()),channelIds,idLists);
            }
        }else {
            if (pageNum > 0 && pageSize > 0) {
                PageHelper.startPage(pageNum, pageSize);
            }
            list =this.baseMapper.getSubjectList(masterId, TableConstant.gcSubject_type_subject0,state,channelIds,createUser);
        }
          if(list != null && list.size() > 0){
              for(GcSubject li:list) {
                  sysFileService.getResFullUrl(li.getSubImgFile(), request);
              }
          }

    	 return list;
    }

    @Override
    public List<GcSubject> selectSubjectByNewIndexHome(Integer masterId,Integer userId,PageParam pageParam){
        List<GcSubject> subjects = new ArrayList<>();
        if (null!=userId){

            List<Integer> idLists = this.baseMapper.selectSubjectByCreateUser(masterId,userId);
            Integer pageNum = pageParam.getPageNum();
            Integer pageSize=pageParam.getPageSize();
            if (pageNum > 0 && pageSize > 0) {
                PageHelper.startPage(pageNum, pageSize);
            }
            subjects = this.baseMapper.selectSubjectByNewIndexHome(userId,masterId,idLists);
            for (GcSubject subject : subjects) {
                subject.setIsToDo(TableConstant.COMMON_ZERO);
            }
            //may
            if (pageNum > 0 && pageSize > 0) {
                PageHelper.startPage(pageNum, pageSize);
            }
            List<GcSubject> maySubjects = this.baseMapper.selectMaySubjectByNewIndexHome(userId,masterId,idLists,TableConstant.COMMON_ZERO);
            subjects.addAll(maySubjects);
        }else {

        }
        return subjects;
    }

    public List<GcSubject> selectSubjectMay(Integer masterId,Integer userId,PageParam pageParam){
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcSubject> maySubjects = this.baseMapper.selectMaySubjectByNewIndexHome(userId,masterId,null,null);
        return maySubjects;
    }

    /*@Override
    public List<GcSubject> selectActiveSubject(Integer userId,Integer masterId,HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcSubject> level0sublist = this.baseMapper.selectActiveSubject(userId,masterId);
        level0sublist = getSubjectInfos(level0sublist,userId,masterId,request);
        return level0sublist;
    }*/

    @Override
    public Integer inProgressNum(Integer userId,Integer masterId){
        return this.baseMapper.selectActiveSubject(userId,masterId,null,2,null).size();
    }

    @Override
    public Integer getSubjectNameIndex(Integer masterId, String nameIndex) {
        return this.baseMapper.getSubjectNameIndex(masterId,nameIndex);
    }

    @Override
    public Integer getNewMyAssignmentNew(Integer masterId,Integer userId){
        return this.baseMapper.getNewMyAssignmentNew(masterId,userId);
    }

    @Override
    public void initJit(){
        for (int i = 0; i < 30000; i++) {
            // 加入一些无关紧要的操作
            int result = 1 + 1;
        }
    }

    @Override
    public List<Integer> getUserCreateSubject(Integer masterId, Integer userId) {
        return this.baseMapper.getUserCreateSubject(masterId,userId);
    }

    @Override
    public List<Integer> getUserCreateSubjectAdmin(Integer masterId, Integer userId) {
        return this.baseMapper.getUserCreateSubjectAdmin(masterId,userId);
    }

    @Override
    public List<Integer> getUserPublicSubject(Integer masterId, Integer userId){
        return this.baseMapper.getUserPublicSubject(masterId,userId);
    }

    @Override
    public List<GcSubject> selectActiveSubject(Integer userId,Integer masterId,Integer subjectState,String name,HttpServletRequest request) {
        //查询顶级组must课程Ids
        List<GcSubject> orgMustIds = this.baseMapper.selectOrgMustJsonArrayList(userId,masterId);
        List<Integer> orgMustSubjectIds = new ArrayList<>();
        if (null!=orgMustIds){
            for (GcSubject orgMustId : orgMustIds) {
                if (null!=orgMustId.getMustJsonArray()){
                    orgMustSubjectIds.addAll(orgMustId.getMustJsonArray().toJavaList(Integer.class));
                }
            }
        }
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        //查询课程
        List<GcSubject> level0sublist = this.baseMapper.selectActiveSubject(userId,masterId,name,subjectState,orgMustSubjectIds);
        //level0sublist = getSubjectInfos(level0sublist,userId,masterId,request);
        //putSubjectIdentifyings(level0sublist,getNewAssignments(masterId,userId));
        for (GcSubject subject : level0sublist) {
            subject.setIsToDo(TableConstant.COMMON_ZERO);
        }
        return level0sublist;
    }


    @Override
    public List<GcSubject> selectDiscoverSubject(Integer userId, Integer masterId,Integer subjectState,String name,HttpServletRequest request) {


        //查询顶级组may课程Ids
        List<GcSubject> orgMayIds = this.baseMapper.selectOrgMayJsonArrayList(userId,masterId);
        List<Integer> orgMaySubjectIds = new ArrayList<>();
        for (GcSubject orgMustId : orgMayIds) {
            orgMaySubjectIds.addAll(orgMustId.getMayJsonArray().toJavaList(Integer.class));
        }
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcSubject> level0sublist = this.baseMapper.selectDiscoverSubject(userId,masterId,name,subjectState,orgMaySubjectIds);

        //putSubjectIdentifyings(level0sublist,getNewAssignments(masterId,userId));
        return level0sublist;
    }

    @Override
    public List<GcSubject> selectCompletedSubject(Integer userId, Integer masterId,Integer subjectState,String name,HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcSubject> level0sublist = this.baseMapper.selectCompletedSubject(userId,masterId,name,subjectState);
        //level0sublist = getSubjectInfos(level0sublist,userId,masterId,request);

        //putSubjectIdentifyings(level0sublist,getNewAssignments(masterId,userId));
        return level0sublist;
    }


    @Override
    public List<GcSubject> selectAllCourseSubject(Integer userId,Integer masterId,String name,HttpServletRequest request,Integer orderType){
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcSubject> level0sublist = this.baseMapper.selectAllCourseSubject(userId,masterId,name,orderType);
        //level0sublist = getSubjectInfos(level0sublist,userId,masterId,request);

        //putSubjectIdentifyings(level0sublist,getNewAssignments(masterId,userId));
        return level0sublist;
    }

    @Override
    public List<GcSubject> selectCompanyResourcesSubject(Integer userId,Integer masterId,String name,HttpServletRequest request){

        //查询顶级组may课程Ids
        List<GcSubject> orgMayIds = this.baseMapper.selectOrgMayJsonArrayList(userId,masterId);
        List<Integer> orgMaySubjectIds = new ArrayList<>();
        for (GcSubject orgMustId : orgMayIds) {
            orgMaySubjectIds.addAll(orgMustId.getMayJsonArray().toJavaList(Integer.class));
        }
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcSubject> level0sublist = this.baseMapper.selectCompanyResourcesSubject(userId,masterId,name,orgMaySubjectIds);
        //level0sublist = getSubjectInfos(level0sublist,userId,masterId,request);

        //putSubjectIdentifyings(level0sublist,getNewAssignments(masterId,userId));
        return level0sublist;
    }

    /**
     * 返回标识
     */
    public List<GcSubject> putSubjectIdentifyings(List<GcSubject> subjects,List<Integer> newAssignmentIds){
        for (GcSubject subject : subjects) {
            List<Integer> identifyings = new ArrayList<>();

            if (null!=subject.getIdentifying()){
                identifyings.add(subject.getIdentifying());

            }

            if (null==subject.getState()||subject.getState().equals(TableConstant.COMMON_ZERO)){
                identifyings.add(TableConstant.COMMON_THREE);
            }

            if (null!=subject.getIsToDo()&&newAssignmentIds.contains(subject.getId())){
                identifyings.add(TableConstant.COMMON_ZERO);
            }

            if (null!=subject.getCertificatesFlag()&&subject.getCertificatesFlag().equals(TableConstant.COMMON_ONE)){
                identifyings.add(TableConstant.COMMON_FOUR);
            }

            /*if (identifyings.size()==TableConstant.COMMON_ZERO){
                identifyings.add(TableConstant.COMMON_ONE);
            }*/
            subject.setIdentifyings(identifyings);
        }
        return subjects;
    }

    /**
     * 获取newAssignments数据
     * @param masterId
     * @param userId
     * @return
     */
    public List<Integer> getNewAssignments(Integer masterId,Integer userId){
        return this.baseMapper.getNewAssignments(userId,masterId);
    }

    @Override
    public List<GcSubject> selectFromMyTeamSubject(Integer userId,Integer masterId,String name,HttpServletRequest request){
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcSubject> level0sublist = this.baseMapper.selectFromMyTeamSubject(userId,masterId,name);
        //level0sublist = getSubjectInfos(level0sublist,userId,masterId,request);
        //putSubjectIdentifyings(level0sublist,getNewAssignments(masterId,userId));
        return level0sublist;
    }

    @Override
    public List<GcSubject> selectCreatedByTeams(Integer userId,Integer masterId,String name,HttpServletRequest request,List<String> groupCodeList,Integer orderType){
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcSubject> level0sublist = this.baseMapper.selectCreatedByTeamsSubject(userId,masterId,name,groupCodeList,orderType);
        for (GcSubject subject : level0sublist) {
            subject.setMode(TableConstant.COMMON_ZERO);
        }
        //putSubjectIdentifyings(level0sublist,getNewAssignments(masterId,userId));
        return level0sublist;
    }

    @Override
    public List<GcSubject> selectCreateByTeamsOrgAdmin(Integer userId, Integer masterId, String name,HttpServletRequest request,List<String> groupCodeList,Integer orderType) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcSubject> orgAdminSubject = this.baseMapper.selectCreateByTeamsOrgAdmin(userId,masterId,name,groupCodeList,orderType);
        for (GcSubject subject : orgAdminSubject) {
            subject.setMode(TableConstant.COMMON_ONE);
        }
        //putSubjectIdentifyings(orgAdminSubject,getNewAssignments(masterId,userId));
        return orgAdminSubject;
    }

    @Override
    public List<GcSubject> selectPublishedSubject(Integer userId,Integer masterId,String name,HttpServletRequest request){
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcSubject> level0sublist = this.baseMapper.selectPublishedSubject(userId,masterId,name);
        //level0sublist = getSubjectInfos(level0sublist,userId,masterId,request);

        //putSubjectIdentifyings(level0sublist,getNewAssignments(masterId,userId));
        return level0sublist;
    }

    @Override
    public List<GcSubject> selectDraftsSubject(Integer userId, Integer masterId,String name,HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcSubject> level0sublist = this.baseMapper.selectDraftsSubject(userId,masterId,name);
        //level0sublist = getSubjectInfos(level0sublist,userId,masterId,request);

        //putSubjectIdentifyings(level0sublist,getNewAssignments(masterId,userId));
        return level0sublist;
    }

    @Override
    public List<GcSubject> getSubjectInfoByList(List<GcSubject> level0sublist,Integer masterId,Integer userId,HttpServletRequest request){

        putSubjectIdentifyings(level0sublist,getNewAssignments(masterId,userId));

        List<Integer>level0subIds = level0sublist.stream().map(GcSubject::getId).collect(Collectors.toList());
        List<Integer> subImgIds = level0sublist.stream().map(GcSubject::getSubImgId).collect(Collectors.toList());
        List<SysFile> sysFileList = new ArrayList<>();
        if (TableConstant.COMMON_ZERO!=subImgIds.size()){
            sysFileList = sysFileService.listByIds(subImgIds);
        }
        //封面
        Map<Integer,SysFile> sysFileMap = sysFileList.stream().collect(Collectors.toMap(SysFile::getId,SysFile -> SysFile, (key1, key2) -> key2, LinkedHashMap::new));
        //点赞
        Map<Integer, GcUser> subjectUsers = gcUserService.getWatchedUserNum(level0subIds,masterId);
        //评分
        Map<String, Object> videoParams = new HashMap<>(2);
        Integer ids = TableConstant.COMMON_ZERO;
        videoParams.put("ids",ids);
        videoParams.put("subjectIds", level0subIds);
        Map<Integer, GcUserVideoAction> subjectUserStar = new HashMap<>();
        subjectUserStar = videoActionService.getSubjectUserStar(videoParams);
        //视频时长和数量
        Map<Integer, videoLongVo> videosTotalLongMap = videoService.getVideoLongMapBySubjectId(level0subIds);

        //组装信息
        if(level0sublist != null && level0sublist.size() > 0) {
            for (GcSubject li : level0sublist) {
                //封面
                if(null==li.getSubImgId()) {
                    //continue;
                }
                if (null!=sysFileMap.get(li.getSubImgId())){
                    SysFile file = sysFileMap.get(li.getSubImgId());
                    file.setFullFileUrl(sysFileService.getResFullUrl(file, request));

                    li.setSubImgFile(file);
                }
                //参与人数
                if(CollectionUtils.isNotEmpty(subjectUsers)) {
                    GcUser gcUser = subjectUsers.get(li.getId());
                    if (null != gcUser) {
                        li.setSubjectUsers(gcUser.getSubjectUsers());
                    }else {
                        li.setSubjectUsers(TableConstant.COMMON_ZERO);
                    }
                }
                //视频长度数量
                if (null!=videosTotalLongMap.get(li.getId())){
                   videoLongVo vo =  videosTotalLongMap.get(li.getId());
                    li.setVideosTotalLong(vo.getVideoLong());
                    li.setVideosTotalNum(vo.getVideoCount());
                }
                //评分
                GcUserVideoAction videoActions = subjectUserStar.get(li.getId());
                if(videoActions != null) {
                    //按type 进行分组
                    // 1.2k type=3的平均值 1.2k是打星的总人数
                    li.setStarValue(videoActions.getSubjectStarAvg());//星级平均值
                    // 打星总人数
                    li.setStarUsers(videoActions.getSubjectStarUsers());
                }else {
                    li.setStarValue(TableConstant.starValue0);//星级平均值
                    li.setStarUsers(TableConstant.starUsers);
                }
                //进度
                li.setPercents(new BigDecimal(li.getVideoProgressPercent()));
            }
        }

        //Map<Integer,GcSubject> subjectMap = level0sublist.stream().collect(Collectors.toMap(GcSubject::getId, (p) -> p));
        return level0sublist;
    }


    public List<GcSubject> getSubjectInfos(List<GcSubject> level0sublist,Integer userId,Integer masterId,HttpServletRequest request){
        List<Integer> level0subIds = level0sublist.stream().map(GcSubject::getId).collect(Collectors.toList());
        List<Integer> subImgIds = level0sublist.stream().map(GcSubject::getSubImgId).collect(Collectors.toList());

        //获取封面
        List<SysFile> sysFileList = new ArrayList<>();
        if (TableConstant.COMMON_ZERO!=subImgIds.size()){
            sysFileList = sysFileService.listByIds(subImgIds);
        }
        Map<Integer,SysFile> sysFileMap = sysFileList.stream().collect(Collectors.toMap(SysFile::getId,SysFile -> SysFile, (key1, key2) -> key2, LinkedHashMap::new));
        //点赞
        Map<Integer, GcUser> subjectUsers = gcUserService.getWatchedUserNum(level0subIds,masterId);
        //视频信息
        List<Integer> videoIdlist = videoService.getVideoIdListBySubId(level0subIds);
        List<GcVideo> videoList = videoService.getVideoLongListByVideoId(videoIdlist);
        if (null!=userId){
            videoList = videoService.buildVideoInfo(userId,null,videoList,masterId,request,EnvType.PT.getCode());
        }
        Map<Integer,List<GcVideo>> groupBySubId = videoList.stream().filter(e -> null!=e.getSubjectSubId()).collect(Collectors.groupingBy(GcVideo::getSubjectSubId));
        Map<String, Object> videoParams = new HashMap<>(2);
        Integer ids = TableConstant.COMMON_ZERO;
        videoParams.put("ids",ids);
        videoParams.put("subjectIds", level0subIds);
        Map<Integer, GcUserVideoAction> subjectUserStar = new HashMap<>();
        subjectUserStar = videoActionService.getSubjectUserStar(videoParams);

        //组装信息
        if(level0sublist != null && level0sublist.size() > 0){
            for(GcSubject li:level0sublist) {
                if(null==li.getSubImgId()) {
                    //continue;
                }
                if (null!=sysFileMap.get(li.getSubImgId())){
                    SysFile file = sysFileMap.get(li.getSubImgId());
                    file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
                    li.setSubImgFile(file);
                }

                if(CollectionUtils.isNotEmpty(subjectUsers)) {
                    GcUser gcUser = subjectUsers.get(li.getId());
                    if (null != gcUser) {
                        li.setSubjectUsers(gcUser.getSubjectUsers());
                    }else {
                        li.setSubjectUsers(TableConstant.COMMON_ZERO);
                    }
                }

                //视频长度
                if(groupBySubId.get(li.getId())!=null){
                    List<GcVideo> gcVideos = groupBySubId.get(li.getId());
                    Integer totalSeconds = gcVideos.stream().filter(a -> a.getVideoLong()!=null).mapToInt(GcVideo::getVideoLong).sum();
                    li.setVideosTotalLong(totalSeconds);
                    li.setVideosTotalNum(gcVideos.size());
                }

                GcUserVideoAction videoActions = subjectUserStar.get(li.getId());
                if(videoActions != null) {
                    //按type 进行分组
                    // 1.2k type=3的平均值 1.2k是打星的总人数
                    li.setStarValue(videoActions.getSubjectStarAvg());//星级平均值
                    // 打星总人数
                    li.setStarUsers(videoActions.getSubjectStarUsers());
                }else {
                    li.setStarValue(TableConstant.starValue0);//星级平均值
                    li.setStarUsers(TableConstant.starUsers);
                }

                if (null!=userId) {
                    if (null!=groupBySubId.get(li.getId())){
                        Map<Integer, List<GcVideo>> sub1Map = groupBySubId.get(li.getId()).stream().collect(Collectors.groupingBy(GcVideo::getSubId));
                        List<GcSubject> twoSubject = buildSubject1(sub1Map);
                        SubjectTotals subjectTotals = calcTotals(twoSubject, userId, true, masterId, EnvType.PT.getCode());
                        li.setPercents(new BigDecimal(subjectTotals.getTotalProgressPercent()));
                    }
                }

            }
        }
        return level0sublist;
    }

    private Map<Integer, List<GcVideo>> subjectVideos(List<GcSubject> subjects) {
        Map<Integer, List<GcVideo>> map = new HashMap<>(subjects.size());
        for(GcSubject subject : subjects){
            map.put(subject.getId(), subject.getGcVideos());
        }
        return map;
    }

    private List<GcSubject> buildSubject1(Map<Integer, List<GcVideo>> sub1Map) {
        List<GcSubject> subjects = new ArrayList<>(sub1Map.keySet().size());
        for(Map.Entry<Integer, List<GcVideo>> sub1 : sub1Map.entrySet()){
            GcSubject subject = new GcSubject();
            subject.setId(sub1.getKey());//二级课程id
            List<GcVideo> value = sub1.getValue();
            int min = value.stream().mapToInt(GcVideo::getCompleteStatus).min().getAsInt();
            int max = value.stream().mapToInt(GcVideo::getCompleteStatus).max().getAsInt();
            subject.setSubjectCompleteStatus(TableConstant.SUBJECT_COMPLETE_STATUS1);//
            if(min == max){//相同就设置为一个值
                subject.setSubjectCompleteStatus((short)max);
            }
            subject.setGcVideos(value);//视频列表
            subjects.add(subject);
        }
        return subjects;
    }

    //计算进度
    private SubjectTotals calcTotals(List<GcSubject> subjects, Integer userId, boolean ifStudent, Integer masterId,Integer envFlag) {
        SubjectTotals totals = new SubjectTotals();
        if(CollectionUtils.isNotEmpty(subjects)){
            Map<Integer, List<GcVideo>> videoCompleteStatusBySubject =subjectVideos(subjects);
            //1、计算总的进度 查询这个课程下的所有
            if(MapUtils.isNotEmpty(videoCompleteStatusBySubject)){
                List<GcSubject>  vos = new ArrayList<>(subjects.size());
                List<GcVideo> gcVideos = new ArrayList<>();
                for(Map.Entry<Integer, List<GcVideo>> map : videoCompleteStatusBySubject.entrySet()){
                    //计算单个课程是否完成，课程下的所有视频都完成，则该课程前端显示绿色，黄色、灰色
                    //根据视频来设置二级课程的完成情况
                    GcSubject vo = new GcSubject();
                    vo.setId(map.getKey());
                    vo.setSubjectCompleteStatus(TableConstant.SUBJECT_COMPLETE_STATUS0);
                    List<GcVideo> tmpVideos = map.getValue();
                    if(CollectionUtils.isNotEmpty(tmpVideos)){
                        int min = tmpVideos.stream().mapToInt(GcVideo::getCompleteStatus).min().getAsInt();
                        int max = tmpVideos.stream().mapToInt(GcVideo::getCompleteStatus).max().getAsInt();
                        vo.setSubjectCompleteStatus(TableConstant.SUBJECT_COMPLETE_STATUS1);//
                        if(min == max){//相同就设置为一个值
                            vo.setSubjectCompleteStatus((short)max);
                        }
                        gcVideos.addAll(tmpVideos);
                    }
                    vos.add(vo);
                }
                //返回值里添加排序字段
                for (GcSubject gcSubject : subjects) {
                    for (GcSubject gcSub : vos) {
                        if (gcSub.getId() == gcSubject.getId()) {
                            gcSub.setOrder(gcSubject.getOrder());
                        }
                    }
                }
                //排序
                if(!vos.stream().filter(e->e.getOrder()==null).findAny().isPresent()) {
                    vos = vos.stream().sorted(Comparator.comparing(GcSubject::getOrder)).collect(Collectors.toList());
                }
                totals.setSubjects(vos);

                List<GcVideo> playState1 = gcVideos.stream().filter(e -> TableConstant.VIDEO_COMPLETE_STATUS2 == e.getCompleteStatus()).collect(Collectors.toList());
//				if (envFlag.equals(EnvType.PT.getCode())) {
                Integer sumTasks = gcVideos.stream().filter(e -> null != e.getAnsweredSumNums()).mapToInt(GcVideo::getAnsweredSumNums).sum();
                Integer sumVideos = gcVideos.size();
                Integer answeredTaksNum = gcVideos.stream().filter(e -> null != e.getAnsweredNums()).mapToInt(GcVideo::getAnsweredNums).sum();
                List<GcVideo> videos = gcVideos.stream().filter(e -> null != e.getPlayState() & ("1").equals(e.getPlayState())).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(videos) || TableConstant.COMMON_ZERO!=sumTasks) {
                    BigDecimal percent = new BigDecimal(answeredTaksNum + videos.size()).divide(new BigDecimal(sumTasks + sumVideos), 2, BigDecimal.ROUND_DOWN);
                    totals.setTotalProgressPercent(percent.multiply(BigDecimal.valueOf(100)).intValue());//视频总数
                }else {
                    totals.setTotalProgressPercent(0);
                }
                totals.setLessonsTotalProgress(gcVideos.size());
                totals.setLessonsCompleteProgress(playState1.size());


                //这里是课程的播放进度，重新计算
//				totals.setTotalProgressPercent(calcSubjectProgressPercent(vos));//所有视频的播放进度百分比，可能有用
                if (!ifStudent) return totals;

                if(CollectionUtils.isNotEmpty(gcVideos)){
                    List<Integer> videoIds = gcVideos.stream().map(GcVideo::getId).collect(Collectors.toList());
                    //2、视频播放分钟数和总的时长
                    //查询视频总时长
                    totals.setVideoTotalProgress(videoService.sumVideoLongByIdUser(videoIds, userId));
                    //查询已看分钟数 一个视频多次看取endtime最大的一个，一个视频可能看多次
                    if(Objects.nonNull(userId)) {
                        totals.setVideoCompleteProgress(videoService.sumPlayVideoLongByIdUser(videoIds, userId));
                    }

                    //4、已回答问题数和问题总数
                    List<GcEvent> eventList = eventService.getEventListByVideoIds(videoIds,userId);
                    totals.setTaskTotalProgress(eventList.size());//问题总数
                    if(Objects.nonNull(userId)) {
                        List<GcEvent> eventAnswers = eventService.findEventAnswerByVideoIdsUser(videoIds, userId, masterId, envFlag);//查询视频
                        if (CollectionUtils.isNotEmpty(eventAnswers)) {
                            Set<GcEvent> collect = eventAnswers.stream().filter(e -> com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(e.getAnswerJson())).collect(Collectors.toSet());
                            totals.setTaskCompleteProgress(collect.size());//已完成问题数
                        }
                    }
                }
            }
        }
        return totals;
    }



    @Override
    public List<GcSubject> setSubListImg(List<GcSubject> list,SysSystem sys, HttpServletRequest request) {
    	if(list != null && list.size() > 0){
            for(GcSubject li:list) {
                if(li.getSubImgId()==null ) {
                    continue;
                }
                SysFile file=sysFileService.getById(li.getSubImgId());
                if(file!=null) {
                    file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
                    li.setSubImgFile(file);
                }

            }
        }
        return list;
    }
    
    //编码不合理，需改造
    @Override
    public List<GcSubject> getSubListWithImgByIds(List<Integer> subIds,SysSystem sys, HttpServletRequest request,Integer masterId) {
        List<GcSubject> list = new ArrayList<GcSubject>();
        for (Integer subId : subIds) {
            //GcSubject subject = this.getById(subId);
            GcSubject subject = this.baseMapper.selectTagsById(subId,masterId);
            if(null!=subject) {
                List<GcSubject> GcSubjects = this.baseMapper.selectListByMasterId(subject.getMasterId());
                this.lookupChildren(GcSubjects, subject, list);
            }
        }
        if(list != null && list.size() > 0){
            for(GcSubject li:list) {
                if(li.getSubImgId()==null ) {
                    continue;
                }
                SysFile file=sysFileService.getById(li.getSubImgId());
                if(file!=null) {
                    file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
                    li.setSubImgFile(file);
                }

            }
        }
        return list;
    }

    @Override
    public List<GcSubject> getLevel0SubLis(Integer masterId){
    	 QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
         queryWrapper.eq("master_id", masterId);
         queryWrapper.eq("level", 0);
         queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
         queryWrapper.orderByAsc("`order`");
         List<GcSubject> list = this.list(queryWrapper);
         return list;
    }
    
    @Override
    public List<GcSubject> getSubListWithHidden(Integer masterId) {
    	//显示隐藏课程
        // TODO Auto-generated method stub
        QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.orderByAsc("`order`");
        return this.list(queryWrapper);
    }

     @Override
     public List<GcSubject> selectSubjectAssociation(Integer masterId,List<Integer> subIds,boolean ifLevel0) {
   	  List<GcSubject> list = this.baseMapper.selectSubjectAssociation(masterId,subIds,ifLevel0);
   	  	for (GcSubject subject:list) {
   	  	    if (null!=subject.getCourseTags()&&subject.getCourseTags().size()!=0) {
                List lists = JSONArray.parseArray(subject.getCourseTags().toJSONString());
                HashSet hs = new HashSet(lists);
                subject.setCourseTags(JSONArray.parseArray(JSONObject.toJSONString(hs)));
            }
   	  	sysFileService.getResFullUrlSaveType2(subject.getSubImgFile());
   		  if(subject.getType()==TableConstant.gcSubject_type_subject0) {
   			  subject.setOrder(subject.getSubjectAssociationOrder());//使用门户自己的排序
    	  }
      }
   	  	
   	  return list;
     }
//    @Override
//    public List<GcSubject> selectImportedSubject(Integer masterId) {
//    	 List<GcSubject> list = this.baseMapper.selectImportedSubject(masterId);
//    	 for (GcSubject subject:list) {
//    		 subject.setOriginalId(subject.getId());//导入的原课id
//    		 if(subject.getType()==TableConstant.gcSubject_type_subject0) {
//    			 subject.setId(subject.getThisId());//当前门户的课的id
//    			 subject.setOrder(subject.getThisOrder());//当前门户的课的排序
//    		 }else {
//    			 subject.setSubId(subject.getThisId());//将当前门户的课的id设到导入的课的话题级别课的父id
//    			 subject.setFid(subject.getThisId());
//    		 }
//
//         }/
//    	 return list;
//    }


    @Override
    public List<GcSubject> getSubList(Integer masterId,Integer subType) {
        // TODO Auto-generated method stub
        QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
        queryWrapper.eq("master_id", masterId);
        if(Objects.nonNull(subType)){
            queryWrapper.eq("type",subType);
        }
//        queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
        queryWrapper.orderByAsc("`order`");
        return this.list(queryWrapper);
    }

    @Override
    public List<GcSubject> selectAllTopicList(Integer masterId,Integer subType,Integer userId,GcSubject gcSubject) {
        // TODO Auto-generated method stub
        QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("create_user",userId);
        queryWrapper.eq("fid",gcSubject.getFid());
        queryWrapper.ne("id",gcSubject.getId());
        if(Objects.nonNull(subType)){
            queryWrapper.eq("type",subType);
        }
//        queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
        queryWrapper.orderByAsc("`order`");
        return this.list(queryWrapper);
    }

    @Override
    public List<GcSubject> selectAllSub0ListByUserId(Integer masterId,Integer subType,Integer userId) {
        // TODO Auto-generated method stub
        QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("create_user",userId);
//        queryWrapper.eq("type",TableConstant.COMMON_ZERO);
//        queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
        return this.list(queryWrapper);
    }

    @Override
    public List<GcSubject> getSubList0(Integer masterId) {
        // TODO Auto-generated method stub
        QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("level",0);
//        queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
        queryWrapper.orderByAsc("`order`");
        return this.list(queryWrapper);
    }

    @Override
    public List<GcSubject> getSubListTop(Integer masterId) {
    	List<GcSubject> l = this.getTopSubList(masterId);
    	List<GcSubject> list = this.baseMapper.selectSubjectAssociation(masterId,null,false);
    	l.addAll(list);
        return l;
    }
    

    public List<GcSubject> getTopSubList(Integer masterId) {
        // TODO Auto-generated method stub
        QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("level", 0);
        queryWrapper.eq("type", 0);
//        queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
        return this.list(queryWrapper);
    }

    @Override
    @Transactional
    public boolean deleteSub(Integer subId, Integer masterId) {
        // TODO Auto-generated method stub
        GcSubject subject = this.getById(subId);
        if(subject.getMasterId().intValue()==masterId.intValue()) {
        	List<GcSubject> list = this.getSubListWithHidden(subject.getMasterId());

            //计算当前属于第几层
            List<GcSubject> resultList = new ArrayList<GcSubject>();
            this.lookupChildren(list, subject, resultList);
            LOGGER.info(resultList.size() + "");


            List<Integer> subIds = resultList.stream().map(GcSubject::getId).collect(Collectors.toList());
            //删除ResultList中的Subject下面的视频
            videoService.deleteVideoBySubIds(subIds);
            return this.removeByIds(subIds);
        }else {
        	 if(gcSubjectAssociationMapper.deleteGcSubjectAssociation(subId, masterId)>0) {
        		 //删除关联课程，删除gc_user_access_permission及gc_access的课程，否则用户端还会出现
        		 if(this.deleteSubAccessInJson(subId, masterId) && this.deleteUserSubAccessInJson(subId, masterId))return true;
        	 }

        	 return false;
        }

    }

    public boolean deleteSubAccessInJson(int subId,int masterId) {
    	List<GcAccess> gcAccessList = gcAccessMapper.listContainsSub(masterId,subId);
    	if(gcAccessList!=null && !gcAccessList.isEmpty() ) {
    		for(GcAccess access: gcAccessList) {
    			JSONArray accessArray  = access.getSubjectJson();
    			List list = new ArrayList();
    			for (int i=0;i<accessArray.size();i++) {
    	    		if(subId!=(int)accessArray.get(i)) {
    	    			list.add((int)accessArray.get(i));
    	    		}
    			}
    			access.setSubjectJson(new JSONArray(list));
    		}
    	}
    	if(gcAccessList==null || gcAccessList.size()==0) {
    		return true;
    	}
    	boolean a = gcAccessService.updateBatchById(gcAccessList);
    	return a;
    }

    public boolean deleteUserSubAccessInJson(int subId,int masterId) {
    	List<GcUserAccessPermission> gcUserAccessPermissionList = gcUserAccessPermissionMapper.listContainsSubPremission(masterId, subId);
    	if(gcUserAccessPermissionList!=null && !gcUserAccessPermissionList.isEmpty() ) {
    		for(GcUserAccessPermission permission: gcUserAccessPermissionList) {
    			JSONArray permissionArray  = permission.getSubPermission();
    			List list = new ArrayList();
    			for (int i=0;i<permissionArray.size();i++) {
    	    		if(subId!=(int)permissionArray.get(i)) {
    	    			list.add((int)permissionArray.get(i));
    	    		}
    			}
    			permission.setSubPermission(new JSONArray(list));
    		}
    	}
    	if(gcUserAccessPermissionList==null || gcUserAccessPermissionList.size()==0) {
    		return true;
    	}
    	boolean a = gcUserAccessPermissionService.updateBatchById(gcUserAccessPermissionList);
    	return a;
    }

    @Override
    public int getSubTopicNum(Integer masterId,List<Integer> subIds,Integer managerId) {
        // TODO Auto-generated method stub

        QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
        queryWrapper.eq("master_id",masterId);
        queryWrapper.eq("level", 1);
        queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
        if(subIds !=null && subIds.size()!=TableConstant.COMMON_ZERO){
           queryWrapper.in(true,"fid",subIds);
        }
        //查询导入课程的topic数量
        Integer importedTopicNum = this.baseMapper.countImportedToicNum(masterId,TableConstant.COMMON_ONE,TableConstant.gcSubject_state_hidden_0,subIds,managerId);
        return this.count(queryWrapper)+importedTopicNum;


    }

    @Override
    public int getSubjectNum(Integer masterId,List<Integer> subIds,Integer managerId) {
//        // TODO Auto-generated method stub
//        QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
//        queryWrapper.eq("master_id", masterId).eq("type", 0);
//        queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
        Integer type = TableConstant.COMMON_ZERO;
        return this.baseMapper.countCourseInPortal(masterId,type,subIds,managerId);
//        return this.count(queryWrapper);

    }

    @Override
    public List<Integer> getSubjectIds(Integer masterId) {
        // TODO Auto-generated method stub
        QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
        queryWrapper.select("id").eq("master_id", masterId).eq("type",TableConstant.gcSubject_type_subject0);
        queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
        List<Integer> list = this.list(queryWrapper).stream().map(GcSubject::getId).collect(Collectors.toList());
        return list;
    }

    @Override
    public List<Integer> getPublicSubjectIds(Integer masterId) {
        return this.baseMapper.getPublicSubjectIds(masterId);
    }

    @Override
    public List<Integer> getSubjectChildIds(Integer subId) {
        // TODO Auto-generated method stub
        QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
        queryWrapper.select("id").eq("fid", subId);
        queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
        queryWrapper.orderByAsc("`order`");
        List<Integer> list = this.list(queryWrapper).stream().map(GcSubject::getId).collect(Collectors.toList());
        return list;
    }
    
    @Override
    public List<GcSubject> getSubjectChild(Integer subId) {
        // TODO Auto-generated method stub
        QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
        queryWrapper.eq("fid", subId);
        queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
        queryWrapper.orderByAsc("`order`");
        List list = this.list(queryWrapper);
        return list;
    }

    @Override
    public GcSubject getSubNameBysubId(Integer subId) {
        QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
        queryWrapper.eq("id", subId);
        queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
        return this.getOne(queryWrapper);
    }

    @Override
    public List<GcSubject> getSubjectUserInfo(List<Integer> userIdList,List<Integer> subList,Integer masterId) {
        List<GcSubject> gcSubjectList = this.baseMapper.getSubjectUserInfo(userIdList,subList,masterId);
        gcSubjectList.forEach(i->{
                Integer videoPlayState = 0;
                Integer answeredNums = 0;
                Integer answeredSumNums = 0;
                for (GcVideo video : i.getVideoChildList()) {
                    video.setCompleteStatus(buildCompleteStatusEventNum(video.getPlayState(),video.getAnsweredNums(),video.getAnsweredSumNums()));
                    if (null!= video.getPlayState() && video.getPlayState().equals(TableConstant.gcUserVideoAction_value_rate2_1)){
                        videoPlayState++;
                    }
                    answeredNums+=video.getAnsweredNums();
                    answeredSumNums+=video.getAnsweredSumNums();
                }
                if (TableConstant.COMMON_ZERO!=videoPlayState&&TableConstant.COMMON_ZERO!=i.getVideoChildList().size()){
                    BigDecimal completedPercent = new BigDecimal(answeredNums).add(new BigDecimal(videoPlayState));
                    BigDecimal allPercent = new BigDecimal(i.getVideoChildList().size()).add(new BigDecimal(answeredSumNums));
                    BigDecimal percent = completedPercent.divide(allPercent, 2, BigDecimal.ROUND_DOWN).multiply(new BigDecimal("100"));
                    i.setTotalPercent(percent);
                }else {
                    i.setTotalPercent(new BigDecimal("0"));
                }
                sysFileService.getResFullUrl(i.getUserInfo().getInfo().getAvatarFile(),null);
            });
        return gcSubjectList;
    }

    private short buildCompleteStatusEventNum(String playState,Integer answeredNums,Integer answeredSumNums){
        if(null == playState){//没有播放记录
            return TableConstant.VIDEO_COMPLETE_STATUS0;
        }
        if(playState.equals("1")&&answeredNums.equals(answeredSumNums)){
            //看完视频，并且回答完
            return TableConstant.VIDEO_COMPLETE_STATUS2;
        }
        return TableConstant.VIDEO_COMPLETE_STATUS1;
    }

    @Override
    public GcSubject getSubByToken(String token) {
        QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
        queryWrapper.eq("token", token);
        queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
        return this.getOne(queryWrapper);
    }

    @Override
    public List<GcSubject> getChildSubjectBySubId(Integer subId) {
        QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<GcSubject>();
        queryWrapper.eq("fid", subId);
        queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
        queryWrapper.orderByAsc("`order`");
        return this.list(queryWrapper);
    }

    @Override
    public GcSubject getSubByVid(Integer vid) {
        // TODO Auto-generated method stub
        return this.baseMapper.selectSubByVid(vid);
    }

    @Override
    public List<GcSubject> getSubVideoEventList(Integer subId, Integer studentId,Integer masterId,HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.baseMapper.getSubVideoEventList(subId,studentId,masterId);
    }
    
    @Override
    public List<GcSubject> getLevel1VideoEventList(Integer subId, Integer userId, Integer teacherId) {
        return this.baseMapper.getLevel1VideoEventList(subId,userId,teacherId);
    }
    
    @Override
    public Map<String,Object> selectEventResNumMapForWorkbook(Integer subId, Integer userId){
    	return this.baseMapper.selectEventResNumMapForWorkbook(subId, userId);
    }
    
    @Override
    public Map<String,Object> getAnswerMessageMapForTeacherWorkbook(Integer subId, Integer studentId, Integer teacherId){
    	return this.baseMapper.getAnswerMessageMapForTeacherWorkbook(subId, studentId, teacherId);
    	
    }
    
    @Override
    public Map<String,Object> selectEventResNumMapForWorkbookTeacher(Integer subId, Integer studentId, Integer teacherId){
    	return this.baseMapper.selectEventResNumMapForWorkbookTeacher(subId, studentId,teacherId);
    }
    

    @Override
    public List<GcSubject> getSubListByIds(List<Integer> subIds,HttpServletRequest request) {
        QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<>();
        if (subIds.size()!=0){
            queryWrapper.in("id", subIds);
            queryWrapper.ne("state", TableConstant.gcSubject_state_hidden_0);//不显示隐藏
            PageParam pageParam = new PageParam(request);
            Integer pageNum = pageParam.getPageNum();
            Integer pageSize=pageParam.getPageSize();
            if (pageNum > 0 && pageSize > 0) {
                PageHelper.startPage(pageNum, pageSize);
            }
            return this.list(queryWrapper);
        }
        //如果学生权限为空，返回空list
        List<GcSubject> gcSubject = new ArrayList<>();
        return gcSubject;
    }
    @Override
    public JSONArray addUnReadTag(List<GcEvent> eventList,Integer teacherId,Integer studentId,Integer masterId){
        List<Integer> eventIds = eventList.stream().map(GcEvent::getId).collect(Collectors.toList());
        //查出video下所有事件的问题回答
        List<Map<String,Object>> outEventAnswerUnReadList = masterMessageService.getEventAnswerUnReadList(eventIds,
                studentId, masterId, teacherId);
        //查出video下所有事件的资源回答，SELECT mm.id,mm.res_id,ua.event_id FROM gc_master_message AS mm LEFT JOIN gc_user_answer AS ua ON ua.id = mm.res_id WHERE ua.event_id IN ( ? , ? , ? , ? , ? ) AND mm.master_id = ? AND mm.event_type = 5 AND mm.user_id = ? AND mm.target_user_id = ? AND mm.read_state = 0 
        List<Map<String, Object>> videoResourceLists = userEventResourceService.getALLResourceListByEventIds(eventIds, studentId, teacherId);

        List<Map<String, Object>> videoResourceList  = this.removeRepeatMapByKey(videoResourceLists,"id");

        List<Integer> videoAllResourceIds = new ArrayList<>();
        List<Map<String, Object>> outEventUnReadMessageCount = new ArrayList<>();
        if (videoResourceList != null && videoResourceList.size() > 0) {
            for (Map<String, Object> map : videoResourceList) {
                videoAllResourceIds.add(Integer.parseInt((String) map.get("id")));
            }
            outEventUnReadMessageCount =
                    masterMessageService.getUnReadMessageCountByResourceIds(videoAllResourceIds, studentId, masterId, teacherId);
        }

        JSONArray reJSONArray = new JSONArray();

        for (GcEvent gcEvent:eventList){
            JSONObject eventObject = new JSONObject();
            eventObject.put("eventId", gcEvent.getId());
            eventObject.put("eventTitle", gcEvent.getEventTitle());
            eventObject.put("videoId", gcEvent.getVideoId());
            Long outNums = null;
            if (outEventUnReadMessageCount != null && outEventUnReadMessageCount.size() > 0) {
                for (Map<String, Object> eventUnRead : outEventUnReadMessageCount) {
                    if (eventUnRead.get("event_id").equals(gcEvent.getId())) {
                        outNums = (Long) eventUnRead.get("nums");
                    }
                }
            }
            eventObject.put("unReadCount", outNums);

            eventObject.put("newAnswer", true);
            if(outEventAnswerUnReadList != null && outEventAnswerUnReadList.size() > 0){
                for (Map<String,Object> map: outEventAnswerUnReadList ) {
                    if(map.get("event_id").equals(gcEvent.getId())){
                        eventObject.put("newAnswer", false);
                    }
                }
            }
            reJSONArray.add(eventObject);
        }
        return reJSONArray;
    }

    /**
     * 根据map中的某个key 去除List中重复的map
     * @author shijing
     * @param list
     * @param mapKey
     * @return
     */
    public static List<Map<String, Object>> removeRepeatMapByKey(List<Map<String, Object>> list, String mapKey){
        if (list == null || list.size() == 0) return null;
        //把list中的数据转换成msp,去掉同一id值多余数据，保留查找到第一个id值对应的数据
        List<Map<String, Object>> listMap = new ArrayList<>();
        Map<String, Map> msp = new HashMap<>();
        for(int i = list.size()-1 ; i>=0; i--){
            Map map = list.get(i);
            Integer id = (Integer)map.get(mapKey);
            map.remove(mapKey);
            msp.put(id.toString(), map);
        }
        //把msp再转换成list,就会得到根据某一字段去掉重复的数据的List<Map>
        Set<String> mspKey = msp.keySet();
        for(String key: mspKey){
            Map newMap = msp.get(key);
            newMap.put(mapKey, key);
            listMap.add(newMap);
        }
        return listMap;
    }
    @Override
    public boolean changeSubOrder(List<Integer> subIds, Integer masterId) {

//    	 QueryWrapper<GcSubject> queryWrapper = new QueryWrapper<>();
//         if(subIds!=null&&subIds.size()>0){
//             queryWrapper.in("id",subIds);
//         }
//         List<GcSubject> allSubList=this.list(queryWrapper);
//         List<Integer> allSubMasterIds = allSubList.stream().map(GcSubject::getMasterId).collect(Collectors.toList());
//
         //subIds可能包含导入的课程，通过masterId判断
        List<GcSubject> subjectList = new ArrayList<>();
        List<GcSubjectAssociation> subjectAssociationList=new ArrayList<>();

        Integer order = 1;
        for (Integer subId : subIds) {
        	GcSubject thisSub = this.getById(subId);

        	if(thisSub.getMasterId().intValue()==masterId.intValue()) {
//        		GcSubject newSubject = new GcSubject();
//                newSubject.setId(subId);
                thisSub.setOrder(order);
                
                subjectList.add(thisSub);
                order++;
        	}else {
        		GcSubjectAssociation sa = new GcSubjectAssociation();
        		sa.setMasterId(masterId);
        		sa.setSubjectId(subId);
        		sa.setOrder(order);
        		subjectAssociationList.add(sa);
        		order++;
        	}

        }
        if(subjectAssociationList!=null && subjectAssociationList.size()>0) {
        	long result = gcSubjectAssociationMapper.bulkUpdatOrderByMasterIdAndSubjetId(subjectAssociationList);
        }
        boolean a = this.updateBatchById(subjectList);
        return a;
    }

//    @Override
//    public boolean changeSubOrder(List<Integer> subIds) {
//        List<GcSubject> subjectList = new ArrayList<>();
//        Integer order = 1;
//        for (Integer subId : subIds) {
//            GcSubject newSubject = new GcSubject();
//            newSubject.setId(subId);
//            newSubject.setOrder(order);
//            subjectList.add(newSubject);
//            order++;
//        }
//        return this.updateBatchById(subjectList);
//    }

	@Override
	public List<GcSubject> listSubByIds(List<Integer> subIds) {
//		GcSubject a = this.baseMapper.selectByid(375);
		 return this.baseMapper.listSubByIds(subIds);
	}

    @Override
    public List<GcSubject> listSubByIdsAndName(List<Integer> subIds,String name) {
        return this.baseMapper.listSubByIdsAndName(subIds,name);
    }

    @Override
	public List<GcSubject> listSubWithAssoByIds(Integer masterId,List<Integer> subIds) {
//		GcSubject a = this.baseMapper.selectByid(375);
		 return this.baseMapper.listSubWithAssoByIds(masterId,subIds);
	}

	
	@Override
	public Integer countCourseForName(GcSubject subject) {
		return this.baseMapper.countCourseForName(subject);
	}
	
	@Override
	public List<GcSubject> selecUnitNumForVideo(Integer videoId){
		return this.baseMapper.selecUnitNumForVideo(videoId);
	}

    @Override
    public List<Integer> countSessions(List<Integer> id){
        return newUiGcSubjectMapper.countSessions(id);
    }

    @Override
    public List<GcSubject> selectAllSubByUserId(Integer masterId,Integer userId,HttpServletRequest request){
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize=pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return gcSubjectMapper.selectAllSubByUserId(masterId,userId);
    }


    @Override
    public List<GcSubject> selectTwoSubjectsByFids(List<Integer> fids){
        return gcSubjectMapper.selectTwoSubjectsByFids(fids);
    }


    @Override
    public List<GcSubject> selectAllLevel1SubList(List<Integer> subIds,String order,Integer masterId){
//        Integer stateAndType = TableConstant.COMMON_ZERO;
//        Integer pageNum = pageParam.getPageNum();
//        Integer pageSize=pageParam.getPageSize();
////		 	if (pageNum == null) {
////	            pageNum = 0;
////	        }
////
////	        if (pageSize == null) {
////	            pageSize = 0;
////	        }
//
//        if (pageNum > 0 && pageSize > 0) {
//            PageHelper.startPage(pageNum, pageSize);
//        }
        return gcSubjectMapper.selectAllLevel1SubList(subIds,order,masterId);
    }

    private static final String masterRandomToken = "masterRandomToken_";
    @Override
    public GcSubject saveSubInfo(GcSubject sub, GcManager manager,GcMaster master,GcUser user,HttpServletRequest request) {
        Integer masterId = master.getId();
        //是否是一级课程
        if(sub.getFid()==null || sub.getFid().intValue()==0) {
            sub.setFid(null);
        }
        //1级课程不可添加重命名关联课程
        if(sub.getFid()==null && sub.getAliasSubId()!=null) {
            throw new SystemException("1级课程不可添加重命名关联课程");
        }
        GcSubject fullSub = this.getById(sub.getId());
        if(Objects.isNull(sub.getTagType())) {
            if (sub.getMasterId() != null && sub.getMasterId().intValue() != masterId)
                throw new SystemException("该课程不属于该门户，不可修改状态");

            if (sub.getId() != null && sub.getMasterId() == null) {
                if (fullSub.getMasterId().intValue() != masterId) throw new SystemException("该课程不属于该门户，不可修改状态");
            }
        }
        //判断state
        if(sub.getState()!=null) {
            ApiAssert.jsonValueIntegerIn(sub.getState(), TableConstant.gcSubject_state_jsonStr, "state值必须为通用枚举中的一个");
        }else {
            sub.setState(TableConstant.gcSubject_state_visible_1);
        }

        //判断更新时的token
//        if(sub.getId()!=null && sub.getToken()!=null && !StringUtils.isBlank(sub.getToken())) {
//        	throw new SystemException("更新课程时，token值必须为空，因为token不可在此接口更新");
//        }

        if(sub.getMasterId()==null) {
            sub.setMasterId(masterId);
        }
        if(sub.getName()==null)throw new SystemException("名字name不可空");

//        if(sub.getName().contains("-"))throw new SystemException("名称不可含横杠字符-");

        int c = this.countCourseForName(sub);
        if(c>0)throw new SystemException(10000,"'"+sub.getName()+"'- "+ I18NUtil.get("guidecore.master.sameCourseNameNotice"));


        if(sub.getToken()!=null && !StringUtils.isBlank(sub.getToken()) ) {//插入时，如果token不为空，
            if(fullSub!=null && !sub.getToken().equals(fullSub.getToken())) {//则判断可导入，判断token是否与db中的值一致
                if(redisOperator.get(masterRandomToken+master.getId())==null) {//重新从redis获取token
                    throw new SystemException("请重新生成token");
                }
                sub.setToken((String)redisOperator.get(masterRandomToken+master.getId()));
            }
        }

//        if(sub.getId()==null)
        //	sub.setMasterId(masterId);
//
        if (null==sub.getId()&&null==sub.getOrder()){
            List<GcSubject> subjectList = this.getSubList(masterId,null);
            if (null!=subjectList&&TableConstant.COMMON_ZERO!=subjectList.size()){
                Integer max = subjectList.stream().mapToInt(GcSubject::getOrder).max().getAsInt();
                sub.setOrder(max+1);

            }
        }
        if (null!=user){
            sub.setCreateUser(user.getId());
        }
        if (null!=sub.getId()){
            sub.setUpdateTime(new Date());
        }
        this.saveSub(sub);
        GcSubject subs = this.getById(sub.getId());


        if (null!=sub.getAvailableType()) {
            List<GcAccess> accessList = new ArrayList<>();

            //may
            if (null!=sub.getAccessIds()&&TableConstant.COMMON_ZERO != sub.getAccessIds().size()&&null==sub.getAllPublishedMay()) {
                accessList = gcAccessService.selectMasterIdAndIds(masterId, sub.getAccessIds());
            }
            //全部可查看不加入权限表
            if (sub.getAvailableType().equals(TableConstant.COMMON_ONE)||sub.getAvailableType().equals(TableConstant.COMMON_THREE)){
            //私有
            }else if (TableConstant.COMMON_FOUR == sub.getState()){

            }

            List<GcAccess> mustAccessList = new ArrayList<>();
            List<Integer> accessIds = new ArrayList<>();

            //发布当前用户可发布的所有组may
            if (null!=sub.getAllPublishedMay()&&sub.getAllPublishedMay().equals(TableConstant.COMMON_ZERO)){
                if (user.getIsOrgAdmin()){
                    accessList = gcAccessService.findAccessListByMasterId(masterId);
                }else {
                    accessList = gcAccessMapper.listAccess(null, masterId, user.getId());
                }
                sub.getAccessIds().addAll(accessList.stream().map(GcAccess::getId).collect(Collectors.toList()));
                accessIds = accessList.stream().map(GcAccess::getId).collect(Collectors.toList());
            }

            if (TableConstant.COMMON_ZERO!=accessList.size()) {
                for (GcAccess gcAccess : accessList) {
                    accessIds.add(gcAccess.getId());
                    if (null!=sub.getAccessIds()&&sub.getAccessIds().contains(gcAccess.getId())){
                        if (null!=gcAccess.getSubjectJson()){
                            gcAccess.getSubjectJson().add(sub.getId());
                        }else {
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.add(sub.getId());
                            gcAccess.setSubjectJson(jsonArray);
                        }
                    }
                    if (null!=sub.getAccessIds()&&sub.getAccessIds().contains(gcAccess.getId())){
                        if (null!=gcAccess.getMaySubjectJson()){
                            gcAccess.getMaySubjectJson().add(sub.getId());
                        }else {
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.add(sub.getId());
                            gcAccess.setMaySubjectJson(jsonArray);
                        }
                    }
                }

                gcAccessService.insertOrUpdateList(accessList);
                List<Integer> userAccessList = userAccessService.selectGetUserAccessIdListUserIds(masterId, accessIds);

                List<GcUserAccessPermission> gcUserAccessPermissions = new ArrayList<>();
                if (TableConstant.COMMON_ZERO!=userAccessList.size()){
                    gcUserAccessPermissions = gcUserAccessPermissionService.getPermissionByUserAccessIdList(userAccessList);
                }
                for (GcUserAccessPermission accessPermission : gcUserAccessPermissions) {
                    if (null!=accessPermission.getMaySubjectJson()){
                        if (!accessPermission.getMaySubjectJson().contains(sub.getId())) {
                            accessPermission.getMaySubjectJson().add(sub.getId());
                        }
                    }else {
                        JSONArray subPermission = new JSONArray();
                        subPermission.add(sub.getId());
                        accessPermission.setMaySubjectJson(subPermission);
                    }
                    if (null!=accessPermission.getSubPermission()){
                        if (!accessPermission.getSubPermission().contains(sub.getId())) {
                            accessPermission.getSubPermission().add(sub.getId());
                        }
                    }else {
                        JSONArray subPermission = new JSONArray();
                        subPermission.add(sub.getId());
                        accessPermission.setSubPermission(subPermission);
                    }
                }
                if (TableConstant.COMMON_ZERO!=gcUserAccessPermissions.size()){
                    gcUserAccessPermissionService.updateGcUserAccessPermissions(gcUserAccessPermissions);
                }
            }

            //must
            if(null!=sub.getMustAccessIds()&&TableConstant.COMMON_ZERO != sub.getMustAccessIds().size()&&null==sub.getAllPublished()){
                mustAccessList = gcAccessService.selectMasterIdAndIds(masterId, sub.getMustAccessIds());
            }
            List<Integer> mustAccessIds = new ArrayList<>();
            //发布当前用户可发布的所有组must
            if (null!=sub.getAllPublished()&&sub.getAllPublished().equals(TableConstant.COMMON_ZERO)){
                if (user.getIsOrgAdmin()){
                    mustAccessList = gcAccessService.findAccessListByMasterId(masterId);
                }else {
                    mustAccessList = gcAccessMapper.listAccess(null,masterId,user.getId());
                }
                sub.getMustAccessIds().addAll(mustAccessList.stream().map(GcAccess::getId).collect(Collectors.toList()));
                mustAccessIds = mustAccessList.stream().map(GcAccess::getId).collect(Collectors.toList());
            }
            if (TableConstant.COMMON_ZERO!=mustAccessList.size()){
                for (GcAccess gcAccess : mustAccessList) {
                    mustAccessIds.add(gcAccess.getId());
                    if (null!=sub.getMustAccessIds()&&sub.getMustAccessIds().contains(gcAccess.getId())){
                        if (null!=gcAccess.getMustSubjectJson()){
                            gcAccess.getMustSubjectJson().add(sub.getId());
                        }else {
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.add(sub.getId());
                            gcAccess.setMustSubjectJson(jsonArray);
                        }

                        if (null!=gcAccess.getSubjectJson()){
                            gcAccess.getSubjectJson().add(sub.getId());
                        }else {
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.add(sub.getId());
                            gcAccess.setSubjectJson(jsonArray);
                        }
                    }
                }
                gcAccessService.insertOrUpdateList(mustAccessList);
                //List<Integer> userAccessIds = mustAccessList.stream().map(GcAccess::getId).collect(Collectors.toList());
                List<Integer> userAccessList = userAccessService.selectGetUserAccessIdListUserIds(masterId, mustAccessIds);

                List<GcUserAccessPermission> gcUserAccessPermissions = new ArrayList<>();
                if (TableConstant.COMMON_ZERO!=userAccessList.size()){
                    gcUserAccessPermissions = gcUserAccessPermissionService.getPermissionByUserAccessIdList(userAccessList);
                }
                for (GcUserAccessPermission accessPermission : gcUserAccessPermissions) {
                    if (null!=accessPermission.getMustSubjectJson()){
                        if (!accessPermission.getMustSubjectJson().contains(sub.getId())) {
                            accessPermission.getMustSubjectJson().add(sub.getId());
                        }
                    }else {
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.add(sub.getId());
                        accessPermission.setMustSubjectJson(jsonArray);
                    }

                    if (null!=accessPermission.getSubPermission()){
                        if (!accessPermission.getSubPermission().contains(sub.getId())) {
                            accessPermission.getSubPermission().add(sub.getId());
                        }
                    }else {
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.add(sub.getId());
                        accessPermission.setSubPermission(jsonArray);
                    }
                }
                if (TableConstant.COMMON_ZERO!=gcUserAccessPermissions.size()){
                    gcUserAccessPermissionService.updateGcUserAccessPermissions(gcUserAccessPermissions);
                }
            }
        }
        sub.setCreateTime(subs.getCreateTime());
        sub.setUpdateTime(subs.getUpdateTime());

        sub.setSubImgFile(sysFileService.getById(sub.getSubImgId()));
        sysFileService.getResFullUrl(sub.getSubImgFile(),request);
        if(null==sub.getFid() && null!=manager) {
            GcUserAccess gcUserAccess = userAccessService.selectUserAccessByManagerAndMaster(manager.getId(), masterId);
            if(null!=gcUserAccess) {
                GcUserAccessPermission gcUserAccessPermission = userAccessService.getUserAccessPermission(gcUserAccess.getId());
                GcAccess gcAccess = gcAccessService.getAccessById(gcUserAccess.getAccessId());
                JSONArray jsonArray = gcAccess.getSubjectJson();
                JSONArray permissionJsonArray = gcUserAccessPermission.getSubPermission();
                if (!jsonArray.contains(sub.getId())) {
                    jsonArray.add(sub.getId());
                    gcAccessService.updateById(gcAccess);
                }
                if (!permissionJsonArray.contains(sub.getId())) {
                    permissionJsonArray.add(sub.getId());
                    gcUserAccessPermissionService.saveOrUpdate(gcUserAccessPermission);
                }
            }
        }
//        if(null==sub.getFid()) {
//            GcUserAccess gcUserAccess = gcUserAccessService.getUserAccessByMasterIdAndUserId(userId,masterId);
//            GcAccess gcAccess = gcAccessService.getAccessById(gcUserAccess.getAccessId());
//        }

        if (null!=sub.getSubdetail_img_id()&&sub.getSubdetail_img_id().size()!=0){
            Integer subDetailImgId = Integer.parseInt(sub.getSubdetail_img_id().get("subDetailImgId").toString());
            SysFile sysFile = sysFileService.getById(subDetailImgId);
            sysFileService.getResFullUrl(sysFile,request);
            sub.setSubDetailImgUrl(sysFile.getFullFileUrl());
        }
        if (null!=sub.getCourseTags()){
            QueryWrapper<PtTags> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("subject_id", sub.getId());
            queryWrapper.in("master_id",masterId);
            queryWrapper.in("type",TableConstant.COMMON_ONE);
            ptTagsService.remove(queryWrapper);
            List<String> tagList = sub.getCourseTags().toJavaList(String.class);
            List<PtTags> ptTagsList = new ArrayList<>();
            Integer finalMasterId = masterId;
            tagList.forEach(i->{
                PtTags newTags = new PtTags();
                newTags.setMasterId(finalMasterId);
                newTags.setTagText(i);
                newTags.setSubjectId(sub.getId());
                newTags.setType(TableConstant.COMMON_ONE);
                newTags.setOrder(TableConstant.COMMON_ZERO);
                ptTagsList.add(newTags);
            });
            ptTagsService.saveOrUpdateBatch(ptTagsList);
        }

        return sub;
    }

    @Override
    public Integer selectSubjectPt(String subjectName,Integer type,Integer state,Integer createUser,Integer masterId, Integer userId, List<Integer> channelIds,List<Integer> publicSubjectIds) {
        return this.baseMapper.selectSubjectPt(subjectName,type,state,createUser,masterId,userId,channelIds,publicSubjectIds).size();
    }

    @Override
    public Integer getCreateUserPublished(Integer userId, Integer masterId,Integer state) {
        return this.baseMapper.getCreateUserPublished(userId,masterId,state);
    }

    @Override
    public List<GcSubject> getAvailableCourses(String name,Integer masterId,List<Integer> subIds,Integer userId,String order) {
        return this.baseMapper.getAvailableCourses(name,masterId,subIds,userId,order);
    }

    @Override
    public List<GcSubject> newGetAvailableCourses(String name,Integer masterId,List<Integer> subIds,Integer userId,String order){
        return this.baseMapper.newGetAvailableCourses(name,masterId,subIds,userId,order);
    }

    @Override
    public List<GcSubject> getCompletedTwoCourse(Map<String,Object> paramMap){
        return this.baseMapper.getCompletedTwoCourse(paramMap);
    }

//    @Override
//    public List<SysFile> selectBySubId(Integer subId) {
//        List<SysFile> sysFile = sysFileMapper.selectBySubId(subId);
//        return sysFile;
////    }
}
