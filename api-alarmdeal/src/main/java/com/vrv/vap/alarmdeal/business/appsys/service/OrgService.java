package com.vrv.vap.alarmdeal.business.appsys.service;

import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;

import java.util.List;

public interface OrgService {

    public List<BaseKoalOrg> getOrgsCache();

    public List<BaseKoalOrg> getOrgs();
    public List<BasePersonZjg> getPerson();
}
