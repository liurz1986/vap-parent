package com.vrv.vap.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.common.util.Tools;
import com.vrv.vap.admin.model.CollectorRule;
import com.vrv.vap.admin.model.CollectorRuleCollection;
import com.vrv.vap.admin.service.CollectorRuleCollectionService;
import com.vrv.vap.admin.service.CollectorRuleService;
import com.vrv.vap.admin.util.CloseStreamUtil;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.admin.vo.CollectorRuleCollectionVO;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author lilang
 * @date 2022/1/4
 * @description
 */
@Service
@Transactional
public class CollectorRuleCollectionServiceImpl extends BaseServiceImpl<CollectorRuleCollection> implements CollectorRuleCollectionService {

    private static final Logger logger = LoggerFactory.getLogger(CollectorRuleCollectionServiceImpl.class);

    @Autowired
    private CollectorRuleService collectorRuleService;

    // 自定义
    private static final int TYPE_ADD = 0;

    @Override
    public List<CollectorRuleCollectionVO> transformRuleCollection(List<CollectorRuleCollection> ruleCollectionList) {
        List<CollectorRuleCollectionVO> ruleCollectionVOList = new ArrayList<>();
        ruleCollectionList.stream().forEach(item -> {
            CollectorRuleCollectionVO collectionVO = new CollectorRuleCollectionVO();
            BeanUtils.copyProperties(item,collectionVO);
            Integer collectionId = item.getId();
            List<CollectorRule> ruleList = collectorRuleService.findByProperty(CollectorRule.class,"collectionId",collectionId);
            collectionVO.setRuleCount(CollectionUtils.isNotEmpty(ruleList) ? ruleList.size() : 0);
            ruleCollectionVOList.add(collectionVO);
        });
        return ruleCollectionVOList;
    }

    public void export(HttpServletResponse response, String content,Integer collectionId){
        response.setCharacterEncoding("utf-8");
        String downloadName = "collectorRuleCollection" + collectionId + ".zip";
        //将文件进行打包下载
        try {
            OutputStream out = response.getOutputStream();
            byte[] data = this.createZip(content);
            response.reset();
            response.setHeader("Content-Disposition","attachment;fileName="+downloadName);
            response.setContentLength(data != null ? data.length : 0);
            response.setContentType("application/octet-stream;charset=UTF-8");
            IOUtils.write(data, out);
            IOUtils.closeQuietly(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            logger.error("",e);
        }
    }

    @Override
    public Integer importRuleCollection(MultipartFile file) {
        int result = 0;
        InputStream in = null;
        ZipInputStream zin = null;
        try {
            in = file.getInputStream();
            Charset gbk = Charset.forName("gbk");
            zin = new ZipInputStream(in, gbk);
            while (zin.getNextEntry() != null) {
                byte[] buf = new byte[1024];
                int num = -1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((num = zin.read(buf, 0, buf.length)) != -1) {
                    baos.write(buf, 0, num);
                }
                byte[] bytes = baos.toByteArray();
                String importData = new String(bytes);
                logger.info("导入信息：" + LogForgingUtil.validLog(importData));
                CollectorRuleCollectionVO collectionVO = JSON.parseObject(importData,CollectorRuleCollectionVO.class);
                CollectorRuleCollection collection = new CollectorRuleCollection();
                BeanUtils.copyProperties(collectionVO,collection);
                collection.setId(null);
                collection.setType(TYPE_ADD);
                collection.setName(collection.getName() +"-"+ TimeTools.formatTimeStamp(new Date()));
                result = this.save(collection);
                List<CollectorRule> ruleList = collectionVO.getRuleList();
                if (CollectionUtils.isNotEmpty(ruleList)) {
                    String ruleListJson = JSON.toJSONString(ruleList);
                    String md5Value = Tools.string2MD5(ruleListJson);
                    collection.setVersion(md5Value);
                    this.updateSelective(collection);
                    ruleList.stream().forEach(item -> {
                        item.setId(null);
                        item.setCollectionId(collection.getId());
                        collectorRuleService.save(item);
                    });
                }
            }
        } catch (IOException e) {
            logger.error("",e);
        } finally {
            CloseStreamUtil.closeStream(zin);
            CloseStreamUtil.closeStream(in);
        }
        return result;
    }

    public byte[] createZip(String exportJsonStr) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        byte[] arrayOfByte = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
            zipOutputStream.putNextEntry(new ZipEntry("collectorRuleCollection.txt"));
            //这里采用gbk方式压缩，如果采用编译器默认的utf-8，这里就直接getByte();
            zipOutputStream.write(exportJsonStr.getBytes("UTF-8"));
            zipOutputStream.flush();
            zipOutputStream.closeEntry();
            zipOutputStream.finish();
            zipOutputStream.close();
            arrayOfByte = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            logger.error("",e);
        } finally {
            if (zipOutputStream != null) {
                try {
                    zipOutputStream.close();
                } catch (IOException e) {
                    logger.error("",e);
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    logger.error("",e);
                }
            }
        }
        return arrayOfByte;
    }

    @Override
    public void updateVersion(Integer collectionId) {
        // 修改规则集版本号
        List<CollectorRule> ruleList = collectorRuleService.findByProperty(CollectorRule.class,"collectionId",collectionId);
        String ruleListJson = JSON.toJSONString(ruleList);
        String md5Value = Tools.string2MD5(ruleListJson);
        CollectorRuleCollection ruleCollection = this.findById(collectionId);
        if (ruleCollection != null) {
            ruleCollection.setVersion(md5Value);
            this.updateSelective(ruleCollection);
        }
    }
}
