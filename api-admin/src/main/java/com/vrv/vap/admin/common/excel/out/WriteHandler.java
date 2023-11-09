package com.vrv.vap.admin.common.excel.out;

import com.vrv.vap.admin.common.excel.out.Export.Progress;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 数据写入处理
 * 
 * @author xw
 *
 * @date 2018年4月3日
 */
public class WriteHandler implements Runnable {
	private static final Log log = LogFactory.getLog(WriteHandler.class);

	Progress progress;
	SimpleWriter simpleWriter;

	public WriteHandler(SimpleWriter simpleWriter) {
		this(null, simpleWriter);
	}

	public WriteHandler(Progress progress) {
		this(progress, null);
	}

	public WriteHandler(Progress progress, SimpleWriter simpleWriter) {
		this.progress = progress;
		this.simpleWriter = simpleWriter;
	}

	@Override
	public void run() {
		simpleWriter.write(progress);
		log.debug("读取数据进度(" + progress.id + "): " + progress.writeCount.get() + "/" + progress.total);
		toDisk(progress.toExcel());
		progress.isFinish = true;
	}

	/**
	 * 简单的构造一个新的写入处理器
	 * 
	 * @param simpleWriter
	 * @return
	 */
	public static WriteHandler fun(SimpleWriter simpleWriter) {
		return new WriteHandler(simpleWriter);
	}

	void toDisk(Workbook wb) {
		try (FileOutputStream out = new FileOutputStream(new File(progress.dataList.get(0).getExcelInfo().getFilePath()))) {
			wb.write(out);
		} catch (IOException e) {
			log.error("", e);
		} finally {
			try {
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
