package com.vrv.vap.data.util.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.vrv.vap.data.component.config.DictConfig;
import com.vrv.vap.data.model.SourceField;
import com.vrv.vap.data.util.TimeTools;
import com.vrv.vap.data.util.excel.core.WriteExcel;
import org.apache.commons.lang3.StringUtils;

import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class SourceExporter extends WriteExcel {

    private List<SourceField> fields;
    private List<Map<String, Object>> sqlResult;
    private List<JsonNode> esResult;
    private DictConfig dict;

    private Map<String, String> DATE_PARSER_MAP = new HashMap<>();

    private boolean isSQL = true;
    private Area sheet;


    public SourceExporter(List<SourceField> fields, List list, DictConfig dict) {
        this.fields = fields.stream().filter(field -> field.getShow() && StringUtils.isNotBlank(field.getName())).collect(Collectors.toList());
        this.dict = dict;
        if (list.size() <= 0) {
            this.sqlResult = new ArrayList<>();
        } else {
            Object obj = list.get(0);
            if (obj instanceof Map) {
                this.sqlResult = list;
            } else if (obj instanceof JsonNode) {
                this.isSQL = false;
                this.esResult = list;
            } else {
                this.sqlResult = new ArrayList<>();
            }
        }
    }

    public void write(OutputStream outputStream) throws Exception {
        WriteExcel writer = WriteExcel.create(this, "template.xlsx");
        sheet = writer.createArea("Sheet", 0, 0);
        this.writeTitle();
        if (this.isSQL) {
            this.writeSQLContent();
        } else {
            this.writeESContent();
        }
        this.format();
        writer.closeByStream(outputStream);
    }

    private void writeTitle() throws Exception {
        String header = this.fields.stream().map(SourceField::getName).collect(Collectors.joining("/"));
        sheet.header(header, "#hdrBlue").writeArea();
    }


    private String formatNumber(String number, String unit) {
        // TODO
        return number;
    }

    private String formatField(String value, SourceField field) {
        if (value == null) {
            return null;
        }
        switch (field.getType()) {
            case "date":
                try {
                    String fieldName = field.getField();
                    if (!DATE_PARSER_MAP.containsKey(fieldName)) {
                        String pattern = TimeTools.getPattern(value);
                        DATE_PARSER_MAP.put(fieldName, pattern);
                    }
                    String pattern = DATE_PARSER_MAP.get(fieldName);
                    if (TimeTools.UTC_PTN.equals(pattern)) {
                        return TimeTools.utc2Local(value,TimeTools.UTC_PTN, TimeTools.GMT_PTN);
                    } else {
                        return TimeTools.format(TimeTools.parse(value, pattern), TimeTools.GMT_PTN);
                    }
                } catch (Exception e) {
                    return value;
                }
            case "long":
            case "double":
                String unit = field.getUnit();
                if (StringUtils.isNotBlank(unit)) {
                    return this.formatNumber(value, unit);
                }
            case "keyword":
                if (StringUtils.isNotBlank(field.getDict())) {
                    return this.dict.getString(field.getDict(), value);
                }
            default:
                break;
        }


        return value;
    }


    private void writeSQLContent() throws Exception {
        int rowSize = fields.size();
        for (int i = 0, _i = sqlResult.size(); i < _i; i++) {
            Map<String, Object> item = sqlResult.get(i);
            String[] row = new String[rowSize];
            for (int j = 0; j < rowSize; j++) {
                SourceField field = fields.get(j);
                Object value = item.get(field.getField());
                String str = this.formatField(value == null ? null : value.toString(), field);
                row[j] = str;
            }
            if (i % 2 == 0) {
                sheet.addRow(row, "#evn");
            } else {
                sheet.addRow(row, "#odd");
            }
        }
        sheet.writeArea();
    }

    private void writeESContent() throws Exception {
        int rowSize = fields.size();
        for (int i = 0, _i = esResult.size(); i < _i; i++) {
            JsonNode item = esResult.get(i);
            String[] row = new String[rowSize];
            for (int j = 0; j < rowSize; j++) {
                SourceField field = fields.get(j);
                String value = item.get(field.getField()) == null ? null : item.get(field.getField()).asText("");
                String str = this.formatField(value, field);
                row[j] = str;
            }
            if (i % 2 == 0) {
                sheet.addRow(row, "#evn");
            } else {
                sheet.addRow(row, "#odd");
            }
        }
        sheet.writeArea();
    }

    private void format() throws Exception {
        for (int i = 0, _i = this.fields.size(); i < _i; i++) {
            SourceField field = this.fields.get(i);
            switch (field.getType()) {
                case "long":
                case "double":
                    sheet.colWidth(i, 20);
                    break;
                case "date":
                    sheet.colWidth(i, 24);
                    break;
                case "text":
                    sheet.colWidth(i, 40);
                    break;
                default:
                    sheet.colWidth(i, 20);
                    break;
            }
        }

    }


}
