package com.vrv.vap.admin.web;

import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.admin.common.constant.RetMsgEnum;
import com.vrv.vap.admin.common.excel.ExcelInfo;
import com.vrv.vap.admin.common.excel.out.Export;
import com.vrv.vap.admin.common.util.VoBuilder;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Locale;

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

	@Value("${elk.zipPwd:123qwe}")
	private String zipPwd;

	@ResponseBody
	@GetMapping("/progress/excel/{workId}")
	@ApiOperation("根据workid获取导出进度")
	public Result getProgress(@ApiParam("workId") @PathVariable("workId") String workId) {
		Export.Progress progress = Export.getProcess(workId);
		VData vdata = new VData<Export.Progress>();
		vdata.setCode(RetMsgEnum.SUCCESS.getCode());
		vdata.setMessage(RetMsgEnum.SUCCESS.getMsg());
		vdata.setData(progress);
		return null == progress ? VoBuilder.result(RetMsgEnum.EMPTY_RET) : vdata;
	}

	@GetMapping("/download/excel/{workId}")
	@ApiOperation("根据workid下载文件")
	@SysRequestLog(description = "根据workid下载文件",actionType = ActionType.DOWNLOAD)
	public void download(@ApiParam("workId") @PathVariable("workId") String workId, HttpServletResponse resp, HttpServletRequest req) {
		SyslogSenderUtils.sendDownLosdSyslog();
		Export.Progress progress = Export.getProcess(workId);
		if (null == progress) {
			log.error("未查询到指定workid文件: " + LogForgingUtil.validLog(workId));
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
			createZip(info);
		} else {
			downloadName = info.getFilename() + ".xlsx";
		}
		try {
			if(agent != null && agent.toLowerCase(Locale.ENGLISH).indexOf("firefox") > 0) {
				downloadName = "=?UTF-8?B?" + (new String(Base64Utils.encodeToString(downloadName.getBytes("UTF-8")))) + "?=";
			} else {
				downloadName =  java.net.URLEncoder.encode(downloadName, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		resp.setHeader("Content-Disposition", "attachment;filename=" + downloadName);
		try (InputStream in = FileUtils.openInputStream(new File(info.getFilePath()));
			 OutputStream out = resp.getOutputStream();) {
			IOUtils.copy(in, out);
			// 下载完清理引用（防止短时间大量下载导致内存溢出）
			Export.cleanStatus(workId);
		} catch (IOException e) {
			log.error("", e);
		}
	}

	private void createZip(ExcelInfo excelInfo) {
		String excelPath = excelInfo.getFilePath();
		String zipPath = excelPath.replace(".xls", ".zip");
		try {
			// 生成的压缩文件
			ZipFile zipFile = new ZipFile(zipPath);
			ZipParameters parameters = new ZipParameters();
			// 压缩方式
			parameters.setCompressionMethod(CompressionMethod.DEFLATE);
			// 压缩级别
			parameters.setCompressionLevel(CompressionLevel.NORMAL);
			// 是否设置加密文件
			parameters.setEncryptFiles(true);
			// 设置加密算法
			parameters.setEncryptionMethod(EncryptionMethod.AES);
			// 设置AES加密密钥的密钥强度
			parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
			// 设置密码
			zipFile.setPassword(zipPwd.toCharArray());
			// 要打包的文件夹
			zipFile.addFile(excelPath, parameters);

		} catch (Exception e) {
			e.printStackTrace();
		}
		excelInfo.setFilePath(zipPath);
	}

}
