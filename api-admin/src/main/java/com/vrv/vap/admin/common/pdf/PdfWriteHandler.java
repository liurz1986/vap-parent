package com.vrv.vap.admin.common.pdf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 数据写入处理
 * 
 * @author xw
 *
 * @date 2018年4月3日
 */
public class PdfWriteHandler implements Runnable {
	private static final Log log = LogFactory.getLog(PdfWriteHandler.class);

	PdfExport.PdfProgress progress;
	PdfSimpleWriter simpleWriter;

	public PdfWriteHandler(PdfSimpleWriter simpleWriter) {
		this(null, simpleWriter);
	}

	public PdfWriteHandler(PdfExport.PdfProgress progress) {
		this(progress, null);
	}

	public PdfWriteHandler(PdfExport.PdfProgress progress, PdfSimpleWriter simpleWriter) {
		this.progress = progress;
		this.simpleWriter = simpleWriter;
	}

	@Override
	public void run() {
		simpleWriter.write(progress);
		progress.isFinish = true;
	}

	/**
	 * 简单的构造一个新的写入处理器
	 * 
	 * @param simpleWriter
	 * @return
	 */
	public static PdfWriteHandler fun(PdfSimpleWriter simpleWriter) {
		return new PdfWriteHandler(simpleWriter);
	}

	public void setPdfProgress(PdfExport.PdfProgress progress) {
		this.progress = progress;
	}

	@FunctionalInterface
	public interface PdfSimpleWriter {
		void write(PdfExport.PdfProgress progress);
	}
}

