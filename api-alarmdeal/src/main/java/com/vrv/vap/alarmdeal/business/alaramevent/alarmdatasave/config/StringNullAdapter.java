package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * 上报的过程中字符串不要是null而是空字符串
 * 所以用这个是匹配器统一进行处理
 */
public class StringNullAdapter extends TypeAdapter<String> {
    @Override
    public void write(JsonWriter jsonWriter, String s) throws IOException {
        //将对象转成json对象时，若有字段为null ，则转为""
        if (s==null){
            jsonWriter.value("");
            return;
        }
        jsonWriter.value(s);
    }

    @Override
    public String read(JsonReader jsonReader) throws IOException {
        //将json对象转成对象时，若有字段为null ，则转为""
        if (jsonReader.peek()== JsonToken.NULL){
            jsonReader.nextNull();
            return "";
        }
        return jsonReader.nextString();
    }

}
