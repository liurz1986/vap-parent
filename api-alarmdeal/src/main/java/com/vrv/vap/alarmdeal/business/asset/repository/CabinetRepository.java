package com.vrv.vap.alarmdeal.business.asset.repository;


import com.vrv.vap.alarmdeal.business.asset.model.Cabinet;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * 机柜dao层
 * @author wd-pc
 *
 */
@Repository
public interface CabinetRepository extends BaseRepository<Cabinet, String> {

}
