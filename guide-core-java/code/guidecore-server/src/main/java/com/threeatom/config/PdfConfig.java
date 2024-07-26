package com.threeatom.config;

import com.threeatom.common.pdf.PdfServicePt;
import com.threeatom.common.pdf.impl.PdfServicePtImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Configuration
public class PdfConfig {

    @Bean(name = "pdfServicePt")
    public PdfServicePt pdfServicePt() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("pdf/Diploma.pdf");
        byte[] pdfBytes = this.inputStream2ByteArray(classPathResource.getPath());
        return new PdfServicePtImpl(pdfBytes);
    }

    private byte[] inputStream2ByteArray(String filePath) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(filePath);
        if (url == null) {
            throw new IllegalArgumentException("");
        }
        InputStream in = new FileInputStream(url.getFile());
        byte[] data = this.toByteArray(in);
        in.close();

        return data;
    }

    private byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }
}
