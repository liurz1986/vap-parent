package com.vrv.vap.alarmdeal.business.baseauth.service;

import com.vrv.vap.alarmdeal.business.baseauth.vo.CoordinateVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.TrendResultVO;
import com.vrv.vap.jpa.web.Result;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2021年12月24日 15:20
 */
public interface BaseAuthService {

    Map<String, List<Map<String, Object>>> checkImportData(MultipartFile file, Integer code);

    void saveList(Map<String, Object> map);

    Result<String> exportInfo(Map<String, Object> map);

    void dealData(List<Integer> saveList);

    Result<Map<String, Object>> getTotalStatisticsV2(String type) throws ParseException;

    Result<Map<String, Object>> getPrintStatistics() throws ParseException;

    Result<Map<String, Object>> getBurnStatistics() throws ParseException;

    Result<Map<String, Object>> getAccessHostStatistics() throws ParseException;

    Result<Map<String, Object>> getExternalAssetStatistics() throws ParseException;

    Result<List<CoordinateVO>> getMaintenFlagCountStatistics() throws ParseException;

    List<TrendResultVO> getMaintenFlagMonthStatistics()throws ParseException;
}
