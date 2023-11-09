package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import java.util.List;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.DataRow;
import lombok.Data;

@Data
public class ParamsData {
	List<DataRow> display;//展示
	List<DataRow> hand;//手工录入
	List<DataRow> baseline;//基线
}