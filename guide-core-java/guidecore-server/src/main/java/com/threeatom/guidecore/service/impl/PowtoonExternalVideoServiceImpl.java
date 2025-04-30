package com.threeatom.guidecore.service.impl;

import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.PowtoonExternalVideo;
import com.threeatom.guidecore.mapper.PowtoonExternalVideoMapper;
import com.threeatom.guidecore.service.PowtoonExternalVideoService;
import com.threeatom.system.entity.SysFile;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PowtoonExternalVideoServiceImpl extends ServiceImpl<PowtoonExternalVideoMapper, PowtoonExternalVideo>
	implements PowtoonExternalVideoService {

	private void createNewExternalVideo (Integer sysFileId, String origin, String externalId, String version, String publicToken) {
		PowtoonExternalVideo externalVideo = new PowtoonExternalVideo();
		externalVideo.setSysFileId(sysFileId);
		externalVideo.setExternalId(externalId);
		externalVideo.setOrigin(origin);
		externalVideo.setVersion(version);
		externalVideo.setPublicToken(publicToken);
		save(externalVideo);
	}

	@Override
	public PowtoonExternalVideo getBySysFileId(Integer sysFileId) {
		QueryWrapper<PowtoonExternalVideo> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("sys_file_id", sysFileId);
		return getOne(queryWrapper);
	}

	@Override
	public List<PowtoonExternalVideo> getByExternalId(String externalId) {
		QueryWrapper<PowtoonExternalVideo> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("external_id", externalId);
		return this.baseMapper.selectList(queryWrapper);
	}

	@Override
	@Transactional
	public void createExternalVideoForSysFile(SysFile sysFile) {
		Integer sysFileId = sysFile.getId();
		JSONObject source = sysFile.getSource();
		if (source == null || getBySysFileId(sysFileId) != null) {
			return;
		}

		String externalId = source.getString("id");
		String origin = source.getString("origin");
		String version = source.getString("version");
		String publicToken = source.getString("publicToken");
		createNewExternalVideo(sysFileId, origin, externalId, version, publicToken);
    }
}
