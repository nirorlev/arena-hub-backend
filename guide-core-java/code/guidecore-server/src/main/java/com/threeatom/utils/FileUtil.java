package com.threeatom.utils;

import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.TableConstant;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@UtilityClass
@Slf4j
public class FileUtil {

    public static String getExtensionName(String filename) {
        if (!StringUtils.isEmpty(filename)) {
            int dotIndex = filename.lastIndexOf(46);
            if (dotIndex > -1 && dotIndex < filename.length() - 1) {
                return filename.substring(dotIndex);
            }
        }

        return filename;
    }

    public static void save(String filePath, String fileName, byte[] content) {
        File filedir = new File(filePath);
        if (!filedir.exists()) {
            filedir.mkdirs();
        }
        File file = new File(filedir, fileName);

        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(content, 0, content.length);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MultipartFile createFileItem(String url) throws Exception {
        HttpURLConnection conn = createConnection(url);
        String fileName = getFileName(conn);

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (InputStream is = conn.getInputStream();
                 BufferedInputStream bis = new BufferedInputStream(createConnection(url).getInputStream())) {

                String type = HttpURLConnection.guessContentTypeFromStream(bis);
                FileItemFactory factory = new DiskFileItemFactory(16, null);

                if (!fileName.contains(".") && type != null) {
                    fileName = fileName + ".vtt";
                }

                FileItem item =
                    factory.createItem(
                        "downloadFile",
                        TableConstant.sysFile_folder_guidecoreVedioCaption,
                        false,
                        fileName.replace(".srt", ".vtt"));
                writeToFile(item, is);

                if (item != null) {
                    return new CommonsMultipartFile(item);
                }
            }
        }
        return null;
    }

    private HttpURLConnection createConnection(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setReadTimeout(30000);
        conn.setConnectTimeout(30000);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        return conn;
    }

    private String getFileName(HttpURLConnection conn) {
        String newUrl = conn.getURL().getFile();
        if (newUrl != null || newUrl.isEmpty()) {
            newUrl = java.net.URLDecoder.decode(newUrl, StandardCharsets.UTF_8);
            int slashIndex = newUrl.indexOf('?');
            if (slashIndex > 0) {
                newUrl = newUrl.substring(0, slashIndex);
            }
            slashIndex = newUrl.lastIndexOf('/');
            return newUrl.substring(slashIndex + 1);
        }

        return "";
    }

    private void writeToFile(FileItem item, InputStream is) throws IOException {
        try (OutputStream outputStream = item.getOutputStream()) {
            int bytesRead;
            byte[] buffer = new byte[1024 * 4];
            while ((bytesRead = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    public static byte[] toByteArray(InputStream in) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024 * 4];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        }
    }

    public byte[] retrieveFileFromUrl(String fileUrl) throws SystemException {
        try (
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(new HttpGet(fileUrl))
        ) {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                String errMessage = "Failed to download file. Null entity found for url requested: " + fileUrl;
                log.error(errMessage);
                throw new SystemException(errMessage);
            }
            try (InputStream inputStream = entity.getContent()) {
                return IOUtils.toByteArray(inputStream);
            }
        } catch (IOException e) {
            String errMessage = "Failed to retrieve file from URL: " + fileUrl;
            log.error(errMessage, e);
            throw new SystemException(errMessage);
        }
    }
}