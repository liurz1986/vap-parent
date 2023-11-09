package com.vrv.vap.data.controller;

import com.google.gson.Gson;
import com.vrv.vap.data.common.excel.ExcelInfo;
import com.vrv.vap.data.common.excel.out.ProgressInfo;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.data.constant.RetMsgEnum;
import com.vrv.vap.data.util.FastDFSUtils;
import com.vrv.vap.data.util.FileUtil;
import com.vrv.vap.data.util.VoBuilder;
import com.vrv.vap.data.common.excel.out.Export;
import com.vrv.vap.data.model.FileUpLoadInfo;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * 文件下载及进度查询
 * 
 * @author xw
 *
 * @date 2018年4月3日
 */
@RestController
public class ExportController {

	private static Log log = LogFactory.getLog(ExportController.class);

	@Value("${zipPwd:123qwe}")
	private String zipPwd;

	@Resource
	private Export export;

	@Resource
	private RedisTemplate<String, FileUpLoadInfo> exportTemplate;

	@Autowired
	private FastDFSUtils fastDFSUtils;

	private static final String SEARCH_EXPORT_PREFIX = "_SEARCH_EXPORT:";

	@ResponseBody
	@GetMapping("/progress/excel/{workId}")
	@ApiOperation("根据workid获取导出进度")
	public Result getProgress(@PathVariable("workId") String workId) {
		ProgressInfo progress = export.getProcess(workId);
		VData vdata = new VData<Export.Progress>();
		vdata.setCode(RetMsgEnum.SUCCESS.getCode());
		vdata.setMessage(RetMsgEnum.SUCCESS.getMsg());
		vdata.setData(progress);
		return null == progress ? VoBuilder.result(RetMsgEnum.EMPTY_RET) : vdata;
	}

	@GetMapping("/download/excel/{workId}")
	@ApiOperation("根据workid下载文件")
	@SysRequestLog(description = "根据workid下载文件",actionType = ActionType.SELECT)
	public void download(@ApiParam("文件导出WorkId") @PathVariable("workId") String workId, HttpServletResponse resp, HttpServletRequest req) {
		SyslogSenderUtils.sendSelectSyslog();
		ProgressInfo progress = export.getProcess(workId);
		if (null == progress) {
			log.error("未查询到指定workid文件: " + workId);
			return;
		}
		ExcelInfo info = progress.getFirstExcelInfo();
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("content-type", "application/octet-stream");
		resp.setContentType("application/octet-stream");
		String agent = req.getHeader("USER-AGENT");
		String downloadName;
		if (info.getFilename().startsWith("搜索明细")) {
			downloadName = info.getFilename() + ".zip";
			createZip(info,workId);
		} else {
			downloadName = info.getFilename() + ".xlsx";
		}
		try {
			if(agent != null && agent.toLowerCase().indexOf("firefox") > 0)
			{
				downloadName = "=?UTF-8?B?" + (new String(Base64Utils.encodeToString(downloadName.getBytes("UTF-8")))) + "?=";
			} else {
				downloadName =  java.net.URLEncoder.encode(downloadName, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		resp.setHeader("Content-Disposition", "attachment;filename=" + downloadName);
		OutputStream out = null;
		InputStream in = null;
		try {
			out = resp.getOutputStream();
			File file = new File(info.getFilePath());
			this.downloadByPath(info.getFilePath(),workId);
			in = FileUtils.openInputStream(file);
			IOUtils.copy(in, out);
		} catch (Exception e) {
			log.error("", e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	private void createZip(ExcelInfo excelInfo,String workId) {
		String excelPath = excelInfo.getFilePath();
		this.downloadByPath(excelInfo.getFilePath(),workId);
		String zipPath = excelPath.replace(".xls", ".zip");
		try {
			// 生成的压缩文件
			net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(zipPath);
			ZipParameters parameters = new ZipParameters();
			// 压缩方式
			parameters.setCompressionMethod(CompressionMethod.DEFLATE);
			// 压缩级别
			parameters.setCompressionLevel(CompressionLevel.NORMAL);
			// 是否设置加密文件
			//parameters.setEncryptFiles(true);
			// 设置加密算法
			//parameters.setEncryptionMethod(EncryptionMethod.AES);
			// 设置AES加密密钥的密钥强度
			//parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
			// 设置密码
			//zipFile.setPassword(zipPwd.toCharArray());
			// 要打包的文件夹
			zipFile.addFile(excelPath, parameters);

		} catch (Exception e) {
			log.error("",e);
		}
		excelInfo.setFilePath(zipPath);
	}

	private void downloadByPath(String path,String workId) {
		File file = new File(path);
		if (!file.exists()) {
			Gson gson = new Gson();
			if (exportTemplate.opsForValue().get(SEARCH_EXPORT_PREFIX + workId) != null) {
				FileUpLoadInfo fileUpLoadInfo = gson.fromJson(gson.toJson(exportTemplate.opsForValue().get(SEARCH_EXPORT_PREFIX + workId)),FileUpLoadInfo.class);
				String filePath;
				InputStream st = null;
				if (fileUpLoadInfo.getUploadType() == 1) {
					filePath = fileUpLoadInfo.getFilePath();
					st = fastDFSUtils.downloadSteamFileByPath(filePath);
				}
				try {
					if (fileUpLoadInfo.getUploadType() == 2) {
						filePath = fileUpLoadInfo.getFilePath();
						st = new URL(filePath).openStream();
					}
					FileUtil.inputStream2File(st,file);
				} catch (Exception e) {
					log.error("",e);
				}
			}
		}
	}
}
