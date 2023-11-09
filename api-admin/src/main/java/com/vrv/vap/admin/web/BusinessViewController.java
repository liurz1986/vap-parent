package com.vrv.vap.admin.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vrv.vap.admin.model.BigScreen;
import com.vrv.vap.admin.model.BusinessView;
import com.vrv.vap.admin.service.BigScreenService;
import com.vrv.vap.admin.service.BusinessViewService;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import tk.mybatis.mapper.entity.Example;

/**
 * @BelongsProject api-admin
 * @BelongsPackage com.vrv.vap.admin.web
 * @Author 涂美政
 * @CreateTime 2019/04/08 14:51
 * @Description (业务视图相关接口)
 * @Version
 */
@RestController
@Api(value = "业务视图")
@RequestMapping("/businessView/")
public class BusinessViewController extends ApiController {

	@Autowired
	private BusinessViewService businessViewService;
	@Autowired
	private BigScreenService bigScreenService;

	/**
	 * 获取所有数据--业务视图
	 */
	@ApiOperation(value = "获取所有业务视图已选择数据")
	@GetMapping(value = "/getBusinessViewData")
	public VData<List<BusinessView>> getAllBusinessView(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
		Example example = new Example(BusinessView.class);
		example.createCriteria().andEqualTo("userId", user.getId());
		example.setOrderByClause(" add_time desc ");
		List<BusinessView> list = businessViewService.findByExample(example);
		return this.vData(list);
	}

	/**
	 * 添加资源
	 **/
	@ApiOperation(value = "添加业务视图数据", hidden = false)
	@PutMapping(value = "/add")
	public Result add(@RequestBody BusinessView businessView) {
		businessView.setAddTime(new Date());
		int result = businessViewService.save(businessView);
		return this.result(result == 1);
	}

	/**
	 * 修改资源
	 **/
	@ApiOperation(value = "修改业务视图数据", hidden = false)
	@PatchMapping(value = "/update")
	public Result update(@RequestBody BusinessView businessView) {
		int result = businessViewService.update(businessView);
		return this.result(result == 1);
	}

	/**
	 * 删除
	 **/
	@ApiOperation(value = "删除业务视图数据", hidden = false)
	@DeleteMapping(value = "/del")
	public Result del(@RequestBody DeleteQuery deleteQuery) {
		int result = businessViewService.deleteByIds(deleteQuery.getIds());
		return this.result(result == 1);
	}

	/**
	 * 获取所有数据--业务视图
	 */
	@ApiOperation(value = "获取所有业务视图数据")
	@GetMapping(value = "/getBigScreenData")
	public VData<List<BigScreen>> getAllBigScreen() {
		List<BigScreen> list = bigScreenService.findAll();
		return this.vData(list);
	}

}
