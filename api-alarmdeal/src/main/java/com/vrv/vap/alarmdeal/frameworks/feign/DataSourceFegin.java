package com.vrv.vap.alarmdeal.frameworks.feign;

import com.vrv.vap.alarmdeal.frameworks.contract.audit.LabelConf;
import com.vrv.vap.alarmdeal.frameworks.contract.dataSource.DataSource;
import com.vrv.vap.alarmdeal.frameworks.contract.dataSource.DataSourceField;
import com.vrv.vap.jpa.web.ResultObjVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年05月26日 11:09
 */
@FeignClient(name = "api-data",configuration = ConfigurationFegin.class)
public interface DataSourceFegin {
    /**
     * 新增数据源
     * @param param
     * @return
     */
    @RequestMapping(value = "/source", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultObjVO<List<DataSource>> addSource(@RequestBody DataSource param);

    /**
     * 查询数据源
     * @param param
     * @return
     */
    @RequestMapping(value = "/source", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultObjVO<List<DataSource>> querySource(@RequestBody Map<String,Object> param);

    /**
     * 删除数据源
     * @param param
     * @return
     */
    @RequestMapping(value = "/source", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultObjVO<List<DataSource>> deleteSource(@RequestBody Map<String,Object> param);

    /**
     * 获取全部可用数据源
     * @return
     */
    @RequestMapping(value = "/source", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultObjVO<List<DataSource>> getSource();

    /**
     * 获取全部可用数据源
     * @return
     */
    @RequestMapping(value = "/source", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultObjVO<List<DataSource>> getSourceByParam(@RequestBody Map<String,Object> param);

    /**
     * 修改数据源
     * @param param
     * @return
     */
    @RequestMapping(value = "/source", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultObjVO<List<DataSource>> editSource(@RequestBody Map<String, Object> param);

    /**
     * 获取字段, 说明： 仅传 type 和 name
     * @param param
     * @return
     */
    @RequestMapping(value = "/source/field/fetch", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultObjVO<List<DataSourceField>> fetchSource(@RequestBody DataSource param);

    /**
     * 根据指定数据源下的全部字段
     * @param sourceId
     * @return
     */
    @RequestMapping(value = "/source/field/{sourceId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultObjVO<List<DataSourceField>> getFieldBySourceId(@PathVariable ("sourceId") String sourceId);

    /**
     * 根据指定数据源下的全部字段
     * @param sourceId
     * @return
     */
    @RequestMapping(value = "/source/monitor/{sourceId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultObjVO<List<DataSourceField>> getMonitorBySourceId(@PathVariable ("sourceId") String sourceId);
}
