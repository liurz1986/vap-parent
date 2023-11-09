package com.vrv.vap.netflow.vo;

import com.vrv.vap.netflow.model.Asset;


public class AssetVo extends Asset {

        private String assetExtendInfo;

        private String groupName;

        private String typeName;

        private String treeCode;

        private String domainName;

        public String getAssetExtendInfo() {
                return assetExtendInfo;
        }

        public void setAssetExtendInfo(String assetExtendInfo) {
                this.assetExtendInfo = assetExtendInfo;
        }

        public String getGroupName() {
                return groupName;
        }

        public void setGroupName(String groupName) {
                this.groupName = groupName;
        }

        public String getTypeName() {
                return typeName;
        }

        public void setTypeName(String typeName) {
                this.typeName = typeName;
        }

        public String getTreeCode() {
                return treeCode;
        }

        public void setTreeCode(String treeCode) {
                this.treeCode = treeCode;
        }

        public String getDomainName() {
                return domainName;
        }

        public void setDomainName(String domainName) {
                this.domainName = domainName;
        }

        @Override
        public String toString() {
                return "AssetVo{" +
                        "assetExtendInfo='" + assetExtendInfo + '\'' +
                        ", groupName='" + groupName + '\'' +
                        ", typeName='" + typeName + '\'' +
                        ", treeCode='" + treeCode + '\'' +
                        ", domainName='" + domainName + '\'' +
                        '}';
        }
}
