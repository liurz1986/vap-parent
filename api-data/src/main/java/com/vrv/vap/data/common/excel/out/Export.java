package com.vrv.vap.data.common.excel.out;

import com.google.gson.Gson;
import com.vrv.vap.data.common.excel.ExcelInfo;
import com.vrv.vap.data.model.FileUpLoadInfo;
import com.vrv.vap.data.service.feign.AdminClient;
import com.vrv.vap.data.util.CommonTools;
import com.vrv.vap.data.util.ConvertTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据导出为excel
 *
 * @author xw
 *
 * @date 2018年4月2日
 */
@Component
public final class Export {
	private static final Log log = LogFactory.getLog(Export.class);

	/**
	 * 每页sheet的最大条数
	 */
	private static final int ROWS = 50000;

	private static final String EXPORT_PROGRESS = "_EXPORT_PROGRESS:";

	private static final String EXPORT_COOKIE = "_EXPORT_COOKIE:";

	private static final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(5000);
	private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(4, 16, 60, TimeUnit.SECONDS, queue);

	@Resource
	RedisTemplate<String, ProgressInfo> progressTemplate;
	@Autowired
	AdminClient adminClient;
	@Resource
	private RedisTemplate<String, FileUpLoadInfo> exportTemplate;
	@Resource
	private StringRedisTemplate cookieTemplate;

	/**
	 * 构造写入
	 *
	 * @param dataList
	 *            excel信息
	 * @param handlerList
	 *            字段处理器,不设置默认直接复制所有字段
	 * @return
	 */
	public Progress build(List<ExcelData> dataList, FieldHandler... handlerList) {
		Progress progress = new Progress(dataList, handlerList);
		ProgressInfo progressInfo = new ProgressInfo();
		progressInfo.setId(progress.id);
		progressInfo.setStartTime(progress.startTime);
		progressInfo.setReadCount(progress.readCount.get());
		progressInfo.setWriteCount(progress.writeCount.get());
		progressInfo.setTotal(progress.total);
		progressInfo.setFinish(progress.isFinish);
		progressInfo.setProcess(progress.getProcess());
		progressInfo.setFirstExcelInfo(progress.firstExcelInfo);
		progressTemplate.opsForValue().set(EXPORT_PROGRESS + progress.getWorkId(),progressInfo,30,TimeUnit.MINUTES);
		return progress;
	}

	/**
	 * 获取生成进度
	 *
	 * @param workId
	 * @return
	 */
	public ProgressInfo getProcess(String workId) {
		Gson gson = new Gson();
		String pro = gson.toJson(progressTemplate.opsForValue().get(EXPORT_PROGRESS + workId));
		ProgressInfo progressInfo = gson.fromJson(pro,ProgressInfo.class);
		return progressInfo;
	}

	public class Progress {

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

		/**
		 * 创建默认字段处理器
		 *
		 * @param cs
		 * @return
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
		 * @param writeHandler
		 */
		public Progress start(WriteHandler writeHandler) {
			writeHandler.setProgress(this);
			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			RequestContextHolder.setRequestAttributes(requestAttributes,true);
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			cookieTemplate.opsForValue().set(EXPORT_COOKIE + writeHandler.progress.id,request.getHeader("Cookie"),30,TimeUnit.MINUTES);
			writeHandler.setAdminClient(adminClient);
			writeHandler.setExportTemplate(exportTemplate);
            writeHandler.setCookieTemplate(cookieTemplate);
			pool.execute(writeHandler);
			return this;
		}

		/**
		 *
		 * @param index
		 *            写入第几个excel data
		 * @param data
		 * @return 返回当前共写入的条数
		 */
		public int writeMap(int index, Map<String, Object> data) {
			dataList.get(index).getData().add(handlerList[index].fix(data));
			if (writeCount.get() % ALERT == 0) {
				log.debug("读取数据进度(" + id + "): " + writeCount.get() + "/" + total);
			}
			return writeCount.addAndGet(1);
		}

		/**
		 *
		 * @param index
		 *            写入第几个excel data
		 * @param data
		 * @return 返回当前共写入的条数
		 */
		@SuppressWarnings("unchecked")
		public int writeBean(int index, Object data) {
			if(data instanceof Map){
				return writeMap(index, (Map<String, Object>) data);
			}
			return writeMap(index, ConvertTools.bean2Map(data));
		}

		/**
		 *
		 * @param index
		 *            写入第几个excel data
		 * @param datas
		 * @return 返回当前共写入的条数
		 */
		public int writeBatchMap(int index, List<Map<String, Object>> datas) {
			datas.forEach(a -> {
				writeMap(index, a);
			});
			this.updateProgress(writeCount.get(),readCount.get(),id);
			return writeCount.get();
		}

		/**
		 *
		 * @param index
		 *            写入第几个excel data
		 * @param datas
		 * @return 返回当前共写入的条数
		 */
		public <T> int writeBatchBean(int index, List<T> datas) {
			datas.forEach(a -> {
				writeBean(index, a);
			});


			return writeCount.get();
		}

		public void updateProgress(Integer writeCount,Integer readCount,String id) {
			Gson gson = new Gson();
			String pro = gson.toJson(progressTemplate.opsForValue().get(EXPORT_PROGRESS + id));
			ProgressInfo progressInfo = gson.fromJson(pro,ProgressInfo.class);
			progressInfo.setWriteCount(writeCount);
			progressInfo.setReadCount(readCount);
			if ((writeCount + readCount) == total * 2 && !isFinish) {
				progressInfo.setProcess(0.99f);
			} else {
				progressInfo.setProcess((progressInfo.getWriteCount() + progressInfo.getReadCount())/(progressInfo.getTotal() * 2));
			}
			progressInfo.setFinish(isFinish());
			progressTemplate.opsForValue().set(EXPORT_PROGRESS + id,progressInfo,30,TimeUnit.MINUTES);
		}

		/**
		 * 生成excel文件
		 *
		 * @param
		 * @return
		 */
		Workbook toExcel() {
			Workbook workBook = new SXSSFWorkbook(100);

//			workBook.getCustomPalette().setColorAtIndex((short) 50, (byte) 79, (byte) 129, (byte) 189);
//			workBook.getCustomPalette().setColorAtIndex((short) 51, (byte) 219, (byte) 229, (byte) 241);

			// 奇数行
			CellStyle cellStyle = workBook.createCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.LEFT);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBottomBorderColor((short) 57);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setRightBorderColor((short) 57);

			// 偶数行
			CellStyle cellStyle2 = workBook.createCellStyle();
			cellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle2.setFillForegroundColor((short) 22);
			cellStyle2.setAlignment(HorizontalAlignment.LEFT);
			cellStyle2.setBorderBottom(BorderStyle.THIN);
			cellStyle2.setBottomBorderColor((short) 57);
			cellStyle2.setBorderRight(BorderStyle.THIN);
			cellStyle2.setRightBorderColor((short) 57);

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
//						cell.setCellType(Cell.CELL_TYPE_STRING);
						cell.setCellValue(data.get(i)[j]);
					}
					if (readCount.get() % ALERT == 0) {
						this.updateProgress(writeCount.get(),readCount.get(),id);
						log.debug("生成excel进度(" + id + "): " + readCount.get() + "/" + total);
					}
				}
				this.updateProgress(writeCount.get(),readCount.get(),id);
				log.debug("生成excel进度(" + id + "): " + readCount.get() + "/" + total);
			}
			return workBook;
		}

		private Sheet createSheet(Workbook workBook, int num, ExcelData excelData, int sheetIndex) {
			Sheet sheet = workBook.createSheet(
					new StringBuilder().append(num).append(".").append(excelData.getExcelInfo().getSheetName())
							.append("(").append(sheetIndex).append(")").toString());
							//.toString());
			sheet.setDefaultColumnWidth(18);
			Row row = sheet.createRow(0);
			CellStyle cellStyle = workBook.createCellStyle();
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle.setFillForegroundColor((short) 57);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setRightBorderColor((short) 57);


			Font font = workBook.createFont();
//			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			font.setColor(IndexedColors.WHITE.index);
			font.setFontHeightInPoints((short) 12);

			cellStyle.setFont(font);
			String[] column = excelData.getExcelInfo().getColumnsCn() == null ? excelData.getExcelInfo().getColumns()
					: excelData.getExcelInfo().getColumnsCn();
			for (int i = 0; i < column.length; i++) {
				Cell cell = row.createCell(i);
//				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(column[i]);
				cell.setCellStyle(cellStyle);
			}

			return sheet;
		}

		/**
		 *
		 * @return 返回进度,完成为1.0
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
			return ((writeCount.get() + readCount.get()) >= total) && isFinish;
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
}
