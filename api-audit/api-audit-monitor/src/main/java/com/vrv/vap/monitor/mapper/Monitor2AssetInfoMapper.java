package com.vrv.vap.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.monitor.entity.Monitor2AssetInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * V2-资产监控表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-10-26
 */
public interface Monitor2AssetInfoMapper extends BaseMapper<Monitor2AssetInfo> {

    List<Map<String, Object>> selectAssetConnectStatus();

    int selectAssetConnectCount();

    int selectAssetUnConnectCount();

    int selectAssetUnMonitorCount();

    List<Map<String, Object>> selectAssetOnlineTop();

}
