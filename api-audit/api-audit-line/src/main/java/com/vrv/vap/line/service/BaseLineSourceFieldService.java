package com.vrv.vap.line.service;


import com.vrv.vap.line.model.BaseLineSourceField;

import java.util.Collection;
import java.util.List;

public interface BaseLineSourceFieldService {
    List<BaseLineSourceField> findBySource(String sourceId);

    int batchSave(List<BaseLineSourceField> list);

    BaseLineSourceField add(BaseLineSourceField field);

    BaseLineSourceField update(BaseLineSourceField field);

    int delete(String ids);

    List<BaseLineSourceField> findBySourceIds(Collection sourceIds);
}
