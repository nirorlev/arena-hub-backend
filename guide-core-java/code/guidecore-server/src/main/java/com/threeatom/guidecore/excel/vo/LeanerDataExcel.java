package com.threeatom.guidecore.excel.vo;

import java.math.BigDecimal;

import com.alibaba.excel.annotation.ExcelProperty;

public class LeanerDataExcel {


	@ExcelProperty(value="FirstName")
	private String firstName;
	@ExcelProperty(value="LastName")
	private String lastName;
	@ExcelProperty(value="WatchVideo")
	private long videoNum;
	@ExcelProperty(value="UploadVideo")
	private long uploadResNum;
	@ExcelProperty(value="Message")
	private long messageNum;
	@ExcelProperty(value="PlayingTime")
	private BigDecimal videoPlayTime;
	@ExcelProperty(value="LastActive")
	private String lastLogInDate;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public long getVideoNum() {
		return videoNum;
	}
	public void setVideoNum(long videoNum) {
		this.videoNum = videoNum;
	}
	public long getUploadResNum() {
		return uploadResNum;
	}
	public void setUploadResNum(long uploadResNum) {
		this.uploadResNum = uploadResNum;
	}
	public long getMessageNum() {
		return messageNum;
	}
	public void setMessageNum(long messageNum) {
		this.messageNum = messageNum;
	}
	
	
	public BigDecimal getVideoPlayTime() {
		return videoPlayTime;
	}
	public void setVideoPlayTime(BigDecimal videoPlayTime) {
		this.videoPlayTime = videoPlayTime;
	}
	public String getLastLogInDate() {
		return lastLogInDate;
	}
	public void setLastLogInDate(String lastLogInDate) {
		this.lastLogInDate = lastLogInDate;
	}
	
	
	
	
}
