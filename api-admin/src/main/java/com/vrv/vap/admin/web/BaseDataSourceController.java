package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.util.AESUtil;
import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.common.util.YmlUtils;
import com.vrv.vap.admin.model.BaseDataSource;
import com.vrv.vap.admin.model.BaseDataSource;
import com.vrv.vap.admin.model.BaseReport;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.service.BaseDataSourceService;
import com.vrv.vap.admin.service.BaseDataSourceService;
import com.vrv.vap.admin.service.UserService;
import com.vrv.vap.admin.vo.AreaUserQuery;
import com.vrv.vap.admin.vo.BaseDataSourceVo;
import com.vrv.vap.admin.vo.IpRangeVO;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(path = "/dataSource")
public class BaseDataSourceController extends ApiController {

    @Autowired
    private BaseDataSourceService baseDataSourceService;

    private String sKey = "1234567887654321";

    /**
     * 获取所数据源
     * @return
     */
    @ApiOperation(value = "获取所有数据源")
    @GetMapping(value = "/all")
    public VData<List<BaseDataSource>> queryAllArea() {
        return this.vData(baseDataSourceService.findAll());
    }


    /**
     * 条件查询地区
     * 支持分页查询、条件查询 、任意字段排序
     */
    @ApiOperation(value = "条件查询数据源")
    @PostMapping
    public VList<BaseDataSource> queryAreas(@RequestBody BaseDataSourceVo baseDataSourceVo) {
        Example example = this.pageQuery(baseDataSourceVo, BaseDataSource.class);
        return this.vList(baseDataSourceService.findByExample(example));
    }

    /**
     * 添加数据源
     */
    @ApiOperation(value = "添加数据源")
    @PutMapping
    public Result add(@RequestBody BaseDataSource baseDataSource) throws Exception {
        baseDataSource.setCreateTime(new Date());
        baseDataSource.setPassword(AESUtil.Encrypt(baseDataSource.getPassword(),sKey));
        int result = baseDataSourceService.save(baseDataSource);
        if(result == 1){
            return this.vData(baseDataSource);
        }
        return this.result( false);
    }

    /**
     * 修改数据源
     */
    @ApiOperation(value = "修改数据源")
    @PatchMapping
    public Result edit(@RequestBody BaseDataSource baseDataSource){
        int result = baseDataSourceService.updateSelective(baseDataSource);
        return this.result(result == 1);
    }

    @ApiOperation(value = "数据源详情")
    @GetMapping(value = "/{dataSourceId}")
    public VData<BaseDataSource> detail(@PathVariable("dataSourceId") Integer dataSourceId){
        return this.vData( baseDataSourceService.findById(dataSourceId));
    }

    @ApiOperation(value = "删除数据源")
    @DeleteMapping
    public Result deleteModel(@RequestBody DeleteQuery param) {
        int result = baseDataSourceService.deleteByIds(param.getIds());
        return this.result(result >= 1);
    }

}
