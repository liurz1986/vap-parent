package com.vrv.vap.monitor.tools.zip;


import com.vrv.vap.toolkit.excel.out.FieldHandler;
import com.vrv.vap.toolkit.tools.CommonTools;
import com.vrv.vap.toolkit.tools.TimeTools;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 数据导出为excel
 *
 * @author xw
 * @date 2018年4月2日
 */
public final class ZipExport {
    private static final Log log = LogFactory.getLog(ZipExport.class);

    private static final Map<String, ZipProgress> STATUS = new HashMap<>();

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
            Map<String, ZipProgress> tmp = new HashMap<>(STATUS);
            tmp.forEach((k, v) -> {
                if (now - v.getStartTime() > range) {
                    log.info("清除2小时前的导出历史文件,id->" + k + " ,file->" + v.dataList.get(0).getZipFilePath());
                    if (new File(v.dataList.get(0).getZipFilePath()).delete()) {
                        log.info("清除成功！");
                    }
                    STATUS.remove(k);
                }
            });
        }, 0, 10, TimeUnit.MINUTES);
    }

    /**
     * 构造写入
     *
     * @param dataList    excel信息
     * @param handlerList 字段处理器,不设置默认直接复制所有字段
     * @return
     */
    public static ZipProgress build(List<ZipData> dataList, FieldHandler... handlerList) {
        ZipProgress progress = new ZipProgress(dataList);
        STATUS.put(progress.getWorkId(), progress);
        return progress;
    }

    /**
     * 获取生成进度
     *
     * @param workId
     * @return
     */
    public static ZipProgress getProcess(String workId) {
        return STATUS.get(workId);
    }

    public static class ZipProgress {

        /**
         * 开始时间
         */
        long startTime;

        /**
         * 为每个导入随机生成id
         */
        String id;

        /**
         * 总条数
         */
        float total;

        /**
         * 写入ExcelData条数
         */
        AtomicInteger writeCount = new AtomicInteger();

        /**
         * 已读取的数据条数
         */
        AtomicInteger readCount = new AtomicInteger();

        /**
         * 被读取的数据
         */
        List<ZipData> dataList;

        /**
         * 是否完成
         */
        boolean isFinish = false;

        /**
         * 第一List<ZipData>的信息
         */
        ZipData firstZipData;

        /**
         * 区别是哪个工程的
         */
        String basePathType;

        public String getBasePathType() {
            return basePathType;
        }

        public void setBasePathType(String basePathType) {
            this.basePathType = basePathType;
        }

        /**
         * id前缀
         */
        private static final String PREFIX = "ZIP_PROGRESS_";

        public ZipProgress(List<ZipData> dataList) {
            this.firstZipData = dataList.get(0);
            this.startTime = System.currentTimeMillis();
            this.id = PREFIX + CommonTools.generateId();
            this.dataList = dataList;

            this.total = dataList.get(0).getTotal();

            if (log.isDebugEnabled()) {
                log.debug("导入信息: " + this.toString());
            }
        }

        /**
         * 开始生成excel
         *
         * @param writeHandler
         */
        public ZipProgress start(ZipWriteHandler writeHandler) {
            writeHandler.setZipProgress(this);
            POOL.execute(writeHandler);
            return this;
        }

        public int writeBean(int index, ZipInfo data) throws OverCountException {
            dataList.get(index).getZipInfo().add(data);
            log.debug("读取数据进度(" + id + "): " + writeCount.get() + "/" + total);
            return writeCount.addAndGet(1);
        }

        public int writeBatchBean(int index, List<ZipInfo> datas) throws OverCountException {
            for (ZipInfo a : datas) {
                writeBean(index, a);
            }
            return writeCount.get();
        }

        /**
         * 生成文件
         *
         * @param
         * @return
         */
        void toDiskZip() {
            String filePath = firstZipData.getZipFilePath();
            List<ZipInfo> zipInfoList = firstZipData.getZipInfo();
            List<String> fileNameList = new ArrayList<>();
            try (FileOutputStream bos = new FileOutputStream(filePath);
                 ZipOutputStream out = new ZipOutputStream(bos)) {
                for (ZipInfo data : zipInfoList) {
                    // 创建ZIP实体，并添加进压缩包
                    if (!fileNameList.contains(data.getName())) {
                        fileNameList.add(data.getName());
                        ZipEntry zipEntry = new ZipEntry(data.getName());
                        out.putNextEntry(zipEntry);
                        byte[] dataArray = null;
						/*if(data.getData()==null){
							//从fastdfs中获取
							FastDFSUtils.downloadFileWithCompress(data.getFilePath(),out);
						}else
						{*/
                        dataArray = data.getData();
                        out.write(dataArray);
                        //}
                        out.closeEntry();
                    }
                    readCount.addAndGet(1);
                    log.debug("生成进度(" + id + "): " + readCount.get() + "/" + total);
                }
            } catch (IOException e) {
                log.error("", e);
            }
        }


        /**
         * @return 返回进度, 完成为1.0
         */
        public float getProcess() {
            if (total == 0) {
                return 1.0f;
            }
            return (writeCount.get() + readCount.get()) / (total * 2);
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
            return (((writeCount.get() + readCount.get()) >= total) && isFinish);
        }

        /**
         * 获取开始时间
         *
         * @return
         */
        public long getStartTime() {
            return startTime;
        }

        public ZipData getFirstZipData() {
            return firstZipData;
        }

        @Override
        public String toString() {
            return "{\"startTime\":" + startTime + ", \"id\":" + id + ", \"total\":" + total + ", \"writeCount\":"
                    + writeCount + ", \"readCount\":" + readCount + ",\"progress\":" + getProcess() + ", \"isFinish\":"
                    + isFinish + "}";
        }

    }

    public static class OverCountException extends Exception {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public OverCountException(String msg) {
            super(msg);
        }
    }
}
