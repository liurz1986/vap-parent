package com.vrv.vap.data.service.impl;

import com.vrv.vap.data.mapper.SourceFieldMapper;
import com.vrv.vap.data.model.SourceField;
import com.vrv.vap.data.service.SourceFieldService;
import com.vrv.vap.base.BaseServiceImpl;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class SourceFieldServiceImpl extends BaseServiceImpl<SourceField> implements SourceFieldService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private SourceFieldMapper sourceFieldMapper;

    @Autowired
    private Cache<Integer, List<SourceField>> sourceFieldCache;

    @Autowired
    private Cache<Integer, HashMap<String, SourceField>> sourceFieldMapCache;


    @Override
    public List<SourceField> findAllBySourceId(Integer sourceId) {
        if (sourceFieldCache.containsKey(sourceId)) {
            return sourceFieldCache.get(sourceId);
        }
        Example example = new Example(SourceField.class);
        example.createCriteria().andEqualTo("sourceId", sourceId);
        List<SourceField> fields = this.findByProperty(SourceField.class, "sourceId", sourceId);
        HashMap<String, SourceField> fieldNameMap = new HashMap<>();
        fields.forEach(field -> fieldNameMap.put(field.getField(), field));
        sourceFieldCache.put(sourceId, fields);
        System.out.println(sourceId);
        System.out.println(fieldNameMap);
        sourceFieldMapCache.put(sourceId, fieldNameMap);
        return fields;
    }

    @Override
    public Integer save(List<SourceField> fields) {
        int count = super.save(fields);
        if (count > 0) {
            for (SourceField field : fields) {
                sourceFieldCache.remove(field.getSourceId());
                sourceFieldMapCache.remove(field.getSourceId());
            }
        }
        return count;
    }

    @Override
    public Integer save(SourceField record) {
        int count = super.save(record);
        if (count > 0) {
            sourceFieldCache.remove(record.getSourceId());
            sourceFieldMapCache.remove(record.getSourceId());
        }
        return count;
    }

    @Override
    public Integer saveSelective(SourceField record) {
        int count = super.saveSelective(record);
        if (count > 0) {
            sourceFieldCache.remove(record.getSourceId());
            sourceFieldMapCache.remove(record.getSourceId());
        }
        return count;
    }

    @Override
    public Integer update(SourceField record) {
        int count = super.update(record);
        if (count > 0) {
            sourceFieldCache.remove(record.getSourceId());
            sourceFieldMapCache.remove(record.getSourceId());
        }
        return count;
    }

    @Override
    public Integer updateSelective(SourceField record) {
        int count = super.updateSelective(record);
        if (count > 0) {
            sourceFieldCache.remove(record.getSourceId());
            sourceFieldMapCache.remove(record.getSourceId());
        }
        return count;
    }

    @Override
    public Integer deleteById(Integer id) {
        SourceField filed = this.findById(id);
        if (filed == null) {
            return 0;
        }
        sourceFieldCache.remove(filed.getSourceId());
        sourceFieldMapCache.remove(filed.getSourceId());
        return super.deleteById(filed.getId());

    }

    @Override
    public Integer deleteByIds(String ids) {
        List<SourceField> sourceFields = this.findByids(ids);
        if (sourceFields.size() == 0) {
            return 0;
        }
        for (SourceField field : sourceFields) {
            sourceFieldCache.remove(field.getSourceId());
            sourceFieldMapCache.remove(field.getSourceId());
        }
        return super.deleteByIds(ids);
    }

}
