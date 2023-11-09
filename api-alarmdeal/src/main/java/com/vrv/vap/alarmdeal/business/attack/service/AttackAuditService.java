package com.vrv.vap.alarmdeal.business.attack.service;

import com.vrv.vap.jpa.web.NameValue;

import java.util.List;
import java.util.Map;

public interface AttackAuditService {
    List<NameValue> getAttackStageCount(Map<String, String> threatReq);
}
