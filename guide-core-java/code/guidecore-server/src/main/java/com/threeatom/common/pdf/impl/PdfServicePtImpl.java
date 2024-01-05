package com.threeatom.common.pdf.impl;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.threeatom.common.pdf.PdfModel;
import com.threeatom.common.pdf.PdfServicePt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @title: PdfServicePtImpl
 * @projectName jeeplus-core
 * @description: TODO
 * @date 2022/8/22/02214:57
 */
public class PdfServicePtImpl implements PdfServicePt {

    private final byte[] pdfByte;

    public PdfServicePtImpl(byte[] pdfByte) {
        this.pdfByte = pdfByte;
    }

    @Override
    public ByteArrayOutputStream getPdfBytes(PdfModel model) throws IOException {


        PdfReader reader;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            reader = new PdfReader(pdfByte);
            bos = new ByteArrayOutputStream();
            PdfStamper stamper = new PdfStamper(reader, bos);

            AcroFields form = stamper.getAcroFields();
            Map<String, String> map = new HashMap<>();

            map.put("userName", model.getUserName());
            map.put("courseName", model.getCourseName());
            map.put("completionDate", model.getCompletionDate());
            map.put("number", model.getNumber());
            map.put("courseTotalTime",model.getCourseTotalTime());
            map.put("context",model.getContext());
            fillPdfCellForm(map, form);

            /*
             * 设置图片
            byte[] image1 = IOUtils.toByteArray(model.getImg1Url());
            byte[] image2 = IOUtils.toByteArray(model.getImg2Url());
            //添加图片
            addImage(image1, stamper, form, "img1Url");
            addImage(image2, stamper, form, "img2Url");
*/


            // true代表生成的PDF文件不可编辑
            stamper.setFormFlattening(true);
            stamper.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }

//        FileOutputStream fos = new FileOutputStream("/Users/kylewong/Desktop/shouju_fb.pdf");
//        fos.write(bos.toByteArray());

        return bos;
    }

    private static void fillPdfCellForm(Map<String, String> map, AcroFields form) throws IOException, DocumentException {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            form.setField(key, value);
        }
    }

    private void addImage(byte[] jpgBytes, PdfStamper stamper, AcroFields form, String textId) throws IOException, DocumentException
    {
        int pageNo = form.getFieldPositions(textId).get(0).page;
        Rectangle signRect = form.getFieldPositions(textId).get(0).position;
        float x = signRect.getLeft();
        float y = signRect.getBottom();
        Image image = Image.getInstance(jpgBytes);

        float imageHeight = image.getHeight();
        float imageWidth = image.getWidth();
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
        image.scaleAbsolute(realWidth, realHeight);

        //设置图片位置
        image.setAbsolutePosition(realX, realY);
        under.addImage(image);
    }
}
