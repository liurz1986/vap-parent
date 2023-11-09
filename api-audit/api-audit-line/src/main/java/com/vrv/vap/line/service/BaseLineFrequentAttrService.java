package com.vrv.vap.line.service;

import com.vrv.vap.line.model.BaseLineFrequentAttr;

import java.util.List;

public interface BaseLineFrequentAttrService {
    List<BaseLineFrequentAttr> findByFrequents(List<String> frequents,String userId);
}
