package com.threeatom.guidecore.constant;

import io.swagger.models.auth.In;

public enum TimeZoneType {

	GMT_plus_1(1,"GMT+1"),
	GMT_plus_2(2,"GMT+2"),
	GMT_plus_3(3,"GMT+3"),
	GMT_plus_4(4,"GMT+4"),
	GMT_plus_5(5,"GMT+5"),
	GMT_plus_6(6,"GMT+6"),
	GMT_plus_7(7,"GMT+7"),
	GMT_plus_8(8,"GMT+8"),
	GMT_plus_9(9,"GMT+9"),
	GMT_plus_10(10,"GMT+10"),
	GMT_plus_11(11,"GMT+11"),
	GMT_plus_12(12,"GMT+12"),
	GMT_minus_1(-1,"GMT-1"),
	GMT_minus_2(-2,"GMT-2"),
	GMT_minus_3(-3,"GMT-3"),
	GMT_minus_4(-4,"GMT-4"),
	GMT_minus_5(-5,"GMT-5"),
	GMT_minus_6(-6,"GMT-6"),
	GMT_minus_7(-7,"GMT-7"),
	GMT_minus_8(-8,"GMT-8"),
	GMT_minus_9(-9,"GMT-9"),
	GMT_minus_10(-10,"GMT-10"),
	GMT_minus_11(-11,"GMT-11"),
	GMT_minus_12(-12,"GMT-12"),
	GMT_minus_13(-13,"GMT-13"),
	GMT_minus_14(-14,"GMT-14");

	private int code;
	private String desc;

	TimeZoneType(int code, String desc){
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
	public static TimeZoneType getByCode(int code) {
        for (TimeZoneType enumType : values()) {
            if (enumType.getCode() == code) {  
                return enumType;  
            }  
        }  
        return null;  
    }

	public static Integer getByValue(String value) {
		for (TimeZoneType timezonevalue: TimeZoneType.values()) {
			if (timezonevalue.desc.equals(value)) {
				return timezonevalue.getCode();
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	
}
