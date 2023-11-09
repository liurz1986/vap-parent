package com.vrv.vap.alarmdeal.business.analysis.vo;

import java.util.List;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.ParamsColumn;
import lombok.Data;

@Data
public class ParamsColumns {

	List<ParamsColumn> display;//展示
	List<ParamsColumn> hand;//手工录入
	List<ParamsColumn> baseline;//基线
}
