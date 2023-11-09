package com.vrv.vap.admin.common.enums;

public enum FileTypeEnum {
    JPG(0,"jpg"),
    PNG(1,"png"),
    GIF(2,"gif"),
    JPEG(3,"jpeg"),
    MP4(4,"mp4"),
    OGG(5,"ogg"),
    MP3(6,"mp3");
    private Integer code;
    private String name;
    FileTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
