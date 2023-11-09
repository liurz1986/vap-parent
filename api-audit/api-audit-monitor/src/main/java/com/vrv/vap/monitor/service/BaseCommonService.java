package com.vrv.vap.monitor.service;

import com.vrv.vap.toolkit.vo.Query;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;

import java.util.List;

public interface BaseCommonService<T> {

    int addItem(T t);

    int updateItem(T t);

    int deleteItem(T t);

    T querySingle(T t);

    VList<T> queryByPage(Query t);

    VData<List<T>> queryAll(Query t);

}
