package com.vrv.vap.alarmdeal.business.asset.datasync.vo;


import com.vrv.vap.alarmdeal.business.asset.datasync.util.ExportExcelField;
import lombok.Data;

import java.util.Date;


/**
 *
 * 资产审核导出VO
 * @data 2023-04-07
 *
 */
@Data
public class AssetVerifyExportVO {
	@ExportExcelField(title = "名称", order = 1,width =40*256)
	private String name;
	@ExportExcelField(title = "类型", order = 2,width =40*256)
	private String typeUnicode ;  // 类型名称用type_unicode
	@ExportExcelField(title = "小类", order = 3,width =20*256)
	private String deviceDesc ;
	@ExportExcelField(title = "设备序列号", order = 4,width =30*256)
	private String serialNumber ;
	@ExportExcelField(title = "IP地址", order = 5,width =20*256)
	private String ip;
	@ExportExcelField(title = "MAC", order = 6,width =20*256)
	private String mac;
	@ExportExcelField(title = "涉密等级", order = 7,width =20*256)
	private String equipmentIntensive;
	@ExportExcelField(title = "位置", order = 8,width =20*256)
	private String location;
	@ExportExcelField(title = "责任人名称", order = 9,width =20*256)
	private String responsibleName;
	@ExportExcelField(title = "部门", order = 10,width =20*256)
	private String orgName; // 部门
	@ExportExcelField(title = "磁盘序列号", order = 11,width =30*256)
	private String extendDiskNumber;
	@ExportExcelField(title = "操作系统版本", order = 12,width =20*256)
	private String osList;
	@ExportExcelField(title = "品牌型号", order = 13,width =20*256)
	private String typeSnoGuid;
	@ExportExcelField(title = "设备编号", order = 14,width =20*256)
	private String assetNum ;
	@ExportExcelField(title = "系统安装时间", order = 15,width =20*256)
	private Date osSetuptime;
	@ExportExcelField(title = "启用时间", order = 16,width =20*256)
	private Date registerTime;
	@ExportExcelField(title = "架构", order = 17,width =40*256)
	private String deviceArch;
	@ExportExcelField(title = "备注", order = 18,width =40*256)
	private String remarkInfo;
}
