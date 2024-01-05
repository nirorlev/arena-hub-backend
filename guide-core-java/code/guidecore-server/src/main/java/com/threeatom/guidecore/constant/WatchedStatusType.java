package com.threeatom.guidecore.constant;

import java.util.ArrayList;
import java.util.List;

public enum WatchedStatusType {


	STARTED(0,"Started"),
	NOTSTARTED(1,"Not Started"),
	COMPLETE(2,"Complete"),
	CH(4,""),
	NOACCESS(3,"No Access");




	private int code;
	private String desc;


	WatchedStatusType(int code, String desc){
		this.code=code;
		this.desc=desc;
	}



	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	/***
	 * 根据CODE获取枚举实例
	 * @param code
	 * @return
	 */
	public static WatchedStatusType getByCode(int code) {
		for (WatchedStatusType enumType : values()) {
			if (enumType.getCode() == code) {
				return enumType;
			}
		}
		return null;
	}







	
	
	
	
}
