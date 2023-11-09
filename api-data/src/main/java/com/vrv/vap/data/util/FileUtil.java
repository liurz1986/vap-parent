package com.vrv.vap.data.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import java.io.*;

/**
 * @author lilang
 * @date 2022/3/14
 * @description
 */
public class FileUtil {

    public static void inputStream2File(InputStream is, File file) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int len = 0;
            byte[] buffer = new byte[8192];

            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } finally {
            os.close();
            is.close();
        }
    }

    public static FileItem createFileItem(File file, String fieldName) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem(fieldName, "text/plain", true, file.getName());
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     * 拷贝文件到新路径下
     *
     * @param oldPath
     *            原始文件路径
     * @param newPath
     *            拷贝新的文件路径
     * @throws Exception
     */
    public static void copyFile(String oldPath, String newPath)
            throws Exception {
        // int bytesum = 0;
        int byteread = 0;
        File oldfile = new File(oldPath);
        if (oldfile.exists()) { // 文件存在时
            InputStream inStream = new FileInputStream(oldPath); // 读入原文件
            FileOutputStream fs = new FileOutputStream(newPath);
            byte[] buffer = new byte[1444];
            while ((byteread = inStream.read(buffer)) != -1) {
                // bytesum += byteread; // 字节数 文件大小
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
            fs.close();
        }
    }
}
