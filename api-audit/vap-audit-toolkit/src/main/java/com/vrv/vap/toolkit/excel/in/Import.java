package com.vrv.vap.toolkit.excel.in;

import com.vrv.vap.toolkit.excel.ExcelInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * excel数据读取工具
 *
 * @author xw
 * @date 2018年4月2日
 */
public final class Import {
    private static final Log log = LogFactory.getLog(Import.class);

    /**
     * excel数据转为list map
     *
     * @param excel         导入的excel描述(filePath和columns必填)
     * @param importHandler 自定义的数据处理接口
     * @return
     */
    public static <T> List<Map<String, Object>> getExcelData(ExcelInfo excel, ImportHandler importHandler) {
        log.info("开始读取excel文件:" + excel.getFilePath());
        List<Map<String, Object>> list = importHandler.toListMap(excel);
        log.info("结束读取excel文件:" + excel.getFilePath());
        return list;
    }

    /**
     * excel数据转为list map
     *
     * @param excel
     * @return
     */
    public static List<Map<String, Object>> getExcelData(ExcelInfo excel) {
        return getExcelData(excel, e -> {
            File file = new File(e.getFilePath());
            Workbook workBook = createWorkbook(file);
            List<Sheet> list = new ArrayList<>();
            workBook.sheetIterator().forEachRemaining(list::add);
            List<Map<String, Object>> result = list.parallelStream().map(sheet -> {
                List<Map<String, Object>> tmp = new LinkedList<>();
                sheet.forEach(row -> {
                    Map<String, Object> data = new HashMap<>();
                    for (int i = 0; i < e.getColumns().length; i++) {
                        data.put(e.getColumns()[i], getValue(row.getCell(i)));
                    }
                    tmp.add(data);
                });
                // 删除第一行
                if (excel.isSkipFirstLine()) {
                    tmp.remove(0);
                }
                return tmp;
            }).reduce((a, b) -> {
                a.addAll(b);
                return a;
            }).get();
            close(workBook);
            return result;
        });
    }

    /**
     * excel数据转为list map
     * 只获取第一个sheet
     *
     * @param excel
     * @return
     */
    public static List<Map<String, Object>> getFirstSheetExcelData(ExcelInfo excel) {
        return getExcelData(excel, e -> {
            File file = new File(e.getFilePath());
            Workbook workBook = createWorkbook(file);
            Sheet sheet = workBook.getSheetAt(0);
            List<Map<String, Object>> tmp = new LinkedList<>();
            sheet.forEach(row -> {
                Map<String, Object> data = new HashMap<>();
                for (int i = 0; i < e.getColumns().length; i++) {
                    data.put(e.getColumns()[i], getValue(row.getCell(i)));
                }
                tmp.add(data);
            });
            // 删除第一行
            if (excel.isSkipFirstLine()) {
                tmp.remove(0);
            }
            close(workBook);
            return tmp;
        });
    }

    /**
     * excel数据转为list map
     * 获取第n个sheet
     *
     * @param excel
     * @return
     */
    public static List<Map<String, Object>> getSelectSheetExcelData(ExcelInfo excel, int sheetIndex) {
        return getExcelData(excel, e -> {
            File file = new File(e.getFilePath());
            Workbook workBook = createWorkbook(file);
            Sheet sheet = workBook.getSheetAt(sheetIndex);
            List<Map<String, Object>> tmp = new LinkedList<>();
            sheet.forEach(row -> {
                Map<String, Object> data = new LinkedHashMap<>();
                for (int i = 0; i < e.getColumns().length; i++) {
                    data.put(e.getColumns()[i], getValue(row.getCell(i)));
                }
                tmp.add(data);
            });
            // 删除第一行
            if (e.isSkipFirstLine()) {
                tmp.remove(0);
            }
            close(workBook);
            return tmp;
        });
    }

    private static Workbook createWorkbook(File file) {
        try {
            return new HSSFWorkbook(new POIFSFileSystem(file));
        } catch (Exception e) {
            try {
                log.error(e);
                return new XSSFWorkbook(file);
            } catch (InvalidFormatException | IOException e1) {
                log.error(e1);
                return null;
            }
        }
    }

    private static void close(Workbook workBook) {
        try {
            if (null != workBook) {
                workBook.close();
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

    private static String getValue(Cell cell) {
        if (null == cell) {
            return "";
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue().trim();
            case Cell.CELL_TYPE_NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue()).trim();
            default:
                return "";
        }
    }

//	public static void main(String[] args) {
//		getExcelData(new Excel(null, new String[] { "地区", "用户名", "身份证", "系统名称", "单位", "终端IP", "操作时间", "操作类型", "操作条件" },
//				null, null, true, "f:/test/应用审计-20171114.562.xls", null)).forEach(System.out::println);
//	}
}
