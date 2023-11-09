package com.vrv.vap.admin.common.util;


import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author zhangkai
 * @ClassName FastDFSUtils
 * @Description FastDFS工具类
 * @date 2017年7月18日
 */
@Component
public class FastDFSUtils implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(FastDFSUtils.class);

    @Autowired
    private FastFileStorageClient storageClient;

    /**
     * 上传文件
     *
     * @param file 文件对象
     * @return 文件访问地址
     * @throws IOException
     */
    public String uploadFile(MultipartFile file) throws IOException {
        try (InputStream in = file.getInputStream()) {
            StorePath storePath = storageClient.uploadFile(in, file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), null);
            return getResAccessUrl(storePath);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 上传文件
     *
     * @param file 文件对象
     * @return 文件访问地址
     * @throws IOException
     */
    public String uploadFile(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            StorePath storePath = storageClient.uploadFile(inputStream, file.length(), FilenameUtils.getExtension(file.getName()), null);
            return getResAccessUrl(storePath);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    /**
     * 上传字节流文件
     * @param buff
     * @param fileName
     * @return
     * @throws IOException
     */
    public String uploadFile(byte[] buff,String fileName) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(buff);
        StorePath storePath = storageClient.uploadFile(stream,buff.length, FilenameUtils.getExtension(fileName),null);
        return getResAccessUrl(storePath);
    }

    /**
     * 将一段字符串生成一个文件上传
     *
     * @param content       文件内容
     * @param fileExtension
     * @return
     */
    public String uploadFile(String content, String fileExtension) {
        byte[] buff = content.getBytes(Charset.forName("UTF-8"));
        ByteArrayInputStream stream = new ByteArrayInputStream(buff);
        StorePath storePath = storageClient.uploadFile(stream, buff.length, fileExtension, null);
        return getResAccessUrl(storePath);
    }

    // 封装图片完整URL地址
    private String getResAccessUrl(StorePath storePath) {
        String path = storePath.getFullPath();
        return path;
    }

    public static String getFileNameFormFilePath(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    /**
     * 下载文件
     *
     * @param fileUrl 文件url
     * @return
     */
    public byte[] downloadFileByPath(String fileUrl) {
        String group = fileUrl.substring(0, fileUrl.indexOf("/"));
        String path = fileUrl.substring(fileUrl.indexOf("/") + 1);
        byte[] bytes = storageClient.downloadFile(group, path, new DownloadByteArray());
        return bytes;
    }



    /**
     * 下载文件
     *
     * @param fileUrl 文件url
     * @return
     */
    public InputStream downloadSteamFileByPath(String fileUrl) {
        String group = fileUrl.substring(0, fileUrl.indexOf("/"));
        String path = fileUrl.substring(fileUrl.indexOf("/") + 1);
        InputStream bytes = storageClient.downloadFile(group, path, new DownloadInputStream());
        return bytes;
    }

    /**
     * 根据文件路径获取文件大小
     * @param fileUrl
     * @return
     */
    public int getFileLengthFromFilePath(String fileUrl) {
        int fileLength = 0;
        try {
            String group = fileUrl.substring(0, fileUrl.indexOf("/"));
            String path = fileUrl.substring(fileUrl.indexOf("/") + 1);
            byte[] content = storageClient.downloadFile(group, path, new DownloadByteArray());
            InputStream inputStream = new ByteArrayInputStream(content);
            fileLength = inputStream.available();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileLength;
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件访问地址
     * @return
     */
    public void deleteFile(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            return;
        }
        try {
            StorePath storePath = StorePath.praseFromUrl(fileUrl);
            storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
        } catch (FdfsUnsupportStorePathException e) {
            logger.warn(e.getMessage());
        }
    }
}

