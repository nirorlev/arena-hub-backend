package com.threeatom.guidecore.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.PowtoonExternalVideo;
import com.threeatom.guidecore.mapper.PowtoonExternalVideoMapper;
import com.threeatom.guidecore.service.PowtoonExternalVideoService;
import com.threeatom.system.entity.SysFile;

import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @title: PowtoonExternalVideoServiceImpl
 */
@Service
public class PowtoonExternalVideoServiceImpl extends ServiceImpl<PowtoonExternalVideoMapper, PowtoonExternalVideo>
	implements PowtoonExternalVideoService {

	@Override
	public PowtoonExternalVideo getBySysFileId(Integer sysFileId) {
		QueryWrapper<PowtoonExternalVideo> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("sys_file_id", sysFileId);
		return getOne(queryWrapper);
	}

	@Override
	public PowtoonExternalVideo createExternalVideoForSysFile(SysFile sysFile) {
        Integer sysFileId = sysFile.getId();
        JSONObject source = sysFile.getSource();
        if (source == null) return null;
        if (getBySysFileId(sysFileId) != null) return null;

        String externalId = source.getString("id");
        String origin = source.getString("origin");
        String version = source.getString("version");
        String publicToken = source.getString("publicToken");
		PowtoonExternalVideo externalVideo = new PowtoonExternalVideo();
		externalVideo.setSysFileId(sysFileId);
		externalVideo.setExternalId(externalId);
		externalVideo.setOrigin(origin);
		externalVideo.setVersion(version);
		externalVideo.setPublicToken(publicToken);
		save(externalVideo);
		return externalVideo;
    }
}
