package com.vrv.vap.data.util;

import com.vrv.vap.common.exception.ApiException;
import com.vrv.vap.data.component.config.DictConfig;
import com.vrv.vap.data.constant.ErrorCode;
import com.vrv.vap.data.model.Maintain;
import com.vrv.vap.data.model.Source;
import com.vrv.vap.data.model.SourceField;
import com.vrv.vap.data.util.excel.SourceExporter;
import com.vrv.vap.data.util.excel.SourceImporter;
import com.vrv.vap.data.util.excel.SourceTemplate;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


/**
 * 使用说明
 * http://gael-home.appspot.com/home/WriteExcel/overview.htm
 */
public class ExcelUtil {

    public static void createTemplate(OutputStream outputStream, String name, String pk, List<SourceField> fields) {
        SourceTemplate sourceTemplate = new SourceTemplate(name, pk, fields);
        try {
            sourceTemplate.writeStream(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void exportData(OutputStream outputStream, List<SourceField> fields, List list, DictConfig dict) {
        try {
//            System.out.println(list.size());
//            System.out.println(fields.size());
            SourceExporter sourceExporter = new SourceExporter(fields, list, dict);
            sourceExporter.write(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static int importData(InputStream inputStream, boolean isXlsx, DataSource dataSource, Maintain maintain, Source source, List<SourceField> fields, DictConfig dict) throws ApiException {
        SourceImporter sourceImporter = null;
        try {
            sourceImporter = new SourceImporter(inputStream, isXlsx, dataSource, maintain, source, fields, dict);
        } catch (IOException e) {
            throw new ApiException(ErrorCode.MAINTAIN_TEMPLATE_FORMAT_ERROR.getResult().getCode(),ErrorCode.MAINTAIN_TEMPLATE_FORMAT_ERROR.getResult().getMessage());
        }
        int rowCount = sourceImporter.run();
        System.out.println(rowCount);
        return rowCount;
    }
}
