package com.threeatom.guidecore.constant;

import java.util.List;

public enum FieldType {



	FIRSTNAME("firstName","ui.first_name"),
	LASTNAME("lastName","ui.last_name"),
	EMAIL("email","u.username"),
	SUBID("subId","v.sub_id"),
	VIDEONAME ("videoName","v.video_name"),
	CODEID("codeId","ac.id");

	private String fieldName;
	private String desc;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	FieldType(String fieldName, String desc) {
		this.fieldName = fieldName;
		this.desc = desc;
	}


	public static FieldType getByCode(String fieldName) {
        for (FieldType enumType : values()) {
            if (enumType.getFieldName().equals(fieldName)) {
                return enumType;  
            }  
        }  
        return null;  
    }

	
	
	
	
	
	
	
	
}
