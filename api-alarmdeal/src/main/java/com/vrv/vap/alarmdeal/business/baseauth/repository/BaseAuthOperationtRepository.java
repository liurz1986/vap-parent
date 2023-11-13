package com.vrv.vap.alarmdeal.business.baseauth.repository;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthOperation;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * 2023-08
 * @author liurz
 */
@Repository
public interface BaseAuthOperationtRepository extends BaseRepository<BaseAuthOperation, Integer> {
}
