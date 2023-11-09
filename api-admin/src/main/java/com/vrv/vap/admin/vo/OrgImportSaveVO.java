package com.vrv.vap.admin.vo;

import java.util.List;

/**
 * @author lilang
 * @date 2022/8/25
 * @description
 */
public class OrgImportSaveVO {

    List<BaseKoalOrgExcel> baseKoalOrgExcelList;

    Integer importType;

    public List<BaseKoalOrgExcel> getBaseKoalOrgExcelList() {
        return baseKoalOrgExcelList;
    }

    public void setBaseKoalOrgExcelList(List<BaseKoalOrgExcel> baseKoalOrgExcelList) {
        this.baseKoalOrgExcelList = baseKoalOrgExcelList;
    }

    public Integer getImportType() {
        return importType;
    }

    public void setImportType(Integer importType) {
        this.importType = importType;
    }
}
