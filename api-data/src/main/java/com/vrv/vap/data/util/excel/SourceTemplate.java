package com.vrv.vap.data.util.excel;

import com.vrv.vap.data.model.SourceField;
import com.vrv.vap.data.util.excel.core.WriteExcel;
import org.apache.commons.lang3.StringUtils;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据模板下载器
 */
public class SourceTemplate extends WriteExcel {

    private String name;
    private String pk;
    private List<SourceField> fields;
    private WriteExcel writer;

    public SourceTemplate(String name, String pk, List<SourceField> fields) {
        this.name = name;
        this.pk = pk;
        this.fields = fields.stream().filter(field -> !this.pk.equals(field.getField())).collect(Collectors.toList());
    }

    public void writeFile() throws Exception {
        this.writeFile(name + " - 数据模板.xlsx");
    }


    public void writeFile(String fileName) throws Exception {
        this.writer = WriteExcel.create(this, fileName);
        this.writer.setNegativeFormat(true, "");
        this.content();
        this.tips();
        this.writer.close();
        this.writer = null;
    }

    public void writeStream(OutputStream outputStream) throws Exception {
        this.writer = WriteExcel.create(this, name + " - 数据模板.xlsx");
        this.writer.setNegativeFormat(true, "");
        this.content();
        this.tips();
        this.writer.closeByStream(outputStream);
        this.writer = null;
    }

    private void content() throws Exception {
        Area sheet = this.writer.createArea(this.name, 0, 0);

        String header = fields.stream().map(SourceField::getName).collect(Collectors.joining("/"));
        sheet.header(header, "#hdrBlue").writeArea();
        for (int i = 0, _i = fields.size(); i < _i; i++) {
            SourceField field = fields.get(i);
            switch (field.getType()) {
                case "long":
                case "double":
                    sheet.colWidth(i, 16);
                    break;
                case "date":
                    sheet.colWidth(i, 24);
                    break;
                case "text":
                    sheet.colWidth(i, 32);
                    break;
                default:
                    sheet.colWidth(i, 20);
                    break;
            }
        }
    }

    private void tips() throws Exception {
        List tips = new ArrayList();
        for (SourceField field : fields) {
            if (StringUtils.isNotBlank(field.getDict())) {
                tips.add(field.getDict());
            }
        }
        if (tips.size() == 0) {
            return;
        }
//        Area sheet = this.writer.createArea(this.name, 0, 0);
//        sheet.header(fields.stream().map(SourceField::getField).collect(Collectors.joining("/")), "#hdrBlue");
//        sheet.writeArea();

    }

}
