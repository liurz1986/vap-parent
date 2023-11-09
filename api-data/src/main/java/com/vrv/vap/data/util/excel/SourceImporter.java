package com.vrv.vap.data.util.excel;

import com.vrv.vap.common.exception.ApiException;
import com.vrv.vap.data.component.config.DictConfig;
import com.vrv.vap.data.constant.ErrorCode;
import com.vrv.vap.data.model.Maintain;
import com.vrv.vap.data.model.Source;
import com.vrv.vap.data.model.SourceField;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据导入工具
 */
public class SourceImporter {

    private DataSource dataSource;
    private Source source;
    private List<SourceField> fields;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Workbook oWB;
    private Sheet sheet;

    private int FIRST = 0;
    private int LAST = 0;
    private int LEN = 0;

    private CellReader[] cells;

    public SourceImporter(InputStream inputStream, boolean isXlsx, DataSource dataSource, Maintain maintain, Source source, List<SourceField> fields, DictConfig dict) throws IOException {
        this.oWB = isXlsx ? new XSSFWorkbook(inputStream) : new HSSFWorkbook(inputStream);
        this.dataSource = dataSource;
        this.source = source;
        this.fields = fields.stream().filter(field -> !maintain.getPrimaryKey().equals(field.getField())).collect(Collectors.toList());
        this.LEN = this.fields.size();
        this.cells = new CellReader[this.LEN];
        for (int i = 0; i < this.LEN; i++) {
            this.cells[i] = new CellReader(this.fields.get(i), dict);
        }
    }


    public int run() throws ApiException {
        logger.info("Start Import Data");
        if (oWB == null) {
            logger.error(ErrorCode.MAINTAIN_TEMPLATE_FAIL.getResult().getMessage());
            throw new ApiException(ErrorCode.MAINTAIN_TEMPLATE_FAIL.getResult().getCode(),ErrorCode.MAINTAIN_TEMPLATE_FAIL.getResult().getMessage());
        }
        sheet = oWB.getSheetAt(0);
        FIRST = sheet.getFirstRowNum();
        LAST = sheet.getLastRowNum();
        if ((LAST - FIRST) <= 1) {
            throw new ApiException(ErrorCode.MAINTAIN_TEMPLATE_EMPTY.getResult().getCode(),ErrorCode.MAINTAIN_TEMPLATE_EMPTY.getResult().getMessage());
        }
        StringBuffer sb = new StringBuffer();
        this.genHeader(sb);
        this.genContent(sb);
        int rowEffected = this.importData(sb);
        try {
            oWB.close();

        } catch (IOException e) {
            logger.error("Close Stream Fail", e);
        }
//        if (!success) {
//            throw new ApiException(ErrorCode.MAINTAIN_TEMPLATE_EMPTY);
//        }
        return rowEffected;

    }


    private void genHeader(StringBuffer sb) throws ApiException {
        Row header = sheet.getRow(FIRST);
        int len = fields.size();
        if (header.getLastCellNum() != len) {
            throw new ApiException(ErrorCode.MAINTAIN_TEMPLATE_ERROR.getResult().getCode(),ErrorCode.MAINTAIN_TEMPLATE_ERROR.getResult().getMessage()); // 模板模式有误
        }
        sb.append("INSERT INTO `").append(source.getName()).append("` (");
        for (int i = 0; i < len; i++) {
            Cell cell = header.getCell(i);
            SourceField field = fields.get(i);
            if (!field.getName().equalsIgnoreCase(cell.getStringCellValue().trim())) {
                throw new ApiException(ErrorCode.MAINTAIN_TEMPLATE_ERROR.getResult().getCode(),ErrorCode.MAINTAIN_TEMPLATE_ERROR.getResult().getMessage()); // 模板模式有误
            }
            sb.append('`').append(field.getField()).append("`,");

        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(") VALUES ");
    }

    private void genContent(StringBuffer sb) {
        for (int _i = FIRST + 1; _i < LAST; _i++) {
            sb.append('(');
            Row row = sheet.getRow(_i);
            for (int i = 0; i < LEN; i++) {
                Cell cell = row.getCell(i);
                String value = this.cells[i].getValue(cell);
                sb.append(value);
                if (i < (LEN - 1)) {
                    sb.append(',');
                }
            }
            sb.append(')');
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
    }

    private int importData(StringBuffer sb) throws ApiException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.dataSource.getConnection();
            String sql = sb.toString();
            logger.info("IMPORT SQL : " + sql);
            ps = conn.prepareStatement(sql);
            int result = ps.executeUpdate();
            return result;

        } catch (SQLException e) {
            logger.error("SQL ERROR", e);
            throw new ApiException(ErrorCode.MAINTAIN_TEMPLATE_EMPTY.getResult().getCode(),ErrorCode.MAINTAIN_TEMPLATE_EMPTY.getResult().getMessage());
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    ps = null;
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    conn = null;
                }
            }
        }
    }

}
