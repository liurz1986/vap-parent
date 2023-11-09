package com.vrv.vap.alarmdeal.business.asset.util;

import com.alibaba.fastjson.JSON;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * csv处理：读取，导出
 * 2021-09-06
 */
public class CsvUtil {
    private static Logger logger = LoggerFactory.getLogger(CsvUtil.class);

    /**
     * 生成 csv 文件
     * csv文件格式统一的情况
     */
    public static void writeCsvFile(String writeCsvFilePath, List<String[]> datas) {
        File csv = new File(writeCsvFilePath);
        if (!csv.exists()) {
            try {
                csv.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            CSVWriter writer = null;
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(csv);
                // 创建 CSV Writer 对象, 参数说明（写入的文件路径，分隔符，编码格式)
                writer = new CSVWriter(new OutputStreamWriter(out, "UTF-8"), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER);
                writer.writeAll(datas);
                writer.flush();
            } catch (Exception e) {
                logger.error(" 生成 csv 文件异常 ,{}", e);
                throw new RuntimeException("生成 csv 文件异常");
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 读取CVS数据
     * csv文件格式统一的情况
     *
     * @param cvsPath cvsPath
     * @return List<String [ ]>
     */
    public static List<String[]> readCsv(String cvsPath) {
        List<String[]> datas = new ArrayList<String[]>();
        DataInputStream in = null;
        InputStreamReader inputStreamReader = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(cvsPath));
            in = new DataInputStream(fileInputStream);
            inputStreamReader = new InputStreamReader(in, "UTF-8");
            // CSVParser.DEFAULT_SEPARATOR表示每行以逗号分割，CSVParser.DEFAULT_QUOTE_CHARACTER,分割后前后加上冒号，
            // CSVParser.DEFAULT_ESCAPE_CHARACTER                 最后的从第几行开始读取，0表是第一行，1表示第二行
            CSVReader csvReader = new CSVReader(inputStreamReader, CSVParser.DEFAULT_SEPARATOR,
                    CSVParser.DEFAULT_QUOTE_CHARACTER, CSVParser.DEFAULT_ESCAPE_CHARACTER, 2);
            String[] strs;
            while ((strs = csvReader.readNext()) != null) {
                datas.add(strs);
            }
            csvReader.close();
        } catch (Exception e) {
            logger.error(" 读取csv文件异常,{}", e);
            throw new RuntimeException("读取csv文件异常", e);
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStreamReader != null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return datas;
    }

    /**
     * 读取CVS数据第一、二行数据
     *
     * @param cvsPath cvsPath
     * @return List<String [ ]>
     */
    public static List<String> readCsvTielteRowAsset(String cvsPath) {
        List<String> datas = new ArrayList<String>();
        String inString = "";
        BufferedReader reader = null;
        FileInputStream in = null;
        InputStreamReader reader1 = null;
        try {
            in = new FileInputStream(cvsPath);
            reader1 = new InputStreamReader(in, "UTF-8");
            reader = new BufferedReader(reader1);
            int index = 0;
            while ((inString = reader.readLine()) != null) {
                datas.add(inString);
                if (index > 0) {
                    return datas;
                }
                index++;

            }
        } catch (Exception e) {
            logger.error("读取csv文件异常,cvsPath:{},e:{}", cvsPath, e);
            throw new RuntimeException("读取csv文件异常", e);
        } finally {
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(reader1 != null){
                try {
                    reader1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 生成 csv 文件
     * csv文件格式不统一的情况
     */
    public static void writeCsvFileAsset(String writeCsvFilePath, List<String[]> datas, List<String> firstRowDatas) {
        if (null == datas || datas.size() <= 0) {
            return;
        }
        logger.info("csv数据总条数 size:" + datas.size());
        BufferedWriter writer = null;
        FileOutputStream out = null;
        OutputStreamWriter streamWriter = null;
        try {
            File csv = new File(writeCsvFilePath);
            if (!csv.exists()) {
                csv.createNewFile();
            }
            out = new FileOutputStream(csv);
            streamWriter = new OutputStreamWriter(out, "UTF-8");
            writer = new BufferedWriter(streamWriter);
            for (String data : firstRowDatas) {
                writer.write(data);
                writer.newLine();
            }
            String lineStr = "";
            if (null != datas && datas.size() > 0) {
                for (String[] data : datas) {
                    for (int i = 0; i < data.length; i++) {
                        lineStr = lineStr + "," + data[i];
                    }
                    lineStr = lineStr.substring(lineStr.indexOf(",") + 1, lineStr.length());
                    writer.write(lineStr);
                    writer.newLine();
                    lineStr = "";
                }
            }
            writer.flush();
        } catch (Exception e) {
            logger.error(" 生成 csv 文件异常{}", e);
            throw new RuntimeException("生成 csv 文件异常");
        } finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(streamWriter != null){
                try {
                    streamWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

