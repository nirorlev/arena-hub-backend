package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.entity.UserAvatar;
import com.threeatom.guidecore.mapper.UserAvatarMapper;
import com.threeatom.guidecore.service.AwsS3StorageService;
import com.threeatom.guidecore.service.UserAvatarService;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAvatarServiceImpl extends ServiceImpl<UserAvatarMapper, UserAvatar> implements UserAvatarService {

    private static final String SIGNATURE_QUERY_PARAM = "signature";

    private final AwsS3StorageService awsS3StorageService;

    @Override
    public UserAvatar saveUserAvatar(String powtoonFileUrl, Integer userId, Integer masterId) {
        if (StringUtils.isNotBlank(powtoonFileUrl) && powtoonFileUrl.contains(SIGNATURE_QUERY_PARAM)) {
            String fileUrl = extractUrl(powtoonFileUrl);
            String key = awsS3StorageService.uploadFileToS3(fileUrl, userId, masterId);
            UserAvatar userAvatar = createUserAvatar(fileUrl, key, userId);
            save(userAvatar);

            return userAvatar;
        }

        Optional<UserAvatar> optionalUserAvatar = getByUserId(userId);
        if (optionalUserAvatar.isPresent()) {
            return optionalUserAvatar.get();
        }

        throw new SystemException("Cannot find user avatar for user with id: " + userId);
    }

    @Override
    public void updateUserAvatar(String powtoonProfileUrl, Integer userId, Integer masterId) {
        Optional<UserAvatar> optionalUserAvatar = getByUserId(userId);

        if (optionalUserAvatar.isEmpty()) {
            saveUserAvatar(powtoonProfileUrl, userId, masterId);
            return;
        }

        UserAvatar userAvatar = optionalUserAvatar.get();
        String fileUrl = extractUrl(powtoonProfileUrl);

        if (!userAvatar.getPowtoonFileUrl().equals(fileUrl)) {
            String key = awsS3StorageService.uploadFileToS3(fileUrl, userId, masterId);

            userAvatar.setFileS3Key(key);
            userAvatar.setPowtoonFileUrl(fileUrl);
            userAvatar.setUpdatedDate(OffsetDateTime.now());
            updateById(userAvatar);
        }
    }

    private Optional<UserAvatar> getByUserId(Integer userId) {
        QueryWrapper<UserAvatar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return Optional.ofNullable(getOne(queryWrapper));
    }

    private String extractUrl(String powtoonFileUrl) {
        return powtoonFileUrl.substring(0, powtoonFileUrl.indexOf(SIGNATURE_QUERY_PARAM) - 1);
    }

    private UserAvatar createUserAvatar(String powtoonFileUrl, String key, Integer userId) {
        UserAvatar userAvatar = new UserAvatar();
        userAvatar.setFileS3Key(key);
        userAvatar.setPowtoonFileUrl(powtoonFileUrl);
        userAvatar.setUserId(userId);
        userAvatar.setCreatedDate(OffsetDateTime.now());
        userAvatar.setUpdatedDate(OffsetDateTime.now());
        return userAvatar;
    }
}
