package com.vrv.vap.alarmdeal.business.model.util;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

public class ZipFileUtil {
    private static Logger logger = LoggerFactory.getLogger(ZipFileUtil.class);
    private static final String DEFAULT_ENCODING = "GBK";

    /**
     * @param file zip压缩文件
     * @return void
     * @Author zhang
     * @brief 检测file是否为zip压缩包
     */
    public static boolean isZipFile(File file) {
        boolean flag = false;
        if (file.exists() && file.getName().endsWith(".zip")) {
            flag = true;
        }
        return flag;
    }

    /**
     * zip包解压到指定目录
     *
     * @param file     zip压缩包
     * @param destDir  解压缩目录
     * @param encoding 编码
     * @return boolean
     * @Author zhang
     * @brief 解压缩zip
     */
    public static boolean unzip(File file, String destDir, String encoding) throws Exception {
        if (!file.exists()) {
            throw new IllegalArgumentException("请检查文件" + file.getName() + "是否存在");
        }

        File destFile = new File(destDir);
        if (!destFile.exists()) {
            destFile.mkdir();
        }
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(file);
            zipFile.setCharset(Charset.forName(encoding));
            zipFile.extractAll(destDir);
        } catch (Exception e) {
            throw new Exception("文件解压缩失败!");
        }
        return true;
    }

    /**
     * 使用给定密码解压zip文件解压到当前目录
     *
     * @param zipPath 指定的ZIP压缩文件
     * @param passwd  ZIP文件的密码
     * @return 解压后文件数组
     * @throws ZipException 压缩文件有损坏或者解压缩失败抛出
     * @Author zhang
     */
    public static void unzipPassword(String zipPath, String passwd) throws ZipException {
        File zipFile = new File(zipPath);
        File parentDir = zipFile.getParentFile();
        unzipPassword(zipFile, parentDir.getAbsolutePath(), passwd);
    }

    /**
     * 使用给定密码解压zip文件解压到指定目录
     *
     * @param zip    指定的ZIP压缩文件
     * @param dest   解压目录
     * @param passwd ZIP文件的密码
     * @return 解压后文件数组
     * @throws ZipException 压缩文件有损坏或者解压缩失败抛出
     * @Author zhang
     */
    public static void unzipPassword(String zip, String dest, String passwd) throws ZipException {
        File zipFile = new File(zip);
        unzipPassword(zipFile, dest, passwd);
    }

    /**
     * 使用给定密码解压zip文件解压到指定目录
     *
     * @param zipFile 指定的ZIP压缩文件
     * @param dest    解压目录
     * @param passwd  ZIP文件的密码
     * @return 解压后文件数组
     * @throws ZipException 压缩文件有损坏或者解压缩失败抛出
     * @Author zhang
     */
    public static void unzipPassword(File zipFile, String dest, String passwd) throws ZipException {
        ZipFile zFile = new ZipFile(zipFile);
        zFile.setCharset(Charset.forName(DEFAULT_ENCODING));
        if (!zFile.isValidZipFile()) {
            throw new ZipException("压缩文件不合法,可能被损坏");
        }
        File destDir = new File(dest);
        if (destDir.isDirectory() && !destDir.exists()) {
                destDir.mkdir();
        }
        // 有密码的话，判断文件是不是加密；没有密码的话不判断文件是否加密
        if(StringUtils.isNotEmpty(passwd)){
            // 检查文件是否加密
            if (zFile.isEncrypted()) { // 检查是否需要密码
                zFile.setPassword(passwd.toCharArray());
                zFile.extractAll(dest);
            }else{
                throw new ZipException("文件没有加密！");
            }
        }
        return;
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(String oldPath, String newPath) throws ZipException {
        try {
            File newFilePath = new File(newPath);  //如果文件夹不存在 则建立新文件夹
            if (!newFilePath.exists()) {
                newFilePath.mkdirs();
            }
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            logger.error("复制整个文件夹内容操作出错", e);
            throw new ZipException("复制整个文件夹内容操作出错!");
        }
    }
}
