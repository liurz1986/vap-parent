package com.vrv.vap.xc.tools;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileToZip {
    private Log log = LogFactory.getLog(FileToZip.class);

    public FileToZip() {
    }

    /**
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
     *
     * @param sourceFilePathList :待压缩的文件路径
     * @param zipFilePath        :压缩后存放路径
     * @param fileName           :压缩后文件的名称
     * @return
     */
    public void fileToZip(String sourceFilePathList, String zipFilePath, String fileName) {
        try {
            //创建压缩文件
            ZipFile zipFile = new ZipFile(zipFilePath + "/" + fileName + ".zip");
           /* ArrayList<File> files = new ArrayList<>();
            for(String str: sourceFilePathList) {
                files.add(new File(str));
            }*/
            String folderToAdd = sourceFilePathList;
            //设置压缩文件参数
            ZipParameters parameters = new ZipParameters();
            //设置压缩方法
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

            //设置压缩级别
            //DEFLATE_LEVEL_FASTEST     - Lowest compression level but higher speed of compression
            //DEFLATE_LEVEL_FAST        - Low compression level but higher speed of compression
            //DEFLATE_LEVEL_NORMAL  - Optimal balance between compression level/speed
            //DEFLATE_LEVEL_MAXIMUM     - High compression level with a compromise of speed
            //DEFLATE_LEVEL_ULTRA       - Highest compression level but low speed
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            //设置压缩文件加密
            parameters.setEncryptFiles(true);

            //设置加密方法
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);

            //设置aes加密强度
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);

            //设置密码
            parameters.setPassword("wzx");

            //添加文件到压缩文件
            zipFile.addFolder(folderToAdd, parameters);
        } catch (ZipException e) {
            log.error("", e);
        }
    }

    public static void main(String[] args) {
        FileToZip fileToZip = new FileToZip();
        String sourceFilePath = "F:\\123";
      /*  List<String> list = new ArrayList<String>();
        list.add("F:\\123\\features.txt");
        list.add("F:\\123\\info.txt");
        list.add("F:\\123\\version.txt");*/
        String zipFilePath = "F:\\456";
        String fileName = "12700153file";
        fileToZip.fileToZip(sourceFilePath, zipFilePath, fileName);
    }
}
