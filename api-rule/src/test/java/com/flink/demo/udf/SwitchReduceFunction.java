package com.flink.demo.udf;

import org.apache.flink.table.functions.AggregateFunction;

import com.flink.demo.vo.SwitchFlagVO;
import com.flink.demo.vo.SwitchFoldVO;

public class SwitchReduceFunction extends AggregateFunction<SwitchFoldVO,SwitchFlagVO>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public SwitchFlagVO createAccumulator() {
		return new SwitchFlagVO();
	}

	@Override
	public SwitchFoldVO getValue(SwitchFlagVO arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
