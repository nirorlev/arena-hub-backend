package com.threeatom.guidecore.service.impl;

import static com.threeatom.common.jwt.JwtUtil.createTokenByUser;

import com.alibaba.fastjson.JSONArray;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.client.PowtoonClient;
import com.threeatom.client.dto.PowtoonUserDto;
import com.threeatom.common.exception.SystemException;
import com.threeatom.constant.SysConstant;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.user.vo.PtGroupsVo;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserAccess;
import com.threeatom.guidecore.entity.GcUserInfo;
import com.threeatom.guidecore.entity.PtLoginConfig;
import com.threeatom.guidecore.mapper.GcUserMapper;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.GcUserInfoService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.service.PtChannelSubscribeService;
import com.threeatom.guidecore.service.UserLicenseService;
import com.threeatom.guidecore.util.AuthorizationUtil;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import com.threeatom.utils.PasswordSecretUtil;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcUserServiceImpl extends ServiceImpl<GcUserMapper, GcUser> implements GcUserService {

    private final GcUserAccessService userAccessService;
    private final SysFileService fileService;
    private final GcUserInfoService infoService;
    private final PowtoonClient powtoonClient;
    private final GcAccessService accessService;
    private final GcSubjectService gcSubjectService;
    private final PortalUserService portalUserService;
    private final UserLicenseService userLicenseService;

    @Override
    public GcUser getUserInfo(Integer userId) {
        GcUser user = this.baseMapper.selectById(userId);
        GcUserInfo info = infoService.getById(user.getInfoId());
        user.setInfo(info);

        if (info.getAvatarFileId() != null) {
            SysFile file = fileService.getById(info.getAvatarFileId());
            info.setAvatarFile(file);
        }

        return user;
    }

    @Override
    public GcUser getUserByIdCache(Integer id) {
        return this.baseMapper.getGcUserByUserId(id);
    }

    @Override
    public void checkGcUser(String username, String password) {
        GcUser user = getUserByUsername(username);
        String salt = user.getSalt();
        String passwordHash = new SimpleHash("MD5", password, salt + SysConstant.PASS_SALT).toHex();

        if (!passwordHash.equals(user.getPassword())) {
            throw new SystemException(101, I18NUtil.get("guidecore.master.login.passError"));
        }
    }


    @Override
    public GcUser getUserByUsername(String username) {
        QueryWrapper<GcUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        GcUser user = this.getOne(queryWrapper);
        if (user == null) {
            throw new SystemException(706, I18NUtil.get("guidecore.master.login.noUser"));
        }

        return user;
    }

    @Override
    public GcUser createGcUser(
        Integer sysId, String username, String password, String firstName, String lastName) {
        if (countUsers(sysId, username) > 0) {
            throw new SystemException(707, I18NUtil.get("user.login"));
        }

        GcUser user = createNewUser(sysId, username, password, firstName, lastName);
        this.save(user);
        return user;
    }

    private GcUser createNewUser(Integer sysId, String username, String password, String firstName, String lastName) {
        GcUser user = new GcUser();
        GcUserInfo userInfo = infoService.createNew(firstName, lastName);

        user.setInfo(userInfo);
        user.setInfoId(userInfo.getId());
        user.setSysId(sysId);
        user.setUsername(username);

        String salt = PasswordSecretUtil.createSalt();
        user.setSalt(salt);
        user.setState(1);
        String passwordHash = new SimpleHash("MD5", password, salt + SysConstant.PASS_SALT).toHex();
        user.setPassword(passwordHash);
        return user;
    }

    private int countUsers(Integer sysId, String username) {
        QueryWrapper<GcUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sys_id", sysId)
            .eq("username", username);

        return this.count(queryWrapper);
    }

    @Override
    public List<Integer> getTalkerIds(Integer userId, Integer masterId) {
        List<GcUserAccess> userContentGroups =
            userAccessService.selectPtUserAccessByMasterIdAndUserId(userId, masterId);

        if (!userContentGroups.isEmpty()) {
            return userAccessService.getUserAccessListUserIds(masterId, getUserContentGroupIds(userContentGroups));
        }
        return new ArrayList<>();
    }

    private List<Integer> getUserContentGroupIds(List<GcUserAccess> userContentGroups) {
        return userContentGroups.stream()
            .map(GcUserAccess::getId)
            .collect(Collectors.toList());
    }

    @Override
    public List<GcUser> getUserByUserAccessIds(List<Integer> userAccessIds) {
        return this.baseMapper.getUserByUserAccessIds(userAccessIds);
    }

    @Override
    public String generateJwtToken(GcUser user, Integer masterId) {
        Map<String, String> params = new HashMap<>();
        params.put("sysId", "2");
        params.put("role", "user");
        params.put("client", "native");
        params.put("masterId", masterId.toString());

        return createTokenByUser(user.getId().toString(), params, user.getPassword());
    }

    @Override
    public void verifyUsernameNotExists(String userName, String errorMessage) {
        QueryWrapper<GcUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userName);

        if (this.count(queryWrapper) > 0) {
            throw new SystemException(errorMessage);
        }
    }

    @Override
    public Map<Integer, GcUser> getWatchedUserNum(List<Integer> subjectIds, Integer masterId) {
        return this.baseMapper.getWatchedUserNum(subjectIds, masterId);
    }

    @Override
    public void deleteById(Integer id) {
        this.baseMapper.deleteById(id);
    }

    @Override
    public List<GcUser> getTeamUser(Map<String, Object> params, HttpServletRequest request) {
        return this.baseMapper.getTeamUser(params);
    }

    @Override
    public GcUser getCurrentUser(HttpServletRequest request) {
        if (AuthorizationUtil.isUser(request)) {
            return getUserByIdCache(AuthorizationUtil.getUserUid(request));
        }

        return null;
    }

    @Override
    public GcUser syncPowtoonUser(String accessToken, PtLoginConfig ptLoginConfig, Integer masterId)
        throws IOException, ClientException {
        final String bearerToken = "Bearer " + accessToken;
        PtGroupsVo groups =
            powtoonClient.getGroups(
                URI.create(ptLoginConfig.getPtRootUrl() + ptLoginConfig.getGroups()), bearerToken);
        log.info("PtGroups interface returns:" + groups);

        PowtoonUserDto powtoonUserInfo = powtoonClient.getUserInfo(URI.create(ptLoginConfig.getPtRootUrl()), bearerToken);
        GcUser user = getUserByUsername(powtoonUserInfo.getProfile().getEmail());

        List<Integer> courseIds = gcSubjectService.getCourseIds(masterId);
        GcAccess studentContentGroup = accessService.getStudentContentGroup(courseIds, masterId);

        user = saveOrUpdateUser(user, powtoonUserInfo, studentContentGroup, masterId);

        accessService.syncContentGroupsWithPowtoonGroups(powtoonUserInfo, groups, masterId, user.getId());
        portalUserService.saveOrUpdate(user.getId(), masterId, powtoonUserInfo.getPermissions().getOrg().getRoleId());

        return user;
    }

    private GcUser saveOrUpdateUser(GcUser user, PowtoonUserDto powtoonUserInfo, GcAccess studentAccess, Integer masterId)
        throws ClientException, IOException {
        if (null == user) {
            return createGcUser(powtoonUserInfo, studentAccess, masterId);
        }
        user.setPtUser(TableConstant.COMMON_ONE);
        updateById(user);

        GcUserInfo gcUserInfo = infoService.getById(user.getInfoId());
        gcUserInfo.setFirstName(powtoonUserInfo.getProfile().getFirstName());
        gcUserInfo.setLastName(powtoonUserInfo.getProfile().getLastName());

        if (null != gcUserInfo.getAvatarFileId()) {
            SysFile file = fileService.getById(gcUserInfo.getAvatarFileId());
            file.setFileUrl(powtoonUserInfo.getProfile().getThumbUrl());
            fileService.saveOrUpdate(file);
        } else {
            SysFile file = createAvatarFile(user.getId(), masterId, powtoonUserInfo.getProfile().getThumbUrl());
            fileService.saveOrUpdate(file);
            gcUserInfo.setAvatarFileId(file.getId());
        }

        infoService.updateById(gcUserInfo);
        return updateUser(powtoonUserInfo, getUserByIdCache(user.getId()));
    }

    private GcUser createGcUser(PowtoonUserDto userInfo, GcAccess studentAccess, Integer masterId)
        throws ClientException, IOException {
        GcUser user = createGcUser(2, userInfo.getProfile().getEmail(), get8UUID(),
            userInfo.getProfile().getFirstName(), userInfo.getProfile().getLastName());
        user.setInfo(infoService.getById(user.getInfoId()));
        accessService.checkUserAccess(masterId, user.getId(), studentAccess.getCode(), null, null, null);
        user.setPtUser(TableConstant.COMMON_ONE);
        user.setFirstName(userInfo.getProfile().getFirstName());
        user.setLastName(userInfo.getProfile().getLastName());

        SysFile file = createAvatarFile(user.getId(), userInfo.getProfile().getThumbUrl(), masterId);

        fileService.saveOrUpdate(file);
        user.getInfo().setAvatarFileId(file.getId());

        infoService.saveOrUpdate(user.getInfo());
        updateById(user);

        return user;
    }

    private SysFile createAvatarFile(Integer uploadUserId, String thumbUrl, Integer masterId) {
        SysFile file = new SysFile();
        file.setSysId(TableConstant.COMMON_TWO);
        file.setUploadUid(uploadUserId);
        file.setName(thumbUrl);
        file.setFolder(TableConstant.sysFile_folder_guidecoreImages);
        file.setFileType(TableConstant.sysFile_fileType_resLink);
        file.setFileTypeIndex(TableConstant.COMMON_ONE);
        file.setMasterId(masterId);
        file.setSaveType(TableConstant.COMMON_THREE);
        file.setFileRemark(new JSONArray());
        return file;
    }

    private SysFile createAvatarFile(Integer uploadUserId, Integer masterId, String thumbUrl) {
        SysFile file = createAvatarFile(uploadUserId, thumbUrl, masterId);
        file.setFileUrl(thumbUrl);
        return file;
    }

    private GcUser updateUser(PowtoonUserDto userInfo, GcUser user) {
        if (null != userInfo.getProfile().getThumbUrl()) {
            user.setThumbUrl(userInfo.getProfile().getThumbUrl());
        }
        if (null != userInfo.getProfile().getEmail()) {
            user.setPtEmail(userInfo.getProfile().getEmail());
        }
        if (null != userInfo.getProfile().getId()) {
            user.setPtId(userInfo.getProfile().getId().toString());
        }

        return user;
    }

    public String get8UUID() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0];
    }
}
