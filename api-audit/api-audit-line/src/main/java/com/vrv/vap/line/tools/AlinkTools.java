package com.vrv.vap.line.tools;
import com.alibaba.alink.operator.batch.sink.CsvSinkBatchOp;
import com.vrv.amt.utils.alg.FrequentSequenceDM;
import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.model.BaseLineFrequent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.types.Row;

import com.alibaba.alink.operator.batch.BatchOperator;
import com.alibaba.alink.operator.batch.associationrule.PrefixSpanBatchOp;
import com.alibaba.alink.operator.batch.source.MemSourceBatchOp;
import com.alibaba.alink.operator.batch.similarity.TextNearestNeighborPredictBatchOp;
import com.alibaba.alink.operator.batch.similarity.TextNearestNeighborTrainBatchOp;

import java.util.*;
import java.util.stream.Collectors;

public class AlinkTools {
    //private static LogUtil log = new LogUtil();

    private static String separator = ";";//项分隔符

    public static void main(String[] args) throws Exception {
        //testSimilarity();
    }

    public static String apriori(List<String> urls,String mode){
        String result = "";
        if("1".equals(mode)){
            List<Row> rows = apriori4Alink2(urls);
            if(CollectionUtils.isNotEmpty(rows)){
                List<String> frees = rows.stream().map(i -> i.getField(0).toString()).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(frees)){
                    Collections.sort(frees, Comparator.comparingInt(i -> i == null ? 0 : i.toString().length()).reversed());
                    StringBuffer temStr = new StringBuffer();
                    for(String s : frees){
                        if(StringUtils.isNotEmpty(s) && temStr.toString().indexOf(s) == -1){
                            if(StringUtils.isNotEmpty(temStr)){
                                temStr.append(LineConstants.SQ.itemSeparator);
                            }
                            temStr.append(s);
                        }
                    }
                    result = temStr.toString();
                }
            }
        }else{
            Map<String, FrequentSequenceDM.Sequence> sq = FrequentSequenceDM.fromCollection(urls);
            List<String> frees = new ArrayList<>();
            sq.entrySet().forEach((r) ->{
                //System.out.println(TimeTools.format2(new Date())+" "+"当前线程："+Thread.currentThread().getName());
                String item = r.getValue().getItem();
                if(StringUtils.isNotEmpty(item)){
                    item = item.substring(0,item.length()-1);
                    frees.add(item);
                }
            });
            StringBuffer temStr = new StringBuffer("");
            if(CollectionUtils.isNotEmpty(frees)){
                Collections.sort(frees, Comparator.comparingInt(i -> i == null ? 0 : i.toString().length()).reversed());
                for(String s : frees){
                    if(StringUtils.isNotEmpty(s) && temStr.toString().indexOf(s) == -1){
                        if(StringUtils.isNotEmpty(temStr)){
                            temStr.append(LineConstants.SQ.itemSeparator);
                        }
                        temStr.append(s);
                    }
                }
            }
            result = temStr.toString();
        }
        return result;
    }

    public static  List<Row> apriori4Alink2(List<String> urls){
        List<Row> collect = new ArrayList<>();
        try {
            List <Row> datas = new ArrayList<>();
            for(String s : urls){
                datas.add(Row.of(s));
            }
            BatchOperator <?> data = new MemSourceBatchOp(datas, "sequence string");

            BatchOperator <?> prefixSpan = new PrefixSpanBatchOp()
                    .setItemsCol("sequence")
                    .setMinSupportCount(2);
            BatchOperator<?> o = prefixSpan.linkFrom(data);
            collect = o.collect();
        }catch (Exception e){
            e.printStackTrace();
        }
        return collect;
    }

    public static  List<Row> apriori4AlinkBySupport(List<String> urls,int support){
        List<Row> collect = new ArrayList<>();
        try {
            List <Row> datas = new ArrayList<>();
            for(String s : urls){
                datas.add(Row.of(s));
            }
            BatchOperator <?> data = new MemSourceBatchOp(datas, "sequence string");

            BatchOperator <?> prefixSpan = new PrefixSpanBatchOp()
                    .setItemsCol("sequence")
                    .setMinSupportCount(support);
            BatchOperator<?> o = prefixSpan.linkFrom(data);
            collect = o.collect();
        }catch (Exception e){
            e.printStackTrace();
        }
        return collect;
    }

    public static List<Row> apriori4Alink(List<String> urls){

        return new Apriori4Alink().predict(urls);
    }
    public static List<Row> apriori4Alink(List<String> urls,String uid){

        return new Apriori4Alink().predict(urls,uid);
    }

    public static DataSet<Row> apriori4AlinkDateSet(List<String> urls){
        DataSet<Row> collect = null;
        try {
            List <Row> datas = new ArrayList<>();
            for(String s : urls){
                datas.add(Row.of(s));
            }
            BatchOperator <?> data = new MemSourceBatchOp(datas, "sequence string");
            BatchOperator <?> prefixSpan = new PrefixSpanBatchOp()
                    .setItemsCol("sequence")
                    .setMinSupportCount(2);
            BatchOperator<?> o = prefixSpan.linkFrom(data);
            collect = o.getDataSet();
            //collect = o.collect();
        }catch (Exception e){
            e.printStackTrace();
        }
        return collect;
    }

    public static void testSimilarity(List<BaseLineFrequent> frequents,String item) throws Exception {
        List <Row> datas = new ArrayList<>();
        for(BaseLineFrequent f : frequents){
            datas.add(Row.of(f.getId(),f.getFrequents().replaceAll(separator," "),item));
        }
        BatchOperator <?> inOp = new MemSourceBatchOp(datas, "id int, text1 string, text2 string");
        BatchOperator <?> train = new TextNearestNeighborTrainBatchOp().setIdCol("id").setSelectedCol("text1")
                .setMetric("LEVENSHTEIN_SIM").linkFrom(inOp);
        BatchOperator <?> predict =
                new TextNearestNeighborPredictBatchOp().setSelectedCol("text2").setTopN(1).linkFrom(
                        train, inOp);
        List<Row> collect = predict.distinct().collect();

    }


}

class Apriori4Alink{
    protected List<Row> predict(List<String> urls){
        return this.predict(urls, String.valueOf(Math.random()));
    }
    protected List<Row> predict(List<String> urls,String uid){
        List<Row> collect = new ArrayList<>();
        try {
            List <Row> datas = new ArrayList<>();
            for(String s : urls){
                datas.add(Row.of(s));
            }
            System.out.println(uid+" >>>>>>>>>>>时间："+urls.size());
            // 释放内存
            urls.clear();
            urls = null;

            BatchOperator <?> data = new MemSourceBatchOp(datas, "sequence string");
            //data.get
            BatchOperator <?> prefixSpan = new PrefixSpanBatchOp()
                    .setItemsCol("sequence")
                    .setMaxPatternLength(100)
                    .setMinConfidence(0.0001)
                    .setMinSupportCount(2);
            BatchOperator<?> o = prefixSpan.linkFrom(data);
//            o.link(new CsvSinkBatchOp().setFilePath("/prefixspan/"+uid+"-frequen.store.csv").setOverwriteSink(true));
//            BatchOperator.execute();
            System.out.println(">>>>>>>>>>>prefixspan执行collect开始时间："+System.currentTimeMillis());
            collect = o.collect();
            System.out.println(">>>>>>>>>>>prefixspan执行collect结束时间："+System.currentTimeMillis());


        }catch (Exception e){
            e.printStackTrace();
        }
        return collect;
    }

}
