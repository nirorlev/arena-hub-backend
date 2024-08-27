package com.threeatom.guidecore.controller.api.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.controller.Message;
import com.threeatom.common.redis.RedisOperator;

import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.controller.api.manager.NewUiGcVideoController;
import com.threeatom.guidecore.service.PtLoginConfigService;
import com.threeatom.guidecore.service.impl.PowtoonVideoProviderService;
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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/guidecore/video-providers")
public class VideoProvidersController extends GuideCoreController{

	@Value("${youtubeApiKey}")
	private String youtubeApiKey;

	@Value("${videoUrl}")
	private String getVideoDetails;

	@Autowired
	private RedisOperator redisOperator;

	@Autowired
	private PtLoginConfigService ptLoginConfigService;

	@Autowired
	private PowtoonVideoProviderService powtoonVideoProviderService;

	@Autowired
	private Environment env;

	@GetMapping("/youtube/video-data")
	public Message getYoutubeVideos(@RequestParam String url) throws IOException {

			Message message = new Message();
			List<Map<String,Object>> youtubeList = new ArrayList<>();

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

	private JSONObject formatKalturaVideoData (JSONObject videoData) {
		JSONObject result = new JSONObject();
		result.put("url", videoData.getString("playerUrl"));
		result.put("thumbNail", videoData.getString("thumbnailUrl"));
		result.put("title", videoData.getString("title"));
		result.put("description", videoData.getString("description"));
		result.put("duration", videoData.getFloat("duration"));
		result.put("source", videoData.getJSONObject("source"));
		return result;
	}

	@GetMapping("/powtoon/video-data")
	public Message getKalturaVideos(@RequestBody String videoUrl, HttpServletRequest request) {
		Message message = new Message();

		try {
			videoUrl = videoUrl.replaceAll(" ", "%2B");
			URL url = new URL(videoUrl);
			JSONObject videoData = powtoonVideoProviderService.getVideoDataFromUrl(url);
			JSONObject formattedData = formatKalturaVideoData(videoData);
			return message.ok().setJsonData(formattedData);
		} catch (Exception e) {
			String extractedInfo = e.getMessage();
			if (e.getMessage().contains("detail")) {
				int startIndex = e.getMessage().indexOf("detail\":\"") + "detail\":\"".length();
				int endIndex = e.getMessage().indexOf("\"", startIndex);
				extractedInfo = e.getMessage().substring(startIndex, endIndex);
				return message.error(extractedInfo + "\n" + "API:" + request.getServerName());
			}

			return message.error(extractedInfo);
		}
	}
}
