package com.vrv.vap.alarmdeal.business.analysis.repository;

import java.util.List;

import com.vrv.vap.alarmdeal.business.analysis.model.ThreatInfo;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.data.jpa.repository.Query;


/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年4月22日 上午10:34:38 
* 类说明 威胁管理数据访问层
*/
public interface ThreatInfoRespository extends BaseRepository<ThreatInfo, String> {
        
	/**
	 * 获得威胁库的guid数值
	 * @return
	 */
	@Query(value="select library_guid from threat_info", nativeQuery = true)
    public List<String> findThreatLibrary();

}
