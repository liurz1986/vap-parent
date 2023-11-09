package com.flink.demo.udf;

import java.sql.Timestamp;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.functions.TableFunction;

import com.flink.demo.vo.SwitchFlagVO;
import com.vrv.logVO.monior.IfEntry;
import com.vrv.logVO.monior.SwitchVo;

/**
 * 
 * @author wd-pc
 *
 */
public class SwitchFlatMapFunction extends TableFunction<SwitchFlagVO> {

	public SwitchFlatMapFunction() {
		
	}
	
	public void eval(String assetGuid,IfEntry[] ifEntryList,Timestamp triggerTime){
		for (int i = 0; i < ifEntryList.length; i++) {
			SwitchFlagVO switchFlagVO = new SwitchFlagVO();
			switchFlagVO.setSwitchId(assetGuid);
			switchFlagVO.setException(false);
			switchFlagVO.setPortCount(ifEntryList.length);
			switchFlagVO.setPortId(ifEntryList[i].getIfIndex());
			switchFlagVO.setHappenTime(triggerTime);
			switchFlagVO.setSpeedValue(Integer.valueOf(ifEntryList[i].getIfMtu()));
			collect(switchFlagVO);
		}
	}
	
	 @Override
	 public TypeInformation<SwitchFlagVO> getResultType(){
		 TypeInformation<SwitchFlagVO> typeInformation = TypeInformation.of(SwitchFlagVO.class);
		 return typeInformation;
		
		 
	 }
	
}
