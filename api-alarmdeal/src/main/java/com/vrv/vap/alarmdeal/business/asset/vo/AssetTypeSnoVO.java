package com.vrv.vap.alarmdeal.business.asset.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="资产品牌型号")
public class AssetTypeSnoVO {
        
	    @ApiModelProperty(value="资产品牌型号guid")
		private String guid;
	    @ApiModelProperty(value="资产品牌型号树编号")
		private String treeCode;
	    @ApiModelProperty(value="资产品牌型号唯一标识")
		private String uniqueCode;
	    @ApiModelProperty(value="资产品牌型号名称")
		private String name;
	    @ApiModelProperty(value="资产品牌型号英文名称")
		private String nameEn;
	    @ApiModelProperty(value="资产品牌型号标识")
		private String icon;
	    @ApiModelProperty(value="资产品牌是否可以发送syslog日志")
		private String canSyslog;
	    @ApiModelProperty(value="资产品牌是否可以监控")
		private String canMonitor;
	    @ApiModelProperty(value="资产品牌是否可以远程监控")
		private String canRCtrl;
	    @ApiModelProperty(value="资产类型guid")
		private String assetTypeGuid;
	    @ApiModelProperty(value="资产品牌型号tree编号头部")
		private String treeCodeHead;
	    @ApiModelProperty(value="资产品牌型号tree编号尾部")
		private String treeCodeTail;
	    @ApiModelProperty(value="资产品牌状态")
		private Integer status;
	    @ApiModelProperty(value="资产品牌型号排序顺序")
		private Integer orderNum;

		public String getGuid() {
			return this.guid;
		}

		public void setGuid(String guid) {
			this.guid = guid;
		}
		
		public String getAssetTypeGuid() {
			return this.assetTypeGuid;
		}

		public void setAssetTypeGuid(String assetTypeGuid) {
			this.assetTypeGuid = assetTypeGuid;
		}
		
		public String getTreeCodeHead() {
			return this.treeCodeHead;
		}

		public void setTreeCodeHead(String treeCodeHead) {
			this.treeCodeHead = treeCodeHead;
		}
		public String getTreeCodeTail() {
			return this.treeCodeTail;
		}

		public void setTreeCodeTail(String treeCodeTail) {
			this.treeCodeTail = treeCodeTail;
		}
		public String getTreeCode() {
			this.treeCode = this.treeCodeHead+this.treeCodeTail;
			return this.treeCode;
		}

		public void setTreeCode(String treeCode) {
			this.treeCode = treeCode;
			int idx = treeCode.lastIndexOf("-");
			this.treeCodeHead = treeCode.substring(0, idx+1);
			this.treeCodeTail = treeCode.substring(idx+1, treeCode.length());
			
		}

		public String getUniqueCode() {
			return this.uniqueCode;
		}

		public void setUniqueCode(String uniqueCode) {
			this.uniqueCode = uniqueCode;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getNameEn() {
			return this.nameEn;
		}

		public void setNameEn(String nameEn) {
			this.nameEn = nameEn;
		}

		public String getIcon() {
			return this.icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getCanSyslog() {
			return canSyslog;
		}

		public void setCanSyslog(String canSyslog) {
			this.canSyslog = canSyslog;
		}

		public String getCanMonitor() {
			return canMonitor;
		}

		public void setCanMonitor(String canMonitor) {
			this.canMonitor = canMonitor;
		}

		public String getCanRCtrl() {
			return canRCtrl;
		}

		public void setCanRCtrl(String canRCtrl) {
			this.canRCtrl = canRCtrl;
		}

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public Integer getOrderNum() {
			return orderNum;
		}

		public void setOrderNum(Integer orderNum) {
			this.orderNum = orderNum;
		}


		
}
