package com.threeatom.guidecore.controller.user.vo;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import com.threeatom.common.ApiAssert;

import lombok.Data;

@Data
public class PageParam implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer pageSize=0;
	private Integer pageNum=0;
	private String orderByString;
	private Integer lastDaysScope;
	public static String pageSizeStr= "pageSize";
	public static String pageNumStr = "pageNum";
	public static String orderByStr = "orderByString";
	public static String lastScopeStr = "lastDaysScope";
	
	 public PageParam(HttpServletRequest request) {
		 
		 if(request!=null) {
//			 两种形式
			 
			 //1 form-data
			 if(request.getParameter(pageSizeStr)!=null)pageSize=Integer.parseInt(request.getParameter(pageSizeStr));
			 if(request.getParameter(pageNumStr)!=null)pageNum=Integer.parseInt(request.getParameter(pageNumStr));
			 if(request.getParameter(orderByStr)!=null)orderByString=request.getParameter(orderByStr);
			 if(request.getParameter(lastScopeStr)!=null)lastDaysScope=Integer.parseInt(request.getParameter(lastScopeStr));
			 //2 头
			 if(request.getHeader(pageSizeStr)!=null)pageSize=Integer.parseInt(request.getHeader(pageSizeStr));
			 if(request.getHeader(pageNumStr)!=null)pageNum=Integer.parseInt(request.getHeader(pageNumStr));
			 if(request.getHeader(orderByStr)!=null)orderByString=request.getHeader(orderByStr);
			 if(request.getHeader(lastScopeStr)!=null)lastDaysScope=Integer.parseInt(request.getHeader(lastScopeStr));



		 }
		 
	}



}
