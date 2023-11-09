package com.vrv.vap.admin.common.util;



import org.yaml.snakeyaml.DumperOptions;
        import org.yaml.snakeyaml.Yaml;

        import java.io.File;
        import java.io.FileReader;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Relic
 * @desc 操作yml的工具类
 * @date 2019-01-29 20:03
 */
public class YmlUtils {

    private final static DumperOptions OPTIONS = new DumperOptions();

    static {
        //将默认读取的方式设置为块状读取
        OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    }


    public static void addMapIntoYml(File dest, Map dataMap) throws IOException {
        Yaml yaml = new Yaml(OPTIONS);
        //如果yml内容为空,则会引发空指针异常,此处进行判断
        if (null == dataMap) {
            dataMap = new LinkedHashMap<>();
        }
        //将数据重新写回文件
        try (FileWriter writer = new FileWriter(dest)) {
            yaml.dump(dataMap, writer);
        } catch (Exception e) {
        }
    }

    /**
     * 在目标文件中添加新的配置信息
     *
     * @param dest  需要添加信息的目标yml文件
     * @param key   添加的key值
     * @param value 添加的对象(如key下方还有链接则添加LinkedHashMap)
     * @author Relic
     * @title addIntoYml
     * @date 2019/1/29 20:52
     */
    public static void addIntoYml(File dest, String key, Object value) throws IOException {
        Yaml yaml = new Yaml(OPTIONS);
        //载入当前yml文件
        LinkedHashMap<String, Object> dataMap = null;
        try (FileReader reader = new FileReader(dest)) {
            dataMap = (LinkedHashMap)yaml.load(reader);
        } catch (Exception e) {

        }

        //如果yml内容为空,则会引发空指针异常,此处进行判断
        if (null == dataMap) {
            dataMap = new LinkedHashMap<>();
        }
        dataMap.put(key, value);
        //将数据重新写回文件
        try (FileWriter writer = new FileWriter(dest)) {
            yaml.dump(dataMap, writer);
        } catch (Exception e) {
        }
    }

    /**
     * 从目标yml文件中读取出所指定key的值
     *
     * @param source 获取yml信息的文件
     * @param key    需要获取信息的key值
     * @return java.lang.Object
     * @author Relic
     * @title getFromYml
     * @date 2019/1/29 20:56
     */
    public static Object getFromYml(File source, String key) throws IOException {
        Yaml yaml = new Yaml(OPTIONS);
        //载入文件
        try (FileReader reader = new FileReader(source)) {
            LinkedHashMap<String, Object> dataMap = (LinkedHashMap)yaml.load(reader);
            //获取当前key下的值(如果存在多个节点,则value可能为map,自行判断)
            return dataMap.get(key);
        } catch (Exception e) {

        }
        return  null;
    }

}
