package com.vrv.vap.data.common.excel.out;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.data.model.FileUpLoadInfo;
import com.vrv.vap.data.service.feign.AdminClient;
import com.vrv.vap.data.util.FileUtil;
import com.vrv.vap.data.common.excel.out.Export.Progress;
import com.vrv.vap.data.util.TimeTools;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
	AdminClient adminClient;
	private RedisTemplate<String, FileUpLoadInfo> exportTemplate;
	private StringRedisTemplate cookieTemplate;

	private static final String EXPORT_COOKIE = "_EXPORT_COOKIE:";

	private static final String SEARCH_EXPORT_PREFIX = "_SEARCH_EXPORT:";

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
		progress.updateProgress(progress.writeCount.get(),progress.readCount.get(),progress.id);
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
		FileOutputStream out = null;
		try {
			File file = new File(progress.dataList.get(0).getExcelInfo().getFilePath());
			out = new FileOutputStream(file);
			wb.write(out);
			//上传至文件服务器
			FileItem item = FileUtil.createFileItem(file,file.getName());
			MultipartFile mfile = new CommonsMultipartFile(item);
			String cookie = cookieTemplate.opsForValue().get(EXPORT_COOKIE + progress.id);
			VData vData = adminClient.uploadFile(mfile,"","",cookie);
			if (vData.getData() != null) {
				Gson gson = new GsonBuilder().setDateFormat(TimeTools.GMT_PTN).create();
				FileUpLoadInfo fileUpLoadInfo = gson.fromJson(gson.toJson(vData.getData()),FileUpLoadInfo.class) ;
				exportTemplate.opsForValue().set(SEARCH_EXPORT_PREFIX + progress.id,fileUpLoadInfo,30, TimeUnit.MINUTES);
			} else {
				log.info("上传至文件服务器失败！");
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				if (null != out) {
					out.flush();
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

    public AdminClient getAdminClient() {
        return adminClient;
    }

    public void setAdminClient(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    public RedisTemplate<String, FileUpLoadInfo> getExportTemplate() {
        return exportTemplate;
    }

    public void setExportTemplate(RedisTemplate<String, FileUpLoadInfo> exportTemplate) {
        this.exportTemplate = exportTemplate;
    }

	public StringRedisTemplate getCookieTemplate() {
		return cookieTemplate;
	}

	public void setCookieTemplate(StringRedisTemplate cookieTemplate) {
		this.cookieTemplate = cookieTemplate;
	}
}
