package com.vrv.vap.monitor.vo;

import com.vrv.vap.monitor.entity.Monitor2AssetInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * <p>
 * V2-指标算法测试Vo
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-10-26
 */
@Data
@ToString
@ApiModel(value = "OidAlgTest测试对象", description = "V2-指标算法测试")
public class OidAlgTest extends Monitor2AssetInfo {

    private int algId;

    @ApiModelProperty(value = "oid's")
    private String oids;

    @ApiModelProperty(value = "算法")
    private String algo;


}
