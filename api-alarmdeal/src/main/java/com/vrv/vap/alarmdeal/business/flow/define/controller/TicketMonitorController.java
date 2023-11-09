package com.vrv.vap.alarmdeal.business.flow.define.controller;

import java.util.ArrayList;
import java.util.List;


import com.vrv.vap.alarmdeal.business.flow.core.constant.MyTicketConstant;
import com.vrv.vap.alarmdeal.business.flow.monitor.vo.MyTicketTreeVO;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicket;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.MyTicketService;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("ticketMonitor")
public class TicketMonitorController {

	@Autowired
	private MapperUtil mapper;
	@Autowired
	private MyTicketService myTicketService;

	/**
	 * 流程分类下：增加内部工单、外部工单
	 * 2022-11-2
	 * @return
	 */
	@RequestMapping(value = "/getMyticketTree")
	@ApiOperation("流程分类树查询")
	@SysRequestLog(description="流程分类树查询", actionType = ActionType.SELECT,manually = false)
	public List<MyTicketTreeVO> getMyticketTree() {
		List<MyTicketTreeVO> result = new ArrayList<>();
		MyTicketTreeVO root = new MyTicketTreeVO();
		root.setKey("0");
		root.setChildren(new ArrayList<>());
		root.setTitle("流程分类");
		result.add(root);
		List<MyTicket> tickets = myTicketService.getProcessTree();
		List<MyTicketTreeVO> ticketTypeOnes = new ArrayList<>();
		List<MyTicketTreeVO> ticketTypeTwos = new ArrayList<>();
		List<MyTicketTreeVO> ticketTypes = new ArrayList<>();
		int ticketTypeOne = 0;
		int ticketTypeTwo = 0;
		for (MyTicket myTicket : tickets) {
			String ticketType = myTicket.getTicketType();
			int count= myTicket.getCount();
			MyTicketTreeVO map = mapper.map(myTicket, MyTicketTreeVO.class);
			map.setKey(myTicket.getName());
			// 内部工单菜单节点
			if(MyTicketConstant.TICKETTYPEONE.equals(ticketType)){
				ticketTypeOnes.add(map);
				ticketTypeOne = ticketTypeOne+count;
			}else if(MyTicketConstant.TICKETTYPETWO.equals(ticketType)){
				ticketTypeTwos.add(map);
				ticketTypeTwo = ticketTypeTwo+count;
			}
		}
		// 构造内外部
		MyTicketTreeVO ticketTypeOnevo = new MyTicketTreeVO();
		ticketTypeOnevo.setKey(MyTicketConstant.TICKETTYPEONEDESC);
		ticketTypeOnevo.setCount(ticketTypeOne);
		ticketTypeOnevo.setChildren(ticketTypeOnes);
		MyTicketTreeVO ticketTypeTwovo= new MyTicketTreeVO();
		ticketTypeTwovo.setKey(MyTicketConstant.TICKETTYPETWODESC);
		ticketTypeTwovo.setCount(ticketTypeTwo);
		ticketTypeTwovo.setChildren(ticketTypeTwos);
		ticketTypes.add(ticketTypeOnevo);
		ticketTypes.add(ticketTypeTwovo);
		root.setChildren(ticketTypes);
		return result;
	}
}
