package com.threeatom.guidecore.controller.api.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.threeatom.guidecore.service.GvgMasterService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.constant.EventUnifyType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/guidecore/manager")
@RequiresRoles({"manager"})
@Api(tags = "管理平台资源管理功能")
public class ResourceManagerGuidecoreController extends GuideCoreController{
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceManagerGuidecoreController.class);

    @Autowired
    private SysFileService sysFileService;

    @Autowired
	private GvgMasterService gvgMasterService;
    
    @ApiOperation(value = "视频文件列表", httpMethod = "POST")
    @PostMapping("/videoFileList")
    public Message getVideoFileList(@RequestBody JSONObject jsonParams, HttpServletRequest request) {
		GcMaster master = this.getMaster();
		Integer masterId = request.getIntHeader("masterId");
		if (Objects.isNull(master)&&Objects.nonNull(masterId)){
			master = new GcMaster();
			master.setId(masterId);
		}
		PageInfo<SysFile> page=gvgMasterService.getSysFile(jsonParams,EventUnifyType.videoTypes,request,master,null);
    	return new Message().ok().addData("page", page);
    	
    }
    
    @ApiOperation(value = "资源文件列表", httpMethod = "POST")
    @PostMapping("/resFileList")
    public Message getResFileList(@RequestBody JSONObject jsonParams,HttpServletRequest request) {
		GcMaster master = this.getMaster();
		Integer masterId = request.getIntHeader("masterId");
		if (Objects.isNull(master)&&Objects.nonNull(masterId)){
			master = new GcMaster();
			master.setId(masterId);
		}
    	PageInfo<SysFile> page=gvgMasterService.getSysFile(jsonParams,EventUnifyType.resTypes,request,master,null);
    	return new Message().ok().addData("page", page);
    }
    
//    private PageInfo<SysFile> getSysFile(JSONObject jsonParams, List<Integer> typeIndexIds,HttpServletRequest request) {
//    	SysSystem system=this.getSystem();
//    	GcMaster master = this.getMaster();
//    	Integer pageSize=jsonParams.getInteger("pageSize");
//    	Integer pageNum=jsonParams.getInteger("pageNum");
//    	String searchTag=jsonParams.getString("searchTag");
//    	String searchString=jsonParams.getString("searchString");
//    	List<SysFile> list=sysFileService.getFiles(master.getId(), typeIndexIds, searchTag, pageNum, pageSize,searchString);
//    	PageInfo<SysFile> page=new PageInfo<SysFile>(list);
//    	return page;
//	}
    
    @ApiOperation(value = "资源文件标签列表", httpMethod = "GET")
    @GetMapping("/fileTag/{type}")
    public Message getAllFileTag(@PathVariable("type") String type) {
    	
//    	try {
    		SysSystem system=this.getSystem();
        	GcMaster master = this.getMaster();
        	
        	List<String> folders=new ArrayList<>();
        	if(type.equals("video")) {
        		folders=TableConstant.sysFile_video_folder_list;
        	}else if(type.equals("res")) {
        		folders=TableConstant.sysFile_res_folder_list;
        	}
        	
        	List<String> tags=sysFileService.getAllTag(master.getId(),system, folders);
        	return new Message().ok().addData("tags", tags);
//    	}catch (Exception e) {
//    		return new Message().error().addData("Exception", e.toString());
//		}
    	
    	
    }

}
