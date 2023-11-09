package com.vrv.vap.alarmdeal.frameworks.util;

import com.vrv.vap.alarmdeal.business.baseauth.service.impl.BaseAuthConfigServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Paths;

/**
 * 文件模板下载
 *
 *  2023-08
 */
public class FileTemplateUtil {
    private static Logger logger = LoggerFactory.getLogger(FileTemplateUtil.class);

    /**
     * 下载templates目录下模板信息
     *
     * @param response
     * @param fileName
     */
    public static  void downloadExportTemplate(HttpServletResponse response,String fileName) {
        fileName = fileName+".xls";
        ClassPathResource classPathResource = new ClassPathResource(Paths.get("/templates", fileName).toString());
        try  {
            InputStream fis = classPathResource.getInputStream();
            ServletOutputStream out = response.getOutputStream();
            HSSFWorkbook workbook = new HSSFWorkbook(fis);
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType("application/binary;charset=utf-8");
            workbook.write(out);
            out.flush();
        } catch (Exception e) {
            logger.error("下载"+fileName+"失败",  e);
        }
    }
}
