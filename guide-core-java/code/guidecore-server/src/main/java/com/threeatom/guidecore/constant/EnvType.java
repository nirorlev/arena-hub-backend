package com.threeatom.guidecore.constant;

import java.util.List;

public enum EnvType {

	GVG(1,"GVG"),
	GC(2,"GC"),
	PT(3,"PT");

	private int code;
	private String desc;

	EnvType(int code, String desc){
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
	public static EnvType getByCode(int code) {
        for (EnvType enumType : values()) {
            if (enumType.getCode() == code) {  
                return enumType;  
            }  
        }  
        return null;  
    }
	
	
	
	
	
	
	
	
}
