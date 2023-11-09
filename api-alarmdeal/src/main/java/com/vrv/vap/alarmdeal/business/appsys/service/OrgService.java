package com.vrv.vap.alarmdeal.business.appsys.service;

import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;

import java.util.List;

public interface OrgService {

    public List<BaseKoalOrg> getOrgsCache();

    public List<BaseKoalOrg> getOrgs();
}
