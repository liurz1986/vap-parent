package com.vrv.vap.line.service;

import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.line.model.EsTemplate;

import java.util.Map;

/**
 * es公共服务
 * Created by lizj on 2019/09/16.
 */
public interface CommonService {

    Result createAlias();

    Map create365Alias(String index, String indexPrefix, String timeField, String timeFormat, String year, boolean force);

    Result createTemplate(EsTemplate template);

    boolean indexTemplateExists(String templateName);

    boolean add2ApiData(String name);
}
