package com.vrv.vap.amonitor.model.es;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * es数据存储信息
 *
 * @author xw
 * @date 2018年7月18日
 */
@ApiModel("es数据存储信息")
public class StorageModel {

    @ApiModelProperty("索引名")
    private String indexName;

    @ApiModelProperty("索引中文名")
    private String indexNameCn;

    @ApiModelProperty("主分片数据条数")
    private long docsCountPrimary;
//
//	@ApiModelProperty("主分片占用空间,单位GB")
//	private double takeSpacePrimary;
//
//	@ApiModelProperty("数据条数")
//	private long docsCountTotal;

    @ApiModelProperty("占用空间,单位GB")
    private double takeSpaceTotal;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexNameCn() {
        return indexNameCn;
    }

    public void setIndexNameCn(String indexNameCn) {
        this.indexNameCn = indexNameCn;
    }

    public long getDocsCountPrimary() {
        return docsCountPrimary;
    }

    public void setDocsCountPrimary(long docsCountPrimary) {
        this.docsCountPrimary = docsCountPrimary;
    }

//	public double getTakeSpacePrimary() {
//		return takeSpacePrimary;
//	}
//
//	public void setTakeSpacePrimary(double takeSpacePrimary) {
//		this.takeSpacePrimary = takeSpacePrimary;
//	}
//
//	public long getDocsCountTotal() {
//		return docsCountTotal;
//	}
//
//	public void setDocsCountTotal(long docsCountTotal) {
//		this.docsCountTotal = docsCountTotal;
//	}

    public double getTakeSpaceTotal() {
        return takeSpaceTotal;
    }

    public void setTakeSpaceTotal(double takeSpaceTotal) {
        this.takeSpaceTotal = takeSpaceTotal;
    }

}
