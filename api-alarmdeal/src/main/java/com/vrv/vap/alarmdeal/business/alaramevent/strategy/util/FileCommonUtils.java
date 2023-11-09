package com.vrv.vap.alarmdeal.business.alaramevent.strategy.util;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年04月11日 14:57
 */
public class FileCommonUtils {

    private static Logger logger = Logger.getLogger(FileCommonUtils.class);
    /**
     *  <h1>获取指定文件夹下所有文件，含文件夹</h1>
     * @param dirFilePath 文件夹路径
     * @return
     */
    public static List<File> getAllFile(String dirFilePath){
        if(StringUtils.isBlank(dirFilePath)){
            return null;
        }
        return getAllFile(new File(dirFilePath));
    }

    /**
     *  <h1>获取指定文件夹下所有文件，不含文件夹</h1>
     * @param dirFile 文件夹
     * @return
     */
    public static List<File> getAllFile(File dirFile){
        // 如果文件夹不存在或着不是文件夹，则返回 null
        if(Objects.isNull(dirFile) || !dirFile.exists() || dirFile.isFile()) {
            return null;
        }
        File[] childrenFiles =  dirFile.listFiles();
        if(Objects.isNull(childrenFiles) || childrenFiles.length == 0) {
            return null;
        }
        List<File> files = new ArrayList<>();
        for(File childFile : childrenFiles) {

            // 如果时文件，直接添加到结果集合
            if(childFile.isFile()) {
                files.add(childFile);
            }else {
                // 如果是文件夹。则先将其添加到结果集合，再将其内部文件添加进结果集合。
                files.add(childFile);
                List<File> cFiles =  getAllFile(childFile);
                if(Objects.isNull(cFiles) || cFiles.isEmpty()){ continue;}
                files.addAll(cFiles);
            }
        }
        return files;
    }

    /**
     * 通过NIO的channel完成文件压缩的工作（大文件压缩，速度比较文件流传输要快近5倍）
     * @param sourceFilePath
     * @param zipFilePath
     */
    public static void zipFileByChannel(String sourceFilePath,String zipFilePath) {
        ZipOutputStream zipOut = null;
        WritableByteChannel zipOutChannel = null;
        try {
            zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath));
            zipOutChannel = Channels.newChannel(zipOut);
            File  inputFile = new File(sourceFilePath);  //输入文件
            zip(inputFile.getName(), inputFile, zipOut, zipOutChannel);
        } catch (FileNotFoundException e) {
            logger.error("创建文件找不到",e);
        }finally {
            try {
                if(zipOut!=null) {
                    zipOut.close();
                }

                if(zipOutChannel!=null) {
                    zipOutChannel.close();
                }

            } catch (IOException e) {
                logger.error("文件关闭出现问题，请检查！",e);
            }
        }
    }

    /**
     * 通过NIO的压缩方法
     * @param base
     * @param inputFile
     * @param zipOut
     * @param zipOutChannel
     */
    private static void zip(String base,File inputFile,ZipOutputStream zipOut,WritableByteChannel zipOutChannel) { //压缩方法
        try {
            if(inputFile.isDirectory()) {  //目录文件
                File[] listFiles = inputFile.listFiles();
                if(listFiles.length==0) {
                    zipOut.putNextEntry(new ZipEntry(base + File.separator)); // 创建zip压缩进入点base
                }else {
                    for (File file : listFiles) {
                        zip(base+File.separator+file.getName(), file, zipOut, zipOutChannel);
                    }
                }
            }else {
                FileChannel fileChannel = new FileInputStream(inputFile).getChannel();
                zipOut.putNextEntry(new ZipEntry(base));  //创建zip压缩进入base路径下
                fileChannel.transferTo(0, inputFile.length(), zipOutChannel);
            }
        } catch (FileNotFoundException e) {
            logger.error("对应文件没有找到:{},请检查！", e);
        }catch (IOException e) {
            logger.error("文件I/O输出异常:{},请检查！", e);
        }
    }

    public static void compressZip(String dstFile,String sourceFile,String password){
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
        // Below line is optional. AES 256 is used by default. You can override it to use AES 128. AES 192 is supported only for extracting.
        zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        ZipFile zipFile = new ZipFile(dstFile, password.toCharArray());
        try {
            if(StringUtils.isNotBlank(password)){
                zipFile.addFolder(new File(sourceFile),zipParameters);
            }else{
                zipFile.addFolder(new File(sourceFile));
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static void compressZip(String dstFile,String sourceFile){
        compressZip(dstFile, sourceFile,null);
    }

    public static void decompressionZip(String sourceFile,String dstPath,String password){
        try {
            new ZipFile(new File(sourceFile), password.toCharArray()).extractAll(dstPath);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static void decompressionZip(String sourceFile,String dstPath){
        try {
            new ZipFile(new File(sourceFile)).extractAll(dstPath);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
}
