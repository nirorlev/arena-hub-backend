//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.export.excel.impl;

import com.alibaba.excel.EasyExcel;
import com.threeatom.common.export.excel.ExcelOperator;
import com.threeatom.guidecore.constant.WatchedStatusType;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.excel.vo.StudentBehaviorDataExcel;
import io.swagger.models.auth.In;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;

public class EasyExcelOperatorImpl implements ExcelOperator {
    public EasyExcelOperatorImpl() {
    }

    public <T> void writeExcelToWebResponse(HttpServletResponse response, String fileName, String sheetName, Class<T> dataClass, List<T> datas) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileNameWeb = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileNameWeb + ".xlsx");
        EasyExcel.write(response.getOutputStream(), dataClass).sheet(sheetName).doWrite(datas);
    }

    @Override
    public <T> void writeDynamicHeadExcelToWebResponse(HttpServletResponse response, List<GcSubject> subjectList, String fileName, String sheetName, List<StudentBehaviorDataExcel> studentBehaviorExcelData) throws IOException {
        OutputStream outputStream = getOutputStream(response, fileName);
        EasyExcel.write(outputStream)
                .head(createTestListStringHead(subjectList))
                .sheet(sheetName)
                .doWrite(createDynamicModelList(studentBehaviorExcelData));
        outputStream.flush();
        outputStream.close();
    }


    private static OutputStream getOutputStream(HttpServletResponse response,String fileName) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String name = URLEncoder.encode(fileName, "utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + name + ".xlsx");
            return response.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<List<Object>> createDynamicModelList(List<StudentBehaviorDataExcel> studentBehaviorExcelData){
        List<List<Object>> rows = new ArrayList<>();
        for (StudentBehaviorDataExcel studentBehaviorDataExcel : studentBehaviorExcelData) {
            List<Object> row = new ArrayList<>();
            row.add(studentBehaviorDataExcel.getFirstName());
            row.add(studentBehaviorDataExcel.getLastName());
            row.add(studentBehaviorDataExcel.getUserName());
            row.add(studentBehaviorDataExcel.getWatchedNum());
            if(studentBehaviorDataExcel.getWatchedNum()!=null&&studentBehaviorDataExcel.getWatchedNum()!=null){
                row.add(timeConversion(studentBehaviorDataExcel.getAllPlayTime()));
            }else {
                row.add(studentBehaviorDataExcel.getAllPlayTime());
            }

            row.add(studentBehaviorDataExcel.getAnswerNum());
            if (null!=studentBehaviorDataExcel.getSubjectList()){
            for (int i = 0; i < studentBehaviorDataExcel.getSubjectList().size(); i++) {
                row.add(WatchedStatusType.getByCode(studentBehaviorDataExcel.getSubjectList().get(i).getWatchedStatus()).getDesc());
                if (Objects.nonNull(studentBehaviorDataExcel.getSubjectList())&&studentBehaviorDataExcel.getSubjectList().size()!=0) {
                    if (null != studentBehaviorDataExcel.getSubjectList().get(i).getSubjects()){
                    studentBehaviorDataExcel.getSubjectList().get(i).getSubjects().forEach(j -> {
                        if (null!=j.getWatchedStatus()){
                            row.add(WatchedStatusType.getByCode(j.getWatchedStatus()).getDesc());
                        }
                    });
                    }
                }
            }
            }
            rows.add(row);
        }
        return rows;
    }
    //秒钟转换分钟
    public static float timeConversion(Integer num){
            float second = (float) num;
            float minute = second/60;
            return ( float )(Math.round(minute* 100 ))/ 100 ;
    }

    public static List<List<String>> createTestListStringHead(List<GcSubject> subjectList){
        // 模型上没有注解，表头数据动态传入
        List<List<String>> head = new ArrayList<List<String>>();
        //String titleStr = "First Name,Last Name,Email,Number of videos watched,Number of minutes watched,Number of questions answered";
        StringBuilder sb = new StringBuilder("First Name&&Last Name&&Email&&Number of videos watched&&Number of minutes watched&&Number of questions answered");
        for (GcSubject gcSubject : subjectList) {
           // titleStr = String.join(",", titleStr, gcSubject.getName());
            sb.append("&&"+gcSubject.getName());
            if (Objects.nonNull(gcSubject.getSubjects())){
                gcSubject.getSubjects().forEach(i->{
                    sb.append("&&"+i.getName());
                });
            }
        }
        String[] titles = sb.toString().split("&&");

        for (String title : titles) {
            List<String> headCoulumn1 = new ArrayList<String>();
            headCoulumn1.add(title);
            head.add(headCoulumn1);
        }

        return head;
    }



}
