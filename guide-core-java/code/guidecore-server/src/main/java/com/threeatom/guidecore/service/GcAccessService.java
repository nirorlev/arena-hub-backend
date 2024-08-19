package com.threeatom.guidecore.service;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUser;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

public interface GcAccessService extends IService<GcAccess> {

    boolean addAccess(GcAccess access);

    List<GcAccess> findAccessListByMasterId(Integer masterId);

    List<GcAccess> getAdminAccessListByMasterId(Integer masterId);

    GcAccess getAccessByName(String codeName, Integer masterId);

    List<GcAccess> listAccess(String name, Integer masterId, Integer userId);

    List<GcAccess> getTeamAccessList(
            String name, Integer masterId, Integer userId, HttpServletRequest request);

    List<GcAccess> getTeamAccessSubjectNumList(
            String name,
            Integer masterId,
            Integer userId,
            List<Integer> subIds,
            HttpServletRequest request);

    List<GcAccess> getTeamAccessSubjectNumAdminList(
            String name,
            Integer masterId,
            Integer userId,
            List<Integer> availableTypeFour,
            List<Integer> availableTypeOneAndThree,
            List<Integer> subIds);

    @Transactional
    GcAccess getTeacherStudentContentGroup(List<Integer> courseIds, Integer masterId);

    List<GcAccess> listAllAccess(Map<String, Object> params, HttpServletRequest request);

    Integer deleteAccess(Integer id);

    Message checkUserAccess(
            Integer masterId,
            Integer userId,
            String accessCode,
            Integer inviteUserId,
            GcUser user,
            HttpServletRequest request)
            throws ClientException, IOException;

    GcAccess getAccessById(Integer id);

    List<GcAccess> getAccessBySubjectId(Integer masterId, Integer subjectId);

    List<GcAccess> getAccessByChannelId(Integer masterId, Integer channelId);

    List<GcAccess> getAccessByAdminId(Integer adminId);

    List<GcAccess> getAccessListByAdminId(Integer adminId);

    Map<String, Long> getALlAccessCodeNumsByMasterId(Integer masterId);

    List<GcAccess> getAccessByMasterIdAndCode(GcAccess access);

    List<GcAccess> getAllPackage(
            Integer masterId,
            PageParam pageParam,
            List<Integer> packageIdList,
            List<Integer> subscriptionIdList);

    GcAccess selectFreeCodeByMaster(Integer masterId);

    List<GcAccess> selectMasterIdAndIds(Integer masterId, List<Integer> ids);

    List<GcAccess> selectAccessByCodeAndMasterId(List<String> codeList, Integer masterId);

    void insertOrUpdateList(List<GcAccess> accessList);

    List<GcAccess> selectAccessByIds(List<Integer> ids);

    List<GcAccess> selectAccessBySubId(Integer subId, Integer masterId);

    void deleteSubIdAccess(Integer masterId, Integer subId);

    List<GcAccess> getAllAccessByMasterId(Integer masterId);

    List<GcAccess> selectAccessLevel0(Integer masterId, Integer userId);
}
