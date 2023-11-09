package com.vrv.vap.amonitor.model;

import lombok.Data;

@Data
public class OidInfo {

    private Oid cpu;
    private Oid disk;
    private Oid memory;
    private Oid process;

    private String port = "161";

    private String securityName = "public";

    public static class Oid {
        private String oid;
        private String oidName;
        private String unit;
        /**
         * 计算式 ,如 oid1/(1024*1024),(oid1-oid2)/(1024*1024)
         */
        private String ex;

        private String[] exs;

        public String getOid() {
            return oid;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }

        public String getOidName() {
            return oidName;
        }

        public void setOidName(String oidName) {
            this.oidName = oidName;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getEx() {
            return ex;
        }

        public String[] getExs() {
            return exs;
        }

        public void setEx(String ex) {
            this.ex = ex;
            this.exs = ex.split(",");
        }
    }
}
