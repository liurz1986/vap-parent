package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo;

import java.util.List;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.AssetIpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.AssetPathVO;
import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年5月17日 下午2:04:31 
* 类说明 
*/
@Data
public class AlarmAttackPath {
      
	private List<AssetIpVO> srcIps;  //源IP集合
	private List<AssetIpVO> dstIps;  //目的IP集合
	private List<AssetPathVO> attackPath; //攻击路径
}
