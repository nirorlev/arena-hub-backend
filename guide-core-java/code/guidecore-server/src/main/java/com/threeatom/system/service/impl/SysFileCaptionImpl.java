package com.threeatom.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.constant.LanuageType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.mapper.GcVideoMapper;
import com.threeatom.system.controller.SysFIleController;
import com.threeatom.system.entity.*;
import com.threeatom.system.mapper.SysFileCaptionMapper;
import com.threeatom.system.service.SysFileCaptionService;
import com.threeatom.system.service.SysFileService;
import com.threeatom.utils.HttpUtil;
import com.threeatom.utils.TranslateUtil;
import com.threeatom.utils.data.TransData;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

@Service
public class SysFileCaptionImpl extends ServiceImpl<SysFileCaptionMapper, SysFileCaption> implements SysFileCaptionService {

    @Value("${ym.keyPath:73d6ZDwQcT9PUFr5vonI5Ztw}")
    private String key;

    @Value("${ym.subUrl:https://api.guangfan.tech/v1/get-subtitle}")
    private String subUrl;

    @Value("${ym.callBackUrl:https://apigc.da-life.cn/api/v1/guidecore/callback/yunMaoCallback/}")
    private String callBackUrl;

    @Value("${fanyi.transApiHost:http://api.fanyi.baidu.com/api/trans/vip/translate}")
    private  String transApiHost;

    @Value("${fanyi.appid:20220817001310424}")
    private  String appid;

    @Value("${fanyi.securityKey:zS1Ecu_BePNRybsH43OA}")
    private  String securityKey;

    @Value("${fanyi.rate:1000}")
    private Integer rateLength;

    @Resource
    SysFileCaptionMapper sysFileCaptionMapper;

    @Autowired
    private SysFileService sysFileService;

    @Resource
    private GcVideoMapper gcVideoMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(SysFIleController.class);

    @Override
    public SysFile selectSysFileCaption(Integer id) {
        SysFile list = this.sysFileCaptionMapper.selectFileCaption(id);
        return list;
    }

    public List<SysFileCaption> selectSysFileCaptionId(Integer id) {
        List<SysFileCaption> sysFileCaption = this.sysFileCaptionMapper.selectSysFileCaptionId(id);
        return sysFileCaption;
    }



    public SysFileCaption saveYmId(Integer id) {
        SysFileCaption sysFileCaption = new SysFileCaption();
        this.sysFileCaptionMapper.saveYmTaskId(id);
        return sysFileCaption;
    }

    public SysFileCaption selectById(Integer id){
        return this.sysFileCaptionMapper.selectById(id);
    }

    public SysFileCaption selectMainSysFile(Integer id){
        return this.sysFileCaptionMapper.selectMainSysFile(id);
    }

    @Override
    public SysFileCaption callYunMao(SysCaptionRequest request) {
        String ymTaskId = null;
        SysFileCaption caption = new SysFileCaption();
        try {
            //拼接头部请求
            Map<String, String> header = new HashMap<String, String>();
            header.put("content-type", "application/json");
            header.put("api-key", key);
            request.setFileUrl(request.getFileUrl()+"&abc.mp4");
            request.setNotifyUrl(callBackUrl + request.getCaptionId());
            request.setResultType("srt");
            //发送请求并返回信息
            String JsonRequest = HttpUtil.doPost1(subUrl,header, JSONObject.toJSONString(request));
            JSONObject jsonObject = JSONObject.parseObject(JsonRequest);
            caption.setYmCode(jsonObject.getInteger("code"));
            if (!jsonObject.getInteger("code").equals(TableConstant.COMMON_ZERO)){
                return caption;
            }
            if (jsonObject.getString("data")!=null&&!jsonObject.getString("data").equals("{}")){
                ymTaskId = jsonObject.getString("data");
                caption.setYmTaskId(ymTaskId);
            }
            LOGGER.error("json请求{}",JSONObject.toJSONString(request));
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("请求字幕异常,json请求{}",JSONObject.toJSONString(request));
            LOGGER.error(e.getMessage());
        }
        return caption;
    }



    public void asyncTask(SysFileCaption fileCaption,MultipartFile file,SysSystem sys){
        //GcExternalMessage message = null;
        GcVideo video = gcVideoMapper.selectById(fileCaption.getVideoId());
        SysFile videoFiles = sysFileService.getInfoById(video.getFileId());
        List<String> translateList = null;

        if (null!=fileCaption.getNewTargetLangJson()){
            JSONArray cases = JSONArray.parseArray(JSON.toJSONString(fileCaption.getNewTargetLangJson()));
            videoFiles.getTargetLangJson().addAll(cases);
            sysFileService.saveOrUpdate(videoFiles);
            translateList = LanuageType.getByCodeList(fileCaption.getNewTargetLangJson());
        }else {
            if (null==videoFiles.getTargetLangJson() || videoFiles.getTargetLangJson().size()==0){
                translateList = LanuageType.selectNotCode(fileCaption.getLang());
            }else {
                List<String> langList = videoFiles.getTargetLangJson().toJavaList(String.class);
                langList.remove(fileCaption.getLang());
                translateList = LanuageType.getByCodeList(langList);
            }
        }
        //List<String> translateList = LanuageType.selectNotCode(fileCaption.getLang());
        String sourceLanguage = LanuageType.getByCode(fileCaption.getLang()).getBdCode();
        try {
            Long fileLength = file.getSize();
            byte[] fileContent = new byte[fileLength.intValue()];
            InputStream inputStream = file.getInputStream();
            inputStream.read(fileContent);
            //获取多种字幕文件

            String context = new String(fileContent).substring(new String(fileContent).indexOf("1"));
            /*context = context.substring(context.indexOf("1"));*/
            translateList.forEach(i->{
                try {
                    StringBuilder builder = new StringBuilder();
                    Integer start = 0;
                    if (context.length()>=rateLength){
                        String[] split = context.split("\n");
                        StringBuilder sb = new StringBuilder();
                        //获取时间段落下标
                        Integer counts = 0;
                        //获取时间集合
                        List<String> times = new ArrayList<>();
                        for (int i1 = 0; i1 < split.length; i1++) {
                            if (i1%4==1){
                                times.add(split[i1]);
                            }
                            if ((sb.toString().length()+(split[i1]).length())>=start+rateLength||i1 == split.length -1){
                                if (i1 == split.length -1){
                                    sb.append(split[i1]+"\n");
                                }
                                List<TransData> tr = TranslateUtil.getTransResult(sb.substring(start), sourceLanguage, i,transApiHost,appid,securityKey/*,message*/);
                                Thread.sleep(1500);
                                if (tr.size()!=0){
                                    for (int i2 = 0; i2 < tr.size(); i2++) {
                                        builder.append(times.get(counts)+"\n"+tr.get(i2).getDst()+"\n\n");
                                        counts++;
                                    }
                                }
                                start = sb.toString().length();
                            }
                            if (i1%4==2){
                                sb.append(split[i1]+"\n");
                            }
                        }
                    }else{
                        //时间集合
                        List<String> times = new ArrayList<>();
                        //时间段落下标
                        Integer counts = 0;
                        String[] timeList = context.split("\n");
                        for (int i1 = 0; i1 < timeList.length; i1++) {
                            if (i1 % 4 == 1){
                                times.add(timeList[i1]);
                            }
                        }
                        List<TransData> list = TranslateUtil.getTransResult(context, sourceLanguage, i,transApiHost,appid,securityKey/*,message*/);
                        Thread.sleep(1000);
                        builder = new StringBuilder();
                        //换行
                        for (int i1 = 0; i1 < list.size(); i1++) {
                            if(i1%3==1){
                                builder.append(times.get(counts)+"\n");
                                counts++;
                            }
                            if (i1%3==2){
                                builder.append(list.get(i1).getDst()+"\n\n");
                            }
                        }
                    }
                    InputStream is = new ByteArrayInputStream(builder.toString().getBytes());
                    FileItemFactory factory = new DiskFileItemFactory(16, null);
                    FileItem item = factory.createItem("downloadFile", TableConstant.sysFile_folder_guidecoreVedioCaption, false,fileCaption.getYmTaskId()+i+".vtt");
                    OutputStream os = item.getOutputStream();

                    Integer ch;
                    while ((ch = is.read())!= -1){
                        os.write(ch);
                    }
                    os.close();
                    is.close();
                    inputStream.close();
                    MultipartFile file1 = new CommonsMultipartFile(item);
                    SysFile sysFile = sysFileService.saveRes(null,sys,TableConstant.sysFile_folder_guidecoreVedioCaption,TableConstant.sysFile_saveType_aliOSS_2,file1.getOriginalFilename(),file1);
                    SysFileCaption sysFileCaption = new SysFileCaption();
                    sysFileCaption.setLang(LanuageType.getByBdCode(i).getCode());
                    sysFileCaption.setCaptionFileId(sysFile.getId());
                    sysFileCaption.setVideoId(fileCaption.getVideoId());
                    this.baseMapper.insert(sysFileCaption);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public SysFileCaption getCaptionInfo(SysFileCaption sysFileCaption) {
        return this.baseMapper.getCaptionInfo(sysFileCaption);
    }

    @Override
    public void updateCaptionState(JSONArray targetLang, Integer videoId) {
        List<String> targetList = targetLang.toJavaList(String.class);
        this.baseMapper.updateCaptionState(targetList,videoId);
        this.baseMapper.updateInCaptionState(targetList,videoId);
    }

    @Override
    public void deleteCaption(Integer videoId) {
        QueryWrapper<SysFileCaption> queryWrapper=new QueryWrapper<SysFileCaption>();
        queryWrapper.eq("video_id",videoId);
        this.baseMapper.delete(queryWrapper);
    }

    @Override
    public SysFileCaption selectSrtData(Integer videoId) {
        return baseMapper.selectSrtData(videoId);
    }
}
