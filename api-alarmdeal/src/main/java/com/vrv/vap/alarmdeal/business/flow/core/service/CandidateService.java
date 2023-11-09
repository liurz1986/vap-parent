package com.vrv.vap.alarmdeal.business.flow.core.service;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.flow.auth.AuthService;
import com.vrv.vap.alarmdeal.business.flow.core.constant.FlowConstant;
import com.vrv.vap.alarmdeal.business.flow.core.listener.CandidateType;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTaskLog;
import com.vrv.vap.alarmdeal.business.flow.core.model.FixModel;
import com.vrv.vap.alarmdeal.business.flow.processdef.exception.FlowException;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.jpa.log.LoggerUtil;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import org.activiti.engine.delegate.VariableScope;
import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CandidateService {


    private static LoggerUtil logger = LoggerUtil.getLogger(CandidateService.class);



    @Autowired
    private BusinessTaskLogService businessTaskLogService;


    @Autowired
    private AuthService authService;
    @Autowired
    private FlowService flowService;

    public List<String> getUsers(VariableScope delegateTask, FixModel fixModel) {
        List<String> users = new ArrayList<>();
        FixedValue candidateType=fixModel.getCandidateType();
        if(candidateType!=null){
            logger.info(JSON.toJSONString(candidateType));
            FixedValue candidate=fixModel.getCandidate();
            FixedValue secParam=fixModel.getSecParam();
            FixedValue roleParam=fixModel.getRoleParam();
            FixedValue node=fixModel.getNode();
            Object busiArgs=fixModel.getBusiArgs();
            FixedValue userValue = fixModel.getUserValue();
            FixedValue roleValue = fixModel.getRoleValue();
            switch (candidateType.getValue(delegateTask).toString()) {
                case CandidateType.USER:
                    if (null == candidate) {
                        throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有配置处理人(candidate)，请联系管理员操作！");
                    }
                    String userStr = candidate.getValue(delegateTask).toString(); //TODO 特别注意，用户最好不要重复
                    logger.info("userStr: "+userStr);
                    String[] split = userStr.split(",");
                    users = Arrays.asList(split);
                    break;
                case CandidateType.R_ARG:
                    users = this.getRargCandidate(delegateTask,candidate,busiArgs,users);
                    break;
                case CandidateType.ROLE:
                    if (null == candidate) {
                        throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有配置处理人角色(candidate)，请联系管理员操作！");
                    }
                    String roles = candidate.getValue(delegateTask).toString();
                    String[] roleArray  = roles.split(",");
                    for (String role : roleArray) {
                        List<String> roleList = authService.getUsersByRole(role);
                        users.addAll(roleList);
                    }
                    getSecUsers(delegateTask, users,secParam);
                    break;
                case CandidateType.R_ROLE_ARG:
                    users = this.getRargCandidateByRole(delegateTask,candidate,fixModel.getTaskId(),users);
                    getSecUsers( delegateTask,users,secParam);
                    break;
                case CandidateType.SEC:
                    if (null == candidate) {
                        throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有配置安全域(candidate)，请联系管理员操作！");
                    }
                    String secs = candidate.getValue(delegateTask).toString();
                    String[] secArray = secs.split(",");
                    for (String secStr : secArray) {
                        Map<String,Object> map = new HashMap<>();
                        map.put("code", secStr);
                        List<String> secList = authService.byCode(map);
                        users.addAll(secList);
                    }
                    getRoleUsers(delegateTask, users,roleParam);
                    break;
                case CandidateType.R_SEC_ARG:
                    users=this.getRargCandidateBySec(delegateTask,candidate,busiArgs,users);
                    getRoleUsers(delegateTask, users,roleParam);
                    break;
                case CandidateType.R_ORG_LEADER: //组织结构领导
                    users = getLeaderByOrg(fixModel.getExecutionId());
                    break;
                case CandidateType.R_ORG_UP_ROLE: //指定用户上级机构成员
                    users = getUpOrgRole(fixModel.getExecutionId());
                    break;
                case CandidateType.F_LASTACTIONUSER:
                    String userId = flowService.getVariableByExecutionId(fixModel.getExecutionId(), FlowConstant.USERID).toString();
                    users = new ArrayList<>();
                    users.add(userId);
                    break;
                case CandidateType.F_CREATE_USER:
                    BusinessIntance instance = flowService.getFlowInstance(fixModel.getExecutionId());
                    String createUser=instance.getCreateUserId();
                    users = new ArrayList<>();
                    users.add(createUser);
                    break;
                case CandidateType.ASSGIN_TASK_PERSION:
                    if (null == node) {
                        throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有配置指定某一节点的处理人(node)，请联系管理员操作！");
                    }
                    String  nodeName=node.getValue(delegateTask).toString();
                    Set<String> setUsers=intiDealPeople(fixModel.getIntanceId(),nodeName);
                    users.addAll(setUsers);
                    break;
                case CandidateType.BUSINESTYPE:
                    if (null == candidate) {
                        throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有配置业务触发(candidate)，请联系管理员操作！");
                    }
                    Object object=candidate.getValue(delegateTask);
                    String deals=String.valueOf(object);
                    String[] ids = deals.split(",");
                    users = new ArrayList<>(Arrays.asList(ids));
                    users.add(CandidateType.BUSINESTYPE);
                    break;
                case CandidateType.USERORROLE: //同时配置用户和角色 2021-09-10
                    // 获取配置的用户:配置了就读取
                    if (null != userValue) {
                        Object userValueObj = userValue.getValue(delegateTask);
                        if (null != userValueObj) {
                            String userValueStr = String.valueOf(userValueObj);
                            String[] userIds = userValueStr.split(",");
                            users.addAll(Arrays.asList(userIds));
                        }
                    }
                    // 获取配置的角色，转化为用户：:配置了就读取
                    if (null != roleValue) {
                        Object roleValueObj = roleValue.getValue(delegateTask);
                        if (null != roleValue) {
                            String roleValueStr = String.valueOf(roleValueObj);
                            String[] roleCodes = roleValueStr.split(",");
                            for (String role : roleCodes) {
                                // 通过角色获得对应的用户
                                List<String> usersByRole = authService.getUsersByRole(role);
                                users.addAll(usersByRole);
                            }
                        }
                    }
                    if(null == userValue && null== roleValue){
                        throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "用户和角色都没配置(userValue、roleValue)，请联系管理员操作！");
                    }
                default:
                    break;
            }
        }
        return users;
    }


    /**
     * 某一个节点的上次处理人
     */
    public  Set<String> intiDealPeople(String instanceId,String lastNode){
        List<BusinessTaskLog> businessTaskLogList=businessTaskLogService.queryByInstanceIdAndNode(instanceId,lastNode);
        logger.info("businessTaskLogList: "+JSON.toJSONString(businessTaskLogList));
        Set<String> users = new HashSet<>();
        int size=businessTaskLogList.size();
        if(size>0){
            users.add(businessTaskLogList.get(size-1).getPeopleId());
        }
        return  users;
    }


    /**
     * 获得安全域参数绑定的人员
     * @param delegateTask
     * @param users
     */
    private void getSecUsers(VariableScope delegateTask, List<String> users,FixedValue secParam) {
        if(secParam!=null){
            String sec = secParam.getValue(delegateTask).toString();
            String[] secArray  = sec.split(",");
            for (String secStr : secArray) {
                Map<String,Object> map = new HashMap<>();
                map.put("code", secStr);
                List<String> list = authService.byCode(map);
                users.addAll(list);
            }
            Set<String> result = new HashSet<String>(users);
            users = new ArrayList<>(result);
        }
    }

    /**
     * 获得角色参数绑定的人员
     * @param delegateTask
     * @param users
     */
    private void getRoleUsers(VariableScope delegateTask, List<String> users,FixedValue roleParam) {
        if(roleParam!=null){
            String role = roleParam.getValue(delegateTask).toString();
            String[] roleArray  = role.split(",");
            for (String roleStr : roleArray) {
                List<String> list = authService.getUsersByRole(roleStr);
                users.addAll(list);
            }
            Set<String> result = new HashSet<String>(users);
            users = new ArrayList<>(result);
        }
    }



    private List<String> getUpOrgRole(String executionId) {
        List<String> users = new ArrayList<>();
        String userId = flowService.getVariableByExecutionId(executionId, FlowConstant.USERID).toString();
        try {
            BaseKoalOrg organization = authService.getOrgByUser(userId);
            if (organization != null) {
                users = authService.getUpOrgMemebers(organization.getUuId().toString(),organization.getCode());
                //TODO 硬代码
                if(users.size()==0){
                    users.add("31");
                }
                return users;
            }else{
                throw new RuntimeException("组织结构为空，请检查");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 根据用户所在机构获得对应的领导
     *
     * @param delegateTask
     * @return
     */
    private List<String> getLeaderByOrg(String executionId) {
        List<String> users = new ArrayList<>();
        String userId = flowService.getVariableByExecutionId(executionId, FlowConstant.USERID).toString();
        try {
            BaseKoalOrg organization = authService.getOrgByUser(userId);
            if (organization != null){
                users = authService.getOrgLeader(organization.getCode().toString());
                return users;
            }else{
                throw new RuntimeException("组织结构为空，请检查");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获得特定字段获得对应的Candidate
     *
     * @param delegateTask
     * @param users
     * @return
     */
    public List<String> getRargCandidate(VariableScope delegateTask,FixedValue candidate,Object busiArgs,List<String> users) {
        logger.info("busiArgs: "+JSON.toJSONString(busiArgs));
        String field = candidate.getValue(delegateTask).toString();
        logger.info("field: "+field);
        if (busiArgs instanceof HashMap<?, ?>) {
            HashMap<String, Object> hash = (HashMap<String, Object>) busiArgs;
            Object object=hash.get(field);
            if(object!=null){
                String createUserIds = String.valueOf(hash.get(field));
                // 空处理人处理:2021-10-18
                if(StringUtils.isEmpty(createUserIds)){
                    return users;
                }
                String[] split2 = createUserIds.split(",");
                users = Arrays.asList(split2);
            }
        } else {
            Field field2;
            try {
                field2 = busiArgs.getClass().getDeclaredField(field);
                ReflectionUtils.makeAccessible(field2);
                String createUserIds = field2.get(busiArgs).toString();
                // 空处理人处理:2021-10-18
                if(StringUtils.isEmpty(createUserIds)){
                    return users;
                }
                String[] split2 = createUserIds.split(",");
                users = Arrays.asList(split2);
            } catch (NoSuchFieldException e) {
                logger.error("没有该属性值", e);
            } catch (SecurityException e) {
                logger.error("反射安全性异常", e);
            } catch (IllegalArgumentException e) {
                logger.error("IllegalArgumentException", e);
            } catch (IllegalAccessException e) {
                logger.error("非法反射异常", e);
            }
        }
        return users;
    }




    /**
     * 根据安全域获得对应的
     * @param delegateTask
     * @param roles
     * @return
     */
    private List<String> getRargCandidateBySec(VariableScope delegateTask,FixedValue candidate,Object busiArgs,List<String> secs) {
        String field = candidate.getValue(delegateTask).toString();
        logger.info("field值(r_sec_arg)："+field);
        if (busiArgs instanceof HashMap<?, ?>) {
            HashMap<String, Object> hash = (HashMap<String, Object>) busiArgs;
            String secIds = String.valueOf(hash.get(field));
            String[] split2 = secIds.split(",");
            secs = Arrays.asList(split2);
        } else {
            Field field2 = null;
            try {
                field2 = busiArgs.getClass().getDeclaredField(field);
                ReflectionUtils.makeAccessible(field2);
                String createUserIds = field2.get(busiArgs).toString();
                String[] split2 = createUserIds.split(",");
                secs = Arrays.asList(split2);
            } catch (NoSuchFieldException e) {
                logger.error("没有该属性值", e);
            } catch (SecurityException e) {
                logger.error("反射安全性异常", e);
            } catch (IllegalArgumentException e) {
                logger.error("IllegalArgumentException", e);
            } catch (IllegalAccessException e) {
                logger.error("非法反射异常", e);
            }
        }
        //TODO 通过安全域获得对应的用户
        List<String> users = new ArrayList<>();
        for (String sec : secs) {
            Map<String,Object> map = new HashMap<>();
            map.put("code", sec);
            List<String> secList = authService.byCode(map);
            users.addAll(secList);
        }
        logger.info("根据安全域获得用户的数据个人"+users.size());
        return users;
    }


    private List<String> getRargCandidateByRole(VariableScope delegateTask,FixedValue candidate,Object busiArgs, List<String> roles) {
        String field = candidate.getValue(delegateTask).toString();
        logger.info("field值："+field);
        if (busiArgs instanceof HashMap<?, ?>) {
            HashMap<String, Object> hash = (HashMap<String, Object>) busiArgs;
            String createUserIds = String.valueOf(hash.get(field));
            // 空角色处理:2021-10-18
            if(StringUtils.isEmpty(createUserIds)){
                return roles;
            }
            String[] split2 = createUserIds.split(",");
            roles = Arrays.asList(split2);
        } else {
            Field field2;
            try {
                field2 = busiArgs.getClass().getDeclaredField(field);
                ReflectionUtils.makeAccessible(field2);
                String createUserIds = field2.get(busiArgs).toString();
                // 空角色处理:2021-10-18
                if(StringUtils.isEmpty(createUserIds)){
                    return roles;
                }
                String[] split2 = createUserIds.split(",");
                roles = Arrays.asList(split2);
            } catch (NoSuchFieldException e) {
                logger.error("没有该属性值", e);
            } catch (SecurityException e) {
                logger.error("反射安全性异常", e);
            } catch (IllegalArgumentException e) {
                logger.error("IllegalArgumentException", e);
            } catch (IllegalAccessException e) {
                logger.error("非法反射异常", e);
            }
        }
        logger.info("roles值："+JSON.toJSONString(roles));
        //TODO 通过角色获得对应的用户
        List<String> users = new ArrayList<>();
        for (String role : roles) {
            List<String> usersByRole = authService.getUsersByRole(role);
            users.addAll(usersByRole);
        }
        logger.info("根据角色获得用户的数据个人"+users.size());
        return users;
    }

}
