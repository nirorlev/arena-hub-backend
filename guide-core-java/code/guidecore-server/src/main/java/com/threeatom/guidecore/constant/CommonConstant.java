package com.threeatom.guidecore.constant;

import java.util.ArrayList;
import java.util.List;

public class CommonConstant {

	public static final int UNREAD_MESSAGE_TYPE_VIDEO_1=1;
	public static final int UNREAD_MESSAGE_TYPE_0=0;
	
	
	public static final List<String> defaultNoPortalName = new ArrayList<String>() {
        {
            this.add("user");//this 可以省略
            this.add("user5");
            this.add("newgt");
            this.add("api");
            this.add("apigc");
        }
    };
    
	public static final List<String> defaultNoCourseOrVideName = new ArrayList<String>() {
        {
            this.add("404");
        }
    };
}
