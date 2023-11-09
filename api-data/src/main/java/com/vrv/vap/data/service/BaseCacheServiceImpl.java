package com.vrv.vap.data.service;

import com.vrv.vap.base.BaseServiceImpl;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 说明：Base Cahce Service 使用条件：
 * 1. 对象 T 里面的 有 Integer getId() 方法
 * 2. 在 com.vrv.vap.data.component.config.CacheConfig 里面注册一个 Cache<Integer, T> 的 Bean
 * 3。 XyzServiceImpl 继承此类 BaseCacheServiceImpl 而非 BaseServiceImpl
 * <p>
 * 4. 目前本项目中使用 BaseCacheServiceImpl 的有：（不要随便复制，普通 Impl　还是继承 BaseServiceImpl） ：
 * Source
 * Maintain
 * Entity
 * Edge
 */
public abstract class BaseCacheServiceImpl<T> extends BaseServiceImpl<T> {

    @Autowired
    private Cache<Integer, T> cache;


    public T findById(String id) {
        return this.findById(Integer.parseInt(id));
    }

    private Method GETID = null;

    private int getRecordId(T record) {
        if (GETID == null) {
            try {
                GETID = record.getClass().getMethod("getId");
            } catch (NoSuchMethodException e) {
            }
        }
        if (GETID == null) {
            return 0;
        }
        try {
            return (Integer) GETID.invoke(record);
        } catch (IllegalAccessException e) {
//            e.printStackTrace();`
        } catch (InvocationTargetException e) {
//            e.printStackTrace();
        }
        return 0;

    }


    @Override
    public T findById(Integer id) {
        if (cache.containsKey(id)) {
//            System.out.println("Cache Hit!");
            return cache.get(id);
        }
//        System.out.println("Cache Hit! Find it by jdbc.");
        T item = super.findById(id);
        if (item != null) {
            cache.put(id, item);
        }
        return item;
    }


    @Override
    public Integer save(T record) {
        int result = super.save(record);
        Integer id = getRecordId(record);
        if (id > 0) {
//            System.out.println("Update Cache : " + id);
            cache.put(id, record);
        }
        return result;
    }
//
//    @Override
//    public Integer saveSelective(T record) {
//        int result = super.saveSelective(record);
//        Integer id = getRecordId(record);
//        if (id > 0) {
//            System.out.println("Update Cache : " + id);
////            cache.put(id, record);
//        }
//        return result;
//    }

    @Override
    public Integer update(T record) {
        int result = super.update(record);
        if (result > 0) {
            int id = this.getRecordId(record);
            if (id > 0) {
//            System.out.println("Update Cache : " + id);
                cache.put(id, record);
            }
        }
        return result;

    }

    @Override
    public Integer updateSelective(T record) {
        int result = super.updateSelective(record);
        if (result > 0) {
            int id = this.getRecordId(record);
            if (id > 0) {
//            System.out.println("Update Cache : " + id);
//                cache.put(id, record);
                cache.remove(id);
            }
        }
        return result;
    }

    public Integer deleteById(String id) {
        return this.deleteById(Integer.parseInt(id));
    }

    @Override
    public Integer deleteById(Integer id) {
        int count = super.deleteById(id);
        if (count > 0) {
            cache.remove(id);
        }
        return count;
    }

    @Override
    public Integer deleteByIds(String ids) {
        int count = super.deleteByIds(ids);
        if (count > 0) {
            String[] ptns = ids.split(",");
            for (String str : ptns) {
                int id = Integer.parseInt(str.trim());
                cache.remove(id);
            }
        }
        return count;
    }
}
