package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.util.DateUtil;
import com.vrv.vap.admin.model.BaseDictAll;
import com.vrv.vap.admin.service.BaseDictAllService;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.admin.vo.BaseDictAllQuery;
import com.vrv.vap.admin.vo.BaseDictAllTreeVO;
import com.vrv.vap.admin.vo.EventTaVo;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *@author qinjiajing E-mail:
 * 创建时间 2018年9月26日 上午9:23:09
 * 类说明：BaseDictAllController
 */
@RestController
@RequestMapping("/dictionary")
public class BaseDictAllController extends ApiController{
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private BaseDictAllService baseDictAllService;

	private static final String CACHE_DICT_KEY = "_BASEINFO:BASE_DICT_ALL:ALL";

	@Autowired
	StringRedisTemplate redisTemplate;
	
	@PostMapping("/tree")
	public Result getTree(@RequestBody Map<String, String> cons) {
		String name = "";
		List<BaseDictAllTreeVO> list = null;
		if (cons != null && cons.containsKey("rootName")) {// 当前端传参为""时
			name = cons.get("rootName").toString();
		}
		logger.info(String.format("rootName: %s", LogForgingUtil.validLog(name)));
		if (StringUtils.isNotEmpty(name)) {
			list = baseDictAllService.findByRootName(name);
		}
		list = baseDictAllService.getTree();
		return this.vList(list, list.size());
	}

	@ApiOperation(value = "查询所有字典")
	@GetMapping
	@SysRequestLog(description = "查询所有字典", actionType = ActionType.SELECT)
	public VData<List<BaseDictAll>> getAllDict(){
		return  this.vData(baseDictAllService.findAll());
	}

	@ApiOperation(value = "新增字典")
	@PutMapping
	@SysRequestLog(description = "新增字典", actionType = ActionType.ADD)
	public Result addDict(@RequestBody BaseDictAll baseDictAll){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		com.vrv.vap.common.model.User currentUser = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
		Integer createId = currentUser.getId();
		baseDictAll.setCreateId(String.valueOf(createId));
        baseDictAll.setCreateTime(DateUtil.format(new Date()).toString());
		int result = baseDictAllService.save(baseDictAll);
		if (result == 1) {
			SyslogSenderUtils.sendAddSyslog(baseDictAll, "新增字典");
		}
		baseDictAllService.cacheDict();
		baseDictAllService.sendChangeMessage();
		return  this.result(result==1);
	}

	@ApiOperation(value = "更新字典")
	@PatchMapping
	@SysRequestLog(description = "更新字典", actionType = ActionType.UPDATE)
	public Result updateDict(@RequestBody BaseDictAll baseDictAll){
		BaseDictAll dictAllSec = baseDictAllService.findById(baseDictAll.getId());
		baseDictAll.setUpdateTime(DateUtil.format(new Date()).toString());
		int result = baseDictAllService.update(baseDictAll);
		if (result == 1) {
			SyslogSenderUtils.sendUpdateSyslog(dictAllSec, baseDictAll,"更新字典");
		}
		baseDictAllService.cacheDict();
		baseDictAllService.sendChangeMessage();
		return  this.result(result==1);
	}

	@ApiOperation(value = "删除字典")
	@DeleteMapping
	@SysRequestLog(description = "删除字典", actionType = ActionType.DELETE)
	public Result deleteDict(@RequestBody DeleteQuery deleteQuery){

		List<BaseDictAll> baseDictAlls = baseDictAllService.findByids(deleteQuery.getIds());
		for (BaseDictAll baseDictAll : baseDictAlls) {
			if (StringUtils.isEmpty(baseDictAll.getCreateId()) || "default".equals(baseDictAll.getCreateId())) {
				return this.vData(ErrorCode.BUILD_IN_DATA_DELETE_ERROR);
			}
		}
		baseDictAlls.forEach(p->{
			if("0".equals(p.getParentType())){
				List<BaseDictAll> leafDicts = baseDictAllService.findByProperty(BaseDictAll.class,"parentType",p.getType());
				leafDicts.forEach(leaf->{
					baseDictAllService.deleteById(leaf.getId());
					SyslogSenderUtils.sendDeleteSyslog(leaf,"删除字典");
				});

			}
			int result = baseDictAllService.deleteById(p.getId());
			if (result > 0) {
				SyslogSenderUtils.sendDeleteSyslog(p,"删除字典");
			}
		});
		baseDictAllService.cacheDict();
		baseDictAllService.sendChangeMessage();
		return this.result(true);
	}


	@ApiOperation(value = "查询字典，分页")
	@PostMapping
	@SysRequestLog(description = "查询字典", actionType = ActionType.SELECT)
	public VList<BaseDictAll> queryDict( @RequestBody BaseDictAllQuery query){
		SyslogSenderUtils.sendSelectSyslog();
		Example example = this.pageQuery(query,BaseDictAll.class);
		return this.vList(baseDictAllService.findByExample(example));
	}
	@ApiOperation(value = "通过code_value和CODE查询（告警事件对象查询）")
	@PostMapping("/event")
	@SysRequestLog(description = "通过code_value和CODE查询（事件对象查询）", actionType = ActionType.SELECT)
	public VData<List<EventTaVo>> queryEventDict(@RequestBody List<EventTaVo> eventTaVos){
		SyslogSenderUtils.sendSelectSyslog();
		return this.vData(baseDictAllService.queryEventDict(eventTaVos));
	}




}
