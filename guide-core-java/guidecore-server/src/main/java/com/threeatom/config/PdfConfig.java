package com.threeatom.config;

import com.threeatom.common.pdf.PdfServicePt;
import com.threeatom.common.pdf.impl.PdfServicePtImpl;
import com.threeatom.utils.FileUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class PdfConfig {

    @Bean(name = "pdfServicePt")
    public PdfServicePt pdfServicePt() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("pdf/Diploma.pdf");
        byte[] pdfBytes = this.inputStream2ByteArray(classPathResource.getPath());
        return new PdfServicePtImpl(pdfBytes);
    }

    private byte[] inputStream2ByteArray(String filePath) throws IOException {
        URL url = getClass().getClassLoader().getResource(filePath);
        if (url == null) {
            throw new IllegalArgumentException("The file path " + filePath + " could not be found.");
        }

        try (InputStream in = new FileInputStream(url.getFile())) {
            return FileUtil.toByteArray(in);
        }
    }
}
