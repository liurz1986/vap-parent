package com.vrv.vap.xc.controller.behavior;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.toolkit.constant.Common;
import com.vrv.vap.toolkit.excel.ExcelInfo;
import com.vrv.vap.toolkit.excel.out.Export;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
public class DownloadController {
    private static final Log log = LogFactory.getLog(PrintStatisticsController.class);
    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/download/excel/{workId}")
    @ApiOperation("根据workid下载文件")
    public void download(@PathVariable("workId") String workId, HttpServletRequest req, HttpServletResponse resp) {
        Export.Progress progress = Export.getProcess(workId);
        String downloadName;
        String filePath;
        String channle = "local";
        if (null == progress) {
            String proStr = (String) redisTemplate.opsForValue().get(Common.EXPORT_REDIS_PRO_PATH + workId);
            JSONObject jsonObject = JSONObject.parseObject(proStr);
            if (jsonObject == null) {
                log.error("未查询到指定workid文件: " + workId);
                return;
            }
            JSONObject subObject = (JSONObject) jsonObject.get("firstExcelInfo");
            downloadName = subObject.getString("filename");
            filePath = subObject.getString("filePath");
            channle = "redis";
        } else {
            ExcelInfo info = progress.getFirstExcelInfo();
            String path = info.getFilePath();
            downloadName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf('.'));
            filePath = info.getFilePath();
        }

        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("content-type", "application/octet-stream");
        resp.setContentType("application/octet-stream");
        // 兼容火狐浏览器导出文件名乱码问题
        String agent = req.getHeader("USER-AGENT");
        try {
            if (agent != null && agent.toLowerCase().indexOf("firefox") > 0) {
                downloadName = "=?UTF-8?B?" + (Base64Utils.encodeToString(downloadName.getBytes(StandardCharsets.UTF_8))) + "?=";
            } else {
                downloadName = java.net.URLEncoder.encode(downloadName, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        resp.setHeader("Content-Disposition", "attachment;filename=" + downloadName + ".xls");
        OutputStream out = null;
        InputStream in = null;
        try {
            out = resp.getOutputStream();
            if ("local".equals(channle)) {
                in = FileUtils.openInputStream(new File(filePath));
                IOUtils.copy(in, out);
            } else {
                List blist = redisTemplate.opsForList().range(Common.EXPORT_REDIS_FILE_PATH + workId + "_file", 0, -1);
                int lengthTotal = 0;
                for (Object item : blist) {
                    byte[] tmp = (byte[]) item;
                    lengthTotal += tmp.length;
                }
                byte[] totalByte = new byte[lengthTotal];
                int begin = 0;
                for (Object item : blist) {
                    byte[] tmp = (byte[]) item;
                    System.arraycopy(tmp, 0, totalByte, begin, tmp.length);
                    begin += tmp.length;
                }
                out.write(totalByte);
            }
        } catch (IOException e) {
            log.error("", e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
}
