package com.vrv.vap.alarmdeal.frameworks.util;

import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellType;

public class ExcelUtil {

    public static void exportExcel(HSSFWorkbook workbook, int sheetNum,
                                   String sheetTitle, List<String> headers, List<?> result,
                                   OutputStream out) throws Exception {
// 生成一个表格
        HSSFSheet sheet = workbook.createSheet();
        workbook.setSheetName(sheetNum, sheetTitle);
// 设置表格默认列宽度为20个字节
        sheet.setDefaultColumnWidth((short) 20);
// 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
// 设置这些样式
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());

        style.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_ORANGE.getIndex());

// 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        font.setFontHeightInPoints((short) 12);
// 把字体应用到当前的样式
        style.setFont(font);

// 指定当单元格内容显示不下时自动换行
        style.setWrapText(true);

// 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            HSSFCell cell = row.createCell((short) i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headers.get(i));
            cell.setCellValue(text.toString());
        }
// 遍历集合数据，产生数据行
        if (result != null) {
            Iterator iterator = result.iterator();
            int index=1;
            while(iterator.hasNext()){
                Object object = iterator.next();
                row=sheet.createRow(index);
                if (object.getClass().isArray()) {
                    for(int j = 0; j < Array.getLength(object); ++j) {
                        row.createCell(j, CellType.STRING).setCellValue(Array.get(object, j).toString());
                    }
                } else if (object instanceof Collection) {
                    Collection<?> items = (Collection)object;
                    int j = 0;

                    Object item;
                    for(Iterator var13 = items.iterator(); var13.hasNext(); row.createCell(j++, CellType.STRING).setCellValue(item.toString())) {
                        item = var13.next();
                        if (item == null) {
                            item = "";
                        }
                    }
                } else {
                    row.createCell(0, CellType.STRING).setCellValue(object.toString());
                }
                index++;
            }

        }
    }
}
