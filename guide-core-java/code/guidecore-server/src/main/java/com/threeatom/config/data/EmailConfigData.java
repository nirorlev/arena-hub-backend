package com.threeatom.config.data;

import lombok.Data;

@Data
public class EmailConfigData {
	
	private String regionId;
	
	private String accessKey;
	
	private String secret;
	
	private String accountName;

	private String templateName;

	private String url;

}
