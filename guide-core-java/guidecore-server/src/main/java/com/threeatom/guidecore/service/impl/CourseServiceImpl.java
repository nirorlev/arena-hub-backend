package com.threeatom.guidecore.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.exception.ResourceNotFoundException;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.controller.user.vo.videoLongVo;
import com.threeatom.guidecore.dto.response.AssignedCourseDto;
import com.threeatom.guidecore.dto.response.BasicCourseDto;
import com.threeatom.guidecore.dto.response.CourseDto;
import com.threeatom.guidecore.dto.response.CourseListDto;
import com.threeatom.guidecore.dto.response.CourseProgramDto;
import com.threeatom.guidecore.dto.response.CourseVideoBookmarkDto;
import com.threeatom.guidecore.entity.Course;
import com.threeatom.guidecore.entity.CourseContent;
import com.threeatom.guidecore.entity.CourseEnrollment;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcContentGroupCourseAssignment;
import com.threeatom.guidecore.entity.GcEvent;
import com.threeatom.guidecore.entity.GcManager;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcSubjectAssociation;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserAccess;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.enums.CoursePublishState;
import com.threeatom.guidecore.enums.CourseType;
import com.threeatom.guidecore.enums.UserGroupRole;
import com.threeatom.guidecore.mapper.GcAccessMapper;
import com.threeatom.guidecore.mapper.GcSubjectAssociationMapper;
import com.threeatom.guidecore.mapper.CoursetMapper;
import com.threeatom.guidecore.mapper.NewUiGcSubjectMapper;
import com.threeatom.guidecore.mapping.CourseMapping;
import com.threeatom.guidecore.service.CourseContentService;
import com.threeatom.guidecore.service.CourseEnrollmentService;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcEventService;
import com.threeatom.guidecore.service.GcMasterMessageService;
import com.threeatom.guidecore.service.CourseService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.GcUserEventResourceService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.GcUserVideoActionService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.PtTagsService;
import com.threeatom.guidecore.service.VideoPlaySegmentService;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.guidecore.util.TaskTimingUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import com.threeatom.utils.TreeUtil;
import com.threeatom.utils.data.TreeNode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CourseServiceImpl extends ServiceImpl<CoursetMapper, Course> implements CourseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseServiceImpl.class);
    private static final String masterRandomToken = "masterRandomToken_";
    @Resource
    NewUiGcSubjectMapper newUiGcSubjectMapper;
    @Resource
    CoursetMapper coursetMapper;
    @Autowired
    private GcVideoService videoService;
    @Autowired
    private GcUserAccessService userAccessService;
    @Autowired
    private GcEventService eventService;
    @Autowired
    private GcMasterMessageService masterMessageService;
    @Lazy
    @Autowired
    private GcUserEventResourceService userEventResourceService;
    @Autowired
    private SysFileService sysFileService;
    @Resource
    private GcSubjectAssociationMapper gcSubjectAssociationMapper;
    @Autowired
    private GcAccessMapper gcAccessMapper;
    @Autowired
    private GcAccessService gcAccessService;
    @Autowired
    private GcContentGroupCourseAssignmentService courseAssignmentService;
    @Autowired
    private PtTagsService ptTagsService;
    @Autowired
    private RedisOperator redisOperator;
    @Lazy
    @Autowired
    private GcUserService gcUserService;//用户服务类--统计参与人数
    @Autowired
    private GcUserVideoActionService videoActionService;//用户视频操作--查询评论、点赞、星级评价
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private CourseMapping courseMapping;
    @Autowired
    private CourseContentService courseContentService;
    @Autowired
    private VideoPlaySegmentService playSegmentService;
    @Autowired
    @Lazy
    private CourseEnrollmentService courseEnrollmentService;

    public static List<Map<String, Object>> removeRepeatMapByKey(List<Map<String, Object>> list, String mapKey) {
        if (list == null || list.size() == 0) {
            return null;
        }
        //把list中的数据转换成msp,去掉同一id值多余数据，保留查找到第一个id值对应的数据
        List<Map<String, Object>> listMap = new ArrayList<>();
        Map<String, Map> msp = new HashMap<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            Map map = list.get(i);
            Integer id = (Integer) map.get(mapKey);
            map.remove(mapKey);
            msp.put(id.toString(), map);
        }
        //把msp再转换成list,就会得到根据某一字段去掉重复的数据的List<Map>
        Set<String> mspKey = msp.keySet();
        for (String key : mspKey) {
            Map newMap = msp.get(key);
            newMap.put(mapKey, key);
            listMap.add(newMap);
        }
        return listMap;
    }

    private Optional<GcContentGroupCourseAssignment> getAssignmentWithLatestDate(
        List<GcContentGroupCourseAssignment> assignments) {

        return assignments.stream()
            .max(Comparator.comparing(GcContentGroupCourseAssignment::getModifiedDate));
    }

    @Override
    public boolean saveSub(Course sub) {
        this.formatSub(sub);
        return this.saveOrUpdate(sub);
    }

    private Course formatSub(Course sub) {
        //生成顶级sub
        if (sub.getFid() == null) {
            sub.setLevel(0);
            sub.setType(0);
            sub.setSubId(null);
        } else {
            List<Course> list = this.getSubList(sub.getMasterId(), null);
            sub.setType(TableConstant.gcSubject_type_topic1);
            //计算当前属于第几层
            List<Course> resultList = new ArrayList<Course>();
            this.lookupParent(list, sub, resultList);
            //sub.setSubId(subId);
            if (resultList.size() < 2) {
                throw new SystemException("找不到父节点");
            }
            LOGGER.info(resultList.size() + "");
            sub.setLevel(resultList.size() - 1);
            sub.setSubId(resultList.get(resultList.size() - 1).getId());
        }

        //计算order
        if (sub.getId() != null) {
            List<Course> orderList = new ArrayList<Course>();
            QueryWrapper<Course> queryWrapper = new QueryWrapper<Course>();
            queryWrapper.eq("master_id", sub.getMasterId());
            queryWrapper.eq("fid", sub.getFid());
            queryWrapper.ne("state", CoursePublishState.PRIVATE.getValue());//不显示隐藏
            orderList = this.list(queryWrapper);
            sub.setOrder((orderList.size() + 1));
        }

        return sub;
    }

    private Course lookupParent(List<Course> list, Course subject, List<Course> resultList) {
        if (subject != null) {
            resultList.add(subject);
            for (Course sub : list) {
                if (subject.getFid() != null && subject.getFid().equals(sub.getId())) {
                    return this.lookupParent(list, sub, resultList);
                }
            }
            return null;
        } else {
            return null;
        }

    }

    private void lookupChildren(List<Course> list, Course subject, List<Course> resultList) {
        if (subject != null) {
            resultList.add(subject);
        }
        //搜索subject下所有的子级
        for (Course sub : list) {
            if (sub.getFid() != null && sub.getFid().equals(subject.getId())) {
                this.lookupChildren(list, sub, resultList);
            }
        }
    }

    @Override
    public TreeNode<Course> getTreeNode(Integer masterId) {
        // TODO Auto-generated method stub
        List<Course> list = this.getSubList(masterId, null);
        Course root = new Course();
        root.setId(0);
        return TreeUtil.createTree(list, root);
    }

    @Override
    public List<Course> getSubListWithImg(Integer masterId, SysSystem sys, HttpServletRequest request) {
        List<Course> list = this.baseMapper.getSubjectListCommon(masterId, null, null);
        if (list != null && list.size() > 0) {
            for (Course li : list) {
                if (null != li.getCourseTags() && li.getCourseTags().size() != 0) {
                    List lists = JSONArray.parseArray(li.getCourseTags().toJSONString());
                    HashSet hs = new HashSet(lists);
                    li.setCourseTags(JSONArray.parseArray(JSONObject.toJSONString(hs)));
                }
                sysFileService.getResFullUrl(li.getSubImgFile(), request);
                if (CollectionUtils.isNotEmpty(li.getSubdetail_img_id()) &&
                    Objects.nonNull(li.getSubdetail_img_id().get("subDetailImgId"))) {
                    SysFile sysFile = sysFileService.getById(
                        Integer.parseInt(li.getSubdetail_img_id().get("subDetailImgId").toString()));
                    String fullUrl = sysFileService.getResFullUrl(sysFile, request);
                    li.setSubDetailImgUrl(fullUrl);
                }
            }
        }
        return list;
    }

    @Override
    public List<Course> getLevel0SubListWithImg(Integer masterId, HttpServletRequest request,
                                                List<Integer> channelIds) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();

        Integer state = CoursePublishState.CERTAIN_TEAMS.getValue();
        if (null != request.getAttribute("state")) {
            state = Integer.parseInt(request.getAttribute("state").toString());
        }
        Integer createUser = null;
        if (null != request.getAttribute("createUser")) {
            createUser = Integer.parseInt(request.getAttribute("createUser").toString());
        }
        String courseName = null;
        if (null != request.getAttribute("subjectName")) {
            courseName = request.getAttribute("subjectName").toString();
        }
        List<Course> courses;

        if (null != request.getAttribute("isPt")) {
            if (null == request.getAttribute("type")) {
                if (null == request.getAttribute("userId")) {
                    if (pageNum > 0 && pageSize > 0) {
                        PageHelper.startPage(pageNum, pageSize);
                    }
                    courses = this.baseMapper.getSubjectList(masterId, TableConstant.gcSubject_type_subject0, state,
                        channelIds, createUser);
                } else {
                    // self created course
                    Integer userId = Integer.parseInt(request.getAttribute("userId").toString());
                    List<Integer> idLists = this.baseMapper.selectSubjectByCreateUser(masterId, userId);

                    if (pageNum > 0 && pageSize > 0) {
                        PageHelper.startPage(pageNum, pageSize);
                    }
                    courses = this.baseMapper.selectSubjectPt(courseName, null, state, createUser, masterId,
                        Integer.parseInt(request.getAttribute("userId").toString()), channelIds, idLists);
                }
            } else {
                List<Integer> publicCourseIds = new ArrayList<>();
                Integer type = Integer.parseInt(request.getAttribute("type").toString());
                if (type.equals(TableConstant.COMMON_FOUR)) {
                    List<Integer> courseIds = courseAssignmentService.getMustCourseIds(
                        Integer.parseInt(request.getAttribute("userId").toString()), masterId,
                        UserGroupRole.GROUP_MEMBER);
                    publicCourseIds.addAll(courseIds);
                } else if (type.equals(TableConstant.COMMON_ZERO) || type.equals(TableConstant.COMMON_TWO)) {
                    publicCourseIds = baseMapper.getPublicSubjectIds(masterId);
                }

                if (pageNum > 0 && pageSize > 0) {
                    PageHelper.startPage(pageNum, pageSize);
                }
                courses = this.baseMapper.selectSubjectPt(courseName,
                    Integer.parseInt(request.getAttribute("type").toString()), state, createUser, masterId,
                    Integer.parseInt(request.getAttribute("userId").toString()), channelIds, publicCourseIds);
            }
        } else {
            if (pageNum > 0 && pageSize > 0) {
                PageHelper.startPage(pageNum, pageSize);
            }
            courses = this.baseMapper.getSubjectList(masterId, TableConstant.gcSubject_type_subject0, state, channelIds,
                createUser);
        }
        if (CollectionUtils.isNotEmpty(courses)) {
            for (Course course : courses) {
                sysFileService.getResFullUrl(course.getSubImgFile(), request);
            }
        }

        return courses;
    }

    @Override
    public List<Course> selectSubjectByNewIndexHome(Integer masterId, Integer userId, PageParam pageParam) {
        List<Course> subjects = new ArrayList<>();
        if (null != userId) {

            List<Integer> idLists = this.baseMapper.selectSubjectByCreateUser(masterId, userId);
            Integer pageNum = pageParam.getPageNum();
            Integer pageSize = pageParam.getPageSize();
            if (pageNum > 0 && pageSize > 0) {
                PageHelper.startPage(pageNum, pageSize);
            }
            subjects = this.baseMapper.selectSubjectByNewIndexHome(userId, masterId, idLists);
            for (Course subject : subjects) {
                subject.setIsToDo(TableConstant.COMMON_ZERO);
            }
            //may
            if (pageNum > 0 && pageSize > 0) {
                PageHelper.startPage(pageNum, pageSize);
            }
            List<Course> maySubjects =
                this.baseMapper.selectMaySubjectByNewIndexHome(userId, masterId, idLists, TableConstant.COMMON_ZERO);
            subjects.addAll(maySubjects);
        } else {

        }
        return subjects;
    }

    public List<Course> selectSubjectMay(Integer masterId, Integer userId, PageParam pageParam) {
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<Course> maySubjects = this.baseMapper.selectMaySubjectByNewIndexHome(userId, masterId, null, null);
        return maySubjects;
    }

    @Override
    public Integer inProgressNum(Integer userId, Integer masterId) {
        return this.baseMapper.selectActiveSubject(userId, masterId, null, 2, null).size();
    }

    @Override
    public Integer getSubjectNameIndex(Integer masterId, String nameIndex) {
        return this.baseMapper.getSubjectNameIndex(masterId, nameIndex);
    }

    @Override
    public Integer getNewMyAssignmentNew(Integer masterId, Integer userId) {
        return this.baseMapper.getNewMyAssignmentNew(masterId, userId);
    }

    @Override
    public void initJit() {
        for (int i = 0; i < 30000; i++) {
            // 加入一些无关紧要的操作
            int result = 1 + 1;
        }
    }

    @Override
    public List<Integer> getUserCreateSubject(Integer masterId, Integer userId) {
        return this.baseMapper.getUserCreateSubject(masterId, userId);
    }

    @Override
    public List<Integer> getUserCreateSubjectAdmin(Integer masterId, Integer userId) {
        return this.baseMapper.getUserCreateSubjectAdmin(masterId, userId);
    }

    @Override
    public List<Integer> getUserPublicSubject(Integer masterId, Integer userId) {
        return this.baseMapper.getUserPublicSubject(masterId, userId);
    }

    @Override
    public List<Course> selectActiveSubject(Integer userId, Integer masterId, Integer subjectState, String name,
                                            HttpServletRequest request) {
        //查询顶级组must课程Ids
        List<Integer> orgMustSubjectIds =
            courseAssignmentService.getMustCoursesContentGroupAssignmentIds(userId, masterId);
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        //查询课程
        List<Course> level0sublist =
            this.baseMapper.selectActiveSubject(userId, masterId, name, subjectState, orgMustSubjectIds);
        for (Course subject : level0sublist) {
            subject.setIsToDo(TableConstant.COMMON_ZERO);
        }
        return level0sublist;
    }

    @Override
    public List<Course> selectDiscoverSubject(Integer userId, Integer masterId, Integer subjectState, String name,
                                              HttpServletRequest request) {


        //查询顶级组may课程Ids
        List<Integer> orgMaySubjectIds =
            courseAssignmentService.getOptionalCoursesContentGroupAssignmentIds(userId, masterId);
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }

        return this.baseMapper.selectDiscoverSubject(userId, masterId, name, subjectState, orgMaySubjectIds);
    }

    @Override
    public List<Course> selectCompletedSubject(Integer userId, Integer masterId, Integer subjectState, String name,
                                               HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.baseMapper.selectCompletedSubject(userId, masterId, name, subjectState);
    }

    @Override
    public List<Course> selectAllCourseSubject(Integer userId, Integer masterId, String name,
                                               HttpServletRequest request, Integer orderType) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.baseMapper.selectAllCourseSubject(userId, masterId, name, orderType);
    }

    @Override
    public List<Course> selectCompanyResourcesSubject(Integer userId, Integer masterId, String name,
                                                      HttpServletRequest request) {

        //查询顶级组may课程Ids
        List<Integer> orgMaySubjectIds =
            courseAssignmentService.getOptionalCoursesContentGroupAssignmentIds(userId, masterId);
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.baseMapper.selectCompanyResourcesSubject(userId, masterId, name, orgMaySubjectIds);
    }

    /**
     * 返回标识
     */
    public List<Course> putSubjectIdentifyings(List<Course> subjects, List<Integer> newAssignmentIds) {
        for (Course subject : subjects) {
            List<Integer> identifyings = new ArrayList<>();

            if (null != subject.getIdentifying()) {
                identifyings.add(subject.getIdentifying());

            }

            if (subject.getState() == null || subject.isPrivate()) {
                identifyings.add(TableConstant.COMMON_THREE);
            }

            if (null != subject.getIsToDo() && newAssignmentIds.contains(subject.getId())) {
                identifyings.add(TableConstant.COMMON_ZERO);
            }

            if (null != subject.getCertificatesFlag() &&
                subject.getCertificatesFlag().equals(TableConstant.COMMON_ONE)) {
                identifyings.add(TableConstant.COMMON_FOUR);
            }

            subject.setIdentifyings(identifyings);
        }
        return subjects;
    }

    public List<Integer> getNewAssignments(Integer masterId, Integer userId) {
        return this.baseMapper.getNewAssignments(userId, masterId);
    }

    @Override
    public List<Course> selectFromMyTeamSubject(Integer userId, Integer masterId, String name,
                                                HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.baseMapper.selectFromMyTeamSubject(userId, masterId, name);
    }

    @Override
    public List<Course> selectCreatedByTeams(Integer userId, Integer masterId, String name,
                                             HttpServletRequest request, List<String> groupCodeList,
                                             Integer orderType) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<Course> level0sublist =
            this.baseMapper.selectCreatedByTeamsSubject(userId, masterId, name, groupCodeList, orderType);
        for (Course subject : level0sublist) {
            subject.setMode(TableConstant.COMMON_ZERO);
        }
        return level0sublist;
    }

    @Override
    public List<Course> selectCreateByTeamsOrgAdmin(Integer userId, Integer masterId, String name,
                                                    HttpServletRequest request, List<String> groupCodeList,
                                                    Integer orderType) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<Course> orgAdminSubject =
            this.baseMapper.selectCreateByTeamsOrgAdmin(userId, masterId, name, groupCodeList, orderType);
        for (Course subject : orgAdminSubject) {
            subject.setMode(TableConstant.COMMON_ONE);
        }
        return orgAdminSubject;
    }

    @Override
    public List<Course> selectPublishedSubject(Integer userId, Integer masterId, String name,
                                               HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.baseMapper.selectPublishedSubject(userId, masterId, name);
    }

    @Override
    public List<Course> selectDraftsSubject(Integer userId, Integer masterId, String name,
                                            HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.baseMapper.selectDraftsSubject(userId, masterId, name);
    }

    @Override
    public List<Course> getSubjectInfoByList(List<Course> level0sublist, Integer masterId, Integer userId,
                                             HttpServletRequest request) {

        putSubjectIdentifyings(level0sublist, getNewAssignments(masterId, userId));

        List<Integer> level0subIds = level0sublist.stream().map(Course::getId).collect(Collectors.toList());
        List<Integer> subImgIds = level0sublist.stream().map(Course::getSubImgId).collect(Collectors.toList());
        List<SysFile> sysFileList = new ArrayList<>();
        if (TableConstant.COMMON_ZERO != subImgIds.size()) {
            sysFileList = sysFileService.listByIds(subImgIds);
        }
        //封面
        Map<Integer, SysFile> sysFileMap = sysFileList.stream()
            .collect(Collectors.toMap(SysFile::getId, SysFile -> SysFile, (key1, key2) -> key2, LinkedHashMap::new));
        //点赞
        Map<Integer, GcUser> subjectUsers = gcUserService.getWatchedUserNum(level0subIds, masterId);
        //评分
        Map<String, Object> videoParams = new HashMap<>(2);
        Integer ids = TableConstant.COMMON_ZERO;
        videoParams.put("ids", ids);
        videoParams.put("subjectIds", level0subIds);
        Map<Integer, GcUserVideoAction> subjectUserStar = new HashMap<>();
        subjectUserStar = videoActionService.getSubjectUserStar(videoParams);
        //视频时长和数量
        Map<Integer, videoLongVo> videosTotalLongMap = videoService.getVideoLongMapBySubjectId(level0subIds);

        //组装信息
        if (level0sublist != null && level0sublist.size() > 0) {
            for (Course li : level0sublist) {
                //封面
                if (null != sysFileMap.get(li.getSubImgId())) {
                    SysFile file = sysFileMap.get(li.getSubImgId());
                    file.setFullFileUrl(sysFileService.getResFullUrl(file, request));

                    li.setSubImgFile(file);
                }
                //参与人数
                if (CollectionUtils.isNotEmpty(subjectUsers)) {
                    GcUser gcUser = subjectUsers.get(li.getId());
                    if (null != gcUser) {
                        li.setSubjectUsers(gcUser.getSubjectUsers());
                    } else {
                        li.setSubjectUsers(TableConstant.COMMON_ZERO);
                    }
                }
                //评分
                GcUserVideoAction videoActions = subjectUserStar.get(li.getId());
                if (videoActions != null) {
                    //按type 进行分组
                    // 1.2k type=3的平均值 1.2k是打星的总人数
                    li.setStarValue(videoActions.getSubjectStarAvg());//星级平均值
                    // 打星总人数
                    li.setStarUsers(videoActions.getSubjectStarUsers());
                } else {
                    li.setStarValue(TableConstant.starValue0);//星级平均值
                    li.setStarUsers(TableConstant.starUsers);
                }
                //进度
            }
        }

        return level0sublist;
    }

    public List<Course> getSubjectInfos(List<Course> level0sublist, Integer userId, Integer masterId,
                                        HttpServletRequest request) {
        List<Integer> level0subIds = level0sublist.stream().map(Course::getId).collect(Collectors.toList());
        List<Integer> subImgIds = level0sublist.stream().map(Course::getSubImgId).collect(Collectors.toList());

        //获取封面
        List<SysFile> sysFileList = new ArrayList<>();
        if (TableConstant.COMMON_ZERO != subImgIds.size()) {
            sysFileList = sysFileService.listByIds(subImgIds);
        }
        Map<Integer, SysFile> sysFileMap = sysFileList.stream()
            .collect(Collectors.toMap(SysFile::getId, SysFile -> SysFile, (key1, key2) -> key2, LinkedHashMap::new));
        //点赞
        Map<Integer, GcUser> subjectUsers = gcUserService.getWatchedUserNum(level0subIds, masterId);
        //视频信息
        List<Integer> videoIdlist = videoService.getVideoIdListBySubId(level0subIds);
        List<GcVideo> videoList = videoService.getVideoLongListByVideoId(videoIdlist);
        if (null != userId) {
            videoList = videoService.buildVideoInfo(userId, videoList, masterId);
        }
        Map<Integer, List<GcVideo>> groupBySubId = videoList.stream().filter(e -> null != e.getSubjectSubId())
            .collect(Collectors.groupingBy(GcVideo::getSubjectSubId));
        Map<String, Object> videoParams = new HashMap<>(2);
        Integer ids = TableConstant.COMMON_ZERO;
        videoParams.put("ids", ids);
        videoParams.put("subjectIds", level0subIds);
        Map<Integer, GcUserVideoAction> subjectUserStar = new HashMap<>();
        subjectUserStar = videoActionService.getSubjectUserStar(videoParams);

        //组装信息
        if (level0sublist != null && level0sublist.size() > 0) {
            for (Course li : level0sublist) {
                if (null != sysFileMap.get(li.getSubImgId())) {
                    SysFile file = sysFileMap.get(li.getSubImgId());
                    file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
                    li.setSubImgFile(file);
                }

                if (CollectionUtils.isNotEmpty(subjectUsers)) {
                    GcUser gcUser = subjectUsers.get(li.getId());
                    if (null != gcUser) {
                        li.setSubjectUsers(gcUser.getSubjectUsers());
                    } else {
                        li.setSubjectUsers(TableConstant.COMMON_ZERO);
                    }
                }

                GcUserVideoAction videoActions = subjectUserStar.get(li.getId());
                if (videoActions != null) {
                    //按type 进行分组
                    // 1.2k type=3的平均值 1.2k是打星的总人数
                    li.setStarValue(videoActions.getSubjectStarAvg());//星级平均值
                    // 打星总人数
                    li.setStarUsers(videoActions.getSubjectStarUsers());
                } else {
                    li.setStarValue(TableConstant.starValue0);//星级平均值
                    li.setStarUsers(TableConstant.starUsers);
                }
            }
        }
        return level0sublist;
    }

    private List<Integer> videoIds(List<GcVideo> videos) {
        return videos.stream()
            .map(GcVideo::getId)
            .collect(Collectors.toList());
    }

    @Override
    public List<Course> setSubListImg(List<Course> list, SysSystem sys, HttpServletRequest request) {
        if (list != null && list.size() > 0) {
            for (Course li : list) {
                if (li.getSubImgId() == null) {
                    continue;
                }
                SysFile file = sysFileService.getById(li.getSubImgId());
                if (file != null) {
                    file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
                    li.setSubImgFile(file);
                }

            }
        }
        return list;
    }

    //编码不合理，需改造
    @Override
    public List<Course> getSubListWithImgByIds(List<Integer> subIds, SysSystem sys, HttpServletRequest request,
                                               Integer masterId) {
        List<Course> list = new ArrayList<Course>();
        for (Integer subId : subIds) {
            Course subject = this.baseMapper.selectTagsById(subId, masterId);
            if (null != subject) {
                List<Course> courses = this.baseMapper.selectListByMasterId(subject.getMasterId());
                this.lookupChildren(courses, subject, list);
            }
        }
        if (list != null && list.size() > 0) {
            for (Course li : list) {
                if (li.getSubImgId() == null) {
                    continue;
                }
                SysFile file = sysFileService.getById(li.getSubImgId());
                if (file != null) {
                    file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
                    li.setSubImgFile(file);
                }

            }
        }
        return list;
    }

    @Override
    public List<Course> getLevel0SubLis(Integer masterId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<Course>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("level", 0);
        queryWrapper.ne("state", CoursePublishState.PRIVATE.getValue());//不显示隐藏
        queryWrapper.orderByAsc("\"order\"");
        return this.list(queryWrapper);
    }

    @Override
    public List<Integer> getCourseIds(Integer masterId) {
        List<Course> courses = getLevel0SubLis(masterId);
        return courses.stream().map(Course::getId).collect(Collectors.toList());
    }

    @Override
    public List<Course> getSubListWithHidden(Integer masterId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<Course>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.orderByAsc("\"order\"");
        return this.list(queryWrapper);
    }

    @Override
    public List<Course> selectSubjectAssociation(Integer masterId, List<Integer> subIds, boolean ifLevel0) {
        List<Course> list = this.baseMapper.selectSubjectAssociation(masterId, subIds, ifLevel0);
        for (Course subject : list) {
            if (null != subject.getCourseTags() && subject.getCourseTags().size() != 0) {
                List lists = JSONArray.parseArray(subject.getCourseTags().toJSONString());
                HashSet hs = new HashSet(lists);
                subject.setCourseTags(JSONArray.parseArray(JSONObject.toJSONString(hs)));
            }
            sysFileService.getResFullUrlSaveType2(subject.getSubImgFile());
            if (subject.getType() == TableConstant.gcSubject_type_subject0) {
                subject.setOrder(subject.getSubjectAssociationOrder());//使用门户自己的排序
            }
        }

        return list;
    }

    @Override
    public List<Course> getSubList(Integer masterId, Integer subType) {
        // TODO Auto-generated method stub
        QueryWrapper<Course> queryWrapper = new QueryWrapper<Course>();
        queryWrapper.eq("master_id", masterId);
        if (Objects.nonNull(subType)) {
            queryWrapper.eq("type", subType);
        }
        queryWrapper.orderByAsc("\"order\"");
        return this.list(queryWrapper);
    }

    @Override
    public List<Course> selectAllTopicList(Integer masterId, Integer subType, Integer userId, Course course) {
        // TODO Auto-generated method stub
        QueryWrapper<Course> queryWrapper = new QueryWrapper<Course>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("create_user", userId);
        queryWrapper.eq("fid", course.getFid());
        queryWrapper.ne("id", course.getId());
        if (Objects.nonNull(subType)) {
            queryWrapper.eq("type", subType);
        }
        queryWrapper.orderByAsc("\"order\"");
        return this.list(queryWrapper);
    }

    @Override
    public List<Course> selectAllSub0ListByUserId(Integer masterId, Integer subType, Integer userId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<Course>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("create_user", userId);
        return this.list(queryWrapper);
    }

    @Override
    public List<Course> getSubList0(Integer masterId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<Course>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("level", 0);
        queryWrapper.orderByAsc("\"order\"");
        return this.list(queryWrapper);
    }

    @Override
    public List<Course> getSubListTop(Integer masterId) {
        List<Course> l = this.getTopSubList(masterId);
        List<Course> list = this.baseMapper.selectSubjectAssociation(masterId, null, false);
        l.addAll(list);
        return l;
    }

    public List<Course> getTopSubList(Integer masterId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<Course>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("level", 0);
        queryWrapper.eq("type", 0);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional
    public boolean deleteSub(Integer subId, Integer masterId) {
        Course subject = this.getById(subId);
        if (subject.getMasterId().intValue() == masterId.intValue()) {
            List<Course> list = this.getSubListWithHidden(subject.getMasterId());

            //计算当前属于第几层
            List<Course> resultList = new ArrayList<Course>();
            this.lookupChildren(list, subject, resultList);
            LOGGER.info(resultList.size() + "");


            List<Integer> subIds = resultList.stream().map(Course::getId).collect(Collectors.toList());
            //删除ResultList中的Subject下面的视频
            videoService.deleteVideoBySubIds(subIds);
            return this.removeByIds(subIds);
        }

        return true;
    }

    @Override
    public int getSubTopicNum(Integer masterId, List<Integer> subIds, Integer managerId) {
        // TODO Auto-generated method stub

        QueryWrapper<Course> queryWrapper = new QueryWrapper<Course>();
        queryWrapper.eq("master_id", masterId);
        queryWrapper.eq("level", 1);
        queryWrapper.ne("state", CoursePublishState.PRIVATE.getValue());//不显示隐藏
        if (subIds != null && subIds.size() != TableConstant.COMMON_ZERO) {
            queryWrapper.in(true, "fid", subIds);
        }
        //查询导入课程的topic数量
        Integer importedTopicNum = this.baseMapper.countImportedToicNum(masterId, TableConstant.COMMON_ONE,
            CoursePublishState.PRIVATE.getValue(), subIds, managerId);
        return this.count(queryWrapper) + importedTopicNum;


    }

    @Override
    public int getSubjectNum(Integer masterId, List<Integer> subIds, Integer managerId) {
        Integer type = TableConstant.COMMON_ZERO;
        return this.baseMapper.countCourseInPortal(masterId, type, subIds, managerId);

    }

    @Override
    public List<Integer> getSubjectIds(Integer masterId) {
        // TODO Auto-generated method stub
        QueryWrapper<Course> queryWrapper = new QueryWrapper<Course>();
        queryWrapper.select("id").eq("master_id", masterId).eq("type", TableConstant.gcSubject_type_subject0);
        queryWrapper.ne("state", CoursePublishState.PRIVATE.getValue());//不显示隐藏
        return this.list(queryWrapper).stream().map(Course::getId).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getPublicSubjectIds(Integer masterId) {
        return this.baseMapper.getPublicSubjectIds(masterId);
    }

    @Override
    public List<Integer> getSubjectChildIds(Integer subId) {
        // TODO Auto-generated method stub
        QueryWrapper<Course> queryWrapper = new QueryWrapper<Course>();
        queryWrapper.select("id").eq("fid", subId);
        queryWrapper.ne("state", CoursePublishState.PRIVATE.getValue());//不显示隐藏
        queryWrapper.orderByAsc("\"order\"");
        return this.list(queryWrapper).stream().map(Course::getId).collect(Collectors.toList());
    }

    @Override
    public List<Course> getSubjectChild(Integer subId) {
        // TODO Auto-generated method stub
        QueryWrapper<Course> queryWrapper = new QueryWrapper<Course>();
        queryWrapper.eq("fid", subId);
        queryWrapper.ne("state", CoursePublishState.PRIVATE.getValue());//不显示隐藏
        queryWrapper.orderByAsc("\"order\"");
        return this.list(queryWrapper);
    }

    @Override
    public Course getSubNameBysubId(Integer subId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<Course>();
        queryWrapper.eq("id", subId);
        queryWrapper.ne("state", CoursePublishState.PRIVATE.getValue());//不显示隐藏
        return this.getOne(queryWrapper);
    }

    private short buildCompleteStatusEventNum(String playState, Integer answeredNums, Integer answeredSumNums) {
        if (null == playState) {//没有播放记录
            return TableConstant.VIDEO_COMPLETE_STATUS0;
        }
        if (playState.equals("1") && answeredNums.equals(answeredSumNums)) {
            //看完视频，并且回答完
            return TableConstant.VIDEO_COMPLETE_STATUS2;
        }
        return TableConstant.VIDEO_COMPLETE_STATUS1;
    }

    @Override
    public List<Course> getChildSubjectBySubId(Integer subId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<Course>();
        queryWrapper.eq("fid", subId);
        queryWrapper.ne("state", CoursePublishState.PRIVATE.getValue());//不显示隐藏
        queryWrapper.orderByAsc("\"order\"");
        return this.list(queryWrapper);
    }

    @Override
    public Course getSubByVid(Integer vid) {
        // TODO Auto-generated method stub
        return this.baseMapper.selectSubByVid(vid);
    }

    @Override
    public List<Course> getSubVideoEventList(Integer subId, Integer studentId, Integer masterId,
                                             HttpServletRequest request) {
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return this.baseMapper.getSubVideoEventList(subId, studentId, masterId);
    }

    @Override
    public List<Course> getLevel1VideoEventList(Integer subId, Integer userId, Integer teacherId) {
        return this.baseMapper.getLevel1VideoEventList(subId, userId, teacherId);
    }

    @Override
    public Map<String, Object> selectEventResNumMapForWorkbook(Integer subId, Integer userId) {
        return this.baseMapper.selectEventResNumMapForWorkbook(subId, userId);
    }

    @Override
    public Map<String, Object> getAnswerMessageMapForTeacherWorkbook(Integer subId, Integer studentId,
                                                                     Integer teacherId) {
        return this.baseMapper.getAnswerMessageMapForTeacherWorkbook(subId, studentId, teacherId);

    }

    @Override
    public Map<String, Object> selectEventResNumMapForWorkbookTeacher(Integer subId, Integer studentId,
                                                                      Integer teacherId) {
        return this.baseMapper.selectEventResNumMapForWorkbookTeacher(subId, studentId, teacherId);
    }

    @Override
    public List<Course> getSubListByIds(List<Integer> subIds, HttpServletRequest request) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        if (subIds.size() != 0) {
            queryWrapper.in("id", subIds);
            queryWrapper.ne("state", CoursePublishState.PRIVATE.getValue());//不显示隐藏
            PageParam pageParam = new PageParam(request);
            Integer pageNum = pageParam.getPageNum();
            Integer pageSize = pageParam.getPageSize();
            if (pageNum > 0 && pageSize > 0) {
                PageHelper.startPage(pageNum, pageSize);
            }
            return this.list(queryWrapper);
        }
        //如果学生权限为空，返回空list
        List<Course> courses = new ArrayList<>();
        return courses;
    }

    @Override
    public JSONArray addUnReadTag(List<GcEvent> eventList, Integer teacherId, Integer studentId, Integer masterId) {
        List<Integer> eventIds = eventList.stream().map(GcEvent::getId).collect(Collectors.toList());
        //查出video下所有事件的问题回答
        List<Map<String, Object>> outEventAnswerUnReadList = masterMessageService.getEventAnswerUnReadList(eventIds,
            studentId, masterId, teacherId);
        //查出video下所有事件的资源回答，SELECT mm.id,mm.res_id,ua.event_id FROM gc_master_message AS mm LEFT JOIN gc_user_answer AS ua ON ua.id = mm.res_id WHERE ua.event_id IN ( ? , ? , ? , ? , ? ) AND mm.master_id = ? AND mm.event_type = 5 AND mm.user_id = ? AND mm.target_user_id = ? AND mm.read_state = 0
        List<Map<String, Object>> videoResourceLists =
            userEventResourceService.getALLResourceListByEventIds(eventIds, studentId, teacherId);

        List<Map<String, Object>> videoResourceList = this.removeRepeatMapByKey(videoResourceLists, "id");

        List<Integer> videoAllResourceIds = new ArrayList<>();
        List<Map<String, Object>> outEventUnReadMessageCount = new ArrayList<>();
        if (videoResourceList != null && videoResourceList.size() > 0) {
            for (Map<String, Object> map : videoResourceList) {
                videoAllResourceIds.add(Integer.parseInt((String) map.get("id")));
            }
            outEventUnReadMessageCount =
                masterMessageService.getUnReadMessageCountByResourceIds(videoAllResourceIds, studentId, masterId,
                    teacherId);
        }

        JSONArray reJSONArray = new JSONArray();

        for (GcEvent gcEvent : eventList) {
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
            if (outEventAnswerUnReadList != null && outEventAnswerUnReadList.size() > 0) {
                for (Map<String, Object> map : outEventAnswerUnReadList) {
                    if (map.get("event_id").equals(gcEvent.getId())) {
                        eventObject.put("newAnswer", false);
                    }
                }
            }
            reJSONArray.add(eventObject);
        }
        return reJSONArray;
    }

    @Override
    public boolean changeSubOrder(List<Integer> subIds, Integer masterId) {

        //subIds可能包含导入的课程，通过masterId判断
        List<Course> subjectList = new ArrayList<>();
        List<GcSubjectAssociation> subjectAssociationList = new ArrayList<>();

        Integer order = 1;
        for (Integer subId : subIds) {
            Course thisSub = this.getById(subId);

            if (thisSub.getMasterId().intValue() == masterId.intValue()) {
                thisSub.setOrder(order);

                subjectList.add(thisSub);
                order++;
            } else {
                GcSubjectAssociation sa = new GcSubjectAssociation();
                sa.setMasterId(masterId);
                sa.setSubjectId(subId);
                sa.setOrder(order);
                subjectAssociationList.add(sa);
                order++;
            }

        }
        if (subjectAssociationList != null && subjectAssociationList.size() > 0) {
            gcSubjectAssociationMapper.bulkUpdatOrderByMasterIdAndSubjetId(subjectAssociationList);
        }
        return this.updateBatchById(subjectList);
    }

    @Override
    public List<Course> listSubByIds(List<Integer> subIds) {
        return this.baseMapper.listSubByIds(subIds);
    }

    @Override
    public List<Course> listSubByIdsAndName(List<Integer> subIds, String name) {
        return this.baseMapper.listSubByIdsAndName(subIds, name);
    }

    @Override
    public List<Course> listSubWithAssoByIds(Integer masterId, List<Integer> subIds) {
        return this.baseMapper.listSubWithAssoByIds(masterId, subIds);
    }

    @Override
    public Integer countCourseForName(Course subject) {
        return this.baseMapper.countCourseForName(subject);
    }

    @Override
    public List<Course> selecUnitNumForVideo(Integer videoId) {
        return this.baseMapper.selecUnitNumForVideo(videoId);
    }

    @Override
    public List<Integer> countSessions(List<Integer> id) {
        return newUiGcSubjectMapper.countSessions(id);
    }

    @Override
    public List<Course> selectTwoSubjectsByFids(List<Integer> fids) {
        return coursetMapper.selectTwoSubjectsByFids(fids);
    }

    @Override
    public List<Course> selectAllLevel1SubList(List<Integer> subIds, String order, Integer masterId) {
        return coursetMapper.selectAllLevel1SubList(subIds, order, masterId);
    }

    @Override
    public Course saveSubInfo(Course course, GcManager manager, GcMaster master, GcUser user,
                              HttpServletRequest request) {
        Integer masterId = master.getId();
        if (course.getFid() == null || course.getFid() == 0) {
            course.setFid(null);
        }
        Course fullCourse = this.getById(course.getId());
        if (Objects.isNull(course.getTagType())) {
            if (course.getMasterId() != null && course.getMasterId().intValue() != masterId) {
                throw new SystemException("该课程不属于该门户，不可修改状态");
            }

            if (course.getId() != null && course.getMasterId() == null) {
                if (fullCourse.getMasterId().intValue() != masterId) {
                    throw new SystemException("该课程不属于该门户，不可修改状态");
                }
            }
        }

        if (course.getState() == null) {
            course.setState(
                course.isTopic() ? CoursePublishState.PUBLIC.getValue() : CoursePublishState.PRIVATE.getValue());
        }
        if (course.getMasterId() == null) {
            course.setMasterId(masterId);
        }
        if (course.getName() == null) {
            throw new SystemException("名字name不可空");
        }

        int courseWithSameNameCount = this.countCourseForName(course);
        if (courseWithSameNameCount > 0) {
            throw new SystemException(10000,
                "'" + course.getName() + "'- " + I18NUtil.get("guidecore.master.sameCourseNameNotice"));
        }

        if (null == course.getId() && null == course.getOrder()) {
            List<Course> subjectList = this.getSubList(masterId, null);
            if (!org.springframework.util.CollectionUtils.isEmpty(subjectList)) {
                int max = subjectList.stream().mapToInt(Course::getOrder).max().getAsInt();
                course.setOrder(max + 1);
            }
        }

        if (null != user) {
            course.setCreateUser(user.getId());
        }
        if (null != course.getId()) {
            course.setUpdateTime(new Date());
        }

        this.saveSub(course);

        Course subs = this.getById(course.getId());
        course.setCreateTime(subs.getCreateTime());
        course.setUpdateTime(subs.getUpdateTime());

        course.setSubImgFile(sysFileService.getById(course.getSubImgId()));
        sysFileService.getResFullUrl(course.getSubImgFile(), request);

        if (course.getFid() == null && manager != null) {
            GcUserAccess gcUserAccess = userAccessService.selectUserAccessByManagerAndMaster(manager.getId(), masterId);
            if (gcUserAccess != null) {
                List<Integer> courseIds =
                    courseAssignmentService.getCourseIdsByContentGroupId(gcUserAccess.getAccessId());
                if (!courseIds.contains(course.getId())) {
                    courseAssignmentService.save(user, course, CourseType.OPTIONAL);
                }
            }
        }

        if (!org.springframework.util.CollectionUtils.isEmpty(course.getSubdetail_img_id())) {
            Integer subDetailImgId = Integer.parseInt(course.getSubdetail_img_id().get("subDetailImgId").toString());
            SysFile sysFile = sysFileService.getById(subDetailImgId);
            sysFileService.getResFullUrl(sysFile, request);
            course.setSubDetailImgUrl(sysFile.getFullFileUrl());
        }

        return course;
    }

    @Override
    public Integer selectSubjectPt(String subjectName, Integer type, Integer state, Integer createUser,
                                   Integer masterId, Integer userId, List<Integer> channelIds,
                                   List<Integer> publicSubjectIds) {
        return this.baseMapper.selectSubjectPt(subjectName, type, state, createUser, masterId, userId, channelIds,
            publicSubjectIds).size();
    }

    @Override
    public Integer getCreateUserPublished(Integer userId, Integer masterId, Integer state) {
        return this.baseMapper.getCreateUserPublished(userId, masterId, state);
    }

    @Override
    public List<Course> getAvailableCourses(String name, Integer masterId, List<Integer> subIds, Integer userId,
                                            String order) {
        return this.baseMapper.getAvailableCourses(name, masterId, subIds, userId, order);
    }

    @Override
    public List<Course> newGetAvailableCourses(String name, Integer masterId, List<Integer> subIds, Integer userId,
                                               String order) {
        return this.baseMapper.newGetAvailableCourses(name, masterId, subIds, userId, order);
    }

    @Override
    public void populateUserId(Course course, GcUser user) {
        if (course.getId() == null) {
            course.setCreateUser(user.getId());
            return;
        }
        Course existingCourse = this.getById(course.getId());
        if (existingCourse.isTopic()) {
            existingCourse = this.getById(existingCourse.getFid());
            course.setCreateUser(existingCourse.getCreateUser());
            return;
        }

        course.setCreateUser(existingCourse.getCreateUser());
    }

    @Override
    public CourseProgramDto courseProgram(Integer courseId, PortalUser portalUser) {
        Course course = this.getById(courseId);

        if (!authorizationService.checkAccess(course, PermitAction.VIEW, portalUser)) {
            log.error("User {} is not authorized to view course {}", portalUser.getUserId(), courseId);
            throw new ForbiddenException("User is not authorized to view this course");
        }

        return courseMapping.mapProgram(course, courseTopicsContent(courseContentService.findCourseContent(courseId)));
    }

    private List<CourseContent> courseTopicsContent(List<CourseContent> courseContent) {
        return courseContent.stream()
            .filter(content -> content.getCourse().isTopic())
            .collect(Collectors.toList());
    }

    @Override
    public void updateUrls(Course course) {
        SysFile subImgFile = course.getSubImgFile();
        if (subImgFile == null) {
            return;
        }

        String fullFileUrl = sysFileService.getFullFileUrl(subImgFile.getFileUrl());
        subImgFile.setFileUrl(fullFileUrl);
        subImgFile.setFullFileUrl(fullFileUrl);
        subImgFile.setSnapshotUrl(sysFileService.getFullFileUrl(subImgFile.getThumbNailUrl()));
    }

    @Override
    public CourseVideoBookmarkDto lastViewedBookmark(Integer courseId, PortalUser portalUser) {
        List<GcVideo> courseVideos = courseVideos(courseId);
        CourseEnrollment activeCourseEnrollment =
            courseEnrollmentService.getActiveEnrollment(courseId, portalUser.getUserId());

        return playSegmentService.videoBookmark(videoIds(courseVideos), activeCourseEnrollment.getStartDate());
    }

    @Override
    public List<GcVideo> courseVideos(Integer courseId) {
        return courseContentService.findCourseContent(courseId).stream()
            .map(CourseContent::getVideo)
            .filter(video -> video.getSubId() != null)
            .collect(Collectors.toList());
    }

    @Override
    public CourseListDto<AssignedCourseDto> getAssignedCourses(PortalUser portalUser) {
        Map<Integer, List<GcContentGroupCourseAssignment>> courseIdToAssignments = getCourseIdToAssignments(portalUser);

        List<AssignedCourseDto> assignedCourses = new ArrayList<>();

        for (Map.Entry<Integer, List<GcContentGroupCourseAssignment>> courseAssignmentEntry : courseIdToAssignments.entrySet()) {
            Integer courseId = courseAssignmentEntry.getKey();
            List<GcContentGroupCourseAssignment> assignments = courseAssignmentEntry.getValue();

            GcContentGroupCourseAssignment strongestAssignment = findStrongestAssignment(assignments);
            Course course = strongestAssignment.getCourse();
            updateUrls(course);
            Map<String, Boolean> permissions = authorizationService.listPermissions(course, portalUser);
            int studentsCount = courseEnrollmentService.countUniqueUsersInCourseEnrollments(courseId);
            int activeStudentsCount = courseEnrollmentService.countActiveUniqueUsersInCourseEnrollments(courseId);
            List<CourseContent> courseContent = courseTopicsContent(courseContentService.findCourseContent(courseId));

            assignedCourses.add(
                createAssignedCourseDto(course, courseContent, strongestAssignment, studentsCount, activeStudentsCount,
                    permissions));
        }

        return createCourseListDto(assignedCourses);
    }

    @Override
    public CourseListDto<CourseDto> getOwnedCourses(PortalUser portalUser) {
        List<Course> courses = ownedCourses(portalUser);
        Set<Integer> courseIds = courses.stream().map(Course::getId).collect(Collectors.toSet());

        List<CourseDto> ownedCourses = courses.stream()
            .map(ownedCourse -> {
                List<CourseContent> courseContent = courseContentService.findCourseContent(ownedCourse.getId());
                int studentsCount = courseEnrollmentService.countUniqueUsersInCourseEnrollments(ownedCourse.getId());
                int activeStudentsCount =
                    courseEnrollmentService.countActiveUniqueUsersInCourseEnrollments(ownedCourse.getId());
                Map<String, Boolean> permissions = authorizationService.listPermissions(ownedCourse, portalUser);

                return createCourseDto(ownedCourse, courseContent, studentsCount, activeStudentsCount, permissions);
            })
            .collect(Collectors.toList());

        return createCourseListDto(ownedCourses);
    }

    @Override
    public CourseListDto<CourseDto> getDiscoverableCourses(PortalUser portalUser) {
        List<Course> publicCourses = getPublicCourses(portalUser);
        List<CourseEnrollment> courseEnrollments = courseEnrollmentService.courseEnrollments(courseIds(publicCourses));
        Map<Integer, Integer> courseIdToUserUniqueEnrollmentCount =
            getCourseIdToUserUniqueEnrollmentCount(courseEnrollments);

        List<CourseDto> discoverableCourses = new ArrayList<>();
        for (Course publicCourse : discoverableCourses(publicCourses, courseEnrollments, portalUser)) {
            List<CourseContent> courseContent =
                courseTopicsContent(courseContentService.findCourseContent(publicCourse.getId()));
            int studentsCount = courseIdToUserUniqueEnrollmentCount.getOrDefault(publicCourse.getId(), 0);
            int activeStudentsCount =
                courseEnrollmentService.countActiveUniqueUsersInCourseEnrollments(publicCourse.getId());
            Map<String, Boolean> permissions = authorizationService.listPermissions(publicCourse, portalUser);

            discoverableCourses.add(
                createCourseDto(publicCourse, courseContent, studentsCount, activeStudentsCount, permissions));
        }

        return createCourseListDto(discoverableCourses);
    }

    private List<Course> discoverableCourses(List<Course> courses, List<CourseEnrollment> courseEnrollments,
                                             PortalUser portalUser) {
        Map<Integer, List<GcContentGroupCourseAssignment>> courseIdToAssignments = getCourseIdToAssignments(portalUser);
        Set<Integer> courseIdsWithAssignment = courseIdToAssignments.keySet();

        Set<Integer> userActiveEnrollmentCourseIds =
            getUserActiveEnrollmentCourseIds(courseEnrollments, portalUser.getUserId());

        return courses.stream()
            .filter(publicCourse -> !courseIdsWithAssignment.contains(publicCourse.getId()))
            .filter(publicCourse -> !userActiveEnrollmentCourseIds.contains(publicCourse.getId()))
            .filter(publicCourse -> !anyNotActiveEnrollmentsFinished(courseEnrollments))
            .collect(Collectors.toList());
    }

    @Override
    public List<Course> searchCourses(String searchName, PortalUser portalUser) {
        List<Course> courses =
            baseMapper.searchCourses(searchName, portalUser.getUserId(), portalUser.getMasterId());
        courses.forEach(this::updateUrls);
        return courses;
    }

    @Override
    public List<Course> searchSuggestedCourses(PortalUser portalUser) {
        List<Course> publicCourses = getPublicCourses(portalUser);
        List<CourseEnrollment> courseEnrollments = courseEnrollmentService.courseEnrollments(courseIds(publicCourses));

        return discoverableCourses(publicCourses, courseEnrollments, portalUser);
    }

    @Override
    public CourseDto getCourseDetails(Integer courseId, PortalUser portalUser) {
        Course course = baseMapper.getCourseById(courseId);
        if (!authorizationService.checkAccess(course, PermitAction.VIEW, portalUser)) {
            throw new ForbiddenException("User has no access to the course");
        }
        updateUrls(course);

        Map<String, Boolean> permissions = authorizationService.listPermissions(course, portalUser);
        List<CourseContent> courseContent = courseContentService.findCourseContent(courseId);
        int uniqueUsersInCourseEnrollmentCount = courseEnrollmentService.countUniqueUsersInCourseEnrollments(courseId);
        int activeStudentsCount =
            courseEnrollmentService.countActiveUniqueUsersInCourseEnrollments(courseId);

        return createCourseDto(course, courseContent, uniqueUsersInCourseEnrollmentCount, activeStudentsCount,
            permissions);
    }

    private boolean anyNotActiveEnrollmentsFinished(List<CourseEnrollment> courseEnrollments) {
        return courseEnrollments.stream()
            .filter(courseEnrollment -> courseEnrollment.getEndDate() != null)
            .map(CourseEnrollment::getLatestProgress)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .anyMatch(courseEnrollmentProgress -> courseEnrollmentProgress.getPercentage() >= 99);
    }

    private Map<Integer, List<GcContentGroupCourseAssignment>> getCourseIdToAssignments(PortalUser portalUser) {
        List<GcContentGroupCourseAssignment> courseAssignments =
            courseAssignmentService.userCourseAssignments(portalUser);
        return courseAssignments.stream()
            .collect(Collectors.groupingBy(GcContentGroupCourseAssignment::getCourseId));
    }

    private Map<Integer, Integer> getCourseIdToUserUniqueEnrollmentCount(List<CourseEnrollment> courseEnrollments) {
        return courseEnrollments.stream()
            .collect(Collectors.groupingBy(
                CourseEnrollment::getCourseId,
                Collectors.collectingAndThen(
                    Collectors.mapping(CourseEnrollment::getUserId, Collectors.toSet()),
                    Set::size
                )
            ));
    }

    private Set<Integer> getUserActiveEnrollmentCourseIds(List<CourseEnrollment> courseEnrollments, Integer userId) {
        return courseEnrollments.stream()
            .filter(courseEnrollment -> courseEnrollment.getUserId().equals(userId))
            .filter(courseEnrollment -> courseEnrollment.getEndDate() == null)
            .map(CourseEnrollment::getCourseId)
            .collect(Collectors.toSet());
    }

    private List<Course> getPublicCourses(PortalUser portalUser) {
        List<Course> courses =
            baseMapper.coursesByState(CoursePublishState.PUBLIC.getValue(), portalUser.getMasterId());
        courses.forEach(this::updateUrls);
        return courses;
    }

    private List<Course> ownedCourses(PortalUser portalUser) {
        List<Course> courses = baseMapper.ownedCourses(portalUser.getUserId(), portalUser.getMasterId());
        courses.forEach(this::updateUrls);
        return courses;
    }

    private GcContentGroupCourseAssignment findStrongestAssignment(
        List<GcContentGroupCourseAssignment> assignments) {
        List<GcContentGroupCourseAssignment> mandatoryAssignments = assignments.stream()
            .filter(contentGroupCourseAssignment -> contentGroupCourseAssignment.getMandatory() == 1)
            .collect(Collectors.toList());

        return getAssignmentWithEarliestDeadLineDate(mandatoryAssignments)
            .or(() -> getAssignmentWithLatestDate(mandatoryAssignments))
            .or(() -> getAssignmentWithLatestDate(assignments))
            .orElseThrow(() -> new ResourceNotFoundException("Cannot find course assignment!"));
    }

    private Optional<GcContentGroupCourseAssignment> getAssignmentWithEarliestDeadLineDate(
        List<GcContentGroupCourseAssignment> assignments) {

        return assignments.stream()
            .filter(assignment -> assignment.getDeadline() != null)
            .min(Comparator.comparing(GcContentGroupCourseAssignment::getDeadline));
    }

    private AssignedCourseDto createAssignedCourseDto(Course course, List<CourseContent> courseContent,
                                                      GcContentGroupCourseAssignment courseAssignment,
                                                      int studentsCount, int activeStudentsCount,
                                                      Map<String, Boolean> permissions) {
        AssignedCourseDto assignedCourseDto =
            new AssignedCourseDto(
                createCourseDto(course, courseContent, studentsCount, activeStudentsCount, permissions));
        assignedCourseDto.setDeadline(courseAssignment.getDeadline());
        assignedCourseDto.setIsMandatory(courseAssignment.getMandatory() == 1);
        return assignedCourseDto;
    }

    private Set<Integer> courseIds(List<Course> publicCourses) {
        return publicCourses.stream().map(Course::getId).collect(Collectors.toSet());
    }

    private CourseDto createCourseDto(Course course, List<CourseContent> courseContent,
                                      int studentsCount, int activeStudentsCount, Map<String, Boolean> permissions) {
        List<Task> courseTasks = courseTasks(courseContent);
        CourseDto courseDto = new CourseDto();

        courseDto.setId(course.getId());
        courseDto.setTitle(course.getName());
        courseDto.setDescription(course.getDescription());
        courseDto.setThumbUrl(course.getSubImgFile() == null ? null : course.getSubImgFile().getFullFileUrl());
        courseDto.setIsPublic(course.isPublic());
        courseDto.setIsPrivate(course.isPrivate());
        courseDto.setVideosCount(courseContent.size());
        courseDto.setVideosDuration(videoTotalDuration(courseContent));
        courseDto.setTasksCount(courseTasks.size());
        courseDto.setTasksDuration(taskDuration(courseTasks));
        courseDto.setStudentsCount(studentsCount);
        courseDto.setActiveStudentsCount(activeStudentsCount);
        courseDto.setAverageRating(0);
        courseDto.setPermissions(permissions);

        return courseDto;
    }

    private int taskDuration(List<Task> courseTasks) {
        return courseTasks.stream()
            .map(Task::getType)
            .mapToInt(TaskTimingUtil::getTaskTiming)
            .sum();
    }

    private List<Task> courseTasks(List<CourseContent> courseContent) {
        return courseContent.stream()
            .map(CourseContent::getVideo)
            .map(GcVideo::getTasks)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private int videoTotalDuration(List<CourseContent> courseContent) {
        return courseContent.stream()
            .map(CourseContent::getVideo)
            .mapToInt(GcVideo::getVideoTime)
            .sum();
    }

    private <T extends BasicCourseDto> CourseListDto<T> createCourseListDto(List<T> courses) {
        CourseListDto<T> courseListDto = new CourseListDto<>();
        courseListDto.setCourses(courses);
        return courseListDto;
    }
}
