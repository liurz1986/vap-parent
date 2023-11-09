package com.vrv.vap.toolkit.excel.out;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.toolkit.constant.Common;
import com.vrv.vap.toolkit.excel.out.Export.OverCountException;
import com.vrv.vap.toolkit.excel.out.Export.Progress;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
public class WriteHandler implements Runnable {
    private static final Log log = LogFactory.getLog(WriteHandler.class);

    Progress progress;
    SimpleWriter simpleWriter;
    RedisTemplate redisTemplate;

    public WriteHandler(SimpleWriter simpleWriter, RedisTemplate redisTemplate) {
        this(null, simpleWriter, redisTemplate);
    }

    public WriteHandler(Progress progress) {
        this(progress, null, null);
    }
    /**WriteHandler*/
    public WriteHandler(Progress progress, SimpleWriter simpleWriter, RedisTemplate redisTemplate) {
        this.progress = progress;
        this.simpleWriter = simpleWriter;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run() {
        try {
            simpleWriter.write(progress);
        } catch (OverCountException e) {
            log.error("", e);
        }
        log.debug("读取数据进度(" + progress.id + "): " + progress.writeCount.get() + "/" + progress.total);
        boolean needMerge = progress.dataList.get(0).getExcelInfo().isNeedMerge();
        if (needMerge) {
            toDisk(progress.toMergeExcel());
        } else {
            toDisk(progress.toExcel());
        }
        progress.isFinish = true;
        try {
            toRedis();
        } catch (IOException e) {
            log.error("", e);
        }
        String key = Common.EXPORT_REDIS_PRO_PATH + progress.getWorkId();
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(progress), 120, TimeUnit.MINUTES);
    }

    /**
     * 简单的构造一个新的写入处理器
     *
     * @SimpleWriter simpleWriter
     * @return
     */
    public static WriteHandler fun(SimpleWriter simpleWriter, RedisTemplate redisTemplate) {
        return new WriteHandler(simpleWriter, redisTemplate);
    }

    void toRedis() throws IOException {
        File file = new File(progress.dataList.get(0).getExcelInfo().getFilePath());
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
        redisTemplate.opsForList().rightPushAll(Common.EXPORT_REDIS_FILE_PATH + progress.getWorkId() + "_file", listrk);
        redisTemplate.expire(Common.EXPORT_REDIS_FILE_PATH + progress.getWorkId() + "_file", 120, TimeUnit.MINUTES);
    }

    void toDisk(Workbook wb) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(progress.dataList.get(0).getExcelInfo().getFilePath()));
            wb.write(out);
        } catch (IOException e) {
            log.error("", e);
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
                wb.close();
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }
}
