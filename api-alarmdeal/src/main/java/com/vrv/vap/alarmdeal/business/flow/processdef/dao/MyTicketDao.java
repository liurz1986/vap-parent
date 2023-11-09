package com.vrv.vap.alarmdeal.business.flow.processdef.dao;

import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicket;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.FlowQueryVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年10月18日 下午5:41:23 
* 类说明 
*/
public interface MyTicketDao {
      
	
	/**
	 * 根据对应的那么进行分组
	 * @return
	 */
	public List<Map<String,Object>> queryRootProcessName(FlowQueryVO flowQueryVO);
	
	
	/**
	 * 该流程是否包含启动的流程
	 * @param processName
	 * @return
	 */
	public List<Map<String,Object>> queryRootUsedProcess(String processName);
	
	/**
	 * 查询流程的根节点数据（即正在启动的流程）
	 * @param startRow
	 * @param PageSize
	 * @return
	 */
	public List<Map<String,Object>> queryRootProcess(String processName);
	
	/**
	 * 查询流程的根节点数据的子流程
	 * @param flowQueryVO
	 * @return
	 */
	public List<Map<String,Object>> queryChildrenProcess(String processName);
	
	/**
	 * 查询总个数
	 * @param flowQueryVO
	 * @return
	 */
	public Long queryRootProcessCount();
	
	/**
	 * 获得最大序号
	 * @return
	 */
	public Integer getMaxOrderNum();
	
	/**
	 * 获得最大版本号
	 * @param processName
	 * @return
	 */
	public Integer getMaxVersion(String processName);
	/**
	 * 查询监控工单
	 * @return
	 */
	public List<MyTicket> queryMonitorTicket();
	
	/**
	 * 获得待办流程数量
	 * @return
	 */
	public List<Map<String,Object>> queryMyTaskTicket(String userId);
	
	
	/**
	 * 与我相关的工单
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> queryRelateTicket(String userId);
	
	/**
	 * 与我相关安全域对应的工单
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> queryRelateTicketBySec(Set<String> userIds);
	
	/**
	 * 已经存在的工单
	 * @return
	 */
	public List<Map<String, Object>> queryMonitorExistTicket();

	/**
	 * 更新工单的名称
	 *
	 * @param oldName oldName
	 * @param newName newName
	 */
	public void updateNameMyTicket(String oldName, String newName);

	public String getTicktType(String processDefName);
}
