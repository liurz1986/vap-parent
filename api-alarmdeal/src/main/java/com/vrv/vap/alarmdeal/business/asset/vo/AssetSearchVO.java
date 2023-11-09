package com.vrv.vap.alarmdeal.business.asset.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 资产查询VO
 * @author wd-pc
 *
 */
@Data
@ApiModel(value="资产查询接口")
public class AssetSearchVO extends SelectIds {
    //资产类型查询
	@ApiModelProperty(value="资产类型名称")
	private String asset_type_name; //资产类型名称
	@ApiModelProperty(value="资产类型树编码")
	private String treeCode;
	@ApiModelProperty(value="资产类型状态")
	private String status; //资产类型状态
	
	//资产查询
	@ApiModelProperty(value="资产guid")
	private String guid; //资产guid
	@ApiModelProperty(value="是否远程控制")
	private String canRCtrl; //是否远程控制
	@ApiModelProperty(value="资产名称")
	private String name; //资产名称
	@ApiModelProperty(value="资产IP")
	private String ip; //资产IP
	@ApiModelProperty(value="资产类型(Type_Guid字段)")
	private String assetType; //资产类型
	@ApiModelProperty(value="资产类型guid（自动适应资产大类和二级类型，以及品牌型号）")
	private String typeGuid;
	
	@ApiModelProperty(value="品牌型号guid")
	private String typeSnoGuid;
	
	@ApiModelProperty(value="资产标签")
	private String tags;
	@ApiModelProperty(value="资产责任人")
	private String employeeCode1;
	
	@ApiModelProperty(value="应用系统Id")
	private String appId; //资产guid
	
	@ApiModelProperty(value="资产价值")
	private String worth;
	@ApiModelProperty(value="窃泄密值")
	private String beginValue;
	@ApiModelProperty(value="窃泄密值")
	private String endValue;
	@ApiModelProperty(value="安全域code")
	private String securityGuid;

	@ApiModelProperty(value="资产类型code")
	private String assetTypeCode;

	@ApiModelProperty(value="ip/mac/名称")
	private String assetInfo;
	@ApiModelProperty(value="安全域查询方式：1、查询自身 2、查询自身和子级 3、查询自身和子集的子集 。该查询条件，仅对（securityGuid字段、安全域code）有效")
	private String domainSearchType;


	@ApiModelProperty(value = "仅看关注的资产")
	Boolean isJustAssetOfConcern;
	/**
	 * 格式：/code/code/code
	 */
	@ApiModelProperty(value="安全域domainCodeTree")
	private String domainCodeTree;
	
	

	//格式说明
//	序号	格式	说明
//	1	/+/+/+	表示查询所有数据
//	2	/*/*/*	
//	3	/*	
//	4	/+	
//	5	/code/code/*	查询2级下的所有节点数据
//	6	/code/*	查询一级下的所有节点数据
//	7	/code/code/+	查询2级+2级下所有节点数据
//	8	/code/+	查询一级+一级下的所有节点数据
//	9	/code/code/code	查询某个三级数据
//	10	/code/code	查询某个二级数据
//	11	/code	查询某个一级数据

//复杂格式说明
//	序号	格式	说明
//	1	/code/code/code1,code2	逗号分隔，表示某一个节点下的多个下级节点（暂不支持）
//	2	/code/code1,code2/code	不支持
//	3	/code/code1/*;/code/code2/*	分号分隔（暂不支持）
//	4	/code/code1/code,code;/code/code2/*	分号分隔和逗号分隔同时存在（暂不支持）


//	
//	@ApiModelProperty(value="查询类型")
//	private String type;

	@ApiModelProperty(value="失陷状态")
	private String failedStatus;  // 可疑（1）、高危（2）、失陷（3）。（解除状态是一种隐藏状态，表示安全（0）
	
	
	@ApiModelProperty(value="关联应用系统")
	private Boolean linkSys=null;   
	
	@ApiModelProperty(value="关联漏洞")
	private Boolean linkVul=null;   
	
	@ApiModelProperty(value="查询类型")
	private String type;
	
	
	@ApiModelProperty(value="资产IP")
	private String ipEq; //资产IP

	@ApiModelProperty(value="部门")
	private String orgName; //部门

	@ApiModelProperty(value="部门code")
	private String orgCode; //部门
	@ApiModelProperty(value="密级")
	private String equipmentIntensive;//  涉密等级 绝密0，机密1，秘密2，内部3，非密4
	@ApiModelProperty(value="资产guid")
	private String assetGuids;
	@ApiModelProperty(value="需要排除的资产guid")
	private String assetGuidNodes;
	@ApiModelProperty(value="序列号")
	private String serialNumber;
	@ApiModelProperty(value="责任人code(对应用户的userNo)")
	private String responsibleCode;
	@ApiModelProperty(value="责任人姓名(对应用户的userName)")
	private String responsibleName;
	@ApiModelProperty(value="终端类型")
	private String terminalType;// 终端类型 ：运维终端/用户终端
	@ApiModelProperty(value="是不是USB外设或存储介质")
	private String hasUsbAsset; // 0或null:表示查询非USB外设和存储介质外的数据 1：表示查询包括USB外设或存储介质数据

	@ApiModelProperty(value="排序字段")
	private String order_;    // 排序字段
	@ApiModelProperty(value="排序顺序")
	private String by_;   // 排序顺序
	@ApiModelProperty(value="起始页")
	private Integer start_;//起始页
	@ApiModelProperty(value="每页行数")
	private Integer count_; //每页行数
	
}
