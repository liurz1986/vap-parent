package com.vrv.vap.admin.dao;

import com.github.pagehelper.Page;
import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.vo.IpRangeQuery;

public interface BaseKoalOrgDao {
	Page<BaseKoalOrg> getOrgPageByIpRange(IpRangeQuery iprange);
}
