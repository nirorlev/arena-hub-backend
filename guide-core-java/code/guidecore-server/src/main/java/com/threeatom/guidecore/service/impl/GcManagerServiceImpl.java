package com.threeatom.guidecore.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.jwt.JwtUtil;
import com.threeatom.constant.SysConstant;
import com.threeatom.guidecore.constant.LevelType;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcManager;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUserAccess;
import com.threeatom.guidecore.entity.GcUserAccessPermission;
import com.threeatom.guidecore.mapper.GcAccessMapper;
import com.threeatom.guidecore.mapper.GcManagerMapper;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcManagerService;
import com.threeatom.guidecore.service.GcMasterService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.OrgLicenseLimitationService;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.utils.PasswordSecretUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.mortbay.util.ajax.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GcManagerServiceImpl extends ServiceImpl<GcManagerMapper, GcManager> implements GcManagerService {

    private static final String CACHE_TAG = "GcManager";

    private final OrgLicenseLimitationService orgLicenseLimitationService;
    private final GcUserAccessService userAccessService;
    private final GcAccessMapper accessMapper;
    private final GcMasterService masterService;
    private final GcContentGroupCourseAssignmentService contentGroupCourseAssignmentService;

    @Override
    @Cacheable(value = CACHE_TAG, key = "'entity:'+#p0")
    public GcManager getManagerByIdCache(Integer id) {
        return this.getById(id);
    }

    @Override
    @Transactional
    public void createManager(
        Integer sysId, String email, String password, String fName, String lName, String code) {
        try {
            GcManager manager = new GcManager();
            manager.setSysId(sysId);
            manager.setFirstName(fName);
            manager.setLastName(lName);
            manager.setUsername(email);
            String salt = PasswordSecretUtil.createSalt();
            manager.setSalt(salt);
            manager.setState(1);
            String pwdHash = new SimpleHash("MD5", password, salt + SysConstant.PASS_SALT).toHex();
            manager.setPassword(pwdHash);
            this.save(manager);

            if (StringUtils.isNotBlank(code)) {

                QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("code", code);
                GcAccess accessCode = accessMapper.selectOne(queryWrapper);

                if (accessCode != null && !accessCode.equals("")) {
                    // 绑定门户
                    manager.setMasterId(accessCode.getMasterId());
                } else {
                    throw new SystemException(I18NUtil.get("guidecore.master.codeError"));
                }
                // 设置用户层级为门户用户
                manager.setLevel(LevelType.MASTER_MANAGER);
                // 将用户绑定注册码
                GcUserAccess userAccess = new GcUserAccess();
                userAccess.setMasterId(manager.getMasterId());
                userAccess.setManagerId(manager.getId());
                userAccess.setAccessId(accessCode.getId());
                userAccess.setAccess(accessCode);
                userAccessService.save(userAccess);
                // 复制课程权限
                GcUserAccessPermission userAccessPermission = new GcUserAccessPermission();
                userAccessPermission.setSubPermission(
                    JSONArray.parseArray(JSON.toString(contentGroupCourseAssignmentService.getCourseIdsByContentGroupId(accessCode.getId())))
                );
                userAccessPermission.setUserAccessId(userAccess.getId());
                userAccessService.saveUserAccessPermission(userAccessPermission);
                // 分配课程管理员角色

                // 为课程管理员角色初始化权限

            } else { // 无注册码注册方式
                // 创建新门户
                GcMaster master = new GcMaster();
                master.setState(1);
                masterService.save(master);
                manager.setMasterId(master.getId());
            }

            this.updateById(manager);

            orgLicenseLimitationService.save(manager.getMasterId());
        } catch (DuplicateKeyException ex) {
            throw new SystemException(I18NUtil.get("guidecore.master.register.mulReg"));
        }
    }

    @Transactional
    @CacheEvict(value = CACHE_TAG, allEntries = true)
    public boolean saveOrUpdateManager(GcManager manager) {

        return this.saveOrUpdate(manager);
    }

    @Override
    public String loginGetToken(String email, String password) {
        GcManager manager = this.getManagerByUsername(email);
        if (manager == null) {
            throw new SystemException(I18NUtil.get("guidecore.master.login.usernameError"));
        }

        String salt = manager.getSalt();
        String passwordHash = new SimpleHash("MD5", password, salt + SysConstant.PASS_SALT).toHex();
        if (passwordHash.equals(manager.getPassword())) {
            Map<String, String> map = new HashMap<>();
            map.put("role", "manager");
            map.put("client", "web");
            map.put("sysId", manager.getSysId().toString());
            map.put("masterId", String.valueOf(manager.getMasterId()));
            return JwtUtil.createTokenByUser(manager.getId().toString(), map, manager.getPassword());
        } else {
            throw new SystemException(I18NUtil.get("guidecore.master.login.passError"));
        }
    }

    @Override
    public GcManager getManagerByUsername(String username) {
        QueryWrapper<GcManager> queryWrapper = new QueryWrapper<GcManager>();
        queryWrapper.eq("username", username);
        return this.getOne(queryWrapper);
    }
}
