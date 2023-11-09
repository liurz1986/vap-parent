package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportResponseVo extends Result {
    private Map<String,Object> data;
    private List<Map<String,Object>> list;

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public ReportResponseVo() {
        this.data = new HashMap();
        this.list = new ArrayList();
    }
}
