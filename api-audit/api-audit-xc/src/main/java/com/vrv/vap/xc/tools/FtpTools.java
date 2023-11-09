package com.vrv.vap.xc.tools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.net.SocketException;

public class FtpTools {
    private Log log = LogFactory.getLog(FtpTools.class);

    /*  //主机地址  端口号
      private String HOSTNAME = "127.0.0.1";
      private Integer PORT = 21;
      //本地图片路径
      private String FilePackage = "F:\\Programming_Package\\IMG_3733_1.JPG";

      //用户名 密码
      private String USERNAME = "czy";
      private String PASSWORD = "czy";*/
    public FTPClient getFtp(String ftpHost, String ftpUserName,
                            String ftpPassword, int ftpPort) throws Exception {

        //创建一个FtpClient对象
        FTPClient ftpClient = new FTPClient();
        //创建ftp连接。默认是21端口
        ftpClient.connect(ftpHost, ftpPort);
        //登录ftp服务器，使用用户名和密码
        ftpClient.login(ftpUserName, ftpPassword);
        return ftpClient;

    }

    public void downloadFile(String localUrl) {
        FTPClient ftpClient = null;
        OutputStream os = null;
        try {
            ftpClient = getFtp("127.0.0.1", "ybq", "12345678", 21);
            ftpClient.setControlEncoding("UTF-8"); // 中文支持
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory("");
            File localFile = new File(localUrl);
            os = new FileOutputStream(localFile);
            ftpClient.retrieveFile("threat", os);
            ftpClient.logout();

        } catch (FileNotFoundException e) {
            log.error("没有找到threat文件", e);
        } catch (SocketException e) {
            log.error("连接FTP失败", e);
        } catch (IOException e) {
            log.error("文件读取错误", e);
        } catch (Exception e) {
            log.error("", e);
        }finally {
            if (os != null) {
                try{
                    os.close();
                }catch (IOException e){
                    log.error(e);
                }
            }
        }
    }
}
