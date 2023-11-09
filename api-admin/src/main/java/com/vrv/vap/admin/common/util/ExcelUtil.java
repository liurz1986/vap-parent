package com.vrv.vap.admin.common.util;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @Author tongliang@VRV
 * @CreateTime 2019/01/09 19:57
 * @Description (Excel导入和Excel模板下载工具)
 * @Version
 */
@Slf4j
public class ExcelUtil {

    /**
     * @Description (Excel导入)
     */
    public static <T> List<T> importExcel(InputStream io, Integer titleRows,
                                          Integer headerRows, Class<T> pojoClass) {
        if (io == null){
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        // 需要验证
        params.setNeedVerify(true);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(io, pojoClass, params);
        } catch (Exception e) {
            log.error("ExcelUtil工具类中importExcel方法异常！"+e.toString());
        }
        return list;
    }



    /**
     * 导出Excel，包括文件名以及表名。是否创建表头
     *
     * @param list 导出的实体类
     * @param title 表头名称
     * @param sheetName sheet表名
     * @param pojoClass 映射的实体类
     * @param isCreateHeader 是否创建表头
     * @param fileName 文件名
     * @param response
     * @return
     */
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, boolean isCreateHeader, HttpServletResponse response){
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, fileName, response, exportParams);

    }

    /**
     * 导出Excel 默认格式 默认有创建表头
     */
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass,String fileName, HttpServletResponse response){
        defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName));
    }

    /**
     * 导出Excel 默认格式 默认有创建表头
     */
    public static void exportLocalExcel(List<?> list, String title, String sheetName, Class<?> pojoClass,String fileName, HttpServletResponse response) throws IOException {
        defaultLocalExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName));
    }


    private static void defaultLocalExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) throws IOException {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams,pojoClass,list);
        ExcelExportUtil.closeExportBigExcel();
        if (workbook != null) {
            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                workbook.write(fos);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * map多sheet形式导出
     * @param list
     * @param fileName
     * @param response
     */
    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response){
        defaultExport(list, fileName, response);
    }

    /**
     * 常规默认导出方式
     * @param list
     * @param pojoClass
     * @param fileName
     * @param response
     * @param exportParams
     */
    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams,pojoClass,list);
        ExcelExportUtil.closeExportBigExcel();
        if (workbook != null);
        downLoadExcel(fileName, response, workbook);
    }

    /**
     * 多sheet默认导出方式
     * @param list
     * @param fileName
     * @param response
     */
    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        ExcelExportUtil.closeExportBigExcel();
        if (workbook != null);
        downLoadExcel(fileName, response, workbook);
    }

    /**
     * 下载excel
     * @param fileName
     * @param response
     * @param workbook
     */
    private static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
           // throw new ZXException(e.getMessage());
        }
    }

    /**
     * 导入 文件路径形式
     * @param filePath
     * @param titleRows
     * @param headerRows
     * @param pojoClass
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(String filePath,Integer titleRows,Integer headerRows, Class<T> pojoClass){
        if (StringUtils.isBlank(filePath)){
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        }catch (NoSuchElementException e){
            throw new RuntimeException("模板不能为空");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return list;
    }

    /**
     * 导入 MultipartFile 形式
     * @param file
     * @param titleRows
     * @param headerRows
     * @param pojoClass
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass){
        if (file == null){
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try(InputStream in = file.getInputStream()) {
            list = ExcelImportUtil.importExcel(in, pojoClass, params);
        }catch (NoSuchElementException e){
            throw new RuntimeException("excel文件不能为空");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return list;
    }

    /**
     * @Description (下载项目resource路径下的Excel模板)
     * @param fileName 文件名包括后缀
     */
    public static void downLoadModelExcel(String fileName, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        BufferedInputStream bufferedInputStream = null;
        try {
            String agent = request.getHeader("USER-AGENT").toLowerCase();
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            if (agent.contains("firefox")) {
                response.setCharacterEncoding("utf-8");
                response.setHeader("content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
            } else {
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            }
            byte[] buff = new byte[1024];
            Resource resource = new ClassPathResource("excel/"+fileName);
            bufferedInputStream = new BufferedInputStream(resource.getInputStream());
            ServletOutputStream outputStream = response.getOutputStream();
            int i = bufferedInputStream.read(buff);
            while (i != -1) {
                outputStream.write(buff, 0, buff.length);
                outputStream.flush();
                i = bufferedInputStream.read(buff);
            }
        } catch (IOException e) {
            log.error("ExcelUtil工具类中downLoadExcel方法异常！");
        } finally {
            if (bufferedInputStream != null){
                bufferedInputStream.close();
            }
        }
    }

}
