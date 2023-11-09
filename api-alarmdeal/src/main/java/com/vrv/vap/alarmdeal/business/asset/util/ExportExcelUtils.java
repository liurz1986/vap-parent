package com.vrv.vap.alarmdeal.business.asset.util;

import com.vrv.vap.alarmdeal.business.asset.vo.ExcelValidationData;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.util.List;

public class ExportExcelUtils {
	
 
 
	private CellStyle mergedtStyle=null;
	public CellStyle getMergedtStyle(Workbook workbook) {

		
		if(mergedtStyle==null)
		{
			mergedtStyle = workbook.createCellStyle();  
			mergedtStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());  
			mergedtStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//SOLID_FOREGROUND
			mergedtStyle.setBorderBottom(BorderStyle.THIN);
			mergedtStyle.setBorderLeft(BorderStyle.THIN);  
			mergedtStyle.setBorderRight(BorderStyle.THIN);  
			mergedtStyle.setBorderTop(BorderStyle.THIN );  
			mergedtStyle.setAlignment(HorizontalAlignment.GENERAL);

			Font font = this.getDefaultFont(workbook);

	        // 把字体应用到当前的样式  
			  mergedtStyle.setFont(font);  
	  
	        // 指定当单元格内容显示不下时自动换行  
			  mergedtStyle.setWrapText(true);  
			
		}
		
		return mergedtStyle;
	}
	

	private CellStyle defaultStyle=null;

	public CellStyle getDefaultStyle(Workbook workbook) {

		if (defaultStyle == null) {
			defaultStyle = workbook.createCellStyle();
			defaultStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			defaultStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);// SOLID_FOREGROUND
			defaultStyle.setBorderBottom(BorderStyle.THIN);
			defaultStyle.setBorderLeft(BorderStyle.THIN);
			defaultStyle.setBorderRight(BorderStyle.THIN);
			defaultStyle.setBorderTop(BorderStyle.THIN);
			defaultStyle.setAlignment(HorizontalAlignment.GENERAL);

			DataFormat format = workbook.createDataFormat();
			defaultStyle.setDataFormat(format.getFormat("@"));


			Font font = this.getDefaultFont(workbook);

			// 把字体应用到当前的样式
			defaultStyle.setFont(font);

			// 指定当单元格内容显示不下时自动换行
			defaultStyle.setWrapText(true);
		}

		return defaultStyle;
	}
	
	

	private CellStyle headStyle=null;

	public CellStyle getHeadStyle(Workbook workbook) {

		if (headStyle == null) {
			headStyle = workbook.createCellStyle();
			headStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());
			headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);// SOLID_FOREGROUND
			headStyle.setBorderBottom(BorderStyle.THIN);
			headStyle.setBorderLeft(BorderStyle.THIN);
			headStyle.setBorderRight(BorderStyle.THIN);
			headStyle.setBorderTop(BorderStyle.THIN);
			headStyle.setAlignment(HorizontalAlignment.GENERAL);

			DataFormat format = workbook.createDataFormat();
			headStyle.setDataFormat(format.getFormat("@"));


			Font font = this.getDefaultBoldFont(workbook);


			// 把字体应用到当前的样式
			headStyle.setFont(font);

			// 指定当单元格内容显示不下时自动换行
			headStyle.setWrapText(false);

		}

		return headStyle;
	}

 
	private Font _defaultFont=null;
	public Font getDefaultFont(Workbook workbook) {

 

		if (_defaultFont == null) {
			_defaultFont = workbook.createFont();

			//_defaultFont.setColor(org.apache.poi.ss.usermodel.Color.ColorPredefined.BLACK.getIndex());

			_defaultFont.setFontHeightInPoints((short) 10);
			_defaultFont.setBold(false);
		}
		return _defaultFont;
	}
	

	private Font _defaultBoldFont=null;
	public Font getDefaultBoldFont(Workbook workbook) {

		if (_defaultBoldFont == null) {
			_defaultBoldFont = workbook.createFont();
			//_defaultBoldFont.setColor(org.apache.poi.ss.usermodel.Color.BLACK.getIndex());


			_defaultBoldFont.setFontHeightInPoints((short) 12);
			_defaultBoldFont.setBold(true);
		}
		return _defaultBoldFont;
	}

	
	
	/** 
     * @Title: exportExcel 
     * @Description: 导出Excel的方法 
     * @author: evan @ 2014-01-09  
     * @param workbook  
     * @param sheetTitle  （sheet的名称）
     * @param headers    （表格的标题） 
     * @param result   （表格的数据） 
     * @throws Exception
     */  
    public  void exportExcel(HSSFWorkbook workbook,
							 String sheetTitle, String[] headers, List<List<String>> result ) throws Exception {
        // 生成一个表格  
        HSSFSheet sheet = workbook.createSheet(sheetTitle);
        exportExcel(workbook,sheetTitle, headers, result, null);  
    }   
    
    
    public  void  exportExcel(HSSFWorkbook workbook, 
            String sheetTitle, String[] headers, List<List<String>> result,List<ExcelValidationData> validationDatas ) throws Exception {  
        // 生成一个表格  
        HSSFSheet sheet = workbook.createSheet(sheetTitle);  
        exportExcel(workbook, headers, result, sheet);
        
    }

    
    public  void  exportExcel(HSSFWorkbook workbook, 
            String sheetTitle, String[] headers, List<List<String>> result,List<ExcelValidationData> validationDatas,List<String> helpDocuments ) throws Exception {  
        // 生成一个表格  
        HSSFSheet sheet = workbook.createSheet(sheetTitle);  
        setHelpDocument(workbook, headers, helpDocuments, sheet);
        
        exportExcel(workbook, headers, result, sheet);
        
        setSheetDataValidation(workbook, validationDatas, sheet);
        
        
    }

    public  void  exportExcel(HSSFWorkbook workbook,
							  String sheetTitle, String[] headers, List<List<String>> result, List<ExcelValidationData> validationDatas, HSSFRichTextString helpDocuments ) throws Exception {
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(sheetTitle);

		setHelpDocument(workbook, headers, helpDocuments, sheet);
        
        exportExcel(workbook, headers, result, sheet);
        
        setSheetDataValidation(workbook, validationDatas, sheet);

    }


	private void setHelpDocument(HSSFWorkbook workbook, String[] headers, HSSFRichTextString helpDocuments, HSSFSheet sheet) {
		if(helpDocuments==null|| StringUtils.isEmpty(helpDocuments.getString()) )
		{
			return ;
		}
		//在excel 顶部插入帮助文档
		CellStyle style =this.getMergedtStyle(workbook);


        
        //sheet.shiftRows(0,sheet.getLastRowNum(), 1 ); //插入一行：从0行开始到sheet.getLastRowNum行全部下移一行

        HSSFRow helpRow = sheet.createRow(0);
        short height = helpRow.getHeight();
        if(height==0)
        {
        	height=20;
        }
        
        String string = helpDocuments.getString();
        String[] split = string.split("\r\n");
        
        height=(short)(height*(split.length+1));
        helpRow.setHeight(height);
        HSSFCell cell = helpRow.createCell(0);
        
        //cell.setCellStyle(style);  
        
        for(int i=0;i<headers.length;i++)
        {
        	HSSFCell cell2 = helpRow.getCell(i);
        	if(cell2==null)
        	{
        		cell2=helpRow.createCell(i);
        	}
        	cell2.setCellStyle(style);
        }
        
        cell.setCellValue(helpDocuments);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,headers.length-1));
   
     
	}
    

	private void setHelpDocument(HSSFWorkbook workbook, String[] headers, List<String> HelpDocuments, HSSFSheet sheet) {
		if(HelpDocuments==null||HelpDocuments.isEmpty())
		{
			return ;
		}
		//在excel 顶部插入帮助文档
		  // 生成一个样式  
		 CellStyle style = this.getMergedtStyle(workbook);


        
        //sheet.shiftRows(0,sheet.getLastRowNum(), 1 ); //插入一行：从0行开始到sheet.getLastRowNum行全部下移一行

        HSSFRow helpRow = sheet.createRow(0); 
        short height = helpRow.getHeight();
        if(height==0)
        {
        	height=20;
        }
        height=(short)(height*1.05*(HelpDocuments.size()+1));
        helpRow.setHeight(height);
        HSSFCell cell = helpRow.createCell(0);  
        
        for(int i=0;i<headers.length;i++)
        {
        	HSSFCell cell2 = helpRow.getCell(i);
        	if(cell2==null)
        	{
        		cell2=helpRow.createCell(i);
        	}
        	cell2.setCellStyle(style);
        }
        cell.setCellStyle(style);  
        cell.setCellValue(StringUtils.join(HelpDocuments,"\r\n"));
        
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,headers.length-1));
	}
    


	private void setSheetDataValidation(HSSFWorkbook workbook, List<ExcelValidationData> validationDatas,
			HSSFSheet sheet) {
		if(validationDatas!=null&&!validationDatas.isEmpty())
        {
			
	        int index = 0;
	        
	        while(isMergedRegion(sheet,index,0))
	        {
	        	index++;
	        	if(index>10)
	        	{
	        		break;
	        	}
	        }
			
			for (ExcelValidationData validationData : validationDatas) {

				int firstRow = validationData.getFirstRow();
				int lastRow = validationData.getLastRow();
				int firstCol = validationData.getFirstCol();
				int lastCol = validationData.getLastCol();

				if (firstRow < 0 || lastRow < 0 || firstCol < 0 || lastCol < 0 || lastRow < firstRow
						|| lastCol < firstCol) {
					throw new IllegalArgumentException(
							"Wrong Row or Column index : " + firstRow + ":" + lastRow + ":" + firstCol + ":" + lastCol);
				}

				if (firstRow < index+1) {
					firstRow = index+1;
				}

				CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);

				if (validationData.getDvConstraint() != null) {
					DVConstraint dvConstraint = validationData.getDvConstraint();
					if (validationData.getDvConstraint().getExplicitListValues() != null) {
						
						String[] explicitListValues = validationData.getDvConstraint().getExplicitListValues();
						 
						if (explicitListValues.length > 20||StringUtils.join(explicitListValues,",").length()>128) {
							String name = "hidden_"+sheet.getSheetName()+"_" + validationData.getFirstCol() + "_" + validationData.getLastCol();
							HSSFSheet hidden = workbook.getSheet(name);
							if (hidden == null) {
								String[] dataArray = validationData.getDvConstraint().getExplicitListValues();

								hidden = workbook.createSheet(name);
								HSSFCell cell = null;
								for (int i = 0, length = dataArray.length; i < length; i++) {
									String data = dataArray[i];
									HSSFRow row = hidden.createRow(i);
									cell = row.createCell(0);
									cell.setCellValue(data);
								}
								Name namedCell = workbook.createName();
								namedCell.setNameName(name);
								namedCell.setRefersToFormula(name + "!$A$1:$A$" + dataArray.length);

								workbook.setSheetHidden(workbook.getSheetIndex(name), true);
							}
							// 加载数据,将名称为hidden的
							dvConstraint = DVConstraint.createFormulaListConstraint(name);
						}
					}

					DataValidation validation = new HSSFDataValidation(addressList, dvConstraint);

					if (StringUtils.isNotEmpty(validationData.getErrorTitle())
							&& StringUtils.isNotEmpty(validationData.getErrorMsg())) {
						validation.setShowErrorBox(true);
						validation.createErrorBox(validationData.getErrorTitle(), validationData.getErrorMsg());
					}

					if (StringUtils.isNotEmpty(validationData.getPromptTitle())
							&& StringUtils.isNotEmpty(validationData.getPromptContent())) {
						validation.setShowPromptBox(true);
						validation.createPromptBox(validationData.getPromptTitle(), validationData.getPromptContent());
					}

					sheet.addValidationData(validation);
				}
			}
        }
	}

	private static boolean isMergedRegion(Sheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			int firstColumn = range.getFirstColumn();
			int lastColumn = range.getLastColumn();
			int firstRow = range.getFirstRow();
			int lastRow = range.getLastRow();
			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					return true;
				}
			}
		}
		return false;
	}
	private void exportExcel(HSSFWorkbook workbook, String[] headers,
			List<List<String>> result, HSSFSheet sheet) {
		//workbook.setSheetName(sheetNum, sheetTitle);  
        // 设置表格默认列宽度为20个字节  
        sheet.setDefaultColumnWidth(20);  
        // 生成一个样式  

        CellStyle headstyle =this.getHeadStyle(workbook);
        CellStyle defaultstyle = this.getDefaultStyle(workbook);

        int index = 0;
        
       while(isMergedRegion(sheet,index,0))
        {
        	index++;	        	
        	if(index>10)
        	{
        		break;
        	}
        }
        
        
        // 产生表格标题行  
        HSSFRow row = sheet.createRow(index);  
        for (int i = 0; i < headers.length; i++) {  
        	sheet.setDefaultColumnStyle(i, defaultstyle);//设置列默认属性
        	sheet.setColumnWidth(i, 20*256);
        	  
            HSSFCell cell = row.createCell((short) i);  
          

            cell.setCellStyle(headstyle);  
            cell.setCellValue(headers[i]);  

        }  

        index += 1;

        //sheet.createFreezePane(headers.length, index);//固定第一行十五个列
      
//      sheet.createFreezePane( a,b,c,d);  
//      四个参数的含义：
//	        ａ表示要冻结的列数；
//	        ｂ表示要冻结的行数；
//	        ｃ表示右边区域[可见]的首列序号；
//	   ｄ表示下边区域[可见]的首行序号；
        sheet.createFreezePane(0, index+1, 0, index+1);

        // 遍历集合数据，产生数据行  
        if (result != null) {  
       
            for (List<String> m : result) {  
                row = sheet.createRow(index);  
                int cellIndex = 0;  
                for (String str : m) { 
                	if(str==null) {
                		str = "";
                	}
                    HSSFCell cell = row.createCell((short) cellIndex);  
                    cell.setCellValue(str.toString());  
                    cellIndex++;  
                }  
                index++;  
            }  
        }
	}
    
    
    public  void exportExcel(Workbook workbook, int sheetNum,  
            String sheetTitle, String[] headers, List<List<String>> result ) throws Exception {  
        // 生成一个表格  
    	Sheet sheet = workbook.createSheet();
        workbook.setSheetName(sheetNum, sheetTitle);  
        // 设置表格默认列宽度为20个字节  
        sheet.setDefaultColumnWidth((short) 20);  
        // 生成一个样式  
/*        CellStyle style = workbook.createCellStyle();  
        // 设置这些样式  
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());  
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);//SOLID_FOREGROUND  
        style.setBorderBottom(BorderStyle.THIN);  
        style.setBorderLeft(BorderStyle.THIN);  
        style.setBorderRight(BorderStyle.THIN);  
        style.setBorderTop(BorderStyle.THIN );  
        style.setAlignment(HorizontalAlignment.GENERAL);  
        // 生成一个字体  
        Font font = workbook.createFont();  
        font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());  
        font.setFontHeightInPoints((short) 12);  
        font.setBold(true); 
        
        // 把字体应用到当前的样式  
        style.setFont(font);  
  
        // 指定当单元格内容显示不下时自动换行  
        style.setWrapText(true); 
        
        //定义列默认格式化方案

        CellStyle defaultStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        defaultStyle.setDataFormat(format.getFormat("@"));*/


        CellStyle headstyle =this.getHeadStyle(workbook);
        CellStyle defaultstyle = this.getDefaultStyle(workbook);
        
        int index = 0;
        
        while(isMergedRegion(sheet,index,0))
        {
        	index++;
        	if(index>10)
        	{
        		break;
        	}
        }
        
        // 产生表格标题行  
        Row row = sheet.createRow(index);
        for (int i = 0; i < headers.length; i++) {  
           	sheet.setDefaultColumnStyle(i, defaultStyle);//设置列默认属性
           	sheet.setColumnWidth(i, 20*256);
            Cell cell = row.createCell((short) i);  
          
            cell.setCellStyle(headstyle);  
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);  
            cell.setCellValue(text.toString());  
        }  
        
        index+=1;
        sheet.createFreezePane(headers.length-1, index);//固定第一行十五个列
        // 遍历集合数据，产生数据行  
        if (result != null) {  
             
            for (List<String> m : result) {  
                row = sheet.createRow(index);  
                int cellIndex = 0;  
                for (String str : m) { 
                	if(str==null) {
                		str = "";
                	}
                    Cell cell = row.createCell((short) cellIndex);
                    cell.setCellValue(str.toString());  
                    cellIndex++;  
                }  
                index++;  
            }  
        }  
    }
    
    public  void exportExcel(XSSFWorkbook workbook, int sheetNum,  
            String sheetTitle, String[] headers, List<List<String>> result ) throws Exception {  
        // 生成一个表格  
        XSSFSheet sheet = workbook.createSheet();  
        workbook.setSheetName(sheetNum, sheetTitle);  
        // 设置表格默认列宽度为20个字节  
        sheet.setDefaultColumnWidth((short) 20);  
        // 生成一个样式  
        XSSFCellStyle style = workbook.createCellStyle();  
        // 设置这些样式  
        style.setFillForegroundColor(new XSSFColor(Color.WHITE));  
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);//SOLID_FOREGROUND  
        style.setBorderBottom(BorderStyle.THIN);  
        style.setBorderLeft(BorderStyle.THIN);  
        style.setBorderRight(BorderStyle.THIN);  
        style.setBorderTop(BorderStyle.THIN );  
        style.setAlignment(HorizontalAlignment.GENERAL);  
        // 生成一个字体  
        XSSFFont font = workbook.createFont();  
        font.setColor(new XSSFColor(Color.BLACK));
        font.setFontHeightInPoints((short) 12);  
        font.setBold(true); 
        
        // 把字体应用到当前的样式  
        style.setFont(font);  
  
        // 指定当单元格内容显示不下时自动换行  
        style.setWrapText(true);  
  
        int index = 0;
        
        while(isMergedRegion(sheet,index,0))
        {
        	index++;
        	if(index>10)
        	{
        		break;
        	}
        }
        
        // 产生表格标题行  
        XSSFRow row = sheet.createRow(index);  
        for (int i = 0; i < headers.length; i++) {  
            XSSFCell cell = row.createCell((short) i);  
          
            cell.setCellStyle(style);  
            XSSFRichTextString text = new XSSFRichTextString(headers[i]);  
            cell.setCellValue(text.toString());  
        }  
        
        index+=1;
        // 遍历集合数据，产生数据行  
        if (result != null) {  
  
            for (List<String> m : result) {  
                row = sheet.createRow(index);  
                int cellIndex = 0;  
                for (String str : m) { 
                	if(str==null) {
                		str = "";
                	}
                    XSSFCell cell = row.createCell((short) cellIndex);  
                    cell.setCellValue(str.toString());  
                    cellIndex++;  
                }  
                index++;  
            }  
        }  
    }   
	
}
