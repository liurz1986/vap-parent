package com.vrv.vap.amonitor.tools.zip;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.toolkit.constant.Common;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * 数据写入处理
 *
 * @author xw
 * @date 2018年4月3日
 */
public class ZipWriteHandler implements Runnable {
    private static final Log log = LogFactory.getLog(ZipWriteHandler.class);

    ZipExport.ZipProgress progress;
    ZipSimpleWriter simpleWriter;
    RedisTemplate redisTemplate;

    public ZipWriteHandler(ZipSimpleWriter simpleWriter, RedisTemplate redisTemplate) {
        this(null, simpleWriter, redisTemplate);
    }

    public ZipWriteHandler(ZipExport.ZipProgress progress) {
        this(progress, null, null);
    }

    public ZipWriteHandler(ZipExport.ZipProgress progress, ZipSimpleWriter simpleWriter, RedisTemplate redisTemplate) {
        this.progress = progress;
        this.simpleWriter = simpleWriter;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run() {
        try {
            simpleWriter.write(progress);
        } catch (ZipExport.OverCountException e) {
            log.error("", e);
        }
        progress.toDiskZip();
        progress.isFinish = true;
        toRedis();
        redisTemplate.opsForValue().set(Common.EXPORT_ZIP_REDIS_PRO_PATH + progress.getWorkId(), JSONObject.toJSONString(progress), 120, TimeUnit.MINUTES);
    }

    /**
     * 简单的构造一个新的写入处理器
     *
     * @param simpleWriter
     * @return
     */
    public static ZipWriteHandler fun(ZipSimpleWriter simpleWriter, RedisTemplate redisTemplate) {
        return new ZipWriteHandler(simpleWriter, redisTemplate);
    }

    public void setZipProgress(ZipExport.ZipProgress progress) {
        this.progress = progress;
    }

    @FunctionalInterface
    public interface ZipSimpleWriter {
        void write(ZipExport.ZipProgress progress) throws ZipExport.OverCountException;
    }

    void toRedis() {
        File file = new File(progress.dataList.get(0).getZipFilePath());
        FileInputStream inputFile = null;
        try {
            inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) (file.length() * 1)];
            inputFile.read(buffer);//文件解析把字节数添加到buffer[]中
            IOUtils.closeQuietly(inputFile);
            int viceLength = 200; //每个字节包大小
            int viceNumber = (int) Math.ceil(buffer.length / (double) viceLength);//存多少个包
            int from, to;
            List listrk = new ArrayList();
            for (int i = 0; i < viceNumber; i++) { //将完整音频buffer[]进行循环拆分
                from = i * viceLength;
                to = from + viceLength;
                if (to > buffer.length) {
                    to = buffer.length;
                }
                listrk.add(Arrays.copyOfRange(buffer, from, to));//按字节范围拷贝生成新数组,添加到List列表中
            }
            redisTemplate.opsForList().rightPushAll(Common.EXPORT_ZIP_REDIS_FILE_PATH + progress.getWorkId() + "_file", listrk);
            redisTemplate.expire(Common.EXPORT_ZIP_REDIS_FILE_PATH + progress.getWorkId() + "_file", 120, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            try {
                inputFile.close();
            } catch (IOException e) {
                log.error("", e);
            }
        }

    }
}

