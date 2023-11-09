package com.vrv.vap.admin.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StateMapper {
    List<Map<String, Object>> getOrgInfo();
    Map<String, Object> getPersonInfo();
    List<Map<String, Object>> getNetworkInfo();
    Map<String, Object> getTerminalInfo();
    Map<String, Object> getServerInfo();
    List<Map<String, Object>> getAssetInfo();
    List<Map<String, Object>> getFirewallInfo();
    List<Map<String, Object>> getIdsInfo();
    List<Map<String, Object>> getGatewayInfo();
    List<Map<String, Object>> getScannerInfo();
    Map<String, Object> getFileInfo();
    List<Map<String, Object>> getAppInfo();
    List<Map<String, Object>> getMonitorInfo();
    int getInternetOrgInfo();
}