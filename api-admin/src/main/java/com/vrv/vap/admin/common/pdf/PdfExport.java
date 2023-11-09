package com.vrv.vap.admin.common.pdf;

import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.util.CleanUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 页面导出pdf
 * <p>
 * Created by lizj on 2020/7/16.
 */
public final class PdfExport {
    private static final Log log = LogFactory.getLog(PdfExport.class);

    private static final Map<String, PdfProgress> STATUS = new HashMap<>();

    private static final BlockingQueue<Runnable> QUEUE = new ArrayBlockingQueue<Runnable>(10000);
    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(4, 16, 60, TimeUnit.SECONDS, QUEUE);

    private static final ScheduledExecutorService WATCHER = Executors.newSingleThreadScheduledExecutor();

    private static boolean isWatching = false;

    /**
     * 启动一个监听器,每隔10分钟清除超过两个小时未处理的进度
     */
    public static void startWatcher() {
        if (isWatching) {
            return;
        }
        isWatching = true;
        log.info("启动一个监听器,每隔10分钟清除超过两个小时未处理的导出进度");
        final long range = TimeTools.HOUR_MS * 2;
        WATCHER.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            Map<String, PdfProgress> tmp = new HashMap<>(STATUS);
            tmp.forEach((k, v) -> {
                if (now - v.getStartTime() > range) {
                    STATUS.remove(k);
                }
            });
        }, 0, 10, TimeUnit.MINUTES);
    }

    /**
     * 构造写入
     *
     * @param data excel信息
     * @return
     */
    public static PdfProgress build(PdfData data) {
        PdfProgress progress = new PdfProgress(data);
        STATUS.put(progress.getWorkId(), progress);
        return progress;
    }

    /**
     * 获取生成进度
     *
     * @param workId
     * @return
     */
    public static PdfProgress getProcess(String workId) {
        return STATUS.get(workId);
    }

    public static class PdfProgress {

        /**
         * 开始时间
         */
        long startTime;

        /**
         * 为每个导入随机生成id
         */
        String id;

        /**
         * 文件名
         */
        String fileName;

        /**
         * pdf数据
         */
        PdfData data;

        /**
         * 是否完成
         */
        boolean isFinish = false;

        /**
         * 导出时间，超过300则直接退出
         */
        long exportTime = 0;

        /**
         * 默认总进度
         */
        long totalTime = 30;

        /**
         * 累加进度
         */
        AtomicInteger time = new AtomicInteger();


        public PdfProgress(PdfData data) {
            this.startTime = System.currentTimeMillis();
            this.id = data.getTaskId();
            this.fileName = data.getPdfName();
            this.data = data;

            if (log.isDebugEnabled()) {
                log.debug("导出pdf信息: " + this.toString());
            }
        }

        /**
         * 开始生成pdf
         *
         * @param writeHandler
         */
        public PdfProgress start(PdfWriteHandler writeHandler) {
            writeHandler.setPdfProgress(this);
            POOL.execute(writeHandler);
            return this;
        }


        public void generatePdf(String fileName) {
            while (true) {
            	// 判断文件是否生成
            	boolean isPdfExist = new File(fileName).exists();
                // 休眠一秒
                try {
                    Thread.sleep(1000); //1000 毫秒，也就是1秒.
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                // 29/30时，判断pdf文件是否生成
                // 若pdf文件未生成，则不累加进度
                if (time.get() == 29) {
                    if (isPdfExist) {
                        time.addAndGet(1);
                    }
                    exportTime += 1;
                } else if (time.get() == totalTime) {
					isFinish = true;
					log.debug("导出进度(" + id + "): " + time.get() + "/" + totalTime + " 已完成！");
                	break;
                } else if (exportTime >= 300) {
                    log.debug("导出失败！");
                    break;
                } else {
                    time.addAndGet(1);
                    exportTime += 1;
                }

                log.debug("导出进度(" + id + "): " + time.get() + "/" + totalTime);
            }
        }


        /**
         * @return 返回进度, 完成为1.0
         */
        public float getProcess() {
            float process = (float)time.get() / totalTime;
            log.info(process);
            return process;
        }

        /**
         * 返回随机id
         *
         * @return
         */
        public String getWorkId() {
            return id;
        }

        /**
         * 是否完毕
         *
         * @return
         */
        public boolean isFinish() {
            return ((time.get() >= totalTime) && isFinish);
        }

        public String getFileName() {
            return fileName;
        }

        /**
         * 获取开始时间
         *
         * @return
         */
        public long getStartTime() {
            return startTime;
        }

        @Override
        public String toString() {
            return "{\"startTime\":" + startTime + ", \"id\":" + id + ",\"progress\":" + getProcess() + ", \"isFinish\":"
                    + isFinish + "}";
        }

    }

}
