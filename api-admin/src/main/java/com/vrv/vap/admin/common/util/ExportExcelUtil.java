package com.vrv.vap.admin.common.util;

import com.vrv.vap.admin.vo.SysRequestLogVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;


public class ExportExcelUtil {
    private static final String EMPTY_STRING = "";
    /**
     * 设置excel表格样式
     *
     * @param wb
     * @return
     */
    public static CellStyle setUpCellStyle(HSSFWorkbook wb) {
        // 设置除标题行以外的样式
        CellStyle styleBold = wb.createCellStyle();
        // 设置边框下边框样式
        styleBold.setBorderBottom(BorderStyle.THIN);
        styleBold.setBottomBorderColor(IndexedColors.BLACK.index);
        // 设置边框左边框样式
        styleBold.setBorderLeft(BorderStyle.THIN);
        styleBold.setLeftBorderColor(IndexedColors.BLACK.index);
        // 设置边框上边框样式
        styleBold.setBorderTop(BorderStyle.THIN);
        styleBold.setTopBorderColor(IndexedColors.BLACK.index);
        // 设置边框右边框样式
        styleBold.setBorderRight(BorderStyle.THIN);
        styleBold.setRightBorderColor(IndexedColors.BLACK.index);
        // 垂直居中
        styleBold.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = wb.createFont();
        // 字体高度
        font.setFontHeightInPoints((short) 11);
        styleBold.setFont(font);
        // 水平布局：居中
        styleBold.setAlignment(HorizontalAlignment.CENTER);
        //设置文字自动换行
        styleBold.setWrapText(true);
        return styleBold;
    }

    /**
     * 设置excel表头样式
     *
     * @param wb
     * @return
     */
    public static CellStyle setUpTitleCellStyle(HSSFWorkbook wb) {
        // 设置标题行样式
        CellStyle headerStyle = wb.createCellStyle();
        // 设置背景色
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE
                .getIndex());
        // 设置边框下边框
        headerStyle.setBorderBottom(BorderStyle.THIN);
        // 设置边框左边框
        headerStyle.setBorderLeft(BorderStyle.THIN);
        // 设置边框右边框
        headerStyle.setBorderRight(BorderStyle.THIN);
        // 设置边框上边框
        headerStyle.setBorderTop(BorderStyle.THIN);
        // 水平居中
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        // 垂直居中
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font headerFont = wb.createFont();
        // 字体高度
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.index);
        headerStyle.setFont(headerFont);
        return headerStyle;
    }


    public static void  writeDateToCell(HSSFWorkbook wb, List<SysRequestLogVO> dataList, HSSFSheet sheet, List<String> titleList) {
        //设置cell样式
        CellStyle styleBold = setUpCellStyle(wb);
        //设置表头样式
        CellStyle headerStyle = setUpTitleCellStyle(wb);
        Row row = sheet.createRow(0);
        //设置标题行
        int titleColNum = 0;
        // 设置行高亮
        row.setHeightInPoints(20);
        // 设置列宽
        for (int colNum = 0; colNum < titleList.size(); colNum++) {
            sheet.setColumnWidth(colNum, 6000);
        }
        for (String title : titleList) {
            row.createCell(titleColNum).setCellStyle(headerStyle);
            row.getCell(titleColNum).setCellValue(title);
            titleColNum++;
        }
        // 设置数据行
        if(CollectionUtils.isNotEmpty(dataList)){
            for (short i = 1; i < dataList.size() + 1; i++) {
                // 创建行
                row = sheet.createRow(i);
                SysRequestLogVO sysRequestLogVO = dataList.get(i-1);
                for (int colNum = 0; colNum < titleList.size(); colNum++) {
                    row.createCell(colNum).setCellStyle(styleBold);
                    row.getCell(colNum).setCellValue(getValue(colNum, sysRequestLogVO).toString());
                }
            }
        }
    }

    private static Object getValue(int i, SysRequestLogVO sysRequestLogVO) {
        switch (i) {
            case 0:
                return Optional.ofNullable(sysRequestLogVO.getId()).orElse(EMPTY_STRING);
            case 1:
                return Optional.ofNullable(sysRequestLogVO.getUserId()).orElse(EMPTY_STRING);
            case 2:
                return Optional.ofNullable(sysRequestLogVO.getUserName()).orElse(EMPTY_STRING);
            case 3:
                return Optional.ofNullable(sysRequestLogVO.getOrganizationName()).orElse(EMPTY_STRING);
            case 4:
                return Optional.ofNullable(sysRequestLogVO.getRequestIp()).orElse(EMPTY_STRING);
            case 5:
                return Optional.ofNullable(sysRequestLogVO.getMethodName()).orElse(EMPTY_STRING);
            case 6:
                return Optional.ofNullable(sysRequestLogVO.getRequestMethod()).orElse(EMPTY_STRING);
            case 7:
                return sysRequestLogVO.getLoginType() != null ? getValueForLoginType(sysRequestLogVO.getLoginType()) : EMPTY_STRING;
            case 8:
                return sysRequestLogVO.getType() != null ? getValueForType(sysRequestLogVO.getType()) : EMPTY_STRING;
            case 9:
                return sysRequestLogVO.getRequestTime() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(sysRequestLogVO.getRequestTime()) : EMPTY_STRING;
            case 10:
                return Optional.ofNullable(sysRequestLogVO.getParamsValue()).orElse(EMPTY_STRING);
            case 11:
                return Optional.ofNullable(sysRequestLogVO.getDescription()).orElse(EMPTY_STRING);
            case 12:
                return sysRequestLogVO.getResponseResult() != null ? getValueForResult(sysRequestLogVO.getResponseResult()) : EMPTY_STRING;
            default:
                return EMPTY_STRING;
        }
    }

    private static Object getValueForType(int i) {
        switch (i) {
            case 0:
                return "登录";
            case 1:
                return "查询";
            case 2:
                return "新增";
            case 3:
                return "修改";
            case 4:
                return "删除";
            case 5:
                return "退出";
            case 6:
                return "浏览";
            default:
                return "";
        }
    }

    private static Object getValueForResult(int i) {
        switch (i) {
            case 1:
                return "成功";
            default:
                return "失败";
        }
    }

    private static Object getValueForLoginType(int i) {
        switch (i) {
            case 0:
                return "普通登录";
            case 1:
                return "证书登录";
            case 2:
                return "虹膜登录";
            default:
                return "";
        }
    }
}

