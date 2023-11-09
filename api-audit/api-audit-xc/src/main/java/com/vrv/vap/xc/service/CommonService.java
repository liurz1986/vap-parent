package com.vrv.vap.xc.service;

import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.xc.model.EsTemplate;
import com.vrv.vap.xc.pojo.DataDumpLog;

import java.io.IOException;
import java.util.Map;

/**
 * es公共服务
 * Created by lizj on 2019/09/16.
 */
public interface CommonService {

    Result createAlias();

    Map create365Alias(String index, String indexPrefix, String timeField, String timeFormat, String year, boolean force);

    Result setWindowMaxResult();

    Result dataClean(Map<String, Object> paramModel);

    Result dataTransfer(Map<String, Object> paramModel);

    Result dataBackup(Map<String, Object> paramModel);

    Result dataBackupAndCLean(Map<String, Object> paramModel);

    Result datarollBack(DataDumpLog paramModel);

    Result createTemplate(EsTemplate template);

    boolean indexTemplateExists(String templateName);
}
