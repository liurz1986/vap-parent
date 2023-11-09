package com.vrv.vap.netflow.common.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lilang
 * @date 2019/1/15
 * @description
 */
public class ImportExcelUtil {

    private final static String excel2003L = ".xls";    //2003- 版本的excel
    private final static String excel2007U = ".xlsx";   //2007+ 版本的excel

    /**
     * 描述：获取IO流中的数据，组装成List<List<Object>>对象
     *
     * @param in,fileName
     * @return
     * @throws
     */
    public static List<List<Object>> getListByExcel(InputStream in, String fileName) throws Exception {
        List<List<Object>> list = null;

        //创建Excel工作薄
        Workbook work = getWorkbook(in, fileName);
        if (null == work) {
            throw new Exception("创建Excel工作薄为空！");
        }
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;

        list = new ArrayList<List<Object>>();
        //遍历Excel中所有的sheet
        //确定总列数
        int cloumnStart = 0;
        int cloumnEnd = 21;
        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            sheet = work.getSheetAt(i);
            if (sheet == null) {
                continue;
            }

            //遍历当前sheet中的所有行
            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);
                if (row != null && row.getFirstCellNum() == j) {
                    cloumnEnd = row.getLastCellNum();
                    cloumnStart = row.getFirstCellNum();
                }
                if (row == null) {
                    continue;
                }

                //遍历所有的列
                List<Object> li = new ArrayList<Object>();
                for (int y = cloumnStart; y < cloumnEnd; y++) {
                    cell = row.getCell(y);
                    if (cell == null) {
                        li.add(null);
                    } else {
                        li.add(getCellValue(cell));
                    }
                }
                list.add(li);
            }
        }
        in.close();
        return list;
    }

    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     *
     * @param inStr,fileName
     * @return
     * @throws Exception
     */
    public static Workbook getWorkbook(InputStream inStr, String fileName) throws Exception {
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if (excel2003L.equals(fileType)) {
            wb = new HSSFWorkbook(inStr);  //2003-
        } else if (excel2007U.equals(fileType)) {
            wb = new XSSFWorkbook(inStr);  //2007+
        } else {
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }

    /**
     * 描述：对表格中数值进行格式化
     * @param cell
     * @return
     */
    public static Object getCellValue(Cell cell){
        Object value = null;
        DecimalFormat df = new DecimalFormat("0");  //格式化number String字符
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");  //日期格式化
        DecimalFormat df2 = new DecimalFormat("0.00");  //格式化数字
        if(cell.getCellType() == CellType.STRING){
            value = cell.getRichStringCellValue().getString();
        }
        if(cell.getCellType() == CellType.NUMERIC){
            if("General".equals(cell.getCellStyle().getDataFormatString())){
                value = df.format(cell.getNumericCellValue());
            }else if("m/d/yy".equals(cell.getCellStyle().getDataFormatString())){
                value = sdf.format(cell.getDateCellValue());
            }else{
                value = df2.format(cell.getNumericCellValue());
            }
        }
        if(cell.getCellType() == CellType.BOOLEAN){
            value = cell.getBooleanCellValue();
        }
        if(cell.getCellType() == CellType.BLANK){
            value = "";
        }
        return value;
    }
}
