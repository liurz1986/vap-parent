package com.vrv.vap.data.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import javax.persistence.*;

@Table(name = "data_source_monitor")
@ApiModel(value = "数据源监控状态")
public class SourceMonitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "source_id")
    @ApiModelProperty("数据源")
    private Integer sourceId;

    @ApiModelProperty("健康状态")
    private Byte health;

    @Column(name = "data_count")
    @ApiModelProperty("数据条数")
    private Long dataCount;

    @Column(name = "data_size")
    @ApiModelProperty("数据占用空间")
    private Long dataSize;


    @Column(name = "index_size")
    @ApiModelProperty("索引占用空间")
    private Long indexSize;


    @ApiModelProperty("分片数量（ES）")
    private Integer shards;


    @ApiModelProperty("索引数量（ES）")
    private Integer indices;


    @ApiModelProperty("检测时间")
    private Date time;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Byte getHealth() {
        return health;
    }

    public void setHealth(Byte health) {
        this.health = health;
    }

    public Long getDataCount() {
        return dataCount;
    }

    public void setDataCount(Long dataCount) {
        this.dataCount = dataCount;
    }

    public Long getDataSize() {
        return dataSize;
    }

    public void setDataSize(Long dataSize) {
        this.dataSize = dataSize;
    }

    public Long getIndexSize() {
        return indexSize;
    }

    public void setIndexSize(Long indexSize) {
        this.indexSize = indexSize;
    }

    public Integer getShards() {
        return shards;
    }

    public void setShards(Integer shards) {
        this.shards = shards;
    }

    public Integer getIndices() {
        return indices;
    }

    public void setIndices(Integer indices) {
        this.indices = indices;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}