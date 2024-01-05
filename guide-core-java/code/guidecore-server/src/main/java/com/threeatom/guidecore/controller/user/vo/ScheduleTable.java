package com.threeatom.guidecore.controller.user.vo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;

public class ScheduleTable {
	
	@JSONField(format="yyyy-MM-dd")
	private Date date;
	
	private Set<Integer> vids;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Set<Integer> getVids() {
		return vids;
	}

	public void setVids(Set<Integer> vids) {
		this.vids = vids;
	}
	
	public Set<Integer> addVid(Integer vid){
		if(vids==null) {
			vids=new HashSet<Integer>();
		}
		vids.add(vid);
		return vids;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		
		return date.toString()+vids.size();
	}
	
	
	

}
