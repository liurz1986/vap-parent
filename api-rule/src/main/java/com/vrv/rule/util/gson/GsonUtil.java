package com.vrv.rule.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.ObjectTypeAdapter;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * @author: 梁国露
 * @since: 2022/10/13 14:53
 * @description:
 */
public class GsonUtil {
    public static Gson getGson() {
        Gson gson = new GsonBuilder().create();
        try {
            Field factories = Gson.class.getDeclaredField("factories");
            factories.setAccessible(true);
            Object o = factories.get(gson);
            Class<?>[] declaredClasses = Collections.class.getDeclaredClasses();
            for (Class c : declaredClasses) {
                if ("java.util.Collections$UnmodifiableList".equals(c.getName())) {
                    Field listField = c.getDeclaredField("list");
                    listField.setAccessible(true);
                    List<TypeAdapterFactory> list = (List<TypeAdapterFactory>) listField.get(o);
                    int i = list.indexOf(ObjectTypeAdapter.FACTORY);
                    list.set(i, MapTypeAdapter.FACTORY);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gson;
    }
}
