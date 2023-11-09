package com.vrv.vap.admin.web;

import com.google.gson.Gson;
import com.vrv.vap.admin.util.FileFilterUtil;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.admin.common.constant.PageConstants;
import com.vrv.vap.admin.model.DiscoverIndex;
import com.vrv.vap.admin.model.DiscoverIndexField;
import com.vrv.vap.admin.service.IndexService;
import com.vrv.vap.admin.vo.IndexFieldQuery;
import com.vrv.vap.admin.vo.IndexQuery;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 索引管理
 *
 * @author lilang
 * @date 2018年2月2日
 */
@RestController
@RequestMapping(path = "/index")
public class IndexController extends ApiController {

    public static final String INDEX_ID = "indexid";
    @Autowired
    IndexService indexService;
    @Value("${elk.vap.index}")
    private String SETTING_INDEX;
    @Value("${elk.vap.indexPattern}")
    private String INDEX_PATTERN;
    //索引组
    private static final Integer GROUPTYPE_GROUP = 1;

    @GetMapping
     public Result getIndexes(){
         return  this.vData(indexService.findAll());
     }

    /**
     * 获取所有索引
     *
     * @param param
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @PostMapping
    @ApiOperation("获取所有索引")
    @SysRequestLog(description = "获取所有索引",actionType = ActionType.SELECT)
    public Result queryIndexAll(@RequestBody IndexQuery param) {
        param.setOrder_(INDEX_ID);
        param.setBy_("asc");
        Example example = this.pageQuery(param, DiscoverIndex.class);
        return this.vList(indexService.findByExample(example));
    }

    /**
     * 添加索引
     * @param param
     * @return
     */
    @PutMapping
    @ApiOperation("添加索引")
    @SysRequestLog(description = "添加索引",actionType = ActionType.ADD)
    public Result addIndex(@RequestBody DiscoverIndex param) {
        DiscoverIndex discoverIndex = new DiscoverIndex();
        discoverIndex.setIndexid(param.getIndexid());
        discoverIndex.setIndexname(SETTING_INDEX);
        discoverIndex.setType(INDEX_PATTERN);
        discoverIndex.setTitle(param.getIndexid());
        discoverIndex.setTitledesc(param.getTitledesc());
        discoverIndex.setTimefieldname(param.getTimefieldname());
        discoverIndex.setCategory(param.getCategory());
        discoverIndex.setDefaultindex(0);

        List<DiscoverIndexField> _fields = indexService.queryFieldByIndexId(param.getIndexid());
        JSONArray jsonArray = JSONArray.fromObject(_fields);
        discoverIndex.setIndexfields(jsonArray.toString());
        int result = indexService.save(discoverIndex);
        return this.result(result == 1);

    }

    /**
     * 修改索引
     *
     * @param discoverIndex
     * @return
     */
    @PatchMapping
    @ApiOperation("修改索引")
    @SysRequestLog(description = "修改索引",actionType = ActionType.UPDATE)
    public Result updateIndex(@RequestBody DiscoverIndex discoverIndex) {
        String indexId = discoverIndex.getIndexid();
        if (StringUtils.isEmpty(indexId)) {
            discoverIndex.setIndexid(discoverIndex.getTitle());
        }
        int result = indexService.updateSelective(discoverIndex);
        return this.result(result == 1);

    }

    /**
     * 删除索引
     *
     * @param param
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除索引")
    @SysRequestLog(description = "删除索引",actionType = ActionType.DELETE)
    public Result deleteIndex(@RequestBody DeleteQuery param) {
        int result = indexService.deleteByIds(param.getIds());
        IndexQuery query = new IndexQuery();
        query.setOrder_(INDEX_ID);
        query.setBy_("asc");
        Example example = this.pageQuery(query, DiscoverIndex.class);
        List<DiscoverIndex> indexList = indexService.findByExample(example);
        if (!CollectionUtils.isEmpty(indexList)) {
            boolean flag = false;
            for (DiscoverIndex index : indexList) {
                if (index.getDefaultindex() == PageConstants.IS_OK) {
                    flag = true;
                }
            }
            if (!flag && CollectionUtils.isNotEmpty(indexList)) {
                DiscoverIndex discoverIndex = indexList.get(0);
                discoverIndex.setDefaultindex(PageConstants.IS_OK);
                indexService.updateSelective(discoverIndex);
            }
        }
        return this.result(result >= 1);
    }

    /**
     * 索引字段查询
     *
     * @param param
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @PostMapping(path = "/field")
    @ApiOperation("索引字段查询")
    @SysRequestLog(description = "索引字段查询", actionType = ActionType.SELECT)
    public VList<DiscoverIndexField> queryIndexFields(@RequestBody IndexQuery param) {
        String fields;
        JSONArray jsonArray;
        List<DiscoverIndexField> fieldList;
        String filterType = param.getFilterType();
        // 添加索引关联查询时间字段查es
        if (filterType != null) {
            List<Map<String, Object>> filterList = new ArrayList<>();
            String indexId = param.getIndexid();
            List<DiscoverIndexField> _fields = indexService.queryFieldByIndexId(indexId);
            if (!CollectionUtils.isEmpty(_fields)) {
                if (StringUtils.isEmpty(filterType) || "*".equals(filterType)) {
                    return this.vList(_fields, _fields.size());
                }
                for (DiscoverIndexField field : _fields) {
                    if (filterType.equals(field.getType())) {
                        Map map = new HashMap<String, Object>();
                        map.put("name", field.getName());
                        filterList.add(map);
                    }
                }
            }
            return this.vList(filterList, filterList.size());
        }
        DiscoverIndex discoverIndex = indexService.findById(param.getId());
        fields = discoverIndex.getIndexfields();
        jsonArray = JSONArray.fromObject(fields);
        fieldList = this.convertJsonFields(jsonArray);

        return this.vList(fieldList, fieldList.size());
    }

    private List<DiscoverIndexField> convertJsonFields(JSONArray jsonArray) {
        List<DiscoverIndexField> fieldList;
        Gson gson = new Gson();
        List<String> list = new ArrayList<>();
        if (jsonArray != null && jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size();i++) {
                JSONObject job = jsonArray.getJSONObject(i);
                String jsonFields = gson.toJson(job.get("jsonFields"));
                list.add(jsonFields);
                job.put("jsonFields","");
            }
        }
        fieldList = (List<DiscoverIndexField>) JSONArray.toCollection(jsonArray, DiscoverIndexField.class);
        if (CollectionUtils.isNotEmpty(fieldList)) {
            for (int j = 0; j < fieldList.size();j++) {
                DiscoverIndexField indexField = fieldList.get(j);
                indexField.setJsonFields(list.get(j));
            }
        }
        return fieldList;
    }

    /**
     * 修改索引字段配置
     *
     * @param param
     * @return
     * @throws Exception
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @PatchMapping(path = "/field")
    @SysRequestLog(description = "修改索引字段配置",actionType = ActionType.UPDATE)
    @ApiOperation("修改索引字段配置")
    public Result updateIndexField(@RequestBody IndexFieldQuery param) {
        DiscoverIndex discoverIndex = indexService.findById(param.getId());
        String fields = discoverIndex.getIndexfields();
        JSONArray jsonArray = JSONArray.fromObject(fields);
        DiscoverIndex disIndex = new DiscoverIndex();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject field = jsonArray.getJSONObject(i);
            String name = (String) field.get("name");
            if (name.equals(param.getName())) {
                field.put("nameDesc", param.getNameDesc());
                field.put("filtered", param.getFiltered());
                field.put("displayed", param.getDisplayed());
                field.put("format",param.getFormat());
                field.put("linkType",param.getLinkType());
                field.put("tag",param.getTag());
                field.put("sort",param.getSort());
                field.put("unit",param.getUnit());
                field.put("size",param.getSize());
                field.put("detailed",param.getDetailed());
                field.put("type",param.getType());
            }
        }
        disIndex.setId(discoverIndex.getId());
        disIndex.setIndexfields(jsonArray.toString());
        // 保存字典详情
        int result = indexService.updateSelective(disIndex);
        return this.result(result == 1);
    }

    /**
     * 设置默认索引
     *
     * @param param
     * @return
     */
    @ApiOperation("设置默认索引")
    @PutMapping(path = "/config")
    @SysRequestLog(description = "设置默认索引",actionType = ActionType.ADD)
    public Result setDefaultIndex(@RequestBody IndexQuery param) {
        Example example = new Example(DiscoverIndex.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("defaultindex",PageConstants.IS_OK);
        List<DiscoverIndex> indexList = indexService.findByExample(example);
        if (!indexList.isEmpty()) {
            for (DiscoverIndex discoverIndex : indexList) {
                DiscoverIndex disIndex = new DiscoverIndex();
                disIndex.setId(discoverIndex.getId());
                disIndex.setDefaultindex(PageConstants.IS_NOT);
                indexService.updateSelective(disIndex);
            }
        }
        DiscoverIndex discIndex = new DiscoverIndex();
        discIndex.setId(param.getId());
        discIndex.setDefaultindex(PageConstants.IS_OK);
        int result = indexService.updateSelective(discIndex);
        return this.result(result == 1);
    }

    /**
     * 同步索引字段
     * @param param
     * @return
     */
    @PatchMapping(path = "/syncField")
    @ApiOperation("同步索引字段")
    @SysRequestLog(description = "同步索引字段",actionType = ActionType.UPDATE)
    public Result syncIndexField(@RequestBody IndexQuery param) {
        // es索引字段
        String indexId = param.getIndexid();
        List<DiscoverIndexField> _fields = indexService.queryFieldByIndexId(indexId);
        // 数据库索引字段
        Example example = this.pagination(param, DiscoverIndex.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(indexId)){
            criteria.andEqualTo(INDEX_ID,indexId);
        }
        DiscoverIndex discoverIndex = indexService.findByExample(example).get(0);
        String fields = discoverIndex.getIndexfields();
        List<DiscoverIndexField> fieldList = new ArrayList<>();
        if (!StringUtils.isEmpty(fields)) {
            JSONArray jsonArray = JSONArray.fromObject(fields);
            fieldList = (List<DiscoverIndexField>) JSONArray.toCollection(jsonArray, DiscoverIndexField.class);
        }
        // es中新增的字段
        List <DiscoverIndexField> _fieldList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(_fields)) {
            for (DiscoverIndexField _filed : _fields) {
                boolean exist = false;
                if (!CollectionUtils.isEmpty( fieldList)) {
                    for (DiscoverIndexField indexField : fieldList) {
                        if (_filed.getName().equals(indexField.getName())) {
                            exist = true;
                        }
                    }
                }
                if (!exist) {
                    _fieldList.add(_filed);
                }
            }
        }
        // 保存新增字段
        if (!CollectionUtils.isEmpty(_fieldList)) {
            fieldList.addAll(_fieldList);
            JSONArray _jsonArray = JSONArray.fromObject(fieldList);
            discoverIndex.setIndexfields(_jsonArray.toString());
            int result = indexService.updateSelective(discoverIndex);
            return this.result(result == 1);
        }
        return this.result(true);
    }

    @PostMapping(path = "/import")
    @ApiOperation("离线导入索引")
    @SysRequestLog(description = "离线导入索引",actionType = ActionType.ADD)
    public Result importIndex(@ApiParam(value = "导入的文件", required = true) MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        // 扫描备注：已做文件格式白名单校验
        if (!FileFilterUtil.validFileType(fileType)) {
            return new Result("-1","文件类型错误，支持类型:"+ org.apache.commons.lang3.StringUtils.join(FileFilterUtil.fileTypes.toArray(), ","));
        }
        Integer result = indexService.importIndex(file);
        return this.result(result >= 1);
    }
}
