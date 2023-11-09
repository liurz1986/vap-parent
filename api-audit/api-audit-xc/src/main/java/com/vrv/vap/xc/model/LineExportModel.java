package com.vrv.vap.xc.model;

import com.vrv.vap.xc.pojo.BaseLine;
import com.vrv.vap.xc.pojo.BaseLineSource;
import com.vrv.vap.xc.pojo.BaseLineSourceField;

import java.util.List;

public class LineExportModel {
    private List<BaseLine> lines;
    private List<BaseLineSource> sources;
    private List<BaseLineSourceField> fields;

    public List<BaseLine> getLines() {
        return lines;
    }

    public void setLines(List<BaseLine> lines) {
        this.lines = lines;
    }

    public List<BaseLineSource> getSources() {
        return sources;
    }

    public void setSources(List<BaseLineSource> sources) {
        this.sources = sources;
    }

    public List<BaseLineSourceField> getFields() {
        return fields;
    }

    public void setFields(List<BaseLineSourceField> fields) {
        this.fields = fields;
    }

    public LineExportModel(List<BaseLine> lines, List<BaseLineSource> sources, List<BaseLineSourceField> fields) {
        this.lines = lines;
        this.sources = sources;
        this.fields = fields;
    }

    public LineExportModel() {
    }
}
