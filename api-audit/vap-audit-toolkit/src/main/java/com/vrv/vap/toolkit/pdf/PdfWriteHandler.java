package com.vrv.vap.toolkit.pdf;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.toolkit.constant.Common;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 数据写入处理
 *
 * @author xw
 * @date 2018年4月3日
 */
public class PdfWriteHandler implements Runnable {
    private static final Log log = LogFactory.getLog(PdfWriteHandler.class);

    PdfExport.PdfProgress progress;
    PdfSimpleWriter simpleWriter;
    RedisTemplate redisTemplate;

    public PdfWriteHandler(PdfSimpleWriter simpleWriter, RedisTemplate redisTemplate) {
        this(null, simpleWriter, redisTemplate);
    }

    public PdfWriteHandler(PdfExport.PdfProgress progress) {
        this(progress, null, null);
    }

    public PdfWriteHandler(PdfExport.PdfProgress progress, PdfSimpleWriter simpleWriter, RedisTemplate redisTemplate) {
        this.progress = progress;
        this.simpleWriter = simpleWriter;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run() {
        simpleWriter.write(progress);
        progress.isFinish = true;
        try {
            toRedis();
        } catch (IOException e) {
            log.error("", e);
        }
        redisTemplate.opsForValue().set(Common.EXPORT_PDF_REDIS_PRO_PATH + progress.getWorkId(), JSONObject.toJSONString(progress), 120, TimeUnit.MINUTES);
    }

    void toRedis() throws IOException {
        File file = new File(progress.data.getFilePath());
        log.info("pdf file => " + progress.data.getFilePath() + " exists " + file.exists());
        FileInputStream inputFile = new FileInputStream(file);
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
            byte[] tmp = Arrays.copyOfRange(buffer, from, to);
            listrk.add(tmp);//按字节范围拷贝生成新数组,添加到List列表中
        }
        redisTemplate.opsForList().rightPushAll(Common.EXPORT_PDF_REDIS_FILE_PATH + progress.getWorkId() + "_file", listrk);
        redisTemplate.expire(Common.EXPORT_PDF_REDIS_FILE_PATH + progress.getWorkId() + "_file", 120, TimeUnit.MINUTES);
    }

    /**
     * 简单的构造一个新的写入处理器
     *
     * @param simpleWriter
     * @return
     */
    public static PdfWriteHandler fun(PdfSimpleWriter simpleWriter, RedisTemplate redisTemplate) {
        return new PdfWriteHandler(simpleWriter, redisTemplate);
    }

    public void setPdfProgress(PdfExport.PdfProgress progress) {
        this.progress = progress;
    }

    @FunctionalInterface
    public interface PdfSimpleWriter {
        void write(PdfExport.PdfProgress progress);
    }
}

