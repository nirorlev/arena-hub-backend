package com.threeatom.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.threeatom.guidecore.util.I18NUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.threeatom.common.config.OssConfigData;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.oss.AliyunOssService;
import com.threeatom.common.oss.impl.AliyunOssServiceImpl;
import com.threeatom.common.yml.YamlPropertySourceFactory;

@Configuration
@PropertySource(value="classpath:system.yml",factory=YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "oss")
public class OssConfiguration {
	
	private List<OssConfigData> ossConfigList;
	
	
	public List<OssConfigData> getOssConfigList() {
		return ossConfigList;
	}
	
	public void setOssConfigList(List<OssConfigData> ossConfigList) {
		this.ossConfigList = ossConfigList;
	}
	

	private AliyunOssService ossServiceFactory(OssConfigData configData) {
		if(configData.getProviderName().equals("aliyun")) 
		switch(configData.getProviderName()) {
			case "aliyun":
				return new AliyunOssServiceImpl(configData);
				
		}
		return null;
		
	}
	@Bean("ossServiceMap")
	public Map<String,AliyunOssService> ossServiceMap(){
		/*if(ossConfigList==null||ossConfigList.size()<1) throw new SystemException(I18NUtil.get("Configuration.exception"));
		Map<String,AliyunOssService> map=new HashMap<String, AliyunOssService>();
		for(OssConfigData data:ossConfigList) {
			map.put(data.getBusinessName(), this.ossServiceFactory(data));
		}*/
		Map<String,AliyunOssService> map=new HashMap<String, AliyunOssService>();
		return map;
	}
	
	
	
	

}
