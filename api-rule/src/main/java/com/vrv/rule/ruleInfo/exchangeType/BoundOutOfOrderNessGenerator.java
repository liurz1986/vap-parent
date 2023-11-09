package com.vrv.rule.ruleInfo.exchangeType;

import java.util.List;

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.types.Row;

import com.vrv.rule.util.DateUtil;
import com.vrv.rule.util.FieldInfoUtil;
import com.vrv.rule.vo.FieldInfoVO;

/**
 * 自定义水印生成
 * 
 * @author Administrator
 *
 */
public class BoundOutOfOrderNessGenerator implements AssignerWithPeriodicWatermarks<Row> {

	private List<FieldInfoVO> inputFieldInfos;
	private final long maxOutOfOrderness =30 * 1000; // out of orderness设置为30s
	private long currentMaxTimestamp;

	public BoundOutOfOrderNessGenerator(List<FieldInfoVO> inputFieldInfos) {
		this.inputFieldInfos = inputFieldInfos;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public long extractTimestamp(Row element, long previousElementTimestamp) {
		FieldInfoVO fieldInfoVO = FieldInfoUtil.getEventTimeFieldInfoVO(inputFieldInfos);
		if (fieldInfoVO != null) {
			Integer order = fieldInfoVO.getOrder();
			Object eventTimeOrder = element.getField(order);
			if (eventTimeOrder instanceof String) {
				String eventTime = (String) eventTimeOrder; // yyyy-mm-dd hh:mm:ss
				Long timestamp = DateUtil.getTimestamp(eventTime, DateUtil.DEFAULT_DATE_PATTERN);
				currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
				return timestamp;
			} else {
				throw new RuntimeException(fieldInfoVO.getFieldName() + ":" + eventTimeOrder + "不是字符串类型，请检查！");
			}
		} else {
			throw new RuntimeException("该表没有eventtime字段，请检查！");
		}

	}

	@Override
	public Watermark getCurrentWatermark() {
		return new Watermark(currentMaxTimestamp-maxOutOfOrderness);
		
	}

}
