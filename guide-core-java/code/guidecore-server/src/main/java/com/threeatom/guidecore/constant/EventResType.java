package com.threeatom.guidecore.constant;

public class EventResType {
	/*
	 * 注意：！！
	 * GcUserEventResourceMapper 中也有引用，如果只是refactor rename字段需手动改mapper 
	 */
//	gc_user_event_resource: type:
//	0:文字，1:图片，2:视频，3:声音，4:文件，5:ScreenRock链接
	
	public static final int TEXT_0=EventUnifyType.TEXT_0; //文字
	public static final int IMAGE_1=EventUnifyType.IMAGE_1; //照片
	public static final int VIDEO_2=EventUnifyType.VIDEO_2; //视频
	public static final int AUDIO_3=EventUnifyType.AUDIO_3; //音频
	public static final int DOC_4=EventUnifyType.DOC_4; //文档
	//5留空，为保与gc_master_message的event_type一致，5留空
	public static final int SCREENROCK_6=EventUnifyType.SCREENROCK_6; //ScreenRock链接
	public static final String JSON_STR=EventUnifyType.JSON_STR012346;
	
}
