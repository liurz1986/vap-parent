package com.vrv.vap.xc.client;

import com.vrv.vap.xc.config.FastDfsConfig;
import org.apache.commons.lang.StringUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.zip.ZipOutputStream;

/**
 * @author zhangkai
 * @ClassName FastDFSUtils
 * @Description FastDFS工具类
 * @date 2017年7月18日
 */
@Component
public class FastDFSUtils implements Serializable {

    private static final long serialVersionUID = -4462272673174266738L;
    private static TrackerClient trackerClient;
    private static TrackerServer trackerServer;
    private static StorageClient1 storageClient1;
    private static final Logger log = LoggerFactory.getLogger(FastDFSUtils.class);


    public static void initClient(FastDfsConfig config) {
        //clientGloble配置
        if (StringUtils.isNotEmpty(config.getTrackerServers())) {
            try {
                Properties props = new Properties();
                props.setProperty("fastdfs.tracker_servers", config.getTrackerServers());
                props.setProperty("fastdfs.connect_timeout_in_seconds", config.getConnectTimeout());
                props.setProperty("fastdfs.network_timeout_in_seconds", config.getNetworkTimeout());
                props.setProperty("fastdfs.charset", config.getCharset());
                props.setProperty("fastdfs.http_anti_steal_token", config.getStealToken());
                props.setProperty("fastdfs.http_secret_key", config.getSecretKey());
                props.setProperty("fastdfs.http_tracker_http_port", config.getHttpPort());

                ClientGlobal.initByProperties(props);
                //trackerclient
                trackerClient = new TrackerClient();
                trackerServer = trackerClient.getConnection();
                //storageclient
                storageClient1 = new StorageClient1(trackerServer, null);
                ProtoCommon.activeTest(trackerServer.getSocket());
            } catch (Exception e) {
                log.error("init fdfs client error: ", e);
            }
        } else {
            log.error("FastDfsConig is empty!!!");
        }
    }
    /*private static String getFilePath(String filename) throws IOException {
        String name = filename;

        String basePath = (FastDFSUtils.class.getResource("/") + "").replace("file:", "");

        File file = new File(basePath + name);

        if (file.exists()) {
            return basePath+ name;
        } else {
            InputStream in = FastDFSUtils.class.getClassLoader().getResourceAsStream(name);
            if (null != in) {
               return FastDFSUtils.class.getClassLoader().getResource(name).getPath();

            } else {
                in = ClassLoader.getSystemResourceAsStream(filename);
                if (null != in) {
                    ClassPathResource resource = new ClassPathResource(name);
                    return resource.getClassLoader().getResource(name).getPath();
                } else {
                    log.error("未找到配置文件:" + name);
                }
            }
        }

        return "";
    }*/

    public static String uploadFile(byte[] fileBuff, String file_ext_name) throws Exception {
        String fileExtName = "";
        if (file_ext_name.contains(".")) {
            fileExtName = file_ext_name.substring(file_ext_name.lastIndexOf(".") + 1);
        }
        NameValuePair[] metaList = new NameValuePair[2];
        metaList[0] = new NameValuePair("fileName", file_ext_name);
        metaList[1] = new NameValuePair("fileExtName", fileExtName);

        String result = storageClient1.upload_file1(fileBuff, fileExtName, null);

        return result;
    }

    public static String uploadFile(String localFilename, String fileExtName) throws Exception {


        String result = storageClient1.upload_file1(localFilename, fileExtName, null);

        return result;
    }

    /**
     * Upload File to DFS, directly transferring java.io.InputStream to java.io.OutStream
     *
     * @param
     * @param uploadFileName, the name of the file.
     * @param fileLength,     the length of the file.
     * @return the file ID in DFS.
     * @throws IOException
     * @author Poechant
     * @email zhongchao.ustc@gmail.com
     */
    public static String uploadFileByStream(InputStream inStream, String uploadFileName, long fileLength) throws IOException {

        String[] results = null;
        String fileExtName;
        if (uploadFileName.contains(".")) {
            fileExtName = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
        } else {
            return null;
        }


        NameValuePair[] metaList = new NameValuePair[3];
        metaList[0] = new NameValuePair("fileName", uploadFileName);
        metaList[1] = new NameValuePair("fileExtName", fileExtName);
        metaList[2] = new NameValuePair("fileLength", String.valueOf(fileLength));

        try {
            // results[0]: groupName, results[1]: remoteFilename.
            results = storageClient1.upload_file(null, fileLength, new UploadFileSender(inStream), fileExtName, metaList);

        } catch (Exception e) {
            log.error("uploadFileByStream ", e);
        }

        trackerServer.close();
        return results != null ? results[0] + "/" + results[1] : null;
    }


    public static String getSecretKey() {
        return ClientGlobal.getG_secret_key();
    }

    /**
     * 获取访问服务器的token，拼接到地址后面
     *
     * @param filepath      文件路径 group1/M00/00/00/wKgzgFnkTPyAIAUGAAEoRmXZPp876.jpeg
     * @param httpSecretKey 密钥
     * @return 返回token，如： token=078d370098b03e9020b82c829c205e1f&ts=1508141521
     */
    public static String getToken(String filepath, String httpSecretKey) {
        // unix seconds
        //Instant.now().getEpochSecond();
        long epoch = System.currentTimeMillis() / 1000L;
        int ts = (int) epoch;
        // token
        String fileName = filepath.substring(filepath.indexOf("/") + 1);
        String token = "null";
        try {
            token = ProtoCommon.getToken(fileName, ts, httpSecretKey);
        } catch (UnsupportedEncodingException e) {
            log.error("getToken  UnsupportedEncodingException", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("getToken NoSuchAlgorithmException", e);
        } catch (MyException e) {
            log.error("getToken MyException", e);
        }
        return "token=" + token + "&ts=" + ts;
    }

    /**
     * fastDFS文件下载
     *
     * @param groupName      组名
     * @param remoteFileName 文件名
     * @param specFileName   真实文件名
     * @return ResponseEntity<byte [ ]>
     */
    public static ResponseEntity<byte[]> downloadFile(String groupName, String remoteFileName, String specFileName) {
        byte[] content = null;
        HttpHeaders headers = new HttpHeaders();
        try {
            content = storageClient1.download_file(groupName, remoteFileName);
            headers.setContentDispositionFormData("attachment", new String(specFileName.getBytes("UTF-8"), "iso-8859-1"));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        } catch (Exception e) {
            log.error("downloadFile 201", e);
        }
        return new ResponseEntity<>(content, headers, HttpStatus.CREATED);
    }

    public static void downloadFileWithCompress(String fileId, ZipOutputStream out) {
        boolean activeTest;
        try {
            activeTest = ProtoCommon.activeTest(trackerServer.getSocket());
        } catch (IOException e) {
            log.error("activeTest Error ======>", e);
            activeTest = false;
        }
        try {
            if (!activeTest) {
                log.info("======= trackerServer createing ======");
                trackerServer = trackerClient.getConnection();
                StorageServer storageServer = null;
                storageClient1 = new StorageClient1(trackerServer, storageServer);
            }
            storageClient1.download_file1(fileId, new FileDownLoad(out));
        } catch (Exception e) {
            log.error("downloadFileWithCompress 214", e);
        }
    }

    public static void downloadFileWithCompress(String fileId, OutputStream out) {
        try {
            ProtoCommon.activeTest(trackerServer.getSocket());
            storageClient1.download_file1(fileId, new FileDownLoad2(out));
        } catch (Exception e) {
            log.error("downloadFileWithCompress 223", e);
        }
    }

    /**
     * 根据fastDFS返回的path得到文件的组名
     *
     * @param path fastDFS返回的path
     * @return
     */
    public static String getGroupFormFilePath(String path) {
        return path.split("/")[0];
    }

    /**
     * 根据fastDFS返回的path得到文件名
     *
     * @param path fastDFS返回的path
     * @return
     */
    public static String getFileNameFormFilePath(String path) {
        return path.substring(path.indexOf("/") + 1);
    }


    public static void deleteFile(String fileId) {

        try {
            storageClient1.delete_file1(fileId);

        } catch (Exception e) {
            log.error("deleteFile", e);
        }
    }


    private static class UploadFileSender implements UploadCallback {

        private InputStream inStream;

        public UploadFileSender(InputStream inStream) {
            this.inStream = inStream;
        }

        @Override
        public int send(OutputStream out) throws IOException {
//            int readBytes;
//            while((readBytes = inStream.read()) > 0) {
//                out.write(readBytes);
//            }
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inStream.read(buffer, 0, 8192)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            //  out.close();
            inStream.close();
            return 0;
        }
    }


    private static class FileDownLoad implements DownloadCallback {
        private ZipOutputStream out;

        public FileDownLoad(ZipOutputStream out) {
            this.out = out;
        }

        @Override
        public int recv(long file_size, byte[] data, int bytes) {
            if (out != null) {
                try {
                    out.write(data, 0, bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    return -1;
                }
            } else {
                return -1;
            }
            return 0;
        }
    }

    private static class FileDownLoad2 implements DownloadCallback {
        private OutputStream out;

        public FileDownLoad2(OutputStream out) {
            this.out = out;
        }

        @Override
        public int recv(long file_size, byte[] data, int bytes) {
            if (out != null) {
                try {
                    out.write(data, 0, bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    return -1;
                }
            } else {
                return -1;
            }
            return 0;
        }
    }
}

