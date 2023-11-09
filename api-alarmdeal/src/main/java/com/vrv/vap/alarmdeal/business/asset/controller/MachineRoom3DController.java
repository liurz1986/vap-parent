package com.vrv.vap.alarmdeal.business.asset.controller;

import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.Cabinet;
import com.vrv.vap.alarmdeal.business.asset.model.MachineRoom;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.CabinetService;
import com.vrv.vap.alarmdeal.business.asset.service.MachineRoomService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetVO;
import com.vrv.vap.alarmdeal.business.asset.vo.CabinetVO;
import com.vrv.vap.alarmdeal.business.asset.vo.MachineRoomVO;
import com.vrv.vap.common.controller.BaseController;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** * 
 * @author wudi 
 * E‐mail:wudi@vrvmail.com.cn
 * @version 创建时间：2019年2月14日 上午10:46:56
 * 类说明   3d机房controller类
 */
@RestController
@RequestMapping("/machineRoom")
public class MachineRoom3DController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(MachineRoom3DController.class);

    @Autowired
    private MachineRoomService machineRoomService;
    @Autowired
    private CabinetService cabinetService;
    @Autowired
    private AssetService assetService;
    @Autowired
    private MapperUtil mapper;


    @PostMapping(value="getMachineRoomGrid")
    @ApiOperation(value="获取机房列表（无分页）",notes="获取机房列表（无分页）")
    @SysRequestLog(description="获取机房列表（无分页）", actionType = ActionType.SELECT,manually = false)
    @ApiImplicitParams(@ApiImplicitParam(name="guid",value="guid",required=true,dataType="String"))
    public Result<List<MachineRoom>> getMachineRoomGrid(@RequestBody Map<String,Object> map){
        String guid = map.get("guid").toString();
        List<MachineRoom> list = machineRoomService.getMachineRoomGrid(guid);
        return ResultUtil.success(list);
    }

    @GetMapping(value="getMachineRoomGridWithNotGuid")
    @ApiOperation(value="获取机房列表（无分页，无guid）",notes="获取机房列表（无分页）")
    @ApiImplicitParams(@ApiImplicitParam(name="guid",value="guid",required=true,dataType="String"))
    @SysRequestLog(description="获取机房列表（无分页，无guid）", actionType = ActionType.SELECT,manually = false)
    public Result<List<MachineRoom>> getMachineRoomGridWithNotGuid(){
        List<MachineRoom> list = machineRoomService.getMachineRoomGrid(null);
        return ResultUtil.success(list);
    }


    @PostMapping(value="getCabinetGrid")
    @ApiOperation(value="获取机柜列表（无分页）",notes="获取机柜列表（无分页）")
    @ApiImplicitParams(@ApiImplicitParam(name="guid",value="guid",required=true,dataType="String"))
    @SysRequestLog(description="获取机柜列表（无分页）", actionType = ActionType.SELECT,manually = false)
    public Result<List<Cabinet>> getCabinetGrid(@RequestBody Map<String,Object> map){
        String guid = map.get("guid").toString();
        List<Cabinet> list = cabinetService.getCabinetGrid(guid);
        return ResultUtil.success(list);
    }

    @PostMapping(value="getCabinetGridWithNotGuid")
    @ApiOperation(value="获取机柜列表（无分页，无guid）",notes="获取机柜列表（无分页）")
    @ApiImplicitParams(@ApiImplicitParam(name="guid",value="guid",required=true,dataType="String"))
    @SysRequestLog(description="获取机柜列表（无分页，无guid）", actionType = ActionType.SELECT,manually = false)
    public Result<List<Cabinet>> getCabinetGridWithNotGuid(){
        List<Cabinet> list = cabinetService.getCabinetGrid(null);
        return ResultUtil.success(list);
    }


    @PostMapping(value="getCabinetAssets")
    @ApiOperation(value="获取机柜中所有的资产",notes="获取机柜列表（无分页）")
    @ApiImplicitParams(@ApiImplicitParam(name="cabinetGuids",value="cabinetGuids",required=true,dataType="String[]"))
    @SysRequestLog(description="获取机柜中所有的资产", actionType = ActionType.SELECT,manually = false)
    public Result<List<AssetVO>> getCabinetsAssets(@RequestBody Map<String,Object> map){
        String[] cabinetGuids = (String[])map.get("cabinetGuids");
        List<AssetVO> list = cabinetService.getCabinetsAssets(cabinetGuids);
        return ResultUtil.success(list);
    }


    @GetMapping(value = "/getSingleCabinetAsset/{cabinetGuid}")
    @ApiOperation(value=" 获得单个机柜中的资产",notes=" 获得单个机柜中的资产")
    @ApiImplicitParams(@ApiImplicitParam(name="guid",value="cabinetGuid",required=true,dataType="String"))
    @SysRequestLog(description="获得单个机柜中的资产", actionType = ActionType.SELECT,manually = false)
    public Result<List<AssetVO>> getSignleCabinetAssets(@PathVariable String cabinetGuid) {
        List<QueryCondition> conditions = new ArrayList<QueryCondition>();
        conditions.add(QueryCondition.eq("cabinetGuid", cabinetGuid));
        List<Asset> list = assetService.findAll(conditions);
        List<AssetVO> mapList = mapper.mapList(list, AssetVO.class);
        return ResultUtil.success(mapList);
    }

    /**
     *
     *
     * @param cabinetVO
     * @return
     */
    @PostMapping(value = "/saveCabinetPosition")
    @ApiOperation(value="保存机柜的坐标",notes="保存机柜的坐标")
    @SysRequestLog(description="保存机柜的坐标", actionType = ActionType.ADD,manually = false)
    public Result<Boolean> saveCabinetPosition(@RequestBody CabinetVO cabinetVO) {
        String guid = cabinetVO.getGuid();
        Cabinet cabinet = cabinetService.getOne(guid);
        if (cabinet != null) {
            double positionX = cabinetVO.getPositionX();
            double positionY = cabinetVO.getPositionY();
            double positionZ = cabinetVO.getPositionZ();
            cabinet.setPositionX(positionX);
            cabinet.setPositionY(positionY);
            cabinet.setPositionZ(positionZ);
            cabinet.setMoved(1);
            cabinetService.save(cabinet);
        }
        return ResultUtil.success(true);
    }



    @GetMapping(value = "/getSingleCabinetAssetDetail/{cabinetGuid}")
    @ApiOperation(value="获得单个机柜中的资产信息、风险权值",notes="获得单个机柜中的资产信息、风险权值")
    @SysRequestLog(description="获得单个机柜中的资产信息、风险权值", actionType = ActionType.SELECT,manually = false)
    public Result<List<AssetVO>> getSingleCabinetAssetDetail(@PathVariable String cabinetGuid) {
        List<AssetVO> list = cabinetService.getSingleCabinetAssetDetail(cabinetGuid);
        return ResultUtil.success(list);
    }


    @GetMapping(value = "/getMachineRoom/{id}")
    @ApiOperation(value="获得机房的信息",notes="获得机房的信息")
    @SysRequestLog(description="获得机房的信息", actionType = ActionType.SELECT,manually = false)
    public Result<MachineRoom> getMachineRoomById(@PathVariable String id) {
        MachineRoom machineroom = machineRoomService.getOne(id);
        return ResultUtil.success(machineroom);
    }


    /**
     * 判断唯一编码是否重复
     *
     *
     * @return
     */
    @PostMapping(value = "/roomcodeRepetition")
    @ApiOperation(value="判断唯一编码是否重复",notes="判断唯一编码是否重复")
    @ApiImplicitParams({
            @ApiImplicitParam(name="guid",value="guid",required=true,dataType="String"),
            @ApiImplicitParam(name="code",value="code",required=true,dataType="String"),
    })
    @SysRequestLog(description="判断唯一编码是否重复", actionType = ActionType.SELECT,manually = false)
    public Result<Boolean> roomcodeRepetition(@RequestBody Map<String,Object> map) {
        String code = map.get("code").toString();
        String guid = map.get("guid").toString();
        if (StringUtils.isNotEmpty(guid)) { //编辑
            MachineRoom machineroom = machineRoomService.getOne(guid);
            List<QueryCondition> condition = new ArrayList<QueryCondition>();
            condition.add(QueryCondition.eq("code", code));
            condition.add(QueryCondition.notEq("guid", guid));
            List<MachineRoom> machinerooms = machineRoomService.findAll(condition);
            logger.info("coded:{}",machineroom.getCode());
            logger.info("machineroomsde个数:{}",machinerooms.size());
            if (machinerooms.size() == 1) {
                return ResultUtil.success(true); //存在
            } else {
                return ResultUtil.success(false); //不存在
            }
        } else {
            List<QueryCondition> condition = new ArrayList<>();
            condition.add(QueryCondition.eq("code", code));
            List<MachineRoom> machinerooms = machineRoomService.findAll(condition);
            if (machinerooms.isEmpty()) {
                return ResultUtil.success(false); //不存在
            } else {
                return ResultUtil.success(true);// 存在
            }
        }
    }


    @PostMapping("/getMachineRoomInfoPager")
    @ApiOperation(value="获取机房列表",notes="获取机房列表")
    @SysRequestLog(description="获取机房列表", actionType = ActionType.SELECT,manually = false)
    public PageRes<MachineRoom> getMachineRoomInfoPager(@RequestBody MachineRoomVO machineRoomVO){
        PageReq pageReq=new PageReq();
        int start = machineRoomVO.getStart_().intValue();
        int count = machineRoomVO.getCount_().intValue();
        pageReq.setCount(machineRoomVO.getCount_());
        pageReq.setStart(start);
        pageReq.setCount(count);
        pageReq.setOrder(machineRoomVO.getOrder_());
        pageReq.setBy(machineRoomVO.getBy_());
        return machineRoomService.getMachineRoomInfoPager(machineRoomVO, pageReq.getPageable());

    }


    /**
     * 保存添加机房
     *
     *
     * @return
     */
    @PostMapping(value = "/addMachineroom")
    @ApiOperation(value="保存添加机房",notes="保存添加机房")
    @SysRequestLog(description="保存添加机房", actionType = ActionType.ADD,manually = false)
    public Result<Boolean> addMachineroom(@RequestBody MachineRoomVO machineRoomVO) {
        MachineRoom machineroom = mapper.map(machineRoomVO, MachineRoom.class);
        machineroom.setGuid(UUIDUtils.get32UUID());
        machineroom.setOpenAnalysis(0);
        machineroom.setOpenMonitor(0);
        machineroom.setPushMonitor(1);
        machineroom.setPushAnalysis(1);
        machineroom.setShowWall(1);
        try {
            machineRoomService.save(machineroom);
            return ResultUtil.success(true);
        }catch(Exception e) {
            throw new RuntimeException("保存失败", e);
        }
    }


    @PostMapping(value = "/editMachineroom")
    @ApiOperation(value="保存修改机房",notes="保存修改机房")
    @SysRequestLog(description="保存修改机房", actionType = ActionType.ADD,manually = false)
    public Result<Boolean> editMachineroom(@RequestBody MachineRoomVO machineRoomVO) {
        MachineRoom machineroom = machineRoomService.getOne(machineRoomVO
                .getGuid());
        mapper.copy(machineRoomVO, machineroom);
        machineRoomService.save(machineroom);
        return ResultUtil.success(true);
    }



    @GetMapping(value = "/getCabinet/{guid}")
    @ApiOperation(value="通过单个机柜数据",notes="通过单个机柜数据")
    @SysRequestLog(description="通过单个机柜数据", actionType = ActionType.SELECT,manually = false)
    public Result<Cabinet> getCabinet(@PathVariable String guid) {
        Cabinet cabinet = cabinetService.getOne(guid);
        return ResultUtil.success(cabinet);
    }

    /**
     *
     *
     *
     * @return
     */
    @PostMapping(value = "/cabinetcodeRepetition")
    @ApiOperation(value="判断唯一编码是否重复",notes="判断唯一编码是否重复")
    @ApiImplicitParams({
            @ApiImplicitParam(name="guid",value="guid",required=true,dataType="String"),
            @ApiImplicitParam(name="code",value="code",required=true,dataType="String"),
            @ApiImplicitParam(name="roomGuid",value="roomGuid",required=true,dataType="String")
    })
    @SysRequestLog(description="判断唯一编码是否重复", actionType = ActionType.SELECT,manually = false)
    public Result<Boolean> cabinetcodeRepetition(@RequestBody Map<String,Object> map) {
        String guid = map.get("guid").toString();
        String code = map.get("code").toString();
        String roomGuid = map.get("roomGuid").toString();
        List<QueryCondition> condition = new ArrayList<>();
        condition.add(QueryCondition.eq("code", code));
        condition.add(QueryCondition.eq("roomGuid", roomGuid));
        List<Cabinet> cabinets = cabinetService.findAll(condition);
        if (StringUtils.isNotEmpty(guid) &&!"null".equals(guid)) {
            if (cabinets != null && !cabinets.isEmpty()) {
                if (cabinets.size() == 1) {
                    if (cabinets.get(0).getGuid().equals(guid)) {
                        return ResultUtil.success(false);
                    } else {
                        return ResultUtil.success(true);
                    }
                } else {
                    return ResultUtil.success(true);// 多条记录，存在
                }
            } else {
                return ResultUtil.success(false);// 不存在
            }
        } else {
            if (cabinets != null && !cabinets.isEmpty()) {
                return ResultUtil.success(true);
            } else {
                return ResultUtil.success(false);
            }
        }
    }


    /**
     * 判断位置上是否存在了机柜
     *
     * @param map
     * @return
     */
    @PostMapping(value = "/cabinetPosition")
    @ApiOperation(value="保存机柜的坐标",notes="保存机柜的坐标")
    @ApiImplicitParams({
            @ApiImplicitParam(name="guid",value="guid",required=true,dataType="String"),
            @ApiImplicitParam(name="roomGuid",value="roomGuid",required=true,dataType="String"),
            @ApiImplicitParam(name="marginTop",value="marginTop",required=true,dataType="String"),
            @ApiImplicitParam(name="marginLeft",value="marginLeft",required=true,dataType="String")
    })
    @SysRequestLog(description="保存机柜的坐标", actionType = ActionType.ADD,manually = false)
    public Result<Boolean> cabinetPosition(@RequestBody Map<String,Object> map) {
        String guid = map.get("guid").toString();
        String roomGuid = map.get("roomGuid").toString();
        String marginTop = map.get("marginTop").toString();
        String marginLeft = map.get("marginLeft").toString();
        Integer mTop = Integer.parseInt(marginTop);
        Integer mLeft = Integer.parseInt(marginLeft);
        List<QueryCondition> condition = new ArrayList<>();
        condition.add(QueryCondition.eq("roomGuid", roomGuid));
        condition.add(QueryCondition.eq("marginTop", mTop));
        condition.add(QueryCondition.eq("marginLeft", mLeft));
        List<Cabinet> cabinets = cabinetService.findAll(condition);
        if (StringUtils.isNotEmpty(guid) && !"null".equals(guid)) {// 编辑
            if (cabinets != null && !cabinets.isEmpty()) {
                if (cabinets.size() == 1) {
                    if (cabinets.get(0).getGuid().equals(guid)) {
                        return ResultUtil.success(false);
                    } else {
                        return ResultUtil.success(true);
                    }
                } else {
                    return ResultUtil.success(true);// 多条记录，存在
                }
            } else {
                return ResultUtil.success(false);// 不存在
            }
        } else {
            if (cabinets != null && !cabinets.isEmpty()) {
                return ResultUtil.success(true);
            } else {
                return ResultUtil.success(false);
            }
        }
    }


    /**
     * 保存添加机柜
     *
     *
     * @return
     */
    @PostMapping(value = "/addCabinet")
    @ApiOperation(value="保存添加机柜",notes="保存添加机柜")
    @SysRequestLog(description="保存添加机柜", actionType = ActionType.ADD,manually = false)
    public Result<Boolean> addCabinet(@RequestBody CabinetVO cabinetVO) {
        Cabinet cabinet = mapper.map(cabinetVO, Cabinet.class);
        cabinet.setGuid(UUIDUtils.get32UUID());
        cabinet.setMoved(0);
        try{
            cabinetService.save(cabinet);
            return ResultUtil.success(true);
        }catch(Exception e) {
            throw new RuntimeException("保存失败", e);
        }
    }


    @PostMapping(value = "/editCabinet")
    @ApiOperation(value="保存修改机柜",notes="保存修改机柜")
    @SysRequestLog(description="保存修改机柜", actionType = ActionType.UPDATE,manually = false)
    public Result<Boolean> editCabinet(@RequestBody CabinetVO cabinetVO) {
        Cabinet cabinet = cabinetService.getOne(cabinetVO.getGuid());
        mapper.copy(cabinetVO, cabinet);
        try{
            cabinetService.save(cabinet);
            return ResultUtil.success(true);
        }catch(Exception e){
            throw new RuntimeException("保存失败", e);
        }
    }

    @PostMapping("/getCabinetoPager")
    @ApiOperation(value="获取机柜列表",notes="获取机柜列表")
    @SysRequestLog(description="获取机柜列表", actionType = ActionType.SELECT,manually = false)
    public PageRes<CabinetVO> getCabinetoPager(@RequestBody CabinetVO cabinetVO){
        PageReq pageReq=new PageReq();
        int start = cabinetVO.getStart_().intValue();
        int count = cabinetVO.getCount_().intValue();
        pageReq.setCount(cabinetVO.getCount_());
        pageReq.setStart(start);
        pageReq.setCount(count);
        pageReq.setOrder(cabinetVO.getOrder_());
        pageReq.setBy(cabinetVO.getBy_());
        PageRes<CabinetVO> pageRes = cabinetService.getCabinetPager(cabinetVO, pageReq.getPageable());
        return pageRes;
    }

    /**
     * 删除机房
     *
     * @param guid
     * @return
     */
    @GetMapping(value = "/deleteMachineRoom/{guid}")
    @ApiOperation(value="删除机房",notes="删除机房")
    @ApiImplicitParams(@ApiImplicitParam(name="guid",value="guid",required=true,dataType="String"))
    @SysRequestLog(description="删除机房", actionType = ActionType.DELETE,manually = false)
    public Result<Boolean> deleteMachineRoom(@PathVariable String guid){
        List<QueryCondition> conditions = new ArrayList<>();
        MachineRoom machineroom = machineRoomService.getOne(guid);
        conditions.add(QueryCondition.eq("roomGuid", machineroom.getGuid()));
        List<Cabinet> cabinets = cabinetService.findAll(conditions);
        if (cabinets != null && !cabinets.isEmpty()) {
            for (Cabinet ca : cabinets) {
                cabinetService.delete(ca);
            }
        }
        machineRoomService.delete(guid);
        return ResultUtil.success(true);
    }


    /**
     * 删除机柜
     *
     * @param guid
     * @return
     */
    @GetMapping(value = "/deleteCabinet/{guid}")
    @ApiOperation(value="删除机柜",notes="删除机柜")
    @ApiImplicitParams(@ApiImplicitParam(name="guid",value="guid",required=true,dataType="String"))
    @SysRequestLog(description="删除机房", actionType = ActionType.DELETE,manually = false)
    public Result<Boolean> deleteCabinet(@PathVariable String guid){
        cabinetService.delete(guid);
        return ResultUtil.success(true);
    }

}
