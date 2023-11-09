package com.vrv.vap.monitor.controller.canvas;


import com.vrv.vap.monitor.entity.Monitor2AssetOidAlg;
import com.vrv.vap.monitor.entity.Monitor2AssetType;
import com.vrv.vap.monitor.service.AssetMonitorOidV2Service;
import com.vrv.vap.monitor.service.Monitor2AssetTypeService;
import com.vrv.vap.monitor.vo.DeleteModel;
import com.vrv.vap.monitor.vo.Monitor2AssetOidAlgQuery;
import com.vrv.vap.monitor.vo.Monitor2AssetTypeQuery;
import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VoBuilder;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Monitor2AssetTypeController
{
    @Autowired
    private Monitor2AssetTypeService service;

    @Autowired
    private AssetMonitorOidV2Service monitorOidV2Service;

    private static Log log = LogFactory.getLog(Monitor2AssetTypeController.class);

    @PutMapping("/v2/asset_monitor/assettype")
    @ApiOperation("新增监控资产类型")
    public Result addIMonitor2AssetType(@RequestBody Monitor2AssetType record) {
        Result res = null;
        try {
            //不能为空
            if (StringUtils.isEmpty(record.getUniqueCode())) {
                return VoBuilder.result(new Result("999", "UniqueCode不能为空"));
            }

            if (StringUtils.isEmpty(record.getTitle())) {
                return VoBuilder.result(new Result("999", "标题不能为空"));
            }
            if (StringUtils.isEmpty(record.getParentTreeCode())) {
                return VoBuilder.result(new Result("999", "父节点treeCode不能为空"));
            }

            if (record.getTypeLevel().intValue() != 3) {
                return VoBuilder.result(new Result("1000", "只允许修改三级类型"));
            }

            // 查找该分类的上级类型
            Monitor2AssetTypeQuery parentQuery = new Monitor2AssetTypeQuery();
            parentQuery.setTreeCode(record.getParentTreeCode());
            VData<List<Monitor2AssetType>> parents = service.queryAll(parentQuery);

            //a、上级类型不存在  不允许操作
            if (parents == null || parents.getData() == null || parents.getData().isEmpty()) {
                return VoBuilder.result(new Result("1001", "父节点查询失败：未找到"));
            } else if (parents.getData().stream().count() > 1) {
                return VoBuilder.result(new Result("1001", "父节点查询失败：存在多个"));
            } else {
                //判断父类下是否存在相同uniqueCode
                Monitor2AssetType parent = parents.getData().get(0);

                // 查找改父类的所有下级 ，并且相同uniqueCode
                Monitor2AssetTypeQuery childrenQuery = new Monitor2AssetTypeQuery();
                childrenQuery.setParentTreeCode(parent.getTreeCode());
                childrenQuery.setUniqueCode(record.getUniqueCode());

                VData<List<Monitor2AssetType>> children = service.queryAll(childrenQuery);

                //相同uniqueCode 不允许添加
                if (children != null && children.getData() != null && !children.getData().isEmpty()) {
                    return VoBuilder.result(new Result("1002", "该父类型下存在相同UniqueCode子节点"));
                }
                //构造值
                record.setTreeCode(record.getParentTreeCode()+"-"+record.getUniqueCode());
                record.setTypeLevel(parent.getTypeLevel()+1);
            }



            int code = service.addItem(record);
            res = VoBuilder.vd(record, RetMsgEnum.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            res = VoBuilder.result(RetMsgEnum.FAIL);
        }
        return res;
    }

    @PatchMapping("/v2/asset_monitor/assettype")
    @ApiOperation("修改监控资产类型")
    public Result updateMonitor2AssetType(@RequestBody Monitor2AssetType record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {

            //不能为空
            if(StringUtils.isEmpty(record.getUniqueCode())){
                return VoBuilder.result(new Result("999","UniqueCode不能为空"));
            }
            if (StringUtils.isEmpty(record.getTitle())) {
                return VoBuilder.result(new Result("999", "标题不能为空"));
            }
            if (StringUtils.isEmpty(record.getParentTreeCode())) {
                return VoBuilder.result(new Result("999", "父节点treeCode不能为空"));
            }
            if(record.getTypeLevel().intValue()!=3){
                return VoBuilder.result(new Result("1000","只允许修改三级类型"));
            }

            //检查将要修改的数据还是否存在
            Monitor2AssetType historyItem=   service.querySingle(record);
            if(historyItem==null){
                return VoBuilder.result(RetMsgEnum.EMPTY_RET);
            }

            // 查找改分类的上级类型
            Monitor2AssetTypeQuery parentQuery=new Monitor2AssetTypeQuery();
            parentQuery.setTreeCode(record.getParentTreeCode());
            VData<List<Monitor2AssetType>>  parents= service.queryAll(parentQuery);
            if(parents==null||parents.getData()==null||parents.getData().isEmpty()) {
                return VoBuilder.result(new Result("1001", "父节点查询失败：未找到"));
            }
            else if(parents.getData().stream().count()>1){
                return VoBuilder.result(new Result("1001", "父节点查询失败：存在多个"));
            }else{
                //判断父类下是否存在相同uniqueCode
                Monitor2AssetType parent=parents.getData().get(0);
                //构造值
                record.setTreeCode(record.getParentTreeCode()+"-"+record.getUniqueCode());
                record.setTypeLevel(parent.getTypeLevel()+1);

                //如果修改了UniqueCode
                if(!historyItem.getUniqueCode().equals(record.getUniqueCode())){

                    //查看当前节点是否存在子节点
                    Monitor2AssetTypeQuery childrenQuery2=new Monitor2AssetTypeQuery();
                    childrenQuery2.setParentTreeCode(record.getTreeCode());
                    VData<List<Monitor2AssetType>>  children2= service.queryAll(childrenQuery2);


                    if (children2!= null && children2.getData() != null && !children2.getData().isEmpty()) {
                        return VoBuilder.result(new Result("1002", "该节点下存在子节点,无法修改UniqueCode"));
                    }


                    Monitor2AssetOidAlgQuery t=new Monitor2AssetOidAlgQuery();
                    t.setSnoUnicode(record.getUniqueCode());
                    t.setAssetType(parent.getGuid());

                    VData<List<Monitor2AssetOidAlg>>   monitorOidsVdata=   monitorOidV2Service.queryAll(t);
                    if(monitorOidsVdata==null || monitorOidsVdata.getData()==null  || monitorOidsVdata.getData().isEmpty()){
                        //不存在监控oid配置的 都可以修改 ，uniqueCode不可以重复
                        // 查找改父类的所有下级 ，并且相同uniqueCode
                        Monitor2AssetTypeQuery childrenQuery=new Monitor2AssetTypeQuery();
                        childrenQuery.setParentTreeCode(parent.getTreeCode());
                        childrenQuery.setUniqueCode(record.getUniqueCode());
                        VData<List<Monitor2AssetType>>  children= service.queryAll(childrenQuery);

                        //因为UniqueCode 已经修改了，所以只需要判断集合中是否存在相同UniqueCode，不用判断guid
                        if (children != null && children.getData() != null && !children.getData().isEmpty()) {
                            return VoBuilder.result(new Result("1002", "该父类型下存在相同UniqueCode子节点"));
                        }
                    }else{

                        return VoBuilder.result(new Result("1003", "存在监控oid配置的 只允许修改名称和图标"));
                    }


                }


            }





            int code=  service.updateItem(record);
            res = VoBuilder.vd(record, RetMsgEnum.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }

    @DeleteMapping("/v2/asset_monitor/assettype")
    @ApiOperation("删除监控资产类型")
    public Result deleteMonitor2AssetTypes(@RequestBody DeleteModel record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {


            //存在监控oid配置的 不允许删除

            Monitor2AssetType param = new Monitor2AssetType();

            //check
            List<Monitor2AssetType> delList = new ArrayList<>();


            for (String guid : record.getStringIdList()) {

                param.setGuid(guid);
                Monitor2AssetType item = service.querySingle(param);
                if (item == null) {
                    continue;
                }
                // 查找改分类的上级类型
                Monitor2AssetTypeQuery parentQuery = new Monitor2AssetTypeQuery();
                parentQuery.setTreeCode(item.getParentTreeCode());
                VData<List<Monitor2AssetType>> parents = service.queryAll(parentQuery);
                if (parents == null || parents.getData() == null || parents.getData().isEmpty()) {
                    return VoBuilder.result(new Result("1001", "父节点查询失败：未找到"));
                }


                //查看当前节点是否存在子节点
                Monitor2AssetTypeQuery childrenQuery=new Monitor2AssetTypeQuery();
                childrenQuery.setParentTreeCode(item.getTreeCode());
                VData<List<Monitor2AssetType>>  children= service.queryAll(childrenQuery);


                if (children != null && children.getData() != null && !children.getData().isEmpty()) {
                    return VoBuilder.result(new Result("1002", "该节点下存在子节点"));
                }



                Monitor2AssetType parent = parents.getData().get(0);
                Monitor2AssetOidAlgQuery t = new Monitor2AssetOidAlgQuery();
                t.setSnoUnicode(item.getUniqueCode());
                t.setAssetType(parent.getGuid());

                VData<List<Monitor2AssetOidAlg>> monitorOidsVdata = monitorOidV2Service.queryAll(t);
                if (monitorOidsVdata != null && monitorOidsVdata.getData() != null && !monitorOidsVdata.getData().isEmpty()) {

                    return VoBuilder.result(new Result("1003", "存在监控oid配置的 只允许修改名称和图标"));
                }
                delList.add(item);

            }


            delList.forEach(item -> {
                service.deleteItem(item);
            });

        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }

}
