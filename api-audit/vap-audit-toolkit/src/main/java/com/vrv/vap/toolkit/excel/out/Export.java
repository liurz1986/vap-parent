package com.vrv.vap.toolkit.excel.out;

import com.vrv.vap.toolkit.excel.ExcelInfo;
import com.vrv.vap.toolkit.tools.CommonTools;
import com.vrv.vap.toolkit.tools.ConvertTools;
import com.vrv.vap.toolkit.tools.PropertiesTools;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
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

/**
 * 数据导出为excel
 *
 * @author xw
 * @date 2018年4月2日
 */
public final class Export {
    private static final Log log = LogFactory.getLog(Export.class);

    /**
     * 每页sheet的最大条数
     */
    private static final int ROWS = 50000;

    private static final Map<String, Progress> STATUS = new HashMap<>();

    private static final BlockingQueue<Runnable> QUEUE = new ArrayBlockingQueue<Runnable>(10000);
    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(4, 16, 60, TimeUnit.SECONDS, QUEUE);

    private static final ScheduledExecutorService WATCHER = Executors.newSingleThreadScheduledExecutor();

    private static boolean isWatching = false;

    /**
     * 新疆要求导出无颜色样式
     */
    private static boolean noStyle = Boolean.parseBoolean(System.getenv("XJ_EXPORT_NOSTYLE"));

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
            Map<String, Progress> tmp = new HashMap<>(STATUS);
            tmp.forEach((k, v) -> {
                if (now - v.getStartTime() > range) {
                    log.info("清除2小时前的导出历史文件,id->" + k + " ,file->" + v.dataList.get(0).getExcelInfo().getFilePath());
                    new File(v.dataList.get(0).getExcelInfo().getFilePath()).delete();
                    STATUS.remove(k);
                }
            });
        }, 0, 10, TimeUnit.MINUTES);
    }

    // public static void main(String[] args) {
    // List<ExcelData> list = new ArrayList<>();
    // ExcelInfo info = new ExcelInfo("测试", new String[] { "ip" }, new String[] {
    // "ip地址" }, "测试", false, "f:/cc.xls",
    // null);
    // ExcelData data = new ExcelData(info, 10, new ArrayList<>());
    // list.add(data);
    //
    // build(list, (t -> {
    // return new String[] { t.get("ip").toString() };
    // })).start(WriteHandler.fun(p -> {
    // List<Map<String, Object>> list2 = new ArrayList<>();
    // Map<String, Object> map = new HashMap<>();
    // map.put("ip", "asdasd");
    // list2.add(map);
    // list2.add(map);
    // list2.add(map);
    // list2.forEach(a -> p.writeMap(0, a));
    // }));
    // }

    /**
     * 构造写入
     *
     * @param dataList    excel信息
     * @param handlerList 字段处理器,不设置默认直接复制所有字段
     * @return
     */
    public static Progress build(List<ExcelData> dataList, FieldHandler... handlerList) {
        Progress progress = new Progress(dataList, handlerList);
        STATUS.put(progress.getWorkId(), progress);
        return progress;
    }

    /**
     * 获取生成进度
     *
     * @return
     * @String workId
     */
    public static Progress getProcess(String workId) {
        return STATUS.get(workId);
    }

    public static class Progress {

        /**
         * 开始时间
         */
        long startTime;

        /**
         * 为每个导入随机生成id
         */
        String id;

        /**
         * 区别是哪个工程的，默认business
         */
        String basePathType = "api-audit";

        public String getBasePathType() {
            return basePathType;
        }

        public void setBasePathType(String basePathType) {
            this.basePathType = basePathType;
        }

        /**
         * 总条数
         */
        float total;

        /**
         * 导出最大条数
         */
        static int maxTotal = PropertiesTools.getSysPropertiesIntVal("excel.export.max.count", 10000);

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
        List<ExcelData> dataList;

        /**
         * 数据处理解析方法
         */
        FieldHandler[] handlerList;

        /**
         * 是否完成
         */
        boolean isFinish = false;

        /**
         * 第一List<ExcelData>的信息
         */
        ExcelInfo firstExcelInfo;

        /**
         * 每个sheet的最大条数
         */
        private static final int ALERT = 5000;

        /**
         * id前缀
         */
        private static final String PREFIX = "PROGRESS_";

        public Progress(List<ExcelData> dataList, FieldHandler... handlerList) {
            this.firstExcelInfo = dataList.get(0).getExcelInfo();
            this.startTime = System.currentTimeMillis();
            this.id = PREFIX + CommonTools.generateId();
            this.dataList = dataList;
            this.handlerList = handlerList;
            if (null == handlerList || handlerList.length == 0) {
                if (log.isDebugEnabled()) {
                    log.debug(id + ": 未设置数据处理器,使用默认方法");
                }
                this.handlerList = new FieldHandler[this.dataList.size()];
                for (int i = 0; i < this.handlerList.length; i++) {
                    this.handlerList[i] = createHandler(dataList.get(i).getExcelInfo().getColumns());
                }
            } else {
                for (int i = 0; i < this.handlerList.length; i++) {
                    if (null == this.handlerList[i]) {
                        this.handlerList[i] = createHandler(dataList.get(i).getExcelInfo().getColumns());
                    }
                }
            }

            this.total = dataList.stream().mapToLong(m -> m.getTotal()).sum();

            if (log.isDebugEnabled()) {
                log.debug("导入信息: " + this.toString());
            }
        }

        public Progress() {
        }

        /**
         * 创建默认字段处理器
         *
         * @return
         * @String cs
         */
        private FieldHandler createHandler(String[] cs) {
            return t -> {
                List<String> tmp = new ArrayList<>();
                Object tmp2 = null;
                for (String c : cs) {
                    tmp2 = t.get(c);
                    tmp.add(null == tmp2 ? "" : tmp2.toString());
                }
                return tmp.toArray(new String[0]);
            };
        }

        /**
         * 开始生成excel
         *
         * @WriteHandler writeHandler
         */
        public Progress start(WriteHandler writeHandler) {
            writeHandler.setProgress(this);
            POOL.execute(writeHandler);
            return this;
        }

        /**
         * @return 返回当前共写入的条数
         * @int index 写入第几个excel data
         * @Map data
         * @OverCountException OverCountException
         */
        public int writeMap(int index, Map<String, Object> data) throws OverCountException {
            if (isOverCount()) {
                throw new OverCountException("超过最大导出条数:" + maxTotal);
            }
            dataList.get(index).getData().add(handlerList[index].fix(data));
            if (writeCount.get() % ALERT == 0) {
                log.debug("读取数据进度(" + id + "): " + writeCount.get() + "/" + total);
            }
            return writeCount.addAndGet(1);
        }

        /**
         * @return 返回当前共写入的条数
         * @index index 写入第几个excel data
         * @Object data
         * @OverCountException OverCountException
         */
        @SuppressWarnings("unchecked")
        public int writeBean(int index, Object data) throws OverCountException {
            if (data instanceof Map) {
                return writeMap(index, (Map<String, Object>) data);
            }
            return writeMap(index, ConvertTools.bean2Map(data));
        }

        /**
         * @param index 写入第几个excel data
         * @param datas
         * @return 返回当前共写入的条数
         * @throws OverCountException
         */
        public int writeBatchMap(int index, List<Map<String, Object>> datas) throws OverCountException {
            for (Map<String, Object> a : datas) {
                writeMap(index, a);
            }
            return writeCount.get();
        }

        /**
         * @param index 写入第几个excel data
         * @param datas
         * @return 返回当前共写入的条数
         * @throws OverCountException
         */
        public <T> int writeBatchBean(int index, List<T> datas) throws OverCountException {
            for (T a : datas) {
                writeBean(index, a);
            }
            return writeCount.get();
        }

        public boolean isOverCount() {
            return writeCount.get() >= maxTotal;
        }

        /**
         * 生成excel文件
         *
         * @param
         * @return
         */
        Workbook toExcel() {
            HSSFWorkbook workBook = new HSSFWorkbook();

            workBook.getCustomPalette().setColorAtIndex((short) 50, (byte) 79, (byte) 129, (byte) 189);
            workBook.getCustomPalette().setColorAtIndex((short) 51, (byte) 219, (byte) 229, (byte) 241);

            HSSFDataFormat dataFormat = workBook.createDataFormat();
            // 奇数行
            CellStyle cellStyle = workBook.createCellStyle();
            cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
            cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
            cellStyle.setBottomBorderColor((short) 50);
            cellStyle.setBorderRight(CellStyle.BORDER_THIN);
            cellStyle.setRightBorderColor((short) 50);
            if (noStyle) {
                cellStyle.setBottomBorderColor((short) 0x8);
                cellStyle.setRightBorderColor((short) (0x8));
            }
            cellStyle.setDataFormat(dataFormat.getFormat("@"));

            // 偶数行
            CellStyle cellStyle2 = workBook.createCellStyle();
            cellStyle2.setAlignment(CellStyle.ALIGN_LEFT);
            cellStyle2.setFillPattern(CellStyle.SOLID_FOREGROUND);
            cellStyle2.setFillForegroundColor((short) 51);
            cellStyle2.setBorderBottom(CellStyle.BORDER_THIN);
            cellStyle2.setBottomBorderColor((short) 50);
            cellStyle2.setBorderRight(CellStyle.BORDER_THIN);
            cellStyle2.setRightBorderColor((short) 50);
            if (noStyle) {
                cellStyle2.setFillForegroundColor((short) 0x9);
                cellStyle2.setBottomBorderColor((short) 0x8);
                cellStyle2.setRightBorderColor((short) (0x8));
            }
            cellStyle2.setDataFormat(dataFormat.getFormat("@"));

            // 第几个ExcelData
            int num = 0;
            for (ExcelData excelData : dataList) {
                num++;
                // 获取数据
                List<String[]> data = excelData.getData();
                int sheetIndex = 0;
                Sheet sheet = this.createSheet(workBook, num, excelData, sheetIndex);
                Row row = null;
                // 每个sheet的最大行数
                int rowNum = 1;
                for (int i = 0; i < data.size(); i++) {
                    // 超过最大行数限制,新建一个sheet
                    if ((i + 1) % ROWS == 0) {
                        sheetIndex++;
                        sheet = this.createSheet(workBook, num, excelData, sheetIndex);
                        rowNum = 1;
                    }
                    row = sheet.createRow(rowNum);
                    rowNum++;
                    readCount.addAndGet(1);
                    int len = data.get(i).length;
                    for (int j = 0; j < len; j++) {
                        Cell cell = row.createCell(j);
                        cell.setCellStyle(i % 2 == 0 ? cellStyle : cellStyle2);
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cell.setCellValue(data.get(i)[j]);
                        if (readCount.get() % ALERT == 0) {
                            log.debug("生成excel进度(" + id + "): " + readCount.get() + "/" + total);
                        }
                    }
                }
            }
            log.debug("生成excel进度(" + id + "): " + readCount.get() + "/" + total);
            return workBook;
        }

        /**
         * 生成excel文件
         *
         * @param
         * @return
         */
        Workbook toMergeExcel() {
            HSSFWorkbook workBook = new HSSFWorkbook();

            workBook.getCustomPalette().setColorAtIndex((short) 50, (byte) 79, (byte) 129, (byte) 189);
            workBook.getCustomPalette().setColorAtIndex((short) 51, (byte) 219, (byte) 229, (byte) 241);

            CellStyle cellStyle = workBook.createCellStyle();
            cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
            cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

            // 第几个ExcelData
            int num = 0;
            for (ExcelData excelData : dataList) {
                num++;
                // 获取数据
                List<String[]> data = excelData.getData();
                int sheetIndex = 0;
                Sheet sheet = this.createSheet(workBook, num, excelData, sheetIndex);
                Row row = null;
                // 每个sheet的最大行数
                int rowNum = 1;
                if ("概览".equals(excelData.getExcelInfo().getSheetName())) {
                    int mergeColStart = excelData.getExcelInfo().getMergeColStart();
                    int mergeColEnd = excelData.getExcelInfo().getMergeColEnd();
                    int[] fields = excelData.getExcelInfo().getMergerFields();
                    String perUserId = "";// 上一个userId
                    StringBuilder onUserId = new StringBuilder("");// 当前userId
                    int startMergeRow = 1;// 要融合的第一行
                    int mergeCount = 0; //融合行数增量

                    for (int i = 0; i < data.size(); i++) {
                        // 超过最大行数限制,新建一个sheet
                        if ((i + 1) % ROWS == 0) {
                            sheetIndex++;
                            sheet = this.createSheet(workBook, num, excelData, sheetIndex);
                            rowNum = 1;
                        }
                        row = sheet.createRow(rowNum);
                        readCount.addAndGet(1);
                        int len = data.get(i).length;
                        for (int j = 0; j < len; j++) {
                            Cell cell = row.createCell(j);
                            cell.setCellStyle(cellStyle);
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            cell.setCellValue(data.get(i)[j]);
                            if (readCount.get() % ALERT == 0) {
                                log.debug("生成excel进度(" + id + "): " + readCount.get() + "/" + total);
                            }
                        }

                        onUserId.setLength(0);
                        for (int m : fields) {
                            onUserId.append(data.get(i)[m]).append(";");
                        }
                        if (perUserId.equals(onUserId.toString())) {
                            mergeCount++;
                            if (i == data.size() - 1 && mergeCount != 0) {
                                //最后一行数据有合并项时直接合
                                for (int k = mergeColStart; k < mergeColEnd + 1; k++) {
                                    sheet.addMergedRegion(new CellRangeAddress(startMergeRow, startMergeRow + mergeCount, k, k));
                                }
                            }
                        } else if (i != 0 && !perUserId.equals(onUserId.toString()) && mergeCount != 0) {
                            for (int k = mergeColStart; k < mergeColEnd + 1; k++) {
                                sheet.addMergedRegion(new CellRangeAddress(startMergeRow, startMergeRow + mergeCount, k, k));
                            }
                            //下一轮合并重新开始计数
                            startMergeRow = startMergeRow + mergeCount + 1;
                            mergeCount = 0;
                            perUserId = onUserId.toString();
                        } else {
                            //没有合并项
                            perUserId = onUserId.toString();
                            startMergeRow = rowNum;
                        }
                        rowNum++;
                    }
                } else {
                    for (int i = 0; i < data.size(); i++) {
                        // 超过最大行数限制,新建一个sheet
                        if ((i + 1) % ROWS == 0) {
                            sheetIndex++;
                            sheet = this.createSheet(workBook, num, excelData, sheetIndex);
                            rowNum = 1;
                        }
                        row = sheet.createRow(rowNum);
                        rowNum++;
                        readCount.addAndGet(1);
                        int len = data.get(i).length;
                        for (int j = 0; j < len; j++) {
                            Cell cell = row.createCell(j);
                            cell.setCellStyle(cellStyle);
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            cell.setCellValue(data.get(i)[j]);
                            if (readCount.get() % ALERT == 0) {
                                log.debug("生成excel进度(" + id + "): " + readCount.get() + "/" + total);
                            }
                        }
                    }
                }
            }
            log.debug("生成excel进度(" + id + "): " + readCount.get() + "/" + total);
            return workBook;
        }

        private Sheet createSheet(Workbook workBook, int num, ExcelData excelData, int sheetIndex) {
            Sheet sheet = workBook.createSheet(
                    new StringBuilder().append(num).append(".").append(excelData.getExcelInfo().getSheetName())
                            .append("(").append(sheetIndex).append(")").toString());
            sheet.setDefaultColumnWidth(18);
            Row row = sheet.createRow(0);
            CellStyle cellStyle = workBook.createCellStyle();
            cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            cellStyle.setFillForegroundColor((short) 50);
            cellStyle.setRightBorderColor((short) 51);
            cellStyle.setBorderRight(CellStyle.BORDER_THIN);
            if (noStyle) {
                cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                cellStyle.setBorderRight(CellStyle.BORDER_THIN);
                cellStyle.setFillForegroundColor((short) 0x9);
                cellStyle.setBottomBorderColor((short) 0x8);
                cellStyle.setRightBorderColor((short) 0x8);
            }
            cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

            DataFormat dataFormat = workBook.createDataFormat();
            cellStyle.setDataFormat(dataFormat.getFormat("@"));

            Font font = workBook.createFont();
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            if (!noStyle) {
                font.setColor(HSSFColor.WHITE.index);
            }
            font.setFontHeightInPoints((short) 12);

            cellStyle.setFont(font);
            String[] column = excelData.getExcelInfo().getColumnsCn() == null ? excelData.getExcelInfo().getColumns()
                    : excelData.getExcelInfo().getColumnsCn();
            for (int i = 0; i < column.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(column[i]);
                cell.setCellStyle(cellStyle);
            }

            return sheet;
        }

        /**
         * @return 返回进度, 完成为1.0
         */
        public float getProcess() {
            if (total == 0 || isOverCount()) {
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
            return (((writeCount.get() + readCount.get()) >= total) && isFinish) || isOverCount();
        }

        /**
         * 获取开始时间
         *
         * @return
         */
        public long getStartTime() {
            return startTime;
        }

        public ExcelInfo getFirstExcelInfo() {
            return firstExcelInfo;
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
