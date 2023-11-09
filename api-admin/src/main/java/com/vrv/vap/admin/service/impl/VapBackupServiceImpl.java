package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.config.DbBackupConfig;
import com.vrv.vap.admin.common.properties.VapBackupProperties;
import com.vrv.vap.admin.common.util.DateUtil;
import com.vrv.vap.admin.mapper.NacosMapper;
import com.vrv.vap.admin.service.NacosBackupService;
import com.vrv.vap.admin.service.VapBackupService;
import com.vrv.vap.admin.util.SqlCheckUtil;
import com.vrv.vap.admin.vo.BackupTableVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class VapBackupServiceImpl implements VapBackupService {
    @Resource
    private NacosMapper nacosMapper;
    @Autowired
    private VapBackupProperties vapBackupProperties;

    @Override
    public List<Map> getInfo(String tableName) {
        if (DbBackupConfig.getTableNames().contains(tableName)) {
            return nacosMapper.getInfo(tableName);
        }
        return new ArrayList<>();
    }

    @Override
    public void insertInfo(BackupTableVO backupTable, List<Map<String,Object>> rowsData) {
        BackupTableVO backupTableVO = new BackupTableVO();
        BeanUtils.copyProperties(backupTable, backupTableVO);
        List<Map<String,Object>> rows = new ArrayList<>(rowsData);
        if (!DbBackupConfig.getTableNames().contains(backupTableVO.getName())) {
            return;
        }

        if(rows !=null && rows.size()>0){
            //List<String> keys = rows.get(0).keySet().stream().collect(Collectors.toList());
            List<String> fieldsTemp = nacosMapper.getColumns(backupTableVO.getName(), "ajb_vap");
            final List<String> dateFields = new ArrayList<>();
            if(backupTableVO.getDateFields()!=null){
                dateFields.addAll( Arrays.stream(backupTableVO.getDateFields()).filter(p->fieldsTemp.contains(p)).collect(Collectors.toList()));
            }
            List<String> fields = new ArrayList<>();
            fieldsTemp.forEach(f->{
                fields.add( SqlCheckUtil.filterCol(f));
            });

            rows.forEach(content->{
                dateFields.forEach(field->{
                    if(content.get(field)!=null && content.get(field) instanceof Long){
                        content.put(field, DateUtil.format(new Date((long)content.get(field))));
                    }
                });
                nacosMapper.insertInfo(backupTableVO.getName(),fields,content);
            });
        }

    }

    @Override
    public void clearRows(String tableName) {
        if (DbBackupConfig.getTableNames().contains(tableName)) {
            nacosMapper.clearRows(tableName);
        }
    }
}
