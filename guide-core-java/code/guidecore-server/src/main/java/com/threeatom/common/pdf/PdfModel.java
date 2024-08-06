package com.threeatom.common.pdf;

import java.net.MalformedURLException;
import java.net.URL;

public class PdfModel {
    private String userName;

    private String courseName;

    private String menNo;

    private String completionDate;

    private String number; // 可用几年

    private URL img1Url;

    private URL img2Url;

    private String courseTotalTime;

    private String context;

    private Integer cpdFlag;

    public String getCourseTotalTime() {
        return courseTotalTime;
    }

    public void setCourseTotalTime(String courseTotalTime) {
        this.courseTotalTime = courseTotalTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getMenNo() {
        return menNo;
    }

    public void setMenNo(String menNo) {
        this.menNo = menNo;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public URL getImg1Url() {
        return img1Url;
    }

    public void setImg1Url(String img1Url) throws MalformedURLException {
        this.img1Url = new URL(img1Url);
    }

    public URL getImg2Url() {
        return img2Url;
    }

    public void setImg2Url(String img2Url) throws MalformedURLException {
        this.img2Url = new URL(img2Url);
    }

    public Integer getCpdFlag() {
        return cpdFlag;
    }

    public void setCpdFlag(Integer cpdFlag) {
        this.cpdFlag = cpdFlag;
    }
}
