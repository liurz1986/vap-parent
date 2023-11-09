package com.vrv.vap.alarmdeal.business.analysis.server.command;

import java.util.List;

public  interface DealCommandService<T> {

    void executeResponseCommond(List<T> responseCommonVOList);

}
