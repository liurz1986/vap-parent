package com.vrv.vap.alarmdeal.business.analysis.controller;

import com.vrv.vap.alarmdeal.business.analysis.enums.ObjectResourceConst;
import com.vrv.vap.alarmdeal.business.analysis.model.ObjectResource;
import com.vrv.vap.alarmdeal.business.analysis.vo.ObjectResourceVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.PatternVO;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.alarmdeal.business.analysis.server.ObjectResourceService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


@Api(description="资源对象")
@RestController
@RequestMapping("/objectResource")
public class ObjectResourceController extends BaseController {

    @Autowired
    private ObjectResourceService objectResourceService;

    @Autowired
    private MapperUtil mapperUtil;

    /**
     * 新增资源对象
     */
    @PutMapping("")
    @ApiOperation(value="新增资源对象",notes="")
    @SysRequestLog(description = "新增资源对象", actionType = ActionType.ADD, manually = false)
    public Result<ObjectResource> addResource(@RequestBody ObjectResourceVO objectResourceVO){
        Result<ObjectResource> result=objectResourceService.addResource(objectResourceVO);
        return  result;
    }

    /**
     * 编辑资源对象
     */
    @PatchMapping("")
    @ApiOperation(value="编辑资源对象",notes="")
    @SysRequestLog(description = "编辑资源对象", actionType = ActionType.UPDATE, manually = false)
    public Result<ObjectResource> editResource(@RequestBody ObjectResourceVO objectResourceVO){
        Result<ObjectResource> result=objectResourceService.editResource(objectResourceVO);
        return  result;
    }

    /**
     *  删除资源对象
     */
    @DeleteMapping("")
    @ApiOperation(value="删除资源对象",notes="")
    @SysRequestLog(description = "删除资源对象", actionType = ActionType.DELETE, manually = false)
    public  Result<Boolean> deleteResource(@RequestBody Map<String,Object> map){
        String ids=map.get("ids").toString();
        String[] idsArray = ids.split(",");
        for (String id : idsArray) {
            objectResourceService.deleteResource(id);
        }
        return ResultUtil.success(true);
    }

    /**
     * 资源对象分页查询
     */
    @PostMapping("")
    @ApiOperation(value="资源对象分页查询",notes="")
    @SysRequestLog(description = "资源对象分页查询", actionType = ActionType.SELECT, manually = false)
    public  PageRes<ObjectResource> getResourcePage(@RequestBody ObjectResourceVO objectResourceVO){
        PageReq pageReq=new PageReq();
        pageReq.setCount(objectResourceVO.getCount_());
        pageReq.setStart(objectResourceVO.getStart_());
        if(StringUtils.isNotEmpty(objectResourceVO.getOrder_())){
            pageReq.setOrder(objectResourceVO.getOrder_());
        }
        if(StringUtils.isNotEmpty(objectResourceVO.getBy_())){
            pageReq.setBy(objectResourceVO.getBy_());
        }else{
            pageReq.setBy("desc");
        }
        PageRes<ObjectResource> pageRes = objectResourceService.getObjectResourcePager(objectResourceVO, pageReq.getPageable());
        return pageRes;
    }

    /**
     *资源对象查询
     */
    @PostMapping("getResourceList")
    @ApiOperation(value = "资源对象列表查询",notes="")
    @SysRequestLog(description = "资源对象列表查询", actionType = ActionType.SELECT, manually = false)
    public Result<List<ObjectResource>> getResourceList(@RequestBody ObjectResourceVO objectResourceVO){
        ObjectResource objectResource=new ObjectResource();
        mapperUtil.copy(objectResourceVO,objectResource);
        objectResource.setDeleteFlag(ObjectResourceConst.DELETE_FLAG_NORMAL);
        List<ObjectResource> list=objectResourceService.findAll(objectResource);
        return ResultUtil.success(list);
    }
    
    @PostMapping("checkRegexIsRight")
    @ApiOperation(value = "验证正则表达式是否正确",notes="")
    @SysRequestLog(description = "验证正则表达式是否正确", actionType = ActionType.SELECT, manually = false)
    public Result<Boolean> checkRegexIsRight(@RequestBody PatternVO patternVO){
    	String regex = patternVO.getRegex();  //正则表达式
    	String content = patternVO.getContent();
    	boolean matches = Pattern.matches(regex, content);
    	return ResultUtil.success(matches);
    	
    }
    
    

}