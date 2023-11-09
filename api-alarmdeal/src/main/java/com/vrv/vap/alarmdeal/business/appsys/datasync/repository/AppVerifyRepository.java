package com.vrv.vap.alarmdeal.business.appsys.datasync.repository;

import com.vrv.vap.alarmdeal.business.appsys.datasync.model.AppSysManagerVerify;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppVerifyRepository extends BaseRepository<AppSysManagerVerify, Integer> {
}
