package com.threeatom.guidecore.controller.api;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.util.DateUtils;
import com.threeatom.common.aws.entity.ResultVO;
import com.threeatom.common.aws.entity.TemporaryCertVO;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.config.AwsS3Configuration;
import com.threeatom.config.AwsUploadSignUrlConfiguration;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.utils.FileUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jets3t.service.CloudFrontService;
import org.jets3t.service.CloudFrontServiceException;
import org.jets3t.service.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.constant.ObjectStorageConstants;
import com.threeatom.guidecore.constant.EventUnifyType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.constant.UploadType;
import com.threeatom.guidecore.constant.VideoConstant;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.entity.GcManager;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;

import io.swagger.annotations.Api;
import org.springframework.web.util.UriUtils;


@RestController
@RequestMapping("/api/v1/guidecore/file")
@Api(tags="文件上传")
public class FileGuideCoreController extends GuideCoreController{

	private static final Logger LOGGER = LoggerFactory.getLogger(FileGuideCoreController.class);
	@Autowired
	private SysFileService fileService;

	@Autowired
	private AwsS3Configuration awsS3Configuration;

	@Autowired
	private RedisOperator redisOperator;

	@Autowired
	private AwsUploadSignUrlConfiguration awsUploadSignUrlConfiguration;

	@PostMapping("/uploadImg")
	public Message uploadImg(MultipartFile img,HttpServletRequest request) {
		GcManager manager= this.getManager();
		SysSystem sys=this.getSystem();
		SysFile file=fileService.saveSysImg(manager.getId(),sys, TableConstant.sysFile_folder_guidecoreImages, ObjectStorageConstants.ALIYUN_OSS,img);
		
		fileService.getResFullUrl(file,request);

		return new Message().ok().addData("fileImg", file);
	}
	@PostMapping("/uploadEventImg")
	public Message uploadEventImg(MultipartFile img,HttpServletRequest request) {
		GcManager manager= this.getManager();
		SysSystem sys=this.getSystem();
		SysFile file=fileService.saveSysImg(manager.getId(),sys, "guidecore/event/images", ObjectStorageConstants.ALIYUN_OSS,img);
		
		fileService.getResFullUrl(file, request);

		return new Message().ok().addData("fileImg", file);
	}
	@PostMapping("/uploadVedio")
	public Message uploadVedio(MultipartFile vedio,HttpServletRequest request) {
		GcManager manager= this.getManager();
		SysSystem sys=this.getSystem();
		SysFile file=fileService.saveVedio(manager.getId(), sys, TableConstant.sysFile_folder_guidecoreVedio, ObjectStorageConstants.ALIYUN_OSS, vedio);
		
		fileService.getResFullUrl(file, request);
		
		return new Message().ok().addData("fileVedio", file);
	}
	@PostMapping("/uploadRes")
	public Message uploadRes(MultipartFile res,HttpServletRequest request) {
		GcManager manager= this.getManager();
		SysSystem sys=this.getSystem();

		SysFile file=fileService.saveRes(manager.getId(), sys, TableConstant.sysFile_folder_guidecoreRes, ObjectStorageConstants.ALIYUN_OSS, res);
		fileService.getResFullUrl(file, request);
		return new Message().ok().addData("fileRes", file);
	}
	@GetMapping("/getFile/{id}")
	public Message getFile(@PathVariable("id") Integer id,HttpServletRequest request) {
		if(id==null||id.equals(0)) throw new SystemException(I18NUtil.get("id.illegal"));
		SysSystem sys=this.getSystem();
		SysFile file=fileService.getById(id);
		fileService.getResFullUrl(file, request);
		return new Message().ok().addData("file", file);
	}

	//课程上传
	@PostMapping("/uploadPolicyVideo")
	public Message uploadPolicyVideo(@RequestBody JSONObject jsonObject) {
		GcManager manager= this.getManager();
		SysSystem sys=this.getSystem();
		
		String originFileName=jsonObject.getString("originFileName");
		String videoName=jsonObject.getString("videoName");
		
		Integer subId=jsonObject.getInteger("subId");
		Integer videoSource=jsonObject.getInteger("videoSource");
		Integer id=jsonObject.getInteger("id");
//		String  ifCaption=jsonObject.getString("ifCaption");


		Map<String,Object> extParams=new HashMap<>();
		extParams.put("videoName", videoName);
		if(StringUtils.isBlank(jsonObject.getString("videoDesc"))!=true){
			extParams.put("videoDesc", jsonObject.getString("videoDesc"));
		}
		
		if(jsonObject.getInteger("videoLong")!=null){
			extParams.put("videoLong", jsonObject.getString("videoLong"));
		}

//		extParams.put("ifCaption",ifCaption);
		extParams.put("subId", subId);
		extParams.put("videoSource", videoSource);
		extParams.put("id", id);
		JSONObject policyStr=fileService.saveFilePolicy(manager.getId(), sys, TableConstant.sysFile_folder_guidecoreVedio, ObjectStorageConstants.ALIYUN_OSS, originFileName,extParams);
		
		return new Message().ok().addData("policy", policyStr);
	}

	//门户视频上传
	@PostMapping("/uploadPolicyMasterVideo")
	public Message uploadPolicyMasterVideo(@RequestBody JSONObject jsonObject) {
		GcManager manager= this.getManager();
		SysSystem sys=this.getSystem();
		
		String originFileName=jsonObject.getString("originFileName");
		 GcMaster master = this.getMaster();
		
		Map<String,Object> extParams=new HashMap<>();
		extParams.put("masterId", master.getId());
		extParams.put("uploadType", UploadType.portalFrontPageVideo_2);
		
		JSONObject policyStr=fileService.saveFilePolicy(manager.getId(), sys, TableConstant.sysFile_folder_guidecoreVedio, ObjectStorageConstants.ALIYUN_OSS, originFileName,extParams);
		
		return new Message().ok().addData("policy", policyStr);
	}
	
	//批量上传
	//用户端上传
	@PostMapping("/uploadPolicyFile")
	public Message uploadPolicyFile(@RequestBody JSONObject jsonParams,HttpServletRequest request) {
		Integer uploaderId=null;
//		GcManager manager=this.getManager();
		SysSystem sys=this.getSystem();
		Integer masterId = null;
		GcMaster master = this.getMaster();
		
		if(master==null) {
			masterId = getHeaderMasterId(request);
			if(masterId==null)throw new SystemException(I18NUtil.get("student.site.error"));
		}else {
			masterId=master.getId();
		}
		
		String originFileName=jsonParams.getString("originFileName");
		String tag=jsonParams.getString("tag");
		String type=jsonParams.getString("type");
		
		Map<String,Object> extParams=new HashMap<>();
		
		String folder="";
		if(type==null) {//学生端
			folder=jsonParams.getString("folder");
			extParams.put("fileTypeIndex", jsonParams.getString("fileTypeIndex"));
			uploaderId=this.getGcUser().getId();
		}else if(type.equals("video")) {
			folder=TableConstant.sysFile_folder_guidecoreVedio;
			extParams.put("fileTypeIndex", EventUnifyType.VIDEO_2);
			uploaderId=this.getManager().getId();
		}else if(type.equals("res")) {
			folder=TableConstant.sysFile_folder_guidecoreRes;
			extParams.put("fileTypeIndex", EventUnifyType.RES_FILE);
			uploaderId=this.getManager().getId();
		}else if(type.equals("audio")){
			folder=TableConstant.sysFile_folder_guidecoreAudio;
			extParams.put("fileTypeIndex",EventUnifyType.AUDIO_3);
			uploaderId=this.getManager().getId();
		}else if(type.equals("document")){
			folder=TableConstant.sysFile_folder_guidecoreDocument;
			extParams.put("fileTypeIndex",EventUnifyType.DOC_4);
			uploaderId=this.getManager().getId();
		}else if(type.equals("photo")){
			folder=TableConstant.sysFile_folder_guidecoreImages;
			extParams.put("fileTypeIndex",EventUnifyType.IMAGE_1);
			//门户端用户端新增给webm视频添加缩略图
			if(Objects.nonNull(this.getManager())) {
				uploaderId = this.getManager().getId();
			}else {
				uploaderId = this.getGcUser().getId();
			}
		}
		
		if(StringUtils.isEmpty(folder)) throw new SystemException(I18NUtil.get("resource.type.error"));
		
		if(jsonParams.getInteger("videoLong")!=null){
			extParams.put("videoLong", jsonParams.getInteger("videoLong"));
		}
		
		if(jsonParams.getString("ifFragment")!=null){
			extParams.put("ifFragment", jsonParams.getString("ifFragment"));
		}
		
		
		extParams.put("uploadType", UploadType.portalBulk_1);//回调时判断用
		if(StringUtils.isNotBlank(tag)) {
			extParams.put("tag", tag);
		}
		
		extParams.put("masterId", masterId);
		extParams.put("thumbNailId",jsonParams.get("thumbNailId"));
		
		JSONObject policyStr=fileService.saveFilePolicy(uploaderId, sys, folder, ObjectStorageConstants.ALIYUN_OSS, originFileName,extParams);


		
		return new Message().ok().addData("policy", policyStr);
	}
	
	
	//需优化
	@PostMapping("/uploadPolicyFileFromUser")
	public Message uploadPolicyFileFromUser(@RequestBody JSONObject jsonParams,HttpServletRequest request) {
		GcUser user=this.getGcUser();
		SysSystem sys=this.getSystem();
		Integer masterId = Integer.parseInt(request.getHeader("masterId"));
//		GcMaster master = this.getMaster();
		
		String originFileName=jsonParams.getString("originFileName");
//		String tag=jsonParams.getString("tag");
		String type=jsonParams.getString("type");
		
		Map<String,Object> extParams=new HashMap<>();
		String folder="";
		folder=TableConstant.sysFile_folder_guidecoreUserEvent;
		extParams.put("fileTypeIndex", type);
		
		if(StringUtils.isEmpty(folder)) throw new SystemException(I18NUtil.get("resource.type.error"));
		
		
		extParams.put("uploadType", UploadType.portalBulk_1);//回调时判断用
		
		extParams.put("masterId", masterId);
		extParams.put("userRole", TableConstant.sysFile_userRole_user2);
		
		
		JSONObject policyStr=fileService.saveFilePolicy(user.getId(), sys, folder, ObjectStorageConstants.ALIYUN_OSS, originFileName,extParams);
		
		return new Message().ok().addData("policy", policyStr);
	}

//	禁用s3通用方式上传，用cloudfront 上传 /api/v1/guidecore/file/awsUploadSignUrl
//	@GetMapping("/getUploadCert")
//	@ResponseBody
	public ResultVO getUploadCert() {
		try {
			AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
					.withCredentials(new DefaultAWSCredentialsProviderChain())
					.withRegion(awsS3Configuration.getRegion())
					.build();

			AssumeRoleRequest roleRequest = new AssumeRoleRequest()
					.withRoleArn(awsS3Configuration.getRoleArn())
					.withRoleSessionName(awsS3Configuration.getRoleSessionName())
					.withDurationSeconds(awsS3Configuration.getDurationSeconds());
			AssumeRoleResult roleResponse = stsClient.assumeRole(roleRequest);
			Credentials sessionCredentials = roleResponse.getCredentials();

			TemporaryCertVO certVO = new TemporaryCertVO();
			certVO.setRegion(awsS3Configuration.getRegion());
			certVO.setBucketName(awsS3Configuration.getBucketName());
			certVO.setAccessKey(sessionCredentials.getAccessKeyId());
			certVO.setSecretKey(sessionCredentials.getSecretAccessKey());
			certVO.setSessionToken(sessionCredentials.getSessionToken());
			certVO.setExpiration(sessionCredentials.getExpiration());
			return ResultVO.success(certVO);

		}
		catch(AmazonServiceException e) {
			throw new SystemException(I18NUtil.get("guidecore.aws.error")+e.getMessage());
//			e.printStackTrace();
		}
		catch(SdkClientException e) {
			throw new SystemException(I18NUtil.get("guidecore.aws.error")+e.getMessage());
//			e.printStackTrace();
		}
//		return ResultVO.fail();
	}

	//
	/*
	 * ID:    338344d9-5658-4931-9206-3d5cf5c2cd33
	 * Nme:   powtoon_guide_signed_links_key_group
	 * https://us-east-1.console.aws.amazon.com/cloudfront/v3/home?region=eu-west-2#/keygrouplist/details/338344d9-5658-4931-9206-3d5cf5c2cd33
	 * Containing Public Key ID: K1JQYEVI2UZJ98
	 */
//	@GetMapping("/SignUrlTest")
//	public void SignUrlTest() {
//		SignerUtils.Protocol protocol = SignerUtils.Protocol.https;
//		String distributionDomain = "stage.store.demoguide.xyz";
//		File privateKeyFile = new File("E:\\privateKey\\powtoon_guide_signed_links_private_key.der");
//		String s3ObjectKey = "a/b/hai.jpg";
//		s3ObjectKey = s3ObjectKey.replace(" ", "+");
//		s3ObjectKey = UriUtils.encodePath(s3ObjectKey, StandardCharsets.UTF_8);
//
//		//K1JQYEVI2UZJ98
//		//338344d9-5658-4931-9206-3d5cf5c2cd33
//		String keyPairId = "K1JQYEVI2UZJ98";
//		Date dateLessThan = DateUtils.parseISO8601Date("2022-07-20T22:20:00.000Z");
//		Date dateGreaterThan = DateUtils.parseISO8601Date("2022-07-10T22:20:00.000Z");
//		String ipRange = "0.0.0.0/0";
//		//0.0.0.0/0
//		try {
//			String url1 = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
//					protocol, distributionDomain, privateKeyFile,
//					s3ObjectKey, keyPairId, dateLessThan);
//			String url2 = CloudFrontUrlSigner.getSignedURLWithCustomPolicy(
//					protocol, distributionDomain, privateKeyFile,
//					s3ObjectKey, keyPairId, dateLessThan,
//					dateGreaterThan, ipRange);
//			System.out.println("u1:"+url1);
//			System.out.println("u2:"+url2);
//		} catch (InvalidKeySpecException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}



	/**
	 * aws上传文件
	 * @return
	 */
	@SneakyThrows
	@PostMapping("/awsUploadSignUrl")
	public Message awsUploadSignUrl(@RequestBody JSONObject jsonParams,HttpServletRequest request){
		String S3ObjectKey = jsonParams.getString("S3ObjectKey");
		//1.加载Hash和签名算法类
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		byte[] derPrivateKey = null;
		String signedUrl = null;
		try{
		if (null==redisOperator.get("awsPrivateKey")){
			String keyName = awsUploadSignUrlConfiguration.getPrivateKeyFilePath();
			ClassPathResource classPathResource = new ClassPathResource("privatekey/"+keyName);
			//解析秘钥
			ClassLoader classLoader = getClass().getClassLoader();
			URL url = classLoader.getResource(classPathResource.getPath());
			if (url == null) {
				throw new IllegalArgumentException("cannot find url: "+ "privatekey/"+keyName);
			}
			InputStream in = new FileInputStream(url.getFile());
			byte[] data = FileUtil.toByteArray(in);
			in.close();
			derPrivateKey = data;
			//保存秘钥
			String byteToString = Base64.getEncoder().encodeToString(derPrivateKey);
			redisOperator.set("awsPrivateKey",byteToString);
		}else {
			derPrivateKey = Base64.getDecoder().decode((String) redisOperator.get("awsPrivateKey"));
		}
		String param_UrlToBeSigned = "https://" + awsUploadSignUrlConfiguration.getDistributionDomain() + "/" + S3ObjectKey;

		Date param_DateLessThan = ServiceUtils.parseIso8601Date("2123-07-15T22:20:00.000Z");

		String policy = CloudFrontService.buildPolicyForSignedUrl(
						param_UrlToBeSigned,
						param_DateLessThan,
						awsUploadSignUrlConfiguration.getLimitToIpAddressCIDR(),
						null
				);
		signedUrl = CloudFrontService.signUrl(param_UrlToBeSigned, awsUploadSignUrlConfiguration.getKeyPairId(), derPrivateKey, policy);
		}catch (Exception e){
			String msg = e.getMessage();
			throw new SystemException(I18NUtil.get("powtoon.awsUploadSignUrl.error")+msg);
		}
		return new Message().ok().addData("signedUrl",signedUrl);
	}


//	@SneakyThrows
//	@GetMapping("/test1")
//	public void test1() throws IOException, ParseException, CloudFrontServiceException {
//		//1.加载Hash和签名算法类
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//
//		// ================================================================
//		// ================2.签名相关参数====================================
//		// ================================================================
//
//		//2.1.CloudFront为发布点分配的域名或者用户自己的域名
//		//String param_DistributionDomain = "你自己的域名或cloudfront发布点的域名";
//		String param_DistributionDomain = "stage.store.demoguide.xyz";
//
//		//2.2.将转化成"＊.der"格式的私钥文件
//		//String param_PrivateKeyFilePath = "你本地保存的*.der格式的cloudfront密钥对私钥文件路径全名";
//		String param_PrivateKeyFilePath = "E:\\privateKey\\powtoon_guide_signed_links_private_key.der";
//
//		//2.3.S3存储桶中文件的访问Key值
//		//String param_S3ObjectKey = "需要被访问的S3存储桶内文件访问key值";
//		String param_S3ObjectKey = "a/b/hpppp.jpg";
//
//		//2.4.CloudFront密钥对对应的访问KEY值
//		//String param_KeyPairId = "你使用根账号创建的CloudFront密钥对Key值";
//		String param_KeyPairId = "K1JQYEVI2UZJ98";
//
//		//2.5.待签名的URL
//		/*String param_UrlToBeSigned = "http://或者https://"
//				+ param_DistributionDomain
//				+ "/"
//				+ param_S3ObjectKey;*/
//
//	     String param_UrlToBeSigned = "https://"
//	                                + param_DistributionDomain
//	                                + "/"
//	                                + param_S3ObjectKey;
//
//
//		//3.加载私钥文件内容
//		byte[] derPrivateKey =
//				ServiceUtils.readInputStreamToBytes(
//						new FileInputStream(param_PrivateKeyFilePath));
//
//		// ================================================================
//		// ================4.定制策略相关参数================================
//		// ================================================================
//
//		//4.1.权限策略生效的路径，可以使用"*"和"?"来实现批量匹配,
//		//具体协议(http/https)需要和CloudFront发布点设置对应
//		/*String param_PolicyResourcePath = "http://或者https://"
//				+ param_DistributionDomain
//				+ "/"
//				+ param_S3ObjectKey;*/
//	      String param_PolicyResourcePath = "https://"
//	                                      + param_DistributionDomain
//	                                      + "/"
//	                                      + param_S3ObjectKey;
//
//		//4.2.签名URL失效时间
//		//Date param_DateLessThan = ServiceUtils.parseIso8601Date("UTC格式的签名URL失效时间");
//		Date param_DateLessThan = ServiceUtils.parseIso8601Date("2022-07-15T10:00:00.000Z");
//
//		//4.3.请求客户端的Ip地址范围CIDR设置（可选参数）
//		//String param_limitToIpAddressCIDR = "CIDR格式的请求源IP地址范围";
//		String param_limitToIpAddressCIDR = "0.0.0.0/0";
//
//		//4.4.签名URL生效时间（可选参数，不输入立即生效）
//		//Date param_DateGreaterThan = ServiceUtils.parseIso8601Date("UTC格式的签名URL生效时间");
//		Date param_DateGreaterThan = ServiceUtils.parseIso8601Date("2022-07-10T06:31:56.000Z");
//
//		//5.根据输入参数创建定制策略
//		String policy =
//				CloudFrontService.buildPolicyForSignedUrl(
//						param_PolicyResourcePath,
//						param_DateLessThan,
//						param_limitToIpAddressCIDR,
//						param_DateGreaterThan
//				);
//
//		System.out.println("［INFO］实际构造的的定制策略内容是【" + policy + "】");
//
//		//6.执行实际签名操作（哈希＋签名＋Base64编码）
//		String signedUrl =
//				CloudFrontService.signUrl(
//						param_UrlToBeSigned,
//						param_KeyPairId,
//						derPrivateKey,
//						policy
//				);
//
//		System.out.println("［INFO］输出的签名URL内容【" + signedUrl + "】");
//
//		//下面是输出内容的例子,当你产生类似下列输出后，可以直接输入到浏览器或提供给移动客户端中下载S3存储桶中的内容。
//		//［INFO］实际构造的的定制策略内容是【{"Statement": [{"Resource":"http://dqlbgmeivj213.cloudfront.net/example.txt","Condition":{"DateLessThan":{"AWS:EpochTime":1447539600},"IpAddress":{"AWS:SourceIp":"0.0.0.0/0"},"DateGreaterThan":{"AWS:EpochTime":1429165916}}}]}】
//		//［INFO］输出的签名URL内容【http://dqlbgmeivj213.cloudfront.net/example.txt?Policy=eyJTdGF0ZW1lbnQiOiBbeyJSZXNvdXJjZSI6Imh0dHA6Ly9kcWxiZ21laXZqMjEzLmNsb3VkZnJvbnQubmV0L2V4YW1wbGUudHh0IiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNDQ3NTM5NjAwfSwiSXBBZGRyZXNzIjp7IkFXUzpTb3VyY2VJcCI6IjAuMC4wLjAvMCJ9LCJEYXRlR3JlYXRlclRoYW4iOnsiQVdTOkVwb2NoVGltZSI6MTQyOTE2NTkxNn19fV19&Signature=qWjDSIaiYmgA-0ptW4DXXhjDtAzSYBtl-5yxvd25xokhR2lutBIfkIvbfISomMPAtCbH90Q1H9GGiegR1LP7lKx7lmYKqX40nAljvi12lrpKwftX4qrIBkJB3XL1XMBVEkgRnW0xEZh6qRFkNpWIS48FnnQvNGJt9C8j3IB-k1Pk8OaitssNpMf~C-nbmmd485pbUJpNf8SLwSv51OHxZeI5yj8z~u8OQa4rjRM6eBGkjzf1lTdKegi1HQRsmNn7-tgydmA3Hv6EY4-tIanHmV8o~pZ1mdoKlnlQoYlg~L-DaUiZkald8dplQNk3YXermcLsXq3q71Mw94ygrG03fw__&Key-Pair-Id=APKAIVAT4VOBHNXXXXXX】
//
//	}
	/**
	 * 签名URL
	 *
	 * @param distributionDomain 域名
	 * @param privateKeyFile     私钥文件
	 * @param s3ObjectKey        s3key
	 * @param keyPairId          密钥id
	 * @param dateLessThan       小于日期
	 * @param dateGreaterThan    大于日期
	 * @return 签名url
	 *
	https://blog.csdn.net/fxtxz2/article/details/119108474
	https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/CFPrivateDistJavaDevelopment.html
	 */
	public static String signUrl(String distributionDomain, File privateKeyFile, String s3ObjectKey, String keyPairId, Date dateLessThan, Date dateGreaterThan) {
		SignerUtils.Protocol protocol = SignerUtils.Protocol.https;
		// 中文URL处理，在s3中空格需要替换成+
		s3ObjectKey = s3ObjectKey.replace(" ", "+");
		s3ObjectKey = UriUtils.encodePath(s3ObjectKey, StandardCharsets.UTF_8);

		String ipRange = "0.0.0.0/0";

		try {
			return CloudFrontUrlSigner.getSignedURLWithCustomPolicy(
					protocol, distributionDomain, privateKeyFile,
					s3ObjectKey, keyPairId, dateLessThan,
					dateGreaterThan, ipRange);
		} catch (InvalidKeySpecException | IOException e) {
			LOGGER.error("签名url异常：", e);
		}
		return "";
	}
	
}
