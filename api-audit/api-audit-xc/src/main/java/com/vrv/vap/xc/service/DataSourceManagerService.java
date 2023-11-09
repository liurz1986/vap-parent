package com.vrv.vap.xc.service;

import com.vrv.vap.xc.model.FileInfoModel;
import com.vrv.vap.xc.pojo.DataSourceManager;
import com.vrv.vap.xc.pojo.WhiteList;
import com.vrv.vap.xc.vo.DataSourceManagerQuery;
import com.vrv.vap.xc.vo.WhiteListQuery;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;

/**
 * 数据源 服务接口
 *
 * @author lizj
 */
public interface DataSourceManagerService {


    VList<DataSourceManager> selectDataSourceListByPage(DataSourceManagerQuery device);

    int deleteDataSourceById(int singleId);

    int insertDataSource(DataSourceManager device);

    void updateDataSourceByKey(DataSourceManager param);

    VData<Export.Progress> exportDataSource(DataSourceManagerQuery record);

    Result importDataSource(FileInfoModel fileInfoModel);

    Result importDataSource2(FileInfoModel fileInfoModel);

    VData<Export.Progress> exportDataSource2(WhiteListQuery record);

    VList<WhiteList> selectWhitelistListByPage(WhiteListQuery device);

    int deleteWhitelistById(int singleId);

    int insertWhitelist(WhiteList device);

    void updateWhitelistByKey(WhiteList param);

    void readJsonDataAndStore();
}
