package com.threeatom.guidecore.controller.user.vo;



import lombok.Data;

import java.util.List;

@Data
public class MessageFIlterVo{


	
	public MessageFIlterVo() {
	}


	public MessageFIlterVo(Integer masterId, Integer thisUserId, Integer readState) {
		super();
		this.masterId = masterId;
		this.thisUserId = thisUserId;
		this.readState = readState;
	}
	
	
	public MessageFIlterVo(Integer masterId, Integer thisUserId, Integer readState, Integer sendUserId,
			Double lastDaysScope, Integer groupId,Integer fileTypeIndex) {
		super();
		this.masterId = masterId;
		this.thisUserId = thisUserId;
		this.readState = readState;
		this.sendUserId = sendUserId;
		this.lastDaysScope = lastDaysScope;
		this.groupId = groupId;
		this.fileTypeIndex = fileTypeIndex;
	}


	private static final long serialVersionUID = 1L;
	private Integer masterId;//门户id
	private Integer thisUserId;//当前用户
	private Integer readState;//0=未读，1=已读
	private Integer sendUserId;//发送消息的用户
	private Double lastDaysScope;
	private Integer groupId;//老师查看某个班级的消息
	private Integer fileTypeIndex;
	private String order;
	private List<String> subIdList;
	private List<String> sub0IdList;
	private Integer eventId;
          
}
