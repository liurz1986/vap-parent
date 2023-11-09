package com.vrv.vap.alarmdeal.business.baseauth.service;

import com.vrv.vap.alarmdeal.business.baseauth.vo.CoordinateVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.TrendResultVO;
import com.vrv.vap.jpa.web.Result;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface BaseAuthOverviewService {
   public  Result<Map<String, Object>> getTotalStatistics(String type) throws ParseException;

   public Result<Map<String, Object>> getPrintStatistics() throws ParseException;

   public Result<Map<String, Object>> getBurnStatistics() throws ParseException;

   public Result<Map<String, Object>> getAccessHostStatistics() throws ParseException;

   public Result<Map<String, Object>> getExternalAssetStatistics() throws ParseException;

   public Result<List<CoordinateVO>> getMaintenFlagCountStatistics();

   public List<TrendResultVO> getMaintenFlagMonthStatistics() throws ParseException;
}
