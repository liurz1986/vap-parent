package com.vrv.vap.alarmdeal.business.buinesssystem.controller;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.buinesssystem.service.BuinessSystemService;
import com.vrv.vap.alarmdeal.business.buinesssystem.vo.*;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 业务资产
 *
 * 2022-11-16
 */
@RestController
@RequestMapping(value = "/buinessSystem")
public class BuinessSystemController {
    Logger logger = LoggerFactory.getLogger(BuinessSystemController.class);
    @Autowired
    private BuinessSystemService buinessSystemService;

    /**
     * 查询业务系统树形结构数据
     *
     * @return
     */
    @GetMapping("/getTree")
    @ApiOperation(value = "查询业务系统树形结构数据", notes = "")
    @SysRequestLog(description="查询业务系统树形结构数据", actionType = ActionType.SELECT,manually = false)
    public Result<BuinessSystemTreeVO> getTree() {
        return ResultUtil.success(buinessSystemService.getAllTree());
    }

    /**
     * 查询业务系统
     *
     * @return
     */
    @PostMapping("/getInfoPager")
    @ApiOperation(value = "查询业务系统", notes = "")
    @SysRequestLog(description="查询业务系统", actionType = ActionType.SELECT,manually = false)
    public PageRes<BuinessSystemVO> getInfoPager(@RequestBody BuinessSystemSearchVO buinessSystemSearchVO) {
       logger.debug("查询业务系统:"+ JSON.toJSONString(buinessSystemSearchVO));
        PageRes<BuinessSystemVO> result = new   PageRes<BuinessSystemVO>();
        if(StringUtils.isEmpty(buinessSystemSearchVO.getCode())){
            result.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode()+"");
            result.setMessage("key不能为空");
            return result;
        }
        if(buinessSystemSearchVO==null) {
            buinessSystemSearchVO=new BuinessSystemSearchVO();
        }
        if(buinessSystemSearchVO.getCount_()==null||buinessSystemSearchVO.getCount_()<0) {
            buinessSystemSearchVO.setCount_(10);
        }

        if(buinessSystemSearchVO.getStart_()==null||buinessSystemSearchVO.getStart_()<0) {
            buinessSystemSearchVO.setStart_(0);
        }
        try{
            PageReq pageReq=new PageReq();
            pageReq.setCount(buinessSystemSearchVO.getCount_());
            pageReq.setBy("desc");
            pageReq.setOrder("createTime");
            pageReq.setStart(buinessSystemSearchVO.getStart_());
            return buinessSystemService.getInfoPager(buinessSystemSearchVO,pageReq.getPageable());
        }catch (Exception e){
            logger.error("查询业务系统异常",e);
            result.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode().toString());
            result.setMessage("查询业务系统异常");
            return result;
        }

    }

    /**
     *通过业务系统id获取业务系统详情
     *
     * @return
     */
    @GetMapping("/getBusiSystem/{busId}")
    @ApiOperation(value = "通过业务系统id获取业务系统详情", notes = "")
    @SysRequestLog(description="通过业务系统id获取业务系统详情", actionType = ActionType.SELECT,manually = false)
    public Result<BuinessSystemVO> getBusiSystem(@PathVariable("busId") String busId){
        if(StringUtils.isEmpty(busId)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"业务系统Id不能为空");
        }
        return buinessSystemService.getBusiSystem(busId);
    }

    /**
     *通过业务系统获取对应资产信息
     *
     * @return
     */
    @GetMapping("/getAssets/{busId}")
    @ApiOperation(value = "通过业务系统iD获取对应资产信息", notes = "")
    @SysRequestLog(description="通过业务系统iD获取对应资产信息", actionType = ActionType.SELECT,manually = false)
    public Result<List<BusiAssetVO>> getAssetByBusId(@PathVariable("busId") String busId){
        if(StringUtils.isEmpty(busId)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"业务系统id不能为空");
        }
        return ResultUtil.successList(buinessSystemService.getAssetByBusId(busId));
    }

    /**
     *通过业务系统获取对应资产信息
     *
     * @return
     */
    @GetMapping("/getAssetsIp/{busId}")
    @ApiOperation(value = "通过业务系统iD获取对应资产信息(仅ip)", notes = "")
    @SysRequestLog(description="通过业务系统iD获取对应资产ip", actionType = ActionType.SELECT,manually = false)
    public Result<List<String>> getAssetByBusIdForIps(@PathVariable("busId") String busId){
        if(StringUtils.isEmpty(busId)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"业务系统id不能为空");
        }
        List<BusiAssetVO> busiAssetVOS = buinessSystemService.getAssetByBusId(busId);
        List<String> ips = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(busiAssetVOS)){
            List<String> ipList = busiAssetVOS.stream().map(BusiAssetVO::getIp).collect(Collectors.toList());
            ips.addAll(ipList);
        }
        return ResultUtil.successList(ips);
    }

    /**
     * 新增业务系统
     *
     * @return
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存业务系统", notes = "")
    @SysRequestLog(description="保存业务系统", actionType = ActionType.ADD,manually = false)
    public Result<Boolean> save(@RequestBody BuinessSystemSaveVO buinessSystemSaveVO){
        try{
            logger.debug("保存业务系统:"+JSON.toJSONString(buinessSystemSaveVO));
            return buinessSystemService.saveBusi(buinessSystemSaveVO);
        }catch (Exception e){
            logger.error("保存业务系统异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"保存业务系统异常");
        }

    }

    /**
     * 编辑业务系统
     *
     * @return
     */
    @PostMapping("/saveEdit")
    @ApiOperation(value = "编辑业务系统", notes = "")
    @SysRequestLog(description="编辑业务系统", actionType = ActionType.UPDATE,manually = false)
    public Result<Boolean> saveEdit(@RequestBody BuinessSystemSaveVO buinessSystemSaveVO){
        try{
            logger.debug("编辑业务系统:"+JSON.toJSONString(buinessSystemSaveVO));
            return   buinessSystemService.saveEdit(buinessSystemSaveVO);
        }catch (Exception e){
            logger.error("编辑业务系统异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"编辑业务系统异常");
        }
    }

    /**
     * 删除业务系统
     *
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除业务系统", notes = "")
    @SysRequestLog(description="删除业务系统", actionType = ActionType.DELETE,manually = false)
    public Result<Boolean> delete(@RequestBody BuinessSystemVO buinessSystemVO){
        try{
            logger.debug("删除业务系统:"+buinessSystemVO.getGuid());
            return   buinessSystemService.deleteBusi(buinessSystemVO);
        }catch (Exception e){
            logger.error("删除业务系统异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"删除业务系统异常");
        }
    }

    /**
     * 获取父级业务系统下拉列表
     *
     * @return
     */
    @GetMapping("/getParentBusi")
    @ApiOperation(value = "获取父级业务系统下拉列表", notes = "")
    @SysRequestLog(description="获取父级业务系统下拉列表", actionType = ActionType.SELECT,manually = false)
    public Result<List<ParentBusiVO>> getParentBusi(){
        return   buinessSystemService.getParentBusi();
    }
}
