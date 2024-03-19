package com.threeatom.guidecore.controller.api.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.Message;
import com.threeatom.common.redis.RedisOperator;

import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.controller.api.manager.NewUiGcVideoController;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PtLoginConfig;
import com.threeatom.guidecore.service.PtLoginConfigService;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.utils.HttpUtil;
import io.swagger.annotations.ApiOperation;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.shiro.web.filter.mgt.DefaultFilter.user;


@RestController
@RequestMapping("/api/v1/guidecore/user/youtube")
public class YoutubeGuideCoreController extends GuideCoreController{

	private static final Logger log = LoggerFactory.getLogger(NewUiGcVideoController.class);

	public YoutubeGuideCoreController() throws IOException {
	}

	@Value("${youtubeApiKey}")
	private String youtubeApiKey;

	@Value("${videoUrl}")
	private String getVideoDetails;

	@Autowired
	private RedisOperator redisOperator;

	@Autowired
	private PtLoginConfigService ptLoginConfigService;

	@Autowired
	private Environment env;

	@ApiOperation(value = "统一下单，并组装所需支付参数")
	@PostMapping("/getYoutubeUrl")
	public Message getYoutubeVideos(@RequestBody JSONObject params) throws IOException {

			Message message = new Message();
			List<Map<String,Object>> youtubeList = new ArrayList<>();

			String url = params.getString("url");
			String listId = new String();
			//合辑
			if (url.contains("list") ) {
				int index = url.indexOf("list=");
				String cutUrl = url.substring(index,url.length());
				int index0 = cutUrl.indexOf("list=");
				int index1 = cutUrl.indexOf("&");
				if(index1<0){
					listId = cutUrl.substring(index0,cutUrl.length()).substring("list=".length());
				}else {
					listId = cutUrl.substring(index0, index1).substring("list=".length());
				}
			} else if (!url.contains("list=")) {
				Map<String,Object> map = new HashMap<>();
				List<Map<String,Object>> vidList = new ArrayList<>();
				//分享链接
				if(!url.contains("v=")){
					int index = url.indexOf("youtu.be/");
					String vid = url.substring(index,url.length()).substring("youtu.be/".length());
					String playUrl = "https://www.youtube.com/embed/" + vid;
					String videoTimeUrl = "https://youtube.googleapis.com/youtube/v3/videos?part=id&part=contentDetails&part=snippet"+"&id="+vid+"&key="+youtubeApiKey;
					final OkHttpClient okHttpClient1 = new OkHttpClient();
					final Request request1 = new Request.Builder()
							.url(videoTimeUrl)
							.get()
							.build();
					final Response response1 = okHttpClient1.newCall(request1).execute();
					JSONObject jsonObject1 = JSONObject.parseObject(response1.body().string());
					JSONArray jsonArray1 = jsonObject1.getJSONArray("items");
					JSONObject obJson = jsonArray1.getJSONObject(0).getJSONObject("contentDetails");
					String duration = obJson.get("duration").toString();
					String title = jsonArray1.getJSONObject(0).getJSONObject("snippet").get("title").toString();
					String desc = jsonArray1.getJSONObject(0).getJSONObject("snippet").get("description").toString();
					if(null!=duration){
						int hoursIndex = duration.indexOf("H");
						if(hoursIndex < TableConstant.COMMON_ZERO) {
							int ptIndex = duration.indexOf("PT");
							String cutUrl = duration.substring(ptIndex+2, duration.length());
							int indexm = cutUrl.indexOf("M");
							int indexs = cutUrl.indexOf("S");
							Integer minutes = TableConstant.COMMON_ZERO;
							Integer seconds = TableConstant.COMMON_ZERO;
							if(indexm>=0) {
								minutes = Integer.parseInt(cutUrl.substring(0, indexm));
								if(indexs>=0){
									seconds = Integer.parseInt(cutUrl.substring(indexm, indexs).substring("M".length()));
								}
							}else {
								seconds = Integer.parseInt(cutUrl.substring(0, indexs));
							}

							Integer totalDuration = minutes * 60 + seconds;
							map.put("duration",totalDuration);
						}else {
							int ptIndex = duration.indexOf("PT");
							String cutUrl = duration.substring(ptIndex + 2, duration.length());
							int hourIndex = cutUrl.indexOf("H");
							int indexm = cutUrl.indexOf("M");
							int indexs = cutUrl.indexOf("S");
							Integer hours = Integer.parseInt(cutUrl.substring(0,hourIndex));
							Integer minutes = TableConstant.COMMON_ZERO;
							Integer seconds = TableConstant.COMMON_ZERO;
							if(indexm>=0) {
								minutes = Integer.parseInt(cutUrl.substring(hourIndex, indexm).substring("H".length()));
								if(indexs>=0){
									seconds = Integer.parseInt(cutUrl.substring(indexm, indexs).substring("M".length()));
								}
							}else {
								seconds = Integer.parseInt(cutUrl.substring(hourIndex, indexs).substring("H".length()));
							}

							Integer totalDuration = hours*3600+minutes * 60 + seconds;
							map.put("duration", totalDuration);
						}

					}
					map.put("title",title);
					map.put("desc",desc);
					map.put("playUrl",playUrl);
					vidList.add(map);
					return message.ok().addData("yotubeList",vidList);
				}
				//单个视频，非分享链接

				int index = url.indexOf("v=");
				String cutUrl = url.substring(index,url.length());
				int index0 = cutUrl.indexOf("v=");
				int index1 = cutUrl.indexOf("&");
				//如果链接中没有参数
				if(index1<0){
 					String vid1 = cutUrl.substring(index0,cutUrl.length()).substring("v=".length());
					String playUrl = "https://www.youtube.com/embed/" + vid1;
					map.put("playUrl",playUrl);



					String videoTimeUrl = "https://youtube.googleapis.com/youtube/v3/videos?part=id&part=contentDetails&part=snippet"+"&id="+vid1+"&key="+youtubeApiKey;
					final OkHttpClient okHttpClient1 = new OkHttpClient();
					final Request request1 = new Request.Builder()
							.url(videoTimeUrl)
							.get()
							.build();
					final Response response1 = okHttpClient1.newCall(request1).execute();
					JSONObject jsonObject1 = JSONObject.parseObject(response1.body().string());
					JSONArray jsonArray1 = jsonObject1.getJSONArray("items");
					JSONObject obJson = jsonArray1.getJSONObject(0).getJSONObject("contentDetails");
					String duration = obJson.get("duration").toString();
					if(null!=duration){
						int hoursIndex = duration.indexOf("H");
						String title = jsonArray1.getJSONObject(0).getJSONObject("snippet").get("title").toString();
						String desc = jsonArray1.getJSONObject(0).getJSONObject("snippet").get("description").toString();
						map.put("title",title);
						map.put("desc",desc);
						if(hoursIndex < TableConstant.COMMON_ZERO) {
							int ptIndex = duration.indexOf("PT");
							String cutUrl1 = duration.substring(ptIndex+2, duration.length());
							int indexm = cutUrl1.indexOf("M");
							int indexs = cutUrl1.indexOf("S");
							Integer minutes = TableConstant.COMMON_ZERO;
							Integer seconds = TableConstant.COMMON_ZERO;
							if(indexm>=0) {
								minutes = Integer.parseInt(cutUrl1.substring(0, indexm));
								if(indexs>=0 ){
									seconds = Integer.parseInt(cutUrl1.substring(indexm, indexs).substring("M".length()));
								}
							}else {
								seconds = Integer.parseInt(cutUrl1.substring(0, indexs));
							}
							Integer totalDuration = minutes * 60 + seconds;
							map.put("duration",totalDuration);
						}else {
							int ptIndex = duration.indexOf("PT");
							String cutUrl1 = duration.substring(ptIndex + 2, duration.length());
							int hourIndex = cutUrl1.indexOf("H");
							int indexm = cutUrl1.indexOf("M");
							int indexs = cutUrl1.indexOf("S");
							Integer hours = Integer.parseInt(cutUrl1.substring(0,hourIndex));
							Integer minutes = TableConstant.COMMON_ZERO;
							Integer seconds = TableConstant.COMMON_ZERO;
							if(indexm>=0) {
								minutes = Integer.parseInt(cutUrl1.substring(hourIndex, indexm).substring("H".length()));
								if(indexs>=0){
									seconds = Integer.parseInt(cutUrl1.substring(indexm, indexs).substring("M".length()));
								}
							}else {
								seconds = Integer.parseInt(cutUrl1.substring(hourIndex, indexs).substring("H".length()));
							}

							Integer totalDuration = hours*3600+minutes * 60 + seconds;
							map.put("duration", totalDuration);
						}


					}


					vidList.add(map);
					return message.ok().addData("yotubeList",vidList);
				}else {
					//如果有参数&
					String vid = cutUrl.substring(index0, index1).substring("v=".length());
					String playUrl = "https://www.youtube.com/embed/" + vid;
					map.put("playUrl",playUrl);




					String videoTimeUrl = "https://youtube.googleapis.com/youtube/v3/videos?part=id&part=contentDetails&part=snippet"+"&id="+vid+"&key="+youtubeApiKey;
					final OkHttpClient okHttpClient1 = new OkHttpClient();
					final Request request1 = new Request.Builder()
							.url(videoTimeUrl)
							.get()
							.build();
					final Response response1 = okHttpClient1.newCall(request1).execute();
					JSONObject jsonObject1 = JSONObject.parseObject(response1.body().string());
					JSONArray jsonArray1 = jsonObject1.getJSONArray("items");
					JSONObject obJson = jsonArray1.getJSONObject(0).getJSONObject("contentDetails");
					String title = jsonArray1.getJSONObject(0).getJSONObject("snippet").get("title").toString();
					String desc = jsonArray1.getJSONObject(0).getJSONObject("snippet").get("description").toString();
					map.put("title",title);
					map.put("desc",desc);
					String duration = obJson.get("duration").toString();
					if(null!=duration){
						int hoursIndex = duration.indexOf("H");
						if(hoursIndex < TableConstant.COMMON_ZERO) {
							int ptIndex = duration.indexOf("PT");
							String cutUrl1 = duration.substring(ptIndex + 2, duration.length());
							int indexm = cutUrl1.indexOf("M");
							int indexs = cutUrl1.indexOf("S");
							Integer minutes = TableConstant.COMMON_ZERO;
							Integer seconds = TableConstant.COMMON_ZERO;
							if(indexm>=0) {
								minutes = Integer.parseInt(cutUrl1.substring(0, indexm));
								if(indexs>=0){
									seconds = Integer.parseInt(cutUrl1.substring(indexm, indexs).substring("M".length()));
								}
							}else {
								seconds = Integer.parseInt(cutUrl1.substring(0, indexs));
							}

							Integer totalDuration = minutes * 60 + seconds;
							map.put("duration", totalDuration);
						}
						else {
							int ptIndex = duration.indexOf("PT");
							String cutUrl1 = duration.substring(ptIndex + 2, duration.length());
							int hourIndex = cutUrl.indexOf("H");
							int indexm = cutUrl.indexOf("M");
							int indexs = cutUrl.indexOf("S");
							Integer hours = Integer.parseInt(cutUrl1.substring(0,hourIndex));
							Integer minutes = TableConstant.COMMON_ZERO;
							Integer seconds = TableConstant.COMMON_ZERO;
							if(indexm>=0) {
								minutes = Integer.parseInt(cutUrl1.substring(hourIndex, indexm).substring("H".length()));
								if(indexs>=0){
									seconds = Integer.parseInt(cutUrl1.substring(indexm, indexs).substring("M".length()));
								}
							}else {
								seconds = Integer.parseInt(cutUrl1.substring(hourIndex, indexs).substring("H".length()));
							}

							Integer totalDuration = hours*3600+minutes * 60 + seconds;
							map.put("duration", totalDuration);
						}
					}




					vidList.add(map);
					return message.ok().addData("yotubeList", vidList);
				}
			}
			String youtubeUrl = "https://youtube.googleapis.com/youtube/v3/playlistItems?part=id&part=contentDetails&part=snippet&maxResults=999&playlistId=" + listId + "&key=" + youtubeApiKey;
			final OkHttpClient okHttpClient = new OkHttpClient();
			final Request request = new Request.Builder()
					.url(youtubeUrl)
					.get()
					.build();
			final Response response = okHttpClient.newCall(request).execute();
			JSONObject jsonObject = JSONObject.parseObject(response.body().string());
//			System.out.println(jsonObject);
			JSONArray jsonArray = jsonObject.getJSONArray("items");
			List<Object> objectList = jsonArray.toJavaList(Object.class);
			List<String> vidList = new ArrayList<>();
			List<String> videoIdList = new ArrayList<>();
			for (int i = 0; i < jsonArray.size(); i++) {
				Map<String,Object> youtubeMap = new HashMap<>();
				JSONObject obJson = jsonArray.getJSONObject(i).getJSONObject("contentDetails");
				String videoId = obJson.getString("videoId");
				videoIdList.add(videoId);
				String vid = "https://www.youtube.com/embed/" + obJson.getString("videoId");
				youtubeMap.put("title",jsonArray.getJSONObject(i).getJSONObject("snippet").getString("title"));
				youtubeMap.put("desc",jsonArray.getJSONObject(i).getJSONObject("snippet").getString("description"));
				youtubeMap.put("playUrl",vid);
				youtubeMap.put("videoId",obJson.getString("videoId"));
				vidList.add(vid);
				youtubeList.add(youtubeMap);
			}

			//再调一次list接口获取视频时长
			String a = null;
		    String getDurationUrl = "https://youtube.googleapis.com/youtube/v3/videos?part=id&part=contentDetails&part=snippet";
		    StringBuffer stringBuffer = new StringBuffer(getDurationUrl);
			for(int i=0;i<videoIdList.size();i++){
				stringBuffer.append("&id=");
				stringBuffer.append(videoIdList.get(i));
				getDurationUrl = getDurationUrl+"&id="+videoIdList.get(i).toString();
			}
			String videoTimeUrl = String.valueOf(stringBuffer)+"&key="+youtubeApiKey;
		    final OkHttpClient okHttpClient1 = new OkHttpClient();
		    final Request request1 = new Request.Builder()
				.url(videoTimeUrl)
				.get()
				.build();
		   final Response response1 = okHttpClient1.newCall(request1).execute();
		   JSONObject jsonObject1 = JSONObject.parseObject(response1.body().string());
		   JSONArray jsonArray1 = jsonObject1.getJSONArray("items");
		   for (int i = 0; i < jsonArray1.size(); i++) {
			   JSONObject obJson = jsonArray1.getJSONObject(i).getJSONObject("contentDetails");
			   String vid = jsonArray1.getJSONObject(i).get("id").toString();
			   String duration = obJson.get("duration").toString();
			   for(int j = 0;j<youtubeList.size();j++){
			   	if(youtubeList.get(j).get("videoId").equals(vid)){
			   		if(null!=duration){
						String title = new String();
						String desc = new String();
						if(null!=jsonArray1.getJSONObject(i).getJSONObject("snippet").get("title")){
							title = jsonArray1.getJSONObject(i).getJSONObject("snippet").get("title").toString();
						}
						if(null != jsonArray1.getJSONObject(i).getJSONObject("snippet").get("description")){
							desc = jsonArray1.getJSONObject(i).getJSONObject("snippet").get("description").toString();
						}
						youtubeList.get(j).put("title",title);
						youtubeList.get(j).put("desc",desc);
			   			int hoursIndex = duration.indexOf("H");
			   			if(hoursIndex < TableConstant.COMMON_ZERO) {
			   				//如果视频时长不超过一个小时
							int index = duration.indexOf("PT");
							String cutUrl = duration.substring(index + 2, duration.length());
							int index0 = cutUrl.indexOf("M");
							int index1 = cutUrl.indexOf("S");
							Integer minutes = TableConstant.COMMON_ZERO;
							Integer seconds = TableConstant.COMMON_ZERO;
							if(index0>=0) {
								minutes = Integer.parseInt(cutUrl.substring(0, index0));
								if(index1>=0) {
									seconds = Integer.parseInt(cutUrl.substring(index0, index1).substring("M".length()));
								}
							}else {
								seconds = Integer.parseInt(cutUrl.substring(0, index1));
							}
							Integer totalDuration = minutes * 60 + seconds;
							youtubeList.get(j).put("duration", totalDuration);
						}else {
							int index = duration.indexOf("PT");
							String cutUrl = duration.substring(index + 2, duration.length());
							int hourIndex = cutUrl.indexOf("H");
							int index0 = cutUrl.indexOf("M");
							int index1 = cutUrl.indexOf("S");
							Integer hours = Integer.parseInt(cutUrl.substring(0,hourIndex));
							Integer minutes = TableConstant.COMMON_ZERO;
							Integer seconds = TableConstant.COMMON_ZERO;
							if(index0>=0) {
								minutes = Integer.parseInt(cutUrl.substring(hourIndex, index0).substring("H".length()));
								if(index1>=0) {
									seconds = Integer.parseInt(cutUrl.substring(index0, index1).substring("M".length()));
								}
							}else {
								seconds = Integer.parseInt(cutUrl.substring(hourIndex, index1).substring("H".length()));
							}

							Integer totalDuration = hours*3600+minutes * 60 + seconds;
							youtubeList.get(j).put("duration", totalDuration);
						}
					}
				}
			   }
		   }

		    return message.ok().addData("yotubeList",youtubeList);
	}


	@PostMapping("/getKalturaVideos")
	public Message getKalturaVideos(@RequestBody String videoUrl, HttpServletRequest re) {
		Message message = new Message();
		String domain = re.getServerName();
//		if (!videoUrl.contains(domain)){
//			return message.error("The video domain is inconsistent with the API domain!");
//		}
		try {
//			videoUrl = URLEncoder.encode(videoUrl, "UTF-8");
			videoUrl = videoUrl.replaceAll(" ","%2B");
			URL url = new URL(videoUrl);
			InputStream inputStream = url.openStream();
			InputStreamReader inputStreamReader =new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str;
			String pattern = "[^0-9]";
			Pattern r = Pattern.compile(pattern);
			String partnerId = null;
			String uiConfId = null;
			String entryId = null;
			String Url = null;
			String videoID=null;
			int i = videoUrl.indexOf("online-presentation/");
			// 本地视频
			if (i>0){
				String subString = videoUrl.substring(videoUrl.indexOf("online-presentation/") + "online-presentation/".length());
				videoID = subString.substring(0, subString.indexOf("/"));
				System.out.println(videoID);
			}else {
				while ((str = bufferedReader.readLine()) != null) {
					System.out.println(str + "<---------------->");
					if (str.contains("canonical")) {
						//获取到网页元素视频链接
						String startStr = "href=\"";
						String endStr = "\">";
						int startIndex = str.indexOf(startStr) + startStr.length();
						int endIndex = str.indexOf(endStr, startIndex);
						Url = str.substring(startIndex, endIndex);
						//解析视频id
						String subString = Url.substring(Url.indexOf("online-presentation/") + "online-presentation/".length());
						videoID = subString.substring(0, subString.indexOf("/"));
						System.out.println(videoID);
					}
					//废除改为调用pt接口
					/*if(str.contains("uiConfId")){
						String s = str.replaceAll(" ", "");
						Matcher matcher = r.matcher(s);
						String reult = matcher.replaceAll("");
						uiConfId = reult;
					}
					if(str.contains("partnerId")){
						String s = str.replaceAll(" ", "");
						Matcher matcher = r.matcher(s);
						String result = matcher.replaceAll("");
						partnerId = result;
					}
					if(str.contains("entryId")){
						Pattern pattern1 = Pattern.compile("\\'(.*?)\\'");
						String s = str.replaceAll(" ", "");
						Matcher matcher = pattern1.matcher(s);
						ArrayList<String> list = new ArrayList<String>();
						while (matcher.find()) {
							list.add(matcher.group().trim().replace("\"",""));
						}
						entryId = list.get(0);
						entryId = entryId.replaceAll("\'","");
					}*/
				}
			}
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			//获得userid
			GcUser gcUser = this.getGcUser();
			//redis获取pttoken
			String accessToken = (String) redisOperator.get("access_token_userid"+gcUser.getId());
			if (accessToken==null){
				return message.error("Failed to obtain token, please log in again");
			}
			Integer masterId = getHeaderMasterId(re);
			QueryWrapper<PtLoginConfig> loginConfigQueryWrapper = new QueryWrapper<>();
			loginConfigQueryWrapper.eq("master_id",masterId);
			PtLoginConfig ptLoginConfig = ptLoginConfigService.getOne(loginConfigQueryWrapper);

			String replaceApi = ptLoginConfig.getPtRootUrl()+getVideoDetails.replace("{id}", String.valueOf(videoID));
			//调取pt接口，获取视频详情
			JSONObject object = HttpUtil.doGetAuthorization(replaceApi, accessToken);
			if (object==null||object.getJSONObject("video_hosting")==null){
				return message.error(I18NUtil.get("videoURL.not.exist"));
			}
			ApiAssert.notEmpty(object.getJSONObject("video_hosting"), I18NUtil.get("videoURL.not.exist"));
			uiConfId=object.getJSONObject("video_hosting").getString("ui_conf_id");
			partnerId=object.getJSONObject("video_hosting").getString("partner_id");
			entryId=object.getJSONObject("video_hosting").getString("id");
			String title=object.getString("title");
			String description=object.getString("description");
			String videoDuration=object.getString("video_duration");
			String thumbUrl=object.getString("thumb_url");
			if(null != entryId && null!= partnerId && null!=uiConfId){
				String finalUrl = "https://www.kaltura.com/index.php/extwidget/preview/partner_id/"+partnerId+"/uiconf_id/"+uiConfId+"/entry_id/"+entryId+"/embed/dynamic?";
				String thumbNail = "https://cfvod.kaltura.com/p/"+partnerId+"/sp/"+uiConfId+"/thumbnail/entry_id/"+entryId;
				message.ok().addData("url",finalUrl);
				message.ok().addData("thumbNail",thumbUrl);
				message.ok().addData("title",title);
				message.ok().addData("description",description);
				message.ok().addData("videoDuration",videoDuration);
			}

			//调用kaltura api，创建一个session
			String getSessionUrl = "https://www.kaltura.com/api_v3/service/session/action/start";
			final okhttp3.OkHttpClient okHttpClient = new okhttp3.OkHttpClient();
			okhttp3.RequestBody requestBody1 = new FormBody.Builder()
					.add("secret","6ff4811dcc1fa598946c7f47c95e6ed7")
					.add("userId","8d9d6bc7e92f38a51ca75098b148a41b")
					.add("partnerId","4648013")
					.add("type","0")
					.build();

			final okhttp3.Request request = new okhttp3.Request.Builder()
					.addHeader("content-type", "application/json")
					.url(getSessionUrl)
					.post(requestBody1)
					.build();
			final okhttp3.Response response = okHttpClient.newCall(request).execute();
			String result = response.body().string();
			Integer index0 = result.indexOf("<result>");
			String cuturl = result.substring("<xml><result>".length(),result.length());
			Integer index1 = cuturl.indexOf("</result>");
			String sessionId = cuturl.substring(0,index1);
			System.out.println();

			//通过创建的session调用kaltura的具体的视频查询接口
//			okhttp3.RequestBody requestBody = new FormBody.Builder()
//					.add("entryId",entryId)
//					.add("version","-1")
//					.build();
//
//			String mediaUrl = "https://www.kaltura.com/api_v3/service/media/action/get?ks="+sessionId+"&format=1&partnerId=4648013";
////			https://www.kaltura.com/api_v3/service/media/action/get?ks=djJ8NDY0ODAxM3wTqFDN4QFqo0uvdu_j4mYSMsbJYOEQOca5S7yF5HYAGdK29ISyQOMWLLhQS7Dgu1BvvHolUKbEjr-kl2NI-Z8tBoqMYCLMPgDUPfuKGWZ9ueH5KzPCGoszzedsAaDV0Go%3D&format=1&partnerId=4648013
//			final Request mediaRequest = new Request.Builder()
//					.url(mediaUrl)
//					.post(requestBody)
//					.build();
//			final Response mediaResponse = okHttpClient.newCall(mediaRequest).execute();
//			String mediaresult = mediaResponse.body().string();
//			message.ok().addData("duration",mediaresult);


		}catch (Exception e){
			e.printStackTrace();
			String extractedInfo=e.getMessage();
			if(e.getMessage().contains("detail")){
				int startIndex = e.getMessage().indexOf("detail\":\"") + "detail\":\"".length();
				int endIndex = e.getMessage().indexOf("\"", startIndex);
				extractedInfo = e.getMessage().substring(startIndex, endIndex);
				return message.error(extractedInfo+"\n"+"API:"+re.getServerName());
			}
			return message.error(extractedInfo);
		}
		return  message.ok();
	}
}
