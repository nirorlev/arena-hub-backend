package com.threeatom.guidecore.constant;

import java.util.ArrayList;
import java.util.List;

public class TableConstant {

    // 1. 表：sys_file
    // folder字段
    public static final String sysFile_folder_guidecoreImages = "guidecore/images"; //
    public static final String sysFile_folder_guidecoreRes = "guidecore/res"; //
    public static final String sysFile_folder_guidecoreUserEvent = "guidecore/user/event"; //
    public static final String sysFile_folder_guidecoreVedio = "guidecore/vedio"; //
    public static final String sysFile_folder_guidecoreVedioCaption =
            "guidecore/vedio/caption"; // 字幕文件
    public static final String sysFile_folder_guidecoreAudio = "guidecore/audio"; // 音频文件
    public static final String sysFile_folder_guidecoreDocument = "guidecore/document"; // 文档文件
    public static final String sysFile_folder_guidecoreSchedule = "guidecore/schedule"; // 计划中上传的文件

    public static final String sysFile_folder_linkVideo = "link/video"; // 视频类链接
    public static final String sysFile_folder_linkVideoTencent = "link/video/tencent"; // 视频类链接-腾讯
    public static final String sysFile_folder_linkVideoScreenrock =
            "link/video/screenrock"; // 视频类链接-screenrock
    public static final String sysFile_folder_linkRes = "link/res"; // 资源文件类链接
    public static final String sysFile_folder_linkEvent = "link/event"; // 视频事件关联链接

    public static final String screenrock_folder = "screenrock";

    public static final List<String> sysFile_video_folder_list =
            new ArrayList<String>() {
                {
                    this.add(sysFile_folder_guidecoreVedio);
                    this.add(sysFile_folder_linkVideo);
                    this.add(sysFile_folder_linkVideoTencent);
                    this.add(sysFile_folder_linkVideoScreenrock);
                    this.add(sysFile_folder_linkEvent);
                }
            };
    public static final List<String> sysFile_res_folder_list =
            new ArrayList<String>() {
                {
                    this.add(sysFile_folder_guidecoreRes);
                    this.add(sysFile_folder_linkRes);
                }
            };
    public static final List<String> sysFile_folder_link_list =
            new ArrayList<String>() {
                {
                    this.add(sysFile_folder_linkVideo); // this 可以省略
                    this.add(sysFile_folder_linkVideoTencent);
                    this.add(sysFile_folder_linkVideoScreenrock);
                    this.add(sysFile_folder_linkRes);
                }
            };

    // save_type字段，文件储存方式\n1 disk\n2 \n3
    public static final int sysFile_saveType_disk_1 = 1; // 本地存储，一般没用到
    public static final int sysFile_saveType_aliOSS_2 = 2; // 阿里云oss存储
    public static final int sysFile_saveType_link_3 = 3; // 链接形式 - sql备注未加
    public static final int sysFile_saveType_youtubeLink_6 = 6; // 链接形式 - sql备注未加
    public static final String sysFile_if_caption_1 = "1";
    public static final String sysFile_if_caption_0 = "0";
    // file_type文件类型字段
    public static final String sysFile_fileType_videoLink = "video/link"; // 视频类链接类型
    public static final String sysFile_fileType_imageLink = "image/link"; // 图片类链接类型
    public static final String sysFile_fileType_resLink = "resource/link"; // 资源文件类链接类型
    public static final String sysFile_fileType_eventLink = "event/link"; // 视频事件关联链接
    // youtube
    public static final int youtubeFileId = 11;
    public static final int youtueFileTypeIndex = 13;
    // vimeo
    public static final int vimeoFileTypeIndex = 14;

    public static final List<String> sysFile_fileType_list =
            new ArrayList<String>() {
                {
                    this.add(sysFile_fileType_videoLink); // this 可以省略
                    this.add(sysFile_fileType_imageLink);
                    this.add(sysFile_fileType_resLink);
                    this.add(sysFile_fileType_eventLink);
                }
            };
    // user_role, upload_uid的角色，1=门户，2=用户
    public static final int sysFile_userRole_portal1 = 1;
    public static final int sysFile_userRole_user2 = 2;

    // 分账，百分之十
    public static final long stripe10 = 10l;

    public static final Integer master_message_type = 10;

    // monday
    public static final Integer monday_join_type_paid = 108;
    public static final Integer monday_join_type_code = 0;
    public static final Integer monday_payment_type_paid = 4;
    public static final Integer monday_payment_type_code = 6;
    // monday字段枚举
    public static final String Candidate = "Candidate";
    public static final String Candidate_Email_Address = "Candidate Email Address";
    public static final String Portal = "Portal";
    public static final String Payment = "Payment";
    public static final String CodePackage = "Code/Package";
    public static final String Invoice = "Invoice";

    // 2. 表：gc_master_home_info 门户首页信息
    public static final String gcMasterHomeInfo_name_content1 = "content1";
    public static final String gcMasterHomeInfo_name_content2 = "content2";
    public static final String gcMasterHomeInfo_name_img1 = "img1";
    public static final String gcMasterHomeInfo_name_title1 = "title1";
    public static final String gcMasterHomeInfo_name_title2 = "title2";
    public static final String gcMasterHomeInfo_name_studentWelcomeVideo = "studentWelcomeVideo";
    public static final String gcMasterHomeInfo_name_studentIntroVideo = "studentIntroVideo";
    public static final String gcMasterHomeInfo_name_mentorWelcomeVideo = "mentorWelcomeVideo";
    public static final String gcMasterHomeInfo_name_mentorIntroVideo = "mentorIntroVideo";
    public static final String gcMasterHomeInfo_name_pageTitle = "pageTitle";
    public static final String gcMasterHomeInfo_name_pageSubText = "pageSubText";
    public static final String gcMasterHomeInfo_name_pageImages = "pageImages";
    public static final String gcMasterHomeInfo_name_courseTitle = "courseTitle";
    public static final String gcMasterHomeInfo_name_channelTitle = "channelTitle";
    public static final String gcMasterHomeInfo_name_channelIds = "channelIds";
    public static final String gcMasterHomeInfo_name_homepageShareTitle = "homepageShareTitle";
    public static final String gcMasterHomeInfo_name_homepageShareDesc = "homepageShareDesc";
    public static final String gcMasterHomeInfo_name_homepageShareImg = "homepageShareImg";
    public static final String gcMasterHomeInfo_name_coursePageShareTitle = "coursePageShareTitle";
    public static final String gcMasterHomeInfo_name_coursePageShareDesc = "coursePageShareDesc";
    public static final String gcMasterHomeInfo_name_coursePageShareImg = "coursePageShareImg";
    public static final String gcMasterHomeInfo_name_playListPageShareTitle =
            "playListPageShareTitle";
    public static final String gcMasterHomeInfo_name_playListPageShareDesc = "playListPageShareDesc";
    public static final String gcMasterHomeInfo_name_playListPageShareImg = "playListPageShareImg";
    public static final String gcMasterHomeInfo_name_channelPageShareTitle = "channelPageShareTitle";
    public static final String gcMasterHomeInfo_name_channelPageShareDesc = "channelPageShareDesc";
    public static final String gcMasterHomeInfo_name_channelPageShareImg = "channelPageShareImg";
    public static final String gcMasterHomeInfo_name_masterCrawlerSwitch = "masterCrawlerSwitch";
    public static final List<String> gcMasterHomeInfo_name_page =
            new ArrayList<String>() {
                {
                    this.add(gcMasterHomeInfo_name_pageTitle);
                    this.add(gcMasterHomeInfo_name_pageSubText);
                    this.add(gcMasterHomeInfo_name_pageImages);
                    this.add(gcMasterHomeInfo_name_courseTitle);
                    this.add(gcMasterHomeInfo_name_channelTitle);
                    this.add(gcMasterHomeInfo_name_channelIds);
                    this.add(gcMasterHomeInfo_name_homepageShareTitle);
                    this.add(gcMasterHomeInfo_name_homepageShareImg);
                    this.add(gcMasterHomeInfo_name_homepageShareDesc);
                    this.add(gcMasterHomeInfo_name_coursePageShareTitle);
                    this.add(gcMasterHomeInfo_name_coursePageShareDesc);
                    this.add(gcMasterHomeInfo_name_coursePageShareImg);
                    this.add(gcMasterHomeInfo_name_playListPageShareTitle);
                    this.add(gcMasterHomeInfo_name_playListPageShareDesc);
                    this.add(gcMasterHomeInfo_name_playListPageShareImg);
                    this.add(gcMasterHomeInfo_name_channelPageShareTitle);
                    this.add(gcMasterHomeInfo_name_channelPageShareDesc);
                    this.add(gcMasterHomeInfo_name_channelPageShareImg);
                    this.add(gcMasterHomeInfo_name_masterCrawlerSwitch);
                }
            };

    public static final List<String> gcMasterHomeInfo_name_homepage_list =
            new ArrayList<String>() {
                {
                    this.add(gcMasterHomeInfo_name_content1);
                    this.add(gcMasterHomeInfo_name_content2);
                    this.add(gcMasterHomeInfo_name_img1);
                    this.add(gcMasterHomeInfo_name_title1);
                    this.add(gcMasterHomeInfo_name_title2);
                }
            };

    public static final List<String> gcMasterHomeInfo_name_welcomeVideo_list =
            new ArrayList<String>() {
                {
                    this.add(gcMasterHomeInfo_name_studentWelcomeVideo);
                    this.add(gcMasterHomeInfo_name_studentIntroVideo);
                    this.add(gcMasterHomeInfo_name_mentorWelcomeVideo);
                    this.add(gcMasterHomeInfo_name_mentorIntroVideo);
                }
            };
    public static final List<String> gcMasterHomeInfo_name_welcomeVideo_stuList =
            new ArrayList<String>() {
                {
                    this.add(gcMasterHomeInfo_name_studentWelcomeVideo);
                    this.add(gcMasterHomeInfo_name_studentIntroVideo);
                }
            };
    public static final List<String> gcMasterHomeInfo_name_welcomeVideo_mentList =
            new ArrayList<String>() {
                {
                    this.add(gcMasterHomeInfo_name_mentorWelcomeVideo);
                    this.add(gcMasterHomeInfo_name_mentorIntroVideo);
                }
            };

    // channel类型枚举
    public static final Integer Completely_public = 1;
    public static final Integer All_public = 1;
    public static final Integer Certain_teams = 2;
    public static final Integer Specific_person = 3;
    public static final Integer Private = 0;

    public static final String gcSubjectIntroInfo_name_content1 = "content1";
    public static final String gcSubjectIntroInfo_name_content2 = "content2";
    public static final String gcSubjectIntroInfo_name_img1 = "img1";
    public static final String gcSubjectIntroInfo_name_title1 = "title1";
    public static final String gcSubjectIntroInfo_name_title2 = "title2";
    public static final String gcSubjectIntroInfo_name_subjectIntroVideo = "subjectIntroVideo";
    public static final List<String> gcSubjectIntroInfo_name_Introduction_page_list =
            new ArrayList<String>() {
                {
                    this.add(gcSubjectIntroInfo_name_content1);
                    this.add(gcSubjectIntroInfo_name_content2);
                    this.add(gcSubjectIntroInfo_name_img1);
                    this.add(gcSubjectIntroInfo_name_title1);
                    this.add(gcSubjectIntroInfo_name_title2);
                    this.add(gcSubjectIntroInfo_name_subjectIntroVideo);
                }
            };
    // GcEvent,Gc_Event
    public static final int gcEvent_eventType_choice1 = 1;
    public static final int gcEvent_eventType_freeType2 = 2;

    public static final int gcMasterHomeInfo_type_2 = 2; // 图片
    public static final int gcMasterHomeInfo_type_3 = 3; // 视频

    // gcSubject
    public static final int gcSubject_state_hidden_0 = 0; // state：隐藏
    public static final int gcSubject_state_visible_1 = 1; // state：可见
    public static final String gcSubject_state_jsonStr = "[0,1]"; // state

    public static final int gcSubject_isPublic_1 = 1; // 1=公共课程

    public static final int gcSubject_type_subject0 = 0; // 父级主题课程
    public static final int gcSubject_type_topic1 = 1; // 子级话题课程

    // 关联课程的关联关系，1=导入import，2=别名alias
    public static final int gcSubjectAssociation_relationType_1import = 1;

    public static final int gcUserVideoAction_type_like1 = 1;
    public static final int gcUserVideoAction_type_rate2 = 2;
    public static final int gcUserVideoAction_type_star3 = 3; // 视频星级评价
    public static final int gcUserVideoAction_type_star4 = 4; // 课程星级评价

    public static final String gcUserVideoAction_value_rate2_1 = "1"; // It was very useful
    public static final String gcUserVideoAction_value_rate2_2 = "2"; // It was fine
    public static final String gcUserVideoAction_value_rate2_3 = "3"; // It needs improving
    public static final String gcUserVideoAction_value_regexp = "^[1|2|3]$";

    // gc_video
    public static final int gcVideoVideoSource_1local = 1;
    public static final int gcVideoVideoSource_2Tecent = 2;
    public static final int gcVideoVideoSource_3ScreenRock = 3;

    // GcMasterMessage	gc_master_message
    public static final int gcMasterMessage_readState_0Unread = 0;
    public static final int gcMasterMessage_readState_1read = 1;
    public static final String gcMasterMessage_readState_0and1 = "[0,1]";

    // GcUserVideoPlay	gc_user_video_play
    public static final int gcUserVideoPlay_playState_done1 = 1;
    public static final int gcUserVideoPlay_playState_undone0 = 0;

    // 课程完成状态
    public static final short SUBJECT_COMPLETE_STATUS0 = 0; // 灰色 无记录
    public static final short SUBJECT_COMPLETE_STATUS1 = 1; // 黄色 课程或问题有个没完成或都没完成
    public static final short SUBJECT_COMPLETE_STATUS2 = 2; // 绿色 课程视频完成，问题也回答完成

    // 视频完成状态
    public static final short VIDEO_COMPLETE_STATUS0 = 0; // 灰色 无记录
    public static final short VIDEO_COMPLETE_STATUS1 = 1; // 黄色 课程或问题有个没完成或都没完成
    public static final short VIDEO_COMPLETE_STATUS2 = 2; // 绿色 课程视频完成，问题也回答完成

    // 视频观看状态
    public static final int VIDEO_PLAY_STATUS0 = 0; // 视频观看过 绿色
    public static final int VIDEO_PLAY_STATUS1 = 1; // 视频未观看过 灰色

    // mondayApi返回状态码
    public static final int MONDAY_RESPONSE_CODE = 401;

    public static final int SUBJECT_ERROR_CODE = 450;

    // 课程完成进度
    public static final int SUBJECT_COMPLETE_PERCENT0 = 100;

    public static final int COMMON_ZERO = 0; //

    public static final int COMMON_ONE = 1; //

    public static final int COMMON_TWO = 2; //

    public static final int COMMON_FOUR = 4; //

    public static final int COMMON_FIVE = 5; //

    public static final int COMMON_THREE = 3; //

    public static final long LONG_ZERO = 0L; //

    public static final double DOUBLE_ZERO = 0.0D; //

    public static final String VIDEO_SEARCH_RETURN_TYPE1 = "1"; //

    public static final String VIDEO_SEARCH_RETURN_TYPE2 = "2"; //

    public static final String VIDEO_SEARCH_RETURN_TYPE3 = "3"; //

    public static final String VIDEO_SEARCH_RETURN_TYPE4 = "4"; //

    public static final String CHANNEL_SEARCH_RETURN_TYPE5 = "5"; //

    public static final String PLAYLIST_SEARCH_RETURN_TYPE6 = "6"; //

    public static final String RESULTS_SEARCH_RETURN_TYPE7 = "7"; //

    // GcMasterActive，gc_master_active
    public static final int gcMasterActive_type_master0 = 0; // 解锁全站
    public static final int gcMasterActive_type_subject1 = 1; // 解锁一级或二级课程
    public static final int gcMasterActive_type_video2 = 2; // 解锁视频

    public static final int gcUserSchedule_level_subject1 = 1; // 1级课程
    public static final int gcUserSchedule_level_topic2 = 2; // 2级话题
    public static final int gcUserSchedule_level_video11 = 11; // 11级视频

    public static final int stripepaysplit = 57;

    public static final int stripeFunds = 100;

    // 近期时间范围
    public static final int recentDays14 = 14;
    public static final int recentDays7 = 7;

    // 星级
    public static final Double starValue0 = 0.0;
    // 评星人数
    public static final Long starUsers = 0l;

    // 默认code
    public static final String GVG_CODE = "";

    public static final String FILEID_REGEX = ".*FileId.*";
}
