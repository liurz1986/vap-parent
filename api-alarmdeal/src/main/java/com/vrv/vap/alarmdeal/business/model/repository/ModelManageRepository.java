package com.vrv.vap.alarmdeal.business.model.repository;

import com.vrv.vap.alarmdeal.business.model.model.ModelManage;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelManageRepository extends BaseRepository<ModelManage, String> {
}
