package com.vrv.vap.admin.common.util;

import com.vrv.vap.admin.web.DbBackupController;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Paths;

public class BackupUtil {

    private static Logger logger = LoggerFactory.getLogger(BackupUtil.class);

    public static String createBackZip(String tmpPath,String name,String pwd) {
        //String nacosBakPath = Paths.get(tmpPath,name,"nacos.zip").toString();
        String sysBakPath = Paths.get(tmpPath,name,"sys").toString();
        String zipPath = Paths.get(tmpPath,name+".vapbak").toString();
        try {
            // 生成的压缩文件
            net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(zipPath);
            ZipParameters parameters = new ZipParameters();
            // 压缩方式
            parameters.setCompressionMethod(CompressionMethod.DEFLATE);
            // 压缩级别
            parameters.setCompressionLevel(CompressionLevel.NORMAL);
            // 是否设置加密文件
            parameters.setEncryptFiles(true);
            // 设置加密算法
            parameters.setEncryptionMethod(EncryptionMethod.AES);
            // 设置AES加密密钥的密钥强度
            parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
            // 设置密码
            if(StringUtils.isNotEmpty(pwd)) {
                zipFile.setPassword(pwd.toCharArray());
            }
            // 要打包的文件夹
            //zipFile.addFile(nacosBakPath, parameters);
            zipFile.addFile(sysBakPath, parameters);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
        return zipPath;
    }

    public static String createBakFile(String tmpPath,String name,String typeName,String bakJson){
        String bakFilePath = Paths.get(tmpPath,name,typeName).toString();
        String bakFoldPath = Paths.get(tmpPath,name).toString();
        File tmpFile = new File(bakFoldPath);
        if(!tmpFile.exists()){
            tmpFile.mkdirs();
        }
        //将数据写入.json文件--start
        try (FileOutputStream fos = new FileOutputStream(new File(bakFilePath),false);
             OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
             BufferedWriter writer = new BufferedWriter(osw)) {
            writer.write(bakJson);
            return bakFilePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decompressFile(String zipFileFullName,String tmpPath,String typeName,String password){
        File file = new File(zipFileFullName);
        String name = file.getName().replace(".vapbak","");
        String folderPath = Paths.get(tmpPath,name).toString();
        String typeFile = Paths.get(tmpPath,name,typeName).toString();
        file = new File(typeFile);
        if(!file.exists()){
            unZipFile(zipFileFullName,folderPath,password);
        }
        return readToString(typeFile);
    }


    public static String getFolderPath(String zipFileFullName,String tmpPath){
        File file = new File(zipFileFullName);
        String name = file.getName().replace(".vapbak","");
        String folderPath = Paths.get(tmpPath,name).toString();
        return folderPath;
    }

    public static String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try (FileInputStream in = new FileInputStream(file);) {
            in.read(filecontent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }
    public static boolean unZipFile(String zipFileFullName, String filePath, String password) {
        try {
            ZipFile zipFile = new ZipFile(zipFileFullName);
            // 如果解压需要密码
            if(StringUtils.isNotEmpty(password)&&zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(filePath);
            return true;
        } catch (  ZipException e) {
            e.printStackTrace();
            logger.error("解压文件【"+zipFileFullName+"】到路径【"+filePath+"】失败：\n"+e.getMessage());
            return false;
        }
    }


    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName：要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName：要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir：要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }

}
