package com.threeatom.guidecore.constant;

public class MessageEventType {
	/*
	 * 注意：！！
	 * GcMasterMessageMapper 中也有引用，如果只是refactor rename字段需手动改mapper 
	 * 
	 
	gc_master_message: event_type:
	0:文字 1:图片  2:视频  3:声音  4文件   5问题回答
	0，1，2，3，4时，res_id不为空，关联gc_user_event_resource表
	5时，user_answer_id不为空，关联gc_user_answer表
	
	 */
	
	public static final int TEXT_0=EventUnifyType.TEXT_0; //文字
	public static final int IMAGE_1=EventUnifyType.IMAGE_1; //照片
	public static final int VIDEO_2=EventUnifyType.VIDEO_2; //视频
	public static final int AUDIO_3=EventUnifyType.AUDIO_3; //音频
	public static final int DOC_4=EventUnifyType.DOC_4; //文档
	public static final int QUESTION_5=EventUnifyType.QUESTION_5; //问题回答消息
	//5留空，为保与gc_user_event_resource的type一致
	public static final int SCREENROCK_6=EventUnifyType.SCREENROCK_6; //ScreenRock链接
	
	public static final String JSON_STR=EventUnifyType.JSON_STR012345;
	//除5外
	public static final String  notQuestion = EventUnifyType.notQuestion012346; 
	
}
