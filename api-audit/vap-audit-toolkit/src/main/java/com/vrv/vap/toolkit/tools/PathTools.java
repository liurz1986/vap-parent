package com.vrv.vap.toolkit.tools;

import com.vrv.vap.toolkit.config.PathConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 文件夹工具
 *
 * @author xw
 * @date 2018年4月3日
 */
public final class PathTools {
    private static PathConfig pathConfig;

    /**
     * 服务启动时设置一次
     *
     * @PathConfig pathConfig
     */
    public static void setPathConfig(PathConfig pathConfig) {
        PathTools.pathConfig = pathConfig;
        createDir(getBaseDir());
        createDir(getUploadDir());
        createDir(getTemporaryDir());
        createDir(getExcelSavePath());
        createDir(getZipSavePath());
        createDir(getDataBackupDir());
    }

    /**
     * @String path
     */
    public static void createDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            log.info("创建目录: " + path);
            file.mkdirs();
        }
    }

    /**
     * 获取工程根目录
     *
     * @return
     */
    public static String getBaseDir() {
        return pathConfig.getBase();
    }

    /**
     * 获取上传文件保存目录
     *
     * @return
     */
    public static String getUploadDir() {
        return getBaseDir() + pathConfig.getUpload();
    }

    /**
     * 获取临时文件保存目录
     *
     * @return
     */
    public static String getTemporaryDir() {
        return getBaseDir() + pathConfig.getTmp();
    }

    /**
     * 获取文件保存地址
     *
     * @return
     */
    public static String getExcelPath(String filename) {
        return new StringBuilder().append(getExcelSavePath()).append(CommonTools.appendTime(filename)).append(".xls")
                .toString();
    }

    /**
     * 获取文件保存地址
     *
     * @return
     */
    public static String getZipPath(String filename) {
        return new StringBuilder().append(getZipSavePath()).append(CommonTools.appendTime(filename)).append(".zip")
                .toString();
    }

    /**
     * 获取文件保存地址
     *
     * @return
     */
    public static String getExcelSavePath() {
        return new StringBuilder().append(getTemporaryDir()).append("/excel/").toString();
    }

    /**
     * 获取文件保存地址
     *
     * @return
     */
    public static String getZipSavePath() {
        return new StringBuilder().append(getTemporaryDir()).append("/zip/").toString();
    }

    /**
     * 获取数据备份保存目录
     *
     * @return
     */
    public static String getDataBackupDir() {
        return getBaseDir() + pathConfig.getDataBackup();
    }

    private static Log log = LogFactory.getLog(PathTools.class);

    /**
     * 获取项目路径
     *
     * @return
     */
    public static String getBasePath() {
        String basePath = PathTools.class.getResource("/").getPath();
        return basePath;
    }

    /**
     * 获取文件输入流
     *
     * @String fileName
     * @return
     */
    public static InputStream getInputStrem(String fileName) {
        InputStream in = null;
        try {
            log.info("get " + fileName + " from [" + getBasePath() + "]");
            in = new FileInputStream(new File(getBasePath() + fileName));
        } catch (FileNotFoundException e) {
            log.error("", e);
        }
        return in;
    }

    // public static void main(String[] args) {
    // System.out.println(DirTools.getProjectDir());
    // }
}
