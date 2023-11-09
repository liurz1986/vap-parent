package com.vrv.vap.admin.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.admin.common.constant.PageConstants;
import com.vrv.vap.admin.model.VisualMapModel;
import com.vrv.vap.admin.model.VisualWidgetModel;
import com.vrv.vap.admin.service.VisualMapService;
import com.vrv.vap.admin.vo.VisualMapQuery;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 *地图对象管理
 *
 * @author wangneng
 * @date 2018年6月12日
 */
@RestController
@RequestMapping(path = "/map")
public class MapController extends ApiController {




    @Autowired
    VisualMapService visualMapService;



    @GetMapping
    @SysRequestLog(description = "获取所有地图对象",actionType = ActionType.SELECT)
    public Result getAllWidget(){
        return this.vData(visualMapService.findAll());
    }

    /**
     * 获取地图
     *
     * @param param
     * @return
     */
    @ApiOperation("获取地图对象")
    @SuppressWarnings({"unchecked", "rawtypes"})
    @PostMapping
    @SysRequestLog(description = "获取地图对象",actionType = ActionType.SELECT)
    public VList queryMapList(@RequestBody VisualMapQuery param) {
        param.setOrder_("lastUpdateTime");
        param.setBy_("desc");
        Example example = this.pageQuery(param, VisualWidgetModel.class);
        return this.vList(visualMapService.findByExample(example));
    }

    /**
     * 新增地图对象
     *
     * @param visualMapModel
     * @return
     */
    @ApiOperation("新增地图对象")
    @PutMapping
    @SysRequestLog(description = "新增地图对象",actionType = ActionType.ADD)
    public Result addMapp(@RequestBody VisualMapModel visualMapModel) {
        visualMapModel.setLastUpdateTime(new Date());
        visualMapModel.setMapDefault("0");
        //查询是否有默认的地图
        int result = visualMapService.save(visualMapModel);
        initDefault();
        if(result==1){
            return  this.vData(visualMapModel);
        }
        return this.result(false);
    }

    /**
     * 修改地图对象
     *
     * @param visualMapModel
     * @return
     */
    @ApiOperation("修改地图对象")
    @PatchMapping
    @SysRequestLog(description = "修改地图对象",actionType = ActionType.UPDATE)
    public Result updateMap(@RequestBody VisualMapModel visualMapModel) {
        visualMapModel.setLastUpdateTime(new Date());
        int result = visualMapService.updateSelective(visualMapModel);
        initDefault();
        return this.result(result == 1);
    }

    /**
     * 删除地图对象
     *
     * @param param
     * @return
     */
    @ApiOperation("删除地图对象")
    @DeleteMapping
    @SysRequestLog(description = "删除地图对象",actionType = ActionType.DELETE)
    public Result deleteMap(@RequestBody DeleteQuery  param) {
        int result = visualMapService.deleteByIds(param.getIds());
        initDefault();
        return this.result(result >= 1);
    }


    /**
     * 设置默认地图
     *
     * @param param
     * @return
     */
    @ApiOperation("设置默认地图")
    @PutMapping(path = "/default")
    @SysRequestLog(description = "设置默认地图",actionType = ActionType.ADD)
    public Result setDefaultMap(@RequestBody VisualMapQuery param) {
        this.setDefault(param.getId());
        return this.result(true);
    }

    /**
     * 设置默认地图
     *
     * @param
     * @return
     */
    @ApiOperation("获取默认地图")
    @GetMapping(path = "/default")
    @SysRequestLog(description = "获取默认地图",actionType = ActionType.SELECT)
    public Result getDefaultMap() {
        VisualMapQuery queryParam = new VisualMapQuery();
        queryParam.setMapDefault("1");
        queryParam.setCount_(1);
        Example example = this.pageQuery(queryParam, VisualMapModel.class);
        List<VisualMapModel> mapList = visualMapService.findByExample(example);
        return this.vData(mapList);
    }


    private void setDefault(Integer id){
        VisualMapQuery queryParam = new VisualMapQuery();
        queryParam.setMapDefault("1");
        queryParam.setCount_(999);
        Example example = this.pageQuery(queryParam, VisualMapModel.class);
        List<VisualMapModel> mapList = visualMapService.findByExample(example);
        if (!mapList.isEmpty()) {
            for (VisualMapModel visualMapModel : mapList) {
                VisualMapModel visModel = new VisualMapModel();
                visModel.setId(visualMapModel.getId());
                visModel.setMapDefault(String.valueOf(PageConstants.IS_NOT));
                visualMapService.updateSelective(visModel);
            }
        }
        VisualMapModel visModel = new VisualMapModel();
        visModel.setId(id);
        visModel.setMapDefault(String.valueOf(PageConstants.IS_OK));
        int result = visualMapService.updateSelective(visModel);
    }

    private void initDefault(){
        VisualMapQuery queryParam = new VisualMapQuery();
        queryParam.setMapDefault(String.valueOf(PageConstants.IS_OK));
        Example example = this.pageQuery(queryParam, VisualMapModel.class);
        List<VisualMapModel> mapList = visualMapService.findByExample(example);
        if (mapList.isEmpty()) {
            queryParam = new VisualMapQuery();
            queryParam.setOrder_("lastUpdateTime");
            queryParam.setBy_("desc");
            queryParam.setStart_(0);
            queryParam.setCount_(1);
            example = this.pageQuery(queryParam, VisualMapModel.class);
            mapList = visualMapService.findByExample(example);
            if(mapList.size()>0) {
                VisualMapModel visModel = new VisualMapModel();
                visModel.setId(mapList.get(0).getId());
                visModel.setMapDefault(String.valueOf(PageConstants.IS_OK));
                visualMapService.updateSelective(visModel);
            }
        }

    }

}
