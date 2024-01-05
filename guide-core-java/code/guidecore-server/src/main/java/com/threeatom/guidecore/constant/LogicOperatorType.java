package com.threeatom.guidecore.constant;

import java.util.ArrayList;
import java.util.List;

public enum LogicOperatorType {



	EQ(1,"eq"),
	NE(2,"ne"),
	LIKE(3,"like"),
	NOTLIKE(4,"notLike"),
	WATCHED(5,"like"),
	AUTHORIZED (6,"apply"),
	STARTED(7,"已开始的课程/话题"),
	COMPLETED(8,"已完成的课程/话题"),
	IN(9,"in");

	private int code;
	private String desc;

	public static final List<Integer> integerTypes = new ArrayList<Integer>() {
		{
			this.add(AUTHORIZED.code);
			this.add(STARTED.code);
			this.add(COMPLETED.code);
			this.add(IN.code);
		}
	};

	LogicOperatorType(int code, String desc){
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
	public static LogicOperatorType getByCode(int code) {
        for (LogicOperatorType enumType : values()) {
            if (enumType.getCode() == code) {  
                return enumType;  
            }  
        }  
        return null;  
    }
	
	
	/***
	 * 判断code值是否都是enum中的code
	 * @param list
	 * @return
	 */
	public static boolean isValidEnum(List<Integer> list) {
		for(Integer val:list) {
			if(!isValidEnum(val)) return false;
		}
		return true;
	}
	public static boolean isValidEnum(Integer code) {
		for (LogicOperatorType enumType : values()) {
            if (enumType.getCode() == code) {  
                return true;  
            }  
        } 
		return false;
	}
	
	
	
	
	
	
	
	
}
