package com.vrv.vap.alarmdeal.business.asset.vo;

import com.vrv.vap.alarmdeal.business.asset.datasync.util.ExportExcelField;
import com.vrv.vap.exportAndImport.excel.annotation.ExcelField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.List;

/**
 * 资产导出VO
 *
 * @author wd-pc
 */
@Data
@ApiModel(value = "资产导出VO")
public class AssetExportVO {
    @Column(name = "guid")
    @ExportExcelField(title = "资产guid", order = 1)
    private String guid;
	@ExportExcelField(title = "名称", order = 2)
    private String name;
	@ExportExcelField(title = "类型", order = 3)
	private String typeName;
	@ExportExcelField(title = "ip地址", order = 4)
    private String ip;
	@ExportExcelField(title = "MAC地址", order = 5)
	private String mac;
	@ExportExcelField(title = "责任人", order = 6)
	private String responsibleName;
	@ExportExcelField(title = "部门", order = 7)
	private String orgName;
	@ExportExcelField(title = "密级", order = 8)
	private String equipmentIntensiveName;
	@ExportExcelField(title = "窃泄密风险", order = 9)
	private Integer assetStealLeakValue;
	@ExportExcelField(title = "异常事件数", order = 10)
	private Integer eventNumber;

}
