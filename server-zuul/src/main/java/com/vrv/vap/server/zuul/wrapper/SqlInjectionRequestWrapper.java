package com.vrv.vap.server.zuul.wrapper;

/**
 * @author huipei.x
 * @data 创建时间 2019/9/10
 * @description 类说明 :
 */
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SqlInjectionRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String[]> params = new HashMap<>();
    private static Set<String> NOT_ALLOEWD_KEYWORDS = new HashSet<String>(23);
    private static   ObjectMapper mapper = new ObjectMapper();
    private boolean excludes;
    public SqlInjectionRequestWrapper(HttpServletRequest request,boolean excludes) {
        super(request);
        Map<String, String[]> requestMap = request.getParameterMap();
        this.params.putAll(requestMap);
        this.modifyParameterValues();
        this.excludes=excludes;
        this.streamBody = streamBody(request);
    }


    @Override
    public ServletInputStream getInputStream() throws IOException {
        String json = new String(streamBody, "UTF-8");
        if (StringUtils.isEmpty(json)) {
            return super.getInputStream();
        }
        ByteArrayInputStream bis;
        if (this.excludes) {
            bis = new ByteArrayInputStream(streamBody);
        } else {
            JsonNode jsonNode = mapper.readTree(JsonSanitizer.sanitize(json));
            if (jsonNode.isArray()) {
                List<Map<String, Object>> list = jsonStringToList(json);
                bis = new ByteArrayInputStream(mapper.writeValueAsString(list).getBytes("utf-8"));
            } else {
                Map<String, Object> map = jsonStringToMap(json);
                bis = new ByteArrayInputStream(mapper.writeValueAsString(map).getBytes("utf-8"));
            }
        }

        return new MyServletInputStream(bis);
    }

    public void modifyParameterValues(){
        Set<String> set = params.keySet();
        Iterator<String> it = set.iterator();
        while(it.hasNext()){
            String[] values={};
            String key= (String) it.next();
            if(StringUtils.isNotBlank(key)){
                values = params.get(key);
            }
            params.put(key, values);
        }
    }
    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        if(values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public Object getAttribute(String name) {
        if (HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE.equals(name)) {
            Map <String, Object> uriTemplateVars = (Map <String, Object>) super.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (uriTemplateVars.isEmpty()) {
                return uriTemplateVars;
            }
            Map filterValueMap = new LinkedHashMap(uriTemplateVars.size());
            for (Map.Entry <String, Object> entity : uriTemplateVars.entrySet()) {
                if (entity.getValue() instanceof String) {
                    String value=entity.getValue().toString();
                    if (value!=null) {
                        checkSqlKeyWords(value);
                        filterValueMap.put(entity.getKey(), value);
                    }

                }
                filterValueMap.put(entity.getKey(), entity.getValue());
            }

            return filterValueMap;

        }else {
            return super.getAttribute(name);
        }
    }
    @Override
    public String[] getParameterValues(String name) {
        return params.get(name);
    }

    class MyServletInputStream extends ServletInputStream {

        private ByteArrayInputStream bis;

        public MyServletInputStream(ByteArrayInputStream bis) {
            this.bis = bis;
        }

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
        }

        @Override
        public int read() throws IOException {
            return bis.read();
        }
    }

    public static List<Map<String, Object>> jsonStringToList(String jsonString) {

        List<Map<String, Object>> list = new ArrayList<>();
        try {

            list = mapper.readValue(JsonSanitizer.sanitize(jsonString),
                    list.getClass());
            for(Map<String,Object> map:list) {
                cleanMap(map);
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    public static Map<String, Object> jsonStringToMap(String jsonString) {

        Map<String, Object> map = new HashedMap(16);
        try {
            map = mapper.readValue(JsonSanitizer.sanitize(jsonString),
                    new TypeReference<HashMap<String, Object>>() {
                    });
            cleanMap(map);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private static void cleanMap(Map<String,Object> map){
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (map!=null && map.keySet().size()>0) {
                if (StringUtils.isNotBlank(key)) {
                    Object value = null;
                    if (map.get(key) != null) {
                        if (map.get(key) instanceof String) {
                            // Assert.isTrue(checkSqlKeyWords(map.get(key).toString()),"已被过滤，因为参数中包含不允许sql的关键词");;

                            value=cleanSqlKeyWords(map.get(key).toString()).trim();
                            //  value=map.get(key).toString();
                        } else {
                            value = map.get(key);
                        }
                        map.put(key, value);
                    }
                }
            }
        }
    }

    private static boolean checkSqlKeyWords(String value) {
        String paramValue = value;
        if(StringUtils.isEmpty(paramValue)){
            return true;
        }
        String[] paramArr = paramValue.split(STRING_REGEX);
        for (String arr : paramArr) {
            if (NOT_ALLOEWD_KEYWORDS.contains(arr.toLowerCase())) {
                return  false;
            }
        }
        return  true;
    }


    private static String cleanSqlKeyWords(String value){
        String paramValue = value;
        String[] paramArr = paramValue.split(STRING_REGEX);
        for (String stringValue : paramArr) {
            if (NOT_ALLOEWD_KEYWORDS.contains(stringValue.toLowerCase())) {
                paramValue = StringUtils.replace(paramValue,stringValue,REPLACED_STRING);
            }
            paramValue = paramValue.replaceAll("(?i)sleep\\(\\d*\\)", REPLACED_STRING);

        }
        return paramValue;
    }


    private byte[] streamBody(HttpServletRequest request) {
        byte[] bytes = new byte[0];
        try {
            bytes = inputStream2Byte(request.getInputStream());
            if (bytes.length > 0) {
                String bodyJson = new String(bytes,"utf-8");
                JsonNode jsonNode = mapper.readTree(JsonSanitizer.sanitize(bodyJson));
                if (jsonNode.isArray()) {
                    List<Map<String, Object>> list = jsonStringToList(bodyJson);
                    bytes = mapper.writeValueAsString(list).getBytes("utf-8");
                } else {
                    Map<String, Object> map = jsonStringToMap(bodyJson);
                    bytes = mapper.writeValueAsString(map).getBytes("utf-8");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bytes.length == 0 && RequestMethod.POST.name().equals(request.getMethod())) {
            request.getParameterMap().entrySet();
            //从ParameterMap获取参数，并保存以便多次获取
            bytes = request.getParameterMap().entrySet().stream()
                    .map(entry -> {
                        String result;
                        String[] value = entry.getValue();
                        if (value != null && value.length > 1) {
                            result = Arrays.stream(value).map(s ->
                                    entry.getKey() + "=" + s)
                                    .collect(Collectors.joining("&"));

                        } else {
                            result = entry.getKey() + "=" + value[0];
                        }

                        return result;
                    }).collect(Collectors.joining("&")).getBytes();
        }

        return bytes;
    }

    private byte[] inputStream2Byte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] bytes = new byte[BUFFER_SIZE];
        int length;
        while ((length = inputStream.read(bytes, 0, BUFFER_SIZE)) != -1) {
            outputStream.write(bytes, 0, length);
        }
        return outputStream.toByteArray();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    private  byte[] streamBody;
    private static final int BUFFER_SIZE = 4096;
    private static String REPLACED_STRING = "";
    private static String SQL_KEYWORD="sleep|select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|sysdate|exec|count|master|drop|execute|from|version|database|extractvalue|concat|system_user|updatexml";
    private static String STRING_REGEX="\\s*( |\\t|\\r|\\n|\\()\\s*";
    static {
        String keyStr[] = SQL_KEYWORD.split("\\|");
        for (String str : keyStr) {
            NOT_ALLOEWD_KEYWORDS.add(str);
        }
    }

    @Override
    public int getContentLength() {
        return streamBody.length;
    }

    @Override
    public long getContentLengthLong() {
        return streamBody.length;
    }

}
