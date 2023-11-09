package com.vrv.vap.xc.model;

public class EsColumns {
    private String key;
    private String type;
    private String format;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EsColumns(String key, String type, String format,String title) {
        this.key = key;
        this.type = type;
        this.title = title;
        if("date".equals(type)){
            this.format = "strict_date_optional_time||epoch_millis||yyyy-MM-dd HH:mm:ss";
        }else{
            this.format = format;
        }
    }

    public EsColumns(String key, String type) {
        this.key = key;
        this.type = type;
    }

    public EsColumns() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
