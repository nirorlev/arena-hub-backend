package com.threeatom.guidecore.service;

import com.aliyuncs.exceptions.ClientException;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.entity.GcAccess;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserAccess;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-11
 */
public interface GcAccessService extends IService<GcAccess> {

    boolean addAccess(GcAccess access);

    List<GcAccess> findAccessListByMasterId(Integer masterId);

    List<GcAccess> getAdminAccessListByMasterId(Integer masterId);

    GcAccess getAccessByName(String codeName,Integer masterId);

    List<GcAccess> listAccess(String name, Integer masterId,Integer userId, HttpServletRequest request);

    List<GcAccess> getTeamAccessList(String name, Integer masterId,Integer userId, HttpServletRequest request);

    List<GcAccess> getTeamAccessSubjectNumList(String name, Integer masterId,Integer userId, List<Integer> subIds,HttpServletRequest request);

    List<GcAccess> getTeamAccessSubjectNumAdminList(String name, Integer masterId, Integer userId,List<Integer> availableTypeFour,List<Integer> availableTypeOneAndThree);

    List<GcAccess> listAllAccess(Map<String, Object> params, HttpServletRequest request);

    Integer deleteAccess(Integer id);

    Message checkUserAccess(Integer masterId, Integer userId, String accessCode, Integer inviteUserId, GcUser user, HttpServletRequest request) throws ClientException, IOException;

    GcAccess getAccessById(Integer id);

    List<GcAccess> getAccessBySubjectId(Integer masterId,Integer subjectId);

    List<GcAccess> getAccessByChannelId(Integer masterId,Integer subjectId);

    List<GcAccess> getAccessByAdminId(Integer adminId);

    List<GcAccess> getAccessListByAdminId(Integer adminId);

    Map<String, Long> getALlAccessCodeNumsByMasterId(Integer masterId);

    List<GcAccess> getContainsAccessList(String ptId,Integer masterId);

    List<GcAccess> getAccessByMasterIdAndCode(GcAccess access);

    List<GcAccess> getAllPackage(Integer masterId, PageParam pageParam,List<Integer> packageIdList,List<Integer> subscriptionIdList);

    GcAccess selectFreeCodeByMaster(Integer masterId);

    List<GcAccess> selectMasterIdAndIds(Integer masterId,List<Integer> ids);

    List<GcAccess> selectAccessByCodeAndMasterId(List<String> codeList,Integer masterId);

    void insertOrUpdateList(List<GcAccess> accessList);

    void insertOrUpdateChannel(List<GcAccess> accessList);

    List<GcAccess> selectAccessByIds(List<Integer> ids);

    List<GcAccess> selectAccessBySubId(Integer subId,Integer masterId);

    void deleteSubIdAccess(Integer masterId,Integer subId);

    List<GcAccess> getAllAccessByMasterId(Integer masterId);

    List<GcAccess> selectAccessLevel0(Integer masterId,Integer userId);
}
