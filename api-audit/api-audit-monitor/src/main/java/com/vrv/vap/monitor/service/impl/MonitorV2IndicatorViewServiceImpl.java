package com.vrv.vap.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.vrv.vap.monitor.entity.Monitor2IndicatorView;
import com.vrv.vap.monitor.mapper.Monitor2IndicatorViewMapper;
import com.vrv.vap.monitor.service.MonitorV2IndicatorViewService;
import com.vrv.vap.toolkit.plugin.util.QueryWrapperUtil;
import com.vrv.vap.toolkit.vo.Query;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitorV2IndicatorViewServiceImpl implements MonitorV2IndicatorViewService {

    @Autowired
    private Monitor2IndicatorViewMapper mapper;

    private final static String VIEW_PARAM_FORMAT = "{\"assetId\":\"xxxxx\",\"indicators\":\"%s\",\"viewType\":\"%s\"}";

    private final static ObjectMapper objectMapper;
    private final static ObjectWriter prettyWriter;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //美化json
        prettyWriter = objectMapper.writerWithDefaultPrettyPrinter();
    }

    @Override
    public int addItem(Monitor2IndicatorView monitor2IndicatorView) {
        fillQueryParam(monitor2IndicatorView);
        compressDataSample(monitor2IndicatorView);
        return mapper.insert(monitor2IndicatorView);
    }

    private void fillQueryParam(Monitor2IndicatorView monitor2IndicatorView) {
        String param = String.format(VIEW_PARAM_FORMAT, monitor2IndicatorView.getIndicators(), monitor2IndicatorView.getViewType());
        monitor2IndicatorView.setParamDesc(param);
    }

    @Override
    public int updateItem(Monitor2IndicatorView monitor2IndicatorView) {
        fillQueryParam(monitor2IndicatorView);
        compressDataSample(monitor2IndicatorView);
        return mapper.updateById(monitor2IndicatorView);
    }

    private void compressDataSample(Monitor2IndicatorView monitor2IndicatorView) {
        try {
            JsonNode jsonNode = objectMapper.readTree(monitor2IndicatorView.getDataSample());
            monitor2IndicatorView.setDataSample(jsonNode.toString());
        } catch (JsonProcessingException e) {
        }
    }

    @Override
    public int deleteItem(Monitor2IndicatorView monitor2IndicatorView) {
        return mapper.deleteById(monitor2IndicatorView.getId());
    }

    @Override
    public Monitor2IndicatorView querySingle(Monitor2IndicatorView monitor2IndicatorView) {
        return mapper.selectById(monitor2IndicatorView.getId());
    }

    @Override
    public VList<Monitor2IndicatorView> queryByPage(Query record) {
        Page<Monitor2IndicatorView> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<Monitor2IndicatorView> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);
        return VoBuilder.vl(mapper.selectPage(page, queryWrapper));
    }

    @Override
    public VData<List<Monitor2IndicatorView>> queryAll(Query query) {
        QueryWrapper<Monitor2IndicatorView> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, query);
        return VoBuilder.vd(mapper.selectList(queryWrapper));
    }
}
