package com.vrv.vap.alarmdeal.business.flow.core.service;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.flow.auth.AuthService;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.EmailTemplate;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.EmailTemplateService;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.MailVO;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.jpa.log.LoggerUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailService {

    private static LoggerUtil logger = LoggerUtil.getLogger(EmailService.class);

    @Autowired
    private AdminFeign adminFeign;

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailTemplateService emailTemplateService;

    /**
     * 异步发送邮件
     */
    public  void sendMessage(Set<String> userSet, String emailMailTemplate, Map<String,Object> args){
       new Thread(new Runnable() {
           @Override
           public void run() {
               excSendMessage(userSet,emailMailTemplate,args);
           }
       }).start();
    }

    private void excSendMessage(Set<String> userSet, String emailKey, Map<String,Object> args){
        String mailVOString="";
        try {
            EmailTemplate emailTemplate = emailTemplateService.getOne(emailKey);
            if(null == emailTemplate){
                logger.error("邮件模板不存在，key为"+emailKey);
                return;
            }
            // 邮件内容为空不处理
            if(StringUtils.isBlank(emailTemplate.getContent())){
                logger.info("邮件模板中内容为空!");
                return;
            }
            MailVO mailContent=new MailVO();
            mailContent.setTitle(emailTemplate.getTitle());
            mailContent.setContent(emailTemplate.getContent());
            // 邮件内容替换
            mailVOString = replacePlaceholder(JSON.toJSONString(mailContent), args);
            MailVO mailVO = new Gson().fromJson(mailVOString, MailVO.class);
            logger.info("邮件发送人："+JSON.toJSONString(userSet));
            // 发件人处理:目前只支持发送目的地址单人
            for(String userId : userSet){
                sendEmail(userId,mailVO);
            }
        } catch (Exception e) {
            logger.error("邮件接口调用异常",e);
        }
    }

    /**
     * 单人发送
     * @param userId
     * @param mailVO
     */
    private void sendEmail(String userId, MailVO mailVO) {
        User user = authService.getUserInfoByUserId(userId);
        String email = "";
        if (null != user && StringUtils.isNotBlank(user.getEmail())) {
            email = user.getEmail();
        } else if (null == user) {
            logger.warn("不进行发邮件操作：用户不存" + userId);
            return;
        } else {
            logger.warn("不进行发邮件操作：没有配制邮件地址，" + userId);
            return;
        }
        mailVO.setSendTo(email);
        logger.warn("调用发邮件接口执行邮件发送: " + JSON.toJSONString(mailVO));
        VData<Boolean> result = adminFeign.sendSimpleEmail(mailVO);
        logger.warn("调用发送邮件接口成功: " + JSON.toJSONString(result));
    }



    public List<String> getDescByPlaceHolder(String srcStr){
        List<String> list=getPlaceHolder(srcStr, "(\\$\\{)([\\w]+)(\\})");
        return list;
    }

    /**
     * "\\$\\{\\w+\\.\\w+\\}"
     * 根据占位符进行对应的解析工作
     * @param desc
     * @return
     */
    public List<String> getPlaceHolder(String desc,String patternRule){
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile(patternRule);
        Matcher matcher = pattern.matcher(desc);
        while(matcher.find()) {
            String group = matcher.group(0);
            list.add(group);
        }
        return list;
    }

    /**
     * 获得每个占位符对应的原始字段名称
     * @param list
     * @return
     * {"${log.cpurate}":"cpurate"}
     */
    public Map<String,Object> getPlaceHolderRelateToField(List<String> list){
        Map<String,Object> map = new HashMap<>();
        for (String str : list) {
            int begin = str.indexOf("{")+1;
            int end = str.lastIndexOf("}");
            String content = str.substring(begin, end);
            map.put(str, content);
        }
        return map;
    }

    /**
     * 内容替换
     * @param desc
     * @param argMap
     * @return
     */
    public String replacePlaceholder(String desc, Map<String,Object> argMap) {
        List<String> list = getDescByPlaceHolder(desc); //获得对应的替换标识符
        Map<String, Object> map = getPlaceHolderRelateToField(list);
        for(Map.Entry<String, Object> entry : map.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            String valueStr = String.valueOf(value);
                if(argMap.containsKey(valueStr)){
                    Object object =argMap.get(valueStr);
                String result="";
                if( object instanceof Date){
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                     result=simpleDateFormat.format(object);
                }else if(object!=null){
                    result=object.toString();
                }
                logger.info(result);
                if(StringUtils.isNotEmpty(result)){
                    desc = desc.replace(key, result);
                }else{
                    desc = desc.replace(key, "未知");
                }
            }
        }
        return desc;
    }


}
