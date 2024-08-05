package com.threeatom.guidecore.controller.api.user.newUI;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.EnvType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.exception.LicenseLimitExceededException;
import com.threeatom.guidecore.service.*;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guidecore/newui/SaveContent") // 与VideoGuideCoreController的一致，注意命名
@Api(tags = "用户端保存的内容相关api")
public class NewUISaveContentController extends GuideCoreController {

    @Autowired private GcUserSaveFolderService gcUserSaveFolderService;

    @Autowired private GcUserSaveContentService gcUserSaveContentService;

    @Autowired private SysFileService sysFileService;

    @Autowired private NewUiGcSubjectService subjectService;

    @Autowired private GcVideoService gcVideoService;

    @Autowired private GcUserSaveContentFollowService gcUserSaveContentFollowService;

    @Autowired private GcUserService gcUserService;

    @Autowired private GcUserInfoService gcUserInfoService;

    @Autowired private GcMasterService gcMasterService;
    @Autowired private GcMasterHomeInfoService iGcMasterHomeInfoService;
    @Autowired private UserLicenseService userLicenseService;

    @ApiOperation(value = "获取已有保存课程/视频的文件夹列表", httpMethod = "GET")
    @GetMapping("/contentFolderList")
    public Message contentFolderList(HttpServletRequest request) {
        GcUser user = this.getGcUser();
        PageParam pageParam = new PageParam(request);
        Integer pageNum = pageParam.getPageNum();
        Integer pageSize = pageParam.getPageSize();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<GcUserSaveFolder> list =
                gcUserSaveFolderService.selectFolderForUserMaster(
                        user.getId(), getHeaderMasterId(request), null, request, null);
        for (GcUserSaveFolder folder : list) {
            for (GcUserSaveContent content : folder.getSaveContentList()) {
                if (content.getVideo() != null)
                    sysFileService.getVideoSnapshotUrl(content.getVideo());
                if (content.getSubject() != null)
                    sysFileService.getResFullUrl(content.getSubject().getSubImgFile(), request);
            }
        }
        PageInfo<GcUserSaveFolder> pageInfo = new PageInfo<>(list);
        Message m = new Message().ok().addData("list", pageInfo);
        return m;
    }

    @ApiOperation(value = "pt-playlist", httpMethod = "GET")
    @GetMapping("/" + "ptContentFolderList")
    public Message ptContentFolderList(HttpServletRequest request) {
        Message m = new Message();
        // myplaylist
        List<GcUserSaveFolder> list =
                gcUserSaveFolderService.selectFolderForUserMaster(
                        this.getGcUser().getId(), getHeaderMasterId(request), null, request, null);
        List<Integer> listIds = list.stream().map(GcUserSaveFolder::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(listIds)) {
            List<GcUserSaveContentFollow> gcUserSaveContentFollowList =
                    gcUserSaveContentFollowService.selectFollowListByPlayListId(listIds);
            Map<Integer, List<GcUserSaveContentFollow>> map =
                    gcUserSaveContentFollowList.stream()
                            .collect(Collectors.groupingBy(GcUserSaveContentFollow::getFolderId));
            for (GcUserSaveFolder folder : list) {
                if (Objects.nonNull(folder.getFirstVideoFileId())) {
                    SysFile sysFile = sysFileService.getById(folder.getFirstVideoFileId());
                    String fullfileurl = sysFileService.getVideoSnapshotUrl(sysFile);
                    folder.setFullFileUrl(sysFileService.getResFullUrl(sysFile, request));
                    folder.setSnapshotUrl(fullfileurl);
                }
                List<GcUserSaveContentFollow> list1 = map.get(folder.getId());
                if (CollectionUtils.isNotEmpty(list1)) {
                    folder.setFollowNum(list1.size());
                }
                for (GcUserSaveContent content : folder.getSaveContentList()) {
                    if (content.getVideoFile() != null)
                        sysFileService.getVideoSnapshotUrl(content.getVideo());
                    if (content.getSubject() != null)
                        sysFileService.getResFullUrl(content.getSubject().getSubImgFile(), request);
                }
            }
        }
        PageInfo<GcUserSaveFolder> pageInfo = new PageInfo<>(list);
        m.ok().addData("myPlayList", pageInfo);

        // followedplaylist
        List<Integer> gcUserSaveContentFollowIdList =
                gcUserSaveContentFollowService.selectFollowPlayList(
                        this.getGcUser().getId(), getHeaderMasterId(request));
        if (CollectionUtils.isNotEmpty(gcUserSaveContentFollowIdList)) {
            List<GcUserSaveFolder> followedplaylist =
                    gcUserSaveFolderService.selectFolderForUserMaster(
                            null, getHeaderMasterId(request), gcUserSaveContentFollowIdList, request, null);
            for (GcUserSaveFolder gcUserSaveFolder : followedplaylist) {
                // playlist下的视频数量
                List<GcUserSaveContent> contents = gcUserSaveFolder.getSaveContentList();
                List<GcUserSaveContent> videoContents =
                        contents.stream().filter(e -> e.getFileId() != null).collect(Collectors.toList());
                gcUserSaveFolder.setVideoNum(videoContents.size());
                if (Objects.nonNull(gcUserSaveFolder.getFirstVideoFileId())) {
                    SysFile sysFile = sysFileService.getById(gcUserSaveFolder.getFirstVideoFileId());
                    gcUserSaveFolder.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(sysFile));
                    gcUserSaveFolder.setFullFileUrl(sysFileService.getResFullUrl(sysFile, request));
                }
                gcUserSaveFolder.setFollowFlag(TableConstant.COMMON_ONE);
            }
            PageInfo<GcUserSaveFolder> followedplaylistPageInfo = new PageInfo<>(followedplaylist);
            m.ok().addData("followedplaylist", followedplaylistPageInfo);
        } else {
            m.ok().addData("followedplaylist", new PageInfo<>());
        }
        // recommenplaylist
        List<GcUserSaveFolder> recommentPlayList =
                gcUserSaveFolderService.selectFolderInMaster(getHeaderMasterId(request));
        List<Integer> recommenFolderIds =
                recommentPlayList.stream().map(GcUserSaveFolder::getId).collect(Collectors.toList());
        List<GcUserSaveFolder> recommenFolderList =
                gcUserSaveFolderService.selectFolderForUserMaster(
                        null, getHeaderMasterId(request), recommenFolderIds, request, listIds);
        for (GcUserSaveFolder gcUserSaveFolder : recommenFolderList) {
            if (gcUserSaveContentFollowIdList.contains(gcUserSaveFolder.getId())) {
                gcUserSaveFolder.setFollowFlag(TableConstant.COMMON_ONE);
            }
            // playlist下的视频数量
            List<GcUserSaveContent> contents = gcUserSaveFolder.getSaveContentList();
            List<GcUserSaveContent> videoContents =
                    contents.stream().filter(e -> e.getFileId() != null).collect(Collectors.toList());
            gcUserSaveFolder.setVideoNum(videoContents.size());
            System.out.println("????::::::" + gcUserSaveFolder);
            // 缩略图
            //			if(CollectionUtils.isNotEmpty(gcUserSaveFolder.getSaveContentList())) {
            if (Objects.nonNull(gcUserSaveFolder.getFirstVideoFileId())) {
                SysFile sysFile = sysFileService.getById(gcUserSaveFolder.getFirstVideoFileId());
                String fullfileurl = sysFileService.getResFullUrl(sysFile, request);
                gcUserSaveFolder.setSnapshotUrl(sysFileService.getVideoSnapshotUrl(sysFile));
                gcUserSaveFolder.setFullFileUrl(fullfileurl);
            }
        }
        PageInfo<GcUserSaveFolder> recommenFolderListPageInfo = new PageInfo<>(recommenFolderList);
        m.ok().addData("recommenFolderList", recommenFolderListPageInfo);
        List<GcMasterHomeInfo> allHomeInfos =
                iGcMasterHomeInfoService.getGcMasterHomeInfoList(
                        request.getIntHeader("masterId"),
                        TableConstant.gcMasterHomeInfo_name_homepage_list,
                        getSystem(),
                        request);
        m.ok().addData("homeInfos", allHomeInfos);
        return m;
    }

    @PostMapping("/saveDeletePlayListFollow")
    public Message savePlayListFollow(
            @RequestBody GcUserSaveContentFollow gcUserSaveContentFollow, HttpServletRequest request) {
        Message message = new Message();
        GcUserSaveContentFollow gcUserSaveContentFollow1 =
                gcUserSaveContentFollowService.selectFollowByUserId(
                        this.getGcUser().getId(), gcUserSaveContentFollow.getFolderId());
        if (Objects.isNull(gcUserSaveContentFollow1)) {
            gcUserSaveContentFollow.setUserId(this.getGcUser().getId());
            if (gcUserSaveContentFollowService.saveOrUpdate(gcUserSaveContentFollow)) {
                return message.ok("success");
            } else {
                return message.error("error");
            }
        } else {
            if (gcUserSaveContentFollowService.removeById(gcUserSaveContentFollow1)) {
                return message.ok("success");
            } else {
                return message.error("error");
            }
        }
    }

    // 参数：name，id-更新
    @ApiOperation(value = "新建一个保存课程/视频的文件夹，带id可更新", httpMethod = "GET")
    @PostMapping("/newContentFolder")
    public Message newContentFolder(@RequestBody GcUserSaveFolder gcUserSaveFolder, HttpServletRequest request) {
        ApiAssert.notNull(gcUserSaveFolder.getName(), "文件夹名称不可空");
        gcUserSaveFolder.setUserId(this.getGcUser().getId());
        gcUserSaveFolder.setMasterId(getHeaderMasterId(request));
        GcUser user = this.getGcUser();

        try {
            if (gcUserSaveFolderService.saveOrUpdate(gcUserSaveFolder)) {
                userLicenseService.addPlaylistCount(gcUserSaveFolder, user.getId());
                return new Message().ok("保存成功").addData("folder", gcUserSaveFolder);
            } else {
                return new Message().ok("保存是吧");
            }
        } catch (LicenseLimitExceededException e) {
            return new Message().error(HttpStatus.FORBIDDEN.value(), e.getMessage());
        }
    }

    // 参数：videoId或subId，folderId，id-更新
    @ApiOperation(value = "保存课程/视频到一个文件夹", httpMethod = "GET")
    @PostMapping("/saveContentToFolder")
    public Message saveContentToFolder(
            @RequestBody GcUserSaveContent gcUserSaveContent, HttpServletRequest request) {
        ApiAssert.notNull(gcUserSaveContent.getFolderId(), "文件夹id不可空");
        if (gcUserSaveContent.getVideoId() != null && gcUserSaveContent.getSubId() != null)
            throw new SystemException("课程id及视频id不可同时存在，一次仅可保存其中一个");

        gcUserSaveContent.setUserId(this.getGcUser().getId());
        gcUserSaveContent.setMasterId(getHeaderMasterId(request));

        Integer count = gcUserSaveContentService.countSaveContent(gcUserSaveContent);
        if (count > 0)
            return new Message().error(101, I18NUtil.get("guidecore.user.saveContentRepeat"));

        if (gcUserSaveContentService.saveOrUpdate(gcUserSaveContent)) {
            return new Message().ok("保存成功").addData("content", gcUserSaveContent);
        } else {
            return new Message().error("保存失败");
        }
    }

    @ApiOperation(value = "获取单个文件夹的内容列表", httpMethod = "POST")
    @PostMapping("/getContentFromOneFolder")
    public Message getContentFromOneFolder(
            @RequestBody GcUserSaveFolder gcUserSaveFolder, HttpServletRequest request) {
        GcUser user = this.getGcUser();
        return gcMasterService.getContentFromOneFolder(
                gcUserSaveFolder, user, request, EnvType.GC.getCode());
    }

    @ApiOperation(value = "删除文件夹", httpMethod = "GET")
    @GetMapping("/deleteFolder")
    public Message deleteFolder(Integer folderId, HttpServletRequest request) {
        ApiAssert.notNull(folderId, "folderId不可空");
        GcUserSaveFolder gcUserSaveFolder = new GcUserSaveFolder();
        gcUserSaveFolder.setId(folderId);
        gcUserSaveFolder.setUserId(this.getGcUser().getId());
        if (gcUserSaveFolderService.countFolder(gcUserSaveFolder) == 0)
            return new Message().error("该用户folderId不存在记录");

        if (gcUserSaveFolderService.removeById(folderId)) return new Message().ok("删除成功");
        else return new Message().error("删除失败");
    }

    @ApiOperation(value = "删除保存内容", httpMethod = "GET")
    @GetMapping("/deleteSaveContent")
    public Message deleteSaveContent(Integer contentId, HttpServletRequest request) {
        ApiAssert.notNull(contentId, "folderId不可空");
        GcUserSaveContent gcUserSaveContent = new GcUserSaveContent();
        gcUserSaveContent.setId(contentId);
        gcUserSaveContent.setUserId(this.getGcUser().getId());
        if (gcUserSaveContentService.countSaveContent(gcUserSaveContent) == 0)
            return new Message().error("该用户contentId不存在记录");

        if (gcUserSaveContentService.removeById(contentId)) return new Message().ok("删除成功");
        else return new Message().error("删除失败");
    }
}
