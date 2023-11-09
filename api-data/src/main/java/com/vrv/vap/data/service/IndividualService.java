package com.vrv.vap.data.service;


import com.vrv.vap.data.vo.CommonRequest;

import java.util.Map;

public interface IndividualService {
    Long query24Total(CommonRequest query);

    Map query24Trend(CommonRequest query);

}
