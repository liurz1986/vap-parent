package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.xc.mapper.core.DataSourceManagerMapper;
import com.vrv.vap.xc.mapper.rds2.WhiteListMapper;
import com.vrv.vap.xc.model.FileInfoModel;
import com.vrv.vap.xc.pojo.DataSourceManager;
import com.vrv.vap.xc.pojo.WhiteList;
import com.vrv.vap.xc.service.DataSourceManagerService;
import com.vrv.vap.xc.tools.ExcelValHandleTools;
import com.vrv.vap.xc.vo.DataSourceManagerQuery;
import com.vrv.vap.xc.vo.WhiteListQuery;
import com.vrv.vap.toolkit.tools.PathTools;
import com.vrv.vap.toolkit.constant.ExcelEnum;
import com.vrv.vap.toolkit.excel.ExcelInfo;
import com.vrv.vap.toolkit.excel.in.Import;
import com.vrv.vap.toolkit.excel.out.ExcelData;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.excel.out.WriteHandler;
import com.vrv.vap.toolkit.plugin.util.QueryWrapperUtil;
import com.vrv.vap.toolkit.tools.ConvertTools;
import com.vrv.vap.toolkit.tools.LogAssistTools;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class DataSourceManagerServiceImpl implements DataSourceManagerService {

    private static final Log log = LogFactory.getLog(DataSourceManagerServiceImpl.class);

    @Autowired
    private DataSourceManagerMapper dataSourceManagerMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private WhiteListMapper whiteListMapper;

    @Override
    public int deleteDataSourceById(int id) {
        return dataSourceManagerMapper.deleteById(id);
    }

    @Override
    public int insertDataSource(DataSourceManager record) {
        record.setLastUpdateTime(new Date());
        record.setCreateTime(new Date());
        return dataSourceManagerMapper.insert(record);
    }

    @Override
    public void updateDataSourceByKey(DataSourceManager record) {
        record.setLastUpdateTime(new Date());
        DataSourceManager old = dataSourceManagerMapper.selectById(record.getId());
        if (old != null) {
            //审计变化
            String changes = LogAssistTools.compareDesc(old, record);
            record.setExtendDesc(changes);
        }
        dataSourceManagerMapper.updateById(record);
    }

    @Override
    public VList<DataSourceManager> selectDataSourceListByPage(DataSourceManagerQuery record) {
        /*DataSourceManagerExample dataSourceExample = new DataSourceManagerExample();
        PageTools.setAll(dataSourceExample, record);
        DataSourceManagerExample.Criteria criteria = dataSourceExample.createCriteria();
        ExampleTools.buildByCriteria(record, criteria).andLike("sourceName", record.getSourceName())
                .andLike("ip", record.getIp()).andEqual("port", record.getPort())
                .andLike("protocol", record.getProtocol()).andLike("description", record.getDescription());
        List<DataSourceManager> list = dataSourceManagerMapper.selectByExample(dataSourceExample);
        long total = dataSourceManagerMapper.countByExample(dataSourceExample);*/
        Page<DataSourceManager> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<DataSourceManager> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);
        return VoBuilder.vl(dataSourceManagerMapper.selectPage(page,queryWrapper));
    }

    @Override
    public VData<Export.Progress> exportDataSource(DataSourceManagerQuery record) {
        VList<DataSourceManager> tmpVlist = this.selectDataSourceListByPage(record);
        final int total = tmpVlist.getTotal();

        List<ExcelData> list = new ArrayList<>();
        ExcelInfo info = new ExcelInfo(
                ExcelEnum.DATA_SOURCE_MANAGER,
                PathTools.getExcelPath(ExcelEnum.DATA_SOURCE_MANAGER.getFilename()));
        ExcelData data = new ExcelData(info, total, new ArrayList<>());
        list.add(data);

        return VoBuilder.vd(Export.build(list, map -> {
            List<String> innerList = new ArrayList<>();
            Object tmp2 = null;
            ExcelValHandleTools ect = new ExcelValHandleTools();

            for (String filedName : ExcelEnum.DATA_SOURCE_MANAGER.getFields()) {
                Object cellValue = map.get(filedName);
                switch (filedName) {
                    case "createTime":
                        innerList.add(TimeTools.format((Date) cellValue, "yyyy-MM-dd HH:mm:ss"));
                        break;
                    case "lastUpdateTime":
                        innerList.add(TimeTools.format((Date) cellValue, "yyyy-MM-dd HH:mm:ss"));
                        break;
                    default:
                        tmp2 = cellValue;
                        innerList.add(null == tmp2 ? "" : tmp2.toString());
                }
            }
            return innerList.toArray(new String[0]);
        }).start(
                WriteHandler.fun(p -> {
                    final int batch = 500;
                    int start = 0;
                    while (start < total) {
                        record.setMyStart(start);
                        record.setMyCount(batch);
                        VList<DataSourceManager> vlist = this
                                .selectDataSourceListByPage(record);
                        start += batch;
                        p.writeBatchBean(0, vlist.getList());
                    }
                }, redisTemplate)));
    }

    @Override
    public Result importDataSource(FileInfoModel fileInfoModel) {
        ExcelInfo excel = new ExcelInfo(ExcelEnum.DATA_SOURCE_MANAGER, fileInfoModel.getLocalPath());
        List<Map<String, Object>> list = Import.getExcelData(excel);
        DataSourceManager record;
        Date updateTime = TimeTools.getNow();
        int sNum = 0;
        int errorNum = 0;
        for (Map<String, Object> map : list) {
            record = ConvertTools.wrapBean(map, new DataSourceManager());
            record.setLastUpdateTime(updateTime);
            try {
                dataSourceManagerMapper.insert(record);
                sNum++;
            } catch (DuplicateKeyException e) {
                log.error("", e);
                errorNum++;
            }
        }
        return VoBuilder.result(new Result("0", "数据插入成功" + sNum + "条，失败" + errorNum + "条"));
    }

    @Override
    public Result importDataSource2(FileInfoModel fileInfoModel) {
        ExcelInfo excel = new ExcelInfo(ExcelEnum.DATA_SOURCE_MANAGER2, fileInfoModel.getLocalPath());
        List<Map<String, Object>> list = Import.getExcelData(excel);
        WhiteList record;
        Date updateTime = TimeTools.getNow();
        int sNum = 0;
        int errorNum = 0;
        for (Map<String, Object> map : list) {
            record = ConvertTools.wrapBean(map, new WhiteList());
            record.setStatus("0");
            try {
                whiteListMapper.insert(record);
                sNum++;
            } catch (DuplicateKeyException e) {
                log.error("", e);
                errorNum++;
            }
        }
        return VoBuilder.result(new Result("0", "数据插入成功" + sNum + "条，失败" + errorNum + "条"));
    }

    @Override
    public VData<Export.Progress> exportDataSource2(WhiteListQuery record) {
        VList<WhiteList> tmpVlist = this.selectWhitelistListByPage(record);
        final int total = tmpVlist.getTotal();

        List<ExcelData> list = new ArrayList<>();
        ExcelEnum dataSourceManager2 = ExcelEnum.DATA_SOURCE_MANAGER2;
        ExcelInfo info = new ExcelInfo(dataSourceManager2, PathTools.getExcelPath(dataSourceManager2.getFilename()));
        ExcelData data = new ExcelData(info, total, new ArrayList<>());
        list.add(data);

        return VoBuilder.vd(Export.build(list, map -> {
            List<String> innerList = new ArrayList<>();
            Object tmp2 = null;
            ExcelValHandleTools ect = new ExcelValHandleTools();

            for (String filedName : dataSourceManager2.getFields()) {
                Object cellValue = map.get(filedName);
                switch (filedName) {
                    case "insertTime":
                        innerList.add(TimeTools.format((Date) cellValue, "yyyy-MM-dd HH:mm:ss"));
                        break;
                    case "status":
                        innerList.add("1".equals(String.valueOf(cellValue)) ? "启用" : "禁用");
                        break;
                    default:
                        tmp2 = cellValue;
                        innerList.add(null == tmp2 ? "" : tmp2.toString());
                }
            }
            return innerList.toArray(new String[0]);
        }).start(
                WriteHandler.fun(p -> {
                    final int batch = 500;
                    int start = 0;
                    while (start < total) {
                        record.setMyStart(start);
                        record.setMyCount(batch);
                        VList<WhiteList> vlist = this.selectWhitelistListByPage(record);
                        start += batch;
                        p.writeBatchBean(0, vlist.getList());
                    }
                }, redisTemplate)));
    }

    @Override
    public VList<WhiteList> selectWhitelistListByPage(WhiteListQuery record) {
        /*WhiteListExample dataSourceExample = new WhiteListExample();
        PageTools.setAll(dataSourceExample, record);
        WhiteListExample.Criteria criteria = dataSourceExample.createCriteria();
        ExampleTools.buildByCriteria(record, criteria).andLike("devid", record.getDevid())
                .andLike("company", record.getCompany()).andLike("number", record.getNumber())
                .andLike("name", record.getName()).andEqual("status", record.getStatus());
        List<WhiteList> list = whiteListMapper.selectByExample(dataSourceExample);
        long total = whiteListMapper.countByExample(dataSourceExample);*/
        Page<WhiteList> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<WhiteList> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);
        return VoBuilder.vl(whiteListMapper.selectPage(page,queryWrapper));
    }

    @Override
    public int deleteWhitelistById(int singleId) {
        /*WhiteListExample dataSourceExample = new WhiteListExample();
        WhiteListExample.Criteria criteria = dataSourceExample.createCriteria();
        criteria.andIdEqualTo(singleId);
        //只能删除非开启状态的数据
        criteria.andStatusNotEqualTo("1");*/
        QueryWrapper<WhiteList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", singleId).ne("status", "1");
        return whiteListMapper.delete(queryWrapper);
    }

    @Override
    public int insertWhitelist(WhiteList device) {
        return whiteListMapper.insert(device);
    }

    @Override
    public void updateWhitelistByKey(WhiteList device) {
        WhiteList old = whiteListMapper.selectById(device.getId());
        if (old != null) {
            //审计变化
            String changes = LogAssistTools.compareDesc(old, device);
            device.setExtendDesc(changes);
        }
        whiteListMapper.updateById(device);
    }

    @Value("${datasource.local-path}")
    private String datasourceJsonPath;

    @Override
    public void readJsonDataAndStore() {
        ObjectMapper om = new ObjectMapper();
        try {
            Map<String, Object> datasources = om.readValue(new File(FilenameUtils.normalize(datasourceJsonPath)), HashMap.class);
            WhiteListQuery param = new WhiteListQuery();
            //数据不多,直接获取所有的
            param.setMyCount(10000);
            List<WhiteList> whiteList = selectWhitelistListByPage(param).getList();
            //获取到数据库中已经存在的所有devid信息
            String JOINT = "_";
            Set<String> devids = whiteList.stream().map(r -> r.getDevid() + JOINT + r.getNumber()).collect(Collectors.toSet());
            List<WhiteList> addWhiteList = new ArrayList<>();
            List<WhiteList> updateWhiteList = new ArrayList<>();
            datasources.forEach((k, v) -> {
                Map<String, String> item = (Map<String, String>) v;
                WhiteList device = new WhiteList();
                //device.setDevid(k);
                device.setDevid(String.valueOf(item.get("devid")));
                device.setCompany(String.valueOf(item.get("company")));
                device.setName(String.valueOf(item.get("name")));
                device.setNumber(String.valueOf(item.get("number")));
                if (devids.contains(device.getDevid() + JOINT + device.getNumber())) {
                    updateWhiteList.add(device);
                } else {
                    device.setStatus("1");
                    addWhiteList.add(device);
                }
            });
            updateWhiteList.forEach(r -> {
                    //WhiteListExample example = new WhiteListExample();
                    //example.createCriteria().andDevidEqualTo(r.getDevid()).andNumberEqualTo(r.getNumber());
                    QueryWrapper<WhiteList> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("devid", r.getDevid()).eq("number", r.getNumber());
                    whiteListMapper.update(r, queryWrapper);
                }
            );
            addWhiteList.forEach(r -> whiteListMapper.insert(r));

        } catch (IOException e) {
            log.error(e);
        }
    }
}
