package com.vrv.vap.alarmdeal.business.asset.datasync.vo;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;


/**
 * 获取kafka同步的数据  2022-05-10
 *
 */
@Data
public class BaseSynchKafkaVO  {
    private String dataType;   // 数据类型，asset：资产

	private Object data; //具体数据
}
