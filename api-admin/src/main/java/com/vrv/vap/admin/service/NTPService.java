package com.vrv.vap.admin.service;

import com.vrv.vap.common.vo.Result;

public interface NTPService {
    public Result synchroTime(String ip);
}
