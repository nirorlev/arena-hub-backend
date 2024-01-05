//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.utils;

import com.threeatom.guidecore.constant.TableConstant;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtil {
    public FileUtil() {
    }

    public static String getExtensionName(String filename) {
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf(46);
            if (dot > -1 && dot < filename.length() - 1) {
                return filename.substring(dot);
            }
        }

        return filename;
    }
    public static byte[] read(String filePath) throws IOException {

        InputStream in = new FileInputStream(filePath);
        byte[] data = inputStream2ByteArray(in);
        in.close();

        return data;
    }

    /**
     * 流转二进制数组
     *
     * @param in
     * @return
     * @throws IOException
     */
    static byte[] inputStream2ByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    /**
     * 保存文件
     *
     * @param filePath
     * @param fileName
     * @param content
     */
    public static void save(String filePath, String fileName, byte[] content) {
        try {
            File filedir = new File(filePath);
            if (!filedir.exists()) {
                filedir.mkdirs();
            }
            File file = new File(filedir, fileName);
            OutputStream os = new FileOutputStream(file);
            os.write(content, 0, content.length);
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MultipartFile createFileItem(String url) throws Exception{
        FileItem item = null;
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setReadTimeout(30000);
        conn.setConnectTimeout(30000);

        // 设置应用程序要从网络连接读取数据
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        String fileName = "";

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream();
            //获取文件名称
            String newUrl = conn.getURL().getFile();
            if (newUrl != null || newUrl.length() <= 0) {
                newUrl = java.net.URLDecoder.decode(newUrl, "UTF-8");
                int pos = newUrl.indexOf('?');
                if (pos > 0) {
                    newUrl = newUrl.substring(0, pos);
                }
                pos = newUrl.lastIndexOf('/');
                fileName = newUrl.substring(pos + 1);
            }
            //此处获取两次，是因为如果只获取一次的话，获取type类型时，文件大小会损坏变小，所以重新获取一次
            BufferedInputStream bis = null;
            HttpURLConnection conn1 = (HttpURLConnection) new URL(url).openConnection();
            bis = new BufferedInputStream(conn1.getInputStream());
            String type = HttpURLConnection.guessContentTypeFromStream(bis);
            FileItemFactory factory = new DiskFileItemFactory(16, null);
            String textFieldName = "downloadFile";  //此处任务取值
            if(!fileName.contains(".") && type != null){
                fileName = fileName + ".vtt";
            }
            item = factory.createItem(textFieldName, TableConstant.sysFile_folder_guidecoreVedioCaption, false, fileName.replace(".srt",".vtt"));
            OutputStream os = item.getOutputStream();

            int bytesRead;
            byte[] buffer = new byte[1024 * conn.getContentLength()];
            while ((bytesRead = is.read(buffer, 0, buffer.length)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            is.close();
        }
        if (item != null) {
            return new CommonsMultipartFile(item);
        }
        return null;
    }

    public static byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }
}
