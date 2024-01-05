package com.threeatom.common.pdf.impl;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.itextpdf.text.pdf.*;
import com.threeatom.guidecore.constant.TableConstant;
import org.apache.commons.io.IOUtils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.threeatom.common.pdf.PdfModel;
import com.threeatom.common.pdf.PdfService;

import javax.imageio.ImageIO;

public class PdfServiceImpl implements PdfService {

    private final byte[] pdfByte;

    public PdfServiceImpl(byte[] pdfByte) {
        this.pdfByte = pdfByte;
    }

    @Override
    public ByteArrayOutputStream getPdfBytes(PdfModel model) throws IOException {
        

        PdfReader reader;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            if(Objects.isNull(model.getCpdFlag()) || !model.getCpdFlag().equals(TableConstant.COMMON_ONE)){
                if(Objects.nonNull(model.getImg1Url())){
                    reader = new PdfReader("pdf/nocpdNoImgsample.pdf");
                }else {
                    reader = new PdfReader("pdf/nocpdsample.pdf");
                }

            }else {
                if(Objects.nonNull(model.getImg1Url())){
                    reader = new PdfReader("pdf/noImgSample.pdf");
                }else {
                    reader = new PdfReader("pdf/sample.pdf");
                }
            }

            bos = new ByteArrayOutputStream();
            PdfStamper stamper = new PdfStamper(reader, bos);

            AcroFields form = stamper.getAcroFields();


            Map<String, Object> map = new HashMap<>();

            map.put("userName", model.getUserName());
            map.put("courseName", model.getCourseName());
//            map.put("menNo", model.getMenNo());
            map.put("completionDate", model.getCompletionDate());
            map.put("number", model.getNumber());
            map.put("courseTotalTime",model.getCourseTotalTime());
//            map.put("img1Url",model.getImg1Url());
/*            BaseFont unicode =
                    BaseFont.createFont("e:/font/arialuni.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            form.addSubstitutionFont(unicode);*/
            fillPdfCellForm(map, form);




            /*
             * 设置图片
            byte[] image1 = IOUtils.toByteArray(model.getImg1Url());
            byte[] image2 = IOUtils.toByteArray(model.getImg2Url());
            //添加图片
            addImage(image1, stamper, form, "img1Url");
            addImage(image2, stamper, form, "img2Url");
*/
//            byte[] image1 = IOUtils.toByteArray(reader,"");
//            PdfContentByte over = stamper.getOverContent(1);
//            Image image = Image.getInstance(model.getImg1Url());
//            image.setAbsolutePosition(1000,3150);
//            image.scaleAbsolute(490,200);
//            over.saveState();
//            over.addImage(image);
//            over.restoreState();
            //添加图片

            if(Objects.nonNull(model.getImg1Url())) {
                byte[] image1 = IOUtils.toByteArray(model.getImg1Url());
                addImage(image1, stamper, form, "img1Url", model.getImg1Url());
            }

            for(Map.Entry entry : map.entrySet()){
                if(entry.getKey().equals("courseName") || entry.getKey().equals("userName")) {
                    String imgUrl = "";
                    if (entry.getKey().equals("userName") && Objects.nonNull(entry.getValue())) {
                        imgUrl = "img1Url2";
                    } else if (entry.getKey().equals("courseName") && Objects.nonNull(entry.getValue())) {
                        imgUrl = "img1Url1";
                    }
                    String[] strArr = entry.getValue().toString().split(",");
                    Font font = new Font("Microsoft Sans Serif", Font.PLAIN, 50); // 字体大小
                    int image_height = 80; // 每张图片的高度
                    int line_height = 40; // 每行或者每个文字的高度
                    int width = 40 * entry.getValue().toString().length();

                    String filePath = "E:\\font\\d.png";
                    File outFile = new File(filePath);
                    // 创建图片
                    BufferedImage image = new BufferedImage(width, image_height, BufferedImage.TYPE_USHORT_565_RGB);
                    Graphics2D g = image.createGraphics();
                    FontMetrics metrics = g.getFontMetrics(font);
                    int stringWidth = metrics.stringWidth(entry.getValue().toString());
                    int x = (width - stringWidth) / 2;
                    int y = (image_height - metrics.getHeight()) / 2 + metrics.getAscent();


                    g.setClip(0, 0, width, image_height);
                    g.setColor(Color.white); // 背景色白色
                    g.fillRect(0, 0, width, image_height);
                    g.setColor(Color.black);//  字体颜色红色
                    g.setFont(font);// 设置画笔字体
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB); // 抗锯齿
                    // 画布
                    g.drawString(strArr[0], x, y);
                    g.dispose(); // 释放资源
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", os);// 输出png图片
                    InputStream input = new ByteArrayInputStream(os.toByteArray());
                    int n;
                    byte[] buffer = new byte[4096];
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    while (-1 != (n = input.read(buffer))) {
                        output.write(buffer, 0, n);
                    }
                    com.itextpdf.text.Image image1 = com.itextpdf.text.Image.getInstance(output.toByteArray());
                    if (Objects.nonNull(image)) {
//                byte[] image1 = IOUtils.toByteArray(image.getUrl());
                        addImage(os.toByteArray(), stamper, form, imgUrl, null);
                    }
                }


            }



            
            // true代表生成的PDF文件不可编辑
            stamper.setFormFlattening(true);
            stamper.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }

//        FileOutputStream fos = new FileOutputStream("e:/font/shouju_fb.pdf");
//        fos.write(bos.toByteArray());
        
        return bos;
    }

    private static void fillPdfCellForm(Map<String, Object> map, AcroFields form) throws IOException, DocumentException {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
//            String value = "جائزة في التعليم والتدريب المستو";
            form.setField(key, value);
        }
    }

//    private Image arabicImage(String test) throws IOException, DocumentException {
//        String str = test;
//
//        return image1;
//    }

    private void addImage(byte[] jpgBytes, PdfStamper stamper, AcroFields form, String textId, URL url) throws IOException, DocumentException
    {
        int pageNo = form.getFieldPositions(textId).get(0).page;
        Rectangle signRect = form.getFieldPositions(textId).get(0).position;
        float x = signRect.getLeft();
        float y = signRect.getBottom();
        Image realImage = Image.getInstance(jpgBytes);
//        Image realImage = Image.getInstance(url);

        float imageHeight = realImage.getHeight();
        float imageWidth = realImage.getWidth();
        float imageRate = imageHeight / imageWidth;

        float signRectHeight = signRect.getHeight();
        float signRectWidth = signRect.getWidth();
        float signRate = signRectHeight / signRectWidth;

        float realWidth;
        float realHeight;
        float realX;
        float realY;

        if (imageRate >= signRate) {
            realHeight = signRectHeight;
            realWidth = imageWidth * (signRectHeight / imageHeight);
            realX = x + (signRectWidth - realWidth) / 2;
            realY = y;
        } else {
            realWidth = signRectWidth;
            realHeight = imageHeight * (signRectWidth / imageWidth);
            realY = y + (signRectHeight - realHeight) / 2;
            realX = x;
        }

        PdfContentByte under = stamper.getOverContent(pageNo);
        //设置图片大小
        realImage.scaleAbsolute(realWidth, realHeight);

        //设置图片位置
        realImage.setAbsolutePosition(realX, realY);
        under.saveState();
        under.addImage(realImage);
        under.restoreState();
    }
}
