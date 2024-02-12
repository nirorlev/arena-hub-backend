package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.jwt.JwtUtil;
import com.threeatom.constant.SysConstant;
import com.threeatom.guidecore.constant.LevelType;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcAccessMapper;
import com.threeatom.guidecore.mapper.GcManagerMapper;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcManagerService;
import com.threeatom.guidecore.service.GcMasterService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.utils.PasswordSecretUtil;
import java.util.HashMap;
import java.util.Map;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 商户管理员 服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-11
 */
@Service
@Transactional
public class GcManagerServiceImpl extends ServiceImpl<GcManagerMapper, GcManager>
        implements GcManagerService {

    private static final String CACHE_TAG = "GcManager";

    @Autowired private GcMasterService masterService;

    @Autowired private GcAccessService accessService;

    @Autowired GcUserAccessService userAccessService;
    @Autowired GcAccessMapper accessMapper;

    @Override
    @Cacheable(value = CACHE_TAG, key = "'entity:'+#p0")
    public GcManager getManagerByIdCache(Integer id) {
        // TODO Auto-generated method stub
        return this.getById(id);
    }

    @Override
    @Transactional
    public boolean createManager(
            Integer sysId, String email, String password, String fName, String lName, String code) {
        boolean flag;
        try {
            // TODO Auto-generated method stub

            GcManager manager = new GcManager();
            GcAccess gcAccess = new GcAccess();
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

            if (code != null && !code.equals("")) { // 注册码注册方式

                QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<GcAccess>();
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
                userAccessPermission.setSubPermission(userAccess.getAccess().getSubjectJson());
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

            flag = this.updateById(manager);
        } catch (DuplicateKeyException ex) {
            throw new SystemException(I18NUtil.get("guidecore.master.register.mulReg"));
        }

        return flag;
    }

    @Transactional
    @CacheEvict(value = CACHE_TAG, allEntries = true)
    public boolean saveOrUpdateManager(GcManager manager) {

        return this.saveOrUpdate(manager);
    }

    @Override
    public String loginGetToken(String email, String password) {
        // TODO Auto-generated method stub

        GcManager manager = this.getManagerByUsername(email);
        if (manager == null)
            throw new SystemException(I18NUtil.get("guidecore.master.login.usernameError"));
        String salt = manager.getSalt();
        String pwdHash = new SimpleHash("MD5", password, salt + SysConstant.PASS_SALT).toHex();
        if (pwdHash.equals(manager.getPassword())) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("role", "manager");
            map.put("client", "web");
            map.put("sysId", manager.getSysId().toString());
            return JwtUtil.createTokenByUser(manager.getId().toString(), map, manager.getPassword());
        } else {
            throw new SystemException(I18NUtil.get("guidecore.master.login.passError"));
        }
    }

    @Override
    public GcManager getManagerByUsername(String username) {
        // TODO Auto-generated method stub
        QueryWrapper<GcManager> queryWrapper = new QueryWrapper<GcManager>();
        queryWrapper.eq("username", username);
        return this.getOne(queryWrapper);
    }
}
