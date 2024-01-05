package com.threeatom.guidecore.controller.api;


import com.alibaba.druid.sql.visitor.functions.Char;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.constant.*;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.controller.user.vo.MondayApiVo;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.service.*;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysFileCaption;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.*;
import com.threeatom.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/guidecore/callback")
@Api(tags="其他第三方网站的数据回调")
public class CallbackGuideCoreController extends GuideCoreController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CallbackGuideCoreController.class);
	@Autowired
	private GcVideoService videoService;
	@Autowired
	private SysFileService fileService;

	@Autowired
	private GcMasterService masterService;

	@Autowired
	private GcUserEventResourceService userEventResourceService;

	@Autowired
	private SysSystemService systemService;
	@Autowired
	private Environment env;


	/*@Autowired
	private GcExternalMessageService externalMessageService;*/




	@PostMapping("/aliyunOssCallback")
	public Message aliyunOssCallback(@RequestBody JSONObject jsonObject) {

		LOGGER.info("aliyunOssCallback: "+jsonObject.toJSONString());

		Integer uploadType=jsonObject.getInteger("uploadType");

		//Case 1: 门户批量上传视频
		//及用户端上传
		//uploadPolicyFile
		if(uploadType!=null&&uploadType.equals(UploadType.portalBulk_1)) {//值参考FileGuideCoreController中uploadType
			//添加到数据库中
			SysFile fileEntity=videoService.unifiedFileSave(jsonObject);
			fileService.getResFullUrlSaveType2(fileEntity);
			return new Message().ok().addData("file", fileEntity);
		}

		//Case 2: 门户首页视频上传
		//guidecore/file/uploadPolicyMasterVideo
		if(uploadType!=null&&uploadType.equals(UploadType.portalFrontPageVideo_2)) {
			GcMaster gcMaster=videoService.callbackSaveMasterVideo(jsonObject);
			//设置视频完整路径
			String sysIds = env.getProperty("systemId");
			int sysId = Integer.parseInt(sysIds);
			SysSystem sys = systemService.getSystemById(sysId);
			gcMaster.getIntroVideoFile().setFullFileUrl(fileService.getResFullUrlSaveType2(gcMaster.getIntroVideoFile()));

			masterService.updateSourceNull(gcMaster.getId());//source_type：null或1:本地intro_video_id不为空，2:youku，3:screenRock，2和3时intro_video_id为空，source_url不为空
			return new Message().ok().addData("gcMaster", gcMaster);
		}

		//Case 3: 作业本回复答案的文件上传
		if(uploadType!=null&&uploadType.equals(UploadType.workbookUplodFile_3)) {
			GcUserEventResource userEventResource= userEventResourceService.uploadEventResourceFile(jsonObject);
			return new Message().ok().addData("userEventResource", userEventResource);
		}

		//Case 默认: 门户课程视频上传：
		//guidecore/file/uploadPolicyVideo
		GcVideo video=videoService.callbackSaveVideo(jsonObject);
		return new Message().ok().addData("video", video).addData("ossCallback", jsonObject);
	}




}
