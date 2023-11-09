package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;

public class AppQuery extends Query {

    @QueryLike
    private String name;

    private Byte type;
    @QueryLike
    private String url;
    private Byte status;

    private Byte third;

    private Byte folder;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        if (type == 1) {
            this.type = type;
            this.third = null;
        } else if (type == 2) {
            this.type = type;
            this.third = null;
        } else if (type == 3) {
            this.type = (byte)2;
            this.third = (byte)0;
        } else if (type == 4) {
            this.type = (byte)2;
            this.third = (byte)1;
        } else if (type == 5) {
            this.type = null;
            this.third = null;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }


    public Byte getThird() {
        return third;
    }

    public void setThird(Byte third) {
//        this.third = third;
    }

    public Byte getFolder() {
        return folder;
    }

    public void setFolder(Byte folder) {
        this.folder = folder;
    }
}
