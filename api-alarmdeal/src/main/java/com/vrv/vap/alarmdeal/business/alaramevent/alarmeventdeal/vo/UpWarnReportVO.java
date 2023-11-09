package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import com.google.gson.annotations.SerializedName;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.CoFile;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.UpWarnResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 上报预警，向上级返回预警处理结果
 */
@Data
public class UpWarnReportVO extends UpAbstractReportVO {
	@SerializedName(value = "warn_file",alternate = {"attachment"})
	private List<CoFile> warn_file=new ArrayList<>();
	private List<UpWarnResult> data = new ArrayList<>();


}
